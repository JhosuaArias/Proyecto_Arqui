package Nucleos;

import Caches.BloqueDatos;
import Caches.BloqueInstrucciones;
import Caches.Estado;
import Estructuras_Datos.Hilo;
import Estructuras_Datos.Instruccion;
import MVC.Simulacion;

import java.lang.reflect.Array;
import java.util.Arrays;

import static Caches.Estado.COMPARTIDO;
import static Caches.Estado.INVALIDO;
import static Caches.Estado.MODIFICADO;

public class Nucleo1 extends Nucleo{

    private Hilo hilo;

    private Thread thread;

    public Nucleo1(Simulacion simulacion, int id){
        super(simulacion,id);
        this.thread = new Thread(this,"Thread 2");
        this.thread.start();
    }

    public void setHilo() {
        this.hilo = this.simulacion.pedirHiloCola();
    }

    private void resolverFalloCacheInstrucciones(int pc) {
        /**
         * Recibe pc que contiene la direccion de memoria
         * Tiene que ir a buscar la instruccion a memoria
         * Tiene que subir y poner esa instruccion en el cache, cambiar el estado
         * de esta cache a C
         * Esperar 40 ticks
         * devolver el resultado
         *
         * Nota: N1 no requiere de reservar el bus
         */
        boolean bloqueado=false;

        /**
         * Aqui continuo haciendo ticks hasta que me den el bus
         * ¿Como saber si no me dieron el bus?
         */

        while (!bloqueado) {
            if(!this.simulacion.intentar_pedirBusInstruc_Memoria()){
                this.esperarTick(false);
            }
            else {
                bloqueado=true;
            }
        }

        /**
         * Espera de 40 ticks en lo que se resuelve el fallo
         */

        int i=0;
        while(i!=40){
            this.esperarTick(false);
            ++i;
        }

        /**
         * Obtengo el bloque de instrucciones desde memoria,
         * ahora hay que cargarlos a la cache de instrucciones
          */
        Instruccion ins[]= simulacion.getBloqueMemoriaInstruccion(pc);
        /**
         * Falta setBloque para cache en simulacion?
         */
        int numeroBloque = this.simulacion.getNumeroBloque(pc);
        int posicion = this.simulacion.getPosicionCacheN1(pc);
        this.simulacion.setBloqueCacheInstruccionesN1(new BloqueInstrucciones(ins,numeroBloque,COMPARTIDO), posicion);

        this.simulacion.desbloquear_BusInstruc_Memoria();

    }

    private void resolverFalloCacheDatos(int pc) {
        /**
         * Recibe pc que es la direccion de memoria y cache
         * Esto ya asume de que no existe lo que ocupo en mi cache por lo
         * que solo hara las revisiones al otro cache y a memoria
         */

        /**
         * Debo de poder obtener bus, mi posicion de cache y la posicion del otro cache
         * Esto es porque N0 puede bloquear mi posicion
         */
        boolean bloqueado=false;
        while (!bloqueado) {
            if(!this.simulacion.intentar_pedirBusDatos_Memoria()
                    && !this.simulacion.intentar_pedirPosicion_CacheDatosN1(pc)
                    && !this.simulacion.intentar_pedirPosicion_CacheDatosN0(pc))
            {
                this.esperarTick(false);
            }
            else {
                bloqueado=true;
            }
        }
        /**
         * Luego de obtenerlo debo de ir considerando los casos
         * Si el otro cache lo tiene en COMPARTIDO entonces lo cargo a mi cache
         * Si el otro cache lo tiene en MODIFICADO entonces lo bajo a memoria y luego lo cargo a mi cache, total 80 ticks
         * Si el otro cache lo tiene en INVALIDO entonces lo traigo de memoria
         * Caso default: No esta en la otra cache por lo que hay que traerlo desde memoria
         */
        //TODO COMPLETAR
        switch (this.simulacion.getBloqueCacheDatosN1(pc).getEstado()){
            case COMPARTIDO:
                break;
            case MODIFICADO:
                break;
            case INVALIDO:
                break;
            default:
                break;
        }



        int i=0;
        while(i!=40){
            this.esperarTick(false);
            ++i;
        }

       // Instruccion ins[]= simulacion.getBloqueMemoriaInstruccion(pc);

    }

    private void iteracion() {
        boolean mismoHilo = true;

        while (mismoHilo) {
            /**Calculamos el bloque y posicion en caché*/
            int pc = this.hilo.getPc();
            int numeroBloque = this.simulacion.getNumeroBloque(pc);

            BloqueInstrucciones bloqueInstrucciones = this.simulacion.getBloqueCacheInstruccionesN1(pc);
            /**Verificamos si hay fallo de caché**/
            if (!(bloqueInstrucciones.getEstado() == COMPARTIDO && bloqueInstrucciones.getEtiqueta() == numeroBloque)) {
                //Hay fallo
                this.resolverFalloCacheInstrucciones(pc);
                bloqueInstrucciones = this.simulacion.getBloqueCacheInstruccionesN1(pc);

            }
            /**Se agarra la instrucción**/
            int posicionCache = this.simulacion.getPosicionBloque(pc);
            this.hilo.setIr(bloqueInstrucciones.getInstruccion(posicionCache));

            System.err.println(Arrays.toString(this.hilo.getIr().getPalabra()));
            /**Se suma el PC**/
            this.hilo.sumarPc();

            /**Se ejecuta la instruccion**/
            this.ejecutar_instruccion(this.hilo,null,this);

            /**Verificaciones de fin o quantum**/
            if (this.hilo.isEsFin()) {
                mismoHilo = false;
                this.simulacion.setInactivoHilo(this.hilo.getId());
                this.hilo = null;
                /**Esperar un tick**/
                this.esperarTick(false);
            } else if (this.hilo.getQuantumRestante() == 1) {
                this.simulacion.devolverHiloCola(this.hilo);
                this.hilo.reiniciarQuantum();
                this.hilo = null;
                mismoHilo = false;
                /**Esperar un tick**/
                this.esperarTick(false);
            }else{
                /**Esperar un tick**/
                this.esperarTick(true);
            }

        }
    }


    @Override
    public void esperarTick(boolean restarQuantum) {
        super.esperarTick(restarQuantum);
        if(restarQuantum)
            this.hilo.restarQuantum();
    }

    @Override
    public void run() {
        super.run();
        while (!this.simulacion.isColaNull()){
            this.hilo = this.simulacion.pedirHiloCola();
            if(this.hilo == null){
                this.esperarTick(false);
            } else {
                this.iteracion();
            }
        }
        System.err.println("Terminé: " + Thread.currentThread().getName());
    }

    /***********************/
    public void lw(int direccionMemoria){

        boolean noTermine=true;

        while (noTermine)
        {

            int posicion = simulacion.getPosicionCacheN1(direccionMemoria); //posicion en el cache

            if(!(simulacion.intentar_pedirPosicion_CacheDatosN1(posicion))) //No bloquee el indice, vuelve a intentar
            {
                esperarTick(false);
            }

            else //Logre bloquear el indice, sigo
            {
                BloqueDatos bloqueCacheDatos= simulacion.getBloqueCacheDatosN1(direccionMemoria); //Obtengo el bloque del cache

                if (bloqueCacheDatos.getEtiqueta()!=simulacion.getNumeroBloque(direccionMemoria)) //La etiqueta no corresponde al bloque
                {
                    if (!simulacion.intentar_pedirBusDatos_Memoria()) //No pude bloquear el bus
                    {
                        simulacion.desbloquear_Posicion_CacheDatosN1(posicion);
                        esperarTick(false);
                    } else //Pude bloquear el bus
                    {
                        if (bloqueCacheDatos.getEstado()== Estado.MODIFICADO) //Es otra etiqueta y esta modificado
                        {
                            /*Esperar 40 tics, cargar el bloque victima a memoria e invalidar*/
                        }

                        int posicionOtroExtremo = simulacion.getPosicionCacheN1(direccionMemoria);

                        if (!(simulacion.intentar_pedirPosicion_CacheDatosN1(posicionOtroExtremo))) //No pude bloquear el otro indice
                        {
                            simulacion.desbloquear_Posicion_CacheDatosN1(posicion);
                            simulacion.desbloquear_BusDatos_Memoria();
                            // esperarTick();
                        }
                        else //pude bloquear el otro indice

                        {
                            lwVerificarOtroCache_vengodeN1(direccionMemoria);
                            noTermine=false;
                        }

                    }


                }
                else //La etiqueta corresponde al bloque
                {
                    if(bloqueCacheDatos.getEstado()== Estado.INVALIDO)  //La etiqueta esta invalida
                    {
                        if (!simulacion.intentar_pedirBusDatos_Memoria()) //No pude bloquear el bus
                        {
                            simulacion.desbloquear_Posicion_CacheDatosN1(posicion);
                            //esperarTick();
                        }

                        else
                        {
                            int posicionOtroExtremo = simulacion.getPosicionCacheN0(direccionMemoria);

                            if (!(simulacion.intentar_pedirPosicion_CacheDatosN0(posicionOtroExtremo))) //No pude bloquear el otro indice
                            {
                                simulacion.desbloquear_Posicion_CacheDatosN1(posicion);
                                simulacion.desbloquear_BusDatos_Memoria();
                                // esperarTick();
                            }
                            else //Pude bloquear el otro indice
                            {
                                lwVerificarOtroCache_vengodeN1(direccionMemoria);
                                noTermine=false;
                            }

                        }


                    }

                    else //La etiqueta no esta invalida
                    {
                        /*Averiguo la palabra, cargo a registro, desbloqueo la posicion*/

                        simulacion.desbloquear_Posicion_CacheDatosN1(posicion);

                        /*Indicar que no se vuelva a meter en el while*/
                        noTermine=false;
                    }
                }

            }

        }
    }

    /*****************************/

    public  void lwVerificarOtroCache_vengodeN1(int direccionMemoria)
    {
        /*vengo de bloquear el indice del otro cache*/

        BloqueDatos bloqueCacheDatosOtroExtremo = simulacion.getBloqueCacheDatosN0(direccionMemoria);
        if (bloqueCacheDatosOtroExtremo.getEtiqueta()== simulacion.getNumeroBloque(direccionMemoria) && bloqueCacheDatosOtroExtremo.getEstado() == Estado.MODIFICADO) //Corresponde a la etiqueta y esta modificado
        {
            /*Diagrama en morado*/
        }
        else //No corresponde la etiqueta o no esta modificado
        {
            /*Diagrama en anaranjado*/
        }

    }

    /*******************************/
    public void sw() {

    }
/*****************************************************/

}

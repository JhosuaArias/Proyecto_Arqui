package Nucleos;

import Caches.BloqueDatos;
import Caches.BloqueInstrucciones;
import Caches.Estado;
import Estructuras_Datos.Hilo;
import Estructuras_Datos.Instruccion;
import MVC.Simulacion;
import javafx.util.Pair;

import java.util.Arrays;

import static Caches.Estado.COMPARTIDO;
import static Caches.Estado.INVALIDO;

public class Nucleo0 extends Nucleo{

    private Hilo hilo;

    private Pair<EstadoThread,Integer> estadoHilo;

    private Thread thread;


    /***
     * Nucleo 1 Constructor. Aquí se crea un nuevo tread apra el núcleo.
     * @param simulacion Referencia de la simulacion que creó este núcleo.
     * @param id id del núcleo.
     */
    public Nucleo0(Simulacion simulacion, int id){
        super(simulacion,id);
        this.estadoHilo = new Pair<>(EstadoThread.EJECUTANDO,-1);
        this.thread = new Thread(this,"Thread "+id);
        this.thread.start();
    }


    /***
     * Método que se encarga de resolver un fallo de caché de instrucciones.
     * @param pc dirección de memoria de la instrucción requerida.
     */
    private void resolverFalloCacheInstrucciones(int pc) {
        this.setEstado(EstadoThread.FALLO_CACHE_INSTRUCCIONES,this.simulacion.getPosicionCacheN0(pc));
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

        this.esperar40Ticks();

        /**
         * Obtengo el bloque de instrucciones desde memoria,
         * ahora hay que cargarlos a la cache de instrucciones
         */
        Instruccion ins[]= simulacion.getBloqueMemoriaInstruccion(pc);
        /**
         * Falta setBloque para cache en simulacion?
         */
        int numeroBloque = this.simulacion.getNumeroBloque(pc);
        int posicion = this.simulacion.getPosicionCacheN0(pc);
        this.simulacion.setBloqueCacheInstruccionesN0(new BloqueInstrucciones(ins,numeroBloque,COMPARTIDO), posicion);

        this.simulacion.desbloquear_BusInstruc_Memoria();
        this.setEstado(EstadoThread.EJECUTANDO,-1);
    }

    /***
     * Método que ejecuta el núcleo para ejecutar instrucciones de un hilo. Se encarga de sumar pc, ejecutar el IR,
     * resolver fallos de caché de instrucciones, verifica si el hihlo ya terminó o su quantum llegó a cero y lo reinicia.
     * También se encarga de devolver hilos a la Cola.
     */
    private void iteracion() {
        boolean mismoHilo = true;

        while (mismoHilo) {
            /**Calculamos el bloque y posicion en caché*/
            int pc = this.hilo.getPc();
            int numeroBloque = this.simulacion.getNumeroBloque(pc);

            BloqueInstrucciones bloqueInstrucciones = this.simulacion.getBloqueCacheInstruccionesN0(pc);
            /**Verificamos si hay fallo de caché**/
            if (!(bloqueInstrucciones.getEstado() == COMPARTIDO && bloqueInstrucciones.getEtiqueta() == numeroBloque)) {
                //Hay fallo
                this.resolverFalloCacheInstrucciones(pc);
                bloqueInstrucciones = this.simulacion.getBloqueCacheInstruccionesN0(pc);

            }
            /**Se agarra la instrucción**/
            int posicionCache = this.simulacion.getPosicionBloque(pc);
            this.hilo.setIr(bloqueInstrucciones.getInstruccion(posicionCache));

            System.out.println(Arrays.toString(this.hilo.getIr().getPalabra()));
            /**Se suma el PC**/
            this.hilo.sumarPc();

            /**Se ejecuta la instruccion**/
            this.ejecutar_instruccion(this.hilo,this,null);

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

    /***
     * Método que se encarga de esperar en barreras para la sincronización de núcleos.
     * @param restarQuantum parámetro que indica si hay que restarle quantum al hilo o no.
     */
    @Override
    public void esperarTick(boolean restarQuantum) {
        super.esperarTick(restarQuantum);

        if (this.hilo != null){
            this.hilo.sumarCiclosPasados();
            if(restarQuantum)
                this.hilo.restarQuantum();
        }

        this.simulacion.esperarSegundaBarrera();
    }

    /***
     * Método que ejecuta el Thread desde el principio. Verifica si la Cola es nula, en el caso de no serlo intenta
     * agarrar Hilos, y si obtiene un hilo ejecuta una iteración. En el caso de que la Cola sea null, el Thread debe
     * finalizar.
     */
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

    /***
     * Método para esperar 40 ticks.
     */
    private void esperar40Ticks(){
        for(int i = 0 ; i < 40 ; i++){
            esperarTick(false);
        }
    }

    private void lw_resolverFalloCacheDatos(Hilo hiloEjecucion, int numRegistro, int direccionMemoria, TipoDeFallo tipo){

        /*Ya tengo bloqueado todos los recursos*/

        switch (tipo)
        {
            case LW_NOESMIETIEQUETAYMODIFICADO:

                /*Espera 40 tics*/
                this.esperar40Ticks();

                /*Guarda el bloque victima en memoria e invalida */
                BloqueDatos bloqueDatos = simulacion.getBloqueCacheDatosN0(direccionMemoria);
                simulacion.setBloqueCacheDatosMemoria(bloqueDatos ,bloqueDatos.getEtiqueta());
                simulacion.setEstadoN0(this.simulacion.getPosicionCacheN0(direccionMemoria),INVALIDO);

                break;

            case LW_CARGARDESDEMEMORIA:

                /*Libero la otra posicion*/
                simulacion.desbloquear_Posicion_CacheDatosN1(simulacion.getPosicionCacheN1(direccionMemoria));

                /*Espera 40 tics*/
                this.esperar40Ticks();

                /*Carga de memoria al cache y lo pone en compartido*/
                simulacion.setBloqueCacheDatosN0(simulacion.getBloqueMemoriaDatos(direccionMemoria),Estado.COMPARTIDO,direccionMemoria);

                /*Libero el bus*/
                simulacion.desbloquear_BusDatos_Memoria();

                break;

            case LW_CARGARDESDECACHE:

                /*Espera 40 tics*/
                this.esperar40Ticks();

                /*Guarda el bloque (del otro cache) en memoria */
                BloqueDatos bloqueDatos1 = simulacion.getBloqueCacheDatosN1(direccionMemoria);
                simulacion.setBloqueCacheDatosMemoria(bloqueDatos1,bloqueDatos1.getEtiqueta());

                /*Pone en compartido el del otro cache*/
                simulacion.setEstadoN1(this.simulacion.getPosicionCacheN1(direccionMemoria),COMPARTIDO);

                /*Libero  la otra posicion*/
                simulacion.desbloquear_Posicion_CacheDatosN1(simulacion.getPosicionCacheN1(direccionMemoria));

                /*Carga de memoria al cache y lo pone en compartido*/
                simulacion.setBloqueCacheDatosN0(simulacion.getBloqueMemoriaDatos(direccionMemoria),Estado.COMPARTIDO,direccionMemoria);

                /*Libero el bus*/
                simulacion.desbloquear_BusDatos_Memoria();

                break;
        }


    }


    private void sw_resolverFalloCacheDatos(Hilo hiloEjecucion, int numRegistro, int direccionMemoria, TipoDeFallo tipo){

        /*Ya tengo bloqueado todos los recursos*/

        switch (tipo)
        {
            case SW_NOESMIETIEQUETAYMODIFICADO:

                /*Espera 40 tics*/
                this.esperar40Ticks();

                /*Guarda el bloque victima en memoria e invalida */
                BloqueDatos bloqueDatos = simulacion.getBloqueCacheDatosN0(direccionMemoria);
                simulacion.setBloqueCacheDatosMemoria(bloqueDatos,bloqueDatos.getEtiqueta());
                simulacion.setEstadoN0(this.simulacion.getPosicionCacheN0(direccionMemoria),INVALIDO);
                break;

            case SW_CARGARDESDEMEMORIA:


                /*Libero la otra posicion*/
                simulacion.desbloquear_Posicion_CacheDatosN1(simulacion.getPosicionCacheN1(direccionMemoria));

                /*Espera 40 tics*/
                this.esperar40Ticks();

                /*Carga de memoria al cache, lo pone en modificado hasta guardar la palabra*/
                simulacion.setBloqueCacheDatosN0(simulacion.getBloqueMemoriaDatos(direccionMemoria),Estado.COMPARTIDO,direccionMemoria);

                /*Libero el bus*/
                simulacion.desbloquear_BusDatos_Memoria();

                break;

            case SW_CARGARDESDECACHE:

                /*Espera 40 tics*/
                this.esperar40Ticks();

                /*Guarda el bloque (del otro cache) en memoria*/
                BloqueDatos bloqueDatos1 = simulacion.getBloqueCacheDatosN1(direccionMemoria);
                simulacion.setBloqueCacheDatosMemoria(bloqueDatos1,bloqueDatos1.getEtiqueta());

                /*Pone en invalido el del otro cache*/
                simulacion.setEstadoN1(this.simulacion.getPosicionCacheN1(direccionMemoria),Estado.INVALIDO);

                /*Libero  la otra posicion */
                simulacion.desbloquear_Posicion_CacheDatosN1(simulacion.getPosicionCacheN1(direccionMemoria));

                /*Carga de memoria al cache y lo pone en compartido, lo pone ne modificado al guardar la palabra*/
                simulacion.setBloqueCacheDatosN0(simulacion.getBloqueMemoriaDatos(direccionMemoria),Estado.COMPARTIDO,direccionMemoria);

                /*Liberar el bus*/
                simulacion.desbloquear_BusDatos_Memoria();

                break;

            case SW_ESTOYCOMPARTIDO:

                /*Libero la otra posicion*/
                simulacion.desbloquear_Posicion_CacheDatosN1(simulacion.getPosicionCacheN1(direccionMemoria));

                /*Libero el bus*/
                simulacion.desbloquear_BusDatos_Memoria();


                break;
        }


    }



    public void lw(Hilo hiloEjecucion, int numRegistro, int direccionMemoria){

        boolean noTermine=true;

        while (noTermine)
        {

            int posicion = simulacion.getPosicionCacheN0(direccionMemoria); //posicion en el cache

            if(!(simulacion.intentar_pedirPosicion_CacheDatosN0(posicion))) //No bloquee el indice, vuelve a intentar
            {
                esperarTick(false);
            }
            else //Logre bloquear el indice, sigo
            {
                BloqueDatos bloqueCacheDatos= simulacion.getBloqueCacheDatosN0(direccionMemoria); //Obtengo el bloque del cache

                if (bloqueCacheDatos.getEtiqueta()!=simulacion.getNumeroBloque(direccionMemoria)) //La etiqueta no corresponde al bloque
                {
                    this.setEstado(EstadoThread.FALLO_CACHE_DATOS,posicion);
                    if (!simulacion.intentar_pedirBusDatos_Memoria()) //No pude bloquear el bus
                    {
                        simulacion.desbloquear_Posicion_CacheDatosN0(posicion);
                        esperarTick(false);

                    }
                    else //Pude bloquear el bus
                    {
                        if (bloqueCacheDatos.getEstado()== Estado.MODIFICADO) //Es otra etiqueta y esta modificado
                        {
                            lw_resolverFalloCacheDatos(hiloEjecucion,numRegistro,direccionMemoria,TipoDeFallo.LW_NOESMIETIEQUETAYMODIFICADO);

                        }

                        int posicionOtroExtremo = simulacion.getPosicionCacheN1(direccionMemoria);

                        if (!(simulacion.intentar_pedirPosicion_CacheDatosN1(posicionOtroExtremo))) //No pude bloquear el otro indice
                        {
                            simulacion.desbloquear_BusDatos_Memoria();
                            simulacion.desbloquear_Posicion_CacheDatosN0(posicion);
                            esperarTick(false);
                        }
                        else //pude bloquear el otro indice
                        {
                            /*Soluciono el fallo*/
                            lw_VerificarSiEstaEnN1(hiloEjecucion,numRegistro,direccionMemoria);

                            /*Cargo la palabra*/
                            cargarPalabraN0(hiloEjecucion,numRegistro,direccionMemoria);

                            /*Termine LW*/
                            noTermine=false;

                            this.setEstado(EstadoThread.EJECUTANDO,-1);
                        }
                    }
                }
                else //La etiqueta corresponde al bloque
                {
                    if(bloqueCacheDatos.getEstado()== Estado.INVALIDO)  //La etiqueta esta invalida
                    {
                        this.setEstado(EstadoThread.FALLO_CACHE_DATOS,posicion);
                        if (!simulacion.intentar_pedirBusDatos_Memoria()) //No pude bloquear el bus
                        {
                            simulacion.desbloquear_Posicion_CacheDatosN0(posicion);
                            esperarTick(false);
                        }

                        else //Logre bloquear el bus
                        {
                            int posicionOtroExtremo = simulacion.getPosicionCacheN1(direccionMemoria);

                            if (!(simulacion.intentar_pedirPosicion_CacheDatosN1(posicionOtroExtremo))) //No pude bloquear el otro indice
                            {
                                simulacion.desbloquear_Posicion_CacheDatosN0(posicion);
                                simulacion.desbloquear_BusDatos_Memoria();
                                esperarTick(false);
                            }
                            else //Pude bloquear el otro indice
                            {
                                /*Soluciono el fallo*/
                                lw_VerificarSiEstaEnN1(hiloEjecucion,numRegistro,direccionMemoria);

                                /*Cargo la palabra*/
                                cargarPalabraN0(hiloEjecucion,numRegistro,direccionMemoria);

                                /*Termine LW*/
                                noTermine=false;

                                this.setEstado(EstadoThread.EJECUTANDO,-1);

                            }

                        }


                    }

                    else //La etiqueta esta compartida o modificada, caso trivial
                    {
                        /*Cargo la palabra*/
                        cargarPalabraN0(hiloEjecucion,numRegistro,direccionMemoria);
                        /*Termine LW*/
                        noTermine=false;
                    }
                }

            }

        }
    }

    /*****************************/

    private void lw_VerificarSiEstaEnN1(Hilo hiloEjecucion, int numRegistro, int direccionMemoria)
    {
        /*vengo de bloquear el indice del otro cache*/

        BloqueDatos bloqueCacheDatosOtroExtremo = simulacion.getBloqueCacheDatosN1(direccionMemoria);
        if (bloqueCacheDatosOtroExtremo.getEtiqueta()== simulacion.getNumeroBloque(direccionMemoria) && bloqueCacheDatosOtroExtremo.getEstado() == Estado.MODIFICADO) //Corresponde a la etiqueta y esta modificado
        {
            lw_resolverFalloCacheDatos(hiloEjecucion,numRegistro,direccionMemoria,TipoDeFallo.LW_CARGARDESDECACHE);
        }
        else //No corresponde la etiqueta o no esta modificado
        {
            lw_resolverFalloCacheDatos(hiloEjecucion,numRegistro,direccionMemoria,TipoDeFallo.LW_CARGARDESDEMEMORIA);
        }



    }

    /*****************************/

    private void cargarPalabraN0(Hilo hiloEjecucion, int numRegistro, int direccionMemoria)
    {

        /*Averiguo la palabra, cargo a registro, desbloqueo la posicion*/

        int palabra=simulacion.getPalabraCacheDatosN0(this.simulacion.getPosicionCacheN0(direccionMemoria),this.simulacion.getNumeroPalabra(direccionMemoria));

        /*Cargar la palabra al registro*/
        hiloEjecucion.setRegistro(numRegistro,palabra);

        /*Desbloquear la posicion*/
        simulacion.desbloquear_Posicion_CacheDatosN0(simulacion.getPosicionCacheN0(direccionMemoria));

    }

    /*********** SW *********/


    public void sw(Hilo hiloEjecucion, int numRegistro, int direccionMemoria){

        boolean noTermine=true;

        while (noTermine)
        {

            int posicion = simulacion.getPosicionCacheN0(direccionMemoria); //posicion en el cache

            if(!(simulacion.intentar_pedirPosicion_CacheDatosN0(posicion))) //No bloquee el indice, vuelve a intentar
            {
                esperarTick(false);
            }

            else //Logre bloquear el indice, sigo
            {
                BloqueDatos bloqueCacheDatos= simulacion.getBloqueCacheDatosN0(direccionMemoria); //Obtengo el bloque del cache

                if (bloqueCacheDatos.getEtiqueta()!=simulacion.getNumeroBloque(direccionMemoria)) //La etiqueta no corresponde al bloque
                {
                    this.setEstado(EstadoThread.FALLO_CACHE_DATOS,posicion);
                    if (!simulacion.intentar_pedirBusDatos_Memoria()) //No pude bloquear el bus
                    {
                        simulacion.desbloquear_Posicion_CacheDatosN0(posicion);
                        esperarTick(false);

                    }

                    else //Pude bloquear el bus
                    {
                        if (bloqueCacheDatos.getEstado()== Estado.MODIFICADO) //Es otra etiqueta y esta modificado
                        {
                            sw_resolverFalloCacheDatos(hiloEjecucion,numRegistro,direccionMemoria,TipoDeFallo.SW_NOESMIETIEQUETAYMODIFICADO);

                        }

                        int posicionOtroExtremo = simulacion.getPosicionCacheN1(direccionMemoria);

                        noTermine = BuscarEnOtraCache(hiloEjecucion, numRegistro, direccionMemoria, noTermine, posicion, posicionOtroExtremo);

                        if(!noTermine)
                            this.setEstado(EstadoThread.EJECUTANDO,-1);

                    }


                }
                else //La etiqueta corresponde al bloque
                {
                    if(bloqueCacheDatos.getEstado()== Estado.INVALIDO)  //La etiqueta esta invalida
                    {
                        this.setEstado(EstadoThread.FALLO_CACHE_DATOS,posicion);
                        if (!simulacion.intentar_pedirBusDatos_Memoria()) //No pude bloquear el bus
                        {
                            simulacion.desbloquear_Posicion_CacheDatosN0(posicion);
                            esperarTick(false);
                        }

                        else //Logre bloquear el bus
                        {
                            int posicionOtroExtremo = simulacion.getPosicionCacheN1(direccionMemoria);

                            noTermine = BuscarEnOtraCache(hiloEjecucion, numRegistro, direccionMemoria, noTermine, posicion, posicionOtroExtremo);

                            if(!noTermine)
                                this.setEstado(EstadoThread.EJECUTANDO,-1);
                        }


                    }

                    else { //La etiqueta no esta invalida

                        if (bloqueCacheDatos.getEstado()== Estado.COMPARTIDO) //La etiqueta esa compartida
                        {

                            if (!simulacion.intentar_pedirBusDatos_Memoria()) //No pude bloquear el bus
                            {
                                simulacion.desbloquear_Posicion_CacheDatosN0(posicion);
                                esperarTick(false);
                            }

                            else //Logre bloquear el bus
                            {

                                int posicionOtroExtremo = simulacion.getPosicionCacheN1(direccionMemoria);

                                if (!(simulacion.intentar_pedirPosicion_CacheDatosN1(posicionOtroExtremo))) //No pude bloquear el otro indice
                                {
                                    simulacion.desbloquear_Posicion_CacheDatosN0(posicion);
                                    simulacion.desbloquear_BusDatos_Memoria();
                                    esperarTick(false);
                                }
                                else //pude bloquear el otro indice

                                {
                                    sw_estoyEnCompartidoN0(hiloEjecucion, numRegistro, direccionMemoria);

                                    /*Cargo la palabra*/
                                    guardarPalabraN0(hiloEjecucion,numRegistro,direccionMemoria);

                                    /*Termine SW*/
                                    noTermine=false;
                                }
                            }

                        }

                        else //La etiqueta esta modificada, caso trivial
                        {
                            /*Cargo la palabra*/
                            guardarPalabraN0(hiloEjecucion, numRegistro, direccionMemoria);
                            /*Termine SW*/
                            noTermine = false;
                        }
                    }
                }

            }//Fin del while

        }
    }

    private boolean BuscarEnOtraCache(Hilo hiloEjecucion, int numRegistro, int direccionMemoria, boolean noTermine, int posicion, int posicionOtroExtremo) {
        if (!(simulacion.intentar_pedirPosicion_CacheDatosN1(posicionOtroExtremo))) //No pude bloquear el otro indice
        {
            simulacion.desbloquear_Posicion_CacheDatosN0(posicion);
            simulacion.desbloquear_BusDatos_Memoria();
            esperarTick(false);
        }
        else //Pude bloquear el otro indice
        {
            /*Soluciono el fallo*/
            sw_VerificarSiEstaEnN1(hiloEjecucion,numRegistro,direccionMemoria);

            /*Cargo la palabra*/
            guardarPalabraN0(hiloEjecucion,numRegistro,direccionMemoria);

            /*Termine SW*/
            noTermine=false;
        }
        return noTermine;
    }


    /*******************************************/


    private void sw_VerificarSiEstaEnN1(Hilo hiloEjecucion, int numRegistro, int direccionMemoria)
    {
        /*vengo de bloquear el indice del otro cache*/

        BloqueDatos bloqueCacheDatosOtroExtremo = simulacion.getBloqueCacheDatosN1(direccionMemoria);

        if (bloqueCacheDatosOtroExtremo.getEtiqueta()== simulacion.getNumeroBloque(direccionMemoria)) //La etiqueta corresponde al bloque
        {

            switch (bloqueCacheDatosOtroExtremo.getEstado())
            {
                case INVALIDO:
                    sw_resolverFalloCacheDatos(hiloEjecucion,numRegistro,direccionMemoria,TipoDeFallo.SW_CARGARDESDEMEMORIA);
                    break;

                case COMPARTIDO:

                    /*HAY QUE INVALIDAR EN EL OTRO CACHE ANTES DE HACER LO MISMO*/
                    simulacion.setEstadoN1(this.simulacion.getPosicionCacheN1(direccionMemoria), Estado.INVALIDO);

                    /*AHORA SI CARGO DE MEMORIA*/
                    sw_resolverFalloCacheDatos(hiloEjecucion,numRegistro,direccionMemoria,TipoDeFallo.SW_CARGARDESDEMEMORIA);
                    break;

                case MODIFICADO:
                    sw_resolverFalloCacheDatos(hiloEjecucion,numRegistro,direccionMemoria,TipoDeFallo.SW_CARGARDESDECACHE);
                    break;
            }
        }
        else //La etiqueta no corresponde al bloque
        {
            sw_resolverFalloCacheDatos(hiloEjecucion,numRegistro,direccionMemoria,TipoDeFallo.SW_CARGARDESDEMEMORIA);
        }



    }

    /*****************************/

    private void sw_estoyEnCompartidoN0(Hilo hiloEjecucion, int numRegistro, int direccionMemoria)
    {
        /*vengo de bloquear el indice del otro cache*/

        BloqueDatos bloqueCacheDatosOtroExtremo = simulacion.getBloqueCacheDatosN1(direccionMemoria);

        if (bloqueCacheDatosOtroExtremo.getEtiqueta()== simulacion.getNumeroBloque(direccionMemoria) && bloqueCacheDatosOtroExtremo.getEstado()==Estado.COMPARTIDO) //La etiqueta corresponde al bloque y esta compartido
        {
            /*HAY QUE INVALIDAR EN EL OTRO CACHE ANTES DE HACER LO MISMO*/
            simulacion.setEstadoN1(this.simulacion.getPosicionCacheN1(direccionMemoria), Estado.INVALIDO);

            /*AHORA SI, cargo el registro en cache*/
            sw_resolverFalloCacheDatos(hiloEjecucion,numRegistro,direccionMemoria,TipoDeFallo.SW_ESTOYCOMPARTIDO);

        }

        else
        {
            /*Solamente carga el registro*/
            sw_resolverFalloCacheDatos(hiloEjecucion,numRegistro,direccionMemoria,TipoDeFallo.SW_ESTOYCOMPARTIDO);
        }

    }


    /*****************************************************/

    private void guardarPalabraN0(Hilo hiloEjecucion, int numRegistro, int direccionMemoria)
    {
        /*Averiguo la palabra del bloque a modificar, al llegar aqui ya el cache está listo para ser modificado*/

        int numeroPalabra= simulacion.getNumeroPalabra(direccionMemoria);

        /*Actualizo la palabra*/
        simulacion.setPalabraCacheDatosN0(this.simulacion.getPosicionCacheN0(direccionMemoria),numeroPalabra,hiloEjecucion.getRegistro(numRegistro));

        /*Lo pongo en modificado*/
        simulacion.setEstadoN0(this.simulacion.getPosicionCacheN0(direccionMemoria),Estado.MODIFICADO);

        /*Desbloqueo la posicion*/
        simulacion.desbloquear_Posicion_CacheDatosN0(simulacion.getPosicionCacheN0(direccionMemoria));

    }



    /**********/


    /**Getters**/
    public Pair<EstadoThread, Integer> getEstadoHilo() {
        return estadoHilo;
    }

    public Hilo getHilo() {
        return hilo;
    }

    /**Setter**/

    public void setEstado(EstadoThread estado, int posicion){
        this.estadoHilo = new Pair<>(estado,posicion);
    }
}



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

public class Nucleo1 extends Nucleo{

    private Hilo hilo;

    private Pair<EstadoThread,Integer> estadoHilo;

    private Thread thread;

    public Nucleo1(Simulacion simulacion, int id){
        super(simulacion,id);
        this.estadoHilo = new Pair<>(EstadoThread.EJECUTANDO,-1);
        this.thread = new Thread(this,"Thread 1");
        this.thread.start();
    }



    private void resolverFalloCacheInstrucciones(int pc) {
        this.setEstado(EstadoThread.FALLO_CACHE_INSTRUCCIONES,this.simulacion.getPosicionCacheN1(pc));
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
        this.setEstado(EstadoThread.EJECUTANDO,-1);
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
        this.simulacion.esperarSegundaBarrera();
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

    private void esperar40Ticks(){
        for(int i = 0 ; i < 40 ; i++){
            esperarTick(false);
        }
    }

    public void sw(Hilo hiloEjecucion, int numRegistro, int direccionMemoria){
        int posicionCacheN1 = this.simulacion.getPosicionCacheN1(direccionMemoria);
        int posicionCacheN0 = this.simulacion.getPosicionCacheN0(direccionMemoria);
        int numeroBloque = this.simulacion.getNumeroBloque(direccionMemoria);
        boolean termine = false;
        while (!termine){
            if(this.simulacion.intentar_pedirPosicion_CacheDatosN1(posicionCacheN1)){ //logré bloquearlo
                BloqueDatos bloqueDatosN1 = this.simulacion.getBloqueCacheDatosN1(direccionMemoria);
                int posicionPalabra = this.simulacion.getNumeroPalabra(direccionMemoria);
                if(bloqueDatosN1.getEtiqueta() == numeroBloque ){ // la etiqueta corresponde al bloque
                    switch (bloqueDatosN1.getEstado()){
                        case COMPARTIDO:
                            //todo buscarlo del otro lado y si está compartido invalidar
                            if(this.simulacion.intentar_pedirBusDatos_Memoria()){//logré agarrar el bus de datos-memoria
                                if(this.simulacion.intentar_pedirPosicion_CacheDatosN0(posicionCacheN0)){ //intentar bloquear la posicion en el otro caché
                                    BloqueDatos bloqueDatosN0 = this.simulacion.getBloqueCacheDatosN0(direccionMemoria);
                                    if(bloqueDatosN0.getEtiqueta() == numeroBloque) { // la etiqueta es el bloque que ocupo
                                        if (bloqueDatosN0.getEstado() == Estado.COMPARTIDO){
                                                this.simulacion.getBloqueCacheDatosN0(direccionMemoria).setEstado(Estado.INVALIDO);
                                        }
                                    }//en caso contrario no se hace nada
                                    this.simulacion.setPalabraCacheDatosN1(posicionCacheN1,posicionPalabra,hiloEjecucion.getRegistro(numRegistro));
                                    this.simulacion.desbloquear_Posicion_CacheDatosN0(posicionCacheN0);
                                    this.simulacion.desbloquear_BusDatos_Memoria();
                                    this.simulacion.desbloquear_Posicion_CacheDatosN1(posicionCacheN1);
                                    termine = true;
                                }else{ // no logré bloquear la posicion del otro caché
                                    this.simulacion.desbloquear_BusDatos_Memoria();
                                    this.simulacion.desbloquear_Posicion_CacheDatosN1(posicionCacheN1);
                                    esperarTick(false);
                                }
                            }else{//no logré agarrar el bus, desbloqueo la posicion
                                this.simulacion.desbloquear_Posicion_CacheDatosN1(posicionCacheN1);
                                esperarTick(false);
                            }
                        case INVALIDO:
                            //todo buscarlo del otro lado y si no de memoria
                            break;
                        case MODIFICADO:
                            this.simulacion.setPalabraCacheDatosN1(posicionCacheN1,posicionPalabra,hiloEjecucion.getRegistro(numRegistro));
                            this.simulacion.desbloquear_Posicion_CacheDatosN1(posicionCacheN1);
                            termine = true;
                            break;
                    }
                }else{// La etiqueta no corresponde al bloque
                    switch (bloqueDatosN1.getEstado()){
                        case MODIFICADO:
                        case INVALIDO:
                        case COMPARTIDO:

                            break;
                    }

                }
            }
            else{
                this.esperarTick(false);
            }
        }
    }


    public void lw(Hilo hiloEjecucion, int numRegistro, int direccionMemoria){
        int posicionCacheN1 = this.simulacion.getPosicionCacheN1(direccionMemoria);
        int posicionCacheN0 = this.simulacion.getPosicionCacheN0(direccionMemoria);
        int numeroBloque = this.simulacion.getNumeroBloque(direccionMemoria);
        boolean termine = false;
        while (!termine){
            if(this.simulacion.intentar_pedirPosicion_CacheDatosN1(posicionCacheN1)){ //logré bloquearlo
                BloqueDatos bloqueDatosN1 = this.simulacion.getBloqueCacheDatosN1(direccionMemoria);
                int posicionPalabra = this.simulacion.getNumeroPalabra(direccionMemoria);
                if(bloqueDatosN1.getEtiqueta() == numeroBloque ){ // la etiqueta corresponde al bloque
                    if(bloqueDatosN1.getEstado() == Estado.INVALIDO){//es invalido
                        termine = buscarBloqueEnOtraCacheLW(numRegistro, direccionMemoria, posicionCacheN1, posicionCacheN0, numeroBloque, termine, bloqueDatosN1, posicionPalabra, hiloEjecucion);
                    }else { // no es invalido
                        hiloEjecucion.setRegistro(numRegistro,bloqueDatosN1.getPalabra()[posicionPalabra]);
                        this.simulacion.desbloquear_Posicion_CacheDatosN1(posicionCacheN1);
                        termine = true;
                    }
                }else{// La etiqueta no corresponde al bloque
                    switch (bloqueDatosN1.getEstado()){
                        case MODIFICADO:
                            if (this.simulacion.intentar_pedirBusDatos_Memoria()) { // logré bloquear el bus de datos-memoria
                                this.simulacion.setBloqueCacheDatosMemoria(bloqueDatosN1,numeroBloque);
                                this.esperar40Ticks();
                                this.simulacion.desbloquear_BusDatos_Memoria();
                                termine = buscarBloqueEnOtraCacheLW(numRegistro, direccionMemoria, posicionCacheN1, posicionCacheN0, numeroBloque, termine, bloqueDatosN1, posicionPalabra, hiloEjecucion);
                            }else{ //no logré bloquear el bus de datos-memoria
                                this.simulacion.desbloquear_Posicion_CacheDatosN1(posicionCacheN1);
                                this.esperarTick(false);
                            }
                            break;
                        case INVALIDO:
                        case COMPARTIDO:
                            termine = buscarBloqueEnOtraCacheLW(numRegistro, direccionMemoria, posicionCacheN1, posicionCacheN0, numeroBloque, termine, bloqueDatosN1, posicionPalabra, hiloEjecucion);
                            break;
                    }

                }
            }
            else{ // no logré bloquear mi bloque de caché
                this.esperarTick(false);
            }
        }
    }

    private boolean buscarBloqueEnOtraCacheLW(int numRegistro, int direccionMemoria, int posicionCacheN1, int posicionCacheN0, int numeroBloque, boolean termine, BloqueDatos bloqueDatosN1, int posicionPalabra, Hilo hiloEjecucion) {
        if(this.simulacion.intentar_pedirBusDatos_Memoria()){ // Logró bloquear el bus
            if(this.simulacion.intentar_pedirPosicion_CacheDatosN0(posicionCacheN0)){ // lo logré
                BloqueDatos bloqueDatosN0 = this.simulacion.getBloqueCacheDatosN0(direccionMemoria);
                BloqueDatos bloqueACargar;
                if(bloqueDatosN0.getEtiqueta() == numeroBloque){ // la etiqueta es el bloque que ocupo
                    switch (bloqueDatosN0.getEstado()){
                        case COMPARTIDO:
                            bloqueACargar = new BloqueDatos(bloqueDatosN0);
                            break;
                        case INVALIDO:
                            bloqueACargar = new BloqueDatos(this.simulacion.getBloqueMemoriaDatos(direccionMemoria),numeroBloque,Estado.COMPARTIDO);
                            this.esperar40Ticks();
                            break;
                        case MODIFICADO:
                            this.simulacion.getBloqueCacheDatosN0(direccionMemoria).setEstado(Estado.COMPARTIDO);
                            bloqueACargar = new BloqueDatos(bloqueDatosN0);
                            //todo verificar que esto sea cierto
                            this.simulacion.setBloqueCacheDatosMemoria(bloqueACargar,numeroBloque);
                            this.esperar40Ticks();
                            break;
                        default:
                            bloqueACargar= null;
                            break;
                    }
                }else{ //la etiqueta no es el bloque que ocupo
                    bloqueACargar = new BloqueDatos(this.simulacion.getBloqueMemoriaDatos(direccionMemoria),numeroBloque,Estado.COMPARTIDO);
                    this.esperar40Ticks();
                }
                this.simulacion.setBloqueCacheDatosN1(bloqueACargar,posicionCacheN1);
                //Meto en el registro el valor y desbloqueo recursos
                hiloEjecucion.setRegistro(numRegistro,bloqueDatosN1.getPalabra()[posicionPalabra]);
                this.simulacion.desbloquear_Posicion_CacheDatosN0(posicionCacheN0);
                this.simulacion.desbloquear_BusDatos_Memoria();
                this.simulacion.desbloquear_Posicion_CacheDatosN1(posicionCacheN1);
                termine = true;
            }else{//No logré bloquear la posicion del otro caché, entonces desbloqueo bus y mi posicion
                this.simulacion.desbloquear_BusDatos_Memoria();
                this.simulacion.desbloquear_Posicion_CacheDatosN1(posicionCacheN1);
                this.esperarTick(false);
            }
        }else{ //no logré bloquear el bus de datos-memoria, vuelvo a intentar la iteracion y desbloqueo la posicion
            this.simulacion.desbloquear_Posicion_CacheDatosN1(posicionCacheN1);
            this.esperarTick(false);
        }
        return termine;
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

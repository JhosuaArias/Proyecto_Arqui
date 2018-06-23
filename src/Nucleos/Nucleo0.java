package Nucleos;

import Caches.BloqueDatos;
import Caches.BloqueInstrucciones;
import Caches.Estado;
import Estructuras_Datos.Hilo;
import Estructuras_Datos.Instruccion;
import MVC.Simulacion;
import javafx.util.Pair;

import java.util.Arrays;

public class Nucleo0 extends Nucleo{

    private Thread thread0;
    private Thread thread1;

    private  Pair<EstadoThread,Integer> estadoThread0;
    private  Pair<EstadoThread,Integer> estadoThread1;

    private Hilo hiloThread0;
    private Hilo hiloThread1;

    public Nucleo0(Simulacion simulacion, int id){
        super(simulacion,id);
        this.thread0 = new Thread(this,"Thread 0");
        this.thread1 = new Thread(this,"Thread 1");

        this.estadoThread0 = new Pair<>(EstadoThread.EJECUTANDO,-1);
        this.estadoThread1 = new Pair<>(EstadoThread.ESPERANDO,-1);

        this.thread0.start();
        this.thread1.start();
    }


    public synchronized Pair<EstadoThread,Integer> getEstado(){
        if(Thread.currentThread().getId() == this.thread0.getId()){
            return estadoThread0;
        } else if(Thread.currentThread().getId() == this.thread1.getId()) {
            return estadoThread1;
        }else{
            System.err.println("No debería pasar");
            return null;
        }
    }

    public synchronized void setEstado(EstadoThread estado, int posicion ,int thread) {
        if(thread == 0) {
            this.estadoThread0 = new Pair<EstadoThread,Integer>(estado,posicion);
        }else{
            this.estadoThread1 = new Pair<EstadoThread,Integer>(estado,posicion);
        }
    }

    /**Flujo del Núcleo**/

    private void restarQuantum() {
        if(Thread.currentThread().getId() == thread0.getId()) {
           this.hiloThread0.restarQuantum();
        }else if(Thread.currentThread().getId() == thread1.getId()){
            this.hiloThread1.restarQuantum();
        }else {
            System.err.println("No debería pasar");
        }
    }

    private void  iteracion(){
        if(Thread.currentThread().getId() == thread0.getId()) {
            this.iteracionHilo(this.hiloThread0);
        }else if(Thread.currentThread().getId() == thread1.getId()){
            this.iteracionHilo(this.hiloThread1);
        }else {
            System.err.println("No debería pasar");
        }
    }

    private void iteracionHilo(Hilo hilo){

        boolean mismoHilo = true;

        while (mismoHilo) {
            /**Calculamos el bloque y posicion en caché*/
            int pc = hilo.getPc();
            int numeroBloque = this.simulacion.getNumeroBloque(pc);

            BloqueInstrucciones bloqueInstrucciones = this.simulacion.getBloqueCacheInstruccionesN0(pc);
            /**Verificamos si hay fallo de caché**/
            if (!(bloqueInstrucciones.getEstado() == Estado.COMPARTIDO && bloqueInstrucciones.getEtiqueta() == numeroBloque)) {
                //Hay fallo
                this.resolverFalloCacheInstrucciones(pc);
                bloqueInstrucciones = this.simulacion.getBloqueCacheInstruccionesN0(pc);

            }
            /**Se agarra la instrucción**/
            int posicionCache = this.simulacion.getPosicionBloque(pc);
            hilo.setIr(bloqueInstrucciones.getInstruccion(posicionCache));

            System.err.println(Arrays.toString(hilo.getIr().getPalabra()));
            /**Se suma el PC**/
            hilo.sumarPc();

            /**Se ejecuta la instruccion**/
            this.ejecutar_instruccion(hilo,this, null);

            /**Verificaciones de fin o quantum**/
            if (hilo.isEsFin()) {
                mismoHilo = false;
                this.simulacion.setInactivoHilo(hilo.getId());
                hilo = null;
                /**Esperar un tick**/
                this.esperarTick(false);
            } else if (hilo.getQuantumRestante() == 1) {
                this.devolverHilo();
                hilo.reiniciarQuantum();
                hilo = null;
                mismoHilo = false;
                /**Esperar un tick**/
                this.esperarTick(false);
            }else{
                /**Esperar un tick**/
                this.esperarTick(true);
            }

        }
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
         * Aqui hay que reservar el bus
         */
        boolean bloqueado=false;

        while (!bloqueado) {
            if(!this.simulacion.intentar_pedirBusInstruc_Memoria()){
                this.esperarTick(false);
            }
            else {
                bloqueado=true;
            }
        }

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
        int posicion = this.simulacion.getPosicionCacheN0(pc);
        this.simulacion.setBloqueCacheInstruccionesN0(new BloqueInstrucciones(ins,numeroBloque,Estado.COMPARTIDO), posicion);

        this.simulacion.desbloquear_BusInstruc_Memoria();

    }

    private void escogerHilo() {

        if (Thread.currentThread().getId() == thread0.getId()) {
            this.hiloThread0 = this.simulacion.pedirHiloCola();
        } else if (Thread.currentThread().getId() == thread1.getId()) {
            this.hiloThread1 = this.simulacion.pedirHiloCola();
        } else {
            System.err.println("No debería pasar");
        }
    }

    private void devolverHilo(){
        if(Thread.currentThread().getId() == thread0.getId()) {
            this.simulacion.devolverHiloCola(this.hiloThread0);
            this.hiloThread0 = null;
        }else if(Thread.currentThread().getId() == thread1.getId()){
            this.simulacion.devolverHiloCola(this.hiloThread1);
            this.hiloThread1 = null;
        }else {
            System.err.println("No debería pasar");
        }
    }

    private Hilo getHiloThread() {
        if(Thread.currentThread().getId() == thread0.getId()) {
           return hiloThread0;
        }else if(Thread.currentThread().getId() == thread1.getId()){
            return hiloThread0;
        }else {
            System.err.println("No debería pasar");
        }
        return null;
    }


    @Override
    public void esperarTick(boolean restarQuantum){
        super.esperarTick(restarQuantum);
        if(restarQuantum)
            this.restarQuantum();
        this.simulacion.esperarSegundaBarrera();
    }

    @Override
    public void run() {
        super.run();

        while (!this.simulacion.isColaNull()){
            if(this.getEstado().getKey() == EstadoThread.EJECUTANDO) {
                this.escogerHilo();
                if(this.getHiloThread() == null){
                    this.esperarTick(false);
                } else {
                    this.iteracion();
                }
            }else {
                this.esperarTick(false);
            }

        }
        System.err.println("Terminé: " + Thread.currentThread().getName());
    }

    /******************************************/
    public void lw(int direccionMemoria){

        boolean noTermine=true;

        while (noTermine)
        {

            int posicion = simulacion.getPosicionCacheN0(direccionMemoria); //posicion en el cache

            if(!(simulacion.intentar_pedirPosicion_CacheDatosN0(posicion))) //No bloquee el indice, vuelve a intentar
            {
                //esperarTick();
            }

            else //Logre bloquear el indice, sigo
            {
                BloqueDatos bloqueCacheDatos= simulacion.getBloqueCacheDatosN0(direccionMemoria); //Obtengo el bloque del cache

                if (bloqueCacheDatos.getEtiqueta()!=simulacion.getNumeroBloque(direccionMemoria)) //La etiqueta no corresponde al bloque
                {
                    antesFalloDeCache();

                    if (!simulacion.intentar_pedirBusDatos_Memoria()) //No pude bloquear el bus
                    {
                        simulacion.desbloquear_Posicion_CacheDatosN0(posicion);
                        // esperarTick();
                    }

                    else //Pude bloquear el bus
                    {
                        if (bloqueCacheDatos.getEstado()== Estado.MODIFICADO) //Es otra etiqueta y esta modificado
                        {
                            /*Esperar 40 tics, cargar el bloque victima a memoria e invalidar*/
                        }

                        int posicionOtroExtremo = simulacion.getPosicionCacheN1(direccionMemoria);

                        if (!(simulacion.intentar_pedirPosicion_CacheDatosN1(posicionOtroExtremo))) //No pude bloquear el otro indice
                        {
                            simulacion.desbloquear_Posicion_CacheDatosN0(posicion);
                            simulacion.desbloquear_BusDatos_Memoria();
                            // esperarTick();
                        }
                        else //pude bloquear el otro indice

                        {
                            lwVerificarOtroCache_vengodeN0(direccionMemoria);
                        }

                    }


                }
                else //La etiqueta corresponde al bloque
                {
                    if(bloqueCacheDatos.getEstado()== Estado.INVALIDO)  //La etiqueta esta invalida
                    {
                        antesFalloDeCache();

                        if (!simulacion.intentar_pedirBusDatos_Memoria()) //No pude bloquear el bus
                        {
                            simulacion.desbloquear_Posicion_CacheDatosN0(posicion);
                            // esperarTick();
                        }

                        else
                        {
                            int posicionOtroExtremo = simulacion.getPosicionCacheN1(direccionMemoria);

                            if (!(simulacion.intentar_pedirPosicion_CacheDatosN1(posicionOtroExtremo))) //No pude bloquear el otro indice
                            {
                                simulacion.desbloquear_Posicion_CacheDatosN0(posicion);
                                simulacion.desbloquear_BusDatos_Memoria();
                                //   esperarTick();
                            }
                            else //Pude bloquear el otro indice
                            {
                                lwVerificarOtroCache_vengodeN0(direccionMemoria);
                            }

                        }


                    }

                    else //La etiqueta no esta invalida
                    {
                        /*Averiguo la palabra, cargo a registro, desbloqueo la posicion*/

                        simulacion.desbloquear_Posicion_CacheDatosN0(posicion);

                        /*Indicar que no se vuelva a meter en el while*/
                        noTermine=false;
                    }
                }

            }

        }
    }
    /***********************************************************/

    public  void lwVerificarOtroCache_vengodeN0(int direccionMemoria)
    {
        /*vengo de bloquear el indice del otro cache*/

        BloqueDatos bloqueCacheDatosOtroExtremo = simulacion.getBloqueCacheDatosN1(direccionMemoria);
        if (bloqueCacheDatosOtroExtremo.getEtiqueta()== simulacion.getNumeroBloque(direccionMemoria) && bloqueCacheDatosOtroExtremo.getEstado() == Estado.MODIFICADO) //Corresponde a la etiqueta y esta modificado
        {
            /*Diagrama en morado*/
        }
        else //No corresponde la etiqueta o no esta modificado
        {
            /*Diagrama en anaranjado*/
        }

        seResolvioFalloDeCache();

    }
    /********************************/
    public void seResolvioFalloDeCache(){

    }

    /*********************************/
    public void antesFalloDeCache() {

    }
    /*******************************/
    public void sw() {

    }
    /*****************************************************/

    /**Getters**/
    public Hilo getHiloThread0() {
        return hiloThread0;
    }

    public Hilo getHiloThread1() {
        return hiloThread1;
    }

    public Pair<EstadoThread, Integer> getEstadoThread0() {
        return estadoThread0;
    }

    public Pair<EstadoThread, Integer> getEstadoThread1() {
        return estadoThread1;
    }


}



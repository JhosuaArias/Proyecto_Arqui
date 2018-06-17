package Nucleos;

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
        //TODO CAMBIAR.... PONER MÁS CONDICIONES DE ESPERA
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
            this.ejecutar_instruccion(hilo);

            /**Verificaciones de fin o quantum**/
            if (hilo.isEsFin()) {
                mismoHilo = false;
                this.simulacion.setInactivoHilo(hilo.getId());
                hilo = null;
                /**Esperar un tick**/
                this.esperarTick(false);
            } else if (hilo.getQuantumRestante() == 1) {
                this.simulacion.devolverHiloCola(hilo);
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

        this.setEstado(EstadoThread.FALLO_CACHE_DATOS, pc,0);
        this.setEstado(EstadoThread.EJECUTANDO, pc,1);

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
            if(!this.simulacion.intentar_pedirBusInstruc_Memoria() && !this.simulacion.intentar_reservarPosicion_CacheInstrucN0(pc)){
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
        int posicion = this.simulacion.getPosicionCacheN1(pc);
        this.simulacion.setBloqueCacheInstruccionesN1(new BloqueInstrucciones(ins,numeroBloque,Estado.COMPARTIDO), posicion);

        this.simulacion.desbloquear_BusInstruc_Memoria();

    }

    private void escogerHilo(){
        boolean repetir = true;

        while (repetir) {
            if(Thread.currentThread().getId() == thread0.getId()) {
                if((this.hiloThread0 = this.simulacion.pedirHiloCola()) != null){
                    repetir = false;
                } else {
                  this.esperarTick(false);
                }
            }else if(Thread.currentThread().getId() == thread1.getId()){
                if((this.hiloThread1 = this.simulacion.pedirHiloCola()) != null) {
                    repetir = false;
                } else {
                    this.esperarTick(false);
                }
            }else {
                System.err.println("No debería pasar");
            }
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


    @Override
    public void esperarTick(boolean restarQuantum){
        super.esperarTick(restarQuantum);
        if(restarQuantum)
            this.restarQuantum();
    }

    @Override
    public void run() {
        super.run();

        while (!this.simulacion.isColaNull()){
//            if(this.getEstado() == EstadoThread.EJECUTANDO) {
//                this.escogerHilo();
//                this.iteracion();
//            }
            this.esperarTick(false);
        }
        System.err.println("Terminé: " + Thread.currentThread().getName());
    }
}

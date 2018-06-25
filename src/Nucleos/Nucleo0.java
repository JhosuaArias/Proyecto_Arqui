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

public class Nucleo0 extends Nucleo{

    private Thread thread;


    private  Pair<EstadoThread,Integer> estadoHilo;

    private Hilo hilo;


    public Nucleo0(Simulacion simulacion, int id){
        super(simulacion,id);
        this.thread = new Thread(this,"Thread 0");

        this.estadoHilo = new Pair<>(EstadoThread.EJECUTANDO,-1);

        this.thread.start();
    }

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
         * Nota: N0 no requiere de reservar el bus
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
        int posicion = this.simulacion.getPosicionCacheN0(pc);
        this.simulacion.setBloqueCacheInstruccionesN0(new BloqueInstrucciones(ins,numeroBloque,COMPARTIDO), posicion);

        this.simulacion.desbloquear_BusInstruc_Memoria();
        this.setEstado(EstadoThread.EJECUTANDO,-1);
    }


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

            System.err.println(Arrays.toString(this.hilo.getIr().getPalabra()));
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
//            this.hilo = this.simulacion.pedirHiloCola();
//            if(this.hilo == null){
                this.esperarTick(false);
//            } else {
//                this.iteracion();
//            }
        }
        System.err.println("Terminé: " + Thread.currentThread().getName());
    }


    public void setEstado(EstadoThread estado, int posicion){
        this.estadoHilo = new Pair<>(estado,posicion);
    }

    /**Getters**/
    public Hilo getHilo() {
        return hilo;
    }


    public Pair<EstadoThread, Integer> getEstadoHilo() {
        return estadoHilo;
    }


    public Pair<EstadoThread,Integer> getEstado() {
        return this.estadoHilo;
    }
}



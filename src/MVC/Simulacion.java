package MVC;

import Caches.CacheDatos;
import Caches.CacheInstrucciones;
import Estructuras_Datos.Cola;
import Estructuras_Datos.Hilo;
import Estructuras_Datos.Instruccion;
import Estructuras_Datos.MemoriaPrincipal;
import IO.LectorHilos;
import Nucleos.Nucleo0;
import Nucleos.Nucleo1;

import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.ReentrantLock;

public class Simulacion {
    /**IO**/
    private LectorHilos lectorHilos;
    private Terminal terminal;

    /**Threads Control**/
    private CyclicBarrier barrier;

    private ReentrantLock busNucleo0CacheDatos;
    private ReentrantLock busNucleo0CacheInstruc;

    private ReentrantLock busNucleo1CacheDatos;
    private ReentrantLock busNucleo1CacheInstruc;

    private ReentrantLock busCacheDatos_Memoria;
    private ReentrantLock busCacheInstruc_Memoria;

    private ReentrantLock reservaCacheDatos;
    private ReentrantLock reservaCacheInstruc;

    /**Simulación**/
    private  ArrayList<Hilo> hilos;
    private int numeroHilos;
    private boolean isSlow;
    private int quantum;
    private boolean[] hilosActivos; // false: inactivo ; true : activo
    private int ticks;
    /**Componentes**/
    private Cola cola;
    private MemoriaPrincipal memoriaPrincipal;

    private CacheDatos cacheDatosN0;
    private CacheInstrucciones cacheInstruccionesN0;

    private CacheDatos cacheDatosN1;
    private CacheInstrucciones cacheInstruccionesN1;

    private Nucleo0 nucleo0;
    private Nucleo1 nucleo1;

    /**Constantes**/
    private static final int NUMERO_THREADS = 4;
    private static final int BLOQUES_CACHE_N0 = 8;
    private static final int BLOQUES_CACHE_N1 = 4;

    public Simulacion(String[] args, Terminal terminal) {
        this.numeroHilos = args.length;
        this.lectorHilos = new LectorHilos(args);
        this.terminal = terminal;
        this.ticks = 0;
    }

    public void init() {
        this.lectorHilos.setInstrucciones();
        this.quantum = this.terminal.askForQuantum();
        this.isSlow = this.terminal.askForSimulationSpeed();
        this.setHilos();
        this.setCola();
        this.setMemoriaPrincipal(this.lectorHilos.getInstruccionesHilos() , this.hilos);
        this.setCaches();
        this.setNucleos();
        this.setElementosConcurrencia();
        this.runClock();
    }

    private void runClock() {

    }

    /**Setters**/

    private void setHilos(){
        this.hilos = new ArrayList<>();
        this.hilosActivos = new boolean[this.numeroHilos];
        for (int id= 0 ; id < this.numeroHilos ; id++) {
            this.hilos.add(new Hilo("Hilo "+ id , id));
            this.hilosActivos[id] = true;
        }
    }

    private void setCola() {
        this.cola = new Cola();
        for (Hilo hilo : this.hilos) {
            this.cola.add(hilo);
        }
    }

    private void setMemoriaPrincipal(ArrayList<ArrayList<Instruccion>> instrucciones, ArrayList<Hilo> hilos){
        this.memoriaPrincipal = new MemoriaPrincipal();
        this.memoriaPrincipal.setMemoria();
        this.memoriaPrincipal.setInstrucciones(instrucciones,hilos);
    }

    private void setCaches() {
        this.cacheDatosN0 = new CacheDatos(BLOQUES_CACHE_N0);
        this.cacheInstruccionesN0 = new CacheInstrucciones(BLOQUES_CACHE_N0);

        this.cacheDatosN1 = new CacheDatos(BLOQUES_CACHE_N1);
        this.cacheInstruccionesN1 = new CacheInstrucciones(BLOQUES_CACHE_N1);
    }

    private void setNucleos() {
    }

    private void setElementosConcurrencia() {
        this.barrier = new CyclicBarrier(NUMERO_THREADS);
    }

    /**Pedidos de Recursos*/

    public synchronized Hilo pedirHiloCola() {
        return this.cola.poll();
    }

    public synchronized void devolverHiloCola(Hilo hilo) {
        this.cola.add(hilo);
    }

    public void esperarTick(){
        try {
            this.barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

}

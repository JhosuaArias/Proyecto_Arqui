package MVC;

import Caches.BloqueDatos;
import Caches.BloqueInstrucciones;
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

    private ReentrantLock[] posicionesCacheDatosN0;
    private ReentrantLock[] posicionesCacheInstruccionN0;

    private ReentrantLock[] posicionesCacheDatosN1;

    private ReentrantLock[] reservaPosicionesCacheDatosN0;
    private ReentrantLock[] reservaPosicionesCacheIntruccionN0;

    private ReentrantLock busCacheDatos_Memoria;
    private ReentrantLock busCacheInstruc_Memoria;

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
    private static final int BLOQUES_DATOS = 24;


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
        while(this.sonHilosActivos()){
            this.esperarTick();
            this.ticks++;
        }

        this.cola = null;

        //TODO ESPERAR A QUE TODOS LOS THREADS ACABEN DE EJECUTAR
    }

    private boolean sonHilosActivos(){
        for(boolean value: this.hilosActivos){
            if(value){ return true;}
        }
        return false;
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

        this.nucleo0 = new Nucleo0(this,0);
        this.nucleo1 = new Nucleo1(this,1);
    }

    private void setElementosConcurrencia() {
        this.barrier = new CyclicBarrier(NUMERO_THREADS);

        this.busCacheDatos_Memoria = new ReentrantLock();
        this.busCacheInstruc_Memoria = new ReentrantLock();

        this.posicionesCacheDatosN0 = new ReentrantLock[BLOQUES_CACHE_N0];
        this.posicionesCacheInstruccionN0 = new ReentrantLock[BLOQUES_CACHE_N0];

        this.posicionesCacheDatosN1 = new ReentrantLock[BLOQUES_CACHE_N1];

        this.reservaPosicionesCacheDatosN0 = new ReentrantLock[BLOQUES_CACHE_N0];
        this.reservaPosicionesCacheIntruccionN0 = new ReentrantLock[BLOQUES_CACHE_N0];

        for (int i = 0; i < BLOQUES_CACHE_N0; i++) {
            this.posicionesCacheDatosN0[i] = new ReentrantLock();
            this.posicionesCacheInstruccionN0[i] = new ReentrantLock();

            this.reservaPosicionesCacheDatosN0[i] = new ReentrantLock();
            this.reservaPosicionesCacheIntruccionN0[i] = new ReentrantLock();
        }

        for (int i = 0; i < BLOQUES_CACHE_N1; i++) {
            this.posicionesCacheDatosN1[i] = new ReentrantLock();
        }

    }

    /**Pedidos de Recursos*/

    public Hilo pedirHiloCola() {
        return this.cola.poll();
    }

    public void devolverHiloCola(Hilo hilo) {
        this.cola.add(hilo);
    }

    public boolean isColaNull() {
        if(this.cola == null){
            return true;
        }else{
            return false;
        }
    }

    public void esperarTick(){
        try {
            this.barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    /*Intenta bloquear*/

    public void intentar_pedirBusInstruc_Memoria() throws InterruptedException {
        this.busCacheInstruc_Memoria.tryLock();
    }

    public void intentar_pedirBusDatos_Memoria() throws InterruptedException {
        this.busCacheDatos_Memoria.tryLock();
    }

    public void intentar_pedirPosicion_CacheDatosN0(int posicion) throws InterruptedException {
        this.posicionesCacheDatosN0[posicion].tryLock();
    }

    public void intentar_pedirPosicion_CacheInstrucN0(int posicion) throws InterruptedException {
        this.posicionesCacheInstruccionN0[posicion].tryLock();
    }

    public void intentar_pedirPosicion_CacheDatosN1(int posicion) throws InterruptedException {
        this.posicionesCacheDatosN1[posicion].tryLock();
    }

    public void intentar_reservarPosicion_CacheDatosN0(int posicion) throws InterruptedException {
        this.reservaPosicionesCacheDatosN0[posicion].tryLock();
    }

    public void intentar_reservarPosicion_CacheInstrucN0(int posicion) throws InterruptedException {
        this.reservaPosicionesCacheIntruccionN0[posicion].tryLock();
    }

    /*Desbloquea*/

    public void desbloquear_BusInstruc_Memoria()  {
        this.busCacheInstruc_Memoria.unlock();
    }

    public void desbloquear_BusDatos_Memoria()  {
        this.busCacheDatos_Memoria.unlock();
    }

    public void desbloquear_Posicion_CacheDatosN0(int posicion)  {
        this.posicionesCacheDatosN0[posicion].unlock();
    }

    public void desbloquear_Posicion_CacheInstrucN0(int posicion) {
        this.posicionesCacheInstruccionN0[posicion].unlock();
    }

    public void desbloquear_Posicion_CacheDatosN1(int posicion) {
        this.posicionesCacheDatosN1[posicion].unlock();
    }

    public void desbloquear_ReservaPosicion_CacheDatosN0(int posicion)  {
        this.reservaPosicionesCacheDatosN0[posicion].unlock();
    }

    public void desbloquear_ReservaPosicion_CacheInstrucN0(int posicion) {
        this.reservaPosicionesCacheIntruccionN0[posicion].unlock();
    }

    /*Mapeo a chaces o memoria dada una direccion de memoria*/


    /*Devolver un bloque de Cache instrucciones*/

    public BloqueInstrucciones getPalabraCacheInstrucciones(int direccionMemoria, int nucleo)
    {
        BloqueInstrucciones bloqueDevolver = null;

        int numeroBloque = (direccionMemoria / 16);
        int direccionPalabra = (direccionMemoria -  (16 * numeroBloque)) /  4;

        if (nucleo==0) //Estoy en el nucleo 0
        {
            int posicionCache =  getPosicionCacheN0(direccionMemoria);

            if ( numeroBloque>=BLOQUES_DATOS) //Utilizo la cache de instrucciones
            {bloqueDevolver=cacheInstruccionesN0.getBloque(posicionCache);}

        }

        	else //Soy N1
        {
            int posicionCache = getPosicionCacheN1(direccionMemoria);


            if (numeroBloque>=24) //Utilizo la cache de instrucciones
            {bloqueDevolver= cacheInstruccionesN0.getBloque(posicionCache);}
            //else {return cacheDatosN1[posicionCache] [direccionPalabra];}
        }
        return  bloqueDevolver;
    }

    public BloqueDatos getPalabraCacheDatos(int direccionMemoria, int nucleo)
    {
        BloqueDatos bloqueDevolver = null;

        int numeroBloque = (direccionMemoria / 16);
        int direccionPalabra = (direccionMemoria -  (16 * numeroBloque)) /  4;

        if (nucleo==0) //Estoy en el nucleo 0
        {
            int posicionCache = getPosicionCacheN0(direccionMemoria);

            if ( numeroBloque<BLOQUES_DATOS) //Utilizo la cache de Datos
            {return cacheDatosN0.getBloque(posicionCache);}
        }

        else //Soy N1
        {
            int posicionCache = getPosicionCacheN1(direccionMemoria);

            if (numeroBloque<24) //Utilizo la cache de Datos
            {return cacheDatosN1.getBloque(posicionCache);}
        }
        return  bloqueDevolver;
    }


    public Instruccion[] getBloqueMemoriaInstruccion(int direccionMemoria) {

        Instruccion[] instruccionDevolver = null;
        int numeroBloque = (direccionMemoria / 16);

        if (numeroBloque >= 24) //Utilizo la cache de instrucciones
        {
            instruccionDevolver= memoriaPrincipal.getBloqueInstrucciones(numeroBloque);
        }

               return instruccionDevolver;
    }

    public int[] getBloqueMemoriaDatos(int direccionMemoria) {

        int[] instruccionDevolver = null;

        int numeroBloque = (direccionMemoria / 16);

        if (numeroBloque < 24) //Utilizo la cache de Datos
        {
            instruccionDevolver=memoriaPrincipal.getBloqueDatos(numeroBloque);
        }

        return instruccionDevolver;
    }

    public int getPosicionCacheN0(int direccionMemoria)
    {
        return (direccionMemoria / 16) % BLOQUES_CACHE_N0;
    }

    public int getPosicionCacheN1 (int direccionMemoria)
    {
        return (direccionMemoria / 16) % BLOQUES_CACHE_N1;
    }

}

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
    private final CyclicBarrier barrier = new CyclicBarrier(NUMERO_THREADS);

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
    private static final int BYTES_BLOQUE = 16;


    int i = 0;

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
            System.out.println("Este es el Tick número: " + ticks);

        }

        this.cola = null;

        try {
            for (int i = 0 ; i < (NUMERO_THREADS - 1); i++) {
                wait();
            }

            wait(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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
            this.hilos.add(new Hilo("Hilo "+ id , id, this.quantum));
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
        return this.cola == null;
    }

    public void esperarTick(){
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    /**Intenta bloquear locks**/

    public boolean intentar_pedirBusInstruc_Memoria() {
        return this.busCacheInstruc_Memoria.tryLock();
    }

    public boolean intentar_pedirBusDatos_Memoria() {
        return this.busCacheDatos_Memoria.tryLock();
    }

    public boolean intentar_pedirPosicion_CacheDatosN0(int posicion) {
        return this.posicionesCacheDatosN0[posicion].tryLock();
    }

    public boolean intentar_pedirPosicion_CacheInstrucN0(int posicion) {
        return this.posicionesCacheInstruccionN0[posicion].tryLock();
    }

    public boolean intentar_pedirPosicion_CacheDatosN1(int posicion) {
        return this.posicionesCacheDatosN1[posicion].tryLock();
    }

    public boolean intentar_reservarPosicion_CacheDatosN0(int posicion) {
        return this.reservaPosicionesCacheDatosN0[posicion].tryLock();
    }

    public boolean intentar_reservarPosicion_CacheInstrucN0(int posicion) {
        return this.reservaPosicionesCacheIntruccionN0[posicion].tryLock();
    }

    /**Desbloquea locks**/

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

    /*Mapeo de bloques y direcciones de memoria*/

    public int getNumeroBloque(int direccionMemoria){
        return (direccionMemoria/BYTES_BLOQUE);
    }


    public int getPosicionCacheN0(int direccionMemoria)
    {
        return this.getNumeroBloque(direccionMemoria)%BLOQUES_CACHE_N0;
    }

    public int getPosicionCacheN1 (int direccionMemoria)
    {
        return this.getNumeroBloque(direccionMemoria)%BLOQUES_CACHE_N1;
    }


    /*Devolver un bloque de Cache instrucciones*/

    public BloqueInstrucciones getBloqueCacheInstrucciones(int direccionMemoria, int nucleo)
    {
        BloqueInstrucciones bloqueDevolver = null;

        int numeroBloque = this.getNumeroBloque(direccionMemoria);

        if (nucleo==0) //Estoy en el nucleo 0
        {
            int posicionCache =  this.getPosicionCacheN0(direccionMemoria);

            if (numeroBloque>=BLOQUES_DATOS) //Utilizo la cache de instrucciones
            {bloqueDevolver=cacheInstruccionesN0.getBloque(posicionCache);}

        }else{ //Soy N1

            int posicionCache = getPosicionCacheN1(direccionMemoria);


            if (numeroBloque>=BLOQUES_DATOS) //Utilizo la cache de instrucciones
            {bloqueDevolver= cacheInstruccionesN0.getBloque(posicionCache);}
            //else {return cacheDatosN1[posicionCache] [direccionPalabra];}
        }
        return  bloqueDevolver;
    }

    public BloqueDatos getBloqueCacheDatos(int direccionMemoria, int nucleo)
    {
        BloqueDatos bloqueDevolver = null;

        int numeroBloque = this.getNumeroBloque(direccionMemoria);

        if (nucleo==0) //Estoy en el nucleo 0
        {
            int posicionCache = getPosicionCacheN0(direccionMemoria);

            if ( numeroBloque<BLOQUES_DATOS) //Utilizo la cache de Datos
            {bloqueDevolver = cacheDatosN0.getBloque(posicionCache);}
        }

        else //Soy N1
        {
            int posicionCache = getPosicionCacheN1(direccionMemoria);

            if (numeroBloque<BLOQUES_DATOS) //Utilizo la cache de Datos
            {bloqueDevolver = cacheDatosN1.getBloque(posicionCache);}
        }
        return  bloqueDevolver;
    }


    public Instruccion[] getBloqueMemoriaInstruccion(int direccionMemoria) {

        Instruccion[] instruccionDevolver = null;
        int numeroBloque = this.getNumeroBloque(direccionMemoria);

            if (numeroBloque >= BLOQUES_DATOS) //Utilizo la cache de instrucciones
        {
            instruccionDevolver= memoriaPrincipal.getBloqueInstrucciones(numeroBloque);
        }

               return instruccionDevolver;
    }

    public int[] getBloqueMemoriaDatos(int direccionMemoria) {

        int[] instruccionDevolver = null;

        int numeroBloque = this.getNumeroBloque(direccionMemoria);

        if (numeroBloque < BLOQUES_DATOS) //Utilizo la cache de Datos
        {
            instruccionDevolver=memoriaPrincipal.getBloqueDatos(numeroBloque);
        }

        return instruccionDevolver;
    }


    public void setBloqueCacheInstruccionesN1(BloqueInstrucciones bloque, int posicion){
     cacheInstruccionesN1.setBloque(bloque, posicion);
    }

    public void setInactivoHilo(int posicion) {
        this.hilosActivos[posicion] = false;
    }


}

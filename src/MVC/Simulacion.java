package MVC;

import Caches.*;
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

import static java.lang.Thread.sleep;

public class Simulacion {
    /**IO**/
    private LectorHilos lectorHilos;
    private Terminal terminal;

    /**Threads Control**/
    private final CyclicBarrier barrier = new CyclicBarrier(NUMERO_THREADS);
    private final CyclicBarrier finalBarrier = new CyclicBarrier(NUMERO_THREADS);

    private ReentrantLock[] posicionesCacheDatosN0;
    private ReentrantLock[] posicionesCacheInstruccionN0;

    private ReentrantLock[] posicionesCacheDatosN1;


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
    private static final int NUMERO_THREADS = 3;
    private static final int BLOQUES_CACHE_N0 = 8;
    private static final int BLOQUES_CACHE_N1 = 4;
    private static final int BLOQUES_DATOS = 24;
    private static final int BYTES_BLOQUE = 16;
    private static final int PALABRAS_BLOQUE = 4;

    /***
     * Constructor Simulacion.
     * @param args String array con los hilos que serán ejecutados.
     * @param terminal Referencia de la terminal para UI.
     */
    public Simulacion(String[] args, Terminal terminal) {
        this.numeroHilos = args.length;
        this.lectorHilos = new LectorHilos(args);
        this.terminal = terminal;
        this.ticks = 0;
    }

    /***
     * Inicializa los recursos neceesarios para la simulación.
     */
    public void init() {
        this.lectorHilos.setInstrucciones();
        this.quantum = this.terminal.askForQuantum();
        this.isSlow = this.terminal.askForSimulationSpeed();
        this.setHilos();
        this.setCola();
        this.setMemoriaPrincipal(this.lectorHilos.getInstruccionesHilos() , this.hilos);
        this.setCaches();
        this.setElementosConcurrencia();
        this.setNucleos();
        this.runClock();
    }

    /***
     * Método principal de simulación, es un loop que lleva el reloj, imprime datos, espera en barrera
     * y maneja los demás threads para que finalicen.
     */
    private void runClock() {
        while(this.sonHilosActivos()){
            this.esperarTick();
            if (this.isSlow) {
                if (this.ticks%20 == 0) {
                    this.terminal.imprimirTick(this);
                    this.terminal.esperarUsuario();
                }
            }
            this.ticks++;
            esperarSegundaBarrera();
        }

        this.esperarTick();
        this.cola = null;
        this.esperarSegundaBarrera();


        try {
            Thread.sleep(1000);
            this.terminal.imprimirEstadoFinal(this);
            System.out.println("Finalizando Simulacion...");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /***
     * Método que verifica si todos los hilos han finalizado su ejecución.
     * @return true: si algún hilo no ha terminado, false: si todos los hilos han terminado.
     */
    private boolean sonHilosActivos(){
        for(boolean value: this.hilosActivos){
            if(value){ return true;}
        }
        return false;
    }

    /***
     * Inicializa un array con todos los hilos y otro que indica si aún no han fiinalizado su ejecución.
     */
    private void setHilos(){
        this.hilos = new ArrayList<>();
        this.hilosActivos = new boolean[this.numeroHilos];
        for (int id= 0 ; id < this.numeroHilos ; id++) {
            this.hilos.add(new Hilo("Hilo "+ id , id, this.quantum));
            this.hilosActivos[id] = true;
        }
    }

    /***
     * Inicializa la Cola y la llena con todos los hilos a ejecutar.
     */
    private void setCola() {
        this.cola = new Cola();
        for (Hilo hilo : this.hilos) {
            this.cola.add(hilo);
        }
    }

    /***
     * Inicializa la memoria y la llena con todas las instrucciones de todos los hilos.
     * @param instrucciones una lista de listas con todas las instrucciones.
     * @param hilos Referencia de todos los hilos.
     */
    private void setMemoriaPrincipal(ArrayList<ArrayList<Instruccion>> instrucciones, ArrayList<Hilo> hilos){
        this.memoriaPrincipal = new MemoriaPrincipal();
        this.memoriaPrincipal.setMemoria();
        this.memoriaPrincipal.setInstrucciones(instrucciones,hilos);
    }

    /***
     *1nicializa las caches de datos e instrucciones para Núcleo 0 y Núcleo 1.
     */
    private void setCaches() {
        this.cacheDatosN0 = new CacheDatos(BLOQUES_CACHE_N0);
        this.cacheInstruccionesN0 = new CacheInstrucciones(BLOQUES_CACHE_N0);

        this.cacheDatosN1 = new CacheDatos(BLOQUES_CACHE_N1);
        this.cacheInstruccionesN1 = new CacheInstrucciones(BLOQUES_CACHE_N1);
    }

    /***
     * Inicializa los núcleos de la simulación.
     */
    private void setNucleos() {

        this.nucleo0 = new Nucleo0(this,0);
        this.nucleo1 = new Nucleo1(this,1);
    }

    /***
     * Inicializa todos los elementos de concurrencia de la simulación, como lock y barreras.
     */
    private void setElementosConcurrencia() {

        this.busCacheDatos_Memoria = new ReentrantLock();
        this.busCacheInstruc_Memoria = new ReentrantLock();

        this.posicionesCacheDatosN0 = new ReentrantLock[BLOQUES_CACHE_N0];
        this.posicionesCacheInstruccionN0 = new ReentrantLock[BLOQUES_CACHE_N0];

        this.posicionesCacheDatosN1 = new ReentrantLock[BLOQUES_CACHE_N1];


        for (int i = 0; i < BLOQUES_CACHE_N0; i++) {
            this.posicionesCacheDatosN0[i] = new ReentrantLock();
            this.posicionesCacheInstruccionN0[i] = new ReentrantLock();
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

    public void esperarSegundaBarrera(){
        try {
            this.finalBarrier.await();
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


    /**Mapeo de bloques y direcciones de memoria**/

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

    public int getPosicionBloque(int direccionMemoria){
        return (direccionMemoria/4)%4;
    }

    /**Devolver un bloque de Cache instrucciones**/

    public BloqueInstrucciones getBloqueCacheInstruccionesN0(int direccionMemoria)
    {

        int posicionCache =  getPosicionCacheN0(direccionMemoria);

        return cacheInstruccionesN0.getBloque(posicionCache);

    }

    public BloqueInstrucciones getBloqueCacheInstruccionesN1(int direccionMemoria)
    {
        int posicionCache = getPosicionCacheN1(direccionMemoria);

        return cacheInstruccionesN1.getBloque(posicionCache);


    }


    public BloqueDatos getBloqueCacheDatosN0(int direccionMemoria) {


        int posicionCache = getPosicionCacheN0(direccionMemoria);

        return cacheDatosN0.getBloque(posicionCache);
    }

    public BloqueDatos getBloqueCacheDatosN1(int direccionMemoria) {

        int posicionCache = getPosicionCacheN1(direccionMemoria);
        return cacheDatosN1.getBloque(posicionCache);
    }


    public int getNumeroPalabra (int direccionMemoria)
    {

        return (direccionMemoria -  (BYTES_BLOQUE * getNumeroBloque(direccionMemoria))) /  PALABRAS_BLOQUE;
    }

    public Instruccion[] getBloqueMemoriaInstruccion(int direccionMemoria) {

        int numeroBloque = getNumeroBloque(direccionMemoria);
        return memoriaPrincipal.getBloqueInstrucciones(numeroBloque);

    }

    public int[] getBloqueMemoriaDatos(int direccionMemoria) {

        int numeroBloque = getNumeroBloque(direccionMemoria);
        return memoriaPrincipal.getBloqueDatos(numeroBloque).clone();
    }

    public void setBloqueCacheDatosN1(int []palabras, Estado estado, int direccionMemoria) {

        BloqueDatos bloqueNuevo= new BloqueDatos();

        bloqueNuevo.setPalabra(palabras);
        bloqueNuevo.setEtiqueta(getNumeroBloque(direccionMemoria));
        bloqueNuevo.setEstado(estado);

        /*Cargo el bloque en la cache*/
        cacheDatosN1.setBloque(bloqueNuevo,getPosicionCacheN1(direccionMemoria));

    }

    public void setBloqueCacheDatosN0(int []palabras, Estado estado, int direccionMemoria) {

        BloqueDatos bloqueNuevo= new BloqueDatos();

        bloqueNuevo.setPalabra(palabras);
        bloqueNuevo.setEtiqueta(getNumeroBloque(direccionMemoria));
        bloqueNuevo.setEstado(estado);

        /*Cargo el bloque en la cache*/
        cacheDatosN0.setBloque(bloqueNuevo,getPosicionCacheN0(direccionMemoria));

    }

    public void setBloqueCacheDatosMemoria(BloqueDatos bloque,int numeroBloque) {

        memoriaPrincipal.setBloque(bloque.getPalabra().clone(),numeroBloque);

    }

    public void setEstadoN1(int posicion,Estado E) {

       cacheDatosN1.setEstado(posicion,E);

    }

    public void setEstadoN0(int posicion,Estado E) {

        cacheDatosN0.setEstado(posicion,E);

    }


    public void setBloqueCacheInstruccionesN1(BloqueInstrucciones bloque, int posicion){
        cacheInstruccionesN1.setBloque(bloque, posicion);
    }

    public void setBloqueCacheInstruccionesN0(BloqueInstrucciones bloqueInstrucciones, int posicion) {
        cacheInstruccionesN0.setBloque(bloqueInstrucciones, posicion);
    }

    public void setBloqueCacheDatosN1(BloqueDatos bloqueDatos, int posicion){
        cacheDatosN1.setBloque(bloqueDatos,posicion);
    }

    public void setBloqueCacheDatosN0(BloqueDatos bloqueDatos, int posicion){
        cacheDatosN0.setBloque(bloqueDatos,posicion);
    }

    public void setPalabraCacheDatosN1(int posicionBloque, int posicionPalabra, int palabra){
        this.cacheDatosN1.setPalabra(posicionBloque,posicionPalabra,palabra);
    }

    public void setPalabraCacheDatosN0(int posicionBloque, int posicionPalabra, int palabra){
        this.cacheDatosN0.setPalabra(posicionBloque,posicionPalabra,palabra);
    }

    public int getPalabraCacheDatosN1(int posicionBloque, int posicionPalabra){
        return this.cacheDatosN1.getPalabra(posicionBloque,posicionPalabra);
    }

    public int getPalabraCacheDatosN0(int posicionBloque, int posicionPalabra){
        return this.cacheDatosN0.getPalabra(posicionBloque,posicionPalabra);
    }

    public void setInactivoHilo(int posicion) {
        this.hilosActivos[posicion] = false;
    }



    /**Getters**/
    public ArrayList<Hilo> getHilos() {
        return hilos;
    }

    public void setHilos(ArrayList<Hilo> hilos) {
        this.hilos = hilos;
    }

    public int getNumeroHilos() {
        return numeroHilos;
    }

    public void setNumeroHilos(int numeroHilos) {
        this.numeroHilos = numeroHilos;
    }

    public boolean isSlow() {
        return isSlow;
    }

    public void setSlow(boolean slow) {
        isSlow = slow;
    }

    public int getQuantum() {
        return quantum;
    }

    public void setQuantum(int quantum) {
        this.quantum = quantum;
    }

    public boolean[] getHilosActivos() {
        return hilosActivos;
    }

    public void setHilosActivos(boolean[] hilosActivos) {
        this.hilosActivos = hilosActivos;
    }

    public int getTicks() {
        return ticks;
    }

    public void setTicks(int ticks) {
        this.ticks = ticks;
    }

    public Cola getCola() {
        return cola;
    }

    public void setCola(Cola cola) {
        this.cola = cola;
    }

    public MemoriaPrincipal getMemoriaPrincipal() {
        return memoriaPrincipal;
    }

    public void setMemoriaPrincipal(MemoriaPrincipal memoriaPrincipal) {
        this.memoriaPrincipal = memoriaPrincipal;
    }

    public CacheDatos getCacheDatosN0() {
        return cacheDatosN0;
    }

    public void setCacheDatosN0(CacheDatos cacheDatosN0) {
        this.cacheDatosN0 = cacheDatosN0;
    }

    public CacheInstrucciones getCacheInstruccionesN0() {
        return cacheInstruccionesN0;
    }

    public void setCacheInstruccionesN0(CacheInstrucciones cacheInstruccionesN0) {
        this.cacheInstruccionesN0 = cacheInstruccionesN0;
    }

    public CacheDatos getCacheDatosN1() {
        return cacheDatosN1;
    }

    public void setCacheDatosN1(CacheDatos cacheDatosN1) {
        this.cacheDatosN1 = cacheDatosN1;
    }

    public CacheInstrucciones getCacheInstruccionesN1() {
        return cacheInstruccionesN1;
    }

    public void setCacheInstruccionesN1(CacheInstrucciones cacheInstruccionesN1) {
        this.cacheInstruccionesN1 = cacheInstruccionesN1;
    }

    public Nucleo0 getNucleo0() {
        return nucleo0;
    }

    public void setNucleo0(Nucleo0 nucleo0) {
        this.nucleo0 = nucleo0;
    }

    public Nucleo1 getNucleo1() {
        return nucleo1;
    }

    public void setNucleo1(Nucleo1 nucleo1) {
        this.nucleo1 = nucleo1;
    }


}

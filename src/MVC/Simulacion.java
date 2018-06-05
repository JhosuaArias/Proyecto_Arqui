package MVC;

import Estructuras_Datos.Cola;
import Estructuras_Datos.Hilo;
import Estructuras_Datos.Instruccion;
import Estructuras_Datos.MemoriaPrincipal;
import IO.LectorHilos;

import java.util.ArrayList;

public class Simulacion {
    /**IO**/
    private LectorHilos lectorHilos;
    private Terminal terminal;

    /**Simulación**/
    private  ArrayList<Hilo> hilos;
    private int numeroHilos;
    private boolean isSlow;
    private int quantum;
    boolean[] hilosActivos; // false: inactivo ; true : activo

    /**Componentes**/
    private Cola cola;
    private MemoriaPrincipal memoriaPrincipal;

    public Simulacion(String[] args, Terminal terminal) {
        this.numeroHilos = args.length;
        this.lectorHilos = new LectorHilos(args);
        this.terminal = terminal;
    }

    public void init() {
        this.lectorHilos.setInstrucciones();
        this.quantum = this.terminal.askForQuantum();
        this.isSlow = this.terminal.askForSimulationSpeed();
        this.setHilos();
        this.setCola();
        this.setMemoriaPrincipal(this.lectorHilos.getInstruccionesHilos() , this.hilos);
        this.setCaches();
    }

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
    }



}

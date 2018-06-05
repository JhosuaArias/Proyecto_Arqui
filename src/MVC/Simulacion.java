package MVC;

import IO.LectorHilos;

public class Simulacion {
    /**IO**/
    private LectorHilos lectorHilos;
    private Terminal terminal;

    /**Simulación**/
    private boolean isSlow;
    private int quantum;

    public Simulacion(String[] args, Terminal terminal) {
        this.lectorHilos = new LectorHilos(args);
        this.terminal = terminal;
    }

    public void init() {
        this.lectorHilos.setInstrucciones();
        this.quantum = this.terminal.askForQuantum();
        this.isSlow = this.terminal.askForSimulationSpeed();
    }
}

package Estructuras_Datos;

import java.util.Arrays;

public class Hilo {
    private String nombre;
    private int id;
    private int[] registros;
    private int pc;
    private Instruccion ir;
    private boolean esFin;
    private int quantumRestante;
    private int quantum;

    public Hilo(String nombre, int id, int quantum) {
        this.nombre = nombre;
        this.id = id;
        this.quantumRestante = quantum;
        this.quantum = quantum;
        this.setUp();
    }

    private void setUp() {
        this.registros = new int[32];
        this.pc = 0;
        this.ir = null;
        this.esFin = false;
    }

    public void reiniciarQuantum() {
        this.quantumRestante = this.quantum;
    }

    public void restarQuantum() {
        this.quantumRestante--;
    }

    public void sumarPc(){
        this.pc+=4;
    }

    /**Setters and getters**/
    public int getPc() {
        return pc;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }

    public Instruccion getIr() {
        return ir;
    }

    public void setIr(Instruccion ir) {
        this.ir = ir;
    }

    public boolean isEsFin() {
        return esFin;
    }

    public void setEsFin(boolean esFin) {
        this.esFin = esFin;
    }

    public void setRegistro(int registro, int valor){
        if(registro > 0 && registro < 32) {
            this.registros[registro] = valor;
        }
    }

    public int getRegistro(int registro){
        int valor = 0;
        if(registro > 0 && registro < 32) {
            valor = this.registros[registro];
        }
        return valor;
    }

    public String getNombre() {
        return nombre;
    }

    public int getId() {
        return id;
    }

    public int getQuantumRestante() {
        return quantumRestante;
    }

    public void setQuantumRestante(int quantumRestante) {
        this.quantumRestante = quantumRestante;
    }

    @Override
    public String toString(){
        String hilera = "*Estado del Hilo " + this.id + "\n";
        hilera += "*PC: " + this.pc + "\n";
        hilera += "*IR: " + this.ir + "\n";
        hilera += "*Quantum Restante: " + this.quantumRestante + "\n";
        hilera += "*Registros: " + Arrays.toString(this.registros) + "\n";
        return hilera;
    }

}

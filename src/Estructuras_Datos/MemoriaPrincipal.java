package Estructuras_Datos;

public class MemoriaPrincipal {
    private int[][] datos;
    private Instruccion[][] instrucciones;

    public MemoriaPrincipal(){
        this.datos = new int[24][4];
        this.instrucciones = new Instruccion[40][4];
    }
}

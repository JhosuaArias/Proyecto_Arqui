package Caches;

import Estructuras_Datos.Instruccion;

import java.util.Arrays;

public class BloqueInstrucciones {
    private Instruccion[] palabra;
    private int etiqueta;
    private Estado estado;

    private static final int PALABRAS_BLOQUE = 4;

    /**
     * Construdctor de BloqueInstrucciones.
     */
    public BloqueInstrucciones() {
        this.palabra = new Instruccion[PALABRAS_BLOQUE];
        this.etiqueta = -1;
        this.estado = Estado.INVALIDO;
    }

    /**
     * Constructor de BloqueInstrucciones
     * @param instruccion instrucciones del bloque.
     * @param etiqueta etiqueta asociada al bloque.
     * @param estado estado del bloque.
     */
    public BloqueInstrucciones(Instruccion[] instruccion, int etiqueta, Estado estado) {
        this.palabra = instruccion;
        this.etiqueta = etiqueta;
        this.estado = estado;
    }

    /**Getters and Setters**/
    public Instruccion[] getPalabra() {
        return this.palabra;
    }

    public void setPalabra(Instruccion[] palabra) {
        this.palabra = palabra;
    }

    public int getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(int etiqueta) {
        this.etiqueta = etiqueta;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Instruccion getInstruccion(int posicion) {
        return this.palabra[posicion];
    }

    @Override
    public String toString(){
        return Arrays.toString(palabra) + " etq: " + etiqueta + " est: " + estado;
    }

}

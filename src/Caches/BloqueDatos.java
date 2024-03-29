package Caches;

import Estructuras_Datos.Instruccion;

import java.util.Arrays;

public class BloqueDatos {
    private int[] palabra;
    private int etiqueta;
    private Estado estado;

    private static final int PALABRAS_BLOQUE = 4;

    /**
     * Constructor de BloqueDatos.
     */
    public BloqueDatos(){
        this.palabra = new int[PALABRAS_BLOQUE];
        this.etiqueta = -1;
        this.estado = Estado.INVALIDO;
    }

    /**
     * Constructor de BloqueDatos.
     * @param palabra un array con las 4 palabras del bloque.
     * @param etiqueta la etiqueta asociada al bloque.
     * @param estado el estado del bloque.
     */
    public BloqueDatos(int[] palabra, int etiqueta, Estado estado){
        this.palabra = palabra.clone();
        this.etiqueta = etiqueta;
        this.estado = estado;
    }

    /**
     * Constructor de BloqueDatos.
     * @param bloqueDatos bloque de datos para copiar información.
     */
    public BloqueDatos(BloqueDatos bloqueDatos){
        this.palabra = bloqueDatos.getPalabra().clone();
        this.etiqueta = bloqueDatos.getEtiqueta();
        this.estado = bloqueDatos.estado;
    }

    /**Getters and Setters*/
    public int[] getPalabra() {
        return palabra;
    }

    public void setPalabra(int[] palabra) {
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

    @Override
    public String toString(){
        return Arrays.toString(palabra) + " etq: " + etiqueta + " est: " + estado;
    }
}

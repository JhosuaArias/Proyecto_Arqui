package Caches;

import Estructuras_Datos.Instruccion;

public class BloqueDatos {
    private int[] palabra;
    private int etiqueta;
    private Estado estado;

    private static final int PALABRAS_BLOQUE = 4;

    public BloqueDatos(){
        this.palabra = new int[PALABRAS_BLOQUE];
        this.etiqueta = 0;
        this.estado = Estado.INVALIDO;
    }

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

}

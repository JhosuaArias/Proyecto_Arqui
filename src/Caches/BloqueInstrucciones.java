package Caches;

import Estructuras_Datos.Instruccion;

public class BloqueInstrucciones {
    private Instruccion[] palabra;
    private int etiqueta;
    private Estado estado;

    private static final int PALABRAS_BLOQUE = 4;

    public BloqueInstrucciones() {
        this.palabra = new Instruccion[PALABRAS_BLOQUE];
        this.etiqueta = 0;
        this.estado = Estado.INVALIDO;
    }


    public Instruccion[] getPalabra() {
        return palabra;
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

}

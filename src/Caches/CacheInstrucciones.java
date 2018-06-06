package Caches;

public class CacheInstrucciones {
    private BloqueInstrucciones[] bloques;

    public CacheInstrucciones(int numeroBloques) {
        this.bloques = new BloqueInstrucciones[numeroBloques];
        for (int i = 0 ; i < this.bloques.length ; i++) {
            this.bloques[i] = new BloqueInstrucciones();
        }
    }

    public BloqueInstrucciones getBloque(int posicion) {
        if(posicion >= 0 && posicion < this.bloques.length) {
            return this.bloques[posicion];
        }
        return null;
    }

    public void setBloque(BloqueInstrucciones bloque, int posicion) {

    }

    public void setDato(int posicion, int palabra, int valor) {

    }

    public void setEtiqueta(int posicion, int valor) {

    }

    public void setEstado(int posicion, Estado estado) {

    }
}

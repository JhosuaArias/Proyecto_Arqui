package Caches;

public class CacheInstrucciones {
    private BloqueInstrucciones[] bloques;

    /**
     * Constructor CacheInstrucciones.
     * @param numeroBloques número de bloques que tendrá la caché.
     */
    public CacheInstrucciones(int numeroBloques) {
        this.bloques = new BloqueInstrucciones[numeroBloques];
        for (int i = 0 ; i < this.bloques.length ; i++) {
            this.bloques[i] = new BloqueInstrucciones();
        }
    }

    /**Getters and Setters**/
    public BloqueInstrucciones getBloque(int posicion) {
        if(posicion >= 0 && posicion < this.bloques.length) {
            return this.bloques[posicion];
        }
        return null;
    }

    public void setBloque(BloqueInstrucciones bloque, int posicion) {
        if(posicion >= 0 && posicion < this.bloques.length) {
            this.bloques[posicion] = bloque;
        }
    }

    public void setDato(int posicion, int palabra, int valor) {

    }

    public void setEtiqueta(int posicion, int etiqueta) {
        if(posicion >= 0 && posicion < this.bloques.length) {
            this.bloques[posicion].setEtiqueta(etiqueta); ;
        }
    }

    public void setEstado(int posicion, Estado estado) {
        if(posicion >= 0 && posicion < this.bloques.length) {
            this.bloques[posicion].setEstado(estado);
        }
    }

    @Override
    public String toString(){
        String hilera = "";

        for (int i = 0; i < bloques.length ; i++) {
            hilera+= i +": " + bloques[i].toString()+"\n";
        }
        return hilera;
    }
}

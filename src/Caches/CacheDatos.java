package Caches;

public class CacheDatos {
    private BloqueDatos[] bloques;

    public CacheDatos(int numeroBloques) {
        this.bloques = new BloqueDatos[numeroBloques];
        for (int i = 0 ; i < this.bloques.length ; i++) {
            this.bloques[i] = new BloqueDatos();
        }
    }

    public BloqueDatos getBloque(int posicion) {
        if(posicion >= 0 && posicion < this.bloques.length) {
            return this.bloques[posicion];
        }
        return null;
    }

    public void setBloque(BloqueDatos bloque, int posicion) {
        if(posicion >= 0 && posicion < this.bloques.length) {
            this.bloques[posicion] = bloque;
        }
    }

    public void setPalabra(int posicionBloque, int posicionPalabra, int palabra){
        if(posicionBloque >= 0 && posicionBloque < this.bloques.length) {
            if(posicionPalabra >= 0 && posicionPalabra < 4){
                this.bloques[posicionBloque].getPalabra()[posicionPalabra] = palabra;
            }
        }
    }

    public void setDato(int posicion, int palabra, int valor) {

    }

    public void setEtiqueta(int posicion, int valor) {

    }

    public void setEstado(int posicion, Estado estado) {

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

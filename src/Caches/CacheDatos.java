package Caches;

public class CacheDatos {
    private BloqueDatos[] bloques;

    /**
     * Constructor de CacheDatos.
     * @param numeroBloques número de bloques que tendrá la caché.
     */
    public CacheDatos(int numeroBloques) {
        this.bloques = new BloqueDatos[numeroBloques];
        for (int i = 0 ; i < this.bloques.length ; i++) {
            this.bloques[i] = new BloqueDatos();
        }
    }

    /**Getters and Setters**/
    public BloqueDatos getBloque(int posicion) {
            return this.bloques[posicion];
    }

    public void setBloque(BloqueDatos bloque, int posicion) {
            this.bloques[posicion] = bloque;
    }

    public void setPalabra(int posicionBloque, int posicionPalabra, int palabra){
            this.bloques[posicionBloque].getPalabra()[posicionPalabra] = palabra;
    }

    public int getPalabra(int posicionBloque, int posicionPalabra){
        return  this.bloques[posicionBloque].getPalabra()[posicionPalabra];
    }

    public void setDato(int posicion, int palabra, int valor) {

    }

    public void setEtiqueta(int posicion, int valor) {

    }

    public void setEstado(int posicion, Estado estado) {
        this.bloques[posicion].setEstado(estado);
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

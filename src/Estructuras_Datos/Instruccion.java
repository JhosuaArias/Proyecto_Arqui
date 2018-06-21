package Estructuras_Datos;

public class Instruccion {

    private int[] palabra;

    public Instruccion(int[] instruccion) {
        this.palabra = instruccion;
    }

    public int[] getPalabra() {
        return palabra;
    }

    @Override
    public  String toString(){
        String hilera = "";
        for (int i: palabra) {
            hilera+= i;
        }
        return hilera;
    }
}

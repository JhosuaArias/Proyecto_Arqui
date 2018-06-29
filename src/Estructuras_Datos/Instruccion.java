package Estructuras_Datos;

public class Instruccion {

    private int[] palabra;

    /***
     * Constructor de Instruccion.
     * @param instruccion la instrucción separada en 4 enteros.
     */
    public Instruccion(int[] instruccion) {
        this.palabra = instruccion;
    }

    /**Getters and setters**/
    public int[] getPalabra() {
        return palabra;
    }

    @Override
    public  String toString(){
        String hilera = "";
        for (int i: palabra) {
            hilera+= i + " ";
        }
        return hilera;
    }
}

package Estructuras_Datos;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class MemoriaPrincipal {
    /**Datos**/
    private int[][] datos;
    private Instruccion[][] instrucciones;
    /**Constantes**/
    private static final int BYTES_PALABRA = 4;
    private static final int PALABRAS_BLOQUE = 4;
    private static final int BLOQUES_DATOS = 24;
    private static final int BLOQUES_INSTRUCCIONES = 40;
    private static final int DIRRECION_DATOS = 0;
    private static final int DIRECCION_INSTRUCCIONES = 384;

    /***
     * Constructor de MemoriaPrincipal.
     */
    public MemoriaPrincipal(){
        this.datos = new int[BLOQUES_DATOS][PALABRAS_BLOQUE];
        this.instrucciones = new Instruccion[BLOQUES_INSTRUCCIONES][PALABRAS_BLOQUE];
    }

    /***
     * Inicializa todas las celdas de memoria en unos.
     */
    public void setMemoria() {
        for (int i = 0; i < BLOQUES_DATOS ; i++) {
            for (int j = 0; j < PALABRAS_BLOQUE ; j++) {
                this.datos[i][j] = 1;
            }
        }
    }

    /***
     * Mete las instrucciones en la sección de Instrucciones de la memoria principal en orden.
     * @param instrucciones Una listas de listas con Instrucciones de todos los hilos.
     * @param hilos La referencia de todos los hilos para definir los PCs.
     */
    public void setInstrucciones(ArrayList<ArrayList<Instruccion>> instrucciones, ArrayList<Hilo> hilos) {
        int indiceHilo = 0;
        int indiceMemoriaBloque = -1;
        int indiceMemoriaPalabra = 0;
        int direccionMemoria = DIRECCION_INSTRUCCIONES;

        for ( ArrayList<Instruccion> instruccionesHilo : instrucciones) {
            hilos.get(indiceHilo).setPc(direccionMemoria);
            for (Instruccion instruccion : instruccionesHilo) {
                int palabra = indiceMemoriaPalabra % 4;
                indiceMemoriaBloque += palabra == 0 ? 1 : 0;
                this.instrucciones[indiceMemoriaBloque][palabra] = instruccion;
                direccionMemoria += 4;
                indiceMemoriaPalabra++;
            }
            indiceHilo++;
        }
    }

    /**Getters and Setters**/
    public int[] getBloqueDatos(int posicionBloque) {
        if(posicionBloque >= 0 && posicionBloque < BLOQUES_DATOS) {
            return this.datos[posicionBloque];
        } else {
            return null;
        }
    }

    public Instruccion[] getBloqueInstrucciones(int posicionBloque) {
        if(posicionBloque >= BLOQUES_DATOS  && posicionBloque < BLOQUES_DATOS + BLOQUES_INSTRUCCIONES) {
            return this.instrucciones[posicionBloque-BLOQUES_DATOS];
        } else {
            return null;
        }
    }

    public void setBloque(int[] bloque, int posicionBloque){
            this.datos[posicionBloque] = bloque.clone();
    }

    public void setBloque(Instruccion[] bloque , int posicionBloque) {
            this.instrucciones[posicionBloque] = bloque.clone();
    }

    @Override
    public String toString(){
        String memoria = "*Datos:\n";
        int i = 0;
        for (int[] bloque : this.datos){
            memoria+="Bloque"+i+": "+ Arrays.toString(bloque) + "\n";
            i++;
        }

        memoria+="*Instrucciones\n";
        for(Instruccion[] bloque: this.instrucciones){
            memoria+="Bloque"+i+": "+Arrays.toString(bloque)+"\n";
            i++;
        }


        return memoria;
    }
}

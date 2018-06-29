package Estructuras_Datos;

import java.util.ArrayDeque;
import java.util.Queue;

public class Cola {
    private Queue<Hilo> cola;

    /***
     * Constructor de Cola.
     */
    public Cola(){
        this.cola = new ArrayDeque<>();
    }

    /***
     * Método que saca y devuelve un Hilo de la Cola.
     * @return Hilo sacado de la Cola.
     */
    public synchronized Hilo poll() {
        return this.cola.poll();
    }

    /***
     * Método que inserta un Hilo a la Cola.
     * @param hilo Hilo a insertar en la Cola.
     */
    public synchronized void add(Hilo hilo) {
        this.cola.add(hilo);
    }
}

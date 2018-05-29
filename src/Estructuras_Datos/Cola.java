package Estructuras_Datos;

import java.util.ArrayDeque;
import java.util.Queue;

public class Cola {
    private Queue<Hilo> cola;

    public Cola(){
        this.cola = new ArrayDeque<>();
    }

    public synchronized Hilo poll() {
        return this.cola.poll();
    }

    public synchronized void add(Hilo hilo) {
        this.cola.add(hilo);
    }
}

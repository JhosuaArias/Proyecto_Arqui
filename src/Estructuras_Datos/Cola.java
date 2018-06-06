package Estructuras_Datos;

import java.util.ArrayDeque;
import java.util.Queue;

public class Cola {
    private Queue<Hilo> cola;

    public Cola(){
        this.cola = new ArrayDeque<>();
    }

    public Hilo poll() {
        return this.cola.poll();
    }

    public void add(Hilo hilo) {
        this.cola.add(hilo);
    }
}

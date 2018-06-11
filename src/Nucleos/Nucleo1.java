package Nucleos;

import Estructuras_Datos.Hilo;
import MVC.Simulacion;

public class Nucleo1 extends Nucleo{

    private Hilo hilo;

    private Thread thread;

    public Nucleo1(Simulacion simulacion, int id){
        super(simulacion,id);
        this.thread = new Thread(this,"Thread 2");
        this.thread.start();
    }

    public void setHilo() {
        this.hilo = this.simulacion.pedirHiloCola();
    }

    public void resolverFallo() {

    }

    private void iteracion() {
        int pc = this.hilo.getPc();
        
    }

    private void esperarTick(){
        this.simulacion.esperarTick();
        this.hilo.restarQuantum();
    }

    @Override
    public void run() {
        super.run();
        while (!this.simulacion.isColaNull()){
            System.out.println("Soy: " + this.thread.getName());
            this.hilo = this.simulacion.pedirHiloCola();
            if(this.hilo == null){
                this.esperarTick();
            } else {
                this.iteracion();
            }
        }
    }


}

package Nucleos;

import Caches.Estado;
import Estructuras_Datos.Hilo;
import MVC.Simulacion;

public class Nucleo0 extends Nucleo{

    private Thread thread0;
    private Thread thread1;

    private volatile EstadoThread estadoThread0;
    private volatile EstadoThread estadoThread1;

    private Hilo hiloThread0;
    private Hilo hiloThread1;

    public Nucleo0(Simulacion simulacion, int id){
        super(simulacion,id);
        this.thread0 = new Thread(this,"Thread 0");
        this.thread1 = new Thread(this,"Thread 1");

        this.estadoThread0 = EstadoThread.EJECUTANDO;
        this.estadoThread1 = EstadoThread.ESPERANDO;

        this.thread0.start();
        this.thread1.start();
    }


    public synchronized EstadoThread getEstado(){
        if(Thread.currentThread().getId() == this.thread0.getId()){
            return estadoThread0;
        } else if(Thread.currentThread().getId() == this.thread1.getId()) {
            return estadoThread1;
        }else{
            System.err.println("No debería pasar");
            return null;
        }
    }

    public synchronized void setEstado(EstadoThread estadoThread, int thread) {
        if(thread == 0) {
            this.estadoThread0 = estadoThread;
        }else{
            this.estadoThread1 = estadoThread;
        }
    }

    /**Flujo del Núcleo**/

    private void restarQuantum() {
        if(Thread.currentThread().getId() == thread0.getId()) {
           this.hiloThread0.restarQuantum();
        }else if(Thread.currentThread().getId() == thread1.getId()){
            this.hiloThread1.restarQuantum();
        }else {
            System.err.println("No debería pasar");
        }
    }

    private void iteracion() {

    }

    private void escogerHilo(){
        boolean repetir = true;

        while (repetir) {
            if(Thread.currentThread().getId() == thread0.getId()) {
                if((this.hiloThread0 = this.simulacion.pedirHiloCola()) != null){
                    repetir = false;
                } else {
                  this.esperarTick(false);
                }
            }else if(Thread.currentThread().getId() == thread1.getId()){
                if((this.hiloThread1 = this.simulacion.pedirHiloCola()) != null) {
                    repetir = false;
                } else {
                    this.esperarTick(false);
                }
            }else {
                System.err.println("No debería pasar");
            }
        }
    }

    private void devolverHilo(){
        if(Thread.currentThread().getId() == thread0.getId()) {
            this.simulacion.devolverHiloCola(this.hiloThread0);
            this.hiloThread0 = null;
        }else if(Thread.currentThread().getId() == thread1.getId()){
            this.simulacion.devolverHiloCola(this.hiloThread1);
            this.hiloThread1 = null;
        }else {
            System.err.println("No debería pasar");
        }
    }


    @Override
    public void esperarTick(boolean restarQuantum){
        super.esperarTick(restarQuantum);
        if(restarQuantum)
            this.restarQuantum();
    }

    @Override
    public void run() {
        super.run();

        while (!this.simulacion.isColaNull()){
            if(this.getEstado() == EstadoThread.EJECUTANDO) {
                this.escogerHilo();
                this.iteracion();
            }
            this.esperarTick(false);
        }
    }
}

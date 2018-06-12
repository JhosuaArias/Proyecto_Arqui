package Nucleos;

import MVC.Simulacion;

public class Nucleo0 extends Nucleo{

    private Thread thread0;
    private Thread thread1;

    public Nucleo0(Simulacion simulacion, int id){
        super(simulacion,id);
        this.thread0 = new Thread(this,"Thread 0");
        this.thread1 = new Thread(this,"Thread 1");

        this.thread0.start();
        this.thread1.start();
    }

    @Override
    public void run() {
        super.run();

        while (true){
            if(Thread.currentThread().getId() == thread0.getId()) {
                System.out.println("Soy " + thread0.getName());
            }else if(Thread.currentThread().getId() == thread1.getId()){
                System.out.println("Soy " + thread1.getName());
            }else {
                System.err.println("No debería pasar");
            }
            this.esperarTick();
        }
    }
}

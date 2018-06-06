package Nucleos;

import MVC.Simulacion;

public class Nucleo implements Runnable {
    protected Simulacion simulacion;
    protected int id;
    protected Thread[] threads;

    public Nucleo(Simulacion simulacion, int id) {
        this.simulacion = simulacion;
        this.id = id;
    }

    public void daddi(){

    }

    public void dadd(){

    }

    public void dsub(){

    }

    public void dmul(){

    }

    public void ddiv(){

    }

    public void beqz(){

    }

    public void bnez(){

    }

    public void jal(){

    }

    public void jr(){

    }

    public void lw(){

    }

    public void sw(){

    }

    public void fin(){

    }

    @Override
    public void run() {

    }
}

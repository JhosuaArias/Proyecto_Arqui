package Nucleos;

import Estructuras_Datos.Hilo;
import MVC.Simulacion;

public class Nucleo1 extends Nucleo{

    private Hilo hilo;

    public Nucleo1(Simulacion simulacion, int id){
        super(simulacion,id);
    }

    public void setHilo() {
        this.hilo = this.simulacion.pedirHiloCola();
    }

    @Override
    public void run() {
        super.run();

    }
}

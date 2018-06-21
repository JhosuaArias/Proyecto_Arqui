package Nucleos;
import Caches.BloqueDatos;
import Caches.Estado;
import Estructuras_Datos.Hilo;
import Estructuras_Datos.Instruccion;
import MVC.Simulacion;

public class Nucleo implements Runnable {

    protected Simulacion simulacion;
    protected int id;


    public Nucleo(Simulacion simulacion, int id) {
        this.simulacion = simulacion;
        this.id = id;
    }
  
  
    public void ejecutar_instruccion(Hilo hiloEjecucion, Nucleo0 n0, Nucleo1 n1) {
        int[] ejecucion = hiloEjecucion.getIr().getPalabra();
        switch (ejecucion[0]) {
            case 8: //Daddi
                /**
                 * palabra 0: CP| palabra 1 y 2: #Registro|palabra 3: Immediato
                 * getRegistro(ejecuccion[1]: Obtiene el valor del registro
                 * ejecucion[2] obtiene el numero del registro
                 * ejecucion[3] obtiene el immediato
                 */
                daddi(hiloEjecucion, hiloEjecucion.getRegistro(ejecucion[1]),ejecucion[2],ejecucion[3]);
                break;
            case 32: //Dadd
                dadd(hiloEjecucion, hiloEjecucion.getRegistro(ejecucion[1]),hiloEjecucion.getRegistro(ejecucion[2]),ejecucion[3]);
                break;
            case 34: //Dsub
                dsub(hiloEjecucion, hiloEjecucion.getRegistro(ejecucion[1]),hiloEjecucion.getRegistro(ejecucion[2]),ejecucion[3]);
                break;
            case 12: //Dmul
                dmul(hiloEjecucion, hiloEjecucion.getRegistro(ejecucion[1]),hiloEjecucion.getRegistro(ejecucion[2]),ejecucion[3]);
                break;
            case 14: //Ddiv
                ddiv(hiloEjecucion, hiloEjecucion.getRegistro(ejecucion[1]),hiloEjecucion.getRegistro(ejecucion[2]),ejecucion[3]);
                break;
            case 4: //BEQZ
                //Listo
                beqz(hiloEjecucion, hiloEjecucion.getRegistro(ejecucion[1]),ejecucion[3]);
                break;
            case 5: //BNEZ
                //Listo
                bnez(hiloEjecucion, hiloEjecucion.getRegistro(ejecucion[1]),ejecucion[3]);
                break;
            case 3: //Jal
               jal(hiloEjecucion, hiloEjecucion.getRegistro(ejecucion[1]), hiloEjecucion.getPc());
                break;
            case 2: //JR
                jr(hiloEjecucion, hiloEjecucion.getRegistro(ejecucion[1]));
                break;
            case 35: //LW
                // TODO
                //this.lw(this.id, registro, dir);
                if(n0 != null){
                    //n0.lw(1000);
                }else{
                    //n1.lw(hiloEjecucion,ejecucion[1],);
                }
                break;
            case 43: //SW
                //TODO
                if(n0 != null){
                    //n0.sw(1000);
                }else{
                    //n1.sw(1000);
                }
                break;
            case 63: //Fin
                this.fin(hiloEjecucion);
                break;
        }
    }
    /**Operations**/

    public void daddi(Hilo hiloEjecucion, int RF1, int RF2, int Imm){
        int op=RF1+Imm;
        hiloEjecucion.setRegistro(RF2,op);
        }

    public void dadd(Hilo hiloEjecucion, int RF1, int RF2, int RD){
        int op=RF1+RF2;
        hiloEjecucion.setRegistro(RD,op);
        }

    public void dsub(Hilo hiloEjecucion, int RF1, int RF2, int RD){
        int op=RF1-RF2;
        hiloEjecucion.setRegistro(RD,op);
        }

    public void dmul(Hilo hiloEjecucion, int RF1, int RF2, int RD){
        int op=RF1*RF2;
        hiloEjecucion.setRegistro(RD,op);
        }

    public void ddiv(Hilo hiloEjecucion, int RF1, int RF2, int RD){
        int op=RF1/RF2;
        hiloEjecucion.setRegistro(RD,op);
    }

    public void beqz(Hilo hiloEjecucion, int RF, int Imm){
        if(RF==0){
            hiloEjecucion.setPc(hiloEjecucion.getPc() +  4*Imm);
        }
    }

    public void bnez(Hilo hiloEjecucion, int RF, int Imm){
        if(RF!=0){
            hiloEjecucion.setPc(hiloEjecucion.getPc() + 4*Imm);
        }
    }

    public void jal(Hilo hiloEjecucion, int RD, int PC){

        hiloEjecucion.setRegistro(31,hiloEjecucion.getPc()); //Deberia cambiar el registro R31 del contexto que lo pidio, el pc actual
        hiloEjecucion.setPc(hiloEjecucion.getPc() +  RD);

    }

    public void jr(Hilo hiloEjecucion, int RF){
        hiloEjecucion.setPc(RF);
    }


    /*Fin de logica de LW*/

    public void fin(Hilo hilo){ //Pone el hilo.esFin a true
        hilo.setEsFin(true);
    }

    public  void fin_quantum(){

    }

    public void esperarTick(boolean restarQuantum) {
        this.simulacion.esperarTick();
    }

    public void fallo_instrucciones(){}

    @Override
    public void run() {

    }

}

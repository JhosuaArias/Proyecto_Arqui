package Nucleos;
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
  
  
    public void ejecutar_instruccion(Hilo hiloEjecucion,Instruccion instruccion) {
        int[] ejecuccion = instruccion.getPalabra();
        switch (ejecuccion[0]) {
            case 8: //Daddi
                /**
                 * palabra 0: CP| palabra 1 y 2: #Registro|palabra 3: Immediato
                 * getRegistro(ejecuccion[1]: Obtiene el valor del registro
                 * ejecucion[2] obtiene el numero del registro
                 * ejecucion[3] obtiene el immediato
                 */
                daddi(hiloEjecucion, hiloEjecucion.getRegistro(ejecuccion[1]),ejecuccion[2],ejecuccion[3]);
                break;
            case 32: //Dadd
                dadd(hiloEjecucion, hiloEjecucion.getRegistro(ejecuccion[1]),ejecuccion[2],hiloEjecucion.getRegistro(ejecuccion[3]));
                break;
            case 34: //Dsub
                dsub(hiloEjecucion, hiloEjecucion.getRegistro(ejecuccion[1]),ejecuccion[2],hiloEjecucion.getRegistro(ejecuccion[3]));
                break;
            case 12: //Dmul
                dmul(hiloEjecucion, hiloEjecucion.getRegistro(ejecuccion[1]),ejecuccion[2],hiloEjecucion.getRegistro(ejecuccion[3]));
                break;
            case 14: //Ddiv
                ddiv(hiloEjecucion, hiloEjecucion.getRegistro(ejecuccion[1]),ejecuccion[2],hiloEjecucion.getRegistro(ejecuccion[3]));
                break;
            case 4: //BEQZ
                beqz(hiloEjecucion, hiloEjecucion.getRegistro(ejecuccion[1]),ejecuccion[3]);
                break;
            case 5: //BNEZ
                bnez(hiloEjecucion, hiloEjecucion.getRegistro(ejecuccion[1]),ejecuccion[3]);
                break;
            case 3: //Jal
                jal(hiloEjecucion, hiloEjecucion.getRegistro(ejecuccion[1]), hiloEjecucion.getPc());
                break;
            case 2: //JR
                jr(hiloEjecucion, hiloEjecucion.getRegistro(ejecuccion[1]));
                break;
            case 35: //LW
                break;
            case 43: //SW
                break;
            case 63: //Fin
                break;
        }
        /**Resto el quantum al hilo**/
        hiloEjecucion.restarQuantum();
    }
    /**Operations**/

    public void daddi(Hilo hiloEjecucion, int RF1, int RF2, int Imm){
        int op=RF1+Imm;
        hiloEjecucion.setRegistro(RF2,op);
        }

    public void dadd(Hilo hiloEjecucion, int RF1, int RF2, int RD){
        int op=RF1+RD;
        hiloEjecucion.setRegistro(RF2,op);
        }

    public void dsub(Hilo hiloEjecucion, int RF1, int RF2, int RD){
        int op=RF1-RD;
        hiloEjecucion.setRegistro(RF2,op);
        }

    public void dmul(Hilo hiloEjecucion, int RF1, int RF2, int RD){
        int op=RF1*RD;
        hiloEjecucion.setRegistro(RF2,op);
        }

    public void ddiv(Hilo hiloEjecucion, int RF1, int RF2, int RD){
        int op=RF1/RD;
        hiloEjecucion.setRegistro(RF2,op);
    }

    public void beqz(Hilo hiloEjecucion, int RF, int Imm){
        if(RF==0){
            hiloEjecucion.setPc(4*Imm);
        }
    }

    public void bnez(Hilo hiloEjecucion, int RF, int Imm){
        if(RF!=0){
            hiloEjecucion.setPc(4*Imm);
        }
    }

    public void jal(Hilo hiloEjecucion, int RD, int PC){

        hiloEjecucion.setRegistro(31,hiloEjecucion.getPc()); //Deberia cambiar el registro R31 del contexto que lo pidio, el pc actual
        hiloEjecucion.setPc(RD);

    }

    public void jr(Hilo hiloEjecucion, int PC){
        hiloEjecucion.setPc(PC*4);
    }

    public void lw(){ //Lleva override y super

    }

    public void sw(){ //Lleva override y super

    }

    public void fin(Hilo hilo){ //Pone el hilo.esFin a true
        hilo.setEsFin(true);
    }

    public  void fin_quantum(){

    }

    public void esperarTick() {
        this.simulacion.esperarTick();
    }

    public void fallo_instrucciones(){}

    @Override
    public void run() {

    }

}

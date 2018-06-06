package Nucleos;
import MVC.Simulacion;

public class Nucleo implements Runnable {

    protected Simulacion simulacion;
    protected int id;


    public Nucleo(Simulacion simulacion, int id) {
        this.simulacion = simulacion;
        this.id = id;
    }
  
  
    public void ejecutar_instruccion(Instruccion instruccion){
        int[] ejecuccion=instruccion.getPalabra();
        switch(ejecuccion[0]){
            case 8: //Daddi
                //this.simulacion...
                break;
            case 32: //Dadd
                break;
            case 34: //Dsub
                break;
            case 12: //Dmul
                break;
            case 14: //Ddiv
                break;
            case 4: //BEQZ
                break;
            case 5: //BNEZ
                break;
            case 3: //Jal
                break;
            case 2: //JR
                break;
            case 35: //LW
                break;
            case 43: //SW
                break;
            case 63: //Fin
                break;
        }

    /**Operations**/
    public void daddi(){


    }


    public int daddi(int RF1, int RF2, int RD){
        RF2=RF1+RD;
        //hilo.pc+=4;
        return RF2;
    }

    public int dadd(int RF1, int RF2, int RD){
        RF2= RF1+RD;
        return RF2;
    }

    public int dsub(int RF1, int RF2, int RD){
        RF2=RF1-RD;
        return RF2;
    }

    public int dmul(int RF1, int RF2, int RD){
        RF2=RF1*RD;
        return RF2;
    }

    public int ddiv(int RF1, int RF2, int RD){
        RF2=RF1/RD;
        return RF2;

    }

    public int beqz(int RF, int RD){
        int pc=0;
        if(RF==0){
            pc=4*RD;
        }
        return pc;
    }

    public int bnez(int RF, int RD){
        int pc=0;
        if(RF!=0){
            pc=4*RD;
        }
        return pc;
    }

    public int jal(int RD, int PC){
        int pc;
        int R31=PC; //Deberia cambiar el registro R31 del contexto que lo pidio
        pc=RD;
        return pc;
    }

    public int jr(int RF){
        int pc=RF;
        return  pc;
    }

    public void lw(){ //Lleva override y super

    }

    public void sw(){ //Lleva override y super

    }

    public void fin(){ //Pone el hilo.esFin a true

    }

    public void fallo_instrucciones(){}

    public void fallo_datos(){}

    @Override
    public void run() {

    }
}

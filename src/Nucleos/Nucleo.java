package Nucleos;
import Estructuras_Datos.Hilo;
import Estructuras_Datos.Instruccion;
import MVC.Simulacion;

public class Nucleo implements Runnable {

    protected Simulacion simulacion;
    protected int id;
    public Hilo hiloEjecucion;

    public Nucleo(Simulacion simulacion, int id) {
        this.simulacion = simulacion;
        this.id = id;
        hiloEjecucion=simulacion.pedirHiloCola();
    }
  
  
    public void ejecutar_instruccion(Instruccion instruccion) {
        int[] ejecuccion = instruccion.getPalabra();
        switch (ejecuccion[0]) {
            case 8: //Daddi
                daddi(hiloEjecucion.getRegistro(ejecuccion[1]),hiloEjecucion.getRegistro(ejecuccion[2]),hiloEjecucion.getRegistro(ejecuccion[3]));
                break;
            case 32: //Dadd
                dadd(hiloEjecucion.getRegistro(ejecuccion[1]),hiloEjecucion.getRegistro(ejecuccion[2]),hiloEjecucion.getRegistro(ejecuccion[3]));
                break;
            case 34: //Dsub
                dsub(hiloEjecucion.getRegistro(ejecuccion[1]),hiloEjecucion.getRegistro(ejecuccion[2]),hiloEjecucion.getRegistro(ejecuccion[3]));
                break;
            case 12: //Dmul
                dmul(hiloEjecucion.getRegistro(ejecuccion[1]),hiloEjecucion.getRegistro(ejecuccion[2]),hiloEjecucion.getRegistro(ejecuccion[3]));
                break;
            case 14: //Ddiv
                ddiv(hiloEjecucion.getRegistro(ejecuccion[1]),hiloEjecucion.getRegistro(ejecuccion[2]),hiloEjecucion.getRegistro(ejecuccion[3]));
                break;
            case 4: //BEQZ
                beqz(hiloEjecucion.getRegistro(ejecuccion[1]),hiloEjecucion.getRegistro(ejecuccion[3]));
                break;
            case 5: //BNEZ
                bnez(hiloEjecucion.getRegistro(ejecuccion[1]),hiloEjecucion.getRegistro(ejecuccion[3]));
                break;
            case 3: //Jal
                jal(hiloEjecucion.getRegistro(ejecuccion[1]), hiloEjecucion.getPc());
                break;
            case 2: //JR
                jr(hiloEjecucion.getRegistro(ejecuccion[1]));
                break;
            case 35: //LW
                break;
            case 43: //SW
                break;
            case 63: //Fin
                break;
        }
    }
    /**Operations**/

    public void daddi(int RF1, int RF2, int RD){
        int op=RF1+RD;
        hiloEjecucion.setRegistro(RF2,op);
        hiloEjecucion.setPc(hiloEjecucion.getPc()+4);
    }

    public void dadd(int RF1, int RF2, int RD){
        int op=RF1+RD;
        hiloEjecucion.setRegistro(RF2,op);
        hiloEjecucion.setPc(hiloEjecucion.getPc()+4);
    }

    public void dsub(int RF1, int RF2, int RD){
        int op=RF1-RD;
        hiloEjecucion.setRegistro(RF2,op);
        hiloEjecucion.setPc(hiloEjecucion.getPc()+4);
    }

    public void dmul(int RF1, int RF2, int RD){
        int op=RF1*RD;
        hiloEjecucion.setRegistro(RF2,op);
        hiloEjecucion.setPc(hiloEjecucion.getPc()+4);;
    }

    public void ddiv(int RF1, int RF2, int RD){
        int op=RF1/RD;
        hiloEjecucion.setRegistro(RF2,op);
        hiloEjecucion.setPc(hiloEjecucion.getPc()+4);
    }

    public void beqz(int RF, int RD){
        if(RF==0){
            hiloEjecucion.setPc(4*RD);
        }
    }

    public void bnez(int RF, int RD){
        if(RF!=0){
            hiloEjecucion.setPc(4*RD);
        }
    }

    public void jal(int RD, int PC){

        hiloEjecucion.setRegistro(31,hiloEjecucion.getPc()); //Deberia cambiar el registro R31 del contexto que lo pidio, el pc actual
        hiloEjecucion.setPc(RD);

    }

    public void jr(int PC){
        hiloEjecucion.setPc(PC);
    }

    public void lw(){ //Lleva override y super

    }

    public void sw(){ //Lleva override y super

    }

    public void fin(){ //Pone el hilo.esFin a true

    }

    public  void fin_quantum(){
        this.hiloEjecucion.setEsFin(true);
    }

    public void fallo_instrucciones(){}

    public void fallo_datos(){}

    public void cargar(int nucleoID, int direccionMemoria)
    {
        if (nucleoID==0) //Soy el nucleo 0
        {
            //simulacion.intentar_pedirPosicion_CacheDatosN0();

        }
    }


    @Override
    public void run() {

    }

}

package Nucleos;
import Caches.BloqueDatos;
import Caches.Estado;
import Estructuras_Datos.Hilo;
import Estructuras_Datos.Instruccion;
import MVC.Simulacion;

public class Nucleo implements Runnable {

    protected Simulacion simulacion;
    protected int id;

    /***
     * Constructor Nucleo
     * @param simulacion Referencia de la simulación que contiene el núcleo.
     * @param id id del núcleo.
     */
    public Nucleo(Simulacion simulacion, int id) {
        this.simulacion = simulacion;
        this.id = id;
    }

    /***
     * Método que se encarga de obtener el IR de un hilo y parsearpara ejecutarla.
     * @param hiloEjecucion referencia del hilo al que se ejecutará la instrucción.
     * @param n0 Referencia de Núcleo 0, si quien ejecuta es Nucleo 0.
     * @param n1 Referencia de Núcleo 1, si quien ejecuta es Nucleo 1.
     */
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
                beqz(hiloEjecucion, hiloEjecucion.getRegistro(ejecucion[1]),ejecucion[3]);
                break;
            case 5: //BNEZ
                bnez(hiloEjecucion, hiloEjecucion.getRegistro(ejecucion[1]),ejecucion[3]);
                break;
            case 3: //Jal
               jal(hiloEjecucion, ejecucion[3]);
                break;
            case 2: //JR
                jr(hiloEjecucion, hiloEjecucion.getRegistro(ejecucion[1]));
                break;
            case 35: //LW
                if(n0 != null){
                    n0.lw(hiloEjecucion,ejecucion[2],hiloEjecucion.getRegistro(ejecucion[1])+ejecucion[3]);
                }else{
                    n1.lw(hiloEjecucion,ejecucion[2],hiloEjecucion.getRegistro(ejecucion[1])+ejecucion[3]);
                }
                break;
            case 43: //SW
                if(n0 != null){
                    n0.sw(hiloEjecucion,ejecucion[2],hiloEjecucion.getRegistro(ejecucion[1])+ejecucion[3]);
                }else{
                    n1.sw(hiloEjecucion,ejecucion[2],hiloEjecucion.getRegistro(ejecucion[1])+ejecucion[3]);
                }
                break;
            case 63: //Fin
                this.fin(hiloEjecucion);
                break;
        }
    }
    /**Operations**/

    /***
     * Método que ejecuta un DADDI.
     * @param hiloEjecucion Referencia del hilo en ejecución.
     * @param RF1 valor del primer registro.
     * @param RF2 número del registro destino.
     * @param Imm valor de un inmediato.
     */
    public void daddi(Hilo hiloEjecucion, int RF1, int RF2, int Imm){
        int op=RF1+Imm;
        hiloEjecucion.setRegistro(RF2,op);
        }

    /***
     * Método que ejecuta un DADD.
     * @param hiloEjecucion Referencia del hilo en ejecución.
     * @param RF1  valor del primer registro.
     * @param RF2  valor del segundo registro.
     * @param RD número del registro destino.
     */
    public void dadd(Hilo hiloEjecucion, int RF1, int RF2, int RD){
        int op=RF1+RF2;
        hiloEjecucion.setRegistro(RD,op);
        }

    /**
     * Método que ejecuta un DSUB.
     * @param hiloEjecucion Referencia del hilo en ejecución.
     * @param RF1 valor del primer registro.
     * @param RF2 valor del segundo registro.
     * @param RD número del registro destino.
     */
    public void dsub(Hilo hiloEjecucion, int RF1, int RF2, int RD){
        int op=RF1-RF2;
        hiloEjecucion.setRegistro(RD,op);
        }

    /***
     * Método que ejecuta un DMUL.
     * @param hiloEjecucion Referencia del hilo en ejecución.
     * @param RF1 valor del primer registro.
     * @param RF2 valor del segundo registro.
     * @param RD número del registro destino.
     */
    public void dmul(Hilo hiloEjecucion, int RF1, int RF2, int RD){
        int op=RF1*RF2;
        hiloEjecucion.setRegistro(RD,op);
        }

    /***
     * Método que ejecuta un DDIV.
     * @param hiloEjecucion Referencia del hilo en ejecución.
     * @param RF1 valor del primer registro.
     * @param RF2 valor del segundo registro.
     * @param RD número del registro destino.
     */
    public void ddiv(Hilo hiloEjecucion, int RF1, int RF2, int RD){
        int op=RF1/RF2;
        hiloEjecucion.setRegistro(RD,op);
    }

    /***
     * Método que ejecuta un BEQZ.
     * @param hiloEjecucion Referencia del hilo en ejecución.
     * @param RF Valor del registro condicional.
     * @param Imm número de palabras a saltar.
     */
    public void beqz(Hilo hiloEjecucion, int RF, int Imm){
        if(RF==0){
            hiloEjecucion.setPc(hiloEjecucion.getPc() +  4*Imm);
        }
    }

    /***
     * Método que ejecuta un BNEZ.
     * @param hiloEjecucion Referencia del hilo en ejecución.
     * @param RF Valor de registro condicional.
     * @param Imm número de palabras a saltar.
     */
    public void bnez(Hilo hiloEjecucion, int RF, int Imm){
        if(RF!=0){
            hiloEjecucion.setPc(hiloEjecucion.getPc() + 4*Imm);
        }
    }

    /***
     * Método que ejecuta un JAL.
     * @param hiloEjecucion Referencia del hilo en ejecución.
     * @param Imm Número de instrucciones a saltar.
     */

    public void jal(Hilo hiloEjecucion,int Imm){

        hiloEjecucion.setRegistro(31,hiloEjecucion.getPc()); //Deberia cambiar el registro R31 del contexto que lo pidio, el pc actual
        hiloEjecucion.setPc(hiloEjecucion.getPc() +  Imm);

    }

    /***
     * Método que ejecuta un JR.
     * @param hiloEjecucion Referencia del hilo en ejecución.
     * @param RF Dirección a la cual saltar.
     */
    public void jr(Hilo hiloEjecucion, int RF){
        hiloEjecucion.setPc(RF);
    }


    /***
     * Método que activa un flag para indicar que el hilo ya terminó.
     * @param hilo Referencia del Hilo que ya terminó su ejecución.
     */
    public void fin(Hilo hilo){ //Pone el hilo.esFin a true
        hilo.setEsFin(true);
    }

    /***
     * Método que llama a otro método en simulación para esperar en barrera a que todos los Threads se sincronicen.
     * @param restarQuantum parámetro que indica si hay que restarle quantum al hilo o no.
     */
    public void esperarTick(boolean restarQuantum) {
        this.simulacion.esperarTick();
    }


    @Override
    public void run() {

    }

}

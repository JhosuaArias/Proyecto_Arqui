package Nucleos;
import Estructuras_Datos.Instruccion;
import MVC.Simulacion;

public class Nucleo implements Runnable {

    protected Simulacion simulacion;
    protected int id;


    public Nucleo(Simulacion simulacion, int id) {
        this.simulacion = simulacion;
        this.id = id;
    }
  
  
    public void ejecutar_instruccion(Instruccion instruccion) {
        int[] ejecuccion = instruccion.getPalabra();
        switch (ejecuccion[0]) {
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
    }
    /**Operations**/

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
/**TODO Arreglar esto luego**/
//    public int[] getBloqueMemoria(int direccionMemoria) {
//        int bloqueACargar[] = new int[4];
//        int numeroBloque = (direccionMemoria / 16);
//
//        if (numeroBloque > 24) //Utilizo la cache de instrucciones
//        {
//            for (int i = 0; i <= 3; ++i) {
//                //Falta de arreglar
//                bloqueACargar[i] = memoria.instrucciones[numeroBloque - 24][i];
//            }
//
//            return bloqueACargar;
//        }
//
//        //Utilizo la cache de Datos
//        else {
//
//            for (int i = 0; i <= 3; ++i) {
//                //Falta de arreglar
//                bloqueACargar[i] = memoria.instrucciones[numeroBloque][i];
//            }
//
//            return bloqueACargar;
//        }
//    }
//
//    public int getPalabraCache(int direccionMemoria, int nucleo)
//    {
//        int numeroBloque = (direccionMemoria / 16);
//        int direccionPalabra = (direccionMemoria -  (16 * numeroBloque)) /  4;
//
//        if (nucleo==0) //Estoy en el nucleo 0
//        {
//            int posicionCache = (direccionMemoria / 16) % 8;
//
//            if ( numeroBloque>24) //Utilizo la cache de instrucciones
//            {return cacheInstruccionesN0[posicionCache] [direccionPalabra];}
//
//            else {return cacheDatosN0[posicionCache] [direccionPalabra];}
//            }
//
//        	else //Soy N1
//            {
//                int posicionCache = (direccionMemoria / 16) % 4;
//
//                if (numeroBloque>24) //Utilizo la cache de instrucciones
//                {return cacheInstruccionesN1[posicionCache] [direccionPalabra];}
//
//                else {return cacheDatosN1[posicionCache] [direccionPalabra];}
//                }
//
//    }

    @Override
    public void run() {

    }

}

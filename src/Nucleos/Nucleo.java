package Nucleos;

import Estructuras_Datos.Instruccion;

public class Nucleo {
    private int id;
    private Thread[] threads;

    public void ejecutar_instruccion(Instruccion instruccion){
        int[] ejecuccion=instruccion.getPalabra();
        switch(ejecuccion[0]){
            case 8:
                break;
            case 32:
                break;
            case 34:
                break;
            case 12:
                break;
            case 14:
                break;
            case 4:
                break;
            case 5:
                break;
            case 3:
                break;
            case 2:
                break;
            case 35:
                break;
            case 43:
                break;
            case 63:
                break;
        }

    }


    public int daddi(int RF1, int RF2, int RD){
        RF2=RF1+RD;
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

    public void lw(){

    }

    public void sw(){

    }

    public void fin(){

    }

}

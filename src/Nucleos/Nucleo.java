package Nucleos;

public class Nucleo {
    private int id;
    private Thread[] threads;

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

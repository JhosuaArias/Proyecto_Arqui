package Nucleos;

public class Nucleo {
    private int id;
    private Thread[] threads;

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

    public int[] getBloqueMemoria(int direccionMemoria)

    {
        int bloqueACargar[] = new int[4];
        int numeroBloque = (direccionMemoria / 16);

        if (numeroBloque > 24) //Utilizo la cache de instrucciones
        {
            for (int i = 0; i <= 3; ++i) {
                //Falta de arreglar
                bloqueACargar[i] = memoria.instrucciones[numeroBloque - 24][i];
            }

            return bloqueACargar;
        }

        //Utilizo la cache de Datos
        else {

            for (int i = 0; i <= 3; ++i) {
                //Falta de arreglar
                bloqueACargar[i] = memoria.instrucciones[numeroBloque][i];
            }

            return bloqueACargar;
        }
    }

    public int getPalabraCache(int direccionMemoria, int nucleo)
    {
        int numeroBloque = (direccionMemoria / 16);
        int direccionPalabra = (direccionMemoria -  (16 * numeroBloque)) /  4;

        if (nucleo==0) //Estoy en el nucleo 0
        {
            int posicionCache = (direccionMemoria / 16) % 8;

            if ( numeroBloque>24) //Utilizo la cache de instrucciones
            {return cacheInstruccionesN0[posicionCache] [direccionPalabra];}

            else {return cacheDatosN0[posicionCache] [direccionPalabra];}
            }

        	else //Soy N1
            {
                int posicionCache = (direccionMemoria / 16) % 4;

                if (numeroBloque>24) //Utilizo la cache de instrucciones
                {return cacheInstruccionesN1[posicionCache] [direccionPalabra];}

                else {return cacheDatosN1[posicionCache] [direccionPalabra];}
                }

    }



}

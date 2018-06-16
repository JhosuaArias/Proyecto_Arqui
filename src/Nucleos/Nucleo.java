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
  
  
    public void ejecutar_instruccion(Hilo hiloEjecucion) {
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
                dadd(hiloEjecucion, hiloEjecucion.getRegistro(ejecucion[1]),ejecucion[2],hiloEjecucion.getRegistro(ejecucion[3]));
                break;
            case 34: //Dsub
                dsub(hiloEjecucion, hiloEjecucion.getRegistro(ejecucion[1]),ejecucion[2],hiloEjecucion.getRegistro(ejecucion[3]));
                break;
            case 12: //Dmul
                dmul(hiloEjecucion, hiloEjecucion.getRegistro(ejecucion[1]),ejecucion[2],hiloEjecucion.getRegistro(ejecucion[3]));
                break;
            case 14: //Ddiv
                ddiv(hiloEjecucion, hiloEjecucion.getRegistro(ejecucion[1]),ejecucion[2],hiloEjecucion.getRegistro(ejecucion[3]));
                break;
            case 4: //BEQZ
                //beqz(hiloEjecucion, hiloEjecucion.getRegistro(ejecucion[1]),ejecucion[3]);
                break;
            case 5: //BNEZ
               // bnez(hiloEjecucion, hiloEjecucion.getRegistro(ejecucion[1]),ejecucion[3]);
                break;
            case 3: //Jal
               // jal(hiloEjecucion, hiloEjecucion.getRegistro(ejecucion[1]), hiloEjecucion.getPc());
                break;
            case 2: //JR
               // jr(hiloEjecucion, hiloEjecucion.getRegistro(ejecucion[1]));
                break;
            case 35: //LW
                break;
            case 43: //SW
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

    public void jr(Hilo hiloEjecucion, int PC){
        hiloEjecucion.setPc(PC);
    }

/*Logica de lw*/

    public void lw(int nucleo, int direccionMemoria )
    {

        if (nucleo==0)
        {lwN0(direccionMemoria);}
        else
        {
            lwN1(direccionMemoria);
        }
    }

    public void lwN0(int direccionMemoria){

        boolean noTermine=true;

        while (noTermine)
        {

            int posicion = simulacion.getPosicionCacheN0(direccionMemoria); //posicion en el cache

            if(!(simulacion.intentar_pedirPosicion_CacheDatosN0(posicion))) //No bloquee el indice, vuelve a intentar
            {
                //esperarTick();
            }

            else //Logre bloquear el indice, sigo
            {
                BloqueDatos bloqueCacheDatos= simulacion.getBloqueCacheDatosN0(direccionMemoria); //Obtengo el bloque del cache

                if (bloqueCacheDatos.getEtiqueta()!=simulacion.getNumeroBloque(direccionMemoria)) //La etiqueta no corresponde al bloque
                {
                    antesFalloDeCache();

                    if (!simulacion.intentar_pedirBusDatos_Memoria()) //No pude bloquear el bus
                    {
                        simulacion.desbloquear_Posicion_CacheDatosN0(posicion);
                       // esperarTick();
                    }

                    else //Pude bloquear el bus
                    {
                        if (bloqueCacheDatos.getEstado()== Estado.MODIFICADO) //Es otra etiqueta y esta modificado
                        {
                                /*Esperar 40 tics, cargar el bloque victima a memoria e invalidar*/
                        }

                        int posicionOtroExtremo = simulacion.getPosicionCacheN1(direccionMemoria);

                        if (!(simulacion.intentar_pedirPosicion_CacheDatosN1(posicionOtroExtremo))) //No pude bloquear el otro indice
                        {
                            simulacion.desbloquear_Posicion_CacheDatosN0(posicion);
                            simulacion.desbloquear_BusDatos_Memoria();
                           // esperarTick();
                        }
                        else //pude bloquear el otro indice

                        {
                            lwVerificarOtroCache_vengodeN0(direccionMemoria);
                        }

                    }


                }
                else //La etiqueta corresponde al bloque
                {
                    if(bloqueCacheDatos.getEstado()== Estado.INVALIDO)  //La etiqueta esta invalida
                    {
                        antesFalloDeCache();

                        if (!simulacion.intentar_pedirBusDatos_Memoria()) //No pude bloquear el bus
                        {
                            simulacion.desbloquear_Posicion_CacheDatosN0(posicion);
                           // esperarTick();
                        }

                        else
                        {
                            int posicionOtroExtremo = simulacion.getPosicionCacheN1(direccionMemoria);

                            if (!(simulacion.intentar_pedirPosicion_CacheDatosN1(posicionOtroExtremo))) //No pude bloquear el otro indice
                            {
                                simulacion.desbloquear_Posicion_CacheDatosN0(posicion);
                                simulacion.desbloquear_BusDatos_Memoria();
                             //   esperarTick();
                            }
                            else //Pude bloquear el otro indice
                            {
                                lwVerificarOtroCache_vengodeN0(direccionMemoria);
                            }

                        }


                    }

                    else //La etiqueta no esta invalida
                    {
                                /*Averiguo la palabra, cargo a registro, desbloqueo la posicion*/

                        simulacion.desbloquear_Posicion_CacheDatosN0(posicion);

                                /*Indicar que no se vuelva a meter en el while*/
                        noTermine=false;
                    }
                }

            }

        }
    }


    public void lwN1(int direccionMemoria){

        boolean noTermine=true;

        while (noTermine)
        {

            int posicion = simulacion.getPosicionCacheN1(direccionMemoria); //posicion en el cache

            if(!(simulacion.intentar_pedirPosicion_CacheDatosN1(posicion))) //No bloquee el indice, vuelve a intentar
            {
               // esperarTick();
            }

            else //Logre bloquear el indice, sigo
            {
                BloqueDatos bloqueCacheDatos= simulacion.getBloqueCacheDatosN1(direccionMemoria); //Obtengo el bloque del cache

                if (bloqueCacheDatos.getEtiqueta()!=simulacion.getNumeroBloque(direccionMemoria)) //La etiqueta no corresponde al bloque
                {
                    if (!simulacion.intentar_pedirBusDatos_Memoria()) //No pude bloquear el bus
                    {
                        simulacion.desbloquear_Posicion_CacheDatosN1(posicion);
                      //  esperarTick();
                    }

                    else //Pude bloquear el bus
                    {
                        if (bloqueCacheDatos.getEstado()== Estado.MODIFICADO) //Es otra etiqueta y esta modificado
                        {
                                /*Esperar 40 tics, cargar el bloque victima a memoria e invalidar*/
                        }

                        int posicionOtroExtremo = simulacion.getPosicionCacheN1(direccionMemoria);

                        if (!(simulacion.intentar_pedirPosicion_CacheDatosN1(posicionOtroExtremo))) //No pude bloquear el otro indice
                        {
                            simulacion.desbloquear_Posicion_CacheDatosN1(posicion);
                            simulacion.desbloquear_BusDatos_Memoria();
                           // esperarTick();
                        }
                        else //pude bloquear el otro indice

                        {
                            lwVerificarOtroCache_vengodeN1(direccionMemoria);
                        }

                    }


                }
                else //La etiqueta corresponde al bloque
                {
                    if(bloqueCacheDatos.getEstado()== Estado.INVALIDO)  //La etiqueta esta invalida
                    {
                        if (!simulacion.intentar_pedirBusDatos_Memoria()) //No pude bloquear el bus
                        {
                            simulacion.desbloquear_Posicion_CacheDatosN1(posicion);
                            //esperarTick();
                        }

                        else
                        {
                            int posicionOtroExtremo = simulacion.getPosicionCacheN0(direccionMemoria);

                            if (!(simulacion.intentar_pedirPosicion_CacheDatosN0(posicionOtroExtremo))) //No pude bloquear el otro indice
                            {
                                simulacion.desbloquear_Posicion_CacheDatosN1(posicion);
                                simulacion.desbloquear_BusDatos_Memoria();
                               // esperarTick();
                            }
                            else //Pude bloquear el otro indice
                            {
                                lwVerificarOtroCache_vengodeN1(direccionMemoria);
                            }

                        }


                    }

                    else //La etiqueta no esta invalida
                    {
                                /*Averiguo la palabra, cargo a registro, desbloqueo la posicion*/

                        simulacion.desbloquear_Posicion_CacheDatosN1(posicion);

                                /*Indicar que no se vuelva a meter en el while*/
                        noTermine=false;
                    }
                }

            }

        }
    }

    public  void lwVerificarOtroCache_vengodeN0(int direccionMemoria)
    {
    /*vengo de bloquear el indice del otro cache*/

        BloqueDatos bloqueCacheDatosOtroExtremo = simulacion.getBloqueCacheDatosN1(direccionMemoria);
        if (bloqueCacheDatosOtroExtremo.getEtiqueta()== simulacion.getNumeroBloque(direccionMemoria) && bloqueCacheDatosOtroExtremo.getEstado() == Estado.MODIFICADO) //Corresponde a la etiqueta y esta modificado
        {
        /*Diagrama en morado*/
        }
        else //No corresponde la etiqueta o no esta modificado
        {
        /*Diagrama en anaranjado*/
        }

        seResolvioFalloDeCache();
    }



    public  void lwVerificarOtroCache_vengodeN1(int direccionMemoria)
    {
    /*vengo de bloquear el indice del otro cache*/

        BloqueDatos bloqueCacheDatosOtroExtremo = simulacion.getBloqueCacheDatosN0(direccionMemoria);
        if (bloqueCacheDatosOtroExtremo.getEtiqueta()== simulacion.getNumeroBloque(direccionMemoria) && bloqueCacheDatosOtroExtremo.getEstado() == Estado.MODIFICADO) //Corresponde a la etiqueta y esta modificado
        {
        /*Diagrama en morado*/
        }
        else //No corresponde la etiqueta o no esta modificado
        {
        /*Diagrama en anaranjado*/
        }

    }

    /*Fin de logica de LW*/

    public void antesFalloDeCache() {
    }

    public void seResolvioFalloDeCache() {
    }

    public void sw(){ //Lleva override y super

    }

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

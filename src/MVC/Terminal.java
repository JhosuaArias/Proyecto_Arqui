package MVC;
import Estructuras_Datos.Hilo;
import Nucleos.EstadoThread;
import javafx.util.Pair;

import javax.sound.midi.Soundbank;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Scanner;

public class Terminal {

    private BufferedReader stdIn;

    /***
     * Constructor Terminal
     */
    public Terminal() {
        this.stdIn = new BufferedReader(new InputStreamReader(System.in));
    }

    /***
     * Método que pregunta al usuario por el Quantum. Este se asegura de que el dato sea válido
     * @return devuelve el Valor que digitó el usuario.
     */
    public int askForQuantum() {
        int userInput = 0;
        try {
            while(userInput <= 0 ) {
                System.out.println("-Digite un número valido de tics para el quantum de un hilo");
                System.out.print(">> ");
                try {
                    userInput = Integer.parseInt(stdIn.readLine());
                } catch (NumberFormatException e) {
                    userInput = 0;
                }
            }
            System.out.println("-El quantum de cada hilo es de: " + userInput);
        } catch (IOException e) {
            System.out.println("IOException, could not read from std in");
        }
        return userInput;
    }

    /***
     * Método que pregunta al usuario por la velocidad de la Simulación. Rapida o Lenta.
     * @return retorna un Booleano para determinar si la simulación es Rápida:false o Lenta:true.
     */
    public boolean askForSimulationSpeed()  {
        String userInput = "";
        try {
            while(!(userInput.toLowerCase().compareTo("lento") == 0 || userInput.toLowerCase().compareTo("rapido") == 0)) {
                System.out.println("-Digite \"Lento\" para ejecutar la simulación en modo lento o \"Rapido\" para ejecutarla en modo rápido ");
                System.out.print(">> ");
                userInput = stdIn.readLine();
            }
            if(userInput.toLowerCase().compareTo("lento") == 0) {
                System.out.println("-Ejecutando simulación en modo lento...");
                return true;
            } else {
                System.out.println("-Ejecutando simulación en modo rápido...");
                return false;
            }
        } catch (IOException e) {
            System.out.println("IOException, could not read from std in");
            System.out.println("-Ejecutando simulación en modo lento...");
        }
        return true;
    }

    /***
     * Imprime resultados de un tick determinado.
     * @param simulacion Referencia de la simulación para obtener la información.
     */
    public void imprimirTick(Simulacion simulacion){
        System.out.println("-Tick número: " +simulacion.getTicks());

        if(simulacion.getNucleo0().getHilo() != null){
            System.out.println("-Hilos corriendo en Núcleo 1:");
            System.out.print(simulacion.getNucleo0().getHilo().toString());
            Pair<EstadoThread,Integer> par = simulacion.getNucleo0().getEstadoHilo();
            System.out.println("*Estado: " + par.getKey());
            if(par.getKey() != EstadoThread.EJECUTANDO){
                System.out.println("*Posicion de cache requerido: " + par.getValue());
            }
        }
        
        if(simulacion.getNucleo1().getHilo() != null){
            System.out.println("-Hilos corriendo en Núcleo 1:");
            System.out.print(simulacion.getNucleo1().getHilo().toString());
            Pair<EstadoThread,Integer> par = simulacion.getNucleo1().getEstadoHilo();
            System.out.println("*Estado: " + par.getKey());
            if(par.getKey() != EstadoThread.EJECUTANDO){
                System.out.println("*Posicion de cache requerido: " + par.getValue());
            }
        }
    }

    /***
     * Imprime resultados al final de toda la simulación.
     * @param simulacion Referencia de la simulación para obtener la información.
     */
    public void imprimirEstadoFinal(Simulacion simulacion){

        System.out.println("Ticks pasados: " + simulacion.getTicks());

        System.out.println("-Memoria Principal:");
        System.out.println(simulacion.getMemoriaPrincipal().toString());

        System.out.println("-Caché de datos N0");
        System.out.println(simulacion.getCacheDatosN0().toString());

        System.out.println("-Caché de instrucciones N0");
        System.out.println(simulacion.getCacheInstruccionesN0().toString());

        System.out.println("-Caché de datos N1");
        System.out.println(simulacion.getCacheDatosN1().toString());

        System.out.println("-Caché de instrucciones N1");
        System.out.println(simulacion.getCacheInstruccionesN1().toString());

        System.out.println("Todos los hilos");
        ArrayList<Hilo> hilos = simulacion.getHilos();

        for (Hilo hilo: hilos) {
            System.out.print(hilo.toString()+"\n");
        }
    }

    /***
     * Método que espera a que el usuario presione Enter para continuar.
     */
    public void esperarUsuario(){
        System.out.println("Presione Enter para continuar:");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

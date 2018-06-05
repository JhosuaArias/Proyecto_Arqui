package MVC;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Scanner;

public class Terminal {

    private BufferedReader stdIn;
    private ArrayList<String> messageQueue;

    public Terminal() {
        this.stdIn = new BufferedReader(new InputStreamReader(System.in));
        this.messageQueue = new ArrayList<>();
    }


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

    public boolean askForSimulationSpeed()  {
        String userInput = "";
        try {
            while(!(userInput.compareTo("Lento") == 0 || userInput.compareTo("Rapido") == 0)) {
                System.out.println("-Digite \"Lento\" para ejecutar la simulación en modo lento o \"Rapido\" para ejecutarla en modo rápido ");
                System.out.print(">> ");
                userInput = stdIn.readLine();
            }
            if(userInput.compareTo("Lento") == 0) {
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
}

package IO;

import Estructuras_Datos.Instruccion;

import java.io.*;
import java.util.ArrayList;

public class LectorHilos {
    private String[] fileNames;
    private ArrayList<ArrayList<Instruccion>> instruccionesHilos;

    public LectorHilos(String[] args) {
        this.fileNames = args;
        this.instruccionesHilos = new ArrayList<>();
    }

    public void setInstrucciones() {

        for (String hilo : fileNames) {
            ArrayList<Instruccion> instruccionesHilo = new ArrayList<>();
            File file = new File(hilo);
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));

                String line;
                while ((line = br.readLine()) != null) {
                    String[] split = line.split(" ");
                    int[] datos = new int[split.length];

                    for (int i = 0 ; i < split.length ; i++) {
                        datos[i] = Integer.parseInt(split[i]);
                    }
                    instruccionesHilo.add(new Instruccion(datos));
                }

                this.instruccionesHilos.add(instruccionesHilo);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<ArrayList<Instruccion>> getInstruccionesHilos() {
        return instruccionesHilos;
    }


}

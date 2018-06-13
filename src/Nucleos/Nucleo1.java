package Nucleos;

import Caches.BloqueInstrucciones;
import Caches.Estado;
import Estructuras_Datos.Hilo;
import Estructuras_Datos.Instruccion;
import MVC.Simulacion;

public class Nucleo1 extends Nucleo{

    private Hilo hilo;

    private Thread thread;

    public Nucleo1(Simulacion simulacion, int id){
        super(simulacion,id);
        this.thread = new Thread(this,"Thread 2");
        this.thread.start();
    }

    public void setHilo() {
        this.hilo = this.simulacion.pedirHiloCola();
    }

    private BloqueInstrucciones resolverFalloCacheInstrucciones(int pc) {
        //TODO
        /**
         * Recibe pc que contiene la direccion de memoria
         * Tiene que ir a buscar la instruccion a memoria
         * Tiene que subir y poner esa instruccion en el cache, cambiar el estado
         * de esta cache a C
         * Esperar 40 ticks
         * devolver el resultado
         *
         * Nota: N1 no requiere de reservar el bus
         */
        boolean bloqueado=false;

        /**
         * Aqui continuo haciendo ticks hasta que me den el bus
         * ¿Como saber si no me dieron el bus?
         */

        while (!bloqueado) {
            if(!this.simulacion.intentar_pedirBusInstruc_Memoria()){
                this.esperarTick(false);
            }
            else {
                bloqueado=true;
            }
        }

        /**
         * Espera de 40 ticks en lo que se resuelve el fallo
         */

        int i=0;
        while(i!=40){
            this.esperarTick(false);
            ++i;
        }

        /**
         * Obtengo el bloque de instrucciones desde memoria,
         * ahora hay que cargarlos a la cache de instrucciones
          */
        Instruccion ins[]= simulacion.getBloqueMemoriaInstruccion(pc);
        /**
         * Falta setBloque para cache en simulacion?
         */
        //this.simulacion.setBloqueCacheInstruccionesN1(ins, pc);
        this.simulacion.desbloquear_BusInstruc_Memoria();

        return null;
    }

    private BloqueInstrucciones resolverFalloCacheDatos() {
        //TODO
        return null;
    }

    private void iteracion() {
        boolean mismoHilo = true;

        while (mismoHilo) {
            /**Calculamos el bloque y posicion en caché*/
            int pc = this.hilo.getPc();
            int numeroBloque = this.simulacion.getNumeroBloque(pc);
            BloqueInstrucciones bloqueInstrucciones = this.simulacion.getBloqueCacheInstrucciones(pc, this.id);
            /**Verificamos si hay fallo de caché**/
            if (!(bloqueInstrucciones.getEstado() == Estado.COMPARTIDO && bloqueInstrucciones.getEtiqueta() == numeroBloque)) {
                //Hay fallo
                bloqueInstrucciones = this.resolverFalloCacheInstrucciones(pc);
            }
            /**Se agarra la instrucción**/
            int posicionCache = this.simulacion.getPosicionCacheN1(pc);
            Instruccion instruccion = bloqueInstrucciones.getInstruccion(posicionCache);

            /**Se suma el PC**/
            this.hilo.sumarPc();

            /**Se ejecuta la instruccion**/
            this.ejecutar_instruccion(this.hilo, instruccion);

            /**Verificaciones de fin o quantum**/
            if (this.hilo.isEsFin()) {
                mismoHilo = false;
                this.simulacion.setInactivoHilo(this.hilo.getId());
                this.hilo = null;
            } else if (this.hilo.getQuantumRestante() == 0) {
                this.simulacion.devolverHiloCola(this.hilo);
                this.hilo = null;
                mismoHilo = false;
            }
            /**Esperar un tick**/
            this.esperarTick(true);
        }
    }



    @Override
    public void esperarTick(boolean restarQuantum) {
        super.esperarTick(restarQuantum);
        if(restarQuantum)
            this.hilo.restarQuantum();
    }

    @Override
    public void run() {
        super.run();
        while (!this.simulacion.isColaNull()){
            System.out.println("Soy: " + this.thread.getName());
            this.hilo = this.simulacion.pedirHiloCola();
            if(this.hilo == null){
                this.esperarTick(false);
            } else {
                this.iteracion();
            }
        }
    }


}

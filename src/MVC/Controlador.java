package MVC;

public class Controlador {
    private Simulacion simulacion;
    private Terminal terminal;

    /**
     * Constructor del Controlador.
     * @param args un String array con todos los nombres de los hilillos a ejecutar.
     */
    public Controlador(String[] args) {
        this.terminal = new Terminal();
        this.simulacion = new Simulacion(args,terminal);
    }

    /**
     * Método que inicia la simulación.
     */
    public void init() {
        this.simulacion.init();
    }

    /**
     * Método main de la aplicación
     * @param args un String array con todos los nombres de los hilillos a ejecutar.
     */
    public static void main(String[] args) {
        Controlador controlador;
        controlador = new Controlador(args);
        controlador.init();
    }
}

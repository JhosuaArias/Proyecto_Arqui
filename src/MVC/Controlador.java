package MVC;

public class Controlador {
    private Simulacion simulacion;
    private Terminal terminal;

    public Controlador(String[] args) {
        this.terminal = new Terminal();
        this.simulacion = new Simulacion(args,terminal);
    }

    public void init() {
        this.simulacion.init();
    }

    public static void main(String[] args) {
        Controlador controlador;
        controlador = new Controlador(args);
        controlador.init();
    }
}

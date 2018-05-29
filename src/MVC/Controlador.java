package MVC;

public class Controlador {
    private Simulacion simulacion;
    private Terminal terminal;

    public Controlador() {
        this.simulacion = new Simulacion();
        this.terminal = new Terminal();
    }

    public void init() {

    }

    public static void main(String[] args) {
        Controlador controlador;
        controlador = new Controlador();
        controlador.init();
    }
}

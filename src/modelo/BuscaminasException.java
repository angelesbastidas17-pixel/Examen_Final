package modelo;

public abstract class BuscaminasException extends Exception {

    private static final long serialVersionUID = 1L;

    public BuscaminasException() {
        super("Error en el juego Buscaminas.");
    }

    public BuscaminasException(String mensaje) {
        super(mensaje);
    }

    public BuscaminasException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    public BuscaminasException(Throwable causa) {
        super(causa);
    }
}
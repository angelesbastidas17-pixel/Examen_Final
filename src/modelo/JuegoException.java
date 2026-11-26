package modelo;

public class JuegoException extends BuscaminasException {

    private static final long serialVersionUID = 1L;

    public JuegoException(String mensaje) {
        super(mensaje);
    }

    public JuegoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
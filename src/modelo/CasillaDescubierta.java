package modelo;

public class CasillaDescubierta extends BuscaminasException {

    private static final long serialVersionUID = 1L;

    public CasillaDescubierta() {
        super("La casilla ya se encuentra descubierta.");
    }

    public CasillaDescubierta(String mensaje) {
        super(mensaje);
    }

    public CasillaDescubierta(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
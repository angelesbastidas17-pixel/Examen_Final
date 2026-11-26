package modelo;

public interface TableroJugable {

    boolean descubrirCasilla(int fila, int col) throws CasillaDescubierta, JuegoException;

    void marcarCasilla(int fila, int col) throws JuegoException;

    boolean esVictoria();

    int getTamano();

    Casilla getCasilla(int fila, int col);
}
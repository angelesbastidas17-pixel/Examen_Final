package modelo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class Tablero implements TableroJugable, Serializable {

    private static final long serialVersionUID = 1L;
    private static final int TAMANO_DEFECTO = 10;
    private static final int MINAS_DEFECTO = 10;

    private final int tamano;
    private final int numMinas;
    private Casilla[][] casillas;

    private transient GeneradorDeTablero generador;
    private transient ColocadorDeMinas colocadorMinas;
    private transient CalculadorDeMinasVecinas calculador;
    private transient GestorDescubrimiento gestorDescubrimiento;
    private transient ValidadorDeVictoria validadorVictoria;

    public Tablero() {
        this(TAMANO_DEFECTO, MINAS_DEFECTO);
    }

    public Tablero(int tamano, int numMinas) {
        if (tamano <= 0) {
            throw new IllegalArgumentException("El tamaño del tablero debe ser mayor que 0.");
        }
        if (numMinas < 0 || numMinas >= tamano * tamano) {
            throw new IllegalArgumentException("Cantidad de minas inválida para el tamaño del tablero.");
        }
        this.tamano = tamano;
        this.numMinas = numMinas;
        this.casillas = new Casilla[tamano][tamano];
        inicializarDelegados();
        inicializar();
    }

    private void inicializarDelegados() {
        this.generador = new GeneradorDeTablero();
        this.colocadorMinas = new ColocadorDeMinas();
        this.calculador = new CalculadorDeMinasVecinas();
        this.gestorDescubrimiento = new GestorDescubrimiento();
        this.validadorVictoria = new ValidadorDeVictoria();
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        inicializarDelegados();
    }

    private void inicializar() {
        this.casillas = generador.inicializarTablero(tamano);
        colocadorMinas.colocarMinas(casillas, tamano, numMinas);
        calculador.calcularMinasVecinas(casillas, tamano);
    }

    @Override
    public boolean descubrirCasilla(int fila, int col)
            throws CasillaDescubierta, JuegoException {
        validarCoordenadas(fila, col);
        return gestorDescubrimiento.descubrirCasilla(casillas, fila, col, tamano);
    }

    @Override
    public boolean esVictoria() {
        return validadorVictoria.esVictoria(casillas, tamano);
    }

    @Override
    public int getTamano() {
        return tamano;
    }

    public int getNumMinas() {
        return numMinas;
    }

    @Override
    public Casilla getCasilla(int fila, int col) {
        if (fila < 0 || fila >= tamano || col < 0 || col >= tamano) {
            throw new IndexOutOfBoundsException("Coordenadas fuera de rango: (" + fila + ", " + col + ")");
        }
        return casillas[fila][col];
    }

    private void validarCoordenadas(int fila, int col) throws JuegoException {
        if (fila < 0 || fila >= tamano || col < 0 || col >= tamano) {
            throw new JuegoException("Coordenadas fuera de rango.");
        }
    }

    @Override
    public void marcarCasilla(int fila, int col) throws JuegoException {
        validarCoordenadas(fila, col);
        Casilla c = casillas[fila][col];
        if (c.estaDescubierta()) {
            throw new JuegoException("No puedes marcar una casilla descubierta.");
        }
        c.alternarMarcada();
    }
}
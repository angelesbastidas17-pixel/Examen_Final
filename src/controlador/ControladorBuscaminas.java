package controlador;

import java.util.Scanner;

import modelo.CasillaDescubierta;
import modelo.JuegoException;
import modelo.Tablero;
import vista.VistaJuego;

/**
 * Coordina la interacción entre el modelo (Tablero) y la vista (VistaJuego).
 */
public class ControladorBuscaminas {

    private static final String OPCION_NUEVA_PARTIDA = "1";
    private static final String OPCION_CARGAR_PARTIDA = "2";
    private static final String OPCION_AYUDA = "3";
    private static final String OPCION_SALIR = "4";

    private static final String COMANDO_GUARDAR = "G";
    private static final String PREFIJO_MARCAR = "M ";

    private static final int TAMANO_DEFECTO = 10;
    private static final char LETRA_INICIAL = 'A';

    private Tablero tablero;
    private final VistaJuego vista;
    private final Scanner scanner;
    private final GestorPersistencia gestorPersistencia;

    public ControladorBuscaminas() {
        this(new VistaJuego(), new Scanner(System.in), new GestorPersistencia());
    }

    public ControladorBuscaminas(VistaJuego vista, Scanner scanner, GestorPersistencia gestorPersistencia) {
        if (vista == null || scanner == null || gestorPersistencia == null) {
            throw new IllegalArgumentException("Las dependencias del controlador no pueden ser nulas");
        }
        this.vista = vista;
        this.scanner = scanner;
        this.gestorPersistencia = gestorPersistencia;
    }

    public void ejecutarMenu() {
        boolean salir = false;
        while (!salir) {
            vista.mostrarMenuPrincipal();
            String opcion = leerLinea();
            if (opcion == null) {
                break;
            }
            salir = procesarOpcionMenu(opcion);
        }
        vista.mostrarMensaje("Gracias por jugar");
    }

    private boolean procesarOpcionMenu(String opcion) {
        switch (opcion) {
            case OPCION_NUEVA_PARTIDA:
                nuevaPartida();
                return false;
            case OPCION_CARGAR_PARTIDA:
                cargarPartida();
                return false;
            case OPCION_AYUDA:
                vista.mostrarAyuda();
                esperarEnter();
                return false;
            case OPCION_SALIR:
                return true;
            default:
                vista.mostrarMensaje("Opción inválida");
                return false;
        }
    }

    private void nuevaPartida() {
        this.tablero = new Tablero();
        jugar();
    }

    private void jugar() {
        boolean juegoAcabado = false;
        boolean perdio = false;

        while (!juegoAcabado) {
            vista.mostrarTablero(tablero, false);
            mostrarOpcionesJugada();

            String entrada = leerLinea();
            if (entrada == null) {
                return;
            }

            String comando = entrada.toUpperCase();
            if (comando.equals(COMANDO_GUARDAR)) {
                guardarYSalir();
                return;
            }

            try {
                if (comando.startsWith(PREFIJO_MARCAR)) {
                    marcarCasilla(comando.substring(PREFIJO_MARCAR.length()));
                } else {
                    perdio = descubrirCasilla(comando);
                    juegoAcabado = perdio || tablero.esVictoria();
                }
            } catch (CasillaDescubierta e) {
                vista.mostrarMensaje(e.getMessage());
            } catch (JuegoException e) {
                vista.mostrarMensaje("Error: " + e.getMessage());
            } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
                vista.mostrarMensaje("Entrada no válida. Usa formato A5 o M A5");
            }
        }

        mostrarResultadoFinal(perdio);
    }

    private void mostrarOpcionesJugada() {
        vista.mostrarMensaje("\nIngresa una jugada:");
        vista.mostrarMensaje("Ejemplo descubrir: A5");
        vista.mostrarMensaje("Ejemplo marcar: M A5");
        vista.mostrarMensaje("G para guardar y salir");
        System.out.print("> ");
    }

    private void marcarCasilla(String coordenada) throws JuegoException {
        int[] posicion = parsearCoordenadas(coordenada);
        tablero.marcarCasilla(posicion[0], posicion[1]);
    }

    private boolean descubrirCasilla(String coordenada) throws CasillaDescubierta, JuegoException {
        int[] posicion = parsearCoordenadas(coordenada);
        return tablero.descubrirCasilla(posicion[0], posicion[1]);
    }

    private void mostrarResultadoFinal(boolean perdio) {
        vista.mostrarTablero(tablero, true);
        if (perdio) {
            vista.mostrarMensaje("Has pisado una mina, perdiste.");
        } else {
            vista.mostrarMensaje("Felicidades! ganaste.");
        }
        esperarEnter();
    }

    private int[] parsearCoordenadas(String entrada) throws JuegoException {
        if (entrada == null) {
            throw new JuegoException("Formato de coordenada incorrecto.");
        }

        String limpia = entrada.trim().toUpperCase();
        if (limpia.length() < 2) {
            throw new JuegoException("Formato de coordenada incorrecto.");
        }

        // Si aún no hay tablero activo (p. ej. en pruebas aisladas), se usa el tamaño por defecto.
        int tamano = (tablero != null) ? tablero.getTamano() : TAMANO_DEFECTO;
        char ultimaLetra = (char) (LETRA_INICIAL + tamano - 1);

        char letraFila = limpia.charAt(0);
        if (letraFila < LETRA_INICIAL || letraFila > ultimaLetra) {
            throw new JuegoException("La fila debe ser una letra (" + LETRA_INICIAL + "-" + ultimaLetra + ").");
        }
        int fila = letraFila - LETRA_INICIAL;

        String columnaTexto = limpia.substring(1).trim();
        int columna;
        try {
            columna = Integer.parseInt(columnaTexto) - 1;
        } catch (NumberFormatException e) {
            throw new JuegoException("La columna debe ser un número.", e);
        }

        if (columna < 0 || columna >= tamano) {
            throw new JuegoException("La columna debe estar entre 1 y " + tamano + ".");
        }

        return new int[]{fila, columna};
    }

    private void guardarYSalir() {
        try {
            gestorPersistencia.guardar(tablero);
            vista.mostrarMensaje("Partida guardada. Volviendo al menú...");
        } catch (JuegoException e) {
            vista.mostrarMensaje("Error: " + e.getMessage());
        }
    }

    private void cargarPartida() {
        try {
            this.tablero = gestorPersistencia.cargar();
            vista.mostrarMensaje("Partida cargada");
            esperarEnter();
            jugar();
        } catch (JuegoException e) {
            vista.mostrarMensaje(e.getMessage());
            esperarEnter();
        }
    }

    private String leerLinea() {
        if (!scanner.hasNextLine()) {
            return null;
        }
        return scanner.nextLine().trim();
    }

    private void esperarEnter() {
        vista.mostrarMensaje("Presiona ENTER para continuar...");
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
    }
}
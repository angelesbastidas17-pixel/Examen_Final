package vista;

import modelo.Casilla;
import modelo.Tablero;


public class VistaJuego {

    private static final String SEPARADOR_BANNER = "=====================================";
    private static final String SIMBOLO_MINA = "*";
    private static final String SIMBOLO_VACIO = " ";
    private static final String SIMBOLO_BANDERA = "P";
    private static final String SIMBOLO_OCULTA = ".";
    private static final char LETRA_INICIAL = 'A';

    public void mostrarBanner() {
        System.out.println(SEPARADOR_BANNER);
        System.out.println("           BUSCAMINAS            ");
        System.out.println(SEPARADOR_BANNER);
    }

    public void mostrarMenuPrincipal() {
        mostrarBanner();
        System.out.println("1. Nueva partida");
        System.out.println("2. Cargar partida");
        System.out.println("3. Ayuda");
        System.out.println("4. Salir");
        System.out.print("Elige una opción: ");
    }

    public void mostrarAyuda() {
        System.out.println("\nAyuda:");
        System.out.println("1. El objetivo es descubrir todas las casillas sin minas");
        System.out.println("2. Ingresa coordenadas como 'A5' o 'C10'");
        System.out.println("3. Para poner o quitar una bandera, escribe 'M' antes");
        System.out.println("   Ejemplo: 'M A5'");
        System.out.println("4. Escribe 'G' durante el juego para guardar y salir");
    }

    public void mostrarTablero(Tablero tablero, boolean mostrarMinas) {
        if (tablero == null) {
            throw new IllegalArgumentException("El tablero no puede ser nulo.");
        }

        int tamano = tablero.getTamano();
        imprimirEncabezadoColumnas(tamano);
        imprimirBordeHorizontal(tamano);

        for (int fila = 0; fila < tamano; fila++) {
            imprimirFila(tablero, fila, mostrarMinas);
        }

        imprimirBordeHorizontal(tamano);
    }

    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    private void imprimirEncabezadoColumnas(int tamano) {
        StringBuilder encabezado = new StringBuilder("\n    ");
        for (int col = 1; col <= tamano; col++) {
            encabezado.append(String.format("%2d ", col));
        }
        System.out.println(encabezado);
    }

    private void imprimirBordeHorizontal(int tamano) {
        StringBuilder borde = new StringBuilder("   +");
        for (int col = 0; col < tamano; col++) {
            borde.append("---");
        }
        borde.append("+");
        System.out.println(borde);
    }

    private void imprimirFila(Tablero tablero, int fila, boolean mostrarMinas) {
        char letra = (char) (LETRA_INICIAL + fila);
        StringBuilder linea = new StringBuilder();
        linea.append(" ").append(letra).append(" |");

        int tamano = tablero.getTamano();
        for (int col = 0; col < tamano; col++) {
            Casilla casilla = tablero.getCasilla(fila, col);
            String simbolo = obtenerSimboloCasilla(casilla, mostrarMinas);
            linea.append(" ").append(simbolo).append(" ");
        }

        linea.append("|");
        System.out.println(linea);
    }

    private String obtenerSimboloCasilla(Casilla casilla, boolean mostrarMinas) {
        if (casilla.estaDescubierta()) {
            if (casilla.esMina()) {
                return SIMBOLO_MINA;
            }
            int minasVecinas = casilla.getMinasVecinas();
            return (minasVecinas == 0) ? SIMBOLO_VACIO : String.valueOf(minasVecinas);
        }

        if (casilla.estaMarcada()) {
            return SIMBOLO_BANDERA;
        }

        if (mostrarMinas && casilla.esMina()) {
            return SIMBOLO_MINA;
        }

        return SIMBOLO_OCULTA;
    }
}
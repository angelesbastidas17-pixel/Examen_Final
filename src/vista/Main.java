package vista;

import java.util.Scanner;
import controlador.ControladorBuscaminas;
import controlador.GestorPersistencia;

public class Main {

    public static void main(String[] args) {
        VistaJuego vista = new VistaJuego();
        GestorPersistencia gestorPersistencia = new GestorPersistencia();

        try (Scanner scanner = new Scanner(System.in)) {
            ControladorBuscaminas controlador =
                    new ControladorBuscaminas(vista, scanner, gestorPersistencia);
            controlador.ejecutarMenu();
        }
    }
}


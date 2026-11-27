package controlador;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import modelo.JuegoException;
import modelo.Tablero;

/**
 * Gestiona el guardado y carga de partidas de Buscaminas mediante serialización.
 */
public class GestorPersistencia {

    private static final String ARCHIVO_POR_DEFECTO = "buscaminas.dat";

    private final String rutaArchivo;

    public GestorPersistencia() {
        this(ARCHIVO_POR_DEFECTO);
    }

    public GestorPersistencia(String rutaArchivo) {
        if (rutaArchivo == null || rutaArchivo.trim().isEmpty()) {
            throw new IllegalArgumentException("La ruta del archivo no puede estar vacía.");
        }
        this.rutaArchivo = rutaArchivo.trim();
    }

    public void guardar(Tablero tablero) throws JuegoException {
        if (tablero == null) {
            throw new IllegalArgumentException("No se puede guardar un tablero nulo.");
        }

        try (ObjectOutputStream salida =
                     new ObjectOutputStream(new FileOutputStream(rutaArchivo))) {
            salida.writeObject(tablero);
        } catch (NotSerializableException e) {
            throw new JuegoException("El estado del tablero no es serializable.", e);
        } catch (IOException e) {
            throw new JuegoException("No se pudo guardar la partida.", e);
        }
    }

    public Tablero cargar() throws JuegoException {
        try (ObjectInputStream entrada =
                     new ObjectInputStream(new FileInputStream(rutaArchivo))) {
            Object objetoLeido = entrada.readObject();
            if (!(objetoLeido instanceof Tablero)) {
                throw new JuegoException("El archivo guardado no contiene un tablero válido.");
            }
            return (Tablero) objetoLeido;
        } catch (FileNotFoundException e) {
            throw new JuegoException("No existe ninguna partida guardada.", e);
        } catch (InvalidClassException e) {
            throw new JuegoException("La partida guardada es incompatible con la versión actual.", e);
        } catch (ClassNotFoundException | IOException e) {
            throw new JuegoException("No se pudo cargar la partida guardada.", e);
        }
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }
}
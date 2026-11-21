package ar.edu.unmdp.tpg.bitacora;

import ar.edu.unmdp.tpg.destino.Observable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Registro temporal de lo que ocurre en la nave: eventos, errores, cambios del
 * Motor Warp, misiones y operaciones sobre recursos.
 *
 * Patrón Observer (Clase 11): la bitácora es observable. Cada vez que registra un
 * evento notifica a sus observadores con el texto del evento, así la vista de
 * Control de la Nave la muestra en vivo sin que la bitácora sepa nada de Swing.
 * Se reutiliza la clase Observable que provee la cátedra junto con Destino y Tablero.
 *
 * Concurrencia: durante una misión registra el hilo de la misión mientras la
 * vista puede consultarla; por eso registrar y consultar son synchronized.
 *
 * Invariantes (metodo invariante()):
 * - La lista de eventos no contiene nulos.
 * - Los eventos se conservan en el orden en que se registraron y no se modifican.
 */
public class Bitacora extends Observable {

    private final List<EventoBitacora> eventos = new ArrayList<>();

    /**
     * Registra un evento en la bitácora.
     *
     * Precondiciones (assert, comprobadas en EventoBitacora):
     * - categoria y descripcion no son nulas, vacías ni contienen solo espacios.
     *
     * Postcondiciones:
     * - Se agrega un evento al final de la bitácora con la categoría y la descripción recibidas, y con la fecha y hora de su creación.
     * - Los eventos anteriores permanecen sin cambios y en el mismo orden.
     * - Se notifica a los observadores con el texto del evento.
     *
     * @param categoria categoría del evento
     * @param descripcion descripción del evento
     */
    public synchronized void registrar(String categoria, String descripcion) {
        int cantidadAnterior = eventos.size();
        EventoBitacora evento = new EventoBitacora(categoria, descripcion);
        eventos.add(evento);

        assert eventos.size() == cantidadAnterior + 1 && eventos.get(cantidadAnterior) == evento
            : "Fallo postcondición: el evento debe quedar al final de la bitácora";
        assert invariante() : "Fallo invariante de Bitacora";
        notificarObservadores(evento.toString());
    }

    /**
     * Invariante de clase de la bitacora.
     *
     * @return true si ningun evento registrado es nulo
     */
    private boolean invariante() {
        for (EventoBitacora evento : eventos) {
            if (evento == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Consulta los eventos registrados, del mas viejo al mas nuevo.
     *
     * Postcondiciones:
     * - La bitacora no se modifica.
     * - Devuelve una copia que no se puede modificar: la bitacora solo cambia por
     *   registrar(), y la copia no se altera si otro hilo registra despues.
     *
     * @return los eventos de la bitacora, en orden temporal
     */
    public synchronized List<EventoBitacora> consultarEventos() {
        return Collections.unmodifiableList(new ArrayList<>(eventos));
    }
}

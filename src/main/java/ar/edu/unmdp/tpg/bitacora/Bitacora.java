package ar.edu.unmdp.tpg.bitacora;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
es para registrar eventos,errores, cambios del Motor , y cosas de
misiones y  recursos.
**/
public class Bitacora {

    private final List<EventoBitacora> eventos = new ArrayList<>();

    /**
     * Registra un evento en la bitácora.
     *
     * Precondiciones:
     * - descripcion no es nula, vacía ni contiene solo espacios.
     *
     * Postcondiciones:
     * - Se agrega un evento al final de la bitácora con la categoría y la descripción recibidas, y con la fecha y hora de su creación.
     * - Los eventos anteriores permanecen sin cambios y en el mismo orden.
     * - Si el registro se rechaza, la bitácora permanece sin cambios.
     *
     * @param categoria categoría del evento
     * @param descripcion descripción del evento
     * @throws IllegalArgumentException si la descripción es nula, vacía
     *         o contiene solo espacios
     */
    public void registrar(String categoria, String descripcion) {
        eventos.add(new EventoBitacora(categoria, descripcion));
    }

    /**
     * Consulta los eventos registrados, del mas viejo al mas nuevo.
     *
     * Postcondiciones:
     * - La bitacora no se modifica.
     * - La lista devuelta no se puede modificar: la bitacora solo cambia por registrar().
     *
     * @return los eventos de la bitacora, en orden temporal
     */
    public List<EventoBitacora> consultarEventos() {
        return Collections.unmodifiableList(eventos);
    }
}

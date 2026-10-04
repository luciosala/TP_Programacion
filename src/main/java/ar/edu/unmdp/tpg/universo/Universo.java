package ar.edu.unmdp.tpg.universo;

import ar.edu.unmdp.tpg.asistente.AsistenteDeComando;

import java.util.ArrayList;
import java.util.List;

/**
 * Centro de control: registra las naves listas para operar (a traves de su
 * asistente) y las devuelve cuando se le piden. Es el punto de entrada del sistema.
 *
 * No crea los asistentes que guarda: los recibe ya construidos. Depende de la
 * interfaz AsistenteDeComando, asi acepta cualquier variante sin modificarse.
 */
public class Universo {

    private final List<AsistenteDeComando> asistentes = new ArrayList<>();
    private AsistenteDeComando naveActiva;

    /**
     * Registra una nave lista para operar, a traves de su asistente.
     *
     * Precondiciones:
     * - asistente no es nulo.
     * - No hay otra nave registrada con el mismo id.
     *
     * Postcondiciones:
     * - El asistente queda registrado en el universo.
     * - Si se rechaza, el universo no cambia.
     *
     * @param asistente asistente de la nave a registrar
     * @throws IllegalArgumentException si el asistente es nulo o el id esta repetido
     */
    public void registrar(AsistenteDeComando asistente) {
        if (asistente == null) {
            throw new IllegalArgumentException("No se puede registrar un asistente nulo");
        }
        if (buscarPorId(asistente.idNave()) != null) {
            throw new IllegalArgumentException("Ya hay una nave registrada con id " + asistente.idNave());
        }
        asistentes.add(asistente);
    }
        /**
     * Devuelve el asistente de una nave registrada.
     *
     * Precondiciones:
     * - idNave no es nulo ni vacio.
     * - Hay una nave registrada con ese id.
     *
     * Postcondiciones:
     * - Devuelve el asistente de la nave pedida. El universo no cambia.
     *
     * @param idNave id de la nave buscada
     * @return el asistente que opera esa nave
     * @throws IllegalArgumentException si el id es invalido o no hay una nave con ese id
     */
    public AsistenteDeComando obtener(String idNave) {
        if (idNave == null || idNave.isBlank()) {
            throw new IllegalArgumentException("El id de la nave no puede ser nulo ni vacío");
        }
        AsistenteDeComando asistente = buscarPorId(idNave);
        if (asistente == null) {
            throw new IllegalArgumentException("No hay una nave registrada con id " + idNave);
        }
        return asistente;
    }

        /**
     * Elige la nave con la que se va a operar. Solo puede haber una activa a la vez:
     * elegir otra reemplaza a la anterior.
     *
     * Precondiciones:
     * - Hay una nave registrada con ese id.
     *
     * Postcondiciones:
     * - La nave pedida queda como activa.
     * - Si se rechaza, la nave activa no cambia.
     *
     * @param idNave id de la nave a operar
     * @throws IllegalArgumentException si el id es invalido o no hay una nave con ese id
     */
    public void seleccionarNave(String idNave) {
        naveActiva = obtener(idNave);
    }

    /**
     * Devuelve el asistente de la nave con la que se esta operando.
     *
     * Precondiciones:
     * - Se selecciono una nave previamente.
     *
     * @return el asistente de la nave activa
     * @throws IllegalStateException si todavia no se selecciono ninguna nave
     */
    public AsistenteDeComando getNaveActiva() {
        if (naveActiva == null) {
            throw new IllegalStateException("No hay ninguna nave seleccionada");
        }
        return naveActiva;
    }

    // Devuelve el asistente de la nave con ese id, o null si no esta registrada.
    private AsistenteDeComando buscarPorId(String idNave) {
        for (AsistenteDeComando asistente : asistentes) {
            if (asistente.idNave().equals(idNave)) {
                return asistente;
            }
        }
        return null;
    }
}
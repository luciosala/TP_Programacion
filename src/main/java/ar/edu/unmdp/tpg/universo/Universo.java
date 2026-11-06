package ar.edu.unmdp.tpg.universo;

import ar.edu.unmdp.tpg.asistente.AsistenteDeComando;
import ar.edu.unmdp.tpg.excepciones.NaveNoRegistradaException;
import ar.edu.unmdp.tpg.excepciones.NaveYaRegistradaException;

import java.util.ArrayList;
import java.util.List;

/**
 * Centro de control: registra las naves listas para operar (a traves de su
 * asistente) y las devuelve cuando se le piden. Es el punto de entrada del sistema.
 *
 * No crea los asistentes que guarda: los recibe ya construidos. Depende de la
 * interfaz AsistenteDeComando, asi acepta cualquier variante sin modificarse.
 *
 * Invariantes (metodo invariante()):
 * - No hay asistentes nulos ni dos naves registradas con el mismo id.
 * - Si hay una nave activa, es una de las registradas.
 */
public class Universo {

    private final List<AsistenteDeComando> asistentes = new ArrayList<>();
    private AsistenteDeComando naveActiva;

    /**
     * Registra una nave lista para operar, a traves de su asistente.
     *
     * Precondiciones (assert):
     * - asistente no es nulo.
     *
     * Postcondiciones:
     * - El asistente queda registrado en el universo y puede obtenerse por su id.
     * - Si se rechaza, el universo no cambia.
     *
     * @param asistente asistente de la nave a registrar
     * @throws NaveYaRegistradaException rechazo de dominio: ya hay otra nave con ese id
     */
    public void registrar(AsistenteDeComando asistente) throws NaveYaRegistradaException {
        assert asistente != null : "No se puede registrar un asistente nulo";

        if (estaRegistrada(asistente.idNave())) {
            throw new NaveYaRegistradaException(asistente.idNave());
        }

        int cantidadAnterior = asistentes.size();
        asistentes.add(asistente);

        assert asistentes.size() == cantidadAnterior + 1 && buscarPorId(asistente.idNave()) == asistente
            : "Fallo postcondición de registrar";
        assert invariante() : "Fallo invariante de Universo";
    }

    /**
     * Consulta si hay una nave registrada con ese id.
     *
     * Precondiciones (assert):
     * - idNave no es nulo ni vacio.
     *
     * @param idNave id de la nave
     * @return true si hay una nave registrada con ese id
     */
    public boolean estaRegistrada(String idNave) {
        assert idNave != null && !idNave.isBlank() : "El id de la nave no puede ser nulo ni vacío";
        return buscarPorId(idNave) != null;
    }

    /**
     * Cuenta las naves registradas.
     *
     * @return cantidad de naves registradas
     */
    public int cantidadNaves() {
        return asistentes.size();
    }

    /**
     * Invariante de clase del universo.
     *
     * @return true si el universo esta en un estado valido
     */
    private boolean invariante() {
        for (int i = 0; i < asistentes.size(); i++) {
            if (asistentes.get(i) == null) {
                return false;
            }
            for (int j = i + 1; j < asistentes.size(); j++) {
                if (asistentes.get(i).idNave().equals(asistentes.get(j).idNave())) {
                    return false;
                }
            }
        }
        return naveActiva == null || asistentes.contains(naveActiva);
    }
        /**
     * Devuelve el asistente de una nave registrada.
     *
     * Precondiciones (assert):
     * - idNave no es nulo ni vacio.
     *
     * Postcondiciones:
     * - Devuelve el asistente de la nave pedida. El universo no cambia.
     *
     * @param idNave id de la nave buscada
     * @return el asistente que opera esa nave
     * @throws NaveNoRegistradaException rechazo de dominio: no hay una nave con ese id
     *         (como NoExisteException en la teoria de Excepciones)
     */
    public AsistenteDeComando obtener(String idNave) throws NaveNoRegistradaException {
        assert idNave != null && !idNave.isBlank() : "El id de la nave no puede ser nulo ni vacío";

        AsistenteDeComando asistente = buscarPorId(idNave);
        if (asistente == null) {
            throw new NaveNoRegistradaException(idNave);
        }
        return asistente;
    }

        /**
     * Elige la nave con la que se va a operar. Solo puede haber una activa a la vez:
     * elegir otra reemplaza a la anterior.
     *
     * Precondiciones (assert):
     * - idNave no es nulo ni vacio.
     *
     * Postcondiciones:
     * - La nave pedida queda como activa.
     * - Si se rechaza, la nave activa no cambia.
     *
     * @param idNave id de la nave a operar
     * @throws NaveNoRegistradaException rechazo de dominio: no hay una nave con ese id
     */
    public void seleccionarNave(String idNave) throws NaveNoRegistradaException {
        naveActiva = obtener(idNave);
        assert naveActiva.idNave().equals(idNave) : "Fallo postcondición de seleccionarNave";
        assert invariante() : "Fallo invariante de Universo";
    }

    /**
     * Consulta si ya se eligio una nave para operar.
     *
     * @return true si hay una nave activa
     */
    public boolean hayNaveActiva() {
        return naveActiva != null;
    }

    /**
     * Devuelve el asistente de la nave con la que se esta operando.
     *
     * Precondiciones (assert):
     * - hayNaveActiva() es true.
     *
     * @return el asistente de la nave activa
     */
    public AsistenteDeComando getNaveActiva() {
        assert hayNaveActiva() : "No hay ninguna nave seleccionada";
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
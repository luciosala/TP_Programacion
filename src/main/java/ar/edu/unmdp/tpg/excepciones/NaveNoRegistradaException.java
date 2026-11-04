package ar.edu.unmdp.tpg.excepciones;

/**
 * Se lanza cuando se pide al universo una nave que no esta registrada.
 *
 * Sigue el ejemplo NoExisteException de la teoria de Excepciones: buscar algo
 * que no existe no se puede resolver dentro del metodo, y es responsabilidad
 * del invocante buscar una solucion. Guarda el id buscado para que la zona de
 * recuperacion pueda usarlo.
 */
public class NaveNoRegistradaException extends NaveException {

    private final String idNave;

    /**
     * Construye la excepcion con el id que no se encontro.
     *
     * @param idNave id de la nave buscada
     */
    public NaveNoRegistradaException(String idNave) {
        super("No hay una nave registrada con id " + idNave);
        this.idNave = idNave;
    }

    /**
     * Devuelve el id de la nave que no se encontro.
     *
     * @return id buscado
     */
    public String getIdNave() {
        return idNave;
    }
}

package ar.edu.unmdp.tpg.excepciones;

/**
 * Se lanza cuando se intenta registrar en el universo una nave cuyo id ya
 * pertenece a otra nave registrada. Es un rechazo del dominio: el universo no
 * cambia. Guarda el id repetido, como recomienda la teoria de Excepciones
 * ("que informacion debe almacenar la excepcion").
 */
public class NaveYaRegistradaException extends NaveException {

    private final String idNave;

    /**
     * Construye la excepcion con el id repetido.
     *
     * @param idNave id que ya estaba registrado
     */
    public NaveYaRegistradaException(String idNave) {
        super("Ya hay una nave registrada con id " + idNave);
        this.idNave = idNave;
    }

    /**
     * Devuelve el id que ya estaba registrado.
     *
     * @return id repetido
     */
    public String getIdNave() {
        return idNave;
    }
}

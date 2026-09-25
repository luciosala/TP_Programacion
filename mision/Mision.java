import java.util.ArrayList;
import java.util.List;
abstract public class Mision{
    protected String nombre;
    protected String descripcion;
    int energiaConsumida =0,combustibleConsumido=0,desgasteAcumulado=0 ; //acumuladores de energia y combustible
    protected final AsistenteDeComando asistente;

    /**
     * Construye una misión
     *
     * La misión no conoce la nave ni la bitácora: todo lo que necesita
     * (consultar recursos, consumirlos, registrar lo ocurrido) se lo pide
     * al Asistente de Comando.
     *
     * @param nombre nombre identificatorio de la misión; no puede ser nulo
     * @param descripcion descripción de la misión; no puede ser nula
     * @param asistente asistente que coordina la misión; no puede ser nulo
     * @throws IllegalArgumentException si {@code nombre}, {@code descripcion} o {@code asistente} son nulos
     */
    protected Mision(String nombre, String descripcion, AsistenteDeComando asistente) {
        if (nombre == null)
            throw new IllegalArgumentException("El nombre de la misión no puede ser nulo");

        if (descripcion == null)
            throw new IllegalArgumentException("La descripción de la misión no puede ser nula ");

        if (asistente == null)
            throw new IllegalArgumentException("La misión requiere un asistente de comando");


        this.nombre = nombre;
        this.descripcion = descripcion;
        this.asistente = asistente;
    }

    //Ejecuta el ciclo común sin permitir que las subclases alteren su orden.
     //PATRON Template METHOD
    //Las fallas del dominio suben al Asistente de Comando, que las registra.
    public final InformeMision realizarMision() throws NaveException {
        preparar();

        ejecutar();
        boolean exito = evaluarResultado();
        return cerrar(exito);
    }

    /*metodos protected para que los hijos puedan accedrr */

    protected final void preparar() throws MisionNoViableException {
        try {
            asistente.validarTripulacionMinima();

            asistente.verificarRecursos(4,costoAccionFinal(),4);

            registrarAccion("Preparación completada");
        } catch (NaveException error) {
            asistente.registrarEvento("MISION", nombre + ": preparación rechazada: " + error.getMessage());

            throw new MisionNoViableException(
                "No se puede iniciar " + nombre + ": " + error.getMessage()
            );
        }
    }
   protected final void ejecutar() throws NaveException {

        asistente.consumirParaMision(4,0,4);
        combustibleConsumido+=4;
        desgasteAcumulado+=4;
        ejecutaMision();
   }
   /**
 * Determina si la misión cumplió su objetivo y cobra el costo de la acción
 * final. Si la energía no alcanza, la acción no se realiza y no se consume
 * nada: la misión se cierra como fallida.

 */
protected final boolean evaluarResultado() throws NaveException {
    if (!condicionDeExito()) {
        registrarAccion("Objetivo no alcanzado");
        return false;
    }

    int costo = costoAccionFinal();

    if (costo > 0) {
        if (asistente.energiaDisponible() < costo) {
            registrarAccion("Energía insuficiente para completar la acción final");
            return false;
        }
        asistente.consumirEnergia(costo);
        energiaConsumida += costo;
    }

    registrarAccion("Acción final completada");
    return true;
}
private final List<String> acciones = new ArrayList<>();

/**
 * Deja constancia de una acción tanto en el informe como en la Bitácora,
 * a través del Asistente de Comando.
 *
 * @param detalle descripción de la acción realizada; no puede ser nulo ni vacío
 */
protected final void registrarAccion(String detalle) {
    if (detalle == null || detalle.isBlank()) {
        throw new IllegalArgumentException("El detalle de la acción no puede ser nulo ni vacío");
    }
    acciones.add(detalle);
    asistente.registrarEvento("MISION", nombre + ": " + detalle);
}


/**
 * Cierra la misión: deja constancia del resultado, manda el motor a enfriarse
 * y pide al asistente el informe con lo acumulado durante el ciclo.
 *
 * @param exito resultado devuelto por la evaluación
 * @return el informe de la misión ejecutada
 */
protected final InformeMision cerrar(boolean exito) {
    asistente.registrarEvento("MISION", nombre + " finalizada: " + (exito ? "EXITOSA" : "FALLIDA"));

    asistente.enfriarMotorTrasMision();

    return asistente.crearInforme(
        nombre,
        descripcion,
        descripcionObjetivo(),
        acciones,
        combustibleConsumido,
        energiaConsumida,
        desgasteAcumulado,
        exito
    );
}
/** Energía que cuesta la acción final de esta misión (0 si no tiene costo). */
protected abstract int costoAccionFinal();
/** Describe el objetivo que persigue esta misión. */
protected abstract String descripcionObjetivo();
protected abstract void ejecutaMision() throws NaveException;
protected abstract boolean condicionDeExito();
}

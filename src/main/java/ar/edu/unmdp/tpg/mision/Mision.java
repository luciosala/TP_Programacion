package ar.edu.unmdp.tpg.mision;

import ar.edu.unmdp.tpg.asistente.AsistenteDeComando;
import ar.edu.unmdp.tpg.excepciones.MisionNoViableException;
import ar.edu.unmdp.tpg.excepciones.NaveException;
import ar.edu.unmdp.tpg.excepciones.TransicionInvalidaException;


import java.util.ArrayList;
import java.util.List;
abstract public class Mision{
    protected String nombre;
    protected String descripcion;
    int energiaConsumida =0,combustibleConsumido=0,desgasteAcumulado=0 ; //acumuladores de energia y combustible
    protected final AsistenteDeComando asistente;
    private boolean realizada = false;

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

    /**
     * Ejecuta el ciclo completo de la misión: preparar, ejecutar, evaluar y cerrar.
     *
     * PATRON TEMPLATE METHOD: este método es final, así las subclases no pueden
     * alterar el orden de las cuatro etapas. Lo que cada misión define es el
     * contenido de los puntos de extensión (ejecutaMision, condicionDeExito,
     * costoAccionFinal, descripcionObjetivo, combustibleNecesario y desgasteQueGenera),
     * nunca la secuencia.
     *
     * Las fallas del dominio suben al Asistente de Comando, que las registra.
     *
     * Precondiciones:
     * - La misión no fue ejecutada antes: una misión se realiza una sola vez.
     * - El Motor Warp de la nave está Disponible.
     * - La nave cumple la tripulación mínima.
     * - La nave dispone de los recursos que la misión va a necesitar.
     *
     * Postcondiciones:
     * - La misión queda marcada como realizada y no puede volver a ejecutarse.
     * - Si la preparación falla, no se consume ningún recurso, el motor no cambia
     *   de estado y queda registrado el rechazo en la bitácora.
     * - Si la misión se ejecuta, se consumen el combustible y el desgaste de la
     *   operación, y la energía de la acción final solo si el objetivo se cumplió
     *   y alcanzaba.
     * - Si el resultado es exitoso, el motor recorre el ciclo completo de salto y
     *   vuelve a quedar Disponible; si es fallido, el motor no se usa.
     * - Devuelve siempre un informe, tanto en el caso exitoso como en el fallido.
     *
     * @return el informe de la misión ejecutada
     * @throws MisionNoViableException si la misión ya fue realizada o no puede prepararse
     * @throws NaveException si falla alguna operación del dominio durante el ciclo
     */
    public final InformeMision realizarMision() throws NaveException {
        if (realizada) {
        throw new MisionNoViableException(nombre + " ya fue realizada; para repetirla hay que crear otra misión");
        }
        preparar();
        realizada = true;
        ejecutar();
        boolean exito = evaluarResultado();
        return cerrar(exito);
    }

    /*metodos protected para que los hijos puedan accedrr */

    /**
     * Primera etapa del ciclo: comprueba que la misión sea viable antes de tocar
     * ningún recurso.
     *
     * Precondiciones:
     * - El Motor Warp está Disponible.
     * - La nave cumple la tripulación mínima.
     * - La nave dispone del combustible, la energía y el margen de desgaste que
     *   la misión va a necesitar.
     *
     * Postcondiciones:
     * - No se modifica ningún recurso ni el estado del motor: esta etapa solo verifica.
     * - Si alguna condición no se cumple, el motivo queda registrado en la bitácora
     *   y la misión no continúa.
     * - Si se completa, queda registrada la acción "Preparación completada".
     *
     * @throws MisionNoViableException si el motor no está disponible, falta
     *         tripulación o no alcanzan los recursos
     */
    protected final void preparar() throws MisionNoViableException {
        try {
            if (!asistente.motorDisponible()) {
                throw new MisionNoViableException(
                    "el Motor Warp no está disponible (estado: " + asistente.estadoDelMotor() + ")");
            }
            asistente.validarTripulacionMinima();

            asistente.verificarRecursos(combustibleNecesario(),costoAccionFinal(),desgasteQueGenera());

            registrarAccion("Preparación completada");
        } catch (NaveException error) {
            asistente.registrarEvento("MISION", nombre + ": preparación rechazada: " + error.getMessage());

            throw new MisionNoViableException(
                "No se puede iniciar " + nombre + ": " + error.getMessage()
            );
        }
    }
   /**
    * Segunda etapa del ciclo: cobra el costo de la operación y delega en la
    * misión concreta lo que esa misión hace.
    *
    * Precondiciones:
    * - La preparación se completó con éxito.
    *
    * Postcondiciones:
    * - El combustible baja y el desgaste sube en las cantidades que declara la
    *   misión, y quedan acumulados para el informe.
    * - La energía no se toca en esta etapa: su costo es el de la acción final.
    * - Si el consumo se rechaza, no se modifica ningún recurso y la misión no
    *   ejecuta su parte específica.
    *
    * @throws NaveException si falta combustible, si el desgaste superaría el
    *         límite de 100, o si falla la parte específica de la misión
    */
   protected final void ejecutar() throws NaveException {

        asistente.consumirParaMision(combustibleNecesario(),0,desgasteQueGenera());
        combustibleConsumido+=combustibleNecesario();
        desgasteAcumulado+=desgasteQueGenera();
        ejecutaMision();
   }
   /**
 * Tercera etapa del ciclo: determina si la misión cumplió su objetivo y cobra
 * el costo de la acción final.
 *
 * Precondiciones:
 * - La misión ya ejecutó su parte específica.
 *
 * Postcondiciones:
 * - Devuelve true si y solo si se cumplió la condición de éxito de la misión
 *   y se pudo pagar el costo de la acción final.
 * - Si el objetivo no se alcanzó, no se consume energía.
 * - Si la energía no alcanza para la acción final, la acción no se realiza, no
 *   se consume nada y la misión se cierra como fallida.
 * - Si se cobra, la energía baja exactamente en el costo declarado y queda
 *   acumulada para el informe.
 *
 * @return el resultado de la misión
 * @throws NaveException si falla alguna operación del dominio
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
 * Precondiciones:
 * - detalle no es nulo, vacío ni contiene solo espacios.
 *
 * Postcondiciones:
 * - La acción queda al final de la lista de acciones de la misión y las
 *   anteriores permanecen en el mismo orden.
 * - Queda registrado un evento de categoría MISION en la bitácora.
 * - Si se rechaza, ni la lista de acciones ni la bitácora se modifican.
 *
 * @param detalle descripción de la acción realizada; no puede ser nulo ni vacío
 * @throws IllegalArgumentException si el detalle es nulo o vacío
 */
protected final void registrarAccion(String detalle) {
    if (detalle == null || detalle.isBlank()) {
        throw new IllegalArgumentException("El detalle de la acción no puede ser nulo ni vacío");
    }
    acciones.add(detalle);
    asistente.registrarEvento("MISION", nombre + ": " + detalle);
}


/**
 * Cuarta y última etapa del ciclo: deja constancia del resultado, hace saltar
 * la nave si la misión salió bien y pide al asistente el informe con lo
 * acumulado durante el ciclo.
 *
 * Precondiciones:
 * - La evaluación del resultado ya se realizó.
 * - Si exito es true, el Motor Warp está Disponible.
 *
 * Postcondiciones:
 * - Queda registrado en la bitácora el cierre de la misión y su resultado.
 * - Si exito es true, el motor recorre el ciclo completo de salto y vuelve a
 *   quedar Disponible; si es false, el motor no se usa.
 * - Devuelve un informe que refleja los consumos de esta misión y el estado de
 *   la nave en este momento, y que no cambia después.
 *
 * @param exito resultado devuelto por la evaluación
 * @return el informe de la misión ejecutada
 * @throws TransicionInvalidaException si el motor no admite el ciclo de salto
 */
protected final InformeMision cerrar(boolean exito) throws TransicionInvalidaException{
    if (exito){
        saltar();
    }
    asistente.registrarEvento("MISION", nombre + " finalizada: " + (exito ? "EXITOSA" : "FALLIDA"));

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

/**
 * Hace saltar a la nave al completarse la misión con éxito, recorriendo el
 * ciclo del Motor Warp de punta a punta.
 *
 * Como todavía no se modela el paso del tiempo, el enfriamiento se completa
 * en el momento y la nave queda nuevamente Disponible.
 *
 * Precondiciones:
 * - El motor está en estado Disponible (lo verificó preparar()).
 *
 * Postcondiciones:
 * - El motor pasa por Preparando salto, En warp y Enfriamiento, y termina en Disponible.
 * - Las cuatro transiciones quedan registradas en la bitácora.
 *
 * @throws TransicionInvalidaException si alguna de las cuatro transiciones es rechazada
 */
private void saltar() throws TransicionInvalidaException {
    asistente.prepararSalto();
    asistente.iniciarSalto();
    asistente.finalizarSalto();
    asistente.completarEnfriamiento();
}
/**
 * Punto de extensión: energía que cuesta la acción final de esta misión.
 *
 * Postcondiciones:
 * - Devuelve un valor mayor o igual a 0. Devolver 0 significa que la misión
 *   no tiene costo de acción final.
 * - La implementación no modifica el estado de la nave.
 *
 * @return energía que cuesta la acción final (0 si no tiene costo)
 */
protected abstract int costoAccionFinal();
/**
 * Punto de extensión: describe el objetivo que persigue esta misión.
 *
 * Postcondiciones:
 * - Devuelve un texto no nulo ni vacío, que se incluye en el informe.
 * - La implementación no modifica el estado de la nave.
 *
 * @return descripción del objetivo de la misión
 */
protected abstract String descripcionObjetivo();
/**
 * Punto de extensión: lo que hace esta misión en particular.
 *
 * Precondiciones:
 * - El consumo de la operación ya se aplicó sobre la nave.
 *
 * Postcondiciones:
 * - La misión deja registradas sus acciones con registrarAccion.
 * - Al terminar, condicionDeExito() puede responder si el objetivo se cumplió.
 * - La implementación no consume recursos por su cuenta: eso lo hace el ciclo.
 *
 * @throws NaveException si falla alguna operación del dominio
 */
protected abstract void ejecutaMision() throws NaveException;

/**
 * Punto de extensión: indica si la misión cumplió su objetivo.
 *
 * Precondiciones:
 * - ejecutaMision() ya se ejecutó.
 *
 * Postcondiciones:
 * - Devuelve true si y solo si se cumplieron todas las condiciones que esta
 *   misión exige para considerarse exitosa.
 * - La consulta no modifica el estado de la misión ni de la nave.
 *
 * @return si el objetivo de la misión se cumplió
 */
protected abstract boolean condicionDeExito();
/**
 * Punto de extensión: combustible que consume la operación de esta misión.
 * Por defecto, 4, como indica la ficha de inicio de la Etapa 1.
 *
 * Postcondiciones:
 * - Devuelve un valor mayor o igual a 0.
 * - La implementación no modifica el estado de la nave.
 *
 * @return combustible que consume la operación
 */
protected int combustibleNecesario() {
    return 4;
}
/**
 * Punto de extensión: desgaste que genera la operación de esta misión.
 * Por defecto, 4, como indica la ficha de inicio de la Etapa 1.
 *
 * Postcondiciones:
 * - Devuelve un valor mayor o igual a 0.
 * - La implementación no modifica el estado de la nave.
 *
 * @return desgaste que genera la operación
 */
protected int desgasteQueGenera() {
    return 4;
}
}

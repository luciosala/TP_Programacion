package ar.edu.unmdp.tpg.mision;

import ar.edu.unmdp.tpg.asistente.AsistenteDeComando;

/**
 * M-03: completar el regreso simulado a una zona designada.
 *
 * Es exitosa cuando se confirma el arribo y la nave sigue operativa al llegar.
 * No tiene costo de accion final.
 */
public class MisionRetornoSeguro extends Mision {

    private final String zonaDestino;

    private boolean regresoCompletado;
    private boolean estadoOperativoValido;

    /**
     * Construye la mision de retorno seguro.
     *
     * Precondiciones:
     * - asistente no es nulo.
     * - zonaDestino no es nula, vacia ni contiene solo espacios.
     *
     * Postcondiciones:
     * - La mision queda lista para ejecutarse una vez, sobre la nave de ese asistente.
     * - La mision conserva la zona de destino para el informe.
     * - La nave no se modifica al construir la mision.
     *
     * @param asistente asistente que coordina la mision
     * @param zonaDestino zona a la que debe regresar la nave
     * @throws IllegalArgumentException si el asistente es nulo o si la zona de destino es nula o vacia
     */
    public MisionRetornoSeguro(AsistenteDeComando asistente, String zonaDestino) {

        super("M-03 - Retorno seguro", "Completar el regreso simulado a una zona designada", asistente);

        if (zonaDestino == null || zonaDestino.isBlank()) {
            throw new IllegalArgumentException("La zona de destino no puede ser nula ni vacía");
        }

        this.zonaDestino = zonaDestino;
    }

    @Override
    protected void ejecutaMision() {
        registrarAccion("Regreso simulado iniciado hacia " + zonaDestino);

        regresoCompletado = true;

        registrarAccion("Arribo confirmado en " + zonaDestino);

        estadoOperativoValido = asistente.naveEstaOperativa();

        registrarAccion("Estado operativo final verificado: " + estadoOperativoValido);
    }

    @Override
    protected boolean condicionDeExito() {
        return regresoCompletado && estadoOperativoValido;
    }

    @Override
    protected int costoAccionFinal() {
        return 0;
    }

    @Override
    protected String descripcionObjetivo() {
        return "Regresar de forma segura a " + zonaDestino;
    }
}

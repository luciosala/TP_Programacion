public class MisionRetornoSeguro extends Mision {

    private final String zonaDestino;

    private boolean regresoCompletado;
    private boolean estadoOperativoValido;

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

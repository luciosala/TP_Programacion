public class MisionRecoleccion extends Mision{

    private final String puntoInteres;
    private final String elemento;

    private boolean elementoObtenido;
    private boolean elementoRegistrado;

    public MisionRecoleccion(AsistenteDeComando asistente, String puntoInteres, String elemento) {

        super("M-02 - Recolección", "Recolectar datos o una muestra en un punto de interés", asistente);

        if (puntoInteres == null || puntoInteres.isBlank()) {
            throw new IllegalArgumentException("El punto de interés no puede ser nulo ni vacío");
        }

        if (elemento == null || elemento.isBlank()) {
            throw new IllegalArgumentException("El elemento no puede ser nulo ni vacío");
        }

        this.puntoInteres = puntoInteres;
        this.elemento = elemento;
    }

    @Override
    protected void ejecutaMision() {
        registrarAccion("Aproximación completada al punto " + puntoInteres);

        elementoObtenido = true;
        registrarAccion("Elemento obtenido: " + elemento);

        elementoRegistrado = true;
        registrarAccion("Elemento registrado como obtenido");
    }

    @Override
    protected boolean condicionDeExito() {
        return elementoObtenido && elementoRegistrado;
    }

    @Override
    protected int costoAccionFinal() {
        return 5;
    }

    @Override
    protected String descripcionObjetivo() {
        return "Recolectar " + elemento + " en " + puntoInteres;
    }
}

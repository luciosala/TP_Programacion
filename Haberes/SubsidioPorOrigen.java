public class SubsidioPorOrigen extends DecoratorHaber {

    private final Origen origen;

    public SubsidioPorOrigen(Haber haber, Origen origen) {
        super(haber);
        if (origen == null) {
            throw new IllegalArgumentException("El origen no puede ser nulo");
        }
        this.origen = origen;
    }

    @Override
    protected String nombreConcepto() {
        return "Subsidio por origen " + origen;
    }

    @Override
    protected double importeConcepto() {
        return origen.getSubsidioMensual();
    }
}

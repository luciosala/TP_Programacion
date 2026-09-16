import java.time.YearMonth;

public final class LiquidacionHaberes {

    private final YearMonth periodo;

    public LiquidacionHaberes(YearMonth periodo) {
        if (periodo == null) {
            throw new IllegalArgumentException("El período de liquidación no puede ser nulo");
        }
        this.periodo = periodo;
    }

    public YearMonth getPeriodo() {
        return periodo;
    }

    public Haber liquidar(Tripulante tripulante) {
        Haber haber = new HaberBase(tripulante);
        haber = new AdicionalAntiguedad(haber, tripulante);
        haber = new SubsidioPorOrigen(haber, tripulante.getOrigen());

        if (tripulante.getCargo() instanceof Consejero) {
            haber = new AdicionalConsejos(haber, tripulante, periodo);
        }
        return haber;
    }
}

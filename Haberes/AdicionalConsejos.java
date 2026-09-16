import java.time.YearMonth;

public class AdicionalConsejos extends DecoratorHaber {

    private final YearMonth periodo; //preguntar esto de YearMonth
    private final int cantidadConsejos;

    public AdicionalConsejos(Haber haber, Tripulante tripulante, YearMonth periodo) {
        super(haber);
        if (tripulante == null || !(tripulante.getCargo() instanceof Consejero)) {
            throw new IllegalArgumentException("El adicional de consejos requiere un tripulante consejero");
        }
        if (periodo == null) {
            throw new IllegalArgumentException("El período de liquidación no puede ser nulo");
        }

        this.periodo = periodo;
        this.cantidadConsejos = tripulante.cantidadConsejos(periodo);
    }

    @Override
    protected String nombreConcepto() {
        return "Consejos de " + periodo + " (" + cantidadConsejos + ")";
    }

    @Override
    protected double importeConcepto() {
        return cantidadConsejos * 2.0;
    }
}

public class AdicionalAntiguedad extends DecoratorHaber {

    private final int antiguedadAnios;
    private final double adicional;

    public AdicionalAntiguedad(Haber haber, Tripulante tripulante) {
        super(haber);
        if (tripulante == null) {
            throw new IllegalArgumentException("El tripulante no puede ser nulo");
        }

        double base = tripulante.getCargo().getRemuneracionBase();
        double porcentaje = tripulante.getCargo().getPorcentajeAntiguedad();
        if (!Double.isFinite(base) || base < 0 || !Double.isFinite(porcentaje) || porcentaje < 0) {
            throw new IllegalArgumentException("La base y el porcentaje deben ser valores válidos no negativos");
        }

        this.antiguedadAnios = tripulante.getAntiguedadAnios();
        double importe = base * porcentaje * antiguedadAnios;
        if (!Double.isFinite(importe) || importe < 0) {
            throw new IllegalArgumentException("El adicional de antigüedad debe ser un importe válido no negativo");
        }
        this.adicional = importe;
    }

    @Override
    protected String nombreConcepto() {
        return "Antigüedad (" + antiguedadAnios + " años)";
    }

    @Override
    protected double importeConcepto() {
        return adicional;
    }
}

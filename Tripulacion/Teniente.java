public class Teniente extends Cargo {

    @Override
    public String getNombre() {
        return "Teniente";
    }

    @Override
    public double getRemuneracionBase() {
        return 400;
    }

    @Override
    public double getPorcentajeAntiguedad() {
        return 0.03;
    }
}

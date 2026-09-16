public class Consejero extends Cargo {

    @Override
    public String getNombre() {
        return "Consejero";
    }

    @Override
    public double getRemuneracionBase() {
        return 600;
    }

    @Override
    public double getPorcentajeAntiguedad() {
        return 0.05;
    }
}

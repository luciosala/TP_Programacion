public class Capitan extends Cargo {

    @Override
    public String getNombre() {
        return "Capitán";
    }

    @Override
    public double getRemuneracionBase() {
        return 1000;
    }

    @Override
    public double getPorcentajeAntiguedad() {
        return 0.20;
    }
}

public class Alferez extends Cargo {

    @Override
    public String getNombre() {
        return "Alférez";
    }

    @Override
    public double getRemuneracionBase() {
        return 200;
    }

    @Override
    public double getPorcentajeAntiguedad() {
        return 0.005;
    }
}

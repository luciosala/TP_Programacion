public enum Origen {
    TERRICOLA(20),
    VULCANO(30),
    MARCIANO(18);

    private final int subsidioMensual;

    Origen(int subsidioMensual) {
        this.subsidioMensual = subsidioMensual;
    }

    public int getSubsidioMensual() {
        return subsidioMensual;
    }
}

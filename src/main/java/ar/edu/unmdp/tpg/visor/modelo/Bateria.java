package ar.edu.unmdp.tpg.visor.modelo;

/**
 * Bateria producida por una generadora del universo paralelo.
 * Es inmutable: una vez producida no cambia, asi que se puede pasar entre hilos sin riesgo.
 */
public final class Bateria {

    private final int numero;
    private final String producidaPor;

    /**
     * @param numero numero de serie de la bateria
     * @param producidaPor nombre de la generadora que la produjo
     */
    public Bateria(int numero, String producidaPor) {
        this.numero = numero;
        this.producidaPor = producidaPor;
    }

    public int getNumero() {
        return numero;
    }

    public String getProducidaPor() {
        return producidaPor;
    }

    @Override
    public String toString() {
        return "Bateria #" + numero + " (" + producidaPor + ")";
    }
}

package ar.edu.unmdp.tpg.visor.modelo;

/**
 * Productor: cada generadora es un hilo (extends Thread, como el Productor de la teoria)
 * y repite el ciclo de la consigna una cantidad fija de veces:
 * 1) produce una bateria, 2) pide la plataforma, 3) si esta llena espera,
 * 4) deposita, 5) notifica (los pasos 2 a 5 los resuelve PlataformaDeCarga.depositar).
 *
 * Cuando termina sus CANTIDAD_BATERIAS vueltas, run() termina y el hilo muere.
 * Si se cierra el visor antes, se la interrumpe y tambien termina.
 */
public class GeneradoraDeEnergia extends Thread {

    /** Cuantas baterias produce cada generadora antes de terminar. */
    public static final int CANTIDAD_BATERIAS = 10;

    /** Tiempo que tarda en producir cada bateria, en milisegundos. */
    public static final int TIEMPO_PRODUCCION = 1000;

    private static int ultimaBateria = 0;

    private final PlataformaDeCarga plataforma;
    private final CanalDeEventos canal;

    /**
     * @param nombre nombre del hilo, se usa en los eventos (por ejemplo, "Generadora 1")
     * @param plataforma plataforma compartida donde deposita
     * @param canal canal donde publica sus eventos
     */
    public GeneradoraDeEnergia(String nombre, PlataformaDeCarga plataforma, CanalDeEventos canal) {
        super(nombre);
        this.plataforma = plataforma;
        this.canal = canal;
    }

    /** Numera las baterias de todas las generadoras sin repetir (lo usan varios hilos a la vez). */
    private static synchronized int siguienteNumero() {
        return ++ultimaBateria;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < CANTIDAD_BATERIAS; i++) {
                sleep(TIEMPO_PRODUCCION);                       // produce la bateria
                Bateria bateria = new Bateria(siguienteNumero(), getName());
                canal.publicar("Batería producida por " + getName() + ".");
                plataforma.depositar(bateria, getName());       // espera si esta llena
            }
            canal.publicar(getName() + " terminó: produjo sus " + CANTIDAD_BATERIAS + " baterías.");
        } catch (InterruptedException e) {
            // La interrumpieron (se cerro el visor): run() termina y el hilo muere.
        }
    }
}

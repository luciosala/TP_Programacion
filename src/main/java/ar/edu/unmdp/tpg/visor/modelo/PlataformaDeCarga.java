package ar.edu.unmdp.tpg.visor.modelo;

import java.util.ArrayList;

/**
 * Recurso compartido del productor-consumidor: la unica plataforma de carga del
 * universo paralelo. Es un MONITOR:
 * - sus metodos son synchronized: en un instante solo una entidad la usa
 *   (una generadora depositando o una nave retirando);
 * - las condiciones de guarda (llena / vacia) se evaluan en un while, para volver
 *   a comprobarlas despues de despertar;
 * - quien no puede continuar hace wait() (se duerme soltando el cerrojo) y quien
 *   cambia el estado hace notifyAll() (despierta a todos los que esperan).
 *
 * Invariante: 0 &lt;= cantidad de baterias &lt;= capacidad.
 */
public class PlataformaDeCarga {

    private final int capacidad;
    private final ArrayList<Bateria> baterias = new ArrayList<>();
    private final CanalDeEventos canal;
    private int maximoAlcanzado = 0;

    /**
     * Precondiciones:
     * - capacidad &gt;= 1.
     * - canal no es nulo.
     *
     * @param capacidad cantidad maxima de baterias que entran en la plataforma
     * @param canal canal donde se publican los eventos de la plataforma
     * @throws IllegalArgumentException si la capacidad es menor que 1 o el canal es nulo
     */
    public PlataformaDeCarga(int capacidad, CanalDeEventos canal) {
        if (capacidad < 1) {
            throw new IllegalArgumentException("La capacidad de la plataforma debe ser al menos 1");
        }
        if (canal == null) {
            throw new IllegalArgumentException("El canal de eventos no puede ser nulo");
        }
        this.capacidad = capacidad;
        this.canal = canal;
    }

    /**
     * Deposita una bateria. Si la plataforma esta llena, el hilo que llama espera
     * (wait) hasta que una nave haga lugar.
     *
     * Precondiciones:
     * - bateria no es nula.
     *
     * Postcondiciones:
     * - La bateria quedo en la plataforma y se aviso a los hilos en espera (notifyAll).
     * - Se respeta el invariante: nunca hay mas baterias que la capacidad.
     *
     * @param bateria bateria a depositar
     * @param generadora nombre de quien deposita (para los eventos)
     * @throws InterruptedException si el hilo es interrumpido mientras espera
     */
    public synchronized void depositar(Bateria bateria, String generadora) throws InterruptedException {
        if (bateria == null) {
            throw new IllegalArgumentException("La bateria no puede ser nula");
        }
        while (baterias.size() == capacidad) {
            canal.publicar(generadora + " esperando a que se libere espacio en la Plataforma.");
            wait();
        }
        baterias.add(bateria);                  // la deja al final
        maximoAlcanzado = Math.max(maximoAlcanzado, baterias.size());
        notifyAll();
    }

    /**
     * Retira la bateria mas antigua. Si no hay baterias, el hilo que llama espera
     * (wait) hasta que una generadora deposite.
     *
     * Postcondiciones:
     * - Devuelve una bateria que ya no esta en la plataforma, y se aviso a los
     *   hilos en espera (notifyAll).
     *
     * @param nave nombre de quien retira (para los eventos)
     * @return la bateria retirada
     * @throws InterruptedException si el hilo es interrumpido mientras espera
     */
    public synchronized Bateria retirar(String nave) throws InterruptedException {
        while (baterias.isEmpty()) {
            canal.publicar(nave + " esperando por falta de baterías en la Plataforma.");
            wait();
        }
        Bateria bateria = baterias.remove(0);   // saca la mas antigua
        notifyAll();
        return bateria;
    }

    /** @return cuantas baterias hay ahora en la plataforma */
    public synchronized int getCantidad() {
        return baterias.size();
    }

    /** @return capacidad maxima de la plataforma */
    public int getCapacidad() {
        return capacidad;
    }

    /** @return la mayor cantidad de baterias que hubo a la vez (sirve para verificar el invariante) */
    public synchronized int getMaximoAlcanzado() {
        return maximoAlcanzado;
    }
}

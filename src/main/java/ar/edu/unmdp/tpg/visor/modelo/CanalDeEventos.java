package ar.edu.unmdp.tpg.visor.modelo;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Sujeto del patron Observer: guarda los observadores y les avisa cada evento.
 *
 * Lo comparten la plataforma, las generadoras, las naves y el universo paralelo:
 * todos publican aca, y los observadores (la vista) no conocen a ninguno de ellos.
 *
 * La lista es CopyOnWriteArrayList porque varios hilos publican a la vez mientras
 * el hilo de Swing puede estar agregando o quitando observadores: con un ArrayList
 * comun, recorrerla mientras otro hilo la modifica lanza ConcurrentModificationException.
 */
public class CanalDeEventos {

    private final List<ObservadorVisor> observadores = new CopyOnWriteArrayList<>();

    /**
     * Suscribe un observador.
     *
     * Precondiciones:
     * - observador no es nulo.
     *
     * Postcondiciones:
     * - El observador recibe todos los eventos publicados a partir de ahora.
     *
     * @param observador quien quiere recibir los eventos
     * @throws IllegalArgumentException si el observador es nulo
     */
    public void agregarObservador(ObservadorVisor observador) {
        if (observador == null) {
            throw new IllegalArgumentException("El observador no puede ser nulo");
        }
        observadores.add(observador);
    }

    /**
     * Desuscribe un observador. Si no estaba suscripto, no pasa nada.
     *
     * @param observador observador a quitar
     */
    public void quitarObservador(ObservadorVisor observador) {
        observadores.remove(observador);
    }

    /**
     * Avisa un evento a todos los observadores suscriptos.
     *
     * Postcondiciones:
     * - Cada observador suscripto recibio el evento una vez, en el hilo que lo publica.
     *
     * @param evento descripcion del evento
     */
    public void publicar(String evento) {
        for (ObservadorVisor o : observadores) {
            o.actualizar(evento);
        }
    }
}

package ar.edu.unmdp.tpg.visor.modelo;

import ar.edu.unmdp.tpg.destino.Observable;

/**
 * Sujeto del patrón Observer (Clase 11): avisa cada evento del universo paralelo
 * a sus observadores (la vista del visor). Reutiliza la clase Observable que
 * provee la cátedra junto con Destino y Tablero, igual que la bitácora y el
 * modelo de la aplicación.
 *
 * Lo comparten la plataforma, las generadoras, las naves y el universo paralelo:
 * todos publican acá, y los observadores no conocen a ninguno de ellos.
 *
 * Concurrencia: varios hilos publican a la vez, por eso publicar es synchronized
 * (los avisos salen de a uno). Los observadores se registran al armar la
 * aplicación, antes de que exista ningún hilo, así la lista no cambia mientras
 * se recorre.
 */
public class CanalDeEventos extends Observable {

    /**
     * Avisa un evento a todos los observadores suscriptos.
     *
     * Postcondiciones:
     * - Cada observador suscripto recibió el evento una vez, en el hilo que lo publica.
     *
     * @param evento descripción del evento
     */
    public synchronized void publicar(String evento) {
        notificarObservadores(evento);
    }
}

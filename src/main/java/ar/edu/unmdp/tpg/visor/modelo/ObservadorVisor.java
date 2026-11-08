package ar.edu.unmdp.tpg.visor.modelo;

/**
 * Observador del patron Observer: lo que tiene que implementar quien quiera
 * enterarse de los eventos del universo paralelo (por ejemplo, la consola del visor).
 *
 * Es push: el evento llega ya armado como texto, asi el observador no necesita
 * consultar a la plataforma (que mientras tanto puede estar cambiando por otro hilo).
 */
public interface ObservadorVisor {

    /**
     * Recibe un evento ocurrido en el universo paralelo.
     *
     * Importante: lo invoca el hilo que produjo el evento (una generadora o una nave),
     * no el hilo de Swing. Si el observador toca componentes graficos, debe pasar ese
     * trabajo al hilo de Swing (SwingUtilities.invokeLater).
     *
     * @param evento descripcion del evento, lista para mostrar
     */
    void actualizar(String evento);
}

package ar.edu.unmdp.tpg.aplicacion;

import ar.edu.unmdp.tpg.asistente.AsistenteDeComando;
import ar.edu.unmdp.tpg.excepciones.NaveException;
import ar.edu.unmdp.tpg.mision.InformeMision;
import ar.edu.unmdp.tpg.mision.Mision;

/**
 * Tarea que ejecuta una misión en su propio hilo (Clase 10: la clase implementa
 * Runnable y el modelo la arranca con new Thread(tarea, nombre).start()).
 *
 * La misión hace un Thread.sleep en cada turno para que el usuario vea el avance:
 * si se ejecutara en el hilo de la interfaz, la ventana quedaría congelada hasta
 * que termine. Al terminar, le avisa al modelo de la aplicación el resultado.
 */
class EjecucionDeMision implements Runnable {

    private final ModeloAplicacion modelo;
    private final AsistenteDeComando asistente;
    private final Mision mision;

    EjecucionDeMision(ModeloAplicacion modelo, AsistenteDeComando asistente, Mision mision) {
        this.modelo = modelo;
        this.asistente = asistente;
        this.mision = mision;
    }

    @Override
    public void run() {
        try {
            InformeMision informe = asistente.ejecutarMision(mision);
            modelo.misionTerminada(informe);
        } catch (NaveException rechazo) {
            // El asistente ya lo registró en la bitácora
            modelo.misionRechazada(rechazo.getMessage());
        }
    }
}

package ar.edu.unmdp.tpg.aplicacion;

import ar.edu.unmdp.tpg.asistente.AsistenteDeComando;
import ar.edu.unmdp.tpg.excepciones.NaveException;
import ar.edu.unmdp.tpg.mision.InformeMision;
import ar.edu.unmdp.tpg.mision.Mision;

/**
 * Hilo en el que se ejecuta una misión (Clase 10: se crea una clase que extiende
 * de Thread y se sobreescribe run).
 *
 * La misión hace un Thread.sleep en cada turno para que el usuario vea el avance:
 * si se ejecutara en el hilo de la interfaz, la ventana quedaría congelada hasta
 * que termine. Al terminar, le avisa al modelo de la aplicación el resultado.
 */
class EjecucionDeMision extends Thread {

    private final ModeloAplicacion modelo;
    private final AsistenteDeComando asistente;
    private final Mision mision;

    EjecucionDeMision(ModeloAplicacion modelo, AsistenteDeComando asistente, Mision mision) {
        super("Mision " + mision.getNombre());
        this.modelo = modelo;
        this.asistente = asistente;
        this.mision = mision;
        setDaemon(true);   // si se cierra la aplicación, el hilo no la mantiene abierta
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

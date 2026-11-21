package ar.edu.unmdp.tpg.gui.controlador;

import ar.edu.unmdp.tpg.gui.vista.VentanaPrincipal;
import ar.edu.unmdp.tpg.visor.MainVisor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controlador de la ventana principal: atiende el boton que abre el visor del
 * universo paralelo. El visor es un modulo aparte con su propio MVC (ver MainVisor).
 */
public class ControladorPrincipal implements ActionListener {

    public ControladorPrincipal(VentanaPrincipal ventana) {
        ventana.setVisorListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (VentanaPrincipal.ABRIR_VISOR.equals(e.getActionCommand())) {
            MainVisor.abrir(MainVisor.CAPACIDAD_PLATAFORMA);
        }
    }
}

package ar.edu.unmdp.tpg.motorwarp;

import ar.edu.unmdp.tpg.excepciones.TransicionInvalidaException;

/**
 * Estado inicial del Motor Warp: la nave puede iniciar una mision.
 *
 * Unica transicion valida: prepararSalto(), que lleva a Preparando salto.
 * Es el unico estado en que estaDisponible() devuelve true.
 */
public class Disponible extends Estado {

    @Override
    public String nombre() {
        return "Disponible";
    }

    @Override
    public Estado prepararSalto() {
        return new PrepararSalto();
    }

    @Override
    public Estado iniciarSalto() throws TransicionInvalidaException {
        throw new TransicionInvalidaException(
            "No se puede iniciar el salto desde Disponible"
        );
    }
    @Override
    public Estado completarEnfriamiento() throws TransicionInvalidaException {
        throw new TransicionInvalidaException(
            "No se puede enfriar desde Disponible"
        );
    }
    @Override
    public Estado finalizarSalto() throws TransicionInvalidaException {
        throw new TransicionInvalidaException(
            "No se puede finalizar salto desde Disponible"
        );
    }
    
    @Override
    boolean estaDisponible() {
        return true;
    }
    
}

package ar.edu.unmdp.tpg.motorwarp;

import ar.edu.unmdp.tpg.excepciones.TransicionInvalidaException;

/**
 * La nave esta viajando en warp.
 *
 * Unica transicion valida: finalizarSalto(), que lleva a Enfriamiento.
 */
public class EnWarp extends Estado {

    @Override
    public String nombre() {
        return "En warp";
    }

    @Override
    public Estado prepararSalto() throws TransicionInvalidaException {
         throw new TransicionInvalidaException(
            "No se puede preparar salto si esta en salto"
         );
    }

    @Override
    public Estado iniciarSalto() throws TransicionInvalidaException {
        throw new TransicionInvalidaException(
            "No se puede iniciar el salto desde Warp"
        );
    }
    @Override
    public Estado completarEnfriamiento() throws TransicionInvalidaException {
        throw new TransicionInvalidaException(
            "No se puede enfriar desde el salto"
        );
    }
    @Override
    public Estado finalizarSalto() {
        return new Enfriando();
    }
    
}

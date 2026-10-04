package ar.edu.unmdp.tpg.motorwarp;

import ar.edu.unmdp.tpg.excepciones.TransicionInvalidaException;

public abstract class Estado {

    abstract Estado prepararSalto() throws TransicionInvalidaException;

    abstract Estado iniciarSalto() throws TransicionInvalidaException;

    abstract Estado finalizarSalto() throws TransicionInvalidaException;

    abstract Estado completarEnfriamiento() throws TransicionInvalidaException;

    abstract String nombre();
    
    boolean estaDisponible() {
        return false;
    }
}

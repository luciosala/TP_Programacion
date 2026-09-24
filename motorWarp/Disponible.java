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
            "No se puede enfriar desde disponible"
        );
    }
    @Override
    public Estado finalizarSalto() throws TransicionInvalidaException {
        throw new TransicionInvalidaException(
            "No se puede finalizar salto desde disponible"
        );
    }
    
}

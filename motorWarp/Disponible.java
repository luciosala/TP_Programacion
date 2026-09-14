public class Disponible extends Estado {

    @Override
    public Estado prepararSalto() {
        return new Preparando();
    }

    @Override
    public Estado iniciarSalto() {
        throw new IllegalStateException(
            "No se puede iniciar el salto desde Disponible"
        );
    }
    @Override
    public Estado completarEnfriamiento(){
        throw new IllegalStateException(
            "No se puede enfriar desde disponible"
        );
    }
    @Override
    public Estado finalizarSalto(){
        throw new IllegalStateException(
            "No se puede finalizar salto desde disponible"
        );
    }
    
}
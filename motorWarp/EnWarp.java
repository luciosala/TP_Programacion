public class EnWarp extends Estado {

    @Override
    public Estado prepararSalto() {
         throw new IllegalStateException(
            "No se puede preparar salto si esta en salto"
    }

    @Override
    public Estado iniciarSalto() {
        throw new IllegalStateException(
            "No se puede iniciar el salto desde Warp"
        );
    }
    @Override
    public Estado completarEnfriamiento(){
        throw new IllegalStateException(
            "No se puede enfriar desde el salto"
        );
    }
    @Override
    public Estado finalizarSalto(){
        return new Enfriando();
    }
    
}
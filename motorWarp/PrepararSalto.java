public class PrepararSalto extends Estado{

    @Override
    public String nombre() {
        return "Preparando salto";
    }

    @Override
    public Estado prepararSalto() {
         throw new IllegalStateException(
            "Ya esta preparando salto"
         );
    }

    @Override
    public Estado iniciarSalto() {
        return new EnWarp();
    }
    @Override
    public Estado completarEnfriamiento(){
        throw new IllegalStateException(
            "No se puede completar enfriamiento si se esta preparando"
        );
    }
    @Override
    public Estado finalizarSalto(){
         throw new IllegalStateException(
            "No se puede finalizar el salto mientras se esta preparando"
        );
    }
}

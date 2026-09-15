public class PrepararSalto extends Estado{

    @Override
    public Estado prepararSalto() {
         throw new IllegalStateException(
            "Ya esta preparando salto"
    }

    @Override
    public Estado iniciarSalto() {
        throw new IllegalStateException(
            "No se puede iniciar el salto mientras esta preparando"
        );
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
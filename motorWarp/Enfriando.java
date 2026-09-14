public class Enfriando extends Estado{

    @Override
    public Estado prepararSalto() {
         throw new IllegalStateException(
            "No se puede preparar salto si esta enfriando"
    }

    @Override
    public Estado iniciarSalto() {
        throw new IllegalStateException(
            "No se puede iniciar el salto mientras esta enfriando"
        );
    }
    @Override
    public Estado completarEnfriamiento(){
        return new Disponible()
    }
    @Override
    public Estado finalizarSalto(){
         throw new IllegalStateException(
            "No se puede finalizar el salto mientras se enfria"
        );
    }
    







}
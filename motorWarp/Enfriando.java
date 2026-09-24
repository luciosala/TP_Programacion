public class Enfriando extends Estado{

    @Override
    public String nombre() {
        return "Enfriamiento";
    }

    @Override
    public Estado prepararSalto() throws TransicionInvalidaException {
         throw new TransicionInvalidaException(
            "No se puede preparar salto si esta enfriando"
         );
    }

    @Override
    public Estado iniciarSalto() throws TransicionInvalidaException {
        throw new TransicionInvalidaException(
            "No se puede iniciar el salto mientras esta enfriando"
        );
    }
    @Override
    public Estado completarEnfriamiento(){
        return new Disponible();
    }
    @Override
    public Estado finalizarSalto() throws TransicionInvalidaException {
         throw new TransicionInvalidaException(
            "No se puede finalizar el salto mientras se enfria"
        );
    }
    







}

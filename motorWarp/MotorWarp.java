public class MotorWarp{
    private Estado estadoActual;

    public MotorWarp(){
        estadoActual=new Disponible();
    }
    public void prepararSalto() throws TransicionInvalidaException {estadoActual = estadoActual.prepararSalto(); }
    public void iniciarSalto() throws TransicionInvalidaException { estadoActual = estadoActual.iniciarSalto(); }
    public void finalizarSalto() throws TransicionInvalidaException { estadoActual = estadoActual.finalizarSalto(); }
    public void completarEnfriamiento() throws TransicionInvalidaException { estadoActual = estadoActual.completarEnfriamiento(); }

    public String getEstadoActual() { return estadoActual.nombre(); }
    
}

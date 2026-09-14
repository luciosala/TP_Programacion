public class MotorWarp{
    private Estado estadoActual;

    public MotorWarp{
        estadoActual=new Disponible();
    }
    public void prepararSalto() {estadoActual = estadoActual.prepararSalto(); }
    public void iniciarSalto() { estadoActual = estadoActual.iniciarSalto(); }
    public void finalizarSalto() { estadoActual = estadoActual.finalizarSalto(); }
    public void completarEnfriamiento() { estadoActual = estadoActual.completarEnfriamiento(); }

    public String getEstadoActual() { return estadoActual.nombre(); }
    }
}
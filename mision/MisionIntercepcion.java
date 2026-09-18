public class MisionIntercepcion extends Mision {

   @Override
    protected void ejecutaMision() {
        registrarAccion("Aproximación completada hasta " + objetivo);
        registrarAccion("Estado del objetivo verificado: " + estadoObjetivo);
        contactoEstablecido = true;
    }


    
}
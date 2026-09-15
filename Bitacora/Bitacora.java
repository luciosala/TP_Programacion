import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
es para registrar eventos,errores, cambios del Motor , y cosas de
misiones y  recursos.
**/
public class Bitacora {

    private final List<EventoBitacora> eventos = new ArrayList<>();

    public void registrar(String categoria, String descripcion) {
        eventos.add(new EventoBitacora(categoria, descripcion));
    }

   /*de mas viejo al mas nuevo */
    public List<EventoBitacora> consultarEventos() {
        return Collections.unmodifiableList(eventos);
    }
}

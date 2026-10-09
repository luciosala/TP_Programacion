package ar.edu.unmdp.tpg.bitacora;

import java.time.Instant;

/**
 * Evento registrado en la bitacora: cuando ocurrio, de que tipo fue y que paso.
 *
 * Es inmutable: una vez registrado, un evento no se puede modificar ni borrar.
 */
public final class EventoBitacora{


    /**
     *  
     * categoria: tipo de evento. motor, error , mision existosa o fallida
     * 
     */
private final String categoria, descripcion;
private final Instant timestamp;
      
   
/**
 * Construye un evento de bitacora con la fecha y hora de su creacion.
 *
 * Precondiciones:
 * - categoria no es nula, vacia ni contiene solo espacios.
 * - descripcion no es nula, vacia ni contiene solo espacios.
 *
 * Postcondiciones:
 * - El evento conserva la categoria y la descripcion recibidas.
 * - El evento queda sellado con el instante en que se creo.
 * - El evento es inmutable: sus tres datos no cambian despues de construido.
 *
 * @param categoria tipo de evento (MISION, MOTOR, RECURSOS, ERROR, COMANDO, HABERES)
 * @param descripcion detalle de lo ocurrido
 * @throws IllegalArgumentException si la categoria o la descripcion son nulas o vacias
 */
public EventoBitacora(String categoria,String descripcion){
    if (categoria == null || categoria.isBlank()) {
        throw new IllegalArgumentException("La categoría del evento no puede ser nula ni vacía");
    }
    if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La Bitacora no acepta eventos nulos o vacios");
        }
    
    this.timestamp = Instant.now();
    this.categoria = categoria;
    this.descripcion = descripcion;

}
public Instant getTimestamp() { return timestamp; }
public String getCategoria() { return categoria; }
public String getDescripcion() { return descripcion; }

@Override //siempre se sobreescribe toString
public String toString() {
    return "[" + timestamp + "] (" + categoria + ") " + descripcion;
}   


}
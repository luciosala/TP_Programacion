import java.time.Instant;
public class EventoBitacora(){


    /**
     *  
     * categoria: tipo de evento. motor, error , mision existosa o fallida
     * 
     */
   private String categoria, descripcion;
      
   
public EventoBitacora(String categoria,String descripcion){
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
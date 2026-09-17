import java.util.ArrayList;
import java.util.List;
abstract public class Mision{
    protected String nombre;
    protected String descripcion;
    int energiaConsumida =0,combustibleConsumido=0,desgasteAcumulado=0 ; //acumuladores de energia y combustible
    protected final Nave nave;
    protected final Bitacora bitacora;

    /**
     * Construye una misión 
     *
     * @param nombre nombre identificatorio de la misión; no puede ser nulo
     * @param descripcion descripción de la misión; no puede ser nula
     * @param nave nave asignada a la misión; no puede ser nula
     * @param bitacora bitácora donde se registran los eventos de la misión; no puede ser nula
     * @throws IllegalArgumentException si {@code nombre}, {@code descripcion}, {@code nave} o {@code bitacora} son nulos
     */
    protected Mision(String nombre, String descripcion, Nave nave, Bitacora bitacora) {
        if (nombre == null)
            throw new IllegalArgumentException("El nombre de la misión no puede ser nulo");
        
        if (descripcion == null)
            throw new IllegalArgumentException("La descripción de la misión no puede ser nula ");
        
        if (nave == null) 
            throw new IllegalArgumentException("La misión requiere una nave");
        
        if (bitacora == null) 
            throw new IllegalArgumentException("La misión requiere una bitácora");


        this.nombre = nombre;
        this.descripcion = descripcion;
        this.nave = nave;
        this.bitacora = bitacora;
    }
    
    //Ejecuta el ciclo común sin permitir que las subclases alteren su orden.
     //PATRON Template METHOD
    public final InformeMision realizarMision() {
        preparar();

        ejecutar();
        boolean exito = evaluarResultado();
        return cerrar(exito);
    }

    /*metodos protected para que los hijos puedan accedrr */
  
    protected void preparar() {
        if (nave.getRecursos().getCombustible() < 4) {
            bitacora.registrar("MISION", "Rechazada: combustible insuficiente para " + nombre);
            throw new IllegalStateException("Recursos insuficientes para iniciar " + nombre);
        }
        else if (nave.getRecursos().getDesgaste()+4 > 100) {
                bitacora.registrar("MISION", "Rechazada: por Demasiado desgaste " + nombre);
                throw new IllegalStateException("Demasiado Desgaste " + nombre);
        }
    }           
   protected final void ejecutar(){

        nave.getRecursos().consumirCombustible(4);
        combustibleConsumido+=4;
        nave.getRecursos().agregarDesgaste(4);
        desgasteAcumulado+=4;
        ejecutaMision();
   }
   /**
 * Determina si la misión cumplió su objetivo y cobra el costo de la acción
 * final. Si la energía no alcanza, la acción no se realiza y no se consume
 * nada: la misión se cierra como fallida.
 
 */
protected boolean evaluarResultado() {
    if (!condicionDeExito()) {
        registrarAccion("Objetivo no alcanzado");
        return false;
    }

    int costo = costoAccionFinal();

    if (costo > 0) {
        if (nave.getRecursos().getEnergia() < costo) {
            registrarAccion("Energía insuficiente para completar la acción final");
            return false;
        }
        nave.getRecursos().consumirEnergia(costo);
        energiaConsumida += costo;
    }

    registrarAccion("Acción final completada");
    return true;
}
private final List<String> acciones = new ArrayList<>();

/**
 * Deja constancia de una acción tanto en el informe como en la Bitácora.
 *
 * @param detalle descripción de la acción realizada; no puede ser nulo ni vacío
 */
protected final void registrarAccion(String detalle) {
    if (detalle == null || detalle.isBlank()) {
        throw new IllegalArgumentException("El detalle de la acción no puede ser nulo ni vacío");
    }
    acciones.add(detalle);
    bitacora.registrar("MISION", nombre + ": " + detalle);
}
/** Energía que cuesta la acción final de esta misión (0 si no tiene costo). */
protected abstract int costoAccionFinal();
   
protected abstract InformeMision cerrar(boolean exito);
protected abstract void ejecutaMision();    
protected abstract boolean condicionDeExito();
}

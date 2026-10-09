package ar.edu.unmdp.tpg.mision;

import ar.edu.unmdp.tpg.naves.Nave;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Resultado de una mision ya cerrada: que se hizo, como termino, cuanto costo y
 * en que estado quedo la nave.
 *
 * Es inmutable: se construye al cerrar la mision y no cambia despues, por lo que
 * sigue reflejando el cierre aunque la nave siga operando.
 */
public final class InformeMision {

    private final String mision;
    private final String descripcion;
    private final String objetivo;
    private final List<String> acciones;

    private final int combustibleConsumido;
    private final int energiaConsumida;
    private final int desgasteGenerado;

    private final boolean exito;

    // Foto del estado de la nave al cerrar la misión
    private final String idNave;
    private final int combustibleFinal;
    private final int energiaFinal;
    private final int desgasteFinal;
    private final boolean requiereMantenimiento;

    /**
     * Construye el informe de una mision cerrada.
     *
     * Precondiciones:
     * - nave no es nula.
     * - acciones no es nula.
     * - Las tres cantidades consumidas son mayores o iguales a 0.
     *
     * Postcondiciones:
     * - El informe conserva el nombre, la descripcion, el objetivo y los consumos recibidos.
     * - El informe guarda una foto del estado de la nave en este momento: id, combustible,
     *   energia, desgaste y si requiere mantenimiento.
     * - El informe es inmutable y su lista de acciones no se puede modificar.
     * - La nave no se modifica.
     *
     * @param mision nombre de la mision ejecutada
     * @param descripcion descripcion de la mision
     * @param objetivo objetivo evaluado
     * @param acciones acciones realizadas durante la mision, en orden
     * @param combustibleConsumido combustible consumido por la mision
     * @param energiaConsumida energia consumida por la mision
     * @param desgasteGenerado desgaste generado por la mision
     * @param exito resultado de la evaluacion
     * @param nave nave de la que se toma el estado final
     * @throws IllegalArgumentException si la nave es nula
     */
    public InformeMision(String mision, String descripcion, String objetivo,
                         List<String> acciones,
                         int combustibleConsumido, int energiaConsumida, int desgasteGenerado,
                         boolean exito, Nave nave) {

        if (nave == null) {
            throw new IllegalArgumentException("El informe requiere la nave de la misión");
        }

        this.mision = mision;
        this.descripcion = descripcion;
        this.objetivo = objetivo;
        this.acciones = Collections.unmodifiableList(new ArrayList<>(acciones));

        this.combustibleConsumido = combustibleConsumido;
        this.energiaConsumida = energiaConsumida;
        this.desgasteGenerado = desgasteGenerado;
        this.exito = exito;

        this.idNave = nave.getId();
        this.combustibleFinal = nave.getCombustible();
        this.energiaFinal = nave.getEnergia();
        this.desgasteFinal = nave.getDesgaste();
        this.requiereMantenimiento = nave.requiereMantenimiento();
    }

    public String getMision() { return mision; }
    public String getDescripcion() { return descripcion; }
    public String getObjetivo() { return objetivo; }

    /** Acciones realizadas durante la misión, en orden. La lista no se puede modificar. */
    public List<String> getAcciones() { return acciones; }

    public int getCombustibleConsumido() { return combustibleConsumido; }
    public int getEnergiaConsumida() { return energiaConsumida; }
    public int getDesgasteGenerado() { return desgasteGenerado; }

    /**
     * Informa como termino la mision.
     *
     * Postcondiciones:
     * - El informe no se modifica.
     * - Devuelve true si y solo si la mision cumplio su objetivo y pudo completar su accion final.
     *
     * @return si la mision fue exitosa
     */
    public boolean fueExitosa() { return exito; }

    public String getIdNave() { return idNave; }
    public int getCombustibleFinal() { return combustibleFinal; }
    public int getEnergiaFinal() { return energiaFinal; }
    public int getDesgasteFinal() { return desgasteFinal; }
    public boolean requiereMantenimiento() { return requiereMantenimiento; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("INFORME DE MISION\n");
        sb.append("Mision: ").append(mision).append("\n");
        sb.append("Descripcion: ").append(descripcion).append("\n");
        sb.append("Objetivo: ").append(objetivo).append("\n");
        sb.append("Resultado: ").append(exito ? "EXITOSA" : "FALLIDA").append("\n");

        sb.append("Acciones realizadas:\n");
        for (String accion : acciones) {
            sb.append("  - ").append(accion).append("\n");
        }

        sb.append("Recursos consumidos: ")
          .append(combustibleConsumido).append(" de combustible, ")
          .append(energiaConsumida).append(" de energia, ")
          .append(desgasteGenerado).append(" de desgaste\n");

        sb.append("Estado final de la nave ").append(idNave).append(": ")
          .append("combustible ").append(combustibleFinal).append(", ")
          .append("energia ").append(energiaFinal).append(", ")
          .append("desgaste ").append(desgasteFinal).append("\n");

        sb.append("Requiere mantenimiento: ").append(requiereMantenimiento ? "si" : "no");

        return sb.toString();
    }
}

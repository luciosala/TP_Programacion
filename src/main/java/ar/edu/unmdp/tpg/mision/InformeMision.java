package ar.edu.unmdp.tpg.mision;

import ar.edu.unmdp.tpg.naves.Nave;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Informe Final de Misión (fase Evaluar): qué se hizo, cómo terminó, el balance
 * detallado de recursos y en qué estado quedó la nave al llegar.
 *
 * Es inmutable: se construye en la evaluación y no cambia después, por lo que
 * sigue reflejando la misión aunque la nave siga operando. Lo que se entrega,
 * recolecta o recarga en el cierre ya figura en el balance.
 */
public final class InformeMision {

    private final String mision;
    private final String descripcion;
    private final String objetivo;
    private final List<String> acciones;
    private final BalanceDeRecursos balance;
    private final boolean exito;

    // Foto del estado de la nave al evaluar (antes del cierre)
    private final String idNave;
    private final int combustibleFinal;
    private final int energiaFinal;
    private final int desgasteFinal;
    private final boolean destruida;
    private final boolean requiereMantenimiento;

    /**
     * Construye el informe de una misión.
     *
     * Precondiciones (assert):
     * - mision no es nula ni vacía; descripcion, objetivo, acciones, balance y nave no son nulos.
     *
     * Postcondiciones:
     * - Guarda una copia de las acciones y una foto del estado de la nave.
     *
     * @param mision nombre de la misión
     * @param descripcion descripción de la misión
     * @param objetivo objetivo evaluado
     * @param acciones acciones realizadas, en orden
     * @param balance balance de recursos de la misión
     * @param exito resultado de la misión
     * @param nave nave que realizó la misión
     */
    public InformeMision(String mision, String descripcion, String objetivo,
                         List<String> acciones, BalanceDeRecursos balance,
                         boolean exito, Nave nave) {
        assert mision != null && !mision.isBlank() : "El informe requiere el nombre de la misión";
        assert descripcion != null && objetivo != null : "El informe requiere descripción y objetivo";
        assert acciones != null : "El informe requiere la lista de acciones";
        assert balance != null : "El informe requiere el balance de recursos";
        assert nave != null : "El informe requiere la nave de la misión";

        this.mision = mision;
        this.descripcion = descripcion;
        this.objetivo = objetivo;
        this.acciones = Collections.unmodifiableList(new ArrayList<>(acciones));
        this.balance = balance;
        this.exito = exito;
        this.idNave = nave.getId();
        this.combustibleFinal = nave.getCombustible();
        this.energiaFinal = nave.getEnergia();
        this.desgasteFinal = nave.getDesgaste();
        this.destruida = nave.estaDestruida();
        this.requiereMantenimiento = nave.requiereMantenimiento();
    }

    public String getMision() { return mision; }
    public String getDescripcion() { return descripcion; }
    public String getObjetivo() { return objetivo; }
    public List<String> getAcciones() { return acciones; }
    public BalanceDeRecursos getBalance() { return balance; }
    public int getCombustibleConsumido() { return balance.getCombustibleConsumido(); }
    public int getEnergiaConsumida() { return balance.getEnergiaConsumida(); }
    public int getDesgasteGenerado() { return balance.getDesgasteGenerado(); }
    public boolean fueExitosa() { return exito; }
    public String getIdNave() { return idNave; }
    public int getCombustibleFinal() { return combustibleFinal; }
    public int getEnergiaFinal() { return energiaFinal; }
    public int getDesgasteFinal() { return desgasteFinal; }
    public boolean naveDestruida() { return destruida; }
    public boolean requiereMantenimiento() { return requiereMantenimiento; }

    /** Resumen de una línea, para listados. */
    public String resumen() {
        return mision + " - " + (exito ? "EXITOSA" : "FALLIDA") + " - nave " + idNave;
    }

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
        sb.append("Balance de recursos:\n").append(balance).append("\n");
        sb.append("Recursos consumidos: ")
          .append(getCombustibleConsumido()).append(" de combustible, ")
          .append(getEnergiaConsumida()).append(" de energia, ")
          .append(getDesgasteGenerado()).append(" de desgaste\n");
        sb.append("Estado de la nave ").append(idNave).append(" al evaluar: ");
        if (destruida) {
            sb.append("DESTRUIDA, ");
        }
        sb.append("combustible ").append(combustibleFinal).append(", ")
          .append("energia ").append(energiaFinal).append(", ")
          .append("desgaste ").append(desgasteFinal).append("\n");
        sb.append("Requiere mantenimiento: ").append(requiereMantenimiento ? "si" : "no");
        return sb.toString();
    }
}

package ar.edu.unmdp.tpg.mision;

/**
 * Balance detallado de los recursos de una misión: lo consumido, lo perdido y lo
 * ganado, separado por etapa (Enunciado Parte 2, fase Evaluar: "balance detallado
 * de los recursos ganados, consumidos o perdidos durante el trayecto").
 *
 * Etapas:
 * - Despegue: costo inicial de la misión (Anexo).
 * - Navegación: saltos simples (combustible), salto warp (energía) y enfriamiento (desgaste).
 * - Encuentros: pérdidas por asteroides y ganancias en estaciones de carga.
 * - Acción final: lo entregado (M-01), las gemas (M-02) o la recarga y reparación (M-03).
 *
 * Invariante: todos los acumuladores son mayores o iguales a 0.
 */
public class BalanceDeRecursos {

    private int combustibleDespegue, energiaDespegue, desgasteDespegue;
    private int combustibleNavegacion, energiaNavegacion, desgasteNavegacion;
    private int energiaPerdida, combustiblePerdido;
    private int combustibleGanado, energiaGanada;
    private int combustibleEntregado, energiaEntregada;
    private int gemas;
    private int combustibleRecargado, energiaRecargada, desgasteReparado;

    private boolean invariante() {
        return combustibleDespegue >= 0 && energiaDespegue >= 0 && desgasteDespegue >= 0
            && combustibleNavegacion >= 0 && energiaNavegacion >= 0 && desgasteNavegacion >= 0
            && energiaPerdida >= 0 && combustiblePerdido >= 0
            && combustibleGanado >= 0 && energiaGanada >= 0
            && combustibleEntregado >= 0 && energiaEntregada >= 0 && gemas >= 0
            && combustibleRecargado >= 0 && energiaRecargada >= 0 && desgasteReparado >= 0;
    }

    /** Suma el costo de despegue. Precondición (assert): valores no negativos. */
    void sumarDespegue(int combustible, int energia, int desgaste) {
        assert combustible >= 0 && energia >= 0 && desgaste >= 0 : "El despegue no puede tener costos negativos";
        combustibleDespegue += combustible;
        energiaDespegue += energia;
        desgasteDespegue += desgaste;
        assert invariante() : "Fallo invariante de BalanceDeRecursos";
    }

    /**
     * Suma lo que cambió en la nave por un turno de movimiento. Las diferencias
     * son "después - antes": el motor solo puede gastar combustible y energía y
     * sumar desgaste.
     */
    void sumarNavegacion(int difCombustible, int difEnergia, int difDesgaste) {
        assert difCombustible <= 0 && difEnergia <= 0 && difDesgaste >= 0 : "El motor solo consume";
        combustibleNavegacion -= difCombustible;
        energiaNavegacion -= difEnergia;
        desgasteNavegacion += difDesgaste;
        assert invariante() : "Fallo invariante de BalanceDeRecursos";
    }

    /**
     * Suma lo que cambió en la nave por un encuentro con un elemento del universo.
     * Las diferencias son "después - antes": positivas son ganancias (estación de
     * carga), negativas son pérdidas (asteroide).
     */
    void sumarEncuentro(int difCombustible, int difEnergia) {
        if (difCombustible > 0) {
            combustibleGanado += difCombustible;
        } else {
            combustiblePerdido -= difCombustible;
        }
        if (difEnergia > 0) {
            energiaGanada += difEnergia;
        } else {
            energiaPerdida -= difEnergia;
        }
        assert invariante() : "Fallo invariante de BalanceDeRecursos";
    }

    /** M-01: recursos que se entregan a la nave aliada en el cierre. */
    void registrarEntrega(int combustible, int energia) {
        assert combustible >= 0 && energia >= 0 : "La entrega no puede ser negativa";
        combustibleEntregado = combustible;
        energiaEntregada = energia;
        assert invariante() : "Fallo invariante de BalanceDeRecursos";
    }

    /** M-02: gemas recolectadas en el destino. */
    void registrarGemas(int cantidad) {
        assert cantidad >= 0 : "Las gemas no pueden ser negativas";
        gemas = cantidad;
        assert invariante() : "Fallo invariante de BalanceDeRecursos";
    }

    /** M-03: lo que se recarga y repara al llegar a la base. */
    void registrarRecarga(int combustible, int energia, int desgaste) {
        assert combustible >= 0 && energia >= 0 && desgaste >= 0 : "La recarga no puede ser negativa";
        combustibleRecargado = combustible;
        energiaRecargada = energia;
        desgasteReparado = desgaste;
        assert invariante() : "Fallo invariante de BalanceDeRecursos";
    }

    // ---------- Totales (consultas) ----------

    /** @return combustible consumido: despegue, navegación y lo entregado */
    public int getCombustibleConsumido() {
        return combustibleDespegue + combustibleNavegacion + combustibleEntregado;
    }

    /** @return energía consumida: despegue, salto warp y lo entregado */
    public int getEnergiaConsumida() {
        return energiaDespegue + energiaNavegacion + energiaEntregada;
    }

    /** @return desgaste generado: despegue y enfriamientos del motor */
    public int getDesgasteGenerado() {
        return desgasteDespegue + desgasteNavegacion;
    }

    /** @return combustible gastado en saltos simples */
    public int getCombustibleNavegacion() {
        return combustibleNavegacion;
    }

    /** @return energía gastada en saltos warp */
    public int getEnergiaNavegacion() {
        return energiaNavegacion;
    }

    /** @return desgaste generado por los enfriamientos del Motor Warp */
    public int getDesgasteNavegacion() {
        return desgasteNavegacion;
    }

    /** @return energía perdida por impactos de asteroides */
    public int getEnergiaPerdida() {
        return energiaPerdida;
    }

    /** @return combustible ganado en estaciones de carga y en la recarga de la base */
    public int getCombustibleGanado() {
        return combustibleGanado + combustibleRecargado;
    }

    /** @return energía ganada en estaciones de carga y en la recarga de la base */
    public int getEnergiaGanada() {
        return energiaGanada + energiaRecargada;
    }

    /** @return gemas recolectadas */
    public int getGemas() {
        return gemas;
    }

    /** @return combustible entregado a la nave aliada */
    public int getCombustibleEntregado() {
        return combustibleEntregado;
    }

    /** @return energía entregada a la nave aliada */
    public int getEnergiaEntregada() {
        return energiaEntregada;
    }

    /** @return desgaste reparado en la base */
    public int getDesgasteReparado() {
        return desgasteReparado;
    }

    /** Detalle del balance, una línea por etapa. */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("  Despegue: -").append(combustibleDespegue).append(" combustible, -")
          .append(energiaDespegue).append(" energía, +").append(desgasteDespegue).append(" desgaste\n");
        sb.append("  Navegación: -").append(combustibleNavegacion).append(" combustible (saltos), -")
          .append(energiaNavegacion).append(" energía (warp), +").append(desgasteNavegacion).append(" desgaste (enfriamiento)\n");
        sb.append("  Perdido en el trayecto: -").append(energiaPerdida).append(" energía");
        if (combustiblePerdido > 0) {
            sb.append(", -").append(combustiblePerdido).append(" combustible");
        }
        sb.append("\n");
        sb.append("  Ganado en el trayecto: +").append(combustibleGanado).append(" combustible, +")
          .append(energiaGanada).append(" energía\n");
        sb.append("  Acción final: ");
        if (combustibleEntregado > 0 || energiaEntregada > 0) {
            sb.append("entregados ").append(combustibleEntregado).append(" combustible y ")
              .append(energiaEntregada).append(" energía");
        } else if (gemas > 0) {
            sb.append("+").append(gemas).append(" gemas");
        } else if (combustibleRecargado > 0 || energiaRecargada > 0 || desgasteReparado > 0) {
            sb.append("recarga +").append(combustibleRecargado).append(" combustible, +")
              .append(energiaRecargada).append(" energía, desgaste -").append(desgasteReparado);
        } else {
            sb.append("sin movimientos de recursos");
        }
        return sb.toString();
    }
}

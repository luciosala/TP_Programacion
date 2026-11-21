package ar.edu.unmdp.tpg.vista;

import ar.edu.unmdp.tpg.haberes.Haber;
import ar.edu.unmdp.tpg.tripulacion.Tripulante;

import javax.swing.table.AbstractTableModel;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de la tabla de tripulantes: hace de puente entre los tripulantes (modelo
 * del dominio) y la JTable, igual que PersonasTableModel en el ejemplo de MVC en
 * Swing de la Clase 13.
 */
public class TablaTripulantesModel extends AbstractTableModel {

    private static final String[] COLUMNAS = {
        "Id", "Nombre", "Apellido", "Cargo", "Origen", "Antigüedad", "Consejos", "Salario (PG)"
    };

    private List<Tripulante> tripulantes = new ArrayList<>();
    private List<Haber> haberes = new ArrayList<>();
    private YearMonth periodo = YearMonth.now();

    /** Reemplaza los datos y avisa a la tabla que se redibuje. */
    public void setDatos(List<Tripulante> tripulantes, List<Haber> haberes) {
        assert tripulantes.size() == haberes.size() : "Cada tripulante necesita su haber";
        this.tripulantes = new ArrayList<>(tripulantes);
        this.haberes = new ArrayList<>(haberes);
        this.periodo = YearMonth.now();
        fireTableDataChanged();
    }

    /** @return id del tripulante de esa fila */
    public String getId(int fila) {
        return tripulantes.get(fila).getId();
    }

    @Override
    public int getRowCount() {
        return tripulantes.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNAS.length;
    }

    @Override
    public String getColumnName(int columna) {
        return COLUMNAS[columna];
    }

    @Override
    public Object getValueAt(int fila, int columna) {
        Tripulante t = tripulantes.get(fila);
        switch (columna) {
            case 0: return t.getId();
            case 1: return t.getNombre();
            case 2: return t.getApellido();
            case 3: return t.getCargo().getNombre();
            case 4: return t.getOrigen();
            case 5: return t.getAntiguedadAnios();
            case 6: return t.esConsejero() ? String.valueOf(t.cantidadConsejos(periodo)) : "-";
            default: return String.format("%.2f", haberes.get(fila).calcularTotal());
        }
    }
}

package ar.edu.unmdp.tpg.gui.vista;

import java.awt.event.ActionListener;
import java.util.List;

/**
 * Seccion de control de la nave: recursos, bitacora, informes, iniciar mision y
 * el tablero en vivo. La vista solo muestra lo que el controlador le pasa.
 */
public interface IVistaControl {

    String SELECCIONAR_NAVE = "SELECCIONAR_NAVE";
    String INICIAR_MISION = "INICIAR_MISION";
    String MANTENIMIENTO = "MANTENIMIENTO";
    String CARGAR_COMBUSTIBLE = "CARGAR_COMBUSTIBLE";
    String CARGAR_ENERGIA = "CARGAR_ENERGIA";

    /** Nombres de las misiones, en el orden en que aparecen en el combo. */
    String[] MISIONES = {"M-01 Intercepción y asistencia", "M-02 Recolección", "M-03 Retorno seguro"};

    void setActionListener(ActionListener listener);

    String getNaveSeleccionada();

    /** Indice de la mision elegida en el combo (0, 1 o 2). */
    int getMisionSeleccionada();

    /**
     * Cantidad a cargar (combustible o energia). La vista valida el formato: si no
     * es un entero positivo, avisa y devuelve null.
     */
    Integer getCantidadCarga();

    void mostrarNaves(List<String> ids);

    void mostrarRecursos(int combustible, int energia, int desgaste, int gemas, String estadoMotor, boolean destruida);

    /** Reemplaza el contenido de la bitacora por estas lineas. */
    void mostrarBitacora(List<String> eventos);

    /** Agrega un informe al final del historial. */
    void agregarInforme(String informe);

    /**
     * Dibuja el tablero. etiquetas[fila][columna] es un texto corto por casilla
     * ("A" asteroide, "E" estacion, "N" nave enemiga) o null si esta vacia.
     * base es {fila, columna} de la base (donde se carga y adonde vuelve M-03),
     * objetivo es {fila, columna} de la casilla a la que va la mision (o null si no hay),
     * y ruta son las casillas {fila, columna} que le faltan recorrer a la nave.
     */
    void mostrarTablero(String[][] etiquetas, int filaNave, int columnaNave, int[] base, int[] objetivo, List<int[]> ruta);

    /** Habilita cargar y mantenimiento solo cuando la nave esta en la base. */
    void habilitarCarga(boolean enLaBase);

    /** Habilita o deshabilita el boton de iniciar mision (mientras una esta en curso). */
    void habilitarMision(boolean habilitada);

    void mostrarError(String mensaje);
}

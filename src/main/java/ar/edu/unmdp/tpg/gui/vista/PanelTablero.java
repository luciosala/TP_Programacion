package ar.edu.unmdp.tpg.gui.vista;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

/**
 * Dibuja el tablero como una grilla. No conoce el modelo: recibe una matriz de
 * etiquetas y la posicion de la nave, y las pinta. Cada vez que el controlador
 * le pasa datos nuevos, se vuelve a dibujar.
 */
public class PanelTablero extends JPanel {

    private static final int LADO = 36;   // pixeles por casilla

    private String[][] etiquetas = new String[10][10];   // grilla vacia hasta que llegue el primer tablero
    private int filaNave = -1;
    private int columnaNave = -1;
    private int[] base = null;                      // {fila, columna} de la base
    private int[] objetivo = null;                  // {fila, columna} o null
    private List<int[]> ruta = new ArrayList<>();   // casillas pendientes {fila, columna}

    public PanelTablero() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(columnas() * LADO + 1, filas() * LADO + 1));
    }

    public void mostrar(String[][] etiquetas, int filaNave, int columnaNave, int[] base, int[] objetivo, List<int[]> ruta) {
        this.etiquetas = etiquetas;
        this.filaNave = filaNave;
        this.columnaNave = columnaNave;
        this.base = base;
        this.objetivo = objetivo;
        this.ruta = ruta;
        setPreferredSize(new Dimension(columnas() * LADO + 1, filas() * LADO + 1));
        revalidate();
        repaint();
    }

    private int filas() {
        return etiquetas.length;
    }

    private int columnas() {
        return filas() == 0 ? 0 : etiquetas[0].length;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        // Primero el fondo de la ruta pendiente y del objetivo, despues los elementos encima
        g.setColor(new Color(215, 230, 250));
        for (int[] casilla : ruta) {
            g.fillRect(casilla[1] * LADO, casilla[0] * LADO, LADO, LADO);
        }
        if (base != null) {
            g.setColor(new Color(225, 245, 225));
            g.fillRect(base[1] * LADO, base[0] * LADO, LADO, LADO);
        }
        if (objetivo != null) {
            g.setColor(new Color(255, 215, 90));
            g.fillRect(objetivo[1] * LADO, objetivo[0] * LADO, LADO, LADO);
        }
        for (int fila = 0; fila < filas(); fila++) {
            for (int columna = 0; columna < columnas(); columna++) {
                // En pantalla, la columna (y) va hacia la derecha y la fila (x) hacia abajo
                int px = columna * LADO;
                int py = fila * LADO;
                if (fila == filaNave && columna == columnaNave) {
                    g.setColor(new Color(70, 130, 220));
                    g.fillRect(px, py, LADO, LADO);
                    g.setColor(Color.WHITE);
                    g.drawString("NAVE", px + 3, py + LADO / 2 + 5);
                } else if (etiquetas[fila][columna] != null) {
                    g.setColor(colorDe(etiquetas[fila][columna]));
                    g.fillOval(px + 4, py + 4, LADO - 8, LADO - 8);
                    g.setColor(Color.WHITE);
                    g.drawString(etiquetas[fila][columna], px + LADO / 2 - 5, py + LADO / 2 + 5);
                }
                if (esRuta(fila, columna) && etiquetas[fila][columna] == null && !(fila == filaNave && columna == columnaNave)) {
                    g.setColor(new Color(70, 130, 220));
                    g.fillOval(px + LADO / 2 - 4, py + LADO / 2 - 4, 8, 8);   // punto del recorrido
                }
                if (objetivo != null && fila == objetivo[0] && columna == objetivo[1]) {
                    g.setColor(Color.BLACK);
                    g.drawString("X", px + LADO / 2 - 5, py + LADO / 2 + 5);   // la meta
                }
                if (base != null && fila == base[0] && columna == base[1]) {
                    g.setColor(new Color(40, 120, 40));
                    g.drawRect(px + 1, py + 1, LADO - 3, LADO - 3);           // marco de la base
                    g.drawRect(px + 2, py + 2, LADO - 5, LADO - 5);
                    if (!(fila == filaNave && columna == columnaNave)) {
                        g.drawString("B", px + 4, py + 14);
                    }
                }
                g.setColor(Color.LIGHT_GRAY);
                g.drawRect(px, py, LADO, LADO);
            }
        }
    }

    private boolean esRuta(int fila, int columna) {
        for (int[] casilla : ruta) {
            if (casilla[0] == fila && casilla[1] == columna) {
                return true;
            }
        }
        return false;
    }

    private static Color colorDe(String etiqueta) {
        switch (etiqueta) {
            case "A":  return Color.DARK_GRAY;            // asteroide
            case "E":  return new Color(40, 160, 70);     // estacion de carga
            default:   return new Color(200, 50, 50);     // nave enemiga
        }
    }
}

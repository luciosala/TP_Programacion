package ar.edu.unmdp.tpg.vista;

import ar.edu.unmdp.tpg.destino.Posicion;
import ar.edu.unmdp.tpg.elementos.ElementoDelUniverso;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

/**
 * Dibujo del tablero: una grilla (GridLayout) con una etiqueta por casilla.
 * Muestra la nave (NAV), el objetivo (OBJ), la base de origen (BAS) y la
 * abreviatura de cada elemento del universo.
 */
public class PanelTablero extends JPanel {

    private static final Color VACIO = Color.WHITE;
    private static final Color NAVE = new Color(70, 130, 220);
    private static final Color OBJETIVO = new Color(90, 190, 90);
    private static final Color BASE = new Color(200, 200, 200);
    private static final Color OBSTACULO = new Color(150, 110, 80);
    private static final Color AYUDA = new Color(255, 220, 90);
    private static final Color OTRO = new Color(230, 90, 90);

    private final JLabel[][] casillas;

    public PanelTablero(int filas, int columnas) {
        setLayout(new GridLayout(filas, columnas, 1, 1));
        setBackground(Color.LIGHT_GRAY);
        casillas = new JLabel[filas][columnas];
        Font fuente = new Font(Font.MONOSPACED, Font.PLAIN, 10);
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                JLabel casilla = new JLabel("", SwingConstants.CENTER);
                casilla.setOpaque(true);
                casilla.setBackground(VACIO);
                casilla.setFont(fuente);
                casilla.setPreferredSize(new Dimension(30, 24));
                casillas[i][j] = casilla;
                add(casilla);
            }
        }
        setBorder(BorderFactory.createTitledBorder("Tablero en tiempo real"));
    }

    /**
     * Dibuja una foto del tablero.
     *
     * @param elementos matriz de elementos (null = casilla vacía), o null si no hay nave
     * @param nave posición de la nave
     * @param objetivo objetivo actual, o null
     * @param base base de origen
     */
    public void dibujar(ElementoDelUniverso[][] elementos, Posicion nave, Posicion objetivo, Posicion base) {
        for (int i = 0; i < casillas.length; i++) {
            for (int j = 0; j < casillas[i].length; j++) {
                JLabel casilla = casillas[i][j];
                Posicion aqui = new Posicion(i, j);
                ElementoDelUniverso elemento = elementos == null ? null : elementos[i][j];
                if (nave != null && aqui.equals(nave)) {
                    pintar(casilla, "NAV", NAVE);
                } else if (elemento != null) {
                    Color color = OTRO;
                    if (elemento.esObstaculo()) {
                        color = OBSTACULO;
                    } else if (elemento.abreviatura().equals("Est")) {
                        color = AYUDA;
                    }
                    pintar(casilla, elemento.abreviatura(), color);
                } else if (objetivo != null && aqui.equals(objetivo)) {
                    pintar(casilla, "OBJ", OBJETIVO);
                } else if (base != null && aqui.equals(base)) {
                    pintar(casilla, "BAS", BASE);
                } else {
                    pintar(casilla, "", VACIO);
                }
            }
        }
    }

    private void pintar(JLabel casilla, String texto, Color color) {
        casilla.setText(texto);
        casilla.setBackground(color);
    }
}

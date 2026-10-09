package ar.edu.unmdp.tpg.haberes;

/**
 * Haber mensual de un tripulante, visto como una suma de conceptos.
 *
 * PATRON DECORATOR: HaberBase es el componente concreto, con el sueldo del cargo.
 * Cada decorador (antiguedad, origen, consejos) envuelve un haber y le agrega su
 * propio concepto, de modo que los adicionales se combinan sin tocar las clases
 * existentes y sin una subclase por combinacion posible.
 *
 * Contrato comun de todas las implementaciones:
 * - Los conceptos se numeran desde 0 y su cantidad nunca disminuye.
 * - calcularTotal() es igual a la suma de los importes de todos los conceptos.
 * - Un haber ya construido no cambia: consultarlo dos veces da el mismo resultado.
 */
public abstract class Haber {

    /**
     * Calcula el total del haber, sumando todos sus conceptos.
     *
     * Postcondiciones:
     * - Devuelve un importe finito y no negativo, expresado en PG.
     * - El resultado coincide con la suma de los importes de los conceptos 0 a cantidadConceptos()-1.
     * - El haber no se modifica.
     *
     * @return total del haber en PG
     */
    public abstract double calcularTotal();

    /**
     * Cuenta los conceptos que componen el haber.
     *
     * Postcondiciones:
     * - Devuelve un valor mayor o igual a 1: todo haber tiene al menos el sueldo base.
     * - El haber no se modifica.
     *
     * @return cantidad de conceptos del haber
     */
    public abstract int cantidadConceptos();

    /**
     *Consulta el nombre de un concepto por su índice, empezando en cero.
     * Las implementaciones deben rechazar índices fuera del rango válido.
     */
    public abstract String consultarNombreConcepto(int indice);

    /**
     * Consulta el importe en PG de un concepto por su índice, empezando en cero.
     * Las implementaciones deben rechazar índices fuera del rango válido.
     */
    public abstract double consultarImporteConcepto(int indice);
}

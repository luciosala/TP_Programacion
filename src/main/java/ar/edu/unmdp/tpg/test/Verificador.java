package ar.edu.unmdp.tpg.test;

/**
 * Verificador propio de los escenarios de prueba (lo pide la devolucion de la
 * Entrega 1: "agregar comprobaciones simples con assert o un verificador propio").
 *
 * Cada comprobacion compara el resultado obtenido con el esperado y lo informa
 * como [OK] o [FALLA]. Al final se imprime un resumen. Los main de prueba
 * terminan con codigo 1 si algo fallo, asi un error no pasa desapercibido
 * aunque nadie lea la salida completa.
 *
 * Vive en el paquete test, separado del modelo, como la arquitectura de
 * paquetes del caso del Club (interfaz / test / mundo) de la teoria.
 */
public final class Verificador {

    private int comprobaciones = 0;
    private int fallos = 0;

    /**
     * Indica si la JVM se lanzo con las aserciones activadas (java -ea).
     *
     * Como dice la teoria, los asertos estan desactivados por defecto. La
     * asignacion que esta dentro del assert solo se ejecuta cuando estan
     * activados, asi que si al final la variable vale true, estan activos.
     *
     * @return true si las aserciones estan activas
     */
    public static boolean asercionesActivas() {
        boolean activas = false;
        assert activas = true;
        return activas;
    }

    /**
     * Comprueba una condicion.
     *
     * @param descripcion que se esta comprobando
     * @param condicion resultado de la comprobacion
     */
    public void verificar(String descripcion, boolean condicion) {
        registrar(condicion, descripcion);
    }

    /**
     * Comprueba que un numero entero sea el esperado.
     *
     * @param descripcion que se esta comprobando
     * @param esperado valor esperado
     * @param obtenido valor obtenido
     */
    public void verificarIgual(String descripcion, int esperado, int obtenido) {
        registrar(esperado == obtenido, descripcion + " (esperado " + esperado + ", obtenido " + obtenido + ")");
    }

    /**
     * Comprueba que un texto sea el esperado.
     *
     * @param descripcion que se esta comprobando
     * @param esperado texto esperado
     * @param obtenido texto obtenido
     */
    public void verificarIgual(String descripcion, String esperado, String obtenido) {
        registrar(esperado.equals(obtenido), descripcion + " (esperado " + esperado + ", obtenido " + obtenido + ")");
    }

    /**
     * Comprueba que un importe coincida con el esperado (tolerancia de 0,001).
     *
     * @param descripcion que se esta comprobando
     * @param esperado importe esperado
     * @param obtenido importe obtenido
     */
    public void verificarImporte(String descripcion, double esperado, double obtenido) {
        boolean ok = Math.abs(esperado - obtenido) < 0.001;
        registrar(ok, descripcion + String.format(" (esperado %.2f, obtenido %.2f)", esperado, obtenido));
    }

    /**
     * Comprueba que los tres recursos de una nave tengan los valores esperados.
     *
     * @param descripcion que se esta comprobando
     * @param combustible combustible esperado
     * @param energia energia esperada
     * @param desgaste desgaste esperado
     * @param combustibleObtenido combustible obtenido
     * @param energiaObtenida energia obtenida
     * @param desgasteObtenido desgaste obtenido
     */
    public void verificarRecursos(String descripcion, int combustible, int energia, int desgaste,
                                  int combustibleObtenido, int energiaObtenida, int desgasteObtenido) {
        boolean ok = combustible == combustibleObtenido && energia == energiaObtenida && desgaste == desgasteObtenido;
        registrar(ok, descripcion + " (esperado " + combustible + "/" + energia + "/" + desgaste
            + ", obtenido " + combustibleObtenido + "/" + energiaObtenida + "/" + desgasteObtenido + ")");
    }

    /**
     * Registra como correcta una operacion que fue rechazada como se esperaba.
     * Se llama desde el catch del rechazo esperado.
     *
     * @param descripcion operacion que debia rechazarse
     * @param rechazo excepcion o error con el que se rechazo
     */
    public void rechazoEsperado(String descripcion, Throwable rechazo) {
        registrar(true, descripcion + " -> " + rechazo.getClass().getSimpleName() + ": " + rechazo.getMessage());
    }

    /**
     * Registra como falla una operacion que debia rechazarse y no se rechazo.
     * Se llama en el try, inmediatamente despues de la operacion.
     *
     * @param descripcion operacion que debia rechazarse
     */
    public void faltoRechazo(String descripcion) {
        registrar(false, descripcion + " (se esperaba un rechazo y no lo hubo)");
    }

    /**
     * Registra como falla un escenario que se interrumpio por un error que no
     * esperaba, para que no oculte el resultado de los escenarios siguientes.
     *
     * @param escenario nombre del escenario
     * @param error error inesperado
     */
    public void escenarioInterrumpido(String escenario, Throwable error) {
        registrar(false, escenario + " se interrumpió por un error inesperado -> "
            + error.getClass().getSimpleName() + ": " + error.getMessage());
    }

    /**
     * Cantidad de comprobaciones que fallaron.
     *
     * @return comprobaciones fallidas
     */
    public int fallos() {
        return fallos;
    }

    /** Imprime el resumen final de la verificacion. */
    public void imprimirResumen() {
        System.out.println();
        System.out.println("=== RESUMEN DE VERIFICACION ===");
        System.out.println("Comprobaciones: " + comprobaciones + " | correctas: " + (comprobaciones - fallos)
            + " | fallidas: " + fallos);
        if (fallos == 0) {
            System.out.println("RESULTADO: TODOS LOS ESCENARIOS COINCIDEN CON LO ESPERADO");
        } else {
            System.out.println("RESULTADO: HAY " + fallos + " COMPROBACION(ES) FALLIDA(S)");
        }
    }

    private void registrar(boolean ok, String detalle) {
        comprobaciones++;
        if (ok) {
            System.out.println("  [OK]    " + detalle);
        } else {
            fallos++;
            System.out.println("  [FALLA] " + detalle);
        }
    }
}

package ar.edu.unmdp.tpg.naves;

import ar.edu.unmdp.tpg.excepciones.LimiteRecursoExcedidoException;
import ar.edu.unmdp.tpg.excepciones.RecursoInsuficienteException;

/**
 * Combustible, energia y desgaste de una nave. Los tres se mueven siempre entre
 * 0 y 100.
 *
 * Es la unica clase que modifica esos valores, y rechaza toda operacion que los
 * sacaria de rango: ninguna nave puede quedar con recursos negativos ni por
 * encima de su maximo.
 *
 * Contratos:
 * - Las precondiciones son obligacion del invocante y se comprueban con assert
 *   (se activan con java -ea).
 * - Las situaciones del dominio que el programa debe gestionar (falta de
 *   recursos, carga por encima de la capacidad) se informan con excepciones y
 *   nunca dejan cambios parciales.
 *
 * Invariantes:
 * - El combustible se mantiene entre 0 y CAPACIDAD_MAXIMA, inclusive.
 * - La energia se mantiene entre 0 y CAPACIDAD_MAXIMA, inclusive.
 * - El desgaste se mantiene entre 0 y DESGASTE_MAXIMO, inclusive.
 */
final public class Recursos{

    /** Capacidad maxima de combustible y de energia. */
    public static final int CAPACIDAD_MAXIMA = 100;
    /** Desgaste maximo admitido. */
    public static final int DESGASTE_MAXIMO = 100;
    /** Desgaste a partir del cual la nave requiere mantenimiento. */
    public static final int UMBRAL_MANTENIMIENTO = 80;

    private int combustible;
    private int energia;
    private int desgaste;


    /**
     * Construye los recursos de la nave
     *
     * Precondiciones (assert):
     * - combustibleInicial está entre 0 y 100, inclusive.
     * - energiaInicial está entre 0 y 100, inclusive.
     * - desgasteInicial está entre 0 y 100, inclusive.
     *
     * Postcondiciones:
     * - El combustible, la energía y el desgaste quedan inicializados con los valores recibidos por parámetro.
     * - Se cumple el invariante.
     *
     * @param combustibleInicial combustible inicial de la nave
     * @param energiaInicial energia inicial de la nave
     * @param desgasteInicial desgaste inicial de la nave
     */
    public Recursos(int combustibleInicial, int energiaInicial, int desgasteInicial) {
        assert combustibleInicial >= 0 && combustibleInicial <= CAPACIDAD_MAXIMA
            : "El combustible debe empezar entre 0 y 100";
        assert energiaInicial >= 0 && energiaInicial <= CAPACIDAD_MAXIMA
            : "La energía debe empezar entre 0 y 100";
        assert desgasteInicial >= 0 && desgasteInicial <= DESGASTE_MAXIMO
            : "El desgaste debe empezar entre 0 y 100";

        this.combustible = combustibleInicial;
        this.energia = energiaInicial;
        this.desgaste = desgasteInicial;

        assert invariante() : "Recursos fuera de rango al construir";
    }

    /**
     * Invariante de clase: los tres recursos dentro de su rango.
     *
     * @return true si el estado de los recursos es valido
     */
    private boolean invariante() {
        return combustible >= 0 && combustible <= CAPACIDAD_MAXIMA
            && energia >= 0 && energia <= CAPACIDAD_MAXIMA
            && desgaste >= 0 && desgaste <= DESGASTE_MAXIMO;
    }

    /**
     * Consulta el combustible actual.
     *
     * Postcondiciones:
     * - Los recursos no se modifican.
     * - El valor devuelto esta entre 0 y 100.
     *
     * @return combustible disponible
     */
    public int getCombustible(){return this.combustible;}
    /**
     * Consulta la energia actual.
     *
     * Postcondiciones:
     * - Los recursos no se modifican.
     * - El valor devuelto esta entre 0 y 100.
     *
     * @return energia disponible
     */
    public int getEnergia(){return this.energia;}
    /**
     * Consulta el desgaste acumulado.
     *
     * Postcondiciones:
     * - Los recursos no se modifican.
     * - El valor devuelto esta entre 0 y 100.
     *
     * @return desgaste acumulado
     */
    public int getDesgaste(){return this.desgaste;}

    /**
     * Carga combustible en los recursos de la nave.
     *
     * Precondiciones (assert):
     * - cantidad es mayor que 0.
     *
     * Postcondiciones:
     * - El combustible aumenta exactamente en cantidad y permanece entre 0 y 100.
     * - Si la carga se rechaza, todos los recursos permanecen sin cambios.
     *
     * @param cantidad cantidad de combustible a cargar
     * @throws LimiteRecursoExcedidoException rechazo de dominio: la carga supera
     *         la capacidad restante (100 - combustible)
     */
    public void cargarCombustible(int cantidad) throws LimiteRecursoExcedidoException {
        assert cantidad > 0 : "La cantidad de combustible debe ser positiva";

        if (cantidad > CAPACIDAD_MAXIMA - combustible) {
            throw new LimiteRecursoExcedidoException("La carga de combustible supera la capacidad máxima de 100");
        }

        int combustibleAnterior = combustible;
        combustible += cantidad;

        assert combustible == combustibleAnterior + cantidad : "Fallo postcondición de cargarCombustible";
        assert invariante() : "Fallo invariante de Recursos";
    }

    /**
     * Carga energía en los recursos de la nave.
     *
     * Precondiciones (assert):
     * - cantidad es mayor que 0.
     *
     * Postcondiciones:
     * - La energía aumenta exactamente en cantidad y permanece entre 0 y 100.
     * - Si la carga se rechaza, todos los recursos permanecen sin cambios.
     *
     * @param cantidad cantidad de energía a cargar
     * @throws LimiteRecursoExcedidoException rechazo de dominio: la carga supera
     *         la capacidad restante (100 - energia)
     */
    public void cargarEnergia(int cantidad) throws LimiteRecursoExcedidoException {
        assert cantidad > 0 : "La cantidad de energía debe ser positiva";

        if (cantidad > CAPACIDAD_MAXIMA - energia) {
            throw new LimiteRecursoExcedidoException("La carga de energía supera la capacidad máxima de 100");
        }

        int energiaAnterior = energia;
        energia += cantidad;

        assert energia == energiaAnterior + cantidad : "Fallo postcondición de cargarEnergia";
        assert invariante() : "Fallo invariante de Recursos";
    }
    /**
     * Descuenta energia de los recursos.
     *
     * Precondiciones (assert):
     * - cantidad es mayor que 0.
     *
     * Postcondiciones:
     * - La energia disminuye exactamente en cantidad y permanece entre 0 y 100.
     * - Si el consumo se rechaza, todos los recursos permanecen sin cambios.
     *
     * @param cantidad energia a descontar
     * @throws RecursoInsuficienteException rechazo de dominio: no hay esa energia disponible
     */
    public void consumirEnergia(int cantidad) throws RecursoInsuficienteException {
        assert cantidad > 0 : "La cantidad a consumir debe ser positiva";

        if (cantidad > energia) {
            throw new RecursoInsuficienteException("No hay energia suficiente para consumir");
        }

        int energiaAnterior = energia;
        energia -= cantidad;

        assert energia == energiaAnterior - cantidad : "Fallo postcondición de consumirEnergia";
        assert invariante() : "Fallo invariante de Recursos";
    }
    /**
     * Descuenta combustible de los recursos.
     *
     * Precondiciones (assert):
     * - cantidad es mayor que 0.
     *
     * Postcondiciones:
     * - El combustible disminuye exactamente en cantidad y permanece entre 0 y 100.
     * - Si el consumo se rechaza, todos los recursos permanecen sin cambios.
     *
     * @param cantidad combustible a descontar
     * @throws RecursoInsuficienteException rechazo de dominio: no hay ese combustible disponible
     */
    public void consumirCombustible(int cantidad) throws RecursoInsuficienteException {
        assert cantidad > 0 : "La cantidad a consumir debe ser positiva";

        if (cantidad > combustible) {
            throw new RecursoInsuficienteException("No hay combustible suficiente para consumir");
        }

        int combustibleAnterior = combustible;
        combustible -= cantidad;

        assert combustible == combustibleAnterior - cantidad : "Fallo postcondición de consumirCombustible";
        assert invariante() : "Fallo invariante de Recursos";
    }
    /**
     * Suma desgaste a los recursos.
     *
     * Precondiciones (assert):
     * - cantidad es mayor que 0.
     *
     * Postcondiciones:
     * - El desgaste aumenta exactamente en cantidad y permanece entre 0 y 100.
     * - Si se rechaza, todos los recursos permanecen sin cambios.
     *
     * @param cantidad desgaste a sumar
     * @throws LimiteRecursoExcedidoException rechazo de dominio: se superaria el limite de 100
     */
    public void agregarDesgaste(int cantidad) throws LimiteRecursoExcedidoException {
        assert cantidad > 0 : "La cantidad a desgastar debe ser positiva";

        if (cantidad > DESGASTE_MAXIMO - desgaste) {
            throw new LimiteRecursoExcedidoException("Se llego al limite de desgaste");
        }

        int desgasteAnterior = desgaste;
        desgaste += cantidad;

        assert desgaste == desgasteAnterior + cantidad : "Fallo postcondición de agregarDesgaste";
        assert invariante() : "Fallo invariante de Recursos";
    }

    /**
     * Consulta si los recursos requieren mantenimiento.
     *
     * Postcondiciones:
     * - Devuelve true si el desgaste es mayor o igual a 80; false en caso contrario.
     *
     * @return si se necesita mantenimiento.
     */
    public boolean requiereMantenimiento() {
        return desgaste >= UMBRAL_MANTENIMIENTO;
    }

    /**
     * Realiza el mantenimiento de los recursos de la nave.
     *
     * Postcondiciones:
     * - El desgaste queda en 0.
     * - El combustible y la energia no se modifican.
     */
    public void realizarMantenimiento() {
        int combustibleAnterior = combustible;
        int energiaAnterior = energia;

        desgaste = 0;

        assert desgaste == 0 && !requiereMantenimiento() : "Fallo postcondición de realizarMantenimiento";
        assert combustible == combustibleAnterior && energia == energiaAnterior
            : "El mantenimiento no debe modificar combustible ni energía";
        assert invariante() : "Fallo invariante de Recursos";
    }

    /**
     * Daño recibido (por ejemplo, el impacto de un asteroide): resta energía
     * pero no baja de 0. No lanza excepción porque un golpe no se puede
     * "rechazar".
     *
     * Precondiciones (assert):
     * - cantidad es mayor que 0.
     *
     * Postcondiciones:
     * - La energia queda en max(0, energia anterior - cantidad).
     * - El combustible y el desgaste no se modifican.
     *
     * @param cantidad energia que se pierde
     */
    public void perderEnergia(int cantidad) {
        assert cantidad > 0 : "El daño debe ser positivo";

        int energiaAnterior = energia;
        energia = Math.max(0, energia - cantidad);

        assert energia == Math.max(0, energiaAnterior - cantidad) : "Fallo postcondición de perderEnergia";
        assert invariante() : "Fallo invariante de Recursos";
    }

    /**
     * Recarga en una estación de carga: suma pero no pasa de 100. Lo que sobra
     * se descarta, en vez de lanzar LimiteRecursoExcedidoException.
     *
     * Precondiciones (assert):
     * - Las dos cantidades son mayores o iguales a 0.
     *
     * Postcondiciones:
     * - Combustible y energia quedan en min(100, anterior + extra).
     * - El desgaste no se modifica.
     *
     * @param combustibleExtra combustible a sumar
     * @param energiaExtra energia a sumar
     */
    public void recargar(int combustibleExtra, int energiaExtra) {
        assert combustibleExtra >= 0 && energiaExtra >= 0
            : "Las cantidades a recargar no pueden ser negativas";

        int combustibleAnterior = combustible;
        int energiaAnterior = energia;
        combustible = Math.min(CAPACIDAD_MAXIMA, combustible + combustibleExtra);
        energia = Math.min(CAPACIDAD_MAXIMA, energia + energiaExtra);

        assert combustible == Math.min(CAPACIDAD_MAXIMA, combustibleAnterior + combustibleExtra)
            && energia == Math.min(CAPACIDAD_MAXIMA, energiaAnterior + energiaExtra)
            : "Fallo postcondición de recargar";
        assert invariante() : "Fallo invariante de Recursos";
    }

    /**
     * Consulta si los recursos estan en un nivel que destruye la nave.
     *
     * Postcondiciones:
     * - Devuelve true si el desgaste llego a 100, o la energia o el combustible a 0.
     *
     * @return si la nave queda en un nivel letal
     */
    public boolean enNivelLetal() {
        return desgaste >= DESGASTE_MAXIMO || energia <= 0 || combustible <= 0;
    }

    /**
     * Aplica en una sola operacion el consumo de una mision.
     *
     * Precondiciones (assert):
     * - Las tres cantidades son mayores o iguales a 0.
     *
     * Postcondiciones:
     * - El combustible y la energia disminuyen y el desgaste aumenta exactamente en
     *   las cantidades indicadas, y los tres permanecen entre 0 y 100.
     * - Si alguna de las tres no es aplicable, no se modifica ninguna: la operacion
     *   es atomica, se aplica completa o no se aplica.
     *
     * @param combustibleConsumido combustible a descontar
     * @param energiaConsumida energia a descontar
     * @param desgasteAgregado desgaste a sumar
     * @throws RecursoInsuficienteException rechazo de dominio: falta combustible o energia
     * @throws LimiteRecursoExcedidoException rechazo de dominio: el desgaste superaria el limite de 100
     */
    public void consumirParaMision(int combustibleConsumido, int energiaConsumida, int desgasteAgregado)
            throws RecursoInsuficienteException, LimiteRecursoExcedidoException {
        verificarDisponibilidad(combustibleConsumido, energiaConsumida, desgasteAgregado);

        int combustibleAnterior = combustible;
        int energiaAnterior = energia;
        int desgasteAnterior = desgaste;

        combustible -= combustibleConsumido;
        energia -= energiaConsumida;
        desgaste += desgasteAgregado;

        assert combustible == combustibleAnterior - combustibleConsumido
            && energia == energiaAnterior - energiaConsumida
            && desgaste == desgasteAnterior + desgasteAgregado
            : "Fallo postcondición de consumirParaMision";
        assert invariante() : "Fallo invariante de Recursos";
    }

    /**
     * Comprueba si los recursos alcanzan para una operacion, sin aplicarla.
     *
     * Precondiciones (assert):
     * - Las tres cantidades son mayores o iguales a 0.
     *
     * Postcondiciones:
     * - Ningun recurso se modifica, ni cuando la verificacion resulta exitosa.
     * - Si no lanza excepcion, consumirParaMision con las mismas cantidades tampoco lo hara.
     *
     * @param combustibleNecesario combustible que la operacion necesita
     * @param energiaNecesaria energia que la operacion necesita
     * @param desgasteAgregado desgaste que la operacion generaria
     * @throws RecursoInsuficienteException rechazo de dominio: falta combustible o energia
     * @throws LimiteRecursoExcedidoException rechazo de dominio: el desgaste superaria el limite de 100
     */
    public void verificarDisponibilidad(int combustibleNecesario, int energiaNecesaria, int desgasteAgregado)
            throws RecursoInsuficienteException, LimiteRecursoExcedidoException {
        assert combustibleNecesario >= 0 && energiaNecesaria >= 0 && desgasteAgregado >= 0
            : "Las cantidades necesarias no pueden ser negativas";

        if (combustibleNecesario > combustible) {
            throw new RecursoInsuficienteException("Combustible insuficiente");
        }

        if (energiaNecesaria > energia) {
            throw new RecursoInsuficienteException("Energía insuficiente");
        }

        if (desgasteAgregado > DESGASTE_MAXIMO - desgaste) {
            throw new LimiteRecursoExcedidoException(
                "El desgaste superaría el límite permitido"
            );
        }
    }
}

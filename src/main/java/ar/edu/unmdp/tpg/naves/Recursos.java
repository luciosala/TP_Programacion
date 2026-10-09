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
 * Invariantes:
 * - El combustible se mantiene entre 0 y 100, inclusive.
 * - La energia se mantiene entre 0 y 100, inclusive.
 * - El desgaste se mantiene entre 0 y 100, inclusive.
 */
final public class Recursos{


    private int combustible;
    private int energia;
    private int desgaste;


    /**
     * Construye los recursos de la nave
     * 
     * Precondiciones:
     * - combustibleInicial está entre 0 y 100, inclusive.
     * - energiaInicial está entre 0 y 100, inclusive.
     * - desgasteInicial está entre 0 y 100, inclusive.
     * 
     * Postcondiciones:
     * - El combustible, la energía y el desgaste quedan inicializados con los valores recibidos por parámetro.
     * 
     * @param combustibleInicial combustible inicial de la nave
     * @param energiaInicial energia inicial de la nave
     * @param desgasteInicial desgaste inicial de la nave
     * @throws IllegalArgumentException si se incumple alguna precondición. 
     */
    public Recursos(int combustibleInicial, int energiaInicial, int desgasteInicial) {
        if (combustibleInicial < 0 || combustibleInicial > 100)
            throw new IllegalArgumentException("El combustible debe empezar entre 0 y 100");

        if (energiaInicial < 0 || energiaInicial > 100)
            throw new IllegalArgumentException("La energía debe empezar entre 0 y 100");

        if (desgasteInicial < 0 || desgasteInicial > 100)
            throw new IllegalArgumentException("El desgaste debe empezar entre 0 y 100");

        this.combustible = combustibleInicial;
        this.energia = energiaInicial;
        this.desgaste = desgasteInicial;
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
     * Precondiciones:
     * - cantidad es mayor que 0.
     * - cantidad no supera la capacidad restante: 100 - combustible.
     *
     * Postcondiciones:
     * - El combustible aumenta exactamente en cantidad y permanece entre 0 y 100.
     * - Si la carga se rechaza, todos los recursos permanecen sin cambios.
     *
     * @param cantidad cantidad de combustible a cargar
     * @throws IllegalArgumentException si cantidad es menor o igual a 0
     * @throws LimiteRecursoExcedidoException si la carga supera la capacidad máxima
     */
    public void cargarCombustible(int cantidad) throws LimiteRecursoExcedidoException {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad de combustible debe ser positiva");
        }

        if (cantidad > 100 - combustible) {
            throw new LimiteRecursoExcedidoException("La carga de combustible supera la capacidad máxima de 100");
        }

        combustible += cantidad;
    }

    /**
     * Carga energía en los recursos de la nave.
     *
     * Precondiciones:
     * - cantidad es mayor que 0.
     * - cantidad no supera la capacidad restante: 100 - energia.
     *
     * Postcondiciones:
     * - La energía aumenta exactamente en cantidad y permanece entre 0 y 100.
     * - Si la carga se rechaza, todos los recursos permanecen sin cambios.
     *
     * @param cantidad cantidad de energía a cargar
     * @throws IllegalArgumentException si cantidad es menor o igual a 0
     * @throws LimiteRecursoExcedidoException si la carga supera la capacidad máxima
     */
    public void cargarEnergia(int cantidad) throws LimiteRecursoExcedidoException {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad de energía debe ser positiva");
        }

        if (cantidad > 100 - energia)
            throw new LimiteRecursoExcedidoException("La carga de energía supera la capacidad máxima de 100");


        energia += cantidad;
    }
    /**
     * Descuenta energia de los recursos.
     *
     * Precondiciones:
     * - cantidad es mayor que 0.
     * - cantidad no supera la energia disponible.
     *
     * Postcondiciones:
     * - La energia disminuye exactamente en cantidad y permanece entre 0 y 100.
     * - Si el consumo se rechaza, todos los recursos permanecen sin cambios.
     *
     * @param cantidad energia a descontar
     * @throws IllegalArgumentException si cantidad es menor o igual a 0
     * @throws RecursoInsuficienteException si no hay esa energia disponible
     */
    public void consumirEnergia(int cantidad) throws RecursoInsuficienteException {
        if (cantidad <= 0)
            throw new IllegalArgumentException("La cantidad a consumir debe ser positiva");
        if (cantidad > energia)
            throw new RecursoInsuficienteException("No hay energia suficiente para consumir");
        energia-=cantidad;
    }
    /**
     * Descuenta combustible de los recursos.
     *
     * Precondiciones:
     * - cantidad es mayor que 0.
     * - cantidad no supera el combustible disponible.
     *
     * Postcondiciones:
     * - El combustible disminuye exactamente en cantidad y permanece entre 0 y 100.
     * - Si el consumo se rechaza, todos los recursos permanecen sin cambios.
     *
     * @param cantidad combustible a descontar
     * @throws IllegalArgumentException si cantidad es menor o igual a 0
     * @throws RecursoInsuficienteException si no hay ese combustible disponible
     */
    public void consumirCombustible(int cantidad) throws RecursoInsuficienteException {          //valida que no se haga negativo aunque los llamados en Mision lo chequeen , pero se confirma por si se llega a llamar desde otro lado que no valide
         if (cantidad <= 0)
            throw new IllegalArgumentException("La cantidad a consumir debe ser positiva");
        if (cantidad > combustible)
            throw new RecursoInsuficienteException("No hay combustible suficiente para consumir");
        combustible-=cantidad;
    }
    /**
     * Suma desgaste a los recursos.
     *
     * Precondiciones:
     * - cantidad es mayor que 0.
     * - cantidad no supera el margen restante: 100 - desgaste.
     *
     * Postcondiciones:
     * - El desgaste aumenta exactamente en cantidad y permanece entre 0 y 100.
     * - Si se rechaza, todos los recursos permanecen sin cambios.
     *
     * @param cantidad desgaste a sumar
     * @throws IllegalArgumentException si cantidad es menor o igual a 0
     * @throws LimiteRecursoExcedidoException si se superaria el limite de 100
     */
    public void agregarDesgaste(int cantidad) throws LimiteRecursoExcedidoException {
        if (cantidad <= 0)
            throw new IllegalArgumentException("La cantidad a desgastar debe ser positiva");
        if (cantidad > 100 - desgaste)
            throw new LimiteRecursoExcedidoException("Se llego al limite de desgaste");
        desgaste += cantidad;
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
        return desgaste >= 80;
    }

    /**
     * Realiza el mantenimiento de los recursos de la nave.
     *
     * Postcondiciones:
     * - El desgaste queda en 0.
     */
    public void realizarMantenimiento() {
        desgaste = 0;
    }



    /**
     * Aplica en una sola operacion el consumo de una mision.
     *
     * Precondiciones:
     * - Las tres cantidades son mayores o iguales a 0.
     * - Hay combustible y energia suficientes, y el desgaste resultante no supera 100.
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
     * @throws IllegalArgumentException si alguna cantidad es negativa
     * @throws RecursoInsuficienteException si falta combustible o energia
     * @throws LimiteRecursoExcedidoException si el desgaste superaria el limite de 100
     */
    public void consumirParaMision(int combustibleConsumido, int energiaConsumida, int desgasteAgregado)
            throws RecursoInsuficienteException, LimiteRecursoExcedidoException {
        verificarDisponibilidad(combustibleConsumido, energiaConsumida, desgasteAgregado);

        combustible -= combustibleConsumido;
        energia -= energiaConsumida;
        desgaste += desgasteAgregado;
    }

    /**
     * Comprueba si los recursos alcanzan para una operacion, sin aplicarla.
     *
     * Precondiciones:
     * - Las tres cantidades son mayores o iguales a 0.
     *
     * Postcondiciones:
     * - Ningun recurso se modifica, ni cuando la verificacion resulta exitosa.
     * - Si no lanza excepcion, consumirParaMision con las mismas cantidades tampoco lo hara.
     *
     * @param combustibleNecesario combustible que la operacion necesita
     * @param energiaNecesaria energia que la operacion necesita
     * @param desgasteAgregado desgaste que la operacion generaria
     * @throws IllegalArgumentException si alguna cantidad es negativa
     * @throws RecursoInsuficienteException si falta combustible o energia
     * @throws LimiteRecursoExcedidoException si el desgaste superaria el limite de 100
     */
    public void verificarDisponibilidad(int combustibleNecesario, int energiaNecesaria, int desgasteAgregado)
            throws RecursoInsuficienteException, LimiteRecursoExcedidoException {

        if (combustibleNecesario < 0 || energiaNecesaria < 0 || desgasteAgregado < 0) {
            throw new IllegalArgumentException("Las cantidades necesarias no pueden ser negativas");
        }

        if (combustibleNecesario > combustible) {
            throw new RecursoInsuficienteException("Combustible insuficiente");
        }

        if (energiaNecesaria > energia) {
            throw new RecursoInsuficienteException("Energía insuficiente");
        }

        if (desgasteAgregado > 100 - desgaste) {
            throw new LimiteRecursoExcedidoException(
                "El desgaste superaría el límite permitido"
            );
        }
    }
}

package ar.edu.unmdp.tpg.mision;

import ar.edu.unmdp.tpg.asistente.AsistenteDeComando;
import ar.edu.unmdp.tpg.excepciones.MisionNoViableException;
import ar.edu.unmdp.tpg.excepciones.NaveException;
import ar.edu.unmdp.tpg.excepciones.TransicionInvalidaException;
import ar.edu.unmdp.tpg.excepciones.TripulacionInvalidaException;
import ar.edu.unmdp.tpg.destino.Destino;
import ar.edu.unmdp.tpg.destino.Posicion;
import ar.edu.unmdp.tpg.destino.Tablero;
import ar.edu.unmdp.tpg.elementos.ElementoDelUniverso;


import java.util.ArrayList;
import java.util.List;

/**
 * Mision encomendada a un Asistente de Comando. Define el ciclo comun
 * preparar, ejecutar, evaluar y cerrar (Template Method).
 *
 * Contratos:
 * - Las precondiciones son obligacion del invocante y se comprueban con assert.
 * - Que la mision no sea viable (motor ocupado, tripulacion incompleta, falta de
 *   recursos, mision ya realizada) es un rechazo del dominio y se informa con
 *   MisionNoViableException.
 *
 * Invariantes (metodo invariante()):
 * - nombre, descripcion, asistente, tablero y fase no son nulos.
 * - Los acumuladores de consumo no son negativos.
 * - Solo hay informe a partir de la fase EVALUADA.
 */
public abstract class Mision{

    /**
     * Fases del ciclo de vida. Garantizan, mediante assert, que cada paso se
     * invoque en orden: no se ejecuta sin preparar, no se evalua sin ejecutar y
     * no se cierra sin resultado e informe.
     */
    private enum Fase { CREADA, PREPARADA, EJECUTADA, EVALUADA, CERRADA }

    private final String nombre;
    private final String descripcion;
    private int energiaConsumida = 0, combustibleConsumido = 0, desgasteAcumulado = 0; //acumuladores de energia y combustible
    protected final AsistenteDeComando asistente;
    private Fase fase = Fase.CREADA;
    private InformeMision informe;
    // Parte 2: tablero y travesía
    protected final Tablero tablero;            // protected: las subclases lo usan para elegir su destino
    private final Destino destino;              // el que mueve los elementos en el turno del tablero
    private final RadarLargoAlcance radarLargo = new RadarLargoAlcance();
    private final RadarCortoAlcance radarCorto = new RadarCortoAlcance();
    private List<Posicion> ruta = new ArrayList<>();
    private static int pausaEntreTurnos = 400;  // milisegundos
    private int combustibleAlPartir, energiaAlPartir, desgasteAlPartir;   // para el balance

    /**
     * Construye una misión
     *
     * La misión no conoce la nave ni la bitácora: todo lo que necesita
     * (consultar recursos, consumirlos, registrar lo ocurrido) se lo pide
     * al Asistente de Comando.
     *
     * Precondiciones (assert):
     * - nombre y descripcion no son nulos ni vacios.
     * - asistente y tablero no son nulos.
     *
     * Postcondiciones:
     * - La mision queda encomendada a ese asistente y en fase CREADA.
     *
     * @param nombre nombre identificatorio de la misión
     * @param descripcion descripción de la misión
     * @param asistente asistente al que se encomienda la misión
     * @param tablero tablero sobre el que se realiza la travesía
     */
    protected Mision(String nombre, String descripcion, AsistenteDeComando asistente, Tablero tablero) {
        assert nombre != null && !nombre.isBlank() : "El nombre de la misión no puede ser nulo ni vacío";
        assert descripcion != null && !descripcion.isBlank() : "La descripción de la misión no puede ser nula ni vacía";
        assert asistente != null : "La misión requiere un asistente de comando";
        assert tablero != null : "La misión requiere un tablero";

        this.nombre = nombre;
        this.descripcion = descripcion;
        this.asistente = asistente;
        this.tablero = tablero;
        this.destino = new Destino(tablero);

        assert invariante() : "Fallo invariante de Mision al construir";
    }

    /**
     * Invariante de clase de la mision.
     *
     * @return true si la mision esta en un estado valido
     */
    private boolean invariante() {
        return nombre != null && descripcion != null && asistente != null
            && tablero != null && fase != null
            && energiaConsumida >= 0 && combustibleConsumido >= 0 && desgasteAcumulado >= 0
            && (informe == null || fase == Fase.EVALUADA || fase == Fase.CERRADA);
    }

    /**
     * Indica si esta mision fue encomendada al asistente recibido. Una mision se
     * encomienda a un asistente en particular: si dos naves hacen el mismo tipo
     * de mision, son dos misiones distintas.
     *
     * @param otro asistente a comparar
     * @return true si la mision pertenece a ese asistente
     */
    public final boolean estaEncomendadaA(AsistenteDeComando otro) {
        return asistente == otro;
    }

    /**
     * Permite acelerar la simulación (por ejemplo, 0 en las pruebas por consola).
     *
     * Precondiciones (assert):
     * - milisegundos es mayor o igual a 0.
     *
     * @param milisegundos pausa entre turnos de la travesía
     */
    public static void setPausaEntreTurnos(int milisegundos) {
        assert milisegundos >= 0 : "La pausa no puede ser negativa";
        pausaEntreTurnos = milisegundos;
    }
    /**
     * Ejecuta el ciclo completo de la misión: preparar, ejecutar, evaluar y cerrar.
     *
     * PATRON TEMPLATE METHOD: este método es final, así las subclases no pueden
     * alterar el orden de las cuatro etapas. Lo que cada misión define es el
    * contenido de los puntos de extensión (destinoDeLaMision,
     * costoAccionFinal, descripcionObjetivo, combustibleNecesario y desgasteQueGenera),
     *
     * Las fallas del dominio suben al Asistente de Comando, que las registra.
     *
     * Condiciones de viabilidad (si no se cumplen, MisionNoViableException):
     * - La misión no fue ejecutada antes: una misión se realiza una sola vez.
     * - La nave no está destruida y su Motor Warp está Disponible.
     * - La tripulación está lista (Nave.tripulacionLista()).
     * - La nave dispone de los recursos que la misión va a necesitar.
     *
     * Postcondiciones:
     * - Si la preparación se completa, la misión queda realizada (fase CERRADA)
     *   y no puede volver a ejecutarse.
     * - Si la preparación falla, no se consume ningún recurso, el motor no cambia
     *   de estado y queda registrado el rechazo en la bitácora.
     * - Si la misión se ejecuta, se consumen el combustible y el desgaste del
     *   despegue; la acción final solo se cobra en el cierre de una misión exitosa.
     * - Si el resultado es exitoso, el motor recorre el ciclo completo de salto y
     *   vuelve a quedar Disponible; si es fallido, el motor no se usa.
     * - Si el ciclo llega a evaluarse, devuelve el informe, tanto en el caso
     *   exitoso como en el fallido.
     *
     * @return el informe de la misión ejecutada
     * @throws MisionNoViableException si la misión ya fue realizada o no puede prepararse
     * @throws NaveException si falla alguna operación del dominio durante el ciclo
     */
    public final InformeMision realizarMision() throws NaveException {
        if (fase != Fase.CREADA) {
            throw new MisionNoViableException(nombre + " ya fue realizada; para repetirla hay que crear otra misión");
        }
        preparar();                                 // CREADA    -> PREPARADA
        boolean exito = ejecutar();                 // PREPARADA -> EJECUTADA (dice si llegó bien)
        InformeMision resultado = evaluar(exito);   // EJECUTADA -> EVALUADA  (arma el informe)
        cerrar(exito);                              // EVALUADA  -> CERRADA   (impacta los recursos)

        assert fase == Fase.CERRADA : "La misión debe terminar cerrada";
        assert resultado != null && resultado.fueExitosa() == exito : "Fallo postcondición: informe inconsistente";
        return resultado;
    }


    /*metodos protected para que los hijos puedan accedrr */

    /**
     * Primera etapa del ciclo: comprueba que la misión sea viable antes de tocar
     * ningún recurso.
     *
     * Precondiciones (assert):
     * - La misión está en fase CREADA.
     *
     * Condiciones de viabilidad (si no se cumplen, MisionNoViableException):
     * - La nave no está destruida y el Motor Warp está Disponible.
     * - La tripulación está lista.
     * - La nave dispone del combustible, la energía y el margen de desgaste que
     *   la misión va a necesitar, sin quedar en nivel letal.
     *
     * Postcondiciones:
     * - Si se completa, la misión pasa a fase PREPARADA y queda calculada la ruta.
     * - No se modifica ningún recurso ni el estado del motor: esta etapa solo verifica.
     * - Si alguna condición no se cumple, el motivo queda registrado en la bitácora
     *   y la misión no continúa.
     * - Si se completa, queda registrada la acción "Preparación completada".
     *
     * @throws MisionNoViableException rechazo de dominio: el motor no está
     *         disponible, falta tripulación o no alcanzan los recursos
     */
    protected final void preparar() throws MisionNoViableException {
        assert fase == Fase.CREADA : "La misión solo puede prepararse una vez";
        try {
            if (asistente.naveDestruida()) {
                throw new MisionNoViableException("la nave está destruida");
            }
            if (!asistente.motorDisponible()) {
                throw new MisionNoViableException(
                    "el Motor Warp no está disponible (estado: " + asistente.estadoDelMotor() + ")");
            }
            if (!asistente.tripulacionLista()) {
                throw new TripulacionInvalidaException(
                    "la tripulación no está lista: se requiere un capitán y cuatro tripulantes adicionales");
            }

            asistente.verificarRecursos(combustibleNecesario() + combustibleAEntregar(), costoAccionFinal(), desgasteQueGenera());    
            //  no se puede dejar a la nave en un nivel letal (combustible 0, energía 0 o desgaste 100)
            if (asistente.combustibleDisponible() - combustibleNecesario() - combustibleAEntregar() <= 0
                    || asistente.energiaDisponible() - costoAccionFinal() <= 0
                    || asistente.desgasteActual() + desgasteQueGenera() >= 100) {
                throw new MisionNoViableException("la misión dejaría a la nave sin recursos");
            }        
            // Radar de largo alcance: calcula y guarda la ruta hasta el objetivo
            Posicion objetivo = destinoDeLaMision();
            ruta = radarLargo.calcularRuta(tablero.getPosicionNave(), objetivo);
            registrarAccion("Ruta calculada hacia " + texto(objetivo) + ": " + ruta.size() + " saltos");
            registrarAccion("Preparación completada");
            fase = Fase.PREPARADA;
        } catch (NaveException error) {
            asistente.registrarEvento("MISION", nombre + ": preparación rechazada: " + error.getMessage());

            throw new MisionNoViableException(
                "No se puede iniciar " + nombre + ": " + error.getMessage()
            );
        }
    }
   /**
    * Segunda etapa del ciclo: cobra el costo de la operación y delega en la
    * misión concreta lo que esa misión hace.
    *
    * Precondiciones (assert):
    * - La misión está en fase PREPARADA: no se ejecuta sin preparación previa.
    *
    * Postcondiciones:
    * - El combustible baja y el desgaste sube en las cantidades del despegue, y
    *   quedan acumulados para el informe; la misión pasa a fase EJECUTADA.
    * - La nave recorre la ruta turno a turno hasta llegar o ser destruida.
    * - Devuelve true si llegó al destino y conserva lo necesario para la acción final.
    *
    * @return si la nave llegó al destino en condiciones de completar la misión
    * @throws NaveException rechazo de dominio: el consumo del despegue no es aplicable
    */
   protected final boolean ejecutar() throws NaveException {
        assert fase == Fase.PREPARADA : "Una misión no puede ejecutarse sin preparación previa";
        // Foto de los recursos al partir, para el balance del informe
        combustibleAlPartir = asistente.combustibleDisponible();
        energiaAlPartir = asistente.energiaDisponible();
        desgasteAlPartir = asistente.desgasteActual();
        // 1. Consumo de despegue
        asistente.consumirParaMision(combustibleNecesario(), 0, desgasteQueGenera());
        combustibleConsumido += combustibleNecesario();
        desgasteAcumulado += desgasteQueGenera();
        fase = Fase.EJECUTADA;

        // 2. Ciclo de travesía
        boolean llego = viajar();
        if (!llego) {
            return false;
        }

        // 3. Verificación en destino
        return verificarEnDestino();
   }

    // Bucle por turnos: mientras la nave siga viva y no haya llegado
   private boolean viajar() {
        int turno = 0;
        while (!asistente.naveDestruida() && !ruta.isEmpty()) {
            turno++;

            // A. Turno de la nave
            // A.1 Inspección de la posición actual
            ElementoDelUniverso elemento = tablero.inspeccionarPosicion();
            if (elemento != null) {
                registrarAccion("Turno " + turno + ": " + asistente.interactuarCon(elemento));
                if (asistente.naveDestruida()) {
                    break;   // la misión falla de inmediato
                }
            }
            // A.2 Escaneo con el radar de corto alcance
            int cercanos = radarCorto.contarElementosCercanos(tablero);
            // A.3 Planificación y movimiento: siguiente casilla de la ruta
            Posicion siguiente = ruta.remove(0);
            tablero.moverNave(siguiente);
            registrarAccion("Turno " + turno + ": salto a " + texto(siguiente)
                    + " (radar: " + cercanos + " elementos cerca)");

            // B. Turno del tablero
            destino.turnoDestino();

            // Pausa para poder ver el avance en la vista
            try {
                Thread.sleep(pausaEntreTurnos);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                registrarAccion("Travesía interrumpida");
                return false;
            }
        }

        if (asistente.naveDestruida()) {
            registrarAccion("La nave fue destruida: misión fallida");
            return false;
        }
        return true;
   }

   //  Verifica y revisa el combustibles
   private boolean verificarEnDestino() {
        // Lo que le quedaría a la nave después de entregar/gastar en el cierre
        int energiaQueQueda = asistente.energiaDisponible() - costoAccionFinal();
        int combustibleQueQueda = asistente.combustibleDisponible() - combustibleAEntregar();
        if (energiaQueQueda <= 0 || combustibleQueQueda <= 0) {
            registrarAccion("Al llegar no quedan recursos suficientes: la acción final dejaría la nave sin energía o sin combustible");
            return false;
        }
        registrarAccion("Destino alcanzado: recursos verificados");
        alLlegar();
        return true;
   }
   /**
     * Tercera etapa del ciclo: confecciona el informe final con el resultado y el
     * balance de recursos. No modifica la nave: el impacto se aplica en el cierre.
     *
     * Precondiciones (assert):
     * - La misión está en fase EJECUTADA.
     *
     * Postcondiciones:
     * - Devuelve un informe no nulo cuyo resultado coincide con exito.
     * - Si exito es true, el informe ya incluye lo que se gastará o entregará en el cierre.
     * - La misión pasa a fase EVALUADA.
     *
     * @param exito resultado de la ejecución
     * @return el informe final de la misión
     */
    protected final InformeMision evaluar(boolean exito) {
        assert fase == Fase.EJECUTADA : "Una misión no puede evaluarse sin haberse ejecutado";
        registrarAccion("Balance del trayecto: " + balanceDelTrayecto());
        if (exito) {
            energiaConsumida += costoAccionFinal();        // lo que se va a gastar o entregar en el cierre
            combustibleConsumido += combustibleAEntregar();
        }
        informe = asistente.crearInforme(
            nombre,
            descripcion,
            descripcionObjetivo(),
            acciones,
            combustibleConsumido,
            energiaConsumida,
            desgasteAcumulado,
            exito
        );
        fase = Fase.EVALUADA;

        assert informe != null && informe.fueExitosa() == exito : "Fallo postcondición de evaluar";
        assert invariante() : "Fallo invariante de Mision";
        return informe;
    }
    private final List<String> acciones = new ArrayList<>();

    /**
     * Deja constancia de una acción tanto en el informe como en la Bitácora,
     * a través del Asistente de Comando.
     *
     * Precondiciones (assert):
     * - detalle no es nulo, vacío ni contiene solo espacios.
     *
     * Postcondiciones:
     * - La acción queda al final de la lista de acciones de la misión y las
     *   anteriores permanecen en el mismo orden.
     * - Queda registrado un evento de categoría MISION en la bitácora.
     *
     * @param detalle descripción de la acción realizada
     */
    protected final void registrarAccion(String detalle) {
        assert detalle != null && !detalle.isBlank() : "El detalle de la acción no puede ser nulo ni vacío";
        int accionesAntes = acciones.size();
        acciones.add(detalle);
        assert acciones.size() == accionesAntes + 1 : "Fallo postcondición de registrarAccion";
        asistente.registrarEvento("MISION", nombre + ": " + detalle);
    }


    /**
     * Cuarta y última etapa del ciclo: impacta en la nave lo ganado o entregado,
     * hace saltar la nave si la misión salió bien y deja constancia del resultado.
     *
     * Precondiciones (assert):
     * - La misión está en fase EVALUADA y ya tiene informe: no se cierra sin
     *   resultado e informe.
     *
     * Postcondiciones:
     * - Queda registrado en la bitácora el cierre de la misión y su resultado.
     * - Si exito es true, se descuenta lo entregado o gastado en la acción final,
     *   se aplica lo ganado y el motor recorre el ciclo de salto hasta quedar
     *   Disponible; si es false, la nave no se modifica.
     * - La misión pasa a fase CERRADA.
     *
     * @param exito resultado devuelto por la evaluación
     * @throws NaveException rechazo de dominio durante el impacto final o el salto
     */
    protected final void cerrar(boolean exito) throws NaveException {
        assert fase == Fase.EVALUADA && informe != null : "Una misión no puede cerrarse sin resultado e informe";
        if (exito) {
            if (costoAccionFinal() > 0 || combustibleAEntregar() > 0) {
                asistente.consumirParaMision(combustibleAEntregar(), costoAccionFinal(), 0);   // recién acá se descuenta
            }
            impactarAlCerrar();
            saltar();
        }
        asistente.registrarEvento("MISION", nombre + " finalizada: " + (exito ? "EXITOSA" : "FALLIDA"));
        fase = Fase.CERRADA;

        assert !exito || asistente.motorDisponible() : "Tras un salto exitoso el motor debe quedar Disponible";
        assert invariante() : "Fallo invariante de Mision";
    }

    /**
     * Hace saltar a la nave al completarse la misión con éxito, recorriendo el
     * ciclo del Motor Warp de punta a punta.
     *
     * Como todavía no se modela el paso del tiempo, el enfriamiento se completa
     * en el momento y la nave queda nuevamente Disponible.
     *
     * Precondiciones (assert):
     * - El motor está en estado Disponible (lo verificó preparar()).
     *
     * Postcondiciones:
     * - El motor pasa por Preparando salto, En warp y Enfriamiento, y termina en Disponible.
     * - Las cuatro transiciones quedan registradas en la bitácora.
     *
     * @throws TransicionInvalidaException si alguna de las cuatro transiciones es rechazada
     */
    private void saltar() throws TransicionInvalidaException {
        assert asistente.motorDisponible() : "Para saltar el motor debe estar Disponible";
        asistente.prepararSalto();
        asistente.iniciarSalto();
        asistente.finalizarSalto();
        asistente.completarEnfriamiento();
    }
    /**
     * Punto de extensión: energía que cuesta la acción final de esta misión.
     *
     * Postcondiciones:
     * - Devuelve un valor mayor o igual a 0. Devolver 0 significa que la misión
     *   no tiene costo de acción final.
     * - La implementación no modifica el estado de la nave.
     *
     * @return energía que cuesta la acción final (0 si no tiene costo)
     */
    protected abstract int costoAccionFinal();
    /**
     * Punto de extensión: describe el objetivo que persigue esta misión.
     *
     * Postcondiciones:
     * - Devuelve un texto no nulo ni vacío, que se incluye en el informe.
     * - La implementación no modifica el estado de la nave.
     *
     * @return descripción del objetivo de la misión
     */
    protected abstract String descripcionObjetivo();

    /**
     * Punto de extensión: casilla del tablero a la que tiene que llegar esta misión.
     *
     * Precondiciones:
     * - Se invoca una sola vez, durante la preparación.
     *
     * Postcondiciones:
     * - Devuelve una posición dentro del tablero.
     * - La misión calcula la ruta desde la posición actual de la nave hasta esta.
     *
     * @return posición de destino de la misión
     */
    protected abstract Posicion destinoDeLaMision();
    /**
     * Punto de extensión: combustible que consume la operación de esta misión.
     * Por defecto, 4, como indica la ficha de inicio de la Etapa 1.
     *
     * Postcondiciones:
     * - Devuelve un valor mayor o igual a 0.
     * - La implementación no modifica el estado de la nave.
     *
     * @return combustible que consume la operación
     */
    protected int combustibleNecesario() {
        return 4;
    }
    /**
     * Punto de extensión: desgaste que genera la operación de esta misión.
     * Por defecto, 4, como indica la ficha de inicio de la Etapa 1.
     *
     * Postcondiciones:
     * - Devuelve un valor mayor o igual a 0.
     * - La implementación no modifica el estado de la nave.
     *
     * @return desgaste que genera la operación
     */
    protected int desgasteQueGenera() {
        return 4;
    }

    // ---------- cada misión los redefine solo si los necesita ----------

    // Combustible que la misión entrega al llegar (por defecto, nada)
    protected int combustibleAEntregar() {
        return 0;
    }

    // Se invoca al verificar la llegada: la misión anota qué va a ganar o entregar
    protected void alLlegar() {
    }

    // Se invoca en el cierre: la misión impacta lo que gana (gemas, recarga, reparación)
    protected void impactarAlCerrar() throws NaveException {
    }

    private String balanceDelTrayecto() {
        return "combustible " + conSigno(asistente.combustibleDisponible() - combustibleAlPartir)
            + ", energía " + conSigno(asistente.energiaDisponible() - energiaAlPartir)
            + ", desgaste " + conSigno(asistente.desgasteActual() - desgasteAlPartir);
    }

    private static String conSigno(int n) {
        return (n > 0 ? "+" : "") + n;
    }

    private static String texto(Posicion p) {
        return "(" + p.getX() + "," + p.getY() + ")";
    }

}

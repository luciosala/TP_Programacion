package ar.edu.unmdp.tpg.mision;

import ar.edu.unmdp.tpg.asistente.AsistenteDeComando;
import ar.edu.unmdp.tpg.destino.Destino;
import ar.edu.unmdp.tpg.destino.Posicion;
import ar.edu.unmdp.tpg.destino.Tablero;
import ar.edu.unmdp.tpg.elementos.ElementoDelUniverso;
import ar.edu.unmdp.tpg.excepciones.MisionNoViableException;
import ar.edu.unmdp.tpg.excepciones.NaveException;
import ar.edu.unmdp.tpg.excepciones.RecursoInsuficienteException;
import ar.edu.unmdp.tpg.excepciones.TripulacionInvalidaException;
import ar.edu.unmdp.tpg.motorwarp.MotorWarp;

import java.util.ArrayList;
import java.util.List;

/**
 * Misión encomendada a un Asistente de Comando. Define el ciclo común
 * preparar, ejecutar, evaluar y cerrar (patrón Template Method).
 *
 * La misión no conoce la nave ni la bitácora: todo lo que necesita (consultar
 * recursos, consumirlos, saltar, registrar lo ocurrido) se lo pide al asistente.
 *
 * Contratos:
 * - Las precondiciones son obligación del invocante y se comprueban con assert.
 * - Que la misión no sea viable (motor ocupado, tripulación incompleta, falta de
 *   recursos, misión ya realizada) es un rechazo del dominio y se informa con
 *   MisionNoViableException.
 *
 * Invariantes (método invariante()):
 * - nombre, descripcion, asistente, tablero, balance y fase no son nulos.
 * - Solo hay informe a partir de la fase EVALUADA.
 */
public abstract class Mision {

    /**
     * Fases del ciclo de vida. Garantizan, mediante assert, que cada paso se
     * invoque en orden: no se ejecuta sin preparar, no se evalúa sin ejecutar y
     * no se cierra sin resultado e informe.
     */
    private enum Fase { CREADA, PREPARADA, EJECUTADA, EVALUADA, CERRADA }

    /** Turnos que la nave acepta esperar al warp antes de avanzar con un salto simple. */
    public static final int MAXIMA_ESPERA_WARP = 4;

    private final String nombre;
    private final String descripcion;
    protected final AsistenteDeComando asistente;
    protected final Tablero tablero;            // protected: las subclases lo usan para elegir su destino
    private final Destino destino;              // el que mueve los elementos en el turno del tablero
    private final RadarLargoAlcance radarLargo = new RadarLargoAlcance();
    private final RadarCortoAlcance radarCorto = new RadarCortoAlcance();
    private final BalanceDeRecursos balance = new BalanceDeRecursos();
    private final List<String> acciones = new ArrayList<>();
    private List<Posicion> ruta = new ArrayList<>();
    private Fase fase = Fase.CREADA;
    private InformeMision informe;
    private static int pausaEntreTurnos = 400;  // milisegundos

    /**
     * Construye una misión.
     *
     * Precondiciones (assert):
     * - nombre y descripcion no son nulos ni vacíos.
     * - asistente y tablero no son nulos.
     *
     * Postcondiciones:
     * - La misión queda encomendada a ese asistente y en fase CREADA.
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

    private boolean invariante() {
        return nombre != null && descripcion != null && asistente != null
            && tablero != null && balance != null && fase != null
            && (informe == null || fase == Fase.EVALUADA || fase == Fase.CERRADA);
    }

    /** @return nombre de la misión */
    public final String getNombre() {
        return nombre;
    }

    /**
     * Indica si esta misión fue encomendada al asistente recibido. Si dos naves
     * hacen el mismo tipo de misión, son dos misiones distintas.
     *
     * @param otro asistente a comparar
     * @return true si la misión pertenece a ese asistente
     */
    public final boolean estaEncomendadaA(AsistenteDeComando otro) {
        return asistente == otro;
    }

    /**
     * Permite cambiar la pausa entre turnos (por ejemplo, 0 en las pruebas por consola).
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
     * alterar el orden de las cuatro etapas. Lo que cada misión define son los
     * puntos de extensión (destinoDeLaMision, descripcionObjetivo, costos de
     * despegue, recursos a entregar, alLlegar e impactarAlCerrar).
     *
     * Postcondiciones:
     * - Si la preparación falla, no se consume ningún recurso y el rechazo queda
     *   registrado en la bitácora.
     * - Si la misión se ejecuta, termina CERRADA y devuelve el informe, tanto si
     *   fue exitosa como si fracasó.
     *
     * @return el informe de la misión
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

    /**
     * Fase 1, Preparar: verificación inicial y cálculo de ruta.
     *
     * Precondiciones (assert):
     * - La misión está en fase CREADA.
     *
     * Condiciones de viabilidad (si no se cumplen, MisionNoViableException):
     * - La nave no está destruida y el Motor Warp está Disponible.
     * - La tripulación está lista.
     * - La nave tiene los recursos del despegue y no queda en nivel letal al pagarlos.
     *
     * Postcondiciones:
     * - Si se completa, la ruta calculada con el radar de largo alcance queda
     *   guardada y la misión pasa a PREPARADA. No se modifica ningún recurso.
     * - Si no se completa, el motivo queda registrado en la bitácora.
     *
     * @throws MisionNoViableException rechazo de dominio
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
            // Verificación inicial: recursos mínimos para iniciar (el despegue)
            asistente.verificarRecursos(combustibleDespegue(), energiaDespegue(), desgasteDespegue());
            if (asistente.combustibleDisponible() - combustibleDespegue() <= 0
                    || asistente.energiaDisponible() - energiaDespegue() <= 0
                    || asistente.desgasteActual() + desgasteDespegue() >= 100) {
                throw new MisionNoViableException("el despegue dejaría a la nave sin recursos");
            }
            // Cálculo de ruta con el radar de largo alcance
            Posicion objetivo = destinoDeLaMision();
            ruta = radarLargo.calcularRuta(tablero.getPosicionNave(), objetivo);
            registrarAccion("Ruta calculada hacia " + texto(objetivo) + " (" + ruta.size() + " casilleros): " + textoRuta());
            registrarAccion("Preparación completada");
            fase = Fase.PREPARADA;
        } catch (NaveException error) {
            asistente.registrarEvento("MISION", nombre + ": preparación rechazada: " + error.getMessage());
            throw new MisionNoViableException("No se puede iniciar " + nombre + ": " + error.getMessage());
        }
    }

    /**
     * Fase 2, Ejecutar: consumo de despegue, ciclo de travesía por turnos y
     * verificación en destino.
     *
     * Precondiciones (assert):
     * - La misión está en fase PREPARADA: no se ejecuta sin preparación previa.
     *
     * Postcondiciones:
     * - Se descuenta el despegue y la misión pasa a EJECUTADA.
     * - La nave recorre la ruta turno a turno hasta llegar, ser destruida o quedar varada.
     * - Devuelve true si llegó y conserva lo que debe entregar en el cierre.
     *
     * @return si la nave llegó al destino en condiciones de completar la misión
     * @throws NaveException rechazo de dominio: el despegue no es aplicable
     */
    protected final boolean ejecutar() throws NaveException {
        assert fase == Fase.PREPARADA : "Una misión no puede ejecutarse sin preparación previa";
        // Consumo de despegue
        asistente.consumirParaMision(combustibleDespegue(), energiaDespegue(), desgasteDespegue());
        balance.sumarDespegue(combustibleDespegue(), energiaDespegue(), desgasteDespegue());
        registrarAccion("Despegue: -" + combustibleDespegue() + " combustible, -" + energiaDespegue()
            + " energía, +" + desgasteDespegue() + " desgaste");
        fase = Fase.EJECUTADA;

        // Ciclo de travesía
        if (!viajar()) {
            return false;
        }
        // Verificación en destino
        return verificarEnDestino();
    }

    /**
     * Ciclo de travesía: mientras la nave siga con vida y no haya llegado, cada
     * turno tiene dos etapas. Si llegó con un salto warp, se agrega el turno de
     * enfriamiento del motor, para que quede Disponible para la próxima misión.
     * A. Turno de la nave: inspección de la posición actual, escaneo con el radar
     *    de corto alcance y planificación y movimiento (salto simple o warp).
     * B. Turno del tablero: Destino mueve los elementos del universo.
     *
     * @return true si la nave llegó al destino; false si fue destruida, quedó
     *         varada o se interrumpió la travesía
     */
    private boolean viajar() {
        int turno = 0;
        int turnosSinAvanzar = 0;
        ElementoDelUniverso ultimoEncuentro = null;   // para no repetir el encuentro si la nave no se movió
        Posicion lugarUltimoEncuentro = null;

        // Sigue mientras falte camino o el motor tenga que terminar de enfriarse
        // (si el salto warp llegó justo al destino, el enfriamiento ocupa un turno más).
        while (!asistente.naveDestruida() && (!ruta.isEmpty() || !asistente.motorDisponible())) {
            turno++;
            if (ruta.isEmpty()) {
                int desgasteAntes = asistente.desgasteActual();
                asistente.saltarWarp();   // en Enfriamiento, cualquier salto se usa para enfriar
                balance.sumarNavegacion(0, 0, asistente.desgasteActual() - desgasteAntes);
                registrarAccion("Turno " + turno + ": ya en destino, el Motor Warp se enfría (+"
                    + (asistente.desgasteActual() - desgasteAntes) + " de desgaste)");
                destino.turnoDestino();
                continue;
            }

            // A.1 Inspección de la posición actual
            ElementoDelUniverso elemento = tablero.inspeccionarPosicion();
            Posicion aqui = tablero.getPosicionNave();
            boolean yaEncontrado = elemento == ultimoEncuentro && aqui.equals(lugarUltimoEncuentro);
            if (elemento != null && !yaEncontrado) {
                int combustibleAntes = asistente.combustibleDisponible();
                int energiaAntes = asistente.energiaDisponible();
                registrarAccion("Turno " + turno + ": " + asistente.interactuarCon(elemento));
                balance.sumarEncuentro(asistente.combustibleDisponible() - combustibleAntes,
                                       asistente.energiaDisponible() - energiaAntes);
                ultimoEncuentro = elemento;
                lugarUltimoEncuentro = aqui;
                if (asistente.naveDestruida()) {
                    break;   // la misión falla de inmediato
                }
            }

            // A.2 Escaneo con el radar de corto alcance
            Posicion siguiente = ruta.get(0);
            boolean obstaculo = radarCorto.hayObstaculoEn(tablero, siguiente);

            // A.3 Planificación y movimiento
            int casilleros;
            int combustibleAntes = asistente.combustibleDisponible();
            int energiaAntes = asistente.energiaDisponible();
            int desgasteAntes = asistente.desgasteActual();
            try {
                casilleros = mover(obstaculo, turnosSinAvanzar);
            } catch (RecursoInsuficienteException sinCombustible) {
                registrarAccion("Turno " + turno + ": no hay combustible para saltar, la nave queda varada");
                return false;
            }
            int desgasteDelTurno = asistente.desgasteActual() - desgasteAntes;
            balance.sumarNavegacion(asistente.combustibleDisponible() - combustibleAntes,
                                    asistente.energiaDisponible() - energiaAntes,
                                    desgasteDelTurno);
            if (casilleros == 0) {
                turnosSinAvanzar++;
                if (desgasteDelTurno > 0) {
                    registrarAccion("Turno " + turno + ": el Motor Warp se enfría (+" + desgasteDelTurno
                        + " de desgaste), la nave no avanza");
                } else {
                    registrarAccion("Turno " + turno + ": obstáculo adelante, la nave espera (Motor Warp: "
                        + asistente.estadoDelMotor() + ")");
                }
            } else {
                turnosSinAvanzar = 0;
                Posicion llegada = null;
                for (int i = 0; i < casilleros && !ruta.isEmpty(); i++) {
                    llegada = ruta.remove(0);
                }
                tablero.moverNave(llegada);
                String tipoSalto = casilleros == 2 ? "salto warp, esquivando el obstáculo," : "salto";
                registrarAccion("Turno " + turno + ": " + tipoSalto + " a " + texto(llegada)
                    + " (radar: " + radarCorto.contarElementosCercanos(tablero) + " elementos cerca)");
            }

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
            registrarAccion("La nave fue destruida junto con su tripulación: misión fallida");
            return false;
        }
        return true;
    }

    /**
     * Decide el movimiento del turno (Anexo, Navegación y Motor Warp):
     * - Si el radar detectó un obstáculo en la próxima casilla, intenta el salto
     *   warp, siempre que la nave conserve más energía que la que consume el salto
     *   (así no queda en 0) y no lleve demasiados turnos esperando.
     * - Si no, salto simple. Si el motor estaba preparando el warp, el salto simple
     *   cancela la preparación; si estaba enfriando, el turno se usa para enfriar.
     */
    private int mover(boolean obstaculo, int turnosSinAvanzar) throws RecursoInsuficienteException {
        boolean conviene = obstaculo
            && asistente.energiaDisponible() > MotorWarp.ENERGIA_SALTO_WARP
            && turnosSinAvanzar < MAXIMA_ESPERA_WARP;
        if (conviene) {
            return asistente.saltarWarp();
        }
        return asistente.saltar();
    }

    /**
     * Verificación en destino: si la misión entrega recursos, la nave tiene que
     * conservarlos después del viaje (y no quedar en 0 al entregarlos). El
     * descuento se hace en el cierre.
     */
    private boolean verificarEnDestino() {
        int energiaQueQueda = asistente.energiaDisponible() - energiaAEntregar();
        int combustibleQueQueda = asistente.combustibleDisponible() - combustibleAEntregar();
        if (energiaQueQueda <= 0 || combustibleQueQueda <= 0) {
            registrarAccion("Destino alcanzado, pero la nave ya no conserva los recursos a entregar ("
                + combustibleAEntregar() + " combustible y " + energiaAEntregar() + " energía)");
            return false;
        }
        registrarAccion("Destino alcanzado: recursos verificados");
        alLlegar();
        return true;
    }

    /**
     * Fase 3, Evaluar: confecciona el Informe Final de Misión con el resultado y
     * el balance detallado de recursos. No modifica la nave.
     *
     * Precondiciones (assert):
     * - La misión está en fase EJECUTADA.
     *
     * Postcondiciones:
     * - Devuelve un informe no nulo cuyo resultado coincide con exito.
     * - Si exito es true, el balance ya incluye lo que se entregará en el cierre.
     * - La misión pasa a EVALUADA.
     *
     * @param exito resultado de la ejecución
     * @return el informe final de la misión
     */
    protected final InformeMision evaluar(boolean exito) {
        assert fase == Fase.EJECUTADA : "Una misión no puede evaluarse sin haberse ejecutado";
        if (exito) {
            balance.registrarEntrega(combustibleAEntregar(), energiaAEntregar());
        }
        informe = asistente.crearInforme(nombre, descripcion, descripcionObjetivo(), acciones, balance, exito);
        fase = Fase.EVALUADA;

        assert informe != null && informe.fueExitosa() == exito : "Fallo postcondición de evaluar";
        assert invariante() : "Fallo invariante de Mision";
        return informe;
    }

    /**
     * Fase 4, Cerrar: impacta de forma definitiva en la nave lo entregado o ganado.
     *
     * Precondiciones (assert):
     * - La misión está en fase EVALUADA y ya tiene informe.
     *
     * Postcondiciones:
     * - Si exito es true, se descuenta lo entregado y se aplica lo ganado.
     * - Si exito es false, la nave no se modifica.
     * - Queda registrado el cierre y la misión pasa a CERRADA.
     *
     * @param exito resultado de la misión
     * @throws NaveException rechazo de dominio durante el impacto final
     */
    protected final void cerrar(boolean exito) throws NaveException {
        assert fase == Fase.EVALUADA && informe != null : "Una misión no puede cerrarse sin resultado e informe";
        if (exito) {
            if (combustibleAEntregar() > 0 || energiaAEntregar() > 0) {
                asistente.consumirParaMision(combustibleAEntregar(), energiaAEntregar(), 0);
            }
            impactarAlCerrar();
        }
        asistente.registrarEvento("MISION", nombre + " finalizada: " + (exito ? "EXITOSA" : "FALLIDA"));
        fase = Fase.CERRADA;
        assert invariante() : "Fallo invariante de Mision";
    }

    /**
     * Deja constancia de una acción en el informe y en la bitácora.
     *
     * Precondiciones (assert):
     * - detalle no es nulo ni vacío.
     *
     * @param detalle descripción de la acción realizada
     */
    protected final void registrarAccion(String detalle) {
        assert detalle != null && !detalle.isBlank() : "El detalle de la acción no puede ser nulo ni vacío";
        acciones.add(detalle);
        asistente.registrarEvento("MISION", nombre + ": " + detalle);
    }

    /** @return el balance de recursos, para que las misiones concretas anoten su acción final */
    protected final BalanceDeRecursos balance() {
        return balance;
    }

    // ---------- Puntos de extensión del Template Method ----------

    /**
     * Casilla del tablero a la que tiene que llegar esta misión. Se invoca una
     * sola vez, durante la preparación.
     *
     * @return posición de destino, dentro del tablero
     */
    protected abstract Posicion destinoDeLaMision();

    /** @return descripción del objetivo, no vacía, para el informe */
    protected abstract String descripcionObjetivo();

    /** @return energía que consume el despegue de esta misión (Anexo) */
    protected abstract int energiaDespegue();

    /** @return combustible que consume el despegue: 4 en todas las misiones (Anexo) */
    protected int combustibleDespegue() {
        return 4;
    }

    /** @return desgaste que genera el despegue: 4 en todas las misiones (Anexo) */
    protected int desgasteDespegue() {
        return 4;
    }

    /** @return combustible que la misión entrega al llegar (por defecto, nada) */
    protected int combustibleAEntregar() {
        return 0;
    }

    /** @return energía que la misión entrega al llegar (por defecto, nada) */
    protected int energiaAEntregar() {
        return 0;
    }

    /** Se invoca al verificar la llegada: la misión anota qué va a ganar o entregar. */
    protected void alLlegar() {
    }

    /** Se invoca en el cierre de una misión exitosa: la misión impacta lo que gana. */
    protected void impactarAlCerrar() throws NaveException {
    }

    // ---------- Auxiliares ----------

    private String textoRuta() {
        StringBuilder sb = new StringBuilder();
        for (Posicion p : ruta) {
            sb.append(texto(p)).append(' ');
        }
        return sb.toString().trim();
    }

    private static String texto(Posicion p) {
        return "(" + p.getX() + "," + p.getY() + ")";
    }
}

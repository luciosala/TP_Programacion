package ar.edu.unmdp.tpg.aplicacion;

import ar.edu.unmdp.tpg.asistente.AsistenteDeComando;
import ar.edu.unmdp.tpg.asistente.AsistenteDeComandoBasico;
import ar.edu.unmdp.tpg.bitacora.Bitacora;
import ar.edu.unmdp.tpg.destino.CantidadDeElementosSuperaCasillaException;
import ar.edu.unmdp.tpg.destino.DimensionesDeTableroInconsistentesException;
import ar.edu.unmdp.tpg.destino.Observable;
import ar.edu.unmdp.tpg.destino.Observador;
import ar.edu.unmdp.tpg.destino.Posicion;
import ar.edu.unmdp.tpg.destino.Tablero;
import ar.edu.unmdp.tpg.elementos.ElementoDelUniverso;
import ar.edu.unmdp.tpg.elementos.FabricaDeElementos;
import ar.edu.unmdp.tpg.excepciones.LimiteRecursoExcedidoException;
import ar.edu.unmdp.tpg.excepciones.MisionNoViableException;
import ar.edu.unmdp.tpg.excepciones.NaveNoRegistradaException;
import ar.edu.unmdp.tpg.excepciones.NaveYaRegistradaException;
import ar.edu.unmdp.tpg.excepciones.TripulacionInvalidaException;
import ar.edu.unmdp.tpg.haberes.Haber;
import ar.edu.unmdp.tpg.mision.InformeMision;
import ar.edu.unmdp.tpg.mision.Mision;
import ar.edu.unmdp.tpg.mision.MisionIntercepcion;
import ar.edu.unmdp.tpg.mision.MisionRecoleccion;
import ar.edu.unmdp.tpg.mision.MisionRetornoSeguro;
import ar.edu.unmdp.tpg.naves.NaveFactory;
import ar.edu.unmdp.tpg.tripulacion.FabricaDeCargos;
import ar.edu.unmdp.tpg.tripulacion.Origen;
import ar.edu.unmdp.tpg.tripulacion.Tripulante;
import ar.edu.unmdp.tpg.universo.Universo;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Modelo de la aplicación del MVC (Clase 13: "modelo de la aplicación" o
 * "coordinador"): se relaciona con el modelo del dominio (Universo, asistentes,
 * naves, tableros, misiones) y notifica a las vistas los cambios, sin conocerlas.
 *
 * Patrón Observer (Clase 11):
 * - Observa la bitácora y el tablero de la nave activa (con dos clases internas)
 *   y reenvía cada aviso a sus propios observadores, las vistas.
 * - Cada vez que el tablero avisa un cambio, guarda una foto del tablero. Las
 *   vistas consultan esa foto y nunca recorren el tablero mientras la misión lo
 *   modifica desde otro hilo.
 *
 * Concurrencia (Clase 10): las misiones se ejecutan en su propio hilo
 * (EjecucionDeMision), para que la interfaz no se congele durante el Thread.sleep
 * de cada turno. Solo puede haber una misión en curso a la vez. Los datos que
 * comparten el hilo de la misión y el de la interfaz se acceden con métodos
 * synchronized.
 *
 * Invariante: si hay una misión en curso, hay una nave activa.
 */
public class ModeloAplicacion extends Observable {

    /** Tamaño del tablero (Guía general del TPG: tablero de 20 x 20). */
    public static final int FILAS = 20;
    public static final int COLUMNAS = 20;
    /** Elementos de cada universo que se crea para una nave. */
    public static final int ASTEROIDES = 30;
    public static final int ESTACIONES = 8;
    public static final int NAVES_ENEMIGAS = 4;

    private final Universo universo = new Universo();
    private final HashMap<String, Tablero> tableros = new HashMap<>();
    private final List<InformeMision> informes = new ArrayList<>();
    private final ObservadorDeBitacora observadorDeBitacora = new ObservadorDeBitacora();
    private final ObservadorDeTablero observadorDeTablero = new ObservadorDeTablero();
    private boolean misionEnCurso = false;
    private Mision misionActual;   // la que se está ejecutando, para mostrar su objetivo y su ruta

    // Foto del tablero de la nave activa, tomada en cada aviso del tablero
    private ElementoDelUniverso[][] fotoTablero;
    private Posicion fotoNave;
    private Posicion fotoObjetivo;
    private List<Posicion> fotoRuta = new ArrayList<>();

    // ---------- Naves ----------

    /**
     * Crea una nave con NaveFactory, su asistente y su bitácora, la registra en
     * el Universo con un tablero propio y la deja como nave activa.
     *
     * Precondiciones (assert):
     * - id no es nulo ni vacío; tipo es exploradora, carguero o combate.
     * - No hay una misión en curso.
     *
     * @param id identificador de la nave
     * @param tipo tipo de nave
     * @throws NaveYaRegistradaException si ya hay una nave con ese id
     */
    public void crearNave(String id, String tipo) throws NaveYaRegistradaException {
        assert id != null && !id.isBlank() : "El id de la nave no puede ser vacío";
        assert NaveFactory.esTipoValido(tipo) : "Tipo de nave desconocido: " + tipo;
        assert !hayMisionEnCurso() : "No se crean naves durante una misión";

        AsistenteDeComando asistente = new AsistenteDeComandoBasico(NaveFactory.createNave(id, tipo), new Bitacora());
        universo.registrar(asistente);
        tableros.put(id, crearTablero());
        try {
            seleccionarNave(id);
        } catch (NaveNoRegistradaException imposible) {
            throw new IllegalStateException("La nave recién registrada no se encuentra", imposible);
        }
    }

    // Los valores de las constantes son válidos: las excepciones del tablero no pueden ocurrir.
    private Tablero crearTablero() {
        try {
            return new Tablero(FILAS, COLUMNAS, FabricaDeElementos.crearElementos(ASTEROIDES, ESTACIONES, NAVES_ENEMIGAS));
        } catch (DimensionesDeTableroInconsistentesException error) {
            throw new IllegalStateException("Configuración del tablero inválida", error);
        } catch (CantidadDeElementosSuperaCasillaException error) {
            throw new IllegalStateException("Configuración del tablero inválida", error);
        }
    }

    /**
     * Elige con qué nave se opera (R2: una única nave a la vez). Las vistas pasan
     * a mostrar su bitácora y su tablero.
     *
     * Precondiciones (assert):
     * - No hay una misión en curso.
     *
     * @param id id de la nave
     * @throws NaveNoRegistradaException si no hay una nave con ese id
     */
    public void seleccionarNave(String id) throws NaveNoRegistradaException {
        assert !hayMisionEnCurso() : "No se cambia de nave durante una misión";
        if (universo.hayNaveActiva()) {
            AsistenteDeComando anterior = universo.getNaveActiva();
            anterior.quitarObservadorBitacora(observadorDeBitacora);
            tableros.get(anterior.idNave()).quitarObservador(observadorDeTablero);
        }
        universo.seleccionarNave(id);
        universo.getNaveActiva().agregarObservadorBitacora(observadorDeBitacora);
        getTablero().agregarObservador(observadorDeTablero);
        tomarFotoDelTablero();
        notificarObservadores("Nave activa: " + id);
    }

    /** @return true si ya se eligió una nave */
    public boolean hayNaveActiva() {
        return universo.hayNaveActiva();
    }

    /**
     * Precondiciones (assert): hay una nave activa.
     *
     * @return el asistente de la nave activa, por el que pasa toda consulta u orden
     */
    public AsistenteDeComando getNaveActiva() {
        return universo.getNaveActiva();
    }

    /** @return ids de las naves registradas */
    public List<String> idsDeNaves() {
        return universo.idsDeNaves();
    }

    private Tablero getTablero() {
        return tableros.get(universo.getNaveActiva().idNave());
    }

    // ---------- Tripulación y haberes ----------

    /**
     * Crea un tripulante y lo asigna a la nave activa.
     *
     * Precondiciones (assert):
     * - Hay una nave activa; los textos no son vacíos; antiguedad no es negativa;
     *   cargo es uno de FabricaDeCargos.NOMBRES.
     *
     * @throws TripulacionInvalidaException si ya hay un tripulante con ese id en la nave
     */
    public void asignarTripulante(String id, String nombre, String apellido, String cargo,
                                  Origen origen, int antiguedad) throws TripulacionInvalidaException {
        assert hayNaveActiva() : "No hay una nave activa";
        Tripulante tripulante = new Tripulante(id, nombre, apellido, FabricaDeCargos.crear(cargo), origen, antiguedad);
        getNaveActiva().asignarTripulante(tripulante);
        notificarObservadores("Tripulante " + id + " asignado");
    }

    /**
     * Registra un consejo de un consejero de la nave activa, con la fecha de hoy.
     *
     * @throws TripulacionInvalidaException si el tripulante no es consejero de la nave
     */
    public void registrarConsejo(String idTripulante, String descripcion) throws TripulacionInvalidaException {
        assert hayNaveActiva() : "No hay una nave activa";
        getNaveActiva().registrarConsejo(idTripulante, LocalDate.now(), descripcion);
        notificarObservadores("Consejo registrado");
    }

    /**
     * Liquida el haber del mes en curso de un tripulante de la nave activa (Decorator).
     *
     * @param tripulante tripulante de la nave activa
     * @return el haber, con el detalle de sus conceptos
     */
    public Haber liquidarHaber(Tripulante tripulante) {
        assert hayNaveActiva() : "No hay una nave activa";
        return getNaveActiva().liquidarHaberes(YearMonth.now(), tripulante);
    }

    // ---------- Recursos ----------
    // La carga de recursos y el mantenimiento se hacen en la base de origen: en el
    // medio del universo no hay de dónde sacar combustible ni quién repare la nave.
    // Si la nave quedó lejos, primero tiene que volver con M-03.

    /** @return true si la nave activa está en su base de origen (donde arrancó) */
    public boolean naveEnLaBase() {
        assert hayNaveActiva() : "No hay una nave activa";
        return getBase().equals(getFotoNave());
    }

    /** Carga combustible en la nave activa. @throws LimiteRecursoExcedidoException si supera 100 */
    public void cargarCombustible(int cantidad) throws LimiteRecursoExcedidoException {
        assert hayNaveActiva() && !hayMisionEnCurso() : "Se carga con una nave activa y sin misión en curso";
        assert naveEnLaBase() : "Solo se carga en la base de origen";
        getNaveActiva().cargarCombustible(cantidad);
    }

    /** Carga energía en la nave activa. @throws LimiteRecursoExcedidoException si supera 100 */
    public void cargarEnergia(int cantidad) throws LimiteRecursoExcedidoException {
        assert hayNaveActiva() && !hayMisionEnCurso() : "Se carga con una nave activa y sin misión en curso";
        assert naveEnLaBase() : "Solo se carga en la base de origen";
        getNaveActiva().cargarEnergia(cantidad);
    }

    /** Realiza el mantenimiento de la nave activa (desgaste a 0). */
    public void realizarMantenimiento() {
        assert hayNaveActiva() && !hayMisionEnCurso() : "Se repara con una nave activa y sin misión en curso";
        assert naveEnLaBase() : "Solo se repara en la base de origen";
        getNaveActiva().realizarMantenimiento();
    }

    // ---------- Misiones ----------

    /** Inicia M-01 en su propio hilo. @throws MisionNoViableException si ya hay una misión en curso */
    public void iniciarIntercepcion() throws MisionNoViableException {
        assert hayNaveActiva() : "No hay una nave activa";
        lanzar(new MisionIntercepcion(getNaveActiva(), getTablero(), "Nave aliada Orión", "sin propulsión"));
    }

    /** Inicia M-02 en su propio hilo. @throws MisionNoViableException si ya hay una misión en curso */
    public void iniciarRecoleccion() throws MisionNoViableException {
        assert hayNaveActiva() : "No hay una nave activa";
        lanzar(new MisionRecoleccion(getNaveActiva(), getTablero(), "el campo de riquezas"));
    }

    /** Inicia M-03 en su propio hilo. @throws MisionNoViableException si ya hay una misión en curso */
    public void iniciarRetorno() throws MisionNoViableException {
        assert hayNaveActiva() : "No hay una nave activa";
        lanzar(new MisionRetornoSeguro(getNaveActiva(), getTablero(), "la base de origen"));
    }

    private void lanzar(Mision mision) throws MisionNoViableException {
        synchronized (this) {
            if (misionEnCurso) {
                throw new MisionNoViableException("Ya hay una misión en curso");
            }
            misionEnCurso = true;
            misionActual = mision;
        }
        notificarObservadores("Misión iniciada: " + mision.getNombre());
        Thread hilo = new Thread(new EjecucionDeMision(this, getNaveActiva(), mision), "Mision " + mision.getNombre());
        hilo.start();
    }

    /** Lo invoca el hilo de la misión al terminar con su informe. */
    void misionTerminada(InformeMision informe) {
        synchronized (this) {
            informes.add(informe);
            misionEnCurso = false;
            misionActual = null;
            tomarFotoDelTablero();
        }
        notificarObservadores("Misión finalizada: " + informe.resumen());
    }

    /** Lo invoca el hilo de la misión si la misión fue rechazada o falló una operación. */
    void misionRechazada(String motivo) {
        synchronized (this) {
            misionEnCurso = false;
            misionActual = null;
            tomarFotoDelTablero();
        }
        notificarObservadores("Misión rechazada: " + motivo);
    }

    /** @return true si hay una misión ejecutándose */
    public synchronized boolean hayMisionEnCurso() {
        return misionEnCurso;
    }

    /** @return copia del historial de informes finales, del más viejo al más nuevo */
    public synchronized List<InformeMision> getInformes() {
        return new ArrayList<>(informes);
    }

    // ---------- Foto del tablero ----------

    private synchronized void tomarFotoDelTablero() {
        Tablero tablero = getTablero();
        fotoTablero = tablero.verTablero();
        fotoNave = tablero.getPosicionNave();
        if (misionActual != null) {
            fotoObjetivo = misionActual.getObjetivo();
            fotoRuta = misionActual.getRutaPendiente();
        } else {
            fotoObjetivo = null;
            fotoRuta = new ArrayList<>();
        }
    }

    /** @return matriz de elementos de la última foto del tablero (filas x columnas) */
    public synchronized ElementoDelUniverso[][] getFotoTablero() {
        return fotoTablero;
    }

    /** @return posición de la nave en la última foto del tablero */
    public synchronized Posicion getFotoNave() {
        return fotoNave;
    }

    /** @return objetivo de la misión en curso, o null si no hay misión */
    public synchronized Posicion getFotoObjetivo() {
        return fotoObjetivo;
    }

    /** @return casilleros que le faltan recorrer a la misión en curso (vacío si no hay misión) */
    public synchronized List<Posicion> getFotoRuta() {
        return fotoRuta;
    }

    /** @return base de origen de la nave: el centro del tablero, donde empieza */
    public Posicion getBase() {
        return new Posicion(FILAS / 2, COLUMNAS / 2);
    }

    // ---------- Observadores internos (Observer, Clase 11) ----------

    /** Recibe los eventos de la bitácora de la nave activa y los reenvía a las vistas. */
    private class ObservadorDeBitacora implements Observador {
        @Override
        public void actualizar(String mensaje) {
            tomarFotoDelTablero();   // la ruta calculada se anota en la bitácora antes del primer salto
            notificarObservadores(mensaje);
        }
    }

    /** Recibe los avisos del tablero: toma la foto y avisa a las vistas. */
    private class ObservadorDeTablero implements Observador {
        @Override
        public void actualizar(String mensaje) {
            tomarFotoDelTablero();
            notificarObservadores(mensaje);
        }
    }
}

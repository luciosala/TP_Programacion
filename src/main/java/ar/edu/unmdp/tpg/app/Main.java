package ar.edu.unmdp.tpg.app;

import ar.edu.unmdp.tpg.asistente.AsistenteDeComando;
import ar.edu.unmdp.tpg.asistente.AsistenteDeComandoBasico;
import ar.edu.unmdp.tpg.bitacora.Bitacora;
import ar.edu.unmdp.tpg.bitacora.EventoBitacora;
import ar.edu.unmdp.tpg.destino.CantidadDeElementosSuperaCasillaException;
import ar.edu.unmdp.tpg.destino.DimensionesDeTableroInconsistentesException;
import ar.edu.unmdp.tpg.destino.Tablero;
import ar.edu.unmdp.tpg.excepciones.NaveException;
import ar.edu.unmdp.tpg.mision.InformeMision;
import ar.edu.unmdp.tpg.mision.Mision;
import ar.edu.unmdp.tpg.mision.MisionIntercepcion;
import ar.edu.unmdp.tpg.mision.MisionRecoleccion;
import ar.edu.unmdp.tpg.mision.MisionRetornoSeguro;
import ar.edu.unmdp.tpg.naves.NaveFactory;
import ar.edu.unmdp.tpg.tripulacion.Alferez;
import ar.edu.unmdp.tpg.tripulacion.Capitan;
import ar.edu.unmdp.tpg.tripulacion.Consejero;
import ar.edu.unmdp.tpg.tripulacion.Origen;
import ar.edu.unmdp.tpg.tripulacion.Teniente;
import ar.edu.unmdp.tpg.tripulacion.Tripulante;
import ar.edu.unmdp.tpg.universo.Universo;

import java.util.ArrayList;
import java.util.List;

/**
 * Programa principal que simula al usuario (R6 de la Aclaracion de la catedra):
 * da de alta naves en el centro de control, crea misiones, se las encomienda a
 * un asistente y las ejecuta, mostrando el informe y la bitacora.
 *
 * Es la unica clase de la aplicacion que usa la consola. El modelo no sabe que
 * existe: en la Entrega 2 se reemplaza por pantallas sin cambiar ninguna clase
 * del modelo.
 *
 * La verificacion de los escenarios con resultados esperados esta en
 * test.PruebaEscenarios.
 */
public class Main {

    /**
     * Arma el universo, encomienda las tres misiones a una nave y muestra los resultados.
     *
     * @param args no se usan
     */
    public static void main(String[] args) {
        Mision.setPausaEntreTurnos(0);
        try {
            Universo universo = new Universo();
            registrarNave(universo, "EXP-1", "exploradora");
            registrarNave(universo, "CAR-1", "carguero");
            registrarNave(universo, "COM-1", "combate");

            universo.seleccionarNave("EXP-1");
            AsistenteDeComando nave = universo.getNaveActiva();
            Tablero tablero = new Tablero(10, 10, new ArrayList<>());

            mostrarEstado(nave);
            ejecutar(nave, new MisionIntercepcion(nave, tablero, "Carguero Orion", "sin propulsion"));
            ejecutar(nave, new MisionRecoleccion(nave, tablero, "Nebulosa K-7", "muestra de gas"));
            ejecutar(nave, new MisionRetornoSeguro(nave, tablero, "Base Andromeda"));

            System.out.println();
            System.out.println("Bitacora de " + nave.idNave() + ":");
            List<EventoBitacora> eventos = nave.consultarBitacora();
            for (EventoBitacora evento : eventos) {
                System.out.println("  " + evento);
            }
        } catch (NaveException error) {
            // Un rechazo del dominio: el programa lo informa al usuario.
            System.out.println("Operación rechazada: " + error.getMessage());
        } catch (DimensionesDeTableroInconsistentesException error) {
            System.out.println("No se pudo crear el tablero: " + error.getMessage());
        } catch (CantidadDeElementosSuperaCasillaException error) {
            System.out.println("No se pudo crear el tablero: " + error.getMessage());
        }
    }

    private static void ejecutar(AsistenteDeComando nave, Mision mision) throws NaveException {
        InformeMision informe = nave.ejecutarMision(mision);
        System.out.println();
        System.out.println(informe);
        mostrarEstado(nave);
    }

    private static void registrarNave(Universo universo, String id, String tipo) throws NaveException {
        AsistenteDeComando asistente = new AsistenteDeComandoBasico(NaveFactory.createNave(id, tipo), new Bitacora());
        asistente.asignarTripulante(new Tripulante(id + "-T1", "Ana", "Rojas", new Capitan(), Origen.TERRICOLA, 10));
        asistente.asignarTripulante(new Tripulante(id + "-T2", "Spok", "Sarek", new Consejero(), Origen.VULCANO, 5));
        asistente.asignarTripulante(new Tripulante(id + "-T3", "Leo", "Marte", new Teniente(), Origen.MARCIANO, 3));
        asistente.asignarTripulante(new Tripulante(id + "-T4", "Sol", "Diaz", new Alferez(), Origen.TERRICOLA, 1));
        asistente.asignarTripulante(new Tripulante(id + "-T5", "Tom", "Vala", new Alferez(), Origen.VULCANO, 0));
        universo.registrar(asistente);
    }

    private static void mostrarEstado(AsistenteDeComando nave) {
        System.out.println(nave.idNave()
            + " | combustible " + nave.combustibleDisponible()
            + " | energia " + nave.energiaDisponible()
            + " | desgaste " + nave.desgasteActual()
            + " | motor " + nave.estadoDelMotor());
    }
}

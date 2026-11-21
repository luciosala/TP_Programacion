package ar.edu.unmdp.tpg.test;

import ar.edu.unmdp.tpg.asistente.AsistenteDeComando;
import ar.edu.unmdp.tpg.asistente.AsistenteDeComandoBasico;
import ar.edu.unmdp.tpg.bitacora.Bitacora;
import ar.edu.unmdp.tpg.bitacora.EventoBitacora;
import ar.edu.unmdp.tpg.destino.Tablero;
import ar.edu.unmdp.tpg.elementos.Asteroide;
import ar.edu.unmdp.tpg.elementos.ElementoDelUniverso;
import ar.edu.unmdp.tpg.excepciones.NaveException;
import ar.edu.unmdp.tpg.mision.InformeMision;
import ar.edu.unmdp.tpg.mision.Mision;
import ar.edu.unmdp.tpg.mision.MisionRecoleccion;
import ar.edu.unmdp.tpg.naves.NaveFactory;
import ar.edu.unmdp.tpg.tripulacion.Alferez;
import ar.edu.unmdp.tpg.tripulacion.Capitan;
import ar.edu.unmdp.tpg.tripulacion.Origen;
import ar.edu.unmdp.tpg.tripulacion.Tripulante;

import java.util.ArrayList;

/**
 * Prueba del Motor Warp dentro de una mision (Anexo de la catedra): en un tablero
 * con muchos asteroides, cuando el radar de corto alcance ve uno en la proxima
 * casilla, la nave lo esquiva con un salto warp de 2 casillas.
 *
 * Como el tablero se arma al azar, se repite la mision hasta ver al menos un
 * salto warp completo (preparacion, salto y enfriamiento).
 *
 * Ejecutar con: java -ea -cp target/classes ar.edu.unmdp.tpg.test.PruebaWarp
 */
public class PruebaWarp {

    public static void main(String[] args) throws Exception {
        Mision.setPausaEntreTurnos(0);
        Verificador verificador = new Verificador();
        boolean huboWarp = false;
        int intentos = 0;

        while (!huboWarp && intentos < 50) {
            intentos++;
            AsistenteDeComando nave = crearNave();
            Tablero tablero = new Tablero(10, 10, asteroides(30));
            InformeMision informe;
            try {
                informe = nave.ejecutarMision(new MisionRecoleccion(nave, tablero, "Campo de asteroides", "mineral"));
            } catch (NaveException rechazo) {
                continue;   // por ejemplo, si el sorteo deja el objetivo muy lejos
            }
            boolean usoWarp = false;
            for (String accion : informe.getAcciones()) {
                if (accion.contains("salto warp de 2 casillas")) {
                    usoWarp = true;
                }
            }
            if (usoWarp) {
                huboWarp = true;
                System.out.println(informe);
                System.out.println();
                System.out.println("Eventos del motor:");
                for (EventoBitacora evento : nave.consultarBitacora()) {
                    if (evento.getCategoria().equals("MOTOR")) {
                        System.out.println("  " + evento);
                    }
                }
                verificador.verificar("La nave esquivó un asteroide con el Motor Warp", true);
                verificador.verificar("Se registró la activación del warp por un obstáculo",
                    informe.getAcciones().stream().anyMatch(a -> a.contains("se activa el Motor Warp")));
                verificador.verificar("Los turnos de preparación y enfriamiento quedaron registrados",
                    informe.getAcciones().stream().anyMatch(a -> a.contains("la nave espera")));
                verificador.verificar("Si la nave sigue viva, el motor termina Disponible",
                    nave.naveDestruida() || nave.estadoDelMotor().equals("Disponible"));
            }
        }
        verificador.verificar("Hubo al menos un salto warp en " + intentos + " intentos", huboWarp);
        verificador.imprimirResumen();
    }

    private static ArrayList<ElementoDelUniverso> asteroides(int cantidad) {
        ArrayList<ElementoDelUniverso> lista = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            lista.add(new Asteroide(3));
        }
        return lista;
    }

    private static AsistenteDeComando crearNave() throws NaveException {
        AsistenteDeComando nave = new AsistenteDeComandoBasico(NaveFactory.createNave("W-1", "combate"), new Bitacora());
        nave.asignarTripulante(new Tripulante("W-1-C", "Ana", "Sol", new Capitan(), Origen.TERRICOLA, 5));
        for (int i = 1; i <= 4; i++) {
            nave.asignarTripulante(new Tripulante("W-1-A" + i, "Tripulante", "N" + i, new Alferez(), Origen.VULCANO, 1));
        }
        return nave;
    }
}

package ar.edu.unmdp.tpg.test;

import ar.edu.unmdp.tpg.acceso.ServicioDeAcceso;
import ar.edu.unmdp.tpg.acceso.UsuarioExistenteException;
import ar.edu.unmdp.tpg.persistencia.ConexionBD;
import ar.edu.unmdp.tpg.persistencia.PersistenciaException;
import ar.edu.unmdp.tpg.persistencia.UsuarioDAOJDBC;

/**
 * Registra en la base H2 los usuarios del grupo, para la demo de la defensa.
 * Se puede correr las veces que haga falta: si un usuario ya existe, lo saltea.
 *
 *   ejecutar_pruebas.bat ar.edu.unmdp.tpg.test.CrearUsuariosDemo
 *   mvn compile exec:exec -Dprograma=ar.edu.unmdp.tpg.test.CrearUsuariosDemo
 *
 * Necesita el jar de H2 en el classpath (Maven lo baja solo; sin Maven, ver lib/LEEME.txt).
 */
public class CrearUsuariosDemo {

    private static final String[][] USUARIOS = {
        {"lucio", "lucio123"},
        {"manu",  "manu123"},
        {"lucas", "lucas123"},
        {"facu",  "facu123"},
        {"demo",  "demo"},
    };

    public static void main(String[] args) {
        try {
            ConexionBD conexion = ConexionBD.porDefecto();
            conexion.inicializarEsquema();
            ServicioDeAcceso servicio = new ServicioDeAcceso(new UsuarioDAOJDBC(conexion));
            for (String[] usuario : USUARIOS) {
                try {
                    servicio.registrar(usuario[0], usuario[1]);
                    System.out.println("Registrado: " + usuario[0]);
                } catch (UsuarioExistenteException e) {
                    System.out.println("Ya existia: " + usuario[0]);
                }
            }
            System.out.println("Base: " + ConexionBD.URL_POR_DEFECTO + " (archivo datos/tpg.mv.db)");
        } catch (PersistenciaException e) {
            System.out.println("No se pudo usar la base H2: " + e.getMessage());
            System.out.println("Sin Maven, hace falta el jar de H2 en la carpeta lib (ver lib/LEEME.txt).");
        }
    }
}

package ar.edu.unmdp.tpg.test;

import ar.edu.unmdp.tpg.acceso.CredencialesInvalidasException;
import ar.edu.unmdp.tpg.acceso.ServicioDeAcceso;
import ar.edu.unmdp.tpg.acceso.Sesion;
import ar.edu.unmdp.tpg.acceso.UsuarioExistenteException;
import ar.edu.unmdp.tpg.persistencia.ConexionBD;
import ar.edu.unmdp.tpg.persistencia.PersistenciaException;
import ar.edu.unmdp.tpg.persistencia.UsuarioDAO;
import ar.edu.unmdp.tpg.persistencia.UsuarioDAOEnMemoria;
import ar.edu.unmdp.tpg.persistencia.UsuarioDAOJDBC;

/**
 * Main de prueba del login y registro (Parte 2): DAO/DTO con JDBC sobre H2.
 * Compara cada resultado con el esperado usando el Verificador.
 *
 * Uso:
 *   mvn compile exec:java -Dexec.mainClass=ar.edu.unmdp.tpg.test.PruebaAcceso
 *       usa H2 en archivo (./datos/tpg.mv.db). Correrlo DOS veces muestra que el
 *       usuario registrado en la primera ejecución sigue existiendo en la segunda.
 *
 *   ... -Dexec.args=memoria
 *       usa el DAO en memoria, sin base de datos.
 */
public class PruebaAcceso {

    private static final Verificador verificador = new Verificador();

    public static void main(String[] args) {
        boolean enMemoria = args.length > 0 && args[0].equals("memoria");
        try {
            UsuarioDAO dao;
            if (enMemoria) {
                System.out.println("(DAO en memoria, sin base de datos)");
                dao = new UsuarioDAOEnMemoria();
            } else {
                ConexionBD conexion = ConexionBD.porDefecto();
                conexion.inicializarEsquema();
                System.out.println("(H2 en " + ConexionBD.URL_POR_DEFECTO + ")");
                dao = new UsuarioDAOJDBC(conexion);
            }
            ejecutar(new ServicioDeAcceso(dao));
        } catch (PersistenciaException e) {
            verificador.escenarioInterrumpido("Acceso a la base de datos",
                e.getCause() != null ? e.getCause() : e);
        }

        verificador.imprimirResumen();
        System.exit(verificador.fallos() == 0 ? 0 : 1);
    }

    private static void ejecutar(ServicioDeAcceso servicio) throws PersistenciaException {
        System.out.println("\n=== 1. Registro");
        try {
            servicio.registrar("capitana", "warp123");
            System.out.println("Usuario 'capitana' registrado (primera ejecución)");
        } catch (UsuarioExistenteException e) {
            // En la segunda ejecución con H2 entra acá: el usuario quedó guardado
            System.out.println("'capitana' ya existía de una ejecución anterior: la persistencia funciona");
        }

        System.out.println("\n=== 2. Registro repetido");
        try {
            servicio.registrar("capitana", "otra");
            verificador.faltoRechazo("Registrar un nombre repetido");
        } catch (UsuarioExistenteException e) {
            verificador.rechazoEsperado("Registrar un nombre repetido", e);
        }

        System.out.println("\n=== 3. Login correcto");
        try {
            Sesion sesion = servicio.iniciarSesion("capitana", "warp123");
            verificador.verificarIgual("Inicia sesión con credenciales correctas", "capitana", sesion.getNombreUsuario());
        } catch (CredencialesInvalidasException e) {
            verificador.verificar("Inicia sesión con credenciales correctas", false);
        }

        System.out.println("\n=== 4. Contraseña incorrecta");
        verificarLoginRechazado(servicio, "Login con contraseña incorrecta", "capitana", "WARP123");

        System.out.println("\n=== 5. Usuario inexistente");
        verificarLoginRechazado(servicio, "Login con usuario inexistente", "nadie", "warp123");

        System.out.println("\n=== 6. Datos vacíos (contrato)");
        try {
            servicio.registrar("  ", "x");
            verificador.faltoRechazo("Registrar un nombre vacío");
        } catch (IllegalArgumentException e) {
            verificador.rechazoEsperado("Registrar un nombre vacío", e);
        } catch (UsuarioExistenteException e) {
            verificador.verificar("Registrar un nombre vacío", false);
        }
    }

    private static void verificarLoginRechazado(ServicioDeAcceso servicio, String descripcion,
                                                String nombre, String contrasena)
            throws PersistenciaException {
        try {
            servicio.iniciarSesion(nombre, contrasena);
            verificador.faltoRechazo(descripcion);
        } catch (CredencialesInvalidasException e) {
            verificador.rechazoEsperado(descripcion, e);
        }
    }
}
package ar.edu.unmdp.tpg.acceso;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;

/**
 * Calcula y verifica hashes de contraseñas, para no guardarlas en texto plano.
 *
 * Cómo funciona:
 * - Al registrar, se genera una "sal": 16 bytes aleatorios propios del usuario.
 * - Se guarda SHA-256(sal + contraseña), nunca la contraseña.
 * - Al iniciar sesión, se recalcula el hash con la sal guardada y se compara.
 *
 * La sal hace que dos usuarios con la misma contraseña tengan hashes distintos.
 *
 * Nota: para un sistema real se usaría un algoritmo lento (bcrypt, PBKDF2);
 * SHA-256 con sal alcanza para el alcance del TP y usa solo la biblioteca estándar.
 */
final class HashContrasena {

    private static final SecureRandom ALEATORIO = new SecureRandom();
    private static final HexFormat HEX = HexFormat.of();

    private HashContrasena() {
    }

    /** @return una sal nueva de 16 bytes, en hexadecimal (32 caracteres) */
    static String generarSal() {
        byte[] sal = new byte[16];
        ALEATORIO.nextBytes(sal);
        return HEX.formatHex(sal);
    }

    /** @return SHA-256(sal + contraseña) en hexadecimal (64 caracteres) */
    static String calcular(String contrasena, String sal) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            sha256.update(HEX.parseHex(sal));
            return HEX.formatHex(sha256.digest(contrasena.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            // Toda JVM está obligada a incluir SHA-256: si falta, es un error del entorno
            throw new IllegalStateException("La JVM no soporta SHA-256", e);
        }
    }

    /**
     * Compara en tiempo constante (MessageDigest.isEqual) para no dar pistas
     * según cuánto tarda la comparación.
     */
    static boolean coincide(String contrasena, String sal, String hashGuardado) {
        byte[] calculado = calcular(contrasena, sal).getBytes(StandardCharsets.US_ASCII);
        byte[] guardado = hashGuardado.getBytes(StandardCharsets.US_ASCII);
        return MessageDigest.isEqual(calculado, guardado);
    }
}

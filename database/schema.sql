-- Esquema de la base de datos del TPG 2026 - Entrega 2.
--
-- Alcance (Enunciado Parte 2): solo se persisten las credenciales de los
-- usuarios. Naves, tripulantes, misiones, etc. NO se guardan.
--
-- La contraseña nunca se guarda en texto plano: se guarda su hash SHA-256
-- junto con una "sal" aleatoria propia de cada usuario.
--
-- Este archivo se copia al classpath al compilar (ver <resources> en pom.xml)
-- y ConexionBD lo ejecuta al iniciar el sistema. Por eso usa IF NOT EXISTS:
-- se puede ejecutar todas las veces que haga falta sin borrar datos.

CREATE TABLE IF NOT EXISTS usuario (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    nombre_usuario   VARCHAR(30) NOT NULL UNIQUE,
    hash_contrasena  CHAR(64)    NOT NULL,
    sal              CHAR(32)    NOT NULL
);



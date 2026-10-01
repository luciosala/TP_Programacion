# TPG_Programacion

Software embarcado de una nave interestelar tripulada — TPG 2026, Programación C.

## Requisitos

- JDK 17 o superior
- Maven 3.8 o superior

## Compilación

```
mvn clean compile
```

## Estructura de paquetes

Todo el código está bajo `src/main/java/ar/edu/unmdp/tpg/`:

| Paquete | Contenido |
|---|---|
| `naves` | `Nave`, tipos de nave, `NaveFactory`, `Recursos` |
| `motorwarp` | `MotorWarp` y sus estados (patrón State) |
| `tripulacion` | `Tripulante`, cargos, `Origen`, `Consejo` |
| `haberes` | Liquidación de haberes (patrón Decorator) |
| `bitacora` | `Bitacora` y `EventoBitacora` |
| `mision` | Misiones M-01, M-02, M-03 (patrón Template Method) e `InformeMision` |
| `asistente` | `AsistenteDeComando` |
| `excepciones` | Excepciones del dominio |

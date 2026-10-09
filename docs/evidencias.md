# Evidencias funcionales — Entrega 1

Todas las evidencias se producen con una sola corrida del programa:

```
mvn clean compile
mvn exec:java
```

La salida de esa corrida está guardada en [`ejecuciones.txt`](ejecuciones.txt). Lo que
sigue indica, para cada requerimiento y para cada evidencia mínima exigida, dónde
mirar y qué resultado esperar.

## Requerimientos E1

| Req. | Dónde está implementado | Dónde se verifica | Resultado esperado |
|---|---|---|---|
| E1-01 Modelo OO de la nave | `naves.Nave` y sus tres subtipos | Encabezado de la corrida y escenario A | Las tres naves se registran con su configuración inicial: EXP-1 60/80/0, CAR-1 100/60/0, COM-1 80/100/0. Recursos y motor quedan encapsulados: no hay forma de tocarlos sin pasar por la nave. |
| E1-02 Motor Warp mediante State | `motorwarp.MotorWarp`, `motorwarp.Estado` y los cuatro estados | Escenario C | El ciclo Disponible → Preparando salto → En warp → Enfriamiento → Disponible se recorre completo. Las dos transiciones inválidas se rechazan y el motor queda en Disponible. |
| E1-03 Asistente de Comando | `asistente.AsistenteDeComando` y `AsistenteDeComandoBasico` | Todos los escenarios | Cada orden del `Main` pasa por el asistente y aparece en la bitácora como `COMANDO`. Ninguna clase fuera del asistente conoce a la vez la nave y la bitácora. |
| E1-04 Tripulación | `tripulacion.Tripulante`, `Cargo` y sus cuatro subtipos | Encabezado y escenario Haberes | Las tres naves quedan con 5 tripulantes y un capitán cada una. Una nave sin tripulación mínima no puede iniciar misiones. |
| E1-05 Bitácora | `bitacora.Bitacora`, `bitacora.EventoBitacora` | Escenarios B, C y D | Cada escenario imprime los eventos que generó, con su instante, su categoría y su descripción. Los eventos son inmutables y la lista que devuelve la bitácora no se puede modificar. |
| E1-06 Tres misiones mediante Template Method | `mision.Mision`, `MisionIntercepcion`, `MisionRecoleccion`, `MisionRetornoSeguro` | Escenario A | Las tres misiones recorren el mismo ciclo preparar → ejecutar → evaluar → cerrar y las tres terminan EXITOSA. Cada una registra sus propias acciones. |
| E1-07 Creación de naves mediante Factory | `naves.NaveFactory` | Encabezado de la corrida | Las tres naves se crean con `NaveFactory.createNave(id, tipo)`. `Main` no instancia `Combate`, `Carguera` ni `Exploradora`: no podría, sus constructores son package private. |
| E1-08 Liquidación de haberes mediante Decorator | `haberes.Haber`, `DecoratorHaber`, `LiquidacionHaberes` | Escenario Haberes | Capitán 3020 PG, Consejero 786 PG, Teniente 454 PG, Alférez 221 PG, con cada concepto detallado por separado. |
| E1-09 Recursos y mantenimiento | `naves.Recursos` | Escenarios A, B y D | Los recursos se actualizan consistentemente y nunca salen del rango 0–100. El mantenimiento deja el desgaste en 0. |
| E1-10 Informe mínimo de misión | `mision.InformeMision` | Escenario A | Cada misión cierra con un informe que incluye misión, objetivo, resultado, acciones realizadas, recursos consumidos, estado final de la nave y si requiere mantenimiento. |

## Evidencias funcionales mínimas

| Evidencia exigida | Dónde | Resultado esperado |
|---|---|---|
| Transiciones válidas e inválidas del Motor Warp | Escenario C | Las cuatro transiciones del ciclo funcionan. `iniciarSalto` y `finalizarSalto` desde Disponible se rechazan con `TransicionInvalidaException` y el motor conserva su estado. |
| Ciclo completo de M-01, M-02 y M-03 | Escenario A | Las tres misiones son exitosas sobre EXP-1. La nave pasa de 60/80/0 a 48/70/12: 4 de combustible y 4 de desgaste por misión, más 5 de energía por cada acción final que tuvo costo (M-01 y M-02; M-03 no tiene). |
| Creación de los tres tipos de nave mediante Factory | Encabezado | Se crean EXP-1 exploradora, CAR-1 carguero y COM-1 combate, cada una con la configuración inicial de su tipo. |
| Sin instanciación directa de naves concretas desde el cliente | `app.Main` | `Main` solo nombra `Nave` y `NaveFactory`. Los constructores de las subclases son package private: la restricción está garantizada por el compilador, no por convención. |
| Modificación consistente de recursos y mantenimiento | Escenarios B y D | Tras el rechazo del escenario B los recursos quedan exactamente como estaban (52/0/48): la línea "Recursos sin cambios parciales: SI" lo deja asentado. En D, una carga que excedería 100 se rechaza y el combustible sigue en 92. |
| Generación del informe de misión | Escenario A | Tres informes completos, uno por misión. |
| Registro temporal de eventos en la Bitácora | Escenarios B, C y D | Cada evento se imprime con su `Instant`, en orden del más viejo al más nuevo. |
| Haberes de los cuatro cargos y los tres orígenes, con dos o más decoradores compuestos | Escenario Haberes | Los cuatro cargos y los tres orígenes aparecen liquidados. Todo haber compone al menos dos decoradores (antigüedad y subsidio por origen) sobre el sueldo base; el del consejero compone tres (suma el adicional por consejos). Del consejero se cuentan solo los 3 consejos del período liquidado: el registrado en septiembre queda afuera. |
| Al menos un rechazo por precondición o invariante incumplida | Escenarios B, C y D | Tres rechazos distintos: recursos insuficientes (B), transición inválida del motor (C) y carga que excedería el máximo (D). Los tres quedan registrados en la bitácora con categoría `ERROR`. |

## Observaciones del enunciado

| Observación | Cómo se cumple |
|---|---|
| La nave no puede tener combustible o energía negativos | `Recursos` rechaza todo consumo mayor al disponible. Escenario B. |
| Desgaste y mantenimiento dentro de rango | `Recursos` rechaza el desgaste que superaría 100 y `realizarMantenimiento()` lo deja en 0. Escenario D. |
| El Motor Warp no acepta transiciones inválidas silenciosas | Cada estado lanza `TransicionInvalidaException` y el asistente la registra. Escenario C. |
| Una misión no puede ejecutarse sin preparación previa | `realizarMision()` es `final` y llama a `preparar()` antes que a `ejecutar()`. Las subclases no pueden alterar el orden. |
| Una misión no puede cerrarse sin resultado e informe | `cerrar(boolean)` recibe el resultado de la evaluación y devuelve siempre un informe. |
| La Bitácora no acepta eventos nulos o vacíos | `EventoBitacora` los rechaza en su constructor. |
| La antigüedad no puede ser negativa ni el haber aceptar importes inválidos | `Tripulante` rechaza antigüedad negativa; `DecoratorHaber` rechaza importes no finitos o negativos. |
| Toda nave creada por la fábrica comienza en un estado válido | `NaveFactory` arma los recursos iniciales del tipo y un `MotorWarp` nuevo, que nace Disponible. |
| Las clases del dominio no dependen de consola ni de Swing | `Main` es la única clase que usa `System.out`. El modelo devuelve objetos (`InformeMision`, `Haber`, `List<EventoBitacora>`); quien los muestre decide cómo. |

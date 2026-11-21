# Evidencias funcionales — Entregas 1 y 2

Las evidencias reproducibles se generan con:

```
mvn clean compile
mvn compile exec:exec -Dprograma=ar.edu.unmdp.tpg.test.PruebaEscenarios
```

La salida de una corrida está guardada en [`ejecuciones.txt`](ejecuciones.txt). El programa compara cada
resultado con el esperado y termina con `RESULTADO: TODOS LOS ESCENARIOS COINCIDEN CON LO ESPERADO`.

Como la Parte 2 incorpora azar (el objetivo lo elige el `Tablero` y `Destino` mueve los elementos), los
escenarios A, B y D usan un tablero de 7 × 7 **sin elementos**: sin obstáculos todos los saltos son simples y
el costo de cada misión se calcula con la distancia recorrida (despegue + 2 de combustible por salto). La
navegación con obstáculos se verifica en el escenario NAVEGACION comprobando que el balance de cada informe
coincide con lo que la nave hizo turno a turno.

## Requerimientos E1 (con los cambios de la Parte 2)

| Req. | Dónde está implementado | Dónde se verifica | Resultado esperado |
|---|---|---|---|
| E1-01 Modelo OO de la nave | `naves.Nave` y sus tres subtipos | CREACION y escenario A | Las tres naves se crean con su configuración inicial: EXP-1 60/80/0, CAR-1 100/60/0, COM-1 80/100/0. |
| E1-02 Motor Warp mediante State | `motorwarp.MotorWarp` y los estados `Disponible`, `PrepararSalto`, `EnWarp`, `Enfriando` | Escenario C | El motor expone solo `saltar()` y `saltarWarp()`. Salto simple: −2 combustible. Warp: 2 turnos de preparación, salto de 2 casilleros con −5 energía y 1 turno de enfriamiento con +3 desgaste. Sin energía se queda preparando; si falta al saltar, aborta y vuelve a preparación. Un salto sin combustible se rechaza sin cambios. Los 4 cambios de estado quedan en la bitácora (`MOTOR`). |
| E1-03 Asistente de Comando | `asistente.AsistenteDeComando` y `AsistenteDeComandoBasico` | Todos los escenarios | Toda orden a la nave pasa por el asistente y queda en la bitácora. |
| E1-04 Tripulación | `tripulacion.Tripulante`, `Cargo` y sus cuatro subtipos | CREACION, E y Haberes | Cada nave con 5 tripulantes y un capitán. Sin tripulación mínima la misión se rechaza. |
| E1-05 Bitácora | `bitacora.Bitacora`, `bitacora.EventoBitacora` | Escenarios A a D | Eventos inmutables, con instante, categoría y descripción, en orden temporal. |
| E1-06 Misiones mediante Template Method | `mision.Mision` y sus tres subclases | Escenario A | Las tres misiones recorren preparar → ejecutar → evaluar → cerrar y terminan EXITOSA. |
| E1-07 Creación de naves mediante Factory | `naves.NaveFactory` | CREACION | Las naves se crean con `NaveFactory.createNave(id, tipo)`; los constructores concretos son package private. |
| E1-08 Haberes mediante Decorator | `haberes.Haber`, `DecoratorHaber`, `LiquidacionHaberes` | HABERES | Capitán 3020 PG, Consejero 786 PG, Teniente 454 PG, Alférez 221 PG, con cada concepto por separado. |
| E1-09 Recursos y mantenimiento | `naves.Recursos` | Escenarios A, B y D | Los recursos nunca salen de 0–100. Las cargas que superarían 100 se rechazan sin cambios. El mantenimiento deja el desgaste en 0. |
| E1-10 Informe de misión | `mision.InformeMision`, `mision.BalanceDeRecursos` | Escenario A | Misión, objetivo, resultado, acciones (ruta y turnos), balance detallado, recursos consumidos y estado de la nave. |

## Requerimientos de la Entrega 2

| Requerimiento | Dónde está implementado | Dónde se verifica | Resultado esperado |
|---|---|---|---|
| Costos de despegue por misión (Anexo) | `energiaDespegue()` de cada misión; 4 de combustible y 4 de desgaste en `Mision` | Escenarios A y B | M-01 4/5/4, M-02 4/10/4, M-03 4/0/4. En B, CAR-1 (60 de energía) completa 5 recolecciones; la 6.ª se rechaza sin cambios parciales. |
| Fase Preparar: verificación y ruta | `Mision.preparar()`, `RadarLargoAlcance` | Escenarios A y E | Rechaza si la nave está destruida, el motor no está Disponible, falta tripulación o no alcanza el despegue. La ruta queda registrada casillero por casillero. |
| Fase Ejecutar: ciclo de turnos | `Mision.viajar()` | Escenario NAVEGACION | Inspección → escaneo con radar corto → movimiento → turno de `Destino`, con `Thread.sleep` entre turnos. Combustible de navegación = 2 × saltos simples; energía = 5 × saltos warp; desgaste = 3 × enfriamientos; energía perdida = 10 × impactos. |
| Salto simple o warp según el radar | `Mision.mover()`, `RadarCortoAlcance.hayObstaculoEn()` | Escenario NAVEGACION y `ejecuciones.txt` | Con un asteroide en la próxima casilla, la nave prepara y hace el salto warp ("salto warp, esquivando el obstáculo"). |
| Destrucción de la nave | `Nave.controlarDestruccion()`, `Recursos.enNivelLetal()` | `PruebaElementos` y NAVEGACION | Energía o combustible en 0, o desgaste en 100, destruyen la nave y la misión falla de inmediato. |
| Verificación en destino | `Mision.verificarEnDestino()` | Escenario A | M-01 solo es exitosa si la nave conserva lo que debe entregar; el descuento se hace en el cierre. |
| M-01 entrega a la nave aliada | `MisionIntercepcion` | Escenario A | Al quedar en la coordenada de la aliada se le transfieren los recursos pedidos (entre 0 y 50 de cada uno). |
| M-02 recolecta gemas | `MisionRecoleccion` | Escenario A | Entre 20 y 100 gemas. |
| M-03 vuelve a la base | `MisionRetornoSeguro` | Escenario A | La nave vuelve al punto de partida (centro del tablero), recarga al 100 % y queda con desgaste 0. |
| Fases Evaluar y Cerrar | `Mision.evaluar()`, `Mision.cerrar()` | Escenario A | Evaluar arma el informe con el balance; Cerrar impacta lo entregado o ganado. |
| Elementos del universo | `elementos.*`, `naves.Nave` implementan `ElementoDelUniverso` | `PruebaElementos` | Asteroide −10 de energía; estación fija y porcentual; naves enemigas. |
| Batallas con Double Dispatch | `Nave.enfrentar()`, `recibirDesafioDe()`, `contraXxx()` | `PruebaElementos` | Los 9 casos de la tabla del Enunciado. Si la nave propia pierde, queda destruida. |
| Destino y Tablero de la cátedra | paquete `destino` (versión 2) | `PruebaTablero` y la vista del tablero | Integrados sin modificar su lógica. |
| Productor-Consumidor con monitores | `visor.modelo.PlataformaDeCarga`, `GeneradoraDeEnergia`, `NaveConsumidora` | `PruebaVisor` y la ficha Visor Recóndito | La plataforma nunca supera su capacidad; las entidades esperan con `wait()` y se despiertan con `notifyAll()`. |
| Login y Registro con DAO/DTO y JDBC | `acceso.ServicioDeAcceso`, `persistencia.*`, `database/schema.sql` | `PruebaAcceso` (dos ejecuciones) | El usuario registrado puede iniciar sesión en una ejecución posterior. Usuario repetido y contraseña incorrecta se rechazan. |
| Interfaz MVC + Observer | paquetes `vista`, `controlador`, `aplicacion` | `mvn compile exec:exec` | Las cinco secciones del Enunciado. El tablero, los recursos, la bitácora y los informes se actualizan solos durante la misión. |

## Evidencias funcionales mínimas de E1

| Evidencia exigida | Dónde | Resultado esperado |
|---|---|---|
| Transiciones válidas e inválidas del Motor Warp | Escenario C | Secuencia completa Disponible → Preparando salto → En warp → Enfriamiento → Disponible. Un salto sin combustible se rechaza con `RecursoInsuficienteException` y el motor no cambia. |
| Ciclo completo de M-01, M-02 y M-03 | Escenario A | Las tres misiones son exitosas sobre EXP-1 con los costos del Anexo. |
| Creación de los tres tipos de nave mediante Factory | CREACION | EXP-1 exploradora, CAR-1 carguero, COM-1 combate. |
| Sin instanciación directa de naves concretas | `app.*`, `aplicacion.*` | Solo se nombran `Nave` y `NaveFactory`; los constructores concretos son package private. |
| Modificación consistente de recursos y mantenimiento | Escenarios B y D | Tras el rechazo de B los recursos quedan exactamente como estaban. En D las cargas que superarían 100 se rechazan y el mantenimiento deja el desgaste en 0. |
| Generación del informe de misión | Escenario A | Un informe completo por misión, con balance detallado. |
| Registro temporal de eventos en la Bitácora | Escenarios A a D | Eventos en orden temporal. |
| Haberes con dos o más decoradores | HABERES | Los cuatro cargos y los tres orígenes; el consejero compone tres decoradores. |
| Rechazo por precondición o invariante | Escenarios B, C, D y E | Recursos insuficientes, salto sin combustible, carga que excede el máximo, precondiciones con `assert`. Quedan en la bitácora como `ERROR`. |

## Observaciones del enunciado

| Observación | Cómo se cumple |
|---|---|
| Combustible y energía nunca negativos | `Recursos` rechaza todo consumo mayor al disponible; los golpes (`perderEnergia`) no bajan de 0. |
| Desgaste dentro de rango | `Recursos` mantiene el desgaste entre 0 y 100; al llegar a 100 la nave se destruye. |
| El Motor Warp no hace cambios silenciosos | Cada cambio de estado queda en la bitácora (`MOTOR`); un salto rechazado lanza excepción y queda como `ERROR`. |
| Una misión no se ejecuta sin preparación ni se cierra sin informe | `realizarMision()` es `final` y las fases se controlan con asserts. |
| La Bitácora no acepta eventos vacíos | `EventoBitacora` los rechaza en su constructor. |
| Las clases del dominio no dependen de consola ni de Swing | La consola solo está en `app.Main` y `test`; Swing solo en `vista`, `controlador`, `visor.vista`, `visor.controlador` y `app.MainGrafico`. |

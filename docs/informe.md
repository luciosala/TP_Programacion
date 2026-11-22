Informe de diseño - Entrega Final

TPG 2026 - Programación C

Este informe continua el de la Entrega 1. Las secciones de la E1 se actualizaron con lo que cambio en la E2 (y con las correcciones de la devolucion), y se agregaron las partes nuevas.

1. Idea general

El sistema arranca con el acceso: el usuario se registra o inicia sesión, y recién ahí se abre la ventana principal. Desde ahí se crea la nave, se arma la tripulación, se lanzan las misiones y se mira el visor del universo paralelo.

El punto de entrada al modelo sigue siendo el Universo, que guarda los asistentes de comando (no las naves), porque toda orden o consulta a una nave pasa por su asistente. En la E2 se agrego una clase por encima, ModeloAplicacion, que es la que usan los controladores de la interfaz.

Una orden del usuario sigue este camino:
Vista -> Controlador -> ModeloAplicacion -> Universo -> AsistenteDeComando -> Nave -> Recursos / MotorWarp

y el asistente va anotando todo en la Bitácora.

Las misiones se le encomiendan a un asistente y ahora se ejecutan sobre un Tablero de 20x20. En cada turno la nave mira donde está parada, escanea alrededor, decide si da un salto normal o un salto warp, y después el Destino (la clase que dio la cátedra) mueve los elementos del universo. Lo que va pasando llega a la pantalla por Observer, sin que el modelo conozca a la vista.

Aparte, el visor del universo paralelo es un módulo independiente de productores y consumidores que comparten una plataforma de carga, resuelto con monitores.



2. Responsabilidades de cada clase

--Núcleo--

-Universo: registra los asistentes, devuelve uno por id y deja elegir la nave con la que se trabaja (una sola a la vez). Conoce a la interfaz AsistenteDeComando.

-AsistenteDeComando: es la interfaz con todo lo que se le puede pedir a una nave. En la E2 se le agregaron saltar(), saltarWarp(), interactuarCon(elemento), naveDestruida() y la posibilidad de observar la bitácora.

-AsistenteDeComandoBasico: recibe las ordenes, se las pasa a la nave, atrapa los errores y deja todo anotado en la bitacora. No hace la logica interna de la nave. Conoce a la Nave y a la Bitacora.

-Nave (abstracta): tiene id, tipo, tripulacion, recursos y gemas. Lo de recursos se lo delega a Recursos y lo del motor a MotorWarp.En la E2 también es un ElementoDelUniverso (las naves enemigas están en el tablero) y sabe si fue destruida.

-Exploradora, Carguera y Combate: los tres tipos de nave. Cambian los recursos con los que arrancan y, desde la E2, cómo les va en una batalla contra cada tipo.

-NaveFactory: es el unico lugar donde se crean naves, tanto la del jugador como las enemigas.

-Recursos: maneja combustible, energia y desgaste, y controla que nunca se salgan de 0 a 100. Dice si la nave quedó en nivel letal.

-MotorWarp y sus estados: el motor y sus cuatro estados.Solo expone saltar() y saltarWarp()

-Mision (abstracta): define el ciclo preparar, ejecutar, evaluar y cerrar, con la travesía por el tablero adentro de ejecutar. Conoce al asistente y al tablero.

-MisionIntercepcion, MisionRecoleccion y MisionRetornoSeguro: son M-01, M-02 y M-03. Solo completan lo que cambia en cada una (costos, destino, qué se entrega o se gana al final).

-InformeMision: el resumen que se arma al cerrar la mision. No se puede modificar.

-BalanceDeRecursos: va sumando lo que se consumió, ganó, perdió y entregó durante la misión, separado por etapa (despegue, navegación, encuentros, cierre). Es lo que usa el informe para el balance detallado.

-Bitacora y EventoBitacora: guardan los eventos en orden. Cada evento tiene fecha y hora, categoria y descripcion, y no se puede modificar. Desde la E2 la Bitacora es observable, para que la pantalla muestre los eventos en vivo.

-Tripulante, Cargo y Origen: los datos de la tripulacion. FabricaDeCargos arma el cargo a partir del nombre que elige el usuario en la pantalla.

-Haber y los decoradores: calculan el sueldo mensual de cada tripulante.

--Universo dinámico--

-ElementoDelUniverso: interfaz de todo lo que puede haber en una casilla del tablero. Tiene interactuarCon(nave), que aplica las consecuencias del encuentro y devuelve un texto para la bitácora, y esObstaculo(), que usa el radar para decidir si conviene esquivar.

-Asteroide: descuenta un monto fijo de energía al chocarlo. Es el obstáculo que la nave intenta esquivar con warp.

-EstacionDeCarga (abstracta), EstacionDeCargaFija y EstacionDeCargaPorcentual: recargan combustible y energía. La clase base hace la recarga y las hijas solo dicen cuánto (un monto fijo o un porcentaje de lo que tiene la nave).

-ResultadoBatalla: enum con GANA, PIERDE y PACTO_DE_PAZ.

-FabricaDeElementos: arma la lista de asteroides, estaciones y naves enemigas con la que se carga el tablero.

-RadarLargoAlcance: calcula la ruta desde la posición de la nave hasta el objetivo, de a una casilla.

-RadarCortoAlcance: mira las 8 casillas que rodean a la nave y dice si en la próxima casilla de la ruta hay un obstáculo.

-Tablero, Destino, Posicion, Observable y Observador: los dio la cátedra y los usamos sin modificar. El tablero sabe dónde está cada elemento, dónde está la nave y cuál es el objetivo; el Destino mueve los elementos en cada turno.

--Aplicación e interfaz gráfica --

-ModeloAplicacion: es la fachada del modelo para la interfaz. Crea naves (cada una con su tablero), asigna tripulantes, carga recursos y lanza misiones. Solo deja cargar recursos o hacer mantenimiento si la nave está en su base de origen. Es observable: avisa cuando cambia algo para que las vistas se actualicen.

-EjecucionDeMision: Runnable que ejecuta una misión en su propio hilo. Existe para que la misión, que hace Thread.sleep en cada turno, no congele la ventana.

-VentanaAcceso, VentanaPrincipal y los paneles (PanelCreacion, PanelTripulantes, PanelControlNave, PanelTablero, PanelVisor): las vistas. Cada una implementa una interfaz IVista con lo que el controlador necesita de ella.

-ControladorAcceso, ControladorCreacion, ControladorTripulantes, ControladorControl y ControladorVisor: reciben las acciones del usuario (ActionListener) y llaman al modelo.

-MainGrafico: arma todo (base de datos, modelos, vistas y controladores) y abre la ventana de acceso. Si se ejecuta con el argumento demo, arranca con una nave de combate y su tripulación ya cargadas, para la demostración.

--Persistencia y acceso--

-UsuarioDTO: lleva los datos de un usuario entre capas (nombre, hash de la contraseña y sal).

-UsuarioDAO: interfaz con las operaciones sobre los usuarios (insertar, buscarPorNombre, existe).

-UsuarioDAOJDBC: implementación con JDBC sobre H2. UsuarioDAOEnMemoria: implementación para las pruebas.

-ConexionBD: abre la conexión y crea la tabla a partir de database/schema.sql.

-ServicioDeAcceso: las reglas del registro y el login (usuario repetido, credenciales inválidas). Devuelve una Sesion.

-HashContrasena: genera la sal y calcula el hash. Nunca se guarda la contraseña.

--Visor del universo paralelo--

-PlataformaDeCarga: el recurso compartido, con capacidad limitada. Es el monitor.

-GeneradoraDeEnergia: productor. NaveConsumidora: consumidor. Las dos son Runnable y cada una corre en su propio hilo.

-Bateria: lo que se produce y se consume. Es inmutable, así que se puede pasar entre hilos sin riesgo.

-UniversoParalelo: crea generadoras y naves mientras la simulación corre, les arma su hilo y los guarda para poder detenerlos.

-CanalDeEventos: observable por donde se publican los mensajes que muestra el visor.

3. Patrones

--State: Motor Warp--

El motor puede estar en Disponible, Preparando salto, En warp o Enfriamiento. Cada estado es una clase que implementa la interfaz Estado y sabe qué hacer cuando le piden saltar() o saltarWarp(). MotorWarp solo guarda el estado actual y le delega todo. Hacia afuera el motor expone únicamente esas dos operaciones, como pide el Anexo; las clases de los estados y el cambio de estado son de visibilidad de paquete, así que nadie de afuera puede forzar una transición.

Cada llamada devuelve cuántos casilleros avanza la nave en ese turno (0, 1 o 2):

-Disponible: saltar() gasta 2 de combustible y avanza 1. saltarWarp() pasa a Preparando salto y cuenta como el primer turno de preparación. -Preparando salto: saltarWarp() comprueba que haya 5 de energía; si hay, suma un turno de preparación y al segundo pasa a En warp. Si no hay energía, se queda preparando. No avanza. -En warp: saltarWarp() consume los 5 de energía y avanza 2 casilleros, y el motor pasa a Enfriamiento. Si en ese momento ya no hay energía, el salto se aborta y vuelve a Preparando salto. -Enfriamiento: cualquier pedido se usa para enfriar. Suma 3 de desgaste y el motor vuelve a Disponible. No avanza.

Si estando en Preparando salto o En warp se pide un saltar() normal, el motor cancela el warp, vuelve a Disponible y hace el salto normal. Preferimos eso a rechazar el pedido, porque un salto normal siempre es una acción posible para la nave.

Así, un warp completo ocupa como mínimo 4 turnos (2 de preparación, el salto y el enfriamiento), cuesta 5 de energía y 3 de desgaste, y avanza 2 casilleros.

Lo elegimos porque cada regla del Anexo queda en el estado al que pertenece, y la nave no tiene ningún if sobre en qué estado está el motor. Agregar un estado nuevo es agregar una clase.

El motor recibe en su constructor los mismos Recursos que la nave (se los pasa NaveFactory), porque los costos del salto salen de ahí. Después de cada salto la nave controla si quedó en nivel letal.

--Factory: creacion de naves--

NaveFactory.createNave(id, tipo) arma la nave con los recursos iniciales de la ficha de inicio y con un motor nuevo en Disponible. Los constructores de Exploradora, Carguera y Combate son package private, asi que desde Main no se pueden crear a mano.

En la E2 la usamos también para las naves enemigas del tablero, a través de FabricaDeElementos, y agregamos FabricaDeCargos para que la vista elija el cargo por nombre sin conocer las clases Capitan, Consejero, etc.

Lo elegimos para que el que usa el sistema no tenga que saber con qué valores arranca cada tipo de nave, y para que toda nave empiece en un estado válido.

--Template Method: misiones--

El metodo realizarMision() de Mision es final y siempre hace los cuatro pasos en el mismo orden. Los cuatro pasos también son final, así que las subclases no pueden cambiar el ciclo:

1) Preparar: verifica que la nave no esté destruida, que el motor esté disponible, que la tripulación esté lista y que alcancen los recursos del despegue sin dejar la nave en nivel letal. Después calcula la ruta con el radar de largo alcance y la guarda.
2) Ejecutar: descuenta el despegue, hace el ciclo de travesía por turnos (sección 4) y, si la nave llegó viva, verifica en el destino que todavía tenga lo que tiene que entregar.
3) Evaluar: arma el informe final con el resultado y el balance de recursos.
4) Cerrar: si la misión salió bien, recién acá se descuenta lo que se entrega y se aplica lo que se gana. Se registra el resultado en la bitácora.

Lo que cambia en cada misión son métodos que las subclases redefinen: energiaDespegue(), destinoDeLaMision(), combustibleAEntregar(), energiaAEntregar(), alLlegar() e impactarAlCerrar().

M-01-Intercepción: Despegue(combustible/energia,desgaste)-> 4/5/4. Destino, Objetivo del tablero(la nave aliada). Al cerrar entrega entre 0 y 50 de energia y de combustible.

M-02-Recolección: Despegue(combustible/energia,desgaste)-> 4/10/4. Destino, Objetivo del tablero. Al cerrar gana entre 10 y 100 gemas.

M-03-Retorno seguro: Despegue(combustible/energia,desgaste)-> 4/0/4. Destino, base de origen (la posición inicial, el centro del tablero). Al cerrar recarga al 100 % y desgaste a 0.

El combustible y el desgaste del despegue son iguales para las tres, así que están en Mision con un valor por defecto; cada misión solo redefine lo suyo. Una misión nueva sigue siendo una subclase nueva, sin tocar las otras.

--Decorator: haberes--

HaberBase tiene el sueldo del cargo y encima se van "envolviendo" los adicionales: AdicionalAntiguedad, SubsidioPorOrigen y, solo para el consejero, AdicionalConsejos. Cada decorador suma su parte y se puede consultar cuanto aporta cada concepto por separado. En la E2 la pantalla de tripulantes muestra el total y el detalle concepto por concepto.

Lo usamos para no tener una clase por cada combinacion de cargo, origen y antiguedad. La que arma la cadena es LiquidacionHaberes.

--Double Dispatch: batallas entre naves--

Cuando la nave del jugador cae en la casilla de una nave enemiga, pelean, y el resultado depende solo de los tipos de las dos. Con un solo despacho (un método común de Nave), la nave sabría su propio tipo pero no el de la otra, y habría que preguntar con instanceof. Con double dispatch se resuelve en dos llamadas:

1. nave.enfrentar(enemiga) llama a enemiga.recibirDesafioDe(nave). Ahí se resuelve el tipo de la enemiga.
2. Cada subclase implementa recibirDesafioDe llamando al método de su tipo sobre la retadora: la Carguera llama a retadora.contraCarguera(), la Combate a retadora.contraCombate(), etc. Ahí se resuelve el tipo de la retadora.

Cada subclase tiene contraExploradora(), contraCarguera() y contraCombate(), que son su fila de la tabla del enunciado:

Nave propia          vs. Exploradora          vs. Carguera          vs. Combate 

Exploradora            pacto de paz                gana                pierde

Cargura                  pierde               pacto de paz              gana

Combate                  gana                     pierde             pacto de paz

Después, en Nave.interactuarCon(), se aplica la consecuencia: si gana el jugador, se destruye la enemiga (queda en el tablero como restos); si pierde, se destruye la nave del jugador y la misión falla; y si hay pacto, no pasa nada. No hay ningún instanceof ni switch por tipo de nave. Si se agrega un tipo nuevo, se agrega su clase con su fila y un método contraNuevo() en cada una.

--Observer: actualizaciones automáticas--

Usamos el Observable y el Observador que vienen con el Tablero de la cátedra, así hay una sola forma de observar en todo el sistema. Son observables el Tablero, la Bitacora, ModeloAplicacion y el CanalDeEventos del visor. Las vistas implementan Observador y se registran en MainGrafico; el modelo solo conoce la interfaz Observador, nunca a la vista.

Como los avisos llegan desde el hilo de la misión o desde los hilos del visor, la vista no toca los componentes de Swing directamente: encola la actualización con SwingUtilities.invokeLater para que la haga el hilo de la interfaz.

--MVC: interfaz gráfica--

Para lo que inicia el usuario seguimos el flujo del enunciado:

1. El usuario aprieta un botón en la vista.
2. El controlador, que es el ActionListener del botón, recibe el evento.
3. El controlador valida lo que haga falta y llama al modelo (ModeloAplicacion o ServicioDeAcceso).
4. El modelo hace la lógica y devuelve el resultado o cambia su estado.
5. El controlador muestra el resultado o el error en la vista. Si el modelo cambió, además las vistas se enteran por Observer.

Los controladores conocen a las vistas por interfaces (IVistaAcceso, IVistaCreacion, IVistaControl, IVistaTripulantes, IVistaVisor), así que se podría cambiar un panel sin tocar el controlador. En el acceso, las responsabilidades quedan separadas como pide la guía: el formato (largo del usuario, contraseña repetida) lo valida la vista, el controlador coordina, las reglas y excepciones están en ServicioDeAcceso y las consultas en el DAO.

La misión se ejecuta en un hilo aparte (un Thread con EjecucionDeMision como Runnable). Si corriera en el hilo de la interfaz, los Thread.sleep de cada turno congelarían la ventana y no se vería el tablero moverse. ModeloAplicacion no deja lanzar una segunda misión mientras hay una en curso.

--DAO y DTO: persistencia de usuarios--

Solo se persisten las credenciales, como pide el enunciado. UsuarioDTO es el objeto que viaja entre el servicio y la base; UsuarioDAO es la interfaz con las operaciones, y UsuarioDAOJDBC la implementa con JDBC y PreparedStatement sobre H2. ServicioDeAcceso depende de la interfaz, no de la implementación: en las pruebas le pasamos UsuarioDAOEnMemoria y funciona igual.

La contraseña nunca se guarda: se guarda el hash SHA-256 de la contraseña con una sal aleatoria propia de cada usuario. El esquema está en database/schema.sql y ConexionBD lo crea al arrancar si no existe.

Lo elegimos porque el resto del sistema no sabe que hay una base de datos: si mañana se cambia H2 por otra cosa, solo cambia el DAO. Esto también se usa al arrancar: si no se puede abrir la base H2, MainGrafico avisa y usa UsuarioDAOEnMemoria, y el resto del sistema funciona igual (los usuarios duran solo esa ejecución).

--Productor-Consumidor con monitores: visor del universo paralelo--

La PlataformaDeCarga es el monitor. Sus métodos depositar() y retirar() son synchronized, así en un instante solo una entidad la usa. Las condiciones de guarda se evalúan en un while (plataforma llena para depositar, vacía para retirar): quien no puede seguir hace wait() y suelta el cerrojo, y al despertar vuelve a comprobar la condición, porque puede que otro hilo se le haya adelantado. Quien cambia el estado de la plataforma hace notifyAll() para despertar a todos los que esperan.

GeneradoraDeEnergia y NaveConsumidora implementan Runnable, y UniversoParalelo le arma a cada una su Thread. Usamos Runnable y no herencia de Thread para separar la tarea (qué hace la entidad) del hilo que la ejecuta; así las clases del modelo no heredan nada de Thread que no necesitan. Los botones del visor crean nuevas entidades mientras la simulación corre. Los mensajes del enunciado ("Batería producida por...", "...esperando a que se libere espacio...", "...solicitando batería", etc.) se publican en el CanalDeEventos, que los manda al visor por Observer.

Cada generadora produce 10 baterías y cada nave consume 10, y después terminan y avisan en el visor. Así se puede ver el caso en que quedan consumidoras esperando porque ya no hay quién produzca, y se sigue la ejecución sin que la consola crezca sin fin.

4. ravesía por el tablero

Cada nave tiene su tablero de 20x20, cargado con 30 asteroides, 8 estaciones de carga (la mitad fijas y la mitad porcentuales) y 4 naves enemigas de tipo al azar. La nave arranca en el centro.

Mientras la nave esté viva y no haya llegado, cada turno hace:

A. Turno de la nave

Inspección: si en la casilla actual hay un elemento, la nave interactúa con él y se aplican las consecuencias. Si la nave queda destruida, la misión falla en ese momento. Si la nave no se movió, el mismo encuentro no se repite.
Escaneo: el radar de corto alcance mira si en la próxima casilla de la ruta hay un obstáculo.
Movimiento: si hay un obstáculo y la nave tiene energía para el warp, usa saltarWarp() para esquivarlo; si no, hace un salto normal. Si el warp no se puede completar después de varios turnos de espera, la nave sigue con saltos normales para no quedarse quieta para siempre.

B. Turno del tablero: el Destino mueve los elementos al azar.

Al final de cada turno hay un Thread.sleep para que se vea el avance en pantalla.

Si la nave llega con un warp sin terminar, sigue los turnos que hagan falta para que el motor se enfríe y quede Disponible.

La nave se destruye (y la misión falla) cuando el desgaste llega a 100, o la energía o el combustible llegan a 0. Esto se controla en la nave después de cada operación que gasta recursos: los saltos, los impactos de asteroides y los consumos de la misión. No es una excepción, porque no es un error de quien llama sino una consecuencia normal del viaje: la nave queda marcada como destruida y la misión lo consulta en cada turno.

5. Principios SOLID

-S (responsabilidad unica): cada clase hace una sola cosa. Recursos cuida los recursos, MotorWarp el motor, los radares solo observan el tablero, Bitacora guarda eventos, BalanceDeRecursos lleva las cuentas del informe, y lo que se muestra está solo en las vistas.

-O (abierto/cerrado): para agregar una mision, un estado del motor o un adicional de haberes se agrega una clase, sin tocar las que ya estan.

-L (sustitucion de Liskov): cualquier mision se puede ejecutar como Mision, cualquier nave se usa como Nave, cualquier elemento del tablero como ElementoDelUniverso y cualquier decorador como Haber, sin que el que los usa sepa cuál es.

-I (segregacion de interfaces): sigue siendo lo más flojo. AsistenteDeComando creció en la E2 porque la usan la interfaz y las misiones. Lo compensamos con interfaces chicas en otros lugares: ElementoDelUniverso, UsuarioDAO, Observador y una interfaz por cada vista.

-D (inversion de dependencias): Universo y Mision dependen de AsistenteDeComando y no de AsistenteDeComandoBasico; ServicioDeAcceso depende de UsuarioDAO y no de UsuarioDAOJDBC; los controladores dependen de las interfaces IVista, y el modelo solo conoce Observador.


6. Pedidos de diseño de la consigna

-"El centro de control no debe depender de como se construyen los objetos que guarda": Universo recibe los asistentes ya armados y las naves las crea NaveFactory.

-"Si aparece otra variante de asistente, debe poder registrarse sin cambiar el centro de control": Universo trabaja con la interfaz AsistenteDeComando.

-"Agregar un tipo de mision no debe modificar las existentes": con Template Method se agrega una subclase de Mision.

-"Agregar un estado no debe llenar la nave de condicionales": con State se agrega una subclase de Estado. La nave no tiene ningun if sobre el estado del motor.

-"Reemplazar el programa principal por una pantalla no debe cambiar el modelo": en la E2 lo hicimos. El modelo no usa System.out, Scanner ni Swing; devuelve objetos (InformeMision, Haber, la lista de eventos) y avisa los cambios por Observer. Las mismas clases las usan la interfaz gráfica y los mains de prueba.

-"El resultado del combate depende solo del tipo de cada nave y se resuelve con Double Dispatch": ver sección 3.

-"Solo se persisten las credenciales": la única tabla es usuario; naves, tripulantes y misiones viven solo en memoria

7. Contratos y errores

-Precondiciones: son obligación del que llama (un null, una cantidad negativa, un tipo de nave que no existe). Se comprueban con assert en el método que protegen, y se activan con java -ea (el pom ya lo hace). Lo mismo para postcondiciones e invariantes: Nave, Recursos, MotorWarp y Mision tienen su método invariante(), que se controla con assert.

-Situaciones del dominio: son cosas que pueden pasar aunque el que llama haga todo bien (no alcanza el combustible, una carga supera 100, un tripulante con id repetido, usuario ya registrado, credenciales inválidas). Se informan con excepciones checked: las de la nave heredan de NaveException y las del acceso de AccesoException, así el que llama está obligado a hacer algo.

Las operaciones que fallan no dejan cambios a medias, porque primero se verifica todo y después se modifica. El asistente atrapa las excepciones del dominio, las anota en la bitácora como ERROR y las vuelve a lanzar; en la interfaz, el controlador las muestra como mensaje.

Reglas de la tripulación: no se admiten dos tripulantes con el mismo id (se rechaza con TripulacionInvalidaException). Los tripulantes se pueden ir asignando de a uno, y para lanzar una misión la tripulación tiene que estar lista: al menos un capitán y 5 tripulantes en total, como pide la ficha de inicio. Lo controla una sola operación, tripulacionLista(), que usa la preparación de la misión.


8. Decisiones que tomamos

-El universo guarda asistentes y no naves, porque la consigna dice que toda consulta a una nave pasa por su asistente.

-La mision no conoce a la nave, solo al asistente. Asi la mision no puede saltearse la bitacora ni tocar los recursos por su cuenta.

-Una mision se hace una sola vez. Si se quiere repetir se crea otra, como dice la consigna (si dos naves hacen el mismo tipo de mision son dos misiones distintas).

-Los tipos de nave por ahora solo cambian los recursos iniciales, que el enunciado de E1 lo permite. Los dejamos como subclases para poder agregarles comportamiento en E2.

-requiereMantenimiento() avisa cuando el desgaste llega a 80 y realizarMantenimiento() lo vuelve a 0.

-Cada nave tiene su propio tablero. Si se cambia de nave activa, se ve el universo de esa nave.

-La carga de combustible y energía y el mantenimiento solo se pueden hacer en la base de origen (donde arrancó la nave): en el medio del universo no hay de dónde sacar combustible ni quién repare la nave. Si la nave quedó lejos, primero tiene que volver con M-03. Durante el viaje, la única forma de recargar son las estaciones de carga del tablero.

-El motor ya no salta al cerrar la misión como en la E1: ahora los saltos son el movimiento real por el tablero durante la travesía.

-Lo que se entrega o se gana al final se verifica al llegar, pero se aplica recién en cerrar, como pide el enunciado. Si en el camino la nave gastó lo que tenía que entregar, la misión falla.

-Las naves enemigas no son obstáculos: no se esquivan con warp, el encuentro se resuelve con la batalla. Solo los asteroides son obstáculos.

-Una nave enemiga destruida queda en el tablero como restos y ya no tiene efecto.

-Usamos el Observable y el Observador de la cátedra en todo el sistema, en vez de java.util.Observer (que está deprecado) o una versión propia.

-La misión corre en su propio hilo para que la interfaz no se congele, y no se pueden lanzar dos misiones a la vez.

-La contraseña se guarda como hash con sal y no en texto plano, aunque el enunciado no lo pida, porque guardar contraseñas en texto plano es un error de seguridad.

9. Uso de IA

Lo que usamos de IA esta en docs/uso-ia.md.

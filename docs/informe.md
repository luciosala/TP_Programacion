Informe de diseño - Entrega 1

TPG 2026 - Programación C


1. Idea general

El sistema tiene un punto de entrada, el Universo. Ahí se registran las naves. Pero el universo no guarda las naves directamente, sino sus asistentes de comando, porque toda orden o consulta a una nave tiene que pasar por su asistente.

Una orden sigue este camino:

Main -> Universo -> AsistenteDeComando -> Nave -> Recursos / MotorWarp

y el asistente va anotando todo en la Bitacora.

Las misiones se le encomiendan a un asistente. La mision sabe que necesita y en que pasos se hace, pero no toca la nave. Le pide al asistente que verifique los recursos, que los consuma, que ordene el salto y que registre lo que paso.


2. Responsabilidades de cada clase

-Universo: registra los asistentes, devuelve uno por id y deja elegir la nave con la que se trabaja (una sola a la vez). Conoce a la interfaz AsistenteDeComando.

-AsistenteDeComando: es la interfaz con todo lo que se le puede pedir a una nave.

-AsistenteDeComandoBasico: recibe las ordenes, se las pasa a la nave, atrapa los errores y deja todo anotado en la bitacora. No hace la logica interna de la nave. Conoce a la Nave y a la Bitacora.

-Nave (abstracta): tiene id, tipo, tripulacion, recursos y motor. Lo de recursos se lo delega a Recursos y lo del motor a MotorWarp.

-Exploradora, Carguera y Combate: los tres tipos de nave. Por ahora solo cambian los recursos con los que arrancan.

-NaveFactory: es el unico lugar donde se crean naves.

-Recursos: maneja combustible, energia y desgaste, y controla que nunca se salgan de 0 a 100.

-MotorWarp y sus estados: el motor y sus cuatro estados. Cada estado sabe a cual puede pasar.

-Mision (abstracta): define el ciclo preparar, ejecutar, evaluar y cerrar. Conoce al asistente.

-MisionIntercepcion, MisionRecoleccion y MisionRetornoSeguro: son M-01, M-02 y M-03. Solo completan lo que cambia en cada una.

-InformeMision: el resumen que se arma al cerrar la mision. No se puede modificar.

-Bitacora y EventoBitacora: guardan los eventos en orden. Cada evento tiene fecha y hora, categoria y descripcion, y no se puede modificar.

-Tripulante, Cargo y Origen: los datos de la tripulacion.

-Haber y los decoradores: calculan el sueldo mensual de cada tripulante.

-Main: simula al usuario. Es la unica clase que imprime por consola.


3. Patrones

--State: Motor Warp--

El motor puede estar en Disponible, Preparando salto, En warp o Enfriamiento. En vez de tener un switch con el estado actual, cada estado es una clase que hereda de Estado y sabe que hacer con cada orden. Si la transicion es valida devuelve el estado siguiente y si no tira TransicionInvalidaException. MotorWarp solo guarda el estado actual y le delega todo.

Lo elegimos porque asi una transicion invalida no cambia nada (el estado se reemplaza solo si la transicion salio bien), y porque si hay que agregar un estado nuevo se agrega una clase y no hay que llenar la nave de if.

--Factory: creacion de naves--

NaveFactory.createNave(id, tipo) arma la nave con los recursos iniciales de la ficha de inicio y con un motor nuevo en Disponible. Los constructores de Exploradora, Carguera y Combate son package private, asi que desde Main no se pueden crear a mano.

Lo elegimos para que el que usa el sistema no tenga que saber con que valores arranca cada tipo de nave, y para que toda nave empiece en un estado valido.

--Template Method: misiones--

El metodo realizarMision() de Mision es final y siempre hace los cuatro pasos en el mismo orden:

1) preparar: verifica que el motor este disponible, que haya tripulacion minima y que alcancen los recursos.
2) ejecutar: consume el combustible y el desgaste y llama a lo propio de cada mision.
3) evaluar: se fija si se cumplio el objetivo y cobra la energia de la accion final.
4) cerrar: si salio bien la nave prepara el salto y salta (como pide la aclaracion de la catedra), se registra el resultado y se arma el informe.

Las misiones concretas no pueden cambiar ese orden, solo completan los metodos abstractos (ejecutaMision, condicionDeExito, costoAccionFinal y descripcionObjetivo). Entonces una mision nueva es una subclase nueva y no hay que tocar las otras.

--Decorator: haberes--

HaberBase tiene el sueldo del cargo y encima se van "envolviendo" los adicionales: AdicionalAntiguedad, SubsidioPorOrigen y, solo para el consejero, AdicionalConsejos. Cada decorador suma su parte y se puede consultar cuanto aporta cada concepto por separado.

Lo usamos para no tener una clase por cada combinacion de cargo, origen y antiguedad, que serian un monton. La que arma la cadena es LiquidacionHaberes.


4. Principios SOLID

-S (responsabilidad unica): cada clase hace una sola cosa. Recursos cuida los recursos, MotorWarp el motor, Bitacora guarda eventos, y lo que se muestra por pantalla esta solo en Main.

-O (abierto/cerrado): para agregar una mision, un estado del motor o un adicional de haberes se agrega una clase, sin tocar las que ya estan.

-L (sustitucion de Liskov): cualquier mision se puede ejecutar como Mision, cualquier nave se usa como Nave y cualquier decorador como Haber, sin que el que los usa sepa cual es.

-I (segregacion de interfaces): es lo mas flojo que tenemos. AsistenteDeComando quedo bastante grande porque la usan Main y las misiones. Mas adelante se podria separar en una interfaz para el usuario y otra para las misiones.

-D (inversion de dependencias): Universo y Mision dependen de la interfaz AsistenteDeComando y no de AsistenteDeComandoBasico. Por eso se puede registrar otra variante de asistente sin cambiar el universo.


5. Pedidos de diseño de la consigna

-"El centro de control no debe depender de como se construyen los objetos que guarda": Universo recibe los asistentes ya armados y las naves las crea NaveFactory.

-"Si aparece otra variante de asistente, debe poder registrarse sin cambiar el centro de control": Universo trabaja con la interfaz AsistenteDeComando.

-"Agregar un tipo de mision no debe modificar las existentes": con Template Method se agrega una subclase de Mision.

-"Agregar un estado no debe llenar la nave de condicionales": con State se agrega una subclase de Estado. La nave no tiene ningun if sobre el estado del motor.

-"Reemplazar el programa principal por una pantalla no debe cambiar el modelo": el modelo no usa System.out ni Scanner. Devuelve objetos (InformeMision, Haber, la lista de eventos) y el que los muestra decide como.


6. Contratos y errores

Las precondiciones se controlan en la clase que cuida el dato. Por ejemplo Recursos no deja cargar por encima de 100 ni consumir mas de lo que hay, Tripulante no acepta antiguedad negativa y EventoBitacora no acepta descripciones vacias.

Los errores del dominio heredan de NaveException, que es checked, asi el que llama esta obligado a hacer algo con el error. Las operaciones que fallan no dejan cambios a medias porque primero se verifica todo y despues se modifica. El asistente atrapa estos errores, los anota en la bitacora como ERROR y los vuelve a lanzar.

Los argumentos invalidos (nulos o cantidades negativas) tiran IllegalArgumentException, porque son errores de programacion y no situaciones del dominio.


7. Decisiones que tomamos

-El universo guarda asistentes y no naves, porque la consigna dice que toda consulta a una nave pasa por su asistente.

-La mision no conoce a la nave, solo al asistente. Asi la mision no puede saltearse la bitacora ni tocar los recursos por su cuenta.

-Una mision se hace una sola vez. Si se quiere repetir se crea otra, como dice la consigna (si dos naves hacen el mismo tipo de mision son dos misiones distintas).

-Los tipos de nave por ahora solo cambian los recursos iniciales, que el enunciado de E1 lo permite. Los dejamos como subclases para poder agregarles comportamiento en E2.

-requiereMantenimiento() avisa cuando el desgaste llega a 80 y realizarMantenimiento() lo vuelve a 0.


8. Uso de IA

Lo que usamos de IA esta en docs/uso-ia.md.

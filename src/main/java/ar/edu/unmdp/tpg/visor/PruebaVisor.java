package ar.edu.unmdp.tpg.visor;

import ar.edu.unmdp.tpg.visor.modelo.UniversoParalelo;

/**
 * Prueba por consola, sin ventana: corre la simulacion unos segundos con varias
 * generadoras y naves, muestra los eventos y verifica el invariante de la plataforma.
 */
public class PruebaVisor {

    public static void main(String[] args) throws InterruptedException {
        int capacidad = 2;
        UniversoParalelo universo = new UniversoParalelo(capacidad);
        universo.getCanal().agregarObservador(evento ->
                System.out.println("[" + Thread.currentThread().getName() + "] " + evento));

        universo.crearGeneradora();
        universo.crearGeneradora();
        universo.crearNave();
        universo.crearNave();
        universo.crearNave();

        Thread.sleep(8000);
        universo.detenerTodo();
        Thread.sleep(200);

        int max = universo.getPlataforma().getMaximoAlcanzado();
        System.out.println();
        System.out.println("Maximo de baterias a la vez: " + max + " (capacidad " + capacidad + ")");
        System.out.println(max <= capacidad ? "OK: nunca se supero la capacidad" : "ERROR: se supero la capacidad");
    }
}

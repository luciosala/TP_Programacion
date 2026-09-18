final public class Recursos{


    private int combustible;
    private int energia;
    private int desgaste;


    /**
     * Construye los recursos de la nave
     * 
     * Precondiciones:
     * - combustibleInicial está entre 0 y 100, inclusive.
     * - energiaInicial está entre 0 y 100, inclusive.
     * - desgasteInicial está entre 0 y 100, inclusive.
     * 
     * Postcondiciones:
     * - El combustible, la energía y el desgaste quedan inicializados con los valores recibidos por parámetro.
     * 
     * @param combustibleInicial combustible inicial de la nave
     * @param energiaInicial energia inicial de la nave
     * @param desgasteInicial desgaste inicial de la nave
     * @throws IllegalStateException si se incumple alguna precondición. 
     */
    public Recursos(int combustibleInicial, int energiaInicial, int desgasteInicial) {
        if (combustibleInicial < 0 || combustibleInicial > 100)
            throw new IllegalStateException("El combustible debe empezar entre 0 y 100");
        
        if (energiaInicial < 0 || energiaInicial > 100)
            throw new IllegalStateException("La energía debe empezar entre 0 y 100");

        if (desgasteInicial < 0 || desgasteInicial > 100)
            throw new IllegalStateException("El desgaste debe empezar entre 0 y 100");
        
        this.combustible = combustibleInicial;
        this.energia = energiaInicial;
        this.desgaste = desgasteInicial;
    }

    public int getCombustible(){return this.combustible;}
    public int getEnergia(){return this.energia;}
    public int getDesgaste(){return this.desgaste;}

    /**
     * Carga combustible en los recursos de la nave.
     *
     * Precondiciones:
     * - cantidad es mayor que 0.
     * - cantidad no supera la capacidad restante: 100 - combustible.
     *
     * Postcondiciones:
     * - El combustible aumenta exactamente en cantidad y permanece entre 0 y 100.
     * - Si la carga se rechaza, todos los recursos permanecen sin cambios.
     *
     * @param cantidad cantidad de combustible a cargar
     * @throws IllegalArgumentException si cantidad es menor o igual a 0
     * @throws IllegalStateException si la carga supera la capacidad máxima
     */
    public void cargarCombustible(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad de combustible debe ser positiva");
        }

        if (cantidad > 100 - combustible) {
            throw new IllegalStateException("La carga de combustible supera la capacidad máxima de 100");
        }

        combustible += cantidad;
    }

    /**
     * Carga energía en los recursos de la nave.
     *
     * Precondiciones:
     * - cantidad es mayor que 0.
     * - cantidad no supera la capacidad restante: 100 - energia.
     *
     * Postcondiciones:
     * - La energía aumenta exactamente en cantidad y permanece entre 0 y 100.
     * - Si la carga se rechaza, todos los recursos permanecen sin cambios.
     *
     * @param cantidad cantidad de energía a cargar
     * @throws IllegalArgumentException si cantidad es menor o igual a 0
     * @throws IllegalStateException si la carga supera la capacidad máxima
     */
    public void cargarEnergia(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad de energía debe ser positiva");
        

        if (cantidad > 100 - energia) 
            throw new IllegalStateException("La carga de energía supera la capacidad máxima de 100");
        

        energia += cantidad;
    }
    public void consumirEnergia(int cantidad){
        if (cantidad <= 0) 
            throw new IllegalArgumentException("La cantidad a consumir debe ser positiva");
        if (cantidad > energia)
            throw new IllegalStateException("No hay energia suficiente para consumir");
        energia-=cantidad;
    }
    public void consumirCombustible(int cantidad){          //valida que no se haga negativo aunque los llamados en Mision lo chequeen , pero se confirma por si se llega a llamar desde otro lado que no valide
         if (cantidad <= 0) 
            throw new IllegalArgumentException("La cantidad a consumir debe ser positiva");
        if (cantidad > combustible)
            throw new IllegalStateException("No hay combustible suficiente para consumir");
        combustible-=cantidad;
    }
    public void agregarDesgaste(int cantidad){
            if (cantidad <= 0) 
                throw new IllegalArgumentException("La cantidad a desgastar debe ser positiva");
             if (cantidad +desgaste >100 )
                throw new IllegalStateException("Se llego al limite de desgaste");
            desgaste+=cantidad;
    }

    /**
     * Consulta si los recursos requieren mantenimiento.
     *
     * Postcondiciones:
     * - Devuelve true si el desgaste es mayor o igual a 80; false en caso contrario.
     *
     * @return si se necesita mantenimiento.
     */
    public boolean requiereMantenimiento() {
        return desgaste >= 80;
    }

    /**
     * Realiza el mantenimiento de los recursos de la nave.
     *
     * Postcondiciones:
     * - El desgaste queda en 0.
     */
    public void realizarMantenimiento() {
        desgaste = 0;
    }
}

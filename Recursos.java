final public class Recursos{


    private int combustible;
    private int energia;
    private int desgaste;

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

    public void cargarCombustible(int cantidad) throws LimiteRecursoExcedidoException {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad de combustible debe ser positiva");
        }

        if (cantidad > 100 - combustible) {
            throw new LimiteRecursoExcedidoException("La carga de combustible supera la capacidad máxima de 100");
        }

        combustible += cantidad;
    }

    public void cargarEnergia(int cantidad) throws LimiteRecursoExcedidoException {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad de energía debe ser positiva");
        }

        if (cantidad > 100 - energia)
            throw new LimiteRecursoExcedidoException("La carga de energía supera la capacidad máxima de 100");


        energia += cantidad;
    }
    public void consumirEnergia(int cantidad) throws RecursoInsuficienteException {
        if (cantidad <= 0)
            throw new IllegalArgumentException("La cantidad a consumir debe ser positiva");
        if (cantidad > energia)
            throw new RecursoInsuficienteException("No hay energia suficiente para consumir");
        energia-=cantidad;
    }
    public void consumirCombustible(int cantidad) throws RecursoInsuficienteException {          //valida que no se haga negativo aunque los llamados en Mision lo chequeen , pero se confirma por si se llega a llamar desde otro lado que no valide
         if (cantidad <= 0)
            throw new IllegalArgumentException("La cantidad a consumir debe ser positiva");
        if (cantidad > combustible)
            throw new RecursoInsuficienteException("No hay combustible suficiente para consumir");
        combustible-=cantidad;
    }
    public void agregarDesgaste(int cantidad) throws LimiteRecursoExcedidoException {
            if (cantidad <= 0)
                throw new IllegalArgumentException("La cantidad a desgastar debe ser positiva");
             if (cantidad +desgaste >100 )
                throw new LimiteRecursoExcedidoException("Se llego al limite de desgaste");
            desgaste+=cantidad;
    }

    public boolean requiereMantenimiento() {
        return desgaste >= 80;
    }

    public void realizarMantenimiento() {
        desgaste = 0;
    }
}

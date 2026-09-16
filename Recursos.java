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

    public void cargarCombustible(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad de combustible debe ser positiva");
        }

        if (cantidad > 100 - combustible) {
            throw new IllegalStateException("La carga de combustible supera la capacidad máxima de 100");
        }

        combustible += cantidad;
    }

    public void cargarEnergia(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad de energía debe ser positiva");
        }

        if (cantidad > 100 - energia) {
            throw new IllegalStateException("La carga de energía supera la capacidad máxima de 100");
        }

        energia += cantidad;
    }

    public boolean requiereMantenimiento() {
        return desgaste >= 80;
    }

    public void realizarMantenimiento() {
        desgaste = 0;
    }
}

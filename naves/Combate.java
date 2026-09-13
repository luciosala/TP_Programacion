public class Combate extends Nave {
    private final int capacidadDeAtaque;
    private final int capacidadDeDefensa;

    public Combate(String id, TipoNave tipo, Recursos recursos, MotorWarp motorWarp){
      super(id, tipo, recursos, motorWarp);
      this.combustible=80;
      this.energia=100;
    }

}
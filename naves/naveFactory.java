
    public class naveFactory {

        public static Nave createNave(String id, String tipo) {
            MotorWarp motorWarp = new MotorWarp();
            if (tipo.equalsIgnoreCase("combate")) {
                Recursos recursos = new Recursos(80, 100, 0);
                return new Combate(id, tipo, recursos, motorWarp);
            } else if (tipo.equalsIgnoreCase("carguero")) {
                Recursos recursos = new Recursos(100, 60, 0);
                return new Carguera(id, tipo, recursos, motorWarp);
            } else if (tipo.equalsIgnoreCase("exploradora")) {
                Recursos recursos = new Recursos(60, 80, 0);
                return new Exploradora(id, tipo, recursos, motorWarp);
            } else {
                throw new IllegalArgumentException("tipo desconocido nave: " + tipo);
            }
        }



    
    }
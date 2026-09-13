    public class naveFactory {

        public static nave createNave(String type) {
            if (type.equalsIgnoreCase("combate")) {
                return new FighterNave();
            } else if (type.equalsIgnoreCase("carguero")) {
                return new BomberNave();
            } else if (type.equalsIgnoreCase("exploradora")) {
                return new ExplorerNave();
            } else {
                throw new IllegalArgumentException("Unknown nave type: " + type);
            }
        }
    }

    public class naveFactory {

        /**
         * Crea una nave con la configuración inicial correspondiente a su tipo.
         *
         * Precondiciones:
         * - tipo no es nulo ni vacio.
         * - Los argumentos cumplen el contrato del constructor de Nave.
         *
         * Postcondiciones:
         * - Devuelve una nave del tipo solicitado con la configuración inicial del TP.
         *
         * @param id identificador de la nave
         * @param tipo tipo de nave solicitado
         * @return nave creada
         * @throws IllegalArgumentException si algún argumento es inválido
         */
        public static Nave createNave(String id, String tipo) {
            MotorWarp motorWarp = new MotorWarp();
            if (tipo == null || tipo.isBlank()){
                throw new IllegalArgumentException("El tipo no puede ser nulo ni vacio");
            }
            else{
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



    
    }

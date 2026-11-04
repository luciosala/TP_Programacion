package ar.edu.unmdp.tpg.naves;

import ar.edu.unmdp.tpg.motorwarp.MotorWarp;

    /**
     * PATRON FACTORY: unico punto donde se crean naves.
     *
     * Quien pide una nave indica solo su tipo; la fabrica elige la subclase, le da los
     * recursos iniciales que le corresponden y le arma su propio Motor Warp. Asi el
     * cliente no depende de las clases concretas y los valores iniciales de cada tipo
     * quedan definidos en un solo lugar.
     */
    public class NaveFactory {

        /**
         * Consulta si la fabrica sabe crear naves del tipo indicado. Sirve para que
         * el cliente (el main, o mas adelante la vista) cumpla la precondicion de
         * createNave antes de invocarla.
         *
         * @param tipo tipo de nave
         * @return true si tipo es combate, carguero o exploradora (sin distinguir mayusculas)
         */
        public static boolean esTipoValido(String tipo) {
            return tipo != null
                && (tipo.equalsIgnoreCase("combate")
                    || tipo.equalsIgnoreCase("carguero")
                    || tipo.equalsIgnoreCase("exploradora"));
        }

        /**
         * Crea una nave con la configuración inicial correspondiente a su tipo.
         *
         * Precondiciones (assert):
         * - id no es nulo ni vacio.
         * - esTipoValido(tipo) es true.
         *
         * Postcondiciones:
         * - Devuelve una nave del tipo solicitado con la configuración inicial del TP.
         * - La nave comienza con el motor Disponible, sin desgaste y sin tripulantes.
         *
         * @param id identificador de la nave
         * @param tipo tipo de nave solicitado
         * @return nave creada
         */
        public static Nave createNave(String id, String tipo) {
            assert id != null && !id.isBlank() : "El id no puede ser nulo ni vacio";
            assert esTipoValido(tipo) : "Tipo de nave desconocido: " + tipo;

            MotorWarp motorWarp = new MotorWarp();
            Nave nave;
            if (tipo.equalsIgnoreCase("combate")) {
                nave = new Combate(id, tipo, new Recursos(80, 100, 0), motorWarp);
            } else if (tipo.equalsIgnoreCase("carguero")) {
                nave = new Carguera(id, tipo, new Recursos(100, 60, 0), motorWarp);
            } else if (tipo.equalsIgnoreCase("exploradora")) {
                nave = new Exploradora(id, tipo, new Recursos(60, 80, 0), motorWarp);
            } else {
                // Solo se llega aca con las aserciones desactivadas.
                throw new IllegalArgumentException("Tipo de nave desconocido: " + tipo);
            }

            assert nave.motorDisponible() && nave.getDesgaste() == 0 && nave.cantidadTripulantes() == 0
                : "Fallo postcondición: la nave debe comenzar en un estado válido";
            return nave;
        }
    }

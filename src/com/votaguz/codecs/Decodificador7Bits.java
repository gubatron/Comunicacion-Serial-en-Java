package com.votaguz.codecs;

public class Decodificador7Bits {
   
    /** Asi puedes utilizar esta clase con un mapa, con un arreglo o lo que quieras, simplemente utilizas
     * una implementacion de LectorDeTabla
     * @author gubatron
     *
     */
    interface LectorDeTabla {
        /** Dado un indice entero obtiene el caracter adecuado en una tabla de simbolos*/
        public Character obtenCaracter(int indiceCaracterEnTabla);
    }

    /**
     *
     * @param buffer - Arreglo de bytes leidos.
     * @param tabla - Tabla de simbolos.
     * @return
     */
    public static String decodificaArregloDeBytes(byte[] buffer,
            LectorDeTabla lectorDeTabla) {
       
        StringBuilder result = new StringBuilder();
       
        /** indice en el arreglo mientras iteramos */
        int i = 0;
       
        /** el byte actual del arreglo */
        byte b = 0;

        /**
         * Aqui quedara el caracter de 7 bits, una vez que hayamos leido y concatenado bits restantes.
         * Utilizo este byte como indice para sacar el Caracter de la tabla dada.
         */
        byte siete_bits_resultantes = 0;

        /** aqui el numero de bits sobrantes de la iteracion anterior. */
        byte num_bits_acarreo = 1;

        /** Aca almaceno los bits sobrantes del byte actual, para concatenarlos al proximo conjunto de bits a leer */
        byte bits_acarreo = 0;
        byte bits_lectura = 0;       

        while (i < buffer.length) {
            // el byte con que vamos a trabajar.
            b = buffer[i]; //sale ya con solo los 7 bits que me importan
            //System.out.print(String.format("i=%d  num_bits_sobrantes = %d b => %s ", i, num_bits_acarreo,Integer.toBinaryString(b)));
            i++;
           
            /** Si tenemos los 7 bits de una, como al comienzo... cuquita.*/
            if (num_bits_acarreo == 1) {
                //son los 7 bits de la izquierda, los muevo todos una vez a la derecha.
                siete_bits_resultantes = (byte) (b >> 1);

                //guardo el bit de acarreo para la proxima iteracion.
                bits_acarreo =  (byte) (b & 1);
               
                System.out.print(" bits_acarreo = " + Integer.toBinaryString(bits_acarreo));
            } else {
                //Nos sale concatenar los bits de acarreo viejos al comienzo
                //...a la izquierda
                byte bits_acarreo_viejos_izquierda = (byte) (bits_acarreo << (8 - num_bits_acarreo));
               
                //ahora tengo que leer los bits mas a la izquierda sin contar el nuevo acarreo
                bits_lectura = (byte) (b >> num_bits_acarreo);
               
                //concateno acarreo viejo (izq) con bits_leidos nuevos (der)
                siete_bits_resultantes = (byte) (bits_acarreo_viejos_izquierda | bits_lectura);
               
                System.out.print(String.format(" bits_acarreo_viejos = %s, bits_lectura = %s", Integer.toBinaryString(bits_acarreo), Integer.toBinaryString(bits_lectura)));
               
                //guardamos los nuevos bits de acarreo para la proxima iteracion
                //esto lo hago haciendo un AND con una potencia de 2  menos 1.
                // b & 0x0000001 = 1 << 1 - 1 = 2 - 1 = 1
                // b & 0x0000011 = 1 << 2 - 1 = 4 - 1 = 3
                // b & 0x0000111 = ... 7, 15, 31
                bits_acarreo = (byte) (b & ((1 << num_bits_acarreo) - 1));
               
                //System.out.print(String.format(" (nuevo acarreo = %s)", Integer.toBinaryString(bits_acarreo)));
            }

            if (num_bits_acarreo < 7)
                num_bits_acarreo+=1;
            else
                num_bits_acarreo = 1;

            //System.out.println(" CARACTER 7 bits => " + Integer.toBinaryString(siete_bits_resultantes));
           
            result.append(lectorDeTabla.obtenCaracter(siete_bits_resultantes));
        }
       
        return result.toString();
    }
   /**
    public static void main(String [] args) {
        //Ejemplo leyendo de una tabla de caracteres almacenada en un sencillo arreglo de Character
        final Character[] tablaGSM = new Character[128];
        for (int i = 0; i < tablaGSM.length; i++) {
            //voy a llenar esto con basura... por simplicidad
            tablaGSM[i] = new Character('a');
        }

       
        //Supon que tienes el siguiente arreglo de bytes que leiste por USB
        byte[] buffer = new byte[140];
        for (int i=0; i < 140; i++) {
            buffer[i]=85; //En binario luce como 0b1010101
        }
       
        //Asi utilizarias la clase para leer de 7 bits en 7 bits y obtener los caracteres correspondientes de la tabla.
        String decodeByteArray = LectorDe7Bits.decodificaArregloDeBytes(buffer, new LectorDeTabla() {

            //Un LectorDeTabla que sabe leer de un arreglo de simbolos, pudieras hacer lo mismo con HashMap si tu tabla
            //esta implementada en un Hashmap, es solo reimplementar el metodo "sacaCaracter" aqui abajo.

            public Character obtenCaracter(int indiceCaracterEnTabla) {
                return tablaGSM[indiceCaracterEnTabla & 0x7f];
               
                //si tuvieras tu tabla en un mapa harias
                //return tablaGSM.get(indiceCaracterEnTabla);
            }

        });
        //System.out.println(decodeByteArray);
    }
    */
}

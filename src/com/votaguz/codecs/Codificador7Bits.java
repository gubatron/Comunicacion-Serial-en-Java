package com.votaguz.codecs;

import java.util.HashMap;
import java.util.Map;

public class Codificador7Bits implements Codificador {

	public static void main(String[] args) {
		pruebaCINTEL_RQD_07();
	}

	public static void pruebaCINTEL_RQD_07() {
		/**
		 * Prueba con ejemplo de la lamina 85 de
		 * "curso de redes inalambricas de banda libre y moviles celulares.pdf"
		 */
		Codificador7Bits encoder = new Codificador7Bits();
		String entrada = "CINTEL RQD 07";
		byte[] resultado7bits = encoder.codificar(entrada);
		byte b;

		int length = entrada.length();
		for (int i = 0; i < length; i++) {
			b = resultado7bits[i];
			System.out.println(entrada.charAt(i) + " " + b + " "
					+ Integer.toString(b, 2));
		}

		System.out.println("["
				+ encoder.normalizacionHexadecimal(resultado7bits)
						.toUpperCase() + "]");
		System.out.println("[C3A4935A6482A45122087603]");
		// System.out.println("[D16A11040D4E83206A11044D0E83A0221334AC329F]");
	}

	public static final Map<String, Byte> ALPHABET_7BITS = new HashMap<String, Byte>();

	/**
	 * Toma un string y utiliza la tabla de simbolos para crear un arreglo de
	 * bytes codificados en 7 bits.
	 */
	public byte[] codificar(String input) {
		byte[] encodedUserDataBytes = new byte[input.length() + 1];

		int len = input.length();

		// 1. Sacamos la representacion de la tabla de 7 bits.
		for (int i = 0; i < len; i++) {
			char c = input.charAt(i);

			Byte c7bit = ALPHABET_7BITS.get(Character.toString(c));

			if (c7bit != null) {
				encodedUserDataBytes[i] = c7bit;
			}
		}

		// agrego 00 hexa al final.
		encodedUserDataBytes[input.length()] = 0;

		return encodedUserDataBytes;
	}

	/**
	 * Toma un arreglo de bytes codificados en 7 bits y aplica el algoritmo de
	 * normalizacion a 8 bits y esos bytes son representados en un String
	 * formado por Hexadecimales.
	 * 
	 * @param input
	 *            -
	 * @return
	 */
	public String normalizacionHexadecimal(byte[] input) {
		/*
		 * 7 bits [1000011,1001001,1010100,1010100,1000101,1001100, 0100000,
		 * 1010010, 1010001,0100000,0110000,0110111,0000000 1000011,
		 */

		/**
		 * Para construir el String de resultado un caracter a la vez sin crear
		 * instancias de String por cada iteracion.
		 */
		StringBuilder resultBuilder = new StringBuilder();

		/** cantidad de bits de acarreo */
		byte numAcarreo = 1;

		/** donde almacenamos los bits de acarreo */
		byte acarreo = 0;

		/** byte procesado actualmente */
		byte actual = 0;

		/** el siguiente byte de donde tomamos los bits de acarreo */
		byte proximo = 0;

		for (int i = 0; i < input.length; i++) {
			actual = (byte) (input[i] & 0xFF);

			// Si aun tenemos un byte mas de donde tomar prestado.
			if (i < input.length - 1) {
				proximo = input[i + 1];
			} else {
				// resultBuilder.append("000000000");
				break;
			}

			/**
			 * Agarro los bits de mas a la derecha del siguiente byte.
			 * 
			 * Esto lo hago con una potencia de 2 menos 1, El exponente es
			 * precisamente el numero de bits a tomar del proximo byte.
			 * */
			acarreo = (byte) (proximo & ((1 << numAcarreo) - 1));
			/** Pego los bits de acarreo, al lado izquierdo del byte actual. */
			actual = (byte) (actual | (acarreo << (8 - numAcarreo)));

			/**
			 * Muevo hacia la derecha el proximo bit para tener espacio libre en
			 * los bits izquierdos cuando hagamos la siguiente iteracion.
			 */
			if (numAcarreo < 6)
			input[i + 1] = (byte) ((proximo & 0xFF) >> (numAcarreo));

			// Concatenamos el byte normalizado a 8bits en su representacion
			// hexadecimal
			// asegurandonos que sea un par de caracteres hexadecimales.
			String hex = Integer.toHexString(actual);
			if (hex.length() >= 2) {
				hex = hex.substring(hex.length() - 2);
			} else if (hex.length() == 1) {
				hex = "0" + hex;
			}

			System.out.println("HEX> " + actual + " " + hex + " numAcarreo = "
					+ numAcarreo);

			// no puedo acarrear mas de 7 bits
			if (++numAcarreo > 7) {
				numAcarreo = 1;
			}

			resultBuilder.append(hex);
		}

		return resultBuilder.toString();
	}

	static {
		// Alfabeto Griego
		ALPHABET_7BITS.put("\u0040", (byte) Integer.parseInt("0000111", 2));
		ALPHABET_7BITS.put("\u0020", (byte) Integer.parseInt("0000100", 2));
		ALPHABET_7BITS.put("\u0394", (byte) Integer.parseInt("0010000", 2));
		ALPHABET_7BITS.put("\u03A6", (byte) Integer.parseInt("0010010", 2));
		ALPHABET_7BITS.put("\u0393", (byte) Integer.parseInt("0010011", 2));
		ALPHABET_7BITS.put("\u039B", (byte) Integer.parseInt("0010100", 2));
		ALPHABET_7BITS.put("\u03A9", (byte) Integer.parseInt("0010101", 2));
		ALPHABET_7BITS.put("\u0398", (byte) Integer.parseInt("0011001", 2));
		ALPHABET_7BITS.put("\u039E", (byte) Integer.parseInt("0011010", 2));
		ALPHABET_7BITS.put("\u03B2", (byte) Integer.parseInt("0011110", 2));
		ALPHABET_7BITS.put("\u03A8", (byte) Integer.parseInt("0010111", 2));
		ALPHABET_7BITS.put("\u03A0", (byte) Integer.parseInt("0010110", 2));
		ALPHABET_7BITS.put("\u03A3", (byte) Integer.parseInt("0011000", 2));

		// Alfabeto Normal
		ALPHABET_7BITS.put("\r", (byte) Integer.parseInt("0000000", 2));
		ALPHABET_7BITS.put("\n", (byte) Integer.parseInt("0001010", 2));
		ALPHABET_7BITS.put(" ", (byte) Integer.parseInt("0100000", 2));
		ALPHABET_7BITS.put("$", (byte) Integer.parseInt("0000010", 2));
		ALPHABET_7BITS.put("!", (byte) Integer.parseInt("0100001", 2));
		ALPHABET_7BITS.put("#", (byte) Integer.parseInt("0100111", 2));
		ALPHABET_7BITS.put("%", (byte) Integer.parseInt("0100101", 2));
		ALPHABET_7BITS.put("&", (byte) Integer.parseInt("0100110", 2));
		ALPHABET_7BITS.put("`", (byte) Integer.parseInt("0100111", 2));
		ALPHABET_7BITS.put("(", (byte) Integer.parseInt("0101000", 2));
		ALPHABET_7BITS.put(")", (byte) Integer.parseInt("0101001", 2));
		ALPHABET_7BITS.put("*", (byte) Integer.parseInt("0101010", 2));
		ALPHABET_7BITS.put("+", (byte) Integer.parseInt("0101011", 2));
		ALPHABET_7BITS.put("'", (byte) Integer.parseInt("0101100", 2));
		ALPHABET_7BITS.put("-", (byte) Integer.parseInt("0101101", 2));
		ALPHABET_7BITS.put(".", (byte) Integer.parseInt("0101110", 2));
		ALPHABET_7BITS.put("/", (byte) Integer.parseInt("0101111", 2));
		ALPHABET_7BITS.put("0", (byte) Integer.parseInt("0110000", 2));
		ALPHABET_7BITS.put("1", (byte) Integer.parseInt("0110001", 2));
		ALPHABET_7BITS.put("2", (byte) Integer.parseInt("0110010", 2));
		ALPHABET_7BITS.put("3", (byte) Integer.parseInt("0110011", 2));
		ALPHABET_7BITS.put("4", (byte) Integer.parseInt("0110100", 2));
		ALPHABET_7BITS.put("5", (byte) Integer.parseInt("0110101", 2));
		ALPHABET_7BITS.put("6", (byte) Integer.parseInt("0110110", 2));
		ALPHABET_7BITS.put("7", (byte) Integer.parseInt("0110111", 2));
		ALPHABET_7BITS.put("8", (byte) Integer.parseInt("0111000", 2));
		ALPHABET_7BITS.put("9", (byte) Integer.parseInt("0111001", 2));
		ALPHABET_7BITS.put(":", (byte) Integer.parseInt("0111010", 2));
		ALPHABET_7BITS.put(";", (byte) Integer.parseInt("0111011", 2));
		ALPHABET_7BITS.put("<", (byte) Integer.parseInt("0111100", 2));
		ALPHABET_7BITS.put("=", (byte) Integer.parseInt("0111101", 2));
		ALPHABET_7BITS.put(">", (byte) Integer.parseInt("0111110", 2));
		ALPHABET_7BITS.put("?", (byte) Integer.parseInt("0111111", 2));
		ALPHABET_7BITS.put("A", (byte) Integer.parseInt("1000001", 2));
		ALPHABET_7BITS.put("B", (byte) Integer.parseInt("1000010", 2));
		ALPHABET_7BITS.put("C", (byte) Integer.parseInt("1000011", 2));
		ALPHABET_7BITS.put("D", (byte) Integer.parseInt("1000100", 2));
		ALPHABET_7BITS.put("E", (byte) Integer.parseInt("1000101", 2));
		ALPHABET_7BITS.put("F", (byte) Integer.parseInt("1001000", 2));
		ALPHABET_7BITS.put("G", (byte) Integer.parseInt("1001001", 2));
		ALPHABET_7BITS.put("H", (byte) Integer.parseInt("1001010", 2));
		ALPHABET_7BITS.put("I", (byte) Integer.parseInt("1001001", 2));
		ALPHABET_7BITS.put("J", (byte) Integer.parseInt("1001100", 2));
		ALPHABET_7BITS.put("K", (byte) Integer.parseInt("1001101", 2));
		ALPHABET_7BITS.put("L", (byte) Integer.parseInt("1001100", 2));
		ALPHABET_7BITS.put("M", (byte) Integer.parseInt("1001101", 2));
		ALPHABET_7BITS.put("N", (byte) Integer.parseInt("1001110", 2));
		ALPHABET_7BITS.put("O", (byte) Integer.parseInt("1001111", 2));
		ALPHABET_7BITS.put("P", (byte) Integer.parseInt("1010000", 2));
		ALPHABET_7BITS.put("Q", (byte) Integer.parseInt("1010001", 2));
		ALPHABET_7BITS.put("R", (byte) Integer.parseInt("1010010", 2));
		ALPHABET_7BITS.put("S", (byte) Integer.parseInt("1010011", 2));
		ALPHABET_7BITS.put("T", (byte) Integer.parseInt("1010100", 2));
		ALPHABET_7BITS.put("U", (byte) Integer.parseInt("1010101", 2));
		ALPHABET_7BITS.put("V", (byte) Integer.parseInt("1010110", 2));
		ALPHABET_7BITS.put("W", (byte) Integer.parseInt("1010111", 2));
		ALPHABET_7BITS.put("X", (byte) Integer.parseInt("1011000", 2));
		ALPHABET_7BITS.put("Y", (byte) Integer.parseInt("1011001", 2));
		ALPHABET_7BITS.put("Z", (byte) Integer.parseInt("1011010", 2));
		// ALPHABET_7BITS.put("@", (byte) Integer.parseInt("1011011",2));
		// ALPHABET_7BITS.put("@", (byte) Integer.parseInt("1011100",2));
		// ALPHABET_7BITS.put("@", (byte) Integer.parseInt("1011110",2));
		// ALPHABET_7BITS.put("@", (byte) Integer.parseInt("0000000",2));
		ALPHABET_7BITS.put("a", (byte) Integer.parseInt("1100001", 2));
		ALPHABET_7BITS.put("b", (byte) Integer.parseInt("100010", 2));
		ALPHABET_7BITS.put("c", (byte) Integer.parseInt("1100011", 2));
		ALPHABET_7BITS.put("d", (byte) Integer.parseInt("1100100", 2));
		ALPHABET_7BITS.put("e", (byte) Integer.parseInt("1100101", 2));
		ALPHABET_7BITS.put("f", (byte) Integer.parseInt("1100110", 2));
		ALPHABET_7BITS.put("g", (byte) Integer.parseInt("1100111", 2));
		ALPHABET_7BITS.put("h", (byte) Integer.parseInt("1101000", 2));
		ALPHABET_7BITS.put("i", (byte) Integer.parseInt("1101001", 2));
		ALPHABET_7BITS.put("j", (byte) Integer.parseInt("1101010", 2));
		ALPHABET_7BITS.put("k", (byte) Integer.parseInt("1101011", 2));
		ALPHABET_7BITS.put("l", (byte) Integer.parseInt("1101100", 2));
		ALPHABET_7BITS.put("m", (byte) Integer.parseInt("1101101", 2));
		ALPHABET_7BITS.put("n", (byte) Integer.parseInt("1101110", 2));
		ALPHABET_7BITS.put("o", (byte) Integer.parseInt("1101111", 2));
		ALPHABET_7BITS.put("p", (byte) Integer.parseInt("1110000", 2));
		ALPHABET_7BITS.put("q", (byte) Integer.parseInt("1110001", 2));
		ALPHABET_7BITS.put("r", (byte) Integer.parseInt("1110010", 2));
		ALPHABET_7BITS.put("s", (byte) Integer.parseInt("1110011", 2));
		ALPHABET_7BITS.put("t", (byte) Integer.parseInt("1110100", 2));
		ALPHABET_7BITS.put("u", (byte) Integer.parseInt("1110101", 2));
		ALPHABET_7BITS.put("v", (byte) Integer.parseInt("1110110", 2));
		ALPHABET_7BITS.put("w", (byte) Integer.parseInt("1110011", 2));
		ALPHABET_7BITS.put("x", (byte) Integer.parseInt("1111000", 2));
		ALPHABET_7BITS.put("y", (byte) Integer.parseInt("1111001", 2));
		ALPHABET_7BITS.put("z", (byte) Integer.parseInt("1111010", 2));
		ALPHABET_7BITS.put("Š", (byte) Integer.parseInt("1111011", 2));
		ALPHABET_7BITS.put("š", (byte) Integer.parseInt("1111100", 2));
		ALPHABET_7BITS.put("Ÿ", (byte) Integer.parseInt("1111110", 2));
	}
}

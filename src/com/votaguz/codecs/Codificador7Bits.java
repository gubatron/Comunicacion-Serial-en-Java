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
		byte[] resultado = encoder.codificar(entrada);
		byte b;

		int length = entrada.length();
		for (int i = 0; i < length; i++) {
			b = resultado[i];
			System.out.println(entrada.charAt(i) + " " + b + " "
					+ Integer.toString(b, 2));
		}
	}

	public static final Map<String, Byte> ALPHABET_7BITS = new HashMap<String, Byte>();

	public byte[] codificar(String input) {
		byte[] encodedUserDataBytes = new byte[160];

		int len = input.length();

		// 1. Sacamos la representacion de la tabla de 7 bits.
		for (int i = 0; i < len; i++) {
			char c = input.charAt(i);

			Byte c7bit = ALPHABET_7BITS.get(Character.toString(c));

			if (c7bit != null) {
				encodedUserDataBytes[i] = c7bit;
			}
		}

		// 2. Aplicamos algoritmo de normalizacion a 8 bits en Hexadecimal

		return encodedUserDataBytes;
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

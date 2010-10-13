package com.votaguz.modelo;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Scanner;

import com.votaguz.modelo.concurrente.NamedRunnableImpl;
import com.votaguz.modelo.concurrente.ThreadPool;

public class ComunicacionSerial {

	// Field descriptor #73 I
	public static final int PORT_SERIAL = 1;

	// Field descriptor #73 I
	public static final int PORT_PARALLEL = 2;

	// Field descriptor #73 I
	public static final int PORT_I2C = 3;

	// Field descriptor #73 I
	public static final int PORT_RS485 = 4;

	// Field descriptor #73 I
	public static final int PORT_RAW = 5;

	public static HashMap<Integer, String> PORT_NAMES_MAP;

	public static HashMap<String, String> EXPECTED_PORT_ADDRESSES_FOR_OS;

	public static ThreadPool THREAD_POOL = new ThreadPool("Comunicaciones");

	static {

		PORT_NAMES_MAP = new HashMap<Integer, String>();
		PORT_NAMES_MAP.put(PORT_RAW, "PORT_RAW");
		PORT_NAMES_MAP.put(PORT_RS485, "PORT_RS485");
		PORT_NAMES_MAP.put(PORT_SERIAL, "PORT_SERIAL");

		// Mac PORT SERIAL Name
		EXPECTED_PORT_ADDRESSES_FOR_OS = new HashMap<String, String>();
		EXPECTED_PORT_ADDRESSES_FOR_OS.put("mac os x", "/dev/ttys002");

		// Windows PORT SERIAL Name
		// OS_PORT_NAME.put("Windows", "COM1");
		EXPECTED_PORT_ADDRESSES_FOR_OS.put("windows", "COM2");

		// Linux PORT SERIAL Name
		EXPECTED_PORT_ADDRESSES_FOR_OS.put("Linux Distro", "/dev/ttyS0");

	}

	public static void main(String[] args) {

		try {
			final CommPort serialPort = getSerialPort();

			if (serialPort == null) {
				System.out.println("Puerto Serial No Encontrado.");
				return;
			}

			// Hilo que lee de la consola y manda por el puerto
			THREAD_POOL.execute(new NamedRunnableImpl("Consola") {
				public void run() {
					readAndPrintFromLocalConsole(serialPort,">>");
//					try {
//						writeOnPort(serialPort);
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
				}
			});

			// Hilo que lee del puerto
			THREAD_POOL.execute(new NamedRunnableImpl("Puerto") {
				public void run() {
					// readAndPrintFromSerialPortInputStream(serialPort);
					try {
						readFromPort(serialPort);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			System.out.println("\n\n <<[ Connected ]>> ");

		} catch (PortInUseException e) {
			e.printStackTrace();
		}
	}

	// [Detect and Return all ports Available]
	@SuppressWarnings("unchecked")
	public static CommPort getSerialPort() throws PortInUseException {

		String os_name = System.getProperty("os.name").toLowerCase();
		String expectedPortAddress = EXPECTED_PORT_ADDRESSES_FOR_OS
				.get(os_name);

		CommPortIdentifier portIdentifier;
		CommPort commPort = null;

		Enumeration<CommPortIdentifier> identifiers = CommPortIdentifier
				.getPortIdentifiers();

		while (identifiers.hasMoreElements()) {
			portIdentifier = (CommPortIdentifier) identifiers.nextElement();

			if (portIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL) {

				System.out.println("Found Serial Port: "
						+ portIdentifier.getName() + " - Type: "
						+ PORT_NAMES_MAP.get(portIdentifier.getPortType())
						+ " owned by " + portIdentifier.getCurrentOwner());

				if (portIdentifier.getName().equals(expectedPortAddress)) {
					commPort = portIdentifier.open("ComunicacionSerial", 2000);
					return commPort;
				} else {
					System.out.println("Not the one we're looking for...");
				}
			}
		}

		return null;
	}

	/**
	 * Lee de cualquier stream, muestra en pantalla y manda lo que lee por el
	 * puerto. Esta funcion termina si recibimos cualquiera de los siguientes
	 * strings: "quit","close","bye","q!","disconnect" (no importa si vienen en
	 * mayusculas, minusculas o cualquier combinacion de minusculas o
	 * mayusculas)
	 * 
	 * @param inputStream
	 *            - Un Stream cualquiera de donde leemos. Ejemplos: - El
	 *            System.in (la consola) - Un InputStream de un puerto de
	 *            comunicacion.
	 * @param prompt
	 *            - Un String a mostrar en pantalla cuando imprimimos la proxima
	 *            linea leida del InputStream.
	 * @param port
	 *            - Opcional. Lo que venga por el inputstream se manda a este
	 *            puerto.
	 * 
	 */

	/**
	 * Read everything that is coming from the serial port. Needs a Port for get
	 * the inputStream.
	 * 
	 * @param port
	 *            - A port obtained from getSerialPort() Method
	 * @throws IOException
	 */
	public static void readFromPort(CommPort port) throws IOException {
		InputStream portInput = port.getInputStream();
		byte[] b = new byte[1024]; // so you can read up to 1k
		int readBytes = 0; // how many bytes we have read.
		while (portInput.read() != -1) {
			readBytes = portInput.read(b);
			System.out.println(new String(b, 0, readBytes));
		}
	}

	/**
	 * Write everything that's coming from the System.in (CONSOLE) Needs a Port
	 * for get the OutputStrem for sent the data.
	 * 
	 * @param port
	 *            - A port obtained from getSerialPort() Method
	 * @throws IOException
	 */
	public static void writeOnPort(CommPort port) throws IOException {
		OutputStream portOut = port.getOutputStream();
		int i;
		while (System.in.read() != -1) {
			i = System.in.read();
			portOut.write(i);
		}

	}

	/**
	 * Reads from the input stream, and writes to the output stream of the given
	 * port. Whatever is written is also printed on stdout.
	 * 
	 * @param inputStream
	 * @param prompt
	 * @param port
	 */
	public static void readAndPrintFromStream(InputStream inputStream,
			String prompt, CommPort port) {
		Scanner consoleScanner = new Scanner(inputStream);

		PrintStream printStream = null;
		
		//if I'm given a port, I will use this printStream to write to it.
		if (port != null) {
			try {
				printStream = new PrintStream(port.getOutputStream());
			} catch (IOException e) {

			}
		}

		while (consoleScanner.hasNextLine()) {
			String nextLine = consoleScanner.nextLine();
			if (nextLine.equalsIgnoreCase("quit")
					|| nextLine.equalsIgnoreCase("bye")
					|| nextLine.equalsIgnoreCase("q!")
					|| nextLine.equalsIgnoreCase("close")
					|| nextLine.equalsIgnoreCase("disconnect")) {
				System.out.println(prompt + " EOF");
				return;
			}
			System.out.println(prompt + " " + nextLine);

			//if you can write to the port, do so.
			if (printStream != null) {
				printStream.print(nextLine);
			}

		}
	}

	/**
	 * Lee del stream de la consola e imprime en pantalla. || Read the Stream
	 * from serial port and print on screen
	 * 
	 * @param serialPort
	 */
	public static void readAndPrintFromLocalConsole(CommPort port, String prompt) {
		readAndPrintFromStream(System.in, prompt, port);
	}

	/**
	 * Dado un puerto de comunicacion, lee e imprime en la pantalla.
	 * 
	 * @param port
	 * @throws IOException
	 */
	public static void readAndPrintFromSerialPortInputStream(CommPort port) {
		try {
			readAndPrintFromStream(port.getInputStream(), "<<", null);
			port.close(); //not sure if we should close this right away...
		} catch (IOException e) {
			port.close();
		}
	}

}
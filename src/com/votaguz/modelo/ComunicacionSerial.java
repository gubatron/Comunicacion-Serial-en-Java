package com.votaguz.modelo;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

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

	public static HashMap<String, String> OS_PORT_NAME;
	
	public static ThreadPool THREAD_POOL = new ThreadPool("Comunicaciones"); 

	static {
		PORT_NAMES_MAP = new HashMap<Integer, String>();
		PORT_NAMES_MAP.put(PORT_RAW, "PORT_RAW");
		PORT_NAMES_MAP.put(PORT_RS485, "PORT_RS485");
		PORT_NAMES_MAP.put(PORT_SERIAL, "PORT_SERIAL");

		OS_PORT_NAME = new HashMap<String, String>();
		OS_PORT_NAME.put("Mac OS X", "/dev/tty.usbserial");
	}

	public static void main(String[] args) {

		try {
			final CommPort serialPort = getSerialPort();

			 if (serialPort == null) {
			 System.out.println("Puerto Serial No Encontrado.");
			 return;
			 }
			
			//Hilo que lee de la consola y manda por el puerto
			THREAD_POOL.execute(new NamedRunnableImpl("Consola") {
				public void run() {
					readAndPrintFromLocalConsole(serialPort,">>");
				}
			});
			
			//Hilo que lee del puerto
			THREAD_POOL.execute(new NamedRunnableImpl("Puerto") {
				public void run() {
					readAndPrintFromSerialPortInputStream(serialPort);
				}
			});
			
			System.out.println("Fin del Programa.");

		} catch (PortInUseException e) {
			e.printStackTrace();
		}
	}

	// Metodo que va a devolver un Objeto SerialPort que este disponible
	@SuppressWarnings("unchecked")
	public static CommPort getSerialPort() throws PortInUseException {

		String portAddress = OS_PORT_NAME.get(System.getProperty("os.name"));
		CommPortIdentifier portIdentifier;
		CommPort commPort = null;

		Enumeration<CommPortIdentifier> identificadores = CommPortIdentifier
				.getPortIdentifiers();

		while (identificadores.hasMoreElements()) {
			portIdentifier = (CommPortIdentifier) identificadores.nextElement();

			System.out.println("Found: " + portIdentifier.getName()
					+ " - Type: "
					+ PORT_NAMES_MAP.get(portIdentifier.getPortType())
					+ " owned by " + portIdentifier.getCurrentOwner());

			if (portIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				if (portIdentifier.getName().equals(portAddress)) {
					// inicializar el puerto para devolverlo
					commPort = portIdentifier.open("ComunicacionSerial", 2000);
					return commPort;
				}
			}
		}

		return null;
	}

	/**
	 * Lee de cualquier stream, muestra en pantalla y manda lo que lee por el puerto.
	 * Esta funcion termina si recibimos cualquiera de los siguientes strings:
	 * "quit","close","bye","q!","disconnect" 
	 * (no importa si vienen en mayusculas, minusculas o cualquier combinacion de minusculas o mayusculas)
	 * 
	 * @param inputStream - Un Stream cualquiera de donde leemos. 
	 * 		Ejemplos: - El System.in (la consola)
	 *          	      - Un InputStream de un puerto de comunicacion.
	 * @param prompt - Un String a mostrar en pantalla cuando imprimimos la proxima linea leida del InputStream.
	 * @param port - Opcional. Lo que venga por el inputstream se manda a este puerto.
	 * 
	 */
	public static void readAndPrintFromStream(InputStream inputStream,
			String prompt, CommPort port) {
		Scanner consoleScanner = new Scanner(inputStream);

		PrintStream printStream = null;
		if (port != null)
			try {
				printStream = new PrintStream(port.getOutputStream());
			} catch (IOException e) {
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

			if (printStream != null) {
				printStream.print(nextLine);
			}

		}
	}

	/**
	 * Lee del stream de la consola e imprime en pantalla.
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
			readAndPrintFromStream(port.getInputStream(), "<<",null);
			port.close();
		} catch (IOException e) {
			port.close();
		}
	}

	// Lo que entra por el puerto
	public void entradaPuerto(SerialPort puerto) throws PortInUseException,
			UnsupportedCommOperationException, IOException {

		// Configuramos el puerto para Comunicarnos
		puerto.setSerialPortParams(57600, SerialPort.DATABITS_8,
				SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

	}

	// Lo que sale por el puerto
	public OutputStream salidaPuerto(SerialPort puerto) throws IOException,
			UnsupportedCommOperationException {

		puerto.setSerialPortParams(57600, SerialPort.DATABITS_8,
				SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		return puerto.getOutputStream();
	}

}
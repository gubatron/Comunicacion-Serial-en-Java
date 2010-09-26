package com.votaguz.modelo;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
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

		//Mac PORT SERIAL Name
		OS_PORT_NAME = new HashMap<String, String>();
		OS_PORT_NAME.put("Mac OS X", "/dev/tty.usbserial");
		
		//Windows PORT SERIAL Name
		OS_PORT_NAME.put("Windows", "COM1");
		OS_PORT_NAME.put("Windows", "COM2");
		
		//Linux PORT SERIAL Name
		OS_PORT_NAME.put("Linux Distro", "/dev/ttyS0");
		
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
				public void run(){
					//readAndPrintFromLocalConsole(serialPort,">>");
					try {
						writeOnPort(serialPort);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			
			//Hilo que lee del puerto
			THREAD_POOL.execute(new NamedRunnableImpl("Puerto") {
				public void run() {
					//readAndPrintFromSerialPortInputStream(serialPort);
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

		String portAddress = OS_PORT_NAME.get(System.getProperty("os.name"));
		CommPortIdentifier portIdentifier;
		CommPort commPort = null;

		Enumeration<CommPortIdentifier> identifiers = CommPortIdentifier.getPortIdentifiers();

		while (identifiers.hasMoreElements()) {
			portIdentifier = (CommPortIdentifier) identifiers.nextElement();

			System.out.println("Found Available Port: " + portIdentifier.getName()
					+ " - Type: "
					+ PORT_NAMES_MAP.get(portIdentifier.getPortType())
					+ " owned by " + portIdentifier.getCurrentOwner());

			if (portIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				if (portIdentifier.getName().equals(portAddress)) {
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
	
	
	/**
	 * Read everything that inconmig for the serial port. 
	 * Needs a Port for get the inputStream.
	 * @param port - A port obtained from getSerialPort() Method
	 * @throws IOException 
	 */
	public static void readFromPort(CommPort port) throws IOException{
		
		InputStream portInput = port.getInputStream();
		byte[] b = null;
		while(portInput.read() != -1){
			portInput.read(b);
			System.out.println(new String(b,1, portInput.read()));
		}
	}
	
	
	/**
	 * Write everything that inconmig from the System.in (CONSOLE) 
	 * Needs a Port for get the OutputStrem for sent the data.
	 * @param port - A port obtained from getSerialPort() Method
	 * @throws IOException 
	 */
	public static void writeOnPort(CommPort port) throws IOException{
		
		OutputStream portOut = port.getOutputStream();
		int i;
		while(System.in.read() != -1){
			i = System.in.read();
			portOut.write(i);
		}
		
	}
	
	
	
	public static void readAndPrintFromStream(InputStream inputStream, 
			String prompt, CommPort port) {
		Scanner consoleScanner = new Scanner(inputStream);

		PrintStream printStream = null;
		if (port != null)
			try {
				printStream = new PrintStream(port.getOutputStream());
			} catch (IOException e) {
				//TODO handle Exception Here
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
	 * Lee del stream de la consola e imprime en pantalla. || Read the Stream from serial port and print on screen
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

}
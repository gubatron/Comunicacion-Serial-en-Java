package com.votaguz.modelo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Scanner;

import gnu.io.*;

public class ComunicacionSerial {
	
	public static void main(String[] args){
		
	}

	//Metodo que va a devolver un Objeto SerialPort que este disponible
	public static SerialPort getPuertoSerial() throws PortInUseException{
		
		String miPuerto = "/dev/tty.usbserial";
		CommPortIdentifier identificadorDePuerto;
		CommPort puertoSerial = null;
		SerialPort puertoDisponible = null;
		boolean puertoEncontrado = false;
		
		Enumeration listaDePuertos = CommPortIdentifier.getPortIdentifiers();
		
		while(listaDePuertos.hasMoreElements()){
			identificadorDePuerto = (CommPortIdentifier) listaDePuertos.nextElement();
			
			if(identificadorDePuerto.getPortType() == CommPortIdentifier.PORT_SERIAL){ 
				if(identificadorDePuerto.getName().equals(miPuerto)){
					puertoEncontrado = true;
					System.out.println("Puerto Encontrado  " + miPuerto);
					// inicializar el puerto para devolverlo
					puertoSerial = identificadorDePuerto.open("ComunicacionSerial",2000);
					puertoDisponible = (SerialPort) puertoSerial;
				}
			}
		}
		if(!puertoEncontrado){ System.out.println("Puerto "+miPuerto+" no encontrado"); }
		return puertoDisponible;
	}
	

	public void iniciarConsola(InputStream buffer_entrada, OutputStream buffer_salida){
	//TODO Agarrar el INPUT y el OUTPUT para meterlos en un hilo y leerlo y escribirlo continuamente
		
		Scanner entradaDeConsola = new Scanner(buffer_entrada);
		while(entradaDeConsola.hasNextLine() == true){
			System.out.println("<<"+entradaDeConsola.nextLine());
		}
	}
	
	
	//Lo que entra por el puerto
	public void entradaPuerto(SerialPort puerto) throws PortInUseException, UnsupportedCommOperationException, IOException{
		
		//Configuramos el puerto para Comunicarnos
		puerto.setSerialPortParams(57600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		
	}
	
	
	//Lo que sale por el puerto
	public OutputStream salidaPuerto(SerialPort puerto) throws IOException, UnsupportedCommOperationException{
		
		puerto.setSerialPortParams(57600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		return puerto.getOutputStream();
	}

}
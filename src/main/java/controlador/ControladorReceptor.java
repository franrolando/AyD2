package controlador;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Utils.Utils;
import modelo.Mensaje;
import modelo.MensajeAvisoRecep;

public class ControladorReceptor {
	private final static Logger log = LoggerFactory.getLogger(ControladorReceptor.class);
	private static ControladorReceptor instance;
	private final static Integer PUERTO = 8090;
	private DatagramSocket socketUDP = null;	
	private ServerSocket socket = null;

	private ControladorReceptor() {
		super();
	}

	public static ControladorReceptor getInstance() {
		if (Objects.isNull(instance)) {
			instance = new ControladorReceptor();
		}
		return instance;
	}

	public Mensaje receptionMessageUDP() {
		Mensaje message = null;
		try {
			byte[] buffer = new byte[1024];
			DatagramPacket pregunta = new DatagramPacket(buffer, buffer.length);
			socketUDP.receive(pregunta);
			log.info("Recepcion de datagrama");
			message = Utils.toObject(pregunta.getData());
			System.out.println(message);
		} catch (IOException e) {
			log.error("ERROR:", e);
		} 
		return message;
	}

	public MensajeAvisoRecep leerMensajeAvisoRecepcion() {
		MensajeAvisoRecep mensaje = null;
		try {
			ObjectInputStream in = new ObjectInputStream(socket.accept().getInputStream());
			mensaje = (MensajeAvisoRecep) in.readObject();
			log.info(mensaje.toString());
		} catch (Exception e) {
			log.error("ERROR:", e);
		}
		return mensaje;
	}
	
	public void instanciarSocketServer () {
		try {
			socketUDP = new DatagramSocket(PUERTO);
			socket = new ServerSocket(PUERTO);
		} catch (IOException e) {
			log.error("ERROR:", e);
		}
	}

}

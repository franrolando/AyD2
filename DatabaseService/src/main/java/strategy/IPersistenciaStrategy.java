package strategy;

import java.util.List;

import modelo.Mensaje;

public interface IPersistenciaStrategy {
	
	void persistirMensaje(Mensaje mensaje);
	List<Mensaje> getMensajes(String nombreReceptor);
	void eliminaMensajes(String nombreReceptor);
}

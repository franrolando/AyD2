package controlador;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import java.util.stream.Collectors;

import Configuration.ConfigDirectorio;
import modelo.Receptor;
import observable.ReceptoresMap;

public class ControladorDirectorio implements Observer {

	private static ControladorDirectorio instance = null;
	private ReceptoresMap receptores;
	private ServerSocket serverSocket;
	private Socket socketReplica;

	private ControladorDirectorio() {
		super();
		this.receptores = new ReceptoresMap();
		this.receptores.addObserver(this);
	}

	public static ControladorDirectorio getInstance() {
		if (Objects.isNull(instance)) {
			synchronized (ControladorDirectorio.class) {
				if (Objects.isNull(instance)) {
					instance = new ControladorDirectorio();
				}
			}
		}
		return instance;
	}

	public void listenEmisores() {
		try {
			ServerSocket serverSocket = new ServerSocket(ConfigDirectorio.getInstance().getPuertoEmisores());
			new ObjectOutputStream(serverSocket.accept().getOutputStream()).writeObject(listaReceptores());
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void listenReceptores() {
		try {
			ServerSocket serverSocket = new ServerSocket(ConfigDirectorio.getInstance().getPuertoReceptores());
			Socket socket = serverSocket.accept();
			ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
			String action = (String) inputStream.readObject();
			switch (action) {
			case "estado":
				Receptor recep = (Receptor) inputStream.readObject();
				recep.setIp(socket.getInetAddress().getHostAddress());
				receptores.addReceptor(recep);
				break;
			case "nombreValido":
				new ObjectOutputStream(socket.getOutputStream()).writeObject(listaReceptores());
				break;
			}
			serverSocket.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void listenDirectorioReplica() {
		try {
			serverSocket = new ServerSocket(ConfigDirectorio.getInstance().getPuertoDirectorioReplica());
			socketReplica = serverSocket.accept();
			ReceptoresMap receptoresAux = (ReceptoresMap) new ObjectInputStream(socketReplica.getInputStream()).readObject();
			receptoresAux.getReceptores().values().forEach(receptor -> {
				this.receptores.addReceptor(receptor);
			});
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void iniciaDirectorio() {
		if (!ConfigDirectorio.getInstance().isDirectorioReplica().equals(true)) {
			Thread tReceptores = threadReceptores();
			Thread tEmisores = threadEmisores();
			Thread tReplica = threadConexionReplica();
			tReceptores.start();
			tEmisores.start();
			tReplica.start();
		} else {
			conexionDirectorioOrig();
		}
	}

	private Thread threadConexionReplica() {
		return new Thread() {

			@Override
			public void run() {
				ControladorDirectorio.getInstance().listenDirectorioReplica();
			}

		};
	}

	private Thread threadEmisores() {
		Thread tEmisores = new Thread() {

			@Override
			public void run() {
				while (true) {
					ControladorDirectorio.getInstance().listenEmisores();
				}
			}

		};
		return tEmisores;
	}

	private Thread threadReceptores() {
		Thread tReceptores = new Thread() {

			@Override
			public void run() {
				while (true) {
					ControladorDirectorio.getInstance().listenReceptores();
				}
			}

		};
		return tReceptores;
	}

	private void conexionDirectorioOrig() {
		Thread tReplica = new Thread() {

			@Override
			public void run() {
				while (true) {
					try {
						tDirectorioOrig();
					} catch (IOException e) {
						new Thread() {

							@Override
							public void run() {
								ControladorDirectorio.getInstance().listenEmisores();
							};

						}.start();
						new Thread() {

							@Override
							public void run() {
								ControladorDirectorio.getInstance().listenReceptores();
							};

						}.start();
						e.printStackTrace();
					}
				}
			}

		};
		tReplica.start();
	}

	private void tDirectorioOrig() throws IOException {
		Socket socket = new Socket(ConfigDirectorio.getInstance().getIpDirectorioOriginal(),
				ConfigDirectorio.getInstance().getPuertoDirectorioReplica());
		try {
			new ObjectOutputStream(socket.getOutputStream()).writeObject(receptores);
			ReceptoresMap receptoresAux = (ReceptoresMap) new ObjectInputStream(socket.getInputStream()).readObject();
			receptores.setReceptores(receptoresAux.getReceptores());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public List<Receptor> listaReceptores() {
		return receptores.getReceptores().values().stream().collect(Collectors.toList());
	}

	public ReceptoresMap getReceptores() {
		return receptores;
	}

	public void setReceptores(ReceptoresMap receptores) {
		this.receptores = receptores;
	}

	@Override
	public void update(Observable o, Object arg) {
		if (!ConfigDirectorio.getInstance().isDirectorioReplica()) {
			Thread thread = new Thread() {

				@Override
				public void run() {
					try {
						new ObjectOutputStream(socketReplica.getOutputStream()).writeObject(receptores);
						socketReplica = serverSocket.accept();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			thread.start();
		}
	}

}

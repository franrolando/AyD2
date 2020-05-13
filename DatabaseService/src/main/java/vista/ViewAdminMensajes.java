package vista;

import java.awt.EventQueue;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import DAO.MensajesDAO;
import modelo.Mensaje;
import strategy.FileSystemStrategy;

import java.awt.Color;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import Configuration.Config;

import java.awt.Font;

public class ViewAdminMensajes {

	private JFrame frmDatabase;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ViewAdminMensajes window = new ViewAdminMensajes();
					window.frmDatabase.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ViewAdminMensajes() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmDatabase = new JFrame();
		frmDatabase.setTitle("Database");
		frmDatabase.getContentPane().setBackground(new Color(240, 230, 140));
		frmDatabase.setBounds(100, 100, 450, 300);
		frmDatabase.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmDatabase.setSize(450, 150);
		frmDatabase.setVisible(true);
		frmDatabase.setLocationRelativeTo(null);
		
		ImageIcon imgDatabase = new ImageIcon("./src/main/img/email-icon.png");
		frmDatabase.setIconImage(imgDatabase.getImage());
		
		JPanel panel = new JPanel();
		panel.setBackground(new Color(240, 230, 140));
		panel.setLayout(new GridLayout(0, 1, 0, 0));
		frmDatabase.getContentPane().add(panel, BorderLayout.CENTER);
		
		JLabel lblTitulo = new JLabel("-Sistema administrador DB-");
		lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblTitulo);
		
		JLabel lblAviso = new JLabel("El programa dejar� de funcionar al cerrarse la ventana.",new ImageIcon("./src/main/img/exit.png"),0);
		lblAviso.setForeground(new Color(204, 0, 0));
		lblAviso.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblAviso.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblAviso);
		Mensaje a = new Mensaje();
		a.setReceptor("Franco Receptor");
		a.setAsunto("Asunto");
		a.setCuerpo("Cuerpo");
		a.setIpDestino("Ip destino");
		a.setEmisor("Emisor");
		MensajesDAO.getInstance().insertarMensaje(a);
		MensajesDAO.getInstance().eliminarMensajes("Franco Receptor").forEach(e ->  {
			System.out.println(e.getIpDestino());
		});;
	}

}

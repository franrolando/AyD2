package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import Configuration.Config;

public class Conexion {

	public Connection conectar() {
		Connection conexion = null;
		try {
			Class.forName(Config.getInstance().getControlador());
			conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/db-ayd2?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC"
					, Config.getInstance().getUsuario(),
					Config.getInstance().getClave());
		} catch (ClassNotFoundException e) {
			System.out.println("Error al cargar el controlador");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("Error en la conexion");
			e.printStackTrace();
		}
		return conexion;
	}

}

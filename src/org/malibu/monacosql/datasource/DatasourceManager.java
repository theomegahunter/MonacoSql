package org.malibu.monacosql.datasource;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.swing.tree.DefaultMutableTreeNode;

import org.malibu.monacosql.exception.MonacoSqlException;
import org.malibu.monacosql.util.FilesystemUtil;

public class DatasourceManager {

	private static final String DATASOURCE_CONFIG_FILE = "datasources.config";
	
	private static Map<String,Connection> connectionMap = new HashMap<>();
	
	public static Connection getEstablishedConnection(String connectionId) {
		return connectionMap.get(connectionId);
	}
	
	public static String establishConnection(Datasource datasource) throws MonacoSqlException, ClassNotFoundException, SQLException {
		validateDatasource(datasource);
		if(datasource != null) {
			DatasourceConfiguration config = DatasourceConfigManager.getDatasourceConfigurationById(datasource.getDriverId());
			validateDatasourceConfiguration(config);
			try {
				addJarToClasspath(new File(config.getLibrary()));
			} catch (Exception e) {
				throw new MonacoSqlException("Failed to load jars for DB configuration: " + config.getLibrary(), e);
			}
			try {
				Class.forName(config.getClassName());
			} catch (Throwable t) {
				throw new MonacoSqlException("Failed to create instance of driver class: " + config.getClassName(), t);
			}
			Connection conn = DriverManager.getConnection(datasource.getUrl(), datasource.getUsername(), datasource.getPassword());
			String connectionId = UUID.randomUUID().toString();
			connectionMap.put(connectionId, conn);
			return connectionId;
		}
		throw new MonacoSqlException("Datasource failed to be created, review datasource params and try again");
	}
	
	private static void addJarToClasspath(File file) throws Exception {
	    Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
	    method.setAccessible(true);
	    method.invoke(ClassLoader.getSystemClassLoader(), new Object[]{file.toURI().toURL()});
	}
	
	public static boolean closeConnection(String connectionId) throws MonacoSqlException, SQLException {
		if(connectionId == null || !connectionMap.containsKey(connectionId)) {
			throw new MonacoSqlException("No connection to close, a connection may be stuck open!");
		}
		connectionMap.get(connectionId).close();
		return false;
	}
	
	private static void validateDatasource(Datasource datasource) throws MonacoSqlException {
		if(datasource == null) {
			throw new MonacoSqlException("Invalid datasource selected, datasource is null");
		}
		if(datasource.getUrl() == null || datasource.getUrl().trim().length() == 0) {
			throw new MonacoSqlException("Datasource has no URL specified");
		}
	}
	
	private static void validateDatasourceConfiguration(DatasourceConfiguration config) throws MonacoSqlException {
		if(config == null) {
			throw new MonacoSqlException("Missing datasource configuration, update this datasource and try again");
		}
		if(config.getClassName() == null) {
			throw new MonacoSqlException("Datasource config missing Java classname, please update the datasource configuration");
		}
		if(config.getLibrary() == null) {
			throw new MonacoSqlException("Datasource config missing driver jars, please update the datasource configuration");
		}
	}
	
	public static DefaultMutableTreeNode getDatasourcesFromFile() throws IOException {
		String datasourceConfigLocation = FilesystemUtil.getJarDirectory() + DATASOURCE_CONFIG_FILE;
		
		if(!new File(datasourceConfigLocation).exists()) {
			return new DefaultMutableTreeNode("Datasources");
		}
		
		FileInputStream fis = new FileInputStream(datasourceConfigLocation);
		ObjectInputStream ois = new ObjectInputStream(fis);
		DefaultMutableTreeNode root = null;
		try {
			root = (DefaultMutableTreeNode)ois.readObject();
		} catch (ClassNotFoundException e1) {}
		catch (EOFException ex) {}
		ois.close();
		fis.close();
		
		return root;
	}
	
	public static void saveDatasourcesToFile(DefaultMutableTreeNode root) throws IOException {
		if(root != null) {
			// should make a quick backup here first...
			String datasourceConfigLocation = FilesystemUtil.getJarDirectory() + DATASOURCE_CONFIG_FILE;
			FileOutputStream fos = new FileOutputStream(datasourceConfigLocation);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(root);
			oos.close();
			fos.close();
		}
	}
}

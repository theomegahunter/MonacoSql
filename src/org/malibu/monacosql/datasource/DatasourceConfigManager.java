package org.malibu.monacosql.datasource;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.malibu.monacosql.util.FilesystemUtil;

public class DatasourceConfigManager {
	
	private static List<DatasourceConfiguration> loadedList = null;
	
	public static final String DATASOURCE_CONFIG_FILE_NAME = "datasource-configs.config";
	
	public static DatasourceConfiguration getDatasourceConfigurationById(String datasourceId) {
		if(datasourceId != null) {
			for (DatasourceConfiguration config : getDatasourceConfigurations()) {
				if(datasourceId.equals(config.getId())) {
					return config;
				}
			}
		}
		return null;
	}
	
	public static List<DatasourceConfiguration> getDatasourceConfigurations() {
		return getDatasourceConfigsFromFile(true);
	}
	
	public static List<DatasourceConfiguration> getDatasourceConfigsFromFile(boolean useCaching) {
		if(useCaching && loadedList != null) {
			return loadedList;
		}
		
		loadedList = new ArrayList<>();
		
		// load datasource config items from file
		XMLDecoder xmlReader = null;
		try {
			String dsConfigFilePath = FilesystemUtil.getJarDirectory() + DATASOURCE_CONFIG_FILE_NAME;
			File dsConfigFile = new File(dsConfigFilePath);
			if(dsConfigFile.exists() && dsConfigFile.isFile()) {
				// read in objects from config file
				xmlReader = new XMLDecoder(new FileInputStream(dsConfigFile));
				DatasourceConfiguration config = null;
				while((config = (DatasourceConfiguration)xmlReader.readObject()) != null) {
					loadedList.add(config);
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// occurs when we reach the end of the file, no big deal
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if(xmlReader != null) {
				xmlReader.close();
			}
		}
		
		return loadedList;
	}
	
	public static void writeDatasourceConfigsToFile(List<DatasourceConfiguration> configs) {
		XMLEncoder xmlWriter = null;
		
		if(configs != null) {
			try {
				String dsConfigFilePath = FilesystemUtil.getJarDirectory() + DATASOURCE_CONFIG_FILE_NAME;
				File dsConfigFile = new File(dsConfigFilePath);
				// if config file already exists, create backup first, then remove it
//				if(dsConfigFile.exists() && dsConfigFile.isFile()) {
//					FilesystemUtil.createBackupOfFile(dsConfigFilePath);
//					dsConfigFile.delete();
//				}
				
				// write out new config file
				xmlWriter = new XMLEncoder(new FileOutputStream(dsConfigFile));
				for(DatasourceConfiguration config : configs) {
					xmlWriter.writeObject(config);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if(xmlWriter != null) {
					xmlWriter.close();
				}
			}
		}
	}
	
}

package org.malibu.monacosql.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class FilesystemUtil {
	public static String getJarDirectory() {
		// get directory .jar file is running from (using substring() to remove leading slash)
//		String workingDir = FilesystemUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
//		File file = new File(workingDir);
//		workingDir = file.getAbsolutePath();
//		if(workingDir.startsWith("\\")) {
//			workingDir = workingDir.substring(1);
//		}
//		if(workingDir.endsWith(".")) {
//			workingDir = workingDir.substring(0, workingDir.length() - 2);
//		}
//		return workingDir + "\\";
		
		return "C:/test/";
	}
	
	public static void createBackupOfFile(String filePath) throws IOException {
		int backupNumber = 0;
		if(filePath != null) {
			File file = new File(filePath);
			if(file.exists() && file.isFile()) {
				String backupFilePath = filePath + ".bak";
				while(new File(backupFilePath).exists()) {
					backupNumber++;
					backupFilePath = filePath + ".bak." + backupNumber;
				}
				copyFile(filePath, backupFilePath);
			}
		}
	}
	
	public static void copyFile(String originalFilePath, String newFilePath) throws IOException {
		if(originalFilePath != null && newFilePath != null) {
			FileInputStream fis = null;
			FileOutputStream fos = null;
			try {
				fis = new FileInputStream(new File(originalFilePath));
				fos = new FileOutputStream(new File(newFilePath));
				byte[] buffer = new byte[5120];
				int bytesRead = -1;
				while((bytesRead = fis.read(buffer)) != -1) {
					fos.write(buffer, 0, bytesRead);
				}
			} finally {
				if(fis != null) {
					try { fis.close(); } catch (IOException e) {}
				}
				if(fos != null) {
					try { fos.close(); } catch (IOException e) {}
				}
			}
		}
	}
	
	public static List<String> getClassesInJarThatImplementJdbcDriver(String jarFileLocation) {
		List<String> classNames = new ArrayList<>();
		
		// get classloader
		final ClassLoader classLoader = FilesystemUtil.class.getClassLoader();
		
		// verify that it's a .jar file
		if(jarFileLocation != null && jarFileLocation.toLowerCase().endsWith(".jar")) {
            URL url = null;
			try {
				url = new URL("jar:file:" + jarFileLocation + "!/");
			} catch (MalformedURLException e1) { /* shouldn't ever occur */ }
            URLClassLoader ucl = new URLClassLoader(new URL[] { url }, classLoader);
			
			// try to load the .jar file and look for all classes that implement java.sql.Driver
			try {
				JarInputStream jarFile = new JarInputStream(new FileInputStream(jarFileLocation));
				JarEntry jarEntry = null;
				while((jarEntry = jarFile.getNextJarEntry()) != null) {
					if(jarEntry.getName().endsWith(".class") && !jarEntry.getName().contains("$")) {
						String classname = jarEntry.getName().replaceAll("/", "\\.");
						classname = classname.substring(0, classname.length() - 6);
						
						try{
							final Class<?> myLoadedClass = Class.forName(classname, true, ucl);
                            if (java.sql.Driver.class.isAssignableFrom(myLoadedClass)) {
                            	classNames.add(myLoadedClass.getName());
                            }
						} catch (Throwable t) {
							System.err.println("Error occurred trying to load class: " + classname + ", reason: " + t.getMessage());
						}
					}
				}
			} catch (Throwable t) {
				System.err.println("Error occurred trying to load jar: " + jarFileLocation);
			}
		}
		
		return classNames;
	}
}

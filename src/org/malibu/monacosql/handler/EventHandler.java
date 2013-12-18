package org.malibu.monacosql.handler;

import java.awt.Window;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class EventHandler {
	
	private static Window mainWindow = null;
	private static JTextArea textarea = null;
	
	public static void setCurrentWindow(Window window) {
		mainWindow = window;
	}
	
	public static void setTextArea(JTextArea newTextArea) {
		textarea = newTextArea;
	}
	
	public static void handleError(String message, Throwable t) {
//		JOptionPane.showMessageDialog(mainWindow, message, "Error", JOptionPane.ERROR_MESSAGE);
		// need to log and add to error list at bottom of window
		writeToTextArea(message);
		writeToTextArea("Exception: " + t.getMessage());
	}
	
	public static void handleInfo(String message) {
		writeToTextArea(message);
	}
	
	private static void writeToTextArea(String message) {
		if(textarea != null) {
			StringBuilder buffer = new StringBuilder();
			buffer.append(textarea.getText());
			buffer.append(message);
			buffer.append('\n');
			textarea.setText(buffer.toString());
		}
	}
}

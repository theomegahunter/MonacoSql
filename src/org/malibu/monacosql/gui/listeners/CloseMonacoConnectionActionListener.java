package org.malibu.monacosql.gui.listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;

import javax.swing.JTabbedPane;

import org.malibu.monacosql.exception.MonacoSqlException;
import org.malibu.monacosql.gui.MonacoConnectionPanel;
import org.malibu.monacosql.handler.EventHandler;

public class CloseMonacoConnectionActionListener implements MouseListener {
	
	private MonacoConnectionPanel panel = null;
	private JTabbedPane parentPane = null;
	
	public CloseMonacoConnectionActionListener(MonacoConnectionPanel panel, JTabbedPane parentPane) {
		this.panel = panel;
		this.parentPane = parentPane;
	}

	public void mouseClicked(MouseEvent e) {
		if(this.panel != null) {
			try {
				this.panel.destroy();
				EventHandler.handleInfo("Successfully closed connection");
			} catch (MonacoSqlException | SQLException e1) {
				EventHandler.handleError("Error occurred closing connection panel", e1);
			}
		}
		if(this.parentPane != null) {
			this.parentPane.remove(this.panel);
		}
	}

	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
}

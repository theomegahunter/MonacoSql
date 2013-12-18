package org.malibu.monacosql.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.ShorthandCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.malibu.monacosql.datasource.Datasource;
import org.malibu.monacosql.datasource.DatasourceManager;
import org.malibu.monacosql.exception.MonacoSqlException;
import org.malibu.monacosql.handler.EventHandler;

public class MonacoConnectionPanel extends JPanel {
	
	private static final long serialVersionUID = -56698894409944983L;

	private String connectionId = null;
	private RSyntaxTextArea textArea = null;
	
	public MonacoConnectionPanel(Datasource datasource) throws MonacoSqlException {
		super();
		
		// attempt to make a connection, and exception out if it fails
		try {
			this.connectionId = DatasourceManager.establishConnection(datasource);
		} catch (ClassNotFoundException | SQLException e) {
			throw new MonacoSqlException(e);
		}
		
		setPreferredSize(new Dimension(90, 90));
		setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane_2 = new JSplitPane();
		add(splitPane_2, BorderLayout.CENTER);
		
		JSplitPane splitPane_3 = new JSplitPane();
		splitPane_3.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane_2.setRightComponent(splitPane_3);
		
		splitPane_3.setLeftComponent(createNewSqlTextEditor());
		
		JTabbedPane tabbedPane_2 = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane_2.setVisible(false);
		splitPane_3.setRightComponent(tabbedPane_2);
		splitPane_3.setDividerLocation(200);
		
		JTree tree_1 = new JTree();
		tree_1.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode("Database") {
				{
					// add database nodes here, when ready to add that functionality
				}
			}
		));
		splitPane_2.setLeftComponent(tree_1);
	}
	
	private RTextScrollPane createNewSqlTextEditor() {
		CompletionProvider provider = createCompletionProvider();
		AutoCompletion ac = new AutoCompletion(provider);
		
		this.textArea = new RSyntaxTextArea(20, 60);
		// add listener to detect when the user presses CTRL+Enter (will want to be able to customize this later)
		textArea.addKeyListener(new RunSqlKeypressListener());
		textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
		// textArea.setCodeFoldingEnabled(true);
		RTextScrollPane sp = new RTextScrollPane(textArea);
		ac.install(textArea);
		
		return sp;
	}
	
	private void executeSql(String sql) throws SQLException {
		Connection conn = DatasourceManager.getEstablishedConnection(this.connectionId);
		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		
		EventHandler.handleInfo("Query successfully executed");
		
		int rowIndex = 1;
		ResultSetMetaData metadata = rs.getMetaData();
		while(rs.next()) {
			EventHandler.handleInfo("Row: " + rowIndex);
			for(int index = 1; index < metadata.getColumnCount(); index++) {
				EventHandler.handleInfo(metadata.getColumnName(index) + ": " + rs.getObject(index));
			}
			System.out.println();
		}
		
		EventHandler.handleInfo("Query returned " + rowIndex + " rows");
		
		rs.close();
		stmt.close();
	}
	
	private String getSelectedSql() {
		String text = this.textArea.getText();
		int start = this.textArea.getSelectionStart();
		int end = this.textArea.getSelectionEnd();
		
		if(start == end) {
			int currentLineOffset = getBeginningOfLineOffset(text, start);
			String currentLine = getLine(text, currentLineOffset);
			
			if("".equals(currentLine)) {
				// on an empty line, return no SQL
				return "";
			}
			
			int saveCurrentLineOffest = currentLineOffset;
			// while we aren't on an empty line, keep looking backwards lines, to find the start
			// of the SQL statement
			while(currentLineOffset >= 0 && !"".equals((currentLine = getLine(text, currentLineOffset)))) {
				saveCurrentLineOffest = currentLineOffset;
				currentLineOffset = getBeginningOfLineOffset(text, currentLineOffset - 1);
			}
			
			// we now know the beginning of the SQL statement(s), read forward until we find an empty line
			currentLineOffset = saveCurrentLineOffest;
			StringBuilder buffer = new StringBuilder();
			while(!"".equals((currentLine = getLine(text, currentLineOffset)))) {
				buffer.append(currentLine).append(" ");
				currentLineOffset += currentLine.length() + 1;
			}
			
			return buffer.toString();
		}
		
		return text.substring(start, end).trim();
	}
	
	private int getBeginningOfLineOffset(String text, int currentOffset) {
		if(text == null) return -1;
		
		String currentChar = getCharSafe(text, currentOffset);
		String leftChar = getCharSafe(text, currentOffset - 1);
		
		if("".equals(currentChar) && "".equals(leftChar)) {
			// text area is empty
			return -1;
		}
		if("".equals(leftChar)) {
			// left is the beginning of the text area, we're already at the beginning of the line
			return currentOffset;
		}
		// we're either at the end of the text area, at the end of a line, or in the middle
		// of some text, so look left until we run into a new line or the end of the text area
		int newOffset = currentOffset - 1;
		while(!"".equals(getCharSafe(text, newOffset)) && !"\n".equals(getCharSafe(text, newOffset))) newOffset--;
		if(("\n".equals(getCharSafe(text, newOffset)) || "".equals(getCharSafe(text, newOffset))) 
				&& !"\n".equals(getCharSafe(text, newOffset + 1)) && !"".equals(getCharSafe(text, newOffset + 1))) {
			// at beginning of text area, or on new line and next char is the start of the current line, move our offset
			newOffset++;
		}
		return newOffset;
	}
	
	private String getLine(String text, int beginningOfLineOffset) {
		if(text == null) return "";
		
		int endOffset = beginningOfLineOffset;
		while(!"".equals(getCharSafe(text, endOffset)) && !"\n".equals(getCharSafe(text, endOffset))) endOffset++;
		if(endOffset == beginningOfLineOffset) {
			// current line is either only just a newline, or the end of the file
			return "";
		}
		
		return text.substring(beginningOfLineOffset, endOffset).trim();
	}
	
	private String getCharSafe(String text, int location) {
		if(text != null) {
			if(location >= 0 && location < text.length()) {
				return String.valueOf(text.charAt(location));
			}
		}
		return "";
	}
	
	private CompletionProvider createCompletionProvider() {
		// A DefaultCompletionProvider is the simplest concrete implementation
		// of CompletionProvider. This provider has no understanding of
		// language semantics. It simply checks the text entered up to the
		// caret position for a match against known completions. This is all
		// that is needed in the majority of cases.
		DefaultCompletionProvider provider = new DefaultCompletionProvider();

		Set<String> db2Keywords = getListOfSqlKeywords();
		for (String keyword : db2Keywords) {
			provider.addCompletion(new BasicCompletion(provider, keyword));
		}

		provider.addCompletion(new ShorthandCompletion(provider, "SFU",
				"SELECT *\nFROM UJ.\nFOR READ ONLY WITH UR;", "SFU"));

		return provider;
	}
	
	private Set<String> getListOfSqlKeywords() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				MonacoSqlGui.class.getResourceAsStream("db2.properties")));
		Set<String> db2Keywords = new HashSet<>();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				db2Keywords.add(line);
			}
		} catch (Exception e) {
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
		return db2Keywords;
	}
	
	public void destroy() throws MonacoSqlException, SQLException {
		DatasourceManager.closeConnection(this.connectionId);
	}
	
	private class RunSqlKeypressListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_ENTER) {
				// execute selected SQL
				String selectedSql = getSelectedSql();
				if(selectedSql == null || selectedSql.trim().length() == 0) {
					EventHandler.handleInfo("No SQL selected");
					return;
				}
				try {
					executeSql(selectedSql);
				} catch (SQLException e1) {
					EventHandler.handleError("Error running SQL", e1);
				}
			}
		}
	}
}

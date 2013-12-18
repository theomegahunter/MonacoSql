package org.malibu.monacosql.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.malibu.monacosql.datasource.Datasource;
import org.malibu.monacosql.datasource.DatasourceManager;
import org.malibu.monacosql.gui.datasource.DatasourceTreeManager;
import org.malibu.monacosql.gui.listeners.CloseMonacoConnectionActionListener;
import org.malibu.monacosql.handler.EventHandler;
import javax.swing.JScrollPane;
import java.awt.Font;

public class MonacoSqlGui {

	private JFrame frmMonacosql;
	private JTree tree;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
					MonacoSqlGui window = new MonacoSqlGui();
					window.frmMonacosql.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MonacoSqlGui() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		final JFrame window = this.frmMonacosql;
		
		EventHandler.setCurrentWindow(window);
		
		frmMonacosql = new JFrame();
		frmMonacosql.setTitle("MonacoSql 1.1");
		frmMonacosql.setBounds(100, 100, 866, 514);
		frmMonacosql.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frmMonacosql.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JButton btnNewButton = new JButton("New button");
		frmMonacosql.getContentPane().add(btnNewButton, BorderLayout.NORTH);
		
		JPanel panel_1 = new JPanel();
		panel_1.setAlignmentX(Component.LEFT_ALIGNMENT);
		frmMonacosql.getContentPane().add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane_1 = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane_1.setVisible(false);
		panel_1.add(tabbedPane_1);
		
		JPanel panel_2 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_2.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel_1.add(panel_2, BorderLayout.SOUTH);
		
		JLabel lblNewLabel = new JLabel("Ready");
		panel_2.add(lblNewLabel);
		
		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
		frmMonacosql.getContentPane().add(splitPane_1, BorderLayout.CENTER);
		
		JPanel panel_5 = new JPanel();
		panel_5.setPreferredSize(new Dimension(50, 50));
		splitPane_1.setLeftComponent(panel_5);
		panel_5.setLayout(new BorderLayout(0, 0));
		
		final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		
				JPanel panel_3 = new JPanel();
				panel_3.setLayout(new BorderLayout(0, 0));
				this.tree = new JTree();
				tree.setDragEnabled(true);
				try {
					tree.setModel(new DefaultTreeModel(DatasourceManager.getDatasourcesFromFile()));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				if(tree.getModel() == null) {
					// if no model exists, just add the default root
					tree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("Datasources")));
				}
				
				tree.addMouseListener(new MouseAdapter() {
				    public void mousePressed(MouseEvent e) {
				        int selRow = tree.getRowForLocation(e.getX(), e.getY());
				        TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
				        if(selRow != -1 && e.getClickCount() == 2) {
				        	Object selectedComponent = ((DefaultMutableTreeNode)selPath.getLastPathComponent()).getUserObject();
				        	if(selectedComponent instanceof Datasource) {
				        		Datasource datasource = (Datasource)selectedComponent;
				        		MonacoConnectionPanel newConnectionPanel = null;
				        		try {
									newConnectionPanel = new MonacoConnectionPanel(datasource);
									EventHandler.handleInfo("Successfully connected to datasource '" + datasource.getName() + "'");
								} catch (Throwable t) {
									EventHandler.handleError("Error connecting to datasource " + (datasource.getName() != null ? "'" + datasource.getName() + "'" : "") + ": " + t.getMessage(), t);
								}
				        		if(newConnectionPanel != null) {
				        			// connection successfully established, create a new connection tab!
				        			int selectedTabIndex = tabbedPane.getSelectedIndex();
				        			// check if this is the first tab we're adding
				        			if(selectedTabIndex == -1) {
				        				selectedTabIndex = 0;
				        			}
									tabbedPane.add(newConnectionPanel, ((Datasource)selectedComponent).getName(), selectedTabIndex);
									
									// add name and close button to tab
									JPanel pnlTab = new JPanel(new GridBagLayout());
									pnlTab.setOpaque(false);
									JLabel lblTitle = new JLabel(((Datasource)selectedComponent).getName() + " "); // LAME SPACING HERE
									JLabel btnClose = new JLabel("x");
									btnClose.setPreferredSize(new Dimension(10, 10));

									GridBagConstraints gbc = new GridBagConstraints();
									gbc.gridx = 0;
									gbc.gridy = 0;
									gbc.weightx = 1;

									pnlTab.add(lblTitle, gbc);

									gbc.gridx++;
									gbc.weightx = 0;
									pnlTab.add(btnClose, gbc);

									tabbedPane.setTabComponentAt(selectedTabIndex, pnlTab);

									btnClose.addMouseListener(new CloseMonacoConnectionActionListener(newConnectionPanel, tabbedPane));
									
									tabbedPane.setSelectedIndex(selectedTabIndex);
				        		}
				        	}
				        }
				    }
				});
				tree.setPreferredSize(new Dimension(60, 64));
				panel_3.add(tree);
				
						JPanel panel_4 = new JPanel();
						panel_4.setLayout(new BorderLayout(0, 0));
						
						panel_4.add(tabbedPane);
						
						JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel_3, panel_4);
						panel_5.add(splitPane, BorderLayout.CENTER);
						
						JPanel panel = new JPanel();
						FlowLayout flowLayout_1 = (FlowLayout) panel.getLayout();
						flowLayout_1.setAlignment(FlowLayout.LEFT);
						panel_3.add(panel, BorderLayout.NORTH);
						
						JButton btnNewButton_2 = new JButton("");
						btnNewButton_2.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent arg0) {
								JDialog dialog = new AddDatasourceDialog(frmMonacosql, tree);
								dialog.setVisible(true);
							}
						});
						btnNewButton_2.setPreferredSize(new Dimension(25, 25));
						btnNewButton_2.setIcon(new ImageIcon(MonacoSqlGui.class.getResource("/org/malibu/monacosql/gui/10.png")));
						panel.add(btnNewButton_2);
						
						JButton button = new JButton("");
						button.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent arg0) {
								int option = JOptionPane.showConfirmDialog(window, "Remove?");
								if(option == 0) {
									DatasourceTreeManager.removeSelectedNodeFromTree(tree);
								}
							}
						});
						button.setIcon(new ImageIcon(MonacoSqlGui.class.getResource("/org/malibu/monacosql/gui/12.png")));
						button.setPreferredSize(new Dimension(25, 25));
						panel.add(button);
						
						JButton button_1 = new JButton("");
						button_1.setIcon(new ImageIcon(MonacoSqlGui.class.getResource("/org/malibu/monacosql/gui/13.png")));
						button_1.setPreferredSize(new Dimension(25, 25));
						panel.add(button_1);
						
						JButton button_2 = new JButton("");
						button_2.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent arg0) {
								// get name of new folder to add
								String newFolderName = JOptionPane.showInputDialog("What do you want to call the new folder?");
								DatasourceTreeManager.addFolderToTreeAtSelectedNode(tree, newFolderName);
							}
						});
						button_2.setIcon(new ImageIcon(MonacoSqlGui.class.getResource("/org/malibu/monacosql/gui/2.png")));
						button_2.setPreferredSize(new Dimension(25, 25));
						panel.add(button_2);
						
						JButton btnNewButton_1 = new JButton("");
						btnNewButton_1.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								JDialog dialog = new DatasourceConfigurationManagerGui(window);
								dialog.setVisible(true);
							}
						});
						btnNewButton_1.setIcon(new ImageIcon(MonacoSqlGui.class.getResource("/org/malibu/monacosql/gui/gears.png")));
						btnNewButton_1.setPreferredSize(new Dimension(25, 25));
						panel.add(btnNewButton_1);
						splitPane.setDividerLocation(200);
		
		JPanel panel_6 = new JPanel();
		splitPane_1.setRightComponent(panel_6);
		panel_6.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(20, 20));
		panel_6.add(scrollPane, BorderLayout.CENTER);
		
		JTextArea textArea_1 = new JTextArea();
		textArea_1.setFont(new Font("Monospaced", Font.PLAIN, 11));
		scrollPane.setViewportView(textArea_1);
		textArea_1.setLineWrap(true);
		
		EventHandler.setTextArea(textArea_1);
		
		splitPane_1.setDividerLocation(320);
	}
}

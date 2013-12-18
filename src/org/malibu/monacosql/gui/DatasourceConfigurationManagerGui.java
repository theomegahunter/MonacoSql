package org.malibu.monacosql.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;

import org.malibu.monacosql.datasource.DatasourceConfigManager;
import org.malibu.monacosql.datasource.DatasourceConfiguration;
import org.malibu.monacosql.util.FilesystemUtil;

public class DatasourceConfigurationManagerGui extends JDialog {
	
	private Component parent = null;
	
	private final JPanel contentPanel = new JPanel();
	private JTextField txtName;
	private JTextField txtLibrary;
	private JComboBox<String> comboBoxDriverClassnames = null;
	private JTextField txtSampleUrl;
	private DatasourceConfiguration selectedConfig = null;
	
	private JList<DatasourceConfiguration> datasourceList;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			DatasourceConfigurationManagerGui dialog = new DatasourceConfigurationManagerGui(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public DatasourceConfigurationManagerGui(final Component parent) {
		this.parent = parent;
		
		final Component thisDialog = this;
		
		setTitle("Manage Datasource Configurations");
		setBounds(100, 100, 728, 467);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JSplitPane splitPane = new JSplitPane();
			contentPanel.add(splitPane, BorderLayout.CENTER);
			{
				JPanel panel = new JPanel();
				splitPane.setRightComponent(panel);
				GridBagLayout gbl_panel = new GridBagLayout();
				gbl_panel.columnWidths = new int[] {0, 0, 0, 0};
				gbl_panel.rowHeights = new int[] {0, 0, 0, 0, 0, 0};
				gbl_panel.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
				gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
				panel.setLayout(gbl_panel);
				{
					JLabel lblName = new JLabel("Name:");
					GridBagConstraints gbc_lblName = new GridBagConstraints();
					gbc_lblName.insets = new Insets(0, 0, 5, 5);
					gbc_lblName.anchor = GridBagConstraints.EAST;
					gbc_lblName.gridx = 0;
					gbc_lblName.gridy = 0;
					panel.add(lblName, gbc_lblName);
				}
				{
					txtName = new JTextField();
					txtName.getDocument().addDocumentListener(new DocumentListener() {
						public void removeUpdate(DocumentEvent arg0) {}
						public void changedUpdate(DocumentEvent e) {}
						
						public void insertUpdate(DocumentEvent arg0) {
							if(selectedConfig != null) {
								selectedConfig.setName(txtName.getText());
								datasourceList.repaint();
							}
						}
					});
					GridBagConstraints gbc_txtName = new GridBagConstraints();
					gbc_txtName.insets = new Insets(0, 0, 5, 5);
					gbc_txtName.fill = GridBagConstraints.HORIZONTAL;
					gbc_txtName.gridx = 1;
					gbc_txtName.gridy = 0;
					panel.add(txtName, gbc_txtName);
					txtName.setColumns(10);
				}
				{
					JLabel lblLibrary = new JLabel("Library:");
					GridBagConstraints gbc_lblLibrary = new GridBagConstraints();
					gbc_lblLibrary.anchor = GridBagConstraints.EAST;
					gbc_lblLibrary.insets = new Insets(0, 0, 5, 5);
					gbc_lblLibrary.gridx = 0;
					gbc_lblLibrary.gridy = 1;
					panel.add(lblLibrary, gbc_lblLibrary);
				}
				{
					txtLibrary = new JTextField();
					txtLibrary.getDocument().addDocumentListener(new DocumentListener() {
						public void removeUpdate(DocumentEvent arg0) {}
						public void changedUpdate(DocumentEvent arg0) {}
						
						public void insertUpdate(DocumentEvent e) {
							if(selectedConfig != null) {
								selectedConfig.setLibrary(txtLibrary.getText());
								datasourceList.repaint();
							}
						}
					});
					GridBagConstraints gbc_txtLibrary = new GridBagConstraints();
					gbc_txtLibrary.insets = new Insets(0, 0, 5, 5);
					gbc_txtLibrary.fill = GridBagConstraints.HORIZONTAL;
					gbc_txtLibrary.gridx = 1;
					gbc_txtLibrary.gridy = 1;
					panel.add(txtLibrary, gbc_txtLibrary);
					txtLibrary.setColumns(10);
				}
				{
					JButton button = new JButton("...");
					button.addMouseListener(new MouseAdapter() {
						public void mouseClicked(MouseEvent arg0) {
							final JFileChooser fc = new JFileChooser();
							fc.setMultiSelectionEnabled(true);
							fc.setFileFilter(new FileFilter() {
								public String getDescription() {
									return "Java .jar files";
								}
								
								@Override
								public boolean accept(File f) {
									if(f != null) {
										return f.getAbsolutePath().toLowerCase().endsWith(".jar");
									}
									return false;
								}
							});
							int returnVal = fc.showDialog(parent, "Select");
							
							if(returnVal == JFileChooser.APPROVE_OPTION) {
								File[] selectedFiles = fc.getSelectedFiles();
								StringBuilder buffer = new StringBuilder();
								for (File file : selectedFiles) {
									String semicolon = "";
									if(file != null) {
										buffer.append(semicolon);
										buffer.append(file.getAbsolutePath());
										semicolon = ";";
									}
								}
								txtLibrary.setText(buffer.toString());
							}
						}
					});
					button.setPreferredSize(new Dimension(25, 25));
					GridBagConstraints gbc_button = new GridBagConstraints();
					gbc_button.insets = new Insets(0, 0, 5, 0);
					gbc_button.gridx = 2;
					gbc_button.gridy = 1;
					panel.add(button, gbc_button);
				}
				{
					JLabel lblClassname = new JLabel("Classname:");
					GridBagConstraints gbc_lblClassname = new GridBagConstraints();
					gbc_lblClassname.anchor = GridBagConstraints.EAST;
					gbc_lblClassname.insets = new Insets(0, 0, 5, 5);
					gbc_lblClassname.gridx = 0;
					gbc_lblClassname.gridy = 2;
					panel.add(lblClassname, gbc_lblClassname);
				}
				{
					JButton btnNewButton = new JButton("");
					btnNewButton.addMouseListener(new MouseAdapter() {
						public void mouseClicked(MouseEvent e) {
							// look in each jar specified in the libraries, and collect names of classes that implement java.sql.Driver
							List<String> driverClassNames = new ArrayList<>();
							String libLocations = txtLibrary.getText();
							if(libLocations != null && libLocations.trim().length() > 0) {
								String[] fileLocation = libLocations.split(";");
								for (String location : fileLocation) {
									driverClassNames.addAll(FilesystemUtil.getClassesInJarThatImplementJdbcDriver(location));
								}
							}
							
							// add class names to driver class dropdown
							comboBoxDriverClassnames.removeAllItems();
							for (String driverClass : driverClassNames) {
								comboBoxDriverClassnames.addItem(driverClass);
							}
							comboBoxDriverClassnames.repaint();
						}
					});
					{
						this.comboBoxDriverClassnames = new JComboBox<String>();
						comboBoxDriverClassnames.addMouseListener(new MouseListener() {
							
							public void mouseReleased(MouseEvent arg0) {}
							public void mousePressed(MouseEvent arg0) {}
							public void mouseExited(MouseEvent arg0) {}
							public void mouseEntered(MouseEvent arg0) {}
							
							@Override
							public void mouseClicked(MouseEvent e) {
								if(selectedConfig != null) {
									selectedConfig.setClassName((String)comboBoxDriverClassnames.getSelectedItem());
									datasourceList.repaint();
								}
							}
						});
						GridBagConstraints gbc_comboBoxDriverClassnames = new GridBagConstraints();
						gbc_comboBoxDriverClassnames.insets = new Insets(0, 0, 5, 5);
						gbc_comboBoxDriverClassnames.fill = GridBagConstraints.HORIZONTAL;
						gbc_comboBoxDriverClassnames.gridx = 1;
						gbc_comboBoxDriverClassnames.gridy = 2;
						panel.add(comboBoxDriverClassnames, gbc_comboBoxDriverClassnames);
					}
					btnNewButton.setIcon(new ImageIcon(DatasourceConfigurationManagerGui.class.getResource("/org/malibu/monacosql/gui/6.png")));
					btnNewButton.setPreferredSize(new Dimension(25, 25));
					GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
					gbc_btnNewButton.insets = new Insets(0, 0, 5, 0);
					gbc_btnNewButton.gridx = 2;
					gbc_btnNewButton.gridy = 2;
					panel.add(btnNewButton, gbc_btnNewButton);
				}
				{
					JLabel lblSampleUrl = new JLabel("Sample URL:");
					GridBagConstraints gbc_lblSampleUrl = new GridBagConstraints();
					gbc_lblSampleUrl.anchor = GridBagConstraints.EAST;
					gbc_lblSampleUrl.insets = new Insets(0, 0, 5, 5);
					gbc_lblSampleUrl.gridx = 0;
					gbc_lblSampleUrl.gridy = 3;
					panel.add(lblSampleUrl, gbc_lblSampleUrl);
				}
				{
					txtSampleUrl = new JTextField();
					txtSampleUrl.getDocument().addDocumentListener(new DocumentListener() {
						public void removeUpdate(DocumentEvent arg0) {}
						public void changedUpdate(DocumentEvent arg0) {}
						
						public void insertUpdate(DocumentEvent e) {
							if(selectedConfig != null) {
								selectedConfig.setSampleUrl(txtSampleUrl.getText());
								datasourceList.repaint();
							}
						}
					});
					GridBagConstraints gbc_txtSampleUrl = new GridBagConstraints();
					gbc_txtSampleUrl.insets = new Insets(0, 0, 5, 5);
					gbc_txtSampleUrl.fill = GridBagConstraints.HORIZONTAL;
					gbc_txtSampleUrl.gridx = 1;
					gbc_txtSampleUrl.gridy = 3;
					panel.add(txtSampleUrl, gbc_txtSampleUrl);
					txtSampleUrl.setColumns(10);
				}
			}
			{
				JPanel datasourceListPanel = new JPanel();
				splitPane.setLeftComponent(datasourceListPanel);
				datasourceListPanel.setLayout(new BorderLayout(0, 0));
				{
					JPanel listButtonPanel = new JPanel();
					FlowLayout fl_listButtonPanel = (FlowLayout) listButtonPanel.getLayout();
					fl_listButtonPanel.setVgap(2);
					fl_listButtonPanel.setHgap(2);
					fl_listButtonPanel.setAlignment(FlowLayout.LEFT);
					listButtonPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
					datasourceListPanel.add(listButtonPanel, BorderLayout.NORTH);
					{
						JButton btnNewButton_1 = new JButton("");
						btnNewButton_1.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								String name = "New Config";
								String library = "";
								String classname = "";
								String sampleUrl = "";
								// load values from inputs, if filled out
//								if(txtName.getText() != null && txtName.getText().trim().length() > 0) {
//									name = txtName.getText();
//								}
//								if(txtLibrary.getText() != null && txtLibrary.getText().trim().length() > 0) {
//									library = txtLibrary.getText();
//								}
//								if(comboBoxDriverClassnames.getSelectedItem() != null && ((String)comboBoxDriverClassnames.getSelectedItem()).trim().length() > 0) {
//									classname = (String)comboBoxDriverClassnames.getSelectedItem();
//								}
//								if(txtSampleUrl.getText() != null && txtSampleUrl.getText().trim().length() > 0) {
//									sampleUrl = txtSampleUrl.getText();
//								}
								
								// create new config
								DatasourceConfiguration newConfig = new DatasourceConfiguration();
								newConfig.setName(name);
								newConfig.setLibrary(library);
								newConfig.setClassName(classname);
								newConfig.setSampleUrl(sampleUrl);
								newConfig.setId(UUID.randomUUID().toString());
								
								// add new config to list
								((DefaultListModel<DatasourceConfiguration>)datasourceList.getModel()).addElement(newConfig);
							}
						});
						btnNewButton_1.setIcon(new ImageIcon(DatasourceConfigurationManagerGui.class.getResource("/org/malibu/monacosql/gui/10.png")));
						btnNewButton_1.setPreferredSize(new Dimension(25, 25));
						listButtonPanel.add(btnNewButton_1);
					}
					{
						JButton btnNewButton_2 = new JButton("");
						btnNewButton_2.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent arg0) {
								((DefaultListModel<DatasourceConfiguration>)datasourceList.getModel()).removeElement(datasourceList.getSelectedValue());
							}
						});
						btnNewButton_2.setIcon(new ImageIcon(DatasourceConfigurationManagerGui.class.getResource("/org/malibu/monacosql/gui/12.png")));
						btnNewButton_2.setPreferredSize(new Dimension(25, 25));
						listButtonPanel.add(btnNewButton_2);
					}
				}
				{
					datasourceList = new JList<>();
					datasourceList.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							selectedConfig = datasourceList.getSelectedValue();
							txtName.setText(selectedConfig.getName());
							txtLibrary.setText(selectedConfig.getLibrary());
							comboBoxDriverClassnames.removeAll();
							comboBoxDriverClassnames.addItem(selectedConfig.getClassName());
							txtSampleUrl.setText(selectedConfig.getSampleUrl());
						}
					});
					{
						// create model from saved config file
						DefaultListModel<DatasourceConfiguration> listModel = new DefaultListModel<DatasourceConfiguration>();
						datasourceList.setModel(listModel);
						
						List<DatasourceConfiguration> configs = DatasourceConfigManager.getDatasourceConfigurations();
						for(DatasourceConfiguration config : configs) {
							listModel.addElement(config);
						}
						
						// redraw list after we've added several items
						datasourceList.repaint();
					}
					datasourceListPanel.add(datasourceList, BorderLayout.CENTER);
				}
			}
			splitPane.setDividerLocation(170);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) {
						// write updated datasources to file
						DefaultListModel<DatasourceConfiguration> model = (DefaultListModel<DatasourceConfiguration>)datasourceList.getModel();
						
						List<DatasourceConfiguration> configs = new ArrayList<>();
						for(int index = 0; index < model.getSize(); index++) {
							configs.add(model.get(index));
						}
						DatasourceConfigManager.writeDatasourceConfigsToFile(configs);
						
						// close window
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}
}

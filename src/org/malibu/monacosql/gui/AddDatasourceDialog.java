package org.malibu.monacosql.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.malibu.monacosql.datasource.Datasource;
import org.malibu.monacosql.datasource.DatasourceConfigManager;
import org.malibu.monacosql.datasource.DatasourceConfiguration;
import org.malibu.monacosql.datasource.DatasourceManager;

public class AddDatasourceDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JComboBox<DatasourceConfiguration> comboDriver;
	private JTextField txtName;
	private JTextField txtUrl;
	private JTextField txtUsername;
	private JTextField txtPassword;
	
	private String existingDatasourceId = null;
	
	private JTree datasourceTree;

	/**
	 * Create the dialog.
	 */
	public AddDatasourceDialog(JFrame parent, final JTree datasourceTree) {
		super(parent,true);
		
		this.datasourceTree = datasourceTree;
		
		setPreferredSize(new Dimension(300, 310));
		setType(Type.UTILITY);
		setBounds(100, 100, 300, 342);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JLabel lblAddDatasource = new JLabel("Add Datasource");
			lblAddDatasource.setHorizontalAlignment(SwingConstants.CENTER);
			contentPanel.add(lblAddDatasource, BorderLayout.NORTH);
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.CENTER);
			GridBagLayout gbl_panel = new GridBagLayout();
			gbl_panel.columnWidths = new int[] {0, 70, 80};
			gbl_panel.rowHeights = new int[] {30, 30, 30, 30, 30};
			gbl_panel.columnWeights = new double[]{0.0, 0.0, 1.0};
			gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
			panel.setLayout(gbl_panel);
			{
				JLabel lblName = new JLabel("Name:");
				lblName.setHorizontalAlignment(SwingConstants.RIGHT);
				GridBagConstraints gbc_lblName = new GridBagConstraints();
				gbc_lblName.fill = GridBagConstraints.BOTH;
				gbc_lblName.insets = new Insets(0, 0, 5, 5);
				gbc_lblName.gridx = 1;
				gbc_lblName.gridy = 0;
				panel.add(lblName, gbc_lblName);
			}
			{
				txtName = new JTextField();
				txtName.setPreferredSize(new Dimension(160, 10));
				txtName.setMinimumSize(new Dimension(160, 10));
				GridBagConstraints gbc_txtName = new GridBagConstraints();
				gbc_txtName.fill = GridBagConstraints.BOTH;
				gbc_txtName.insets = new Insets(0, 0, 5, 0);
				gbc_txtName.gridx = 2;
				gbc_txtName.gridy = 0;
				panel.add(txtName, gbc_txtName);
				txtName.setColumns(10);
			}
			{
				JLabel lblDbType = new JLabel("Driver:");
				GridBagConstraints gbc_lblDbType = new GridBagConstraints();
				gbc_lblDbType.anchor = GridBagConstraints.EAST;
				gbc_lblDbType.insets = new Insets(0, 0, 5, 5);
				gbc_lblDbType.gridx = 1;
				gbc_lblDbType.gridy = 1;
				panel.add(lblDbType, gbc_lblDbType);
			}
			{
				comboDriver = new JComboBox<DatasourceConfiguration>();
				comboDriver.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e) {
						if(e.getStateChange() == ItemEvent.DESELECTED || e.getStateChange() == ItemEvent.SELECTED) {
							DatasourceConfiguration config = (DatasourceConfiguration)comboDriver.getSelectedItem();
							txtUrl.setText(config.getSampleUrl());
						}
					}
				});
				DefaultComboBoxModel<DatasourceConfiguration> model = new DefaultComboBoxModel<DatasourceConfiguration>();
				List<DatasourceConfiguration> configs = DatasourceConfigManager.getDatasourceConfigurations();
				for(DatasourceConfiguration config : configs) {
					model.addElement(config);
				}
				comboDriver.setModel(model);
				comboDriver.setMinimumSize(new Dimension(120, 22));
				comboDriver.setPreferredSize(new Dimension(120, 22));
				GridBagConstraints gbc_comboDriver = new GridBagConstraints();
				gbc_comboDriver.insets = new Insets(0, 0, 5, 0);
				gbc_comboDriver.fill = GridBagConstraints.HORIZONTAL;
				gbc_comboDriver.gridx = 2;
				gbc_comboDriver.gridy = 1;
				panel.add(comboDriver, gbc_comboDriver);
			}
			{
				JLabel lblUrl = new JLabel("URL:");
				lblUrl.setHorizontalAlignment(SwingConstants.RIGHT);
				GridBagConstraints gbc_lblUrl = new GridBagConstraints();
				gbc_lblUrl.fill = GridBagConstraints.BOTH;
				gbc_lblUrl.insets = new Insets(0, 0, 5, 5);
				gbc_lblUrl.gridx = 1;
				gbc_lblUrl.gridy = 2;
				panel.add(lblUrl, gbc_lblUrl);
			}
			{
				txtUrl = new JTextField();
				txtUrl.setPreferredSize(new Dimension(160, 10));
				txtUrl.setMinimumSize(new Dimension(160, 10));
				GridBagConstraints gbc_txtUrl = new GridBagConstraints();
				gbc_txtUrl.fill = GridBagConstraints.BOTH;
				gbc_txtUrl.insets = new Insets(0, 0, 5, 0);
				gbc_txtUrl.gridx = 2;
				gbc_txtUrl.gridy = 2;
				panel.add(txtUrl, gbc_txtUrl);
				txtUrl.setColumns(10);
				
				// set URL for first config in list (before user changes it)
				if(comboDriver != null && comboDriver.getItemCount() > 0) {
					txtUrl.setText(comboDriver.getItemAt(0).getSampleUrl());
				}
			}
			{
				JLabel lblUsername = new JLabel("Username:");
				lblUsername.setHorizontalAlignment(SwingConstants.RIGHT);
				GridBagConstraints gbc_lblUsername = new GridBagConstraints();
				gbc_lblUsername.fill = GridBagConstraints.BOTH;
				gbc_lblUsername.insets = new Insets(0, 0, 5, 5);
				gbc_lblUsername.gridx = 1;
				gbc_lblUsername.gridy = 3;
				panel.add(lblUsername, gbc_lblUsername);
			}
			{
				txtUsername = new JTextField();
				txtUsername.setPreferredSize(new Dimension(160, 10));
				txtUsername.setMinimumSize(new Dimension(160, 10));
				GridBagConstraints gbc_txtUsername = new GridBagConstraints();
				gbc_txtUsername.fill = GridBagConstraints.BOTH;
				gbc_txtUsername.insets = new Insets(0, 0, 5, 0);
				gbc_txtUsername.gridx = 2;
				gbc_txtUsername.gridy = 3;
				panel.add(txtUsername, gbc_txtUsername);
				txtUsername.setColumns(10);
			}
			{
				JLabel lblPassword = new JLabel("Password:");
				lblPassword.setHorizontalAlignment(SwingConstants.RIGHT);
				GridBagConstraints gbc_lblPassword = new GridBagConstraints();
				gbc_lblPassword.fill = GridBagConstraints.BOTH;
				gbc_lblPassword.insets = new Insets(0, 0, 5, 5);
				gbc_lblPassword.gridx = 1;
				gbc_lblPassword.gridy = 4;
				panel.add(lblPassword, gbc_lblPassword);
			}
			{
				txtPassword = new JTextField();
				txtPassword.setPreferredSize(new Dimension(160, 10));
				txtPassword.setMinimumSize(new Dimension(160, 10));
				GridBagConstraints gbc_txtPassword = new GridBagConstraints();
				gbc_txtPassword.insets = new Insets(0, 0, 5, 0);
				gbc_txtPassword.fill = GridBagConstraints.BOTH;
				gbc_txtPassword.gridx = 2;
				gbc_txtPassword.gridy = 4;
				panel.add(txtPassword, gbc_txtPassword);
				txtPassword.setColumns(10);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				final JDialog window = this;
				okButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						if(datasourceTree != null) {
							Datasource datasource = new Datasource();
							datasource.setDatasourceId(existingDatasourceId);
							datasource.setDriverId(((DatasourceConfiguration)comboDriver.getSelectedItem()).getId());
							datasource.setName(txtName.getText());
							datasource.setUrl(txtUrl.getText());
							datasource.setUsername(txtUsername.getText());
							datasource.setPassword(txtPassword.getText());
							
							DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)datasourceTree.getLastSelectedPathComponent();
							DefaultMutableTreeNode root = (DefaultMutableTreeNode)((DefaultTreeModel)datasourceTree.getModel()).getRoot();
							
							if (selectedNode == null) {
								// nothing selected, get root
								selectedNode = root;
							}
							
							if(selectedNode.getUserObject() instanceof Datasource) {
								// datasource was selected
								((DefaultMutableTreeNode)selectedNode.getParent()).add(new DefaultMutableTreeNode(datasource));
							} else if (selectedNode.getUserObject() instanceof String) {
								selectedNode.add(new DefaultMutableTreeNode(datasource));
							}
							
							try {
								DatasourceManager.saveDatasourcesToFile(root);
							} catch (IOException e1) {
								JOptionPane.showMessageDialog(window, "Error occurred saving datasource, your datasources.config file may be messed up...",
										"Error saving datasource", JOptionPane.ERROR_MESSAGE);
								e1.printStackTrace();
							}
							
							// reload tree
							((DefaultTreeModel)datasourceTree.getModel()).reload(root);
						}
						
						window.dispose();
					}
				});
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				final JDialog window = this;
				cancelButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						window.dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		pack();
	}
	
	public void setExistingDatasourceId(String datasourceId) {
		this.existingDatasourceId = datasourceId;
	}

}

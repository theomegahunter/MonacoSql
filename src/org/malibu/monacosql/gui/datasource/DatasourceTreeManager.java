package org.malibu.monacosql.gui.datasource;

import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.malibu.monacosql.datasource.Datasource;
import org.malibu.monacosql.datasource.DatasourceManager;

public class DatasourceTreeManager {
	public static void removeSelectedNodeFromTree(JTree tree) {
		if(tree != null) {
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
			((DefaultTreeModel)tree.getModel()).removeNodeFromParent(selectedNode);
		}
	}
	
	public static void addFolderToTreeAtSelectedNode(JTree tree, String newFolderName) {
		if(newFolderName != null) {
			// get selected node and root
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
			DefaultMutableTreeNode root = (DefaultMutableTreeNode)((DefaultTreeModel)tree.getModel()).getRoot();
			
			if (selectedNode == null) {
				// nothing selected, get root
				selectedNode = root;
			}
			
			if(selectedNode.getUserObject() instanceof Datasource) {
				// datasource was selected
				((DefaultMutableTreeNode)selectedNode.getParent()).add(new DefaultMutableTreeNode(newFolderName));
			} else if (selectedNode.getUserObject() instanceof String) {
				selectedNode.add(new DefaultMutableTreeNode(newFolderName));
			}
			
			try {
				DatasourceManager.saveDatasourcesToFile(root);
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, "Error occurred saving datasource, your datasources.config file may be messed up...",
						"Error saving datasource", JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
			
			// reload tree
			((DefaultTreeModel)tree.getModel()).reload(root);
		}
	}
}

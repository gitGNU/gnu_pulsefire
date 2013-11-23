/*
 * Copyright (c) 2011, Willem Cazander
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *   following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *   the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.nongnu.pulsefire.device.ui.debug;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

public class JDebugTreePanel extends JPanel {

	private static final long serialVersionUID = 7103843894253672031L;
	private JDebugPanel debugPanel = null;
	private JTree debugTree = null;
	private JTextField filterName = null;
	private JComboBox filterClass = null;
	private List<Class<?>> filterClassList = null;
	
	public JDebugTreePanel(JDebugPanel debugPanel) {
		this.debugPanel = debugPanel;
		filterClassList = new ArrayList<Class<?>>(100);
		setLayout(new BorderLayout());
		add(createFilterPanel(), BorderLayout.PAGE_START);
		add(createTreePanel(), BorderLayout.CENTER);
	}
	
	private JPanel createFilterPanel() {
		
		filterName = new JTextField(20);
		filterClass = new JComboBox();
		
		JPanel resultPanel = new JPanel();
		resultPanel.setBorder(BorderFactory.createTitledBorder("Filters"));
		resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.PAGE_AXIS));
		
		resultPanel.add(new JLabel("Name:"));
		resultPanel.add(filterName);
		resultPanel.add(new JLabel("Class:"));
		resultPanel.add(filterClass);
		
		return resultPanel;
	}
	
	private JPanel createTreePanel() {
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(new SwingTreeNode(SwingTreeNodeType.ROOT));
		
		debugTree = new JTree(new SwingTreeModel(root));
		debugTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if (e.getClickCount() == 1 && debugTree.getSelectionModel().isSelectionEmpty()==false) {
					try {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)debugTree.getSelectionModel().getSelectionPath().getLastPathComponent();
					if (node.getUserObject() instanceof String) {
						return;
					}
					SwingTreeNode swingNode = (SwingTreeNode)node.getUserObject();
					if (swingNode != null) {
						if (swingNode.getType() == SwingTreeNodeType.COMPONENT) {
							debugPanel.fireNodeSelected(swingNode);
						}
					}
					} catch (Exception ee) {
						ee.printStackTrace();
					}
				}
			}
		});

		JPanel treePanel = new JPanel();
		treePanel.add(debugTree);
		treePanel.setLayout(new GridLayout(1,1));
		
		rebuildTree();
		return treePanel;
	}
	
	class SwingTreeModel extends DefaultTreeModel {
		public SwingTreeModel(TreeNode root) {
			super(root);
		}

		private static final long serialVersionUID = -7436681803506994277L;

		@Override
		public void addTreeModelListener(TreeModelListener l) {
			super.addTreeModelListener(l);
		}
	}
	
	public void rebuildTree() {
		Container debugComponent = debugPanel.getDebugComponent();
		if (debugComponent==null) {
			return;
		}
		filterClassList.clear();
		
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)debugTree.getModel().getRoot();
		root.removeAllChildren();
		walkTree(debugComponent,root);
		
		filterClass.removeAllItems();
		for (Class<?> filterItem:filterClassList) {
			filterClass.addItem(filterItem);
		}
		
		SwingUtilities.updateComponentTreeUI(debugTree);
	}
	
	private void walkTree(Container parent,DefaultMutableTreeNode parentNode) {
		DefaultMutableTreeNode parentChilderen = createComponentNode(parent,parentNode);
		for (Component child:parent.getComponents()) {
			if (child instanceof Container) {
				walkTree((Container)child,parentChilderen);
			} else {
				createComponentNode(child,parentChilderen);
			}
		}
	}
	
	private DefaultMutableTreeNode createComponentNode(Component c,DefaultMutableTreeNode parentNode) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(new SwingTreeNode(SwingTreeNodeType.COMPONENT,c));
		parentNode.add(node);
		Class<?> compClass = c.getClass();
		if (filterClassList.contains(compClass)==false) {
			filterClassList.add(compClass);
		}
		return node;
	}
}

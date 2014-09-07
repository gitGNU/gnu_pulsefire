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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;

public class JDebugPanel extends JPanel {
	
	private static final long serialVersionUID = 5834323332973417L;
	private JTabbedPane tabPane = null;
	private JDebugTreePanel treePanel = null;
	private Container debugComponent = null;
	private List<SwingTreeNodeListener> nodeListeners = null;
	
	public JDebugPanel() {
		nodeListeners = new ArrayList<SwingTreeNodeListener>(10);
		
		setLayout(new BorderLayout());
		add(createTreeSplit(), BorderLayout.CENTER);
		
		treePanel.rebuildTree();
	}
	
	static public JDebugPanel openDebugFrame(String title) {
		JFrame frame = new JFrame(title);
		JDebugPanel result = new JDebugPanel();
		frame.getContentPane().add(result);
		frame.setPreferredSize(new Dimension(640, 480));
		frame.pack();
		frame.setVisible(true);
		return result;
	}
	
	private JSplitPane createTreeSplit() {
		treePanel = new JDebugTreePanel(this);
		JScrollPane sp0 = createJScrollPane(treePanel);
		JPanel sp1 = createContentPanel();
		JSplitPane treeSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,sp0,sp1);
		treeSplitPane.setOneTouchExpandable(true);
		treeSplitPane.setResizeWeight(0.2);
		treeSplitPane.setDividerLocation(170);
		sp0.setMinimumSize(new Dimension(200, 400));
		sp1.setMinimumSize(new Dimension(400, 400));
		return treeSplitPane;
	}
	
	private JPanel createContentPanel() {
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new GridLayout(1,0));
		
		tabPane = new JTabbedPane();
		contentPane.add(tabPane);
		
		addTabPanel("Bean Properties",new JDebugBeanPanel());
		addTabPanel("Action Map",new JDebugActionPanel());
		
		return contentPane;
	}
	
	private void addTabPanel(String title,JPanel tab) {
		JScrollPane p = createJScrollPane(tab);
		tabPane.addTab(title, p);
		if (tab instanceof SwingTreeNodeListener) {
			addSwingTreeNodeListener((SwingTreeNodeListener)tab);
		}
	}
	
	private JScrollPane createJScrollPane(JPanel innerPanel) {
		JScrollPane scrollPane = new JScrollPane(innerPanel);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
		//innerPanel.setParentScrollPane(scrollPane);
		return scrollPane;
	}
	
	
	public JTabbedPane getTabPane() {
		return tabPane;
	}
	
	/**
	 * @return the debugComponent
	 */
	public Container getDebugComponent() {
		return debugComponent;
	}
	
	/**
	 * @param debugComponent the debugComponent to set
	 */
	public void setDebugComponent(Container debugComponent) {
		this.debugComponent = debugComponent;
		treePanel.rebuildTree();
	}
	
	public void addSwingTreeNodeListener(SwingTreeNodeListener listener) {
		nodeListeners.add(listener);
	}
	
	public void removeSwingTreeNodeListener(SwingTreeNodeListener listener) {
		nodeListeners.remove(listener);
	}
	
	public List<SwingTreeNodeListener> getSwingTreeNodeListeners() {
		return nodeListeners;
	}
	
	protected void fireNodeSelected(SwingTreeNode node) {
		for (int i=0;i<nodeListeners.size();i++) {
			SwingTreeNodeListener listener = nodeListeners.get(i);
			listener.selectNode(node);
		}
	}
}

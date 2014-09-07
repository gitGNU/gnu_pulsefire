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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ToolTipManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

public class JDebugActionPanel extends JPanel implements SwingTreeNodeListener {
	
	private static final long serialVersionUID = -1570334020885970014L;
	private JTable table = null;
	private ActionMapTableModel tableModel = null;
	
	public JDebugActionPanel() {
		tableModel = new ActionMapTableModel();
		table = new JTable(tableModel);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setFillsViewportHeight(true);
		table.setShowHorizontalLines(true);
		table.setRowMargin(2);
		table.setRowHeight(26);
		setLayout(new BorderLayout());
		add(table.getTableHeader(), BorderLayout.PAGE_START);
		add(table, BorderLayout.CENTER);
		
		ToolTipManager.sharedInstance().unregisterComponent(table);
		ToolTipManager.sharedInstance().unregisterComponent(table.getTableHeader());
		
		TableColumn tcName = table.getColumnModel().getColumn(0);
		TableColumn tcValue = table.getColumnModel().getColumn(1);
		TableColumn tcClass = table.getColumnModel().getColumn(2);
		
		tcName.setPreferredWidth(100);
		tcValue.setPreferredWidth(160);
		tcClass.setPreferredWidth(220);
	}

	@Override
	public void selectNode(SwingTreeNode node) {
		if (node.getComponent() instanceof JComponent) {
			JComponent comp = (JComponent)node.getComponent();
			tableModel.changeActionMap(comp.getActionMap());
		}
	}

	@Override
	public void selectClear() {
		tableModel.changeActionMap(null);
	}
	
	class ActionMapTableModel extends AbstractTableModel {
		
		private static final long serialVersionUID = -2533301317314196916L;
		private static final String PARENT_KEY = "IS_PARENT_ACTION";
		private List<Action> actions = null;
		
		public ActionMapTableModel() {
			actions = new ArrayList<Action>(100);
		}
		
		public void changeActionMap(ActionMap actionMap) {
			actions.clear();
			if (actionMap!=null) {
				initActionMap(actionMap);
			}
			fireTableDataChanged();
		}
		
		private void initActionMap(ActionMap actionMap) {
			Object[] keysAll = actionMap.allKeys();
			Object[] keys = actionMap.keys();
			if (keysAll==null) {
				return; // no keys
			}
			List<Object> actionsLocal = null;
			if (keys==null) {
				actionsLocal = Collections.emptyList();
			} else {
				actionsLocal = Arrays.asList(actionMap.keys());
			}
			List<Object> actionsAll = Arrays.asList(keysAll);
			for (Object key:actionsAll) {
				Action action = actionMap.get(key);
				if (actionsLocal.contains(action)) {
					action.putValue(PARENT_KEY, "true");
				}
				actions.add(action);
			}
		}
		
		@Override
		public String getColumnName(int col) {
			switch (col) {
			case 0:return Action.NAME;
			case 1:return Action.ACTION_COMMAND_KEY;
			case 2:return Action.ACCELERATOR_KEY;
			case 3:return Action.MNEMONIC_KEY;
			case 4:return Action.SHORT_DESCRIPTION;
			}
			return "error";
		}
		
		@Override
		public int getRowCount() {
			return actions.size();
		}

		@Override
		public int getColumnCount() {
			return 5;
		}

		@Override
		public Object getValueAt(int row, int column) {
			if (row>actions.size()) {
				return "error";
			}
			Action action = actions.get(row);
			switch (column) {
			case 0:
				String name = ""+action.getValue(Action.NAME);
				if (action.getValue(PARENT_KEY)!=null) {
					name = name+"(parent)";
				}
				return name;
			case 1:
				return ""+action.getValue(Action.ACTION_COMMAND_KEY);
			case 2:
				return ""+action.getValue(Action.ACCELERATOR_KEY);
			case 3:
				return ""+action.getValue(Action.MNEMONIC_KEY);
			case 4:
				return ""+action.getValue(Action.SHORT_DESCRIPTION);
			default:
				return "error";
			}
		}
		
	}


}

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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ToolTipManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

public class JDebugBeanPanel extends JPanel implements SwingTreeNodeListener {

	private static final long serialVersionUID = -1410066224391554267L;
	private Logger logger = null;
	private JTable table = null;
	private BeanTableModel tableModel = null; 
	
	public JDebugBeanPanel() {
		logger = Logger.getLogger(JDebugBeanPanel.class.getName());
		tableModel = new BeanTableModel();
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
		
		tcName.setPreferredWidth(160);
		tcValue.setPreferredWidth(220);
		tcClass.setPreferredWidth(300);
	}
	
	@Override
	public void selectNode(SwingTreeNode node) {
		tableModel.changeBean(node.getComponent());
	}
	
	@Override
	public void selectClear() {
		tableModel.changeBean(null);
	}

	class BeanTableModel extends AbstractTableModel {
		
		private static final long serialVersionUID = -2533301317314196916L;
		private List<String> properties = null;
		private List<String> values = null;
		private List<String> types = null;
		
		public BeanTableModel() {
			properties = new ArrayList<String>(100);
			values = new ArrayList<String>(100);
			types = new ArrayList<String>(100);
		}
		
		public void changeBean(Object bean) {
			properties.clear();
			values.clear();
			types.clear();
			if (bean!=null) {
				initBean(bean);
			}
			fireTableDataChanged();
		}
		
		private void initBean(Object bean) {
			Class<?> beanClass = bean.getClass();
			
			for (Method m:beanClass.getMethods()) {
				if (!m.getName().startsWith("set")) {
					continue;
				}
				if (m.getParameterTypes().length!=1) {
					continue;
				}
				String property = m.getName().substring(3);
				properties.add(property);
			}
			Collections.sort(properties);
			List<String> propertiesRm = new ArrayList<String>(10);
			for (String property:properties) {
				Method getMethod = null;
				for (Method m:beanClass.getMethods()) {
					if (m.getName().equals("get"+property)) {
						getMethod = m;
						break;
					}
					if (m.getName().equals("is"+property)) {
						getMethod = m;
						break;
					}
					if (m.getName().equals("has"+property)) {
						getMethod = m;
						break;
					}
				}
				if (getMethod==null) {
					logger.warning("No bean getter for property: "+property);
					propertiesRm.add(property);
					continue;
				}
				if (getMethod.getParameterTypes().length>0) {
					propertiesRm.add(property);
					continue;
				}
				String value = null;
				try {
					Object valueObj = getMethod.invoke(bean, new Object[]{});
					value = ""+valueObj;
				} catch (Exception e) {
					logger.warning("Error while getting property: "+property+" "+e.getMessage());
					propertiesRm.add(property);
					continue;
				}
				values.add(value);
				types.add(getMethod.getReturnType().getName());
			}
			properties.removeAll(propertiesRm);
		}
		
		@Override
		public String getColumnName(int col) {
			switch (col) {
			case 0:return "Property";
			case 1:return "Value";
			case 2:return "ClassType";
			}
			return "error";
		}
		
		@Override
		public int getRowCount() {
			return properties.size();
		}

		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public Object getValueAt(int row, int column) {
			if (row>properties.size()) {
				return "error";
			}
			switch (column) {
			case 0:
				return properties.get(row);
			case 1:
				return values.get(row);
			case 2:
				return types.get(row);
			default:
				return "error";
			}
		}
		
	}


}

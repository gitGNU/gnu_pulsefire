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

package org.nongnu.pulsefire.device.ui.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.ToolTipManager;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.DocumentFilter;
import javax.swing.text.InternationalFormatter;

import org.nongnu.pulsefire.device.DeviceConnectListener;
import org.nongnu.pulsefire.device.DeviceData;
import org.nongnu.pulsefire.device.ui.PulseFireUI;
import org.nongnu.pulsefire.wire.Command;
import org.nongnu.pulsefire.wire.CommandName;

/**
 * JFireQMapTable is configed JTable for PulseFire QMAP data type.
 * 
 * @author Willem Cazander
 */
public class JFireQMapTable extends JPanel {

	private static final long serialVersionUID = 6413723813214593946L;
	private DeviceConfigVariableTableModel tableModel = null;
	private JTable qmapTable = null;
	
	public JFireQMapTable(CommandName commandName,String colNameA,String colNameB) {
		
		tableModel = new DeviceConfigVariableTableModel(commandName,colNameA,colNameB);
		qmapTable = new JTable(tableModel);
		qmapTable.getTableHeader().setReorderingAllowed(false);
		qmapTable.getTableHeader().setResizingAllowed(false);
		qmapTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		qmapTable.setFillsViewportHeight(true);
		qmapTable.setShowHorizontalLines(true);
		qmapTable.setRowMargin(2);
		qmapTable.setRowHeight(26);
		
		// remove tooltip support in table which make it faster
		ToolTipManager.sharedInstance().unregisterComponent(qmapTable);
		ToolTipManager.sharedInstance().unregisterComponent(qmapTable.getTableHeader());
		
		TableColumn mapNum = qmapTable.getColumnModel().getColumn(0);
		mapNum.setPreferredWidth(50);
		TableColumn varId = qmapTable.getColumnModel().getColumn(1);
		varId.setPreferredWidth(160);
		varId.setCellEditor(new MapVariableIdInputCellEditor());
		varId.setCellRenderer(new MapVariableIdInputCellRenderer());
		TableColumn colA = qmapTable.getColumnModel().getColumn(2);
		colA.setPreferredWidth(90);
		colA.setCellEditor(new MapArguInputCellEditor());
		TableColumn colB = qmapTable.getColumnModel().getColumn(3);
		colB.setCellEditor(new MapArguInputCellEditor());
		colB.setPreferredWidth(90);
		TableColumn varIdx = qmapTable.getColumnModel().getColumn(4);
		varIdx.setPreferredWidth(55);
		varIdx.setCellEditor(new MapVariableIdxInputCellEditor());
		
		setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
		setLayout(new BorderLayout());
		add(qmapTable.getTableHeader(), BorderLayout.PAGE_START);
		add(qmapTable, BorderLayout.CENTER);
	}
	
	public class DeviceConfigVariableTableModel extends AbstractTableModel  implements DeviceConnectListener  {
		
		private static final long serialVersionUID = 3636761640345147211L;
		private String[] columnNames = new String[] {"num","variable","a","b","idx"};
		private CommandName variableName = null;
		volatile private int indexMaxA  = 2;
		private DeviceData deviceData = null;
		private String colNameA = null;
		private String colNameB = null;
		private boolean connected = false;
		
		public DeviceConfigVariableTableModel(CommandName variableName,String colNameA,String colNameB) {
			this.variableName=variableName;
			this.colNameA=colNameA;
			this.colNameB=colNameB;
			deviceData = PulseFireUI.getInstance().getDeviceData();
			PulseFireUI.getInstance().getDeviceManager().addDeviceConnectListener(this);
		}

		JLabel l = new JLabel();
		@Override
		public void deviceConnect() {
			indexMaxA = variableName.getMaxIndexA();
			connected = true;
			fireTableDataChanged();
		}

		@Override
		public void deviceDisconnect() {
			indexMaxA = 2;
			connected = false;
			fireTableDataChanged();
		}
		
		public String getColumnName(int col) {
			if (col==2) {
				return colNameA;
			}
			if (col==3) {
				return colNameB;
			}
			return columnNames[col];
		}
		public int getRowCount() { return indexMaxA; }
		public int getColumnCount() { return columnNames.length; }
		public Object getValueAt(int row, int col) {
			Command cmd = deviceData.getDeviceParameterIndexed(variableName,row);
			if (cmd==null) {
				return "";
			}
			if (connected==false) {
				return "";
			}
			if (col==0) {
				return row;
			}
			if (col==1) {
				return cmd.getArgu1();
			}
			if (col==2) {
				return cmd.getArgu2();
			}
			if (col==3) {
				return cmd.getArgu3();
			}
			if (col==4) {
				return cmd.getArgu4();
			}
			return "";
		}
		public boolean isCellEditable(int row, int col) {
			if (connected==false) {
				return false;
			}
			if (col==0) {
				return false;
			}
			if (col==1) {
				return true;
			}
			String cmdName = (String)getValueAt(row, 1);
			if (cmdName.isEmpty() | "65535".equals(cmdName)) {
				return false;
			}
			if (col!=4) {
				return true;
			}
			Integer mapIdx = new Integer(cmdName);
			for (CommandName c:CommandName.values()) {
				if (c.isMappable() && mapIdx.equals(c.getMapIndex()) && c.isIndexedA()) {
					return true;
				}
			}
			return false;
		}
		public void setValueAt(Object value, int row, int col) {
			Command cmd = deviceData.getDeviceParameterIndexed(variableName,row);
			if (cmd==null) {
				return;
			}
			if (col==0) {
				return;
			}
			if (col==1) {
				cmd.setArgu1((String)value);
			}
			if (col==2) {
				cmd.setArgu2((String)value);
			}
			if (col==3) {
				cmd.setArgu3((String)value);
			}
			if (col==4) {
				cmd.setArgu4((String)value);
			}
			
			PulseFireUI.getInstance().getDeviceManager().requestCommand(cmd);
			
			fireTableCellUpdated(row, col);
		}
	}
	
	public class MapArguInputCellEditor extends AbstractCellEditor implements TableCellEditor,ChangeListener {
		private static final long serialVersionUID = -8600090390794431579L;
		JSpinner component = null;
		int rowIndex = 0;
		int colIndex = 0;
		AbstractFormatterFactory formatFactory = null;
		@SuppressWarnings("serial")
		public MapArguInputCellEditor() {
			component = new JSpinner();
			component.addChangeListener(this);
					
			NumberFormat format = NumberFormat.getIntegerInstance();
			format.setGroupingUsed(false); // or add the group chars to the filter
			formatFactory = new DefaultFormatterFactory(
					new InternationalFormatter(format) {
						@Override
						protected DocumentFilter getDocumentFilter() {
							return new IntegerDocumentFilter();
						}
					}
				);
		}

		
		public Component getTableCellEditorComponent(JTable table, Object value,
				boolean isSelected, int rowIndex, int colIndex) {
			this.rowIndex=rowIndex;
			this.colIndex=colIndex;
			if (value.toString().isEmpty()) {
				return component;
			}
			Integer valueInt = new Integer(value.toString());
			component.setModel(new SpinnerNumberModel((int)valueInt,0,0xFFFF,1));
			//JSpinner.NumberEditor jsEditor = (JSpinner.NumberEditor)component.getEditor();
			//JFormattedTextField textField = jsEditor.getTextField();
			//textField.setFormatterFactory(formatFactory);
			return component;
		}
		
		public Object getCellEditorValue() {
			return component.getValue().toString();
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			qmapTable.getModel().setValueAt(getCellEditorValue(), rowIndex, colIndex);
		}
	}
	class IntegerDocumentFilter extends DocumentFilter {
		@Override
		public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
			if (stringContainsOnlyDigits(string)) {
				super.insertString(fb, offset, string, attr);
			}
		}

		@Override
		public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
			super.remove(fb, offset, length);
		}

		@Override
		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
			if (stringContainsOnlyDigits(text)) {
				super.replace(fb, offset, length, text, attrs);
			}
		}

		private boolean stringContainsOnlyDigits(String text) {
			for (int i = 0; i<text.length(); i++) {
				if (!Character.isDigit(text.charAt(i))) {
					return false;
				}
			}
			return true;
		}
	}
	
	public class MapVariableIdInputCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 4124140489638626053L;

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			/*
			if ((row & 1) > 0) {
				component.setBackground(UIManager.getColor("Table.background"));
			} else {
				component.setBackground(UIManager.getColor("Table.alternateRowColor"));
			}
			*/
			JLabel component = this;
			
			if ("65535".equals(value)) {
				component.setText("NONE");
				return component;
			}
			if (value.toString().isEmpty()) {
				component.setText("");
				return component;
			}
			for (CommandName var:CommandName.values()) {
				if (var.isMappable() && new Integer((String)value).equals(var.getMapIndex())) {
					component.setText(var.name());
					return component;
				}
			}
			setText("");
			return this;
		}		
	}
	
	public class MapVariableIdInputCellEditor extends AbstractCellEditor implements TableCellEditor,DeviceConnectListener,ActionListener {
		private static final long serialVersionUID = -5036452317089888791L;
		private JComboBox component = null;
		
		public MapVariableIdInputCellEditor() {
			component = new JComboBox();
			//component.setOpaque(false);
			component.addActionListener(this);
			PulseFireUI.getInstance().getDeviceManager().addDeviceConnectListener(this);
		}
		
		public Component getTableCellEditorComponent(JTable table, Object value,boolean isSelected, int rowIndex, int vColIndex) {
						
			for (int i=0;i<component.getItemCount();i++) {
				String varName = (String)component.getItemAt(i);
				if ("NONE".equals(varName)) {
					continue;
				}
				CommandName var = CommandName.valueOf(varName);
				if (var.isMappable() && new Integer(var.getMapIndex()).equals(new Integer(value.toString()))) {
					component.setSelectedIndex(i);
				}
			}
			return component;
		}

		public Object getCellEditorValue() {
			if (component.getSelectedItem()==null) {
				return "";
			}
			if (component.getSelectedIndex()==0) {
				return ""+0xFFFF;
			}
			return ""+CommandName.valueOf(component.getSelectedItem().toString()).getMapIndex();
		}
		
		@Override
		public void deviceConnect() {
			component.addItem("NONE");
			List<String> mapVars = new ArrayList<String>(50);
			for (CommandName var:CommandName.values()) {
				if (var.isMappable()) {
					mapVars.add(var.name());
				}
			}
			Collections.sort(mapVars);
			for (String mapVar:mapVars) {
				component.addItem(mapVar);
			}
		}

		@Override
		public void deviceDisconnect() {
			component.removeAllItems();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			fireEditingStopped();
		}
	}
	
	public class MapVariableIdxInputCellEditor extends AbstractCellEditor implements TableCellEditor,ActionListener {
		private static final long serialVersionUID = 1890108511408714142L;
		private JComboBox component = null;

		public MapVariableIdxInputCellEditor() {
			component = new JComboBox();
			component.addActionListener(this);
		}
		
		public Component getTableCellEditorComponent(JTable table, Object value,boolean isSelected, int rowIndex, int vColIndex) {
			
			
			String cmdName = (String)table.getModel().getValueAt(rowIndex, 1);
			if (cmdName.isEmpty() | "65535".equals(value)) {
				component.addItem(value.toString());
				return component;
			}
			Integer mapIdx = new Integer(cmdName);
			for (CommandName c:CommandName.values()) {
				if (c.isMappable() && mapIdx.equals(c.getMapIndex())) {
					if (c.isIndexedA()==false) {
						return component;
					}
					component.removeAllItems();
					for (int i=0;i<c.getMaxIndexA();i++) {
						String item = ""+i;
						component.addItem(item);
						if (item.equals(value)) {
							component.setSelectedIndex(i);
						}
					}
					String allItem = "255";
					component.addItem(allItem);
					if (allItem.equals(value)) {
						component.setSelectedIndex(component.getItemCount()-1);
					}
					return component;
				}
			}
			component.addItem(value.toString());
			return component;
		}

		public Object getCellEditorValue() {
			if (component.getSelectedItem()==null) {
				return "0";
			}
			return component.getSelectedItem().toString();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			fireEditingStopped();
		}
	}
}

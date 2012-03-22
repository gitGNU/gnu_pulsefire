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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.BadLocationException;

import org.nongnu.pulsefire.device.ui.JComponentFactory;
import org.nongnu.pulsefire.device.ui.PulseFireUI;
import org.nongnu.pulsefire.device.ui.SpringLayoutGrid;
import org.nongnu.pulsefire.wire.CommandName;
import org.nongnu.pulsefire.wire.MalCommand;
import org.nongnu.pulsefire.wire.MalCommand.CmdType;
import org.nongnu.pulsefire.wire.MalCommand.ExtType;
import org.nongnu.pulsefire.wire.MalCommand.ExtOpIf;
import org.nongnu.pulsefire.wire.MalCommand.ExtOpVar;
import org.nongnu.pulsefire.wire.MalCommand.ValueType;
import org.nongnu.pulsefire.wire.MalCommand.VarIndex;

/**
 * JMalEditor is table editor for mal commands
 * 
 * @author Willem Cazander
 */
public class JMalEditor extends JPanel implements ActionListener {

	private static final long serialVersionUID = 6107809207895017949L;
	private List<MalCommand> programLines = null;
	private MalCommandTableModel tableModel = null;
	private JTable cmdTable = null;
	private int maxOpcodes = -1;
	private JButton addLineButton = null;
	private JButton editLineButton = null;
	private JButton delLineButton = null;
	
	
	public JMalEditor() {
		programLines = new ArrayList<MalCommand>(32);
		tableModel = new MalCommandTableModel();
		cmdTable = new JTable(tableModel);
		cmdTable.getTableHeader().setReorderingAllowed(false);
		cmdTable.getTableHeader().setResizingAllowed(false);
		cmdTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		cmdTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		cmdTable.setFillsViewportHeight(true);
		cmdTable.setShowHorizontalLines(true);
		cmdTable.setRowMargin(2);
		cmdTable.setRowHeight(26); // 26 or 40 with inline editor
		cmdTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount()==2) {
					MalCommand cmd = programLines.get(cmdTable.getSelectedRow());
					JNewLineDialog newLine = new JNewLineDialog(PulseFireUI.getInstance().getMainFrame(),cmd);
					newLine.setVisible(true);
				}
			}
		});
		
		ToolTipManager.sharedInstance().unregisterComponent(cmdTable); // rm tooltips
		ToolTipManager.sharedInstance().unregisterComponent(cmdTable.getTableHeader());
		
		TableColumn lineNumber = cmdTable.getColumnModel().getColumn(0);
		lineNumber.setPreferredWidth(50);
		TableColumn lineCode = cmdTable.getColumnModel().getColumn(1);
		lineCode.setPreferredWidth(600);
		lineCode.setMinWidth(500);
		lineCode.setCellRenderer(new LineCodeCellRenderer());
		
		setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
		setLayout(new BorderLayout());
		add(cmdTable.getTableHeader(), BorderLayout.PAGE_START);
		add(cmdTable, BorderLayout.CENTER);
		add(createTableActions(),BorderLayout.SOUTH);
	}
	
	private JPanel createTableActions() {
		JPanel tableActions = new JPanel();
		tableActions.setLayout(new FlowLayout(FlowLayout.RIGHT));
		addLineButton = new JButton("Add");
		editLineButton = new JButton("Edit");
		delLineButton = new JButton("Delete");
		
		addLineButton.addActionListener(this);
		editLineButton.addActionListener(this);
		delLineButton.addActionListener(this);
		
		tableActions.add(addLineButton);
		tableActions.add(editLineButton);
		tableActions.add(delLineButton);
		return tableActions;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(addLineButton)) {
			JNewLineDialog newLine = new JNewLineDialog(PulseFireUI.getInstance().getMainFrame(),null);
			newLine.setVisible(true);			
		} else if (e.getSource().equals(editLineButton) && cmdTable.getSelectedRow()!=-1) {
			MalCommand cmd = programLines.get(cmdTable.getSelectedRow());
			JNewLineDialog newLine = new JNewLineDialog(PulseFireUI.getInstance().getMainFrame(),cmd);
			newLine.setVisible(true);
		} else if (e.getSource().equals(delLineButton) && cmdTable.getSelectedRow()!=-1) {
			
			int row = cmdTable.getSelectedRow();
			cmdTable.getSelectionModel().clearSelection();
			programLines.remove(row);
			tableModel.fireTableDataChanged();
			// make shift happe
		}
		
	}
	
	/**
	 * @return the maxOpcodes
	 */
	public int getMaxOpcodes() {
		return maxOpcodes;
	}
	
	/**
	 * @param maxOpcodes the maxOpcodes to set
	 */
	public void setMaxOpcodes(int maxOpcodes) {
		this.maxOpcodes = maxOpcodes;
	}
	
	/**
	 * Saves the opcodes to this byte list.
	 * @return
	 */
	public List<Byte> saveData() {
		List<Byte> programData = new ArrayList<Byte>(programLines.size());
		for (MalCommand cmd:programLines) {
			programData.addAll(cmd.getOpcodes());
		}
		return programData;
	}
	
	/**
	 * Load the opcodes as byte into mal commands.
	 * @param programData
	 */
	public void loadData(List<Byte> programData) {
		programLines.clear();
		Iterator<Byte> data = programData.iterator();
		while (data.hasNext()) {
			MalCommand cmd = new MalCommand();
			boolean result = cmd.parse(data);
			if (result==false) {
				break;
			}
			programLines.add(cmd);
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				tableModel.fireTableDataChanged(); // run event in eventQ
			}
		});
	}
	
	class JNewLineDialog extends JDialog implements ActionListener {

		private static final long serialVersionUID = 8511082377154332785L;
		private JEditPanel editPanel = null;
		private JButton saveButton = null;
		private JButton cancelButton = null;
		
		public JNewLineDialog(Frame parentFrame,MalCommand malCommand) {
			super(parentFrame, true);
			//boolean newEdit = false;
			editPanel = new JEditPanel();
			if (malCommand!=null) {
				MalCommand cmd = new MalCommand();
				cmd.setCmdArgu(malCommand.getCmdArgu());
				cmd.setCmdType(malCommand.getCmdType());
				cmd.setExtOp(malCommand.getExtOp());
				cmd.setExtType(malCommand.getExtType());
				cmd.setValueType(malCommand.getValueType());
				cmd.setVarIndex(malCommand.getVarIndex());
				cmd.compile();
				editPanel.configComponent(cmd); // use clone so we can cancel.
			} else {
				//newEdit = true;
				malCommand = new MalCommand();
				malCommand.init();
				editPanel.configComponent(malCommand);
			}
			JPanel editBorder = JComponentFactory.createJFirePanel("Edit Command");
			editBorder.add(editPanel);
			
			setTitle("Edit Line");
			setMinimumSize(new Dimension(400,350));
			setPreferredSize(new Dimension(450,400));
			setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent we) {
					clearAndHide();
				}
			});
			JPanel mainPanel = new JPanel();
			mainPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
			mainPanel.setLayout(new BorderLayout());
			mainPanel.add(editBorder,BorderLayout.CENTER);
			mainPanel.add(createPanelBottom(),BorderLayout.SOUTH);
			getContentPane().add(mainPanel);
			
			pack();
			setLocationRelativeTo(parentFrame);
		}
		
		public void clearAndHide() {
			setVisible(false);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource()==saveButton) {
				MalCommand cmd = editPanel.getMalCommand();
				programLines.set(cmdTable.getSelectedRow(), cmd);
				tableModel.fireTableDataChanged();
				clearAndHide();
				return;
			} else if (e.getSource()==cancelButton) {
				clearAndHide();
				return;
			}
		}
		
		private JPanel createPanelBottom() {
			JPanel panel = new JPanel();
			panel.setBorder(BorderFactory.createEmptyBorder());
			panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			saveButton = new JButton("Save");
			saveButton.addActionListener(this);
			panel.add(saveButton);
			cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(this);
			panel.add(cancelButton);
			return panel;
		}
	}
	
	public class MalCommandTableModel extends AbstractTableModel {

		private static final long serialVersionUID = -3225731793752475674L;
		private String[] columnNames = new String[] {"LINE","CMD","HEX"};
		
		public String getColumnName(int col) {
			return columnNames[col];
		}
		
		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public int getRowCount() {
			return programLines.size();
		}
		
		@Override
		public Object getValueAt(int row, int col) {
			if (programLines.isEmpty()) {
				return "";
			}
			if (programLines.size()<row) {
				return "";
			}
			if (col==0) {
				return (row+1)*10; // start at "line 10" like basic
			} else if (col==1) {
				return programLines.get(row);
			} else if (col==2) {
				return programLines.get(row).toStringHexOpcodes();
			}
			return "";
		}
	}
	public class LineCodeCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = -4400164721185858628L;
		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			MalCommand cmd = (MalCommand)value;
			setText(cmd.toString());
			return this;
		}
	}
	
	class JEditPanel extends JPanel implements ActionListener {

		private static final long serialVersionUID = -2473578115689529402L;
		private MalCommand malCommand = null;
		private JComboBox cmdTypeBox = null;
		private JComboBox varIdxBox = null;
		private JComboBox progIdxBox = null;
		private JComboBox valueTypeBox = null;
		private JComboBox valueTypeLoadBox = null;
		private JComboBox valueCommandBox = null;
		private JComboBox extTypeComboBox = null;
		private JComboBox extOpComboBox = null;
		private JComboBox gotoLineComboBox = null;
		private JIntegerTextField valueRawTextField = null;
		private volatile boolean actions = true;
		private JLabel malCommandLabel = null;
		private JLabel malCommandDataLabel = null;
		private JLabel cmdTypeBoxLabel = null;
		private JLabel varIdxBoxLabel = null;
		private JLabel progIdxBoxLabel = null;
		private JLabel valueTypeBoxLabel = null;
		private JLabel valueCommandBoxLabel = null;
		private JLabel extTypeComboBoxLabel = null;
		private JLabel extOpComboBoxLabel = null;
		private JLabel gotoLineComboBoxLabel = null;
		private JLabel valueRawTextFieldLabel = null;
		
		public JEditPanel() {
			setLayout(new SpringLayout());
			valueRawTextField	= new JIntegerTextField(-1, 6);
			cmdTypeBox			= new JComboBox(MalCommand.CmdType.values());
			varIdxBox			= new JComboBox(MalCommand.VarIndex.values());
			progIdxBox			= new JComboBox(MalCommand.VarIndex.values());
			valueTypeBox		= new JComboBox(MalCommand.ValueType.values());
			valueTypeLoadBox	= new JComboBox(MalCommand.ValueType.values());
			extTypeComboBox		= new JComboBox(MalCommand.ExtType.values());
			valueCommandBox		= new JComboBox(CommandName.valuesMapIndex().toArray());
			extOpComboBox		= new JComboBox();
			gotoLineComboBox	= new JComboBox();
			valueTypeBox.removeItemAt(valueTypeBox.getItemCount()-1); // remove reversed load 
			
			malCommandDataLabel		= new JLabel();
			malCommandLabel			= new JLabel("Command");
			cmdTypeBoxLabel			= new JLabel("CmdType");
			varIdxBoxLabel			= new JLabel("VarIndex");
			progIdxBoxLabel			= new JLabel("ProgIndex");
			valueTypeBoxLabel		= new JLabel("ValueType");
			valueCommandBoxLabel	= new JLabel("ValueCmd");
			extTypeComboBoxLabel	= new JLabel("ExtType");
			extOpComboBoxLabel		= new JLabel("ExtOp");
			gotoLineComboBoxLabel	= new JLabel("Goto");
			valueRawTextFieldLabel	= new JLabel("ValueRaw");
			
			cmdTypeBox.		addActionListener(this);
			varIdxBox.		addActionListener(this);
			progIdxBox.		addActionListener(this);
			valueTypeBox.	addActionListener(this);
			valueTypeLoadBox.addActionListener(this);
			valueCommandBox.addActionListener(this);
			extTypeComboBox.addActionListener(this);
			extOpComboBox.	addActionListener(this);
			valueRawTextField.getDocument().addDocumentListener(new DocumentListener() {
				@Override public void removeUpdate(DocumentEvent e)  { update(e); }
				@Override public void insertUpdate(DocumentEvent e)  { update(e); }
				@Override public void changedUpdate(DocumentEvent e) { update(e); }
				private void update(DocumentEvent event) {
					if (malCommand!=null) {
						try {
							String value = event.getDocument().getText(0, event.getDocument().getLength());
							if (value.isEmpty()) {
								value = "0";
							}
							malCommand.setCmdArgu(new Integer(value));
						} catch (BadLocationException e) {
							e.printStackTrace();
						}
					}
				}
			});
		}
		
		public MalCommand getMalCommand() {
			return malCommand;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (actions==false) {
				return;
			}
			if (e.getSource().equals(varIdxBox) && varIdxBox.getSelectedIndex()!=-1) {
				malCommand.setVarIndex(VarIndex.values()[varIdxBox.getSelectedIndex()]);
			} else if (e.getSource().equals(cmdTypeBox) && cmdTypeBox.getSelectedIndex()!=-1) {
				malCommand.setCmdType(CmdType.values()[cmdTypeBox.getSelectedIndex()]);
				malCommand.setCmdArgu(0);
				malCommand.setValueType(ValueType.RAW_VALUE);
			} else if (e.getSource().equals(progIdxBox) && progIdxBox.getSelectedIndex()!=-1) {
				malCommand.setCmdArgu(VarIndex.values()[progIdxBox.getSelectedIndex()].ordinal());
			} else if (e.getSource().equals(valueTypeBox) && valueTypeBox.getSelectedIndex()!=-1) {
				malCommand.setValueType(ValueType.values()[valueTypeBox.getSelectedIndex()]);
				malCommand.setCmdArgu(0);
			} else if (e.getSource().equals(valueTypeLoadBox) && valueTypeLoadBox.getSelectedIndex()!=-1) {
				malCommand.setValueType(ValueType.values()[valueTypeLoadBox.getSelectedIndex()]);
				malCommand.setCmdArgu(0);
			} else if (e.getSource().equals(valueCommandBox) && valueCommandBox.getSelectedIndex()!=-1) {
				malCommand.setCmdArgu(((CommandName)valueCommandBox.getSelectedItem()).getMapIndex());
			} else if (e.getSource().equals(extTypeComboBox) && extTypeComboBox.getSelectedIndex()!=-1) {
				malCommand.setExtType(ExtType.values()[extTypeComboBox.getSelectedIndex()]);
			} else if (e.getSource().equals(extOpComboBox) && extOpComboBox.getSelectedIndex()!=-1) {
				switch (malCommand.getExtType()) {
				case VOP:
					malCommand.setExtOp(ExtOpVar.values()[extOpComboBox.getSelectedIndex()].ordinal());
					break;
				case IF:
					malCommand.setExtOp(ExtOpIf.values()[extOpComboBox.getSelectedIndex()].ordinal());
					break;
				default:
					break;
				}
			}
			malCommand.compile();
			configComponent(malCommand);
			doLayout();
			repaint();
		}
		
		private void configComponent(MalCommand cmd) {
			actions = false;
			malCommand = cmd;
			removeAll();
			malCommandDataLabel.setText(cmd.toString()+" ["+cmd.toStringHexOpcodes()+"]");
			add(malCommandLabel);
			add(malCommandDataLabel);
			cmdTypeBox.setSelectedItem(cmd.getCmdType());
			add(cmdTypeBoxLabel);
			add(cmdTypeBox);
			switch (cmd.getCmdType()) {
			case LOAD:
				switch (cmd.getValueType()) {
				case RAW_VALUE:
					varIdxBox.setSelectedItem(cmd.getVarIndex());
					add(varIdxBoxLabel);
					add(varIdxBox);
					valueRawTextField.setText(""+cmd.getCmdArgu());
					add(valueRawTextFieldLabel);
					add(valueRawTextField);
					break;
				case PROG_VALUE:
					varIdxBox.setSelectedItem(cmd.getVarIndex());
					add(varIdxBoxLabel);
					add(varIdxBox);
					progIdxBox.setSelectedIndex(cmd.getCmdArgu());
					add(progIdxBoxLabel);
					add(progIdxBox);
					break;
				case PF_VALUE:
					varIdxBox.setSelectedItem(cmd.getVarIndex());
					add(varIdxBoxLabel);
					add(varIdxBox);
					valueCommandBox.setSelectedItem(CommandName.valueOfMapIndex(cmd.getCmdArgu()));
					add(valueCommandBoxLabel);
					add(valueCommandBox);
					break;
				case PF_VALUE_SET:
					valueCommandBox.setSelectedItem(CommandName.valueOfMapIndex(cmd.getCmdArgu()));
					add(valueCommandBoxLabel);
					add(valueCommandBox);
					varIdxBox.setSelectedItem(cmd.getVarIndex());
					add(varIdxBoxLabel);
					add(varIdxBox);
					break;
				}
				valueTypeBox.setSelectedItem(cmd.getValueType());
				add(valueTypeBoxLabel);
				add(valueTypeLoadBox);
				break;
			case EXTENDED:
				switch (cmd.getExtType()) {
				case VOP:
					extTypeComboBox.setSelectedItem(MalCommand.ExtType.VOP);
					add(extTypeComboBoxLabel);
					add(extTypeComboBox);
					varIdxBox.setSelectedItem(cmd.getVarIndex());
					add(varIdxBoxLabel);
					add(varIdxBox);
					extOpComboBox.removeAllItems();
					for (ExtOpVar op:ExtOpVar.values()) {
						extOpComboBox.addItem(op.getCharCode());
					}
					extOpComboBox.setSelectedIndex(cmd.getExtOp());
					add(extOpComboBoxLabel);
					add(extOpComboBox);
					switch (cmd.getValueType()) {
					case RAW_VALUE:
						valueRawTextField.setText(""+cmd.getCmdArgu());
						add(valueRawTextFieldLabel);
						add(valueRawTextField);
						break;
					case PROG_VALUE:
						progIdxBox.setSelectedItem(cmd.getVarIndex());
						add(progIdxBoxLabel);
						add(progIdxBox);
						break;
					case PF_VALUE:
						valueCommandBox.setSelectedItem(CommandName.valueOfMapIndex(cmd.getCmdArgu()));
						add(valueCommandBoxLabel);
						add(valueCommandBox);
						break;
					case PF_VALUE_SET:
						varIdxBox.setSelectedItem(cmd.getVarIndex());
						add(varIdxBoxLabel);
						add(varIdxBox);
						break;
					}
					add(valueTypeBoxLabel);
					add(valueTypeBox);
					break;
				case STOP:
					extTypeComboBox.setSelectedItem(MalCommand.ExtType.STOP);
					add(extTypeComboBoxLabel);
					add(extTypeComboBox);
					break;
				case GOTO:
					extTypeComboBox.setSelectedItem(MalCommand.ExtType.GOTO);
					add(extTypeComboBoxLabel);
					add(extTypeComboBox);
					gotoLineComboBox.removeAllItems();
					for (int i=0;i<programLines.size();i++) {
						gotoLineComboBox.addItem(""+((i+1)*10));
					}
					add(gotoLineComboBoxLabel);
					add(gotoLineComboBox);
					break;
				case IF:
					extTypeComboBox.setSelectedItem(MalCommand.ExtType.IF);
					add(extTypeComboBoxLabel);
					add(extTypeComboBox);
					varIdxBox.setSelectedItem(cmd.getVarIndex());
					add(varIdxBoxLabel);
					add(varIdxBox);
					extOpComboBox.removeAllItems();
					for (ExtOpIf op:ExtOpIf.values()) {
						extOpComboBox.addItem(op.getCharCode());
					}
					extOpComboBox.setSelectedIndex(cmd.getExtOp());
					add(extOpComboBoxLabel);
					add(extOpComboBox);
					switch (cmd.getValueType()) {
					case RAW_VALUE:
						valueRawTextField.setText(""+cmd.getCmdArgu());
						add(valueRawTextFieldLabel);
						add(valueRawTextField);
						break;
					case PROG_VALUE:
						progIdxBox.setSelectedItem(cmd.getVarIndex());
						add(progIdxBoxLabel);
						add(progIdxBox);
						break;
					case PF_VALUE:
						valueCommandBox.setSelectedItem(CommandName.valueOfMapIndex(cmd.getCmdArgu()));
						add(valueCommandBoxLabel);
						add(valueCommandBox);
						break;
					case PF_VALUE_SET:
						varIdxBox.setSelectedItem(cmd.getVarIndex());
						add(varIdxBoxLabel);
						add(varIdxBox);
						break;
					}
					valueTypeBox.setSelectedItem(cmd.getValueType());
					add(valueTypeBoxLabel);
					add(valueTypeBox);
					break;
				case ENDIF:
					extTypeComboBox.setSelectedItem(MalCommand.ExtType.ENDIF);
					add(extTypeComboBoxLabel);
					add(extTypeComboBox);
					break;
				}
				break;
			case RESERVED:
				//prefixLabel.setText("RESERVED");
				//add(prefixLabel);
				break;
			case LAST_CMD:
				//prefixLabel.setText("END");
				//add(prefixLabel);
				break;
			}
			actions = true;
			SpringLayoutGrid.makeCompactGrid(this,this.getComponentCount()/2,2);
			SwingUtilities.updateComponentTreeUI(this);
		}
	}
}

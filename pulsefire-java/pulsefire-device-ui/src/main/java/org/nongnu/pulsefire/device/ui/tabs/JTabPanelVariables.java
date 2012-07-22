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

package org.nongnu.pulsefire.device.ui.tabs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import org.nongnu.pulsefire.device.DeviceCommandListener;
import org.nongnu.pulsefire.device.DeviceConnectListener;
import org.nongnu.pulsefire.device.DeviceData;
import org.nongnu.pulsefire.device.ui.JComponentFactory;
import org.nongnu.pulsefire.device.ui.PulseFireUI;
import org.nongnu.pulsefire.device.ui.SpringLayoutGrid;
import org.nongnu.pulsefire.wire.Command;
import org.nongnu.pulsefire.wire.CommandName;
import org.nongnu.pulsefire.wire.CommandVariableType;
import org.nongnu.pulsefire.wire.WireChipFlags;

/**
 * JTabPanelVariables
 * 
 * @author Willem Cazander
 */
public class JTabPanelVariables extends AbstractFireTabPanel {

	private static final long serialVersionUID = -4134436278702264489L;

	private boolean filterIndexed = true;
	private String filterType = null;
	private int filterData = 0;
	private List<DeviceConfigVariableTableModel> models = null;
	
	public JTabPanelVariables() {
		models = new ArrayList<DeviceConfigVariableTableModel>(4);
		setLayout(new FlowLayout(FlowLayout.LEFT));
		setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
		JPanel topSplit = new JPanel();
		topSplit.setLayout(new BorderLayout());
		JPanel wrap = new JPanel();
		wrap.setLayout(new SpringLayout());
		wrap.add(createVars("Conf",CommandVariableType.CONF));
		wrap.add(createVars("Data",CommandVariableType.DATA));
		wrap.add(createVars("Prog/Chip/Freq",CommandVariableType.PROG));
		SpringLayoutGrid.makeCompactGrid(wrap,1,3,0,6,6,6);
		
		topSplit.add(createTopPanelFilter(),BorderLayout.PAGE_START);
		topSplit.add(wrap,BorderLayout.CENTER);
		add(topSplit);
	}
	
	private void fireUpdateModels() {
		for (DeviceConfigVariableTableModel m:models) {
			m.fireTableDataChanged();
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SwingUtilities.updateComponentTreeUI(getParentScrollPane());
			}
		});
	}
	
	private JPanel createTopPanelFilter() {
		JPanel filterPanel = JComponentFactory.createJFirePanel("Filter");
		filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		filterPanel.add(new JLabel("Filter"));
		JComboBox filterBox = new JComboBox(WireChipFlags.values());
		filterBox.addItem("PULSE");
		filterBox.addItem("CHIP");
		filterBox.addItem("FREQ");
		filterBox.addItem("SYS");
		filterBox.addItem("ALL");
		filterBox.setSelectedIndex(filterBox.getItemCount()-1);
		filterBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				filterType = ((JComboBox)e.getSource()).getSelectedItem().toString();
				fireUpdateModels();
			}
		});
		filterPanel.add(filterBox);
		filterPanel.add(new JLabel("Indexed"));
		JCheckBox filterIndexedCheckBox = new JCheckBox();
		filterIndexedCheckBox.setSelected(true);
		filterIndexedCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				filterIndexed = ((JCheckBox)e.getSource()).isSelected();
				fireUpdateModels();
			}
		});
		filterPanel.add(filterIndexedCheckBox);
		filterPanel.add(new JLabel("Data"));
		JComboBox filterDataBox = new JComboBox(new String[] {"DATA","MAP_IDX","IDX_A","IDX_B","MAX"});
		filterDataBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				filterData = ((JComboBox)e.getSource()).getSelectedIndex();
				fireUpdateModels();
			}
		});
		filterPanel.add(filterDataBox);
		return filterPanel;
	}
	
	private JPanel createVars(String name, CommandVariableType type) {
		JPanel confPanel = JComponentFactory.createJFirePanel(name);
		confPanel.setLayout(new GridLayout(1,1));
		add(confPanel);
		DeviceConfigVariableTableModel tableConfModel = new DeviceConfigVariableTableModel(type);
		JTable tableConf = new JTable(tableConfModel);
		models.add(tableConfModel);
		
		TableColumn nameCol = tableConf.getColumnModel().getColumn(0);
		nameCol.setPreferredWidth(150);
		TableColumn valueCol = tableConf.getColumnModel().getColumn(1);
		if (CommandVariableType.CONF==type | CommandVariableType.DATA==type) {
			valueCol.setPreferredWidth(120);
		} else {
			valueCol.setPreferredWidth(180);
		}
		
		tableConf.getTableHeader().setReorderingAllowed(false);
		JPanel tableConfPanel = new JPanel();
		tableConfPanel.setLayout(new BorderLayout());
		tableConfPanel.add(tableConf.getTableHeader(), BorderLayout.PAGE_START);
		tableConfPanel.add(tableConf, BorderLayout.CENTER);
		confPanel.add(tableConfPanel);
		return confPanel;
	}
	
	@Override
	public Class<?> getTabClassName() {
		return this.getClass();
	}
	
	public class DeviceConfigVariableTableModel extends AbstractTableModel implements DeviceConnectListener,DeviceCommandListener {
		
		private static final long serialVersionUID = 6439448246187594793L;
		private String[] columnNames = new String[] {"Name","Value"};
		private DeviceData deviceData = null;
		private CommandVariableType type = null;
		
		public DeviceConfigVariableTableModel(CommandVariableType type) {
			this.type=type;
			deviceData = PulseFireUI.getInstance().getDeviceManager().getDeviceData();
			PulseFireUI.getInstance().getDeviceManager().addDeviceConnectListener(this);
			PulseFireUI.getInstance().getDeviceManager().addDeviceCommandListener(CommandName.info_data, this);
		}
		@Override
		public void deviceConnect() {
			fireTableDataChanged();
		}
		@Override
		public void deviceDisconnect() {
			fireTableDataChanged();
		}
		public String getColumnName(int col) {
			return columnNames[col];
		}
		public int getRowCount() {
			return getFilteredCommandMap(type).size();
		}
		
		private Map<String, Command> getFilteredCommandMap(CommandVariableType type) {
			Map<String, Command> result = new HashMap<String, Command>(33);
			Map<CommandName, Command> cmdMap = new HashMap<CommandName, Command>(33);
			cmdMap.putAll(deviceData.getTypeMap(type)); // clone for concurrency
			if (type.equals(CommandVariableType.PROG)) {
				Map<CommandName, Command> chipMap = deviceData.getTypeMap(CommandVariableType.CHIP);
				cmdMap.putAll(chipMap);
				Map<CommandName, Command> freqMap = deviceData.getTypeMap(CommandVariableType.FREQ);
				cmdMap.putAll(freqMap);
			}
			for (CommandName name:cmdMap.keySet()) {
				result.put(name.name(), cmdMap.get(name));
			}
			if (filterIndexed) {
				for (CommandName cmd:CommandName.values()) {
					if (cmd.isIndexedA()==false) {
						continue;
					}
					if (type.equals(CommandVariableType.PROG)) {
						if ((cmd.getType().equals(type) | cmd.getType().equals(CommandVariableType.CHIP) | cmd.getType().equals(CommandVariableType.FREQ))==false) {
							continue;
						}
					} else if (cmd.getType().equals(type)==false) {
						continue;
					}
					for (int i=0;i<cmd.getMaxIndexA();i++) {
						String key = cmd.name();
						if (i<10) {
							key = key+"0"+i;
						} else {
							key = key+i;
						}
						Command cmdIdx = deviceData.getDeviceParameterIndexed(cmd,i);
						if (cmdIdx!=null) {
							result.put(key,cmdIdx);
						}
					}
				}
			}
			if (filterType==null) {
				return result;
			}
			if (filterType.isEmpty()) {
				return result;
			}
			if ("ALL".equals(filterType)) {
				return result;
			}
			String ff = filterType.toLowerCase();
			
			Map<String, Command> resultFiltered = new HashMap<String, Command>(33);
			for (String name:result.keySet()) {
				if (name.startsWith(ff)) {
					resultFiltered.put(name, result.get(name));
				}
			}
			return resultFiltered;
		}
		
		public int getColumnCount() {
			return columnNames.length;
		}
		public Object getValueAt(int row, int col) {
			Map<String, Command> cmdMap = getFilteredCommandMap(type);
			List<String> keys = new ArrayList<String>(cmdMap.size());
			keys.addAll(cmdMap.keySet());
			Collections.sort(keys);
			Command cmd = cmdMap.get(keys.get(row));
			if (cmd==null) {
				return "ErrNoCmd";
			}
			if (col==0) {
				return keys.get(row);
			} else {
				if (filterData==0) {
					if (cmd.getCommandName().isIndexedA() && cmd.getCommandName().isIndexedB()) {
						StringBuffer buf = new StringBuffer(50);
						if (cmd.getArgu1()!=null) { buf.append(cmd.getArgu1());buf.append(' '); }
						if (cmd.getArgu2()!=null) { buf.append(cmd.getArgu2());buf.append(' '); }
						if (cmd.getArgu3()!=null) { buf.append(cmd.getArgu3());buf.append(' '); }
						if (cmd.getArgu4()!=null) { buf.append(cmd.getArgu4());buf.append(' '); }
						if (cmd.getArgu5()!=null) { buf.append(cmd.getArgu5());buf.append(' '); }
						if (cmd.getArgu6()!=null) { buf.append(cmd.getArgu6());buf.append(' '); }
						if (cmd.getArgu7()!=null) { buf.append(cmd.getArgu7()); }
						return buf.toString();
					} else if (cmd.getCommandName().isIndexedA()) {
						return cmd.getArgu0();
					} else {
						return cmd.getArgu0();
					}
				} else if (filterData==1) {
					return cmd.getCommandName().getMapIndex();
				} else if (filterData==2) {
					return cmd.getCommandName().getMaxIndexA();
				} else if (filterData==3) {
					return cmd.getCommandName().getMaxIndexB();
				} else if (filterData==4) {
					return cmd.getCommandName().getMaxValue();
				} else {
					return "";
				}
			}
		}
		public boolean isCellEditable(int row, int col) {
			return false;
		}
		public void setValueAt(Object value, int row, int col) {
		}
		@Override
		public void commandReceived(Command command) {
			fireTableDataChanged();
		}
	}
}

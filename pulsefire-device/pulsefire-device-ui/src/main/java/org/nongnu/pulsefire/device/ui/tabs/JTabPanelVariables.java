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

import org.nongnu.pulsefire.device.io.protocol.Command;
import org.nongnu.pulsefire.device.io.protocol.CommandName;
import org.nongnu.pulsefire.device.io.protocol.CommandVariableType;
import org.nongnu.pulsefire.device.io.protocol.WireChipFlags;
import org.nongnu.pulsefire.device.io.transport.DeviceCommandListener;
import org.nongnu.pulsefire.device.io.transport.DeviceConnectListener;
import org.nongnu.pulsefire.device.io.transport.DeviceData;
import org.nongnu.pulsefire.device.ui.JComponentFactory;
import org.nongnu.pulsefire.device.ui.PulseFireUI;
import org.nongnu.pulsefire.device.ui.SpringLayoutGrid;

/**
 * JTabPanelVariables
 * 
 * @author Willem Cazander
 */
public class JTabPanelVariables extends AbstractFireTabPanel {

	private boolean filterIndexed = true;
	private String filterType = null;
	private int filterData = 0;
	private List<DeviceConfigVariableTableModel> models = null;
	
	public JTabPanelVariables() {
		models = new ArrayList<DeviceConfigVariableTableModel>(4);
		JPanel topSplit = new JPanel();
		topSplit.setLayout(new BorderLayout());
		JPanel wrap = new JPanel();
		wrap.setLayout(new SpringLayout());
		wrap.add(createVars("Conf",CommandVariableType.CONF));
		wrap.add(createVars("Data",CommandVariableType.DATA));
		wrap.add(createVars("Info",CommandVariableType.INFO));
		SpringLayoutGrid.makeCompactGrid(wrap,1,3,0,6,6,6);
		
		topSplit.add(createTopPanelFilter(),BorderLayout.PAGE_START);
		topSplit.add(wrap,BorderLayout.CENTER);
		getJPanel().add(topSplit);
		getJPanel().setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
	}
	
	private void fireUpdateModels() {
		for (DeviceConfigVariableTableModel m:models) {
			m.fireTableDataChanged();
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SwingUtilities.updateComponentTreeUI(getJScrollPane());
			}
		});
	}
	
	private JPanel createTopPanelFilter() {
		JPanel filterPanel = JComponentFactory.createJFirePanel("Filter");
		filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		filterPanel.add(new JLabel("Filter"));
		final JComboBox<String> filterBox = new JComboBox<String>();
		for (WireChipFlags flag:WireChipFlags.values()) {
			filterBox.addItem(flag.name());
		}
		filterBox.addItem("DIC");
		filterBox.addItem("DOC");
		filterBox.addItem("DEV");
		filterBox.addItem("PULSE");
		filterBox.addItem("PPM");
		filterBox.addItem("CHIP");
		filterBox.addItem("FREQ");
		filterBox.addItem("SYS");
		filterBox.addItem("ALL");
		filterBox.setSelectedIndex(filterBox.getItemCount()-1);
		filterBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				filterType = filterBox.getSelectedItem().toString();
				fireUpdateModels();
			}
		});
		filterPanel.add(filterBox);
		filterPanel.add(new JLabel("Indexed"));
		final JCheckBox filterIndexedCheckBox = new JCheckBox();
		filterIndexedCheckBox.setSelected(true);
		filterIndexedCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				filterIndexed = filterIndexedCheckBox.isSelected();
				fireUpdateModels();
			}
		});
		filterPanel.add(filterIndexedCheckBox);
		filterPanel.add(new JLabel("Variables"));
		final JComboBox<String> filterDataBox = new JComboBox<String>(new String[] {"VALUES","META_ID","META_IDX","META_MAX"});
		filterDataBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				filterData = filterDataBox.getSelectedIndex();
				filterBox.setEnabled(filterData == 0);
				filterIndexedCheckBox.setEnabled(filterData == 0);
				fireUpdateModels();
			}
		});
		filterPanel.add(filterDataBox);
		return filterPanel;
	}
	
	private JPanel createVars(String name, CommandVariableType type) {
		JPanel confPanel = JComponentFactory.createJFirePanel(name);
		confPanel.setLayout(new GridLayout(1,1));
		
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
			if (type.equals(CommandVariableType.INFO)) {
				Map<CommandName, Command> chipMap = deviceData.getTypeMap(CommandVariableType.CHIP);
				cmdMap.putAll(chipMap);
			}
			for (CommandName name:cmdMap.keySet()) {
				result.put(name.name(), cmdMap.get(name));
			}
			if (filterIndexed && filterData == 0) {
				for (CommandName cmd:CommandName.values()) {
					if (cmd.isIndexedA()==false) {
						continue;
					}
					if (type.equals(CommandVariableType.INFO)) {
						if ((cmd.getType().equals(type) | cmd.getType().equals(CommandVariableType.CHIP))==false) {
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
			// Add index meta var
			if (filterData > 0) {
				for (CommandName cmd:CommandName.values()) {
					if (cmd.isIndexedA()==false) {
						continue;
					}
					if (type.equals(CommandVariableType.INFO)) {
						if ((cmd.getType().equals(type) | cmd.getType().equals(CommandVariableType.CHIP))==false) {
							continue;
						}
					} else if (cmd.getType().equals(type)==false) {
						continue;
					}
					Command cmdIdx = deviceData.getDeviceParameterIndexed(cmd,0);
					if (cmdIdx!=null) {
						result.put(cmd.name(),cmdIdx);
					}
				}
			}
			// rm non-idx
			if (filterData == 2) {
				List<String> keys = new ArrayList<String>();
				keys.addAll(result.keySet());
				for (String key:keys) {
					Command cmd = result.get(key);
					if (!cmd.getCommandName().isIndexedA()) {
						result.remove(key);
					}
				}
			}
			if (filterData > 0) {
				return result;
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
						StringBuilder buf = new StringBuilder(50);
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
					return cmd.getCommandName().getId()+(cmd.getCommandName().getMapIndex()==-1?"":"*");
				} else if (filterData==2) {
					return cmd.getCommandName().getMaxIndexA()+" "+cmd.getCommandName().getMaxIndexB();
				} else if (filterData==3) {
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

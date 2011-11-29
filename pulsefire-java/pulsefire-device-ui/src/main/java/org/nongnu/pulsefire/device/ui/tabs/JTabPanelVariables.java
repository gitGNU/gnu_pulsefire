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
import javax.swing.BoxLayout;
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
import org.nongnu.pulsefire.device.ui.time.EventTimeTrigger;
import org.nongnu.pulsefire.wire.Command;
import org.nongnu.pulsefire.wire.CommandName;
import org.nongnu.pulsefire.wire.CommandVariableType;
import org.nongnu.pulsefire.wire.WireChipFlags;

/**
 * JTabPanelVariables
 * 
 * @author Willem Cazander
 */
public class JTabPanelVariables extends AbstractTabPanel {

	private static final long serialVersionUID = -4134436278702264489L;

	private boolean filterIndexed = false;
	private String filterType = null;
	private List<DeviceConfigVariableTableModel> models = null;
	
	public JTabPanelVariables() {
		models = new ArrayList<DeviceConfigVariableTableModel>(4);
		setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel topSplit = new JPanel();
		topSplit.setLayout(new BorderLayout());
		topSplit.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
		JPanel wrap = new JPanel();
		wrap.setLayout(new SpringLayout());
		wrap.add(createVars("Conf",CommandVariableType.CONF));
		wrap.add(createVars("Data",CommandVariableType.DATA));
		wrap.add(createVars("Prog/Chip/Freq",CommandVariableType.PROG));	
		SpringLayoutGrid.makeCompactGrid(wrap,1,3);
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel,BoxLayout.LINE_AXIS));
		topPanel.add(createTopPanelFilter());
		topPanel.add(createTopPanelPulling());
		topSplit.add(JComponentFactory.createJPanelJWrap(topPanel),BorderLayout.PAGE_START);
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
		JCheckBox filterIndexedCheckBox = new JCheckBox("Indexed");
		filterIndexedCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				filterIndexed = ((JCheckBox)e.getSource()).isSelected();
				fireUpdateModels();
			}
		});
		filterPanel.add(filterIndexedCheckBox);
		return filterPanel;
	}
	
	private JPanel createTopPanelPulling() {
		JPanel pullPanel = JComponentFactory.createJFirePanel("Pulling");
		pullPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		pullPanel.add(new JLabel("Refresh"));
		JComboBox refreshBox = new JComboBox(new Integer[] {10*60*1000,5*60*1000,1*60*1000,30*1000,10*1000,5*1000,1000});
		refreshBox.setSelectedIndex(4);
		refreshBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EventTimeTrigger trig = PulseFireUI.getInstance().getEventTimeManager().getEventTimeTriggerByName("refreshData");
				if (trig!=null) {
					trig.setTimeStep((Integer)((JComboBox)e.getSource()).getSelectedItem());
				}
			}
		});
		pullPanel.add(refreshBox);
		return pullPanel;
	}
	
	private JPanel createVars(String name, CommandVariableType type) {
		JPanel confPanel = JComponentFactory.createJFirePanel(name);
		confPanel.setLayout(new GridLayout(1,1));
		add(confPanel);
		DeviceConfigVariableTableModel tableConfModel = new DeviceConfigVariableTableModel(type);
		JTable tableConf = new JTable(tableConfModel);
		models.add(tableConfModel);
		
		TableColumn nameCol = tableConf.getColumnModel().getColumn(0);
		nameCol.setPreferredWidth(130);
		TableColumn valueCol = tableConf.getColumnModel().getColumn(1);
		valueCol.setPreferredWidth(130);
		
		tableConf.getTableHeader().setReorderingAllowed(false);
		//tableConf.getTableHeader().setResizingAllowed(false);
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
			Map<CommandName, Command> cmdMap = deviceData.getTypeMap(type);
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
				if (cmd.getCommandName().isIndexedA() && cmd.getCommandName().isIndexedB()) {
					return cmd.getLineRaw().substring(3); // removed internal prefix of index b
				} else if (cmd.getCommandName().isIndexedA()) {
					return cmd.getLineRaw().substring(0,cmd.getLineRaw().length()-3); // removed internal postfix of index b 
				} else {
					return cmd.getLineRaw();
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

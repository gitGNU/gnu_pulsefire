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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.ToolTipManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import org.nongnu.pulsefire.device.ui.JComponentFactory;
import org.nongnu.pulsefire.device.ui.SpringLayoutGrid;
import org.nongnu.pulsefire.device.ui.components.JCommandButton;
import org.nongnu.pulsefire.device.ui.components.JCommandCheckBox;
import org.nongnu.pulsefire.device.ui.components.JCommandDial;
import org.nongnu.pulsefire.wire.CommandName;

/**
 * JTabPanelLPM
 * 
 * @author Willem Cazander
 */
public class JTabPanelLPM extends AbstractTabPanel implements ListSelectionListener {

	private static final long serialVersionUID = -6711428986888517858L;
	private JTable table = null;
	private LpmTestStepsTableModel tableModel = null;
	
	public JTabPanelLPM() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel wrap = new JPanel();
		wrap.setLayout(new SpringLayout());
		wrap.add(createLPM());
		wrap.add(createLPMTune());
		SpringLayoutGrid.makeCompactGrid(wrap,1,2);
		add(wrap);
	}
	
	private JPanel createLPM() {
		JPanel inputPanel = JComponentFactory.createJFirePanel("LPM");
		inputPanel.setLayout(new SpringLayout());
				
		inputPanel.add(JComponentFactory.createJLabel("Lpm Start"));
		inputPanel.add(JComponentFactory.createJPanelJWrap(new JCommandDial(CommandName.lpm_start)));
		
		inputPanel.add(JComponentFactory.createJLabel("Lpm Stop"));
		inputPanel.add(JComponentFactory.createJPanelJWrap(new JCommandDial(CommandName.lpm_stop)));

		inputPanel.add(JComponentFactory.createJLabel("Lpm Size"));
		inputPanel.add(JComponentFactory.createJPanelJWrap(new JCommandDial(CommandName.lpm_size)));
		
		inputPanel.add(JComponentFactory.createJLabel("Relay Invert"));
		inputPanel.add(new JCommandCheckBox(CommandName.lpm_relay_inv));
		
		inputPanel.add(JComponentFactory.createJLabel("Start Lpm"));
		inputPanel.add(new JCommandButton("Start",CommandName.req_auto_lpm));
		
		JProgressBar bar = new JProgressBar();
		bar.setIndeterminate(true);
		inputPanel.add(JComponentFactory.createJLabel("Lpm progress"));
		inputPanel.add(bar);
		
		SpringLayoutGrid.makeCompactGrid(inputPanel,6,2);
		return inputPanel;
	}
	
	private JPanel createLPMTune() {
		JPanel panel = JComponentFactory.createJFirePanel("Auto Tune");
		panel.setLayout(new BorderLayout());
		
		tableModel = new LpmTestStepsTableModel();
		table = new JTable(tableModel);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setFillsViewportHeight(true);
		table.setShowHorizontalLines(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(this);
		table.setRowMargin(2);
		table.setRowHeight(26);
		TableColumn nameColumn = table.getColumnModel().getColumn(0);
		nameColumn.setPreferredWidth(150);
		TableColumn speedColumn = table.getColumnModel().getColumn(1);
		speedColumn.setPreferredWidth(130);
		ToolTipManager.sharedInstance().unregisterComponent(table);
		ToolTipManager.sharedInstance().unregisterComponent(table.getTableHeader());

		JScrollPane scroll = new JScrollPane(table);
		panel.add(scroll,BorderLayout.CENTER);
		
		JPanel tableActions = new JPanel();
		tableActions.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton editButton = new JButton("Edit");
		JButton addButton = new JButton("Add");
		JButton delButton = new JButton("Delete");
		tableActions.add(editButton);
		tableActions.add(addButton);
		tableActions.add(delButton);
		panel.add(tableActions,BorderLayout.SOUTH);
		
		JPanel testActions = new JPanel();
		testActions.setLayout(new FlowLayout(FlowLayout.LEFT));
		JButton startButton = new JButton("Start");
		JButton stopButton = new JButton("Stop");
		testActions.add(startButton);
		testActions.add(stopButton);
		panel.add(testActions,BorderLayout.NORTH);
		
		return panel;
	}
	
	@Override
	public Class<?> getTabClassName() {
		return this.getClass();
	}

	public class LpmTestStep {
		private Integer id = null;
		private Integer order = null;
		private CommandName commandName = null;
		private String commandArgument = null;
		/**
		 * @return the id
		 */
		public Integer getId() {
			return id;
		}
		/**
		 * @param id the id to set
		 */
		public void setId(Integer id) {
			this.id = id;
		}
		/**
		 * @return the order
		 */
		public Integer getOrder() {
			return order;
		}
		/**
		 * @param order the order to set
		 */
		public void setOrder(Integer order) {
			this.order = order;
		}
		/**
		 * @return the commandName
		 */
		public CommandName getCommandName() {
			return commandName;
		}
		/**
		 * @param commandName the commandName to set
		 */
		public void setCommandName(CommandName commandName) {
			this.commandName = commandName;
		}
		/**
		 * @return the commandArgument
		 */
		public String getCommandArgument() {
			return commandArgument;
		}
		/**
		 * @param commandArgument the commandArgument to set
		 */
		public void setCommandArgument(String commandArgument) {
			this.commandArgument = commandArgument;
		}
	}
	
	public class LpmTestStepsTableModel extends AbstractTableModel {

		private static final long serialVersionUID = -1432038909521987705L;
		private String[] columnNames = new String[] {"Command","Argument","Order"};
		private List<LpmTestStep> data = null;
		
		public LpmTestStepsTableModel() {
			data = new ArrayList<LpmTestStep>(100);
			
			LpmTestStep s = new LpmTestStep();
			s.setCommandName(CommandName.adc_map);
			s.setCommandArgument("12 123 33 34 4");
			s.setOrder(1);
			s.setId(1);
			
			data.add(s);
		}
		
		public List<LpmTestStep> getData() {
			return data;
		}
		
		
		@Override
		public int getRowCount() {
			return data.size();
		}
		
		@Override
		public int getColumnCount() {
			return columnNames.length;
		}
		
		public String getColumnName(int col) {
			return columnNames[col];
		}
		
		@Override
		public Object getValueAt(int row, int col) {
			LpmTestStep step = data.get(row);
			switch (col) {
			default:
			case 0:		return step.commandName.name();
			case 1:		return step.commandArgument;
			case 2:		return step.order;

			}
		}
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		// TODO Auto-generated method stub
		
	}
}

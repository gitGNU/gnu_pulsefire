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
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.ToolTipManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import org.nongnu.pulsefire.device.ui.JComponentEnableStateListener;
import org.nongnu.pulsefire.device.ui.JComponentFactory;
import org.nongnu.pulsefire.device.ui.PulseFireUI;
import org.nongnu.pulsefire.device.ui.SpringLayoutGrid;
import org.nongnu.pulsefire.device.ui.components.JCommandButton;
import org.nongnu.pulsefire.device.ui.components.JCommandCheckBox;
import org.nongnu.pulsefire.device.ui.components.JCommandDial;
import org.nongnu.pulsefire.device.ui.components.JIntegerTextField;
import org.nongnu.pulsefire.wire.CommandName;

/**
 * JTabPanelLPM
 * 
 * @author Willem Cazander
 */
public class JTabPanelLPM extends AbstractTabPanel implements ListSelectionListener, ActionListener, TableModelListener {

	private static final long serialVersionUID = -6711428986888517858L;
	private JTable tuneStepTable = null;
	private JTable tuneResultTable = null;
	private LpmTuneStepTableModel tuneStepModel = null;
	private LpmTuneResultTableModel tuneResultModel = null;
	private JLabel lpmStepLabel = null;
	private JButton stepEditButton = null;
	private JButton stepAddButton = null;
	private JButton stepDelButton = null;
	private JButton lpmSingleButton = null;
	private JButton lpmTuneStartButton = null;
	private JButton lpmTuneStopButton = null;
	private JButton lpmTuneNextButton = null;
	private JButton resultFieldsButton = null;
	private JButton resultClearButton = null;
	private JButton resultExportButton = null;
	
	public JTabPanelLPM() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel wrap = new JPanel();
		wrap.setLayout(new SpringLayout());
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new SpringLayout());
		leftPanel.add(createLpmConfig());
		leftPanel.add(createLpmTuneConfig());
		leftPanel.add(createLpmTune());
		SpringLayoutGrid.makeCompactGrid(leftPanel,3,1);
		wrap.add(leftPanel);
		
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
		JPanel wrapRightPanel = new JPanel();
		wrapRightPanel.add(createLpmTuneResult());
		rightPanel.add(wrapRightPanel);
		wrap.add(rightPanel);
		
		SpringLayoutGrid.makeCompactGrid(wrap,1,2,0,0,0,0);
		add(wrap);
	}
	
	private JPanel createLpmConfig() {
		JPanel inputPanel = JComponentFactory.createJFirePanel("Lpm Config");
		inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
				
		inputPanel.add(JComponentFactory.createJLabel("Start"));
		inputPanel.add(JComponentFactory.createJPanelJWrap(new JCommandDial(CommandName.lpm_start)));
		
		inputPanel.add(JComponentFactory.createJLabel("Stop"));
		inputPanel.add(JComponentFactory.createJPanelJWrap(new JCommandDial(CommandName.lpm_stop)));

		inputPanel.add(JComponentFactory.createJLabel("Size"));
		inputPanel.add(JComponentFactory.createJPanelJWrap(new JCommandDial(CommandName.lpm_size)));
		
		inputPanel.add(JComponentFactory.createJLabel("Relay Invert"));
		inputPanel.add(new JCommandCheckBox(CommandName.lpm_relay_inv));

		return inputPanel;
	}
	
	private JPanel createLpmTuneConfig() {
		JPanel panel = JComponentFactory.createJFirePanel("Auto Lpm");
		panel.setLayout(new BorderLayout());
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new SpringLayout());
		JPanel butPanel = new JPanel();
		butPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		lpmSingleButton = new JCommandButton("Single",CommandName.req_auto_lpm); 
		lpmTuneStartButton = new JButton("Start");
		lpmTuneStopButton = new JButton("Stop");
		lpmTuneNextButton = new JButton("Next");
		
		butPanel.add(lpmSingleButton);
		butPanel.add(lpmTuneStartButton);
		butPanel.add(lpmTuneStopButton);
		butPanel.add(lpmTuneNextButton);
		
		lpmTuneStartButton.addActionListener(this);
		lpmTuneStopButton.addActionListener(this);
		lpmTuneNextButton.addActionListener(this);
		
		lpmTuneStartButton.setEnabled(false);
		lpmTuneStopButton.setEnabled(false);
		lpmTuneNextButton.setEnabled(false);
		
		topPanel.add(JComponentFactory.createJLabel("Actions"));
		topPanel.add(butPanel);
		
		lpmStepLabel = new JLabel("0/0");
		JPanel stepPanel = new JPanel();
		stepPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		stepPanel.add(lpmStepLabel);
		topPanel.add(JComponentFactory.createJLabel("Steps"));
		topPanel.add(stepPanel);
		
		JPanel barPanel = new JPanel();
		barPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		JProgressBar bar = new JProgressBar();
		barPanel.add(bar);
		
		topPanel.add(JComponentFactory.createJLabel("Progress"));
		topPanel.add(barPanel);
		
		SpringLayoutGrid.makeCompactGrid(topPanel,3,2,6,0,0,0);
		panel.add(topPanel);
		return panel;
	}
	
	private JPanel createLpmTune() {
		JPanel panel = JComponentFactory.createJFirePanel("Tune Steps");
		panel.setLayout(new BorderLayout());
		
		tuneStepModel = new LpmTuneStepTableModel();
		tuneStepModel.addTableModelListener(this);
		tuneStepTable = new JTable(tuneStepModel);
		tuneStepTable.getTableHeader().setReorderingAllowed(false);
		tuneStepTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tuneStepTable.setFillsViewportHeight(true);
		tuneStepTable.setShowHorizontalLines(true);
		tuneStepTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tuneStepTable.getSelectionModel().addListSelectionListener(this);
		tuneStepTable.setRowMargin(2);
		tuneStepTable.setRowHeight(26);
		TableColumn orderColumn = tuneStepTable.getColumnModel().getColumn(0);
		orderColumn.setPreferredWidth(55);
		TableColumn cmdColumn = tuneStepTable.getColumnModel().getColumn(1);
		cmdColumn.setPreferredWidth(160);
		tuneStepTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount()==2) {
					LpmTuneStep step = tuneStepModel.dataGet(tuneStepTable.getSelectedRow());
					JLpmTuneStepDialog dialog = new JLpmTuneStepDialog(PulseFireUI.getInstance().getMainFrame(),step);
					dialog.setVisible(true);
				}
			}
		});
		ToolTipManager.sharedInstance().unregisterComponent(tuneStepTable);
		ToolTipManager.sharedInstance().unregisterComponent(tuneStepTable.getTableHeader());

		JScrollPane scroll = new JScrollPane(tuneStepTable);
		scroll.setPreferredSize(new Dimension(450,160));
		panel.add(scroll,BorderLayout.CENTER);
		
		JPanel tableActions = new JPanel();
		tableActions.setLayout(new FlowLayout(FlowLayout.RIGHT));
		stepAddButton = new JButton("Add");
		stepEditButton = new JButton("Edit");
		stepDelButton = new JButton("Delete");
		
		JComponentEnableStateListener.attach(stepAddButton);
		JComponentEnableStateListener.attach(stepEditButton);
		JComponentEnableStateListener.attach(stepDelButton);
		
		stepAddButton.addActionListener(this);
		stepEditButton.addActionListener(this);
		stepDelButton.addActionListener(this);
		
		tableActions.add(stepAddButton);
		tableActions.add(stepEditButton);
		tableActions.add(stepDelButton);
		panel.add(tableActions,BorderLayout.SOUTH);
		
		return panel;
	}
	
	private JPanel createLpmTuneResult() {
		JPanel panel = JComponentFactory.createJFirePanel("Lpm Results");
		panel.setLayout(new BorderLayout());
		
		tuneResultModel = new LpmTuneResultTableModel();
		tuneResultTable = new JTable(tuneResultModel);
		tuneResultTable.getTableHeader().setReorderingAllowed(false);
		tuneResultTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tuneResultTable.setFillsViewportHeight(true);
		tuneResultTable.setShowHorizontalLines(true);
		tuneResultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//tuneResultTable.getSelectionModel().addListSelectionListener(this);
		tuneResultTable.setRowMargin(2);
		tuneResultTable.setRowHeight(26);
		TableColumn dateColumn = tuneResultTable.getColumnModel().getColumn(0);
		dateColumn.setPreferredWidth(180);
		TableColumn lpmResultColumn = tuneResultTable.getColumnModel().getColumn(1);
		lpmResultColumn.setPreferredWidth(80);
		TableColumn stepDataColumn = tuneResultTable.getColumnModel().getColumn(2);
		stepDataColumn.setPreferredWidth(200);
		TableColumn stepFieldsColumn = tuneResultTable.getColumnModel().getColumn(3);
		stepFieldsColumn.setPreferredWidth(190);
		ToolTipManager.sharedInstance().unregisterComponent(tuneResultTable);
		ToolTipManager.sharedInstance().unregisterComponent(tuneResultTable.getTableHeader());

		JScrollPane scroll = new JScrollPane(tuneResultTable);
		scroll.setPreferredSize(new Dimension(660,450));
		panel.add(scroll,BorderLayout.CENTER);
		
		JPanel tableActions = new JPanel();
		tableActions.setLayout(new FlowLayout(FlowLayout.RIGHT));
		resultFieldsButton = new JButton("Fields");
		resultClearButton = new JButton("Clear");
		resultExportButton = new JButton("Export");
		
		tableActions.add(resultFieldsButton);
		tableActions.add(resultClearButton);
		tableActions.add(resultExportButton);
		panel.add(tableActions,BorderLayout.SOUTH);
		
		return panel;
	}
	
	@Override
	public Class<?> getTabClassName() {
		return this.getClass();
	}

	public class LpmTuneStep {
		private int order = 1;
		private CommandName commandName = null;
		private int valueStart = 0;
		private int valueStop = 10;
		private int valueStep = 1;
		private int valueCurrent = 0;
		
		/**
		 * @return the order
		 */
		public int getOrder() {
			return order;
		}
		/**
		 * @param order the order to set
		 */
		public void setOrder(int order) {
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
		 * @return the valueStart
		 */
		public int getValueStart() {
			return valueStart;
		}
		/**
		 * @param valueStart the valueStart to set
		 */
		public void setValueStart(int valueStart) {
			this.valueStart = valueStart;
		}
		/**
		 * @return the valueStop
		 */
		public int getValueStop() {
			return valueStop;
		}
		/**
		 * @param valueStop the valueStop to set
		 */
		public void setValueStop(int valueStop) {
			this.valueStop = valueStop;
		}
		/**
		 * @return the valueStep
		 */
		public int getValueStep() {
			return valueStep;
		}
		/**
		 * @param valueStep the valueStep to set
		 */
		public void setValueStep(int valueStep) {
			this.valueStep = valueStep;
		}
		/**
		 * @return the valueCurrent
		 */
		public int getValueCurrent() {
			return valueCurrent;
		}
		/**
		 * @param valueCurrent the valueCurrent to set
		 */
		public void setValueCurrent(int valueCurrent) {
			this.valueCurrent = valueCurrent;
		}
	}
	public class LpmTuneResult {
		private Date date = null;
		private String stepData = null;
		private int lpmTime = 0;
		private int lpmResult = 0;
		private String stepFields = null;
		
		/**
		 * @return the date
		 */
		public Date getDate() {
			return date;
		}
		/**
		 * @param date the date to set
		 */
		public void setDate(Date date) {
			this.date = date;
		}
		/**
		 * @return the stepData
		 */
		public String getStepData() {
			return stepData;
		}
		/**
		 * @param stepData the stepData to set
		 */
		public void setStepData(String stepData) {
			this.stepData = stepData;
		}
		/**
		 * @return the lpmTime
		 */
		public int getLpmTime() {
			return lpmTime;
		}
		/**
		 * @param lpmTime the lpmTime to set
		 */
		public void setLpmTime(int lpmTime) {
			this.lpmTime = lpmTime;
		}
		/**
		 * @return the lpmResult
		 */
		public int getLpmResult() {
			return lpmResult;
		}
		/**
		 * @param lpmResult the lpmResult to set
		 */
		public void setLpmResult(int lpmResult) {
			this.lpmResult = lpmResult;
		}
		/**
		 * @return the stepFields
		 */
		public String getStepFields() {
			return stepFields;
		}
		/**
		 * @param stepFields the stepFields to set
		 */
		public void setStepFields(String stepFields) {
			this.stepFields = stepFields;
		}
		
	}
	
	public class LpmTuneStepTableModel extends AbstractTableModel {

		private static final long serialVersionUID = -1432038909521987705L;
		private String[] columnNames = new String[] {"Order","Command","Start","Stop","Step"};
		private List<LpmTuneStep> data = null;
		
		public LpmTuneStepTableModel() {
			data = new ArrayList<LpmTuneStep>(100);
		}
		
		public LpmTuneStep dataGet(int row) {
			if (row < data.size()) {
				return data.get(row);
			}
			return null;
		}
		
		public void dataAdd(LpmTuneStep step) {
			data.add(step);
			fireTableDataChanged();
		}
		
		public void dataRemove(int row) {
			if (row < data.size()) {
				data.remove(row);
				fireTableDataChanged();
			}
		}
		
		public boolean dataContains(LpmTuneStep step) {
			return data.contains(step);
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
			LpmTuneStep step = data.get(row);
			switch (col) {
			default:
			case 0:		return step.getOrder();
			case 1:		return step.getCommandName().name();
			case 2:		return step.getValueStart();
			case 3:		return step.getValueStop();
			case 4:		return step.getValueStep();
			}
		}
	}
	
	public class LpmTuneResultTableModel extends AbstractTableModel {

		private static final long serialVersionUID = -1432038909521987705L;
		private String[] columnNames = new String[] {"Date","LpmResult","StepData","StepFields"};
		private List<LpmTuneResult> data = null;
		
		public LpmTuneResultTableModel() {
			data = new ArrayList<LpmTuneResult>(1000);
		}
		
		public LpmTuneResult dataGet(int row) {
			if (row < data.size()) {
				return data.get(row);
			}
			return null;
		}
		
		public void dataAdd(LpmTuneResult result) {
			data.add(result);
			fireTableDataChanged();
		}
		
		public void dataClear() {
			data.clear();
			fireTableDataChanged();
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
			LpmTuneResult result = data.get(row);
			switch (col) {
			default:
			case 0:		return result.getDate();
			case 1:		return result.getLpmResult();
			case 2:		return result.getStepData();
			case 3:		return result.getStepFields();
			}
		}
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	class JLpmTuneStepDialog extends JDialog implements ActionListener {

		private static final long serialVersionUID = 8511082377154332785L;
		private LpmTuneStep step = null;
		private JButton saveButton = null;
		private JButton cancelButton = null;
		private JIntegerTextField orderField = null;
		private JComboBox stepCommandBox = null;
		private JIntegerTextField startValueField = null;
		private JIntegerTextField stopValueField = null;
		private JIntegerTextField stepValueField = null;
		
		public JLpmTuneStepDialog(Frame parentFrame,LpmTuneStep step) {
			super(parentFrame, true);
			this.step=step;
			
			setTitle("Edit Tune Step");
			setMinimumSize(new Dimension(250,250));
			setPreferredSize(new Dimension(300,250));
			setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent we) {
					clearAndHide();
				}
			});
			JPanel mainPanel = new JPanel();
			mainPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
			mainPanel.setLayout(new BorderLayout());
			mainPanel.add(createPanelCenter(),BorderLayout.CENTER);
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
				step.setOrder(orderField.getValue());
				step.setCommandName((CommandName)stepCommandBox.getSelectedItem());
				step.setValueStart(startValueField.getValue());
				step.setValueStop(stopValueField.getValue());
				step.setValueStep(stepValueField.getValue());
				if (tuneStepModel.dataContains(step)==false) {
					tuneStepModel.dataAdd(step);
				} else {
					tuneStepModel.fireTableDataChanged();
				}
				clearAndHide();
				return;
			} else if (e.getSource()==cancelButton) {
				clearAndHide();
				return;
			}
		}
		
		private JPanel createPanelCenter() {
			JPanel panel = new JPanel();
			panel.setLayout(new SpringLayout());
			
			panel.add(new JLabel("Order"));
			orderField = new JIntegerTextField(step.getOrder(), 6);
			panel.add(orderField);
			
			panel.add(new JLabel("Command"));
			stepCommandBox = new JComboBox(CommandName.valuesMapIndex().toArray());
			panel.add(stepCommandBox);
			
			panel.add(new JLabel("Start"));
			startValueField = new JIntegerTextField(step.getValueStart(), 6);
			panel.add(startValueField);
			
			panel.add(new JLabel("Stop"));
			stopValueField = new JIntegerTextField(step.getValueStop(), 6);
			panel.add(stopValueField);
			
			panel.add(new JLabel("Step"));
			stepValueField = new JIntegerTextField(step.getValueStep(), 6);
			panel.add(stepValueField);
			
			SpringLayoutGrid.makeCompactGrid(panel,5,2);
			return panel;
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

	@Override
	public void actionPerformed(ActionEvent e) {
		if (stepEditButton.equals(e.getSource()) && tuneStepTable.getSelectedRow()!=-1) {
			LpmTuneStep step = tuneStepModel.dataGet(tuneStepTable.getSelectedRow());
			JLpmTuneStepDialog dialog = new JLpmTuneStepDialog(PulseFireUI.getInstance().getMainFrame(),step);
			dialog.setVisible(true);
		} else if (stepAddButton.equals(e.getSource())) {
			LpmTuneStep step = new LpmTuneStep();
			for (int i=0;i<tuneStepModel.getRowCount();i++) {
				LpmTuneStep s = tuneStepModel.dataGet(i);
				if (s.getOrder() > step.getOrder()) {
					step.setOrder(s.getOrder()+10);
				}
			}
			JLpmTuneStepDialog dialog = new JLpmTuneStepDialog(PulseFireUI.getInstance().getMainFrame(),step);
			dialog.setVisible(true);
		} else if (stepDelButton.equals(e.getSource()) && tuneStepTable.getSelectedRow()!=-1) {
			tuneStepModel.dataRemove(tuneStepTable.getSelectedRow());
			tuneStepModel.fireTableDataChanged();
		} else if (lpmSingleButton.equals(e.getSource())) {
			
		} else if (lpmTuneStartButton.equals(e.getSource())) {
			lpmSingleButton.setEnabled(false);
			lpmTuneStartButton.setEnabled(false);
			lpmTuneStopButton.setEnabled(true);
			lpmTuneNextButton.setEnabled(true);
			
		} else if (lpmTuneStopButton.equals(e.getSource())) {
			
		} else if (lpmTuneNextButton.equals(e.getSource())) {
			
		} else if (resultFieldsButton.equals(e.getSource())) {
		
		} else if (resultClearButton.equals(e.getSource())) {
			tuneResultModel.dataClear();
		} else if (resultExportButton.equals(e.getSource())) {
			
		}
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		if (tuneStepModel.getRowCount()>0) {
			lpmTuneStartButton.setEnabled(true);
		} else {
			lpmTuneStartButton.setEnabled(false);
		}
		
		int stepsTotal = 0;
		for (int i=0;i<tuneStepModel.getRowCount();i++) {
			LpmTuneStep s = tuneStepModel.dataGet(i);
			int ss = (s.getValueStop()-s.getValueStart())/s.getValueStep();
			if (stepsTotal!=0) {
				stepsTotal *= ss;
			} else {
				stepsTotal = ss;
			}
		}
		
		lpmStepLabel.setText("0/"+stepsTotal);
	}
}

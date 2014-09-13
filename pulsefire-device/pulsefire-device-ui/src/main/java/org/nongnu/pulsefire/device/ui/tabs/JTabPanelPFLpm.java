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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.ToolTipManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import org.nongnu.pulsefire.device.io.protocol.Command;
import org.nongnu.pulsefire.device.io.protocol.CommandName;
import org.nongnu.pulsefire.device.io.protocol.CommandVariableType;
import org.nongnu.pulsefire.device.io.transport.DeviceCommandListener;
import org.nongnu.pulsefire.device.io.transport.DeviceConnectListener;
import org.nongnu.pulsefire.device.io.transport.DeviceData;
import org.nongnu.pulsefire.device.ui.JComponentEnableStateListener;
import org.nongnu.pulsefire.device.ui.JComponentFactory;
import org.nongnu.pulsefire.device.ui.PulseFireUI;
import org.nongnu.pulsefire.device.ui.PulseFireUISettingKeys;
import org.nongnu.pulsefire.device.ui.PulseFireUISettingListener;
import org.nongnu.pulsefire.device.ui.SpringLayoutGrid;
import org.nongnu.pulsefire.device.ui.components.JCommandSettingListDialog;
import org.nongnu.pulsefire.device.ui.components.JIntegerTextField;
import org.nongnu.pulsefire.device.ui.time.EventTimeTrigger;

/**
 * JTabPanelLPM
 * 
 * @author Willem Cazander
 */
public class JTabPanelPFLpm extends AbstractFireTabPanel implements ActionListener, TableModelListener, DeviceCommandListener, PulseFireUISettingListener, DeviceConnectListener {

	private JTable tuneStepTable = null;
	private JTable tuneResultTable = null;
	private LpmTuneStepTableModel tuneStepModel = null;
	private LpmTuneResultTableModel tuneResultModel = null;
	private JProgressBar progressBar = null;
	private JLabel lpmStepLabel = null;
	private JLabel lpmStateLabel = null;
	private JButton stepEditButton = null;
	private JButton stepAddButton = null;
	private JButton stepDelButton = null;
	private JButton lpmAutoStartButton = null;
	private JButton lpmAutoCancelButton = null;
	private JButton lpmAutoLoopButton = null;
	private JButton lpmTuneStartButton = null;
	private JButton lpmTuneStopButton = null;
	private JButton lpmTuneNextButton = null;
	private JButton resultFieldsButton = null;
	private JButton resultClearButton = null;
	private JButton resultExportButton = null;
	private List<CommandName> stepFields = null;
	private volatile boolean runSingle = false;
	private volatile boolean runLoop = false;
	private volatile boolean runTune = false;
	private volatile int tuneStep = 0;
	private List<LpmCommandStep> tuneCommandSteps = null;
	private volatile LpmState lpm_state = LpmState.LPM_IDLE; 
	private volatile long lpm_start_time = 0;
	private volatile long lpm_total_time = 0;
	private volatile long lpm_result = 0;
	private volatile int lpm_level = 0;
	
	enum LpmState {
		LPM_INIT,
		LPM_IDLE,
		LPM_START,
		LPM_START_WAIT,
		LPM_STOP,
		LPM_RUN,
		LPM_DONE,
		/* LPM_DONE_WAIT, */
		LPM_RECOVER,
		LPM_RECOVER_WAIT
	}
	
	public JTabPanelPFLpm() {
		stepFields = CommandName.decodeCommandList(PulseFireUI.getInstance().getSettingsManager().getSettingString(PulseFireUISettingKeys.LPM_RESULT_FIELDS));
		tuneCommandSteps = new ArrayList<LpmCommandStep>(4000);
		
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
		getJPanel().add(wrap);
		
		PulseFireUI.getInstance().getEventTimeManager().addEventTimeTriggerConnected(new EventTimeTrigger("LpmStateCheck", new LpmStateCheck(), 200));
		PulseFireUI.getInstance().getDeviceManager().addDeviceCommandListener(CommandName.adc_value, this);
		PulseFireUI.getInstance().getDeviceManager().addDeviceConnectListener(this);
		PulseFireUI.getInstance().getSettingsManager().addSettingListener(PulseFireUISettingKeys.LPM_RESULT_FIELDS,this);
	}
	
	private JPanel createLpmConfig() {
		JPanel configPanel = JComponentFactory.createJFirePanel("Lpm Config");
		configPanel.setLayout(new BorderLayout());
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
				
		inputPanel.add(JComponentFactory.createJLabel("Start"));
		inputPanel.add(JComponentFactory.createJPanelJWrap(JComponentFactory.createSettingsJFireDial(PulseFireUISettingKeys.LPM_START, 0, 1024)));
		
		
		inputPanel.add(JComponentFactory.createJLabel("Stop"));
		inputPanel.add(JComponentFactory.createJPanelJWrap(JComponentFactory.createSettingsJFireDial(PulseFireUISettingKeys.LPM_STOP, 0, 1024)));

		inputPanel.add(JComponentFactory.createJLabel("Size"));
		inputPanel.add(JComponentFactory.createJPanelJWrap(JComponentFactory.createSettingsJFireDial(PulseFireUISettingKeys.LPM_SIZE, 0, 1024)));
		
		inputPanel.add(JComponentFactory.createJLabel("Connections"));
		JPanel connPanel = new JPanel();
		connPanel.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
		connPanel.add(JComponentFactory.createJLabel("Level adc port"));
		connPanel.add(JComponentFactory.createSettingsJComboBox(PulseFireUISettingKeys.LPM_LEVEL_ADC,new String[] {"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15"}));
		connPanel.add(JComponentFactory.createJLabel("Relay doc port"));
		connPanel.add(JComponentFactory.createSettingsJComboBox(PulseFireUISettingKeys.LPM_RELAY_DOC,new String[] {"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15"}));

		configPanel.add(inputPanel,BorderLayout.CENTER);
		configPanel.add(connPanel,BorderLayout.SOUTH);
		
		return configPanel;
	}
	
	private JPanel createLpmTuneConfig() {
		JPanel panel = JComponentFactory.createJFirePanel("Actions");
		panel.setLayout(new BorderLayout());
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new SpringLayout());
		JPanel butSinglePanel = new JPanel();
		butSinglePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel butTunePanel = new JPanel();
		butTunePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		lpmAutoStartButton = new JButton("Single");
		lpmAutoLoopButton = new JButton("Loop");
		lpmAutoCancelButton = new JButton("Cancel");
		lpmTuneStartButton = new JButton("Start");
		lpmTuneStopButton = new JButton("Stop");
		lpmTuneNextButton = new JButton("Next");
		
		butSinglePanel.add(lpmAutoStartButton);
		butSinglePanel.add(lpmAutoLoopButton);
		butSinglePanel.add(lpmAutoCancelButton);
		butTunePanel.add(lpmTuneStartButton);
		butTunePanel.add(lpmTuneStopButton);
		butTunePanel.add(lpmTuneNextButton);
		
		lpmAutoStartButton.addActionListener(this);
		lpmAutoLoopButton.addActionListener(this);
		lpmAutoCancelButton.addActionListener(this);
		lpmTuneStartButton.addActionListener(this);
		lpmTuneStopButton.addActionListener(this);
		lpmTuneNextButton.addActionListener(this);
		
		lpmAutoStartButton.setEnabled(false);
		lpmAutoLoopButton.setEnabled(false);
		lpmAutoCancelButton.setEnabled(false);
		lpmTuneStartButton.setEnabled(false);
		lpmTuneStopButton.setEnabled(false);
		lpmTuneNextButton.setEnabled(false);
		
		JPanel barPanel = new JPanel();
		barPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		progressBar = new JProgressBar();
		barPanel.add(progressBar);
		barPanel.add(JComponentFactory.createJLabel("Steps"));
		lpmStepLabel = new JLabel("0/0");
		barPanel.add(lpmStepLabel);
		lpmStateLabel = new JLabel("IDLE");
		barPanel.add(lpmStateLabel);
		
		topPanel.add(JComponentFactory.createJLabel("Auto Lpm"));
		topPanel.add(butSinglePanel);
		
		topPanel.add(JComponentFactory.createJLabel("Auto Tune"));
		topPanel.add(butTunePanel);
		
		topPanel.add(JComponentFactory.createJLabel("Progress"));
		topPanel.add(barPanel);
		
		SpringLayoutGrid.makeCompactGrid(topPanel,3,2,6,0,0,0);
		panel.add(topPanel);
		return panel;
	}
	
	private JPanel createLpmTune() {
		JPanel panel = JComponentFactory.createJFirePanel("Auto Tune");
		panel.setLayout(new BorderLayout());
		
		tuneStepModel = new LpmTuneStepTableModel();
		tuneStepModel.addTableModelListener(this);
		tuneStepTable = new JTable(tuneStepModel);
		tuneStepTable.getTableHeader().setReorderingAllowed(false);
		tuneStepTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tuneStepTable.setFillsViewportHeight(true);
		tuneStepTable.setShowHorizontalLines(true);
		tuneStepTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tuneStepTable.setRowMargin(2);
		tuneStepTable.setRowHeight(26);
		tuneStepTable.getColumnModel().getColumn(0).setPreferredWidth(55);
		tuneStepTable.getColumnModel().getColumn(1).setPreferredWidth(160);
		tuneStepTable.getColumnModel().getColumn(2).setPreferredWidth(55);
		tuneStepTable.getColumnModel().getColumn(3).setPreferredWidth(55);
		tuneStepTable.getColumnModel().getColumn(4).setPreferredWidth(55);
		tuneStepTable.getColumnModel().getColumn(5).setPreferredWidth(55);
		
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
		scroll.setPreferredSize(new Dimension(450,120));
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
		tuneResultModel.addTableModelListener(this);
		tuneResultTable = new JTable(tuneResultModel);
		tuneResultTable.getTableHeader().setReorderingAllowed(false);
		tuneResultTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tuneResultTable.setFillsViewportHeight(true);
		tuneResultTable.setShowHorizontalLines(true);
		tuneResultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tuneResultTable.setRowMargin(2);
		tuneResultTable.setRowHeight(26);
		tuneResultTable.getColumnModel().getColumn(0).setPreferredWidth(220);
		tuneResultTable.getColumnModel().getColumn(1).setPreferredWidth(80);
		tuneResultTable.getColumnModel().getColumn(2).setPreferredWidth(80);
		tuneResultTable.getColumnModel().getColumn(3).setPreferredWidth(80);

		ToolTipManager.sharedInstance().unregisterComponent(tuneResultTable);
		ToolTipManager.sharedInstance().unregisterComponent(tuneResultTable.getTableHeader());

		JScrollPane scroll = new JScrollPane(tuneResultTable);
		scroll.setPreferredSize(new Dimension(700,500));
		panel.add(scroll,BorderLayout.CENTER);
		
		JPanel tableActions = new JPanel();
		tableActions.setLayout(new FlowLayout(FlowLayout.RIGHT));
		resultFieldsButton = new JButton("Fields");
		resultClearButton = new JButton("Clear");
		resultExportButton = new JButton("Export");
		
		resultFieldsButton.addActionListener(this);
		resultClearButton.addActionListener(this);
		resultExportButton.addActionListener(this);
		
		resultClearButton.setEnabled(false);
		resultExportButton.setEnabled(false);
		
		tableActions.add(resultFieldsButton);
		tableActions.add(resultClearButton);
		tableActions.add(resultExportButton);
		panel.add(tableActions,BorderLayout.SOUTH);
		
		return panel;
	}
	
	public class LpmTuneStep {
		private int order = 1;
		private CommandName commandName = null;
		private int commandIndex = 0;
		private int valueStart = 0;
		private int valueStop = 10;
		private int valueStep = 1;
		private int valueCurrent = 0;
		private int recoveryTime = 10;
		
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
		 * @return the commandIndex
		 */
		public int getCommandIndex() {
			return commandIndex;
		}
		/**
		 * @param commandIndex the commandIndex to set
		 */
		public void setCommandIndex(int commandIndex) {
			this.commandIndex = commandIndex;
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
		/**
		 * @return the recoveryTime
		 */
		public int getRecoveryTime() {
			return recoveryTime;
		}
		/**
		 * @param recoveryTime the recoveryTime to set
		 */
		public void setRecoveryTime(int recoveryTime) {
			this.recoveryTime = recoveryTime;
		}
		
	}
	public class LpmTuneResult {
		private Date date = null;
		private String lpmTime = null;
		private String lpmResult = null;
		private String mmwResult = null;
		private List<String> stepData = new ArrayList<String>(10);
		private List<String> stepFields = new ArrayList<String>(10);
		
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
		 * @return the lpmTime
		 */
		public String getLpmTime() {
			return lpmTime;
		}
		/**
		 * @param lpmTime the lpmTime to set
		 */
		public void setLpmTime(String lpmTime) {
			this.lpmTime = lpmTime;
		}
		/**
		 * @return the lpmResult
		 */
		public String getLpmResult() {
			return lpmResult;
		}
		/**
		 * @param lpmResult the lpmResult to set
		 */
		public void setLpmResult(String lpmResult) {
			this.lpmResult = lpmResult;
		}
		/**
		 * @return the mmwResult
		 */
		public String getMmwResult() {
			return mmwResult;
		}
		/**
		 * @param mmwResult the mmwResult to set
		 */
		public void setMmwResult(String mmwResult) {
			this.mmwResult = mmwResult;
		}
		/**
		 * @return the stepData
		 */
		public List<String> getStepData() {
			return stepData;
		}
		/**
		 * @return the stepFields
		 */
		public List<String> getStepFields() {
			return stepFields;
		}
	}
	
	public class LpmTuneStepTableModel extends AbstractTableModel {

		private static final long serialVersionUID = -1432038909521987705L;
		private String[] columnNames = new String[] {"Order","Command","Start","Stop","Step","RTime"};
		private List<LpmTuneStep> data = null;
		
		public LpmTuneStepTableModel() {
			data = new ArrayList<LpmTuneStep>(100);
		}
		
		public void fireDataReorder() {
			Collections.sort(data, new Comparator<LpmTuneStep>() {
				@Override
				public int compare(LpmTuneStep o1, LpmTuneStep o2) {
					return new Integer(o1.getOrder()).compareTo(o2.getOrder());
				}
			});
			fireTableDataChanged();
		}
		
		public LpmTuneStep dataGet(int row) {
			if (row < data.size()) {
				return data.get(row);
			}
			return null;
		}
		
		public void dataAdd(LpmTuneStep step) {
			data.add(step);
			fireDataReorder();
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
			case 1:
				if (step.getCommandName().isIndexedA()) {
					String idx = ""+step.getCommandIndex();
					if (idx.length()==1) { idx = "0"+idx; }
					return step.getCommandName().name()+idx;
				} else {
					return step.getCommandName().name();
				}
			case 2:		return step.getValueStart();
			case 3:		return step.getValueStop();
			case 4:		return step.getValueStep();
			case 5:		return step.getRecoveryTime();
			}
		}
	}
	
	public class LpmTuneResultTableModel extends AbstractTableModel {

		private static final long serialVersionUID = -1432038909521987705L;
		private String[] columnNames = new String[] {"Date","LpmTime","LpmResult","LpmMmw"};
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
			return columnNames.length + tuneStepModel.getRowCount() + stepFields.size();
		}
		
		public String getColumnName(int col) {
			if (col < columnNames.length) {
				return columnNames[col];
			} else if (col-columnNames.length < tuneStepModel.getRowCount()) {
				return tuneStepModel.dataGet(col-columnNames.length).getCommandName().name();
			} else if (col-columnNames.length-tuneStepModel.getRowCount() < stepFields.size()){
				return stepFields.get(col-columnNames.length-tuneStepModel.getRowCount()).name();
			} else {
				return "error";
			}
		}
		
		@Override
		public Object getValueAt(int row, int col) {
			LpmTuneResult result = data.get(row);
			if (col < columnNames.length) {
				switch (col) {
				default:
				case 0:		return result.getDate();
				case 1:		return result.getLpmTime();
				case 2:		return result.getLpmResult();
				case 3:		return result.getMmwResult();
				}
			} else if (col-columnNames.length < tuneStepModel.getRowCount()) {
				if (result.getStepData().size() > col-columnNames.length) {
					return result.getStepData().get(col-columnNames.length);
				} else {
					return "";
				}
			} else if (col-columnNames.length-tuneStepModel.getRowCount() < stepFields.size()){
				if (result.getStepFields().size() > col-columnNames.length-tuneStepModel.getRowCount()) {
					return result.getStepFields().get(col-columnNames.length-tuneStepModel.getRowCount());
				} else {
					return "";
				}
			} else {
				return "error";
			}
		}
	}
	
	class JLpmTuneStepDialog extends JDialog implements ActionListener {

		private static final long serialVersionUID = 8511082377154332785L;
		private LpmTuneStep step = null;
		private JButton saveButton = null;
		private JButton cancelButton = null;
		private JIntegerTextField orderField = null;
		private JComboBox<CommandName> stepCommandBox = null;
		private JComboBox<Integer> stepCommandIndexBox = null;
		private JIntegerTextField startValueField = null;
		private JIntegerTextField stopValueField = null;
		private JIntegerTextField stepValueField = null;
		private JIntegerTextField recoveryTimeField = null;
		
		public JLpmTuneStepDialog(Frame parentFrame,LpmTuneStep step) {
			super(parentFrame, true);
			this.step=step;
			
			setTitle("Edit Tune Step");
			setMinimumSize(new Dimension(300,350));
			setPreferredSize(new Dimension(350,350));
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
				step.setCommandIndex(stepCommandBox.getSelectedIndex());
				step.setValueStart(startValueField.getValue());
				step.setValueStop(stopValueField.getValue());
				step.setValueStep(stepValueField.getValue());
				step.setRecoveryTime(recoveryTimeField.getValue());
				if (tuneStepModel.dataContains(step)==false) {
					tuneStepModel.dataAdd(step);
				} else {
					tuneStepModel.fireDataReorder();
				}
				clearAndHide();
				return;
			} else if (e.getSource()==cancelButton) {
				clearAndHide();
				return;
			} else  if (stepCommandBox.equals(e.getSource()) && stepCommandBox.getSelectedIndex()!=-1) {
				CommandName cn = (CommandName)stepCommandBox.getSelectedItem();
				if (cn.isIndexedA()) {
					stepCommandIndexBox.removeAllItems();
					for (int i=0;i<cn.getMaxIndexA();i++) {
						stepCommandIndexBox.addItem(i);
					}
					stepCommandIndexBox.setEnabled(true);
				} else {
					stepCommandIndexBox.setEnabled(false);
				}
			}
		}
		
		private JPanel createPanelCenter() {
			JPanel panel = new JPanel();
			panel.setLayout(new SpringLayout());
			
			panel.add(new JLabel("Order"));
			orderField = new JIntegerTextField(step.getOrder(), 6);
			panel.add(orderField);
			
			panel.add(new JLabel("Command"));
			List<CommandName> cmds = CommandName.valuesMapIndex();
			for (int i=0;i<tuneStepModel.getRowCount();i++) {
				LpmTuneStep s = tuneStepModel.dataGet(i);
				if (s.getCommandName().equals(step.getCommandName())==false) {
					cmds.remove(s.getCommandName());
				}
			}
			stepCommandBox = new JComboBox<CommandName>(cmds.toArray(new CommandName[]{}));
			stepCommandBox.setSelectedItem(step.getCommandName());
			stepCommandBox.addActionListener(this);
			panel.add(stepCommandBox);
			
			panel.add(new JLabel("Index"));
			stepCommandIndexBox = new JComboBox<Integer>();
			stepCommandIndexBox.setEnabled(false);
			panel.add(stepCommandIndexBox);
			
			panel.add(new JLabel("Start"));
			startValueField = new JIntegerTextField(step.getValueStart(), 6);
			panel.add(startValueField);
			
			panel.add(new JLabel("Stop"));
			stopValueField = new JIntegerTextField(step.getValueStop(), 6);
			panel.add(stopValueField);
			
			panel.add(new JLabel("Step"));
			stepValueField = new JIntegerTextField(step.getValueStep(), 6);
			panel.add(stepValueField);
			
			panel.add(new JLabel("Recovery Time"));
			recoveryTimeField = new JIntegerTextField(step.getRecoveryTime(), 6);
			panel.add(recoveryTimeField);
			
			SpringLayoutGrid.makeCompactGrid(panel,7,2);
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
	public void deviceConnect() {
		lpmAutoStartButton.setEnabled(true);
		lpmAutoLoopButton.setEnabled(true);
	}
	
	@Override
	public void deviceDisconnect() {
		lpmAutoStartButton.setEnabled(false);
		lpmAutoLoopButton.setEnabled(false);
		lpmAutoCancelButton.setEnabled(false);
		lpmTuneStartButton.setEnabled(false);
		lpmTuneStopButton.setEnabled(false);
		lpmTuneNextButton.setEnabled(false);
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
		} else if (lpmAutoStartButton.equals(e.getSource())) {
			lpmAutoCancelButton.setEnabled(true);
			lpmAutoLoopButton.setEnabled(false);
			lpmAutoStartButton.setEnabled(false);
			lpmTuneStartButton.setEnabled(false);
			stepEditButton.setEnabled(false);
			stepAddButton.setEnabled(false);
			stepDelButton.setEnabled(false);
			runSingle = true;
			requestLpm();
		} else if (lpmAutoLoopButton.equals(e.getSource())) {
			runLoop = true;
			lpmAutoCancelButton.setEnabled(true);
			lpmAutoLoopButton.setEnabled(false);
			lpmAutoStartButton.setEnabled(false);
			lpmTuneStartButton.setEnabled(false);
			requestLpm();
		} else if (lpmAutoCancelButton.equals(e.getSource())) {
			runLoop = false;
			lpmAutoLoopButton.setEnabled(true);
			lpmAutoStartButton.setEnabled(true);
			lpmTuneStartButton.setEnabled(true);
			stepEditButton.setEnabled(true);
			stepAddButton.setEnabled(true);
			stepDelButton.setEnabled(true);
			
		} else if (lpmTuneStartButton.equals(e.getSource())) {
			lpmAutoStartButton.setEnabled(false);
			lpmTuneStartButton.setEnabled(false);
			lpmTuneStopButton.setEnabled(true);
			lpmTuneNextButton.setEnabled(true);
			runTune = true;
			tuneStep = 0;
			calcCommandSteps();
			requestCommandStep();
		} else if (lpmTuneStopButton.equals(e.getSource())) {
			lpmAutoStartButton.setEnabled(true);
			lpmTuneStartButton.setEnabled(true);
			lpmTuneStopButton.setEnabled(false);
			lpmTuneNextButton.setEnabled(false);
			runTune = false;
		} else if (lpmTuneNextButton.equals(e.getSource())) {
			progressBar.setValue(0);
			requestCommandStep();
		} else if (resultFieldsButton.equals(e.getSource())) {
			List<CommandName> commands = new ArrayList<CommandName>(100);
			for (CommandName cn:CommandName.values()) {
				if (CommandVariableType.CMD==cn.getType()) {
					continue;
				}
				if (CommandVariableType.INFO==cn.getType()) {
					continue;
				}
				commands.add(cn);
			}
			JCommandSettingListDialog dialog = new JCommandSettingListDialog(
					PulseFireUI.getInstance().getMainFrame(),
					"Select Lpm Result Fields",
					"Select the fields to log with the lpm result.",
					PulseFireUISettingKeys.LPM_RESULT_FIELDS,
					commands,commands);
			dialog.setVisible(true);
		} else if (resultClearButton.equals(e.getSource())) {
			tuneResultModel.dataClear();
		} else if (resultExportButton.equals(e.getSource())) {
			JFileChooser fc = new JFileChooser();
			fc.setApproveButtonText("Save");
			fc.setSelectedFile(new File("lpm-results.csv"));
			int returnVal = fc.showOpenDialog((JButton)e.getSource());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				writeExport(file);
			}
		}
	}

	private void writeExport(File file) {
		//String FIELD_SPACE = " ";
		String FIELD_QUOTE = "\"";
		String FIELD_SEPERATOR = ",";
		String FIELD_END = System.getProperty("line.separator");
		Writer writer = null;
		try {
			writer = new OutputStreamWriter(new FileOutputStream(file,true),Charset.forName("UTF-8"));
			
			writer.append("#");
			for (int c=0;c<tuneResultModel.getColumnCount();c++) {
				writer.append(tuneResultModel.getColumnName(c));
				if (c<tuneResultModel.getColumnCount()-1) {
					writer.append(FIELD_SEPERATOR);
				}
			}
			writer.append(FIELD_END); // wroter header
			for (int i=0;i<tuneResultModel.getRowCount();i++) {
				for (int c=0;c<tuneResultModel.getColumnCount();c++) {
					writer.append(FIELD_QUOTE);
					writer.append(tuneResultModel.getValueAt(i, c).toString());
					writer.append(FIELD_QUOTE);
					if (c<tuneResultModel.getColumnCount()-1) {
						writer.append(FIELD_SEPERATOR);
					}
				}
				writer.append(FIELD_END);
				writer.flush();
			}
			writer.append(FIELD_END);
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (writer!=null) {
				try {
					writer.close();
				} catch (IOException e) {}
			}
		}
	}
	
	@Override
	public void tableChanged(TableModelEvent e) {
		if (tuneResultModel.equals(e.getSource())) {
			if (tuneResultModel.getRowCount()>0) {
				resultClearButton.setEnabled(true);
				resultExportButton.setEnabled(true);
			} else {
				resultClearButton.setEnabled(false);
				resultExportButton.setEnabled(false);
			}
			return;
		}
		if (tuneStepModel.getRowCount()>0) {
			lpmTuneStartButton.setEnabled(true);
		} else {
			lpmTuneStartButton.setEnabled(false);
		}
		
		updateStepLabel();
		tuneResultModel.fireTableStructureChanged();
	}

	private void updateStepLabel() {
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
		
		lpmStepLabel.setText(tuneStep+"/"+stepsTotal);
	}
	
	private void requestLpm() {
		lpm_state = LpmState.LPM_INIT;
	}
	
	private void requestLpmSetRelay(boolean value) {
		Command cmd = new Command(CommandName.req_doc);
		cmd.setArgu0(PulseFireUI.getInstance().getSettingsManager().getSettingString(PulseFireUISettingKeys.LPM_RELAY_DOC));
		if (value) {
			cmd.setArgu1("0");
		} else {
			cmd.setArgu1("1");
		}
		PulseFireUI.getInstance().getDeviceManager().requestCommand(cmd);
	}
	
	class LpmStateCheck implements Runnable {
		@Override
		public void run() {
			try {
				checkLpmState();
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		}
	}
	
	private void checkLpmState() {
		int lpm_start = PulseFireUI.getInstance().getSettingsManager().getSettingInteger(PulseFireUISettingKeys.LPM_START);
		int lpm_stop = PulseFireUI.getInstance().getSettingsManager().getSettingInteger(PulseFireUISettingKeys.LPM_STOP);
		int lpm_size = PulseFireUI.getInstance().getSettingsManager().getSettingInteger(PulseFireUISettingKeys.LPM_SIZE);
		LpmState lpm_state_old = lpm_state;
		switch (lpm_state) {
			case LPM_INIT:
				if ( lpm_level < lpm_start) {
					lpm_state = LpmState.LPM_RECOVER;
				} else {
					lpm_state = LpmState.LPM_START;
				}
				break;
			case LPM_IDLE:
				return;
			case LPM_START:
				requestLpmSetRelay(true); // close the output tube
				lpm_state = LpmState.LPM_START_WAIT;
				break;
			case LPM_START_WAIT:
				if (lpm_level < lpm_start) {
					lpm_start_time = System.currentTimeMillis();
					lpm_state = LpmState.LPM_RUN;
				}
				break;
			case LPM_STOP:
				lpm_state = LpmState.LPM_RECOVER;
				break;
			case LPM_RUN: {
				lpm_total_time = (System.currentTimeMillis()-lpm_start_time)/10;
				int stepSize = (lpm_start-lpm_stop)/10; // 10 steps
				int stepDone = (lpm_start-lpm_level) / stepSize;
				if (stepDone < 0) {
					stepDone = 0;
				}
				if (stepDone > 13) {
					stepDone = 13;
				}
				if (lpm_total_time/100 == 0) {
					break; // rm div by zero
				}
				// calulate lpm in 100x size.
				// 600 = 1 minute in seconds
				// lpm_totalTime/10 = time in seconds
				// lpm_size is in ML!
				lpm_result = (600/(lpm_total_time/100))*lpm_size/100;

				if (lpm_level < lpm_stop) {
					lpm_state = LpmState.LPM_DONE;
				}
				if (lpm_result == 0) {
					lpm_state = LpmState.LPM_DONE; // timeout of calculations
				}
				//Chip_delay(10);
				break;
			}
			case LPM_DONE:
				requestLpmSetRelay(false); // open output tube
				processLpmDone(); // process info
				lpm_state = LpmState.LPM_RECOVER;
				//lpm_fire = ONE;
				break;
			//case LPM_DONE_WAIT:
			//	// was used for wait for user input now goto recove
			//	lpm_state = LpmState.LPM_RECOVER;
			//	break;
			case LPM_RECOVER:
				requestLpmSetRelay(false); // open output tube
				lpm_state = LpmState.LPM_RECOVER_WAIT;
				break;
			case LPM_RECOVER_WAIT:
				if (lpm_level < lpm_start) {
					break;
				}
				lpm_state = LpmState.LPM_IDLE;
				break;
		}
		if (lpm_state_old != lpm_state) {
			lpmStateLabel.setText(lpm_state.name());
		}
	}
	
	private void processLpmDone() {
		//  result format: req_lpm_fire==19.53 2.67
		LpmTuneResult result = new LpmTuneResult();
		result.setDate(new Date());
		result.setLpmResult(""+lpm_result);
		result.setLpmTime(""+lpm_total_time);
		result.setMmwResult("");
		for (CommandName cn:stepFields) {
			result.getStepFields().add(renderStepField(cn));
		}
		for (int i=0;i<tuneStepModel.getRowCount();i++) {
			if (runTune) {
				LpmCommandStep step = tuneCommandSteps.get(tuneStep);
				if (step.commands.size() > i) {
					Command c = step.commands.get(i);
					result.getStepData().add(c.getArgu0());
				} else {
					result.getStepData().add("");
				}
			} else {
				result.getStepData().add("");
			}
		}
		tuneResultModel.dataAdd(result);
		
		progressBar.setValue(0);
		
		if (runSingle) {
			runSingle = false;
			lpmAutoCancelButton.setEnabled(false);
			lpmAutoLoopButton.setEnabled(true);
			lpmAutoStartButton.setEnabled(true);
			if (tuneStepModel.getRowCount()>0) {
				lpmTuneStartButton.setEnabled(true);
			}
			return;
		}
		if (runLoop) {
			PulseFireUI.getInstance().getEventTimeManager().addRunOnce(new TriggerFire());
			return;
		}
		if (runTune) {
			tuneStep++;
			requestCommandStep();
			return;
		}
	}
	
	@Override
	public void commandReceived(Command command) {
		if (CommandName.adc_value.equals(command.getCommandName())==false) {
			return;
		}
		
		Integer adcPort = PulseFireUI.getInstance().getSettingsManager().getSettingInteger(PulseFireUISettingKeys.LPM_LEVEL_ADC);
		if (adcPort.equals(new Integer(command.getArgu1()))==false) {
			return; // other port
		}
		lpm_level = new Integer(command.getArgu0());
		
		if (lpm_state == LpmState.LPM_RUN) {
			int lpm_start = PulseFireUI.getInstance().getSettingsManager().getSettingInteger(PulseFireUISettingKeys.LPM_START);
			int lpm_stop = PulseFireUI.getInstance().getSettingsManager().getSettingInteger(PulseFireUISettingKeys.LPM_STOP);
			int stepSize = (lpm_start-lpm_stop) / 100;
			int stepProgress = (lpm_start-lpm_level) / stepSize;
			if (stepProgress<0 | stepProgress>100) {
				stepProgress=0;
			}
			progressBar.setValue(stepProgress);
		}
	}

	@Override
	public void settingUpdated(PulseFireUISettingKeys key, String value) {
		stepFields = CommandName.decodeCommandList(value);
		tuneResultModel.fireTableStructureChanged();
	}

	class LpmCommandStep {
		List<Command> commands = new ArrayList<Command>(10);
		long recoveryTime = 0;
	}
	
	private void calcCommandSteps() {
		if (tuneStepModel.getRowCount()==0) {
			return;
		}
		tuneCommandSteps.clear();
		for (int i=0;i<tuneStepModel.getRowCount();i++) {
			LpmTuneStep s = tuneStepModel.dataGet(i);
			s.setValueCurrent(s.getValueStart());
		}
		calcCommandStepsDeep(0);
	}
	
	private void calcCommandStepsDeep(int stepIndex) {
		if (stepIndex >= tuneStepModel.getRowCount()) {
			return;
		}
		LpmTuneStep step = tuneStepModel.dataGet(stepIndex);
		for (int ii=0;ii<(step.getValueStop()-step.getValueStart())/step.getValueStep();ii++) {
			step.setValueCurrent(step.getValueStart()+(ii*step.getValueStep()));
			calcCommandStepsDeep(stepIndex+1);
			if (tuneStepModel.getRowCount() > stepIndex+1) {
				continue; // this makes it work correctly
			}
			LpmCommandStep cmd = new LpmCommandStep();
			long time = 0;
			for (int i=0;i<tuneStepModel.getRowCount();i++) {
				LpmTuneStep s = tuneStepModel.dataGet(i);
				Command c = new Command(s.getCommandName());
				c.setArgu0(""+s.getValueCurrent());
				if (s.getCommandName().isIndexedA()) {
					c.setArgu1(""+s.getCommandIndex());
				}
				cmd.commands.add(c);
				long timeS = s.getRecoveryTime();
				if (timeS==0) { timeS = 1; }
				time += timeS;
			}
			cmd.recoveryTime=time;
			tuneCommandSteps.add(cmd);
		}
	}
	
	private void requestCommandStep() {
		
		if (tuneCommandSteps.isEmpty() || tuneStep >= tuneCommandSteps.size()) {
			// done so reset state
			tuneStep = 0;
			runTune = false;
			progressBar.setValue(0);
			lpmAutoStartButton.setEnabled(true);
			lpmTuneStartButton.setEnabled(true);
			lpmTuneStopButton.setEnabled(false);
			lpmTuneNextButton.setEnabled(false);
			stepEditButton.setEnabled(true);
			stepAddButton.setEnabled(true);
			stepDelButton.setEnabled(true);
			
		} else {
			PulseFireUI.getInstance().getEventTimeManager().addRunOnce(new TriggerFire());
		}
		updateStepLabel();
	}
	
	class TriggerFire implements Runnable {
		@Override
		public void run() {
			try {
				if (runTune && tuneStep < tuneCommandSteps.size()) {
					LpmCommandStep step = tuneCommandSteps.get(tuneStep);
					for (Command cmd:step.commands) {
						PulseFireUI.getInstance().getDeviceManager().requestCommand(cmd).waitForResponseChecked();
					}
					PulseFireUI.getInstance().getEventTimeManager().addRunOnce(new TriggerLpm(),step.recoveryTime*1000);
				} else {
					PulseFireUI.getInstance().getEventTimeManager().addRunOnce(new TriggerLpm(),15000l);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		}
	}
	class TriggerLpm implements Runnable {
		@Override
		public void run() {
			if (runTune | runLoop) {
				requestLpm();
			}
		}
	}
	
	private String renderStepField(CommandName cn) {
		DeviceData devData = PulseFireUI.getInstance().getDeviceData();
		StringBuilder buf = new StringBuilder(200);
		String FIELD_SPACE = " ";
		String FIELD_SEPERATOR = ",";

		if (cn.isIndexedA()) {
			for (int i=0;i<cn.getMaxIndexA();i++) {
				Command cmd = devData.getDeviceParameterIndexed(cn, i);
				if (cmd!=null) {
					if (cn.isIndexedB()) {
						buf.append(cmd.getArgu0());
						if (cmd.getArgu1()!=null) { buf.append(FIELD_SPACE);buf.append(cmd.getArgu1()); }
						if (cmd.getArgu2()!=null) { buf.append(FIELD_SPACE);buf.append(cmd.getArgu2()); }
						if (cmd.getArgu3()!=null) { buf.append(FIELD_SPACE);buf.append(cmd.getArgu3()); }
						if (cmd.getArgu4()!=null) { buf.append(FIELD_SPACE);buf.append(cmd.getArgu4()); }
						if (cmd.getArgu5()!=null) { buf.append(FIELD_SPACE);buf.append(cmd.getArgu5()); }
						if (cmd.getArgu6()!=null) { buf.append(FIELD_SPACE);buf.append(cmd.getArgu6()); }
						if (cmd.getArgu7()!=null) { buf.append(FIELD_SPACE);buf.append(cmd.getArgu7()); }
					} else {
						buf.append(cmd.getArgu0());
					}
				} else {
					buf.append(FIELD_SPACE);
				}
				if (i<cn.getMaxIndexA()-1) {
					buf.append(FIELD_SEPERATOR);
				}
			}
		} else {
			Command cmd = devData.getDeviceParameter(cn);
			if (cmd!=null) {
				buf.append(cmd.getArgu0());
			} else {
				buf.append(FIELD_SPACE);
			}
		}
		return buf.toString();
	}
}

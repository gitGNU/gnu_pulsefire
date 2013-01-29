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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.nongnu.pulsefire.device.flash.FlashControllerConfig;
import org.nongnu.pulsefire.device.flash.FlashHexReader;
import org.nongnu.pulsefire.device.flash.FlashLogListener;
import org.nongnu.pulsefire.device.flash.FlashManager;
import org.nongnu.pulsefire.device.flash.FlashProgramController;
import org.nongnu.pulsefire.device.ui.DevicePortsComboBoxModel;
import org.nongnu.pulsefire.device.ui.JComponentFactory;
import org.nongnu.pulsefire.device.ui.PulseFireUI;
import org.nongnu.pulsefire.device.ui.PulseFireUISettingKeys;
import org.nongnu.pulsefire.device.ui.SpringLayoutGrid;

/**
 * JFlashDialog displays all device firmwares and let user flash them.
 * 
 * @author Willem Cazander
 */
public class JFlashDialog extends JDialog implements ActionListener,ListSelectionListener,FlashLogListener, MouseListener {
	
	private static final long serialVersionUID = -7552916322807635756L;
	private Logger logger = null;
	private FlashControllerConfig flashConfig = null;
	private volatile FlashProgramController flashProgramController = null;
	private JComboBox mcuTypeBox = null;
	private JComboBox mcuSpeedBox = null;
	private JPanel filterItemPanel = null;
	private Map<String,FilterItem> filterItems = new HashMap<String,FilterItem>(10);
	private JComboBox portsComboBox = null;
	private JComboBox progComboBox = null;
	private JCheckBox progVerboseBox = null; 
	private JCheckBox progVerifyBox = null;
	private JProgressBar flashProgressBar = null;
	private JTextArea flashLog = null;
	private DateFormat flashLogTimeFormat = null;
	private JButton closeButton = null;
	private JButton flashButton = null;
	private JButton saveButton = null;
	private DeviceImagesTableModel tableModel = null;
	private JTable table = null;
	private JLabel burnName = null;
	private JLabel burnDeviceId = null;
	private String nativeFlashCmd = null;
	private String nativeFlashConfig = null;
	private String[] columnNames = new String[] {"name","speed",
			"SPI","LCD","PWM","ADC","PTT","STV","VFC","MAL","CIP","PTC0","PTC1","VSC0","VSC1",};
			
	public JFlashDialog(Frame aFrame) {
		super(aFrame, true);
		logger = Logger.getLogger(JFlashDialog.class.getName());
		nativeFlashCmd = PulseFireUI.getInstance().getSettingsManager().getSettingString(PulseFireUISettingKeys.AVRDUDE_CMD);
		nativeFlashConfig = PulseFireUI.getInstance().getSettingsManager().getSettingString(PulseFireUISettingKeys.AVRDUDE_CONFIG);
		setTitle("Flash chip firmware");
		setMinimumSize(new Dimension(640,480));
		setPreferredSize(new Dimension(999,666));
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				clearAndHide();
			}
		});
		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(createPanelTop(),BorderLayout.NORTH);
		mainPanel.add(createPanelCenter(),BorderLayout.CENTER);
		mainPanel.add(createPanelBottom(),BorderLayout.SOUTH);
		getContentPane().add(mainPanel);
		pack();
		setLocationRelativeTo(aFrame);
	}
	
	class FilterItem {
		String name;
		JCheckBox checkBox;
		JLabel label;
	}
	
	private JPanel createPanelTop() {
		JPanel filterHeaderPanel = JComponentFactory.createJFirePanel("Filter options");
		
		JPanel tableOption = new JPanel();
		tableOption.setLayout(new SpringLayout());
		
		JLabel mcuText = new JLabel("mcu:");
		tableOption.add(mcuText);
		mcuTypeBox = JComponentFactory.createSettingsJComboBox(PulseFireUISettingKeys.FLASH_MCU_TYPE, new String[] {"ALL","atmega328p","atmega168p","atmega1280","atmega2560"}); 
		mcuTypeBox.addActionListener(this);
		tableOption.add(mcuTypeBox);

		JLabel speedText = new JLabel("speed:");
		tableOption.add(speedText);
		mcuSpeedBox = JComponentFactory.createSettingsJComboBox(PulseFireUISettingKeys.FLASH_MCU_SPEED,new String[] {"ALL","16Mhz","20Mhz","72Mhz","720Mhz"}); 
		mcuSpeedBox.addActionListener(this);
		tableOption.add(mcuSpeedBox);
		
		SpringLayoutGrid.makeCompactGrid(tableOption,2,2);
		
		filterItemPanel = new JPanel();
		filterItemPanel.setLayout(new SpringLayout());
		
		filterHeaderPanel.add(tableOption);
		filterHeaderPanel.add(filterItemPanel);
		
		JPanel optionWrapPanel = new JPanel();
		optionWrapPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		optionWrapPanel.add(filterHeaderPanel);
		
		return optionWrapPanel;
	}
	
	private JScrollPane createPanelCenter() {
		tableModel = new DeviceImagesTableModel();
		table = new JTable(tableModel);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setFillsViewportHeight(true);
		table.setShowHorizontalLines(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(this);
		table.setRowMargin(2);
		table.setRowHeight(26);
		table.getTableHeader().addMouseListener(this);
		
		TableColumn nameColumn = table.getColumnModel().getColumn(0);
		nameColumn.setPreferredWidth(150);
		TableColumn speedColumn = table.getColumnModel().getColumn(1);
		speedColumn.setPreferredWidth(130);
		ToolTipManager.sharedInstance().unregisterComponent(table);
		ToolTipManager.sharedInstance().unregisterComponent(table.getTableHeader());

		JScrollPane scroll = new JScrollPane(table);
		return scroll;
	}
	
	private JPanel createPanelBottom() {
		JPanel burnPanel = JComponentFactory.createJFirePanel("Burn");
		burnPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		JPanel burnOptionPanel = new JPanel();
		burnOptionPanel.setLayout(new SpringLayout());
		burnOptionPanel.add(new JLabel("Name:"));
		burnName = new JLabel();
		burnOptionPanel.add(burnName);
		burnOptionPanel.add(new JLabel("DeviceId:"));
		burnDeviceId = new JLabel();
		burnOptionPanel.add(burnDeviceId);
		burnOptionPanel.add(new JLabel("Port:"));
		DevicePortsComboBoxModel portModel = new DevicePortsComboBoxModel();
		portsComboBox = new JComboBox(portModel);
		portsComboBox.addPopupMenuListener(portModel);
		burnOptionPanel.add(portsComboBox);
		burnOptionPanel.add(new JLabel("Programer:"));
		if (nativeFlashCmd!=null && nativeFlashCmd.isEmpty()==false && nativeFlashConfig!=null && nativeFlashConfig.isEmpty()==false) {
			progComboBox = new JComboBox(new String[] {"arduino","stk500v2","native-arduino","native-stk500v1","native-stk500v2"});
		} else {
			progComboBox = new JComboBox(new String[] {"arduino","stk500v2"});
		}
		burnOptionPanel.add(progComboBox);
		burnOptionPanel.add(new JLabel("logVerbose:"));
		progVerboseBox = new JCheckBox();
		burnOptionPanel.add(progVerboseBox);
		burnOptionPanel.add(new JLabel("flashVerify:"));
		progVerifyBox = new JCheckBox();
		burnOptionPanel.add(progVerifyBox);
		SpringLayoutGrid.makeCompactGrid(burnOptionPanel,6,2);
		burnPanel.add(burnOptionPanel);
		
		JPanel burnProgressPanel = new JPanel();
		burnProgressPanel.setLayout(new GridLayout(1,1));
		burnProgressPanel.setBorder(BorderFactory.createEmptyBorder(6,0,12,0));
		flashProgressBar = new JProgressBar();
		flashProgressBar.setStringPainted(true);
		burnProgressPanel.add(flashProgressBar);
		
		JPanel logPanel = JComponentFactory.createJFirePanel("Burn Log");
		flashLogTimeFormat = new SimpleDateFormat("HH:mm:ss");
		flashLog = new JTextArea(10,50);
		flashLog.setMargin(new Insets(2, 2, 2, 2));
		flashLog.setAutoscrolls(true);
		flashLog.setEditable(false);
		JScrollPane consoleScrollPane = new JScrollPane(flashLog);
		consoleScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		consoleScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		consoleScrollPane.getViewport().setOpaque(false);
		logPanel.add(consoleScrollPane);
		updateText("Ready to flash.");
		
		JPanel burnActionPanel = new JPanel();
		burnActionPanel.setBorder(BorderFactory.createEmptyBorder());
		burnActionPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		saveButton = new JButton("Export");
		saveButton.addActionListener(this);
		burnActionPanel.add(saveButton);
		flashButton = new JButton("Flash");
		flashButton.addActionListener(this);
		burnActionPanel.add(flashButton);
		closeButton = new JButton("Close");
		closeButton.addActionListener(this);
		burnActionPanel.add(closeButton);
		
		JPanel burnWrapPanel = new JPanel();
		burnWrapPanel.setLayout(new BorderLayout());
		burnWrapPanel.add(burnProgressPanel,BorderLayout.PAGE_START);
		burnWrapPanel.add(burnPanel,BorderLayout.CENTER);
		burnWrapPanel.add(logPanel,BorderLayout.LINE_END);
		burnWrapPanel.add(burnActionPanel,BorderLayout.PAGE_END);
		return burnWrapPanel;
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==closeButton) {
			clearAndHide();
			return;
		}
		if (e.getSource()==saveButton) {
			if (burnName.getText().isEmpty()) {
				return;
			}
			JFileChooser fileSelect = new JFileChooser();
			fileSelect.setSelectedFile(new File("pulsefire-"+burnName.getText()+".hex"));
			int returnVal = fileSelect.showSaveDialog((JButton)e.getSource());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				saveToFile(burnName.getText(),fileSelect.getSelectedFile());
			}
			return;
		}
		if (e.getSource()==flashButton) {
			if (burnName.getText().isEmpty()) {
				return;
			}
			
			String hexResource = "firmware/"+burnName.getText()+"/pulsefire.hex";
			logger.info("Start chip flash of: "+hexResource);
			byte[] flashData = null;
			try {
				flashData = new FlashHexReader().loadHex(hexResource);
			} catch (Exception hexException) {
				logger.log(Level.WARNING,hexException.getMessage(),hexException);
				JComponentFactory.showWarningDialog(this.getRootPane(), "Hex data error", "There has been an error in loading or parsing the flash hex data correctly.\nMessage: "+hexException.getMessage());
				return;
			}
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
			portsComboBox.setEnabled(false);
			progComboBox.setEnabled(false);
			progVerboseBox.setEnabled(false);
			progVerifyBox.setEnabled(false);
			flashButton.setEnabled(false);
			closeButton.setEnabled(false);
			table.setEnabled(false);
			if (flashLog.getText().length()>32) {
				flashLog.setText(""); // clear log for second flash 
			}
			flashConfig = new FlashControllerConfig();
			flashConfig.setPort(portsComboBox.getSelectedItem().toString());
			flashConfig.setPortProtocol(progComboBox.getSelectedItem().toString());
			flashConfig.setLogDebug(progVerboseBox.isSelected());
			flashConfig.setFlashVerify(progVerifyBox.isSelected());
			flashConfig.setFlashData(flashData);
			String deviceId = burnDeviceId.getText();
			if (deviceId!=null && deviceId.isEmpty()==false && deviceId.startsWith("0x") && deviceId.length()>2) {
				flashConfig.setDeviceSignature(Integer.parseInt(burnDeviceId.getText().substring(2),16));
			}
			if (nativeFlashCmd!=null && nativeFlashCmd.isEmpty()==false) {
				flashConfig.setNativeFlashCmd(nativeFlashCmd);
			}
			if (nativeFlashConfig!=null && nativeFlashConfig.isEmpty()==false) {
				flashConfig.setNativeFlashConfig(nativeFlashConfig);
			}
			flashProgramController = FlashManager.createFlashController(flashConfig);
			FlashThread t = new FlashThread();t.start();
			ProgressThread p = new ProgressThread();p.start();
			return;
		}
		
		// Always refilter (also used for filter checkboxes)
		tableModel.refilterData();
	}

	private void saveToFile(String burnName,File file) {
		try {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			if (cl==null) {
				cl = this.getClass().getClassLoader();
			}
			InputStream is = cl.getResourceAsStream("firmware/"+burnName+"/pulsefire.hex");
			OutputStream os = new FileOutputStream(file);
			byte[] buf = new byte[4096];
			int cnt = is.read(buf);
			while (cnt > 0) {
				os.write(buf, 0, cnt);
				cnt = is.read(buf);
			}
			os.close();
			is.close();
		} catch (Exception saveException) {
			logger.log(Level.WARNING,saveException.getMessage(),saveException);
			JComponentFactory.showWarningDialog(this.getRootPane(), "Save file error", "There has been an error in saving the flash hex data correctly.\nMessage: "+saveException.getMessage());
			return;
		}
	}
	
	private void updateText(String data) {	
		flashLog.append(flashLogTimeFormat.format(new Date()));
		flashLog.append(" # ");
		flashLog.append(data);
		flashLog.append("\n");
		flashLog.repaint();
		flashLog.setCaretPosition(flashLog.getText().length()); // auto scroll to end
	}
	
	@Override
	public void flashLogMessage(String message) {
		updateText(message);
	}
	
	class FlashThread extends Thread {
		@Override
		public void run() {
			try {
				logger.fine("Start flash thread.");
				flashProgramController.addFlashLogListener(JFlashDialog.this);
				flashProgramController.flash(flashConfig);
			} catch (Exception runException) {
				logger.log(Level.WARNING,runException.getMessage(),runException);
			} finally {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						JFlashDialog.this.setCursor(Cursor.getDefaultCursor());
					}
				});
				flashProgramController.removeFlashLogListener(JFlashDialog.this);
				flashProgramController = null;
				logger.fine("Stopped flash thread.");
			}
		}
	}
	class ProgressThread extends Thread {
		@Override
		public void run() {
			logger.fine("Start progress thread.");
			while (flashProgramController!=null) {
				int progress = flashProgramController.getProgress();
				logger.finer("Flash progress: "+progress);
				flashProgressBar.getModel().setValue(progress);
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
				}
			}
			flashProgressBar.getModel().setValue(0);
			portsComboBox.setEnabled(true);
			progComboBox.setEnabled(true);
			progVerboseBox.setEnabled(true);
			progVerifyBox.setEnabled(true);
			flashButton.setEnabled(true);
			closeButton.setEnabled(true);
			table.setEnabled(true);
			logger.fine("Stopped progress thread.");
		}
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting()) {
			return;
		}
		if (table.getSelectedRow()==-1) {
			return; // no selection
		}
		BuildOption option = tableModel.getBuildOptions().get(table.getSelectedRow());
		burnName.setText(option.name);
		if ("stk500v2".equals(option.ispProg)) {
			progComboBox.setSelectedIndex(1);
		} else {
			progComboBox.setSelectedIndex(0);
		}
		// dirty update small hard codes chip device id table
		if (option.name.startsWith("atmega168p")) {
			burnDeviceId.setText("0x1e940b");
		} else if (option.name.startsWith("atmega328p")) {
			burnDeviceId.setText("0x1e950f");
		} else if (option.name.startsWith("atmega1280")) {
			burnDeviceId.setText("0x1e9703");
		} else if (option.name.startsWith("atmega2560")) {
			burnDeviceId.setText("0x1e9801");
		} else {
			burnDeviceId.setText("");
		}
	}
	
	
	/** This method clears the dialog and hides it. */
	public void clearAndHide() {
		setVisible(false);
	}
	
	public class DeviceImagesTableModel extends AbstractTableModel {

		private static final long serialVersionUID = -1432038909521987705L;
		private BuildOptionComparator dataComp = null;
		private List<BuildOption> data = null;
		private List<BuildOption> dataFull = null;
		
		public DeviceImagesTableModel() {
			dataComp = new BuildOptionComparator();
			data = new ArrayList<BuildOption>(100);
			dataFull = new ArrayList<BuildOption>(100);
			try {
				dataFull.addAll(readBuildOptionsResource("firmware/makefile"));
			} catch (Exception e) {
				logger.warning("Could not load resource makefile.");
			}
			refilterData();
		}
		
		public List<BuildOption> getBuildOptions() {
			return data;
		}
		
		public void refilterData() {
			data.clear();
			for (BuildOption option:dataFull) {
				if(option.name!=null && option.name.startsWith("arm")) {
					continue; // skip test arm builds for now
				}
				
				boolean filter = false;
				for (FilterItem fi:filterItems.values()) {
					boolean selected = fi.checkBox.isSelected();
					boolean flag = checkFlag(option,fi.name);
					if (selected && flag==false) {
						filter = true;
						break;
					}
					if (selected==false && flag) {
						filter = true;
						break;
					}
				}
				if (filter) {
					continue;
				}
				if (mcuTypeBox.getSelectedIndex()>0) {
					String mcyTypeText = mcuTypeBox.getSelectedItem().toString();
					if (mcyTypeText.equals(option.mcu)==false) {
						continue;
					}
				}
				if (mcuSpeedBox.getSelectedIndex()>0) {
					String speedText = mcuSpeedBox.getSelectedItem().toString().replace("Mhz", "");
					Integer speed = new Integer(speedText+"000000");
					if (speed.equals(option.speed)==false) {
						continue;
					}
				}
				data.add(option);
			}
			Collections.sort(data,dataComp);
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
			BuildOption option = data.get(row);
			switch (col) {
			case 0:		return option.name;
			case 1:		return option.speed;
			default:	return checkFlag(option,columnNames[col]);
			}
		}
		
		private boolean checkFlag(BuildOption option,String checkFlag) {
			for (String flag:option.flags) {
				if (flag.endsWith(checkFlag)) {
					return true;
				}
			}
			return false;
		}
		/*
		private String getFlagOption(BuildOption option,String checkOption) {
			for (String flag:option.options) {
				if (flag.contains(checkOption)) {
					String[] values = flag.split("=");
					if (values.length>1) {
						return values[1];
					}
				}
			}
			return "";
		}
		*/
	}
	
	public class BuildOption {
		public String name;
		public String mcu;
		public Integer speed;
		public String ispMcu;
		public String ispProg;
		public List<String> flags = new ArrayList<String>(20);
		public List<String> options = new ArrayList<String>(20);
	}
	
	class BuildOptionComparator implements Comparator<BuildOption> {
		@Override
		public int compare(BuildOption o1, BuildOption o2) {
			return o1.name.compareTo(o2.name);
		}
	}
	
	public Collection<BuildOption> readBuildOptionsFile(File inputFile) throws IOException {
		return readBuildOptions(new FileInputStream(inputFile));
	}
	
	public Collection<BuildOption> readBuildOptionsResource(String inputResource) throws IOException {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (cl==null) {
			cl = this.getClass().getClassLoader();
		}
		return readBuildOptions(cl.getResourceAsStream(inputResource));
	}
	
	public Collection<BuildOption> readBuildOptions(InputStream input) throws IOException {
		if (input==null) {
			throw new NullPointerException("Can't read null inputstream.");
		}
		Map<String,BuildOption> result = new HashMap<String,BuildOption>(100);
		LineNumberReader reader = null;
		try {
			reader = new LineNumberReader(new InputStreamReader(input,Charset.forName("UTF-8")));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("#")) {
					continue;
				}
				if (line.startsWith("//")) {
					continue;
				}
				int idx = line.indexOf(':');
				if (idx<4) {
					continue; // skip nonfound and small lines.
				}
				if (line.contains("-isp:")) {
					idx-=4;
				}
				char buildNumber2 = line.charAt(idx-1);
				char buildNumber1 = line.charAt(idx-2);
				char buildNumber0 = line.charAt(idx-3);
				if (Character.isDigit(buildNumber2)==false) {
					continue;
				}
				if (Character.isDigit(buildNumber1)==false) {
					continue;
				}
				if (Character.isDigit(buildNumber0)==false) {
					continue;
				}
				// found correct line, get the build option
				String[] split = line.split(":");
				String buildName = split[0];
				buildName = buildName.replaceAll("-isp", "");
				BuildOption option = result.get(buildName);
				if (option==null) {
					option = new BuildOption();
					option.name = split[0];
					result.put(option.name,option);
				}
				
				// fillin the option of this line.
				String[] lineOption = split[1].trim().split("=");
				if (lineOption.length<2) {
					continue;
				}
				if (lineOption[0].startsWith("MCU")) {
					option.mcu=lineOption[1].trim();
					continue;
				}
				if (lineOption[0].startsWith("F_CPU")) {
					option.speed=new Integer(lineOption[1].trim());
					continue;
				}
				if (lineOption[0].startsWith("ISP_MCU")) {
					option.ispMcu=lineOption[1].trim();
					continue;
				}
				if (lineOption[0].startsWith("ISP_PROG")) {
					option.ispProg=lineOption[1].trim();
					continue;
				}
				if (lineOption[0].startsWith("PFLAGS")) {
					String flag = lineOption[1].trim();
					if (flag.startsWith("-D_")) {
						if (lineOption.length>2) {
							option.options.add(flag+"="+lineOption[2]);
						} else {
							option.options.add(flag);
						}
					} else {
						option.flags.add(flag);
					}
					continue;
				}
			}
		} finally {
			if (reader!=null) {
				reader.close();
			}
			if (input!=null) {
				input.close();
			}
		}
		logger.info("Loaded makefile with: "+result.size()+" builds.");
		return result.values();
	}

	@Override
	public void mouseClicked(MouseEvent evt) {
		TableColumnModel colModel = table.getColumnModel();
		int columnIndex = colModel.getColumnIndexAtX(evt.getX());
		int modelIndex = table.convertColumnIndexToModel(columnIndex);
		if (columnIndex < 0 | modelIndex < 0) {
			return;
		}
		Rectangle headerPadding = table.getTableHeader().getHeaderRect(columnIndex);
		if (columnIndex == 0) {
			headerPadding.width -= 3;
		} else {
			headerPadding.grow(-3, 0);
		}
		if (headerPadding.contains(evt.getX(), evt.getY())==false) {
			return; // between columns
		}
		if (modelIndex==0) {
			return; // name column
		}
		if (modelIndex==1) {
			return; // speed column
		}
		
		String name = tableModel.getColumnName(modelIndex);
		
		FilterItem fi = filterItems.get(name);
		if (fi==null) {
			fi = new FilterItem();
			fi.name=name;
			fi.checkBox=new JCheckBox();
			fi.checkBox.addActionListener(this);
			fi.checkBox.setSelected(true);
			fi.label=new JLabel(name);
			filterItems.put(name,fi);
		} else {
			fi.checkBox.removeActionListener(this);
			filterItems.remove(fi.name);
		}
		
		filterItemPanel.removeAll();
		for (FilterItem i:filterItems.values()) {
			filterItemPanel.add(i.label);
			filterItemPanel.add(i.checkBox);
		}
		
		int compomentCount = filterItems.size();
		int columnCount = 2;
		int spaceSize = (compomentCount/columnCount)*columnCount;
		if (spaceSize!=compomentCount) {
			spaceSize += columnCount;
		}
		for (int i=compomentCount;i<spaceSize;i++) {
			filterItemPanel.add(new JLabel(""));
			filterItemPanel.add(new JLabel(""));
		}
		SpringLayoutGrid.makeCompactGrid(filterItemPanel,2,filterItemPanel.getComponentCount()/2);
		
		filterItemPanel.revalidate();
		tableModel.refilterData();
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {}
	@Override
	public void mouseExited(MouseEvent arg0) {}
	@Override
	public void mousePressed(MouseEvent arg0) {}
	@Override
	public void mouseReleased(MouseEvent arg0) {}

}

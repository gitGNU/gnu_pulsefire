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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import javax.swing.ToolTipManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import org.nongnu.pulsefire.device.flash.FlashControllerConfig;
import org.nongnu.pulsefire.device.flash.FlashHexReader;
import org.nongnu.pulsefire.device.flash.FlashLogListener;
import org.nongnu.pulsefire.device.flash.FlashManager;
import org.nongnu.pulsefire.device.flash.FlashProgramController;
import org.nongnu.pulsefire.device.ui.DevicePortsComboBoxModel;
import org.nongnu.pulsefire.device.ui.JComponentFactory;
import org.nongnu.pulsefire.device.ui.SpringLayoutGrid;

/**
 * JFlashDialog displays all device firmwares and let user flash them.
 * 
 * @author Willem Cazander
 */
public class JFlashDialog extends JDialog implements ActionListener,ListSelectionListener,FlashLogListener {
	
	private static final long serialVersionUID = -7552916322807635756L;
	private Logger logger = null;
	private FlashControllerConfig flashConfig = null;
	private volatile FlashProgramController flashProgramController = null;
	private JComboBox mcuTypeBox = null;
	private JComboBox mcuSpeedBox = null;
	private JCheckBox buildLcdBox = null;
	private JCheckBox buildLpmBox = null;
	private JCheckBox buildPpmBox = null;
	private JCheckBox buildAdcBox = null;
	private JCheckBox buildExtOutBox = null;
	private JCheckBox buildExtOut16Box = null;
	private JCheckBox buildExtLcdBox = null;
	private JCheckBox buildExtDocBox = null;
	private JComboBox portsComboBox = null;
	private JComboBox progComboBox = null;
	private JCheckBox progDebugBox = null; 
	private JProgressBar flashProgressBar = null;
	private JTextArea flashLog = null;
	private DateFormat flashLogTimeFormat = null;
	private JButton cancelButton = null;
	private JButton flashButton = null;
	private JButton saveButton = null;
	private DeviceImagesTableModel tableModel = null;
	private JTable table = null;
	private JLabel burnName = null;
	private String[] columnNames = new String[] {"name","speed",
			"EXT_OUT","EXT_O16","EXT_LCD","EXT_DIC","EXT_DOC",
			"PWM","LCD","LPM","PPM","ADC","DIC","DOC","DEV","PTC","PTT","STV","VFC","SWC","MAL"};
			
	public JFlashDialog(Frame aFrame) {
		super(aFrame, true);
		logger = Logger.getLogger(JFlashDialog.class.getName());
		setTitle("Flash");
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
	}
	
	private JPanel createPanelTop() {
		JPanel tableOption = JComponentFactory.createJFirePanel("Filter options");
		tableOption.setLayout(new SpringLayout());
		
		JLabel mcuText = new JLabel("mcu:");
		tableOption.add(mcuText);
		mcuTypeBox = new JComboBox(new String[] {"ALL","atmega328p","atmega168p","atmega1280"});
		mcuTypeBox.addActionListener(this);
		tableOption.add(mcuTypeBox);

		JLabel lcdText = new JLabel("lcd:");
		tableOption.add(lcdText);
		buildLcdBox = new JCheckBox();
		buildLcdBox.addActionListener(this);
		tableOption.add(buildLcdBox);
		
		JLabel ppmText = new JLabel("ppm:");
		tableOption.add(ppmText);
		buildPpmBox = new JCheckBox();
		buildPpmBox.addActionListener(this);
		tableOption.add(buildPpmBox);	

		JLabel extOutText = new JLabel("ext_out:");
		tableOption.add(extOutText);
		buildExtOutBox = new JCheckBox();
		buildExtOutBox.addActionListener(this);
		tableOption.add(buildExtOutBox);
		
		JLabel extLcdText = new JLabel("ext_lcd:");
		tableOption.add(extLcdText);
		buildExtLcdBox = new JCheckBox();
		buildExtLcdBox.addActionListener(this);
		tableOption.add(buildExtLcdBox);
		
		JLabel speedText = new JLabel("speed:");
		tableOption.add(speedText);
		mcuSpeedBox = new JComboBox(new String[] {"ALL","16Mhz","20Mhz","8Mhz","72Mhz" /*,"400Mhz","1Ghz"*/});
		mcuSpeedBox.addActionListener(this);
		tableOption.add(mcuSpeedBox);
		
		JLabel lpmText = new JLabel("lpm:");
		tableOption.add(lpmText);
		buildLpmBox = new JCheckBox();
		buildLpmBox.addActionListener(this);
		tableOption.add(buildLpmBox);
		
		JLabel adcText = new JLabel("adc:");
		tableOption.add(adcText);
		buildAdcBox = new JCheckBox();
		buildAdcBox.addActionListener(this);
		tableOption.add(buildAdcBox);
		
		JLabel extOut16Text = new JLabel("ext_o16:");
		tableOption.add(extOut16Text);
		buildExtOut16Box = new JCheckBox();
		buildExtOut16Box.addActionListener(this);
		tableOption.add(buildExtOut16Box);
		
		JLabel extDocText = new JLabel("ext_doc:");
		tableOption.add(extDocText);
		buildExtDocBox = new JCheckBox();
		buildExtDocBox.addActionListener(this);
		tableOption.add(buildExtDocBox);	
		
		SpringLayoutGrid.makeCompactGrid(tableOption,2,10);
		
		JPanel optionWrapPanel = new JPanel();
		optionWrapPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		optionWrapPanel.add(tableOption);
		
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
		
		//JPanel burnOptionWrapPanel = new JPanel();
		JPanel burnOptionPanel = new JPanel();
		burnOptionPanel.setLayout(new SpringLayout());
		burnOptionPanel.add(new JLabel("Name:"));
		burnName = new JLabel();
		burnOptionPanel.add(burnName);
		burnOptionPanel.add(new JLabel("Port:"));
		DevicePortsComboBoxModel portModel = new DevicePortsComboBoxModel();
		portsComboBox = new JComboBox(portModel);
		portsComboBox.addPopupMenuListener(portModel);
		burnOptionPanel.add(portsComboBox);
		burnOptionPanel.add(new JLabel("Programer:"));
		progComboBox = new JComboBox(new String[] {"arduino","stk500v2"});
		burnOptionPanel.add(progComboBox);
		burnOptionPanel.add(new JLabel("logDebug:"));
		progDebugBox = new JCheckBox();
		burnOptionPanel.add(progDebugBox);
		SpringLayoutGrid.makeCompactGrid(burnOptionPanel,4,2);
		burnPanel.add(burnOptionPanel);
		//burnPanel.add(burnOptionWrapPanel,BorderLayout.CENTER);
		
		JPanel burnProgressPanel = new JPanel();
		burnProgressPanel.setLayout(new GridLayout(1,1));
		burnProgressPanel.setBorder(BorderFactory.createEmptyBorder(6,0,12,0));
		flashProgressBar = new JProgressBar();
		flashProgressBar.setStringPainted(true);
		burnProgressPanel.add(flashProgressBar);
		
		JPanel logPanel = JComponentFactory.createJFirePanel("Burn Log");
		flashLogTimeFormat = new SimpleDateFormat("HH:mm:ss");
		flashLog = new JTextArea(7,30);
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
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		burnActionPanel.add(cancelButton);
		
		JPanel burnWrapPanel = new JPanel();
		burnWrapPanel.setLayout(new BorderLayout());
		burnWrapPanel.add(burnProgressPanel,BorderLayout.PAGE_START);
		burnWrapPanel.add(burnPanel,BorderLayout.CENTER);
		burnWrapPanel.add(logPanel,BorderLayout.LINE_END);
		burnWrapPanel.add(burnActionPanel,BorderLayout.PAGE_END);
		return burnWrapPanel;
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==cancelButton) {
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
		}
		if (e.getSource()==flashButton) {
			if (burnName.getText().isEmpty()) {
				return;
			}
			if (flashLog.getText().length()>32) {
				flashLog.setText(""); // clear log for second flash 
			}
			flashButton.setEnabled(false);
			
			String hexResource = "firmware/"+burnName.getText()+"/pulsefire.hex";
			byte[] flashData = null;
			try {
				flashData = new FlashHexReader().loadHex(hexResource);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return;
			}
			
			flashConfig = new FlashControllerConfig();
			flashConfig.setPort(portsComboBox.getSelectedItem().toString());
			flashConfig.setPortProtocol(progComboBox.getSelectedItem().toString());
			flashConfig.setLogDebug(progDebugBox.isSelected());
			flashConfig.setFlashData(flashData);
			flashProgramController = FlashManager.createFlashController(flashConfig);
			FlashThread t = new FlashThread();t.start();
			ProgressThread p = new ProgressThread();p.start();
			return;
		}
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
		} catch (Exception e) {
			e.printStackTrace();
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
			} catch (Exception e1) {
				flashProgressBar.setString(e1.getMessage());
				e1.printStackTrace();
			} finally {
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
			flashButton.setEnabled(true);
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
	}
	
	
	/** This method clears the dialog and hides it. */
	public void clearAndHide() {
		setVisible(false);
	}

	public class DeviceImagesTableModel extends AbstractTableModel {

		private static final long serialVersionUID = -1432038909521987705L;
		private List<BuildOption> data = null;
		private List<BuildOption> dataFull = null;
		
		public DeviceImagesTableModel() {
			data = new ArrayList<BuildOption>(100);
			dataFull = new ArrayList<BuildOption>(100);
			try {
				dataFull.addAll(readBuildOptionsResource("firmware/makefile"));
			} catch (IOException e) {
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
				if (buildLcdBox.isSelected() && checkFlag(option,"LCD")==false) {
					continue;
				}
				if (buildLpmBox.isSelected() && checkFlag(option,"LPM")==false) {
					continue;
				}
				if (buildPpmBox.isSelected() && checkFlag(option,"PPM")==false) {
					continue;
				}
				if (buildAdcBox.isSelected() && checkFlag(option,"ADC")==false) {
					continue;
				}
				if (buildExtOutBox.isSelected() && checkFlag(option,"EXT_OUT")==false) {
					continue;
				}
				if (buildExtOut16Box.isSelected() && checkFlag(option,"EXT_OUT_16BIT")==false) {
					continue;
				}
				if (buildExtLcdBox.isSelected() && checkFlag(option,"EXT_LCD")==false) {
					continue;
				}
				if (buildExtDocBox.isSelected() && checkFlag(option,"EXT_LCD_DOC")==false) {
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
			default:
			case 0:		return option.name;
			case 1:		return option.speed;
			case 2:		return checkFlag(option,"EXT_OUT");
			case 3:		return checkFlag(option,"EXT_OUT_16BIT");
			case 4:		return checkFlag(option,"EXT_LCD");
			case 5:		return checkFlag(option,"EXT_LCD_DIC");
			case 6:		return checkFlag(option,"EXT_LCD_DOC");
			case 7:		return checkFlag(option,"PWM");
			case 8:		return checkFlag(option,"LCD");
			case 9:		return checkFlag(option,"LPM");
			case 10:	return checkFlag(option,"PPM");
			case 11:	return checkFlag(option,"ADC");
			case 12:	return checkFlag(option,"DIC");
			case 13:	return checkFlag(option,"DOC");
			case 14:	return checkFlag(option,"DEV");
			case 15:	return checkFlag(option,"PTC");
			case 16:	return checkFlag(option,"PTT");
			case 17:	return checkFlag(option,"STV");
			case 18:	return checkFlag(option,"VFC");
			case 19:	return checkFlag(option,"SWC");
			case 20:	return checkFlag(option,"MAL");
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

}

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
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import org.nongnu.pulsefire.device.flash.FlashManager;
import org.nongnu.pulsefire.device.ui.DevicePortsComboBoxModel;
import org.nongnu.pulsefire.device.ui.JComponentFactory;
import org.nongnu.pulsefire.device.ui.SpringLayoutGrid;

/**
 * JFlashDialog displays all device firmwares and let user flash them.
 * 
 * @author Willem Cazander
 */
public class JFlashDialog extends JDialog implements ActionListener,ListSelectionListener {
	
	private static final long serialVersionUID = -7552916322807635756L;
	private Logger logger = null;
	private volatile FlashManager flashManager = null;
	private JComboBox mcuTypeBox = null;
	private JComboBox mcuSpeedBox = null;
	private JCheckBox buildLcdBox = null;
	private JCheckBox buildLpmBox = null;
	private JCheckBox buildPpmBox = null;
	private JCheckBox buildAdcBox = null;
	private JCheckBox buildLcdExtBox = null;
	private JCheckBox buildLcd4Box = null;
	private JComboBox portsComboBox = null;
	private JComboBox progComboBox = null;
	private JProgressBar flashProgressBar = null;
	private JButton cancelButton = null;
	private JButton flashButton = null;
	private DeviceImagesTableModel tableModel = null;
	private JTable table = null;
	private JLabel burnName = null;
	private JLabel burnProg = null;
	private String[] columnNames = new String[] {"name","speed",
			"EXT_OUT","EXT_O16","EXT_LCD","EXT_DIC","EXT_DOC",
			"PWM","LCD","LPM","PPM","ADC","DIC","DOC","DEV","PTC","PTT","STV","VFC","SWC","MAL"};
			
	public JFlashDialog(Frame aFrame) {
		super(aFrame, true);
		setTitle("Flash");
		logger = Logger.getLogger(JFlashDialog.class.getName());

		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		mainPanel.setLayout(new BorderLayout());
		
		JPanel tableOption = JComponentFactory.createJFirePanel("Filter options");
		tableOption.setLayout(new SpringLayout());
		
		JLabel mcuText = new JLabel("mcu:");
		tableOption.add(mcuText);
		mcuTypeBox = new JComboBox(new String[] {"ALL","atmega328p","atmega168p","mega"});
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

		JLabel lcdExtText = new JLabel("lcd_ext:");
		tableOption.add(lcdExtText);
		buildLcdExtBox = new JCheckBox();
		buildLcdExtBox.addActionListener(this);
		tableOption.add(buildLcdExtBox);	
		
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
		
		JLabel lcd4Text = new JLabel("lcd_4row:");
		tableOption.add(lcd4Text);
		buildLcd4Box = new JCheckBox();
		buildLcd4Box.addActionListener(this);
		tableOption.add(buildLcd4Box);	
		
		SpringLayoutGrid.makeCompactGrid(tableOption,2,8);
		
		JPanel optionWrapPanel = new JPanel();
		optionWrapPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		optionWrapPanel.add(tableOption);
		mainPanel.add(optionWrapPanel,BorderLayout.NORTH);
		
		//JPanel tablePanel = new JPanel();
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
		mainPanel.add(scroll,BorderLayout.CENTER);
		
		JPanel burnPanel = JComponentFactory.createJFirePanel("Burn");
		burnPanel.setLayout(new BorderLayout());
		
		JPanel burnInfoPanel = new JPanel();
		burnInfoPanel.setLayout(new SpringLayout());
		burnInfoPanel.add(new JLabel("name:"));
		burnName = new JLabel();
		burnInfoPanel.add(burnName);
		burnInfoPanel.add(new JLabel("prog:"));
		burnProg = new JLabel();
		burnInfoPanel.add(burnProg);
		SpringLayoutGrid.makeCompactGrid(burnInfoPanel,2,2);
		burnPanel.add(burnInfoPanel,BorderLayout.NORTH);
		
		JPanel burnOptionWrapPanel = new JPanel();
		burnOptionWrapPanel.setLayout(new BoxLayout(burnOptionWrapPanel, BoxLayout.PAGE_AXIS));
		JPanel burnOptionPanel = new JPanel();
		burnOptionPanel.setLayout(new SpringLayout());
		burnOptionPanel.add(new JLabel("Port"));
		DevicePortsComboBoxModel portModel = new DevicePortsComboBoxModel();
		portsComboBox = new JComboBox(portModel);
		portsComboBox.addPopupMenuListener(portModel);
		burnOptionPanel.add(portsComboBox);
		burnOptionPanel.add(new JLabel("Programer"));
		progComboBox = new JComboBox();
		burnOptionPanel.add(progComboBox);
		SpringLayoutGrid.makeCompactGrid(burnOptionPanel,2,2);
		burnOptionWrapPanel.add(burnOptionPanel);
		JPanel burnProgressPanel = new JPanel();
		flashProgressBar = new JProgressBar();
		flashProgressBar.setStringPainted(true);
		burnProgressPanel.add(flashProgressBar);
		burnOptionWrapPanel.add(burnProgressPanel);
		burnPanel.add(burnOptionWrapPanel,BorderLayout.CENTER);

		JPanel burnActionPanel = new JPanel();
		burnActionPanel.setBorder(BorderFactory.createEmptyBorder());
		flashButton = new JButton("Flash");
		flashButton.addActionListener(this);
		burnActionPanel.add(flashButton);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		burnActionPanel.add(cancelButton);
		burnPanel.add(burnActionPanel,BorderLayout.SOUTH);
		
		JPanel burnWrapPanel = new JPanel();
		burnWrapPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		burnWrapPanel.add(burnPanel);
		mainPanel.add(burnWrapPanel,BorderLayout.SOUTH);
		
		getContentPane().add(mainPanel);		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				clearAndHide();
			}
		});
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==cancelButton) {
			clearAndHide();
			return;
		}
		if (e.getSource()==flashButton) {
			flashButton.setEnabled(false);
			flashManager = new FlashManager();
			flashManager.setPort(portsComboBox.getSelectedItem().toString());
			flashManager.setProtocol(burnProg.getText());
			FlashThread t = new FlashThread();t.start();
			ProgressThread p = new ProgressThread();p.start();
			return;
		}
		tableModel.refilterData();
	}

	class FlashThread extends Thread {
		@Override
		public void run() {
			try {
				String hexResource = "firmware/"+burnName.getText()+"/pulsefire.hex";
				logger.info("Start flash thread with hex: "+hexResource);
				flashManager.loadHex(hexResource);
				flashManager.flash();
			} catch (Exception e1) {
				flashProgressBar.setString(e1.getMessage());
				e1.printStackTrace();
			} finally {
				flashManager = null;
				logger.fine("Stopped flash thread.");
			}
		}
	}
	class ProgressThread extends Thread {
		@Override
		public void run() {
			logger.fine("Start progress thread.");
			while (flashManager!=null) {
				int progress = flashManager.getProgress();
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
		burnProg.setText(option.ispProg);
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
				if (buildLcdExtBox.isSelected() && checkFlag(option,"EXT_LCD")==false) {
					continue;
				}
				if (buildLcd4Box.isSelected() && "4".equals(getFlagOption(option,"LCD_SIZE_ROW"))==false) {
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

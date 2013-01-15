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

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.filechooser.FileFilter;

import org.nongnu.pulsefire.device.DeviceData;
import org.nongnu.pulsefire.device.ui.JComponentEnableStateListener;
import org.nongnu.pulsefire.device.ui.JComponentFactory;
import org.nongnu.pulsefire.device.ui.PulseFireUI;
import org.nongnu.pulsefire.device.ui.SpringLayoutGrid;
import org.nongnu.pulsefire.device.ui.components.JCommandButton;
import org.nongnu.pulsefire.device.ui.components.JCommandComboBox;
import org.nongnu.pulsefire.device.ui.components.JCommandDial;
import org.nongnu.pulsefire.device.ui.components.JCommandLabel;
import org.nongnu.pulsefire.device.ui.components.JFireQMapTable;
import org.nongnu.pulsefire.device.ui.time.PulseFireDataPuller;
import org.nongnu.pulsefire.wire.Command;
import org.nongnu.pulsefire.wire.CommandName;
import org.nongnu.pulsefire.wire.CommandVariableType;
import org.nongnu.pulsefire.wire.CommandWire;

/**
 * JTabPanelSystem
 * 
 * @author Willem Cazander
 */
public class JTabPanelSystem extends AbstractFireTabPanel {

	private static final long serialVersionUID = -5523263800067726564L;
	private Logger logger = null;

	public JTabPanelSystem() {
		logger = Logger.getLogger(JTabPanelSystem.class.getName());
		setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel wrap = new JPanel();
		wrap.setLayout(new SpringLayout());
		wrap.add(createSystemConfig());
		wrap.add(createSystemIO());
		wrap.add(createSystemLcd());
		wrap.add(createSystemWarmup());		
		SpringLayoutGrid.makeCompactGrid(wrap,1,4);
		add(wrap);
	}
	
	private JPanel createSystemConfig() {
		JPanel wrapPanel = JComponentFactory.createJFirePanel(this,"config");
		JPanel confPanel = new JPanel();
		confPanel.setLayout(new SpringLayout());
		
		confPanel.add(new JCommandLabel(CommandName.save));
		confPanel.add(new JCommandButton(CommandName.save));
		
		confPanel.add(new JCommandLabel(CommandName.reset_conf));
		confPanel.add(new JCommandButton(CommandName.reset_conf));

		confPanel.add(new JCommandLabel(CommandName.reset_data));
		confPanel.add(new JCommandButton(CommandName.reset_data));

		confPanel.add(new JCommandLabel(CommandName.reset_chip));
		confPanel.add(new JCommandButton(CommandName.reset_chip));
		
		JButton loadButton = new JButton("Load");
		JComponentEnableStateListener.attach(loadButton,null);
		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new ChipConfigFileFilter());
				int returnVal = fc.showOpenDialog((JButton)e.getSource());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					final File file = fc.getSelectedFile();
					Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							PulseFireDataPuller puller = (PulseFireDataPuller)PulseFireUI.getInstance().getEventTimeManager().getEventTimeTriggerByName("refreshData").getRunnable();
							try {
								puller.setRunPause(true);
								logger.info("Reading config from: "+file.getAbsolutePath());
								readConfig(new FileInputStream(file));
							} catch (Exception e1) {
								e1.printStackTrace();
							} finally {
								puller.setRunPause(false);
							}
						}
					});
					t.start();
				}
			}
		});
		confPanel.add(JComponentFactory.createJLabel("Load File"));
		confPanel.add(loadButton);
		
		JButton saveButton = new JButton("Save");
		JComponentEnableStateListener.attach(saveButton,null);
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new ChipConfigFileFilter());
				fc.setSelectedFile(new File("my-config.pfcc"));
				int returnVal = fc.showSaveDialog((JButton)e.getSource());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File selFile = fc.getSelectedFile();
					if (selFile.getName().contains(".")==false) {
						selFile = new File(selFile.getAbsolutePath()+ChipConfigFileFilter.CONFIG_EXT);
					}
					final File file = selFile;
					Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								logger.info("Writing config to: "+file.getAbsolutePath());
								writeConfig(new FileOutputStream(file));
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
					});
					t.start();
					
				}
			}
		});
		confPanel.add(JComponentFactory.createJLabel("Save File"));
		confPanel.add(saveButton);
		
		SpringLayoutGrid.makeCompactGrid(confPanel,6,2);
		wrapPanel.add(confPanel);
		return wrapPanel;
	}

	class ChipConfigFileFilter extends FileFilter {
		static public final String CONFIG_EXT = ".pfcc";
		static private final String DESC = "*"+CONFIG_EXT+" - PulseFire ChipConfig";
		
		@Override
		public String getDescription() {
			return DESC;
		}
		
		@Override
		public boolean accept(File file) {
			return file.getName().endsWith(CONFIG_EXT);
		}
	}
	
	private void writeConfig(OutputStream output) throws IOException {
		try {
			DeviceData devData = PulseFireUI.getInstance().getDeviceData();
			Writer writer = new BufferedWriter(new OutputStreamWriter(output, Charset.forName("UTF-8")));
			writer.append("#??pulsefire??");
			writer.append(System.getProperty("line.separator"));
			writeConfigCommand(writer,devData.getDeviceParameter(CommandName.chip_version));
			writeConfigCommand(writer,devData.getDeviceParameter(CommandName.chip_name));
			writeConfigCommand(writer,devData.getDeviceParameter(CommandName.chip_name_id));
			for (CommandName cn:CommandName.values()) {
				if (CommandVariableType.CONF.equals(cn.getType())==false) {
					continue;
				}
				if (cn.isIndexedA()) {
					for (int i=0;i<cn.getMaxIndexA();i++) {
						Command cmd = devData.getDeviceParameterIndexed(cn, i);
						writeConfigCommand(writer, cmd);
					}
				} else {
					Command cmd = devData.getDeviceParameter(cn);
					writeConfigCommand(writer,cmd);
				}
			}
			writer.append("# END");
			writer.append(System.getProperty("line.separator"));
			writer.flush();
			writer.close();
			
		} finally {
			try {
				output.close();
			} catch (IOException e) {
			}
		}
	}
	
	private void writeConfigCommand(Writer writer,Command cmd) throws IOException {
		if (cmd==null) {
			return;
		}
		String cmdLine = CommandWire.encodeCommand(cmd,true);
		writer.append(cmdLine);
		writer.append(System.getProperty("line.separator"));
		writer.flush();
	}
	
	private void readConfig(InputStream input) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(input, Charset.forName("UTF-8")));
		String line = reader.readLine();
		if (line.startsWith("#??pulsefire??")==false) {
			logger.warning("No magic header found in line: '"+line+"'");
			reader.close();
			return;
		}
		while ((line = reader.readLine()) != null) {
			if (line.isEmpty()) {
				continue;
			}
			if (line.startsWith("#")) {
				continue;
			}
			int idxComment = line.indexOf("#");
			if (idxComment>1) {
				line = line.substring(0,idxComment);
			}
			if (line.startsWith("chip")) {
				continue;
			}
			try {
				Command cmd = CommandWire.decodeCommand(line);
				PulseFireUI.getInstance().getDeviceManager().requestCommand(cmd).waitForResponse();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		reader.close();
	}
	
	private JPanel createSystemIO() {
		JPanel wrapPanel = JComponentFactory.createJFirePanel(this,"io");
		JPanel ioPanel = new JPanel();
		ioPanel.setLayout(new SpringLayout());
		
		ioPanel.add(new JCommandLabel(CommandName.dev_volt_dot));
		ioPanel.add(new JCommandComboBox(CommandName.dev_volt_dot));
		
		ioPanel.add(new JCommandLabel(CommandName.dev_amp_dot));
		ioPanel.add(new JCommandComboBox(CommandName.dev_amp_dot));
		
		ioPanel.add(new JCommandLabel(CommandName.dev_temp_dot));
		ioPanel.add(new JCommandComboBox(CommandName.dev_temp_dot));
		
		SpringLayoutGrid.makeCompactGrid(ioPanel,3,2);
		wrapPanel.add(ioPanel);
		return wrapPanel;
	}
	
	private JPanel createSystemLcd() {
		JPanel wrapPanel = JComponentFactory.createJFirePanel(this,"lcd");
		JPanel lcdPanel = new JPanel();
		lcdPanel.setLayout(new SpringLayout());
		
		lcdPanel.add(new JCommandLabel(CommandName.lcd_size));
		lcdPanel.add(new JCommandComboBox(CommandName.lcd_size));
		
		lcdPanel.add(new JCommandLabel(CommandName.lcd_defp));
		lcdPanel.add(new JCommandComboBox(CommandName.lcd_defp));
		
		lcdPanel.add(new JCommandLabel(CommandName.lcd_mode));
		lcdPanel.add(new JCommandComboBox(CommandName.lcd_mode));
		
		lcdPanel.add(new JCommandLabel(CommandName.lcd_plp));
		JPanel plpPanel = new JPanel();
		//plpPanel.setLayout(new GridLayout(4,0));
		lcdPanel.add(plpPanel);
		
		JFireQMapTable t = new JFireQMapTable(CommandName.lcd_plp);
		plpPanel.add(t);
		
		SpringLayoutGrid.makeCompactGrid(lcdPanel,4,2);
		wrapPanel.add(lcdPanel);
		return wrapPanel;
	}
	
	private JPanel createSystemWarmup() {
		JPanel wrapPanel = JComponentFactory.createJFirePanel(this,"warmup");
		wrapPanel.setLayout(new BoxLayout(wrapPanel,BoxLayout.PAGE_AXIS));
		
		JPanel warmDialPanel = new JPanel();
		warmDialPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		warmDialPanel.add(JComponentFactory.createJPanelJWrap(new JCommandDial(CommandName.swc_delay)));
		warmDialPanel.add(JComponentFactory.createJPanelJWrap(new JCommandDial(CommandName.swc_secs)));
		warmDialPanel.add(JComponentFactory.createJPanelJWrap(new JCommandDial(CommandName.swc_duty)));
		wrapPanel.add(warmDialPanel);
		
		JPanel warmPanel = new JPanel();
		warmPanel.add(new JFireQMapTable(CommandName.swc_map,"warmup","normal"));
		wrapPanel.add(warmPanel);
		
		return wrapPanel;
	}	

	@Override
	public Class<?> getTabClassName() {
		return this.getClass();
	}
}

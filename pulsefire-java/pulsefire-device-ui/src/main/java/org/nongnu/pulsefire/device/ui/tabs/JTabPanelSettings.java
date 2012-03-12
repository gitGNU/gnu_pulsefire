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
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.nongnu.pulsefire.device.ui.JComponentFactory;
import org.nongnu.pulsefire.device.ui.PulseFireUI;
import org.nongnu.pulsefire.device.ui.PulseFireUISettingKeys;
import org.nongnu.pulsefire.device.ui.PulseFireUISettingListener;
import org.nongnu.pulsefire.device.ui.PulseFireUISettingManager;
import org.nongnu.pulsefire.device.ui.SpringLayoutGrid;
import org.nongnu.pulsefire.device.ui.components.JCommandSettingListDialog;
import org.nongnu.pulsefire.device.ui.components.JFlashDialog;
import org.nongnu.pulsefire.wire.CommandName;
import org.nongnu.pulsefire.wire.CommandVariableType;

/**
 * JTabPanelSettings
 * 
 * @author Willem Cazander
 */
public class JTabPanelSettings extends AbstractFireTabPanel {

	private static final long serialVersionUID = -1646229038565969537L;
	private JButton burnButton = null;
	
	public JTabPanelSettings() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel wrap = new JPanel();
		wrap.setLayout(new SpringLayout());
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new SpringLayout());
		leftPanel.add(createSettingsLogFile(0));
		leftPanel.add(createSettingsLogFile(1));
		leftPanel.add(createSettingsLogFile(2));
		SpringLayoutGrid.makeCompactGrid(leftPanel,3,1);
		wrap.add(leftPanel);
		
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
		JPanel wrapRightPanel = new JPanel();
		wrapRightPanel.setLayout(new SpringLayout());
		wrapRightPanel.add(createFlashPanel());
		wrapRightPanel.add(createSettingsUI());
		SpringLayoutGrid.makeCompactGrid(wrapRightPanel,2,1);
		rightPanel.add(wrapRightPanel);
		wrap.add(rightPanel);
		
		SpringLayoutGrid.makeCompactGrid(wrap,1,2,0,0,0,0);
		add(wrap);
	}
	
	private JPanel createSettingsLogFile(int loggerId) {
		final String logId = "LOG"+loggerId+"_";
		final PulseFireUISettingManager config = PulseFireUI.getInstance().getSettingsManager();
		JPanel panel = JComponentFactory.createJFirePanel("Logger"+loggerId);
		
		panel.setLayout(new SpringLayout());
		
		panel.add(JComponentFactory.createJLabel("Enable"));
		panel.add(JComponentFactory.createSettingsJCheckBox(PulseFireUISettingKeys.valueOf(logId+"ENABLE")));
		panel.add(new JLabel());

		panel.add(JComponentFactory.createJLabel("Timestamp"));
		panel.add(JComponentFactory.createSettingsJCheckBox(PulseFireUISettingKeys.valueOf(logId+"TIMESTAMP")));
		panel.add(new JLabel());
		
		panel.add(JComponentFactory.createJLabel("Filename"));
		panel.add(JComponentFactory.createSettingsJTextField(PulseFireUISettingKeys.valueOf(logId+"FILENAME")));
		panel.add(new JLabel());
		
		panel.add(JComponentFactory.createJLabel("Path"));
		final JTextField cmdFile = new JTextField(25);
		cmdFile.setEnabled(false);
		cmdFile.setText(config.getSettingString(PulseFireUISettingKeys.valueOf(logId+"PATH")));
		panel.add(cmdFile);
		JButton cmdButton = new JButton("path");
		cmdButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = fc.showOpenDialog((JButton)e.getSource());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					cmdFile.setText(file.getAbsolutePath());
					config.setSettingString(PulseFireUISettingKeys.valueOf(logId+"PATH"), file.getAbsolutePath());
				}
			}
		});
		panel.add(cmdButton);
		
		panel.add(JComponentFactory.createJLabel("Speed"));
		panel.add(JComponentFactory.createSettingsJComboBox(PulseFireUISettingKeys.valueOf(logId+"SPEED"),new String[] {"1000","2000","5000","10000","30000","60000","120000",""+5*60*1000,""+15*60*1000,""+30*60*1000,""+60*60*1000}));
		panel.add(new JLabel());
		
		panel.add(JComponentFactory.createJLabel("Fields"));
		final JTextField logFields = new JTextField(25);
		logFields.setEnabled(false);
		List<CommandName> list = CommandName.decodeCommandList(config.getSettingString(PulseFireUISettingKeys.valueOf(logId+"FIELDS")));
		logFields.setText("columns: "+list.size());
		PulseFireUI.getInstance().getSettingsManager().addSettingListener(PulseFireUISettingKeys.valueOf(logId+"FIELDS"),new PulseFireUISettingListener() {
			@Override
			public void settingUpdated(PulseFireUISettingKeys key, String value) {
				List<CommandName> list = CommandName.decodeCommandList(value);
				logFields.setText("columns: "+list.size());
			}
		});
		panel.add(logFields);
		JButton pullButton = new JButton("Fields");
		pullButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
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
						"Select Logger Fields",
						"Select the fields to log to file.",
						PulseFireUISettingKeys.valueOf(logId+"FIELDS"),
						commands,commands);
				dialog.setVisible(true);
			}
		});
		panel.add(pullButton);

		SpringLayoutGrid.makeCompactGrid(panel,6,3);
		
		return panel;
	}
	
	private JPanel createSettingsUI() {
		JPanel panel = JComponentFactory.createJFirePanel("Interface");
		panel.setLayout(new SpringLayout());
		
		panel.add(JComponentFactory.createJLabel("UI Colors"));
		JComboBox colors = JComponentFactory.createSettingsJComboBox(PulseFireUISettingKeys.LAF_COLORS,new String[] {"dark-red","light-blue","black-white"});
		colors.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(PulseFireUI.getInstance().getMainFrame(), "This setting is activated on next run of application.","Requires restart",JOptionPane.INFORMATION_MESSAGE);
			}
		});
		panel.add(colors);
		
		panel.add(JComponentFactory.createJLabel("Limit Channels"));
		panel.add(JComponentFactory.createSettingsJCheckBox(PulseFireUISettingKeys.LIMIT_CHANNELS));
		
		panel.add(JComponentFactory.createJLabel("Auto Connect"));
		panel.add(JComponentFactory.createSettingsJCheckBox(PulseFireUISettingKeys.AUTO_CONNECT));
		
		panel.add(JComponentFactory.createJLabel("Console lines"));
		panel.add(JComponentFactory.createSettingsJComboBox(PulseFireUISettingKeys.CONSOLE_LINES,new String[] {"300","500","1000","2000","5000","10000","20000","50000"}));
		
		panel.add(JComponentFactory.createJLabel("Audio Scope"));
		panel.add(JComponentFactory.createSettingsJCheckBox(PulseFireUISettingKeys.SCOPE_ENABLE));
		
		panel.add(JComponentFactory.createJLabel("Pull Speed"));
		panel.add(JComponentFactory.createSettingsJComboBox(PulseFireUISettingKeys.PULL_SPEED,new String[] {"2000","3000","5000","10000","30000","60000","120000",""+5*60*1000,""+15*60*1000,""+30*60*1000,""+60*60*1000}));
		
		SpringLayoutGrid.makeCompactGrid(panel,6,2);
		return panel;
	}
	
	private JPanel createFlashPanel() {
		final PulseFireUISettingManager config = PulseFireUI.getInstance().getSettingsManager();
		JPanel panel = JComponentFactory.createJFirePanel("Flash");
		panel.setLayout(new SpringLayout());
		
		panel.add(JComponentFactory.createJLabel("Flash Chip"));
		burnButton = new JButton("Burn");
		burnButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFlashDialog flashDialog = new JFlashDialog(PulseFireUI.getInstance().getMainFrame());
				flashDialog.setVisible(true);
			}
		});
		panel.add(new JLabel());
		panel.add(burnButton);
		
		/*
		panel.add(JComponentFactory.createJLabel("Flash Chip"));
		JButton burnZipButton = new JButton("BurnZip");
		burnZipButton.setEnabled(false);
		panel.add(new JLabel());
		panel.add(burnZipButton);
		*/
		
		panel.add(JComponentFactory.createJLabel("avrdude cmd"));
		final JTextField avrdudeFile = new JTextField(25);
		avrdudeFile.setEnabled(false);
		avrdudeFile.setText(config.getSettingString(PulseFireUISettingKeys.AVRDUDE_CMD));
		panel.add(avrdudeFile);
		JButton avrdudeButton = new JButton("file");
		avrdudeButton.setToolTipText("Avrdude values are only needed if normal flash does not work or is to slow.");
		avrdudeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog((JButton)e.getSource());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					avrdudeFile.setText(file.getAbsolutePath());
					config.setSettingString(PulseFireUISettingKeys.AVRDUDE_CMD, file.getAbsolutePath());
					config.saveSettings();
				}
			}
		});
		panel.add(avrdudeButton);
		
		panel.add(JComponentFactory.createJLabel("avrdude conf"));
		final JTextField avrdudeConfFile = new JTextField(25);
		avrdudeConfFile.setEnabled(false);
		avrdudeConfFile.setText(config.getSettingString(PulseFireUISettingKeys.AVRDUDE_CONFIG));
		panel.add(avrdudeConfFile);
		JButton avrdudeConfButton = new JButton("file");
		avrdudeConfButton.setToolTipText("Avrdude values are only needed if normal flash does not work or is to slow.");
		avrdudeConfButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog((JButton)e.getSource());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					avrdudeConfFile.setText(file.getAbsolutePath());
					config.setSettingString(PulseFireUISettingKeys.AVRDUDE_CONFIG, file.getAbsolutePath());
					config.saveSettings();
				}
			}
		});
		panel.add(avrdudeConfButton);
		
		SpringLayoutGrid.makeCompactGrid(panel,3,3);
		
		return panel;
	}
	
	
	@Override
	public Class<?> getTabClassName() {
		return this.getClass();
	}
	
	@Override
	public void deviceConnect() {
		burnButton.setEnabled(false);
		super.deviceConnect();
	}

	@Override
	public void deviceDisconnect() {
		super.deviceDisconnect();
		burnButton.setEnabled(true);
	}
}

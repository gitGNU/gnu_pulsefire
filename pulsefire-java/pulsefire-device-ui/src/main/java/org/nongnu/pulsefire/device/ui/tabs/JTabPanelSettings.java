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

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.nongnu.pulsefire.device.ui.JComponentFactory;
import org.nongnu.pulsefire.device.ui.PulseFireUI;
import org.nongnu.pulsefire.device.ui.PulseFireUISettingKeys;
import org.nongnu.pulsefire.device.ui.SpringLayoutGrid;
import org.nongnu.pulsefire.device.ui.components.JFlashDialog;

/**
 * JTabPanelSettings
 * 
 * @author Willem Cazander
 */
public class JTabPanelSettings extends AbstractTabPanel {

	private static final long serialVersionUID = -1646229038565969537L;
	private JButton burnButton = null;
	
	public JTabPanelSettings() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel wrap = new JPanel();
		wrap.setLayout(new SpringLayout());
		wrap.add(JComponentFactory.createJPanelJWrap(createSettingsUI()));
		wrap.add(JComponentFactory.createJPanelJWrap(createSettingsLogFile()));
		SpringLayoutGrid.makeCompactGrid(wrap,1,2);
		add(wrap);
	}
	
	private JPanel createSettingsLogFile() {
		JPanel panel = JComponentFactory.createJFirePanel("Logging");
		
		panel.setLayout(new SpringLayout());
		
		panel.add(JComponentFactory.createJLabel("File append"));
		panel.add(JComponentFactory.createSettingsJCheckBox(PulseFireUISettingKeys.LOG_FILE_APPEND));
		panel.add(new JLabel());
		
		panel.add(JComponentFactory.createJLabel("cmd enable"));
		panel.add(JComponentFactory.createSettingsJCheckBox(PulseFireUISettingKeys.LOG_CMD_ENABLE));
		panel.add(new JLabel());
		
		panel.add(JComponentFactory.createJLabel("cmd rx"));
		panel.add(JComponentFactory.createSettingsJCheckBox(PulseFireUISettingKeys.LOG_CMD_RX));
		panel.add(new JLabel());
		
		panel.add(JComponentFactory.createJLabel("cmd tx"));
		panel.add(JComponentFactory.createSettingsJCheckBox(PulseFireUISettingKeys.LOG_CMD_TX));
		panel.add(new JLabel());
				
		panel.add(JComponentFactory.createJLabel("cmd file"));
		final JTextField cmdFile = new JTextField(35);
		cmdFile.setEnabled(false);
		cmdFile.setText(PulseFireUI.getInstance().getSettingString(PulseFireUISettingKeys.LOG_CMD_FILE));
		panel.add(cmdFile);
		JButton cmdButton = new JButton("file");
		cmdButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog((JButton)e.getSource());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					cmdFile.setText(file.getAbsolutePath());
					PulseFireUI.getInstance().getSettings().setProperty(PulseFireUISettingKeys.LOG_CMD_FILE.name(), file.getAbsolutePath());
				}
			}
		});
		panel.add(cmdButton);
		
		panel.add(JComponentFactory.createJLabel("pull enable"));
		panel.add(JComponentFactory.createSettingsJCheckBox(PulseFireUISettingKeys.LOG_PULL_ENABLE));
		panel.add(new JLabel());
		
		panel.add(JComponentFactory.createJLabel("pull file"));
		final JTextField pullFile = new JTextField(35);
		pullFile.setEnabled(false);
		pullFile.setText(PulseFireUI.getInstance().getSettingString(PulseFireUISettingKeys.LOG_PULL_FILE));
		panel.add(pullFile);
		JButton pullButton = new JButton("file");
		pullButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog((JButton)e.getSource());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					pullFile.setText(file.getAbsolutePath());
					PulseFireUI.getInstance().getSettings().setProperty(PulseFireUISettingKeys.LOG_PULL_FILE.name(), file.getAbsolutePath());
					PulseFireUI.getInstance().saveSettings();
				}
			}
		});
		panel.add(pullButton);
				
		
		panel.add(JComponentFactory.createJLabel("avrdude cmd"));
		final JTextField avrdudeFile = new JTextField(35);
		avrdudeFile.setEnabled(false);
		avrdudeFile.setText(PulseFireUI.getInstance().getSettingString(PulseFireUISettingKeys.AVRDUDE_CMD));
		panel.add(avrdudeFile);
		JButton avrdudeButton = new JButton("file");
		avrdudeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog((JButton)e.getSource());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					avrdudeFile.setText(file.getAbsolutePath());
					PulseFireUI.getInstance().getSettings().setProperty(PulseFireUISettingKeys.AVRDUDE_CMD.name(), file.getAbsolutePath());
					PulseFireUI.getInstance().saveSettings();
				}
			}
		});
		panel.add(avrdudeButton);
		
		panel.add(JComponentFactory.createJLabel("avrdude conf"));
		final JTextField avrdudeConfFile = new JTextField(35);
		avrdudeConfFile.setEnabled(false);
		avrdudeConfFile.setText(PulseFireUI.getInstance().getSettingString(PulseFireUISettingKeys.AVRDUDE_CONFIG));
		panel.add(avrdudeConfFile);
		JButton avrdudeConfButton = new JButton("file");
		avrdudeConfButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog((JButton)e.getSource());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					avrdudeConfFile.setText(file.getAbsolutePath());
					PulseFireUI.getInstance().getSettings().setProperty(PulseFireUISettingKeys.AVRDUDE_CONFIG.name(), file.getAbsolutePath());
					PulseFireUI.getInstance().saveSettings();
				}
			}
		});
		panel.add(avrdudeConfButton);
		
		
		SpringLayoutGrid.makeCompactGrid(panel,9,3);
		
		return panel;
	}
	
	private JPanel createSettingsUI() {
		
		JPanel panel = JComponentFactory.createJFirePanel("Interface");
		panel.setLayout(new SpringLayout());
		
		panel.add(JComponentFactory.createJLabel("NOTE"));
		panel.add(JComponentFactory.createJLabel("Change requires restart"));
		
		panel.add(JComponentFactory.createJLabel("UI Colors"));
		JComboBox colors = new JComboBox();
		colors.addItem("dark-red");
		colors.addItem("light-blue");
		colors.addItem("black-white");
		colors.setSelectedItem(PulseFireUI.getInstance().getSettingString(PulseFireUISettingKeys.LAF_COLORS));
		colors.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PulseFireUI.getInstance().getSettings().setProperty(PulseFireUISettingKeys.LAF_COLORS.name(), ""+((JComboBox)e.getSource()).getSelectedItem());
			}
		});
		panel.add(colors);
		
		panel.add(JComponentFactory.createJLabel("Limit Channels"));
		panel.add(JComponentFactory.createSettingsJCheckBox(PulseFireUISettingKeys.LIMIT_CHANNELS));
		
		panel.add(JComponentFactory.createJLabel("Auto Connect"));
		panel.add(JComponentFactory.createSettingsJCheckBox(PulseFireUISettingKeys.AUTO_CONNECT));
		
		panel.add(JComponentFactory.createJLabel("Console lines"));
		JComboBox consoleLines = new JComboBox();
		consoleLines.addItem("300");
		consoleLines.addItem("500");
		consoleLines.addItem("1000");
		consoleLines.addItem("2000");
		consoleLines.addItem("5000");
		consoleLines.addItem("10000");
		consoleLines.setSelectedItem(PulseFireUI.getInstance().getSettingString(PulseFireUISettingKeys.CONSOLE_LINES));
		consoleLines.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PulseFireUI.getInstance().getSettings().setProperty(PulseFireUISettingKeys.CONSOLE_LINES.name(), ""+((JComboBox)e.getSource()).getSelectedItem());
			}
		});
		panel.add(consoleLines);
		
		panel.add(JComponentFactory.createJLabel("Audio Scope"));
		panel.add(JComponentFactory.createSettingsJCheckBox(PulseFireUISettingKeys.SCOPE_ENABLE));

		panel.add(JComponentFactory.createJLabel("Flash Chip"));
		burnButton = new JButton("Burn");
		burnButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFlashDialog flashDialog = new JFlashDialog(PulseFireUI.getInstance().getMainFrame());
				flashDialog.pack();
				flashDialog.setLocationRelativeTo(PulseFireUI.getInstance().getMainFrame());
				flashDialog.setVisible(true);
			}
		});
		panel.add(burnButton);
		
		SpringLayoutGrid.makeCompactGrid(panel,7,2);
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

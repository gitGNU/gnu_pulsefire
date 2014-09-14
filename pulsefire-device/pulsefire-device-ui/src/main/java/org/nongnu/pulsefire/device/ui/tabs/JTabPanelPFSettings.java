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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.nongnu.pulsefire.device.io.transport.DeviceConnectListener;
import org.nongnu.pulsefire.device.ui.JComponentFactory;
import org.nongnu.pulsefire.device.ui.PulseFireUI;
import org.nongnu.pulsefire.device.ui.PulseFireUISettingKeys;
import org.nongnu.pulsefire.device.ui.PulseFireUISettingManager;
import org.nongnu.pulsefire.device.ui.SpringLayoutGrid;
import org.nongnu.pulsefire.device.ui.components.JFlashDialog;

/**
 * JTabPanelSettings
 * 
 * @author Willem Cazander
 */
public class JTabPanelPFSettings extends AbstractFireTabPanel {

	private JButton burnButton = null;
	
	public JTabPanelPFSettings() {
		JPanel wrap = new JPanel();
		wrap.setLayout(new SpringLayout());
		
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
		JPanel wrapRightPanel = new JPanel();
		wrapRightPanel.setLayout(new SpringLayout());
		wrapRightPanel.add(createFlashPanel());
		wrapRightPanel.add(createSettingsUI());
		SpringLayoutGrid.makeCompactGrid(wrapRightPanel,2,1);
		rightPanel.add(wrapRightPanel);
		wrap.add(rightPanel);
		
		SpringLayoutGrid.makeCompactGrid(wrap,1,1,0,0,0,0);
		getJPanel().add(wrap);
		
		PulseFireUI.getInstance().getDeviceManager().addDeviceConnectListener(new DeviceConnectListener() {
			
			@Override
			public void deviceDisconnect() {
				burnButton.setEnabled(true);
			}
			
			@Override
			public void deviceConnect() {
				burnButton.setEnabled(false);
			}
		});
	}
	
	private JPanel createSettingsUI() {
		JPanel panel = JComponentFactory.createJFirePanel("Interface");
		panel.setLayout(new SpringLayout());
		
		panel.add(JComponentFactory.createJLabel("UI Colors"));
		JComboBox<String> colors = JComponentFactory.createSettingsJComboBox(PulseFireUISettingKeys.LAF_COLORS,new String[] {"dark-red","yellow-purple","light-blue","black-white"});
		colors.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(PulseFireUI.getInstance().getMainFrame(), "This setting is activated on next run of application.","Requires restart",JOptionPane.INFORMATION_MESSAGE);
			}
		});
		panel.add(colors);
		
		panel.add(JComponentFactory.createJLabel("Auto Connect"));
		panel.add(JComponentFactory.createSettingsJCheckBox(PulseFireUISettingKeys.AUTO_CONNECT));
		
		panel.add(JComponentFactory.createJLabel("Console lines"));
		panel.add(JComponentFactory.createSettingsJComboBox(PulseFireUISettingKeys.CONSOLE_LINES,new String[] {"300","500","1000","2000","5000","10000","20000","50000"}));
		
		panel.add(JComponentFactory.createJLabel("Tab UILog"));
		panel.add(JComponentFactory.createSettingsJCheckBox(PulseFireUISettingKeys.TAB_UILOG_ENABLE));
		
		panel.add(JComponentFactory.createJLabel("Tab LPM"));
		panel.add(JComponentFactory.createSettingsJCheckBox(PulseFireUISettingKeys.TAB_LPM_ENABLE));
		
		panel.add(JComponentFactory.createJLabel("Pull Speed"));
		panel.add(JComponentFactory.createSettingsJComboBox(PulseFireUISettingKeys.PULL_SPEED,new String[] {"250","500","1000","2000","3000","5000","10000","30000","60000","120000",""+5*60*1000,""+15*60*1000,""+30*60*1000,""+60*60*1000}));
		
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
}

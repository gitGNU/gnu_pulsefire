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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.nongnu.pulsefire.device.io.protocol.CommandName;
import org.nongnu.pulsefire.device.io.protocol.CommandVariableType;
import org.nongnu.pulsefire.device.ui.JComponentFactory;
import org.nongnu.pulsefire.device.ui.PulseFireUI;
import org.nongnu.pulsefire.device.ui.PulseFireUISettingKeys;
import org.nongnu.pulsefire.device.ui.PulseFireUISettingListener;
import org.nongnu.pulsefire.device.ui.PulseFireUISettingManager;
import org.nongnu.pulsefire.device.ui.SpringLayoutGrid;
import org.nongnu.pulsefire.device.ui.components.JCommandSettingListDialog;

/**
 * JTabPanelDataLog
 * 
 * @author Willem Cazander
 */
public class JTabPanelPFDataLog extends AbstractFireTabPanel {
	
	public JTabPanelPFDataLog() {
		JPanel wrap = new JPanel();
		wrap.setLayout(new SpringLayout());
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new SpringLayout());
		leftPanel.add(createSettingsLogFile(0));
		leftPanel.add(createSettingsLogFile(1));
		SpringLayoutGrid.makeCompactGrid(leftPanel,1,2);
		wrap.add(leftPanel);
		
		SpringLayoutGrid.makeCompactGrid(wrap,1,1,0,0,0,0);
		getJPanel().add(wrap);
	}
	
	private JPanel createSettingsLogFile(int loggerId) {
		final String logId = "LOG"+loggerId+"_";
		final PulseFireUISettingManager config = PulseFireUI.getInstance().getSettingsManager();
		JPanel panel = JComponentFactory.createJFirePanel("File Logger "+loggerId);
		
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
}

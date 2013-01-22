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

package org.nongnu.pulsefire.device.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import org.nongnu.pulsefire.device.DeviceConnectListener;
import org.nongnu.pulsefire.device.DeviceDataListener;
import org.nongnu.pulsefire.wire.Command;
import org.nongnu.pulsefire.wire.CommandWire;

/**
 * JPanelConsole
 * 
 * @author Willem Cazander
 */
public class JPanelConsole extends JPanel implements DeviceDataListener,DeviceConnectListener,ActionListener,PulseFireUISettingListener {

	private static final long serialVersionUID = -8155913876470234844L;
	private JTextArea consoleLog = null;
	private JTextField consoleInput = null;
	private int consoleLogLinesMax = 255;
	private DateFormat timeFormat = null;
	
	public JPanelConsole() {
		// Use simple time based format for console logging
		timeFormat = new SimpleDateFormat("HH:mm:ss");
		consoleLogLinesMax = new Integer(PulseFireUI.getInstance().getSettingsManager().getSettingString(PulseFireUISettingKeys.CONSOLE_LINES));
		// Config panel and inner panel
		setLayout(new GridLayout(1,1)); // take max size
		setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		JPanel innerPanel = JComponentFactory.createJFirePanel("Console");
		innerPanel.setLayout(new BorderLayout());
		add(innerPanel);
		
		// Config console output
		consoleLog = new JTextArea(10,50);
		consoleLog.setMargin(new Insets(2, 2, 2, 2));
		consoleLog.setAutoscrolls(true);
		consoleLog.setEditable(false);
		JScrollPane consoleScrollPane = new JScrollPane(consoleLog);
		consoleScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		consoleScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		consoleScrollPane.getViewport().setOpaque(false);
		innerPanel.add(consoleScrollPane,BorderLayout.CENTER);
		
		// Config console input
		consoleInput = new JTextField(25);
		consoleInput.setMargin(new Insets(5, 5, 5, 5));
		consoleInput.addActionListener(this);
		consoleInput.setEnabled(false);
		JPanel consoleActionPanel = new JPanel();
		consoleActionPanel.setLayout(new BorderLayout());
		consoleActionPanel.add(consoleInput,BorderLayout.LINE_START);
		JButton consoleClear = new JButton("Clear");
		consoleClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				synchronized (consoleLog) {
					consoleLog.setText("");
				}
			}
		});
		consoleActionPanel.add(consoleClear,BorderLayout.LINE_END);
		innerPanel.add(consoleActionPanel,BorderLayout.SOUTH);
		
		PulseFireUI.getInstance().getDeviceManager().addDeviceDataListener(this);
		PulseFireUI.getInstance().getDeviceManager().addDeviceConnectListener(this);
		PulseFireUI.getInstance().getSettingsManager().addSettingListener(PulseFireUISettingKeys.CONSOLE_LINES,this);
		
		updateText("Ready to connect.","##");
	}

	@Override
	public void deviceDataReceived(String data) {
		updateText(data,"");
	}

	@Override
	public void deviceDataSend(String data) {
		updateText(data,">>");
	}
	
	private void updateText(String data,String prefix) {
		synchronized (consoleLog) {
			consoleLog.append(timeFormat.format(new Date()));
			consoleLog.append(" ");
			consoleLog.append(prefix);
			consoleLog.append(" ");
			consoleLog.append(data);
			consoleLog.append("\n");
			
			if (consoleLog.getLineCount() > consoleLogLinesMax) {
				String t = consoleLog.getText();
				int l = 0;
				int rm = consoleLogLinesMax/2;
				for (int i=0;i<rm;i++) {
					int ll = t.indexOf('\n',l+1);
					if (ll==-1) {
						break;
					}
					l = ll;
				}
				String tt = t.substring(l,t.length());
				consoleLog.setText(tt);
			}
			
			consoleLog.repaint();
			consoleLog.setCaretPosition(consoleLog.getDocument().getLength()); // auto scroll to end
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (consoleInput.getText().isEmpty()) {
			return;
		}
		try {
			Command cmd = CommandWire.decodeCommand(consoleInput.getText());
			PulseFireUI.getInstance().getDeviceManager().requestCommand(cmd);
		} catch (Exception e) {
			updateText(e.getMessage(),"## Err:");
		}
		consoleInput.setText("");
	}

	@Override
	public void deviceConnect() {
		consoleInput.setEnabled(true);
		updateText("Connected succesfully.","##");
	}

	@Override
	public void deviceDisconnect() {
		consoleInput.setEnabled(false);
		updateText("Closed connection succesfully.","##");
		updateText("Ready to connect.","##");
	}
	
	public void settingUpdated(PulseFireUISettingKeys key,String value) {
		consoleLogLinesMax = new Integer(value);
		synchronized (consoleLog) {
			consoleLog.setText("");
		}
	}
}

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

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

import org.nongnu.pulsefire.device.io.protocol.Command;
import org.nongnu.pulsefire.device.io.protocol.CommandName;
import org.nongnu.pulsefire.device.io.transport.DeviceConnectListener;
import org.nongnu.pulsefire.device.io.transport.DeviceData;
import org.nongnu.pulsefire.device.ui.time.EventTimeTrigger;

/**
 * JTopPanelPPM
 * 
 * @author Willem Cazander
 */
public class JTopPanelStatus extends JPanel {

	private static final long serialVersionUID = 94571180445561814L;
	private final DeviceData deviceData;
	private JLabel adc1Label = null;
	private JLabel adc2Label = null;
	private JLabel dicLabel = null;
	private JLabel docLabel = null;

	public JTopPanelStatus() {
		deviceData = PulseFireUI.getInstance().getDeviceManager().getDeviceData();
		
		setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		setLayout(new GridLayout(1,0));
		JPanel borderPanel = JComponentFactory.createJFirePanel("Status");
		add(borderPanel);
		
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new SpringLayout());
		borderPanel.add(inputPanel);
		
		inputPanel.add(new JLabel("Analog-In:"));
		adc1Label = new JLabel();
		inputPanel.add(adc1Label);

		inputPanel.add(new JLabel("Analog-In:"));
		adc2Label = new JLabel();
		inputPanel.add(adc2Label);
		
		inputPanel.add(new JLabel("Digital-In:"));
		dicLabel = new JLabel();
		inputPanel.add(dicLabel);
		
		inputPanel.add(new JLabel("Digital-Out:"));
		docLabel = new JLabel();
		inputPanel.add(docLabel);
		
		SpringLayoutGrid.makeCompactGrid(inputPanel,4,2);
		
		PulseFireUI.getInstance().getEventTimeManager().addEventTimeTriggerConnected(new EventTimeTrigger("AutoUpdateStatus",new AutoUpdateStatus(),100));
		PulseFireUI.getInstance().getDeviceManager().addDeviceConnectListener(new DeviceConnectListener() {
			@Override
			public void deviceDisconnect() {
				adc1Label.setText("");
				adc2Label.setText("");
				dicLabel.setText("");
				docLabel.setText("");
			}
			@Override
			public void deviceConnect() {
				// Fix for missing redraw event which happens sometimes on first application boot and connect;
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						SwingUtilities.updateComponentTreeUI(JTopPanelStatus.this);
					}
				});
			}
		});
	}
	
	class AutoUpdateStatus implements Runnable {
		@Override
		public void run() {
			updateAdcLabels();
			updateDicLabel();
			updateDocLabel();
		}
	}
	
	private void updateAdcLabels() {
		StringBuilder buf = new StringBuilder(100);
		int s = CommandName.adc_value.getMaxIndexA();
		if (s>7) {
			s = 7;
		}
		for (int i=s;i>=0;i--) {
			Command cmd = deviceData.getDeviceParameterIndexed(CommandName.adc_value, i);
			if (cmd==null) {
				continue;
			}
			buf.append('A');
			buf.append('0');
			buf.append(i);
			buf.append(": ");
			String adcValue = cmd.getArgu0();
			if (adcValue.length()==3) {
				buf.append('0');
			} else if (adcValue.length()==2) {
				buf.append("00");
			} else if (adcValue.length()==1) {
				buf.append("000");
			}
			buf.append(adcValue);
			buf.append("  ");
		}
		adc1Label.setText(buf.toString());
		buf = new StringBuilder(100);
		if (CommandName.adc_value.getMaxIndexA()>7) {
			for (int i=CommandName.adc_value.getMaxIndexA();i>=8;i--) {
				Command cmd = deviceData.getDeviceParameterIndexed(CommandName.adc_value, i);
				if (cmd==null) {
					continue;
				}
				buf.append('A');
				if (i<10) {
					buf.append('0');
				}
				buf.append(i);
				buf.append(": ");
				String adcValue = cmd.getArgu0();
				if (adcValue.length()==3) {
					buf.append('0');
				} else if (adcValue.length()==2) {
					buf.append("00");
				} else if (adcValue.length()==1) {
					buf.append("000");
				}
				buf.append(adcValue);
				buf.append("  ");
			}
		}
		adc2Label.setText(buf.toString());
	}
	
	private void updateDicLabel() {
		Command cmd = deviceData.getDeviceParameter(CommandName.dic_value);
		if (cmd==null) {
			return;
		}
		StringBuilder buf = new StringBuilder(100);
		buf.append("0b");
		for (int i=15;i>=0;i--) {
			int value = new Integer(cmd.getArgu0());
			int result = (value >> i) & 1;
			buf.append(result);
		}
		buf.append(" (15-0)");
		dicLabel.setText(buf.toString());
	}
	
	private void updateDocLabel() {
		StringBuilder buf = new StringBuilder(100);
		buf.append("0b");
		for (int i=15;i>=0;i--) {
			Command cmd = deviceData.getDeviceParameterIndexed(CommandName.doc_port, i);
			if (cmd==null) {
				continue;
			}
			buf.append(cmd.getArgu0());
		}
		buf.append(" (15-0)");
		docLabel.setText(buf.toString());
	}
}

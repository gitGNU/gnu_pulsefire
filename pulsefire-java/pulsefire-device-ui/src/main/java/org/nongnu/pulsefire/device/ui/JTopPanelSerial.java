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

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

import org.nongnu.pulsefire.device.DeviceConnectListener;
import org.nongnu.pulsefire.device.DeviceDataListener;
import org.nongnu.pulsefire.device.ui.components.JConnectDialog;

/**
 * JTopPanelSerial
 * 
 * @author Willem Cazander
 */
public class JTopPanelSerial extends JPanel implements ActionListener,DeviceConnectListener,DeviceDataListener {

	private static final long serialVersionUID = -6521267550228492042L;
	private JButton connectButtton = null;
	private JComboBox portsComboBox = null;
	private JLabel versionLabel = null;
	private JConnectDialog connectDialog = null;
	private JLabel dataTxCounter = null;
	private JLabel dataRxCounter = null;
	private JLabel dataRxsCounter = null;
	private JLabel errorCounter = null;
	private JLabel cmdTxCounter = null;
	private JLabel cmdRxCounter = null;
	private JLabel cmdRxsCounter = null;
	private long txBytes = 0;
	private long rxBytes = 0;
	private long rxBytesSpeed = 0;
	private long rxBytesLast = 0;
	private long rxCmdSpeed = 0;
	private long rxCmdLast = 0;
	private long rxSpeedTime = 0;
	
	public JTopPanelSerial() {
		setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		setLayout(new GridLayout(1,0));
		JPanel borderPanel = JComponentFactory.createJFirePanel(this,"serial");
		add(borderPanel);
		
		JPanel serialPanel = new JPanel();
		serialPanel.setLayout(new SpringLayout());
		borderPanel.add(serialPanel);
		
		serialPanel.add(JComponentFactory.createJLabel(this, "ports"));
		DevicePortsComboBoxModel portModel = new DevicePortsComboBoxModel();
		portsComboBox = new JComboBox(portModel);
		portsComboBox.addPopupMenuListener(portModel);
		serialPanel.add(portsComboBox);
		
		serialPanel.add(JComponentFactory.createJLabel(this, "connect"));
		connectButtton = new JButton("Connect");
		connectButtton.addActionListener(this);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				connectButtton.requestFocusInWindow();
			}
		});
		
		serialPanel.add(connectButtton);
		SpringLayoutGrid.makeCompactGrid(serialPanel,2,2);
		
		JPanel serialInfoPanel = new JPanel();
		serialInfoPanel.setLayout(new SpringLayout());
		borderPanel.add(serialInfoPanel);
		serialInfoPanel.add(JComponentFactory.createJLabel(this,"version"));
		versionLabel = new JLabel("");
		serialInfoPanel.add(versionLabel);
		dataTxCounter = new JLabel();
		dataTxCounter.setPreferredSize(new Dimension(80,15));
		serialInfoPanel.add(JComponentFactory.createJLabel(this,"dataTxCounter"));
		serialInfoPanel.add(dataTxCounter);
		dataRxCounter = new JLabel();
		dataRxCounter.setPreferredSize(new Dimension(80,15)); // Removes screen update jitter on the right side of the label
		serialInfoPanel.add(JComponentFactory.createJLabel(this,"dataRxCounter"));
		serialInfoPanel.add(dataRxCounter);
		dataRxsCounter = new JLabel();
		dataRxsCounter.setPreferredSize(new Dimension(80,15)); // Removes screen update jitter on the right side of the label
		serialInfoPanel.add(JComponentFactory.createJLabel(this,"dataRxsCounter"));
		serialInfoPanel.add(dataRxsCounter);
		SpringLayoutGrid.makeCompactGrid(serialInfoPanel,4,2);
		
		JPanel countInfoPanel = new JPanel();
		countInfoPanel.setLayout(new SpringLayout());
		borderPanel.add(countInfoPanel);
		errorCounter = new JLabel();
		countInfoPanel.add(JComponentFactory.createJLabel(this,"errorCounter"));
		countInfoPanel.add(errorCounter);
		cmdTxCounter = new JLabel();
		countInfoPanel.add(JComponentFactory.createJLabel(this,"cmdTxCounter"));
		countInfoPanel.add(cmdTxCounter);
		cmdRxCounter = new JLabel();
		countInfoPanel.add(JComponentFactory.createJLabel(this,"cmdRxCounter"));
		countInfoPanel.add(cmdRxCounter);
		cmdRxsCounter = new JLabel();
		countInfoPanel.add(JComponentFactory.createJLabel(this,"cmdRxsCounter"));
		countInfoPanel.add(cmdRxsCounter);
		SpringLayoutGrid.makeCompactGrid(countInfoPanel,4,2);
		
		PulseFireUI.getInstance().getDeviceManager().addDeviceConnectListener(this);
		PulseFireUI.getInstance().getDeviceManager().addDeviceDataListener(this);
		updateCounters();
	}

	public void autoConnect() {
		Boolean autoConnect = PulseFireUI.getInstance().getSettingsManager().getSettingBoolean(PulseFireUISettingKeys.AUTO_CONNECT);
		String devicePort = PulseFireUI.getInstance().getSettingsManager().getSettingString(PulseFireUISettingKeys.DEVICE_PORT);
		if (autoConnect && devicePort.isEmpty()==false) {
			// check if port is there
			for (int i=0;i<portsComboBox.getModel().getSize();i++) {
				String port = (String)portsComboBox.getModel().getElementAt(i);
				if (port.equals(devicePort)) {
					portsComboBox.setSelectedIndex(i);
					SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
							try {
								actionPerformed(null);
							} catch (Exception e) {
								Logger.getAnonymousLogger().log(Level.WARNING,"Error in auto-connect: "+e.getMessage(),e);
							}
						}
					});
					break;
				}
			}
			
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String port = portsComboBox.getSelectedItem().toString();
		if (portsComboBox.isEnabled()) {
			if (connectDialog!=null && connectDialog.isVisible()==false) {
				connectDialog = null; // reset when is closed.
			}
			if (connectDialog==null) {
				PulseFireUI.getInstance().getSettingsManager().setSettingString(PulseFireUISettingKeys.DEVICE_PORT,port);
				connectDialog = new JConnectDialog(PulseFireUI.getInstance().getMainFrame(),port);
			}
		} else {
			PulseFireUI.getInstance().getDeviceManager().disconnect(false);
		}
	}

	@Override
	public void deviceConnect() {
		connectDialog = null;
		portsComboBox.setEnabled(false);
		connectButtton.setText("Disconnect");
		versionLabel.setText(""+new Float(PulseFireUI.getInstance().getDeviceManager().getDeviceVersion())/10);
	}

	@Override
	public void deviceDisconnect() {
		portsComboBox.setEnabled(true);
		connectButtton.setText("Connect");
		versionLabel.setText("");
		txBytes = 0;
		rxBytes = 0;
		rxBytesSpeed = 0;
		rxCmdSpeed = 0;
		updateCounters();
		updateSpeedCounters();
	}

	@Override
	public void deviceDataSend(String data) {
		txBytes+=data.length();
		updateCounters();
	}

	@Override
	public void deviceDataReceived(String data) {
		rxBytes+=data.length();
		updateCounters();
	}
	
	public void updateSpeedCounters() {
		long rxCmdTotal = PulseFireUI.getInstance().getDeviceManager().getTotalCmdRx();
		long currentTime = System.currentTimeMillis();
		if (currentTime > rxSpeedTime) {
			rxSpeedTime = currentTime + 1000;
			if (rxCmdTotal > 0) {
				rxCmdSpeed = rxCmdTotal-rxCmdLast;
				rxBytesSpeed = rxBytes - rxBytesLast;
			}
			rxCmdLast = rxCmdTotal;
			rxBytesLast = rxBytes;
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				dataRxsCounter.setText(printNiceBytes(rxBytesSpeed));
				dataRxsCounter.repaint();
				cmdRxsCounter.setText(""+rxCmdSpeed);
				cmdRxsCounter.repaint();
			}
		});
	}
	
	private void updateCounters() {
		dataTxCounter.setText(printNiceBytes(txBytes));
		dataTxCounter.repaint();
		dataRxCounter.setText(printNiceBytes(rxBytes));
		dataRxCounter.repaint();

		errorCounter.setText(""+PulseFireUI.getInstance().getDeviceManager().getTotalErrors());
		errorCounter.repaint();
		cmdTxCounter.setText(""+PulseFireUI.getInstance().getDeviceManager().getTotalCmdTx());
		cmdTxCounter.repaint();
		cmdRxCounter.setText(""+PulseFireUI.getInstance().getDeviceManager().getTotalCmdRx());
		cmdRxCounter.repaint();

	}
	
	private String printNiceBytes(long bytes) {
		String byteSize = "B";
		String byteDotSize = "";
		if (bytes>1024) {
			byteSize = "KB";
			byteDotSize = ""+bytes%1024;
			bytes = bytes/1024;
		}
		if (bytes>1024) {
			byteSize = "MB";
			byteDotSize = ""+bytes%1024;
			bytes = bytes/1024;
		}
		if (bytes>1024) {
			byteSize = "GB";
			byteDotSize = ""+bytes%1024;
			bytes = bytes/1024;
		}
		if (byteDotSize.length()==2) {
			byteDotSize = ".0"+byteDotSize;
		} else if (byteDotSize.length()==1) {
			byteDotSize = ".00"+byteDotSize;
		} else if (byteDotSize.length()==0) {
			byteDotSize = "";
		} else {
			byteDotSize = "."+byteDotSize;
		}
		
		StringBuffer buf = new StringBuffer(10);
		buf.append(bytes);
		buf.append(byteDotSize);
		buf.append(' ');
		buf.append(byteSize);
		return buf.toString();
	}
}

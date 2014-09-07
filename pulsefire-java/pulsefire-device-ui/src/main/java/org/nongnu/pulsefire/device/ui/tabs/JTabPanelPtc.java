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

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.nongnu.pulsefire.device.DeviceCommandListener;
import org.nongnu.pulsefire.device.DeviceData;
import org.nongnu.pulsefire.device.DeviceWireManager;
import org.nongnu.pulsefire.device.ui.JComponentFactory;
import org.nongnu.pulsefire.device.ui.PulseFireUI;
import org.nongnu.pulsefire.device.ui.SpringLayoutGrid;
import org.nongnu.pulsefire.device.ui.components.JCommandDial;
import org.nongnu.pulsefire.device.ui.components.JCommandLabel;
import org.nongnu.pulsefire.wire.Command;
import org.nongnu.pulsefire.wire.CommandName;

/**
 * JTabPanelPTC
 * 
 * @author Willem Cazander
 */
public class JTabPanelPtc extends AbstractFireTabPanel implements DeviceCommandListener {
	
	private JLabel statusLabelTimer0 = null;
	private JLabel statusLabelTimer1 = null;
	
	public JTabPanelPtc() {
		JPanel wrap = new JPanel();
		wrap.setLayout(new SpringLayout());
		wrap.add(createTimeConfPanel0());
		wrap.add(createTimeConfPanel1());
		wrap.add(JComponentFactory.createJFirePanelQMapTable(this, "timer0", CommandName.ptc_0map,"value","time"));
		wrap.add(JComponentFactory.createJFirePanelQMapTable(this, "timer1", CommandName.ptc_1map,"value","time"));
		SpringLayoutGrid.makeCompactGrid(wrap,2,2);
		getJPanel().add(wrap);
		DeviceWireManager deviceManager = PulseFireUI.getInstance().getDeviceManager();
		deviceManager.addDeviceCommandListener(CommandName.info_data, this); // we go on pull, todo change after all push
	}
	
	private JPanel createTimeConfPanel0() {
		JPanel panel = JComponentFactory.createJFirePanel(this,"config0");
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(new JCommandLabel(CommandName.ptc_0run));
		panel.add(new JCommandDial(CommandName.ptc_0run));
		panel.add(new JCommandLabel(CommandName.ptc_0mul));
		panel.add(new JCommandDial(CommandName.ptc_0mul));
		statusLabelTimer0 = new JLabel();
		panel.add(statusLabelTimer0);
		return panel;
	}
	
	private JPanel createTimeConfPanel1() {
		JPanel panel = JComponentFactory.createJFirePanel(this,"config1");
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(new JCommandLabel(CommandName.ptc_1run));
		panel.add(new JCommandDial(CommandName.ptc_1run));
		panel.add(new JCommandLabel(CommandName.ptc_1mul));
		panel.add(new JCommandDial(CommandName.ptc_1mul));
		statusLabelTimer1 = new JLabel();
		panel.add(statusLabelTimer1);
		return panel;
	}
	
	@Override
	public void commandReceived(Command command) {
		DeviceData deviceData = PulseFireUI.getInstance().getDeviceManager().getDeviceData();
		Command ptc0Cnt = deviceData.getDeviceParameter(CommandName.ptc_0cnt);
		Command ptc1Cnt = deviceData.getDeviceParameter(CommandName.ptc_1cnt);
		if (ptc1Cnt==null || ptc1Cnt==null) {
			return;
		}
		StringBuilder buf = new StringBuilder(100);
		buf.append(CommandName.ptc_0cnt.name());
		buf.append(": ");
		buf.append(ptc0Cnt.getArgu0());
		statusLabelTimer0.setText(buf.toString());
		
		buf = new StringBuilder(100);
		buf.append(CommandName.ptc_1cnt.name());
		buf.append(": ");
		buf.append(ptc1Cnt.getArgu0());
		statusLabelTimer1.setText(buf.toString());
	}
}

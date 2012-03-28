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
import org.nongnu.pulsefire.device.ui.components.JCommandButton;
import org.nongnu.pulsefire.device.ui.components.JFireQMapTable;
import org.nongnu.pulsefire.wire.Command;
import org.nongnu.pulsefire.wire.CommandName;

/**
 * JTabPanelPTT
 * 
 * @author Willem Cazander
 */
public class JTabPanelPtt extends AbstractFireTabPanel implements DeviceCommandListener {

	private static final long serialVersionUID = 2716662787208065889L;
	private JLabel runLabel = null;
	private JLabel runStepLabel = null;

	public JTabPanelPtt() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel wrap = new JPanel();
		wrap.setLayout(new SpringLayout());
		
		wrap.add(createTriggerStatusPanel());
		wrap.add(createTriggerTestPanel());
		
		JPanel trig0Panel = JComponentFactory.createJFirePanel(this,"ptt0");
		trig0Panel.add(new JFireQMapTable(CommandName.ptt_0map,"value","time"));
		wrap.add(trig0Panel);
		
		JPanel trig1Panel = JComponentFactory.createJFirePanel(this,"ptt1");
		trig1Panel.add(new JFireQMapTable(CommandName.ptt_1map,"value","time"));
		wrap.add(trig1Panel);

		JPanel trig2Panel = JComponentFactory.createJFirePanel(this,"ptt2");
		trig2Panel.add(new JFireQMapTable(CommandName.ptt_2map,"value","time"));
		wrap.add(trig2Panel);
		
		JPanel trig3Panel = JComponentFactory.createJFirePanel(this,"ptt3");
		trig3Panel.add(new JFireQMapTable(CommandName.ptt_3map,"value","time"));
		wrap.add(trig3Panel);
		
		SpringLayoutGrid.makeCompactGrid(wrap,3,2);
		add(wrap);
		
		DeviceWireManager deviceManager = PulseFireUI.getInstance().getDeviceManager();
		deviceManager.addDeviceCommandListener(CommandName.info_data, this);
	}
	
	private JPanel createTriggerStatusPanel() {
		JPanel header = JComponentFactory.createJFirePanel(this,"pttStatus");
		JPanel wrap = new JPanel();
		wrap.setLayout(new SpringLayout());
		
		wrap.add(new JLabel("Run:"));
		runLabel = new JLabel();
		wrap.add(runLabel);
		
		wrap.add(new JLabel("Step:"));
		runStepLabel = new JLabel();
		wrap.add(runStepLabel);
		
		SpringLayoutGrid.makeCompactGrid(wrap,2,2);
		header.add(wrap);
		return header;
	}
	
	private JPanel createTriggerTestPanel() {
		JPanel header = JComponentFactory.createJFirePanel(this,"pttTrigger");
		JPanel wrap = new JPanel();
		wrap.add(new JLabel("Fire: "));
		wrap.add(new JCommandButton(CommandName.req_ptt_fire,0));
		wrap.add(new JCommandButton(CommandName.req_ptt_fire,1));
		wrap.add(new JCommandButton(CommandName.req_ptt_fire,2));
		wrap.add(new JCommandButton(CommandName.req_ptt_fire,3));		
		header.add(wrap);
		return header;
	}
	
	@Override
	public Class<?> getTabClassName() {
		return this.getClass();
	}

	@Override
	public void commandReceived(Command command) {
		DeviceData deviceData = PulseFireUI.getInstance().getDeviceManager().getDeviceData();
		StringBuilder buf = new StringBuilder(100);
		for (int i=CommandName.ptt_cnt.getMaxIndexA()-1;i>=0;i--) {
			Command cmd = deviceData.getDeviceParameterIndexed(CommandName.ptt_cnt, i);
			int value = -1;
			if (cmd!=null) {
				value = new Integer(cmd.getArgu0());
			}
			buf.append(CommandName.ptt_cnt.name());
			buf.append(i);
			buf.append(": ");
			buf.append(value);
			buf.append(" ");
		}
		runLabel.setText(buf.toString());

		buf = new StringBuilder(100);
		for (int i=CommandName.ptt_idx.getMaxIndexA()-1;i>=0;i--) {
			Command cmd = deviceData.getDeviceParameterIndexed(CommandName.ptt_idx, i);
			int value = -1;
			if (cmd!=null) {
				value = new Integer(cmd.getArgu0());
			}
			buf.append(CommandName.ptt_idx.name());
			buf.append(i);
			buf.append(": ");
			buf.append(value);
			buf.append(" ");
		}
		runStepLabel.setText(buf.toString());
	}
}

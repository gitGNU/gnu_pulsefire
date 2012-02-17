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

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.nongnu.pulsefire.device.DeviceCommandListener;
import org.nongnu.pulsefire.device.DeviceData;
import org.nongnu.pulsefire.device.DeviceWireManager;
import org.nongnu.pulsefire.device.ui.JComponentFactory;
import org.nongnu.pulsefire.device.ui.PulseFireUI;
import org.nongnu.pulsefire.device.ui.SpringLayoutGrid;
import org.nongnu.pulsefire.device.ui.components.JFireQMapTable;
import org.nongnu.pulsefire.wire.Command;
import org.nongnu.pulsefire.wire.CommandName;

/**
 * JTabPanelPTT
 * 
 * @author Willem Cazander
 */
public class JTabPanelPTT extends AbstractTabPanel implements DeviceCommandListener {

	private static final long serialVersionUID = 2716662787208065889L;
	private JLabel runLabel = null;
	private JLabel runStepLabel = null;

	public JTabPanelPTT() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel wrap = new JPanel();
		wrap.setLayout(new SpringLayout());
		
		wrap.add(createTriggerStatusPanel());
		wrap.add(createTriggerTestPanel());
		
		JPanel trig0Panel = JComponentFactory.createJFirePanel("Trigger0");
		trig0Panel.add(new JFireQMapTable(CommandName.ptt_0map,"value","time"));
		wrap.add(trig0Panel);
		
		JPanel trig1Panel = JComponentFactory.createJFirePanel("Trigger1");
		trig1Panel.add(new JFireQMapTable(CommandName.ptt_1map,"value","time"));
		wrap.add(trig1Panel);

		JPanel trig2Panel = JComponentFactory.createJFirePanel("Trigger2");
		trig2Panel.add(new JFireQMapTable(CommandName.ptt_2map,"value","time"));
		wrap.add(trig2Panel);
		
		JPanel trig3Panel = JComponentFactory.createJFirePanel("Trigger3");
		trig3Panel.add(new JFireQMapTable(CommandName.ptt_3map,"value","time"));
		wrap.add(trig3Panel);
		
		SpringLayoutGrid.makeCompactGrid(wrap,3,2);
		add(wrap);
		
		DeviceWireManager deviceManager = PulseFireUI.getInstance().getDeviceManager();
		deviceManager.addDeviceCommandListener(CommandName.info_data, this);
	}
	
	private JPanel createTriggerStatusPanel() {
		JPanel header = JComponentFactory.createJFirePanel("Trigger Status");
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
		JPanel header = JComponentFactory.createJFirePanel("Trigger Tests");
		JPanel wrap = new JPanel();
		wrap.add(new JLabel("Test fire: "));
		
		JButton trig0 = new JButton("Trigger0");
		trig0.addActionListener(new TriggerTest(CommandName.req_ptt_fire,0));
		wrap.add(trig0);
		JButton trig1 = new JButton("Trigger1");
		trig1.addActionListener(new TriggerTest(CommandName.req_ptt_fire,1));
		wrap.add(trig1);
		JButton trig2 = new JButton("Trigger2");
		trig2.addActionListener(new TriggerTest(CommandName.req_ptt_fire,2));
		wrap.add(trig2);
		JButton trig3 = new JButton("Trigger3");
		trig3.addActionListener(new TriggerTest(CommandName.req_ptt_fire,3));
		wrap.add(trig3);
		
		header.add(wrap);
		return header;
	}
	
	@Override
	public Class<?> getTabClassName() {
		return this.getClass();
	}

	class TriggerTest implements ActionListener {
		CommandName cmd = null;
		int index = 0;
		public TriggerTest(CommandName cmd,int index) {
			this.cmd=cmd;
			this.index=index;
		}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			Command command = new Command(cmd);
			command.setArgu0(""+index);
			PulseFireUI.getInstance().getDeviceManager().requestCommand(command);
		}
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

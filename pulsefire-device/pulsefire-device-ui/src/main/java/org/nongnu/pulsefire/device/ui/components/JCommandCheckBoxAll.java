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

package org.nongnu.pulsefire.device.ui.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import org.nongnu.pulsefire.device.io.protocol.Command;
import org.nongnu.pulsefire.device.io.protocol.CommandName;
import org.nongnu.pulsefire.device.io.transport.DeviceCommandListener;
import org.nongnu.pulsefire.device.io.transport.DeviceWireManager;
import org.nongnu.pulsefire.device.ui.JComponentEnableStateListener;
import org.nongnu.pulsefire.device.ui.PulseFireUI;

/**
 * JCommandCheckBoxAll sets and resets full bitfields
 * 
 * @author Willem Cazander
 */
public class JCommandCheckBoxAll extends JCheckBox implements ActionListener,DeviceCommandListener {

	private static final long serialVersionUID = -3660219975769332446L;
	private DeviceWireManager deviceManager = null;
	private Command command = null;
	private int bit = -1;

	public JCommandCheckBoxAll(CommandName commandName) {
		this(commandName,-1);
	}
	
	public JCommandCheckBoxAll(CommandName commandName,int bit) {
		super();
		this.bit=bit;
		deviceManager = PulseFireUI.getInstance().getDeviceManager();
		command = new Command(commandName);
		this.addActionListener(this);
		JComponentEnableStateListener.attach(this,commandName);
		deviceManager.addDeviceCommandListener(command.getCommandName(), this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		boolean selected = isSelected();
		String argu0 = "0";
		if (selected) {
			argu0 = "65535";
		}
		if (bit==-1) {
			command.setArgu0(argu0);
			deviceManager.requestCommand(command);
		} else {
			Command cmd = PulseFireUI.getInstance().getDeviceData().getDeviceParameterIndexed(command,0); // hardcoded to use 0 as value
			if (cmd==null) {
				return;
			}
			int andBits = 1 << bit;
			int data = new Integer(cmd.getArgu0());
			if ((data & andBits)==0) {
				command.setArgu0(""+(data + andBits));
			} else {
				command.setArgu0(""+(data - andBits));
			}
			deviceManager.requestCommand(command);
		}
	}
	
	@Override
	public void commandReceived(Command command) {
		if (bit==-1) {
			boolean selectedNew = "1".endsWith(command.getArgu0());
			boolean selectedBox = isSelected();
			if (selectedNew==selectedBox) {
				return; // no change
			}
			setSelected(selectedNew);
		} else {

			if (command.getArgu1()==null) {
				return;
			}
			if (new Integer(command.getArgu1()).equals(0)==false) {
				return; // not for me.
			}
			Command cmd = PulseFireUI.getInstance().getDeviceData().getDeviceParameterIndexed(command, 0);
			if (cmd==null) {
				return;
			}
			int andBits = 1 << bit;
			int data = new Integer(cmd.getArgu0());
			setSelected((data & andBits)>0);
			
		}
	}
}

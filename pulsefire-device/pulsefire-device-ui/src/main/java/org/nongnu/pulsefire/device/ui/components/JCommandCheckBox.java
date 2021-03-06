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
 * JCommandCheckBox
 * 
 * @author Willem Cazander
 */
public class JCommandCheckBox extends JCheckBox implements ActionListener,DeviceCommandListener {

	private static final long serialVersionUID = -3660219975769332446L;
	private DeviceWireManager deviceManager = null;
	private Command command = null;
	private int bit = -1;
	private int idx = -1;
	private volatile boolean noEvent = false;
	private Boolean enabledOverride = true;

	public JCommandCheckBox(CommandName commandName) {
		this(commandName,-1,-1);
	}
	
	public JCommandCheckBox(CommandName commandName,int bit) {
		this(commandName,bit,-1);
	}
	
	public JCommandCheckBox(CommandName commandName,int bit,int idx) {
		super();
		this.idx=idx;
		this.bit=bit;
		deviceManager = PulseFireUI.getInstance().getDeviceManager();
		command = new Command(commandName);
		this.addActionListener(this);
		if (idx!=-1) {
			JComponentEnableStateListener.attach(this,commandName,idx);
		} else {
			JComponentEnableStateListener.attach(this,commandName);
		}
		deviceManager.addDeviceCommandListener(command.getCommandName(), this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (noEvent) {
			return;
		}
		boolean selected = isSelected();
		String argu0 = "0";
		if (selected) {
			argu0 = "1";
		}
		command.setArgu0(argu0);
		if (idx != -1) {
			command.setArgu1(""+idx);
		}
		if (bit==-1) {
			deviceManager.requestCommand(command);
		} else {
			if (idx == -1) {
				Command cmd = PulseFireUI.getInstance().getDeviceData().getDeviceParameter(command);
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
			} else {
				Command cmd = PulseFireUI.getInstance().getDeviceData().getDeviceParameterIndexed(command,idx);
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
	}
	@Override
	public void commandReceived(Command command) {
		if (bit==-1) {
			boolean selectedNew = "1".endsWith(command.getArgu0());
			boolean selectedBox = isSelected();
			if (selectedNew==selectedBox) {
				return; // no change
			}
			try {
				noEvent = true;
				setSelected(selectedNew);
			} finally {
				noEvent = false;
			}
		} else {
			if (idx == -1) {
				int andBits = 1 << bit;
				int data = new Integer(command.getArgu0());
				try {
					noEvent = true;
					setSelected((data & andBits)>0);
				} finally {
					noEvent = false;
				}
			} else {
				if (command.getArgu1()==null) {
					return;
				}
				if (new Integer(command.getArgu1()).equals(idx)==false) {
					return; // not for me.
				}
				Command cmd = PulseFireUI.getInstance().getDeviceData().getDeviceParameterIndexed(command, idx);
				if (cmd==null) {
					return;
				}
				int andBits = 1 << bit;
				int data = new Integer(cmd.getArgu0());
				try {
					noEvent = true;
					setSelected((data & andBits)>0);
				} finally {
					noEvent = false;
				}
			}
			
		}
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		if (getEnabledOverride()==null) {
			super.setEnabled(enabled);
		} else {
			super.setEnabled(getEnabledOverride());
		}
	}
	
	/**
	 * @return the enabledOverride
	 */
	public Boolean getEnabledOverride() {
		return enabledOverride;
	}
	
	/**
	 * @param enabledOverride the enabledOverride to set
	 */
	public void setEnabledOverride(Boolean enabledOverride) {
		this.enabledOverride = enabledOverride;
		setEnabled(enabledOverride);
	}

}

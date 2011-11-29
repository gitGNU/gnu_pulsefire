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

import javax.swing.JPanel;

import org.nongnu.pulsefire.device.DeviceCommandListener;
import org.nongnu.pulsefire.device.DeviceConnectListener;
import org.nongnu.pulsefire.device.DeviceWireManager;
import org.nongnu.pulsefire.device.ui.JComponentEnableStateListener;
import org.nongnu.pulsefire.device.ui.PulseFireUI;
import org.nongnu.pulsefire.device.ui.components.JFireDial.DialEvent;
import org.nongnu.pulsefire.device.ui.components.JFireDial.DialListener;
import org.nongnu.pulsefire.wire.Command;
import org.nongnu.pulsefire.wire.CommandName;

/**
 * JCommandDial
 * 
 * @author Willem Cazander
 */
public class JCommandDial extends JPanel implements DialListener,DeviceCommandListener,DeviceConnectListener {

	private static final long serialVersionUID = -2345234552345789002L;
	private JFireDial fireDial = null;
	private DeviceWireManager deviceManager = null;
	private Command command = null;
	private int idx = -1;
	
	public JCommandDial(CommandName commandName) {
		this(commandName,-1);
	}
	
	public JCommandDial(CommandName commandName,int idx) {
		super();
		this.idx=idx;
		deviceManager = PulseFireUI.getInstance().getDeviceManager();
		command = new Command(commandName);
		fireDial = new JFireDial(commandName.name());
		fireDial.setMaximum(commandName.getMaxValue());
		fireDial.addDialListener(this);
		JComponentEnableStateListener.attach(fireDial,commandName);
		
		deviceManager.addDeviceCommandListener(command.getCommandName(), this);
		deviceManager.addDeviceConnectListener(this);
		
		add(fireDial);
	}
	
	@Override
	public void dialAdjusted( DialEvent e ) {
		int v = e.getValue();
		command.setArgu0(""+v);
		if (idx!=-1) {
			command.setArgu1(""+idx);
		}
		deviceManager.requestCommand(command);
	}
	
	@Override
	public void commandReceived(Command command) {
		if (fireDial.isMouseDialing()) {
			return; // skip until mouse is released
		}
		if (command.getArgu0()==null) {
			return; // no value
		}
		if (command.getArgu0().isEmpty()) {
			return; // no value
		}
		
		Integer valueNew = Integer.parseInt(command.getArgu0());
		Integer valueOld = fireDial.getValue();
		if (valueNew==valueOld) {
			return; // no change
		}
		if (idx!=-1) {
			if (command.getArgu1()==null) {
				return; // happend with info_cnf ?
			}
			Integer idxNew = Integer.parseInt(command.getArgu1());
			if (idx!=idxNew) {
				return; // this cmd is for some other channel
			}
		}
		fireDial.setValue(valueNew);
	}

	@Override
	public void deviceConnect() {
		int maxValue = command.getCommandName().getMaxValue();
		if (maxValue==0) {
			maxValue = Integer.MAX_VALUE;
		}
		fireDial.setMaximum(maxValue);
	}

	@Override
	public void deviceDisconnect() {
	}
	
	public JFireDial getFireDial() {
		return fireDial;
	}
}

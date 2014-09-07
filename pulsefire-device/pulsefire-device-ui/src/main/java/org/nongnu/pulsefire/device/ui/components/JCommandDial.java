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
import javax.swing.SpringLayout;

import org.nongnu.pulsefire.device.io.protocol.Command;
import org.nongnu.pulsefire.device.io.protocol.CommandName;
import org.nongnu.pulsefire.device.io.transport.DeviceCommandListener;
import org.nongnu.pulsefire.device.io.transport.DeviceConnectListener;
import org.nongnu.pulsefire.device.io.transport.DeviceWireManager;
import org.nongnu.pulsefire.device.ui.JComponentEnableStateListener;
import org.nongnu.pulsefire.device.ui.PulseFireUI;
import org.nongnu.pulsefire.device.ui.SpringLayoutGrid;
import org.nongnu.pulsefire.device.ui.components.JFireDial.DialEvent;
import org.nongnu.pulsefire.device.ui.components.JFireDial.DialListener;

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
	volatile private boolean noEvent = false;
	
	public JCommandDial(CommandName commandName) {
		this(commandName,-1);
	}
	
	public JCommandDial(CommandName commandName,int idx) {
		super();
		this.idx=idx;
		deviceManager = PulseFireUI.getInstance().getDeviceManager();
		command = new Command(commandName);
		fireDial = new JFireDial(0,commandName.getMaxValue(),0);
		fireDial.setText(PulseFireUI.getInstance().getContext().getResourceMap().getString(commandName.getKeyLabel()));
		fireDial.setToolTipText(PulseFireUI.getInstance().getContext().getResourceMap().getString(commandName.getKeyDescription()));
		fireDial.addDialListener(this);
		if (idx!=-1) {
			JComponentEnableStateListener.attach(fireDial,commandName,idx);
		} else {
			JComponentEnableStateListener.attach(fireDial,commandName);
		}
		deviceManager.addDeviceCommandListener(command.getCommandName(), this);
		deviceManager.addDeviceConnectListener(this);
		
		// fixme
		//setLayout(new SpringLayout());
		//add(new JCommandLabel(commandName));
		add(fireDial);
		//SpringLayoutGrid.makeCompactGrid(this,2,1,0,0,0,0);
	}
	
	@Override
	public void dialAdjusted( DialEvent e ) {
		long v = e.getValue();
		command.setArgu0(""+v);
		if (idx!=-1) {
			command.setArgu1(""+idx);
		}
		if (noEvent==false) {
			deviceManager.requestCommand(command);
		}
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
		
		Long valueNew = Long.parseLong(command.getArgu0());
		Long valueOld = fireDial.getValue();
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
		try {
			noEvent = true;
			fireDial.setValue(valueNew);
		} finally {
			noEvent = false;
		}
	}
	
	@Override
	public void deviceConnect() {
		long maxValue = command.getCommandName().getMaxValue();
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

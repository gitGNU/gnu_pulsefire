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
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.nongnu.pulsefire.device.io.protocol.Command;
import org.nongnu.pulsefire.device.io.protocol.CommandName;
import org.nongnu.pulsefire.device.io.transport.DeviceCommandListener;
import org.nongnu.pulsefire.device.io.transport.DeviceConnectListener;
import org.nongnu.pulsefire.device.io.transport.DeviceWireManager;
import org.nongnu.pulsefire.device.ui.JComponentEnableStateListener;
import org.nongnu.pulsefire.device.ui.PulseFireUI;

/**
 * JCommandSpinner
 * 
 * @author Willem Cazander
 */
public class JCommandSpinner extends JPanel implements DeviceCommandListener,DeviceConnectListener, ChangeListener {

	private static final long serialVersionUID = -2345234552345789002L;
	private JSpinner spinner = null;
	private SpinnerNumberModel spinnerModel = null;
	private DeviceWireManager deviceManager = null;
	private Command command = null;
	private int idx = -1;
	volatile private boolean noEvent = false;
	
	public JCommandSpinner(CommandName commandName) {
		this(commandName,-1);
	}
	
	public JCommandSpinner(CommandName commandName,int idx) {
		super();
		this.idx=idx;
		deviceManager = PulseFireUI.getInstance().getDeviceManager();
		command = new Command(commandName);
		long stepSize = 1l;
		long maximum = commandName.getMaxValue();
		long minimum = 0l;
		long value = 0l;
		spinnerModel = new SpinnerNumberModel(new Long(value), new Long(minimum), new Long(maximum), new Long(stepSize));
		spinner = new JSpinner(spinnerModel);
		spinner.setName("commandname."+commandName.name()+".spinner");
		spinner.addChangeListener(this);
		if (idx!=-1) {
			JComponentEnableStateListener.attach(spinner,commandName,idx);
		} else {
			JComponentEnableStateListener.attach(spinner,commandName);
		}
		deviceManager.addDeviceCommandListener(command.getCommandName(), this);
		deviceManager.addDeviceConnectListener(this);
		add(spinner);
	}
	
	@Override
	public void commandReceived(Command command) {
		if (command.getArgu0()==null) {
			return; // no value
		}
		if (command.getArgu0().isEmpty()) {
			return; // no value
		}
		
		Long valueNew = Long.parseLong(command.getArgu0());
		Long valueOld = (Long)spinnerModel.getValue();
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
			spinnerModel.setValue(valueNew);
		} finally {
			noEvent = false;
		}
	}

	@Override
	public void deviceConnect() {
		long maxValue = command.getCommandName().getMaxValue();
		if (maxValue==0) {
			maxValue = Long.MAX_VALUE;
		}
		spinnerModel.setMaximum(maxValue);
	}

	@Override
	public void deviceDisconnect() {
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		Long v = (Long)spinnerModel.getValue();
		command.setArgu0(""+v);
		if (idx!=-1) {
			command.setArgu1(""+idx);
		}
		if (noEvent==false) {
			deviceManager.requestCommand(command);
		}
	}
}

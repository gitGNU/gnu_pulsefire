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

import javax.swing.JComponent;

import org.nongnu.pulsefire.device.DeviceCommandListener;
import org.nongnu.pulsefire.device.DeviceConnectListener;
import org.nongnu.pulsefire.wire.Command;
import org.nongnu.pulsefire.wire.CommandName;
import org.nongnu.pulsefire.wire.WirePulseMode;

/**
 * JComponentEnableStateListener
 * 
 * @author Willem Cazander
 */
public class JComponentEnableStateListener implements DeviceConnectListener,DeviceCommandListener {
	private JComponent component = null;
	private CommandName commandName = null;
	
	public JComponentEnableStateListener(JComponent component,CommandName commandVariableFilter) {
		this.commandName=commandVariableFilter;
		this.component=component;
		this.component.setEnabled(false);
		PulseFireUI.getInstance().getDeviceManager().addDeviceConnectListener(this);
	}
	
	static public void attach(JComponent component,CommandName commandVariableFilter) {
		new JComponentEnableStateListener(component,commandVariableFilter);
	}
	
	@Override
	public void deviceConnect() {
		if (commandName==null) {
			component.setEnabled(true);
			return;
		}
		if (commandName.isDisabled()) {
			return; // cmd disabled
		}
		if (commandName.getChipFlagDependency()!=null) {
			Command cmd = PulseFireUI.getInstance().getDeviceData().getDeviceParameter(CommandName.chip_flags);
			if (commandName.getChipFlagDependency().isFlagActive(cmd)==false) {
				return; // feature not enabled.
			}
		}
		if (commandName.isPulseModeDependency()) {
			PulseFireUI.getInstance().getDeviceManager().addDeviceCommandListener(CommandName.pulse_mode, this);	
			commandReceived(PulseFireUI.getInstance().getDeviceData().getDeviceParameter(CommandName.pulse_mode));
		} else {
			component.setEnabled(true);
		}
	}

	@Override
	public void deviceDisconnect() {
		component.setEnabled(false);
	}

	@Override
	public void commandReceived(Command command) {
		Integer mode = new Integer(command.getArgu0()); 
		for (int i=0;i<WirePulseMode.values().length;i++) {
			if (mode.equals(i)) {
				WirePulseMode pulseMode = WirePulseMode.values()[i];
				checkMode(pulseMode);
				break;
			}
		}
	}
	
	private void checkMode(WirePulseMode newMode) {
		if (commandName.getPulseModeDependencies()==null) {
			component.setEnabled(true);
			return;
		}
		for (WirePulseMode depMode:commandName.getPulseModeDependencies()) {
			if (depMode.equals(newMode)) {
				component.setEnabled(true);
				return;
			}
		}
		component.setEnabled(false);
	}
}

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
	private Integer index = null;
	
	public JComponentEnableStateListener(JComponent component,CommandName commandVariableFilter,Integer index) {
		this.commandName=commandVariableFilter;
		this.component=component;
		this.index=index;
		this.component.setEnabled(false);
		PulseFireUI.getInstance().getDeviceManager().addDeviceConnectListener(this);
	}
	
	static public void attach(JComponent component) {
		attach(component,null);
	}
	
	static public void attach(JComponent component,CommandName commandVariableFilter) {
		attach(component,commandVariableFilter,null);
	}
	
	static public void attach(JComponent component,CommandName commandVariableFilter,Integer index) {
		new JComponentEnableStateListener(component,commandVariableFilter,index);
	}
	
	@Override
	public void deviceConnect() {
		checkAll();
		if (commandName!=null && commandName.isPulseModeDependency()) {
			PulseFireUI.getInstance().getDeviceManager().addDeviceCommandListener(CommandName.pulse_mode, this);
		}
		if (commandName!=null && commandName.name().endsWith("_a")) {
			PulseFireUI.getInstance().getDeviceManager().addDeviceCommandListener(CommandName.pulse_bank, this);
		}
		if (commandName!=null && commandName.name().endsWith("_b")) {
			PulseFireUI.getInstance().getDeviceManager().addDeviceCommandListener(CommandName.pulse_bank, this);
		}
		if (commandName!=null && commandName.name().startsWith("req_pulse")) {
			PulseFireUI.getInstance().getDeviceManager().addDeviceCommandListener(CommandName.pulse_trig, this);
		}
	}

	@Override
	public void deviceDisconnect() {
		component.setEnabled(false);
		if (commandName!=null && commandName.isPulseModeDependency()) {
			PulseFireUI.getInstance().getDeviceManager().removeDeviceCommandListener(CommandName.pulse_mode, this);
		}
		if (commandName!=null && commandName.name().endsWith("_a")) {
			PulseFireUI.getInstance().getDeviceManager().removeDeviceCommandListener(CommandName.pulse_bank, this);
		}
		if (commandName!=null && commandName.name().endsWith("_b")) {
			PulseFireUI.getInstance().getDeviceManager().removeDeviceCommandListener(CommandName.pulse_bank, this);
		}
		if (commandName!=null && commandName.name().startsWith("req_pulse")) {
			PulseFireUI.getInstance().getDeviceManager().removeDeviceCommandListener(CommandName.pulse_trig, this);
		}
	}

	@Override
	public void commandReceived(Command command) {
		checkAll();
	}
	
	private void checkAll() {
		if (commandName==null) {
			component.setEnabled(true);
			return;
		}
		if (commandName.isDisabled()) {
			component.setEnabled(false);
			return; // cmd disabled
		}
		if (checkChipFlag()==false) {
			component.setEnabled(false);
			return;
		}
		if (checkPulseMode()==false) {
			component.setEnabled(false);
			return;
		}
		if (checkPulseTrigger()==false) {
			component.setEnabled(false);
			return;
		}
		if (checkPulseBank()==false) {
			component.setEnabled(false);
			return;
		}
		component.setEnabled(true);
	}
	
	private boolean checkChipFlag() {
		if (commandName!=null && commandName.getChipFlagDependency()!=null) {
			Command cmd = PulseFireUI.getInstance().getDeviceData().getDeviceParameter(CommandName.chip_flags);
			if (commandName.getChipFlagDependency().isFlagActive(cmd)==false) {
				return false; // feature not enabled.
			}
		}
		return true;
	}
	
	private boolean checkPulseBank() {
		if (commandName!=null && (commandName.name().endsWith("_a") || commandName.name().endsWith("_b"))) {
			Command command = PulseFireUI.getInstance().getDeviceData().getDeviceParameter(CommandName.pulse_bank);
			if (command==null) {
				return true;
			}
			if (commandName.name().endsWith("_a")) {
				if ("0".equals(command.getArgu0())) {
					return true;
				} else {
					return false;
				}
			} else {
				if ("1".equals(command.getArgu0())) {
					return true;
				} else {
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean checkPulseMode() {
		Command command = PulseFireUI.getInstance().getDeviceData().getDeviceParameter(CommandName.pulse_mode);
		if (command==null) {
			return true;
		}
		Integer mode = new Integer(command.getArgu0()); 
		WirePulseMode[] modes = WirePulseMode.values();
		for (int i=0;i<modes.length;i++) {
			if (mode.equals(i)) {
				WirePulseMode pulseMode = modes[i];
				return checkPulseModeWire(pulseMode);
			}
		}
		return true;
	}
	
	private boolean checkPulseModeWire(WirePulseMode newMode) {
		if (commandName==null) {
			return true;
		}
		if (index!=null && (WirePulseMode.FLASH_ZERO==newMode || WirePulseMode.PPM_ALL==newMode)) {
			if (WirePulseMode.PPM_ALL==newMode && index>0 && commandName.name().startsWith("ppm_data")) {
				return true;
			}
			if (CommandName.pwm_tune_cnt==commandName && index>0) {
				return false;
			}
			if (commandName.name().endsWith("_a") && index>0) {
				return false;
			}
			if (commandName.name().endsWith("_b") && index>0) {
				return false;
			}
		}
		if (commandName.getPulseModeDependencies()==null) {
			return true;
		}
		for (WirePulseMode depMode:commandName.getPulseModeDependencies()) {
			if (depMode.equals(newMode)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean checkPulseTrigger() {
		if (commandName==null) {
			return true;
		}
		/* TODO
		if (CommandName.req_pulse_fire.equals(commandName) || CommandName.req_pulse_hold_fire.equals(commandName)) {
			Command command = PulseFireUI.getInstance().getDeviceData().getDeviceParameter(CommandName.pulse_trig);
			if (command==null) {
				return true;
			}
			Integer trigger = new Integer(command.getArgu0()); 
			if (trigger==0) {
				return false;
			}	
		}
		*/
		return true;
	}
}

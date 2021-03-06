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

package org.nongnu.pulsefire.device.ui.pull;

import org.nongnu.pulsefire.device.io.protocol.Command;
import org.nongnu.pulsefire.device.io.protocol.CommandName;
import org.nongnu.pulsefire.device.io.transport.DeviceDataListener;
import org.nongnu.pulsefire.device.ui.PulseFireUI;

/**
 * UpdatePwmData updates pwm/freq data after pwm config change.
 * 
 * @author Willem Cazander
 */
public class UpdatePwmData implements Runnable,DeviceDataListener {

	static public final int INIT_SPEED = 500;
	private volatile boolean update = false;
	
	public UpdatePwmData() {
		PulseFireUI.getInstance().getDeviceManager().addDeviceDataListener(this);
	}
	
	@Override
	public void run() {
		if (update) {
			PulseFireUI.getInstance().getDeviceManager().requestCommand(new Command(CommandName.info_pwm));
			PulseFireUI.getInstance().getDeviceManager().requestCommand(new Command(CommandName.info_freq));
			update = false;
		}
	}
	
	@Override
	public void deviceDataSend(String data) {
	}
	
	@Override
	public void deviceDataReceived(String data) {
		Command flags = PulseFireUI.getInstance().getDeviceData().getDeviceParameter(CommandName.chip_flags);
		if (flags==null || flags.getArgu0()==null) {
			return;
		}
		if (flags.getArgu0().contains("PWM")==false) {
			return;
		}
		for (CommandName cn:CommandName.values()) {
			if (cn.getBits() > 0) {
				if ((cn.getBits() & 2) > 0) { // TODO add var bits enum
					if (data.contains(cn.name())) {
						update = true;
						return;
					}
					if (data.startsWith(CommandName.byte2hex((byte)cn.getId()))) {
						update = true; // Also check hex version, TODO: move to deviceCmd ALL listener
						return;
					}
				}
			}
		}
		// 2 extra for freq speed
		if (data.startsWith(CommandName.pwm_clock.name()+"=") || data.startsWith(CommandName.pwm_loop.name()+"=") || data.startsWith(CommandName.pulse_enable.name()+"=")) {
			update = true;
			return;
		}
		if (data.startsWith(CommandName.byte2hex((byte)CommandName.pwm_clock.getId())) || data.startsWith(CommandName.byte2hex((byte)CommandName.pwm_loop.getId())) || data.startsWith(CommandName.byte2hex((byte)CommandName.pulse_enable.getId()))) {
			update = true;
			return;
		}
	}
}

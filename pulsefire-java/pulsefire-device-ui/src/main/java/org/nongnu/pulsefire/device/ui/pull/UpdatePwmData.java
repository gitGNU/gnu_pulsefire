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

import org.nongnu.pulsefire.device.DeviceConnectListener;
import org.nongnu.pulsefire.device.DeviceDataListener;
import org.nongnu.pulsefire.device.ui.PulseFireUI;
import org.nongnu.pulsefire.wire.Command;
import org.nongnu.pulsefire.wire.CommandName;

/**
 * UpdatePwmData updates pwm/freq data after pwm config change.
 * 
 * @author Willem Cazander
 */
public class UpdatePwmData implements Runnable,DeviceConnectListener,DeviceDataListener {

	static public final int INIT_SPEED = 500;
	private volatile boolean run = false;
	private volatile boolean update = false;
	
	public UpdatePwmData() {
		PulseFireUI.getInstance().getDeviceManager().addDeviceConnectListener(this);
		PulseFireUI.getInstance().getDeviceManager().addDeviceDataListener(this);
	}
	
	@Override
	public void run() {
		if (run && update) {
			PulseFireUI.getInstance().getDeviceManager().requestCommand(new Command(CommandName.info_pwm));
			PulseFireUI.getInstance().getDeviceManager().requestCommand(new Command(CommandName.info_freq));
			update = false;
		}
	}

	@Override
	public void deviceConnect() {
		run = true;
	}

	@Override
	public void deviceDisconnect() {
		run = false;
	}

	@Override
	public void deviceDataSend(String data) {
	}

	@Override
	public void deviceDataReceived(String data) {
		for (CommandName cn:CommandName.values()) {
			if (cn.getBits() > 0) {
				if ((cn.getBits() & 2) > 0) { // TODO add var bits enum
					if (data.contains(cn.name())) {
						update = true;
					}
				}
			}
		}
		// 2 extra for freq speed
		if (data.startsWith(CommandName.pwm_clock.name()+"=") || data.startsWith(CommandName.pwm_loop.name()+"=")) {
			update = true;
		}
	}
}

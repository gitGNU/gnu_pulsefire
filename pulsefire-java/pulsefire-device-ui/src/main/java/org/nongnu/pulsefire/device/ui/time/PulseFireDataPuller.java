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

package org.nongnu.pulsefire.device.ui.time;

import org.nongnu.pulsefire.device.DeviceConnectListener;
import org.nongnu.pulsefire.device.ui.PulseFireUI;
import org.nongnu.pulsefire.wire.Command;
import org.nongnu.pulsefire.wire.CommandName;

/**
 * PulseFireDataPuller pull every X seconds the data of device.
 * 
 * @author Willem Cazander
 */
public class PulseFireDataPuller implements Runnable,DeviceConnectListener {

	private volatile boolean run = false;
	private volatile boolean runOnce = true;
	private volatile boolean runPause = false;
	
	public PulseFireDataPuller() {
		PulseFireUI.getInstance().getDeviceManager().addDeviceConnectListener(this);
	}
	
	@Override
	public void run() {
		if (run & runPause==false) {
			PulseFireUI.getInstance().getDeviceManager().requestCommand(new Command(CommandName.info_data));
			PulseFireUI.getInstance().getDeviceManager().requestCommand(new Command(CommandName.info_prog));
			PulseFireUI.getInstance().getDeviceManager().requestCommand(new Command(CommandName.info_freq));
			if (runOnce) {
				runOnce = false; // request one time extra info_conf so if some cmds where jammed we get them here.
				PulseFireUI.getInstance().getDeviceManager().requestCommand(new Command(CommandName.info_conf));
			}
		}
	}

	@Override
	public void deviceConnect() {
		run = true;
		runOnce = true;
	}

	@Override
	public void deviceDisconnect() {
		run = false;
		runOnce = true;
	}

	/**
	 * @return the runPause
	 */
	public boolean isRunPause() {
		return runPause;
	}

	/**
	 * @param runPause the runPause to set
	 */
	public void setRunPause(boolean runPause) {
		this.runPause = runPause;
	}
}

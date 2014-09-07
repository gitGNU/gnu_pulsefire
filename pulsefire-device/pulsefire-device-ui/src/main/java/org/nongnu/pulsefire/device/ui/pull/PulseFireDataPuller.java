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
import org.nongnu.pulsefire.device.ui.PulseFireUI;
import org.nongnu.pulsefire.device.ui.PulseFireUISettingKeys;
import org.nongnu.pulsefire.device.ui.PulseFireUISettingListener;
import org.nongnu.pulsefire.device.ui.time.EventTimeTrigger;
import org.nongnu.pulsefire.wire.Command;
import org.nongnu.pulsefire.wire.CommandName;

/**
 * PulseFireDataPuller pull every X seconds the data of device.
 * 
 * @author Willem Cazander
 */
public class PulseFireDataPuller implements Runnable,DeviceConnectListener,PulseFireUISettingListener {

	static public final int INIT_SPEED = 8000;
	private volatile boolean run = false;
	private volatile boolean runOnce = true;
	private volatile boolean runPause = false;
	
	public PulseFireDataPuller() {
		PulseFireUI.getInstance().getDeviceManager().addDeviceConnectListener(this);
		PulseFireUI.getInstance().getSettingsManager().addSettingListener(PulseFireUISettingKeys.PULL_SPEED, this);
	}
	
	@Override
	public void run() {
		if (run & runPause==false) {
			if (runOnce) {
				runOnce = false; // request one time extra info_conf so if some cmds where jammed we get them here.
				PulseFireUI.getInstance().getDeviceManager().requestCommand(new Command(CommandName.info_conf,"all")); // do before pull data to fix missing meta data.
				PulseFireUI.getInstance().getDeviceManager().requestCommand(new Command(CommandName.info_chip)); // Get chip meta extra for some win32 connect issues.
				if (PulseFireUI.getInstance().getDeviceManager().getDeviceVersion() < 11) {
					PulseFireUI.getInstance().getDeviceManager().requestCommand(new Command(CommandName.help,"map")); // get help map for safty
				} else {
					PulseFireUI.getInstance().getDeviceManager().requestCommand(new Command(CommandName.info_vars));
				}
				settingUpdated(PulseFireUISettingKeys.PULL_SPEED,PulseFireUI.getInstance().getSettingsManager().getSettingString(PulseFireUISettingKeys.PULL_SPEED));
			}
			
			// Pull all chip data as all chip config is already auto push. 
			if (PulseFireUI.getInstance().getDeviceManager().getDeviceVersion() < 11) {
				PulseFireUI.getInstance().getDeviceManager().requestCommand(new Command(CommandName.info_data)); // Get all data
			} else {
				PulseFireUI.getInstance().getDeviceManager().requestCommand(new Command(CommandName.info_data,"np")); // Remove auto push data for smaller list
			}
			if (PulseFireUI.getInstance().getDeviceManager().getDeviceVersion() < 11) {
				PulseFireUI.getInstance().getDeviceManager().requestCommand(new Command(CommandName.info_prog)); // moved to data in 1.1
				PulseFireUI.getInstance().getDeviceManager().requestCommand(new Command(CommandName.info_freq)); // moved to UpdatePwmData
			}
		}
	}

	@Override
	public void deviceConnect() {
		run = true;
		runOnce = true;
		settingUpdated(PulseFireUISettingKeys.PULL_SPEED,""+INIT_SPEED);
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

	@Override
	public void settingUpdated(PulseFireUISettingKeys key, String value) {
		EventTimeTrigger trig = PulseFireUI.getInstance().getEventTimeManager().getEventTimeTriggerByName("refreshData");
		if (trig!=null) {
			trig.setTimeStep(new Integer(value));
			trig.setTimeNextRun(trig.getRunStartTime()+trig.getTimeStep());
		}
	}
}

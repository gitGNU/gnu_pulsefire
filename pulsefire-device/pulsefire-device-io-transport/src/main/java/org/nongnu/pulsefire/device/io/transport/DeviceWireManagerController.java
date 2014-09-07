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

package org.nongnu.pulsefire.device.io.transport;

import java.util.ArrayList;
import java.util.List;

/**
 * DeviceWireManagerController controlls the different device managers.
 * 
 * @author Willem Cazander
 * @see DeviceWireManager
 */
public class DeviceWireManagerController {

	private List<DeviceWireManager> deviceManagers  = null;
	
	public DeviceWireManagerController() {
		deviceManagers = new ArrayList<DeviceWireManager>(10);
	}
	
	public DeviceWireManager getDefaultDeviceManager() {
		if (deviceManagers.isEmpty()) {
			return null;
		}
		return deviceManagers.get(0);
	}
	
	public void addDeviceManager(DeviceWireManager deviceManager) {
		deviceManagers.add(deviceManager);
	}
	
	public List<DeviceWireManager> getDeviceManagers() {
		return deviceManagers;
	}
}

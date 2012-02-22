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

package org.nongnu.pulsefire.device;

import java.util.List;

import org.nongnu.pulsefire.wire.Command;
import org.nongnu.pulsefire.wire.CommandName;

/**
 * DeviceWireManager does the connecting to device.
 * 
 * @author Willem Cazander
 * @see DeviceWireManager
 */
public interface DeviceWireManager {

	// main 
	public List<String> getDevicePorts();
	public boolean connect(String port) throws Exception;
	public void disconnect();
	public DeviceData getDeviceData();
	
	// api
	public DeviceCommandRequest requestCommand(Command command);
	public void addDeviceDataListener(DeviceDataListener dataListener);
	public void removeDeviceDataListener(DeviceDataListener dataListener);
	public void addDeviceCommandListener(CommandName cn,DeviceCommandListener commandListener);
	public void removeDeviceCommandListener(CommandName cn,DeviceCommandListener commandListener);
	public void addDeviceConnectListener(DeviceConnectListener connectListener);
	public void removeDeviceConnectListener(DeviceConnectListener connectListener);
	
	
	// meta info
	public boolean isConnected();
	public int getDeviceVersion();
	public int getConnectProgress();
	public String getConnectPhase();
	
	// TODO CLEAN TO INTERFACE
	public void fireSerialConnect(boolean connected);
	public void fireDataSend(String data);
	public void fireDataReceived(String data);
	public void fireCommandReceived(Command command);
}

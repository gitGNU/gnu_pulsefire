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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import org.nongnu.pulsefire.wire.Command;
import org.nongnu.pulsefire.wire.CommandName;
import org.nongnu.pulsefire.wire.CommandNameVersionFactory;

/**
 * AbstractDeviceWireManager had basic implemention of DeviceWireManager
 * 
 * @author Willem Cazander
 * @see DeviceWireManager
 */
abstract public class AbstractDeviceWireManager implements DeviceWireManager {

	protected Logger logger = null;
	protected Queue<DeviceCommandRequest> sendCommandQueue = null;
	protected Map<CommandName,List<DeviceCommandListener>> commandListeners = null;
	protected List<DeviceDataListener> dataListeners = null;
	protected List<DeviceConnectListener> connectListeners = null;
	protected int deviceVersion = 0;
	protected int connectProgress = 0;
	protected String connectPhase = null;
	protected DeviceData deviceData = null;
	
	public AbstractDeviceWireManager() {
		sendCommandQueue = new LinkedBlockingQueue<DeviceCommandRequest>();
		logger = Logger.getLogger(AbstractDeviceWireManager.class.getName());
		commandListeners = new HashMap<CommandName,List<DeviceCommandListener>>(20);
		dataListeners = new ArrayList<DeviceDataListener>(3);
		connectListeners = new ArrayList<DeviceConnectListener>(30);
		deviceData = new DeviceData();
	}
	
	@Override
	public void disconnect() {
		deviceData.createParameters();
		connectProgress = 0;
		sendCommandQueue.clear();
		if (isConnected()) {
			fireSerialConnect(false);
			deviceVersion = 0;
		}
	}
	
	/**
	 * Checks the infoChip response and and performs version check and other connecting steps.
	 * @param infoChip
	 * @return
	 */
	protected boolean doSafeConnect(DeviceCommandRequest infoChip) {
		
		// First check response of info_chip
		if (infoChip.getResponse()==null) {
			disconnect();
			logger.info("Could not request info_chip");
			connectPhase = "Err on info_chip";
			return false;
		}
		
		Command version = getDeviceData().getDeviceParameter(CommandName.chip_version);
		if (version==null) {
			disconnect();
			logger.warning("Could not get "+CommandName.chip_version.name());
			connectPhase = "Err on "+CommandName.chip_version.name();
			return false;
		}
		int deviceVersion = new Double(Double.parseDouble(version.getArgu0())*10).intValue();
		logger.info("Chip version: "+deviceVersion);
		if (CommandNameVersionFactory.configCommandName(deviceVersion)==false) {
			disconnect();
			logger.warning("Version not supported: "+deviceVersion);
			connectPhase = "Version not supported: "+deviceVersion;
			return false;
		}
		this.deviceVersion=deviceVersion; // all oke so flag as connected to version.
		
		connectPhase = "pf rq tx echo";connectProgress = 10;
		requestCommand(new Command(CommandName.req_tx_echo,"0")).waitForResponse(); // note this cmd gets the echo returns so response is not valid.
		
		// Get variable info and variables
		connectPhase = "push promt";connectProgress = 12;
		requestCommand(new Command(CommandName.req_tx_promt,"0")).waitForResponseChecked();
		
		connectPhase = "help max";connectProgress = 15;
		requestCommand(new Command(CommandName.help,		"max")).waitForResponseChecked();
		connectPhase = "help map";connectProgress = 20;
		requestCommand(new Command(CommandName.help,		"map")).waitForResponseChecked();
		connectPhase = "help idx";connectProgress = 25;
		requestCommand(new Command(CommandName.help,		"idx")).waitForResponseChecked();
		
		connectPhase = "info_conf";connectProgress = 30;
		requestCommand(new Command(CommandName.info_conf,	"all")).waitForResponseChecked();
		connectPhase = "info_data";connectProgress = 70;
		requestCommand(new Command(CommandName.info_data)).waitForResponseChecked();
		connectPhase = "info_prog";connectProgress = 85;
		requestCommand(new Command(CommandName.info_prog)).waitForResponseChecked();
		
		// Turn conf push changes on as last.
		requestCommand(new Command(CommandName.req_tx_push,	"1")).waitForResponseChecked();
		
		connectPhase = "Fire events";connectProgress = 95;
		if (isConnected()) {
			fireSerialConnect(true);
		}
		connectPhase = "Connected";connectProgress = 100;
		return true;		
	}
	
	public boolean isConnected() {
		return deviceVersion > 0;
	}
	
	public int getDeviceVersion() {
		return deviceVersion;
	}
	
	public int getConnectProgress() {
		return connectProgress;
	}
	
	public String getConnectPhase() {
		return connectPhase;
	}
	
	public DeviceData getDeviceData() {
		return deviceData;
	}
	
	public DeviceCommandRequest requestCommand(Command command) {
		DeviceCommandRequest rc = new DeviceCommandRequest(command);
		// make specials clear queue for direct effect
		if (command.getCommandName().equals(CommandName.reset_data)) {
			sendCommandQueue.clear();
		} else if (command.getCommandName().equals(CommandName.reset_conf)) {
			sendCommandQueue.clear();
		} else if (command.getCommandName().equals(CommandName.reset_chip)) {
			sendCommandQueue.clear();
		}
		sendCommandQueue.add(rc);
		return rc;
	}
	
	public DeviceCommandRequest pollCommandRequest() {
		return sendCommandQueue.poll();
	}
	public DeviceCommandRequest peekCommandRequest() {
		return sendCommandQueue.peek();
	}
	
	public void addDeviceDataListener(DeviceDataListener dataListener) {
		dataListeners.add(dataListener);
	}
	public void addDeviceCommandListener(CommandName cn,DeviceCommandListener commandListener) {
		List<DeviceCommandListener> list = commandListeners.get(cn);
		if (list==null) {
			list = new ArrayList<DeviceCommandListener>(5);
			commandListeners.put(cn,list);
		}
		list.add(commandListener);
	}
	public void addDeviceConnectListener(DeviceConnectListener connectListener) {
		connectListeners.add(connectListener);
	}
	
	
	// TODO CLEAN TO INTERFACE
	
	public void fireSerialConnect(boolean connected) {
		for (int i=0;i<connectListeners.size();i++) {
			if (connected) {
				connectListeners.get(i).deviceConnect();
			} else {
				connectListeners.get(i).deviceDisconnect();
			}
		}
	}
	
	public void fireDataSend(String data) {
		for (int i=0;i<dataListeners.size();i++) {
			dataListeners.get(i).deviceDataSend(data);
		}
	}
	public void fireDataReceived(String data) {
		for (int i=0;i<dataListeners.size();i++) {
			dataListeners.get(i).deviceDataReceived(data);
		}
	}
	
	public void fireCommandReceived(Command command) {
		List<DeviceCommandListener> list = commandListeners.get(command.getCommandName());
		if (list==null) {
			return;
		}
		for (int i=0;i<list.size();i++) {
			list.get(i).commandReceived(command);
		}
	}

}

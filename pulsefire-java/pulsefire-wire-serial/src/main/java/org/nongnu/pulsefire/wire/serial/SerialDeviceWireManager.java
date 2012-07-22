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

package org.nongnu.pulsefire.wire.serial;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

import org.nongnu.pulsefire.device.DeviceCommandRequest;
import org.nongnu.pulsefire.device.AbstractDeviceWireManager;
import org.nongnu.pulsefire.device.DeviceWireManager;
import org.nongnu.pulsefire.wire.Command;
import org.nongnu.pulsefire.wire.CommandName;

/**
 * SerialDeviceWireManager to to connect to serial pulsefire device.
 * 
 * @author Willem Cazander
 * @see DeviceWireManager
 */
public class SerialDeviceWireManager extends AbstractDeviceWireManager {

	private Logger logger = null;
	private SerialDeviceWireThread serialThread = null;
	
	public SerialDeviceWireManager() {
		logger = Logger.getLogger(SerialDeviceWireManager.class.getName());
	}
	
	@Override
	public List<String> getDevicePorts() {
		List<String> result = new ArrayList<String>(10);
		try {
			Enumeration<?> e = CommPortIdentifier.getPortIdentifiers();
			while(e.hasMoreElements()) {
				CommPortIdentifier cpi = (CommPortIdentifier)e.nextElement();
				if(cpi.getPortType()==CommPortIdentifier.PORT_SERIAL) {
					result.add(cpi.getName());
				}
			}
		} catch (Throwable e) { // missing lib in jvm path...so no ports
		}
		logger.info("Total ports found: "+result.size());
		return result;
	}

	@Override
	public boolean connect(String port) throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException, IOException, InterruptedException {

		if (isConnected()) {
			disconnect(false);
		}
		long startTime = System.currentTimeMillis();
		sendCommandQueue.clear();
		connectPhase = "Opening port";connectProgress = 1;
		// This does the System rescanning, so HOT usb remove and plugin-in can be supported.
		Enumeration<?> e = CommPortIdentifier.getPortIdentifiers();
		while (e.hasMoreElements()) {
			CommPortIdentifier o = (CommPortIdentifier)e.nextElement();
			logger.finer("Rescan port: "+o.getName());
		}
		
		boolean done = false;
		try {
			// Get the commport and config it.
			CommPortIdentifier cpi = CommPortIdentifier.getPortIdentifier(port);
			SerialPort serialPort = (SerialPort) cpi.open("PulseFire", 2000);
			serialPort.setSerialPortParams(115200,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
			serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
			
			// Create and start backend thread for serial communication.
			connectPhase = "Start IO thread";connectProgress = 2;
			serialThread = new SerialDeviceWireThread(this,serialPort);
			serialThread.setName("PulseFire-IO");
			serialThread.start();
			
			// Wait on thread creation and starting
			while (serialThread.isRunning()==false) {
				Thread.sleep(200);	
			}
			
			// Let arduino boot max 10 secs for promt. (note sometimes we do not get prompt)
			connectPhase = "Arduino booting";connectProgress = 5;
			for (int i=0;i<10;i++) {
				Thread.sleep(1000);
				if (serialThread==null) {
					return false;
				}
				if (serialThread.hasSeenPromt()) {
					break;
				}
			}
			
			// Get info_chip as fist in max 2 seconds reponse time
			connectPhase = "Check info_chip";connectProgress = 7;
			DeviceCommandRequest infoChip = requestCommand(new Command(CommandName.info_chip));
			for (int i=0;i<20;i++) {
				Thread.sleep(100);
				if (serialThread==null) {
					return false;
				}
				if (infoChip.getResponse()!=null) {
					break;
				}
				if (i==11 && infoChip.getResponse()==null) {
					infoChip = requestCommand(new Command(CommandName.info_chip)); // request again
				}
			}
			
			// Let do rest of connect by abstract parent
			boolean result = doSafeConnect(infoChip);
			done = true;
			long stopTime = System.currentTimeMillis();
			logger.info("Succesfully connected in "+(stopTime-startTime)+" ms.");
			return result;
		} finally {
			if (done==false) {
				disconnect(true);
			}
		}
	}

	@Override
	public void disconnect(boolean error) {
		if (serialThread==null) {
			return; // already diconnected
		}
		if (error==false) {
			// close nicely so serial buffers do not get flooded.
			requestCommand(new Command(CommandName.req_tx_push,	"0")).waitForResponse(); // disable auto push
			//requestCommand(new Command(CommandName.req_tx_promt,"1")).waitForResponse(); // turn promt on
			//requestCommand(new Command(CommandName.req_tx_echo,"1")).waitForResponse();  // turn echo on
		}
		
		// can be null with multiple clicks fired..
		if (serialThread!=null) { 
			serialThread.shutdown();
			serialThread = null;
		}
		super.disconnect(error);
	}
}

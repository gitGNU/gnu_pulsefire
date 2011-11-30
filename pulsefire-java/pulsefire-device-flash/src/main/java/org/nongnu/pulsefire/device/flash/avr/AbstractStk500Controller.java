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

package org.nongnu.pulsefire.device.flash.avr;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Logger;

import org.nongnu.pulsefire.device.flash.AbstractFlashProgramController;
import org.nongnu.pulsefire.device.flash.FlashControllerConfig;
import org.nongnu.pulsefire.device.flash.FlashException;

/**
 * AbstractStk500Controller is the base for stk500 based flash devices.
 * 
 * @author Willem Cazander
 */
abstract public class AbstractStk500Controller extends AbstractFlashProgramController {

	protected Logger logger = null;
	private SerialPort serialPort = null;
	protected BufferedInputStream input = null;
	protected BufferedOutputStream output = null;
	
	public AbstractStk500Controller() {
		logger = Logger.getLogger(AbstractStk500Controller.class.getName());
	}
	
	abstract public FlashMessage sendFlashMessage(FlashMessage message) throws IOException;	
	abstract public void prepareMessagePrefix(FlashMessage msg,FlashCommandToken command);
	abstract public void prepareMessagePostfix(FlashMessage msg,FlashCommandToken command);
	
	public FlashMessage doFlashCommand(FlashCommandToken command,Integer...param) throws IOException {
		FlashMessage msg = new FlashMessage();
		prepareMessagePrefix(msg,command);
		for (Integer data:param) {
			msg.getRequest().add(data);
		}
		prepareMessagePostfix(msg,command);
		return sendFlashMessage(msg);
	}
	
	protected void rebootDevice() {
		logMessage("Reboot device.");
		serialPort.setRTS(false);
		serialPort.setDTR(false);
		try {
			Thread.sleep(150);
		} catch (InterruptedException e) {
		}
		serialPort.setRTS(true);
		serialPort.setDTR(true);
		try {
			Thread.sleep(150);
		} catch (InterruptedException e) {
		}
	}
	
	protected void disconnectPort() throws FlashException  {
		if (serialPort==null) {
			return;
		}
		if (input==null) {
			return;
		}
		try {
			output.close();
		} catch (IOException e) {
		}
		try {
			input.close();
		} catch (IOException e) {
		}
		try {
			serialPort.close();
		} catch (Exception e) {
		}
		output = null;
		input = null;
		serialPort = null;
		logMessage("Disconnected from port.");
	}
	
	protected void connectPort(FlashControllerConfig flashControllerConfig) throws FlashException {
		Enumeration<?> e = CommPortIdentifier.getPortIdentifiers();
		while (e.hasMoreElements()) {
			e.nextElement();// always reloop the ports before opening.
		}
		try {
			CommPortIdentifier cpi = CommPortIdentifier.getPortIdentifier(flashControllerConfig.getPort());
			serialPort = (SerialPort) cpi.open("FlashManager", 2000);
			serialPort.setSerialPortParams(115200,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
			serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE); 
			input = new BufferedInputStream(serialPort.getInputStream());
			output = new BufferedOutputStream(serialPort.getOutputStream());
			logMessage("Connected to port: "+serialPort.getName());		
		} catch (Exception e1) {
			throw new FlashException("Error while connect to port: "+e1.getMessage(),e1);
		}
	}

	protected void logConfig(FlashControllerConfig flashControllerConfig) {
		logMessage("Flash data size: "+flashControllerConfig.getFlashData().length);
		logMessage("Flash protocol: "+flashControllerConfig.getPortProtocol());
		logMessage("Flash verify: "+flashControllerConfig.isFlashVerify());
	}
	
	abstract protected void flashSafe(FlashControllerConfig flashControllerConfig) throws IOException,FlashException;
	
	protected void flashSafeDisconnect(FlashControllerConfig flashControllerConfig) {
		// hook point for clean up.
	}
	
	public void flash(FlashControllerConfig flashControllerConfig) throws IOException,FlashException {
		try {
			flashSafe(flashControllerConfig);
		} catch (IOException ie) {
			logMessage("IOError: "+ie.getMessage());
			throw ie;
		} catch (FlashException flashException) {
			logMessage("Error: "+flashException.getMessage());
			throw flashException;
		} catch (Exception codeException) {
			logMessage("CError: "+codeException.getMessage());
			throw new FlashException(codeException.getMessage(),codeException);
		} finally {
			try {
				flashSafeDisconnect(flashControllerConfig);
			} finally {
				disconnectPort(); // always disconnect
			}
		}
	}
}

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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.nongnu.pulsefire.device.DeviceCommandRequest;
import org.nongnu.pulsefire.wire.Command;
import org.nongnu.pulsefire.wire.CommandName;
import org.nongnu.pulsefire.wire.CommandNameVersionFactory;
import org.nongnu.pulsefire.wire.CommandWire;
import org.nongnu.pulsefire.wire.CommandWireException;

import gnu.io.SerialPort;

/**
 * SerialDeviceWireThread is the backend communication thread of the serial device manager.
 * 
 * @author Willem Cazander
 * @see SerialDeviceWireManager
 */
public class SerialDeviceWireThread extends Thread {

	private Logger logger = null;
	private SerialDeviceWireManager deviceManager = null;
	private SerialPort serialPort = null;
	private Reader reader = null;
	private Writer writer = null;
	private volatile boolean running = false;
	private volatile boolean seenPromt = false;
	private StringBuffer readBuffer = null;
	private DeviceCommandRequest sendCommand = null;
	static private final String PULSE_FIRE_PROMT = "root@pulsefire:";
	static private final String PULSE_FIRE_ERROR = "# Err:";
	
	public SerialDeviceWireThread(SerialDeviceWireManager deviceManager,SerialPort serialPort) throws IOException {
		if(deviceManager==null) {
			throw new NullPointerException("do not set null deviceManager !!");
		}
		if(serialPort==null) {
			throw new NullPointerException("do not set null serialPort !!");
		}
		this.logger = Logger.getLogger(SerialDeviceWireThread.class.getName());
		this.deviceManager= deviceManager;
		this.serialPort=serialPort;
		readBuffer = new StringBuffer();
		reader = new InputStreamReader(serialPort.getInputStream(),Charset.forName("US-ASCII")); 
		writer = new OutputStreamWriter(serialPort.getOutputStream(),Charset.forName("US-ASCII"));
		logger.info("Connected to port: "+serialPort.getName());
	}
	
	public void run() {
		try {
			running = true;
			while (running) {
				pollInputReader();
				if (sendCommand!=null) {
					softReconnectDevice(true);
					Thread.sleep(5);
					continue; // wait on response
				}
				pollCommandRequest();
			}
		} catch (Exception runException) {
			logger.log(Level.WARNING,runException.getMessage(),runException);
		} finally {
			logger.info("Closing port: "+serialPort.getName());
			try { reader.close(); } catch (IOException e) {}
			try { writer.close(); } catch (IOException e) {}
			serialPort.close();
		}
	}
	
	/**
	 * Check for in system reboot detecting , this should get better someday
	 */
	private void softReconnectDevice(boolean testSendCommand) {
		if (deviceManager.isConnected()==false) {
			return; // don't check before we are connected.
		}
		long currTime = System.currentTimeMillis();
		if (testSendCommand && currTime<sendCommand.getRequestTime()+(15*1000)) {
			return;
		}
		if (testSendCommand) {
			logger.info("In system reboot detected trying soft reconnect. timeout of: "+sendCommand.getRequest().getLineRaw());
		} else {
			logger.info("In system reboot requested trying soft reconnect.");
		}
		//newLineEchos = 0;
		if (sendCommand!=null) {
			sendCommand=null;
		}
		// clear send buffer
		DeviceCommandRequest poll = deviceManager.pollCommandRequest();
		while (poll!=null) {
			poll.setResponse(poll.getRequest()); // release possible waiting requester 
			poll = deviceManager.pollCommandRequest();
		}	
		deviceManager.requestCommand(new Command(CommandName.req_tx_echo,"0"));
		deviceManager.requestCommand(new Command(CommandName.req_tx_promt,"0"));
		deviceManager.requestCommand(new Command(CommandName.info_conf,	"all"));
		deviceManager.requestCommand(new Command(CommandName.info_data));
		deviceManager.requestCommand(new Command(CommandName.info_prog));
		deviceManager.requestCommand(new Command(CommandName.req_tx_push,"1"));
		return;
	}
	
	private void pollCommandRequest() throws InterruptedException, IOException {
		
		DeviceCommandRequest send = deviceManager.pollCommandRequest();
		if (send==null) {
			Thread.sleep(10);
			return; // nothing to send
		}
		DeviceCommandRequest peek = deviceManager.peekCommandRequest();
		while (peek!=null && send.getRequest().getCommandName().equals(peek.getRequest().getCommandName())) {
			send.setResponse(send.getRequest()); // release possible waiting requester 
			send = deviceManager.pollCommandRequest();
			peek = deviceManager.peekCommandRequest(); // skip over all duplicate commands.
		}		
		try {
			if (send.getRequest().getArgu0()==null) {
				logger.fine("Send cmd: "+send.getRequest().getCommandName());
			} else {
				logger.fine("Send cmd: "+send.getRequest().getCommandName()+" with argu0: "+send.getRequest().getArgu0());
			}
			String writeOut = CommandWire.encodeCommand(send.getRequest());
			logger.finest("Raw data write: "+writeOut);
			writer.write(writeOut);
			writer.write('\n');
			writer.flush();
			sendCommand = send;
			deviceManager.fireDataSend(writeOut);
		} catch (IOException sendException) {
			if (sendException.getMessage().contains("writeArray")==false) {
				logger.log(Level.WARNING,sendException.getMessage(),sendException);
			} else {
				logger.log(Level.WARNING,sendException.getMessage());
			}
			deviceManager.disconnect(true);
		}
	}
	
	private void pollInputReader() throws IOException {
		int c = 0;
		boolean lineEnd = false;
		while (reader.ready()) {
			c = reader.read();
			if (c == Command.LINE_END) {
				lineEnd = true;
				break;
			}
			readBuffer.append((char) c);
		}
		if (lineEnd==false) {
			return; // read line until line end is come by.
		}
		String scannedInput = readBuffer.toString().trim();
		readBuffer = new StringBuffer();
		logger.finest("Raw data read: "+scannedInput);
		
		if (scannedInput.isEmpty()) {
			return; // skip empty lines from fire data.
		}
		deviceManager.fireDataReceived(scannedInput);
		
		if (scannedInput.startsWith(PULSE_FIRE_ERROR)) {
			if (sendCommand!=null) {
				sendCommand.setResponse(sendCommand.getRequest()); // release response code.
				sendCommand = null;
			}
		}
		if (scannedInput.startsWith("#")) {
			return; // Skip all comments lines, except '# Err:' see above if.
		}
		
		if (scannedInput.startsWith(PULSE_FIRE_PROMT)) {
			//if (seenPromt && deviceManager.getDeviceData().getDeviceParameter(CommandName.req_tx_promt)!=null) {
			//	softReconnectDevice(false);
			//}
			seenPromt = true; // Remove promt from line and flag it.
			scannedInput=scannedInput.substring(scannedInput.indexOf(':')+1,scannedInput.length()).trim();
		}
		
		if (scannedInput.contains("=")==false) {
			return; // all command except 'local echo' and 'help output' data have = char in it so skip line.
		}
		
		// Extra hack to get chip_version after second connect on some platforms.
		if (scannedInput.startsWith(CommandName.info_chip.name()+CommandName.chip_version.name())) {
			scannedInput=scannedInput.substring(CommandName.info_chip.name().length(),scannedInput.length());
		}
		
		int t = 0;
		if (scannedInput.startsWith("max.")) {
			scannedInput = scannedInput.substring(4);
			t = 1;
		} else if (scannedInput.startsWith("map.")) {
			scannedInput = scannedInput.substring(4);
			t = 2;
		} else if (scannedInput.startsWith("idx.")) {
			scannedInput = scannedInput.substring(4);
			t = 3;
		} else if (scannedInput.startsWith("idg.")) {
			scannedInput = scannedInput.substring(4);
			t = 4;
		}
		Command cmd = null;
		try {
			cmd = CommandWire.decodeCommand(scannedInput);
			logger.finer("Got cmd: "+cmd.getCommandName()+" with argu0: "+cmd.getArgu0());
			if (sendCommand!=null && sendCommand.getRequest().getCommandName().equals(cmd.getCommandName())) {
				sendCommand.setResponse(cmd);
				sendCommand = null;
			}
		} catch (CommandWireException cwe) {
			logger.log(Level.WARNING,cwe.getMessage());
		} catch (Exception parseException) {
			logger.log(Level.WARNING,parseException.getMessage(),parseException);
		}
		if (cmd==null) {
			return;
		}
		
		try {
			updateDeviceData(cmd,t);
			if (t==0) {
				deviceManager.fireCommandReceived(cmd);
				
				// specials check
				if (cmd.getCommandName().equals(CommandName.reset_data)) {
					deviceManager.requestCommand(new Command(CommandName.info_data));
				}
				if (cmd.getCommandName().equals(CommandName.reset_conf)) {
					deviceManager.requestCommand(new Command(CommandName.info_conf,	"all"));
					deviceManager.requestCommand(new Command(CommandName.info_data));
					deviceManager.requestCommand(new Command(CommandName.info_prog));
				}
				if (cmd.getCommandName().equals(CommandName.reset_chip)) {
					Thread.sleep(2000); // wait for boot of chip
					deviceManager.requestCommand(new Command(CommandName.req_tx_echo,"0"));
					deviceManager.requestCommand(new Command(CommandName.req_tx_promt,"0"));
					deviceManager.requestCommand(new Command(CommandName.req_tx_push,"1"));
					deviceManager.requestCommand(new Command(CommandName.info_conf,	"all"));
					deviceManager.requestCommand(new Command(CommandName.info_data));
					deviceManager.requestCommand(new Command(CommandName.info_prog));
				}
			}
		} catch (Exception updateException) {
			logger.log(Level.WARNING,updateException.getMessage(),updateException);
		}
		
	}
	
	private void updateDeviceData(Command cmd,int t) {
		if (t==0) {
			deviceManager.getDeviceData().setDeviceParameter(cmd);
			return;
		}
		String res = cmd.getArgu0();
		if (t==0 && cmd.getCommandName().isIndexedA() && cmd.getCommandName().isIndexedB()==false) {
			res = cmd.getArgu1();
		}
		if (res==null) {
			return;
		}
		if ("null".equals(res)) {
			return; // todo check it.
		}
		int deviceVersion = deviceManager.getDeviceVersion();
		Integer value = new Integer(res);
		CommandName cmdName = cmd.getCommandName();
		if (t==1) {
			CommandNameVersionFactory.configCommandMax(deviceVersion, cmdName, value);
		} else if (t==2) {
			CommandNameVersionFactory.configCommandMapIndex(deviceVersion, cmdName, value);
			if (cmd.getArgu1()!=null) {
				CommandNameVersionFactory.configCommandMaxIndexTrigger(deviceVersion, cmdName, true);
			}
		} else if (t==3) {
			CommandNameVersionFactory.configCommandMaxIndexA(deviceVersion, cmdName, value);
			if (cmd.getArgu1()!=null && "null".equals(cmd.getArgu1())==false) {
				CommandNameVersionFactory.configCommandMaxIndexB(deviceVersion, cmdName, new Integer(cmd.getArgu1()));
			}
		} else if (t==4) {
			CommandNameVersionFactory.configCommandId(deviceVersion, cmdName, value);
		}
	}
	
	public void shutdown(){
		running = false;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public boolean hasSeenPromt() {
		return seenPromt;
	}
}

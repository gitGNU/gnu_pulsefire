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

package org.nongnu.pulsefire.device.serial;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.TooManyListenersException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.nongnu.pulsefire.device.DeviceCommandRequest;
import org.nongnu.pulsefire.wire.Command;
import org.nongnu.pulsefire.wire.CommandName;
import org.nongnu.pulsefire.wire.CommandNameVersionFactory;
import org.nongnu.pulsefire.wire.CommandWire;
import org.nongnu.pulsefire.wire.CommandWireException;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

/**
 * SerialDeviceWireThread is the backend communication thread of the serial device manager.
 * 
 * @author Willem Cazander
 * @see SerialDeviceWireManager
 */
public class SerialDeviceWireThread implements SerialPortEventListener {

	private Logger logger = null;
	private SerialDeviceWireManager deviceManager = null;
	private volatile SerialPort serialPort = null;
	private volatile Reader reader = null;
	private volatile Writer writer = null;
	private volatile boolean running = false;
	private volatile boolean seenPromt = false;
	private StringBuffer readBuffer = null;
	private volatile DeviceCommandRequest sendCommand = null;
	private volatile Object sendCommandLock = null;
	private LinkedBlockingQueue<String> processCmdQueue = null;
	static private final String PULSE_FIRE_PROMT = "root@pulsefire:";
	static private final String PULSE_FIRE_ERROR = "# Err:";
	
	public SerialDeviceWireThread(SerialDeviceWireManager deviceManager,SerialPort serialPort) throws IOException, TooManyListenersException {
		if(deviceManager==null) {
			throw new NullPointerException("Can't work with null deviceManager.");
		}
		if(serialPort==null) {
			throw new NullPointerException("Can't work with null serialPort.");
		}
		this.logger = Logger.getLogger(SerialDeviceWireThread.class.getName());
		this.deviceManager= deviceManager;
		this.serialPort=serialPort;
		sendCommandLock = new Object();
		processCmdQueue = new LinkedBlockingQueue<String>();
		readBuffer = new StringBuffer();
		reader = new InputStreamReader(serialPort.getInputStream(),Charset.forName("US-ASCII"));
		writer = new OutputStreamWriter(serialPort.getOutputStream(),Charset.forName("US-ASCII"));
		serialPort.addEventListener(this);
		serialPort.notifyOnDataAvailable(true);
		logger.info("Connected to port: "+serialPort.getName());
	}
	
	public void start() {
		Thread out = new Thread(new ProcessOutput());
		out.setName("PulseFire-Serial-Out");
		out.start();
		
		Thread in = new Thread(new ProcessInput());
		in.setName("PulseFire-Serial-In");
		in.start();
	}
	
	class ProcessOutput implements Runnable {
		public void run() {
			try {
				running = true;
				while (running) {
					Thread.sleep(50);
					if (sendCommand==null) {
						pollCommandRequest();
					} else {
						softReconnectDevice(true);
						Thread.sleep(100);
					}
				}
			} catch (Exception runException) {
				logger.log(Level.WARNING,runException.getMessage(),runException);
			} finally {
				logger.info("Closing port: "+serialPort.getName());
				try { reader.close();reader=null; } catch (IOException e) {}
				try { writer.close();writer=null; } catch (IOException e) {}
				serialPort.removeEventListener();
				serialPort.close();
				serialPort=null;
			}
		}
	}
	
	class ProcessInput implements Runnable {
		public void run() {
			try {
				Thread.sleep(500);
				while (running) {
					String data = processCmdQueue.poll(500, TimeUnit.MILLISECONDS);
					if (data!=null) {
						processInput(data);
					}
				}
			} catch (Exception runException) {
				logger.log(Level.WARNING,runException.getMessage(),runException);
			}
		}
	}
	
	/**
	 * Check for in system reboot detecting , this should get better someday
	 */
	private void softReconnectDevice(boolean testSendCommand) {
		if (deviceManager.isConnected()==false) {
			return; // don't check before we are connected.
		}
		synchronized (sendCommandLock) {
			if (sendCommand==null) {
				return; // nothing to check
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
			sendCommand = null;
			deviceManager.incTotalError();
		}

		// TODO: rm me tmp until crash bug fix fixed
		deviceManager.disconnect(true);
		
		// clear send buffer
		/*
		DeviceCommandRequest poll = deviceManager.pollCommandRequest();
		while (poll!=null) {
			poll.setResponse(poll.getRequest()); // release possible waiting requester 
			poll = deviceManager.pollCommandRequest();
		}	
		deviceManager.requestCommand(new Command(CommandName.req_tx_echo,"0"));
		deviceManager.requestCommand(new Command(CommandName.req_tx_promt,"0"));
		deviceManager.requestCommand(new Command(CommandName.info_conf));
		deviceManager.requestCommand(new Command(CommandName.info_data));
		if (deviceManager.getDeviceVersion() < 11) {
			deviceManager.requestCommand(new Command(CommandName.info_prog));
		}
		deviceManager.requestCommand(new Command(CommandName.req_tx_push,"1"));
		*/
		return;
	}
	
	private void pollCommandRequest() throws InterruptedException, IOException {
		DeviceCommandRequest send = deviceManager.pollWaitCommandRequest();
		if (send==null) {
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
			synchronized (sendCommandLock) {
				sendCommand = send;
			}
			deviceManager.fireDataSend(writeOut);
		} catch (IOException sendException) {
			if (sendException.getMessage().contains("writeArray")==false) {
				logger.log(Level.WARNING,sendException.getMessage(),sendException);
			} else {
				logger.log(Level.WARNING,sendException.getMessage());
			}
			deviceManager.disconnect(true);
		} catch (Exception e) {
			logger.log(Level.WARNING,e.getMessage(),e); // no disconnect on prog error
			deviceManager.incTotalError();
		}
	}
	
	@Override
	public void serialEvent(SerialPortEvent event) {
		if (event.getEventType() != SerialPortEvent.DATA_AVAILABLE) {
			return;
		}
		if (reader==null) {
			return;
		}
		try {
			int c = 0;
			while (reader.ready()) {
				c = reader.read();
				if (c == Command.LINE_END) {
					String scannedInput = readBuffer.toString().trim();
					readBuffer = new StringBuffer();
					logger.finest("Raw data read: "+scannedInput);
					processCmdQueue.add(scannedInput);
					continue;
				}
				readBuffer.append((char) c);
				if (seenPromt==false && readBuffer.toString().startsWith(PULSE_FIRE_PROMT)) {
					logger.finer("Seen promt continue login");
					seenPromt = true; // Remove promt from line and flag it.
				}
			}
		} catch (IOException e) {
			logger.log(Level.WARNING,"Error in serial event: "+e.getMessage(),e);
			deviceManager.incTotalError();
		}
	}
	
	private void processInput(String scannedInput) throws IOException {
		if (scannedInput.isEmpty()) {
			return; // skip empty lines from fire data.
		}
		
		deviceManager.fireDataReceived(scannedInput);
		
		if (scannedInput.startsWith(PULSE_FIRE_ERROR)) {
			synchronized (sendCommandLock) {
				if (sendCommand!=null) {
					sendCommand.setResponse(sendCommand.getRequest()); // release response code.
					sendCommand = null;
				}
			}
		}
		if (scannedInput.startsWith("#")) {
			return; // Skip all comments lines, except '# Err:' see above if.
		}
		
		if (scannedInput.startsWith(PULSE_FIRE_PROMT)) {
			//if (seenPromt && deviceManager.getDeviceData().getDeviceParameter(CommandName.req_tx_promt)!=null) {
			//	softReconnectDevice(false);
			//}
			// fix for input like promt: info_chip
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
		} else if (scannedInput.startsWith("@")) {
			scannedInput = scannedInput.substring(1);
			t = 5;
		}
		Command cmd = null;
		try {
			cmd = CommandWire.decodeCommand(scannedInput);
			logger.finer("Got cmd: "+cmd.getCommandName()+" with argu0: "+cmd.getArgu0());
			synchronized (sendCommandLock) {
				if (sendCommand!=null && sendCommand.getRequest().getCommandName().equals(cmd.getCommandName())) {
					sendCommand.setResponse(cmd);
					sendCommand = null;
				}
			}
		} catch (CommandWireException cwe) {
			deviceManager.incTotalError();
			logger.log(Level.WARNING,cwe.getMessage());
		} catch (Exception parseException) {
			deviceManager.incTotalError();
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
					if (deviceManager.getDeviceVersion() < 11) {
						deviceManager.requestCommand(new Command(CommandName.info_prog));
					}
				}
				if (cmd.getCommandName().equals(CommandName.reset_chip)) {
					Thread.sleep(2000); // wait for boot of chip
					deviceManager.requestCommand(new Command(CommandName.req_tx_echo,"0"));
					deviceManager.requestCommand(new Command(CommandName.req_tx_promt,"0"));
					deviceManager.requestCommand(new Command(CommandName.req_tx_push,"1"));
					deviceManager.requestCommand(new Command(CommandName.info_conf,"all"));
					deviceManager.requestCommand(new Command(CommandName.info_data));
					if (deviceManager.getDeviceVersion() < 11) {
						deviceManager.requestCommand(new Command(CommandName.info_prog));
					}
				}
			}
		} catch (Exception updateException) {
			deviceManager.incTotalError();
			logger.log(Level.WARNING,updateException.getMessage()+" while parsing: '"+scannedInput+"'",updateException);
		}
	}
	
	private void updateDeviceData(Command cmd,int t) {
		if (t==0) {
			deviceManager.getDeviceData().setDeviceParameter(cmd);
			return;
		}
		if (t==5) {
			// new in 1.1 full vars table copy with info_vars;
			int deviceVersion = deviceManager.getDeviceVersion();
			CommandName cmdName = cmd.getCommandName();
			int id = new Integer(cmd.getArgu0());
			int bitType = new Integer(cmd.getArgu1());
			int idxA = new Integer(cmd.getArgu2());
			int idxB = new Integer(cmd.getArgu3());
			int maxValue = new Integer(cmd.getArgu4());
			int bits = new Integer(cmd.getArgu5());
			//int defaultValue = new Integer(cmd.getArgu6());
			
			CommandNameVersionFactory.configCommandId(deviceVersion, cmdName, id);
			CommandNameVersionFactory.configCommandBitType(deviceVersion, cmdName, bitType);
			CommandNameVersionFactory.configCommandBits(deviceVersion, cmdName, bits);
			CommandNameVersionFactory.configCommandMax(deviceVersion, cmdName, maxValue);
			if ((bits & 8)==0) { // no map
				CommandNameVersionFactory.configCommandMapIndex(deviceVersion, cmdName, id);
			}
			if ((bits & 32)>0) { // is trig
				CommandNameVersionFactory.configCommandMaxIndexTrigger(deviceVersion, cmdName, true);
			}
			if (idxA>0) {
				CommandNameVersionFactory.configCommandMaxIndexA(deviceVersion, cmdName, idxA);
			}
			if (idxB>0) {
				CommandNameVersionFactory.configCommandMaxIndexB(deviceVersion, cmdName, idxB);
			}
			return;
		}
		
		String res = cmd.getArgu0();
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
		for (int i=0;i<10;i++) {
			try { Thread.sleep(100); } catch (InterruptedException e) {}
			if (serialPort==null) {
				break;
			}
		}
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public boolean hasSeenPromt() {
		return seenPromt;
	}
}

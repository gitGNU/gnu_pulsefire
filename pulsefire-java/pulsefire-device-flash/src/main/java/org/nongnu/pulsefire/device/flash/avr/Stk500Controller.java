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

import java.io.IOException;

import org.nongnu.pulsefire.device.flash.FlashControllerConfig;

/**
 * Stk500Controller encode and decodes stk500 messages and flash the device.
 * 
 * @author Willem Cazander
 */
public class Stk500Controller extends AbstractStk500Controller {

	private boolean logDebug = false;
	
	public void prepareMessagePrefix(FlashMessage msg,FlashCommandToken command) {
		msg.addRequestCommand(command);
	}
	public void prepareMessagePostfix(FlashMessage msg,FlashCommandToken command) {
		msg.addRequestCommand(Stk500Command.CRC_EOP);
	}
	public FlashMessage sendFlashMessage(FlashMessage message) throws IOException {
		if (message==null) {
			throw new NullPointerException("Can't send null message");
		}
		if (message.getRequest().isEmpty()) {
			throw new IllegalArgumentException("Can't send empty message");
		}
		StringBuilder buf = new StringBuilder(30);
		for (Integer data:message.getRequest()) {
			output.write(data);
			output.flush();
			
			String hex = Integer.toHexString(data);
			if (hex.length()==1) {
				hex = "0"+hex;
			}
			if (hex.startsWith("ffffff")) {
				hex = hex.substring(6);
			}
			buf.append(hex);
		}
		output.flush();
		if (logDebug) {
			logMessage("Send data: "+buf);
		}
		logger.finer("Send data: "+buf);
		
		int timeout = 1000;
		while(input.available()==0) {
			timeout--;
			if (timeout==0) {
				throw new IOException("timeout on read.");
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		int responseSize = 2;
		if (message.getRequest().get(0) == Stk500Command.STK_GET_PARAMETER.getToken()) {
			responseSize = 3; // extra read
		}
		buf = new StringBuilder(30);
		for (int i=0;i<responseSize;i++) {
			int data = input.read();
			message.getResponse().add(data);
			
			String hex = Integer.toHexString(data);
			if (hex.length()==1) {
				hex = "0"+hex;
			}
			if (hex.startsWith("ffffff")) {
				hex = hex.substring(6);
			}
			buf.append(hex);
		}
		if (logDebug) {
			logMessage("Read data: "+buf);
		}
		logger.finer("Read data: "+buf);
		return message;
	}
	
	
	public void flash(FlashControllerConfig flashControllerConfig) throws IOException {
		if (flashControllerConfig==null) {
			throw new NullPointerException("Can't flash with null config.");
		}
		flashControllerConfig.verifyConfig(); // check the config
		logConfig(flashControllerConfig);
		connectPort(flashControllerConfig);
		logDebug = flashControllerConfig.isLogDebug();
		if (output==null) {
			return;
		}
		progress = 1;
		rebootDevice();
		progress = 2;

		// Sync serial with device
		for (int i = 0; i < 5; i++) {
			doFlashCommand(Stk500Command.STK_GET_SYNC);
		}
		progress = 3;
		
		// Check if we are connected
		FlashMessage msg = doFlashCommand(Stk500Command.STK_GET_SYNC);
		if (msg.getResponse().size()!=2) {
			logMessage("not synced got !=2 bytes: "+msg.getResponse().size());
			return;
		}
		if (msg.getResponse().get(0) != Stk500Command.STK_INSYNC.getToken()) {
			logMessage("not in sync; got: "+msg.getResponse().get(0)+" hex: "+Integer.toHexString(msg.getResponse().get(0))+" wanted: "+Integer.toHexString(Stk500Command.STK_INSYNC.getToken()));
			return;
		}
		if (msg.getResponse().get(1) != Stk500Command.STK_OK.getToken()) {
			logMessage("not connected; got: "+msg.getResponse().get(1));
			return;
		}
		logMessage("Connected and in sync with device.");
		progress = 6;
		
		FlashMessage version = doFlashCommand(Stk500Command.STK_GET_PARAMETER,0x80);

		/*
		output.write(FlashCommand.STK_GET_PARAMETER.getOpCode());
		output.write(0x81);
		output.write(FlashCommand.CRC_EOP.getOpCode());
		output.write(FlashCommand.STK_GET_PARAMETER.getOpCode());
		output.write(0x83);
		output.write(FlashCommand.CRC_EOP.getOpCode());
		output.write(FlashCommand.STK_GET_PARAMETER.getOpCode());
		output.write(0x98);
		output.write(FlashCommand.CRC_EOP.getOpCode());		
		output.flush();
		
		
		response = input.read();
		logger.info("got2A: "+response+" hex: "+Integer.toHexString(response));
		response = input.read();
		logger.info("got2B: "+response+" hex: "+Integer.toHexString(response));
		response = input.read();
		logger.info("got2C: "+response+" hex: "+Integer.toHexString(response));
		*/
		
		
		progress = 10;
		byte[] dataBytes = flashControllerConfig.getFlashData();
		int pageSize = 0x80;
		int pages = dataBytes.length/pageSize;
		logMessage("Start flashing.");
		
		for (int i=0;i<=pages;i++) {
			progress = new Float((90.0f/pages)*i).intValue()+10;
			int address = (i*pageSize)/2;
			if (flashControllerConfig.isLogDebug()) {
				logMessage("Set address: "+Integer.toHexString(address));
			}
			doFlashCommand(Stk500Command.STK_LOAD_ADDRESS,address,address>>8);
			
			FlashMessage flash = new FlashMessage();
			prepareMessagePrefix(flash,Stk500Command.STK_PROG_PAGE);
			flash.getRequest().add(0);
			flash.getRequest().add(pageSize);
			flash.getRequest().add(0x46); // F = flash memory
			
			for (int d=0;d<pageSize;d++) {
				byte byteData = 0;
				int addr = (i*pageSize);
				if ((addr+d)<dataBytes.length) {
					byteData = dataBytes[addr+d]; // this is so last page gets filled with zeros to make square
				}
				flash.getRequest().add((int)byteData);
			}
			
			prepareMessagePostfix(flash,Stk500Command.STK_PROG_PAGE);
			flash = sendFlashMessage(flash);
			// check
		}
		logMessage("Flashing is done.");
		disconnectPort();
		progress = 100;
	}
}

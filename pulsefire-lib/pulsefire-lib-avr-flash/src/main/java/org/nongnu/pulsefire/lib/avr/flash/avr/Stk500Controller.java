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

package org.nongnu.pulsefire.lib.avr.flash.avr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.nongnu.pulsefire.lib.avr.flash.FlashControllerConfig;
import org.nongnu.pulsefire.lib.avr.flash.FlashException;

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
			logMessage("Send data: "+buf+" ("+Stk500Command.valueOfToken(message.getRequest().get(0))+")");
		}
		
		try { // This sleep is needed for windows flashing to work correctly.
			Thread.sleep(15); // (note: it worked when logDebug was enabled)
		} catch (InterruptedException e) {
		}
		
		int timeout = 500;
		while(input.available()==0) {
			timeout--;
			if (timeout==0) {
				throw new IOException("timeout on read.");
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
		
		int responseSize = 2;
		if (message.getRequest().get(0) == Stk500Command.STK_GET_PARAMETER.getToken()) {
			responseSize = 3; // extra read
		}
		if (message.getRequest().get(0) == Stk500Command.STK_UNIVERSAL.getToken()) {
			responseSize = 3; // extra read
		}
		if (message.getRequest().get(0) == Stk500Command.STK_READ_SIGN.getToken()) {
			responseSize = 4; // extra read
		}
		if (message.getRequest().get(0) == Stk500Command.STK_READ_PAGE.getToken()) {
			responseSize = 0x80+2; // extra read
		}
		buf = new StringBuilder(30);
		for (int i=0;i<responseSize;i++) {
		//while (true) {
			timeout = 500;
			while(input.available()==0) {
				timeout--;
				if (timeout==0) {
					throw new IOException("timeout on read.");
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
				}
			}
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
			
			//if (data == Stk500Command.STK_OK.getToken()) {
			//	break; // end of response
			//}
		}
		if (logDebug) {
			logMessage("Read data: "+buf);
		}
		return message;
	}
	
	
	protected void flashSafe(FlashControllerConfig flashControllerConfig) throws IOException,FlashException {
		if (flashControllerConfig==null) {
			throw new FlashException("Can't flash with null config.");
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
			throw new FlashException("not synced got !=2 bytes: "+msg.getResponse().size());
		}
		if (msg.getResponse().get(0) != Stk500Command.STK_INSYNC.getToken()) {
			throw new FlashException("not in sync; got: "+msg.getResponse().get(0)+" hex: "+Integer.toHexString(msg.getResponse().get(0))+" wanted: "+Integer.toHexString(Stk500Command.STK_INSYNC.getToken()));
		}
		if (msg.getResponse().get(1) != Stk500Command.STK_OK.getToken()) {
			throw new FlashException("not connected; got: "+msg.getResponse().get(1));
		}
		logInitSync();
		progress = 6;
		logInitProgrammer("Arduino");
		
		FlashMessage versionHw = doFlashCommand(Stk500Command.STK_GET_PARAMETER,0x80);
		FlashMessage versionSwMajor = doFlashCommand(Stk500Command.STK_GET_PARAMETER,0x81);
		FlashMessage versionSwMinor = doFlashCommand(Stk500Command.STK_GET_PARAMETER,0x82);
		logMessage("Hardware verion: "+(versionHw.getResponse().get(1)));
		logMessage("Firmware verion: "+(versionSwMajor.getResponse().get(1))+"."+(versionSwMinor.getResponse().get(1)));

		FlashMessage vtarget = doFlashCommand(Stk500Command.STK_GET_PARAMETER,0x84);
		FlashMessage varef = doFlashCommand(Stk500Command.STK_GET_PARAMETER,0x85);
		FlashMessage osc0 = doFlashCommand(Stk500Command.STK_GET_PARAMETER,0x86);
		FlashMessage osc1 = doFlashCommand(Stk500Command.STK_GET_PARAMETER,0x87);
		FlashMessage sck = doFlashCommand(Stk500Command.STK_GET_PARAMETER,0x89);
		double voltageTarget = new Double(vtarget.getResponse().get(1))/10;
		logMessage("Vtarget: "+voltageTarget);
		
		//FlashMessage deviceFlags = doFlashCommand(Stk500Command.STK_SET_DEVICE,0x86,0x00,0x00,0x01,0x01,0x01,0x01,0x03,0xFF,0xFF,0xFF,0xFF,0x00,0x80,0x04,0x00,0x00,0x00,0x80,0x00);
		//FlashMessage deviceFlagsExt = doFlashCommand(Stk500Command.STK_SET_DEVICE_EXT,0x05,0x04,0xd7,0xc2,0x00);

		FlashMessage enterProg = doFlashCommand(Stk500Command.STK_ENTER_PROGMODE);
		logMessage("AVR device initialized");
		
		FlashMessage deviceSign = doFlashCommand(Stk500Command.STK_READ_SIGN);
		int deviceId = deviceSign.getResponse().get(3) + (deviceSign.getResponse().get(2)<<8) + (deviceSign.getResponse().get(1)<<16);
		logMessage("Device signature: 0x"+Integer.toHexString(deviceId));
		if (flashControllerConfig.getDeviceSignature()>0 && flashControllerConfig.getDeviceSignature()!=deviceId) {
			throw new FlashException("Device signature is different: "+Integer.toHexString(deviceId)+" expected: "+Integer.toHexString(flashControllerConfig.getDeviceSignature()));
		}
		
		FlashMessage lFuse0 = doFlashCommand(Stk500Command.STK_UNIVERSAL,0x50,0x00,0x00,0x00);
		FlashMessage lFuse1 = doFlashCommand(Stk500Command.STK_UNIVERSAL,0x50,0x00,0x00,0x00);
		FlashMessage lFuse2 = doFlashCommand(Stk500Command.STK_UNIVERSAL,0x50,0x00,0x00,0x00);
		logMessage("lfuse value: 0x"+Integer.toHexString(lFuse2.getResponse().get(1)));
		
		FlashMessage hFuse0 = doFlashCommand(Stk500Command.STK_UNIVERSAL,0x58,0x08,0x00,0x00);
		FlashMessage hFuse1 = doFlashCommand(Stk500Command.STK_UNIVERSAL,0x58,0x08,0x00,0x00);
		FlashMessage hFuse2 = doFlashCommand(Stk500Command.STK_UNIVERSAL,0x58,0x08,0x00,0x00);
		logMessage("hfuse value: 0x"+Integer.toHexString(hFuse2.getResponse().get(1)));
		
		FlashMessage eFuse0 = doFlashCommand(Stk500Command.STK_UNIVERSAL,0x50,0x08,0x00,0x00);
		FlashMessage eFuse1 = doFlashCommand(Stk500Command.STK_UNIVERSAL,0x50,0x08,0x00,0x00);
		FlashMessage eFuse2 = doFlashCommand(Stk500Command.STK_UNIVERSAL,0x50,0x08,0x00,0x00);
		logMessage("efuse value: 0x"+Integer.toHexString(eFuse2.getResponse().get(1) & 0x07));
		
		// Erase flash
		if (flashControllerConfig.isFlashErase()) {
			logMessage("Erase flash memery.");
			FlashMessage eraseFlash0 = doFlashCommand(Stk500Command.STK_UNIVERSAL,0xA0,0x03,0xFC,0x00);
			FlashMessage eraseFlash1 = doFlashCommand(Stk500Command.STK_UNIVERSAL,0xA0,0x03,0xFD,0x00);
			FlashMessage eraseFlash2 = doFlashCommand(Stk500Command.STK_UNIVERSAL,0xA0,0x03,0xFE,0x00);
			FlashMessage eraseFlash3 = doFlashCommand(Stk500Command.STK_UNIVERSAL,0xA0,0x03,0xFF,0x00);
		}
		
		// Chip erase
		//FlashMessage eraseChip0 = doFlashCommand(Stk500Command.STK_UNIVERSAL,0xAC,0x80,0x00,0x00);
		//FlashMessage eraseChipGet0 = doFlashCommand(Stk500Command.STK_GET_PARAMETER,0x81);
		//FlashMessage eraseChipGet1 = doFlashCommand(Stk500Command.STK_GET_PARAMETER,0x82);
		//FlashMessage deviceFlags2 = doFlashCommand(Stk500Command.STK_SET_DEVICE,0x86,0x00,0x00,0x01,0x01,0x01,0x01,0x03,0xFF,0xFF,0xFF,0xFF,0x00,0x80,0x04,0x00,0x00,0x00,0x80,0x00);
		//FlashMessage deviceFlagsExt2 = doFlashCommand(Stk500Command.STK_SET_DEVICE_EXT,0x05,0x04,0xd7,0xc2,0x00);
		
		progress = 10;
		byte[] dataBytes = flashControllerConfig.getFlashData();
		int pageSize = 0x80;
		int pages = dataBytes.length/pageSize;
		logFlashStart();
		float flashTotalPercentage = 90.0f;
		if (flashControllerConfig.isFlashVerify()) {
			flashTotalPercentage = 80.0f;
		}
		
		for (int i=0;i<=pages;i++) {
			progress = new Float((flashTotalPercentage/pages)*i).intValue()+10;
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
		logFlashStop();
		if (flashControllerConfig.isFlashVerify()) {
			logMessage("Reading flash for verify.");
			List<Integer> readBytes = new ArrayList<Integer>(flashControllerConfig.getFlashData().length);
			for (int i=0;i<=pages;i++) {
				progress = new Float((10.0f/pages)*i).intValue()+90;
				int address = (i*pageSize)/2;
				if (flashControllerConfig.isLogDebug()) {
					logMessage("Set address: "+Integer.toHexString(address));
				}
				doFlashCommand(Stk500Command.STK_LOAD_ADDRESS,address,address>>8);
				
				FlashMessage flash = new FlashMessage();
				prepareMessagePrefix(flash,Stk500Command.STK_READ_PAGE);
				flash.getRequest().add(0);
				flash.getRequest().add(pageSize);
				flash.getRequest().add(0x46); // F = flash memory
				prepareMessagePostfix(flash,Stk500Command.STK_READ_PAGE);
				flash = sendFlashMessage(flash);
				for (int ii=2;ii<flash.getResponse().size();ii++) {
					Integer data = flash.getResponse().get(ii);
					readBytes.add(data);
				}
			}
			flashVerify(readBytes,flashControllerConfig.getFlashData());
		}
		progress = 100;
	}
}

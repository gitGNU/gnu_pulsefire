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
import java.util.ArrayList;
import java.util.List;

import org.nongnu.pulsefire.device.flash.FlashControllerConfig;
import org.nongnu.pulsefire.device.flash.FlashException;

/**
 * Stk500v2Controller encode and decodes stk500v2 messages and flash the device.
 * 
 * @author Willem Cazander
 */
public class Stk500v2Controller extends AbstractStk500Controller {
	
	private boolean logDebug = false;
	private int messageSeqenceNumber = 1;
	
	@Override
	public void prepareMessagePrefix(FlashMessage msg, FlashCommandToken command) {
		if (messageSeqenceNumber>255) {
			messageSeqenceNumber = 1;
		}
		msg.addRequestCommand(Stk500v2Command.MESSAGE_START);
		msg.getRequest().add(messageSeqenceNumber++);
		msg.getRequest().add(0);
		msg.getRequest().add(0); // body size
		msg.addRequestCommand(Stk500v2Command.TOKEN);
		msg.addRequestCommand(command);
	}

	@Override
	public void prepareMessagePostfix(FlashMessage msg, FlashCommandToken command) {
		int size = msg.getRequest().size()-5; // -5 = header
		if (size>0) {
			msg.getRequest().set(3,size);
			msg.getRequest().set(2,size>>8);
		}
		byte checksum = 0;
		for (Integer data:msg.getRequest()) {
			checksum = new Integer(checksum^data.byteValue()).byteValue();
		} 
		msg.getRequest().add(new Integer(checksum)); // add calculated checksum
	}
	
	@Override
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
			logMessage("Send data: "+buf+" ("+Stk500v2Command.valueOfToken(message.getRequest().get(5))+")");
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
		
		int msgStart = input.read();
		if (msgStart!=Stk500v2Command.MESSAGE_START.getToken()) {
			throw new IOException("got no message_start but: "+msgStart);
		}
		int msgNum = input.read();
		if (msgNum>256) {
			throw new IOException("to large message number: "+msgNum);
		}
		
		int sizeH = input.read();
		int sizeL = input.read();
		int size = sizeL + (sizeH<<8) + 1;
		buf = new StringBuilder(30);
		for (int i=0;i<size;i++) {
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
		int checksum = input.read();
		
		if (logDebug) {
			logMessage("Read data: "+buf+" sum: "+checksum);
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
			doFlashCommand(Stk500v2Command.CMD_SIGN_ON);
		}
		progress = 3;
		
		// Check if we are connected
		FlashMessage msg = doFlashCommand(Stk500v2Command.CMD_SIGN_ON);
		if (msg.getResponse().size()<4) {
			throw new FlashException("not synced got less then 4 bytes: "+msg.getResponse().size());
		}
		if (msg.getResponse().get(0) != Stk500v2Command.TOKEN.getToken()) {
			throw new FlashException("not in sync; got: "+msg.getResponse().get(0)+" hex: "+Integer.toHexString(msg.getResponse().get(0))+" wanted: "+Integer.toHexString(Stk500v2Command.TOKEN.getToken()));
		}
		if (msg.getResponse().get(1) != Stk500v2Command.CMD_SIGN_ON.getToken()) {
			throw new FlashException("not connected; got: "+msg.getResponse().get(1));
		}
		if (msg.getResponse().get(2) != Stk500v2Command.STATUS_CMD_OK.getToken()) {
			throw new FlashException("not connected; got: "+msg.getResponse().get(2));
		}
		logInitSync();
		StringBuilder buf = new StringBuilder(10);
		for (int i=4;i<msg.getResponse().size();i++) {
			buf.append(Character.toChars(msg.getResponse().get(i)));
		}
		progress = 6;
		logInitProgrammer(buf.toString());
		
		FlashMessage versionHw = doFlashCommand(Stk500v2Command.CMD_GET_PARAMETER,Stk500v2Command.PARAM_HW_VER.getToken());
		FlashMessage versionSwMajor = doFlashCommand(Stk500v2Command.CMD_GET_PARAMETER,Stk500v2Command.PARAM_SW_MAJOR.getToken());
		FlashMessage versionSwMinor = doFlashCommand(Stk500v2Command.CMD_GET_PARAMETER,Stk500v2Command.PARAM_SW_MINOR.getToken());
		logMessage("Hardware verion: "+(versionHw.getResponse().get(3) + (versionHw.getResponse().get(2)<<8)));
		logMessage("Firmware verion: "+(versionSwMajor.getResponse().get(3) + (versionSwMajor.getResponse().get(2)<<8))+"."+
				(versionSwMinor.getResponse().get(3) + (versionSwMinor.getResponse().get(2)<<8)));

		FlashMessage topCard = doFlashCommand(Stk500v2Command.CMD_GET_PARAMETER,Stk500v2Command.PARAM_TOPCARD_DETECT.getToken());
		logMessage("Topcard: "+Integer.toHexString(topCard.getResponse().get(3)));
		FlashMessage vTarget = doFlashCommand(Stk500v2Command.CMD_GET_PARAMETER,Stk500v2Command.PARAM_VTARGET.getToken());
		double voltageTarget = new Double(vTarget.getResponse().get(3))/10;
		logMessage("Vtarget: "+voltageTarget);
		FlashMessage sckTime = doFlashCommand(Stk500v2Command.CMD_GET_PARAMETER,Stk500v2Command.PARAM_SCK_DURATION.getToken());
		FlashMessage vAdjust = doFlashCommand(Stk500v2Command.CMD_GET_PARAMETER,Stk500v2Command.PARAM_VADJUST.getToken());
		FlashMessage oscP = doFlashCommand(Stk500v2Command.CMD_GET_PARAMETER,Stk500v2Command.PARAM_OSC_PSCALE.getToken());
		FlashMessage oscC = doFlashCommand(Stk500v2Command.CMD_GET_PARAMETER,Stk500v2Command.PARAM_OSC_CMATCH.getToken());
		
		FlashMessage resetPol = doFlashCommand(Stk500v2Command.CMD_SET_PARAMETER,Stk500v2Command.PARAM_RESET_POLARITY.getToken(),0x01);

		progress = 9;
		FlashMessage enterIsp = new FlashMessage();
		prepareMessagePrefix(enterIsp,Stk500v2Command.CMD_ENTER_PROGMODE_ISP);
		enterIsp.getRequest().add(0xC8); // timeout in ms
		enterIsp.getRequest().add(0x64); // stabDelay
		enterIsp.getRequest().add(0x19); // cmdexeDelay
		enterIsp.getRequest().add(0x20); // syncLoops
		enterIsp.getRequest().add(0x00); // byteDelay
		enterIsp.getRequest().add(0x53); // pollValue
		enterIsp.getRequest().add(0x03); // pollIndex
		enterIsp.getRequest().add(0xAC); // cmd1
		enterIsp.getRequest().add(0x53); // cmd2
		enterIsp.getRequest().add(0x00); // cmd3
		enterIsp.getRequest().add(0x00); // cmd4
		prepareMessagePostfix(enterIsp,Stk500v2Command.CMD_ENTER_PROGMODE_ISP);
		enterIsp = sendFlashMessage(enterIsp);
		logMessage("AVR device initialized");
		
		FlashMessage deviceId0 = doFlashCommand(Stk500v2Command.CMD_SPI_MULTI,0x04,0x04,0x00,0x30,0x00,0x00,0x00);
		FlashMessage deviceId1 = doFlashCommand(Stk500v2Command.CMD_SPI_MULTI,0x04,0x04,0x00,0x30,0x00,0x01,0x00);
		FlashMessage deviceId2 = doFlashCommand(Stk500v2Command.CMD_SPI_MULTI,0x04,0x04,0x00,0x30,0x00,0x02,0x00);
		int deviceId = deviceId2.getResponse().get(6) + (deviceId1.getResponse().get(6)<<8) + (deviceId0.getResponse().get(6)<<16);
		logMessage("Device signature: 0x"+Integer.toHexString(deviceId));
		if (flashControllerConfig.getDeviceSignature()>0 && flashControllerConfig.getDeviceSignature()!=deviceId) {
			throw new FlashException("Device signature is different: "+Integer.toHexString(deviceId)+" expected: "+Integer.toHexString(flashControllerConfig.getDeviceSignature()));
		}
		
		FlashMessage lFuse0 = doFlashCommand(Stk500v2Command.CMD_SPI_MULTI,0x04,0x04,0x00,0x50,0x00,0x00,0x00);
		FlashMessage lFuse1 = doFlashCommand(Stk500v2Command.CMD_SPI_MULTI,0x04,0x04,0x00,0x50,0x00,0x00,0x00);
		FlashMessage lFuse2 = doFlashCommand(Stk500v2Command.CMD_SPI_MULTI,0x04,0x04,0x00,0x50,0x00,0x00,0x00);
		logMessage("lfuse value: 0x"+Integer.toHexString(lFuse2.getResponse().get(6)));
		
		FlashMessage hFuse0 = doFlashCommand(Stk500v2Command.CMD_SPI_MULTI,0x04,0x04,0x00,0x58,0x08,0x00,0x00);
		FlashMessage hFuse1 = doFlashCommand(Stk500v2Command.CMD_SPI_MULTI,0x04,0x04,0x00,0x58,0x08,0x00,0x00);
		FlashMessage hFuse2 = doFlashCommand(Stk500v2Command.CMD_SPI_MULTI,0x04,0x04,0x00,0x58,0x08,0x00,0x00);
		logMessage("hfuse value: 0x"+Integer.toHexString(hFuse2.getResponse().get(6)));
		
		FlashMessage eFuse0 = doFlashCommand(Stk500v2Command.CMD_SPI_MULTI,0x04,0x04,0x00,0x50,0x08,0x00,0x00);
		FlashMessage eFuse1 = doFlashCommand(Stk500v2Command.CMD_SPI_MULTI,0x04,0x04,0x00,0x50,0x08,0x00,0x00);
		FlashMessage eFuse2 = doFlashCommand(Stk500v2Command.CMD_SPI_MULTI,0x04,0x04,0x00,0x50,0x08,0x00,0x00);
		logMessage("efuse value: 0x"+Integer.toHexString(eFuse2.getResponse().get(6) & 0x07)); // only lower 3 bits for efuse
		
		// Erase flash
		if (flashControllerConfig.isFlashErase()) {
			logMessage("Erase flash memery.");
			FlashMessage eraseFlash0 = doFlashCommand(Stk500v2Command.CMD_SPI_MULTI,0x04,0x04,0x00,0xA0,0x03,0xFC,0x00);
			FlashMessage eraseFlash1 = doFlashCommand(Stk500v2Command.CMD_SPI_MULTI,0x04,0x04,0x00,0xA0,0x03,0xFD,0x00);
			FlashMessage eraseFlash2 = doFlashCommand(Stk500v2Command.CMD_SPI_MULTI,0x04,0x04,0x00,0xA0,0x03,0xFE,0x00);
			FlashMessage eraseFlash3 = doFlashCommand(Stk500v2Command.CMD_SPI_MULTI,0x04,0x04,0x00,0xA0,0x03,0xFF,0x00);
		}
		
		// Chip erase
		FlashMessage eraseChip0 = doFlashCommand(Stk500v2Command.CMD_CHIP_ERASE_ISP,0x09,0x00,0xAC,0x9F,0x7F,0x00);
		FlashMessage eraseChipReset = doFlashCommand(Stk500v2Command.CMD_SET_PARAMETER,Stk500v2Command.PARAM_RESET_POLARITY.getToken(),0x01);
		FlashMessage eraseEnterIsp = new FlashMessage();
		prepareMessagePrefix(eraseEnterIsp,Stk500v2Command.CMD_ENTER_PROGMODE_ISP);
		eraseEnterIsp.getRequest().add(0xC8); // timeout in ms
		eraseEnterIsp.getRequest().add(0x64); // stabDelay
		eraseEnterIsp.getRequest().add(0x19); // cmdexeDelay
		eraseEnterIsp.getRequest().add(0x20); // syncLoops
		eraseEnterIsp.getRequest().add(0x00); // byteDelay
		eraseEnterIsp.getRequest().add(0x53); // pollValue
		eraseEnterIsp.getRequest().add(0x03); // pollIndex
		eraseEnterIsp.getRequest().add(0xAC); // cmd1
		eraseEnterIsp.getRequest().add(0x53); // cmd2
		eraseEnterIsp.getRequest().add(0x00); // cmd3
		eraseEnterIsp.getRequest().add(0x00); // cmd4
		prepareMessagePostfix(eraseEnterIsp,Stk500v2Command.CMD_ENTER_PROGMODE_ISP);
		eraseEnterIsp = sendFlashMessage(eraseEnterIsp);
		
		progress = 10;
		byte[] dataBytes = flashControllerConfig.getFlashData();
		int pageSize = 0x80;
		int pages = dataBytes.length/pageSize;
		logFlashStart();
		float flashTotalPercentage = 90.0f;
		if (flashControllerConfig.isFlashVerify()) {
			flashTotalPercentage = 80.0f;
		}
		doFlashCommand(Stk500v2Command.CMD_LOAD_ADDRESS,0,0,0,0);
		
		for (int i=0;i<=pages;i++) {
			progress = new Float((flashTotalPercentage/pages)*i).intValue()+10;
			
			FlashMessage flash = new FlashMessage();
			prepareMessagePrefix(flash,Stk500v2Command.CMD_PROGRAM_FLASH_ISP);
			flash.getRequest().add(0);
			flash.getRequest().add(pageSize);
			flash.getRequest().add(0xC1); // mode
			flash.getRequest().add(0x06); // delay
			flash.getRequest().add(0x40); // cmd1
			flash.getRequest().add(0x4C); // cmd2
			flash.getRequest().add(0x20); // cmd3
			flash.getRequest().add(0xFF); // poll1
			flash.getRequest().add(0xFF); // poll2
			
			for (int d=0;d<pageSize;d++) {
				byte byteData = 0;
				int addr = (i*pageSize);
				if ((addr+d)<dataBytes.length) {
					byteData = dataBytes[addr+d]; // this is so last page gets filled with zeros to make square
				}
				flash.getRequest().add((int)byteData);
			}
			
			prepareMessagePostfix(flash,Stk500v2Command.CMD_PROGRAM_FLASH_ISP);
			flash = sendFlashMessage(flash);
			// check
		}
		logFlashStop();
		
		if (flashControllerConfig.isFlashVerify()) {
			logMessage("Reading flash for verify.");
			doFlashCommand(Stk500v2Command.CMD_LOAD_ADDRESS,0,0,0,0);
			List<Integer> readBytes = new ArrayList<Integer>(flashControllerConfig.getFlashData().length);
			for (int i=0;i<=pages;i++) {
				progress = new Float((10.0f/pages)*i).intValue()+90;
				
				FlashMessage flash = new FlashMessage();
				prepareMessagePrefix(flash,Stk500v2Command.CMD_READ_FLASH_ISP);
				flash.getRequest().add(0x00);
				flash.getRequest().add(pageSize);
				flash.getRequest().add(0x20);
				prepareMessagePostfix(flash,Stk500v2Command.CMD_READ_FLASH_ISP);
				flash = sendFlashMessage(flash);
				for (int ii=3;ii<flash.getResponse().size()-1;ii++) {
					Integer data = flash.getResponse().get(ii);
					readBytes.add(data);
				}
			}
			flashVerify(readBytes,flashControllerConfig.getFlashData());
		}
		
		FlashMessage leaveIsp = new FlashMessage();
		prepareMessagePrefix(leaveIsp,Stk500v2Command.CMD_LEAVE_PROGMODE_ISP);
		leaveIsp.getRequest().add(0x01);
		leaveIsp.getRequest().add(0x01);
		prepareMessagePostfix(leaveIsp,Stk500v2Command.CMD_LEAVE_PROGMODE_ISP);
		leaveIsp = sendFlashMessage(leaveIsp);
		
		progress = 100;
	}
}

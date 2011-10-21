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

/**
 * Stk500v2Controller encode and decodes stk500v2 messages and flash the device.
 * 
 * @author Willem Cazander
 */
public class Stk500v2Controller extends AbstractStk500Controller {

	int messageSeqenceNumber = 1;
	
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
		logger.info("Send data: "+buf);
		
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
		logger.info("Read data: "+buf);
		int checksum = input.read();
		logger.info("Got checksum return: "+checksum);
		
		return message;
	}
	
	public void flash() throws IOException {
		connectPort(getPort());
		if (output==null) {
			return;
		}
		rebootDevice();

		// Sync serial with device
		for (int i = 0; i < 5; i++) {
			doFlashCommand(Stk500v2Command.CMD_SIGN_ON);
		}
		
		// Check if we are connected
		/*
		FlashMessage msg = doFlashCommand(Stk500v2Command.CMD_SIGN_ON);
		
		logger.info("got: "+msg.getResponse().size());
		if (msg.getResponse().size()!=2) {
			logger.info("not synced got !=2 bytes: "+msg.getResponse().size());
			return;
		}
		if (msg.getResponse().get(0) != Stk500Command.STK_INSYNC.getToken()) {
			logger.info("not in sync; got: "+msg.getResponse().get(0)+" hex: "+Integer.toHexString(msg.getResponse().get(0))+" wanted: "+Integer.toHexString(Stk500Command.STK_INSYNC.getToken()));
			return;
		}
		if (msg.getResponse().get(1) != Stk500Command.STK_OK.getToken()) {
			logger.info("not connected; got: "+msg.getResponse().get(1));
			return;
		}
		
		*/
		FlashMessage cmdOsc = doFlashCommand(Stk500v2Command.CMD_OSCCAL);
		
		FlashMessage version = doFlashCommand(Stk500v2Command.CMD_GET_PARAMETER,Stk500v2Command.PARAM_HW_VER.getToken());
		
		
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
		
		byte[] dataBytes = getFlashData();
		int pageSize = 0x80;
		int pages = dataBytes.length/pageSize;
		
		for (int i=0;i<=pages;i++) {
			int address = (i*pageSize)/2; 
			logger.info("Set address to: "+Integer.toHexString(address));
			doFlashCommand(Stk500v2Command.CMD_LOAD_ADDRESS,0,0,address,address>>8);
			
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
		disconnectPort();
	}
}

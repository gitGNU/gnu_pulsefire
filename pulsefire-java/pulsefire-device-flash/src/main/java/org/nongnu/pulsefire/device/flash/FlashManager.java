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

package org.nongnu.pulsefire.device.flash;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.Charset;

import org.nongnu.pulsefire.device.flash.avr.Stk500Controller;
import org.nongnu.pulsefire.device.flash.avr.Stk500v2Controller;

/**
 * FlashManager can load the hex file and flash devices based on settings.
 * 
 * @author Willem Cazander
 */
public class FlashManager extends AbstractFlashProgramController {

	private String protocol = null;
	private FlashProgramController backendController = null;

	static public void main(String argu[]) {
		try {
			FlashManager fm = new FlashManager();
			for(String arg:argu) {
				if (arg.startsWith("-t=")) {
					fm.setProtocol(arg.substring(3));
				}
				if (arg.startsWith("-p=")) {
					fm.setPort(arg.substring(3));
				}
				if (arg.startsWith("-pp=")) {
					fm.setPortParameter(arg.substring(4));
				}
				if (arg.startsWith("-f=")) {
					fm.loadHex(new File(arg.substring(3)));
				}
			}
			fm.flash();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void loadHex(File hexFile) throws FileNotFoundException, IOException {
		setFlashData(readHexData(new FileInputStream(hexFile)));	
	}
	
	public void loadHex(String hexResource) throws IOException {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (cl==null) {
			cl = this.getClass().getClassLoader();
		}
		setFlashData(readHexData(cl.getResourceAsStream(hexResource)));
	}
	
	public void flash() throws IOException {
		if (getProtocol()==null) {
			throw new IllegalStateException("Can flash without protocol.");
		}
		if (getPort()==null) {
			throw new IllegalStateException("Can flash without port.");
		}
		if (getFlashData()==null) {
			throw new IllegalStateException("Can flash without flashdata.");
		}
		if ("stk500".equals(getProtocol())) {
			backendController = new Stk500Controller();
		} else if ("arduino".equals(getProtocol())) {
			backendController = new Stk500Controller();
		} else if ("stk500v2".equals(getProtocol())) {
			backendController = new Stk500v2Controller();
		} else {
			throw new IllegalStateException("Unknow protocol: "+getProtocol());
		}
		backendController.setPort(getPort());
		backendController.setPortParameter(getPortParameter());
		backendController.setFlashData(getFlashData());
		backendController.flash();
	}
	
	/**
	 * @see org.nongnu.pulsefire.device.flash.AbstractFlashProgramController#getProgress()
	 */
	@Override
	public int getProgress() {
		if (backendController==null) {
			return 0;
		}
		return backendController.getProgress();
	}

	/**
	 * @return the protocol
	 */
	public String getProtocol() {
		return protocol;
	}

	/**
	 * @param protocol the protocol to set
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	static private int INTEGER_HEX = 16;
	public byte[] readHexData(InputStream input) throws IOException {
		ByteArrayOutputStream resultData = new ByteArrayOutputStream(32768);;
		LineNumberReader reader = null;
		try {
			reader = new LineNumberReader(new InputStreamReader(input,Charset.forName("US-ASCII")));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith(":")==false) {
					continue;
				}
				int byteCount = Integer.parseInt(line.substring(1,3), INTEGER_HEX);
				int address = Integer.parseInt(line.substring(3, 7), INTEGER_HEX);
				int recordType = Integer.parseInt(line.substring(7,9), INTEGER_HEX);
				byte[] dataLine = new byte[byteCount];
				for (int i=0;i<byteCount;i++) {
					int idx = 9+(2*i);
					byte dataByte = (byte)Integer.parseInt(line.substring(idx,idx+2), INTEGER_HEX);
					dataLine[i] = dataByte;
				}
				byte checksumLine = new Integer(Integer.parseInt(line.substring(line.length()-2,line.length()), INTEGER_HEX)).byteValue();
				byte checksumParsed = new Integer(byteCount+(address&0xFF)+(address>>8)+recordType).byteValue();
				for (int i=0;i<byteCount;i++) {
					checksumParsed += dataLine[i];
				}
				checksumParsed = new Integer((checksumParsed % 255) * 0xFF).byteValue(); 
				if (checksumLine != checksumParsed) {
					throw new IllegalArgumentException("Invalid checksum on line: "+reader.getLineNumber()+" expected: "+Integer.toHexString(checksumLine)+" got: "+Integer.toHexString(checksumParsed));
				}
				resultData.write(dataLine); // append correct data to data array
			}
		} finally {
			if (reader!=null) {
				reader.close();
			}
			if (input!=null) {
				input.close();
			}
		}
		return resultData.toByteArray();
	}
}

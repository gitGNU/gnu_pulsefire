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

import java.io.File;
import java.io.FileOutputStream;

import org.nongnu.pulsefire.lib.avr.flash.FlashHexReader;

import junit.framework.TestCase;

/**
 * FlashHexReaderTest does simple test with read/writer of hex files.
 * 
 * @author Willem Cazander
 */
public class FlashHexReaderTest extends TestCase {

	static String SAMPLE_HEX_RESOURCE = "org/nongnu/pulsefire/device/flash/sample.hex";
	
	public void testRead() throws Exception {
		FlashHexReader hex = new FlashHexReader();
		byte[] data = hex.loadHex(SAMPLE_HEX_RESOURCE);
		assertEquals(true,data.length>0);
	}
	
	public void testWrite() throws Exception {
		FlashHexReader hex = new FlashHexReader();
		byte[] data = hex.loadHex(SAMPLE_HEX_RESOURCE);
		File tmp = File.createTempFile("test-write", ".hex");
		hex.writeHexData(data, new FileOutputStream(tmp));
		byte[] data2 = hex.loadHex(tmp);
		assertEquals("Check size",data.length , data2.length);
		for (int i=0;i<data.length;i++) {
			byte orgByte = data[i];
			byte newByte = data2[i];
			assertEquals("on address: "+i,orgByte , newByte);
		}
		tmp.delete();
	}
}

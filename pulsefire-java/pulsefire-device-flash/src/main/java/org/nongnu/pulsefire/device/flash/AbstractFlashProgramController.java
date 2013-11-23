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

import java.util.ArrayList;
import java.util.List;

/**
 * AbstractFlashProgramController holds all the basics of the FlashProgramController fields and methods.
 * 
 * @author Willem Cazander
 */
abstract public class AbstractFlashProgramController implements FlashProgramController {

	protected List<FlashLogListener> flashLogListeners = null;
	protected int progress = 0;
	private long flashStartTime = 0l;

	public AbstractFlashProgramController() {
		flashLogListeners = new ArrayList<FlashLogListener>(3);
	}

	/**
	 * Returns the progress of flashing
	 */
	public int getProgress() {
		return progress;
	}
	
	public void addFlashLogListener(FlashLogListener flashLogListener) {
		flashLogListeners.add(flashLogListener);
	}
	
	public void removeFlashLogListener(FlashLogListener flashLogListener) {
		flashLogListeners.remove(flashLogListener);
	}

	protected void logMessage(String message) {
		for (int i=flashLogListeners.size()-1;i>=0;i--) {
			FlashLogListener l = flashLogListeners.get(i);
			l.flashLogMessage(message);
		}
	}
	
	protected void logInitSync() {
		logMessage("Synced communication.");
	}
	
	protected void logInitProgrammer(String programmer) {
		logMessage("Programmer: "+programmer);
	}
	
	protected void logFlashStart() {
		logMessage("Start flashing...");
		flashStartTime = System.currentTimeMillis();
	}
	
	protected void logFlashStop() {
		long totalTime = System.currentTimeMillis()-flashStartTime;
		logMessage("Flashing is done in: "+totalTime+" ms.");
	}
	
	protected void flashVerify(List<Integer> readBytes,byte[] flashData) throws FlashException {
		logMessage("Verify flash data...");
		for (int ii=0;ii<flashData.length;ii++) {
			byte burnData = flashData[ii];
			if (ii>readBytes.size()) {
				throw new FlashException("Missing backread bytes to verify");
			}
			byte readData = readBytes.get(ii).byteValue();
			if (burnData!=readData) {
				throw new FlashException("Mismatch on address: "+Integer.toHexString(ii)+" expected: "+Integer.toHexString(burnData)+" got: "+Integer.toHexString(readData));
			}
		}
		logMessage("Verified "+readBytes.size()+" bytes flash oke.");
	}
}

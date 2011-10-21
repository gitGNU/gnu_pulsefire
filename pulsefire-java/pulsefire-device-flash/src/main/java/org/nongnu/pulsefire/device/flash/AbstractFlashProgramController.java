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

/**
 * AbstractFlashProgramController holds all the basics of the FlashProgramController fields and methods.
 * 
 * @author Willem Cazander
 */
abstract public class AbstractFlashProgramController implements FlashProgramController {

	protected String port = null;
	protected String portParameter = null;
	protected byte[] flashData = null;
	protected int progress = 0;
	
	/**
	 * @return the port
	 */
	public String getPort() {
		return port;
	}
	
	/**
	 * @param port the port to set
	 */
	public void setPort(String port) {
		this.port = port;
	}
	
	/**
	 * @return the portParameter
	 */
	public String getPortParameter() {
		return portParameter;
	}
	
	/**
	 * @param portParameter the portParameter to set
	 */
	public void setPortParameter(String portParameter) {
		this.portParameter = portParameter;
	}
	
	/**
	 * @return the flashData
	 */
	public byte[] getFlashData() {
		return flashData;
	}
	
	/**
	 * @param flashData the flashData to set
	 */
	public void setFlashData(byte[] flashData) {
		this.flashData = flashData;
	}
	
	/**
	 * Returns the progress of flashing
	 */
	public int getProgress() {
		return progress;
	}
}

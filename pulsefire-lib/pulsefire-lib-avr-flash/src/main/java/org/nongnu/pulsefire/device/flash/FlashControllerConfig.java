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
 * FlashControllerConfig holds all the config parameters so the flash controller can flash.
 * 
 * @author Willem Cazander
 */
public class FlashControllerConfig {

	private String port = null;
	private String portParameter = null;
	private String portProtocol = null;
	private int deviceSignature = -1;
	private byte[] flashData = null;
	private boolean flashVerify = false;
	private boolean flashErase = false;
	private boolean logDebug = false;
	private String nativeFlashCmd = null;
	private String nativeFlashConfig = null;
	
	public FlashControllerConfig() {
	}
	public FlashControllerConfig(String port,String portProtocol,byte[] flashData) {
		setPort(port);
		setPortProtocol(portProtocol);
		setFlashData(flashData);
	}
	
	public void verifyConfig() throws IllegalArgumentException,NullPointerException {
		if (port==null) {
			throw new NullPointerException("Can't connect to null port.");
		}
		if (portProtocol==null) {
			throw new NullPointerException("Can't flash with null portProtocol.");
		}
		if (flashData==null) {
			throw new NullPointerException("Can't flash to null flashData.");
		}
	}
	
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
	 * @return the portProtocol
	 */
	public String getPortProtocol() {
		return portProtocol;
	}
	
	/**
	 * @param portProtocol the portProtocol to set
	 */
	public void setPortProtocol(String portProtocol) {
		this.portProtocol = portProtocol;
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
	 * @return the flashVerify
	 */
	public boolean isFlashVerify() {
		return flashVerify;
	}
	
	/**
	 * @param flashVerify the flashVerify to set
	 */
	public void setFlashVerify(boolean flashVerify) {
		this.flashVerify = flashVerify;
	}
	
	/**
	 * @return the flashErase
	 */
	public boolean isFlashErase() {
		return flashErase;
	}
	
	/**
	 * @param flashErase the flashErase to set
	 */
	public void setFlashErase(boolean flashErase) {
		this.flashErase = flashErase;
	}
	
	/**
	 * @return the logDebug
	 */
	public boolean isLogDebug() {
		return logDebug;
	}
	
	/**
	 * @param logDebug the logDebug to set
	 */
	public void setLogDebug(boolean logDebug) {
		this.logDebug = logDebug;
	}
	
	/**
	 * @return the deviceSignature
	 */
	public int getDeviceSignature() {
		return deviceSignature;
	}
	
	/**
	 * @param deviceSignature the deviceSignature to set
	 */
	public void setDeviceSignature(int deviceSignature) {
		this.deviceSignature = deviceSignature;
	}
	
	/**
	 * @return the nativeFlashCmd
	 */
	public String getNativeFlashCmd() {
		return nativeFlashCmd;
	}
	
	/**
	 * @param nativeFlashCmd the nativeFlashCmd to set
	 */
	public void setNativeFlashCmd(String nativeFlashCmd) {
		this.nativeFlashCmd = nativeFlashCmd;
	}
	
	/**
	 * @return the nativeFlashConfig
	 */
	public String getNativeFlashConfig() {
		return nativeFlashConfig;
	}
	
	/**
	 * @param nativeFlashConfig the nativeFlashConfig to set
	 */
	public void setNativeFlashConfig(String nativeFlashConfig) {
		this.nativeFlashConfig = nativeFlashConfig;
	}
}

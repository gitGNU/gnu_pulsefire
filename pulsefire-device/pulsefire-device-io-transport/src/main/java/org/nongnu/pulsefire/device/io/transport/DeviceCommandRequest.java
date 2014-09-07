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

package org.nongnu.pulsefire.device.io.transport;

import org.nongnu.pulsefire.device.io.protocol.Command;
import org.nongnu.pulsefire.device.io.protocol.CommandName;
import org.nongnu.pulsefire.device.io.protocol.CommandVariableType;

/**
 * DeviceCommandRequest is class to hold the request and response toughter.
 * 
 * @author Willem Cazander
 */
public class DeviceCommandRequest {
	
	private Command request = null;
	private Command response = null;
	private Exception error = null;
	private long requestTime = 0l;
	private long responseTime = 0l;
	
	public DeviceCommandRequest(Command request) {
		if(request==null) {
			throw new NullPointerException("request may not be null");
		}
		this.request=request;
		this.requestTime=System.currentTimeMillis();
	}
	
	public void waitForResponseChecked() {
		waitForResponse();
		if (response==null) {
			throw new IllegalStateException("Response is null from request "+request.getCommandName().name());
		}
		if (request==response) {
			throw new IllegalStateException("Response is equal to request.");
		}
		if (request.getCommandName()==CommandName.help && "done".equals(response.getArgu0())) {
			return; // multi line help ends with done
		}
		if (request.getCommandName().getType()==CommandVariableType.CMD && "done".equals(response.getArgu0())) {
			return; // multi line info cmds ends with done
		}
		if (request.getArgu0().equals(response.getArgu0())==false) {
			throw new IllegalStateException("Response argument is different excepted: '"+request.getArgu0()+"' raw: '"+request.getLineRaw()+"' got: '"+response.getLineRaw()+"'");
		}
	}
	
	public void waitForResponse() {
		for (int i=0;i<150;i++) {
			if (response!=null) {
				return; // we wait max 5 seconds ( info_data is long )
			}
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				return;
			}
		}
		return;
	}
	
	public Command getRequest() {
		return request;
	}
	
	public void setResponse(Command response) {
		if(response==null) {
			throw new NullPointerException("response may not be null");
		}
		this.response=response;
		this.responseTime=System.currentTimeMillis();
	}
	
	public Command getResponse() {
		return response;
	}
	
	public void setError(Exception error) {
		this.error=error;
	}
	
	public Exception getError() {
		return error;
	}

	public long getRequestTime() {
		return requestTime;
	}

	public long getResponseTime() {
		return responseTime;
	}
}

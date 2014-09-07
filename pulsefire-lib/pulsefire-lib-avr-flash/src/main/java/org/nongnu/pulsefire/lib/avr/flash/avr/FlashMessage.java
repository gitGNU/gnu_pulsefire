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

import java.util.ArrayList;
import java.util.List;

/**
 * FlashMessage holds the request and response of a message to the flash device.
 * 
 * @author Willem Cazander
 */
public class FlashMessage {

	private List<Integer> request = null;
	private List<Integer> response = null;
	
	public FlashMessage() {
		request=new ArrayList<Integer>(10);
		response=new ArrayList<Integer>(10);
	}
	
	public FlashMessage(Stk500Command command,Stk500Command ... cmds) {
		this();
		request.add(command.getToken());
		for (Stk500Command cmd:cmds) {
			request.add(cmd.getToken());
		}
	}

	/**
	 * @return the request
	 */
	public List<Integer> getRequest() {
		return request;
	}

	public void addRequestCommand(FlashCommandToken command) {
		request.add(command.getToken());
	}
	
	/**
	 * @return the response
	 */
	public List<Integer> getResponse() {
		return response;
	}
}

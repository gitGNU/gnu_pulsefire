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
import java.io.IOException;

import org.nongnu.pulsefire.device.flash.avr.Stk500Controller;
import org.nongnu.pulsefire.device.flash.avr.Stk500v2Controller;

/**
 * FlashManager can load the hex file and flash devices based on settings.
 * 
 * @author Willem Cazander
 */
public class FlashManager {

	static public void main(String argu[]) {
		try {
			FlashControllerConfig config = new FlashControllerConfig();
			for(String arg:argu) {
				if (arg.startsWith("-v")) {
					config.setLogDebug(true);
				}
				if (arg.startsWith("-V")) {
					config.setFlashVerify(true);
				}
				if (arg.startsWith("-t=")) {
					config.setPortProtocol(arg.substring(3));
				}
				if (arg.startsWith("-p=")) {
					config.setPort(arg.substring(3));
				}
				if (arg.startsWith("-pp=")) {
					config.setPortParameter(arg.substring(4));
				}
				if (arg.startsWith("-f=")) {
					config.setFlashData(new FlashHexReader().loadHex(new File(arg.substring(3))));
				}
			}
			FlashProgramController fm = createFlashController(config);
			fm.addFlashLogListener(new FlashLogListener() {
				@Override
				public void flashLogMessage(String message) {
					System.out.println(message);
				}
			});
			fm.flash(config);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	static public FlashProgramController createFlashController(FlashControllerConfig flashControllerConfig) {
		if (flashControllerConfig==null) {
			throw new NullPointerException("Can't fill flash backend with null config.");
		}
		flashControllerConfig.verifyConfig(); // check the config
		FlashProgramController backendController = null;
		if ("stk500".equals(flashControllerConfig.getPortProtocol())) {
			backendController = new Stk500Controller();
		} else if ("arduino".equals(flashControllerConfig.getPortProtocol())) {
			backendController = new Stk500Controller();
		} else if ("stk500v2".equals(flashControllerConfig.getPortProtocol())) {
			backendController = new Stk500v2Controller();
		} else {
			throw new IllegalStateException("Unknow port protocol: "+flashControllerConfig.getPortProtocol());
		}
		return backendController;
	}
}

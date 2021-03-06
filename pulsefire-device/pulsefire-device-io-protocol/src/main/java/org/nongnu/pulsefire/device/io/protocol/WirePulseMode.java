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

package org.nongnu.pulsefire.device.io.protocol;

/**
 * WirePulseMode describs all pulse modes.
 * 
 * @author Willem Cazander
 */
public enum WirePulseMode {

	OFF,
	FLASH,
	FLASH_ZERO,
	TRAIN,
	PPM;
		
	static public String[] getModeList() {
		return new String[] {
				WirePulseMode.OFF.name(),
				WirePulseMode.FLASH.name(),
				WirePulseMode.FLASH_ZERO.name(),
				WirePulseMode.TRAIN.name(),
				WirePulseMode.PPM.name()
			};
	}
	static public String[] getModeList(String magicMode) {
		return new String[] {
				WirePulseMode.OFF.name(),
				WirePulseMode.FLASH.name(),
				WirePulseMode.FLASH_ZERO.name(),
				WirePulseMode.TRAIN.name(),
				WirePulseMode.PPM.name(),
				magicMode
			};
	}
	
	static public WirePulseMode[] valuesOn() {
		return new WirePulseMode[] {
				WirePulseMode.FLASH,
				WirePulseMode.FLASH_ZERO,
				WirePulseMode.TRAIN,
				WirePulseMode.PPM,
			};
	}
}

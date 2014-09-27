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

/**
 * Stk500Command lists all tokes used in stk500 protocol definition.
 * 
 * @author Willem Cazander
 */
public enum Stk500Command implements FlashCommandToken {

	STK_OK              (0x10),
	STK_FAILED          (0x11),  // Not used
	STK_UNKNOWN         (0x12),  // Not used
	STK_NODEVICE        (0x13),  // Not used
	STK_INSYNC          (0x14),  // ' '
	STK_NOSYNC          (0x15),  // Not used
	ADC_CHANNEL_ERROR   (0x16),  // Not used
	ADC_MEASURE_OK      (0x17),  // Not used
	PWM_CHANNEL_ERROR   (0x18),  // Not used
	PWM_ADJUST_OK       (0x19),  // Not used
	CRC_EOP             (0x20),  // 'SPACE'
	STK_GET_SYNC        (0x30),  // '0'
	STK_GET_SIGN_ON     (0x31),  // '1'
	STK_SET_PARAMETER   (0x40),  // '@'
	STK_GET_PARAMETER   (0x41),  // 'A'
	STK_SET_DEVICE      (0x42),  // 'B'
	STK_SET_DEVICE_EXT  (0x45),  // 'E'
	STK_ENTER_PROGMODE  (0x50),  // 'P'
	STK_LEAVE_PROGMODE  (0x51),  // 'Q'
	STK_CHIP_ERASE      (0x52),  // 'R'
	STK_CHECK_AUTOINC   (0x53),  // 'S'
	STK_LOAD_ADDRESS    (0x55),  // 'U'
	STK_UNIVERSAL       (0x56),  // 'V'
	STK_PROG_FLASH      (0x60),  // '`'
	STK_PROG_DATA       (0x61),  // 'a'
	STK_PROG_FUSE       (0x62),  // 'b'
	STK_PROG_LOCK       (0x63),  // 'c'
	STK_PROG_PAGE       (0x64),  // 'd'
	STK_PROG_FUSE_EXT   (0x65),  // 'e'
	STK_READ_FLASH      (0x70),  // 'p'
	STK_READ_DATA       (0x71),  // 'q'
	STK_READ_FUSE       (0x72),  // 'r'
	STK_READ_LOCK       (0x73),  // 's'
	STK_READ_PAGE       (0x74),  // 't'
	STK_READ_SIGN       (0x75),  // 'u'
	STK_READ_OSCCAL     (0x76),  // 'v'
	STK_READ_FUSE_EXT   (0x77),  // 'w'
	STK_READ_OSCCAL_EXT (0x78);  // 'x'
	
	private final Integer token;
	private Stk500Command(Integer token) {
		this.token = token;
	}

	public Integer getToken() {
		return token;
	}
	
	static public Stk500Command valueOfToken(Integer token) {
		for (Stk500Command c:values()) {
			if (c.getToken().equals(token)) {
				return c;
			}
		}
		return null;
	}
}
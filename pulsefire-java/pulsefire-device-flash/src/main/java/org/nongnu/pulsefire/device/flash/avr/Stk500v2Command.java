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

package org.nongnu.pulsefire.device.flash.avr;

/**
 * Stk500v2Command lists all tokes used in stk500v2 protocol definition.
 * 
 * @author Willem Cazander
 */
public enum Stk500v2Command implements FlashCommandToken {

	MESSAGE_START                       (0x1B),//= ESC = 27 decimal
	TOKEN                               (0x0E),
	
	// *****************[ STK general command constants ]**************************
	
	CMD_SIGN_ON                         (0x01),
	CMD_SET_PARAMETER                   (0x02),
	CMD_GET_PARAMETER                   (0x03),
	CMD_SET_DEVICE_PARAMETERS           (0x04),
	CMD_OSCCAL                          (0x05),
	CMD_LOAD_ADDRESS                    (0x06),
	CMD_FIRMWARE_UPGRADE                (0x07),
	
	
	// *****************[ STK ISP command constants ]******************************
	
	CMD_ENTER_PROGMODE_ISP              (0x10),
	CMD_LEAVE_PROGMODE_ISP              (0x11),
	CMD_CHIP_ERASE_ISP                  (0x12),
	CMD_PROGRAM_FLASH_ISP               (0x13),
	CMD_READ_FLASH_ISP                  (0x14),
	CMD_PROGRAM_EEPROM_ISP              (0x15),
	CMD_READ_EEPROM_ISP                 (0x16),
	CMD_PROGRAM_FUSE_ISP                (0x17),
	CMD_READ_FUSE_ISP                   (0x18),
	CMD_PROGRAM_LOCK_ISP                (0x19),
	CMD_READ_LOCK_ISP                   (0x1A),
	CMD_READ_SIGNATURE_ISP              (0x1B),
	CMD_READ_OSCCAL_ISP                 (0x1C),
	CMD_SPI_MULTI                       (0x1D),
	
	// *****************[ STK PP command constants ]*******************************
	
	CMD_ENTER_PROGMODE_PP               (0x20),
	CMD_LEAVE_PROGMODE_PP               (0x21),
	CMD_CHIP_ERASE_PP                   (0x22),
	CMD_PROGRAM_FLASH_PP                (0x23),
	CMD_READ_FLASH_PP                   (0x24),
	CMD_PROGRAM_EEPROM_PP               (0x25),
	CMD_READ_EEPROM_PP                  (0x26),
	CMD_PROGRAM_FUSE_PP                 (0x27),
	CMD_READ_FUSE_PP                    (0x28),
	CMD_PROGRAM_LOCK_PP                 (0x29),
	CMD_READ_LOCK_PP                    (0x2A),
	CMD_READ_SIGNATURE_PP               (0x2B),
	CMD_READ_OSCCAL_PP                  (0x2C),
	CMD_SET_CONTROL_STACK               (0x2D),
	
	// *****************[ STK HVSP command constants ]*****************************
	
	CMD_ENTER_PROGMODE_HVSP             (0x30),
	CMD_LEAVE_PROGMODE_HVSP             (0x31),
	CMD_CHIP_ERASE_HVSP                 (0x32),
	CMD_PROGRAM_FLASH_HVSP              (0x33),
	CMD_READ_FLASH_HVSP                 (0x34),
	CMD_PROGRAM_EEPROM_HVSP             (0x35),
	CMD_READ_EEPROM_HVSP                (0x36),
	CMD_PROGRAM_FUSE_HVSP               (0x37),
	CMD_READ_FUSE_HVSP                  (0x38),
	CMD_PROGRAM_LOCK_HVSP               (0x39),
	CMD_READ_LOCK_HVSP                  (0x3A),
	CMD_READ_SIGNATURE_HVSP             (0x3B),
	CMD_READ_OSCCAL_HVSP                (0x3C),
	
	// *****************[ STK status constants ]***************************
	
	// Success
	STATUS_CMD_OK                       (0x00),
	
	// Warnings
	STATUS_CMD_TOUT                     (0x80),
	STATUS_RDY_BSY_TOUT                 (0x81),
	STATUS_SET_PARAM_MISSING            (0x82),
	
	// Errors
	STATUS_CMD_FAILED                   (0xC0),
	STATUS_CKSUM_ERROR                  (0xC1),
	STATUS_CMD_UNKNOWN                  (0xC9),
	
	// *****************[ STK parameter constants ]***************************
	PARAM_BUILD_NUMBER_LOW              (0x80),
	PARAM_BUILD_NUMBER_HIGH             (0x81),
	PARAM_HW_VER                        (0x90),
	PARAM_SW_MAJOR                      (0x91),
	PARAM_SW_MINOR                      (0x92),
	PARAM_VTARGET                       (0x94),
	PARAM_VADJUST                       (0x95),
	PARAM_OSC_PSCALE                    (0x96),
	PARAM_OSC_CMATCH                    (0x97),
	PARAM_SCK_DURATION                  (0x98),
	PARAM_TOPCARD_DETECT                (0x9A),
	PARAM_STATUS                        (0x9C),
	PARAM_DATA                          (0x9D),
	PARAM_RESET_POLARITY                (0x9E),
	PARAM_CONTROLLER_INIT               (0x9F),
	
	// *****************[ STK answer constants ]***************************
	
	ANSWER_CKSUM_ERROR                  (0xB0);

	private final Integer token;
	private Stk500v2Command(Integer token) {
		this.token = token;
	}

	public Integer getToken() {
		return token;
	}
}

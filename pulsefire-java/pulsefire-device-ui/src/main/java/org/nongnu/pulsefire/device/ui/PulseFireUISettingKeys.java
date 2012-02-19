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

package org.nongnu.pulsefire.device.ui;

/**
 * PulseFireUISettingKeys defines all uniq settings keys as enum with defaults as string.
 * 
 * @author Willem Cazander
 */
public enum PulseFireUISettingKeys {

	LAF_COLORS("dark-red"),
	AUTO_CONNECT("false"),
	DEVICE_PORT(""),
	LIMIT_CHANNELS("true"),
	CONSOLE_LINES("500"),
	SCOPE_ENABLE("false"),
	AVRDUDE_CMD(""),
	AVRDUDE_CONFIG(""),
	GRAPH_SIZE("0"),
	GRAPH_COLS("0"),
	UI_SPLIT_BOTTOM("570"),
	UI_SPLIT_BOTTOM_LOG("600"),
	LOG_FILE_APPEND("false"),
	LOG_CMD_ENABLE("false"),
	LOG_CMD_FILE(""),
	LOG_CMD_TX("true"),
	LOG_CMD_RX("true"),
	LOG_PULL_ENABLE("false"),
	LOG_PULL_FILE(""),
	LOG_PULL_FIELDS("");
	
	private String defaultValue = null;
	private PulseFireUISettingKeys(String defaultValue) {
		if (defaultValue==null) {
			throw new IllegalStateException("Can't allow null default.");
		}
		this.defaultValue=defaultValue;
	}
	
	public String getDefaultValue() {
		return defaultValue;
	}
}

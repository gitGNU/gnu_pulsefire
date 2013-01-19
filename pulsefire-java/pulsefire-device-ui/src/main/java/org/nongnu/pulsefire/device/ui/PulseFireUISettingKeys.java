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
	TAB_SCOPE_ENABLE("false"),
	TAB_UILOG_ENABLE("false"),
	PULL_SPEED("10000"),
	AVRDUDE_CMD(""),
	AVRDUDE_CONFIG(""),
	GRAPH_SIZE("0"),
	GRAPH_COLS("0"),
	GRAPH_LIST(""),
	GRAPH_LIST_FRONT("pwm_loop,pwm_req_freq,pulse_steps,adc_value,dic_value,doc_port,dev_volt,dev_temp,pulse_step,sys_main_loop_cnt"),
	UI_SPLIT_CONTENT("850"),
	UI_SPLIT_BOTTOM("570"),
	UI_SPLIT_BOTTOM_LOG("600"),
	LPM_RESULT_FIELDS("dev_volt,dev_amp,dev_temp,dev_freq"),
	FLASH_MCU_TYPE("ALL"),
	FLASH_MCU_SPEED("16Mhz"),
	
	LOG0_ENABLE("false"),
	LOG0_TIMESTAMP("true"),
	LOG0_FILENAME("pulsefire-log"),
	LOG0_PATH(""),
	LOG0_SPEED("60000"),
	LOG0_FIELDS("pulse_*,pwm_*,adc_*,dic_*,dev_*"),

	LOG1_ENABLE("false"),
	LOG1_TIMESTAMP("true"),
	LOG1_FILENAME("pulsefire-adc"),
	LOG1_PATH(""),
	LOG1_SPEED("10000"),
	LOG1_FIELDS("adc_value"),
	
	LOG2_ENABLE("false"),
	LOG2_TIMESTAMP("true"),
	LOG2_FILENAME("pulsefire-device"),
	LOG2_PATH(""),
	LOG2_SPEED("120000"),
	LOG2_FIELDS("dev_*");
	
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

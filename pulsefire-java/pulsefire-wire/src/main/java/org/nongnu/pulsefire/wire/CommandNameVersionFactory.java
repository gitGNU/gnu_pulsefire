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

package org.nongnu.pulsefire.wire;

/**
 * CommandNameVersionFactory config the Command enum per version.
 * 
 * @author Willem Cazander
 */
public class CommandNameVersionFactory {

	static public final int CURRENT_VERSION = 11;
	
	static public void configCommandId(int version,CommandName commandName,int value) {
		commandName.id=value;
	}
	static public void configCommandBits(int version,CommandName commandName,int value) {
		commandName.bits=value;
	}
	static public void configCommandMax(int version,CommandName commandName,int value) {
		commandName.maxValue=value;
	}
	static public void configCommandMapIndex(int version,CommandName commandName,int value) {
		commandName.mapIndex=value;
	}
	static public void configCommandMaxIndexA(int version,CommandName commandName,int value) {
		commandName.maxIndexA=value;
	}
	static public void configCommandMaxIndexB(int version,CommandName commandName,int value) {
		commandName.maxIndexB=value;
	}
	static public void configCommandMaxIndexTrigger(int version,CommandName commandName,boolean value) {
		commandName.mapIndexTrigger=value;
	}
	
	static public boolean configCommandName(int version) {
		if (version==CURRENT_VERSION) {
			return configCurrentVersion();
		}
		if (version==10) {
			return configVersion10();
		}
		if (version==9) {
			return configVersion9();
		}
		if (version==8) {
			return configVersion8();
		}
		return false;
	}
	
	static private boolean configCurrentVersion() {
		
		// small reset hack because protocol is in classpath, will rewrite for multiple versions support in one classpath
		for (CommandName cmd:CommandName.values()) {
			cmd.maxValue = 65535;
			cmd.maxIndexA = -1;
			cmd.maxIndexB = -1;
			cmd.mapIndex = -1;
			cmd.mapIndexTrigger = false;
			cmd.disabled = false;
			cmd.listValues = null;
			cmd.magicTopListValue = false;
			cmd.aliasName = null;
			cmd.pulseModeDependency = null;
		}
		
		CommandName.pulse_mode.listValues = WirePulseMode.getModeList();
		CommandName.pulse_steps.pulseModeDependency = WirePulseMode.valuesOn();
		CommandName.pulse_steps.listValues = new String[] {
				"0",									// Off       0 outputs
				"1","2","3",							// Minimal   3 outputs
				"4","5","6",							// Default   6 outputs
				"7","8",								// Extened   8 outputs
				"9","10","11","12","13","14","15","16"	// Exteded2 16 outputs
			};
		CommandName.pulse_trig.pulseModeDependency = WirePulseMode.valuesOn();
		CommandName.pulse_trig.listValues = new String[] {
				"LOOP_FIRE",
				"PULSE_FIRE"
			};
		CommandName.pulse_dir.pulseModeDependency = new WirePulseMode[] {
				WirePulseMode.TRAIN,
				WirePulseMode.PPM
			};
		
		CommandName.pulse_dir.listValues = new String[] {
				"LR",
				"RL",
				"LRRL-2",
				"LRRL",
				"LRLR"
			};
		CommandName.pulse_bank.pulseModeDependency = WirePulseMode.valuesOn();
		CommandName.pulse_bank.listValues = new String[] {
				"BANK_A",
				"BANK_B"
			};
		CommandName.pulse_pre_delay.pulseModeDependency = WirePulseMode.valuesOn();
		CommandName.pulse_pre_mul.pulseModeDependency = WirePulseMode.valuesOn();
		CommandName.pulse_pre_mul.listValues = new String[] {
				"OFF",
				"1","2","3","4","5","6","7","8","9","10"
			};
		CommandName.pulse_post_delay.pulseModeDependency = WirePulseMode.valuesOn();
		CommandName.pulse_post_mul.pulseModeDependency = WirePulseMode.valuesOn();
		CommandName.pulse_post_mul.listValues = CommandName.pulse_pre_mul.listValues;
		CommandName.pulse_post_hold.pulseModeDependency = WirePulseMode.valuesOn();
		CommandName.pulse_post_hold.listValues = new String[] {
				"OFF","LAST"
			};
		CommandName.pulse_mask_a.pulseModeDependency = WirePulseMode.valuesOn();
		CommandName.pulse_mask_b.pulseModeDependency = WirePulseMode.valuesOn();
		CommandName.pulse_init_a.pulseModeDependency = new WirePulseMode[] { WirePulseMode.TRAIN };
		CommandName.pulse_init_b.pulseModeDependency = new WirePulseMode[] { WirePulseMode.TRAIN };
		CommandName.pulse_fire_mode.listValues = new String[] {
				"NORMAL",
				"NOSYNC",
				"RESET"
			};
		CommandName.pulse_hold_mode.listValues = new String[] {
				"STOP",
				"CLEAR",
				"ZERO",
				"ZERO_CLEAR"
			};
		CommandName.pwm_on_cnt_a.pulseModeDependency = WirePulseMode.valuesOn();
		CommandName.pwm_on_cnt_b.pulseModeDependency = WirePulseMode.valuesOn();
		CommandName.pwm_off_cnt_a.pulseModeDependency = WirePulseMode.valuesOn();
		CommandName.pwm_off_cnt_b.pulseModeDependency = WirePulseMode.valuesOn();
		CommandName.pwm_tune_cnt.pulseModeDependency = WirePulseMode.valuesOn();
		CommandName.pwm_loop.pulseModeDependency = WirePulseMode.valuesOn();
		CommandName.pwm_req_idx.pulseModeDependency = WirePulseMode.valuesOn();
		CommandName.pwm_req_duty.pulseModeDependency = WirePulseMode.valuesOn();
		CommandName.pwm_req_freq.pulseModeDependency = WirePulseMode.valuesOn();
		CommandName.pwm_clock.pulseModeDependency = WirePulseMode.valuesOn();
		CommandName.pwm_clock.listValues = new String[] {
				"STOP",
				"ON_1",
				"ON_8",
				"ON_64",
				"ON_256",
				"ON_1024",
				"ON_EXTF",
				"ON_EXTR"
			};
		CommandName.pwm_req_idx.magicTopListValue = true; // last value is 255 value
		CommandName.pwm_req_idx.listValues = new String[] {
				"0","1", "2", "3", "4", "5", "6", "7",
				"8","9","10","11","12","13","14","15",
				"ALL"
			};
		CommandName.ppm_data_offset.pulseModeDependency = new WirePulseMode[] {WirePulseMode.PPM};
		CommandName.ppm_data_len.pulseModeDependency = new WirePulseMode[] {WirePulseMode.PPM};
		CommandName.ppm_data_a.pulseModeDependency = new WirePulseMode[] {WirePulseMode.PPM};
		CommandName.ppm_data_b.pulseModeDependency = new WirePulseMode[] {WirePulseMode.PPM};
		
		CommandName.int_0mode.listValues = new String[] {
				"OFF",
				"PULSE_FIRE",
				"PULSE_HOLD",
				"MAP_SET"
			};
		CommandName.int_0trig.listValues = new String[] {
				"LOW",
				"EDGE_ANY",
				"EDGE_FALL",
				"EDGE_RISE"
			};
		CommandName.int_0freq_mul.listValues = new String[] {
				"*1","*2","*3","*4","*5","*6","*7","*8","*9","*10","*11","*12","*100",
				"/1","/2","/3","/4","/5","/6","/7","/8","/9","/10","/11","/12","/100"
			};
		CommandName.int_1mode.listValues = CommandName.int_0mode.listValues ;
		CommandName.int_1trig.listValues = CommandName.int_0trig.listValues ;
		CommandName.int_1freq_mul.listValues = CommandName.int_0freq_mul.listValues ;
		
		CommandName.cip_0clock.listValues = new String[] {
				"STOP",
				"ON_1",
				"ON_8",
				"ON_64",
				"ON_256",
				"ON_1024",
				"ON_TN_FALL",
				"ON_TN_RISE"
			};
		CommandName.cip_0mode.listValues = new String[] {
				"NORMAL",
				"PWM_PHASE_8BIT",
				"PWM_PHASE_9BIT",
				"PWM_PHASE_10BIT",
				"CTC",
				"PWM_FAST_8BIT",
				"PWM_FAST_9BIT",
				"PWM_FAST_10BIT",
				"PWM_FREQ_ICR",
				"PWM_FREQ_OCR_A",
				"PWM_PHASE_ICR",
				"PWM_PHASE_OCR_A",
				"CTC",
				"RESERVED",
				"PWM_FAST_ICR",
				"PWM_FAST_OCR_A"
			};		
		CommandName.cip_0a_com.listValues = new String[] {
				"A_NONE",
				"A_TOGGLE",
				"A_CLEAR",
				"A_SET"
			};
		CommandName.cip_0b_com.listValues = new String[] {
				"B_NONE",
				"B_TOGGLE",
				"B_CLEAR",
				"B_SET"
			};
		CommandName.cip_0c_com.listValues = new String[] {
				"C_NONE",
				"C_TOGGLE",
				"C_CLEAR",
				"C_SET"
			};
		
		CommandName.cip_1clock.listValues = CommandName.cip_0clock.listValues;
		CommandName.cip_1mode.listValues  = CommandName.cip_0mode.listValues;
		CommandName.cip_1a_com.listValues = CommandName.cip_0a_com.listValues;
		CommandName.cip_1b_com.listValues = CommandName.cip_0b_com.listValues;
		CommandName.cip_1c_com.listValues = CommandName.cip_0c_com.listValues;
		
		CommandName.cip_2clock.listValues = CommandName.cip_0clock.listValues;
		CommandName.cip_2mode.listValues  = CommandName.cip_0mode.listValues;
		CommandName.cip_2a_com.listValues = CommandName.cip_0a_com.listValues;
		CommandName.cip_2b_com.listValues = CommandName.cip_0b_com.listValues;
		CommandName.cip_2c_com.listValues = CommandName.cip_0c_com.listValues;
		
		CommandName.lcd_size.listValues = new String[] {
				"LCD_2x16",
				"LCD_2x20",
				"LCD_4x20"
			};
		CommandName.lcd_defp.listValues = new String[] {
				"PAGE_MAIN",
				"PAGE_ADC",
				"PAGE_DIC",
				"PAGE_PLP"
			};
		CommandName.lcd_mode.listValues = new String[] {
				"MENU_OFF",
				"MENU_PAGE",
				"MENU_2BUTTON",
				"MENU_4BUTTON"
			};
		
		CommandName.spi_clock.listValues = new String[] {
				"/2",
				"/8",
				"/32",
				"/64"
			};

		CommandName.dev_volt_dot.listValues = new String[] {
				"/0",
				"/10",
				"/100",
				"/1000",
				"/10000"
			};
		CommandName.dev_amp_dot.listValues = CommandName.dev_volt_dot.listValues;
		CommandName.dev_temp_dot.listValues = CommandName.dev_volt_dot.listValues;
		
		CommandName.avr_pin2_map.listValues = new String[] {
				"OFF",
				"DIC2_IN",
				"DIC8_IN",
				"INT0_IN"
			};
		CommandName.avr_pin3_map.listValues = new String[] {
				"OFF",
				"DIC3_IN",
				"DIC9_IN",
				"INT1_IN"
			};
		CommandName.avr_pin4_map.listValues = new String[] {
				"OFF",
				"DIC4_IN",
				"DIC10_IN",
				"DOC4_OUT",
				"DOC10_OUT"
			};
		CommandName.avr_pin5_map.listValues = new String[] {
				"OFF",
				"DIC5_IN",
				"DIC11_IN",
				"DOC5_OUT",
				"DOC11_OUT",
				"PWM_CLK_IN",
			};
		
		CommandName.mega_port_a.listValues = new String[] {
				"OFF",
				"OUT8",
				"DOC8"
			};
		
		CommandName.mega_port_c.listValues = new String[] {
				"OFF",
				"OUT16",
				"DOC8",
				"DOC16"
			};
		
		CommandName.info_pwm_data.maxIndexA=64; // todo: redesign.
		CommandName.freq_pwm_data.maxIndexA=16; // mm not always true
		CommandName.freq_pwm_data.maxIndexB=3;
	//	CommandName.req_ptt_fire.maxIndexA=3;
	//	CommandName.req_pulse_fire.pulseModeDependency = WirePulseMode.valuesOn();
	//	CommandName.req_pulse_hold_fire.pulseModeDependency = WirePulseMode.valuesOn();
		
		CommandName.req_pwm_freq.disabled=true;
		CommandName.swc_trig.disabled=true;
		CommandName.swc_mode.disabled=true;
		CommandName.swc_mode_org.disabled=true;
		CommandName.stv_warn_mode.disabled=true;
		CommandName.stv_error_mode.disabled=true;
		return true;
	}

	static private boolean configVersion10() {
		
		// Config like current
		configCurrentVersion();
		
		CommandName.pulse_trig.listValues = new String[] {
				"INT_LOOP",
				"INT_FIRE",
				"EXT_PIN",
				"EXT_FIRE"
			};
		CommandName.pwm_loop_delta.pulseModeDependency = WirePulseMode.valuesOn();
		CommandName.pulse_dir.listValues = new String[] {
				"LR",
				"RL",
				"LRRL"
			};
		
		CommandName.avr_pin2_map.listValues = new String[] {
				"OFF",
				"TRIG_IN",
				"DIC2_IN",
				"DIC8_IN",
				"DOC2_OUT",
				"DOC8_OUT",
				"FREQ_IN",
				"FIRE_IN",
				"HOLD_FIRE_IN"
			};
		CommandName.avr_pin3_map.listValues = new String[] {
				"OFF",
				"MENU0_IN",
				"DIC3_IN",
				"DIC9_IN",
				"DOC3_OUT",
				"DOC9_OUT",
				"FREQ_IN",
				"FIRE_IN",
				"HOLD_FIRE_IN",
				"CIT0B_OUT"
			};
		CommandName.avr_pin18_map.listValues = new String[] {
				"OFF",
				"TRIG_IN",
				"DIC4_IN",
				"DIC6_IN",
				"DOC4_OUT",
				"DOC6_OUT",
				"FREQ_IN",
				"FIRE_IN",
				"HOLD_FIRE_IN" 
			};
		CommandName.avr_pin19_map.listValues = new String[] {
				"OFF",
				"TRIG_IN",
				"DIC5_IN",
				"DIC7_IN",
				"DOC5_OUT",
				"DOC7_OUT",
				"FREQ_IN",
				"FIRE_IN",
				"HOLD_FIRE_IN"
			};
		CommandName.avr_pin47_map.listValues = new String[] {
				"OFF",
				"RELAY_OUT"
			};
		CommandName.avr_pin48_map.listValues = new String[] {
				"OFF",
				"MENU0_IN",
				"DIC4_IN",
				"DIC6_IN",
				"DOC4_OUT",
				"DOC6_OUT"
			};
		CommandName.avr_pin49_map.listValues = new String[] {
				"OFF",
				"MENU1_IN",
				"DIC5_IN",
				"DIC7_IN",
				"DOC5_OUT",
				"DOC7_OUT"
			};
		
		return true;
	}
	
	static private boolean configVersion9() {
		
		// Config like current
		configCurrentVersion();
		
		for (CommandName cn:CommandName.values()) {
			if (cn.chipFlagDependency!=null && cn.chipFlagDependency==WireChipFlags.PWM) {
				cn.chipFlagDependency=null; // remove PWM flag dep in 0.9 and older because it was not there
			}
		}
		CommandName.pulse_trig.listValues = new String[] {
				"INT_LOOP",
				"INT_FIRE",
				"EXT_PIN"
			};
		CommandName.avr_pin3_map.listValues = new String[] {
				"OFF",
				"MENU0_IN",
				"DIC3_IN",
				"DIC9_IN",
				"DOC3_OUT",
				"DOC9_OUT",
				"FREQ_IN",
				"FIRE_IN",
				"HOLD_FIRE_IN"
			};
		CommandName.pwm_req_freq.disabled=true;
		CommandName.pwm_req_idx.disabled=true;
		CommandName.pwm_req_duty.aliasName="pwm_duty";
		CommandName.dev_volt_dot.disabled=true;
		CommandName.dev_amp_dot.disabled=true;
		CommandName.dev_temp_dot.disabled=true;
		CommandName.lpm_relay_map.disabled=true;
		CommandName.lcd_size.disabled=true;
		CommandName.adc_enable.disabled=true;
		CommandName.dic_enable.disabled=true;
		CommandName.dic_inv.disabled=true;
		CommandName.dic_sync.disabled=true;
	//	CommandName.req_ptt_fire.disabled=true;
	//	CommandName.req_mal_fire.disabled=true;
		CommandName.mal_fire.aliasName="mal_trig";
		CommandName.pulse_inv_a.disabled=true;
		CommandName.pulse_inv_b.disabled=true;
		CommandName.stv_warn_map.disabled=true;
		CommandName.stv_error_map.disabled=true;
		return true;
	}
	
	static private boolean configVersion8() {
		
		// Config like 9
		configVersion9();

		// Disable and Alias some commands
		CommandName.pulse_trig.aliasName = "pulse_trigger";
		CommandName.pulse_mode.listValues = new String[] {
				WirePulseMode.OFF.name(),
				WirePulseMode.FLASH.name(),
				WirePulseMode.PPM.name()
				/*
				WirePulseMode.PPM_ALL.name(),
				WirePulseMode.PPM_INTERL.name(),
				*/
			};
		CommandName.pulse_bank.disabled=true;
		CommandName.pwm_on_cnt_b.disabled=true;
		CommandName.pwm_off_cnt_b.disabled=true;
		CommandName.ppm_data_b.disabled=true;
		CommandName.pulse_trig.aliasName="pulse_trigger";
		CommandName.pwm_on_cnt_b.aliasName="pwm_on_cnt";
		CommandName.pwm_off_cnt_b.aliasName="pwm_off_cnt";
		CommandName.ppm_data_b.aliasName="ppm_data";
		
		CommandName.ptc_0run.disabled=true;
		CommandName.ptc_1run.disabled=true;
		CommandName.ptc_0mul.disabled=true;
		CommandName.ptc_1mul.disabled=true;
		CommandName.ptc_0map.disabled=true;
		CommandName.ptc_1map.disabled=true;
		
		CommandName.ptt_0map.disabled=true;
		CommandName.ptt_1map.disabled=true;
		CommandName.ptt_2map.disabled=true;
		CommandName.ptt_3map.disabled=true;
		
		CommandName.avr_pin2_map.aliasName="sys_pin2_map";
		CommandName.avr_pin2_map.listValues = new String[] {
				"PIN2_OFF",
				"PIN2_TRIG_IN",
				"PIN2_RELAY_OUT"
			};
		CommandName.avr_pin3_map.disabled=true;
		CommandName.avr_pin4_map.disabled=true;
		CommandName.avr_pin5_map.disabled=true;
		
		CommandName.swc_delay.aliasName="sys_warmup_delay";
		//CommandName.swc_mode.aliasName="sys_warmup_delay";
		CommandName.swc_secs.aliasName="sys_warmup_delay";
		CommandName.swc_duty.aliasName="sys_warmup_delay";
		//CommandName.swc_trig.disabled=true;
		
		return true;
	}
}

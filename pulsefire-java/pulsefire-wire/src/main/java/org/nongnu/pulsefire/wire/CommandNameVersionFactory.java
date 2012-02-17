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

	static public final int CURRENT_VERSION = 10;
	
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
				"INT_LOOP",
				"INT_FIRE",
				"EXT_PIN2"
			};
		CommandName.pulse_dir.pulseModeDependency = new WirePulseMode[] {
				WirePulseMode.TRAIN,
				WirePulseMode.PPM,
				WirePulseMode.PPM_ALL,
				WirePulseMode.PPM_INTERL
			};
		
		CommandName.pulse_dir.listValues = new String[] {
				"LR",
				"RL",
				"LRRL"
			};
		CommandName.pulse_bank.pulseModeDependency = WirePulseMode.valuesOn();
		CommandName.pulse_bank.listValues = new String[] {
				"BANK_A",
				"BANK_B"
			};
		CommandName.pulse_trig_delay.pulseModeDependency = WirePulseMode.valuesOn();
		CommandName.pulse_post_delay.pulseModeDependency = WirePulseMode.valuesOn();
		CommandName.pulse_mask_a.pulseModeDependency = WirePulseMode.valuesOn();
		CommandName.pulse_mask_b.pulseModeDependency = WirePulseMode.valuesOn();
		CommandName.pulse_init_a.pulseModeDependency = new WirePulseMode[] { WirePulseMode.TRAIN };
		CommandName.pulse_init_b.pulseModeDependency = new WirePulseMode[] { WirePulseMode.TRAIN };
		CommandName.pwm_on_cnt_a.pulseModeDependency = WirePulseMode.valuesOn();
		CommandName.pwm_on_cnt_b.pulseModeDependency = WirePulseMode.valuesOn();
		CommandName.pwm_off_cnt_a.pulseModeDependency = WirePulseMode.valuesOn();
		CommandName.pwm_off_cnt_b.pulseModeDependency = WirePulseMode.valuesOn();
		CommandName.pwm_tune_cnt.pulseModeDependency = WirePulseMode.valuesOn();
		CommandName.pwm_loop.pulseModeDependency = WirePulseMode.valuesOn();
		CommandName.pwm_loop_delta.pulseModeDependency = WirePulseMode.valuesOn();
		CommandName.pwm_duty.pulseModeDependency = WirePulseMode.valuesOn();
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
		CommandName.ppm_data_offset.pulseModeDependency = WirePulseMode.valuesPPM();
		CommandName.ppm_data_len.pulseModeDependency = WirePulseMode.valuesPPM();
		CommandName.ppm_data_a.pulseModeDependency = WirePulseMode.valuesPPM();
		CommandName.ppm_data_b.pulseModeDependency = WirePulseMode.valuesPPM();
		CommandName.stv_warn_mode.magicTopListValue = true;
		CommandName.stv_warn_mode.listValues = WirePulseMode.getModeList("KEEP");
		CommandName.stv_error_mode.magicTopListValue = true;
		CommandName.stv_error_mode.listValues = WirePulseMode.getModeList("KEEP");
		
		CommandName.lcd_size.listValues = new String[] {
				"LCD_2x16",
				"LCD_2x20",
				"LCD_4x20"
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
				"PIN2_OFF",
				"PIN2_TRIG_IN",
				"PIN2_RELAY_OUT",
				"PIN2_DIC2_IN",
				"PIN2_DIC8_IN",
				"PIN2_DOC2_OUT",
				"PIN2_DOC8_OUT",
				"PIN2_FREQ_IN",
				"PIN2_FIRE_IN"
			};
		CommandName.avr_pin3_map.listValues = new String[] {
				"PIN3_OFF",
				"PIN3_MENU0_IN",
				"PIN3_RELAY_OUT",
				"PIN3_DIC3_IN",
				"PIN3_DIC9_IN",
				"PIN3_DOC3_OUT",
				"PIN3_DOC9_OUT",
				"PIN3_FREQ_IN",
				"PIN3_FIRE_IN"
			};
		CommandName.avr_pin4_map.listValues = new String[] {
				"PIN4_OFF",
				"PIN4_MENU1_IN",
				"PIN4_RELAY_OUT",
				"PIN4_DIC4_IN",
				"PIN4_DIC10_IN",
				"PIN4_DOC4_OUT",
				"PIN4_DOC10_OUT"
			};
		CommandName.avr_pin5_map.listValues = new String[] {
				"PIN5_OFF",
				"PIN5_CLOCK_IN",
				"PIN5_RELAY_OUT",
				"PIN5_DIC5_IN",
				"PIN5_DIC11_IN",
				"PIN5_DOC5_OUT",
				"PIN5_DOC11_OUT"
			};
	
		CommandName.avr_pin18_map.listValues = new String[] {
				"PIN18_OFF",
				"PIN18_TRIG_IN",
				"PIN18_RELAY_OUT",
				"PIN18_DIC4_IN",
				"PIN18_DIC6_IN",
				"PIN18_DOC4_OUT",
				"PIN18_DOC6_OUT",
				"PIN18_FREQ_IN",
				"IN18_FIRE_IN" 
			};
		CommandName.avr_pin19_map.listValues = new String[] {
				"PIN19_OFF",
				"PIN19_TRIG_IN",
				"PIN19_RELAY_OUT",
				"PIN19_DIC5_IN",
				"PIN19_DIC7_IN",
				"PIN19_DOC5_OUT",
				"PIN19_DOC7_OUT",
				"PIN19_FREQ_IN",
				"PIN19_FIRE_IN"
			};
		CommandName.avr_pin47_map.listValues = new String[] {
				"PIN47_OFF",
				"PIN47_CLOCK_IN",
				"PIN47_RELAY_OUT"
			};
		CommandName.avr_pin48_map.listValues = new String[] {
				"PIN48_OFF",
				"PIN48_MENU0_IN",
				"PIN48_RELAY_OUT",
				"PIN48_DIC4_IN",
				"PIN48_DIC6_IN",
				"PIN48_DOC4_OUT",
				"PIN48_DOC6_OUT"
			};
		CommandName.avr_pin49_map.listValues = new String[] {
				"PIN49_OFF",
				"PIN49_MENU1_IN",
				"PIN49_RELAY_OUT",
				"PIN49_DIC5_IN",
				"PIN49_DIC7_IN",
				"PIN49_DOC5_OUT",
				"PIN49_DOC7_OUT"
			};
		
		CommandName.swc_mode.magicTopListValue = true;
		CommandName.swc_mode.listValues = WirePulseMode.getModeList("KEEP");
		CommandName.swc_trig.magicTopListValue = true;
		CommandName.swc_trig.listValues = new String[] {
				"PTT_0",
				"PTT_1",
				"PTT_2",
				"PTT_3",
				"NONE"
			};
		
		CommandName.freq_pwm_data.maxIndexA=16; // mm not always true
		CommandName.freq_pwm_data.maxIndexB=3;
		CommandName.req_ptt_fire.maxIndexA=3;
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
		CommandName.pwm_duty.disabled=true;
		CommandName.req_pwm_freq.disabled=true; // req_pwm_freq works different in 0.9
		CommandName.req_ptt_fire.disabled=true;
		CommandName.dev_volt_dot.disabled=true;
		CommandName.dev_amp_dot.disabled=true;
		CommandName.dev_temp_dot.disabled=true;
		CommandName.lpm_relay_inv.disabled=true;
		CommandName.lcd_size.disabled=true;
		CommandName.adc_enable.disabled=true;
		CommandName.dic_enable.disabled=true;
		CommandName.dic_inv.disabled=true;
		CommandName.dic_sync.disabled=true;
		
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
				WirePulseMode.PPM.name(),
				WirePulseMode.PPM_ALL.name(),
				WirePulseMode.PPM_INTERL.name(),
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
		CommandName.swc_mode.aliasName="sys_warmup_delay";
		CommandName.swc_secs.aliasName="sys_warmup_delay";
		CommandName.swc_duty.aliasName="sys_warmup_delay";
		CommandName.swc_trig.disabled=true;
		
		return true;
	}
}

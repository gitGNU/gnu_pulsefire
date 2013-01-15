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

import java.util.ArrayList;
import java.util.List;

/**
 * CommandName defines all the commands possible to receive or transmit to PulseFire.
 * 
 * @author Willem Cazander
 */
public enum CommandName {
	
	help					(CommandVariableType.CMD),
	save					(CommandVariableType.CMD),
	
	info_conf				(CommandVariableType.INFO),
	info_data				(CommandVariableType.INFO),
	info_prog				(CommandVariableType.INFO),
	info_freq				(CommandVariableType.INFO,WireChipFlags.PWM),
	info_pwm				(CommandVariableType.INFO,WireChipFlags.PWM),
	info_ppm				(CommandVariableType.INFO,WireChipFlags.PWM),
	info_chip				(CommandVariableType.INFO),
	
	reset_conf				(CommandVariableType.CMD),
	reset_data				(CommandVariableType.CMD),
	reset_chip				(CommandVariableType.CMD),
	
	req_trigger				(CommandVariableType.CMD),
	req_tx_push				(CommandVariableType.CMD),
	req_tx_echo				(CommandVariableType.CMD),
	req_tx_promt			(CommandVariableType.CMD),

	mal_code				(CommandVariableType.CMD,WireChipFlags.MAL),
	mal_ops					(CommandVariableType.CMD,WireChipFlags.MAL),
	mal_ops_fire			(CommandVariableType.CMD,WireChipFlags.MAL),
	mal_mticks				(CommandVariableType.CMD,WireChipFlags.MAL),
	
	sys_id					(CommandVariableType.CONF),
	sys_pass				(CommandVariableType.CONF),
	
	spi_chips				(CommandVariableType.CONF,WireChipFlags.SPI),
	spi_clock				(CommandVariableType.CONF,WireChipFlags.SPI),
	
	pulse_enable			(CommandVariableType.CONF,WireChipFlags.PWM),
	pulse_mode				(CommandVariableType.CONF,WireChipFlags.PWM),
	pulse_steps				(CommandVariableType.CONF,WireChipFlags.PWM),
	pulse_trig				(CommandVariableType.CONF,WireChipFlags.PWM),
	pulse_dir				(CommandVariableType.CONF,WireChipFlags.PWM),
	pulse_bank				(CommandVariableType.CONF,WireChipFlags.PWM),
	pulse_pre_delay			(CommandVariableType.CONF,WireChipFlags.PWM),
	pulse_pre_mul			(CommandVariableType.CONF,WireChipFlags.PWM),
	pulse_post_delay		(CommandVariableType.CONF,WireChipFlags.PWM),
	pulse_post_mul			(CommandVariableType.CONF,WireChipFlags.PWM),
	pulse_post_hold			(CommandVariableType.CONF,WireChipFlags.PWM),
	pulse_mask_a			(CommandVariableType.CONF,WireChipFlags.PWM),
	pulse_mask_b			(CommandVariableType.CONF,WireChipFlags.PWM),
	pulse_init_a			(CommandVariableType.CONF,WireChipFlags.PWM),
	pulse_init_b			(CommandVariableType.CONF,WireChipFlags.PWM),
	pulse_inv_a				(CommandVariableType.CONF,WireChipFlags.PWM),
	pulse_inv_b				(CommandVariableType.CONF,WireChipFlags.PWM),
	pulse_fire_mode			(CommandVariableType.CONF,WireChipFlags.PWM),
	pulse_hold_mode			(CommandVariableType.CONF,WireChipFlags.PWM),
	pulse_hold_auto			(CommandVariableType.CONF,WireChipFlags.PWM),
	pulse_hold_autoclr		(CommandVariableType.CONF,WireChipFlags.PWM),
	pulse_fire_map			(CommandVariableType.CONF,WireChipFlags.PWM),
	pulse_hold_map			(CommandVariableType.CONF,WireChipFlags.PWM),
	pulse_resume_map		(CommandVariableType.CONF,WireChipFlags.PWM),
	pulse_reset_map			(CommandVariableType.CONF,WireChipFlags.PWM),
	
	pwm_on_cnt_a			(CommandVariableType.CONF,WireChipFlags.PWM),
	pwm_on_cnt_b			(CommandVariableType.CONF,WireChipFlags.PWM),
	pwm_off_cnt_a			(CommandVariableType.CONF,WireChipFlags.PWM),
	pwm_off_cnt_b			(CommandVariableType.CONF,WireChipFlags.PWM),
	pwm_tune_cnt			(CommandVariableType.CONF,WireChipFlags.PWM),
	pwm_loop				(CommandVariableType.CONF,WireChipFlags.PWM),
	pwm_loop_delta			(CommandVariableType.CONF,WireChipFlags.PWM),
	pwm_clock				(CommandVariableType.CONF,WireChipFlags.PWM),
	pwm_req_idx				(CommandVariableType.CONF,WireChipFlags.PWM),
	pwm_req_duty			(CommandVariableType.CONF,WireChipFlags.PWM),
	pwm_req_freq			(CommandVariableType.CONF,WireChipFlags.PWM),
	
	ppm_data_offset			(CommandVariableType.CONF,WireChipFlags.PWM),
	ppm_data_len			(CommandVariableType.CONF,WireChipFlags.PWM),
	ppm_data_a				(CommandVariableType.CONF,WireChipFlags.PWM),
	ppm_data_b				(CommandVariableType.CONF,WireChipFlags.PWM),
	
	lpm_done				(CommandVariableType.CMD,WireChipFlags.LPM),  // exception this is no cmd.
	lpm_start				(CommandVariableType.CONF,WireChipFlags.LPM),
	lpm_stop				(CommandVariableType.CONF,WireChipFlags.LPM),
	lpm_size				(CommandVariableType.CONF,WireChipFlags.LPM),
	lpm_relay_map			(CommandVariableType.CONF,WireChipFlags.LPM),
	
	ptc_0run				(CommandVariableType.CONF,WireChipFlags.PTC),
	ptc_0mul				(CommandVariableType.CONF,WireChipFlags.PTC),
	ptc_0map				(CommandVariableType.CONF,WireChipFlags.PTC),
	ptc_1run				(CommandVariableType.CONF,WireChipFlags.PTC),
	ptc_1mul				(CommandVariableType.CONF,WireChipFlags.PTC),
	ptc_1map				(CommandVariableType.CONF,WireChipFlags.PTC),
	
	ptt_0map				(CommandVariableType.CONF,WireChipFlags.PTT),
	ptt_1map				(CommandVariableType.CONF,WireChipFlags.PTT),
	ptt_2map				(CommandVariableType.CONF,WireChipFlags.PTT),
	ptt_3map				(CommandVariableType.CONF,WireChipFlags.PTT),
	
	dev_volt_dot			(CommandVariableType.CONF),
	dev_amp_dot				(CommandVariableType.CONF),
	dev_temp_dot			(CommandVariableType.CONF),
	
	stv_warn_secs			(CommandVariableType.CONF,WireChipFlags.STV),
	stv_warn_map			(CommandVariableType.CONF,WireChipFlags.STV),
	stv_error_secs			(CommandVariableType.CONF,WireChipFlags.STV),
	stv_error_map			(CommandVariableType.CONF,WireChipFlags.STV),
	stv_max_map				(CommandVariableType.CONF,WireChipFlags.STV),
	stv_min_map				(CommandVariableType.CONF,WireChipFlags.STV),
	
	vfc_input_map			(CommandVariableType.CONF,WireChipFlags.VFC),
	vfc_output_map			(CommandVariableType.CONF,WireChipFlags.VFC),
	
	adc_map					(CommandVariableType.CONF,WireChipFlags.ADC),
	adc_enable				(CommandVariableType.CONF,WireChipFlags.ADC),
	adc_jitter				(CommandVariableType.CONF,WireChipFlags.ADC),
	
	int_map					(CommandVariableType.CONF),
	int_0mode				(CommandVariableType.CONF),
	int_0trig				(CommandVariableType.CONF),
	int_0freq_mul			(CommandVariableType.CONF),
	int_1mode				(CommandVariableType.CONF),
	int_1trig				(CommandVariableType.CONF),
	int_1freq_mul			(CommandVariableType.CONF),
	
	dic_map					(CommandVariableType.CONF),
	dic_enable				(CommandVariableType.CONF),
	dic_inv					(CommandVariableType.CONF),
	dic_sync				(CommandVariableType.CONF),
	
	avr_pin2_map			(CommandVariableType.CONF,WireChipFlags.AVR),
	avr_pin3_map			(CommandVariableType.CONF,WireChipFlags.AVR),
	avr_pin4_map			(CommandVariableType.CONF,WireChipFlags.AVR),
	avr_pin5_map			(CommandVariableType.CONF,WireChipFlags.AVR),
	
	mega_port_a				(CommandVariableType.CONF,WireChipFlags.AVR_MEGA),
	mega_port_c				(CommandVariableType.CONF,WireChipFlags.AVR_MEGA),
	
	lcd_size				(CommandVariableType.CONF,WireChipFlags.LCD),
	lcd_defp				(CommandVariableType.CONF,WireChipFlags.LCD),
	lcd_mode				(CommandVariableType.CONF,WireChipFlags.LCD),
	lcd_plp					(CommandVariableType.CONF,WireChipFlags.LCD),
	
	swc_delay				(CommandVariableType.CONF,WireChipFlags.SWC),
	swc_secs				(CommandVariableType.CONF,WireChipFlags.SWC),
	swc_duty				(CommandVariableType.CONF,WireChipFlags.SWC),
	swc_map					(CommandVariableType.CONF,WireChipFlags.SWC),
	
	cip_0clock				(CommandVariableType.CONF,WireChipFlags.CIP),
	cip_0mode				(CommandVariableType.CONF,WireChipFlags.CIP),
	cip_0a_ocr				(CommandVariableType.CONF,WireChipFlags.CIP),
	cip_0a_com				(CommandVariableType.CONF,WireChipFlags.CIP),
	cip_0b_ocr				(CommandVariableType.CONF,WireChipFlags.CIP),
	cip_0b_com				(CommandVariableType.CONF,WireChipFlags.CIP),
	cip_0c_ocr				(CommandVariableType.CONF,WireChipFlags.CIP),
	cip_0c_com				(CommandVariableType.CONF,WireChipFlags.CIP),
	
	cip_1clock				(CommandVariableType.CONF,WireChipFlags.CIP),
	cip_1mode				(CommandVariableType.CONF,WireChipFlags.CIP),
	cip_1a_ocr				(CommandVariableType.CONF,WireChipFlags.CIP),
	cip_1a_com				(CommandVariableType.CONF,WireChipFlags.CIP),
	cip_1b_ocr				(CommandVariableType.CONF,WireChipFlags.CIP),
	cip_1b_com				(CommandVariableType.CONF,WireChipFlags.CIP),
	cip_1c_ocr				(CommandVariableType.CONF,WireChipFlags.CIP),
	cip_1c_com				(CommandVariableType.CONF,WireChipFlags.CIP),
	
	cip_2clock				(CommandVariableType.CONF,WireChipFlags.CIP),
	cip_2mode				(CommandVariableType.CONF,WireChipFlags.CIP),
	cip_2a_ocr				(CommandVariableType.CONF,WireChipFlags.CIP),
	cip_2a_com				(CommandVariableType.CONF,WireChipFlags.CIP),
	cip_2b_ocr				(CommandVariableType.CONF,WireChipFlags.CIP),
	cip_2b_com				(CommandVariableType.CONF,WireChipFlags.CIP),
	cip_2c_ocr				(CommandVariableType.CONF,WireChipFlags.CIP),
	cip_2c_com				(CommandVariableType.CONF,WireChipFlags.CIP),
	
	
	
	/* All non conf types are only used to receiving info so they have no dep info. */
	
	sys_main_loop_cnt		(CommandVariableType.DATA),
	sys_input_time_cnt		(CommandVariableType.DATA),
	
	adc_time_cnt			(CommandVariableType.DATA),	
	adc_value				(CommandVariableType.DATA),
	adc_state				(CommandVariableType.DATA),
	adc_state_idx			(CommandVariableType.DATA),
	adc_state_value			(CommandVariableType.DATA),
	
	int_time_cnt			(CommandVariableType.DATA),
	int_0freq				(CommandVariableType.DATA),
	int_0freq_cnt			(CommandVariableType.DATA),
	int_1freq				(CommandVariableType.DATA),
	int_1freq_cnt			(CommandVariableType.DATA),
	
	dic_time_cnt			(CommandVariableType.DATA),
	dic_value				(CommandVariableType.DATA),
	doc_port				(CommandVariableType.DATA),
	
	swc_secs_cnt			(CommandVariableType.DATA),
	swc_duty_cnt			(CommandVariableType.DATA),
	
	lcd_input				(CommandVariableType.DATA),
	lcd_time_cnt			(CommandVariableType.DATA),
	lcd_page				(CommandVariableType.DATA),
	lcd_redraw				(CommandVariableType.DATA),
	
	lpm_state				(CommandVariableType.DATA),
	lpm_fire				(CommandVariableType.DATA),
	lpm_start_time			(CommandVariableType.DATA),
	lpm_total_time			(CommandVariableType.DATA),
	lpm_result				(CommandVariableType.DATA),
	lpm_level				(CommandVariableType.DATA),
	
	ptc_sys_cnt				(CommandVariableType.DATA),
	ptc_0cnt				(CommandVariableType.DATA),
	ptc_0run_cnt			(CommandVariableType.DATA),
	ptc_0map_idx			(CommandVariableType.DATA),
	ptc_0mul_cnt			(CommandVariableType.DATA),
	ptc_0step				(CommandVariableType.DATA),
	ptc_1cnt				(CommandVariableType.DATA),
	ptc_1run_cnt			(CommandVariableType.DATA),
	ptc_1map_idx			(CommandVariableType.DATA),
	ptc_1mul_cnt			(CommandVariableType.DATA),
	ptc_1step				(CommandVariableType.DATA),
	
	ptt_idx					(CommandVariableType.DATA),
	ptt_cnt					(CommandVariableType.DATA),
	ptt_fire				(CommandVariableType.DATA),
	ptt_step				(CommandVariableType.DATA),
	
	dev_volt				(CommandVariableType.DATA),
	dev_amp					(CommandVariableType.DATA),
	dev_temp				(CommandVariableType.DATA),
	dev_var					(CommandVariableType.DATA),
	
	pulse_fire				(CommandVariableType.DATA),
	pulse_fire_cnt			(CommandVariableType.DATA),
	pulse_fire_freq			(CommandVariableType.DATA),
	pulse_hold_fire			(CommandVariableType.DATA),
	pulse_reset_fire		(CommandVariableType.DATA),
	pulse_resume_fire		(CommandVariableType.DATA),
	pulse_step				(CommandVariableType.DATA),
	
	pwm_state				(CommandVariableType.DATA),
	pwm_loop_cnt			(CommandVariableType.DATA),
	pwm_loop_max			(CommandVariableType.DATA),
	ppm_idx					(CommandVariableType.DATA),
	
	mal_fire				(CommandVariableType.DATA),
	mal_time_cnt			(CommandVariableType.DATA),
	
	chip_version			(CommandVariableType.CHIP),
	chip_conf_max			(CommandVariableType.CHIP),
	chip_conf_size			(CommandVariableType.CHIP),
	chip_free_sram			(CommandVariableType.CHIP),
	chip_cpu_freq			(CommandVariableType.CHIP),
	chip_cpu_type			(CommandVariableType.CHIP),
	chip_name				(CommandVariableType.CHIP),
	chip_name_id			(CommandVariableType.CHIP),
	chip_build				(CommandVariableType.CHIP),
	chip_flags				(CommandVariableType.CHIP),
	
	info_freq_data			(CommandVariableType.DATA),
	info_pwm_data			(CommandVariableType.DATA),

	sys_time_ticks			(CommandVariableType.PROG), /// new v1.0
	sys_time_ssec			(CommandVariableType.PROG),
	
	lcd_menu_state			(CommandVariableType.PROG),
	lcd_menu_mul			(CommandVariableType.PROG),
	lcd_menu_idx			(CommandVariableType.PROG),
	lcd_menu_value_idx		(CommandVariableType.PROG),
	lcd_menu_time_cnt		(CommandVariableType.PROG),
	
	mal_pc					(CommandVariableType.PROG),
	mal_state				(CommandVariableType.PROG),
	mal_var					(CommandVariableType.PROG),
	
	stv_state				(CommandVariableType.PROG),
	stv_time_cnt			(CommandVariableType.PROG),
	stv_mode_org			(CommandVariableType.PROG),
	stv_map_idx				(CommandVariableType.PROG),
	
	// Deleted command v1.1
	pulse_inv				(CommandVariableType.CONF,WireChipFlags.PWM),
	pulse_trig_delay		(CommandVariableType.CONF,WireChipFlags.PWM),
	pulse_trig_delay_cnt	(CommandVariableType.DATA),
	pulse_post_delay_cnt	(CommandVariableType.DATA),
	dev_freq				(CommandVariableType.DATA),
	dev_freq_cnt			(CommandVariableType.DATA),
	avr_pin18_map			(CommandVariableType.CONF,WireChipFlags.AVR_MEGA),
	avr_pin19_map			(CommandVariableType.CONF,WireChipFlags.AVR_MEGA),
	avr_pin47_map			(CommandVariableType.CONF,WireChipFlags.AVR_MEGA),
	avr_pin48_map			(CommandVariableType.CONF,WireChipFlags.AVR_MEGA),
	avr_pin49_map			(CommandVariableType.CONF,WireChipFlags.AVR_MEGA),
	
	// Deleted commands v1.0
	freq_pwm_data			(CommandVariableType.FREQ), // renamed
	pulse_data				(CommandVariableType.DATA),
	pulse_dir_cnt			(CommandVariableType.DATA),
	pulse_bank_cnt			(CommandVariableType.DATA),
	
	// Deleted commands v0.9
	req_pwm_freq			(CommandVariableType.CHIP),
	swc_trig				(CommandVariableType.CHIP),
	swc_mode				(CommandVariableType.CHIP),
	swc_mode_org			(CommandVariableType.CHIP),
	stv_warn_mode			(CommandVariableType.CHIP),
	stv_error_mode			(CommandVariableType.CHIP);
	
	
	
	private CommandVariableType type = null;
	protected WireChipFlags chipFlagDependency = null;
	protected int id = -1;
	protected int maxValue = 65535;
	protected int maxIndexA = -1;
	protected int maxIndexB = -1;
	protected int mapIndex = -1;
	protected boolean mapIndexTrigger = false;
	protected boolean disabled = false;
	protected String[] listValues = null;
	protected boolean magicTopListValue = false;
	protected String aliasName = null;
	protected WirePulseMode[] pulseModeDependency = null;
	
	private CommandName(CommandVariableType type) {
		this(type,null);
	}
	private CommandName(CommandVariableType type,WireChipFlags chipFlagDependency) {
		this.type=type;
		this.chipFlagDependency=chipFlagDependency;
		this.listValues = new String[] {"DISCONN"}; // note this is quick fix for layout todo
	}
	
	public boolean isMappable() {
		return mapIndex!=-1;
	}

	public boolean isIndexedA() {
		return maxIndexA!=-1;
	}
	
	public boolean isIndexedB() {
		return maxIndexB!=-1;
	}
	
	public boolean isDisabled() {
		return disabled;
	}
	
	public boolean isPulseModeDependency() {
		if (pulseModeDependency==null) {
			return false;
		}
		if (pulseModeDependency.length==0) {
			return false;
		}
		return true;
	}
	public WirePulseMode[] getPulseModeDependencies() {
		return pulseModeDependency;
	}
	
	public WireChipFlags getChipFlagDependency() {
		return chipFlagDependency;
	}
	
	public int getMaxValue() {
		return maxValue;
	}

	public CommandVariableType getType() {
		return type;
	}

	public int getMaxIndexA() {
		return maxIndexA;
	}

	public int getMaxIndexB() {
		return maxIndexB;
	}

	public int getMapIndex() {
		return mapIndex;
	}
	
	public int getId() {
		return id;
	}
	
	public boolean isMapIndexTrigger() {
		return mapIndexTrigger;
	}
	
	public String[] getListValues() {
		return listValues;
	}
	
	public String getAliasName() {
		return aliasName;
	}
	
	public boolean isAliased() {
		return aliasName!=null;
	}
	
	public boolean isMagicTopListValue() {
		return magicTopListValue;
	}
	
	static public CommandName valueOfMapIndex(int mapIdx) {
		for (CommandName cn:values()) {
			if (cn.getMapIndex()==mapIdx) {
				return cn;
			}
		}
		return null;
	}
	
	static public List<CommandName> valuesMapIndex() {
		List<CommandName> result = new ArrayList<CommandName>(50);
		for (CommandName cn:values()) {
			if (cn.isMappable()) {
				result.add(cn);
			}
		}
		return result;
	}
	
	static public List<CommandName> decodeCommandList(String setting) {
		List<CommandName> result = new ArrayList<CommandName>(50);
		if (setting!=null && setting.isEmpty()==false) {
			String[] ss = setting.split(",");
			for (String s:ss) {
				if (s.contains("_*")) {
					String prefix = s.substring(0,s.indexOf('*')-1);
					for (CommandName cn:values()) {
						if (cn.name().startsWith(prefix)) {
							result.add(cn);
						}
					}
					continue;
				}
				CommandName cn = null;
				try {
					cn = CommandName.valueOf(s);
				} catch (Exception e) {
				}
				if (cn!=null) {
					result.add(cn);
				}
			}
		}
		return result;
	}
	
	static public String encodeCommandList(List<CommandName> commands) {
		StringBuilder buf = new StringBuilder(100);
		for (int i=0;i<commands.size();i++) {
			CommandName cn = (CommandName)commands.get(i);
			buf.append(cn.name());
			if (i<commands.size()) {
				buf.append(',');
			}
		}
		return buf.toString();
	}
}

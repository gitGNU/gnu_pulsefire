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



#include "pwm.h"

#ifdef SF_ENABLE_PWM

// note: is also called from int.
void PWM_pulsefire(void) {
	if (pf_conf.pulse_trig == PULSE_TRIG_LOOP) {
		return; // no fire support in loop mode
	}
	if (pf_conf.pulse_fire_mode==PULSE_FIRE_MODE_RESET && pf_data.pwm_state != PWM_STATE_FIRE_RESET) {
		return; // no fire support when reset is forced to fire first.
	}
	if (pf_conf.pulse_fire_mode!=PULSE_FIRE_MODE_NOSYNC && pf_data.pwm_state != PWM_STATE_IDLE && pf_data.pwm_state != PWM_STATE_FIRE_RESET) {
		return; // no fire support when train is running synced.
	}
	pf_data.pwm_state            = PWM_STATE_RUN; // Trigger pulse train on external interrupt pin if pulse_trigger
	pf_data.pulse_step           = ZERO;                     // goto step zero
	//pf_data.pulse_trig_delay_cnt = pf_conf.pulse_trig_delay; // reload trig timer
	pf_data.pulse_fire_cnt++;
	pf_data.pulse_fire_freq_cnt++;
	Chip_reg_set(CHIP_REG_PWM_OCR_A,ONE);
	Chip_reg_set(CHIP_REG_PWM_TCNT,ZERO);
	for (uint8_t i=0;i<FIRE_MAP_MAX;i++) {
		uint16_t v = pf_conf.pulse_fire_map[i][QMAP_VAR];
		if (v==QMAP_VAR_NONE) { continue; }
		Vars_setValueInt(v,pf_conf.pulse_fire_map[i][QMAP_VAR_IDX],ZERO,pf_conf.pulse_fire_map[i][QMAP_VALUE_A]);
	}
}



uint16_t PWM_filter_data(uint16_t data) {

	// Apply user defined output enabled flags.
	if (pf_conf.pulse_bank==ZERO) {
		data = data & pf_conf.pulse_mask_a;
	} else {
		data = data & pf_conf.pulse_mask_b;
	}

	// hard limit on output bits, for cleaning extra shifted bits.
	uint16_t out_limit = ZERO;
	for (uint8_t i=ZERO;i < pf_conf.pulse_steps;i++) {
		out_limit |= (ONE << i);
	}
	data = data & out_limit;

	// All output off override, but with respect to inverse request.
	if (pf_conf.pulse_enable == ZERO) {
		data = PULSE_DATA_OFF;
	}

//#ifdef SF_ENABLE_STV
//	if (pf_conf.stv_error_mode == PULSE_MODE_OFF) {
//		// This is so on error and mode off, output stays off while mode changes and innner if save few cycles.
//		if (pf_data.stv_state == STV_STATE_ERROR_MAX || pf_data.stv_state == STV_STATE_ERROR_MIN) {
//			data = PULSE_DATA_OFF;
//		}
//	}
//#endif

	// Inverse per output bank
	if (pf_conf.pulse_bank==ZERO) {
		data = data ^ pf_conf.pulse_inv_a;
	} else {
		data = data ^ pf_conf.pulse_inv_b;
	}

	return data;
}

// Send data to the outputs from extern code
void PWM_send_output(uint16_t data) {
	Chip_out_pwm(PWM_filter_data(data));
}

#ifdef SF_ENABLE_SWC
boolean PWM_soft_warmup(void) {
	uint32_t sys_up_secs = millis()/1000;
	if (sys_up_secs == ZERO) {
		sys_up_secs = ONE; // very fast cpu here.
	}
	if (pf_conf.swc_delay > ZERO && pf_conf.swc_delay > sys_up_secs) {
		return true;
	}
	pf_data.swc_secs_cnt = sys_up_secs - pf_conf.swc_delay; // correct for pre delay
	if (pf_data.swc_secs_cnt == ZERO) {
		pf_data.swc_secs_cnt = ONE;
	}
	if (pf_data.swc_secs_cnt > pf_conf.swc_secs) {
		pf_data.swc_secs_cnt  = ZERO;
		// TODO: move after new time loop code in main
		for (uint8_t i=ZERO;i < SWC_MAP_MAX;i++) {
			uint16_t v = pf_conf.swc_map[i][QMAP_VAR];
			if (v==QMAP_VAR_NONE) {
				continue;
			}
			uint16_t value = pf_conf.swc_map[i][QMAP_VALUE_B];
			if (value==0xFFFF) {
				continue;
			}
			uint16_t vIdx = pf_conf.swc_map[i][QMAP_VAR_IDX];
			Vars_setValueInt(v,vIdx,ZERO,value);
		}
		return false; // we are done with startup.
	}
	uint16_t startup_duty = pf_conf.swc_duty * (pf_conf.swc_secs - pf_data.swc_secs_cnt)/2;
	if (pf_data.pulse_step == ZERO) {
		pf_data.swc_duty_cnt = ZERO; // this is not yet corrent for all modes
	}
	if (pf_data.pulse_step == ONE) {
		pf_data.swc_duty_cnt++;
		if (pf_data.swc_duty_cnt < startup_duty) {
			PWM_send_output(PULSE_DATA_OFF);
			return true; // wait
		}
	}
	return false; // run steps
}
#endif


void PWM_pulse_mode_off(void) {
	pf_data.pwm_data[pf_data.pwm_data_max][PWM_DATA_OUT]=PWM_filter_data(PULSE_DATA_OFF);
	pf_data.pwm_data[pf_data.pwm_data_max][PWM_DATA_CNT]=0xFFFF;
	pf_data.pwm_data_max++;
}

void PWM_pulse_mode_flash_zero(void) {
	uint16_t pwm_on = ZERO;
	uint16_t pwm_off = ZERO;
	if (pf_conf.pulse_bank==ZERO) {
		pwm_on = pf_conf.pwm_on_cnt_a[ZERO];
		pwm_off = pf_conf.pwm_off_cnt_a[ZERO];
	} else {
		pwm_on = pf_conf.pwm_on_cnt_b[ZERO];
		pwm_off = pf_conf.pwm_off_cnt_b[ZERO];
	}
	pf_data.pwm_data[pf_data.pwm_data_max][PWM_DATA_OUT]=PWM_filter_data(PULSE_DATA_ON);
	pf_data.pwm_data[pf_data.pwm_data_max][PWM_DATA_CNT]=pwm_on;
	pf_data.pwm_data_max++;
	pf_data.pwm_data[pf_data.pwm_data_max][PWM_DATA_OUT]=PWM_filter_data(PULSE_DATA_OFF);
	if (pwm_off > ZERO ) {
		pf_data.pwm_data[pf_data.pwm_data_max][PWM_DATA_CNT]=pwm_off;
	} else {
		pf_data.pwm_data[pf_data.pwm_data_max][PWM_DATA_CNT]=pwm_on;
	}
	pf_data.pwm_data_max++;
}

void PWM_pulse_mode_flash(void) {
	uint8_t index = pf_data.pwm_data_max;
	for (uint8_t i=ZERO;i < pf_conf.pulse_steps;i++) {
		uint16_t pwm_on = ZERO;
		uint16_t pwm_off = ZERO;
		if (pf_conf.pulse_bank==ZERO) {
			pwm_on = pf_conf.pwm_on_cnt_a[i];
			pwm_off = pf_conf.pwm_off_cnt_a[i];
		} else {
			pwm_on = pf_conf.pwm_on_cnt_b[i];
			pwm_off = pf_conf.pwm_off_cnt_b[i];
		}
		pf_data.pwm_data[index][PWM_DATA_OUT]=PWM_filter_data(PULSE_DATA_ON);
		pf_data.pwm_data[index][PWM_DATA_CNT]=pwm_on;
		index++;
		if (pwm_off > ZERO) {
			pf_data.pwm_data[index][PWM_DATA_OUT]=PWM_filter_data(PULSE_DATA_OFF);
			pf_data.pwm_data[index][PWM_DATA_CNT]=pwm_off;
			index++;
		}
	}
	pf_data.pwm_data_max = index;
}

void PWM_pulse_mode_train(void) {
	uint8_t pulse_dir = pf_conf.pulse_dir;
	uint16_t pwm_data = ZERO;
	uint8_t index = pf_data.pwm_data_max;
	for (uint8_t i=ZERO;i < pf_conf.pulse_steps;i++) {
		uint16_t pwm_on = ZERO;
		uint16_t pwm_off = ZERO;
		if (pf_conf.pulse_bank==ZERO) {
			pwm_data = pf_conf.pulse_init_a << i;
			pwm_on = pf_conf.pwm_on_cnt_a[i];
			pwm_off = pf_conf.pwm_off_cnt_a[i];
		} else {
			pwm_data = pf_conf.pulse_init_b << i;
			pwm_on = pf_conf.pwm_on_cnt_b[i];
			pwm_off = pf_conf.pwm_off_cnt_b[i];
		}
		if (pulse_dir == PULSE_DIR_RL) {
			pwm_data = reverse_bits(pwm_data,pf_conf.pulse_steps);
		}
		pf_data.pwm_data[index][PWM_DATA_OUT]=PWM_filter_data(pwm_data);
		pf_data.pwm_data[index][PWM_DATA_CNT]=pwm_on;
		index++;
		if (pwm_off > ZERO) {
			pf_data.pwm_data[index][PWM_DATA_OUT]=PWM_filter_data(PULSE_DATA_OFF);
			pf_data.pwm_data[index][PWM_DATA_CNT]=pwm_off;
			index++;
		}
	}
	pf_data.pwm_data_max = index;
}

void PWM_pulse_mode_ppm(void) {
	uint8_t pulse_dir = pf_conf.pulse_dir;
	// Shift all channel data out every step.
	uint8_t index = pf_data.pwm_data_max;
	for (uint8_t i=ZERO;i < pf_conf.pulse_steps;i++) {
		uint16_t pwm_data = ZERO;
		uint16_t pwm_on = ZERO;
		uint16_t pwm_off = ZERO;
		if (pf_conf.pulse_bank==ZERO) {
			pwm_on = pf_conf.pwm_on_cnt_a[i];
			pwm_off = pf_conf.pwm_off_cnt_a[i];
		} else {
			pwm_on = pf_conf.pwm_on_cnt_b[i];
			pwm_off = pf_conf.pwm_off_cnt_b[i];
		}
		for (uint8_t p=pf_conf.ppm_data_offset;p < pf_conf.ppm_data_length;p++) {
			uint16_t ppm_data = ZERO;
			if (pf_conf.pulse_bank==ZERO) {
				ppm_data = pf_conf.ppm_data_a[p];
			} else {
				ppm_data = pf_conf.ppm_data_b[p];
			}
			pwm_data |= ((ppm_data >> i) & ONE) << p;
		}
		if (pulse_dir == PULSE_DIR_RL) {
			pwm_data = reverse_bits(pwm_data,pf_conf.pulse_steps);
		}
		pf_data.pwm_data[index][PWM_DATA_OUT]=PWM_filter_data(pwm_data);
		pf_data.pwm_data[index][PWM_DATA_CNT]=pwm_on;
		index++;
		if (pwm_off > ZERO) {
			pf_data.pwm_data[index][PWM_DATA_OUT]=PWM_filter_data(PULSE_DATA_OFF);
			pf_data.pwm_data[index][PWM_DATA_CNT]=pwm_off;
			index++;
		}
	}
	pf_data.pwm_data_max = index;
}

void PWM_calc_data_dir(void) {
	if (pf_conf.pulse_dir <= PULSE_DIR_RL) {
		return; // no extra step in LR and RL.
	}
	uint8_t pulse_mode = pf_conf.pulse_mode;
	if (pulse_mode < PULSE_MODE_TRAIN) {
		return; // only train and ppm can be reversed.
	}

	// Auto reverse pulse data
	uint8_t index = pf_data.pwm_data_max;
	uint8_t start_index = pf_data.pwm_data_max-ONE;
	uint8_t stop_index = ZERO;
	if (pf_conf.pulse_dir == PULSE_DIR_LRRL_2) {
		start_index = start_index-1;
		stop_index = stop_index+1; // Skip first and last step when not in full mode.
	}
	if (pf_conf.pulse_dir == PULSE_DIR_LRLR) {
		for (uint8_t i=stop_index;i <= start_index;i++) {
			uint16_t pwm_data = pf_data.pwm_data[i][PWM_DATA_OUT];
			uint16_t pwm_cnt  = pf_data.pwm_data[i][PWM_DATA_CNT];
			pf_data.pwm_data[index][PWM_DATA_OUT] = pwm_data;
			pf_data.pwm_data[index][PWM_DATA_CNT] = pwm_cnt;
			index++;
		}
	} else {
		for (uint8_t i=start_index+ONE;i >= stop_index+ONE;i--) {
			uint16_t pwm_data = pf_data.pwm_data[i-ONE][PWM_DATA_OUT];
			uint16_t pwm_cnt  = pf_data.pwm_data[i-ONE][PWM_DATA_CNT];
			pf_data.pwm_data[index][PWM_DATA_OUT] = pwm_data;
			pf_data.pwm_data[index][PWM_DATA_CNT] = pwm_cnt;
			index++;
		}
	}
	pf_data.pwm_data_max = index;
}

void PWM_calc_data(void) {

	// Reset data counter to zero
	pf_data.pwm_data_max = ZERO;

	// Calc pre delay steps
	if (pf_conf.pulse_pre_delay > ZERO) {
		uint8_t max = pf_conf.pulse_pre_mul;
		for (uint8_t i=0;i < max;i++) {
			pf_data.pwm_data[pf_data.pwm_data_max][PWM_DATA_OUT]=PWM_filter_data(PULSE_DATA_OFF);
			pf_data.pwm_data[pf_data.pwm_data_max][PWM_DATA_CNT]=pf_conf.pulse_pre_delay;
			pf_data.pwm_data_max++;
		}
	}

	// Calc pulse data based on mode.
	uint8_t pulse_mode = pf_conf.pulse_mode;
	switch (pulse_mode) {
		case PULSE_MODE_FLASH_ZERO:    PWM_pulse_mode_flash_zero();    break;
		case PULSE_MODE_FLASH:         PWM_pulse_mode_flash();         break;
		case PULSE_MODE_TRAIN:         PWM_pulse_mode_train();         break;
		case PULSE_MODE_PPM:           PWM_pulse_mode_ppm();           break;
		case PULSE_MODE_OFF:default:   PWM_pulse_mode_off();           break;
	}

	// Calc reverse or dub data step
	PWM_calc_data_dir();

	// Add post delay step
	if (pf_conf.pulse_post_delay > ZERO) {
		uint8_t max = pf_conf.pulse_post_mul;
		uint16_t data = PWM_filter_data(PULSE_DATA_OFF);
		if (pf_conf.pulse_post_hold==ONE) {
			data = pf_data.pwm_data[pf_data.pwm_data_max-ONE][PWM_DATA_OUT];
		}
		for (uint8_t i=0;i < max;i++) {
			pf_data.pwm_data[pf_data.pwm_data_max][PWM_DATA_OUT]=data;
			pf_data.pwm_data[pf_data.pwm_data_max][PWM_DATA_CNT]=pf_conf.pulse_post_delay;
			pf_data.pwm_data_max++;
		}
	}
}

// Do all work per timer step cnt
void PWM_do_work(void) {
	uint8_t pwm_state = pf_data.pwm_state;
	if (pwm_state == PWM_STATE_STEP_DUTY) {
		return; // waiting for step duty
	}
	if (pwm_state == PWM_STATE_FIRE_HOLD) {
		return; // wait in fire hold
	}
	uint8_t pulse_trig = pf_conf.pulse_trig;
	if (pulse_trig != PULSE_TRIG_LOOP && (pwm_state == PWM_STATE_IDLE || pwm_state == PWM_STATE_FIRE_RESET)) {
		return; // disable when in manual trigger fire.
	}

	// time step with counter
	uint8_t pwm_loop = pf_conf.pwm_loop;
	if (pwm_loop > ZERO) {
		if (pf_data.pwm_loop_cnt < pwm_loop) {
			pf_data.pwm_loop_cnt++;
			return;
		}
		pf_data.pwm_loop_cnt = ZERO;
	}

	// Get step data and wait time
	uint8_t pulse_step = pf_data.pulse_step;
	uint16_t data_out = pf_data.pwm_data[pulse_step][PWM_DATA_OUT];
	uint16_t data_cnt = pf_data.pwm_data[pulse_step][PWM_DATA_CNT];

	// Set output and set registers.
	Chip_out_pwm(data_out);
	Chip_reg_set(CHIP_REG_PWM_OCR_A,data_cnt);
	Chip_reg_set(CHIP_REG_PWM_TCNT,ZERO);

	// Loop pulse steps
	if (pulse_step >= pf_data.pwm_data_max-ONE) {
		pulse_step = ZERO;
		if (pulse_trig != PULSE_TRIG_LOOP) {
			pf_data.pwm_state = PWM_STATE_FIRE_END; // timeout after trigger
		}
	} else {
		pulse_step++; // Goto next step
	}
	pf_data.pulse_step = pulse_step;
}


// Timer interrupt for step on time
#ifdef SF_ENABLE_RM
void PWM_do_work_aa(void) {
/*
	// Check for soft startup
#ifdef SF_ENABLE_SWC
	if (pf_data.swc_secs_cnt > ZERO) {
		if (PWM_soft_warmup()) {
			return; // wait in startup mode
		}
	}
#endif

	// wait for pulse trig delay
	if (pf_conf.pulse_trig != PULSE_TRIG_LOOP && pf_data.pulse_trig_delay_cnt > ZERO) {
		pf_data.pulse_trig_delay_cnt--;
		return;
	}

	// wait for pulse post delay
	if (pf_data.pwm_state == PWM_STATE_WAIT_POST) {
		PWM_send_output(PULSE_DATA_OFF);
		pf_data.pulse_post_delay_cnt++;
		uint32_t pre_train_wait = ((F_CPU/pf_conf.pwm_on_cnt_a[0]/100) * pf_conf.pulse_post_delay) / pf_conf.pwm_loop;
		if (pf_data.pulse_post_delay_cnt < pre_train_wait) {
			return;
		}
		pf_data.pulse_post_delay_cnt = ZERO;
		pf_data.pwm_state = PWM_STATE_RUN;
	}

	// use - for letting last output time correctly until off.
	if (pf_data.pwm_state == PWM_STATE_FIRE_END) {
		pf_data.pwm_state = PWM_STATE_IDLE;
		pf_data.pulse_fire = ZERO;
		PWM_send_output(PULSE_DATA_OFF);
		return;
	}


	// check on first/zero step todo automatic holding of the step.

	if (pf_conf.pulse_hold_auto > ZERO && pf_data.pulse_step == pf_conf.pulse_hold_auto - ONE) {
		pf_data.pwm_state = PWM_STATE_FIRE_HOLD;
		if (pf_conf.pulse_hold_autoclr > ZERO) {
			PWM_send_output(PULSE_DATA_OFF);
		}
		// goto next step for resume
	}

	// check for output rotation after last step
	
	if (pf_data.pulse_step  >= pf_conf.pulse_steps - ONE) {
		pf_data.pulse_step     = ZERO;                    // goto step zero
		pf_data.pulse_trig_delay_cnt = pf_conf.pulse_trig_delay; // reload trig timer
		pf_data.pulse_bank_cnt = pf_conf.pulse_bank;      // load pulse bank after pulse

		if (pf_conf.pulse_trig != PULSE_TRIG_LOOP) {
			pf_data.pwm_state = PWM_STATE_FIRE_END;         // timeout after trigger
		} else if (pf_conf.pulse_post_delay > ZERO) {
			pf_data.pwm_state = PWM_STATE_WAIT_POST;        // timeout after pulse train
		}
	} else {
		pf_data.pulse_step++;                             // Goto next step in pulse fire train
	}
*/
}
#endif


#endif



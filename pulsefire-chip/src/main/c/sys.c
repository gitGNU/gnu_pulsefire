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



#include "sys.h"

void Sys_time_int(void) {
	pf_data.sys_time_ticks++;
	if (pf_data.sys_time_ticks >= (F_CPU/8/256/100)) { // 78 fits in 8bit on avr8
		pf_data.sys_time_ticks=ZERO;
		pf_data.sys_time_ssec++;
	}
}

void Sys_do_int(uint8_t pin_int) {

	uint8_t mode = ZERO;
	if (pin_int==ZERO) {
		pf_data.int_0freq_cnt++;
		mode = pf_conf.int_0mode;
	} else {
		pf_data.int_1freq_cnt++;
		mode = pf_conf.int_1mode;
	}
	if (mode == INT_MODE_PULSE_FIRE) {
#ifdef SF_ENABLE_PWM
		PWM_pulsefire();
		//if (pf_conf.avr_pin2_map == PIN2_FIRE_IN && pf_data.pulse_fire == ZERO) {
		//	Vars_setValueInt(Vars_getIndexFromPtr((CHIP_PTR_TYPE*)&pf_data.pulse_fire),ZERO,ZERO,ONE);
		//	return;
#endif
	} else if (mode == INT_MODE_PULSE_HOLD) {
#ifdef SF_ENABLE_PWM
	if (pf_data.pulse_hold_fire == ZERO) {
		Vars_setValueInt(Vars_getIndexFromPtr((CHIP_PTR_TYPE*)&pf_data.pulse_hold_fire),ZERO,ZERO,ONE);
		return;
	}
#endif
	} else if (mode == INT_MODE_MAP) {
		for (uint8_t i=ZERO;i < INT_MAP_MAX;i++) {
			uint16_t v = pf_conf.int_map[i][QMAP_VAR];
			if (v==QMAP_VAR_NONE) {
				continue;
			}
			uint16_t value = ZERO;
			if (pin_int==ZERO) {
				value = pf_conf.int_map[i][QMAP_VALUE_A];
			} else {
				value = pf_conf.int_map[i][QMAP_VALUE_B];
			}
			if (value==ZERO) {
				continue;
			}
			uint16_t vIdx = pf_conf.int_map[i][QMAP_VAR_IDX];
			Vars_setValueInt(v,vIdx,ZERO,value);
		}
	}
}

void Sys_loop(void) {

	uint32_t current_time = millis();
	if (current_time < pf_data.int_time_cnt) {
		return;
	}
	pf_data.int_time_cnt = current_time + 1000; // check every second

#ifdef SF_ENABLE_PWM
	// also update pulse_fire_freq here when is different
	if (pf_data.pulse_fire_freq_cnt != pf_data.pulse_fire_freq) {
		Vars_setValue(Vars_getIndexFromName(UNPSTR(pmDataPulseFireFreq)),ZERO,ZERO,pf_data.pulse_fire_freq_cnt);
	}
	pf_data.pulse_fire_freq_cnt = ZERO;
#endif

	if (pf_data.int_0freq_cnt != pf_data.int_0freq) {
		Vars_setValue(Vars_getIndexFromName(UNPSTR(pmDataInt0Freq)),ZERO,ZERO,pf_data.int_0freq_cnt);
	}
	if (pf_data.int_1freq_cnt != pf_data.int_1freq) {
		Vars_setValue(Vars_getIndexFromName(UNPSTR(pmDataInt1Freq)),ZERO,ZERO,pf_data.int_1freq_cnt);
	}

	pf_data.int_0freq_cnt = ZERO;
	pf_data.int_1freq_cnt = ZERO;
}


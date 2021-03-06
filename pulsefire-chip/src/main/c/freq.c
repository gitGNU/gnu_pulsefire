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



#include "freq.h"

#ifdef SF_ENABLE_PWM

#define FREQ_CLK_SCALE_SIZE (sizeof FREQ_CLK_SCALE / sizeof FREQ_CLK_SCALE[0])
static int FREQ_CLK_SCALE[] = {1,8,64,256,1024 };

uint8_t calc_pwm_duty(uint8_t idx) {
	uint32_t pwm_cnt_total = ZERO;
	uint32_t pwm_cnt_on_total = ZERO;
	for (uint8_t i=ZERO;i <= pf_data.pwm_data_size-ONE;i++) {
		uint16_t pwm_data = pf_data.pwm_data[i][PWM_DATA_OUT];
		uint16_t pwm_cnt  = pf_data.pwm_data[i][PWM_DATA_CNT];
		pwm_cnt_total+=pwm_cnt;
		if (((pwm_data >> idx) & ONE) > ZERO) {
			pwm_cnt_on_total+=pwm_cnt;
		}
	}
	return pwm_cnt_on_total / (pwm_cnt_total / 100);
}

uint32_t calc_pwm_freq(uint8_t idx) {
	uint16_t clock_div = ONE;
	if (pf_conf.pwm_clock > ZERO && pf_conf.pwm_clock <= FREQ_CLK_SCALE_SIZE) {
		clock_div = FREQ_CLK_SCALE[pf_conf.pwm_clock-ONE]; // 0 = stop, 1 = 1
	}
	uint32_t pwm_cnt_total = ZERO;
	uint16_t step_pulses = ZERO;
	uint16_t step_last = ZERO;
	for (uint8_t i=ZERO;i < pf_data.pwm_data_size;i++) {
		uint16_t pwm_data = pf_data.pwm_data[i][PWM_DATA_OUT];
		uint16_t pwm_cnt  = pf_data.pwm_data[i][PWM_DATA_CNT];
		uint16_t step_out = ((pwm_data >> idx) & ONE);
		if (step_out != step_last) {
			step_pulses++;
		}
		step_last = step_out;
		pwm_cnt_total+=pwm_cnt;
	}
	if (step_pulses==ZERO) {
		return ZERO;
	}
	if (step_pulses==ONE) {
		step_pulses++;
	}

	uint8_t loop = pf_conf.pwm_loop + ONE; // 0 = /1, 1=/2, 3=/3
	uint32_t freqTrain = ((F_CPU*100) / clock_div / loop / pwm_cnt_total) * (step_pulses/2);
	return freqTrain;
}

void Freq_requestTrainFreq(void) {
	uint32_t freq = pf_conf.pwm_req_freq;
	uint8_t  idx  = pf_conf.pwm_req_idx;
	if (freq == ZERO) {
		return;
	}
	if (idx > OUTPUT_MAX && idx!=QMAP_VAR_IDX_ALL) {
		return; // idx is on non supported index.
	}

	// note freq is in 100 so 1123 = 11.23 Hz !!
	freq *= 2; // double to hz.
	//freq *= pf_conf.pulse_steps; // multiply to one output.

	// Get variable ids.
	uint8_t pwmLoopIdx   = Vars_getIndexFromPtr((CHIP_PTR_TYPE*)&pf_conf.pwm_loop);
	uint8_t pwmClockIdx  = Vars_getIndexFromPtr((CHIP_PTR_TYPE*)&pf_conf.pwm_clock);
	uint8_t pwmOnCntIdx  = ZERO;
	uint8_t pwmOffCntIdx = ZERO;
	if (pf_conf.pulse_bank == ZERO) {
		pwmOnCntIdx  = Vars_getIndexFromPtr((CHIP_PTR_TYPE*)&pf_conf.pwm_on_cnt_a);
		pwmOffCntIdx = Vars_getIndexFromPtr((CHIP_PTR_TYPE*)&pf_conf.pwm_off_cnt_a);
	} else {
		pwmOnCntIdx  = Vars_getIndexFromPtr((CHIP_PTR_TYPE*)&pf_conf.pwm_on_cnt_b);
		pwmOffCntIdx = Vars_getIndexFromPtr((CHIP_PTR_TYPE*)&pf_conf.pwm_off_cnt_b);
	}

	// use pwm_loop to divede by 10 and make bigger so _delta works nice.
	uint16_t pwmLoop = ZERO; // pf_conf.pulse_steps; // * FREQ_MUL
	// freq to low for prescale+tcnt so must use higher train_loop
	uint32_t preFreq = (F_CPU*10)/1024/freq; // 1024 is max clock diveder
	if ( preFreq > 0xFF00)       { pwmLoop = 1;
		if ( preFreq/2 > 0xFF00)   { pwmLoop *= 2;
			if ( preFreq/4 > 0xFF00) { pwmLoop *= 2; }
		}
	}
	
	// Search for best clock divider
	for (uint8_t i = ZERO; i < FREQ_CLK_SCALE_SIZE; i++) {
		uint32_t tcntDivCalc = (F_CPU*10)/FREQ_CLK_SCALE[i]/freq;
		if (tcntDivCalc < 0xFF00) {
			Vars_setValue(pwmClockIdx,0,0,i+ONE); // update pwm_clock
			break;
		}
	}

	// Calc on time
	uint16_t compaValue = (F_CPU*10)/FREQ_CLK_SCALE[pf_conf.pwm_clock-ONE]/freq;

	// Calc off time from duty
	uint8_t duty = pf_conf.pwm_req_duty;
	uint16_t compbValue = (compaValue / 100) * duty;
	if (compaValue < 1000) {
		compbValue = (compaValue * duty) / 100; // reverse calc for more persision in high range
	}

	// up all vars if over 100% duty.
	if (duty > 100 && compaValue > 40000) {
		pwmLoop *= 2;
		compaValue = compaValue/2;
		compbValue = (compaValue / 100) * duty;
	}

	// Set comp a/b and set for index or set all
	if (idx == QMAP_VAR_IDX_ALL) {
		for (uint8_t i=ZERO;i < OUTPUT_MAX;i++) {
			Vars_setValue(pwmOnCntIdx,i,0,compaValue);
			Vars_setValue(pwmOffCntIdx,i,0,compbValue);
		}
	} else {
		Vars_setValue(pwmOnCntIdx,idx,0,compaValue);
		Vars_setValue(pwmOffCntIdx,idx,0,compbValue);
	}

	// set pwm_loop last because duty can turn this up.
	Vars_setValue(pwmLoopIdx,0,0,pwmLoop);
}

#endif



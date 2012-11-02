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


#include "input.h"

// read out digital values.
void Input_loopDic(void) {
	uint32_t current_time = millis();
	if (current_time < pf_data.dic_time_cnt) {
		return;
	}
	pf_data.dic_time_cnt = current_time + DIC_INPUT_TIME;

	uint16_t dic_data = Chip_in_dic();
	for (uint8_t i=ZERO;i < DIC_MAP_MAX ;i++) {
		if ( ((pf_conf.dic_enable >> i) & ONE) == ZERO ) {
			continue; // Enable bit per input
		}
		uint8_t result    = (dic_data >> i) & ONE;
		if ( ((pf_conf.dic_inv >> i) & ONE) > ZERO ) {
			if (result > ZERO) {
				result = ZERO;	// invert input
			} else {
				result = ONE;
			}
		}
		uint8_t resultOld = (pf_data.dic_value >> i) & ONE;
		if (result == resultOld) {
			continue; // no change
		}
#ifdef SF_ENABLE_DEBUG
		Serial_printCharP(PSTR("Read dic: "));Serial_printDec((int)i);
		Serial_printCharP(PSTR(" value: "));Serial_printDec((int)result);
		Serial_printCharP(PSTR(" old: "));Serial_printDec((int)resultOld);
		Serial_println();
#endif
		uint32_t dic_value_new = pf_data.dic_value;
		if (result == ZERO) {
			dic_value_new -= (ONE << i); // clear bit in data
		} else {
			dic_value_new += (ONE << i); // set bit in data
		}
		uint8_t dicVarIdx = Vars_getIndexFromName(UNPSTR(pmDataDicValue)); // set via index to print to serial.
		Vars_setValue(dicVarIdx,ZERO,ZERO,dic_value_new);

		if (pf_conf.dic_map[i][QMAP_VAR] == QMAP_VAR_NONE) {
			continue; // no mapping
		}
		if (result == ZERO) {
			Vars_setValue(pf_conf.dic_map[i][QMAP_VAR],pf_conf.dic_map[i][QMAP_VAR_IDX],ZERO,pf_conf.dic_map[i][QMAP_VALUE_A]);
		} else {
			if ( ((pf_conf.dic_sync >> i) & ONE) == ZERO ) { // only trigger to zero.
				Vars_setValue(pf_conf.dic_map[i][QMAP_VAR],pf_conf.dic_map[i][QMAP_VAR_IDX],ZERO,pf_conf.dic_map[i][QMAP_VALUE_B]);
			}
		}
	}
}

#ifdef SF_ENABLE_ADC
void Input_adc_int(uint16_t result) {
	pf_data.adc_state = ADC_STATE_DONE;
	pf_data.adc_state_value = result;
}
#endif

// read out analog values.
#ifdef SF_ENABLE_ADC
void Input_loopAdc(void) {
	uint32_t current_time = millis();
	if (current_time < pf_data.adc_time_cnt) {
		//return;
	}
	pf_data.adc_time_cnt = current_time + ADC_INPUT_TIME;

	if (pf_data.adc_state==ADC_STATE_RUN) {
		return; // wait more
	}
	if (pf_data.adc_state==ADC_STATE_IDLE) {
		pf_data.adc_state = ADC_STATE_RUN;
		pf_data.adc_state_idx = ZERO;
		Chip_in_adc(ZERO);
		return;
	}

	for (uint8_t i=pf_data.adc_state_idx;i <= ADC_MAP_MAX;i++) {
#ifdef SF_ENABLE_AVR
#ifdef SF_ENABLE_LCD
#ifndef SF_ENABLE_SPI
		if (i < 4) {
			//pin_idx++;
			continue; // only read 4 & 5 in normal connection mode.
		}
#endif
#endif
#endif
		if (i==ADC_MAP_MAX) {
			pf_data.adc_state = ADC_STATE_RUN;
			pf_data.adc_state_idx=ZERO;
			Chip_in_adc(pf_data.adc_state_idx);
			return;
		}
		if (i!=pf_data.adc_state_idx) {
			pf_data.adc_state = ADC_STATE_RUN;
			pf_data.adc_state_idx=i;
			Chip_in_adc(pf_data.adc_state_idx);
			return;
		}
		if ( ((pf_conf.adc_enable >> i) & ONE) == ZERO ) {
			continue; // Enable bit per input
		}

		uint16_t valueAdc    = pf_data.adc_state_value; // analogRead(i);
		uint16_t valueAdcOld = pf_data.adc_value[i];
		if (valueAdc == valueAdcOld) {
			continue; // no change
		}
		if (pf_conf.adc_jitter > ZERO) {
			uint16_t c = valueAdc - valueAdcOld; // only update when change is bigger then jitter treshhold
			if (c > 0 && c < pf_conf.adc_jitter)        { continue; }
			if (c < 0 && c > (ZERO-pf_conf.adc_jitter)) { continue; }
		}

		uint8_t adcVarIdx = Vars_getIndexFromName(UNPSTR(pmDataAdcValue));
		Vars_setValue(adcVarIdx,i,ZERO,valueAdc);

		if (pf_conf.adc_map[i][QMAP_VAR] == QMAP_VAR_NONE) {
			continue; // no mapping
		}

		// map to min/max value and assign to variable
		valueAdc = mapValue(valueAdc,ZERO,ADC_VALUE_MAX,pf_conf.adc_map[i][QMAP_VALUE_A],pf_conf.adc_map[i][QMAP_VALUE_B]);
		if (pf_conf.adc_map[i][QMAP_VAR] < PF_VARS_SIZE) {
			Vars_setValue(pf_conf.adc_map[i][QMAP_VAR],pf_conf.adc_map[i][QMAP_VAR_IDX],ZERO,valueAdc);
		}
	}
}
#endif



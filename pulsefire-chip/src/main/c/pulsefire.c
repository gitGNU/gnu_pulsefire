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


//Short name   : PulseFire
//Short desc   : Automatic PulseFire Seqence Generator.
//First created: 26-Apr-2011
//Last modified: 15-Nov-2011
//Last version : 0.9
//First Author : Willem Cazander
//License-Type : BSD 2-Clause (licence.txt and http://www.opensource.org/licenses/bsd-license.php)
//IO-Hardware  : see IO_DEF_* or IO_EXT_* #defines in vars_define.h
//USB-Serial   : 115200b + "Newline" on enter/return
//Website      : http://www.nongnu.org/pulsefire/

#include "vars.h"
#include "serial.h"
#include "ptc.h"
#include "lpm.h"
#include "lcd.h"
#include "input.h"
#include "freq.h"
#include "utils.h"
#include "chip.h"


#ifdef SF_ENABLE_STV
void STV_loop(void) {
	if (pf_prog.stv_state == STV_STATE_OKE) {
		return;
	}
	uint32_t current_time = millis();
	if (current_time < pf_prog.stv_time_cnt) {
		return;
	}
	pf_prog.stv_time_cnt = current_time + 1000; // check every second
	if (pf_prog.stv_state == STV_STATE_WARNING_MAX || pf_prog.stv_state == STV_STATE_ERROR_MAX) {
		uint16_t checkLevel = ZERO;
		uint8_t confWait = ZERO;
		if (pf_prog.stv_state == STV_STATE_WARNING_MAX) {
			checkLevel = pf_conf.stv_max_map[pf_prog.stv_map_idx][QMAP_VALUE_A];
			confWait = pf_conf.stv_warn_secs;
		} else {
			checkLevel = pf_conf.stv_max_map[pf_prog.stv_map_idx][QMAP_VALUE_B];
			confWait = pf_conf.stv_error_secs;
		}
		if (confWait == 0xFF) {
			return; // wait forever
		}
		uint16_t curValue = Vars_getValue(pf_conf.stv_max_map[pf_prog.stv_map_idx][QMAP_VAR],pf_conf.stv_max_map[pf_prog.stv_map_idx][QMAP_VAR_IDX],ZERO);
		if (curValue < checkLevel) {
			pf_prog.stv_wait_cnt++;
			if (pf_prog.stv_wait_cnt < confWait) {
				return; // waiting until timed recovery/
			}
			pf_prog.stv_wait_cnt         = ZERO;
			if (pf_prog.stv_state == STV_STATE_ERROR_MAX && curValue >= pf_conf.stv_max_map[pf_prog.stv_map_idx][QMAP_VALUE_A]) {
				pf_prog.stv_state            = STV_STATE_WARNING_MAX;
			} else {
				pf_prog.stv_state            = STV_STATE_OKE;
			}
			pf_prog.stv_time_cnt         = ZERO;
			pf_prog.stv_map_idx          = ZERO;
			if (pf_prog.stv_state == STV_STATE_WARNING_MAX) {
				if (pf_conf.stv_warn_mode != 0xFF) {
					Vars_setValue(ONE,ZERO,ZERO,pf_conf.stv_warn_mode);
				}
			} else {
				if (pf_conf.stv_error_mode != 0xFF) {
					Vars_setValue(ONE,ZERO,ZERO,pf_prog.stv_mode_org);
				}
			}
		} else {
			pf_prog.stv_wait_cnt = ZERO; // reset waiting time
		}
	} else if (pf_prog.stv_state == STV_STATE_WARNING_MIN || pf_prog.stv_state == STV_STATE_ERROR_MIN) {
		uint16_t checkLevel = ZERO;
		uint8_t confWait = ZERO;
		if (pf_prog.stv_state == STV_STATE_WARNING_MIN) {
			checkLevel = pf_conf.stv_min_map[pf_prog.stv_map_idx][QMAP_VALUE_A];
			confWait = pf_conf.stv_warn_secs;
		} else {
			checkLevel = pf_conf.stv_min_map[pf_prog.stv_map_idx][QMAP_VALUE_B];
			confWait = pf_conf.stv_error_secs;
		}
		if (confWait == 0xFF) {
			return; // wait forever
		}
		uint16_t curValue = Vars_getValue(pf_conf.stv_min_map[pf_prog.stv_map_idx][QMAP_VAR],pf_conf.stv_min_map[pf_prog.stv_map_idx][QMAP_VAR_IDX],ZERO);
		if (curValue >= checkLevel) {
			pf_prog.stv_wait_cnt++;
			if (pf_prog.stv_wait_cnt < confWait) {
				return; // waiting until timed recovery/
			}
			pf_prog.stv_wait_cnt         = ZERO;
			if (pf_prog.stv_state == STV_STATE_ERROR_MIN && curValue <= pf_conf.stv_min_map[pf_prog.stv_map_idx][QMAP_VALUE_A]) {
				pf_prog.stv_state            = STV_STATE_WARNING_MIN;
			} else {
				pf_prog.stv_state            = STV_STATE_OKE;
			}
			pf_prog.stv_time_cnt         = ZERO;
			pf_prog.stv_map_idx          = ZERO;

			if (pf_prog.stv_state == STV_STATE_WARNING_MIN) {
				if (pf_conf.stv_warn_mode != 0xFF) {
					Vars_setValue(ONE,ZERO,ZERO,pf_conf.stv_warn_mode);
				}
			} else {
				if (pf_conf.stv_error_mode != 0xFF) {
					Vars_setValue(ONE,ZERO,ZERO,pf_prog.stv_mode_org);
				}
			}
		} else {
			pf_prog.stv_wait_cnt = ZERO;
		}
	}
}
#endif


int main(void) {

	// Setup
	Vars_setup();
	Chip_setup();
	Serial_setup();
#ifdef SF_ENABLE_LCD
	lcd_setup();
#endif
 
	sei(); // enable interrupts after init
	for(;;) {
		Chip_loop();
		Serial_loop();
#ifdef SF_ENABLE_LCD
		Input_loopLcd();
		lcd_loop();
#endif
#ifdef SF_ENABLE_ADC
		Input_loopAdc();
#endif
#ifdef SF_ENABLE_DIC
		Input_loopDic();
#endif
#ifdef SF_ENABLE_LPM
		LPM_loop();
#endif
#ifdef SF_ENABLE_STV
		STV_loop();
#endif
#ifdef SF_ENABLE_PTC
		PTC_loop();
#endif
#ifdef SF_ENABLE_FRQ
		Freq_loop();
#endif
	}
}

// EOF


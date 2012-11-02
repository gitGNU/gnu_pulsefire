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

#include "ptc.h"



#ifdef SF_ENABLE_PTC
void ptc_time0_run(void) {
	if (pf_conf.ptc_0run == PTC_RUN_OFF) {
		return; // switch off
	}
	if (pf_conf.ptc_0run != PTC_RUN_LOOP && pf_data.ptc_0run_cnt >= pf_conf.ptc_0run) {
		return; // we are done running until next reset_data
	}
	pf_data.ptc_0cnt++;
	while (true) {
		uint16_t varIdx = pf_conf.ptc_0map[pf_data.ptc_0map_idx][QMAP_VAR];
		if (varIdx == QMAP_VAR_NONE) {
			pf_data.ptc_0map_idx = ZERO; // loop to zero after non-mapping
			pf_data.ptc_0run_cnt++;
			break;
		}

		if (pf_data.ptc_0step == ZERO) {
			pf_data.ptc_0step = ONE;
#ifdef SF_ENABLE_DEBUG
			Serial_printCharP(PSTR("Do time0 step: "));Serial_printDec((int)pf_data.ptc_0map_idx);Serial_printChar(" ");
			Serial_printDec(varIdx);Serial_printChar(" ");Serial_printDec(pf_conf.ptc_0map[pf_data.ptc_0map_idx][QMAP_VALUE_B]);Serial_printChar(" ");
			Serial_printDec(pf_conf.ptc_0map[pf_data.ptc_0map_idx][QMAP_VALUE_A]);Serial_println();
#endif
			// Execute the steps
			Vars_setValue(varIdx,pf_conf.ptc_0map[pf_data.ptc_0map_idx][QMAP_VAR_IDX],ZERO,pf_conf.ptc_0map[pf_data.ptc_0map_idx][QMAP_VALUE_A]);
		}

		// Waittime per time step
		uint16_t waitTime = pf_conf.ptc_0map[pf_data.ptc_0map_idx][QMAP_VALUE_B];
		if (waitTime != ZERO && pf_data.ptc_0cnt < waitTime) {
			return; // waiting
		}
		pf_data.ptc_0cnt = ZERO;
		// waittime per time step * multiplier
		pf_data.ptc_0mul_cnt++;
		if (waitTime != ZERO && pf_data.ptc_0mul_cnt < pf_conf.ptc_0mul) {
			return; // wait
		}
		pf_data.ptc_0mul_cnt = ZERO;

		// Update to map idx to next step
		pf_data.ptc_0step = ZERO;
		pf_data.ptc_0map_idx++;
		if (pf_data.ptc_0map_idx > PTC_TIME_MAP_MAX) {
			pf_data.ptc_0map_idx = ZERO;
			pf_data.ptc_0run_cnt++;
			break;
		}
	}
}
#endif

#ifdef SF_ENABLE_PTC
void ptc_time1_run(void) {
	if (pf_conf.ptc_1run == PTC_RUN_OFF) {
		return; // switch off
	}
	if (pf_conf.ptc_1run != PTC_RUN_LOOP && pf_data.ptc_1run_cnt >= pf_conf.ptc_1run) {
		return; // we are done running until next reset_data
	}
	pf_data.ptc_1cnt++;
	while (true) {
		uint16_t varIdx = pf_conf.ptc_1map[pf_data.ptc_1map_idx][QMAP_VAR];
		if (varIdx == QMAP_VAR_NONE) {
			pf_data.ptc_1map_idx = ZERO; // loop to zero after non-mapping
			pf_data.ptc_1run_cnt++;
			break;
		}
		if (pf_data.ptc_1step == ZERO) {
			pf_data.ptc_1step = ONE;
#ifdef SF_ENABLE_DEBUG
			Serial_printCharP(PSTR("Do time1 step: "));Serial_printDec((int)pf_data.ptc_1map_idx);Serial_printChar(" ");
			Serial_printDec(varIdx);Serial_printChar(" ");Serial_printDec(pf_conf.ptc_0map[pf_data.ptc_1map_idx][QMAP_VALUE_B]);Serial_printChar(" ");
			Serial_printDec(pf_conf.ptc_1map[pf_data.ptc_1map_idx][QMAP_VALUE_A]);Serial_println();
#endif
			// Execute the steps
			Vars_setValue(varIdx,pf_conf.ptc_1map[pf_data.ptc_1map_idx][QMAP_VAR_IDX],ZERO,pf_conf.ptc_1map[pf_data.ptc_1map_idx][QMAP_VALUE_A]);
		}

		// Waittime per time step
		uint16_t waitTime = pf_conf.ptc_1map[pf_data.ptc_1map_idx][QMAP_VALUE_B];
		if (waitTime != ZERO && pf_data.ptc_1cnt < waitTime) {
			return; // waiting
		}
		pf_data.ptc_1cnt = ZERO;
		// waittime per time step * multiplier
		pf_data.ptc_1mul_cnt++;
		if (waitTime != ZERO && pf_data.ptc_1mul_cnt < pf_conf.ptc_1mul) {
			return; // wait
		}
		pf_data.ptc_1mul_cnt = ZERO;

		// Update to map idx to next step
		pf_data.ptc_1step = ZERO;
		pf_data.ptc_1map_idx++;
		if (pf_data.ptc_1map_idx > PTC_TIME_MAP_MAX) {
			pf_data.ptc_1map_idx = ZERO;
			pf_data.ptc_1run_cnt++;
			break;
		}
	}
}
#endif


#ifdef SF_ENABLE_PTT
void ptt_check_triggers(void) {
	for (uint8_t t=ZERO;t<PTT_TRIG_VAR_SIZE;t++) {
		if (pf_data.ptt_fire[t] == ZERO) {
			continue;
		}
		boolean stepStop = false;
		while (stepStop==false) {
			if (pf_data.ptt_idx[t] > PTT_TRIG_MAP_MAX) {
				pf_data.ptt_idx[t] = ZERO; // force start at zero
			}
			boolean wait = false;
			pf_data.ptt_cnt[t]++;
			uint8_t trigIdx   = pf_data.ptt_idx[t];
			uint16_t varId    = QMAP_VAR_NONE;
			uint16_t waitTime = ZERO;
			uint16_t varIdx   = ZERO;
			uint16_t varValue = ZERO;
			if (t==0) {
				varId    = pf_conf.ptt_0map[trigIdx][QMAP_VAR];
				waitTime = pf_conf.ptt_0map[trigIdx][QMAP_VALUE_B];
				varIdx   = pf_conf.ptt_0map[trigIdx][QMAP_VAR_IDX];
				varValue = pf_conf.ptt_0map[trigIdx][QMAP_VALUE_A];
			} else if (t==1) {
				varId    = pf_conf.ptt_1map[trigIdx][QMAP_VAR];
				waitTime = pf_conf.ptt_1map[trigIdx][QMAP_VALUE_B];
				varIdx   = pf_conf.ptt_1map[trigIdx][QMAP_VAR_IDX];
				varValue = pf_conf.ptt_1map[trigIdx][QMAP_VALUE_A];
			} else if (t==2) {
				varId    = pf_conf.ptt_2map[trigIdx][QMAP_VAR];
				waitTime = pf_conf.ptt_2map[trigIdx][QMAP_VALUE_B];
				varIdx   = pf_conf.ptt_2map[trigIdx][QMAP_VAR_IDX];
				varValue = pf_conf.ptt_2map[trigIdx][QMAP_VALUE_A];
			} else if (t==3) {
				varId    = pf_conf.ptt_3map[trigIdx][QMAP_VAR];
				waitTime = pf_conf.ptt_3map[trigIdx][QMAP_VALUE_B];
				varIdx   = pf_conf.ptt_3map[trigIdx][QMAP_VAR_IDX];
				varValue = pf_conf.ptt_3map[trigIdx][QMAP_VALUE_A];
			}
			if (varId != QMAP_VAR_NONE) {
				if (pf_data.ptt_step[t] == ZERO) {
					pf_data.ptt_step[t] = ONE;
					Vars_setValue(varId,varIdx,ZERO,varValue);
				}
				if (waitTime != ZERO && pf_data.ptt_cnt[t] < waitTime) {
					wait = true; // waiting
					stepStop = true;
				}
			} else {
				pf_data.ptt_idx[t]  = 0xFF; // set trigger off
				pf_data.ptt_fire[t] = ZERO; // reset fire trigger
				pf_data.ptt_step[t] = ZERO; // reset step event 
				stepStop = true;
			}
			if (wait==false) {
				pf_data.ptt_cnt[t] = ZERO;
				pf_data.ptt_idx[t]++;
				pf_data.ptt_step[t] = ZERO; // reset step event 
				if (pf_data.ptt_idx[t] > PTT_TRIG_MAP_MAX) {
					pf_data.ptt_idx[t]  = 0xFF; // set trigger off
					pf_data.ptt_fire[t] = ZERO; // reset fire trigger
					stepStop = true;
				}
			}
		}
	}
}
#endif

#ifdef SF_ENABLE_PTC
void PTC_loop(void) {
	uint32_t current_time = millis();
	if (current_time < pf_data.ptc_sys_cnt) {
		return;
	}
	pf_data.ptc_sys_cnt = current_time + 50; // run 10 times per seconds

#ifdef SF_ENABLE_PTT
	ptt_check_triggers();
#endif
	ptc_time0_run();
	ptc_time1_run();
}
#endif




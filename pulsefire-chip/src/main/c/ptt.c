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

#include "ptt.h"

#ifdef SF_ENABLE_PTT
void Ptt_check_triggers(void) {
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

#ifdef SF_ENABLE_PTT
void Ptt_loop(void) {
	Ptt_check_triggers();
}
#endif




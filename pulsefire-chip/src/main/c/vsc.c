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

#include "vsc.h"

#ifdef SF_ENABLE_VSC0
void Vsc_loop0(void) {
	uint8_t vsc_0mode = pf_conf.vsc_0mode;
	if (vsc_0mode == VSC_MODE_OFF) {
		return; // switch off
	}
	if (pf_data.vsc_0time_cnt < pf_conf.vsc_0time) {
		pf_data.vsc_0time_cnt++;
		return;
	}
	pf_data.vsc_0time_cnt = ZERO;
	uint8_t vsc_0step = pf_conf.vsc_0step;
	if (vsc_0step == ZERO) {
		return;
	}
	for (uint8_t i=ZERO;i < VSC_MAP_MAX;i++) {
		uint8_t state = pf_data.vsc_0state[i];
		if ((vsc_0mode == VSC_MODE_ONCE_UP || vsc_0mode == VSC_MODE_ONCE_DOWN) && state == VSC_STATE_DONE) {
			continue;
		}
		uint16_t v = pf_conf.vsc_0map[i][QMAP_VAR];
		if (v==QMAP_VAR_NONE) {
			continue;
		}
		uint16_t vIdx = pf_conf.vsc_0map[i][QMAP_VAR_IDX];
		uint16_t stepMin = pf_conf.vsc_0map[i][QMAP_VALUE_A];
		uint16_t stepMax = pf_conf.vsc_0map[i][QMAP_VALUE_B];
		uint16_t step = Vars_getValue(v,vIdx,ZERO);
		if (state == VSC_STATE_UP) {
			step += vsc_0step;
			if (step > stepMax) {
				if (vsc_0mode == VSC_MODE_ONCE_UP) {
					pf_data.vsc_0state[i] = VSC_STATE_DONE;
					continue;
				}
				if (vsc_0mode == VSC_MODE_LOOP_UP) {
					step = stepMin;
				}
				if (vsc_0mode == VSC_MODE_LOOP_UPDOWN || vsc_0mode == VSC_MODE_LOOP_DOWN) {
					pf_data.vsc_0state[i] = VSC_STATE_DOWN;
					step = stepMax;
				}
			}
		} else {
			step -= vsc_0step;
			if (step < stepMin) {
				if (vsc_0mode == VSC_MODE_ONCE_DOWN) {
					pf_data.vsc_0state[i] = VSC_STATE_DONE;
					continue;
				}
				if (vsc_0mode == VSC_MODE_LOOP_DOWN) {
					step = stepMax;
				}
				if (vsc_0mode == VSC_MODE_LOOP_UPDOWN || vsc_0mode == VSC_MODE_LOOP_UP) {
					pf_data.vsc_0state[i] = VSC_STATE_UP;
					step = stepMin;
				}
			}
		}
		Vars_setValue(v,vIdx,ZERO,step);
	}
}
#endif

#ifdef SF_ENABLE_VSC1
void Vsc_loop1(void) {
	uint8_t vsc_1mode = pf_conf.vsc_1mode;
	if (vsc_1mode == VSC_MODE_OFF) {
		return; // switch off
	}
	if (pf_data.vsc_1time_cnt < pf_conf.vsc_1time) {
		pf_data.vsc_1time_cnt++;
		return;
	}
	pf_data.vsc_1time_cnt = ZERO;
	uint8_t vsc_1step = pf_conf.vsc_1step;
	if (vsc_1step == ZERO) {
		return;
	}
	for (uint8_t i=ZERO;i < VSC_MAP_MAX;i++) {
		uint8_t state = pf_data.vsc_1state[i];
		if ((vsc_1mode == VSC_MODE_ONCE_UP || vsc_1mode == VSC_MODE_ONCE_DOWN) && state == VSC_STATE_DONE) {
			continue;
		}
		uint16_t v = pf_conf.vsc_1map[i][QMAP_VAR];
		if (v==QMAP_VAR_NONE) {
			continue;
		}
		uint16_t vIdx = pf_conf.vsc_1map[i][QMAP_VAR_IDX];
		uint16_t stepMin = pf_conf.vsc_1map[i][QMAP_VALUE_A];
		uint16_t stepMax = pf_conf.vsc_1map[i][QMAP_VALUE_B];
		uint16_t step = Vars_getValue(v,vIdx,ZERO);
		if (state == VSC_STATE_UP) {
			step += vsc_1step;
			if (step > stepMax) {
				if (vsc_1mode == VSC_MODE_ONCE_UP) {
					pf_data.vsc_1state[i] = VSC_STATE_DONE;
					continue;
				}
				if (vsc_1mode == VSC_MODE_LOOP_UP) {
					step = stepMin;
				}
				if (vsc_1mode == VSC_MODE_LOOP_UPDOWN || vsc_1mode == VSC_MODE_LOOP_DOWN) {
					pf_data.vsc_1state[i] = VSC_STATE_DOWN;
					step = stepMax;
				}
			}
		} else {
			step -= vsc_1step;
			if (step < stepMin) {
				if (vsc_1mode == VSC_MODE_ONCE_DOWN) {
					pf_data.vsc_1state[i] = VSC_STATE_DONE;
					continue;
				}
				if (vsc_1mode == VSC_MODE_LOOP_DOWN) {
					step = stepMax;
				}
				if (vsc_1mode == VSC_MODE_LOOP_UPDOWN || vsc_1mode == VSC_MODE_LOOP_UP) {
					pf_data.vsc_1state[i] = VSC_STATE_UP;
					step = stepMin;
				}
			}
		}
		Vars_setValue(v,vIdx,ZERO,step);
	}
}
#endif

#if defined(SF_ENABLE_VSC0) || defined(SF_ENABLE_VSC1)
void Vsc_setup(void) {
#if defined(SF_ENABLE_VSC0)
	for (uint8_t i=ZERO;i < VSC_MAP_MAX;i++) {
		if (pf_conf.vsc_0mode == VSC_MODE_ONCE_DOWN || pf_conf.vsc_0mode == VSC_MODE_LOOP_DOWN ) {
			pf_data.vsc_0state[i] = VSC_STATE_DOWN;
		} else {
			pf_data.vsc_0state[i] = VSC_STATE_UP;
		}
	}
#endif
#if defined(SF_ENABLE_VSC1)
	for (uint8_t i=ZERO;i < VSC_MAP_MAX;i++) {
		if (pf_conf.vsc_1mode == VSC_MODE_ONCE_DOWN || pf_conf.vsc_1mode == VSC_MODE_LOOP_DOWN ) {
			pf_data.vsc_1state[i] = VSC_STATE_DOWN;
		} else {
			pf_data.vsc_1state[i] = VSC_STATE_UP;
		}
	}
#endif
}
#endif


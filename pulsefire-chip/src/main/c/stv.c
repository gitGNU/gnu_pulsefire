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

#include "stv.h"

#ifdef SF_ENABLE_STV
void STV_warn_map(uint8_t isSTVAction) {
	for (uint8_t i=ZERO;i < STV_WARN_MAP_MAX;i++) {
		uint16_t v = pf_conf.stv_warn_map[i][QMAP_VAR];
		if (v==QMAP_VAR_NONE) {
			continue;
		}
		uint16_t value = ZERO;
		if (isSTVAction) {
			value = pf_conf.stv_warn_map[i][QMAP_VALUE_A];
		} else {
			value = pf_conf.stv_warn_map[i][QMAP_VALUE_B];
		}
		if (value==0xFFFF) {
			continue;
		}
		uint16_t vIdx = pf_conf.stv_warn_map[i][QMAP_VAR_IDX];
		Vars_setValue(v,vIdx,ZERO,value);
	}
}
void STV_error_map(uint8_t isSTVAction) {
	for (uint8_t i=ZERO;i < STV_ERROR_MAP_MAX;i++) {
		uint16_t v = pf_conf.stv_error_map[i][QMAP_VAR];
		if (v==QMAP_VAR_NONE) {
			continue;
		}
		uint16_t value = ZERO;
		if (isSTVAction) {
			value = pf_conf.stv_error_map[i][QMAP_VALUE_A];
		} else {
			value = pf_conf.stv_error_map[i][QMAP_VALUE_B];
		}
		if (value==0xFFFF) {
			continue;
		}
		uint16_t vIdx = pf_conf.stv_error_map[i][QMAP_VAR_IDX];
		Vars_setValue(v,vIdx,ZERO,value);
	}
}
#endif


#ifdef SF_ENABLE_STV
void STV_loop(void) {
	if (pf_data.stv_state == STV_STATE_OKE) {
		return;
	}
	uint32_t current_time = millis();
	if (current_time < pf_data.stv_time_cnt) {
		return;
	}
	pf_data.stv_time_cnt = current_time + 1000; // check every second
	if (pf_data.stv_state == STV_STATE_WARNING_MAX || pf_data.stv_state == STV_STATE_ERROR_MAX) {
		uint16_t checkLevel = ZERO;
		uint8_t confWait = ZERO;
		if (pf_data.stv_state == STV_STATE_WARNING_MAX) {
			checkLevel = pf_conf.stv_max_map[pf_data.stv_map_idx][QMAP_VALUE_A];
			confWait = pf_conf.stv_warn_secs;
		} else {
			checkLevel = pf_conf.stv_max_map[pf_data.stv_map_idx][QMAP_VALUE_B];
			confWait = pf_conf.stv_error_secs;
		}
		if (confWait == 0xFF) {
			return; // wait forever
		}
		uint16_t curValue = Vars_getValue(pf_conf.stv_max_map[pf_data.stv_map_idx][QMAP_VAR],pf_conf.stv_max_map[pf_data.stv_map_idx][QMAP_VAR_IDX],ZERO);
		if (curValue < checkLevel) {
			pf_data.stv_wait_cnt++;
			if (pf_data.stv_wait_cnt < confWait) {
				return; // waiting until timed recovery/
			}
			pf_data.stv_wait_cnt         = ZERO;
			if (pf_data.stv_state == STV_STATE_ERROR_MAX && curValue >= pf_conf.stv_max_map[pf_data.stv_map_idx][QMAP_VALUE_A]) {
				pf_data.stv_state            = STV_STATE_WARNING_MAX;
				STV_error_map(false);
				STV_warn_map(true);
			} else {
				pf_data.stv_state            = STV_STATE_OKE;
				STV_warn_map(false);
			}
			pf_data.stv_time_cnt         = ZERO;
			pf_data.stv_map_idx          = ZERO;
		} else {
			pf_data.stv_wait_cnt = ZERO; // reset waiting time
		}
	} else if (pf_data.stv_state == STV_STATE_WARNING_MIN || pf_data.stv_state == STV_STATE_ERROR_MIN) {
		uint16_t checkLevel = ZERO;
		uint8_t confWait = ZERO;
		if (pf_data.stv_state == STV_STATE_WARNING_MIN) {
			checkLevel = pf_conf.stv_min_map[pf_data.stv_map_idx][QMAP_VALUE_A];
			confWait = pf_conf.stv_warn_secs;
		} else {
			checkLevel = pf_conf.stv_min_map[pf_data.stv_map_idx][QMAP_VALUE_B];
			confWait = pf_conf.stv_error_secs;
		}
		if (confWait == 0xFF) {
			return; // wait forever
		}
		uint16_t curValue = Vars_getValue(pf_conf.stv_min_map[pf_data.stv_map_idx][QMAP_VAR],pf_conf.stv_min_map[pf_data.stv_map_idx][QMAP_VAR_IDX],ZERO);
		if (curValue >= checkLevel) {
			pf_data.stv_wait_cnt++;
			if (pf_data.stv_wait_cnt < confWait) {
				return; // waiting until timed recovery/
			}
			pf_data.stv_wait_cnt         = ZERO;
			if (pf_data.stv_state == STV_STATE_ERROR_MIN && curValue <= pf_conf.stv_min_map[pf_data.stv_map_idx][QMAP_VALUE_A]) {
				pf_data.stv_state            = STV_STATE_WARNING_MIN;
				STV_error_map(false);
				STV_warn_map(true);
			} else {
				pf_data.stv_state            = STV_STATE_OKE;
				STV_warn_map(false);
			}
			pf_data.stv_time_cnt         = ZERO;
			pf_data.stv_map_idx          = ZERO;
		} else {
			pf_data.stv_wait_cnt = ZERO;
		}
	}
}
#endif


#ifdef SF_ENABLE_STV
uint8_t STV_is_variable_mapped(uint8_t idx,uint8_t idxA,uint8_t isMaxMap) {
	uint8_t maxMap = ZERO;
	if (isMaxMap) {
		maxMap = STV_MAX_MAP_MAX;
	} else {
		maxMap = STV_MIN_MAP_MAX;
	}
	for (uint8_t i=ZERO;i < maxMap;i++) {
		uint16_t v = ZERO;
		if (isMaxMap) {
			v = pf_conf.stv_max_map[i][QMAP_VAR];
		} else {
			v = pf_conf.stv_min_map[i][QMAP_VAR];
		}
		if (v==QMAP_VAR_NONE) {
			continue;
		}
		if (v != idx) {
			continue;
		}
		if (Vars_isIndexA(idx)==false) {
			return i;
		}
		uint16_t vi = ZERO;
		if (isMaxMap) {
			vi = pf_conf.stv_max_map[i][QMAP_VAR_IDX];
		} else {
			vi = pf_conf.stv_min_map[i][QMAP_VAR_IDX];
		}
		if (vi == QMAP_VAR_IDX_ALL) {
			return i;
		}
		if (vi == idxA) {
			return i;
		}
	}
	return QMAP_VAR_IDX_ALL;
}
#endif

#ifdef SF_ENABLE_STV
void STV_vars_max(uint16_t value,uint8_t stvIdxMax) {
	uint16_t warningLevel = pf_conf.stv_max_map[stvIdxMax][QMAP_VALUE_A];
	uint16_t errorLevel   = pf_conf.stv_max_map[stvIdxMax][QMAP_VALUE_B];
	if (errorLevel > ZERO && value >= errorLevel && pf_data.stv_state != STV_STATE_ERROR_MAX && pf_data.stv_state != STV_STATE_ERROR_MIN) {
		pf_data.stv_state            = STV_STATE_ERROR_MAX;
		pf_data.stv_time_cnt         = millis() + (pf_conf.stv_warn_secs*1000);
		pf_data.stv_map_idx          = stvIdxMax;
		STV_error_map(true);
	} else if (warningLevel > ZERO && value >= warningLevel && pf_data.stv_state == STV_STATE_OKE) {
		pf_data.stv_state            = STV_STATE_WARNING_MAX;
		pf_data.stv_time_cnt         = millis() + (pf_conf.stv_warn_secs*1000);
		pf_data.stv_map_idx          = stvIdxMax;
		STV_warn_map(true);
	}
}
#endif

#ifdef SF_ENABLE_STV
void STV_vars_min(uint16_t value,uint8_t stvIdxMin) {
	uint16_t warningLevel = pf_conf.stv_min_map[stvIdxMin][QMAP_VALUE_A];
	uint16_t errorLevel   = pf_conf.stv_min_map[stvIdxMin][QMAP_VALUE_B];
	if (errorLevel > ZERO && value <= errorLevel && pf_data.stv_state != STV_STATE_ERROR_MIN && pf_data.stv_state != STV_STATE_ERROR_MAX) {
		pf_data.stv_state            = STV_STATE_ERROR_MIN;
		pf_data.stv_time_cnt         = millis() + (pf_conf.stv_warn_secs*1000);
		pf_data.stv_map_idx          = stvIdxMin;
		STV_error_map(true);
	} else if (warningLevel > ZERO && value <= warningLevel && pf_data.stv_state == STV_STATE_OKE) {
		pf_data.stv_state            = STV_STATE_WARNING_MIN;
		pf_data.stv_time_cnt         = millis() + (pf_conf.stv_warn_secs*1000);
		pf_data.stv_map_idx          = stvIdxMin;
		STV_warn_map(true);
	}
}
#endif


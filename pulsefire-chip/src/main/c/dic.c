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


#include "dic.h"

// read out digital values.
void Dic_loop(void) {
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
		Vars_setValue(pf_data.idx_dic_value,ZERO,ZERO,dic_value_new);

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

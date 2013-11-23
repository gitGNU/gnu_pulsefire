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


#include "debug.h"

#ifdef SF_ENABLE_DEBUG_HTX
void Debug_htx_c(char c) {
	Chip_out_debug_htx(c);
}

void Debug_htx_str(char* dstring) {
	while(*dstring != 0) {
		Chip_out_debug_htx(*dstring);
		dstring++;
	}
}

void Debug_htx_hex8(uint8_t argu) {
	uint8_t hn = argu >> 4;
	uint8_t ln = argu & 0x0F;
	if (hn<10) {
		Chip_out_debug_htx('0'+hn);
	} else {
		Chip_out_debug_htx('A'+(hn-10));
	}
	if (ln<10) {
		Chip_out_debug_htx('0'+ln);
	} else {
		Chip_out_debug_htx('A'+(ln-10));
	}
}

void Debug_htx_hex16(uint16_t argu) {
	Debug_htx_hex8(argu >> 8);
	Debug_htx_hex8(argu & 0xFF);
}

#endif

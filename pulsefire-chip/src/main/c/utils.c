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

#include "utils.h"

// Uncopy from program/flash memory to sram
volatile char* UNPSTR(const char* dstring) {
	for (uint8_t i=ZERO;i < UNPSTR_BUFF_SIZE;i++) {
		pf_data.unpstr_buff[i]='\0'; // clean buffer
	}
	int index = ZERO;
	while (Chip_pgm_readByte(dstring) != 0x00) {
		uint8_t c = Chip_pgm_readByte(dstring++);
		pf_data.unpstr_buff[index]=c;
		index++;
	}
	return pf_data.unpstr_buff;
}

// Fill pstr_buff from pointer
volatile char* UNPSTRA(const CHIP_PTR_TYPE* argu) {
	// rm readByte use word which auto size ptr
	uint8_t msb = Chip_pgm_readByte((const char*)argu+1);
	uint8_t lsb = Chip_pgm_readByte((const char*)argu);
	const char*p = (const char*) ((msb*256)+lsb);
	return UNPSTR(p);
}

// Reverse a string
void reverse_str(volatile char s[]) {
	uint8_t nn = ZERO;
	volatile char* ss = s;
	while (*ss++) { nn++; }
	int c, i, j;
	for (i = 0, j = nn-1; i < j; i++, j--){
		c = s[i];
		s[i] = s[j];
		s[j] = c;
	}
}

// Print uint16_t to ascii decimal
void u16toa(uint16_t n, volatile char s[]) { 
	uint16_t i=0;
	do {   // generate digits in reverse order
		s[i++] = n % 10 + '0'; // get next digit
	} while ((n /= 10) > 0); // delete it
	s[i] = '\0'; // add null terminator for string
	reverse_str(s);
}

void u32toa(uint32_t n, volatile char s[]) { 
	uint32_t i=0;
	do {
		s[i++] = n % 10 + '0';
	} while ((n /= 10) > 0);
	s[i] = '\0';
	reverse_str(s);
}


// Parser ascii to uint16_t
uint16_t atou16(volatile char* s) {
	uint8_t nn = ZERO;
	volatile char* ss = s;
	while (*ss++) { nn++; }
	uint16_t num = ZERO;
	for (uint8_t i=ZERO;i <= nn;i++) {
		if (s[i] >= '0' && s[i] <= '9') {
			num = num * 10 + s[i] - '0';
		} else 	{
			break;
		}
	}
	return num;
}

uint32_t atou32(volatile char* s) {
	uint8_t nn = ZERO;
	volatile char* ss = s;
	while (*ss++) { nn++; }
	uint32_t num = ZERO;
	for (uint8_t i=ZERO;i <= nn;i++) {
		if (s[i] >= '0' && s[i] <= '9') {
			num = num * 10 + s[i] - '0';
		} else 	{
			break;
		}
	}
	return num;
}


uint32_t htou32(volatile char* s) {
	uint8_t nn = ZERO;
	volatile char* ss = s;
	while (*ss++) { nn++; }
	uint32_t num = ZERO;
	for (uint8_t i=ZERO;i <= nn;i++) {
		uint8_t c = s[i];
		if (c >= '0' && c <= '9') {
			num = num*16 + (c-'0');
		} else if (c >= 'A' && c <= 'F')	{
			num = num*16 + (c-'A'+10);
		} else {
			break;
		}
	}
	return num;
}

// reserse the bits in the number limited by num_bits
uint16_t reverse_bits(uint16_t num,uint16_t num_bits) {
	uint16_t reverse_num = ZERO;
	for (uint8_t i=ZERO;i < num_bits;i++) {
		if ((num & (ONE << i))) {
			reverse_num |= ONE << ((num_bits - ONE) - i);
		}
	}
	return reverse_num;
}

// map value from range to new range
uint16_t map_value(uint16_t x, uint16_t in_min, uint16_t in_max, uint16_t out_min, uint16_t out_max) {
	return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
}

volatile char* strtok(volatile char *s, const char *delim) {
	const char *spanp;
	int c, sc;
	volatile char *tok;
	volatile static char *last;
	if (s == NULL && (s = last) == NULL) {
		return (NULL);
	}
cont:
	c = *s++;
	for (spanp = delim; (sc = *spanp++) != 0;) {
		if (c == sc) {
			goto cont;
		}
	}
	if (c == 0) {			/* no non-delimiter characters */
		last = NULL;
		return (NULL);
	}
	tok = s - 1;
	for (;;) {
		c = *s++;
		spanp = delim;
		do {
			if ((sc = *spanp++) == c) {
				if (c == 0) {
					s = NULL;
				} else {
					s[-1] = 0;
				}
				last = s;
				return (tok);
			}
		} while (sc != 0);
	}
	/* NOTREACHED */
}

int strcmp(volatile char *s1,volatile char *s2) {
	while (*s1 && *s2 && *s1 == *s2) {
		s1++;
		s2++;
	}
	if (*s1 == *s2) {
		return 0;
	}
	if (*s1 < *s2) {
		return -1;
	}
	return 1;
}






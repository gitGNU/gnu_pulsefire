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
//Short desc   : Automatic PulseFire Sequence Generator.
//First created: 26-Apr-2011
//First Author : Willem Cazander
//License-Type : BSD 2-Clause (see licence.txt)
//IO-Hardware  : see chip_avr.c and chip_avr_mega.c
//USB-Serial   : 115200b + "Newline" on enter/return
//Website      : http://www.nongnu.org/pulsefire/

#include "vars.h"
#include "serial.h"
#include "sys.h"
#include "vsc.h"
#include "ptc.h"
#include "ptt.h"
#include "stv.h"
#include "lpm.h"
#include "lcd.h"
#include "mal.h"
#include "adc.h"
#include "dic.h"
#include "freq.h"
#include "utils.h"
#include "chip.h"

// Main function
int main(void) {

	// Setup
	Chip_setup_serial(); // Setup serial first so we can debug.
	Serial_setup();
	Vars_setup(); // uses serial in debug mode
	Chip_setup(); // uses vars

#ifdef SF_ENABLE_DEBUG
	Serial_printCharP(PSTR("setup oke."));
	Serial_println();
#endif
	Chip_sei(); // enable interrupts after init
#ifdef SF_ENABLE_DEBUG
	Serial_printCharP(PSTR("sei oke."));
	Serial_println();
#endif
#ifdef SF_ENABLE_LCD
	Lcd_setup(); //needs sei() in spi mode
#endif
#ifdef SF_ENABLE_LPM
	LPM_setup();
#endif
#if defined(SF_ENABLE_VSC0) || defined(SF_ENABLE_VSC1)
	Vsc_setup();
#endif
#ifdef SF_ENABLE_PWM
	PWM_send_output(PULSE_DATA_OFF); // send off state to output
#endif

	for(;;) {
		// High speed loop
		pf_data.sys_loop0_cnt++;
		Vars_loop();
		Serial_loop();
#ifdef SF_ENABLE_ADC
		Adc_loop();
#endif

		uint16_t current_time = (uint16_t)Chip_centi_secs();
		if (current_time < pf_data.sys_loop1_cnt) {
			continue;
		}
		pf_data.sys_loop1_cnt = current_time + 5;
		uint16_t idx = pf_data.sys_loop1_cnt_idx;
		uint8_t idxOne = idx & ONE;
		pf_data.sys_loop1_cnt_idx++;
		if (idx > 20) {
			pf_data.sys_loop1_cnt_idx = ZERO;
		}
		// Main 20 hz loop
		Chip_out_doc();
		Dic_loop();
#ifdef SF_ENABLE_VSC0
		Vsc_loop0();
#endif
#ifdef SF_ENABLE_VSC1
		Vsc_loop1();
#endif
#ifdef SF_ENABLE_PTC0
		Ptc_loop0();
#endif
#ifdef SF_ENABLE_PTC1
		Ptc_loop1();
#endif

		// 10 hz loop
		if (idxOne == ZERO) {
#ifdef SF_ENABLE_MAL
			Mal_loop();
#endif
		}
		if (idxOne == ONE) {
#ifdef SF_ENABLE_PTT
			Ptt_loop();
#endif
		}

		// 5 Hz loop
		if ((idx & (ONE+ONE+ONE)) == ZERO) {
#ifdef SF_ENABLE_LCD
			Lcd_loop();
#endif
		}

		// 1Hz loop
#ifdef SF_ENABLE_STV
		if (idx==2) {
			Stv_loop();
		}
#endif
		if (idx==12) {
			Chip_loop();
			Sys_loop();
		}
	}
}

// EOF


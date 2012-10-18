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
//Last modified: 22-Nov-2011
//Last version : 1.0-Beta
//First Author : Willem Cazander
//License-Type : BSD 2-Clause (licence.txt and http://www.opensource.org/licenses/bsd-license.php)
//IO-Hardware  : see IO_DEF_* or IO_EXT_* #defines in chip.h
//USB-Serial   : 115200b + "Newline" on enter/return
//Website      : http://www.nongnu.org/pulsefire/

#include "vars.h"
#include "serial.h"
#include "ptc.h"
#include "stv.h"
#include "lpm.h"
#include "lcd.h"
#include "mal.h"
#include "input.h"
#include "freq.h"
#include "utils.h"
#include "chip.h"

// Main function
int main(void) {

	// Setup
	Vars_setup();
	Chip_setup();
	Serial_setup();
#ifdef SF_ENABLE_LCD
	lcd_setup();
#endif
#ifdef SF_ENABLE_LPM
	LPM_setup();
#endif
 
	Chip_sei(); // enable interrupts after init
	for(;;) {
		Chip_loop();
		Vars_loop();
		Serial_loop();
#ifdef SF_ENABLE_LCD
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
#ifdef SF_ENABLE_PWM
		Freq_loop();
#endif
#ifdef SF_ENABLE_MAL
		Mal_loop();
#endif
	}
}

// EOF


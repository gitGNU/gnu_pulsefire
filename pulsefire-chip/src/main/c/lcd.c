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


#include "lcd.h"

#ifdef SF_ENABLE_LCD

void lcd_write(uint8_t c,uint8_t cmd) {
	lcd_writeMux(c,cmd,ZERO);
}

void lcd_writeMux(uint8_t c,uint8_t cmd,uint8_t mux) {  
	uint8_t hn = c >> 4;
	uint8_t ln = c & 0x0F;

	//Send high nibble
#ifdef SF_ENABLE_EXT_LCD
	uint8_t lcd_out = hn + ((mux & 3) << 6);
	if (cmd==LCD_SEND_DATA) {
		lcd_out += 16; // make RS high
	}
	lcd_out += 32; // make E high
	lcd_write_s2p(lcd_out);
	lcd_out -= 32; // Make E low
	lcd_write_s2p(lcd_out);
#else
	if (cmd==LCD_SEND_DATA) {
		digitalWrite(IO_DEF_IO_PORT,IO_DEF_LCD_RS_PIN,ONE); // write data
	} else {
		digitalWrite(IO_DEF_IO_PORT,IO_DEF_LCD_RS_PIN,ZERO);  // write command
	}
	volatile uint8_t *port = IO_DEF_ADC_PORT;
	*port=(*port & 0xF0)|hn;
	//*port=(IO_DEF_ADC_PORT & 0xF0)|hn;
	digitalWrite(IO_DEF_IO_PORT,IO_DEF_LCD_E_PIN,ONE);
	asm volatile ("nop");
	asm volatile ("nop");
	digitalWrite(IO_DEF_IO_PORT,IO_DEF_LCD_E_PIN,ZERO);  //Now data lines are stable pull E low for transmission
#endif

	if (cmd!=LCD_SEND_INIT) {
		asm volatile ("nop");
		asm volatile ("nop");
#ifdef SF_ENABLE_EXT_LCD
		uint8_t lcd_out = ln + ((mux & 3) << 6);
		if (cmd==LCD_SEND_DATA) {
			lcd_out += 16; // make RS high
		}
		lcd_out += 32; // make E high
		lcd_write_s2p(lcd_out);
		lcd_out -= 32; // Make E low
		lcd_write_s2p(lcd_out);
#else
		volatile uint8_t *port = IO_DEF_ADC_PORT;
		*port=(*port & 0xF0)|ln;
		digitalWrite(IO_DEF_IO_PORT,IO_DEF_LCD_E_PIN,ONE);
		asm volatile ("nop");
		asm volatile ("nop");
		digitalWrite(IO_DEF_IO_PORT,IO_DEF_LCD_E_PIN,ZERO);
#endif
	}
	if (cmd==LCD_SEND_DATA) {
		Chip_delayU(30);
	} else {
		Chip_delay(5); // wait for busy flag
	}
}

#ifdef SF_ENABLE_EXT_LCD
void lcd_write_s2p(uint8_t value) {
#ifdef SF_ENABLE_DOC
#if defined(SF_ENABLE_AVR)
	if (pf_conf.avr_pin2_map == PIN2_DOC2_OUT) {
		digitalWrite(IO_DEF_IO_PORT,IO_DEF_PIN2_PIN,pf_data.doc_port[2] > ZERO);
	}
	if (pf_conf.avr_pin3_map == PIN3_DOC3_OUT) {
		digitalWrite(IO_DEF_IO_PORT,IO_DEF_PIN3_PIN,pf_data.doc_port[3] > ZERO);
	}
	if (pf_conf.avr_pin4_map == PIN4_DOC4_OUT) {
		digitalWrite(IO_DEF_IO_PORT,IO_DEF_PIN4_PIN,pf_data.doc_port[4] > ZERO);
	}
	if (pf_conf.avr_pin5_map == PIN5_DOC5_OUT) {
		digitalWrite(IO_DEF_IO_PORT,IO_DEF_PIN5_PIN,pf_data.doc_port[5] > ZERO);
	}
	if (pf_conf.avr_pin2_map == PIN2_DOC8_OUT) {
		digitalWrite(IO_DEF_IO_PORT,IO_DEF_PIN2_PIN,pf_data.doc_port[8] > ZERO);
	}
	if (pf_conf.avr_pin3_map == PIN3_DOC9_OUT) {
		digitalWrite(IO_DEF_IO_PORT,IO_DEF_PIN3_PIN,pf_data.doc_port[9] > ZERO);
	}
	if (pf_conf.avr_pin4_map == PIN4_DOC10_OUT) {
		digitalWrite(IO_DEF_IO_PORT,IO_DEF_PIN4_PIN,pf_data.doc_port[10] > ZERO);
	}
	if (pf_conf.avr_pin5_map == PIN5_DOC11_OUT) {
		digitalWrite(IO_DEF_IO_PORT,IO_DEF_PIN5_PIN,pf_data.doc_port[11] > ZERO);
	}
#elif defined(SF_ENABLE_AVR_MEGA)
	if (pf_conf.avr_pin5_map == PIN5_DOC11_OUT) {
		digitalWrite(IO_DEF_IO_PORT,IO_DEF_PIN5_PIN,pf_data.doc_port[11] > ZERO);
	}
#else
	#error "Don't know how to run on your MCU_TYPE."
#endif
#endif
	digitalWrite(IO_DEF_OUT_PORT,IO_EXT_S2P_E_PIN,LOW);
#ifdef SF_ENABLE_EXT_LCD_DOC
	uint16_t doc_out = ZERO;
#ifdef SF_ENABLE_DOC
	for (uint8_t t=ZERO;t<DOC_PORT_NUM_MAX;t++) {
		if (pf_data.doc_port[t] > ZERO) {
			doc_out += (ONE << t);
		}
	}
#endif
#ifdef SF_ENABLE_EXT_LCD_DOC_16BIT
	shiftOut(IO_DEF_OUT_PORT,IO_EXT_S2P_DATA_PIN,IO_EXT_S2P_CLK_PIN,(doc_out >> 8)); // send data to last chip first
	shiftOut(IO_DEF_OUT_PORT,IO_EXT_S2P_DATA_PIN,IO_EXT_S2P_CLK_PIN,doc_out);
	shiftOut(IO_DEF_OUT_PORT,IO_EXT_S2P_DATA_PIN,IO_EXT_S2P_CLK_PIN,value);
#else
	shiftOut(IO_DEF_OUT_PORT,IO_EXT_S2P_DATA_PIN,IO_EXT_S2P_CLK_PIN,doc_out);
	shiftOut(IO_DEF_OUT_PORT,IO_EXT_S2P_DATA_PIN,IO_EXT_S2P_CLK_PIN,value);
#endif
#else
	shiftOut(IO_DEF_OUT_PORT,IO_EXT_S2P_DATA_PIN,IO_EXT_S2P_CLK_PIN,value);
#endif
	digitalWrite(IO_DEF_OUT_PORT,IO_EXT_S2P_E_PIN,HIGH);
}
#endif

void lcd_init(void) {
	Chip_delay(30);                 // Wait for more than 15 ms after VCC rises to 4.5 V
	lcd_write(0x30,LCD_SEND_INIT);  // Send Command 0x30
	Chip_delay(8);                  // Wait for more than 4.1 ms
	lcd_write(0x30,LCD_SEND_INIT);  // Send Command 0x30
	Chip_delayU(200);               // Wait for more than 100 us
	lcd_write(0x30,LCD_SEND_INIT);  // Send Command 0x30
	lcd_write(0x20,LCD_SEND_INIT);  // Function set: Set interface to be 4 bits long (only 1 cycle write).
	lcd_write(0x2C,LCD_SEND_CMD);   // Function set: DL=0;Interface is 4 bits, N=1; 2 Lines, F=0; 5x8 dots font)
	lcd_write(0x06,LCD_SEND_CMD);   // Entry Mode Set: I/D=1; Increament, S=0; No shift  == 0x06
	lcd_write(0x0F-3,LCD_SEND_CMD); // Display on,No cursor,No blicking
	lcd_clear();                    // test lcd
	lcd_home();
}

void lcd_home(void) {
	lcd_write(0x02,LCD_SEND_CMD);
}

void lcd_clear(void) {
	lcd_write(0x01,LCD_SEND_CMD);
}

void lcd_cursor(uint8_t col, uint8_t row) {
	if (row < 2) {
		lcd_write( ((row*0x40)+col) | 0x80,LCD_SEND_CMD);
	} else if (row == 2) {
		lcd_write(( 0x14+col) | 0x80,LCD_SEND_CMD); // 4 line lcd are really folded 2 line lcds :)
	} else if (row == 3) {
		lcd_write(( 0x54+col) | 0x80,LCD_SEND_CMD);
	}
}

void lcd_printDot(void) {
	lcd_write('.',LCD_SEND_DATA);
}

void lcd_printSpace(void) {
	lcd_write(' ',LCD_SEND_DATA);
}

void lcd_printChar(char* dstring) {
	while(*dstring != ZERO) {
		if (*dstring == '\n') {
			lcd_write(0xC0,LCD_SEND_CMD); // next line
		} else {
			lcd_write((uint8_t)*dstring,LCD_SEND_DATA);
		}
		dstring++;
	}
}

void lcd_printCharP(const char* argu) {
	lcd_printChar(UNPSTR(argu));
}

void lcd_printByte(uint8_t value) {
	lcd_print((uint16_t)value);
}

void lcd_printByteNum(uint8_t value,uint8_t numSize) {
	lcd_printNum((uint16_t)value,numSize);
}

void lcd_print(uint16_t value) {
	lcd_printNum(value,ZERO);
}

void lcd_printNum(uint16_t value,uint8_t numSize) {
	u16toa(value,pf_prog.lcd_buff);
	uint8_t nn = ZERO;
	char* ss = pf_prog.lcd_buff;
	while (*ss++) { nn++; }
	lcd_printChar(pf_prog.lcd_buff);
	if (numSize > ZERO && (nn) < numSize) {
		uint8_t i=ZERO;
		for (i=ZERO;i<(numSize-nn);i++) {
			lcd_printSpace();
		}
	}
}

void lcd_setup(void) {
	lcd_init();
	lcd_printCharP(pmPulseFire);
	lcd_printSpace();
	lcd_print((uint16_t)PULSE_FIRE_VERSION/10 % 10);
	lcd_printDot();
	lcd_print((uint16_t)PULSE_FIRE_VERSION % 10);
	lcd_cursor(ZERO,ONE);
	lcd_printCharP(pmChipNameStr);
	if (pf_conf.lcd_size == LCD_SIZE_4x20) {
		lcd_cursor(ZERO,2);
		lcd_printCharP(pmChipNameIdStr);
		lcd_cursor(ZERO,3);
		lcd_printCharP(pmChipCPUTypeAVR);
		lcd_printSpace();
		lcd_print(sizeof(pf_conf_struct));
	}

	Chip_delay(LCD_INIT_MSG_TIME);
	uint8_t i=ZERO;
	uint8_t lcd_size_col = 16;
	if (pf_conf.lcd_size == LCD_SIZE_4x20) {
		lcd_size_col = 20;
	}
	if (pf_conf.lcd_size == LCD_SIZE_2x20) {
		lcd_size_col = 20;
	}

	for (i=ZERO;i <= lcd_size_col;i++) {
		lcd_cursor(lcd_size_col-i,0);  lcd_printDot();
		lcd_cursor(i,1);               lcd_printDot();
		if (pf_conf.lcd_size == LCD_SIZE_4x20) {
			lcd_cursor(lcd_size_col-i,2);lcd_printDot();
			lcd_cursor(i,3);             lcd_printDot();
		}
		Chip_delay(LCD_TEST_DOT_TIME);
	}
	for (i=ONE;i < lcd_size_col;i++) {
		lcd_cursor(lcd_size_col-i,0);  lcd_printSpace();
		lcd_cursor(i,1);               lcd_printSpace();
		if (pf_conf.lcd_size == LCD_SIZE_4x20) {
			lcd_cursor(lcd_size_col-i,2);lcd_printSpace();
			lcd_cursor(i,3);             lcd_printSpace();
		}
		Chip_delay(LCD_TEST_DOT_TIME);
	}
	Chip_delay(LCD_TEST_DOT_TIME*5);
}

void lcd_loop(void) {
	uint32_t current_time = millis();
	if (current_time < pf_data.lcd_time_cnt) {
		return; // wait until refresh
	}
	pf_data.lcd_time_cnt = current_time + LCD_REFRESH_TIME;
	if (pf_prog.lcd_menu_state != LCD_MENU_STATE_OFF) {
		pf_data.lcd_redraw = ZERO;
		return; // we are off in menu mode.
	}
#ifdef SF_ENABLE_LPM
	if (pf_data.lpm_state != LPM_IDLE) {
		pf_data.lcd_redraw = ZERO;
		return; // we are off in lpm
	}
#endif

#ifdef SF_ENABLE_STV
	if (pf_prog.stv_state != STV_STATE_OKE ) {
		lcd_clear();
		if (pf_prog.stv_state == STV_STATE_WARNING_MAX || pf_prog.stv_state == STV_STATE_WARNING_MIN) {
			lcd_printCharP(pmLcdSTVWarning);
		} else {
			lcd_printCharP(pmLcdSTVError);
		}
		if (pf_prog.stv_state == STV_STATE_WARNING_MAX || pf_prog.stv_state == STV_STATE_ERROR_MAX) {
			lcd_printCharP(pmLcdSTVMax);
		} else {
			lcd_printCharP(pmLcdSTVMin);
		}
		if (pf_conf.lcd_size != LCD_SIZE_4x20 ) {
			lcd_printCharP(pmLcdValue);
			lcd_print(Vars_getValue(pf_prog.stv_map_idx,ZERO,ZERO));
		}
		lcd_cursor(0,1);lcd_printCharP(pmLcdSelectedOption);
		uint8_t varIdx = ZERO;
		if (pf_prog.stv_state == STV_STATE_WARNING_MAX || pf_prog.stv_state == STV_STATE_ERROR_MAX) {
			varIdx = pf_conf.stv_max_map[pf_prog.stv_map_idx][QMAP_VAR];
		} else {
			varIdx = pf_conf.stv_max_map[pf_prog.stv_map_idx][QMAP_VAR];
		}
		lcd_printChar(Vars_getName(varIdx));
		if (pf_conf.lcd_size == LCD_SIZE_4x20) {
			lcd_cursor(0,2);
			lcd_printCharP(pmLcdValue);
			lcd_print(Vars_getValue(varIdx,ZERO,ZERO));
		}
		pf_data.lcd_redraw = ZERO;
		return;
    	}
#endif

	if (pf_data.lcd_redraw == ZERO) {
		lcd_clear();
		pf_data.lcd_redraw = ONE;
	}
#ifdef SF_ENABLE_PWM
#ifdef SF_ENABLE_FRQ
	lcd_printChar("HZ: ");
	uint32_t freq = calc_pwm_freq(ZERO);
	uint32_t freqHigh = freq/FREQ_MUL;
	uint32_t freqLow  = freq%FREQ_MUL;
	lcd_print((uint16_t)freqHigh);
	lcd_printDot();
	lcd_print((uint16_t)freqLow);
	lcd_printSpace();
	lcd_printSpace(); // cleans if freq goes fast from big number to small number
	lcd_printSpace(); // todo make better
#else
	lcd_printCharP(pmPulseFire); // without HZ there is space for our name here.
#endif
#else
	lcd_printCharP(pmPulseFire); // without HZ there is space for our name here.
#endif

#ifdef SF_ENABLE_PWM

	uint8_t col1 = 6;
	uint8_t col2 = 13;
	if (pf_conf.lcd_size == LCD_SIZE_2x16) {
		col1 = 5;
		col2 = 11;
	}

	lcd_cursor(col2,ZERO);
#ifdef SF_ENABLE_SWC
	if (pf_data.swc_secs_cnt > ZERO) {
		lcd_printCharP(pmLcdSoftStart);
	} else {
#endif
		if (pf_conf.lcd_size == LCD_SIZE_2x16) {
			lcd_printChar("D:");
		} else {
			lcd_printChar("DUT:");
		}
		lcd_printNum(pf_conf.pwm_duty,3);
#ifdef SF_ENABLE_SWC
	}
#endif

	lcd_cursor(ZERO,ONE);
	lcd_printChar("S:");
	lcd_printNum(pf_data.pulse_step,2);
	lcd_cursor(col1,ONE);
	lcd_printChar("DIR:");
	if (pf_conf.pulse_dir == PULSE_DIR_LRRL) {
		lcd_print(pf_data.pulse_dir_cnt);
	} else {
		lcd_print(pf_conf.pulse_dir);
	}
	lcd_cursor(col2,ONE);
	lcd_printChar("MOD:");
	lcd_print(pf_conf.pulse_mode);

	// work only on 4 line display
	if (pf_conf.lcd_size == LCD_SIZE_4x20) {
		lcd_cursor(0,2);
		lcd_printChar("O:");
		lcd_printNum(pf_conf.pulse_steps,2);
		lcd_cursor(col1,2);
		lcd_printChar("ENA:");
		lcd_print(pf_conf.pulse_enable);
		lcd_cursor(col2,2);
		lcd_printChar("TRG:");
		lcd_print(pf_conf.pulse_trig);
		lcd_cursor(0,3);
		lcd_printChar("P:");
		lcd_print(pf_data.pwm_state);
#ifdef SF_ENABLE_PTC
		lcd_cursor(col1,3);
		lcd_printChar("PTC:");
		lcd_printNum(pf_data.ptc_0map_idx,3);
#endif
		lcd_cursor(col2,3);
		lcd_printChar("CNT:");
		lcd_printNum(pf_data.pwm_loop_cnt,3);
	}
	#endif
}

#endif

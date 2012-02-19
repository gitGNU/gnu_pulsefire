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

#ifdef SF_ENABLE_GLCD
uint8_t glcd_col = ZERO;
uint8_t glcd_row = ZERO;
static uint8_t FONT_5x7[]  = {
		0x00, 0x00, 0x00, 0x00, 0x00,// (space)
		0x00, 0x00, 0x5F, 0x00, 0x00,// !
		0x00, 0x07, 0x00, 0x07, 0x00,// "
		0x14, 0x7F, 0x14, 0x7F, 0x14,// #
		0x24, 0x2A, 0x7F, 0x2A, 0x12,// $
		0x23, 0x13, 0x08, 0x64, 0x62,// %
		0x36, 0x49, 0x55, 0x22, 0x50,// &
		0x00, 0x05, 0x03, 0x00, 0x00,// '
		0x00, 0x1C, 0x22, 0x41, 0x00,// (
		0x00, 0x41, 0x22, 0x1C, 0x00,// )
		0x08, 0x2A, 0x1C, 0x2A, 0x08,// *
		0x08, 0x08, 0x3E, 0x08, 0x08,// +
		0x00, 0x50, 0x30, 0x00, 0x00,// ,
		0x08, 0x08, 0x08, 0x08, 0x08,// -
		0x00, 0x60, 0x60, 0x00, 0x00,// .
		0x20, 0x10, 0x08, 0x04, 0x02,// /
		0x3E, 0x51, 0x49, 0x45, 0x3E,// 0
		0x00, 0x42, 0x7F, 0x40, 0x00,// 1
		0x42, 0x61, 0x51, 0x49, 0x46,// 2
		0x21, 0x41, 0x45, 0x4B, 0x31,// 3
		0x18, 0x14, 0x12, 0x7F, 0x10,// 4
		0x27, 0x45, 0x45, 0x45, 0x39,// 5
		0x3C, 0x4A, 0x49, 0x49, 0x30,// 6
		0x01, 0x71, 0x09, 0x05, 0x03,// 7
		0x36, 0x49, 0x49, 0x49, 0x36,// 8
		0x06, 0x49, 0x49, 0x29, 0x1E,// 9
		0x00, 0x36, 0x36, 0x00, 0x00,// :
		0x00, 0x56, 0x36, 0x00, 0x00,// ;
		0x00, 0x08, 0x14, 0x22, 0x41,// <
		0x14, 0x14, 0x14, 0x14, 0x14,// =
		0x41, 0x22, 0x14, 0x08, 0x00,// >
		0x02, 0x01, 0x51, 0x09, 0x06,// ?
		0x32, 0x49, 0x79, 0x41, 0x3E,// @
		0x7E, 0x11, 0x11, 0x11, 0x7E,// A
		0x7F, 0x49, 0x49, 0x49, 0x36,// B
		0x3E, 0x41, 0x41, 0x41, 0x22,// C
		0x7F, 0x41, 0x41, 0x22, 0x1C,// D
		0x7F, 0x49, 0x49, 0x49, 0x41,// E
		0x7F, 0x09, 0x09, 0x01, 0x01,// F
		0x3E, 0x41, 0x41, 0x51, 0x32,// G
		0x7F, 0x08, 0x08, 0x08, 0x7F,// H
		0x00, 0x41, 0x7F, 0x41, 0x00,// I
		0x20, 0x40, 0x41, 0x3F, 0x01,// J
		0x7F, 0x08, 0x14, 0x22, 0x41,// K
		0x7F, 0x40, 0x40, 0x40, 0x40,// L
		0x7F, 0x02, 0x04, 0x02, 0x7F,// M
		0x7F, 0x04, 0x08, 0x10, 0x7F,// N
		0x3E, 0x41, 0x41, 0x41, 0x3E,// O
		0x7F, 0x09, 0x09, 0x09, 0x06,// P
		0x3E, 0x41, 0x51, 0x21, 0x5E,// Q
		0x7F, 0x09, 0x19, 0x29, 0x46,// R
		0x46, 0x49, 0x49, 0x49, 0x31,// S
		0x01, 0x01, 0x7F, 0x01, 0x01,// T
		0x3F, 0x40, 0x40, 0x40, 0x3F,// U
		0x1F, 0x20, 0x40, 0x20, 0x1F,// V
		0x7F, 0x20, 0x18, 0x20, 0x7F,// W
		0x63, 0x14, 0x08, 0x14, 0x63,// X
		0x03, 0x04, 0x78, 0x04, 0x03,// Y
		0x61, 0x51, 0x49, 0x45, 0x43,// Z
		0x00, 0x00, 0x7F, 0x41, 0x41,// [
		0x02, 0x04, 0x08, 0x10, 0x20,// "\"
		0x41, 0x41, 0x7F, 0x00, 0x00,// ]
		0x04, 0x02, 0x01, 0x02, 0x04,// ^
		0x40, 0x40, 0x40, 0x40, 0x40,// _
		0x00, 0x01, 0x02, 0x04, 0x00,// `
		0x20, 0x54, 0x54, 0x54, 0x78,// a
		0x7F, 0x48, 0x44, 0x44, 0x38,// b
		0x38, 0x44, 0x44, 0x44, 0x20,// c
		0x38, 0x44, 0x44, 0x48, 0x7F,// d
		0x38, 0x54, 0x54, 0x54, 0x18,// e
		0x08, 0x7E, 0x09, 0x01, 0x02,// f
		0x08, 0x14, 0x54, 0x54, 0x3C,// g
		0x7F, 0x08, 0x04, 0x04, 0x78,// h
		0x00, 0x44, 0x7D, 0x40, 0x00,// i
		0x20, 0x40, 0x44, 0x3D, 0x00,// j
		0x00, 0x7F, 0x10, 0x28, 0x44,// k
		0x00, 0x41, 0x7F, 0x40, 0x00,// l
		0x7C, 0x04, 0x18, 0x04, 0x78,// m
		0x7C, 0x08, 0x04, 0x04, 0x78,// n
		0x38, 0x44, 0x44, 0x44, 0x38,// o
		0x7C, 0x14, 0x14, 0x14, 0x08,// p
		0x08, 0x14, 0x14, 0x18, 0x7C,// q
		0x7C, 0x08, 0x04, 0x04, 0x08,// r
		0x48, 0x54, 0x54, 0x54, 0x20,// s
		0x04, 0x3F, 0x44, 0x40, 0x20,// t
		0x3C, 0x40, 0x40, 0x20, 0x7C,// u
		0x1C, 0x20, 0x40, 0x20, 0x1C,// v
		0x3C, 0x40, 0x30, 0x40, 0x3C,// w
		0x44, 0x28, 0x10, 0x28, 0x44,// x
		0x0C, 0x50, 0x50, 0x50, 0x3C,// y
		0x44, 0x64, 0x54, 0x4C, 0x44,// z
		0x00, 0x08, 0x36, 0x41, 0x00,// {
		0x00, 0x00, 0x7F, 0x00, 0x00,// |
		0x00, 0x41, 0x36, 0x08, 0x00,// }
		0x08, 0x08, 0x2A, 0x1C, 0x08,// ->
		0x08, 0x1C, 0x2A, 0x08, 0x08 // <-
};
#endif

#ifdef SF_ENABLE_GLCD
void lcd_glcd_data(uint8_t page,uint8_t col,uint8_t data) {
	if(col<(GLCD_WIDTH/2)){
		Chip_out_lcd(GLCD_CMD_PAGE|page,LCD_SEND_CMD+128,ZERO);
		Chip_out_lcd(GLCD_CMD_ADD|col,LCD_SEND_CMD+128,ZERO);
		Chip_out_lcd(data,LCD_SEND_DATA+128,ZERO);
	} else {
		col -= (GLCD_WIDTH/2);
		Chip_out_lcd(GLCD_CMD_PAGE|page,LCD_SEND_CMD+64,ZERO);
		Chip_out_lcd(GLCD_CMD_ADD|col,LCD_SEND_CMD+64,ZERO);
		Chip_out_lcd(data,LCD_SEND_DATA+64,ZERO);
	}
}
#endif


void lcd_write(uint8_t c,uint8_t cmd) {
#ifdef SF_ENABLE_GLCD
	if ((cmd & 0x0F)==LCD_SEND_DATA) {
		if (c>31 && c<127){
			return; // can't display
		}
		for(uint8_t i=ZERO;i<5;i++){
			lcd_glcd_data(glcd_row*7,(glcd_col*5)+i,FONT_5x7[(c-32)*5+i]);
		}
		glcd_col++;
		return;
	}
#endif
	Chip_out_lcd(c,cmd,ZERO);
}


void lcd_init(void) {
#ifndef SF_ENABLE_GLCD
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
#else
	lcd_clear();
	lcd_write(GLCD_CMD_ON,LCD_SEND_CMD+128+64); // Turn both lcd areas on.
	lcd_home();
#endif
}

void lcd_home(void) {
#ifndef SF_ENABLE_GLCD
	lcd_write(LCD_CMD_HOME,LCD_SEND_CMD);
#else
	glcd_col = ZERO;
	glcd_row = ZERO;
#endif
}

void lcd_clear(void) {
#ifndef SF_ENABLE_GLCD
	lcd_write(LCD_CMD_CLEAR,LCD_SEND_CMD);
#else
	for (int col=0;col<GLCD_WIDTH;col++) {
		for (int page=0;page<8;page++) {
			lcd_glcd_data(page,col,0xFF);
		}
	}
#endif
}

void lcd_cursor(uint8_t col, uint8_t row) {
#ifndef SF_ENABLE_GLCD
	if (row < 2) {
		lcd_write( ((row*0x40)+col) | LCD_CMD_CURSOR,LCD_SEND_CMD);
	} else if (row == 2) {
		lcd_write(( 0x14+col) | LCD_CMD_CURSOR,LCD_SEND_CMD); // 4 line lcd are really folded 2 line lcds :)
	} else if (row == 3) {
		lcd_write(( 0x54+col) | LCD_CMD_CURSOR,LCD_SEND_CMD);
	}
#else
	glcd_col = col;
	glcd_row = row;
#endif
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
		lcd_printCharP(Chip_cpu_type());
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
	lcd_printChar("HZ: ");
	uint32_t freq = calc_pwm_freq(ZERO);
	uint32_t freqHigh = freq/FREQ_MUL;
	uint32_t freqLow  = freq%FREQ_MUL;
	lcd_print((uint16_t)freqHigh);
	lcd_printDot();
	if (freqLow<10) {
		lcd_print(ZERO);
	}
	lcd_print((uint16_t)freqLow);
	lcd_printSpace();
	lcd_printSpace(); // cleans if freq goes fast from big number to small number
	lcd_printSpace(); // todo make better
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

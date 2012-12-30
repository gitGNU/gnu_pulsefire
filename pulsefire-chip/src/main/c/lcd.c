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
	Chip_out_lcd(c,cmd,ZERO);
}

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
	lcd_write(LCD_CMD_HOME,LCD_SEND_CMD);
}

void lcd_clear(void) {
	lcd_write(LCD_CMD_CLEAR,LCD_SEND_CMD);
}

void lcd_cursor(uint8_t col, uint8_t row) {
	if (row < 2) {
		lcd_write( ((row*0x40)+col) | LCD_CMD_CURSOR,LCD_SEND_CMD);
	} else if (row == 2) {
		lcd_write(( 0x14+col) | LCD_CMD_CURSOR,LCD_SEND_CMD); // 4 line lcd are really folded 2 line lcds :)
	} else if (row == 3) {
		lcd_write(( 0x54+col) | LCD_CMD_CURSOR,LCD_SEND_CMD);
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
	u16toa(value,pf_data.lcd_buff);
	uint8_t nn = ZERO;
	char* ss = pf_data.lcd_buff;
	while (*ss++) { nn++; }
	lcd_printChar(pf_data.lcd_buff);
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
		//lcd_cursor(ZERO,2);
		//lcd_printCharP(pmChipNameIdStr);
		lcd_cursor(ZERO,3);
		lcd_printCharP(Chip_cpu_type());
		lcd_printSpace();
		lcd_print(sizeof(pf_conf_struct));
	}

	Chip_delay(LCD_INIT_MSG_TIME);
	uint8_t i=ZERO;
	uint8_t lcd_size_col = 20;
	if (pf_conf.lcd_size == LCD_SIZE_2x16) {
		lcd_size_col = 16;
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


void lcd_draw_main(void) {

	lcd_cursor(ZERO,ZERO);
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
	lcd_printNum((uint16_t)freqLow,3);
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
		lcd_printNum(pf_conf.pwm_req_duty,3);
#ifdef SF_ENABLE_SWC
	}
#endif

	lcd_cursor(ZERO,ONE);
	lcd_printChar("S:");
	lcd_printNum(pf_data.pulse_step,2);
	lcd_cursor(col1,ONE);
/*
	lcd_printChar("DIR:");
	if (pf_conf.pulse_dir == PULSE_DIR_LRRL) {
		lcd_print(pf_data.pulse_dir_cnt);
	} else {
		lcd_print(pf_conf.pulse_dir);
	}
*/
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

uint8_t lcd_button(uint8_t but) {
	uint8_t input = pf_data.lcd_input;
	if (pf_conf.lcd_mode == LCD_MODE_BUT2) {
		if (but == ZERO) {
			return !(input > 0 && input < 127);
		} else {
			return !(input > 0 && input > 127);
		}
	} else if (pf_conf.lcd_mode == LCD_MODE_BUT4) {
		if (but == LCD_BUTTON_ESC) {
			return !(input > 0 && input > 200);
		} else if (but == LCD_BUTTON_ENTER) {
			return !(input > 0 && input > 150);
		} else if (but == LCD_BUTTON_UP) {
			return !(input > 0 && input > 100);
		} else if (but == LCD_BUTTON_DOWN) {
			return !(input > 0 && input > 50);
		}
	} 
	return ONE;
}

void lcd_draw_menu_var(uint8_t idx,uint8_t idxA,boolean update_value,boolean count_down,uint16_t value_delta) {
	lcd_clear();
	if (pf_conf.lcd_size == LCD_SIZE_4x20) {
		lcd_printCharP(pmLcdSelect);
		lcd_printCharP(pmLcdSelectValue);
		lcd_cursor(ZERO,ONE);
	}

	lcd_printCharP(pmLcdSelectedOption);
	lcd_printChar(Vars_getName(idx));
	if (Vars_isIndexA(pf_data.lcd_menu_idx)) {
		lcd_print(idxA);
	}

	if (pf_conf.lcd_size == LCD_SIZE_4x20) {
		lcd_cursor(ZERO,2);
	} else {
		lcd_cursor(ZERO,1);
	}
	lcd_printCharP(pmLcdValue);

	// Get the value
	uint16_t value = Vars_getValue(idx,idxA,0);

	// Update the value
	if (update_value) {
		if (count_down) {
			value-=value_delta;  // default is up, so this is down.
		} else {
			value+=value_delta;
		}
	}
	// Save the value
	if (update_value) {
		value = Vars_setValue(idx,idxA,ZERO,value);
	}
	lcd_print(value);

	if (pf_conf.lcd_size == LCD_SIZE_4x20) {
		lcd_cursor(ZERO,3);
	} else {
		if (pf_conf.lcd_size == LCD_SIZE_2x16) {
			lcd_cursor(9,ONE);
		} else {
			lcd_cursor(11,ONE);
		}
	}
	lcd_printCharP(pmLcdMultiply);
	lcd_print(pf_data.lcd_menu_mul);
}


void lcd_draw_menu(void) {
	uint8_t input0 = lcd_button(LCD_BUTTON_ENTER);
	uint8_t input1 = lcd_button(LCD_BUTTON_ESC);
	if (pf_data.lcd_menu_state == LCD_MENU_STATE_OFF && input0 == ONE && input1 == ONE) {
		return; // nothing is pressed
	}
	uint32_t current_time = millis();
	if (pf_data.lcd_menu_state != LCD_MENU_STATE_OFF && input0 == ONE && input1 == ONE) {

		if (current_time < pf_data.lcd_menu_time_cnt) {
			return; // no input to process
		}
		pf_data.lcd_menu_state     = LCD_MENU_STATE_OFF;
		pf_data.lcd_menu_value_idx = ZERO;
		pf_data.lcd_menu_idx       = ZERO;
		pf_data.lcd_menu_mul       = ONE;
		return; // exit menu after idle counter timeout
	}
	pf_data.lcd_menu_time_cnt = current_time + LCD_MENU_TIMEOUT;

	if (pf_data.lcd_menu_state == LCD_MENU_STATE_OFF) {
		if (input0 == ZERO) {
			pf_data.lcd_menu_state = LCD_MENU_STATE_SELECT;
			pf_data.lcd_menu_idx   = ZERO;
		}
		if (input1 == ZERO) {

			// todo add lcd paging.

#ifdef SF_ENABLE_LPM
			if (pf_conf.lpm_size > ZERO) {
				if (pf_data.lpm_state != LPM_IDLE) {
					pf_data.lpm_state = LPM_STOP;
				}
				if (pf_data.lpm_state == LPM_IDLE) {
					pf_data.lpm_state = LPM_INIT;
				}
				lcd_clear();
				Chip_delay(SYS_INPUT_DELAY);
				return;
			}
#endif
			return; // we only check for button if it is selected as source
		}
	}

	if (pf_data.lcd_menu_state == LCD_MENU_STATE_SELECT) {
		if (input0 == ZERO) {
			if (Vars_isTypeData(pf_data.lcd_menu_idx)) {
				pf_data.lcd_menu_idx = PF_VARS_SIZE;
			}
			if (pf_data.lcd_menu_idx == PF_VARS_SIZE) {
				lcd_clear();
				lcd_printCharP(pmLcdSelect);
				lcd_printCharP(pmLcdSelectOption);
				lcd_cursor(ZERO,ONE);
				lcd_printCharP(pmCmdSave);
				pf_data.lcd_menu_idx++;
				Chip_delay(SYS_INPUT_DELAY);
				return;
			}
			if (pf_data.lcd_menu_idx == PF_VARS_SIZE+1) {
				pf_data.lcd_menu_idx = ZERO;
			}
			lcd_clear();
			lcd_printCharP(pmLcdSelect);
			if (pf_conf.lcd_size == LCD_SIZE_4x20) {
				lcd_printCharP(pmLcdSelectOption);
			}
			lcd_cursor(ZERO,ONE);
			lcd_printCharP(pmLcdSelectedOption);
			lcd_printChar(Vars_getName(pf_data.lcd_menu_idx));

			if (pf_conf.lcd_size == LCD_SIZE_4x20) {
				lcd_cursor(0,2);
				lcd_printCharP(pmLcdValue);
			} else {
				if (pf_conf.lcd_size == LCD_SIZE_2x20) {
					lcd_cursor(20-8,ZERO); // have 5 chars for value + 3 for v:
				} else {
					lcd_cursor(16-8,ZERO); // have 5 chars for value + 3 for v:
				}
				lcd_printCharP(pmLcdValue);
			}

			if (Vars_isIndexA(pf_data.lcd_menu_idx)==false) {
				lcd_print(Vars_getValue(pf_data.lcd_menu_idx,ZERO,ZERO));
			} else {
				lcd_printCharP(pmLcdSelectIndex);
			}
			pf_data.lcd_menu_idx++;
			while (Vars_isMenuSkip(pf_data.lcd_menu_idx)) {
				pf_data.lcd_menu_idx++; // skip one or more menu items.
			}
		}
		if (input1 == ZERO) {
			pf_data.lcd_menu_idx--; // go to selected value
			if (pf_data.lcd_menu_idx == PF_VARS_SIZE) {
				lcd_cursor(4,ONE);
				uint8_t i=ZERO;
				for (i=ZERO;i < 4;i++) {
					lcd_printDot();
					Chip_delay(LCD_TEST_DOT_TIME);
				}
				Vars_writeConfig();
				for (i=ZERO;i < 4;i++) {
					lcd_printDot();
					Chip_delay(LCD_TEST_DOT_TIME);
				}
				lcd_cursor(12,ONE);
				lcd_printCharP(pmDone);
				return;
			}
			if (Vars_isIndexA(pf_data.lcd_menu_idx)) {
				pf_data.lcd_menu_state=LCD_MENU_STATE_VALUE_INDEXED;
				lcd_clear();
				lcd_printCharP(pmLcdSelect);
				lcd_printCharP(pmLcdSelectIndex);
				lcd_cursor(ZERO,ONE);
				lcd_printCharP(pmLcdSelectedOption);
				lcd_printChar(Vars_getName(pf_data.lcd_menu_idx));
				lcd_print(pf_data.lcd_menu_value_idx);
			} else {
				pf_data.lcd_menu_state=LCD_MENU_STATE_VALUE;
				lcd_draw_menu_var(pf_data.lcd_menu_idx,pf_data.lcd_menu_value_idx,false,false,ZERO);
			}
			Chip_delay(SYS_INPUT_DELAY);
			return; // After menu state change alway return so next check will run that state.
		}
	} else if (pf_data.lcd_menu_state == LCD_MENU_STATE_VALUE_INDEXED) {
		if (input0 == ZERO) {
			if (pf_data.lcd_menu_value_idx > Vars_getIndexAMax(pf_data.lcd_menu_idx)) { // was pf_conf.pulse_steps
				pf_data.lcd_menu_value_idx = ZERO;
			}
			lcd_clear();
			lcd_printCharP(pmLcdSelect);
			lcd_printCharP(pmLcdSelectIndex);
			lcd_cursor(ZERO,ONE);
			lcd_printCharP(pmLcdSelectedOption);
			lcd_printChar(Vars_getName(pf_data.lcd_menu_idx));
			lcd_print(pf_data.lcd_menu_value_idx);
			pf_data.lcd_menu_value_idx++;
		}
		if (input1 == ZERO) {
			if (pf_data.lcd_menu_value_idx != ZERO) {
				pf_data.lcd_menu_value_idx--;
			}
			pf_data.lcd_menu_state=LCD_MENU_STATE_VALUE;
			Chip_delay(SYS_INPUT_DELAY);
			return;
		}
	} else if (pf_data.lcd_menu_state == LCD_MENU_STATE_VALUE) {
		if (input0 == ZERO && input1 == ZERO) {
			if (pf_data.lcd_menu_mul==10000) {
				pf_data.lcd_menu_mul = ONE;
			} else {
				pf_data.lcd_menu_mul = pf_data.lcd_menu_mul * 10;  // make up&down 10*faster
			}
		}
		lcd_draw_menu_var(pf_data.lcd_menu_idx,pf_data.lcd_menu_value_idx,true,input1==ZERO,pf_data.lcd_menu_mul);
	}
	Chip_delay(SYS_INPUT_DELAY);

}

void lcd_loopMenu(void) {
	uint32_t current_time = millis();
	if (current_time < pf_data.sys_input_time_cnt) {
		return;
	}
	pf_data.sys_input_time_cnt = current_time + SYS_INPUT_TIME;


	lcd_draw_menu();
}

void lcd_draw_adc(void) {
	uint8_t line = 0;
	uint8_t max = 2;
	if (pf_conf.lcd_size == LCD_SIZE_4x20) {
		max = 4;
	}
	for (uint8_t i=ZERO;i < ADC_MAP_MAX;i++) {
		if (pf_conf.adc_map[i][QMAP_VAR] == QMAP_VAR_NONE) {
			continue; // no mapping
		}
		uint16_t valueAdc = pf_data.adc_value[i];
		if (line==max) {
			break;
		}
		lcd_cursor(ZERO,line);
		line++;
		lcd_printCharP(pmChipFlagADC);
		lcd_print(i);
		lcd_printCharP(pmSetSpaced);
		lcd_printNum(valueAdc,5);
	}
}

void lcd_draw_dic(void) {
	lcd_cursor(ZERO,ZERO);
	lcd_printCharP(pmLcdValue); // was dic now V:

	uint16_t data_row = pf_data.dic_value & 0xFF;
	int ii=OUTPUT_MAX-ONE;
	for (ii=8-ONE;ii>=ZERO;ii-- ) {
		uint16_t out = (data_row >> ii) & ONE;
		if (out == ZERO) {
			lcd_print(ZERO);
		} else {
			lcd_print(ONE);
		}
	}

	lcd_cursor(ZERO,ONE);
	lcd_printCharP(pmLcdValue);
	data_row = pf_data.dic_value >> 8;
	for (ii=16-ONE;ii>=8;ii-- ) {
		uint16_t out = (data_row >> ii) & ONE;
		if (out == ZERO) {
			lcd_print(ZERO);
		} else {
			lcd_print(ONE);
		}
	}
}


void lcd_draw_plp(void) {
	uint8_t line = 0;
	uint8_t max = 2;
	if (pf_conf.lcd_size == LCD_SIZE_4x20) {
		max = 4;
	}
	for (uint8_t i=ZERO;i < LCD_PLP_MAX;i++) {
		uint8_t var_id = pf_conf.lcd_plp[i];
		if (var_id == 0xFF) {
			continue; // no mapping
		}
		if (line==max) {
			break;
		}
		lcd_cursor(ZERO,line);
		line++;
		lcd_print(i);
		lcd_printSpace();
		lcd_printChar(Vars_getName(var_id));
		lcd_printCharP(pmSetSpaced);
		lcd_printNum(Vars_getValue(var_id,ZERO,ZERO),5);
	}
}



/*

Lcd pages

0 - main page
1 - adc input
2 - dic input
3 - plp

*/
void lcd_loop(void) {
	uint32_t current_time = millis();
	if (current_time < pf_data.lcd_time_cnt) {
		return; // wait until refresh
	}
	pf_data.lcd_time_cnt = current_time + LCD_REFRESH_TIME;


	Chip_out_doc(); // TODO: move somewhere

	lcd_loopMenu();

	if (pf_data.lcd_menu_state != LCD_MENU_STATE_OFF) {
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
	if (pf_data.stv_state != STV_STATE_OKE ) {
		lcd_clear();
		if (pf_data.stv_state == STV_STATE_WARNING_MAX || pf_data.stv_state == STV_STATE_WARNING_MIN) {
			lcd_printCharP(pmLcdSTVWarning);
		} else {
			lcd_printCharP(pmLcdSTVError);
		}
		if (pf_data.stv_state == STV_STATE_WARNING_MAX || pf_data.stv_state == STV_STATE_ERROR_MAX) {
			lcd_printCharP(pmLcdSTVMax);
		} else {
			lcd_printCharP(pmLcdSTVMin);
		}
		if (pf_conf.lcd_size != LCD_SIZE_4x20 ) {
			lcd_printCharP(pmLcdValue);
			lcd_print(Vars_getValue(pf_data.stv_map_idx,ZERO,ZERO));
		}
		lcd_cursor(0,1);lcd_printCharP(pmLcdSelectedOption);
		uint8_t varIdx = ZERO;
		if (pf_data.stv_state == STV_STATE_WARNING_MAX || pf_data.stv_state == STV_STATE_ERROR_MAX) {
			varIdx = pf_conf.stv_max_map[pf_data.stv_map_idx][QMAP_VAR];
		} else {
			varIdx = pf_conf.stv_max_map[pf_data.stv_map_idx][QMAP_VAR];
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

	uint8_t input1 = lcd_button(LCD_BUTTON_ESC);
	if (input1 == ZERO) {
		pf_data.lcd_page++;
		if (pf_data.lcd_page>3) {
			pf_data.lcd_page = ZERO;
		}
		pf_data.lcd_redraw=ZERO;
	} 


	if (pf_data.lcd_redraw == ZERO) {
		lcd_clear();
		pf_data.lcd_redraw = ONE;
	}


	if (pf_data.lcd_page==1) {
		lcd_draw_adc();
	} else if (pf_data.lcd_page==2) {
		lcd_draw_dic();
	} else if (pf_data.lcd_page==3) {
		lcd_draw_plp();
	} else {
		lcd_draw_main();
	}

	if (input1 == ZERO) {
		Chip_delay(SYS_INPUT_DELAY);
	}
}

#endif

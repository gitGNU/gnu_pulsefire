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


#include "input.h"

#ifdef SF_ENABLE_DIC
void check_avr_pin(uint8_t dic_bit,uint8_t result) {
  uint8_t resultOld = (pf_data.dic_value >> dic_bit) & ONE;
  if (result != resultOld && pf_conf.dic_map[dic_bit][QMAP_VAR] != QMAP_VAR_NONE) {
    if (result == ZERO) { pf_data.dic_value -= (ONE << dic_bit);   // clear bit in data
    } else {              pf_data.dic_value += (ONE << dic_bit); }   // set bit in data  
    if (result == ZERO) {
      Vars_setValue(pf_conf.dic_map[dic_bit][QMAP_VAR],pf_conf.dic_map[dic_bit][QMAP_VAR_IDX],ZERO,pf_conf.dic_map[dic_bit][QMAP_VALUE_A]);
    } else {
      Vars_setValue(pf_conf.dic_map[dic_bit][QMAP_VAR],pf_conf.dic_map[dic_bit][QMAP_VAR_IDX],ZERO,pf_conf.dic_map[dic_bit][QMAP_VALUE_B]);
    }
  }
}
#endif

// read out digital values.
#ifdef SF_ENABLE_DIC
void Input_loopDic(void) {
  uint32_t current_time = millis();
  if (current_time < pf_data.dic_time_cnt) {
    return;
  }
  pf_data.dic_time_cnt = current_time + DIC_INPUT_TIME;
  
  // Special readout of pin2 into dic2 for 1or3 digital inputs without extension
  if (pf_conf.avr_pin2_map == PIN2_DIC2_IN) {
    check_avr_pin(2,digitalRead(IO_DEF_IO_PORT_IN,IO_DEF_PIN2_PIN));
  }
  if (pf_conf.avr_pin3_map == PIN3_DIC3_IN) {
    check_avr_pin(3,digitalRead(IO_DEF_IO_PORT_IN,IO_DEF_PIN3_PIN));
  }
  if (pf_conf.avr_pin4_map == PIN4_DIC4_IN) {
    check_avr_pin(4,digitalRead(IO_DEF_IO_PORT_IN,IO_DEF_PIN4_PIN));
  }
  if (pf_conf.avr_pin5_map == PIN5_DIC5_IN) {
    check_avr_pin(5,digitalRead(IO_DEF_IO_PORT_IN,IO_DEF_PIN5_PIN));
  }
  if (pf_conf.avr_pin2_map == PIN2_DIC8_IN) {
    check_avr_pin(8,digitalRead(IO_DEF_IO_PORT_IN,IO_DEF_PIN2_PIN));
  }
  if (pf_conf.avr_pin3_map == PIN3_DIC9_IN) {
    check_avr_pin(9,digitalRead(IO_DEF_IO_PORT_IN,IO_DEF_PIN3_PIN));
  }
  if (pf_conf.avr_pin4_map == PIN4_DIC10_IN) {
    check_avr_pin(10,digitalRead(IO_DEF_IO_PORT_IN,IO_DEF_PIN4_PIN));
  }
  if (pf_conf.avr_pin5_map == PIN5_DIC11_IN) {
    check_avr_pin(11,digitalRead(IO_DEF_IO_PORT_IN,IO_DEF_PIN5_PIN));
  }
  
  #ifdef SF_ENABLE_EXT_LCD  
  uint8_t i=ZERO;
  for (i=ZERO;i < 8 /* DIC_NUM_MAX */ ;i++) {
    #ifdef SF_ENABLE_EXT_LCD_DIC
      lcd_writeMux(0x80,LCD_SEND_CMD, ((i & 6) >> ONE) );
    #else
      if (i>ONE) {
        break; // check only 2 inputs
      }
    #endif
    if (i==2  && pf_conf.avr_pin2_map == PIN2_DIC2_IN)  { continue; }
    if (i==3  && pf_conf.avr_pin3_map == PIN3_DIC3_IN)  { continue; }
    if (i==4  && pf_conf.avr_pin4_map == PIN4_DIC4_IN)  { continue; }
    if (i==5  && pf_conf.avr_pin5_map == PIN5_DIC5_IN)  { continue; }
    if (i==8  && pf_conf.avr_pin2_map == PIN2_DIC8_IN)  { continue; }
    if (i==9  && pf_conf.avr_pin3_map == PIN3_DIC9_IN)  { continue; }
    if (i==10 && pf_conf.avr_pin4_map == PIN4_DIC10_IN) { continue; }
    if (i==11 && pf_conf.avr_pin5_map == PIN5_DIC11_IN) { continue; }
    
    uint8_t result = ZERO;
    if ((i & ONE) == ZERO) {
      result = digitalRead(IO_DEF_IO_PORT_IN,IO_EXT_INPUT0_PIN);
    } else {
      result = digitalRead(IO_DEF_IO_PORT_IN,IO_EXT_INPUT1_PIN);
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

    if (result == ZERO) {
      pf_data.dic_value -= (ONE << i); // clear bit in data
    } else {
      pf_data.dic_value += (ONE << i); // set bit in data
    }
    if (pf_conf.dic_map[i][QMAP_VAR] == QMAP_VAR_NONE) {
      continue; // no mapping
    }
    if (result == ZERO) {
      Vars_setValue(pf_conf.dic_map[i][QMAP_VAR],pf_conf.dic_map[i][QMAP_VAR_IDX],ZERO,pf_conf.dic_map[i][QMAP_VALUE_A]);
    } else {
      Vars_setValue(pf_conf.dic_map[i][QMAP_VAR],pf_conf.dic_map[i][QMAP_VAR_IDX],ZERO,pf_conf.dic_map[i][QMAP_VALUE_B]);
    }
  }
  #endif
}
#endif

//ISR(ADC_vect) {}

// read out analog values.
#ifdef SF_ENABLE_ADC
void Input_loopAdc(void) {
  uint32_t current_time = millis();
  if (current_time < pf_data.adc_time_cnt) {
    return;
  }
  pf_data.adc_time_cnt = current_time + ADC_INPUT_TIME;
  //int pin_idx = IO_EXT_ADC0_PIN;
  uint8_t i=ZERO;
  for (i=ZERO;i < ADC_NUM_MAX;i++) {
    #ifdef SF_ENABLE_LCD
    #ifndef SF_ENABLE_EXT_LCD
    if (i < 4) {
       //pin_idx++;
       continue; // only read 4 & 5 in normal connection mode.
    }
    #endif
    #endif
    int valueAdc = analogRead(i);
    //pin_idx++;
    int valueAdcOld = pf_data.adc_value[i];
    pf_data.adc_value[i] = valueAdc;
    if (valueAdc == valueAdcOld) {
      continue; // no change
    }
    if (pf_conf.adc_map[i][QMAP_VAR] == QMAP_VAR_NONE) {
      continue; // no mapping
    }
    if (pf_conf.adc_jitter > ZERO) {
      uint16_t c = valueAdc - valueAdcOld; // only update when change is bigger then jitter treshhold
      if (c > 0 && c < pf_conf.adc_jitter)        { continue; }
      if (c < 0 && c > (ZERO-pf_conf.adc_jitter)) { continue; }
    }
    // map to min/max value and assign to variable
    valueAdc = mapValue(valueAdc,ZERO,ADC_VALUE_MAX,pf_conf.adc_map[i][QMAP_VALUE_A],pf_conf.adc_map[i][QMAP_VALUE_B]);
    if (pf_conf.adc_map[i][QMAP_VAR] < PF_VARS_SIZE) {
       Vars_setValue(pf_conf.adc_map[i][QMAP_VAR],pf_conf.adc_map[i][QMAP_VAR_IDX],ZERO,valueAdc);
    }
  }
}
#endif


#ifdef SF_ENABLE_LCD
void Input_updateMenu(uint8_t idx,uint8_t idxA,boolean update_value,boolean count_down,uint16_t value_delta) {
  lcd_clear();
  if (pf_conf.lcd_size == LCD_SIZE_4x20) {
    lcd_printCharP(pmLcdSelect);
    lcd_printCharP(pmLcdSelectValue);
    lcd_cursor(ZERO,ONE);
  }
  
  lcd_printCharP(pmLcdSelectedOption);
  lcd_printChar(Vars_getName(idx));
  if (Vars_isIndexA(pf_prog.lcd_menu_idx)) {
    lcd_print(idxA);
  }
  
  if (pf_conf.lcd_size == LCD_SIZE_4x20) {
    lcd_cursor(ZERO,2);
  } else {
    lcd_cursor(ZERO,1);
  }
  lcd_printCharP(pmLcdValue);

  uint16_t value = Vars_getValue(idx,idxA,0);
  //uint16_t value_old = value;
  // update the value
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
  lcd_print(pf_prog.lcd_menu_mul);
}
#endif

// Check for user input via buttons.
#ifdef SF_ENABLE_LCD
void Input_loopLcd() {
  uint32_t current_time = millis();
  if (current_time < pf_data.sys_input_time_cnt) {
    return;
  }
  pf_data.sys_input_time_cnt = current_time + SYS_INPUT_TIME;
  
  if (pf_conf.avr_pin3_map != PIN3_MENU0_IN && pf_conf.avr_pin4_map != PIN4_MENU1_IN) {
    return;// todo use dic for menu pins.
  }

  int input0 = digitalRead(IO_DEF_IO_PORT_IN,IO_DEF_PIN3_PIN);
  int input1 = digitalRead(IO_DEF_IO_PORT_IN,IO_DEF_PIN4_PIN);
  if (pf_prog.lcd_menu_state != LCD_MENU_STATE_OFF && input0 == ONE && input1 == ONE) {
    if (current_time < pf_prog.lcd_menu_time_cnt) {
      return; // no input to process
    }
    pf_prog.lcd_menu_state     = LCD_MENU_STATE_OFF;
    pf_prog.lcd_menu_value_idx = ZERO;
    pf_prog.lcd_menu_idx       = ZERO;
    pf_prog.lcd_menu_mul       = ONE;
    return; // exit menu after idle counter timeout
  }
  pf_prog.lcd_menu_time_cnt = current_time + LCD_MENU_TIMEOUT;
  
  if (pf_prog.lcd_menu_state == LCD_MENU_STATE_OFF) {
    if (input0 == ZERO) {
      pf_prog.lcd_menu_state = LCD_MENU_STATE_SELECT;
      pf_prog.lcd_menu_idx   = ZERO;
    }
    if (input1 == ZERO) {
      
      // todo add lcd paging.
      
        #ifdef SF_ENABLE_LPM
        if (pf_conf.lpm_size > ZERO) {
          if (pf_data.lpm_state == LPM_IDLE) {
            pf_data.lpm_state = LPM_INIT;
          }
          if (pf_data.lpm_state == LPM_DONE_WAIT) {
            pf_data.lpm_state = LPM_IDLE;
          }
          if (pf_data.lpm_state == LPM_RUN) {
            pf_data.lpm_state = LPM_STOP;
          }
          lcd_clear();
          Chip_delay(SYS_INPUT_DELAY);
          return;
        }
        #endif
        return; // we only check for button if it is selected as source
    }
  }
 
  if (pf_prog.lcd_menu_state == LCD_MENU_STATE_SELECT) {
    if (input0 == ZERO) {
      if (Vars_isTypeConf(pf_prog.lcd_menu_idx)==false) {
        pf_prog.lcd_menu_idx = PF_VARS_SIZE;
      }
      if (pf_prog.lcd_menu_idx == PF_VARS_SIZE) {
        lcd_clear();
        lcd_printCharP(pmLcdSelect);
        lcd_printCharP(pmLcdSelectOption);
        lcd_cursor(ZERO,ONE);
        lcd_printCharP(pmCmdSave);
        pf_prog.lcd_menu_idx++;
        Chip_delay(SYS_INPUT_DELAY);
        return;
      }
      if (pf_prog.lcd_menu_idx == PF_VARS_SIZE+1) {
        pf_prog.lcd_menu_idx = ZERO;
      }
      lcd_clear();
      lcd_printCharP(pmLcdSelect);
      if (pf_conf.lcd_size == LCD_SIZE_4x20) {
        lcd_printCharP(pmLcdSelectOption);
      }
      lcd_cursor(ZERO,ONE);
      lcd_printCharP(pmLcdSelectedOption);
      lcd_printChar(Vars_getName(pf_prog.lcd_menu_idx));
      
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

      if (Vars_isIndexA(pf_prog.lcd_menu_idx)==false) {
        lcd_print(Vars_getValue(pf_prog.lcd_menu_idx,ZERO,ZERO));
      } else {
        lcd_printCharP(pmLcdSelectIndex);
      }
      pf_prog.lcd_menu_idx++;
      while (Vars_isMenuSkip(pf_prog.lcd_menu_idx)) {
        pf_prog.lcd_menu_idx++; // skip one or more menu items.
      }
    }
    if (input1 == ZERO) {
      pf_prog.lcd_menu_idx--; // go to selected value
      if (pf_prog.lcd_menu_idx == PF_VARS_SIZE) {
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
      if (Vars_isIndexA(pf_prog.lcd_menu_idx)) {
        pf_prog.lcd_menu_state=LCD_MENU_STATE_VALUE_INDEXED;
        lcd_clear();
        lcd_printCharP(pmLcdSelect);
        lcd_printCharP(pmLcdSelectIndex);
        lcd_cursor(ZERO,ONE);
        lcd_printCharP(pmLcdSelectedOption);
        lcd_printChar(Vars_getName(pf_prog.lcd_menu_idx));
        lcd_print(pf_prog.lcd_menu_value_idx);
      } else {
        pf_prog.lcd_menu_state=LCD_MENU_STATE_VALUE;
        Input_updateMenu(pf_prog.lcd_menu_idx,pf_prog.lcd_menu_value_idx,false,false,ZERO);
      }
      Chip_delay(SYS_INPUT_DELAY);
     return; // After menu state change alway return so next check will run that state.
    }
  } else if (pf_prog.lcd_menu_state == LCD_MENU_STATE_VALUE_INDEXED) {
    if (input0 == ZERO) {
      if (pf_prog.lcd_menu_value_idx > Vars_getIndexAMax(pf_prog.lcd_menu_idx)) { // was pf_conf.pulse_steps
        pf_prog.lcd_menu_value_idx = ZERO;
      }
      lcd_clear();
      lcd_printCharP(pmLcdSelect);
      lcd_printCharP(pmLcdSelectIndex);
      lcd_cursor(ZERO,ONE);      
      lcd_printCharP(pmLcdSelectedOption);
      lcd_printChar(Vars_getName(pf_prog.lcd_menu_idx));
      lcd_print(pf_prog.lcd_menu_value_idx);
      pf_prog.lcd_menu_value_idx++;
    }
    if (input1 == ZERO) {
      if (pf_prog.lcd_menu_value_idx != ZERO) {
        pf_prog.lcd_menu_value_idx--;
      }
      pf_prog.lcd_menu_state=LCD_MENU_STATE_VALUE;
      Chip_delay(SYS_INPUT_DELAY);
      return;
    }
  } else if (pf_prog.lcd_menu_state == LCD_MENU_STATE_VALUE) {
    if (input0 == ZERO && input1 == ZERO) {
      pf_prog.lcd_menu_mul = pf_prog.lcd_menu_mul * 10;  // make up&down 10*faster
    }
    Input_updateMenu(pf_prog.lcd_menu_idx,pf_prog.lcd_menu_value_idx,true,input1==ZERO,pf_prog.lcd_menu_mul);
  }
  Chip_delay(SYS_INPUT_DELAY);
}
#endif

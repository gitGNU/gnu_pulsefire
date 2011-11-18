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


#include "lpm.h"


// Check for lpm messurements
#ifdef SF_ENABLE_LPM
void LPM_loopRelayOutput(boolean relay_open) {
  int pinLevel = ONE;
  if (relay_open==pf_conf.lpm_relay_inv) {
    pinLevel = ZERO;
  }
  if (pf_conf.avr_pin2_map == PIN2_RELAY_OUT) {
    digitalWrite(IO_DEF_OUT_PORT,IO_DEF_PIN2_PIN,pinLevel);
  }
  if (pf_conf.avr_pin3_map == PIN3_RELAY_OUT) {
    digitalWrite(IO_DEF_OUT_PORT,IO_DEF_PIN3_PIN,pinLevel);
  }
  if (pf_conf.avr_pin4_map == PIN4_RELAY_OUT) {
    digitalWrite(IO_DEF_OUT_PORT,IO_DEF_PIN4_PIN,pinLevel);
  }
  if (pf_conf.avr_pin5_map == PIN5_RELAY_OUT) {
    digitalWrite(IO_DEF_OUT_PORT,IO_DEF_PIN4_PIN,pinLevel);
  }
}

void LPM_loop(void) {
  if (pf_conf.lpm_size == ZERO) {
    return; // auto lpm only works when size is specefied.
  }
  if (pf_data.lpm_auto_cmd == ONE) {
    if (pf_data.lpm_state == LPM_DONE_WAIT) {
      pf_data.lpm_state = LPM_IDLE;
      Serial_println();
      Serial_printCharP(pmCmdReqAutoLPM);
      Serial_printCharP(pmGetSpaced);
      Serial_printDec(pf_data.lpm_result/100 % 100);Serial_printChar(".");
      Serial_printDec(pf_data.lpm_result/10 % 10);  Serial_printDec(pf_data.lpm_result % 10);
      Serial_printChar(" ");
      Serial_printDec(pf_data.lpm_total_time/100 % 100);Serial_printChar(".");
      Serial_printDec(pf_data.lpm_total_time/10 % 10);  Serial_printDec(pf_data.lpm_total_time % 10);
      Serial_println();
      Serial_printCharP(pmPromt);
    }
  }
  
  switch (pf_data.lpm_state) {
    case LPM_INIT:
      if ( pf_data.lpm_level < pf_conf.lpm_start) {
        pf_data.lpm_state = LPM_RECOVER;
      } else {
        pf_data.lpm_state = LPM_START;
      }
      #ifdef SF_ENABLE_LCD
      lcd_clear();
      lcd_printCharP(pmCmdReqAutoLPM);
      lcd_cursor(ZERO,ONE);
      lcd_printCharP(pmLPMWait);
      #endif
      break;
    case LPM_IDLE:
      return;
    case LPM_START:
      LPM_loopRelayOutput(false); // close the output tube
      pf_data.lpm_state = LPM_START_WAIT;
      #ifdef SF_ENABLE_LCD
      lcd_clear();
      lcd_printCharP(pmCmdReqAutoLPM);
      lcd_cursor(ZERO,ONE);
      lcd_printCharP(pmLPMStart);
      #endif
      break;
    case LPM_START_WAIT:
      if (pf_data.lpm_level < pf_conf.lpm_start) {
        pf_data.lpm_start_time = millis();
        pf_data.lpm_state = LPM_RUN;
        #ifdef SF_ENABLE_LCD
        lcd_clear();
        #endif
      }
      break;
    case LPM_STOP:
      #ifdef SF_ENABLE_LCD
      lcd_clear();
      lcd_printCharP(pmLPMCancel);
      lcd_cursor(ZERO,ONE);
      lcd_printCharP(pmLPMWait);
      #endif
      pf_data.lpm_state = LPM_RECOVER;
      break;
    case LPM_RUN: {
      pf_data.lpm_total_time = (millis()-pf_data.lpm_start_time)/10;
      uint16_t stepSize = (pf_conf.lpm_start-pf_conf.lpm_stop)/10; // 10 steps
      int stepDone = (pf_conf.lpm_start-pf_data.lpm_level) / stepSize;
      if (stepDone < ZERO) {
        stepDone = ZERO;
      }
      if (stepDone > 13) {
        stepDone = 13;
      }
      // calulate lpm in 100x size.
      // 600 = 1 minute in seconds
      // lpm_totalTime/10 = time in seconds
      // lpm_size is in ML!
      pf_data.lpm_result = (600/(pf_data.lpm_total_time/100))*pf_conf.lpm_size/100;
      
      if (pf_data.lpm_level < pf_conf.lpm_stop) {
        pf_data.lpm_state = LPM_DONE;
      } 
      if (pf_data.lpm_result == 0) {
        pf_data.lpm_state = LPM_DONE; // timeout of calculations
      }
      _delay_ms(10);
      
      #ifdef SF_ENABLE_LCD
      lcd_cursor(ZERO,ZERO);
      lcd_printChar("P: ");
      uint8_t i=ZERO;
      for (i=ZERO;i < stepDone;i++) {
        lcd_printDot();
      }
      lcd_cursor(ZERO,ONE);
      lcd_printChar("S:");
      lcd_print((uint16_t)pf_data.lpm_total_time/100 % 100);
      lcd_printDot();
      lcd_print((uint16_t)pf_data.lpm_total_time/10 % 10);
      lcd_print((uint16_t)pf_data.lpm_total_time % 10);
      lcd_printChar(" L:");
      lcd_print((uint16_t)pf_data.lpm_result/100 % 100);
      lcd_printDot();
      lcd_print((uint16_t)pf_data.lpm_result/10 % 10);
      lcd_print((uint16_t)pf_data.lpm_result % 10);
      #endif
      break;
    }
    case LPM_DONE:
      LPM_loopRelayOutput(true); // open output tube
      pf_data.lpm_state = LPM_DONE_WAIT;
      #ifdef SF_ENABLE_LCD
      lcd_clear();
      lcd_printChar("lpm: ");
      lcd_print((uint16_t)pf_data.lpm_result/100 % 100);
      lcd_printDot();
      lcd_print((uint16_t)pf_data.lpm_result/10 % 10);
      lcd_print((uint16_t)pf_data.lpm_result % 10);
      lcd_cursor(ZERO,ONE);
      lcd_printChar("time: ");
      lcd_print((uint16_t)pf_data.lpm_total_time/100 % 100);
      lcd_printDot();
      lcd_print((uint16_t)pf_data.lpm_total_time/10 % 10);
      lcd_print((uint16_t)pf_data.lpm_total_time % 10);      
      #endif
      break;
    case LPM_DONE_WAIT:
      // wait for user input
      break;
    case LPM_RECOVER:
       LPM_loopRelayOutput(true); // open output tube
       pf_data.lpm_state = LPM_RECOVER_WAIT;
      break;
    case LPM_RECOVER_WAIT:
      if (pf_data.lpm_level < pf_conf.lpm_start) {
        break;
      }
      pf_data.lpm_state = LPM_INIT;
      break;
  }
}
#endif 



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
 
// Send data to the outputs
void int_send_output(uint16_t data) {
  
  // Reverse output data if needed.
  uint8_t pulse_dir = pf_conf.pulse_dir;
  if (pulse_dir == PULSE_DIR_LRRL) {
      pulse_dir = pf_data.pulse_dir_cnt;
  }
  if (pulse_dir == PULSE_DIR_RL) {
    data = reverse_bits(data,pf_conf.pulse_steps);
  }
  
  // Apply user defined output enabled flags.
  if (pf_data.pulse_bank_cnt==ZERO) {
    data = data & pf_conf.pulse_mask_a;
  } else {
    data = data & pf_conf.pulse_mask_b;
  }
  
  // hard limit on output bits, for cleaning extra shifted bits.
  uint16_t out_limit = ZERO;
  for (uint8_t i=ZERO;i < pf_conf.pulse_steps;i++) {
    out_limit |= (ONE << i); 
  }
  data = data & out_limit;
  
  // All output off override, but with respect to inverse request.
  if (pf_conf.pulse_enable == ZERO) {
    data = PULSE_DATA_OFF;
  }

  #ifdef SF_ENABLE_STV
  if (pf_conf.stv_error_mode == PULSE_MODE_OFF) {
    // This is so on error and mode off, output stays off while mode changes and innner if save few cycles.
    if (pf_prog.stv_state == STV_STATE_ERROR_MAX || pf_prog.stv_state == STV_STATE_ERROR_MIN) {
      data = PULSE_DATA_OFF;
    }
  }

  #endif
  
  // Inverse output if requested
  if (pf_conf.pulse_inv > ZERO) {
    data = ~data; 
  }
  
  // Send data to output depending on connection mode; max outs 16,8,3,6 
  #if defined(SF_ENABLE_EXT_OUT_16BIT)
    digitalWrite(IO_EXT_OUT_E_PIN,LOW);
    shiftOut(IO_EXT_OUT_DATA_PIN,IO_EXT_OUT_CLK_PIN,MSBFIRST,(data >> 8)); // high byte
    shiftOut(IO_EXT_OUT_DATA_PIN,IO_EXT_OUT_CLK_PIN,MSBFIRST,data);        // low byte, is last to that fist chip is zero !
    digitalWrite(IO_EXT_OUT_E_PIN,HIGH);
  #elif defined(SF_ENABLE_EXT_OUT)
    digitalWrite(IO_EXT_OUT_E_PIN,LOW);
    shiftOut(IO_EXT_OUT_DATA_PIN,IO_EXT_OUT_CLK_PIN,MSBFIRST,data);
    digitalWrite(IO_EXT_OUT_E_PIN,HIGH);
  #elif defined(SF_ENABLE_EXT_LCD)
    digitalWrite(IO_DEF_OUT_0_PIN,(data & 1) >> 0); // only set 3 bits on output, other 3 are for lcd
    digitalWrite(IO_DEF_OUT_1_PIN,(data & 2) >> 1);
    digitalWrite(IO_DEF_OUT_2_PIN,(data & 4) >> 2);
  #else
    IO_DEF_OUT_PORT = data;
  #endif
}

#ifdef SF_ENABLE_SWC
boolean int_soft_warmup(void) {
  uint32_t sys_up_secs = millis()/1000;
  if (sys_up_secs == ZERO) {
    sys_up_secs = ONE; // very fast cpu here.
  }
  if (pf_conf.swc_delay > ZERO && pf_conf.swc_delay > sys_up_secs) {
    return true;
  }
  pf_data.swc_secs_cnt = sys_up_secs - pf_conf.swc_delay; // correct for pre delay
  if (pf_data.swc_secs_cnt == ZERO) {
    pf_data.swc_secs_cnt = ONE;
  }
  if (pf_data.swc_secs_cnt > pf_conf.swc_secs) {
    pf_data.swc_secs_cnt  = ZERO;
    if (pf_conf.swc_mode != 0xFF) {
      pf_var_value_set(ONE,ZERO,ZERO,pf_data.swc_mode_org);
    }
    return false; // we are done with startup.
  }  
  int startup_duty = pf_conf.swc_duty * (pf_conf.swc_secs - pf_data.swc_secs_cnt)/2;
  if (pf_data.pulse_step == ZERO) {
    pf_data.swc_duty_cnt = ZERO; // this is not yet corrent for all modes
  }
  if (pf_data.pulse_step == ONE) {
    pf_data.swc_duty_cnt++;
    if (pf_data.swc_duty_cnt < startup_duty) {
      int_send_output(PULSE_DATA_OFF);
      return true; // wait
    }
  }
  return false; // run steps
}
#endif

boolean int_pulse_mode_flash(void) {
  if (pf_data.pulse_data != PULSE_DATA_OFF) {
    pf_data.pulse_data = PULSE_DATA_OFF;
  } else {
    pf_data.pulse_data = PULSE_DATA_ON;
  }
  return true;
}

boolean int_pulse_mode_train(void) {
  if (pf_conf.pulse_steps == ONE) {
      return int_pulse_mode_flash(); // small exception to make it work like expected
  }
  if (pf_data.pulse_bank_cnt==ZERO) {
    pf_data.pulse_data = pf_conf.pulse_init_a << pf_data.pulse_step;
  } else {
    pf_data.pulse_data = pf_conf.pulse_init_b << pf_data.pulse_step;
  }
  return true;
}

#ifdef SF_ENABLE_PPM
boolean int_pulse_mode_ppm(uint8_t step_zero,boolean interleaved) {
  // Do ppm seqence shifting
  uint16_t out_data = ZERO;
  if (pf_data.pulse_bank_cnt==ZERO) {
    uint16_t ppm_data = (pf_conf.ppm_data_a[step_zero] >> pf_data.ppm_idx[step_zero]) & ONE;
    out_data |= ppm_data << pf_data.pulse_step;
  } else {
    uint16_t ppm_data = (pf_conf.ppm_data_b[step_zero] >> pf_data.ppm_idx[step_zero]) & ONE;
    out_data |= ppm_data << pf_data.pulse_step;   
  }

  // do future step with offset for phasing effect
  if (pf_conf.ppm_data_offset > ZERO && pf_conf.ppm_data_offset >= pf_data.ppm_idx[step_zero]) {
    int off_out = step_zero + ONE;
    if (off_out < pf_conf.pulse_steps) {
      if (pf_data.pulse_bank_cnt==ZERO) {
        out_data |= (((pf_conf.ppm_data_a[off_out] >> pf_data.ppm_idx[off_out]) & ONE) << off_out);
      } else {
        out_data |= (((pf_conf.ppm_data_b[off_out] >> pf_data.ppm_idx[off_out]) & ONE) << off_out);
      }
      if (pf_data.ppm_idx[off_out] == ZERO) {
        pf_data.ppm_idx[off_out] = pf_conf.ppm_data_length - ONE;
      } else {
        pf_data.ppm_idx[off_out]--;
      }
    }
  }
  pf_data.pulse_data = out_data;
  if (pf_data.ppm_idx[step_zero] == ZERO) {    
    pf_data.ppm_idx[step_zero] = pf_conf.ppm_data_length - ONE;    
    return true;
  } else {
    pf_data.ppm_idx[step_zero]--;
    if (interleaved) {
      return true;
    } else {
      return false;
    }
  }
}
#endif

#ifdef SF_ENABLE_PPM
boolean int_pulse_mode_ppma(void) {
  // Shift all channel data out every step.
  uint16_t out_data = ZERO;
  for (uint8_t i=ZERO;i < pf_conf.pulse_steps;i++) {
    uint16_t ppm_data = ZERO;
    if (pf_data.pulse_bank_cnt==ZERO) {
      ppm_data = pf_conf.ppm_data_a[i];
    } else {
      ppm_data = pf_conf.ppm_data_b[i];
    }
    out_data |= ((ppm_data >> pf_data.ppm_idx[pf_data.pulse_step]) & ONE) << i;
  }
  pf_data.pulse_data = out_data;
  if (pf_data.ppm_idx[pf_data.pulse_step] == ZERO) {    
    pf_data.ppm_idx[pf_data.pulse_step] = pf_conf.ppm_data_length - ONE;    
    return true;
  } else {
    pf_data.ppm_idx[pf_data.pulse_step]--;
    return false;
  }
}
#endif

// Do all work per timer step cnt
void int_do_work(void) {
  if (pf_data.pwm_state == PWM_STATE_STEP_DUTY) {
    return; // waiting for step duty
  }
  if (pf_conf.pulse_trig != PULSE_TRIG_LOOP && pf_data.pwm_state == PWM_STATE_IDLE) {
    return; // disable when in manuale trigger fire.
  }
  uint8_t step_zero = pf_data.pulse_step;
  if (pf_conf.pulse_mode == PULSE_MODE_TRAIN | pf_conf.pulse_mode == PULSE_MODE_PPMI) {
    step_zero--; // corrected value for compa/compb and wait... mmmm
    if (pf_data.pulse_step == ZERO) {
      step_zero = pf_conf.pulse_steps - ONE;
    }
  }
  
  uint8_t step_ocr = step_zero;
  if (pf_conf.pulse_mode == PULSE_MODE_FLASH_ZERO) {
    step_ocr = ZERO; // always use timeings/data of channel 0 for every step.
  }
  if (pf_data.pulse_bank_cnt==ZERO) {
    OCR1A = pf_conf.pwm_on_cnt_a[step_ocr];
  } else {
    OCR1A = pf_conf.pwm_on_cnt_b[step_ocr];
  }
  TCNT1 = ZERO;

  // time step with counter
  pf_data.pwm_loop_cnt++;
  if (pf_data.pwm_loop_cnt < pf_data.pwm_loop_max) {
    return;
  }
  if (pf_data.pulse_step == ZERO) {
    pf_data.pwm_loop_max = pf_conf.pwm_loop;// reload loop_max before first step but after wait loop of running step
  }
  pf_data.pwm_loop_cnt = ZERO;
  
  // wait loop for this step
  uint16_t c = ZERO;
  for (uint16_t i=ZERO;i < pf_conf.pwm_tune_cnt[step_zero];i++) {
    asm volatile ("nop"); // todo: scope out speed of this loop and tune.
  }

  // Check for soft startup
  #ifdef SF_ENABLE_SWC
  if (pf_data.swc_secs_cnt > ZERO) {
    if (int_soft_warmup()) {
      return; // wait in startup mode
    }
  }
  #endif
  
  // wait for pulse trig delay
  if (pf_conf.pulse_trig != PULSE_TRIG_LOOP && pf_data.pulse_trig_delay_cnt > ZERO) {
    pf_data.pulse_trig_delay_cnt--;
    return;
  }
  
  // wait for pulse post delay
  if (pf_data.pwm_state == PWM_STATE_WAIT_POST) {
    int_send_output(PULSE_DATA_OFF);
    pf_data.pulse_post_delay_cnt++;
    uint32_t pre_train_wait = ((F_CPU/pf_conf.pwm_on_cnt_a[0]/100) * pf_conf.pulse_post_delay) / pf_conf.pwm_loop;
    if (pf_data.pulse_post_delay_cnt < pre_train_wait) {
      return;
    }
    pf_data.pulse_post_delay_cnt = ZERO;
    pf_data.pwm_state = PWM_STATE_RUN;
  }
  
  // use - for letting last output time correctly until off.
  if (pf_data.pwm_state == PWM_STATE_TRIGGER_END) {
    int_send_output(PULSE_DATA_OFF);
    pf_data.pwm_state = PWM_STATE_IDLE;
    return;
  }

  // do step duty timing
  uint16_t off_time = ZERO;
  if (pf_data.pulse_bank_cnt==ZERO) {
    off_time = pf_conf.pwm_off_cnt_a[step_ocr];
  } else {
    off_time = pf_conf.pwm_off_cnt_b[step_ocr];
  } 
  if (off_time > ZERO) {
    if (pf_data.pwm_state != PWM_STATE_STEP_DUTY_DONE) {
      pf_data.pwm_state = PWM_STATE_STEP_DUTY;
      int_send_output(PULSE_DATA_OFF);
      OCR1B = off_time;
      TCNT1 = 0xFFFF-8; // reset again, with some time so interrupts are enabled again, else we miss it sometimes.
      return;
    }
    pf_data.pwm_state = PWM_STATE_RUN;
  }
    
  // Calc pulse_data for postition based on pulse_mode
  boolean step_update = false;
  switch (pf_conf.pulse_mode) {
    case PULSE_MODE_FLASH_ZERO:
    case PULSE_MODE_FLASH:         step_update = int_pulse_mode_flash();              break;
    case PULSE_MODE_TRAIN:         step_update = int_pulse_mode_train();              break;
    #ifdef SF_ENABLE_PPM
    case PULSE_MODE_PPM:           step_update = int_pulse_mode_ppm(step_zero,false); break;
    case PULSE_MODE_PPMA:          step_update = int_pulse_mode_ppma();               break;
    case PULSE_MODE_PPMI:          step_update = int_pulse_mode_ppm(step_zero,true);  break;
    #endif
    case PULSE_MODE_OFF:default:   pf_data.pulse_data = PULSE_DATA_OFF;               break;
  }
    
  // Send data to output with output filtering code.
  int_send_output(pf_data.pulse_data);
  
  // only update pulse_step when needed
  if (step_update==false) {
    return;
  }
  
  // speed up after each step 
  if (pf_conf.pwm_loop_delta > ZERO && pf_data.pwm_loop_max > pf_conf.pwm_loop_delta) {
    pf_data.pwm_loop_max = pf_data.pwm_loop_max-pf_conf.pwm_loop_delta;
  }

  // check for output rotation after last step
  if (pf_data.pulse_step  >= pf_conf.pulse_steps - ONE) {
    pf_data.pulse_step     = ZERO;                    // goto step zero
    pf_data.pulse_trig_delay_cnt = pf_conf.pulse_trig_delay; // reload trig timer
    pf_data.pulse_bank_cnt = pf_conf.pulse_bank;      // load pulse bank after pulse
    if (pf_conf.pulse_dir    == PULSE_DIR_LRRL) {
      pf_data.pwm_loop_cnt = pf_data.pwm_loop_max;    // skip wait after reversal
      if (pf_data.pulse_dir_cnt  == PULSE_DIR_LR) {        
        pf_data.pulse_dir_cnt     = PULSE_DIR_RL; } else { // Auto direction reversal
        pf_data.pulse_dir_cnt     = PULSE_DIR_LR;
      }
    }
    if (pf_conf.pulse_trig != PULSE_TRIG_LOOP) {
      pf_data.pwm_state = PWM_STATE_TRIGGER_END;      // timeout after trigger
    } else if (pf_conf.pulse_post_delay > ZERO) {
      pf_data.pwm_state = PWM_STATE_WAIT_POST;        // timeout after pulse train
    } 
  } else {
    pf_data.pulse_step++;                             // Goto next step in pulse fire train
  }
}

// Timer interrupt for step off time
ISR(TIMER1_COMPB_vect) {
  if (pf_data.pwm_state != PWM_STATE_STEP_DUTY) {
    return;
  }
  // time step with counter
  pf_data.pwm_loop_cnt++;
  if (pf_data.pwm_loop_cnt < pf_data.pwm_loop_max) {
    return;
  }
  
  pf_data.pwm_state = PWM_STATE_STEP_DUTY_DONE;
  pf_data.pwm_loop_cnt = pf_data.pwm_loop_max; // skip normal wait, so we can go to next step
  int_do_work();
}

// Timer interrupt for step on time
ISR(TIMER1_COMPA_vect) {
  int_do_work();
}



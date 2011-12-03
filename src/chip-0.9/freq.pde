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

#ifdef SF_ENABLE_FRQ
uint8_t convert_clock(uint8_t clockScaleMode) {
  int clockScale = 1;
  switch (clockScaleMode) {
    case 1:  clockScale = 1;    break;
    case 2:  clockScale = 8;    break;
    case 3:  clockScale = 64;   break;
    case 4:  clockScale = 256;  break;
    case 5:  clockScale = 1024; break;
    default: clockScale = 1;    break;
  }
  return clockScale;
}
uint32_t calc_pwm_speed(uint8_t idx) {
  uint8_t clockScaleMode = TCCR1B; // todo mask 3 bit
  uint8_t clockScale = convert_clock(clockScaleMode);
  uint32_t freqTrain = F_CPU / clockScale / (pf_conf.pwm_on_cnt_a[idx]+pf_conf.pwm_on_cnt_a[idx]);
  return freqTrain;
}
uint32_t calc_pwm_loop(uint8_t idx) {
  return (calc_pwm_speed(idx) * FREQ_MUL) / pf_conf.pwm_loop;
}
uint32_t calc_pwm_freq(uint8_t idx) {
  uint8_t outs = pf_conf.pulse_steps;
  if (pf_conf.pulse_mode == PULSE_MODE_FLASH) {
    outs = ONE;
  }
  return calc_pwm_loop(idx) / outs;
}


const uint8_t  CLK_SCALE_SIZE = 5;
const uint16_t CLK_SCALE[CLK_SCALE_SIZE] = {1,8,64,256,1024};
void cmd_request_freq_train(uint32_t freq,uint8_t idx,uint8_t duty) {
  // note freq is in 10 so 1123 = 112.3 Hz !!
  freq *= 2; // double to hz.
  //freq *= pf_conf.pulse_steps; // multiply to one output.
  
  uint8_t pwmLoopIdx = Vars_getIndexFromName(UNPSTR(pmConfPWMLoop));
  uint8_t pwmClockIdx = Vars_getIndexFromName(UNPSTR(pmConfPWMClock));
  uint8_t pwmOnCntIdx = Vars_getIndexFromName(UNPSTR(pmConfPWMOnCntA));
  uint8_t pwmOffCntIdx = Vars_getIndexFromName(UNPSTR(pmConfPWMOffCntA));
  
  
  // use pwm_loop to divede by 10 and make bigger so _delta works nice.  
  uint16_t pwmLoop = FREQ_MUL * pf_conf.pulse_steps;
  // freq to low for prescale+tcnt so must use higher train_loop
  if ( F_CPU/1024/freq > 0xFF00)       { pwmLoop *= 2;
    if ( F_CPU/1024/freq/2 > 0xFF00)   { pwmLoop *= 2;
      if ( F_CPU/1024/freq/4 > 0xFF00) { pwmLoop *= 2; }
    }
  }
  pf_var_value_set(pwmLoopIdx,0,0,pwmLoop);
  
  // Search for best clock divider
  for (uint8_t i = 0; i < CLK_SCALE_SIZE; i++) {
    uint32_t tcntDivCalc = F_CPU/CLK_SCALE[i]/freq;
    if (tcntDivCalc < 0xFF00) {
      pf_var_value_set(pwmClockIdx,0,0,i+ONE); // update pwm_clock
      break;
    }
  }
  
  // Calc compa and set for index or set all
  uint16_t compaValue = F_CPU/CLK_SCALE[pf_conf.pwm_clock-ONE]/freq;
  if (idx == QMAP_VAR_IDX_ALL) {
    for (uint8_t i=ZERO;i < OUTPUT_MAX;i++) {
      pf_var_value_set(pwmOnCntIdx,i,0,compaValue);
    }
  } else {
    pf_var_value_set(pwmOnCntIdx,idx,0,compaValue);
  }

  // Get argu duty or global and calc compb value and set for index or all.  
  if (duty == QMAP_VAR_IDX_ALL) {
    duty = pf_conf.pwm_duty;
  }
  uint16_t compbValue = (compaValue / 100) * duty;
  if (compaValue < 1000) {
    compbValue = (compaValue * duty) / 100; // reverse calc for more persision in high range
  }
  if (idx == QMAP_VAR_IDX_ALL) {
    for (uint8_t i=ZERO;i < OUTPUT_MAX;i++) {
      pf_var_value_set(pwmOffCntIdx,i,0,compbValue);
    }
  } else {
    pf_var_value_set(pwmOffCntIdx,idx,0,compbValue);
  }
}

void loop_freq(void) {
  if (pf_conf.avr_pin2_map == PIN2_FREQ_IN | pf_conf.avr_pin3_map == PIN3_FREQ_IN) {
    uint32_t current_time = millis();
    if (current_time < pf_data.dev_freq_time_cnt) {
      return;
    }
    
    pf_var_value_set(Vars_getIndexFromName(UNPSTR(pmDataDevFreq)),ZERO,ZERO,pf_data.dev_freq_cnt);
    
    pf_data.dev_freq_time_cnt = current_time + 1000; // check every second
    pf_data.dev_freq_cnt = ZERO;
  }
}

#endif



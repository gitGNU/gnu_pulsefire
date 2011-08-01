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

// todo: - rs (F_CPU + UART_BAUD_RATE * 8L) / (UART_BAUD_RATE * 16L) - 1)

// short cuts to make code a bit smaller
void Serial_print_P(const char* argu) {
  Serial.print(UNPSTR(argu));
}

void Serial_println_done_P(const char* argu) {
  Serial_print_P(argu);
  Serial_print_P(pmSetSpaced);
  Serial_print_P(pmDone);
  Serial.println();
}

void Serial_println_get_P2(const char* argu0,const char* argu1) {
  Serial_print_P(argu0);
  Serial_print_P(pmGetSpaced);
  Serial_print_P(argu1);
  Serial.println();
}

void cmd_print_var_indexed(uint8_t i,uint8_t setIndexA) { 
  Serial.print(UNPSTRA(&PF_VARS[i][PFVF_NAME]));
  if(setIndexA<10) {Serial.print('0');}
  Serial.print((int)setIndexA);
  Serial_print_P(pmGetSpaced);
  if (Vars_isBitSize32(i)) {
    Serial.print(Vars_getValue32(i,setIndexA));
  } else {
    if (Vars_isIndexB(i)==false)  {
      Serial.print(Vars_getValue(i,setIndexA,ZERO));
    } else {
      for (int idxB=ZERO;idxB<Vars_getIndexBMax(i);idxB++) {
        uint16_t value = Vars_getValue(i,setIndexA,idxB);
        Serial.print(value);
        Serial.print(" ");
      }
    }
  }
  Serial.println();
}

// Print a PF_VARS variable
void cmd_print_var(uint8_t i,boolean limit_to_steps,boolean isSet) {
  boolean indexed = Vars_isIndexA(i);
  if (indexed==false) {
    Serial.print(UNPSTRA(&PF_VARS[i][PFVF_NAME]));
    if (isSet) {
      Serial_print_P(pmSetSpaced);
    } else {
      Serial_print_P(pmGetSpaced);
    }
    if (Vars_isBitSize32(i)) {
      Serial.print(Vars_getValue32(i,0));
    } else {
      Serial.print(Vars_getValue(i,0,0));
    }
    Serial.println();
  } else {
    int limit_index_max = Vars_getIndexAMax(i);
    if (limit_to_steps && Vars_isNolimit(i)==false) {
      limit_index_max = pf_conf.pulse_steps; // limit output of indexed variabled up to configed steps, except nolimit Vars_
    }
    for (uint8_t idxA=ZERO;idxA<limit_index_max;idxA++) {
      cmd_print_var_indexed(i,idxA);
    }
  }
}

// Print long value
void cmd_print_info_value_long(const char* dstring,uint32_t value) {
  Serial_print_P(dstring);
  Serial_print_P(pmGetSpaced);
  Serial.print(value);
  Serial.println();
}

// Print all calculated values
#ifdef SF_ENABLE_FRQ
void cmd_print_info_freq(void) {  
  for (uint8_t i=ZERO;i<pf_conf.pulse_steps;i++) {
    Serial_print_P(pmFreqPWMData);
    if(i<10) {Serial.print('0');}
    Serial.print((int)i);
    Serial_print_P(pmGetSpaced);
    Serial.print(calc_pwm_freq(i));
    Serial.print(' ');
    Serial.print(calc_pwm_loop(i));
    Serial.print(' ');
    Serial.print(calc_pwm_speed(i));
    Serial.print(' ');
    Serial.println();
  }
  Serial_println_done_P(pmCmdInfoFreq);
}
#endif

void cmd_print_info_chip(void) {
  
  Serial_print_P(pmChipVersion);
  Serial_print_P(pmGetSpaced);
  Serial.print(PULSE_FIRE_VERSION/10 % 10);
  Serial.print('.');
  Serial.print(PULSE_FIRE_VERSION % 10);
  Serial.println();
  
  Serial_print_P(pmChipConfMax);
  Serial_print_P(pmGetSpaced);  
  #if (__AVR_ATmega1280__ || __AVR_ATmega2560__)
    Serial.print(4096);
  #elif __AVR_ATmega328P__
    Serial.print(1024);
  #else
    Serial.print(512);
  #endif
  Serial.println();
  
  uint32_t free_ram = ZERO;
  uint8_t *p; 
  uint16_t i;     
  for (i=RAMEND;i>0x100;i--) { 
    p = (uint8_t *) i; 
    if (*p == 0x5A) {
      free_ram++;
    }
  }
  cmd_print_info_value_long(pmChipConfSize,        sizeof(pf_conf_struct));
  cmd_print_info_value_long(pmChipFreeSram,        free_ram);
  cmd_print_info_value_long(pmChipCPUFreq,         F_CPU);
  Serial_println_get_P2(pmChipCPUType, pmChipCPUTypeAVR);
  Serial_println_get_P2(pmChipName,    pmChipNameStr);
  Serial_println_get_P2(pmChipNameId,  pmChipNameIdStr);  
  Serial_println_get_P2(pmChipBuild,   pmChipBuildDate);
  
  Serial_print_P(pmChipFlags);
  Serial_print_P(pmGetSpaced);
  #ifdef SF_ENABLE_LCD
    Serial_print_P(pmChipFlagLCD);
  #endif
  #ifdef SF_ENABLE_LPM
    Serial_print_P(pmChipFlagLPM);
  #endif
  #ifdef SF_ENABLE_PPM
    Serial_print_P(pmChipFlagPPM);
  #endif 
  #ifdef SF_ENABLE_ADC
    Serial_print_P(pmChipFlagADC);
  #endif
  #ifdef SF_ENABLE_DIC
    Serial_print_P(pmChipFlagDIC);
  #endif
  #ifdef SF_ENABLE_DOC
    Serial_print_P(pmChipFlagDOC);
  #endif
  #ifdef SF_ENABLE_DEV
    Serial_print_P(pmChipFlagDEV);
  #endif
  #ifdef SF_ENABLE_STV
    Serial_print_P(pmChipFlagSTV);
  #endif
  #ifdef SF_ENABLE_PTC
    Serial_print_P(pmChipFlagPTC);
  #endif
  #ifdef SF_ENABLE_PTT
    Serial_print_P(pmChipFlagPTT);
  #endif
  #ifdef SF_ENABLE_FRQ
    Serial_print_P(pmChipFlagFRQ);
  #endif
  #ifdef SF_ENABLE_SWC
    Serial_print_P(pmChipFlagSWC);
  #endif
  #ifdef SF_ENABLE_VFC
    Serial_print_P(pmChipFlagVFC);
  #endif
  #ifdef SF_ENABLE_MAL
    Serial_print_P(pmChipFlagMAL);
  #endif  
  #ifdef SF_ENABLE_DEBUG
    Serial_print_P(pmChipFlagDEBUG);
  #endif
  Serial_print_P(pmChipCPUTypeAVR); // also print cpu type for avr_pinX_map module.(=last because of no space after string)
  Serial.println();
  Serial_println_done_P(pmCmdInfoChip);
}

void cmd_print_help(uint8_t type) {
  for (uint8_t i=ZERO;i < PF_VARS_SIZE;i++) {
    if (type==0 && Vars_isNomap(i)==true) {
      continue; // only show mappable
    }
    if (type==2 && Vars_isIndexA(i)==false) {
      continue; // onyl show with indexA
    }
    if (type==0) {
      Serial_print_P(pmCmdHelpMap);
    } else if (type==1) {
      Serial_print_P(pmCmdHelpMax);
    } else if (type==2) {
      Serial_print_P(pmCmdHelpIdx);
    } else if (type==3) {
      Serial_print_P(pmCmdHelpBits);
    }
    Serial.print('.');
    Serial.print(UNPSTRA(&PF_VARS[i][PFVF_NAME]));
    Serial_print_P(pmGetSpaced);
    if (type==0) {
      Serial.print((int)i);
      if (Vars_isTrigger(i)) {
        Serial.print(' ');
        Serial.print(ONE);
      }
    } else if (type==1) {
      uint16_t value_max = pgm_read_word(&(PF_VARS[i][PFVF_MAX]));
      Serial.print(value_max);
    } else if (type==2) {
      Serial.print((int)Vars_getIndexAMax(i));
      if (Vars_isIndexB(i)) {
        Serial.print(' ');
        Serial.print((int)Vars_getIndexBMax(i));
      }
    } else if (type==3) {
      Serial.print(pgm_read_word(&(PF_VARS[i][PFVF_BITS])));
    }
    Serial.println();
  }
}

// execute cmd with the supplied argument
void cmd_execute(char* cmd, char** args) { 
  if ( strcmp_P(cmd,pmCmdHelp) == ZERO ) {
    if (pf_prog.req_tx_promt == ONE) {
      Serial_print_P(pmCmdHelpStart);
    }
    if (args[0] == NULL) {
      for (uint8_t i=ZERO;i < PMCMDLIST_SIZE;i++) {
        // Remove unsupported cmds
        #ifndef SF_ENABLE_PPM
          if ( pgm_read_word(&pmCmdList[i]) == (uint16_t)&pmCmdInfoPPM) { continue; }
        #endif
        #ifndef SF_ENABLE_FRQ
          if ( pgm_read_word(&pmCmdList[i]) == (uint16_t)&pmCmdInfoFreq) { continue; }
          if ( pgm_read_word(&pmCmdList[i]) == (uint16_t)&pmCmdReqPWMFreq) { continue; }
        #endif
        #ifndef SF_ENABLE_LPM
          if ( pgm_read_word(&pmCmdList[i]) == (uint16_t)&pmCmdReqAutoLPM) { continue; }
        #endif
        #ifndef SF_ENABLE_MAL
          if ( pgm_read_word(&pmCmdList[i]) == (uint16_t)&pmConfMALProgram) { continue; }
        #endif
        Serial.print(UNPSTRA((const uint16_t*)&pmCmdList[i]));
        Serial.println();
      }
      for (uint8_t i=ZERO;i < PF_VARS_SIZE;i++) {
        if (Vars_isTypeConf(i)==false) {
          continue;
        }
        Serial.print(UNPSTRA(&PF_VARS[i][PFVF_NAME]));
        Serial.println();
      }
    } else {
      if ( strcmp_P(args[0],pmCmdHelpMap) == ZERO ) {
        cmd_print_help(0);
      } else if ( strcmp_P(args[0],pmCmdHelpMax) == ZERO ) {
        cmd_print_help(1);
      } else if ( strcmp_P(args[0],pmCmdHelpIdx) == ZERO ) {
        cmd_print_help(2);
      } else if ( strcmp_P(args[0],pmCmdHelpBits) == ZERO ) {
        cmd_print_help(3);
      } else {
        Serial_print_P(pmCmdUnknown);
        Serial.println();
      }
    }
    Serial_println_done_P(pmCmdHelp); // end all multi line output with <cmd>=done for parser

  } else if (strcmp_P(cmd,pmCmdInfoConf) == ZERO) {
    for (uint8_t i=ZERO;i < PF_VARS_SIZE;i++) {
      if (Vars_isTypeConf(i) == false) {
        continue;
      }
      cmd_print_var(i,args[0] == NULL,false);
    }
    Serial_println_done_P(pmCmdInfoConf);
  } else if (strcmp_P(cmd,pmCmdInfoData) == ZERO) {
    for (uint8_t i=ZERO;i < PF_VARS_SIZE;i++) {
      if (Vars_isTypeData(i) == false) {
        continue;
      }
      cmd_print_var(i,false,false);
    }
    Serial_println_done_P(pmCmdInfoData);
  } else if (strcmp_P(cmd,pmCmdInfoProg) == ZERO) {
    for (uint8_t i=ZERO;i < PF_VARS_SIZE;i++) {
      if (Vars_isTypeProg(i) == false) {
        continue;
      }
      cmd_print_var(i,false,false);
    }
    Serial_println_done_P(pmCmdInfoProg);
    
  } else if (strcmp_P(cmd,pmCmdInfoChip) == ZERO) {
    cmd_print_info_chip();
    
    #ifdef SF_ENABLE_FRQ
  } else if (strcmp_P(cmd,pmCmdInfoFreq) == ZERO) {
    cmd_print_info_freq();
    #endif
        
    #ifdef SF_ENABLE_PPM
  } else if (strcmp_P(cmd,pmCmdInfoPPM) == ZERO) {
    for (uint8_t i=ZERO;i < OUTPUT_MAX;i++) {
      Serial_print_P(pmConfPPMDataA);
      if (i <= 9) { Serial.print('0'); }
      Serial.print((int)i);
      Serial_print_P(pmGetSpaced);
      uint16_t data_row = pf_conf.ppm_data_a[i];
      for (int i=OUTPUT_MAX-ONE;i>=ZERO;i-- ) {
        uint16_t out = (data_row >> i) & ONE;
        if (out == ZERO) {
          Serial.print('0');
        } else {
          Serial.print('1');
        }
      }
      Serial.println();
      Serial_print_P(pmConfPPMDataB);
      if (i <= 9) { Serial.print('0'); }
      Serial.print((int)i);
      Serial_print_P(pmGetSpaced);
      data_row = pf_conf.ppm_data_b[i];
      for (int i=OUTPUT_MAX-ONE;i>=ZERO;i-- ) {
        uint16_t out = (data_row >> i) & ONE;
        if (out == ZERO) {
          Serial.print('0');
        } else {
          Serial.print('1');
        }
      }
      Serial.println();
    }
    Serial_println_done_P(pmCmdInfoPPM);
    #endif
    
  } else if (strcmp_P(cmd,pmCmdResetConfig) == ZERO) {
    reset_config();
    reset_data();
    Serial_println_done_P(pmCmdResetConfig);
  } else if (strcmp_P(cmd,pmCmdResetData) == ZERO) {
    reset_data();
    Serial_println_done_P(pmCmdResetData);
  } else if (strcmp_P(cmd,pmCmdResetChip) == ZERO) {
    Serial_println_done_P(pmCmdResetChip);
    wdt_enable(WDTO_15MS); // reboot in 15ms.
    _delay_ms(30);
  } else if (strcmp_P(cmd,pmCmdSave) == ZERO) {
    Vars_writeConfig();
    Serial_println_done_P(pmCmdSave);

  } else if (strcmp_P(cmd,pmCmdReqPulseFire) == ZERO) {

    if (pf_conf.pulse_trig == PULSE_TRIG_FIRE) {
      pf_data.pwm_state = PWM_STATE_RUN;
    }
    Serial_println_done_P(pmCmdInfoProg);
    
    #ifdef SF_ENABLE_FRQ
  } else if (strcmp_P(cmd,pmCmdReqPWMFreq) == ZERO) {
    if (args[0] == NULL) {
      Serial_print_P(pmCmdReqPWMFreq);
      Serial_print_P(pmGetSpaced);
      Serial.print(calc_pwm_freq(ZERO));
      Serial.println();
      return;
    } 
    uint16_t freqFull  = atou16(args[0]);
    uint16_t idx = 0xFF;
    uint16_t duty = 0xFF;
    if (args[1] != NULL) { idx  = atou16(args[1]); }
    if (args[2] != NULL) { duty = atou16(args[2]); }
    cmd_request_freq_train(freqFull,idx,duty);
    Serial_print_P(pmCmdReqPWMFreq);
    Serial_print_P(pmSetSpaced);
    Serial.print(freqFull);
    Serial_print_P(pmSetSpaced);
    Serial.print(calc_pwm_freq(ZERO));
    Serial.println();
    #endif
    
    #ifdef SF_ENABLE_LPM
  } else if (strcmp_P(cmd,pmCmdReqAutoLPM) == ZERO) {
    Serial_print_P(pmCmdReqAutoLPM);
    Serial_print_P(pmGetSpaced);
    if (pf_conf.lpm_size > ZERO) {
      if (pf_data.lpm_state==LPM_IDLE) {
        pf_data.lpm_state = LPM_INIT;
        pf_data.lpm_auto_cmd = ONE;
        Serial_print_P(pmLPMStart);
      }
      if (pf_data.lpm_state==LPM_IDLE) {
        pf_data.lpm_state = LPM_STOP;
        pf_data.lpm_auto_cmd = ZERO;
        Serial_print_P(pmLPMCancel);
      }
    } else {
      Serial.print(ZERO);
    }
    Serial.println();
    #endif
 
   } else if (strcmp_P(cmd,pmProgTXPush) == ZERO) {
    Serial_print_P(pmProgTXPush);
    if (args[0] == NULL) {
      Serial_print_P(pmGetSpaced);
    } else {
      Serial_print_P(pmSetSpaced);
      uint16_t push = atou16(args[0]);
      if (push == ZERO) {  pf_prog.req_tx_push = ZERO;
      } else {             pf_prog.req_tx_push = ONE;
      }
    }
    Serial.print((int)pf_prog.req_tx_push);
    Serial.println();
   } else if (strcmp_P(cmd,pmProgTXEcho) == ZERO) {
    Serial_print_P(pmProgTXEcho);
    if (args[0] == NULL) {
      Serial_print_P(pmGetSpaced);
    } else {
      Serial_print_P(pmSetSpaced);
      uint16_t echo = atou16(args[0]);
      if (echo == ZERO) {  pf_prog.req_tx_echo = ZERO;
      } else {             pf_prog.req_tx_echo = ONE;
      }
    }
    Serial.print((int)pf_prog.req_tx_echo);
    Serial.println();
   } else if (strcmp_P(cmd,pmProgTXPromt) == ZERO) {
    Serial_print_P(pmProgTXPromt);
    if (args[0] == NULL) {
      Serial_print_P(pmGetSpaced);
    } else {
      Serial_print_P(pmSetSpaced);
      uint16_t promt = atou16(args[0]);
      if (promt == ZERO) {  pf_prog.req_tx_promt = ZERO;
      } else {              pf_prog.req_tx_promt = ONE;
      }
    }
    Serial.print((int)pf_prog.req_tx_promt);
    Serial.println();
    
    
    #ifdef SF_ENABLE_MAL
  } else if (strcmp_P(cmd,pmConfMALProgram) == ZERO) {
    if (args[0] == NULL) {
      for (uint8_t n=ZERO;n < MAL_PROGRAM_MAX;n++) {
        Serial_print_P(pmConfMALProgram);
        Serial.print((int)n);
        Serial_print_P(pmGetSpaced);
        for (uint8_t i=ZERO;i < MAL_PROGRAM_SIZE;i++) {
          if (pf_conf.mal_program[i][n] < 16) {
            Serial.print('0');
          }
          Serial.print(pf_conf.mal_program[i][n],HEX);
        }
        Serial.println();
      }
    } else {
      uint16_t prog = atou16(args[0]);
      uint16_t base = atou16(args[1]);
      uint32_t res = strtoul(args[2],NULL,16);
      if (prog > MAL_PROGRAM_MAX - ONE) {
        prog = MAL_PROGRAM_MAX - ONE;
      }
      pf_conf.mal_program[base+0][prog] = (uint8_t) (res >> 24) & 0xFF;
      pf_conf.mal_program[base+1][prog] = (uint8_t) (res >> 16) & 0xFF;
      pf_conf.mal_program[base+2][prog] = (uint8_t) (res >> 8 ) & 0xFF;
      pf_conf.mal_program[base+3][prog] = (uint8_t) (res >> 0 ) & 0xFF;
      Serial_print_P(pmConfMALProgram);
      Serial_print_P(pmSetSpaced);
      Serial.print(prog);
      for (uint8_t i=ZERO;i < MAL_PROGRAM_SIZE;i++) {
        if (pf_conf.mal_program[i][prog] < 16) {
          Serial.print('0');
        }
        Serial.print(pf_conf.mal_program[i][prog],HEX);
      }
      Serial.println();
    }
    #endif
    
  } else {
    // process all get/set properties
    boolean done = false;
    for (uint8_t i=ZERO;i < PF_VARS_SIZE;i++) {
      if (Vars_isTypeConf(i)==false) {
        continue;
      }
      if (strcmp(cmd,UNPSTRA(&(PF_VARS[i][PFVF_NAME]))) != ZERO) {
        continue;
      }
      done = true;
      
      if (args[0] == NULL) {
        cmd_print_var(i,false,false);
        break; // print get and done
      }
      
      if (Vars_isIndexA(i)==false) {
          pf_var_value_set_serial(i,ZERO,ZERO,atou16(args[0]));
      } else {
        if (Vars_isIndexB(i)==false) {
          if (args[1] == NULL) {
            for (uint8_t ii=ZERO;ii < Vars_getIndexAMax(i);ii++) {
              pf_var_value_set_serial(i,ii,ZERO,atou16(args[0]));
            }
            
          } else {
            pf_var_value_set_serial(i,atou16(args[1]),ZERO,atou16(args[0]));
          }
        } else {
          
          int bIdxSize = Vars_getIndexBMax(i);
          if (bIdxSize == 4 ) {
            uint16_t idxA         = atou16(args[0]);
            uint16_t qmap_to      = ZERO;
            if (args[1][0] <= '9') {
              qmap_to = atou16(args[1]);
            } else {
              qmap_to = Vars_getIndexFromName(args[1]);
            }
            uint16_t qmap_value_a = ZERO;
            uint16_t qmap_value_b = QMAP_VAR_NONE;
            uint16_t qmap_idx     = QMAP_VAR_IDX_ALL;
            if (Vars_isNomap(qmap_to)) { qmap_to = QMAP_VAR_NONE; }
            if (args[2] != NULL) { qmap_value_a     = atou16(args[2]); }
            if (args[3] != NULL) { qmap_value_b     = atou16(args[3]); }
            if (args[4] != NULL) { qmap_idx         = atou16(args[4]); }
            if (Vars_isIndexA(qmap_to)==false) {
              qmap_idx = QMAP_VAR_IDX_ALL;
            } else if (qmap_idx > Vars_getIndexAMax(qmap_to)) {
              qmap_idx = QMAP_VAR_IDX_ALL;
            }
            pf_var_value_set_serial(i,idxA,0,qmap_to);
            pf_var_value_set_serial(i,idxA,1,qmap_value_a);
            pf_var_value_set_serial(i,idxA,2,qmap_value_b);
            pf_var_value_set_serial(i,idxA,3,qmap_idx);
          } else {
            // other sizes unsupported currently
          }
        }
      }
      if (Vars_isIndexA(i)==false) {
        cmd_print_var(i,false,true); // Print set of value
      } else {
        if (Vars_isIndexB(i)) {
          cmd_print_var_indexed(i,atou16(args[0]));
        } else {
          cmd_print_var_indexed(i,atou16(args[1]));
        }
      }
      
      break; // we can only exec 1 at the time
    }
    if (done == false) {
      Serial_print_P(pmCmdUnknown);
      Serial.println();
    }
  }
}

// Parse the cmd from the cmd_buff
void cmd_parse(void) {
  uint8_t idx = ZERO;
  char *cmd, *ptr, *args[CMD_MAX_ARGS];
  if (strtok((char *)pf_prog.cmd_buff,CMD_WHITE_SPACE) == NULL) {
    Serial_print_P(pmPromt);
    return; // no command given so just print new promt.
  }
  cmd = (char *)pf_prog.cmd_buff;
  while( (ptr = strtok(NULL,CMD_WHITE_SPACE)) != NULL) {
    args[idx] = ptr;
    if (++idx == (CMD_MAX_ARGS - ONE)) {
      break;
    }
  }
  args[idx] = NULL;
  cmd_execute(cmd,args);
  if (pf_prog.req_tx_promt == ONE) {
    Serial_print_P(pmPromt);
  }
}

// Check for data from console and put in cmd_buff
void loop_serial(void) {
  int readBytes = Serial.available();
  if (readBytes==ZERO) {
    return;
  }
  boolean cmd_process = false;
  for (int b=ZERO;b < readBytes;b++) {
    int c = Serial.read();
    if (pf_prog.req_tx_echo == ONE) {
      Serial.write(c);
    }
    if (pf_prog.cmd_buff_idx > CMD_BUFF_SIZE) {
      pf_prog.cmd_buff_idx = ZERO; // protect against to long input
    }
    if (c=='\b') {
      pf_prog.cmd_buff[pf_prog.cmd_buff_idx] = '\0';// backspace
      pf_prog.cmd_buff_idx--;
      if (pf_prog.req_tx_echo == ONE) {
        Serial.write(' ');
        Serial.write(c);
      }
    } else if (c=='\n') {
      pf_prog.cmd_buff[pf_prog.cmd_buff_idx] = '\0';// newline
      pf_prog.cmd_buff_idx = ZERO;
      cmd_process  = true;
    } else {
      pf_prog.cmd_buff[pf_prog.cmd_buff_idx] = c;   // store in buffer
      pf_prog.cmd_buff_idx++;
    }
  }
  if (cmd_process == true) {
     cmd_parse();
  }
}

void setup_serial(void) {
  Serial.begin(SERIAL_SPEED);
  Serial.print('\n');
  Serial_print_P(pmPromt);
}

// Prototype and function for specific c init location.
void SRAM_init(void) __attribute__((naked)) __attribute__ ((section (".init1"))); 
void SRAM_init(void) { 
  uint8_t *p; // Break into the C startup so I can clear SRAM to 
  uint16_t i; // known values making it easier to see how it is used 
  for (i=0x100; i < RAMEND; i++) { 
    p = (uint8_t *)i; *p = 0x5A; 
  } 
}



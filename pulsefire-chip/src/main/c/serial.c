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


#include "serial.h"



void Serial_printHex(int argu) {
}

void Serial_printDec(int argu) {
	u16toa(argu,pf_prog.unpstr_buff);
	Serial_printChar(pf_prog.unpstr_buff);
}
void Serial_print(char argu) {
	Serial_write(argu);
}
void Serial_printChar(char* dstring) {
	while(*dstring != 0) {
		Serial_write(*dstring);
		dstring++;
	}
}
void Serial_println(void) {
  Serial_print('\n');
}

// short cuts to make code a bit smaller
void Serial_printCharP(const char* argu) {
  Serial_printChar(UNPSTR(argu));
}

void Serial_println_done_P(const char* argu) {
  Serial_printCharP(argu);
  Serial_printCharP(pmSetSpaced);
  Serial_printCharP(pmDone);
  Serial_println();
}

void Serial_println_get_P2(const char* argu0,const char* argu1) {
  Serial_printCharP(argu0);
  Serial_printCharP(pmGetSpaced);
  Serial_printCharP(argu1);
  Serial_println();
}

void cmd_print_var_indexed(uint8_t i,uint8_t setIndexA) {
  if (setIndexA>=Vars_getIndexAMax(i)) {
    setIndexA = ZERO; // safty check for indexes
  }
  Serial_printChar(Vars_getName(i));
  if(setIndexA<10) {Serial_print('0');}
  Serial_printDec((int)setIndexA);
  Serial_printCharP(pmGetSpaced);
  if (Vars_isBitSize32(i)) {
    Serial_print(Vars_getValue32(i,setIndexA));
  } else {
    if (Vars_isIndexB(i)==false)  {
      Serial_printDec(Vars_getValue(i,setIndexA,ZERO));
    } else {
      int idxB=ZERO;
      for (idxB=ZERO;idxB<Vars_getIndexBMax(i);idxB++) {
        uint16_t value = Vars_getValue(i,setIndexA,idxB);
        Serial_printDec(value);
        Serial_print(' ');
      }
    }
  }
  Serial_println();
}

// Print a PF_VARS variable
void cmd_print_var(uint8_t i,boolean limit_to_steps,boolean isSet) {
  boolean indexedA = Vars_isIndexA(i);
  if (indexedA==false) {
    Serial_printChar(Vars_getName(i));
    if (isSet) {
      Serial_printCharP(pmSetSpaced);
    } else {
      Serial_printCharP(pmGetSpaced);
    }
    if (Vars_isBitSize32(i)) {
      Serial_printDec(Vars_getValue32(i,0));
    } else {
      Serial_printDec(Vars_getValue(i,0,0));
    }
    Serial_println();
  } else {
    int limit_index_max = Vars_getIndexAMax(i);
    if (limit_to_steps && Vars_isNolimit(i)==false) {
      #ifdef SF_ENABLE_PWM
        limit_index_max = pf_conf.pulse_steps; // limit output of indexed variabled up to configed steps, except nolimit Vars_
      #endif
    }
    uint8_t idxA=ZERO;
    for (idxA=ZERO;idxA<limit_index_max;idxA++) {
      cmd_print_var_indexed(i,idxA);
    }
  }
}

// Print long value
void cmd_print_info_value_long(const char* dstring,uint32_t value) {
  Serial_printCharP(dstring);
  Serial_printCharP(pmGetSpaced);
  u32toa(value,pf_prog.unpstr_buff);
  Serial_printChar(pf_prog.unpstr_buff);
  Serial_println();
}

// Print all calculated values
#ifdef SF_ENABLE_FRQ
void cmd_print_info_freq(void) {  
  #ifdef SF_ENABLE_PWM
  for (uint8_t i=ZERO;i<pf_conf.pulse_steps;i++) {
    Serial_printCharP(pmFreqPWMData);
    if(i<10) {Serial_print('0');}
    Serial_printDec((int)i);
    Serial_printCharP(pmGetSpaced);
    Serial_printDec(calc_pwm_freq(i));
    Serial_print(' ');
    Serial_printDec(calc_pwm_loop(i));
    Serial_print(' ');
    Serial_printDec(calc_pwm_speed(i));
    Serial_print(' ');
    Serial_println();
  }
  #endif
  Serial_println_done_P(pmCmdInfoFreq);
}
#endif


// Prototype and function for specific c init location.
void SRAM_init(void) __attribute__((naked)) __attribute__ ((section (".init1"))); 
void SRAM_init(void) { 
  uint8_t *p; // Break into the C startup so I can clear SRAM to 
  uint16_t i; // known values making it easier to see how it is used 
  for (i=0x100; i < RAMEND; i++) { 
    p = (uint8_t *)i; *p = 0x5A; 
  } 
}

void cmd_print_info_chip(void) {
  
  Serial_printCharP(pmChipVersion);
  Serial_printCharP(pmGetSpaced);
  Serial_printDec(PULSE_FIRE_VERSION/10 % 10);
  Serial_print('.');
  Serial_printDec(PULSE_FIRE_VERSION % 10);
  Serial_println();
  
  Serial_printCharP(pmChipConfMax);
  Serial_printCharP(pmGetSpaced);  
  Serial_printDec(CHIP_EEPROM_SIZE);
  Serial_println();
  
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
  
  Serial_printCharP(pmChipFlags);
  Serial_printCharP(pmGetSpaced);
  #ifdef SF_ENABLE_PWM
    Serial_printCharP(pmChipFlagPWM);
  #endif
  #ifdef SF_ENABLE_LCD
    Serial_printCharP(pmChipFlagLCD);
  #endif
  #ifdef SF_ENABLE_LPM
    Serial_printCharP(pmChipFlagLPM);
  #endif
  #ifdef SF_ENABLE_PPM
    Serial_printCharP(pmChipFlagPPM);
  #endif 
  #ifdef SF_ENABLE_ADC
    Serial_printCharP(pmChipFlagADC);
  #endif
  #ifdef SF_ENABLE_DIC
    Serial_printCharP(pmChipFlagDIC);
  #endif
  #ifdef SF_ENABLE_DOC
    Serial_printCharP(pmChipFlagDOC);
  #endif
  #ifdef SF_ENABLE_DEV
    Serial_printCharP(pmChipFlagDEV);
  #endif
  #ifdef SF_ENABLE_STV
    Serial_printCharP(pmChipFlagSTV);
  #endif
  #ifdef SF_ENABLE_PTC
    Serial_printCharP(pmChipFlagPTC);
  #endif
  #ifdef SF_ENABLE_PTT
    Serial_printCharP(pmChipFlagPTT);
  #endif
  #ifdef SF_ENABLE_FRQ
    Serial_printCharP(pmChipFlagFRQ);
  #endif
  #ifdef SF_ENABLE_SWC
    Serial_printCharP(pmChipFlagSWC);
  #endif
  #ifdef SF_ENABLE_VFC
    Serial_printCharP(pmChipFlagVFC);
  #endif
  #ifdef SF_ENABLE_MAL
    Serial_printCharP(pmChipFlagMAL);
  #endif  
  #ifdef SF_ENABLE_DEBUG
    Serial_printCharP(pmChipFlagDEBUG);
  #endif
  Serial_printCharP(pmChipCPUTypeAVR); // also print cpu type for avr_pinX_map module.(=last because of no space after string)
  Serial_println();
  Serial_println_done_P(pmCmdInfoChip);
}

void cmd_print_help(uint8_t type) {
  uint8_t i=ZERO;
  for (i=ZERO;i < PF_VARS_SIZE;i++) {
    if (type==0 && Vars_isNomap(i)==true) {
      continue; // only show mappable
    }
    if (type==2 && Vars_isIndexA(i)==false) {
      continue; // onyl show with indexA
    }
    if (type==0) {
      Serial_printCharP(pmCmdHelpMap);
    } else if (type==1) {
      Serial_printCharP(pmCmdHelpMax);
    } else if (type==2) {
      Serial_printCharP(pmCmdHelpIdx);
    } else if (type==3) {
      Serial_printCharP(pmCmdHelpBits);
    }
    Serial_print('.');
    Serial_printChar(Vars_getName(i));
    Serial_printCharP(pmGetSpaced);
    if (type==0) {
      Serial_printDec((int)i);
      if (Vars_isTrigger(i)) {
        Serial_print(' ');
        Serial_printDec(ONE);
      }
    } else if (type==1) {
      uint16_t value_max = Vars_getValueMax(i);
      Serial_printDec(value_max);
    } else if (type==2) {
      Serial_printDec((int)Vars_getIndexAMax(i));
      if (Vars_isIndexB(i)) {
        Serial_print(' ');
        Serial_printDec((int)Vars_getIndexBMax(i));
      }
    } else if (type==3) {
      Serial_printDec(Vars_getBitsRaw(i));
    }
    Serial_println();
  }
}

// execute cmd with the supplied argument
void cmd_execute(char* cmd, char** args) {
  uint8_t i=ZERO;
  if ( strcmp_P(cmd,pmCmdHelp) == ZERO ) {
    if (pf_prog.req_tx_promt == ONE) {
      Serial_printCharP(pmCmdHelpStart);
    }
    if (args[0] == NULL) {
      uint8_t i=ZERO;
      for (i=ZERO;i < PMCMDLIST_SIZE;i++) {
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
        Serial_printChar(UNPSTRA((const uint16_t*)&pmCmdList[i]));
        Serial_println();
      }
      for (i=ZERO;i < PF_VARS_SIZE;i++) {
        if (Vars_isTypeConf(i)==false) {
          continue;
        }
        Serial_printChar(Vars_getName(i));
        Serial_println();
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
        Serial_printCharP(pmCmdUnknown);
        Serial_println();
      }
    }
    Serial_println_done_P(pmCmdHelp); // end all multi line output with <cmd>=done for parser

  } else if (strcmp_P(cmd,pmCmdInfoConf) == ZERO) {
    for (i=ZERO;i < PF_VARS_SIZE;i++) {
      if (Vars_isTypeConf(i) == false) {
        continue;
      }
      cmd_print_var(i,args[0] == NULL,false);
    }
    Serial_println_done_P(pmCmdInfoConf);
  } else if (strcmp_P(cmd,pmCmdInfoData) == ZERO) {
    for (i=ZERO;i < PF_VARS_SIZE;i++) {
      if (Vars_isTypeData(i) == false) {
        continue;
      }
      cmd_print_var(i,false,false);
    }
    Serial_println_done_P(pmCmdInfoData);
  } else if (strcmp_P(cmd,pmCmdInfoProg) == ZERO) {
    for (i=ZERO;i < PF_VARS_SIZE;i++) {
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
    for (i=ZERO;i < OUTPUT_MAX;i++) {
      Serial_printCharP(pmConfPPMDataA);
      if (i <= 9) { Serial_print('0'); }
      Serial_printDec((int)i);
      Serial_printCharP(pmGetSpaced);
      uint16_t data_row = pf_conf.ppm_data_a[i];
      int ii=OUTPUT_MAX-ONE;
      for (ii=OUTPUT_MAX-ONE;ii>=ZERO;ii-- ) {
        uint16_t out = (data_row >> ii) & ONE;
        if (out == ZERO) {
          Serial_print('0');
        } else {
          Serial_print('1');
        }
      }
      Serial_println();
    }
    for (i=ZERO;i < OUTPUT_MAX;i++) {
      Serial_printCharP(pmConfPPMDataB);
      if (i <= 9) { Serial_print('0'); }
      Serial_printDec((int)i);
      Serial_printCharP(pmGetSpaced);
      uint16_t data_row = pf_conf.ppm_data_b[i];
      int ii=OUTPUT_MAX-ONE;
      for (ii=OUTPUT_MAX-ONE;ii>=ZERO;ii-- ) {
        uint16_t out = (data_row >> ii) & ONE;
        if (out == ZERO) {
          Serial_print('0');
        } else {
          Serial_print('1');
        }
      }
      Serial_println();
    }
    Serial_println_done_P(pmCmdInfoPPM);
    #endif
    
  } else if (strcmp_P(cmd,pmCmdResetConfig) == ZERO) {
    Vars_resetConfig();
    Vars_resetData();
    Serial_println_done_P(pmCmdResetConfig);
  } else if (strcmp_P(cmd,pmCmdResetData) == ZERO) {
    Vars_resetData();
    Serial_println_done_P(pmCmdResetData);
  } else if (strcmp_P(cmd,pmCmdResetChip) == ZERO) {
    Serial_println_done_P(pmCmdResetChip);
	Chip_reset();
  } else if (strcmp_P(cmd,pmCmdSave) == ZERO) {
    Vars_writeConfig();
    Serial_println_done_P(pmCmdSave);

    #ifdef SF_ENABLE_PWM
  } else if (strcmp_P(cmd,pmCmdReqPulseFire) == ZERO) {

    if (pf_conf.pulse_trig == PULSE_TRIG_FIRE) {
      pf_data.pwm_state = PWM_STATE_RUN;
    }
    Serial_println_done_P(pmCmdInfoProg);
    #endif
    
    #ifdef SF_ENABLE_FRQ
    #ifdef SF_ENABLE_PWM
  } else if (strcmp_P(cmd,pmCmdReqPWMFreq) == ZERO) {
    if (args[0] == NULL) {
      Serial_printCharP(pmCmdReqPWMFreq);
      Serial_printCharP(pmGetSpaced);
      Serial_printDec(calc_pwm_freq(ZERO));
      Serial_println();
      return;
    } 
    uint16_t freqFull  = atou16(args[0]);
    uint16_t idx = 0xFF;
    uint16_t duty = 0xFF;
    if (args[1] != NULL) { idx  = atou16(args[1]); }
    if (args[2] != NULL) { duty = atou16(args[2]); }
    Freq_requestTrainFreq(freqFull,idx,duty);
    Serial_printCharP(pmCmdReqPWMFreq);
    Serial_printCharP(pmSetSpaced);
    Serial_printDec(freqFull);
    Serial_printCharP(pmSetSpaced);
    Serial_printDec(calc_pwm_freq(ZERO));
    Serial_println();
    #endif
    #endif
    
    #ifdef SF_ENABLE_LPM
  } else if (strcmp_P(cmd,pmCmdReqAutoLPM) == ZERO) {
    Serial_printCharP(pmCmdReqAutoLPM);
    Serial_printCharP(pmGetSpaced);
    if (pf_conf.lpm_size > ZERO) {
      if (pf_data.lpm_state==LPM_IDLE) {
        pf_data.lpm_state = LPM_INIT;
        pf_data.lpm_auto_cmd = ONE;
        Serial_printCharP(pmLPMStart);
      }
      if (pf_data.lpm_state==LPM_IDLE) {
        pf_data.lpm_state = LPM_STOP;
        pf_data.lpm_auto_cmd = ZERO;
        Serial_printCharP(pmLPMCancel);
      }
    } else {
      Serial_printDec(ZERO);
    }
    Serial_println();
    #endif
 
   } else if (strcmp_P(cmd,pmProgTXPush) == ZERO) {
    Serial_printCharP(pmProgTXPush);
    if (args[0] == NULL) {
      Serial_printCharP(pmGetSpaced);
    } else {
      Serial_printCharP(pmSetSpaced);
      uint16_t push = atou16(args[0]);
      if (push == ZERO) {  pf_prog.req_tx_push = ZERO;
      } else {             pf_prog.req_tx_push = ONE;
      }
    }
    Serial_printDec((int)pf_prog.req_tx_push);
    Serial_println();
   } else if (strcmp_P(cmd,pmProgTXEcho) == ZERO) {
    Serial_printCharP(pmProgTXEcho);
    if (args[0] == NULL) {
      Serial_printCharP(pmGetSpaced);
    } else {
      Serial_printCharP(pmSetSpaced);
      uint16_t echo = atou16(args[0]);
      if (echo == ZERO) {  pf_prog.req_tx_echo = ZERO;
      } else {             pf_prog.req_tx_echo = ONE;
      }
    }
    Serial_printDec((int)pf_prog.req_tx_echo);
    Serial_println();
   } else if (strcmp_P(cmd,pmProgTXPromt) == ZERO) {
    Serial_printCharP(pmProgTXPromt);
    if (args[0] == NULL) {
      Serial_printCharP(pmGetSpaced);
    } else {
      Serial_printCharP(pmSetSpaced);
      uint16_t promt = atou16(args[0]);
      if (promt == ZERO) {  pf_prog.req_tx_promt = ZERO;
      } else {              pf_prog.req_tx_promt = ONE;
      }
    }
    Serial_printDec((int)pf_prog.req_tx_promt);
    Serial_println();
    
    
    #ifdef SF_ENABLE_MAL
  } else if (strcmp_P(cmd,pmConfMALProgram) == ZERO) {
    if (args[0] == NULL) {
      uint8_t n=ZERO;
      for (n=ZERO;n < MAL_PROGRAM_MAX;n++) {
        Serial_printCharP(pmConfMALProgram);
        Serial_printDec((int)n);
        Serial_printCharP(pmGetSpaced);
        uint8_t i=ZERO;
        for (i=ZERO;i < MAL_PROGRAM_SIZE;i++) {
          if (pf_conf.mal_program[i][n] < 16) {
            Serial_print('0');
          }
          Serial_printHex(pf_conf.mal_program[i][n]);
        }
        Serial_println();
      }
    } else {
/*
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
      Serial_printCharP(pmConfMALProgram);
      Serial_printCharP(pmSetSpaced);
      Serial_printDec(prog);
      for (i=ZERO;i < MAL_PROGRAM_SIZE;i++) {
        if (pf_conf.mal_program[i][prog] < 16) {
          Serial_print('0');
        }
        Serial_printHex(pf_conf.mal_program[i][prog]);
      }*/
      Serial_println();
    }
    #endif
    
  } else {
    // process all get/set properties
    boolean done = false;
    for (i=ZERO;i < PF_VARS_SIZE;i++) {
      if (Vars_isTypeConf(i)==false) {
        continue;
      }
      if (strcmp(cmd,Vars_getName(i)) != ZERO) {
        continue;
      }
      done = true;
      
      if (args[0] == NULL) {
        cmd_print_var(i,false,false);
        break; // print get and done
      }
      
      if (Vars_isIndexA(i)==false) {
          Vars_setValueSerial(i,ZERO,ZERO,atou16(args[0]));
      } else {
        if (Vars_isIndexB(i)==false) {
          if (args[1] == NULL) {
            uint8_t ii=ZERO;
            for (ii=ZERO;ii < Vars_getIndexAMax(i);ii++) {
              Vars_setValueSerial(i,ii,ZERO,atou16(args[0]));
            }
          } else {
            Vars_setValueSerial(i,atou16(args[1]),ZERO,atou16(args[0]));
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
            Vars_setValueSerial(i,idxA,0,qmap_to);
            Vars_setValueSerial(i,idxA,1,qmap_value_a);
            Vars_setValueSerial(i,idxA,2,qmap_value_b);
            Vars_setValueSerial(i,idxA,3,qmap_idx);
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
          if (args[1] == NULL) {
            uint8_t ii=ZERO;
            for (ii=ZERO;ii < Vars_getIndexAMax(i);ii++) {
              cmd_print_var_indexed(i,ii);
            }
          } else {
            cmd_print_var_indexed(i,atou16(args[1]));
          }
        }
      }
      
      break; // we can only exec 1 at the time
    }
    if (done == false) {
      Serial_printCharP(pmCmdUnknown);
      Serial_println();
    }
  }
}

// Parse the cmd from the cmd_buff
void cmd_parse(void) {
  uint8_t idx = ZERO;
  char *cmd, *ptr, *args[CMD_MAX_ARGS];
  if (strtok((char *)pf_prog.cmd_buff,CMD_WHITE_SPACE) == NULL) {
    Serial_printCharP(pmPromt);
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
    Serial_printCharP(pmPromt);
  }
}

// in mega USART0_RX_vect
ISR(USART_RX_vect) {
	char c = Serial_read();
    if (pf_prog.req_tx_echo == ONE) {
      Serial_write(c);
    }
	if (pf_prog.cmd_process == ZERO) {
		return; // skip serial data ???
	}
    if (pf_prog.cmd_buff_idx > CMD_BUFF_SIZE) {
      pf_prog.cmd_buff_idx = ZERO; // protect against to long input
    }
    if (c=='\b') {
      pf_prog.cmd_buff[pf_prog.cmd_buff_idx] = '\0';// backspace
      pf_prog.cmd_buff_idx--;
      if (pf_prog.req_tx_echo == ONE) {
        Serial_print(' ');
        Serial_print(c);
      }
    } else if (c=='\n') {
      pf_prog.cmd_buff[pf_prog.cmd_buff_idx] = '\0';// newline
      pf_prog.cmd_buff_idx = ZERO;
      pf_prog.cmd_process  = ZERO;
    } else {
      pf_prog.cmd_buff[pf_prog.cmd_buff_idx] = c;   // store in buffer
      pf_prog.cmd_buff_idx++;
    }
}

uint8_t Serial_read(void) {
	while ( !(UCSR0A & (1<<RXC0)) );
	return UDR0;
}

void Serial_write(uint8_t data) {
	while ( !(UCSR0A & (1<<UDRE0)));
	UDR0 = data;
}

// Check for data from console and put in cmd_buff
void Serial_loop(void) {
	if (pf_prog.cmd_process==ZERO) {
		cmd_parse();
		pf_prog.cmd_process=ONE;
	}
}

void Serial_setup(void) {
  pf_prog.cmd_process  = ONE; // rm me

  // initialize UART0
  //UBRR0H = (((F_CPU/SERIAL_SPEED)/16)-1)>>8; 	// set baud rate
  //UBRR0L = (((F_CPU/SERIAL_SPEED)/16)-1);
  UBRR0H = ZERO;
  UBRR0L = 16;        // 115K with double rate enabled else 8.
  UCSR0A = (1<<U2X0); // use double so error rate is only 2.1%.

  UCSR0B = (1<<RXEN0)|(1<<TXEN0)|(1<<RXCIE0);  // enable Rx & Tx
  UCSR0C = (1<<UCSZ00) | (1<<UCSZ01);          // 8n1
 
  // Enable pull-up on D0/RX, to supress line noise
  DDRD &= ~_BV(PIND0);
  PORTD |= _BV(PIND0);

  // delay is needed else we get junk on terminal.
  Chip_delay(100);
  Serial_println();
  Serial_printCharP(pmPromt);
}





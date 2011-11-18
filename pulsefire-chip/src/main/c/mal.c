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

#include "mal.h"

/*
    Micro Assembly Language

def: x0 = analog input value

Example; (note this is ~MAX program size :)
LET pf_conf.pwm_duty=x0;
x0=x0/20;
x3=21845;
if (x0 < 3) then;x3=0x0015;goto LABEL;endif;
if (x0 < 6) then;x3=0x0295;goto LABEL;endif;
if (x0 < 9) then;x3=0x2a99;goto LABEL;endif;
LABEL:
LET pf_conf.ppm_data[0xFF]=x3;

Translates to:
3010
40030014
035555
40330003030015402000..4040
40330006030295402000..4040
40330009032A99402000..4040
3313FF
FFFF

Make to 4 bytes + prog cmd + added 3x FF to fill last bytes
Then calc localtion of label which is the 33 of the 330AFF so .. = 48 = 0x30
root@pulsefire: mal_program 0 00 30104003
root@pulsefire: mal_program 0 04 00140355
root@pulsefire: mal_program 0 08 55403300
root@pulsefire: mal_program 0 12 03030015
root@pulsefire: mal_program 0 16 40200030
root@pulsefire: mal_program 0 20 40404033
root@pulsefire: mal_program 0 24 00060302
root@pulsefire: mal_program 0 28 95402000
root@pulsefire: mal_program 0 32 30404040
root@pulsefire: mal_program 0 36 33000903
root@pulsefire: mal_program 0 40 2A994020
root@pulsefire: mal_program 0 44 00304040
root@pulsefire: mal_program 0 48 3313FFFF
root@pulsefire: mal_program 0 52 FFFFFFFF

Setup env for this example;
root@pulsefire: sys_adc_map 4 mal_trigger 20 250 0
root@pulsefire: pulse_mode 4
root@pulsefire: req_pwm_freq 1200
root@pulsefire: save
root@pulsefire: req_auto_push 1

Now turn the pot on analog input4;

mal_trigger00=106
Exec cmd: 30 vt:3 vi:0 ct:0 argu: 10 set: 16 idxA: 0 to: 106
pwm_duty=106
Exec cmd: 40 vt:0 vi:0 ct:1 ext-cmd: 03 et:0 eo:3 argu: 00 var: 6A value: 14
Exec cmd: 03 vt:0 vi:3 ct:0 argu: 55 var: 0 value: 5555
Exec cmd: 40 vt:0 vi:0 ct:1 ext-cmd: 33 et:3 eo:3 argu: 00 var: 5 value: 3
Exec cmd: 03 vt:0 vi:3 ct:0 argu: 00 skip
Exec cmd: 40 vt:0 vi:0 ct:1 ext-cmd: 20 et:2 eo:0 argu: 00 skip
Exec cmd: 40 vt:0 vi:0 ct:1 ext-cmd: 40 et:4 eo:0 endif
Exec cmd: 40 vt:0 vi:0 ct:1 ext-cmd: 33 et:3 eo:3 argu: 00 var: 5 value: 6
Exec cmd: 03 vt:0 vi:3 ct:0 argu: 02 var: 5555 value: 295
Exec cmd: 40 vt:0 vi:0 ct:1 ext-cmd: 20 et:2 eo:0 argu: 00 var: 5 value: 30
Exec cmd: 33 vt:3 vi:3 ct:0 argu: 13 set: 19 idxA: 255 to: 661
ppm_data00=661
ppm_data01=661
ppm_data02=661
ppm_data03=661
ppm_data04=661
ppm_data05=661
ppm_data06=661
ppm_data07=661
ppm_data08=661
ppm_data09=661
ppm_data10=661
ppm_data11=661
ppm_data12=661
ppm_data13=661
ppm_data14=661
ppm_data15=661
Exec cmd: FF vt:3 vi:15 ct:3

==== DONE Example ====



<cmd><cmd_ext><argu><argu_ext>

cmd bits:
7 - cmd_type1
6 - cmd_type0
5 - value_type1 
4 - value_type0
3 - var_idx3
2 - var_idx2
1 - var_idx1
0 - var_idx0

cmd_type:
0 - Load var_idx with value argu from value_type
1 - extended command see ext_cmd
2 - reserved
3 - Last cmd on line

value_type:
0 - 16bit value
1 - program variable
2 - PulseFire variable
3 - PulseFire variable reversed (Load into pf)

var_idx:
mal programs have only 16 internal program variable.

ext cmd bits:
7 - ext_type3
6 - ext_type2
5 - ext_type1
4 - ext_type0
3 - ext_op
2 - ext_op
1 - ext_op
0 - ext_op

ext_type:
0 - variable operation
1 - stop program
2 - goto line
3 - IF 
4 - ENDIF

ext_op if ext_type==0:
0 - Add
1 - Subtract
2 - Multiply
3 - Divide
4 - AND
5 - OR

ext_op if ext_type==3:
0 - IF ==
1 - IF !=
2 - IF  >
3 - IF  <
4 - IF >=
5 - IF <=

*/

// execute program code
#ifdef SF_ENABLE_MAL
boolean mal_execute_pc(uint16_t prog) {
  
  // Fetch cmd and split and fetch extension if needed
  uint8_t cmd = pf_conf.mal_program[pf_prog.mal_pc][prog];
  uint8_t value_type = (cmd >> 4) & 3;
  uint8_t var_idx    =  cmd & 0x0F;
  uint8_t cmd_type   = (cmd >> 6) & 3;
  uint8_t ext_type   = ZERO;
  uint8_t ext_op     = ZERO;

  #ifdef SF_ENABLE_DEBUG
  Serial_printCharP(PSTR("Exec cmd: 0x"));
  if (cmd < 16) { Serial_write('0'); }
  Serial_printHex(cmd);
  Serial_printCharP(PSTR(" vt:"));
  Serial_printDec(value_type);
  Serial_printCharP(PSTR(" vi:"));
  Serial_printDec(var_idx);
  Serial_printCharP(PSTR(" ct:"));
  Serial_printDec(cmd_type);
  #endif
  
  if (cmd_type==1) {
    pf_prog.mal_pc++;
    uint8_t ext_cmd = pf_conf.mal_program[pf_prog.mal_pc][prog];
    ext_type        = (ext_cmd >> 4) & 0x0F;
    ext_op          =  ext_cmd & 0x0F;
    
    #ifdef SF_ENABLE_DEBUG
    Serial_printCharP(PSTR(" ext-cmd: 0x"));
    if (ext_cmd < 16) { Serial_write('0'); }
    Serial_printHex(ext_cmd);
    Serial_printCharP(PSTR(" et:"));
    Serial_printDec(ext_type);
    Serial_printCharP(PSTR(" eo:"));
    Serial_printDec(ext_op);
    #endif
    
    if (ext_type==4) {
      #ifdef SF_ENABLE_DEBUG
      Serial_printCharP(PSTR(" endif"));
      Serial_println();
      #endif
      pf_prog.mal_state = MAL_STATE_RUN;
      return true; // endif
    }
    
  } else if (cmd_type==3) {
    #ifdef SF_ENABLE_DEBUG
    Serial_println();
    #endif
    pf_prog.mal_pc++;
    uint8_t next_line = pf_conf.mal_program[pf_prog.mal_pc][prog];
    if (next_line==0xFF) {
      return false;   // 0xFF and 0xFF is end of program
    }
    return true;      // goto first cmd on new line
  }
  
  uint16_t value = ZERO; // Get the value for the cmd and fetch argument
  pf_prog.mal_pc++;
  uint8_t cmd_argu = pf_conf.mal_program[pf_prog.mal_pc][prog];
  
  #ifdef SF_ENABLE_DEBUG
  Serial_printCharP(PSTR(" argu: 0x"));
  if (cmd_argu < 16) { Serial_write('0'); }
  Serial_printHex(cmd_argu);
  #endif
  
  if (value_type==0) {
    pf_prog.mal_pc++;
    uint8_t cmd_argu_ext = pf_conf.mal_program[pf_prog.mal_pc][prog];
    value = (cmd_argu << 8) + cmd_argu_ext;
  } else if (value_type==1) {
    value = pf_prog.mal_var[cmd_argu];
  } else if (value_type==2) {
    uint8_t idxA = ZERO;
    if (Vars_isIndexA(cmd_argu)) {
      pf_prog.mal_pc++;
      idxA = pf_conf.mal_program[pf_prog.mal_pc][prog];
    }
    value = Vars_getValue(cmd_argu,idxA,ZERO);
  } else {
    uint8_t idxA = ZERO;
    if (Vars_isIndexA(cmd_argu)) {
      pf_prog.mal_pc++;
      idxA = pf_conf.mal_program[pf_prog.mal_pc][prog];
    }
    if (pf_prog.mal_state == MAL_STATE_ENDIF && ext_type != 4) {
      #ifdef SF_ENABLE_DEBUG
      Serial_printCharP(PSTR(" skip"));
      Serial_println();
      #endif
      return true; // skip execution until endif is found.
    }
    #ifdef SF_ENABLE_DEBUG
    Serial_printCharP(PSTR(" set: "));Serial_printDec((int)cmd_argu);
    Serial_printCharP(PSTR(" idxA: "));Serial_printDec((int)idxA);
    Serial_printCharP(PSTR(" to: "));Serial_printDec(pf_prog.mal_var[var_idx]);
    Serial_println();
    #endif
    Vars_setValue(cmd_argu,idxA,ZERO,pf_prog.mal_var[var_idx]);
    return true;  
  }
  if (pf_prog.mal_state == MAL_STATE_ENDIF && ext_type != 4) {
    #ifdef SF_ENABLE_DEBUG
    Serial_printCharP(PSTR(" skip"));
    Serial_println();
    #endif
    return true; // skip execution until endif is found.
  }
  
  #ifdef SF_ENABLE_DEBUG
  Serial_printCharP(PSTR(" var: 0x"));Serial_printHex(pf_prog.mal_var[var_idx]);
  Serial_printCharP(PSTR(" value: 0x"));Serial_printHex(value);
  Serial_println();
  #endif
  
  if (cmd_type==0) {
    pf_prog.mal_var[var_idx] = value;
    return true;
  }
  if (ext_type==0) {
    uint16_t vv = pf_prog.mal_var[var_idx];
    switch(ext_op) {
    case 0: pf_prog.mal_var[var_idx] = vv + value;  break;
    case 1: pf_prog.mal_var[var_idx] = vv - value;  break;
    case 2: pf_prog.mal_var[var_idx] = vv * value;  break;
    case 3: pf_prog.mal_var[var_idx] = vv / value;  break;
    case 4: pf_prog.mal_var[var_idx] = vv & value;  break;
    case 5: pf_prog.mal_var[var_idx] = vv | value;  break;
    default: break;
    }
    return true;
  } else if (ext_type==1) {
    return false; // stop running
  } else if (ext_type==2) {
    pf_prog.mal_state = MAL_STATE_RUN; // end endif
    pf_prog.mal_pc = value - ONE; // goto address/line
    return true; // -one because while loop does auto pc++
  } else if (ext_type==3) {
    boolean if_result = true;
    switch(ext_op) {
    case 0: if_result = pf_prog.mal_var[var_idx] == value;  break;
    case 1: if_result = pf_prog.mal_var[var_idx] != value;  break;
    case 2: if_result = pf_prog.mal_var[var_idx]  > value;  break;
    case 3: if_result = pf_prog.mal_var[var_idx]  < value;  break;
    case 4: if_result = pf_prog.mal_var[var_idx] >= value;  break;
    case 5: if_result = pf_prog.mal_var[var_idx] <= value;  break;
    default: break;
    }
    if (if_result==false) {
      pf_prog.mal_state = MAL_STATE_ENDIF;
    }
  } else {
    pf_prog.mal_state = MAL_STATE_RUN;
  }
  return true;
}

void mal_execute(uint16_t prog,uint16_t input_value) {
  if (prog > MAL_PROGRAM_MAX) {
    return; // invalid
  }
  if (pf_conf.mal_program[ZERO][prog] == 0xFF) {
    return; // there is no program
  }

  // reset program state
  uint8_t v=ZERO;
  for(v=ZERO;v<16;v++) {
    pf_prog.mal_var[v]  = ZERO;
  }
  pf_prog.mal_var[ZERO] = input_value;
  pf_prog.mal_pc        = ZERO;
  pf_prog.mal_state     = MAL_STATE_RUN;
  
  // limit to 250 steps running
  uint8_t st=0;
  for(st=0;st<250;st++) {
    if (mal_execute_pc(prog)==false) {
      break; // end program
    }
    pf_prog.mal_pc++;
  }  
  pf_prog.mal_state          = MAL_STATE_IDLE;
}



#endif



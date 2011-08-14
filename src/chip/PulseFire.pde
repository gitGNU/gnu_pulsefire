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


//Short name   : PulseFire
//Short desc   : Automatic PulseFire Seqence Generator.
//First created: 26-Apr-2011
//Last modified: 25-Jul-2011
//Last version : 0.9
//First Author : Willem Cazander
//License-Type : BSD 2-Clause (licence.txt and http://www.opensource.org/licenses/bsd-license.php)
//IO-Hardware  : see IO_DEF_* or IO_EXT_* #defines ~200 lines below
//USB-Serial   : 115200b + "Newline" on enter/return

#include <avr/io.h>
#include <avr/wdt.h>
#include <avr/eeprom.h>
#include <avr/interrupt.h>
#include <avr/pgmspace.h>
#include <util/delay.h>
#include "vars.h"

// PIN MAPPING FOR DEFAULT CONNECTION MODE
#define IO_DEF_RX_PIN               0    // Rx/Tx for serial is wired internally to USB
#define IO_DEF_TX_PIN               1
#define IO_DEF_PIN2_PIN             2    // (def) Trigger
#define IO_DEF_PIN3_PIN             3    // (def) enter menu
#define IO_DEF_PIN4_PIN             4    // (def) menu or trigger or startlpm
#define IO_DEF_PIN5_PIN             5    // (def) External clock source for pwm
#define IO_DEF_LCD_RS_PIN           6
#define IO_DEF_LCD_E_PIN            7
#define IO_DEF_OUT_PORT         PORTB
#define IO_DEF_OUT_0_PIN            8
#define IO_DEF_OUT_1_PIN            9
#define IO_DEF_OUT_2_PIN           10
#define IO_DEF_OUT_3_PIN           11
#define IO_DEF_OUT_4_PIN           12
#define IO_DEF_OUT_5_PIN           13
#define IO_DEF_LCD_D_PORT       PORTC
#define IO_DEF_LCD_D0_PIN          14
#define IO_DEF_LCD_D1_PIN          15
#define IO_DEF_LCD_D2_PIN          16
#define IO_DEF_LCD_D3_PIN          17
#define IO_DEF_ADC4_PIN            18    // Only analog 4 and 5 are usable in default mode
#define IO_DEF_ADC5_PIN            19

// PIN MAPPING FOR EXTENDED CONNECTION MODE
#define IO_EXT_RX_PIN               0
#define IO_EXT_TX_PIN               1
#define IO_EXT_PIN2_PIN             2
#define IO_EXT_PIN3_PIN             3
#define IO_EXT_PIN4_PIN             4
#define IO_EXT_PIN5_PIN             5
#define IO_EXT_INPUT0_PIN           6    // Digital inputs or maybe push out for pll stuff.
#define IO_EXT_INPUT1_PIN           7    // Will be finalized after some timer2 input code
#define IO_EXT_OUT_DATA_PIN         8    // output 0-7 and 8-15 via 2 chip casade
#define IO_EXT_OUT_CLK_PIN          9
#define IO_EXT_OUT_E_PIN           10
#define IO_EXT_S2P_DATA_PIN        11    // lcd D0-D3,RS,E,mux0/1=Select digital input via dual 4to1 multiplexer
#define IO_EXT_S2P_CLK_PIN         12
#define IO_EXT_S2P_E_PIN           13
#define IO_EXT_ADC0_PIN            14
#define IO_EXT_ADC1_PIN            15
#define IO_EXT_ADC2_PIN            16
#define IO_EXT_ADC3_PIN            17
#define IO_EXT_ADC4_PIN            18
#define IO_EXT_ADC5_PIN            19


pf_data_struct       pf_data;
pf_prog_struct       pf_prog;
pf_conf_struct       pf_conf;
pf_conf_struct EEMEM pf_conf_eeprom;

PGM_P pmCmdList[PMCMDLIST_SIZE] PROGMEM = {
    pmCmdHelp,pmCmdSave,
    pmCmdInfoConf,pmCmdInfoData,pmCmdInfoProg,pmCmdInfoFreq,pmCmdInfoPPM,pmCmdInfoChip,
    pmCmdResetConfig,pmCmdResetData,pmCmdResetChip,
    pmCmdReqPulseFire,pmCmdReqPWMFreq,pmCmdReqAutoLPM,
    pmProgTXPush,pmProgTXEcho,pmProgTXPromt,
    pmConfMALProgram
};


/*
PF variable fields metadata:
0 = Variable Type
    0 = Disabled
    1 = uint8_t
    2 = uint16_t
    3 = uint32_t
1 = Pointer to variable in struct
2 = Pointer to ascii name of variable
3 = Max value
4 = Bitfield,
    0 = index0
    1 = index1
    2 = Remove from menu
    3 = No mapping
    4 = No limiting to configed steps
    5 = Trigger variable
    6&7 = Data type (0=conf,1=data,2=prog,3=reserved)
    8-12 = IndexA max
    13,14,15 = Index B max
5 = Default value

*/
// PFVT_TYPE,  VARIALBE_POINTER,                       ASCII_POINTER,                    MAX_VALUE,            VARIABLE_BITS,                  DEFAULT_VALUE
const uint16_t PF_VARS[
  PF_VARS_PF_SIZE+PF_VARS_LCD_SIZE+PF_VARS_LPM_SIZE+
  PF_VARS_PPM_SIZE+PF_VARS_ADC_SIZE+PF_VARS_DIC_SIZE+
  PF_VARS_DOC_SIZE+PF_VARS_DEV_SIZE+PF_VARS_PTC_SIZE+
  PF_VARS_PTT_SIZE+PF_VARS_STV_SIZE+PF_VARS_VFC_SIZE+
  PF_VARS_MAL_SIZE+PF_VARS_FRQ_SIZE+PF_VARS_SWC_SIZE][PFVF_DEF+ONE] PROGMEM = {

  {PFVT_8BIT,  (uint16_t)&pf_conf.pulse_enable,        (uint16_t)&pmConfPulseEnable,     ONE,                  PFVB_NONE,                      ONE},
  {PFVT_8BIT,  (uint16_t)&pf_conf.pulse_mode,          (uint16_t)&pmConfPulseMode,       PULSE_MODE_PPMI,      PFVB_NONE,                      PULSE_MODE_TRAIN},
  {PFVT_8BIT,  (uint16_t)&pf_conf.pulse_steps,         (uint16_t)&pmConfPulseSteps,      
    #if defined(SF_ENABLE_EXT_OUT_16BIT)
      OUTPUT_MAX,
    #elif defined(SF_ENABLE_EXT_OUT)
      8,
    #elif defined(SF_ENABLE_EXT_LCD)
      3,
    #else
      6,
    #endif
                                                                                                              PFVB_NONE,                      DEFAULT_PULSE_STEPS},
  {PFVT_8BIT,  (uint16_t)&pf_conf.pulse_trig,          (uint16_t)&pmConfPulseTrig,       PULSE_TRIG_EXT,      PFVB_NONE,                      PULSE_TRIG_LOOP},
  {PFVT_8BIT,  (uint16_t)&pf_conf.pulse_dir,           (uint16_t)&pmConfPulseDir,        PULSE_DIR_LRRL,      PFVB_NONE,                      ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_conf.pulse_bank,          (uint16_t)&pmConfPulseBank,       ALL_BANK_MAX,        PFVB_NONE,                      ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_conf.pulse_inv,           (uint16_t)&pmConfPulseInv,        ONE,                 PFVB_NONE,                      ZERO},
  {PFVT_16BIT, (uint16_t)&pf_conf.pulse_trig_delay,    (uint16_t)&pmConfPulseTrigDelay,  0xFFFF,              PFVB_NONE,                      ZERO},
  {PFVT_16BIT, (uint16_t)&pf_conf.pulse_post_delay,    (uint16_t)&pmConfPulsePostDelay,  0xFFFF,              PFVB_NONE,                      ZERO},
  {PFVT_16BIT, (uint16_t)&pf_conf.pulse_mask_a,        (uint16_t)&pmConfPulseMaskA,      0xFFFF,              PFVB_NONE,                      PULSE_DATA_ON},
  {PFVT_16BIT, (uint16_t)&pf_conf.pulse_mask_b,        (uint16_t)&pmConfPulseMaskB,      0xFFFF,              PFVB_NONE,                      PULSE_DATA_ON},
  {PFVT_16BIT, (uint16_t)&pf_conf.pulse_init_a,        (uint16_t)&pmConfPulseInitA,      0xFFFF,              PFVB_NONE,                      DEFAULT_PULSE_DATA_INIT},
  {PFVT_16BIT, (uint16_t)&pf_conf.pulse_init_b,        (uint16_t)&pmConfPulseInitB,      0xFFFF,              PFVB_NONE,                      DEFAULT_PULSE_DATA_INIT},
  
  {PFVT_16BIT, (uint16_t)&pf_conf.pwm_on_cnt_a,        (uint16_t)&pmConfPWMOnCntA,       0xFFFF,              (OUTPUT_MAX<<8)+PFVB_IDXA,      DEFAULT_PWM_ON_CNT},
  {PFVT_16BIT, (uint16_t)&pf_conf.pwm_on_cnt_b,        (uint16_t)&pmConfPWMOnCntB,       0xFFFF,              (OUTPUT_MAX<<8)+PFVB_IDXA,      DEFAULT_PWM_ON_CNT},
  {PFVT_16BIT, (uint16_t)&pf_conf.pwm_off_cnt_a,       (uint16_t)&pmConfPWMOffCntA,      0xFFFF,              (OUTPUT_MAX<<8)+PFVB_IDXA,      ZERO},
  {PFVT_16BIT, (uint16_t)&pf_conf.pwm_off_cnt_b,       (uint16_t)&pmConfPWMOffCntB,      0xFFFF,              (OUTPUT_MAX<<8)+PFVB_IDXA,      ZERO},
  {PFVT_16BIT, (uint16_t)&pf_conf.pwm_tune_cnt,        (uint16_t)&pmConfPWMTuneCnt,      0xFFFF,              (OUTPUT_MAX<<8)+PFVB_IDXA,      ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_conf.pwm_loop,            (uint16_t)&pmConfPWMLoop,         0xFF,                PFVB_NONE,                      DEFAULT_PWM_LOOP},
  {PFVT_8BIT,  (uint16_t)&pf_conf.pwm_loop_delta,      (uint16_t)&pmConfPWMLoopDelta,    0xFF,                PFVB_NONE,                      ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_conf.pwm_clock,           (uint16_t)&pmConfPWMClock,        CLOCK_VALUE_MAX,     PFVB_NONE,                      DEFAULT_PWM_CLOCK},
  {PFVT_8BIT,  (uint16_t)&pf_conf.pwm_duty,            (uint16_t)&pmConfPWMDuty,         110,                 PFVB_NONE,                      ZERO},
  
  #ifdef SF_ENABLE_PPM
  {PFVT_8BIT,  (uint16_t)&pf_conf.ppm_data_offset,     (uint16_t)&pmConfPPMDataOffset,   OUTPUT_MAX-ONE,      PFVB_NONE,                      ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_conf.ppm_data_length,     (uint16_t)&pmConfPPMDataLength,   OUTPUT_MAX,          PFVB_NONE,                      OUTPUT_MAX},
  {PFVT_16BIT, (uint16_t)&pf_conf.ppm_data_a,          (uint16_t)&pmConfPPMDataA,        0xFFFF,              (OUTPUT_MAX<<8)+PFVB_IDXA,      ZERO},
  {PFVT_16BIT, (uint16_t)&pf_conf.ppm_data_b,          (uint16_t)&pmConfPPMDataB,        0xFFFF,              (OUTPUT_MAX<<8)+PFVB_IDXA,      ZERO},
  #endif
  #ifdef SF_ENABLE_LPM
  {PFVT_16BIT, (uint16_t)&pf_conf.lpm_start,           (uint16_t)&pmConfLPMStart,        0xFFFF,              PFVB_NONE,                      DEFAULT_LPM_START},
  {PFVT_16BIT, (uint16_t)&pf_conf.lpm_stop,            (uint16_t)&pmConfLPMStop,         0xFFFF,              PFVB_NONE,                      DEFAULT_LPM_STOP},
  {PFVT_16BIT, (uint16_t)&pf_conf.lpm_size,            (uint16_t)&pmConfLPMSize,         0xFFFF,              PFVB_NONE,                      ZERO},
  #endif
  #ifdef SF_ENABLE_PTC
  {PFVT_8BIT,  (uint16_t)&pf_conf.ptc_0run,            (uint16_t)&pmConfPTC0Run,         PTC_RUN_LOOP,        PFVB_NONE,                      PTC_RUN_OFF},
  {PFVT_8BIT,  (uint16_t)&pf_conf.ptc_1run,            (uint16_t)&pmConfPTC1Run,         PTC_RUN_LOOP,        PFVB_NONE,                      PTC_RUN_OFF},
  {PFVT_8BIT,  (uint16_t)&pf_conf.ptc_0mul,            (uint16_t)&pmConfPTC0Mul,         0xFF,                PFVB_NONE,                      ONE},
  {PFVT_8BIT,  (uint16_t)&pf_conf.ptc_1mul,            (uint16_t)&pmConfPTC1Mul,         0xFF,                PFVB_NONE,                      ONE},
  {PFVT_16BIT, (uint16_t)&pf_conf.ptc_0map,            (uint16_t)&pmConfPTC0Map,         0xFFFF,              (QMAP_SIZE<<13)+(PTC_TIME_MAP_MAX<<8)+PFVB_IDXB+PFVB_IDXA+PFVB_NOLIMIT+PFVB_NOMAP+PFVB_NOMENU,  0xFFFF},
  {PFVT_16BIT, (uint16_t)&pf_conf.ptc_1map,            (uint16_t)&pmConfPTC1Map,         0xFFFF,              (QMAP_SIZE<<13)+(PTC_TIME_MAP_MAX<<8)+PFVB_IDXB+PFVB_IDXA+PFVB_NOLIMIT+PFVB_NOMAP+PFVB_NOMENU,  0xFFFF},
  #endif
  #ifdef SF_ENABLE_PTT
  {PFVT_16BIT, (uint16_t)&pf_conf.ptt_0map,            (uint16_t)&pmConfPTT0Map,         0xFFFF,              (QMAP_SIZE<<13)+(PTT_TRIG_MAP_MAX<<8)+PFVB_IDXB+PFVB_IDXA+PFVB_NOLIMIT+PFVB_NOMAP+PFVB_NOMENU,  0xFFFF},
  {PFVT_16BIT, (uint16_t)&pf_conf.ptt_1map,            (uint16_t)&pmConfPTT1Map,         0xFFFF,              (QMAP_SIZE<<13)+(PTT_TRIG_MAP_MAX<<8)+PFVB_IDXB+PFVB_IDXA+PFVB_NOLIMIT+PFVB_NOMAP+PFVB_NOMENU,  0xFFFF},
  {PFVT_16BIT, (uint16_t)&pf_conf.ptt_2map,            (uint16_t)&pmConfPTT2Map,         0xFFFF,              (QMAP_SIZE<<13)+(PTT_TRIG_MAP_MAX<<8)+PFVB_IDXB+PFVB_IDXA+PFVB_NOLIMIT+PFVB_NOMAP+PFVB_NOMENU,  0xFFFF},
  {PFVT_16BIT, (uint16_t)&pf_conf.ptt_3map,            (uint16_t)&pmConfPTT3Map,         0xFFFF,              (QMAP_SIZE<<13)+(PTT_TRIG_MAP_MAX<<8)+PFVB_IDXB+PFVB_IDXA+PFVB_NOLIMIT+PFVB_NOMAP+PFVB_NOMENU,  0xFFFF},
  #endif
  #ifdef SF_ENABLE_STV
  {PFVT_8BIT,  (uint16_t)&pf_conf.stv_warn_secs,       (uint16_t)&pmConfSTVWarnSecs,     0xFF,                PFVB_NOMAP+PFVB_NOMENU,         ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_conf.stv_warn_mode,       (uint16_t)&pmConfSTVWarnMode,     PULSE_MODE_PPMI,     PFVB_NOMAP+PFVB_NOMENU,         PULSE_MODE_OFF},
  {PFVT_8BIT,  (uint16_t)&pf_conf.stv_error_secs,      (uint16_t)&pmConfSTVErrorSecs,    0xFF,                PFVB_NOMAP+PFVB_NOMENU,         ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_conf.stv_error_mode,      (uint16_t)&pmConfSTVErrorMode,    PULSE_MODE_PPMI,     PFVB_NOMAP+PFVB_NOMENU,         PULSE_MODE_OFF},
  {PFVT_16BIT, (uint16_t)&pf_conf.stv_max_map,         (uint16_t)&pmConfSTVMaxMap,       0xFFFF,              (QMAP_SIZE<<13)+(STV_MAX_MAP_MAX<<8)+PFVB_IDXB+PFVB_IDXA+PFVB_NOLIMIT+PFVB_NOMAP+PFVB_NOMENU,0xFFFF},
  {PFVT_16BIT, (uint16_t)&pf_conf.stv_min_map,         (uint16_t)&pmConfSTVMinMap,       0xFFFF,              (QMAP_SIZE<<13)+(STV_MIN_MAP_MAX<<8)+PFVB_IDXB+PFVB_IDXA+PFVB_NOLIMIT+PFVB_NOMAP+PFVB_NOMENU,0xFFFF},
  #endif
  #ifdef SF_ENABLE_VFC
  {PFVT_16BIT, (uint16_t)&pf_conf.vfc_input_map,       (uint16_t)&pmConfVFCInputMap,     0xFFFF,              (QMAP_SIZE<<13)+(VFC_MAP_MAX<<8)+PFVB_IDXB+PFVB_IDXA+PFVB_NOLIMIT+PFVB_NOMAP+PFVB_NOMENU,0xFFFF},
  {PFVT_16BIT, (uint16_t)&pf_conf.vfc_output_map,      (uint16_t)&pmConfVFCOutputMap,    0xFFFF,              (QMAP_SIZE<<13)+(VFC_MAP_MAX<<8)+PFVB_IDXB+PFVB_IDXA+PFVB_NOLIMIT+PFVB_NOMAP+PFVB_NOMENU,0xFFFF},
  #endif
  #ifdef SF_ENABLE_SWC
  {PFVT_8BIT,  (uint16_t)&pf_conf.swc_delay,           (uint16_t)&pmConfSWCDelay,        0xFF,                PFVB_NOMAP,                     ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_conf.swc_mode,            (uint16_t)&pmConfSWCMode,         PULSE_MODE_PPMI,     PFVB_NOMAP,                     ZERO},
  {PFVT_16BIT, (uint16_t)&pf_conf.swc_secs,            (uint16_t)&pmConfSWCSecs,         0xFFFF,              PFVB_NOMAP,                     ZERO},
  {PFVT_16BIT, (uint16_t)&pf_conf.swc_duty,            (uint16_t)&pmConfSWCDuty,         0xFFFF,              PFVB_NOMAP,                     DEFAULT_SYS_WARMUP_DUTY},
  {PFVT_8BIT,  (uint16_t)&pf_conf.swc_trig,            (uint16_t)&pmConfSWCTrig,         PTT_TRIG_VAR_SIZE-ONE,PFVB_NOMAP,                    0xFF},
  #endif

  {PFVT_8BIT,  (uint16_t)&pf_conf.avr_pin2_map,        (uint16_t)&pmConfAVRPin2Map,      PIN2_FIRE_IN,        PFVB_NOMAP+PFVB_NOMENU,         PIN2_TRIG_IN},
  {PFVT_8BIT,  (uint16_t)&pf_conf.avr_pin3_map,        (uint16_t)&pmConfAVRPin3Map,      PIN3_FIRE_IN,        PFVB_NOMAP+PFVB_NOMENU,         PIN3_MENU0_IN},
  {PFVT_8BIT,  (uint16_t)&pf_conf.avr_pin4_map,        (uint16_t)&pmConfAVRPin4Map,      PIN4_DOC10_OUT,      PFVB_NOMAP+PFVB_NOMENU,         PIN4_MENU1_IN},
  {PFVT_8BIT,  (uint16_t)&pf_conf.avr_pin5_map,        (uint16_t)&pmConfAVRPin5Map,      PIN5_DOC11_OUT,      PFVB_NOMAP+PFVB_NOMENU,         PIN5_CLOCK_IN},

  #ifdef SF_ENABLE_ADC
  {PFVT_16BIT, (uint16_t)&pf_conf.adc_map,             (uint16_t)&pmConfAdcMap,          0xFFFF,              (QMAP_SIZE<<13)+(ADC_NUM_MAX<<8)+PFVB_NOLIMIT+PFVB_NOMAP+PFVB_NOMENU+PFVB_IDXB+PFVB_IDXA,   0xFFFF},
  {PFVT_16BIT, (uint16_t)&pf_conf.adc_jitter,          (uint16_t)&pmConfAdcJitter,       0xFFFF,              PFVB_NOMAP+PFVB_NOMENU,         DEFAULT_SYS_ADC_JITTER},
  #endif
  #ifdef SF_ENABLE_DIC
  {PFVT_16BIT, (uint16_t)&pf_conf.dic_map,             (uint16_t)&pmConfDicMap,          0xFFFF,              (QMAP_SIZE<<13)+(DIC_NUM_MAX<<8)+PFVB_NOLIMIT+PFVB_NOMAP+PFVB_NOMENU+PFVB_IDXB+PFVB_IDXA,   0xFFFF},
  #endif

  // =============== pf_data vars = +64

  {PFVT_32BIT, (uint16_t)&pf_data.sys_main_loop_cnt,   (uint16_t)&pmDataSysMainLoopCnt,  ZERO,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_32BIT, (uint16_t)&pf_data.sys_input_time_cnt,  (uint16_t)&pmDataSysInputTimeCnt, ZERO,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  #ifdef SF_ENABLE_ADC
  {PFVT_32BIT, (uint16_t)&pf_data.adc_time_cnt,        (uint16_t)&pmDataAdcTimeCnt,      ZERO,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_16BIT, (uint16_t)&pf_data.adc_value,           (uint16_t)&pmDataAdcValue,        0xFFFF,              (ADC_NUM_MAX<<8)+PFVB_DT0+PFVB_NOMAP+PFVB_IDXA,  ZERO},
  #endif
  #ifdef SF_ENABLE_DIC  
  {PFVT_32BIT, (uint16_t)&pf_data.dic_time_cnt,        (uint16_t)&pmDataDicTimeCnt,      ZERO,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_data.dic_value,           (uint16_t)&pmDataDicValue,        0xFF,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  #endif
  #ifdef SF_ENABLE_DOC  
  {PFVT_8BIT,  (uint16_t)&pf_data.doc_port,            (uint16_t)&pmDataDocPort,         ONE,                 (DOC_PORT_NUM_MAX<<8)+PFVB_DT0+PFVB_IDXA,        ZERO},
  #endif
  #ifdef SF_ENABLE_SWC
  {PFVT_8BIT,  (uint16_t)&pf_data.swc_mode_org,        (uint16_t)&pmDataSWCModeOrg,      PULSE_MODE_PPMI,     PFVB_DT0+PFVB_NOMAP,            ONE},
  {PFVT_16BIT, (uint16_t)&pf_data.swc_secs_cnt,        (uint16_t)&pmDataSWCSecsCnt,      0xFFFF,              PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_16BIT, (uint16_t)&pf_data.swc_duty_cnt,        (uint16_t)&pmDataSWCDutyCnt,      0xFFFF,              PFVB_DT0+PFVB_NOMAP,            ZERO},
  #endif
  #ifdef SF_ENABLE_LCD
  {PFVT_32BIT, (uint16_t)&pf_data.lcd_time_cnt,        (uint16_t)&pmDataLcdTimeCnt,      ZERO,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_data.lcd_page,            (uint16_t)&pmDataLcdPage,         ZERO,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_data.lcd_redraw,          (uint16_t)&pmDataLcdRedraw,       ZERO,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  #endif
  #ifdef SF_ENABLE_FRQ
  {PFVT_16BIT, (uint16_t)&pf_data.req_pwm_freq,        (uint16_t)&pmCmdReqPWMFreq,       0xFFFF,              PFVB_DT0,                       ZERO},
  #endif

  #ifdef SF_ENABLE_LPM
  {PFVT_8BIT,  (uint16_t)&pf_data.lpm_state,           (uint16_t)&pmDataLPMState,        0xFF,                PFVB_DT0+PFVB_NOMAP,            LPM_IDLE},
  {PFVT_8BIT,  (uint16_t)&pf_data.lpm_auto_cmd,        (uint16_t)&pmCmdReqAutoLPM,       0xFF,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_32BIT, (uint16_t)&pf_data.lpm_start_time,      (uint16_t)&pmDataLPMStartTime,    ZERO,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_32BIT, (uint16_t)&pf_data.lpm_total_time,      (uint16_t)&pmDataLPMTotalTime,    ZERO,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_16BIT, (uint16_t)&pf_data.lpm_result,          (uint16_t)&pmDataLPMResult,       0xFFFF,              PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_16BIT, (uint16_t)&pf_data.lpm_level,           (uint16_t)&pmDataLPMLevel,        0xFFFF,              PFVB_DT0,                       ZERO},
  #endif

  #ifdef SF_ENABLE_PTC
  {PFVT_32BIT, (uint16_t)&pf_data.ptc_sys_cnt,         (uint16_t)&pmDataPTCSysCnt,       0xFFFF,              PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_data.ptc_0cnt,            (uint16_t)&pmDataPTC0Cnt,         0xFF,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_data.ptc_1cnt,            (uint16_t)&pmDataPTC1Cnt,         0xFF,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_data.ptc_0run_cnt,        (uint16_t)&pmDataPTC0RunCnt,      0xFF,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_data.ptc_1run_cnt,        (uint16_t)&pmDataPTC1RunCnt,      0xFF,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_data.ptc_0map_idx,        (uint16_t)&pmDataPTC0MapIdx,      PTC_TIME_MAP_MAX,    PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_data.ptc_1map_idx,        (uint16_t)&pmDataPTC1MapIdx,      PTC_TIME_MAP_MAX,    PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_16BIT, (uint16_t)&pf_data.ptc_0mul_cnt,        (uint16_t)&pmDataPTC0MulCnt,      0xFFFF,              PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_16BIT, (uint16_t)&pf_data.ptc_1mul_cnt,        (uint16_t)&pmDataPTC1MulCnt,      0xFFFF,              PFVB_DT0+PFVB_NOMAP,            ZERO},
  #endif
  #ifdef SF_ENABLE_PTT
  {PFVT_8BIT,  (uint16_t)&pf_data.ptt_idx,             (uint16_t)&pmDataPTTIdx,          0xFF,                (PTT_TRIG_VAR_SIZE<<8)+PFVB_DT0+PFVB_IDXA+PFVB_NOMAP, ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_data.ptt_cnt,             (uint16_t)&pmDataPTTCnt,          0xFF,                (PTT_TRIG_VAR_SIZE<<8)+PFVB_DT0+PFVB_IDXA+PFVB_NOMAP, ZERO}, 
  {PFVT_8BIT,  (uint16_t)&pf_data.ptt_fire,            (uint16_t)&pmDataPTTFire,         0xFF,                (PTT_TRIG_VAR_SIZE<<8)+PFVB_DT0+PFVB_IDXA+PFVB_TRIG,  ZERO},
  #endif

  #ifdef SF_ENABLE_DEV 
  {PFVT_16BIT, (uint16_t)&pf_data.dev_volt,            (uint16_t)&pmDataDevVolt,         0xFFFF,              PFVB_DT0,                       ZERO},
  {PFVT_16BIT, (uint16_t)&pf_data.dev_amp,             (uint16_t)&pmDataDevAmp,          0xFFFF,              PFVB_DT0,                       ZERO},
  {PFVT_16BIT, (uint16_t)&pf_data.dev_temp,            (uint16_t)&pmDataDevTemp,         0xFFFF,              PFVB_DT0,                       ZERO},
  {PFVT_16BIT, (uint16_t)&pf_data.dev_freq,            (uint16_t)&pmDataDevFreq,         0xFFFF,              PFVB_DT0,                       ZERO},
  {PFVT_16BIT, (uint16_t)&pf_data.dev_freq_cnt,        (uint16_t)&pmDataDevFreqCnt,      0xFFFF,              PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_16BIT, (uint16_t)&pf_data.dev_var,             (uint16_t)&pmDataDevVar,          0xFFFF,              (DEV_VAR_MAX<<8)+PFVB_DT0+PFVB_IDXA,            ZERO}, 
  #endif

  {PFVT_8BIT,  (uint16_t)&pf_data.pulse_fire,          (uint16_t)&pmDataPulseFire,       0xFF,                PFVB_DT0+PFVB_TRIG,             ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_data.pulse_step,          (uint16_t)&pmDataPulseStep,       0xFF,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_data.pulse_data,          (uint16_t)&pmDataPulseData,       0xFF,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_data.pulse_dir_cnt,       (uint16_t)&pmDataPulseDirCnt,     0xFF,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_data.pulse_bank_cnt,      (uint16_t)&pmDataPulseBankCnt,    0xFF,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_data.pulse_trig_delay_cnt,(uint16_t)&pmDataPulseTrigDelayCnt,0xFF,               PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_data.pulse_post_delay_cnt,(uint16_t)&pmDataPulsePostDelayCnt,0xFF,               PFVB_DT0+PFVB_NOMAP,            ZERO},
  
  {PFVT_8BIT,  (uint16_t)&pf_data.pwm_state,           (uint16_t)&pmDataPWMState,        0xFF,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_data.pwm_loop_cnt,        (uint16_t)&pmDataPWMLoopCnt,      0xFF,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_data.pwm_loop_max,        (uint16_t)&pmDataPWMLoopMax,      0xFF,                PFVB_DT0+PFVB_NOMAP,            ZERO},

  #ifdef SF_ENABLE_PPM  
  {PFVT_8BIT,  (uint16_t)&pf_data.ppm_idx,             (uint16_t)&pmDataPPMIdx,          0xFF,                (OUTPUT_MAX<<8)+PFVB_DT0+PFVB_NOMAP+PFVB_IDXA,  ZERO},
  #endif
  
  #ifdef SF_ENABLE_MAL
  {PFVT_16BIT, (uint16_t)&pf_data.mal_trig,            (uint16_t)&pmDataMALTrig,         0xFFFF,              (MAL_PROGRAM_MAX<<8)+PFVB_DT0+PFVB_IDXA+PFVB_TRIG,ZERO},
  #endif
  
  // =============== pf_prog vars = +128
 
  {PFVT_8BIT,  (uint16_t)&pf_prog.req_tx_push,         (uint16_t)&pmProgTXPush,          ONE,                 PFVB_DT1+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_prog.req_tx_echo,         (uint16_t)&pmProgTXEcho,          ONE,                 PFVB_DT1+PFVB_NOMAP,            ONE},
  {PFVT_8BIT,  (uint16_t)&pf_prog.req_tx_promt,        (uint16_t)&pmProgTXPromt,         ONE,                 PFVB_DT1+PFVB_NOMAP,            ONE},
  
  #ifdef SF_ENABLE_LCD
  {PFVT_8BIT,  (uint16_t)&pf_prog.lcd_menu_state,      (uint16_t)&pmProgLcdMenuState,    0xFF,                PFVB_DT1+PFVB_NOMAP,            ZERO},
  {PFVT_16BIT, (uint16_t)&pf_prog.lcd_menu_mul,        (uint16_t)&pmProgLcdMenuMul,      0xFFFF,              PFVB_DT1+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_prog.lcd_menu_idx,        (uint16_t)&pmProgLcdMenuIdx,      0xFF,                PFVB_DT1+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_prog.lcd_menu_value_idx,  (uint16_t)&pmProgLcdMenuValueIdx, 0xFF,                PFVB_DT1+PFVB_NOMAP,            ZERO},
  {PFVT_32BIT, (uint16_t)&pf_prog.lcd_menu_time_cnt,   (uint16_t)&pmProgLcdMenuTimeCnt,  ZERO,                PFVB_DT1+PFVB_NOMAP,            ZERO},
  #endif

  #ifdef SF_ENABLE_MAL
  {PFVT_8BIT,  (uint16_t)&pf_prog.mal_pc,              (uint16_t)&pmProgMALPc,           0xFF,                PFVB_DT1+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_prog.mal_state,           (uint16_t)&pmProgMALState,        0xFF,                PFVB_DT1+PFVB_NOMAP,            ZERO},
  {PFVT_16BIT, (uint16_t)&pf_prog.mal_var,             (uint16_t)&pmProgMALVar,          0xFFFF,              (OUTPUT_MAX<<8)+PFVB_DT1+PFVB_NOMAP+PFVB_IDXA,  ZERO},  
  #endif
  
  #ifdef SF_ENABLE_STV
  {PFVT_8BIT,  (uint16_t)&pf_prog.stv_state,           (uint16_t)&pmProgSTVState,        STV_STATE_ERROR_MIN, PFVB_DT1+PFVB_NOMAP,            STV_STATE_OKE},
  {PFVT_32BIT, (uint16_t)&pf_prog.stv_time_cnt,        (uint16_t)&pmProgSTVTimeCnt,      0xFFFF,              PFVB_DT1+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_prog.stv_mode_org,        (uint16_t)&pmProgSTVModeOrg,      0xFF,                PFVB_DT1+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_prog.stv_map_idx,         (uint16_t)&pmProgSTVMapIdx,       0xFF,                PFVB_DT1+PFVB_NOMAP,            ZERO},  
  #endif  

};



boolean Vars_isTrigger(byte idx) {
  uint16_t bits = pgm_read_word(&(PF_VARS[idx][PFVF_BITS])) & PFVB_TRIG;
  if (bits > ZERO) {
    return true;
  }
  return false;
}

boolean Vars_isNolimit(byte idx) {
  uint16_t bits = pgm_read_word(&(PF_VARS[idx][PFVF_BITS])) & PFVB_NOLIMIT;
  if (bits > ZERO) {
    return true;
  }
  return false;
}

boolean Vars_isIndexA(byte idx) {
  if (idx > PF_VARS_SIZE) {
    return false;
  }
  uint16_t bits = pgm_read_word(&(PF_VARS[idx][PFVF_BITS])) & PFVB_IDXA;
  if (bits > ZERO) {
    return true;
  }
  return false;
}

boolean Vars_isIndexB(byte idx) {
  if (idx > PF_VARS_SIZE) {
    return false;
  }
  uint16_t bits = pgm_read_word(&(PF_VARS[idx][PFVF_BITS])) & PFVB_IDXB;
  if (bits > ZERO) {
    return true;
  }
  return false;
}

boolean Vars_isNomap(byte idx) {
  if (idx > PF_VARS_SIZE) {
    return true; // cannot map idx out of range
  }
  uint16_t bits = pgm_read_word(&(PF_VARS[idx][PFVF_BITS])) & PFVB_NOMAP;
  if (bits > ZERO) {
    return true;
  }
  return false;
}

boolean Vars_isBitSize32(byte idx) {
  uint16_t type = pgm_read_word(&(PF_VARS[idx][PFVF_TYPE]));
  if (type == PFVT_32BIT) {
    return true;
  }
  return false;
}

#ifdef SF_ENABLE_LCD
boolean Vars_isMenuSkip(byte idx) {
  uint16_t bits = pgm_read_word(&(PF_VARS[idx][PFVF_BITS])) & PFVB_NOMENU;
  if (bits > ZERO) {
    return true;
  }
  return false;
}
#endif

boolean Vars_isTypeConf(byte idx) {
  uint16_t bits = pgm_read_word(&(PF_VARS[idx][PFVF_BITS])) & PFVB_DT0+PFVB_DT1;
  if (bits == ZERO) {
    return true;
  }
  return false;
}

boolean Vars_isTypeData(byte idx) {
  uint16_t bits = pgm_read_word(&(PF_VARS[idx][PFVF_BITS])) & PFVB_DT0+PFVB_DT1;
  if (bits == PFVB_DT0) {
    return true;
  }
  return false;
}


boolean Vars_isTypeProg(byte idx) {
  uint16_t bits = pgm_read_word(&(PF_VARS[idx][PFVF_BITS])) & PFVB_DT0+PFVB_DT1;
  if (bits == PFVB_DT1) {
    return true;
  }
  return false;
}



uint8_t Vars_getIndexAMax(uint8_t idx) {
  uint16_t bits = pgm_read_word(&(PF_VARS[idx][PFVF_BITS])) & 0xFF00;
  uint8_t result = (bits >> 8) & 0x1F; // only use 5 bits
  return result;
}
uint8_t Vars_getIndexBMax(uint8_t idx) {
  uint16_t bits = pgm_read_word(&(PF_VARS[idx][PFVF_BITS])) & 0xFF00;
  uint8_t result = (bits >> 13);
  return result;
}
uint16_t Vars_getDefaultValue(uint8_t idx) {
  return pgm_read_word(&(PF_VARS[idx][PFVF_DEF]));
}

uint16_t Vars_getIndexFromName(char* name) {
  for (uint8_t i=ZERO;i < PF_VARS_SIZE;i++) {
    if (strcmp_P(name, (const prog_char*)pgm_read_word(&(PF_VARS[i][PFVF_NAME]))) == ZERO) {
      return i;
    }
  }
  return QMAP_VAR_NONE;
}

uint16_t Vars_getValue(uint8_t idx,uint8_t idxA,uint8_t idxB) {
  boolean indexed = Vars_isIndexA(idx);
  uint16_t fieldType = pgm_read_word(&(PF_VARS[idx][PFVF_TYPE]));
  uint16_t value = ZERO;
  if (fieldType == PFVT_16BIT) {
    uint16_t *valuePtr = (uint16_t*)pgm_read_word(&(PF_VARS[idx][PFVF_VAR]));
    if (indexed) {
      if (Vars_isIndexB(idx)) {
        valuePtr += Vars_getIndexBMax(idx)*idxA + idxB;
      } else {
        valuePtr += idxA;
      }
    }
    value = *(valuePtr);
  } else if (fieldType == PFVT_8BIT) {
    uint8_t *valuePtr = (uint8_t*)pgm_read_word(&(PF_VARS[idx][PFVF_VAR]));
    if (indexed) {
      if (Vars_isIndexB(idx)) {
        valuePtr += Vars_getIndexBMax(idx)*idxA + idxB;
      } else {
        valuePtr += idxA;
      }
    }
    value = *(valuePtr);
  }
  return value;
}


uint32_t Vars_getValue32(uint8_t idx,uint8_t idxA) {
  boolean indexed = Vars_isIndexA(idx);
  uint32_t *valuePtr = (uint32_t*)pgm_read_word(&(PF_VARS[idx][PFVF_VAR]));
  if (indexed) {
    valuePtr += idxA;
  }
  uint32_t value = *(valuePtr);
  return value;
}


void Vars_readConfig(void) {
  eeprom_read_block((void*)&pf_conf,(const void*)&pf_conf_eeprom,sizeof(pf_conf_struct));
}

void Vars_writeConfig(void) {
  eeprom_write_block((const void*)&pf_conf,(void*)&pf_conf_eeprom,sizeof(pf_conf_struct));
}

// removes the dubbel print is set is done with serial and req_auto_push==1 
uint16_t pf_var_value_set_serial(uint8_t idx,uint8_t idxA,uint8_t idxB,uint16_t value) {
  return pf_var_value_set_impl(idx,idxA,idxB,value,true,true);
}
// removes the external function calls in reset phase.
uint16_t pf_var_value_set_reset(uint8_t idx,uint8_t idxA,uint16_t value) {
  if (Vars_isIndexB(idx)) {
    for (uint8_t b=0;b<Vars_getIndexBMax(idx);b++) {
      uint16_t valueSet = value; 
      if (b>ZERO) {
        valueSet = ZERO;
      }
      pf_var_value_set_impl(idx,idxA,b,valueSet,false,false);
    }
    return ZERO;
  } else {
    return pf_var_value_set_impl(idx,idxA,(uint8_t)0,value,false,false);
  }
}
uint16_t pf_var_value_set(uint8_t idx,uint8_t idxA,uint8_t idxB,uint16_t value) {
  return pf_var_value_set_impl(idx,idxA,0,value,true,false);
}
uint16_t pf_var_value_set_impl(uint8_t idx,uint8_t idxA,uint8_t idxB,uint16_t value,boolean trig,boolean serial) {
  boolean indexed = Vars_isIndexA(idx);
  uint8_t idxMaxA = Vars_getIndexAMax(idx);
  uint16_t fieldType = pgm_read_word(&(PF_VARS[idx][PFVF_TYPE]));
  uint16_t value_max = pgm_read_word(&(PF_VARS[idx][PFVF_MAX]));
  uint16_t value_min = ZERO;
  if ( pgm_read_word(&(PF_VARS[idx][PFVF_NAME])) == (uint16_t)&pmConfPWMOnCntA) {
    value_min = ONE;
  }
  if ( pgm_read_word(&(PF_VARS[idx][PFVF_NAME])) == (uint16_t)&pmConfPWMOnCntB) {
    value_min = ONE;
  }
  if ( pgm_read_word(&(PF_VARS[idx][PFVF_NAME])) == (uint16_t)&pmConfSWCDuty) {
    value_min = ONE;
  }
  #ifdef SF_ENABLE_PPM
  if ( pgm_read_word(&(PF_VARS[idx][PFVF_NAME])) == (uint16_t)&pmConfPPMDataLength) {
    value_min = ONE;
  }
  #endif
  #ifdef SF_ENABLE_PTC
  if ( pgm_read_word(&(PF_VARS[idx][PFVF_NAME])) == (uint16_t)&pmConfPTC0Mul) {
    value_min = ONE;
  }
  if ( pgm_read_word(&(PF_VARS[idx][PFVF_NAME])) == (uint16_t)&pmConfPTC1Mul) {
    value_min = ONE;
  }
  #endif
  
  if (value==0xFF) {
    value_max = ZERO; // big hack for swc_mode,stv_*_mode.
  }
  
  if (value_max != ZERO && value > value_max) {
    value = value_max;
  }
  if (value_min != ZERO && value < value_min) {
    value = value_min;
  }
  // Set value
  if (fieldType == PFVT_16BIT) {
    uint16_t *valuePtr = (uint16_t*)pgm_read_word(&(PF_VARS[idx][PFVF_VAR]));
      if (indexed) {
        if (Vars_isIndexB(idx)) {
          valuePtr += Vars_getIndexBMax(idx)*idxA + idxB;
        } else {
          valuePtr += idxA;
        }
        if (idxA != QMAP_VAR_IDX_ALL) {
          *valuePtr = value;          
        } else {
          for (uint8_t i=ZERO;i<idxMaxA;i++) {
            uint16_t *valuePtr = (uint16_t*)pgm_read_word(&(PF_VARS[idx][PFVF_VAR]));
            if (Vars_isIndexB(idx)) {
              valuePtr += Vars_getIndexBMax(idx)*i + idxB;
            } else {
              valuePtr += i;
            }
            *valuePtr = value;
          }
        }
      } else {
        *valuePtr = value;
      }
  } else if (fieldType == PFVT_8BIT) {
      uint8_t *valuePtr = (uint8_t*)pgm_read_word(&(PF_VARS[idx][PFVF_VAR]));
      if (indexed) {
        if (Vars_isIndexB(idx)) {
          valuePtr += Vars_getIndexBMax(idx)*idxA + idxB;
        } else {
          valuePtr += idxA;
        }
        if (idxA != QMAP_VAR_IDX_ALL) {
          *valuePtr = value;
        } else {
          for (uint8_t i=ZERO;i<idxMaxA;i++) {
            uint8_t *valuePtr = (uint8_t*)pgm_read_word(&(PF_VARS[idx][PFVF_VAR]));
            if (Vars_isIndexB(idx)) {
              valuePtr += Vars_getIndexBMax(idx)*i + idxB;
            } else {
              valuePtr += i;
            }
            *valuePtr = value;
          }
        }
      } else {
        *valuePtr = value;
      }
  } else if (fieldType == PFVT_32BIT) {
    uint32_t *valuePtr = (uint32_t*)pgm_read_word(&(PF_VARS[idx][PFVF_VAR]));
    if (indexed) {
      valuePtr += idxA;
    }
    uint32_t v = value;
    *valuePtr = v;
  }
  
  // Send to serial if push is on
  if (pf_prog.req_tx_push == ONE && serial==false) {
    if (indexed) {
      if (idxA == QMAP_VAR_IDX_ALL) {
        for (uint8_t i=ZERO;i<idxMaxA;i++) {
          Serial.print(UNPSTRA(&PF_VARS[idx][PFVF_NAME]));
          if(i<10) {Serial.print('0');} Serial.print((int)i);
          Serial_print_P(pmSetSpaced);
          Serial.print(value);
          Serial.println();         
        }
      } else {
        Serial.print(UNPSTRA(&PF_VARS[idx][PFVF_NAME]));
        if(idxA<10) {Serial.print('0');} Serial.print((int)idxA);
        Serial_print_P(pmSetSpaced);
        Serial.print(value);
        Serial.println();
      }
    } else {
      Serial.print(UNPSTRA(&PF_VARS[idx][PFVF_NAME]));
      Serial_print_P(pmSetSpaced);
      Serial.print(value);
      Serial.println();
    }
  }
  
  // Some fields require extra update code;
  uint16_t varName = pgm_read_word(&(PF_VARS[idx][PFVF_NAME]));
  if ( varName == (uint16_t)&pmConfPulseMode && trig==false) {
    reset_data();
  }
  if ( varName == (uint16_t)&pmConfAVRPin2Map) {
    if (pf_conf.avr_pin2_map == PIN2_TRIG_IN | pf_conf.avr_pin2_map == PIN2_FREQ_IN | pf_conf.avr_pin2_map == PIN2_FIRE_IN) {
      EIMSK |= (1 << INT0);   // Enable INT0 External Interrupt
    } else {
      EIMSK &= ~(1 << INT0);
    }
  }
  if ( varName == (uint16_t)&pmConfAVRPin3Map) {
    if (pf_conf.avr_pin3_map == PIN3_FREQ_IN | pf_conf.avr_pin3_map == PIN3_FIRE_IN) {
      EIMSK |= (1 << INT1);   // Enable INT0 External Interrupt
    } else {
      EIMSK &= ~(1 << INT1);
    }
  }
  if ( varName == (uint16_t)&pmConfPWMClock) {
    TCCR1B = pf_conf.pwm_clock & 7;
  }
  
  if (trig==false) {
    return value; // no update of triggers 
  }
  
  #ifdef SF_ENABLE_STV
  uint8_t stvIdxMax = stv_is_variable_mapped(idx,idxA,true);
  uint8_t stvIdxMin = stv_is_variable_mapped(idx,idxA,false);
  if (stvIdxMax != QMAP_VAR_IDX_ALL) {
    uint16_t warningLevel = pf_conf.stv_max_map[stvIdxMax][QMAP_VALUE_A];
    uint16_t errorLevel   = pf_conf.stv_max_map[stvIdxMax][QMAP_VALUE_B];
    if (errorLevel > ZERO && value >= errorLevel && pf_prog.stv_state != STV_STATE_ERROR_MAX && pf_prog.stv_state != STV_STATE_ERROR_MIN) {
      pf_prog.stv_state            = STV_STATE_ERROR_MAX;
      pf_prog.stv_time_cnt         = millis() + (pf_conf.stv_warn_secs*1000);
      pf_prog.stv_map_idx          = stvIdxMax;
      if (pf_conf.stv_error_mode != 0xFF) {
        pf_var_value_set(ONE,ZERO,ZERO,pf_conf.stv_error_mode);
      }
    } else if (warningLevel > ZERO && value >= warningLevel && pf_prog.stv_state == STV_STATE_OKE) {
      pf_prog.stv_state            = STV_STATE_WARNING_MAX;
      pf_prog.stv_time_cnt         = millis() + (pf_conf.stv_warn_secs*1000);
      pf_prog.stv_map_idx          = stvIdxMax;
      if (pf_conf.stv_warn_mode != 0xFF) {
        pf_prog.stv_mode_org         = pf_conf.pulse_mode;
        pf_var_value_set(ONE,ZERO,ZERO,pf_conf.stv_warn_mode);
      }
    }
  }
  if (stvIdxMin != QMAP_VAR_IDX_ALL) {
    uint16_t warningLevel = pf_conf.stv_min_map[stvIdxMin][QMAP_VALUE_A];
    uint16_t errorLevel   = pf_conf.stv_min_map[stvIdxMin][QMAP_VALUE_B];
    if (errorLevel > ZERO && value <= errorLevel && pf_prog.stv_state != STV_STATE_ERROR_MIN && pf_prog.stv_state != STV_STATE_ERROR_MAX) {
      pf_prog.stv_state            = STV_STATE_ERROR_MIN;
      pf_prog.stv_time_cnt         = millis() + (pf_conf.stv_warn_secs*1000);
      pf_prog.stv_map_idx          = stvIdxMin;
      if (pf_conf.stv_error_mode != 0xFF) {
        pf_var_value_set(ONE,ZERO,ZERO,pf_conf.stv_error_mode);
      }
    } else if (warningLevel > ZERO && value <= warningLevel && pf_prog.stv_state == STV_STATE_OKE) {
      pf_prog.stv_state            = STV_STATE_WARNING_MIN;
      pf_prog.stv_time_cnt         = millis() + (pf_conf.stv_warn_secs*1000);
      pf_prog.stv_mode_org         = pf_conf.pulse_mode;
      pf_prog.stv_map_idx          = stvIdxMin;
      if (pf_conf.stv_warn_mode != 0xFF) { 
        pf_var_value_set(ONE,ZERO,ZERO,pf_conf.stv_warn_mode);
      }
    }
  } 
  #endif
  #ifdef SF_ENABLE_FRQ
  if ( varName == (uint16_t)&pmCmdReqPWMFreq) {
    cmd_request_freq_train(value,QMAP_VAR_IDX_ALL,QMAP_VAR_IDX_ALL);
  }
  if ( varName == (uint16_t)&pmConfPWMDuty && pf_data.req_pwm_freq != ZERO) {
    cmd_request_freq_train(pf_data.req_pwm_freq,QMAP_VAR_IDX_ALL,QMAP_VAR_IDX_ALL);
  }
  #endif
  #ifdef SF_ENABLE_MAL
  if ( varName == (uint16_t)&pmDataMALTrig) {  
    mal_execute(idxA,value);
  }
  #endif
  #ifdef SF_ENABLE_VFC
  uint8_t vfcIdx = vfc_is_variable_mapped(idx,idxA);
  if (vfcIdx != QMAP_VAR_IDX_ALL) {
    uint16_t outVar   = pf_conf.vfc_output_map[vfcIdx][QMAP_VAR];
    uint16_t minLevel = pf_conf.vfc_input_map[vfcIdx][QMAP_VALUE_A];
    uint16_t maxLevel = pf_conf.vfc_input_map[vfcIdx][QMAP_VALUE_B];
    if (value > minLevel && value < maxLevel && outVar!=QMAP_VAR_NONE && outVar!=idx) {
      uint16_t minMapLevel = pf_conf.vfc_output_map[vfcIdx][QMAP_VALUE_A];
      uint16_t maxMapLevel = pf_conf.vfc_output_map[vfcIdx][QMAP_VALUE_B];
      uint16_t valueVfc = map(value,minLevel,maxLevel,minMapLevel,maxMapLevel);
      pf_var_value_set(outVar,pf_conf.vfc_output_map[vfcIdx][QMAP_VAR_IDX],ZERO,valueVfc);  // recursive function !!
    }
  }
  #endif
  return value; // return corrected value
}


#ifdef SF_ENABLE_VFC
uint8_t vfc_is_variable_mapped(uint8_t idx,uint8_t idxA) {
  for (uint8_t i=ZERO;i < VFC_MAP_MAX;i++) {
    uint16_t v = pf_conf.vfc_input_map[i][QMAP_VAR];
    if (v==QMAP_VAR_NONE) {
      continue;
    }
    if (v != idx) {
      continue;
    }
    if (Vars_isIndexA(idx)==false) {
      return i;
    }
    uint16_t vi = pf_conf.vfc_input_map[i][QMAP_VAR_IDX];
    if (vi == QMAP_VAR_IDX_ALL) {
      return i;
    }
    if (vi == idxA) {
      return i;
    }
  }
  return QMAP_VAR_IDX_ALL;
}
#endif


#ifdef SF_ENABLE_STV
uint8_t stv_is_variable_mapped(uint8_t idx,uint8_t idxA,boolean isMaxMap) {
  uint8_t maxMap = ZERO;
  if (isMaxMap) {
    maxMap = STV_MAX_MAP_MAX;
  } else {
    maxMap = STV_MIN_MAP_MAX;
  }
  for (uint8_t i=ZERO;i < maxMap;i++) {
    uint16_t v = ZERO;
    if (isMaxMap) {
      v = pf_conf.stv_max_map[i][QMAP_VAR];
    } else {
      v = pf_conf.stv_min_map[i][QMAP_VAR];
    }
    if (v==QMAP_VAR_NONE) {
      continue;
    }
    if (v != idx) {
      continue;
    }
    if (Vars_isIndexA(idx)==false) {
      return i;
    }
    uint16_t vi = ZERO;
    if (isMaxMap) {
      vi = pf_conf.stv_max_map[i][QMAP_VAR_IDX];
    } else {
      vi = pf_conf.stv_min_map[i][QMAP_VAR_IDX];
    }
    if (vi == QMAP_VAR_IDX_ALL) {
      return i;
    }
    if (vi == idxA) {
      return i;
    }
  }
  return QMAP_VAR_IDX_ALL;
}
#endif

// Init all config and data to init state.
void reset_config(void) {  
  // Reset all config values
  for (uint8_t i=ZERO;i < PF_VARS_SIZE;i++) {
    if (Vars_isTypeConf(i)==false) {
      continue;
    }
    pf_var_value_set_reset(i,QMAP_VAR_IDX_ALL,Vars_getDefaultValue(i));
    #ifdef SF_ENABLE_DEBUG
    Serial.print("ResetConf: ");
    Serial.print(UNPSTRA(&PF_VARS[i][PFVF_NAME]));
    Serial.print(" to: ");
    Serial.print(Vars_getDefaultValue(i));
    Serial.print(" setto: ");
    Serial.print(Vars_getValue(i,0,0));
    Serial.println();
    #endif
  }  
  
  pf_conf.sys_version          = PULSE_FIRE_VERSION;
  pf_conf.sys_struct_size      = sizeof(pf_conf_struct);
  #ifdef SF_ENABLE_PPM
  for (uint8_t i=ZERO;i < OUTPUT_MAX;i++) {
    pf_conf.ppm_data_a[i]      = i + DEFAULT_PPM_DATA; // example demo data
    pf_conf.ppm_data_b[i]      = OUTPUT_MAX-i + DEFAULT_PPM_DATA;
  }
  #endif
  #ifdef SF_ENABLE_MAL
  for (uint8_t n=ZERO;n < MAL_PROGRAM_MAX;n++) {
    for (uint8_t i=ZERO;i < MAL_PROGRAM_SIZE;i++) {
      pf_conf.mal_program[i][n] = 0xFF;
    }
  }
  #endif
}

// Reset runtime data
void reset_data(void) {  
  // Reset all data values
  for (uint8_t i=ZERO;i < PF_VARS_SIZE;i++) {
    if (Vars_isTypeData(i)==false) {
      continue;
    }
    pf_var_value_set_reset(i,QMAP_VAR_IDX_ALL,Vars_getDefaultValue(i));
    #ifdef SF_ENABLE_DEBUG
    Serial_print_P(PSTR("ResetData: "));
    Serial.print(UNPSTRA(&PF_VARS[i][PFVF_NAME]));
    Serial.print(" to: ");
    Serial.print(Vars_getDefaultValue(i));
    Serial.print(" setto: ");
    cmd_print_var(i,false,false);
    #endif
  }  

  // Reset custom
  pf_data.pulse_data           = pf_conf.pulse_init_a;
  pf_data.pulse_dir_cnt        = pf_conf.pulse_dir;  
  pf_data.pwm_loop_max         = pf_conf.pwm_loop;
  if (pf_conf.pulse_trig      == PULSE_TRIG_LOOP) {
    pf_data.pwm_state          = PWM_STATE_RUN;
  } else {
    pf_data.pwm_state          = PWM_STATE_IDLE;
  }
  #ifdef SF_ENABLE_SWC
  pf_data.swc_mode_org         = pf_conf.pulse_mode;
  #endif
}


// Setup all interal variables to init state
void setup_vars(void) {
  // Read or reset config to init state
  uint8_t  pfVersion    = eeprom_read_byte((uint8_t*)ZERO);
  uint16_t pfStructSize = eeprom_read_word((uint16_t*)ONE);
  if (pfVersion == PULSE_FIRE_VERSION && pfStructSize == sizeof(pf_conf_struct)) {
    Vars_readConfig();
  } else {
    reset_config(); // if newer/other/none version and/or size in flash then reset all to defaults and save.
    Vars_writeConfig();
  }
  // Reset data to init state
  reset_data();
  #ifdef SF_ENABLE_SWC
  if (pf_conf.swc_mode != ZERO) {
    pf_conf.pulse_mode = pf_conf.swc_mode; // run in startup mode once.
  }
  pf_data.swc_secs_cnt  = ONE; // only set on startup to 'one' so softstart code runs once.
  #endif
  
  // Reset prog to init state
  pf_prog.req_tx_push          = ZERO;
  pf_prog.req_tx_echo          = ONE;
  pf_prog.req_tx_promt         = ONE;
  pf_prog.cmd_buff_idx         = ZERO;
  #ifdef SF_ENABLE_LCD
  pf_prog.lcd_menu_state       = LCD_MENU_STATE_OFF;
  pf_prog.lcd_menu_mul         = ONE;
  pf_prog.lcd_menu_idx         = ZERO;
  pf_prog.lcd_menu_value_idx   = ZERO;
  pf_prog.lcd_menu_time_cnt    = ZERO;
  #endif
  #ifdef SF_ENABLE_MAL
  pf_prog.mal_pc               = ZERO;
  pf_prog.mal_state            = MAL_STATE_IDLE;
  for (uint8_t i=ZERO;i < OUTPUT_MAX;i++) {
    pf_prog.mal_var[i]         = ZERO;
  }
  #endif
  #ifdef SF_ENABLE_STV
  pf_prog.stv_state            = STV_STATE_OKE;
  pf_prog.stv_time_cnt         = ZERO;
  pf_prog.stv_mode_org         = pf_conf.pulse_mode;
  pf_prog.stv_map_idx          = ZERO;
  #endif
}

void setup_chip(void) {
  // Config pin2 which is multi function
  switch (pf_conf.avr_pin2_map) {
    case PIN2_RELAY_OUT:
    case PIN2_DOC2_OUT:
    case PIN2_DOC8_OUT:
      pinMode      (IO_DEF_PIN2_PIN,OUTPUT);
      break;
    default:
       pinMode      (IO_DEF_PIN3_PIN,INPUT);
       digitalWrite (IO_DEF_PIN3_PIN,HIGH);
       break;
  }
  switch (pf_conf.avr_pin3_map) {
    case PIN3_RELAY_OUT:
    case PIN3_DOC3_OUT:
    case PIN3_DOC9_OUT:
       pinMode      (IO_DEF_PIN3_PIN,OUTPUT);
       break;
    default:
       pinMode      (IO_DEF_PIN3_PIN,INPUT);
       digitalWrite (IO_DEF_PIN3_PIN,HIGH);
       break;
  }
  switch (pf_conf.avr_pin4_map) {
    case PIN4_RELAY_OUT:
    case PIN4_DOC4_OUT:
    case PIN4_DOC10_OUT:
       pinMode      (IO_DEF_PIN4_PIN,OUTPUT);
       break;    
    default:
       pinMode      (IO_DEF_PIN4_PIN,INPUT);
       digitalWrite (IO_DEF_PIN4_PIN,HIGH);
       break;
  }
  switch (pf_conf.avr_pin5_map) {
    case PIN5_RELAY_OUT:
    case PIN5_DOC5_OUT:
    case PIN5_DOC11_OUT:
       pinMode      (IO_DEF_PIN5_PIN,OUTPUT);
       break;    
    default:
       pinMode      (IO_DEF_PIN5_PIN,INPUT);
       digitalWrite (IO_DEF_PIN5_PIN,HIGH);
       break;
  }
    
  // Map LCD pins  
  #ifndef SF_ENABLE_EXT_LCD
    pinMode      (IO_DEF_LCD_RS_PIN,OUTPUT);
    pinMode      (IO_DEF_LCD_E_PIN, OUTPUT);
    pinMode      (IO_DEF_LCD_D0_PIN,OUTPUT);
    pinMode      (IO_DEF_LCD_D1_PIN,OUTPUT);
    pinMode      (IO_DEF_LCD_D2_PIN,OUTPUT);
    pinMode      (IO_DEF_LCD_D3_PIN,OUTPUT);    
  #else
    pinMode      (IO_EXT_INPUT0_PIN,INPUT);digitalWrite (IO_EXT_INPUT0_PIN,HIGH);
    pinMode      (IO_EXT_INPUT1_PIN,INPUT);digitalWrite (IO_EXT_INPUT1_PIN,HIGH);   
  #endif

  DDRB  = 0xFF; // Port B is in all connection modes always output
  int_send_output(PULSE_DATA_OFF); // send off state to output

  // 16bit timer used for pulse steps.
  ICR1 = 0xFFFF;OCR1A = 0xFFFF;OCR1B = 0xFFFF;
  TCCR1A = ZERO;
  TCCR1B = pf_conf.pwm_clock & 7;
  TIMSK1|= (ONE << OCF1A);
  TIMSK1|= (ONE << OCF1B);
  TCNT1  = ZERO;
  
  // 8bit timer used for freq/rpm calculation
  //OCR2A  = 0xFF;OCR2B  = 0xFF;
  //TCCR2A = ZERO;//TCCR2B = DEFAULT_STEP_CLOCK;
  //TIMSK2|= (ONE << TOIE2); //TCNT2  = ZERO;
  
  wdt_enable(WDT_MAIN_TIMEOUT); // enable watchdog timer, so if main loop to slow then reboot
  
  // Enable external interrupts on startup
  if (pf_conf.avr_pin2_map == PIN2_TRIG_IN | pf_conf.avr_pin2_map == PIN2_FREQ_IN | pf_conf.avr_pin2_map == PIN2_FIRE_IN) {
    EICRA |= (1 << ISC00);  // Falling-Edge Triggered INT0 for PIN2_TRIG_IN
    EIMSK |= (1 << INT0);   // Enable INT0 External Interrupt
  }
  if (pf_conf.avr_pin3_map == PIN3_FREQ_IN | pf_conf.avr_pin3_map == PIN3_FIRE_IN) {
    EICRA |= (1 << ISC01);  // Falling-Edge Triggered INT0 for PIN2_TRIG_IN
    EIMSK |= (1 << INT1);   // Enable INT1 External Interrupt
  } 
}

// Pin2 input via interrupts 
ISR(INT0_vect) {
  if (pf_conf.avr_pin2_map == PIN2_TRIG_IN) {
    if (pf_conf.pulse_trig != PULSE_TRIG_EXT) {
      return;
    }
    pf_data.pwm_state = PWM_STATE_RUN; // Trigger pulse train on external interrupt pin if pulse_trigger
    return;
  }
  if (pf_conf.avr_pin2_map == PIN2_FREQ_IN) {
    pf_data.dev_freq_cnt++;
    return;
  }
  if (pf_conf.avr_pin2_map == PIN2_FIRE_IN) {
    pf_var_value_set(Vars_getIndexFromName(UNPSTR(pmDataPulseFire)),ZERO,ZERO,ZERO);
    return;
  }
}
ISR(INT1_vect) {
  if (pf_conf.avr_pin3_map == PIN3_FREQ_IN) {
    pf_data.dev_freq_cnt++;
    return;
  }
  if (pf_conf.avr_pin3_map == PIN3_FIRE_IN) {
    pf_var_value_set(Vars_getIndexFromName(UNPSTR(pmDataPulseFire)),ZERO,ZERO,ZERO);
    return;
  }
}

#ifdef SF_ENABLE_STV
void loop_stv(void) {
  if (pf_prog.stv_state == STV_STATE_OKE) {
    return;
  }
  uint32_t current_time = millis();
  if (current_time < pf_prog.stv_time_cnt) {
    return;
  }
  pf_prog.stv_time_cnt = current_time + 1000; // check every second
  if (pf_prog.stv_state == STV_STATE_WARNING_MAX || pf_prog.stv_state == STV_STATE_ERROR_MAX) {
    uint16_t checkLevel = ZERO;
    uint8_t confWait = ZERO;
    if (pf_prog.stv_state == STV_STATE_WARNING_MAX) {
      checkLevel = pf_conf.stv_max_map[pf_prog.stv_map_idx][QMAP_VALUE_A];
      confWait = pf_conf.stv_warn_secs;
    } else {
      checkLevel = pf_conf.stv_max_map[pf_prog.stv_map_idx][QMAP_VALUE_B];
      confWait = pf_conf.stv_error_secs;
    }
    if (confWait == 0xFF) {
      return; // wait forever
    }
    uint16_t curValue = Vars_getValue(pf_conf.stv_max_map[pf_prog.stv_map_idx][QMAP_VAR],pf_conf.stv_max_map[pf_prog.stv_map_idx][QMAP_VAR_IDX],ZERO);
    if (curValue < checkLevel) {
      pf_prog.stv_wait_cnt++;
      if (pf_prog.stv_wait_cnt < confWait) {
        return; // waiting until timed recovery/
      }
      pf_prog.stv_wait_cnt         = ZERO;
      if (pf_prog.stv_state == STV_STATE_ERROR_MAX && curValue >= pf_conf.stv_max_map[pf_prog.stv_map_idx][QMAP_VALUE_A]) {
        pf_prog.stv_state            = STV_STATE_WARNING_MAX;
      } else {
        pf_prog.stv_state            = STV_STATE_OKE;
      }
      pf_prog.stv_time_cnt         = ZERO;
      pf_prog.stv_map_idx          = ZERO;
      if (pf_prog.stv_state == STV_STATE_WARNING_MAX) {
        if (pf_conf.stv_warn_mode != 0xFF) {
          pf_var_value_set(ONE,ZERO,ZERO,pf_prog.stv_mode_org);
        }
      } else {
        if (pf_conf.stv_error_mode != 0xFF) {
          pf_var_value_set(ONE,ZERO,ZERO,pf_prog.stv_mode_org);
        }
      }

    } else {
      pf_prog.stv_wait_cnt = ZERO; // reset waiting time
    }
    
  } else if (pf_prog.stv_state == STV_STATE_WARNING_MIN || pf_prog.stv_state == STV_STATE_ERROR_MIN) {
    uint16_t checkLevel = ZERO;
    uint8_t confWait = ZERO;
    if (pf_prog.stv_state == STV_STATE_WARNING_MIN) {
      checkLevel = pf_conf.stv_min_map[pf_prog.stv_map_idx][QMAP_VALUE_A];
      confWait = pf_conf.stv_warn_secs;
    } else {
      checkLevel = pf_conf.stv_min_map[pf_prog.stv_map_idx][QMAP_VALUE_B];
      confWait = pf_conf.stv_error_secs;
    }
    if (confWait == 0xFF) {
      return; // wait forever
    }
    uint16_t curValue = Vars_getValue(pf_conf.stv_min_map[pf_prog.stv_map_idx][QMAP_VAR],pf_conf.stv_min_map[pf_prog.stv_map_idx][QMAP_VAR_IDX],ZERO);
    if (curValue >= checkLevel) {
      pf_prog.stv_wait_cnt++;
      if (pf_prog.stv_wait_cnt < confWait) {
        return; // waiting until timed recovery/
      }
      pf_prog.stv_wait_cnt         = ZERO;
      if (pf_prog.stv_state == STV_STATE_ERROR_MIN && curValue <= pf_conf.stv_min_map[pf_prog.stv_map_idx][QMAP_VALUE_A]) {
        pf_prog.stv_state            = STV_STATE_WARNING_MIN;
      } else {
        pf_prog.stv_state            = STV_STATE_OKE;
      }
      pf_prog.stv_time_cnt         = ZERO;
      pf_prog.stv_map_idx          = ZERO;

     if (pf_prog.stv_state == STV_STATE_WARNING_MIN) {
        if (pf_conf.stv_warn_mode != 0xFF) {
          pf_var_value_set(ONE,ZERO,ZERO,pf_prog.stv_mode_org);
        }
      } else {
        if (pf_conf.stv_error_mode != 0xFF) {
          pf_var_value_set(ONE,ZERO,ZERO,pf_prog.stv_mode_org);
        }
      }
    } else {
      pf_prog.stv_wait_cnt = ZERO;
    }
  }
}
#endif



void setup() {
  setup_vars();
  setup_chip();
  setup_serial();
  #ifdef SF_ENABLE_LCD
  setup_lcd();
  #endif
}
 
void loop() {
  wdt_reset();
  pf_data.sys_main_loop_cnt++;
  loop_serial();
  #ifdef SF_ENABLE_ADC
  loop_input_adc();
  #endif
  #ifdef SF_ENABLE_DIC
  loop_input_dic();
  #endif
  #ifdef SF_ENABLE_LCD
  loop_input();
  loop_lcd();
  #endif  
  #ifdef SF_ENABLE_LPM
  loop_lpm();
  #endif
  #ifdef SF_ENABLE_STV
  loop_stv();
  #endif
  #ifdef SF_ENABLE_PTC
  loop_ptc();
  #endif
  #ifdef SF_ENABLE_FRQ
  loop_freq();
  #endif
}

// EOF


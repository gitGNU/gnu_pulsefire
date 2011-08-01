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

// Include singleton
#ifndef _STRINGS_H
#define _STRINGS_H

#include <avr/pgmspace.h>

// Strings
const char pmGetSpaced[]               PROGMEM = "==";
const char pmSetSpaced[]               PROGMEM = "=";
const char pmPulseFire[]               PROGMEM = "PulseFire ";
const char pmPromt[]                   PROGMEM = "root@pulsefire: ";
const char pmDone[]                    PROGMEM = "done";
const char pmLcdSelect[]               PROGMEM = "Select ";
const char pmLcdSelectIndex[]          PROGMEM = "index";
const char pmLcdSelectOption[]         PROGMEM = "option";
const char pmLcdSelectValue[]          PROGMEM = "value";
const char pmLcdSelectedOption[]       PROGMEM = "O: ";
const char pmLcdMultiply[]             PROGMEM = "M: ";
const char pmLcdValue[]                PROGMEM = "V: ";
const char pmLcdSoftStart[]            PROGMEM = "WARMUP";
const char pmLcdSTVWarning[]           PROGMEM = "WARNING";
const char pmLcdSTVError[]             PROGMEM = "ERROR";
const char pmLcdSTVMin[]               PROGMEM = " MIN ";
const char pmLcdSTVMax[]               PROGMEM = " MAX ";
const char pmLPMWait[]                 PROGMEM = "Waiting";
const char pmLPMStart[]                PROGMEM = "Starting";
const char pmLPMCancel[]               PROGMEM = "Canceled";
const char pmCmdUnknown[]              PROGMEM = "# Err: command unknown";
const char pmCmdHelpStart[]            PROGMEM = "# The commands are;\r\n";
const char pmCmdHelp[]                 PROGMEM = "help";
const char pmCmdHelpMax[]              PROGMEM = "max";
const char pmCmdHelpMap[]              PROGMEM = "map";
const char pmCmdHelpIdx[]              PROGMEM = "idx";
const char pmCmdHelpBits[]             PROGMEM = "bits";
const char pmCmdSave[]                 PROGMEM = "save";
const char pmCmdResetConfig[]          PROGMEM = "reset_conf";
const char pmCmdResetData[]            PROGMEM = "reset_data";
const char pmCmdResetChip[]            PROGMEM = "reset_chip";
const char pmCmdInfoConf[]             PROGMEM = "info_conf";
const char pmCmdInfoData[]             PROGMEM = "info_data";
const char pmCmdInfoProg[]             PROGMEM = "info_prog";
const char pmCmdInfoFreq[]             PROGMEM = "info_freq";
const char pmCmdInfoPPM[]              PROGMEM = "info_ppm";
const char pmCmdInfoChip[]             PROGMEM = "info_chip";
const char pmCmdReqPulseFire[]         PROGMEM = "req_pulse_fire";
const char pmCmdReqPWMFreq[]           PROGMEM = "req_pwm_freq";
const char pmCmdReqAutoLPM[]           PROGMEM = "req_auto_lpm";
const char pmConfDicMap[]              PROGMEM = "dic_map";
const char pmConfAdcMap[]              PROGMEM = "adc_map";
const char pmConfAdcJitter[]           PROGMEM = "adc_jitter";
const char pmConfAVRPin2Map[]          PROGMEM = "avr_pin2_map";
const char pmConfAVRPin3Map[]          PROGMEM = "avr_pin3_map";
const char pmConfAVRPin4Map[]          PROGMEM = "avr_pin4_map";
const char pmConfAVRPin5Map[]          PROGMEM = "avr_pin5_map";
const char pmConfSWCDelay[]            PROGMEM = "swc_delay";
const char pmConfSWCMode[]             PROGMEM = "swc_mode";
const char pmConfSWCSecs[]             PROGMEM = "swc_secs";
const char pmConfSWCDuty[]             PROGMEM = "swc_duty";
const char pmConfSWCTrig[]             PROGMEM = "swc_trig";
const char pmConfPulseEnable[]         PROGMEM = "pulse_enable";
const char pmConfPulseMode[]           PROGMEM = "pulse_mode";
const char pmConfPulseSteps[]          PROGMEM = "pulse_steps";
const char pmConfPulseTrig[]           PROGMEM = "pulse_trig";
const char pmConfPulseDir[]            PROGMEM = "pulse_dir";
const char pmConfPulseBank[]           PROGMEM = "pulse_bank";
const char pmConfPulseInv[]            PROGMEM = "pulse_inv";
const char pmConfPulseTrigDelay[]      PROGMEM = "pulse_trig_delay";
const char pmConfPulsePostDelay[]      PROGMEM = "pulse_post_delay";
const char pmConfPulseInitA[]          PROGMEM = "pulse_init_a";
const char pmConfPulseInitB[]          PROGMEM = "pulse_init_b";
const char pmConfPulseMaskA[]          PROGMEM = "pulse_mask_a";
const char pmConfPulseMaskB[]          PROGMEM = "pulse_mask_b";
const char pmConfPWMOnCntA[]           PROGMEM = "pwm_on_cnt_a";
const char pmConfPWMOnCntB[]           PROGMEM = "pwm_on_cnt_b";
const char pmConfPWMOffCntA[]          PROGMEM = "pwm_off_cnt_a";
const char pmConfPWMOffCntB[]          PROGMEM = "pwm_off_cnt_b";
const char pmConfPWMTuneCnt[]          PROGMEM = "pwm_tune_cnt";
const char pmConfPWMLoop[]             PROGMEM = "pwm_loop";
const char pmConfPWMLoopDelta[]        PROGMEM = "pwm_loop_delta";
const char pmConfPWMClock[]            PROGMEM = "pwm_clock";
const char pmConfPWMDuty[]             PROGMEM = "pwm_duty";
const char pmConfPPMDataOffset[]       PROGMEM = "ppm_data_offset";
const char pmConfPPMDataLength[]       PROGMEM = "ppm_data_len";
const char pmConfPPMDataA[]            PROGMEM = "ppm_data_a";
const char pmConfPPMDataB[]            PROGMEM = "ppm_data_b";
const char pmConfLPMStart[]            PROGMEM = "lpm_start";
const char pmConfLPMStop[]             PROGMEM = "lpm_stop";
const char pmConfLPMSize[]             PROGMEM = "lpm_size";
const char pmConfMALProgram[]          PROGMEM = "mal_program";
const char pmConfPTC0Run[]             PROGMEM = "ptc_0run";
const char pmConfPTC1Run[]             PROGMEM = "ptc_1run";
const char pmConfPTC0Mul[]             PROGMEM = "ptc_0mul";
const char pmConfPTC1Mul[]             PROGMEM = "ptc_1mul";
const char pmConfPTC0Map[]             PROGMEM = "ptc_0map";
const char pmConfPTC1Map[]             PROGMEM = "ptc_1map";
const char pmConfPTT0Map[]             PROGMEM = "ptt_0map";
const char pmConfPTT1Map[]             PROGMEM = "ptt_1map";
const char pmConfPTT2Map[]             PROGMEM = "ptt_2map";
const char pmConfPTT3Map[]             PROGMEM = "ptt_3map";
const char pmConfSTVWarnSecs[]         PROGMEM = "stv_warn_secs";
const char pmConfSTVWarnMode[]         PROGMEM = "stv_warn_mode";
const char pmConfSTVErrorSecs[]        PROGMEM = "stv_error_secs";
const char pmConfSTVErrorMode[]        PROGMEM = "stv_error_mode";
const char pmConfSTVMaxMap[]           PROGMEM = "stv_max_map";
const char pmConfSTVMinMap[]           PROGMEM = "stv_min_map";
const char pmConfVFCInputMap[]         PROGMEM = "vfc_input_map";
const char pmConfVFCOutputMap[]        PROGMEM = "vfc_output_map";
const char pmDataSysUptime[]           PROGMEM = "sys_uptime";
const char pmDataSysMainLoopCnt[]      PROGMEM = "sys_main_loop_cnt";
const char pmDataLcdTimeCnt[]          PROGMEM = "lcd_time_cnt";
const char pmDataLcdPage[]             PROGMEM = "lcd_page";
const char pmDataLcdRedraw[]           PROGMEM = "lcd_redraw";
const char pmDataAdcTimeCnt[]          PROGMEM = "adc_time_cnt";
const char pmDataAdcValue[]            PROGMEM = "adc_value";
const char pmDataDicTimeCnt[]          PROGMEM = "dic_time_cnt";
const char pmDataDicValue[]            PROGMEM = "dic_value";
const char pmDataDocPort[]             PROGMEM = "doc_port";
const char pmDataSysInputTimeCnt[]     PROGMEM = "sys_input_time_cnt";
const char pmDataSWCModeOrg[]          PROGMEM = "swc_mode_org";
const char pmDataSWCSecsCnt[]          PROGMEM = "swc_secs_cnt";
const char pmDataSWCDutyCnt[]          PROGMEM = "swc_duty_cnt";
const char pmDataLPMState[]            PROGMEM = "lpm_state";
const char pmDataLPMStartTime[]        PROGMEM = "lpm_start_time";
const char pmDataLPMTotalTime[]        PROGMEM = "lpm_total_time";
const char pmDataLPMResult[]           PROGMEM = "lpm_result";
const char pmDataLPMLevel[]            PROGMEM = "lpm_level";
const char pmDataLPMFreqSettle[]       PROGMEM = "lpm_freq_settle";
const char pmDataLPMFreqStart[]        PROGMEM = "lpm_freq_start";
const char pmDataLPMFreqStop[]         PROGMEM = "lpm_freq_stop";
const char pmDataLPMFreqStep[]         PROGMEM = "lpm_freq_step";
const char pmDataPTCSysCnt[]           PROGMEM = "ptc_sys_cnt";
const char pmDataPTC0Cnt[]             PROGMEM = "ptc_0cnt";
const char pmDataPTC1Cnt[]             PROGMEM = "ptc_1cnt";
const char pmDataPTC0RunCnt[]          PROGMEM = "ptc_0run_cnt";
const char pmDataPTC1RunCnt[]          PROGMEM = "ptc_1run_cnt";
const char pmDataPTC0MapIdx[]          PROGMEM = "ptc_0map_idx";
const char pmDataPTC1MapIdx[]          PROGMEM = "ptc_1map_idx";
const char pmDataPTC0MulCnt[]          PROGMEM = "ptc_0mul_cnt";
const char pmDataPTC1MulCnt[]          PROGMEM = "ptc_1mul_cnt";
const char pmDataPTTIdx[]              PROGMEM = "ptt_idx";
const char pmDataPTTCnt[]              PROGMEM = "ptt_cnt";
const char pmDataPTTFire[]             PROGMEM = "ptt_fire";
const char pmDataDevVolt[]             PROGMEM = "dev_volt";
const char pmDataDevAmp[]              PROGMEM = "dev_amp";
const char pmDataDevTemp[]             PROGMEM = "dev_temp";
const char pmDataDevFreq[]             PROGMEM = "dev_freq";
const char pmDataDevFreqCnt[]          PROGMEM = "dev_freq_cnt";
const char pmDataDevVar[]              PROGMEM = "dev_var";
const char pmDataPulseFire[]           PROGMEM = "pulse_fire";
const char pmDataPulseStep[]           PROGMEM = "pulse_step";
const char pmDataPulseData[]           PROGMEM = "pulse_data";
const char pmDataPulseBankCnt[]        PROGMEM = "pulse_bank_cnt";
const char pmDataPulseDirCnt[]         PROGMEM = "pulse_dir_cnt";
const char pmDataPulseTrigDelayCnt[]   PROGMEM = "pulse_trig_delay_cnt";
const char pmDataPulsePostDelayCnt[]   PROGMEM = "pulse_post_delay_cnt";
const char pmDataPWMState[]            PROGMEM = "pwm_state";
const char pmDataPWMLoopCnt[]          PROGMEM = "pwm_loop_cnt";
const char pmDataPWMLoopMax[]          PROGMEM = "pwm_loop_max";
const char pmDataPPMIdx[]              PROGMEM = "ppm_idx";
const char pmDataMALTrig[]             PROGMEM = "mal_trig";
const char pmChipVersion[]             PROGMEM = "chip_version";
const char pmChipConfMax[]             PROGMEM = "chip_conf_max";
const char pmChipConfSize[]            PROGMEM = "chip_conf_size";
const char pmChipFreeSram[]            PROGMEM = "chip_free_sram";
const char pmChipCPUFreq[]             PROGMEM = "chip_cpu_freq";
const char pmChipCPUType[]             PROGMEM = "chip_cpu_type";
const char pmChipCPUTypeAVR[]          PROGMEM = "AVR";
const char pmChipFlags[]               PROGMEM = "chip_flags";
const char pmChipFlagLCD[]             PROGMEM = "LCD ";
const char pmChipFlagLPM[]             PROGMEM = "LPM ";
const char pmChipFlagPPM[]             PROGMEM = "PPM ";
const char pmChipFlagADC[]             PROGMEM = "ADC ";
const char pmChipFlagDIC[]             PROGMEM = "DIC ";
const char pmChipFlagDOC[]             PROGMEM = "DOC ";
const char pmChipFlagDEV[]             PROGMEM = "DEV ";
const char pmChipFlagPTC[]             PROGMEM = "PTC ";
const char pmChipFlagPTT[]             PROGMEM = "PTT ";
const char pmChipFlagSTV[]             PROGMEM = "STV ";
const char pmChipFlagVFC[]             PROGMEM = "VFC ";
const char pmChipFlagFRQ[]             PROGMEM = "FRQ ";
const char pmChipFlagSWC[]             PROGMEM = "SWC ";
const char pmChipFlagMAL[]             PROGMEM = "MAL ";
const char pmChipFlagDEBUG[]           PROGMEM = "DEBUG ";
const char pmChipBuild[]               PROGMEM = "chip_build";
const char pmChipBuildDate[]           PROGMEM = __TIMESTAMP__;
const char pmChipName[]                PROGMEM = "chip_name";
const char pmChipNameStr[]             PROGMEM = CHIP_INFO_NAME;
const char pmChipNameId[]              PROGMEM = "chip_name_id";
const char pmChipNameIdStr[]           PROGMEM = CHIP_INFO_NAME_ID;
const char pmFreqPWMData[]             PROGMEM = "freq_pwm_data";
const char pmProgLcdMenuState[]        PROGMEM = "lcd_menu_state";
const char pmProgLcdMenuMul[]          PROGMEM = "lcd_menu_mul";
const char pmProgLcdMenuIdx[]          PROGMEM = "lcd_menu_idx";
const char pmProgLcdMenuValueIdx[]     PROGMEM = "lcd_menu_value_idx";
const char pmProgLcdMenuTimeCnt[]      PROGMEM = "lcd_menu_time_cnt";
const char pmProgMALPc[]               PROGMEM = "mal_pc";
const char pmProgMALState[]            PROGMEM = "mal_state";
const char pmProgMALVar[]              PROGMEM = "mal_var";
const char pmProgTXPush[]              PROGMEM = "req_tx_push";
const char pmProgTXEcho[]              PROGMEM = "req_tx_echo";
const char pmProgTXPromt[]             PROGMEM = "req_tx_promt";
const char pmProgSTVState[]            PROGMEM = "stv_state";
const char pmProgSTVTimeCnt[]          PROGMEM = "stv_time_cnt";
const char pmProgSTVModeOrg[]          PROGMEM = "stv_mode_org";
const char pmProgSTVMapIdx[]           PROGMEM = "stv_map_idx";

// end include
#endif#endif

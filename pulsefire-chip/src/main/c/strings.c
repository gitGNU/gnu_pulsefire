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

#include "strings.h"


// Strings
const char pmGetSpaced[]               CHIP_PROGMEM = "==";
const char pmSetSpaced[]               CHIP_PROGMEM = "=";
const char pmPulseFire[]               CHIP_PROGMEM = "PulseFire ";
const char pmPromt[]                   CHIP_PROGMEM = "root@pulsefire: ";
const char pmDone[]                    CHIP_PROGMEM = "done";
const char pmLcdSelect[]               CHIP_PROGMEM = "Select ";
const char pmLcdSelectIndex[]          CHIP_PROGMEM = "index";
const char pmLcdSelectOption[]         CHIP_PROGMEM = "option";
const char pmLcdSelectValue[]          CHIP_PROGMEM = "value";
const char pmLcdSelectedOption[]       CHIP_PROGMEM = "O: ";
const char pmLcdMultiply[]             CHIP_PROGMEM = "M: ";
const char pmLcdValue[]                CHIP_PROGMEM = "V: ";
const char pmLcdSoftStart[]            CHIP_PROGMEM = "WARMUP";
const char pmLcdSTVWarning[]           CHIP_PROGMEM = "WARNING";
const char pmLcdSTVError[]             CHIP_PROGMEM = "ERROR";
const char pmLcdSTVMin[]               CHIP_PROGMEM = " MIN ";
const char pmLcdSTVMax[]               CHIP_PROGMEM = " MAX ";
const char pmLPMWait[]                 CHIP_PROGMEM = "Waiting";
const char pmLPMStart[]                CHIP_PROGMEM = "Starting";
const char pmLPMCancel[]               CHIP_PROGMEM = "Canceled";
const char pmCmdUnknown[]              CHIP_PROGMEM = "# Err: command unknown";
const char pmCmdHelpStart[]            CHIP_PROGMEM = "# The commands are;\r\n";
const char pmCmdHelp[]                 CHIP_PROGMEM = "help";
const char pmCmdHelpMax[]              CHIP_PROGMEM = "max";
const char pmCmdHelpMap[]              CHIP_PROGMEM = "map";
const char pmCmdHelpIdx[]              CHIP_PROGMEM = "idx";
const char pmCmdHelpBits[]             CHIP_PROGMEM = "bits";
const char pmCmdSave[]                 CHIP_PROGMEM = "save";
const char pmCmdResetConfig[]          CHIP_PROGMEM = "reset_conf";
const char pmCmdResetData[]            CHIP_PROGMEM = "reset_data";
const char pmCmdResetChip[]            CHIP_PROGMEM = "reset_chip";
const char pmCmdInfoConf[]             CHIP_PROGMEM = "info_conf";
const char pmCmdInfoData[]             CHIP_PROGMEM = "info_data";
const char pmCmdInfoProg[]             CHIP_PROGMEM = "info_prog";
const char pmCmdInfoFreq[]             CHIP_PROGMEM = "info_freq";
const char pmCmdInfoPPM[]              CHIP_PROGMEM = "info_ppm";
const char pmCmdInfoChip[]             CHIP_PROGMEM = "info_chip";
const char pmCmdReqPulseFire[]         CHIP_PROGMEM = "req_pulse_fire";
const char pmCmdReqPWMFreq[]           CHIP_PROGMEM = "req_pwm_freq";
const char pmCmdReqAutoLPM[]           CHIP_PROGMEM = "req_auto_lpm";
const char pmCmdReqPTTFire[]           CHIP_PROGMEM = "req_ptt_fire";
const char pmConfLCDSize[]             CHIP_PROGMEM = "lcd_size";
const char pmConfDicMap[]              CHIP_PROGMEM = "dic_map";
const char pmConfDicEnable[]           CHIP_PROGMEM = "dic_enable";
const char pmConfDicInv[]              CHIP_PROGMEM = "dic_inv";
const char pmConfDicSync[]             CHIP_PROGMEM = "dic_sync";
const char pmConfAdcMap[]              CHIP_PROGMEM = "adc_map";
const char pmConfAdcEnable[]           CHIP_PROGMEM = "adc_enable";
const char pmConfAdcJitter[]           CHIP_PROGMEM = "adc_jitter";
const char pmConfAVRPin2Map[]          CHIP_PROGMEM = "avr_pin2_map";
const char pmConfAVRPin3Map[]          CHIP_PROGMEM = "avr_pin3_map";
const char pmConfAVRPin4Map[]          CHIP_PROGMEM = "avr_pin4_map";
const char pmConfAVRPin5Map[]          CHIP_PROGMEM = "avr_pin5_map";
const char pmConfAVRPin18Map[]         CHIP_PROGMEM = "avr_pin18_map";
const char pmConfAVRPin19Map[]         CHIP_PROGMEM = "avr_pin19_map";
const char pmConfAVRPin47Map[]         CHIP_PROGMEM = "avr_pin47_map";
const char pmConfAVRPin48Map[]         CHIP_PROGMEM = "avr_pin48_map";
const char pmConfAVRPin49Map[]         CHIP_PROGMEM = "avr_pin49_map";
const char pmConfSWCDelay[]            CHIP_PROGMEM = "swc_delay";
const char pmConfSWCMode[]             CHIP_PROGMEM = "swc_mode";
const char pmConfSWCSecs[]             CHIP_PROGMEM = "swc_secs";
const char pmConfSWCDuty[]             CHIP_PROGMEM = "swc_duty";
const char pmConfSWCTrig[]             CHIP_PROGMEM = "swc_trig";
const char pmConfPulseEnable[]         CHIP_PROGMEM = "pulse_enable";
const char pmConfPulseMode[]           CHIP_PROGMEM = "pulse_mode";
const char pmConfPulseSteps[]          CHIP_PROGMEM = "pulse_steps";
const char pmConfPulseTrig[]           CHIP_PROGMEM = "pulse_trig";
const char pmConfPulseDir[]            CHIP_PROGMEM = "pulse_dir";
const char pmConfPulseBank[]           CHIP_PROGMEM = "pulse_bank";
const char pmConfPulseInv[]            CHIP_PROGMEM = "pulse_inv";
const char pmConfPulseTrigDelay[]      CHIP_PROGMEM = "pulse_trig_delay";
const char pmConfPulsePostDelay[]      CHIP_PROGMEM = "pulse_post_delay";
const char pmConfPulseInitA[]          CHIP_PROGMEM = "pulse_init_a";
const char pmConfPulseInitB[]          CHIP_PROGMEM = "pulse_init_b";
const char pmConfPulseMaskA[]          CHIP_PROGMEM = "pulse_mask_a";
const char pmConfPulseMaskB[]          CHIP_PROGMEM = "pulse_mask_b";
const char pmConfPWMOnCntA[]           CHIP_PROGMEM = "pwm_on_cnt_a";
const char pmConfPWMOnCntB[]           CHIP_PROGMEM = "pwm_on_cnt_b";
const char pmConfPWMOffCntA[]          CHIP_PROGMEM = "pwm_off_cnt_a";
const char pmConfPWMOffCntB[]          CHIP_PROGMEM = "pwm_off_cnt_b";
const char pmConfPWMTuneCnt[]          CHIP_PROGMEM = "pwm_tune_cnt";
const char pmConfPWMLoop[]             CHIP_PROGMEM = "pwm_loop";
const char pmConfPWMLoopDelta[]        CHIP_PROGMEM = "pwm_loop_delta";
const char pmConfPWMClock[]            CHIP_PROGMEM = "pwm_clock";
const char pmConfPWMDuty[]             CHIP_PROGMEM = "pwm_duty";
const char pmConfPPMDataOffset[]       CHIP_PROGMEM = "ppm_data_offset";
const char pmConfPPMDataLength[]       CHIP_PROGMEM = "ppm_data_len";
const char pmConfPPMDataA[]            CHIP_PROGMEM = "ppm_data_a";
const char pmConfPPMDataB[]            CHIP_PROGMEM = "ppm_data_b";
const char pmConfLPMStart[]            CHIP_PROGMEM = "lpm_start";
const char pmConfLPMStop[]             CHIP_PROGMEM = "lpm_stop";
const char pmConfLPMSize[]             CHIP_PROGMEM = "lpm_size";
const char pmConfLPMRelayInv[]         CHIP_PROGMEM = "lpm_relay_inv";
const char pmConfMALProgram[]          CHIP_PROGMEM = "mal_program";
const char pmConfPTC0Run[]             CHIP_PROGMEM = "ptc_0run";
const char pmConfPTC1Run[]             CHIP_PROGMEM = "ptc_1run";
const char pmConfPTC0Mul[]             CHIP_PROGMEM = "ptc_0mul";
const char pmConfPTC1Mul[]             CHIP_PROGMEM = "ptc_1mul";
const char pmConfPTC0Map[]             CHIP_PROGMEM = "ptc_0map";
const char pmConfPTC1Map[]             CHIP_PROGMEM = "ptc_1map";
const char pmConfPTT0Map[]             CHIP_PROGMEM = "ptt_0map";
const char pmConfPTT1Map[]             CHIP_PROGMEM = "ptt_1map";
const char pmConfPTT2Map[]             CHIP_PROGMEM = "ptt_2map";
const char pmConfPTT3Map[]             CHIP_PROGMEM = "ptt_3map";
const char pmProgDevVoltDot[]          CHIP_PROGMEM = "dev_volt_dot";
const char pmProgDevAmpDot[]           CHIP_PROGMEM = "dev_amp_dot";
const char pmProgDevTempDot[]          CHIP_PROGMEM = "dev_temp_dot";
const char pmConfSTVWarnSecs[]         CHIP_PROGMEM = "stv_warn_secs";
const char pmConfSTVWarnMode[]         CHIP_PROGMEM = "stv_warn_mode";
const char pmConfSTVErrorSecs[]        CHIP_PROGMEM = "stv_error_secs";
const char pmConfSTVErrorMode[]        CHIP_PROGMEM = "stv_error_mode";
const char pmConfSTVMaxMap[]           CHIP_PROGMEM = "stv_max_map";
const char pmConfSTVMinMap[]           CHIP_PROGMEM = "stv_min_map";
const char pmConfVFCInputMap[]         CHIP_PROGMEM = "vfc_input_map";
const char pmConfVFCOutputMap[]        CHIP_PROGMEM = "vfc_output_map";
const char pmDataSysUptime[]           CHIP_PROGMEM = "sys_uptime";
const char pmDataSysMainLoopCnt[]      CHIP_PROGMEM = "sys_main_loop_cnt";
const char pmDataLcdTimeCnt[]          CHIP_PROGMEM = "lcd_time_cnt";
const char pmDataLcdPage[]             CHIP_PROGMEM = "lcd_page";
const char pmDataLcdRedraw[]           CHIP_PROGMEM = "lcd_redraw";
const char pmDataAdcTimeCnt[]          CHIP_PROGMEM = "adc_time_cnt";
const char pmDataAdcValue[]            CHIP_PROGMEM = "adc_value";
const char pmDataAdcState[]            CHIP_PROGMEM = "adc_state";
const char pmDataAdcStateIdx[]         CHIP_PROGMEM = "adc_state_idx";
const char pmDataAdcStateValue[]       CHIP_PROGMEM = "adc_state_value";
const char pmDataDicTimeCnt[]          CHIP_PROGMEM = "dic_time_cnt";
const char pmDataDicValue[]            CHIP_PROGMEM = "dic_value";
const char pmDataDocPort[]             CHIP_PROGMEM = "doc_port";
const char pmDataSysInputTimeCnt[]     CHIP_PROGMEM = "sys_input_time_cnt";
const char pmDataSWCModeOrg[]          CHIP_PROGMEM = "swc_mode_org";
const char pmDataSWCSecsCnt[]          CHIP_PROGMEM = "swc_secs_cnt";
const char pmDataSWCDutyCnt[]          CHIP_PROGMEM = "swc_duty_cnt";
const char pmDataLPMState[]            CHIP_PROGMEM = "lpm_state";
const char pmDataLPMStartTime[]        CHIP_PROGMEM = "lpm_start_time";
const char pmDataLPMTotalTime[]        CHIP_PROGMEM = "lpm_total_time";
const char pmDataLPMResult[]           CHIP_PROGMEM = "lpm_result";
const char pmDataLPMLevel[]            CHIP_PROGMEM = "lpm_level";
const char pmDataLPMFreqSettle[]       CHIP_PROGMEM = "lpm_freq_settle";
const char pmDataLPMFreqStart[]        CHIP_PROGMEM = "lpm_freq_start";
const char pmDataLPMFreqStop[]         CHIP_PROGMEM = "lpm_freq_stop";
const char pmDataLPMFreqStep[]         CHIP_PROGMEM = "lpm_freq_step";
const char pmDataPTCSysCnt[]           CHIP_PROGMEM = "ptc_sys_cnt";
const char pmDataPTC0Cnt[]             CHIP_PROGMEM = "ptc_0cnt";
const char pmDataPTC1Cnt[]             CHIP_PROGMEM = "ptc_1cnt";
const char pmDataPTC0RunCnt[]          CHIP_PROGMEM = "ptc_0run_cnt";
const char pmDataPTC1RunCnt[]          CHIP_PROGMEM = "ptc_1run_cnt";
const char pmDataPTC0MapIdx[]          CHIP_PROGMEM = "ptc_0map_idx";
const char pmDataPTC1MapIdx[]          CHIP_PROGMEM = "ptc_1map_idx";
const char pmDataPTC0MulCnt[]          CHIP_PROGMEM = "ptc_0mul_cnt";
const char pmDataPTC1MulCnt[]          CHIP_PROGMEM = "ptc_1mul_cnt";
const char pmDataPTTIdx[]              CHIP_PROGMEM = "ptt_idx";
const char pmDataPTTCnt[]              CHIP_PROGMEM = "ptt_cnt";
const char pmDataPTTFire[]             CHIP_PROGMEM = "ptt_fire";
const char pmDataDevVolt[]             CHIP_PROGMEM = "dev_volt";
const char pmDataDevAmp[]              CHIP_PROGMEM = "dev_amp";
const char pmDataDevTemp[]             CHIP_PROGMEM = "dev_temp";
const char pmDataDevFreq[]             CHIP_PROGMEM = "dev_freq";
const char pmDataDevFreqCnt[]          CHIP_PROGMEM = "dev_freq_cnt";
const char pmDataDevVar[]              CHIP_PROGMEM = "dev_var";
const char pmDataPulseFire[]           CHIP_PROGMEM = "pulse_fire";
const char pmDataPulseStep[]           CHIP_PROGMEM = "pulse_step";
const char pmDataPulseData[]           CHIP_PROGMEM = "pulse_data";
const char pmDataPulseBankCnt[]        CHIP_PROGMEM = "pulse_bank_cnt";
const char pmDataPulseDirCnt[]         CHIP_PROGMEM = "pulse_dir_cnt";
const char pmDataPulseTrigDelayCnt[]   CHIP_PROGMEM = "pulse_trig_delay_cnt";
const char pmDataPulsePostDelayCnt[]   CHIP_PROGMEM = "pulse_post_delay_cnt";
const char pmDataPWMState[]            CHIP_PROGMEM = "pwm_state";
const char pmDataPWMLoopCnt[]          CHIP_PROGMEM = "pwm_loop_cnt";
const char pmDataPWMLoopMax[]          CHIP_PROGMEM = "pwm_loop_max";
const char pmDataPWMReqFreq[]          CHIP_PROGMEM = "pwm_req_freq";
const char pmDataPPMIdx[]              CHIP_PROGMEM = "ppm_idx";
const char pmDataMALTrig[]             CHIP_PROGMEM = "mal_trig";
const char pmChipVersion[]             CHIP_PROGMEM = "chip_version";
const char pmChipConfMax[]             CHIP_PROGMEM = "chip_conf_max";
const char pmChipConfSize[]            CHIP_PROGMEM = "chip_conf_size";
const char pmChipFreeSram[]            CHIP_PROGMEM = "chip_free_sram";
const char pmChipCPUFreq[]             CHIP_PROGMEM = "chip_cpu_freq";
const char pmChipCPUType[]             CHIP_PROGMEM = "chip_cpu_type";
const char pmChipCPUTypeAvr[]          CHIP_PROGMEM = "AVR";
const char pmChipCPUTypeAvrMega[]      CHIP_PROGMEM = "AVR_MEGA";
const char pmChipCPUTypeArm7m[]        CHIP_PROGMEM = "ARM_7M";
const char pmChipFlags[]               CHIP_PROGMEM = "chip_flags";
const char pmChipFlagPWM[]             CHIP_PROGMEM = "PWM ";
const char pmChipFlagLCD[]             CHIP_PROGMEM = "LCD ";
const char pmChipFlagLPM[]             CHIP_PROGMEM = "LPM ";
const char pmChipFlagPPM[]             CHIP_PROGMEM = "PPM ";
const char pmChipFlagADC[]             CHIP_PROGMEM = "ADC ";
const char pmChipFlagDIC[]             CHIP_PROGMEM = "DIC ";
const char pmChipFlagDOC[]             CHIP_PROGMEM = "DOC ";
const char pmChipFlagDEV[]             CHIP_PROGMEM = "DEV ";
const char pmChipFlagPTC[]             CHIP_PROGMEM = "PTC ";
const char pmChipFlagPTT[]             CHIP_PROGMEM = "PTT ";
const char pmChipFlagSTV[]             CHIP_PROGMEM = "STV ";
const char pmChipFlagVFC[]             CHIP_PROGMEM = "VFC ";
const char pmChipFlagSWC[]             CHIP_PROGMEM = "SWC ";
const char pmChipFlagMAL[]             CHIP_PROGMEM = "MAL ";
const char pmChipFlagDEBUG[]           CHIP_PROGMEM = "DEBUG ";
const char pmChipBuild[]               CHIP_PROGMEM = "chip_build";
const char pmChipBuildDate[]           CHIP_PROGMEM = __TIMESTAMP__;
const char pmChipName[]                CHIP_PROGMEM = "chip_name";
const char pmChipNameStr[]             CHIP_PROGMEM = CHIP_INFO_NAME;
const char pmChipNameId[]              CHIP_PROGMEM = "chip_name_id";
const char pmChipNameIdStr[]           CHIP_PROGMEM = CHIP_INFO_NAME_ID;
const char pmFreqPWMData[]             CHIP_PROGMEM = "freq_pwm_data";
const char pmProgSysTimeTicks[]        CHIP_PROGMEM = "sys_time_ticks";
const char pmProgSysTimeSsec[]         CHIP_PROGMEM = "sys_time_ssec";
const char pmProgLcdMenuState[]        CHIP_PROGMEM = "lcd_menu_state";
const char pmProgLcdMenuMul[]          CHIP_PROGMEM = "lcd_menu_mul";
const char pmProgLcdMenuIdx[]          CHIP_PROGMEM = "lcd_menu_idx";
const char pmProgLcdMenuValueIdx[]     CHIP_PROGMEM = "lcd_menu_value_idx";
const char pmProgLcdMenuTimeCnt[]      CHIP_PROGMEM = "lcd_menu_time_cnt";
const char pmProgMALPc[]               CHIP_PROGMEM = "mal_pc";
const char pmProgMALState[]            CHIP_PROGMEM = "mal_state";
const char pmProgMALVar[]              CHIP_PROGMEM = "mal_var";
const char pmProgTXPush[]              CHIP_PROGMEM = "req_tx_push";
const char pmProgTXEcho[]              CHIP_PROGMEM = "req_tx_echo";
const char pmProgTXPromt[]             CHIP_PROGMEM = "req_tx_promt";
const char pmProgSTVState[]            CHIP_PROGMEM = "stv_state";
const char pmProgSTVTimeCnt[]          CHIP_PROGMEM = "stv_time_cnt";
const char pmProgSTVModeOrg[]          CHIP_PROGMEM = "stv_mode_org";
const char pmProgSTVMapIdx[]           CHIP_PROGMEM = "stv_map_idx";



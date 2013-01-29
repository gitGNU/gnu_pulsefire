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

//
// All strings in pulsefire (except debug stuff)
//
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
const char pmLPMDone[]                 CHIP_PROGMEM = "lpm_done"; // exception; used as serial event cmd.
const char pmCmdUnknown[]              CHIP_PROGMEM = "# Err: command unknown";
const char pmCmdHelpStart[]            CHIP_PROGMEM = "# The commands are;\r\n";
const char pmCmdHelp[]                 CHIP_PROGMEM = "help";
const char pmCmdSave[]                 CHIP_PROGMEM = "save";
const char pmCmdResetConfig[]          CHIP_PROGMEM = "reset_conf";
const char pmCmdResetData[]            CHIP_PROGMEM = "reset_data";
const char pmCmdResetChip[]            CHIP_PROGMEM = "reset_chip";
const char pmCmdInfoConf[]             CHIP_PROGMEM = "info_conf";
const char pmCmdInfoData[]             CHIP_PROGMEM = "info_data";
const char pmCmdInfoFreq[]             CHIP_PROGMEM = "info_freq";
const char pmCmdInfoFreqData[]         CHIP_PROGMEM = "info_freq_data";
const char pmCmdInfoPPM[]              CHIP_PROGMEM = "info_ppm";
const char pmCmdInfoPWM[]              CHIP_PROGMEM = "info_pwm";
const char pmCmdInfoPWMData[]          CHIP_PROGMEM = "info_pwm_data";
const char pmCmdInfoPWMSteps[]         CHIP_PROGMEM = "info_pwm_steps";
const char pmCmdInfoChip[]             CHIP_PROGMEM = "info_chip";
const char pmCmdInfoVars[]             CHIP_PROGMEM = "info_vars";
const char pmCmdInfoVarsMap[]          CHIP_PROGMEM = "map";
const char pmCmdInfoVarsPrefix[]       CHIP_PROGMEM = "@";
const char pmCmdReqTrigger[]           CHIP_PROGMEM = "req_trigger";
const char pmCmdReqDoc[]               CHIP_PROGMEM = "req_doc";
const char pmConfSysId[]               CHIP_PROGMEM = "sys_id";
const char pmConfSysPass[]             CHIP_PROGMEM = "sys_pass";
const char pmConfSpiClock[]            CHIP_PROGMEM = "spi_clock";
const char pmConfSpiChips[]            CHIP_PROGMEM = "spi_chips";
const char pmConfLCDSize[]             CHIP_PROGMEM = "lcd_size";
const char pmConfLCDDefp[]             CHIP_PROGMEM = "lcd_defp";
const char pmConfLCDMode[]             CHIP_PROGMEM = "lcd_mode";
const char pmConfLCDHcd[]              CHIP_PROGMEM = "lcd_hcd";
const char pmConfLCDPlp[]              CHIP_PROGMEM = "lcd_plp";
const char pmConfIntMap[]              CHIP_PROGMEM = "int_map";
const char pmConfInt0Mode[]            CHIP_PROGMEM = "int_0mode";
const char pmConfInt0Trig[]            CHIP_PROGMEM = "int_0trig";
const char pmConfInt0FreqMul[]         CHIP_PROGMEM = "int_0freq_mul";
const char pmConfInt1Mode[]            CHIP_PROGMEM = "int_1mode";
const char pmConfInt1Trig[]            CHIP_PROGMEM = "int_1trig";
const char pmConfInt1FreqMul[]         CHIP_PROGMEM = "int_1freq_mul";
const char pmConfDicMap[]              CHIP_PROGMEM = "dic_map";
const char pmConfDicEnable[]           CHIP_PROGMEM = "dic_enable";
const char pmConfDicInv[]              CHIP_PROGMEM = "dic_inv";
const char pmConfDicSync[]             CHIP_PROGMEM = "dic_sync";
const char pmConfDicMux[]              CHIP_PROGMEM = "dic_mux";
const char pmConfAdcMap[]              CHIP_PROGMEM = "adc_map";
const char pmConfAdcEnable[]           CHIP_PROGMEM = "adc_enable";
const char pmConfAdcJitter[]           CHIP_PROGMEM = "adc_jitter";
const char pmConfAVRPin2Map[]          CHIP_PROGMEM = "avr_pin2_map";
const char pmConfAVRPin3Map[]          CHIP_PROGMEM = "avr_pin3_map";
const char pmConfAVRPin4Map[]          CHIP_PROGMEM = "avr_pin4_map";
const char pmConfAVRPin5Map[]          CHIP_PROGMEM = "avr_pin5_map";
const char pmConfMegaPortA[]           CHIP_PROGMEM = "mega_port_a";
const char pmConfMegaPortC[]           CHIP_PROGMEM = "mega_port_c";
const char pmConfVsc0Mode[]            CHIP_PROGMEM = "vsc_0mode";
const char pmConfVsc0Time[]            CHIP_PROGMEM = "vsc_0time";
const char pmConfVsc0Step[]            CHIP_PROGMEM = "vsc_0step";
const char pmConfVsc0Map[]             CHIP_PROGMEM = "vsc_0map";
const char pmConfVsc1Mode[]            CHIP_PROGMEM = "vsc_1mode";
const char pmConfVsc1Time[]            CHIP_PROGMEM = "vsc_1time";
const char pmConfVsc1Step[]            CHIP_PROGMEM = "vsc_1step";
const char pmConfVsc1Map[]             CHIP_PROGMEM = "vsc_1map";
const char pmConfPulseEnable[]         CHIP_PROGMEM = "pulse_enable";
const char pmConfPulseMode[]           CHIP_PROGMEM = "pulse_mode";
const char pmConfPulseSteps[]          CHIP_PROGMEM = "pulse_steps";
const char pmConfPulseTrig[]           CHIP_PROGMEM = "pulse_trig";
const char pmConfPulseBank[]           CHIP_PROGMEM = "pulse_bank";
const char pmConfPulseDir[]            CHIP_PROGMEM = "pulse_dir";
const char pmConfPulsePreDelay[]       CHIP_PROGMEM = "pulse_pre_delay";
const char pmConfPulsePreMul[]         CHIP_PROGMEM = "pulse_pre_mul";
const char pmConfPulsePostDelay[]      CHIP_PROGMEM = "pulse_post_delay";
const char pmConfPulsePostMul[]        CHIP_PROGMEM = "pulse_post_mul";
const char pmConfPulsePostHold[]       CHIP_PROGMEM = "pulse_post_hold";
const char pmConfPulseInitA[]          CHIP_PROGMEM = "pulse_init_a";
const char pmConfPulseInitB[]          CHIP_PROGMEM = "pulse_init_b";
const char pmConfPulseMaskA[]          CHIP_PROGMEM = "pulse_mask_a";
const char pmConfPulseMaskB[]          CHIP_PROGMEM = "pulse_mask_b";
const char pmConfPulseInvA[]           CHIP_PROGMEM = "pulse_inv_a";
const char pmConfPulseInvB[]           CHIP_PROGMEM = "pulse_inv_b";
const char pmConfPulseFireMode[]       CHIP_PROGMEM = "pulse_fire_mode";
const char pmConfPulseHoldMode[]       CHIP_PROGMEM = "pulse_hold_mode";
const char pmConfPulseHoldAuto[]       CHIP_PROGMEM = "pulse_hold_auto";
const char pmConfPulseHoldAutoClr[]    CHIP_PROGMEM = "pulse_hold_autoclr";
const char pmConfPulseFireMap[]        CHIP_PROGMEM = "pulse_fire_map";
const char pmConfPulseHoldMap[]        CHIP_PROGMEM = "pulse_hold_map";
const char pmConfPulseResumeMap[]      CHIP_PROGMEM = "pulse_resume_map";
const char pmConfPulseResetMap[]       CHIP_PROGMEM = "pulse_reset_map";
const char pmConfPWMOnCntA[]           CHIP_PROGMEM = "pwm_on_cnt_a";
const char pmConfPWMOnCntB[]           CHIP_PROGMEM = "pwm_on_cnt_b";
const char pmConfPWMOffCntA[]          CHIP_PROGMEM = "pwm_off_cnt_a";
const char pmConfPWMOffCntB[]          CHIP_PROGMEM = "pwm_off_cnt_b";
const char pmConfPWMLoop[]             CHIP_PROGMEM = "pwm_loop";
const char pmConfPWMLoopDelta[]        CHIP_PROGMEM = "pwm_loop_delta";
const char pmConfPWMClock[]            CHIP_PROGMEM = "pwm_clock";
const char pmConfPWMReqIdx[]           CHIP_PROGMEM = "pwm_req_idx";
const char pmConfPWMReqFreq[]          CHIP_PROGMEM = "pwm_req_freq";
const char pmConfPWMReqDuty[]          CHIP_PROGMEM = "pwm_req_duty";
const char pmConfPPMDataOffset[]       CHIP_PROGMEM = "ppm_data_offset";
const char pmConfPPMDataLength[]       CHIP_PROGMEM = "ppm_data_len";
const char pmConfPPMDataA[]            CHIP_PROGMEM = "ppm_data_a";
const char pmConfPPMDataB[]            CHIP_PROGMEM = "ppm_data_b";
const char pmConfLPMStart[]            CHIP_PROGMEM = "lpm_start";
const char pmConfLPMStop[]             CHIP_PROGMEM = "lpm_stop";
const char pmConfLPMSize[]             CHIP_PROGMEM = "lpm_size";
const char pmConfLPMRelayMap[]         CHIP_PROGMEM = "lpm_relay_map";
const char pmConfMALCode[]             CHIP_PROGMEM = "mal_code";
const char pmConfMALOps[]              CHIP_PROGMEM = "mal_ops";
const char pmConfMALOpsFire[]          CHIP_PROGMEM = "mal_ops_fire";
const char pmConfMALWait[]             CHIP_PROGMEM = "mal_wait";
const char pmConfPTC0Run[]             CHIP_PROGMEM = "ptc_0run";
const char pmConfPTC0Mul[]             CHIP_PROGMEM = "ptc_0mul";
const char pmConfPTC0Map[]             CHIP_PROGMEM = "ptc_0map";
const char pmConfPTC1Run[]             CHIP_PROGMEM = "ptc_1run";
const char pmConfPTC1Mul[]             CHIP_PROGMEM = "ptc_1mul";
const char pmConfPTC1Map[]             CHIP_PROGMEM = "ptc_1map";
const char pmConfPTT0Map[]             CHIP_PROGMEM = "ptt_0map";
const char pmConfPTT1Map[]             CHIP_PROGMEM = "ptt_1map";
const char pmConfPTT2Map[]             CHIP_PROGMEM = "ptt_2map";
const char pmConfPTT3Map[]             CHIP_PROGMEM = "ptt_3map";
const char pmConfDevVoltDot[]          CHIP_PROGMEM = "dev_volt_dot";
const char pmConfDevAmpDot[]           CHIP_PROGMEM = "dev_amp_dot";
const char pmConfDevTempDot[]          CHIP_PROGMEM = "dev_temp_dot";
const char pmConfSTVWarnSecs[]         CHIP_PROGMEM = "stv_warn_secs";
const char pmConfSTVWarnMap[]          CHIP_PROGMEM = "stv_warn_map";
const char pmConfSTVErrorSecs[]        CHIP_PROGMEM = "stv_error_secs";
const char pmConfSTVErrorMap[]         CHIP_PROGMEM = "stv_error_map";
const char pmConfSTVMaxMap[]           CHIP_PROGMEM = "stv_max_map";
const char pmConfSTVMinMap[]           CHIP_PROGMEM = "stv_min_map";
const char pmConfVFCInputMap[]         CHIP_PROGMEM = "vfc_input_map";
const char pmConfVFCOutputMap[]        CHIP_PROGMEM = "vfc_output_map";
const char pmConfCip0Clock[]           CHIP_PROGMEM = "cip_0clock";
const char pmConfCip0Mode[]            CHIP_PROGMEM = "cip_0mode";
const char pmConfCip0aOcr[]            CHIP_PROGMEM = "cip_0a_ocr";
const char pmConfCip0aCom[]            CHIP_PROGMEM = "cip_0a_com";
const char pmConfCip0bOcr[]            CHIP_PROGMEM = "cip_0b_ocr";
const char pmConfCip0bCom[]            CHIP_PROGMEM = "cip_0b_com";
const char pmConfCip0cOcr[]            CHIP_PROGMEM = "cip_0c_ocr";
const char pmConfCip0cCom[]            CHIP_PROGMEM = "cip_0c_com";
const char pmConfCip1Clock[]           CHIP_PROGMEM = "cip_1clock";
const char pmConfCip1Mode[]            CHIP_PROGMEM = "cip_1mode";
const char pmConfCip1aOcr[]            CHIP_PROGMEM = "cip_1a_ocr";
const char pmConfCip1aCom[]            CHIP_PROGMEM = "cip_1a_com";
const char pmConfCip1bOcr[]            CHIP_PROGMEM = "cip_1b_ocr";
const char pmConfCip1bCom[]            CHIP_PROGMEM = "cip_1b_com";
const char pmConfCip1cOcr[]            CHIP_PROGMEM = "cip_1c_ocr";
const char pmConfCip1cCom[]            CHIP_PROGMEM = "cip_1c_com";
const char pmConfCip2Clock[]           CHIP_PROGMEM = "cip_2clock";
const char pmConfCip2Mode[]            CHIP_PROGMEM = "cip_2mode";
const char pmConfCip2aOcr[]            CHIP_PROGMEM = "cip_2a_ocr";
const char pmConfCip2aCom[]            CHIP_PROGMEM = "cip_2a_com";
const char pmConfCip2bOcr[]            CHIP_PROGMEM = "cip_2b_ocr";
const char pmConfCip2bCom[]            CHIP_PROGMEM = "cip_2b_com";
const char pmConfCip2cOcr[]            CHIP_PROGMEM = "cip_2c_ocr";
const char pmConfCip2cCom[]            CHIP_PROGMEM = "cip_2c_com";
const char pmDataSysTimeTicks[]        CHIP_PROGMEM = "sys_time_ticks";
const char pmDataSysTimeCsec[]         CHIP_PROGMEM = "sys_time_csec";
const char pmDataSysUpTime[]           CHIP_PROGMEM = "sys_uptime";
const char pmDataSysSpeed[]            CHIP_PROGMEM = "sys_speed";
const char pmDataLcdInput[]            CHIP_PROGMEM = "lcd_input";
const char pmDataLcdPage[]             CHIP_PROGMEM = "lcd_page";
const char pmDataLcdRedraw[]           CHIP_PROGMEM = "lcd_redraw";
const char pmDataAdcValue[]            CHIP_PROGMEM = "adc_value";
const char pmDataAdcState[]            CHIP_PROGMEM = "adc_state";
const char pmDataAdcStateIdx[]         CHIP_PROGMEM = "adc_state_idx";
const char pmDataAdcStateValue[]       CHIP_PROGMEM = "adc_state_value";
const char pmDataInt0Freq[]            CHIP_PROGMEM = "int_0freq";
const char pmDataInt0FreqCnt[]         CHIP_PROGMEM = "int_0freq_cnt";
const char pmDataInt1Freq[]            CHIP_PROGMEM = "int_1freq";
const char pmDataInt1FreqCnt[]         CHIP_PROGMEM = "int_1freq_cnt";
const char pmDataDicValue[]            CHIP_PROGMEM = "dic_value";
const char pmDataDocPort[]             CHIP_PROGMEM = "doc_port";
const char pmDataVsc0TimeCnt[]         CHIP_PROGMEM = "vsc_0time_cnt";
const char pmDataVsc0State[]           CHIP_PROGMEM = "vsc_0state";
const char pmDataVsc1TimeCnt[]         CHIP_PROGMEM = "vsc_1time_cnt";
const char pmDataVsc1State[]           CHIP_PROGMEM = "vsc_1state";
const char pmDataSysInputTimeCnt[]     CHIP_PROGMEM = "sys_input_time_cnt";
const char pmDataLPMState[]            CHIP_PROGMEM = "lpm_state";
const char pmDataLPMFire[]             CHIP_PROGMEM = "lpm_fire";
const char pmDataLPMStartTime[]        CHIP_PROGMEM = "lpm_start_time";
const char pmDataLPMTotalTime[]        CHIP_PROGMEM = "lpm_total_time";
const char pmDataLPMResult[]           CHIP_PROGMEM = "lpm_result";
const char pmDataLPMLevel[]            CHIP_PROGMEM = "lpm_level";
const char pmDataPTC0Cnt[]             CHIP_PROGMEM = "ptc_0cnt";
const char pmDataPTC0RunCnt[]          CHIP_PROGMEM = "ptc_0run_cnt";
const char pmDataPTC0MapIdx[]          CHIP_PROGMEM = "ptc_0map_idx";
const char pmDataPTC0MulCnt[]          CHIP_PROGMEM = "ptc_0mul_cnt";
const char pmDataPTC0Step[]            CHIP_PROGMEM = "ptc_0step";
const char pmDataPTC1Cnt[]             CHIP_PROGMEM = "ptc_1cnt";
const char pmDataPTC1RunCnt[]          CHIP_PROGMEM = "ptc_1run_cnt";
const char pmDataPTC1MapIdx[]          CHIP_PROGMEM = "ptc_1map_idx";
const char pmDataPTC1MulCnt[]          CHIP_PROGMEM = "ptc_1mul_cnt";
const char pmDataPTC1Step[]            CHIP_PROGMEM = "ptc_1step";
const char pmDataPTTIdx[]              CHIP_PROGMEM = "ptt_idx";
const char pmDataPTTCnt[]              CHIP_PROGMEM = "ptt_cnt";
const char pmDataPTTFire[]             CHIP_PROGMEM = "ptt_fire";
const char pmDataPTTStep[]             CHIP_PROGMEM = "ptt_step";
const char pmDataDevVolt[]             CHIP_PROGMEM = "dev_volt";
const char pmDataDevAmp[]              CHIP_PROGMEM = "dev_amp";
const char pmDataDevTemp[]             CHIP_PROGMEM = "dev_temp";
const char pmDataDevVar[]              CHIP_PROGMEM = "dev_var";
const char pmDataPulseFire[]           CHIP_PROGMEM = "pulse_fire";
const char pmDataPulseFireCnt[]        CHIP_PROGMEM = "pulse_fire_cnt";
const char pmDataPulseFireFreq[]       CHIP_PROGMEM = "pulse_fire_freq";
const char pmDataPulseHoldFire[]       CHIP_PROGMEM = "pulse_hold_fire";
const char pmDataPulseResetFire[]      CHIP_PROGMEM = "pulse_reset_fire";
const char pmDataPulseResumeFire[]     CHIP_PROGMEM = "pulse_resume_fire";
const char pmDataPulseStep[]           CHIP_PROGMEM = "pulse_step";
const char pmDataPWMState[]            CHIP_PROGMEM = "pwm_state";
const char pmDataPWMLoopCnt[]          CHIP_PROGMEM = "pwm_loop_cnt";
const char pmDataMALFire[]             CHIP_PROGMEM = "mal_fire";
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
const char pmChipFlagSPI[]             CHIP_PROGMEM = "SPI ";
const char pmChipFlagCIP[]             CHIP_PROGMEM = "CIP ";
const char pmChipFlagLCD[]             CHIP_PROGMEM = "LCD ";
const char pmChipFlagADC[]             CHIP_PROGMEM = "ADC ";
const char pmChipFlagPTC0[]            CHIP_PROGMEM = "PTC0 ";
const char pmChipFlagPTC1[]            CHIP_PROGMEM = "PTC1 ";
const char pmChipFlagPTT[]             CHIP_PROGMEM = "PTT ";
const char pmChipFlagSTV[]             CHIP_PROGMEM = "STV ";
const char pmChipFlagVFC[]             CHIP_PROGMEM = "VFC ";
const char pmChipFlagVSC0[]            CHIP_PROGMEM = "VSC0 ";
const char pmChipFlagVSC1[]            CHIP_PROGMEM = "VSC1 ";
const char pmChipFlagMAL[]             CHIP_PROGMEM = "MAL ";
const char pmChipFlagDEBUG[]           CHIP_PROGMEM = "DEBUG ";
const char pmChipBuild[]               CHIP_PROGMEM = "chip_build";
const char pmChipBuildDate[]           CHIP_PROGMEM = __DATE__" "__TIME__; // Print compile date like; "Apr 22 2012 16:36:10"
const char pmChipName[]                CHIP_PROGMEM = "chip_name";
const char pmChipNameStr[]             CHIP_PROGMEM = CHIP_INFO_NAME;
const char pmProgLcdMenuState[]        CHIP_PROGMEM = "lcd_menu_state";
const char pmProgLcdMenuMul[]          CHIP_PROGMEM = "lcd_menu_mul";
const char pmProgLcdMenuIdx[]          CHIP_PROGMEM = "lcd_menu_idx";
const char pmProgLcdMenuValueIdx[]     CHIP_PROGMEM = "lcd_menu_value_idx";
const char pmProgLcdMenuTimeCnt[]      CHIP_PROGMEM = "lcd_menu_time_cnt";
const char pmProgMALPc[]               CHIP_PROGMEM = "mal_pc";
const char pmProgMALState[]            CHIP_PROGMEM = "mal_state";
const char pmProgMALVar[]              CHIP_PROGMEM = "mal_var";
const char pmProgMALWaitCnt[]          CHIP_PROGMEM = "mal_wait_cnt";
const char pmProgTXPush[]              CHIP_PROGMEM = "req_tx_push";
const char pmProgTXEcho[]              CHIP_PROGMEM = "req_tx_echo";
const char pmProgTXPromt[]             CHIP_PROGMEM = "req_tx_promt";
const char pmProgTXHex[]               CHIP_PROGMEM = "req_tx_hex";
const char pmProgSTVState[]            CHIP_PROGMEM = "stv_state";
const char pmProgSTVWaitCnt[]          CHIP_PROGMEM = "stv_wait_cnt";
const char pmProgSTVMapIdx[]           CHIP_PROGMEM = "stv_map_idx";



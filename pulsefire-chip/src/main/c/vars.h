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
#ifndef _VARS_H
#define _VARS_H

// Includes
#include "vars_define.h"
#include "vars_enum.h"
#include "serial.h"
#include "strings.h"
#include "utils.h"
#include "freq.h"
#include "chip.h"
#include "mal.h"
#include "stv.h"

// Defines Types
typedef uint8_t boolean;
typedef uint8_t byte;

// Defines Programing constants
#define PULSE_FIRE_VERSION         10    // PulseFire version
#define ZERO                        0    // zero
#define ONE                         1    // one
#define false                       0    // false
#define true                        1    // true
#define ALL_BANK_MAX                2    // 0=A, 1=A, 2=AB
#define PMCMDLIST_SIZE             18    // array size of other commands
#define UNPSTR_BUFF_SIZE           64    // max string lenght
#define WDT_MAIN_TIMEOUT         WDTO_4S // if main loop takes more than 4 sec then reset device.
#define ADC_VALUE_MAX            1023    // 10bit adc in avr chips
#define PTT_TRIG_VAR_SIZE           4    // we have always 4 triggers
#define DIC_NUM_MAX                16    // max 8 digital inputs in extended mode, +4 on pins, extra chip hw is todo
#define DOC_PORT_NUM_MAX           16    // max 16 digital outputs in duel extended mode
#define PTC_RUN_OFF                 0    // Dont run ptc
#define PTC_RUN_LOOP               0xFF  // Loop ptc steps
#define QMAP_SIZE                   4    // Quad generic mapping data
#define QMAP_VAR_NONE            0xFFFF  // Mapping idx value of adc/dic to indicate no mapping
#define QMAP_VAR_IDX_ALL           0xFF  // Used for selecting all values of var if var is indexed.
#define CLOCK_VALUE_MAX             8    // max value for clock selection
#define FREQ_MUL                   100   // freq is in 100 so 11112 = 111.12 Hz.
#define OUTPUT_MAX                  16   // Max support for 16 outputs.
#define SERIAL_SPEED              115200 // Connect with this speed.
#define CMD_BUFF_SIZE               40   // max command length
#define CMD_MAX_ARGS                8    // max 8 arguments to command
#define CMD_WHITE_SPACE       " \r\t\n"  // All diffent white space chars to split commands on
#define PULSE_DATA_OFF           0x0000  // Data for OFF output
#define PULSE_DATA_ON            0xFFFF  // Data for ON  output

extern CHIP_PROGMEM_ARRAY pmCmdList[];

// PulseFire internal config
typedef struct {
	volatile uint8_t       sys_version;            // Store PulseFire version so reset_conf if changed.
	volatile uint16_t      sys_struct_size;        // Store this stuct size so reset_conf if changed.
#ifdef SF_ENABLE_ADC
	volatile uint16_t      adc_jitter;                      // Minmal adc value change until variable update
	volatile uint16_t      adc_enable;                      // Per input enable bit field.
	volatile uint16_t      adc_map[ADC_NUM_MAX][QMAP_SIZE]; // Map analog inputs to variable
#endif
#ifdef SF_ENABLE_DIC
	volatile uint16_t      dic_enable;                      // Per input enable bit field.
	volatile uint16_t      dic_inv;                         // Per input invert bit field.
	volatile uint16_t      dic_sync;                        // Per input sync bit field if true then mapping if run on change event else trigger to zero.
	volatile uint16_t      dic_map[DIC_NUM_MAX][QMAP_SIZE];  // Map digital input channel to variable
#endif

#ifdef SF_ENABLE_AVR
	volatile uint8_t       avr_pin2_map;    // Mapping for pin2
	volatile uint8_t       avr_pin3_map;    // Mapping for pin3
	volatile uint8_t       avr_pin4_map;    // Mapping for pin4
	volatile uint8_t       avr_pin5_map;    // Mapping for pin5
#endif

#ifdef SF_ENABLE_AVR_MEGA
	volatile uint8_t       avr_pin18_map;   // Mapping for pin18
	volatile uint8_t       avr_pin19_map;   // Mapping for pin19
	volatile uint8_t       avr_pin47_map;   // Mapping for pin47
	volatile uint8_t       avr_pin48_map;   // Mapping for pin48
	volatile uint8_t       avr_pin49_map;   // Mapping for pin49
#endif

#ifdef SF_ENABLE_LCD
	volatile uint8_t       lcd_size;        // Lcd size type
#endif

#ifdef SF_ENABLE_SWC
	volatile uint8_t       swc_delay;       // Startup delay before softstartup.
	volatile uint8_t       swc_mode;        // If !=0 then use this mode in startup then back to configed mode.
	volatile uint16_t      swc_secs;        // Total secords of warmup softstart period.
	volatile uint16_t      swc_duty;        // Sort of start duty from which the steps are made smaller
	volatile uint8_t       swc_trig;        // Fire trigger after startup.
#endif

#ifdef SF_ENABLE_PWM
	volatile uint8_t       pulse_enable;           // Enable output
	volatile uint8_t       pulse_mode;             // Pulse mode
	volatile uint8_t       pulse_steps;            // Total output steps
	volatile uint8_t       pulse_trig;             // Pulse trigger fireing
	volatile uint8_t       pulse_dir;              // Pulse direction
	volatile uint8_t       pulse_bank;             // Pulse bank selection
	volatile uint8_t       pulse_inv;              // Invert output
	volatile uint16_t      pulse_trig_delay;       // Delay before full pulse train
	volatile uint16_t      pulse_post_delay;       // Off duty after full pulse train.
	volatile uint16_t      pulse_mask_a;           // Disable some output pins
	volatile uint16_t      pulse_mask_b;           // Mask for bank B
	volatile uint16_t      pulse_init_a;           // Init data for train mode
	volatile uint16_t      pulse_init_b;           // Init data for bank B

	volatile uint16_t      pwm_on_cnt_a[OUTPUT_MAX]; // Per step timer value
	volatile uint16_t      pwm_on_cnt_b[OUTPUT_MAX]; // Per step timer value
	volatile uint16_t      pwm_off_cnt_a[OUTPUT_MAX];// Per step timer duty value
	volatile uint16_t      pwm_off_cnt_b[OUTPUT_MAX];// Per step timer duty value
	volatile uint16_t      pwm_tune_cnt[OUTPUT_MAX];// Small Xms offset per step
	volatile uint8_t       pwm_loop;               // Loop diverder of pwm
	volatile uint8_t       pwm_loop_delta;         // Change pwm_loop per step with this value
	volatile uint8_t       pwm_clock;              // Pwm clock divider selection or external input
	volatile uint8_t       pwm_duty;               // only used in req_freq cmd
#endif

#ifdef SF_ENABLE_PPM
	volatile uint8_t       ppm_data_offset;        // Do ppm steps in future
	volatile uint8_t       ppm_data_length;        // Total ppm steps
	volatile uint16_t      ppm_data_a[OUTPUT_MAX]; // Custum ppm data per pulse step
	volatile uint16_t      ppm_data_b[OUTPUT_MAX];
#endif

#ifdef SF_ENABLE_LPM
	volatile uint16_t      lpm_start;              // Start value of messurement
	volatile uint16_t      lpm_stop;               // Stop value of messurement
	volatile uint16_t      lpm_size;               // Size of messurement
	volatile uint8_t       lpm_relay_inv;          // Inverse relay output
#endif

#ifdef SF_ENABLE_PTC
	volatile uint8_t       ptc_0run;           // ptc time0 running, 0=off,1=run1,2=2,3=3,etc,255=run_on
	volatile uint8_t       ptc_1run;           // ptc time1 running
	volatile uint8_t       ptc_0mul;           // Time multiplier for time map0
	volatile uint8_t       ptc_1mul;           // Time multiplier for time map1
	volatile uint16_t      ptc_0map[PTC_TIME_MAP_MAX][QMAP_SIZE]; // Time event map0
	volatile uint16_t      ptc_1map[PTC_TIME_MAP_MAX][QMAP_SIZE]; // Time event map1
#endif
#ifdef SF_ENABLE_PTT
	volatile uint16_t      ptt_0map[PTT_TRIG_MAP_MAX][QMAP_SIZE]; // Trigger event map0
	volatile uint16_t      ptt_1map[PTT_TRIG_MAP_MAX][QMAP_SIZE]; // Trigger event map1
	volatile uint16_t      ptt_2map[PTT_TRIG_MAP_MAX][QMAP_SIZE]; // Trigger event map2
	volatile uint16_t      ptt_3map[PTT_TRIG_MAP_MAX][QMAP_SIZE]; // Trigger event map3
#endif

#ifdef SF_ENABLE_DEV
	volatile uint8_t       dev_volt_dot;           // The dot of value 0 = 0, 1 = /10, 2 = /100, 3 = /1000
	volatile uint8_t       dev_amp_dot;
	volatile uint8_t       dev_temp_dot;
#endif

#ifdef SF_ENABLE_STV
	volatile uint8_t       stv_warn_secs;          // The minimal time to be in warning mode(255=always)
	volatile uint8_t       stv_warn_mode;          // Switch to this mode if warning level is reached
	volatile uint8_t       stv_error_secs;         // The minimal time to be in error mode (255=always)
	volatile uint8_t       stv_error_mode;         // Switch to this mode if error level is reached
	volatile uint16_t      stv_max_map[STV_MAX_MAP_MAX][QMAP_SIZE]; // Max trashhold values
	volatile uint16_t      stv_min_map[STV_MIN_MAP_MAX][QMAP_SIZE]; // Min trashhols values
#endif

#ifdef SF_ENABLE_VFC
	volatile uint16_t      vfc_input_map[VFC_MAP_MAX][QMAP_SIZE];   // Virtual feedback input channels
	volatile uint16_t      vfc_output_map[VFC_MAP_MAX][QMAP_SIZE];  // Virtual feedback output channels
#endif

#ifdef SF_ENABLE_MAL
	volatile uint8_t       mal_program[MAL_PROGRAM_SIZE][MAL_PROGRAM_MAX];    // Progam space for really tiny basic code
#endif

#ifdef SF_ENABLE_CIT_MEGA
	volatile uint8_t       cit_oc1_clock; // test
	volatile uint16_t      cit_oc1_a;
	volatile uint16_t      cit_oc1_b;
	volatile uint16_t      cit_oc1_c;

	volatile uint8_t       cit_oc3_clock;
	volatile uint16_t      cit_oc3_a;
	volatile uint16_t      cit_oc3_b;
	volatile uint16_t      cit_oc3_c;

	volatile uint8_t       cit_oc4_clock;
	volatile uint16_t      cit_oc4_a;
	volatile uint16_t      cit_oc4_b;
	volatile uint16_t      cit_oc4_c;
#endif

} pf_conf_struct;

// PulseFire internal data
typedef struct {
	volatile uint32_t      sys_main_loop_cnt;      // Counter of main loop
	volatile uint32_t      sys_input_time_cnt;
#ifdef SF_ENABLE_ADC
	volatile uint32_t      adc_time_cnt;
	volatile uint16_t      adc_value[ADC_NUM_MAX];
	volatile uint8_t       adc_state;
	volatile uint8_t       adc_state_idx;
	volatile uint16_t      adc_state_value;
#endif
#ifdef SF_ENABLE_DIC
	volatile uint32_t      dic_time_cnt;
	volatile uint16_t      dic_value;
#endif
#ifdef SF_ENABLE_DOC
	volatile uint8_t       doc_port[DOC_PORT_NUM_MAX];
#endif
#ifdef SF_ENABLE_SWC
	volatile uint16_t      swc_secs_cnt;
	volatile uint16_t      swc_duty_cnt;
	volatile uint8_t       swc_mode_org;
#endif
#ifdef SF_ENABLE_LCD
	volatile uint8_t       lcd_page;
	volatile uint8_t       lcd_redraw;
	volatile uint32_t      lcd_time_cnt;
#endif

#ifdef SF_ENABLE_LPM
	volatile uint8_t       lpm_state;
	volatile uint8_t       lpm_auto_cmd;
	volatile uint32_t      lpm_start_time;
	volatile uint32_t      lpm_total_time;
	volatile uint16_t      lpm_result;
	volatile uint16_t      lpm_level;
#endif

#ifdef SF_ENABLE_PTC
	volatile uint32_t      ptc_sys_cnt;
	volatile uint8_t       ptc_0cnt;
	volatile uint8_t       ptc_1cnt;
	volatile uint8_t       ptc_0run_cnt;
	volatile uint8_t       ptc_1run_cnt;
	volatile uint8_t       ptc_0map_idx;
	volatile uint8_t       ptc_1map_idx;
	volatile uint16_t      ptc_0mul_cnt;
	volatile uint16_t      ptc_1mul_cnt;
#endif
#ifdef SF_ENABLE_PTT
	volatile uint8_t       ptt_idx[PTT_TRIG_VAR_SIZE];
	volatile uint8_t       ptt_cnt[PTT_TRIG_VAR_SIZE];
	volatile uint8_t       ptt_fire[PTT_TRIG_VAR_SIZE];
#endif

#ifdef SF_ENABLE_DEV
	volatile uint16_t      dev_volt;
	volatile uint16_t      dev_amp;
	volatile uint16_t      dev_temp;
	volatile uint16_t      dev_freq;
	volatile uint16_t      dev_freq_cnt;
	volatile uint32_t      dev_freq_time_cnt;
	volatile uint16_t      dev_var[DEV_VAR_MAX];
#endif

#ifdef SF_ENABLE_PWM
	volatile uint8_t       pulse_fire;              // The Pulse Fire for internal triggering of pulse
	volatile uint8_t       pulse_step;              // The current pulse step
	volatile uint16_t      pulse_data;              // The output data for next step
	volatile uint8_t       pulse_dir_cnt;           // The current pulse direction
	volatile uint8_t       pulse_bank_cnt;          // The pulse bank index
	volatile uint32_t      pulse_trig_delay_cnt;    // Trigger delay counter
	volatile uint32_t      pulse_post_delay_cnt;    // The post pulse delay timer counter of pulse off duty

	volatile uint8_t       pwm_state;               // Interal state of pwm
	volatile uint8_t       pwm_loop_cnt;            // Pwm loop counter for this step
	volatile uint8_t       pwm_loop_max;            // The init loop counter for pulse, gets refresh from conf every pulse
	volatile uint16_t      pwm_req_freq;            // The last requested pwm freq.
#endif

#ifdef SF_ENABLE_PPM
	volatile uint8_t       ppm_idx[OUTPUT_MAX];     // PPM data index of output state.
#endif

#ifdef SF_ENABLE_MAL
	volatile uint16_t      mal_trig[MAL_PROGRAM_MAX];
#endif

} pf_data_struct;

// PulseFire program data (which does not get reset)
typedef struct {

	// == 4 Special variables because these 4 are not in the VARS list !!! ==
	char unpstr_buff[UNPSTR_BUFF_SIZE];    // buffer to copy progmem data into
	volatile char cmd_buff[CMD_BUFF_SIZE]; // Command buffer for serial cmds
	volatile uint8_t cmd_buff_idx;         // Command index
	volatile uint8_t cmd_process;          // Processing command

	volatile uint32_t      sys_time_ticks;
	volatile uint32_t      sys_time_ssec;

	volatile uint8_t       req_tx_push; // Push conf changes to Serial
	volatile uint8_t       req_tx_echo;
	volatile uint8_t       req_tx_promt;

#ifdef SF_ENABLE_LCD
	char lcd_buff[20];
	volatile uint8_t       lcd_menu_state;
	volatile uint16_t      lcd_menu_mul;
	volatile uint8_t       lcd_menu_idx;
	volatile uint8_t       lcd_menu_value_idx;
	volatile uint32_t      lcd_menu_time_cnt;
#endif

#ifdef SF_ENABLE_MAL
	volatile uint8_t       mal_pc;
	volatile uint8_t       mal_state;
	volatile uint16_t      mal_var[OUTPUT_MAX];
#endif

#ifdef SF_ENABLE_STV
	volatile uint8_t       stv_state;
	volatile uint32_t      stv_time_cnt;
	volatile uint8_t       stv_wait_cnt;
	volatile uint8_t       stv_mode_org;
	volatile uint8_t       stv_map_idx;
#endif

} pf_prog_struct;

// variables
extern pf_data_struct       pf_data;
extern pf_prog_struct       pf_prog;
extern pf_conf_struct       pf_conf;

// Dynamicly calculate PF_VARS size based on SF_ENABLE_* flags.
#define PF_VARS_SIZE Vars_getSize()
#define PF_VARS_PF_SIZE     7
#ifdef SF_ENABLE_PWM
	#define PF_VARS_PWM_SIZE  33
#else
	#define PF_VARS_PWM_SIZE  0
#endif
#ifdef SF_ENABLE_LCD
	#define PF_VARS_LCD_SIZE  9
#else
	#define PF_VARS_LCD_SIZE  0
#endif
#ifdef SF_ENABLE_LPM
	#define PF_VARS_LPM_SIZE  10
#else
	#define PF_VARS_LPM_SIZE  0
#endif
#ifdef SF_ENABLE_PPM
	#define PF_VARS_PPM_SIZE  5
#else
	#define PF_VARS_PPM_SIZE  0
#endif
#ifdef SF_ENABLE_ADC
	#define PF_VARS_ADC_SIZE  8
#else
	#define PF_VARS_ADC_SIZE  0
#endif
#ifdef SF_ENABLE_DIC
	#define PF_VARS_DIC_SIZE  6
#else
	#define PF_VARS_DIC_SIZE  0
#endif
#ifdef SF_ENABLE_DOC
	#define PF_VARS_DOC_SIZE  1
#else
	#define PF_VARS_DOC_SIZE  0
#endif
#ifdef SF_ENABLE_DEV
	#define PF_VARS_DEV_SIZE  9
#else
	#define PF_VARS_DEV_SIZE  0
#endif
#ifdef SF_ENABLE_PTC
	#define PF_VARS_PTC_SIZE  15
#else
	#define PF_VARS_PTC_SIZE  0
#endif
#ifdef SF_ENABLE_PTT
	#define PF_VARS_PTT_SIZE  7
#else
	#define PF_VARS_PTT_SIZE  0
#endif
#ifdef SF_ENABLE_STV
	#define PF_VARS_STV_SIZE  10
#else
	#define PF_VARS_STV_SIZE  0
#endif
#ifdef SF_ENABLE_VFC
	#define PF_VARS_VFC_SIZE  2
#else
	#define PF_VARS_VFC_SIZE  0
#endif
#ifdef SF_ENABLE_MAL
	#define PF_VARS_MAL_SIZE  4
#else
	#define PF_VARS_MAL_SIZE  0
#endif
#ifdef SF_ENABLE_SWC
	#define PF_VARS_SWC_SIZE  8
#else
	#define PF_VARS_SWC_SIZE  0
#endif
#ifdef SF_ENABLE_AVR
	#define PF_VARS_AVR_SIZE  4
#else
	#define PF_VARS_AVR_SIZE  0
#endif
#ifdef SF_ENABLE_AVR_MEGA
	#define PF_VARS_AVR_MEGA_SIZE  5
#else
	#define PF_VARS_AVR_MEGA_SIZE  0
#endif


// functions
uint8_t Vars_getSize(void);
uint16_t Vars_getBitsRaw(uint8_t idx);
uint16_t Vars_getValueMax(uint8_t idx);
boolean Vars_isTrigger(byte idx);
boolean Vars_isNolimit(byte idx);
boolean Vars_isIndexA(byte idx);
boolean Vars_isIndexB(byte idx);
boolean Vars_isNomap(byte idx);
boolean Vars_isBitSize32(byte idx);
boolean Vars_isMenuSkip(byte idx);
boolean Vars_isTypeConf(byte idx);
boolean Vars_isTypeData(byte idx);
boolean Vars_isTypeProg(byte idx);
char*   Vars_getName(uint8_t idx);
uint8_t Vars_getIndexAMax(uint8_t idx);
uint8_t Vars_getIndexBMax(uint8_t idx);
uint16_t Vars_getDefaultValue(uint8_t idx);
uint16_t Vars_getIndexFromName(char* name);
uint16_t Vars_getValue(uint8_t idx,uint8_t idxA,uint8_t idxB);
uint32_t Vars_getValue32(uint8_t idx,uint8_t idxA);
uint16_t Vars_setValueSerial(uint8_t idx,uint8_t idxA,uint8_t idxB,uint16_t value);
uint16_t Vars_setValueReset(uint8_t idx,uint8_t idxA,uint16_t value);
uint16_t Vars_setValue(uint8_t idx,uint8_t idxA,uint8_t idxB,uint16_t value);
uint16_t Vars_setValueImpl(uint8_t idx,uint8_t idxA,uint8_t idxB,uint16_t value,boolean trig,boolean serial);
void Vars_readConfig(void);
void Vars_writeConfig(void);
void Vars_resetConfig(void);
void Vars_resetData(void);
void Vars_setup(void);

// end include
#endif


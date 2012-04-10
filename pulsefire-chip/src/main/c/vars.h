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
#include "pwm.h"

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
#define PMCMDLIST_SIZE             20    // array size of other commands
#define UNPSTR_BUFF_SIZE           64    // max string lenght to copy from flash.
#define WDT_MAIN_TIMEOUT         WDTO_4S // if main loop takes more than 4 sec then reset device.
#define ADC_VALUE_MAX            1023    // 10bit adc in avr chips
#define PTT_TRIG_VAR_SIZE           4    // we have always 4 triggers
#define DOC_PORT_NUM_MAX           16    // max 16 digital outputs in duel extended mode
#define PTC_RUN_OFF                 0    // Dont run ptc
#define PTC_RUN_LOOP               0xFF  // Loop ptc steps
#define MAL_VAR_MAX                16    // mal opcodes define mal vars as 4 bit.
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
#define VARS_INT_NUM_SIZE           4    // cache max 4 vars
#define VARS_INT_SIZE               3    // 0=idx,1=idxA,3=value

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
	//volatile uint8_t       lcd_page;      // Default lcd page view
	//volatile uint8_t       lcd_cypage;    // bits: pages to cycle in view
	//volatile uint8_t       lcd_cytime;    // bits: page cycle time in sec
	//volatile uint8_t       lcd_mode;      // 0=Display only,1=User,2=Admin
	//volatile uint8_t       lcd_skip[LCD_SKIP_NUM_MAX] // skip these menu entries based on var idx.
#endif

#ifdef SF_ENABLE_SWC
	volatile uint8_t       swc_delay;       // Startup delay before softstartup.
	volatile uint16_t      swc_secs;        // Total secords of warmup softstart period.
	volatile uint16_t      swc_duty;        // Sort of start duty from which the steps are made smaller
	volatile uint16_t      swc_map[SWC_MAP_MAX][QMAP_SIZE];  // warmup variable mapping
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
	volatile uint16_t      pulse_inv_a;            // Inverse output data
	volatile uint16_t      pulse_inv_b;            // Inverse output data for bank B

	volatile uint16_t      pwm_on_cnt_a[OUTPUT_MAX]; // Per step timer value
	volatile uint16_t      pwm_on_cnt_b[OUTPUT_MAX]; // Per step timer value
	volatile uint16_t      pwm_off_cnt_a[OUTPUT_MAX];// Per step timer duty value
	volatile uint16_t      pwm_off_cnt_b[OUTPUT_MAX];// Per step timer duty value
	volatile uint16_t      pwm_tune_cnt[OUTPUT_MAX];// Small Xms offset per step
	volatile uint8_t       pwm_loop;               // Loop diverder of pwm
	volatile uint8_t       pwm_loop_delta;         // Change pwm_loop per step with this value
	volatile uint8_t       pwm_clock;              // Pwm clock divider selection or external input
	volatile uint8_t       pwm_req_idx;            // Then output idx of duty and freq.
	volatile uint8_t       pwm_req_duty;           // The requested pwm duty.
	volatile uint16_t      pwm_req_freq;           // The requested pwm freq.
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
	volatile uint16_t      lpm_relay_map[LPM_RELAY_MAP_MAX][QMAP_SIZE];// Output mapping for relay status
#endif

#ifdef SF_ENABLE_PTC
	volatile uint8_t       ptc_0run;                              // ptc time0 running, 0=off,1=run1,2=2,3=3,etc,255=run_on
	volatile uint8_t       ptc_0mul;                              // Time multiplier for time map0
	volatile uint16_t      ptc_0map[PTC_TIME_MAP_MAX][QMAP_SIZE]; // Time event map0
	volatile uint8_t       ptc_1run;                              // ptc time1 running
	volatile uint8_t       ptc_1mul;                              // Time multiplier for time map1
	volatile uint16_t      ptc_1map[PTC_TIME_MAP_MAX][QMAP_SIZE]; // Time event map1
#endif
#ifdef SF_ENABLE_PTT
	volatile uint16_t      ptt_0map[PTT_TRIG_MAP_MAX][QMAP_SIZE]; // Trigger event map0
	volatile uint16_t      ptt_1map[PTT_TRIG_MAP_MAX][QMAP_SIZE]; // Trigger event map1
	volatile uint16_t      ptt_2map[PTT_TRIG_MAP_MAX][QMAP_SIZE]; // Trigger event map2
	volatile uint16_t      ptt_3map[PTT_TRIG_MAP_MAX][QMAP_SIZE]; // Trigger event map3
#endif

#ifdef SF_ENABLE_DEV
	volatile uint8_t       dev_volt_dot;           // Dot value for dev_volt; 0 = 0, 1 = /10, 2 = /100, 3 = /1000, 4= /10000
	volatile uint8_t       dev_amp_dot;            // Dot value for dev_amp
	volatile uint8_t       dev_temp_dot;           // Dot value for dev_temp
#endif

#ifdef SF_ENABLE_STV
	volatile uint8_t       stv_warn_secs;          // The minimal time to be in warning mode(255=always)
	volatile uint16_t      stv_warn_map[STV_WARN_MAP_MAX][QMAP_SIZE];  // Do these actions if warning level is reached
	volatile uint8_t       stv_error_secs;      
	volatile uint16_t      stv_error_map[STV_ERROR_MAP_MAX][QMAP_SIZE];
	volatile uint16_t      stv_max_map[STV_MAX_MAP_MAX][QMAP_SIZE]; // Max trashhold values
	volatile uint16_t      stv_min_map[STV_MIN_MAP_MAX][QMAP_SIZE]; // Min trashhols values
#endif

#ifdef SF_ENABLE_VFC
	volatile uint16_t      vfc_input_map[VFC_MAP_MAX][QMAP_SIZE];   // Virtual feedback input channels
	volatile uint16_t      vfc_output_map[VFC_MAP_MAX][QMAP_SIZE];  // Virtual feedback output channels
#endif

#ifdef SF_ENABLE_MAL
	volatile uint8_t       mal_code[MAL_CODE_SIZE];  // Program space for micro assembly language code
	volatile uint8_t       mal_ops;                  // Operations per step in main loop
	volatile uint8_t       mal_ops_fire;             // Max ops when fired
	volatile uint16_t      mal_mticks;               // Main loop time tick wait
#endif

/*
	volatile uint8_t       int_0enable;
	volatile uint8_t       int_0trig;
	volatile uint8_t       int_0mode;
	volatile uint16_t      int_0map[INT_MAP_MAX][QMAP_SIZE];

	volatile uint8_t       io_mode; // bits: 0=spi_out8,1=spi_out16,2=spi_lcd,3=spi_lcddicmux,4=spi_doc8,5=spi_doc16

	volatile uint8_t       io_out_pin0 // for all: PWM_OUT,DOC_OUT
	volatile uint8_t       io_out_pin1 // SPI_PWM_DATA_PIN,SPI_PWM_CLK_PIN,SPI_PWM_E_PIN,
	volatile uint8_t       io_out_pin2 // SPI_EXT_DATA_PIN,SPI_EXT_CLK_PIN,SPI_EXT_E_PIN
	volatile uint8_t       io_out_pin3
	volatile uint8_t       io_out_pin4 // note: this pin started all this for cit0 comB output !
	volatile uint8_t       io_out_pin5
	volatile uint8_t       io_out_pin6 
	volatile uint8_t       io_out_pin7

	volatile uint8_t       io_int0_pin
	volatile uint8_t       io_int1_pin
	//volatile uint8_t       io_int2_pin
	//volatile uint8_t       io_int3_pin
*/

#ifdef SF_ENABLE_CIT
	volatile uint8_t       cit_0clock;
	volatile uint8_t       cit_0mode;
	volatile uint8_t       cit_0int; // 3 bits
	volatile uint8_t       cit_0a_ocr;
	volatile uint8_t       cit_0a_com;
	volatile uint16_t      cit_0a_map[CIT_MAP_MAX][QMAP_SIZE];
	volatile uint8_t       cit_0b_ocr;
	volatile uint8_t       cit_0b_com;
	volatile uint16_t      cit_0b_map[CIT_MAP_MAX][QMAP_SIZE];
#endif
#ifdef SF_ENABLE_CIP
	volatile uint8_t       cip_0clock;
	volatile uint8_t       cip_0mode;
	volatile uint16_t      cip_0a_ocr;
	volatile uint8_t       cip_0a_com;
	volatile uint16_t      cip_0b_ocr;
	volatile uint8_t       cip_0b_com;
	volatile uint16_t      cip_0c_ocr;
	volatile uint8_t       cip_0c_com;

	volatile uint8_t       cip_1clock;
	volatile uint8_t       cip_1mode;
	volatile uint16_t      cip_1a_ocr;
	volatile uint8_t       cip_1a_com;
	volatile uint16_t      cip_1b_ocr;
	volatile uint8_t       cip_1b_com;
	volatile uint16_t      cip_1c_ocr;
	volatile uint8_t       cip_1c_com;

	volatile uint8_t       cip_2clock;
	volatile uint8_t       cip_2mode;
	volatile uint16_t      cip_2a_ocr;
	volatile uint8_t       cip_2a_com;
	volatile uint16_t      cip_2b_ocr;
	volatile uint8_t       cip_2b_com;
	volatile uint16_t      cip_2c_ocr;
	volatile uint8_t       cip_2c_com;
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
#endif
#ifdef SF_ENABLE_LCD
	volatile uint8_t       lcd_page;          // The current lcd page being draw
	volatile uint8_t       lcd_redraw;        // Notify lcd code to clear and redraw page.
	volatile uint32_t      lcd_time_cnt;      // Timer until next redraw
#endif

#ifdef SF_ENABLE_LPM
	volatile uint8_t       lpm_state;         // The state machine value
	volatile uint8_t       lpm_fire;          // Start lpm messurement
	volatile uint32_t      lpm_start_time;    // The start time of the LPM messurement
	volatile uint32_t      lpm_total_time;    // The total time of LPM messurement
	volatile uint16_t      lpm_result;        // The LPM result in /10.
	volatile uint16_t      lpm_level;         // The level of messurement
#endif

#ifdef SF_ENABLE_PTC
	volatile uint32_t      ptc_sys_cnt;
	volatile uint8_t       ptc_0cnt;
	volatile uint8_t       ptc_0run_cnt;
	volatile uint8_t       ptc_0map_idx;
	volatile uint16_t      ptc_0mul_cnt;
	volatile uint8_t       ptc_0step;

	volatile uint8_t       ptc_1cnt;
	volatile uint8_t       ptc_1run_cnt;
	volatile uint8_t       ptc_1map_idx;
	volatile uint16_t      ptc_1mul_cnt;
	volatile uint8_t       ptc_1step;
#endif
#ifdef SF_ENABLE_PTT
	volatile uint8_t       ptt_idx[PTT_TRIG_VAR_SIZE];
	volatile uint8_t       ptt_cnt[PTT_TRIG_VAR_SIZE];
	volatile uint8_t       ptt_fire[PTT_TRIG_VAR_SIZE];
	volatile uint8_t       ptt_step[PTT_TRIG_VAR_SIZE];
#endif

#ifdef SF_ENABLE_DEV
	volatile uint16_t      dev_volt;               // Device volt variable for programatic usage.
	volatile uint16_t      dev_amp;                // Device amps
	volatile uint16_t      dev_temp;               // Device temperature
	volatile uint16_t      dev_freq;               // Device freqencie
	volatile uint16_t      dev_freq_cnt;           // Device freq counter from input pin.
	volatile uint32_t      dev_freq_time_cnt;      // Device dev_freq value update counter.
	volatile uint16_t      dev_var[DEV_VAR_MAX];   // Generic device parameters for programatic use.
#endif

#ifdef SF_ENABLE_PWM
	volatile uint8_t       pulse_fire;             // The Pulse Fire for internal triggering of pulse
	volatile uint16_t      pulse_fire_cnt;         // Counter for pulsefire 
	volatile uint16_t      pulse_fire_freq;        // The pulsefire cnt freq
	volatile uint16_t      pulse_fire_freq_cnt;    // The pulsefire cnt freq internal counter
	volatile uint8_t       pulse_hold_fire;        // Holds or resets the pulse fire state.
	volatile uint8_t       pulse_step;             // The current pulse step
	volatile uint16_t      pulse_data;             // The output data for next step
	volatile uint8_t       pulse_dir_cnt;          // The current pulse direction
	volatile uint8_t       pulse_bank_cnt;         // The pulse bank index
	volatile uint32_t      pulse_trig_delay_cnt;   // Trigger delay counter
	volatile uint32_t      pulse_post_delay_cnt;   // The post pulse delay timer counter of pulse off duty

	volatile uint8_t       pwm_state;              // Interal state of pwm
	volatile uint8_t       pwm_loop_cnt;           // Pwm loop counter for this step
	volatile uint8_t       pwm_loop_max;           // The init loop counter for pulse, gets refresh from conf every pulse
#endif

#ifdef SF_ENABLE_PPM
	volatile uint8_t       ppm_idx[OUTPUT_MAX];    // PPM data index of output state.
#endif

#ifdef SF_ENABLE_MAL
	volatile uint16_t      mal_fire[MAL_FIRE_MAX]; // MAL fire triggers.
#endif

} pf_data_struct;

// PulseFire program data (which does not get reset)
typedef struct {

	// == 5 Special variables because these 4 are not in the VARS list !!! ==
	char unpstr_buff[UNPSTR_BUFF_SIZE];    // buffer to copy progmem data into
	volatile char cmd_buff[CMD_BUFF_SIZE]; // Command buffer for serial cmds
	volatile uint8_t cmd_buff_idx;         // Command index
	volatile uint8_t cmd_process;          // Processing command
	volatile uint16_t vars_int_buff[VARS_INT_NUM_SIZE][VARS_INT_SIZE]; // print int vars into normal code loop

	volatile uint32_t      sys_time_ticks; // Timer0 ticks
	volatile uint32_t      sys_time_ssec;  // 1/10 of seconds ticks.

	volatile uint8_t       req_tx_push;    // Push conf changes to Serial
	volatile uint8_t       req_tx_echo;    // Local echo of received characters
	volatile uint8_t       req_tx_promt;   // Print promt after cmd is ready.
	//volatile uint8_t     req_tx_hex;     // Print all var names in idx hex.

#ifdef SF_ENABLE_LCD
	char lcd_buff[20];
	volatile uint8_t       lcd_menu_state;
	volatile uint16_t      lcd_menu_mul;
	volatile uint8_t       lcd_menu_idx;
	volatile uint8_t       lcd_menu_value_idx;
	volatile uint32_t      lcd_menu_time_cnt;
#endif

#ifdef SF_ENABLE_MAL
	volatile uint8_t       mal_pc;               // Mal program counter
	volatile uint8_t       mal_pc_fire;          // Temp store pc when run from trigger (NOTE; not in PF_VARS)
	volatile uint8_t       mal_state;            // program state
	volatile uint16_t      mal_var[MAL_VAR_MAX]; // mal internal variables
	volatile uint32_t      mal_time_cnt;         // mal time cnt
#endif

#ifdef SF_ENABLE_STV
	volatile uint8_t       stv_state;
	volatile uint32_t      stv_time_cnt;
	volatile uint8_t       stv_wait_cnt;
	volatile uint8_t       stv_map_idx;
#endif

} pf_prog_struct;

// All pulsefire variables are stored in one of these structs;
extern pf_data_struct       pf_data;
extern pf_prog_struct       pf_prog;
extern pf_conf_struct       pf_conf;

// Dynamicly calculate PF_VARS size based on SF_ENABLE_* flags.
#define PF_VARS_SIZE Vars_getSize()
#define PF_VARS_PF_SIZE     7
#ifdef SF_ENABLE_PWM
	#define PF_VARS_PWM_SIZE  39
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
	#define PF_VARS_PTC_SIZE  17
#else
	#define PF_VARS_PTC_SIZE  0
#endif
#ifdef SF_ENABLE_PTT
	#define PF_VARS_PTT_SIZE  8
#else
	#define PF_VARS_PTT_SIZE  0
#endif
#ifdef SF_ENABLE_STV
	#define PF_VARS_STV_SIZE  9
#else
	#define PF_VARS_STV_SIZE  0
#endif
#ifdef SF_ENABLE_VFC
	#define PF_VARS_VFC_SIZE  2
#else
	#define PF_VARS_VFC_SIZE  0
#endif
#ifdef SF_ENABLE_MAL
	#define PF_VARS_MAL_SIZE  8
#else
	#define PF_VARS_MAL_SIZE  0
#endif
#ifdef SF_ENABLE_SWC
	#define PF_VARS_SWC_SIZE  6
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
uint16_t Vars_getIndexFromPtr(uint16_t* ptr);
uint16_t Vars_getValue(uint8_t idx,uint8_t idxA,uint8_t idxB);
uint32_t Vars_getValue32(uint8_t idx,uint8_t idxA);
uint16_t Vars_setValueSerial(uint8_t idx,uint8_t idxA,uint8_t idxB,uint16_t value);
uint16_t Vars_setValueReset(uint8_t idx,uint8_t idxA,uint16_t value);
uint16_t Vars_setValueInt(uint8_t idx,uint8_t idxA,uint8_t idxB,uint16_t value);
uint16_t Vars_setValue(uint8_t idx,uint8_t idxA,uint8_t idxB,uint16_t value);
uint16_t Vars_setValueImpl(uint8_t idx,uint8_t idxA,uint8_t idxB,uint16_t value,boolean trig,boolean serial,boolean intBuff);
void Vars_readConfig(void);
void Vars_writeConfig(void);
void Vars_resetConfig(void);
void Vars_resetData(void);
void Vars_setup(void);
void Vars_loop(void);

// end include
#endif


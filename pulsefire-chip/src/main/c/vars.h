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
#define PULSE_FIRE_VERSION         11    // PulseFire version
#define ZERO                        0    // zero
#define ONE                         1    // one
#define false                       0    // false
#define true                        1    // true
#define PWM_DATA_MAX     (16*4)+(3*10)+1 // PWM output steps buffer.
#define PMCMDLIST_SIZE             19    // Array size of other commands.
#define UNPSTR_BUFF_SIZE           64    // max string lenght to copy from flash.
#define WDT_MAIN_TIMEOUT         WDTO_4S // if main loop takes more than 4 sec then reset device.
#define SPI_CLOCK_MAX               3    // 4 spi clock modes
#define ADC_VALUE_MAX            1023    // 10bit adc in avr chips
#define LCD_PLP_MAX                 4    // Lcd plp always has 4 values.
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
#define PULSE_BANK_MAX              2    // 0=A, 1=A, 2=AB
#define PULSE_DELAY_MUL_MAX        10    // Max extra steps for pre/post delay (2x10=20 steps)
#define VARS_INT_NUM_SIZE           4    // cache max 4 uniq vars from int based value change.
#define VARS_INT_SIZE               3    // 0=idx,1=idxA,3=value


extern CHIP_PROGMEM_ARRAY const pmCmdList[];

// PulseFire internal config
typedef struct {
	volatile uint8_t       sys_version;            // Store PulseFire version so reset_conf if changed.
	volatile uint16_t      sys_struct_size;        // Store this stuct size so reset_conf if changed.
	volatile uint32_t      sys_id;                 // Store PulseFire id
	volatile uint32_t      sys_pass;               // Store PulseFire login pass.

#ifdef SF_ENABLE_SPI
	volatile uint8_t       spi_clock;              // Hardware spi clock speed
	volatile uint8_t       spi_chips;              // bitwise spi chip selects
#endif 
#ifdef SF_ENABLE_ADC
	volatile uint16_t      adc_jitter;                      // Minmal adc value change until variable update
	volatile uint16_t      adc_enable;                      // Per input enable bit field.
	volatile uint16_t      adc_map[ADC_MAP_MAX][QMAP_SIZE]; // Map analog inputs to variable
#endif

	volatile uint8_t       dic_mux;                         // Dic multiplex enable
	volatile uint16_t      dic_enable;                      // Per input enable bit field.
	volatile uint16_t      dic_inv;                         // Per input invert bit field.
	volatile uint16_t      dic_sync;                        // Per input sync bit field if true then mapping if run on change event else trigger to zero.
	volatile uint16_t      dic_map[DIC_MAP_MAX][QMAP_SIZE];  // Map digital input channel to variable

#ifdef SF_ENABLE_AVR
	volatile uint8_t       avr_pin2_map;    // Mapping for pin2
	volatile uint8_t       avr_pin3_map;    // Mapping for pin3
	volatile uint8_t       avr_pin4_map;    // Mapping for pin4
	volatile uint8_t       avr_pin5_map;    // Mapping for pin5
#endif

#ifdef SF_ENABLE_AVR_MEGA
	volatile uint8_t       mega_port_a;      // Mapping for port a
	volatile uint8_t       mega_port_c;      // Mapping for port c
#endif

#ifdef SF_ENABLE_LCD
	volatile uint8_t       lcd_size;        // Lcd size type
	volatile uint8_t       lcd_defp;        // Default lcd page view
	volatile uint8_t       lcd_mode;        // 0=off,1=page,2=2but,3=4but
	volatile uint8_t       lcd_hcd;         // Lcd hardware command delay.
	volatile uint8_t       lcd_plp[LCD_PLP_MAX]; // Plp values
#endif

#ifdef SF_ENABLE_VSC0
	volatile uint8_t       vsc_0mode;       // Run mode, off,once-up,once-down,loop-up,loop-down,loop-up-down
	volatile uint16_t      vsc_0time;       // Time step
	volatile uint8_t       vsc_0step;       // Step size
	volatile uint16_t      vsc_0map[VSC_MAP_MAX][QMAP_SIZE];  // variable stepper mapping
#endif
#ifdef SF_ENABLE_VSC1
	volatile uint8_t       vsc_1mode;
	volatile uint16_t      vsc_1time;
	volatile uint8_t       vsc_1step;
	volatile uint16_t      vsc_1map[VSC_MAP_MAX][QMAP_SIZE];
#endif

#ifdef SF_ENABLE_PWM
	volatile uint8_t       pulse_enable;           // Enable output
	volatile uint8_t       pulse_mode;             // Pulse mode
	volatile uint8_t       pulse_steps;            // Total output steps
	volatile uint8_t       pulse_trig;             // Pulse trigger fireing
	volatile uint8_t       pulse_bank;             // Pulse bank selection
	volatile uint8_t       pulse_dir;              // Pulse direction selection
	volatile uint16_t      pulse_pre_delay;        // Delay before full pulse train
	volatile uint8_t       pulse_pre_mul;          // Delay multiplier
	volatile uint16_t      pulse_post_delay;       // Delay after full pulse train.
	volatile uint8_t       pulse_post_mul;         // Delay multiplier
	volatile uint8_t       pulse_post_hold;        // 0 = OFF, 1 = Data last step.
	volatile uint16_t      pulse_mask_a;           // Disable some output pins
	volatile uint16_t      pulse_mask_b;           // Mask for bank B
	volatile uint16_t      pulse_init_a;           // Init data for train mode
	volatile uint16_t      pulse_init_b;           // Init data for bank B
	volatile uint16_t      pulse_inv_a;            // Inverse output data
	volatile uint16_t      pulse_inv_b;            // Inverse output data for bank B
	volatile uint8_t       pulse_fire_mode;        // Pulse fire mode
	volatile uint8_t       pulse_hold_mode;        // Pulse hold mode
	volatile uint8_t       pulse_hold_auto;        // Pulse auto hold step
	volatile uint8_t       pulse_hold_autoclr;     // Pulse auto hold clear output
	volatile uint16_t      pulse_fire_map	[FIRE_MAP_MAX][QMAP_SIZE]; // Events for pulse_fire event
	volatile uint16_t      pulse_hold_map	[FIRE_MAP_MAX][QMAP_SIZE]; // Events for pulse_hold_fire event
	volatile uint16_t      pulse_resume_map	[FIRE_MAP_MAX][QMAP_SIZE]; // Events for pulse_resume_fire event
	volatile uint16_t      pulse_reset_map	[FIRE_MAP_MAX][QMAP_SIZE]; // Events for pulse_reset_fire event

	volatile uint16_t      pwm_on_cnt_a[OUTPUT_MAX]; // Per step timer value
	volatile uint16_t      pwm_on_cnt_b[OUTPUT_MAX]; // Per step timer value
	volatile uint16_t      pwm_off_cnt_a[OUTPUT_MAX];// Per step timer duty value
	volatile uint16_t      pwm_off_cnt_b[OUTPUT_MAX];// Per step timer duty value
	volatile uint8_t       pwm_loop;               // Loop diverder of pwm
	volatile uint8_t       pwm_clock;              // Pwm clock divider selection or external input
	volatile uint8_t       pwm_req_idx;            // Then output idx of duty and freq.
	volatile uint8_t       pwm_req_duty;           // The requested pwm duty.
	volatile uint16_t      pwm_req_freq;           // The requested pwm freq.

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

#ifdef SF_ENABLE_PTC0
	volatile uint8_t       ptc_0run;                              // ptc time0 running, 0=off,1=run1,2=2,3=3,etc,255=run_on
	volatile uint8_t       ptc_0mul;                              // Time multiplier for time map0
	volatile uint16_t      ptc_0map[PTC_TIME_MAP_MAX][QMAP_SIZE]; // Time event map0
#endif
#ifdef SF_ENABLE_PTC1
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

	volatile uint8_t       dev_volt_dot;           // Dot value for dev_volt; 0 = 0, 1 = /10, 2 = /100, 3 = /1000, 4= /10000
	volatile uint8_t       dev_amp_dot;            // Dot value for dev_amp
	volatile uint8_t       dev_temp_dot;           // Dot value for dev_temp

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
	volatile uint8_t       mal_wait;                 // Main loop wait time divider
#endif

	volatile uint16_t      int_map[INT_MAP_MAX][QMAP_SIZE];
	volatile uint8_t       int_0mode;
	volatile uint8_t       int_0trig;
	volatile uint8_t       int_0freq_mul;
	volatile uint8_t       int_1mode;
	volatile uint8_t       int_1trig;
	volatile uint8_t       int_1freq_mul;

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
	volatile uint8_t       rm_this_align_fill_bug; // TODO: fixme else 32b counter upper byte comes in cip_2c_com

	volatile uint32_t      sys_loop0_cnt;      // Counter of main loop,(not in vars) todo: move to below with other not vars
	volatile uint8_t       sys_time_ticks;     // Timer ticks
	volatile uint32_t      sys_time_csec;      // Centri seconds = 1/10 of second ticks.
	volatile uint32_t      sys_uptime;         // One second ticks
	volatile uint32_t      sys_speed ;         // Speed in hz of main loop

#ifdef SF_ENABLE_ADC
	volatile uint16_t      adc_value[ADC_MAP_MAX]; // Analog input value per input
	volatile uint8_t       adc_state;              // State of adc
	volatile uint8_t       adc_state_idx;          // input idx for requested adc value
	volatile uint16_t      adc_state_value;        // input value of requested adc
#endif

	volatile uint16_t      int_0freq;              // int0 freq
	volatile uint16_t      int_0freq_cnt;          // int0 freq counter
	volatile uint16_t      int_1freq;              // int1 freq
	volatile uint16_t      int_1freq_cnt;          // int1 freq counter

	volatile uint16_t      dic_value;              // Digital input value
	volatile uint8_t       doc_port[DOC_PORT_NUM_MAX]; // Digital output value

#ifdef SF_ENABLE_VSC0
	volatile uint16_t      vsc_0time_cnt;           // Time tick counter
	volatile uint8_t       vsc_0state[VSC_MAP_MAX]; // Run state per mapping
#endif
#ifdef SF_ENABLE_VSC1
	volatile uint16_t      vsc_1time_cnt;
	volatile uint8_t       vsc_1state[VSC_MAP_MAX];
#endif
#ifdef SF_ENABLE_LCD
	volatile uint8_t       lcd_input;         // The lcd input pins input mappable variable
	volatile uint8_t       lcd_page;          // The current lcd page being draw
	volatile uint8_t       lcd_redraw;        // Notify lcd code to clear and redraw page.
	volatile uint8_t       lcd_menu_state;
	volatile uint16_t      lcd_menu_mul;
	volatile uint8_t       lcd_menu_idx;
	volatile uint8_t       lcd_menu_value_idx;
	volatile uint32_t      lcd_menu_time_cnt;
#endif

#ifdef SF_ENABLE_LPM
	volatile uint8_t       lpm_state;         // The state machine value
	volatile uint8_t       lpm_fire;          // Start lpm messurement
	volatile uint32_t      lpm_start_time;    // The start time of the LPM messurement
	volatile uint32_t      lpm_total_time;    // The total time of LPM messurement
	volatile uint16_t      lpm_result;        // The LPM result in /10.
	volatile uint16_t      lpm_level;         // The level of messurement
#endif

#ifdef SF_ENABLE_PTC0
	volatile uint16_t      ptc_0cnt;
	volatile uint8_t       ptc_0run_cnt;
	volatile uint8_t       ptc_0map_idx;
	volatile uint16_t      ptc_0mul_cnt;
	volatile uint8_t       ptc_0step;
#endif
#ifdef SF_ENABLE_PTC1
	volatile uint16_t      ptc_1cnt;
	volatile uint8_t       ptc_1run_cnt;
	volatile uint8_t       ptc_1map_idx;
	volatile uint16_t      ptc_1mul_cnt;
	volatile uint8_t       ptc_1step;
#endif

#ifdef SF_ENABLE_PTT
	volatile uint8_t       ptt_idx[PTT_TRIG_VAR_SIZE];
	volatile uint16_t      ptt_cnt[PTT_TRIG_VAR_SIZE];
	volatile uint8_t       ptt_fire[PTT_TRIG_VAR_SIZE];
	volatile uint8_t       ptt_step[PTT_TRIG_VAR_SIZE];
#endif

	volatile uint16_t      dev_volt[DEV_VAR_MAX];  // Device volt variable for programatic usage.
	volatile uint16_t      dev_amp[DEV_VAR_MAX];   // Device amps
	volatile uint16_t      dev_temp[DEV_VAR_MAX];  // Device temperature
	volatile uint16_t      dev_var[DEV_VAR_MAX];   // Generic device parameters for programatic use.

#ifdef SF_ENABLE_PWM
	volatile uint8_t       pulse_fire;             // The Pulse Fire for internal triggering of pulse
	volatile uint16_t      pulse_fire_cnt;         // Counter for pulsefire 
	volatile uint16_t      pulse_fire_freq;        // The pulsefire cnt freq
	volatile uint16_t      pulse_fire_freq_cnt;    // The pulsefire cnt freq internal counter // todo missing in vars
	volatile uint8_t       pulse_hold_fire;        // Holds or resets the pulse fire state.
	volatile uint8_t       pulse_reset_fire;       // Resets pulse train
	volatile uint8_t       pulse_resume_fire;      // Resume pulse train after hold.
	volatile uint8_t       pulse_step;             // The current pulse step

	volatile uint8_t       pwm_state;              // Interal state of pwm
	volatile uint8_t       pwm_loop_cnt;           // Pwm loop counter for this step
#endif

#ifdef SF_ENABLE_MAL
	volatile uint8_t       mal_pc;               // Mal program counter
	volatile uint8_t       mal_state;            // program state
	volatile uint16_t      mal_var[MAL_VAR_MAX]; // mal internal variables
	volatile uint8_t       mal_wait_cnt;         // mal wait cnt
	volatile uint16_t      mal_fire[MAL_FIRE_MAX]; // MAL fire triggers.
#endif

#ifdef SF_ENABLE_STV
	volatile uint8_t       stv_state;
	volatile uint8_t       stv_wait_cnt;
	volatile uint8_t       stv_map_idx;
#endif

	volatile uint8_t       req_tx_push;    // Push conf changes to Serial
	volatile uint8_t       req_tx_echo;    // Local echo of received characters
	volatile uint8_t       req_tx_promt;   // Print promt after cmd is ready.
	volatile uint8_t       req_tx_hex;     // Print all var names in idx hex.

	// == Special variables because these are not in the VARS list  ==

	char                   unpstr_buff[UNPSTR_BUFF_SIZE]; // buffer to copy progmem data into
	volatile char          cmd_buff[CMD_BUFF_SIZE];       // Command buffer for serial cmds
	volatile uint8_t       cmd_buff_idx;                  // Command index
	volatile uint8_t       cmd_process;                   // Processing command
	volatile uint16_t      vars_int_buff[VARS_INT_NUM_SIZE][VARS_INT_SIZE]; // print int vars into normal code loop
	volatile uint16_t      pwm_data[PWM_DATA_MAX][2];
	volatile uint8_t       pwm_data_max;
	char                   lcd_buff[20];

	volatile uint8_t       spi_int_req;  // note spi_ not in vars !
	volatile uint8_t       spi_int_pin;
	volatile uint8_t       spi_int_data8;
	volatile uint8_t       spi_int_data16;

	volatile uint16_t      sys_loop1_cnt;      // 20hz loop counter not in ar
	volatile uint8_t       sys_loop1_cnt_idx;  // 20hz loop index

	volatile uint8_t       mal_pc_fire;        // Temp store pc when run from trigger (NOTE; not in PF_VARS)

	volatile uint8_t       idx_adc_value; // cache vars id until Vars_getIndexFromName can be done in macro
	volatile uint8_t       idx_dic_value;

} pf_data_struct;

// All pulsefire variables are stored in one of these structs;
extern pf_data_struct       pf_data;
extern pf_conf_struct       pf_conf;

// Dynamicly calculate PF_VARS size based on SF_ENABLE_* flags.
#define PF_VARS_SIZE Vars_getSize()
#define PF_VARS_PF_SIZE     35
#ifdef SF_ENABLE_SPI
	#define PF_VARS_SPI_SIZE  2
#else
	#define PF_VARS_SPI_SIZE  0
#endif
#ifdef SF_ENABLE_PWM
	#define PF_VARS_PWM_SIZE  47
#else
	#define PF_VARS_PWM_SIZE  0
#endif
#ifdef SF_ENABLE_CIP
	#define PF_VARS_CIP_SIZE  24
#else
	#define PF_VARS_CIP_SIZE  0
#endif
#ifdef SF_ENABLE_LCD
	#define PF_VARS_LCD_SIZE  13
#else
	#define PF_VARS_LCD_SIZE  0
#endif
#ifdef SF_ENABLE_LPM
	#define PF_VARS_LPM_SIZE  10
#else
	#define PF_VARS_LPM_SIZE  0
#endif
#ifdef SF_ENABLE_ADC
	#define PF_VARS_ADC_SIZE  7
#else
	#define PF_VARS_ADC_SIZE  0
#endif
#ifdef SF_ENABLE_PTC0
	#define PF_VARS_PTC0_SIZE  8
#else
	#define PF_VARS_PTC0_SIZE  0
#endif
#ifdef SF_ENABLE_PTC1
	#define PF_VARS_PTC1_SIZE  8
#else
	#define PF_VARS_PTC1_SIZE  0
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
#ifdef SF_ENABLE_VSC0
	#define PF_VARS_VSC0_SIZE  4+2
#else
	#define PF_VARS_VSC0_SIZE  0
#endif
#ifdef SF_ENABLE_VSC1
	#define PF_VARS_VSC1_SIZE  4+2
#else
	#define PF_VARS_VSC1_SIZE  0
#endif
#ifdef SF_ENABLE_AVR
	#define PF_VARS_AVR_SIZE  4
#else
	#define PF_VARS_AVR_SIZE  0
#endif
#ifdef SF_ENABLE_AVR_MEGA
	#define PF_VARS_AVR_MEGA_SIZE  2
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
boolean Vars_isMenuSkip(byte idx);
boolean Vars_isNoReset(byte idx);
boolean Vars_isPush(byte idx);
boolean Vars_isTypeData(byte idx);
char*   Vars_getName(uint8_t idx);
uint8_t Vars_getBitType(byte idx);
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
uint32_t Vars_setValue32(uint8_t idx,uint8_t idxA,uint8_t idxB,uint32_t value);
void Vars_readConfig(void);
void Vars_writeConfig(void);
void Vars_resetConfig(void);
void Vars_resetData(void);
void Vars_setup(void);
void Vars_loop(void);

// end include
#endif


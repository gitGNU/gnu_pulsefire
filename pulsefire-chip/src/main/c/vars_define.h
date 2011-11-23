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
#ifndef _VARS_DEFINE_H
#define _VARS_DEFINE_H

// External defines for building PulseFire custum fit.
// SoftwareFlags to enable features by the build, see makefile.
#//define SF_ENABLE_PWM              // enable pwm code       (+...) !! note no dep check
#//define SF_ENABLE_LCD              // enable lcd output     (+3520)
#//define SF_ENABLE_LPM              // enable lpm code       (+2094)
#//define SF_ENABLE_PPM              // enable ppm code       (+672) (DEP: PWM)
#//define SF_ENABLE_ADC              // enable adc code       (+952)
#//define SF_ENABLE_DIC              // enable dic code       (+370)
#//define SF_ENABLE_DOC              // enable doc code       (+370)
#//define SF_ENABLE_DEV              // enable dev vars       (+...)
#//define SF_ENABLE_PTC              // enable ptc timers     (+...)
#//define SF_ENABLE_PTT              // enable ptt timers     (+...)
#//define SF_ENABLE_STV              // enable stv code       (+...)
#//define SF_ENABLE_VFC              // enable vfc code       (+...)
#//define SF_ENABLE_FRQ              // enable req_freq_* cmd (+1044)
#//define SF_ENABLE_SWC              // enable soft warmup   (+480) (DEP: PWM)
#//define SF_ENABLE_MAL              // enable mal code       (+2250)
#//define SF_ENABLE_DEBUG          // enable some debug     (+1044)
#//define SF_ENABLE_EXT_OUT        // enable extended output for 8 outputs
#//define SF_ENABLE_EXT_OUT_16BIT  // enable extended output for 16 outputs (with: EXT_OUT !!)
#//define SF_ENABLE_EXT_LCD        // enable extended connection mode lcd.
#//define SF_ENABLE_EXT_LCD_DIC    // enable multiplexing DIC inputs (with: EXT_LCD !!)
#//define SF_ENABLE_EXT_LCD_DOC    // enable second extended chip after lcd for DOC output(with: EXT_LCD !!)
#//define SF_ENABLE_EXT_LCD_DOC_16BIT  // enable second extended chip after lcd for DOC output(with: EXT_LCD !!)

// Customizable data
#define LCD_INIT_MSG_TIME         555    // Welcome message timeout
#define LCD_TEST_DOT_TIME          25    // 25ms per col test dot delay
#define LCD_REFRESH_TIME          200    // Refresh after 200ms of last refresh
#define LCD_MENU_TIMEOUT         4000    // Exit menu after 4000ms 
#define SYS_INPUT_TIME              5    // no not check more often then every 5ms
#define SYS_INPUT_DELAY           300    // Delay after user input
#define ADC_INPUT_TIME            100    // read max 10x per second
#define DIC_INPUT_TIME            150    // read after 150ms time
#define CHIP_INFO_NAME "MyFreePulseBox"  // Max 16 chars for Manufacture name box-type.
#define CHIP_INFO_NAME_ID "71296105195"  // Max 16 chars for Manufacture name box-type ID.

// Defaut config values (init or after reset_conf)
#define DEFAULT_SYS_ADC_JITTER      3    // Minimal adc value change to remove jitter
#define DEFAULT_SYS_WARMUP_DUTY     5    // Default duty wait multiplyer for soft warmup
#define DEFAULT_LPM_START        1000    // Some value to start messuring
#define DEFAULT_LPM_STOP          800    // Some value when to stop, which defines size
#define DEFAULT_PULSE_DATA_INIT     1    // Start with output 0 set to high
#define DEFAULT_PULSE_STEPS         3    // Default start with 3 outputs because of lowest max outputs in SF_ config.
#define DEFAULT_PPM_DATA        21845    // 50% pulse rate example data
#define DEFAULT_PWM_LOOP           16    // Divide pwm step by 16 
#define DEFAULT_PWM_CLOCK           2    // Default on divide /8
#define DEFAULT_PWM_ON_CNT      32768    // Count 30k per step before next step

// MCU depended variables
#if (__AVR_ATmega1280__ || __AVR_ATmega2560__)
	#define SF_ENABLE_AVR_MEGA           // Define avr mega
	#define CHIP_EEPROM_SIZE       4096  // 4096 bytes eeprom
	#define MAL_PROGRAM_SIZE        128
	#define MAL_PROGRAM_MAX           8
	#define ADC_NUM_MAX              16
	#define PTC_TIME_MAP_MAX         32
	#define PTT_TRIG_MAP_MAX         16
	#define STV_MAX_MAP_MAX          32
	#define STV_MIN_MAP_MAX          16
	#define DEV_VAR_MAX              16
	#define VFC_MAP_MAX               8  // NOTE: all these mega max are also ~max for gui support for layout issues.
#elif __AVR_ATmega328P__
	#define SF_ENABLE_AVR                // Define AVR
	#define CHIP_EEPROM_SIZE       1024  // 1024 bytes eeprom
	#define MAL_PROGRAM_SIZE         64
	#define MAL_PROGRAM_MAX           2
	#define ADC_NUM_MAX               6
	#define PTC_TIME_MAP_MAX          8
	#define PTT_TRIG_MAP_MAX          4
	#define STV_MAX_MAP_MAX           8
	#define STV_MIN_MAP_MAX           4
	#define DEV_VAR_MAX               4
	#define VFC_MAP_MAX               3
#elif __AVR_ATmega168P__
	#define SF_ENABLE_AVR                  // Define AVR
	#define CHIP_EEPROM_SIZE        512    // 512 bytes eeprom
	#define MAL_PROGRAM_SIZE         64    // config array size of basic program
	#define MAL_PROGRAM_MAX           1    // Total amount of diffent programs
	#define ADC_NUM_MAX               6    // Max 6 analog input
	#define PTC_TIME_MAP_MAX          4    // Programatic Time slots
	#define PTT_TRIG_MAP_MAX          2    // Programatic Trigger Time slots
	#define STV_MAX_MAP_MAX           4    // Maping of safety trashhold values.
	#define STV_MIN_MAP_MAX           2    // Maping of safety trashhold values.
	#define DEV_VAR_MAX               2    // Generic device variables
	#define VFC_MAP_MAX               2    // Virtual feedback channels
#elif __ARM_ARCH_7M__
	#define SF_ENABLE_ARM_7M               // Define ARM
	#define CHIP_EEPROM_SIZE       1024  // 1024 bytes eeprom (7m has no eeprom?)
	#define MAL_PROGRAM_SIZE         64
	#define MAL_PROGRAM_MAX           2
	#define ADC_NUM_MAX               6
	#define PTC_TIME_MAP_MAX          8
	#define PTT_TRIG_MAP_MAX          4
	#define STV_MAX_MAP_MAX           8
	#define STV_MIN_MAP_MAX           4
	#define DEV_VAR_MAX               4
	#define VFC_MAP_MAX               3
#else
	#error "Don't know how to run on your MCU_TYPE."
#endif

//
// Some overrides to have custom builds
//
#ifdef _MAL_PROGRAM_SIZE
	#undef  MAL_PROGRAM_SIZE
	#define MAL_PROGRAM_SIZE _MAL_PROGRAM_SIZE
#endif
#ifdef _MAL_PROGRAM_MAX
	#undef  MAL_PROGRAM_MAX
	#define MAL_PROGRAM_MAX _MAL_PROGRAM_MAX
#endif
#ifdef _ADC_NUM_MAX
	#undef  ADC_NUM_MAX
	#define ADC_NUM_MAX _ADC_NUM_MAX
#endif
#ifdef _PTC_TIME_MAP_MAX
	#undef  PTC_TIME_MAP_MAX
	#define PTC_TIME_MAP_MAX _PTC_TIME_MAP_MAX
#endif
#ifdef _PTT_TRIG_MAP_MAX
	#undef  PTT_TRIG_MAP_MAX
	#define PTT_TRIG_MAP_MAX _PTT_TRIG_MAP_MAX
#endif
#ifdef _STV_MAX_MAP_MAX
	#undef  STV_MAX_MAP_MAX
	#define STV_MAX_MAP_MAX _STV_MAX_MAP_MAX
#endif
#ifdef _STV_MIN_MAP_MAX
	#undef  STV_MIN_MAP_MAX
	#define STV_MIN_MAP_MAX _STV_MIN_MAP_MAX
#endif
#ifdef _DEV_VAR_MAX
	#undef  DEV_VAR_MAX
	#define DEV_VAR_MAX _DEV_VAR_MAX
#endif
#ifdef _VFC_MAP_MAX
	#undef  VFC_MAP_MAX
	#define VFC_MAP_MAX _VFC_MAP_MAX
#endif


// end include
#endif

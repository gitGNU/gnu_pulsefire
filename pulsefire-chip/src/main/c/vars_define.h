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
#//define SF_ENABLE_PWM               // enable pwm code       (+...)
#//define SF_ENABLE_CIP               // enable cip code       (+...) (DEP: MEGA)
#//define SF_ENABLE_ADC               // enable adc code       (+...)
#//define SF_ENABLE_PTC               // enable ptc timers     (+...)
#//define SF_ENABLE_PTT               // enable ptt timers     (+...)
#//define SF_ENABLE_STV               // enable stv code       (+...)
#//define SF_ENABLE_VFC               // enable vfc code       (+...)
#//define SF_ENABLE_SWC               // enable soft warmup    (+...)
#//define SF_ENABLE_MAL               // enable mal code       (+...)
#//define SF_ENABLE_SPI               // enable spi code       (+...)
#//define SF_ENABLE_LCD               // enable lcd output     (+...)
#//define SF_ENABLE_DEBUG             // enable some debug     (+...)

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
	#define INT_MAP_MAX               4  // Set variables on interrupt.
	#define MAL_CODE_SIZE           512  // Micro asm program code size
	#define MAL_FIRE_MAX              6  // Micro fire jump table
	#define FIRE_MAP_MAX              3  // Event on pulse logic
	#define ADC_MAP_MAX              16  // Analog input
	#define DIC_MAP_MAX              16  // Digital inputs
	#define SWC_MAP_MAX               4  // Softwarmup actions
	#define PTC_TIME_MAP_MAX         24  // Programatic Time slots
	#define PTT_TRIG_MAP_MAX         16  // Programatic Trigger Time slots
	#define STV_MAX_MAP_MAX          24  // Mapping of safety trashhold min values
	#define STV_MIN_MAP_MAX          16  // Mapping of safety trashhold max values
	#define STV_WARN_MAP_MAX          6  // Mapping of safety warning action
	#define STV_ERROR_MAP_MAX         6  // Mapping of safety error action
	#define DEV_VAR_MAX               6  // Generic device variables
	#define VFC_MAP_MAX              12  // Virtual feedback channels  // NOTE: All these mega max are also ~max for gui support for layout issues.
	#define LPM_RELAY_MAP_MAX         2  // Output mapping for relay pin
#elif __AVR_ATmega328P__
	#define SF_ENABLE_AVR                // Define AVR
	#define CHIP_EEPROM_SIZE       1024  // 1024 bytes eeprom
	#define INT_MAP_MAX               2
	#define MAL_CODE_SIZE            64
	#define MAL_FIRE_MAX              2
	#define FIRE_MAP_MAX              1
	#define ADC_MAP_MAX               6
	#define DIC_MAP_MAX               8
	#define SWC_MAP_MAX               2
	#define PTC_TIME_MAP_MAX          8
	#define PTT_TRIG_MAP_MAX          4
	#define STV_MAX_MAP_MAX           8
	#define STV_MIN_MAP_MAX           4
	#define STV_WARN_MAP_MAX          2
	#define STV_ERROR_MAP_MAX         2
	#define DEV_VAR_MAX               2
	#define VFC_MAP_MAX               3
	#define LPM_RELAY_MAP_MAX         1
#elif __AVR_ATmega168P__
	#define SF_ENABLE_AVR                // Define AVR
	#define CHIP_EEPROM_SIZE        512  // 512 bytes eeprom
	#define INT_MAP_MAX               1
	#define MAL_CODE_SIZE            24  // note: if all on then config is >512 so todo: check smaller builds.
	#define MAL_FIRE_MAX              1
	#define FIRE_MAP_MAX              1
	#define ADC_MAP_MAX               6
	#define DIC_MAP_MAX               8
	#define SWC_MAP_MAX               1
	#define PTC_TIME_MAP_MAX          3
	#define PTT_TRIG_MAP_MAX          2
	#define STV_MAX_MAP_MAX           2
	#define STV_MIN_MAP_MAX           2
	#define STV_WARN_MAP_MAX          1
	#define STV_ERROR_MAP_MAX         1
	#define DEV_VAR_MAX               1
	#define VFC_MAP_MAX               1
    #define LPM_RELAY_MAP_MAX         1
#elif __ARM_ARCH_7M__
	#define SF_ENABLE_ARM_7M             // Define ARM
	#define CHIP_EEPROM_SIZE       1024  // 1024 bytes eeprom (7m has no eeprom?)
	#define INT_MAP_MAX               2
	#define MAL_CODE_SIZE            64
	#define MAL_FIRE_MAX              2
	#define FIRE_MAP_MAX              1
	#define ADC_MAP_MAX               6
	#define DIC_MAP_MAX               8
	#define SWC_MAP_MAX               2
	#define PTC_TIME_MAP_MAX          8
	#define PTT_TRIG_MAP_MAX          4
	#define STV_MAX_MAP_MAX           8
	#define STV_MIN_MAP_MAX           4
	#define STV_WARN_MAP_MAX          2
	#define STV_ERROR_MAP_MAX         2
	#define DEV_VAR_MAX               2
	#define VFC_MAP_MAX               3
    #define LPM_RELAY_MAP_MAX         1
#else
	#error "Don't know how to run on your MCU_TYPE."
#endif

//
// Some overrides to have custom builds
//
#ifdef _MAL_CODE_SIZE
	#undef  MAL_CODE_SIZE
	#define MAL_CODE_SIZE _MAL_CODE_SIZE
#endif
#ifdef _MAL_FIRE_MAX
	#undef  MAL_FIRE_MAX
	#define MAL_FIRE_MAX _MAL_FIRE_MAX
#endif
#ifdef _ADC_MAP_MAX
	#undef  ADC_MAP_MAX
	#define ADC_MAP_MAX _ADC_MAP_MAX
#endif
#ifdef _DIC_MAP_MAX
	#undef  DIC_MAP_MAX
	#define DIC_MAP_MAX _DIC_MAP_MAX
#endif
#ifdef _SWC_MAP_MAX
	#undef  SWC_MAP_MAX
	#define SWC_MAP_MAX _SWC_MAP_MAX
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
#ifdef _STV_WARN_MAP_MAX
	#undef  STV_WARN_MAP_MAX
	#define STV_WARN_MAP_MAX _STV_WARN_MAP_MAX
#endif
#ifdef _STV_ERROR_MAP_MAX
	#undef  STV_ERROR_MAP_MAX
	#define STV_ERROR_MAP_MAX _STV_ERROR_MAP_MAX
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

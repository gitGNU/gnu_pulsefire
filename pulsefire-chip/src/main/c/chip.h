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
#ifndef _CHIP_H
#define _CHIP_H

#include "vars_define.h"
#if defined(SF_ENABLE_AVR) || defined(SF_ENABLE_AVR_MEGA)
  #include <avr/io.h>
  #include <avr/wdt.h>
  #include <util/delay.h>
  #include <avr/eeprom.h>
  #include <avr/pgmspace.h>
  #include <avr/interrupt.h>
  #define CHIP_PTR_TYPE       uint16_t   // 16 bit pointer size
  #define CHIP_PROGMEM        PROGMEM    // avr gcc flag to define data in progmem
  #define CHIP_PROGMEM_ARRAY    PGM_P    // flag to define data in progmem array
#elif defined(SF_ENABLE_ARM_7M)
  #include <stdint.h>
  #ifdef __cplusplus
    #define NULL 0
  #else
    #define NULL ((void *)0)
  #endif
  #define CHIP_PTR_TYPE       uint32_t       // 32 bit pointer size
  #define CHIP_PROGMEM                       // empty for arm
  #define CHIP_PROGMEM_ARRAY  const char*    // flag to define data in progmem array
#else
# error "Don't know how to run on your MCU_TYPE."
#endif

#include "vars.h"
#include "pwm.h"
#include "input.h"
#include "serial.h"

void Chip_setup(void);
void Chip_loop(void);
void Chip_reset(void);
void Chip_delay(uint16_t delay);
void Chip_delayU(uint16_t delay);
void Chip_sei(void);
uint32_t Chip_free_ram(void);
const char* Chip_cpu_type(void);
uint32_t millis(void);

uint8_t digitalRead(volatile uint8_t *port,uint8_t pin);
void digitalWrite(volatile uint8_t *port,uint8_t pin,uint8_t value);
void shiftOut(volatile uint8_t *port,uint8_t dataPin,uint8_t clkPin,uint8_t dataByte);

void    Chip_eeprom_read(void* eemem);
void    Chip_eeprom_write(void* eemem);
uint8_t       Chip_pgm_readByte(const char* p);
CHIP_PTR_TYPE Chip_pgm_readWord(const CHIP_PTR_TYPE* p);

void          Chip_reg_set(uint8_t reg,uint16_t value);

void Chip_out_pwm(uint16_t data);
void Chip_out_serial(uint8_t data);
void Chip_out_lcd(uint8_t data,uint8_t cmd,uint8_t mux);
void Chip_out_doc(uint16_t data);

void     Chip_in_int_pin(uint8_t pin,uint8_t enable);
void     Chip_in_adc(uint8_t channel);
uint8_t  Chip_in_menu(void);
uint16_t Chip_in_dic(void);

// end include
#endif

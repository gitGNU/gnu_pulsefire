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

#include <avr/wdt.h>
#include <avr/interrupt.h>
#include <util/delay.h>
#include "vars.h"
#include "output.h"
#include "serial.h"

// ATMEL ATMEGA8 & 168 / ARDUINO
//
//                  +-\/-+
//            PC6  1|    |28  PC5 (AI 5)
//      (D 0) PD0  2|    |27  PC4 (AI 4)
//      (D 1) PD1  3|    |26  PC3 (AI 3)
//      (D 2) PD2  4|    |25  PC2 (AI 2)
// PWM+ (D 3) PD3  5|    |24  PC1 (AI 1)
//      (D 4) PD4  6|    |23  PC0 (AI 0)
//            VCC  7|    |22  GND
//            GND  8|    |21  AREF
//            PB6  9|    |20  AVCC
//            PB7 10|    |19  PB5 (D 13)
// PWM+ (D 5) PD5 11|    |18  PB4 (D 12)
// PWM+ (D 6) PD6 12|    |17  PB3 (D 11) PWM
//      (D 7) PD7 13|    |16  PB2 (D 10) PWM
//      (D 8) PB0 14|    |15  PB1 (D 9) PWM
//                  +----+
//

// PIN MAPPING FOR DEFAULT CONNECTION MODE
#define IO_DEF_IO_PORT          &PORTD
#define IO_DEF_IO_PORT_IN       &PIND
#define IO_DEF_RX_PIN               0    // Rx/Tx for serial is wired internally to USB
#define IO_DEF_TX_PIN               1
#define IO_DEF_PIN2_PIN             2    // (def) Trigger
#define IO_DEF_PIN3_PIN             3    // (def) enter menu
#define IO_DEF_PIN4_PIN             4    // (def) menu or trigger or startlpm
#define IO_DEF_PIN5_PIN             5    // (def) External clock source for pwm
#define IO_DEF_LCD_RS_PIN           6
#define IO_DEF_LCD_E_PIN            7
#define IO_DEF_OUT_PORT         &PORTB
#define IO_DEF_OUT_0_PIN            0
#define IO_DEF_OUT_1_PIN            1
#define IO_DEF_OUT_2_PIN            2
#define IO_DEF_OUT_3_PIN            3
#define IO_DEF_OUT_4_PIN            4
#define IO_DEF_OUT_5_PIN            5
#define IO_DEF_ADC_PORT         &PORTC
#define IO_DEF_LCD_D0_PIN           0
#define IO_DEF_LCD_D1_PIN           1
#define IO_DEF_LCD_D2_PIN           2
#define IO_DEF_LCD_D3_PIN           3
#define IO_DEF_ADC4_PIN             4    // Only analog 4 and 5 are usable in default mode
#define IO_DEF_ADC5_PIN             5

// PIN MAPPING FOR EXTENDED CONNECTION MODE
#define IO_EXT_RX_PIN               0
#define IO_EXT_TX_PIN               1
#define IO_EXT_PIN2_PIN             2
#define IO_EXT_PIN3_PIN             3
#define IO_EXT_PIN4_PIN             4
#define IO_EXT_PIN5_PIN             5
#define IO_EXT_INPUT0_PIN           6    // Digital inputs or maybe push out for pll stuff.
#define IO_EXT_INPUT1_PIN           7    // Will be finalized after some timer2 input code
#define IO_EXT_OUT_DATA_PIN         0  // 8  output 0-7 and 8-15 via 2 chip casade
#define IO_EXT_OUT_CLK_PIN          1  // 9
#define IO_EXT_OUT_E_PIN            2  // 10
#define IO_EXT_S2P_DATA_PIN         3  // 11 lcd D0-D3,RS,E,mux0/1=Select digital input via dual 4to1 multiplexer
#define IO_EXT_S2P_CLK_PIN          4 // 12
#define IO_EXT_S2P_E_PIN            5  // 13
#define IO_EXT_ADC0_PIN             0  //
#define IO_EXT_ADC1_PIN             1
#define IO_EXT_ADC2_PIN             2
#define IO_EXT_ADC3_PIN             3
#define IO_EXT_ADC4_PIN             4
#define IO_EXT_ADC5_PIN             5

void Chip_setup(void);
void Chip_loop(void);
void Chip_reset(void);
void Chip_delay(uint16_t delay);
void Chip_delayU(uint16_t delay);
uint32_t millis(void);
uint8_t digitalRead(volatile uint8_t *port,uint8_t pin);
void digitalWrite(volatile uint8_t *port,uint8_t pin,uint8_t value);
uint16_t analogRead(uint8_t channel);
void shiftOut(volatile uint8_t *port,uint8_t dataPin,uint8_t clkPin,uint8_t dataByte);

// end include
#endif

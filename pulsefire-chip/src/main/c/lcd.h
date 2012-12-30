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
#ifndef _LCD_H
#define _LCD_H

#include "vars.h"
#include "chip.h"
#include "utils.h"
#include "freq.h"

#define LCD_CMD_HOME        0x02
#define LCD_CMD_CLEAR       0x01
#define LCD_CMD_CURSOR      0x80

void lcd_write(uint8_t c,uint8_t cmd);
void lcd_home(void);
void lcd_clear(void);
void lcd_cursor(uint8_t col, uint8_t row);
void lcd_printDot(void);
void lcd_printSpace(void);
void lcd_printChar(char* dstring);
void lcd_printCharP(const char* argu);
void lcd_printByte(uint8_t value);
void lcd_printByteNum(uint8_t value,uint8_t numSize);
void lcd_print(uint16_t value);
void lcd_printNum(uint16_t value,uint8_t numSize);
void lcd_setup(void);
void lcd_init(void);
void lcd_loop(void);

// end include
#endif

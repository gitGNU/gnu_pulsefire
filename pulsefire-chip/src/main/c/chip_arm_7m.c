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


void Chip_loop(void) {
}

void Chip_reset(void) {
}

void Chip_setup(void) {
}

uint32_t millis(void) {
	return ZERO;
}

void Chip_delay(uint16_t delay) {
}

void Chip_delayU(uint16_t delay) {
}

uint8_t digitalRead(volatile uint8_t *port,uint8_t pin) {
	return ZERO;
}

void digitalWrite(volatile uint8_t *port,uint8_t pin,uint8_t value) {
}

uint16_t analogRead(uint8_t channel) {
	return ZERO;
}

void shiftOut(volatile uint8_t *port,uint8_t dataPin,uint8_t clkPin,uint8_t dataByte) {
}

uint8_t Chip_pgm_read(const char* p) {
	return ZERO;
}

void Chip_io_pwm(uint16_t data) {
}

void Chip_io_serial(uint8_t data) {
}

void Chip_io_lpm(uint8_t data) {
}

void Chip_io_int_pin(uint8_t pin,uint8_t enable) {
}

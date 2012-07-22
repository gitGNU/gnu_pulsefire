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

// ATMEL ATMEGA8 & 168 / ARDUINO
//
//                  +-\/-+
//            PC6  1|    |28  PC5 (AI 5)
//      (D 0) PD0  2|    |27  PC4 (AI 4)
//      (D 1) PD1  3|    |26  PC3 (AI 3)
//      (D 2) PD2  4|    |25  PC2 (AI 2)
// oc2b (D 3) PD3  5|    |24  PC1 (AI 1)
//      (D 4) PD4  6|    |23  PC0 (AI 0)
//            VCC  7|    |22  GND
//            GND  8|    |21  AREF
//            PB6  9|    |20  AVCC
//            PB7 10|    |19  PB5 (D 13)
//      (D 5) PD5 11|    |18  PB4 (D 12)
//      (D 6) PD6 12|    |17  PB3 (D 11) oc2a
//      (D 7) PD7 13|    |16  PB2 (D 10)
//      (D 8) PB0 14|    |15  PB1 (D 9)
//                  +----+
//

// PIN MAPPING FOR DEFAULT CONNECTION MODE
#define IO_DEF_IO_PORT          &PORTD
#define IO_DEF_IO_PORT_IN       &PIND
#define IO_DEF_RX_PIN               0  // PIN 0 = Rx/Tx for serial is wired internally to USB
#define IO_DEF_TX_PIN               1  // PIN 1
#define IO_DEF_PIN2_PIN             2  // PIN 2 = (def) Trigger
#define IO_DEF_PIN3_PIN             3  // PIN 3 = (def) enter menu
#define IO_DEF_PIN4_PIN             4  // PIN 4 = (def) menu or trigger or startlpm
#define IO_DEF_PIN5_PIN             5  // PIN 5 (def) External clock source for pwm
#define IO_DEF_LCD_RS_PIN           6  // PIN 6
#define IO_DEF_LCD_E_PIN            7  // PIN 7
#define IO_DEF_OUT_PORT         &PORTB
#define IO_DEF_OUT_0_PIN            0  // PIN 8
#define IO_DEF_OUT_1_PIN            1  // PIN 9
#define IO_DEF_OUT_2_PIN            2  // PIN 10
#define IO_DEF_OUT_3_PIN            3  // PIN 11
#define IO_DEF_OUT_4_PIN            4  // PIN 12
#define IO_DEF_OUT_5_PIN            5  // PIN 13
#define IO_DEF_ADC_PORT         &PORTC
#define IO_DEF_LCD_D0_PIN           0  // PIN A0
#define IO_DEF_LCD_D1_PIN           1  // PIN A1
#define IO_DEF_LCD_D2_PIN           2  // PIN A2
#define IO_DEF_LCD_D3_PIN           3  // PIN A3
#define IO_DEF_ADC4_PIN             4  // PIN A4 = Only analog 4 and 5 are usable in default mode
#define IO_DEF_ADC5_PIN             5  // PIN A5

// PIN MAPPING FOR EXTENDED CONNECTION MODE
#define IO_EXT_RX_PIN               0  // PIN 0
#define IO_EXT_TX_PIN               1  // PIN 1
#define IO_EXT_PIN2_PIN             2  // PIN 2
#define IO_EXT_PIN3_PIN             3  // PIN 3
#define IO_EXT_PIN4_PIN             4  // PIN 4
#define IO_EXT_PIN5_PIN             5  // PIN 5
#define IO_EXT_INPUT0_PIN           6  // PIN 6 = Digital inputs or maybe push out for pll stuff.
#define IO_EXT_INPUT1_PIN           7  // PIN 7 = Will be finalized after some timer2 input code
#define IO_EXT_OUT_DATA_PIN         0  // PIN 8 = output 0-7 and 8-15 via 2 chip casade
#define IO_EXT_OUT_CLK_PIN          1  // PIN 9
#define IO_EXT_OUT_E_PIN            2  // PIN 10
#define IO_EXT_S2P_DATA_PIN         3  // PIN 11 lcd D0-D3,RS,E,mux0/1=Select digital input via dual 4to1 multiplexer
#define IO_EXT_S2P_CLK_PIN          4  // PIN 12
#define IO_EXT_S2P_E_PIN            5  // PIN 13
#define IO_EXT_ADC0_PIN             0  // PIN A0
#define IO_EXT_ADC1_PIN             1  // PIN A1
#define IO_EXT_ADC2_PIN             2  // PIN A2
#define IO_EXT_ADC3_PIN             3  // PIN A3
#define IO_EXT_ADC4_PIN             4  // PIN A4
#define IO_EXT_ADC5_PIN             5  // PIN A5

// Prototype and function for specific c init location.
void SRAM_init(void) __attribute__((naked)) __attribute__ ((section (".init1")));
void SRAM_init(void) {
	uint8_t *p; // Break into the C startup so I can clear SRAM to
	uint16_t i; // known values making it easier to see how it is used
	for (i=0x100; i < RAMEND; i++) {
		p = (uint8_t *)i; *p = 0x5A;
	}
}

// Prototype and function for specific c init location.
void wdt_init(void) __attribute__((naked)) __attribute__((section(".init3")));
void wdt_init(void) {
	MCUSR = ZERO;
	wdt_disable(); // Disable watchdog timer in early startup.
}

void Chip_loop(void) {
	wdt_reset();
	pf_data.sys_main_loop_cnt++;
}

void Chip_reset(void) {
	wdt_enable(WDTO_15MS); // reboot in 15ms.
	Chip_delay(30);
}

void Chip_setup(void) {
	// Config pin2 which is multi function
	DDRD = 0x00;  // default to input
	PORTD = 0xFF; // with pullup
	switch (pf_conf.avr_pin2_map) {
		case PIN2_TRIG_IN:
		case PIN2_FREQ_IN:
		case PIN2_FIRE_IN:
		case PIN2_HOLD_FIRE_IN:
			Chip_in_int_pin(ZERO,ZERO); // turn on int0
			break;
		case PIN2_DOC2_OUT:
		case PIN2_DOC8_OUT:
			DDRD  |=  (ONE<<IO_DEF_PIN2_PIN);
			PORTD &= ~(ONE<<IO_DEF_PIN2_PIN);
		default:
			break;
	}
	switch (pf_conf.avr_pin3_map) {
		case PIN3_FREQ_IN:
		case PIN3_FIRE_IN:
		case PIN3_HOLD_FIRE_IN:
			Chip_in_int_pin(ONE,ZERO); // turn on int1
			break;
		case PIN3_DOC3_OUT:
		case PIN3_DOC9_OUT:
		case PIN3_CIT0B_OUT:
			DDRD  |=  (ONE<<IO_DEF_PIN3_PIN);
			PORTD &= ~(ONE<<IO_DEF_PIN3_PIN);
		default:
			break;
	}
	switch (pf_conf.avr_pin4_map) {
		case PIN4_DOC4_OUT:
		case PIN4_DOC10_OUT:
			DDRD  |=  (ONE<<IO_DEF_PIN4_PIN);
			PORTD &= ~(ONE<<IO_DEF_PIN4_PIN);
		default:
			break;
	}
	switch (pf_conf.avr_pin5_map) {
		case PIN5_DOC5_OUT:
		case PIN5_DOC11_OUT:
			DDRD  |=  (ONE<<IO_DEF_PIN5_PIN);
			PORTD &= ~(ONE<<IO_DEF_PIN5_PIN);
		default:
			break;
	}

	DDRC = 0x00;
	PORTC = 0x00;
	// Map LCD pins
#ifndef SF_ENABLE_EXT_LCD
	DDRD  |=  (ONE<<IO_DEF_LCD_RS_PIN);
	PORTD &= ~(ONE<<IO_DEF_LCD_RS_PIN);
	DDRD  |=  (ONE<<IO_DEF_LCD_E_PIN);
	PORTD &= ~(ONE<<IO_DEF_LCD_E_PIN);
	DDRC  |=  (ONE<<PINC0);
	DDRC  |=  (ONE<<PINC1);
	DDRC  |=  (ONE<<PINC2);
	DDRC  |=  (ONE<<PINC3);
	PORTC  =  0x0F;
#endif

	DDRB  = 0xFF; // Port B is in all connection modes always output
#ifdef SF_ENABLE_PWM
	PWM_send_output(PULSE_DATA_OFF); // send off state to output
#endif

	// Timer1 16bit timer used for pulse steps.
	ICR1 = 0xFFFF;OCR1A = 0xFFFF;OCR1B = 0xFFFF;
	TCCR1A = ZERO;
	TCCR1B = (ONE+ONE) & 7;
	TIMSK1|= (ONE << OCF1A);
	TIMSK1|= (ONE << OCF1B);
	TCNT1  = ZERO;
#ifdef SF_ENABLE_PWM
	TCCR1B = pf_conf.pwm_clock & 7;
#endif

	// Timer2 8bit timer used CIT
	OCR2A  = 0xFF;OCR2B  = 0xFF;
	TCCR2A = ZERO;TCCR2B = ZERO;
	//TIMSK2|= (ONE << TOIE2);
	//TIMSK2|= (ONE << OCF2A);
	//TIMSK2|= (ONE << OCF2B);

	// setup interrupts signals
	EICRA |= (1 << ISC01);  // Falling-Edge Triggered INT0
	EICRA |= (1 << ISC11);  // Falling-Edge Triggered INT1

	// Timer0 is used for timemanagement
	TCCR0A |= (1 << WGM00) | (1 << WGM01); // Fast pwm
	TCCR0B |= (1 << CS01);                 // prescaler /8
	TIMSK0 |= (1 << TOIE0);                // int op overflow
	TCNT0 = ZERO;

	// enable adc
	ADCSRA |= (1 << ADEN) | (1 << ADPS2) | (1 << ADPS1) | (1 << ADPS0); // 16 MHz/128 = 125 KHz = 50-200 KHz range
	ADCSRA |= (1<<ADIE); // enable interupts after conversion
	DIDR0 |= (1 << ADC4D) | (1 << ADC5D); // disable digital input on adc pins
#ifdef SF_ENABLE_EXT_LCD
	DIDR0 |= (1 << ADC0D) | (1 << ADC1D) | (1 << ADC2D) | (1 << ADC3D);
#endif

	// initialize UART0
	UCSR0A = (1<<U2X0); // use double so error rate is only 2.1%.
	uint16_t ubrr = (F_CPU/4/SERIAL_SPEED-ONE)/2;
	if (ubrr > 4095) {
		UCSR0A = ZERO;
		ubrr = (F_CPU/8/SERIAL_SPEED-ONE)/2;
	}
	UBRR0H = ubrr>>8;  // set baud rate
	UBRR0L = ubrr;

	UCSR0B = (1<<RXEN0)|(1<<TXEN0)|(1<<RXCIE0);  // enable Rx & Tx
	UCSR0C = (1<<UCSZ00) | (1<<UCSZ01);          // 8n1

	// Enable pull-up on D0/RX, to supress line noise
	DDRD &= ~_BV(PIND0);
	PORTD |= _BV(PIND0);

	wdt_enable(WDT_MAIN_TIMEOUT); // enable watchdog timer, so if main loop to slow then reboot
}

uint32_t millis(void) {
	return pf_prog.sys_time_ssec*10;
}


void Chip_delay(uint16_t delay) {
	for (uint16_t i=ZERO;i<=delay;i++) {
		_delay_ms(ONE);
	}
}
void Chip_delayU(uint16_t delay) {
	for (uint16_t i=ZERO;i<=delay;i++) {
		_delay_us(ONE);
	}
}

void Chip_sei(void) {
	sei();
}

uint32_t Chip_free_ram(void) {
	uint32_t free_ram = ZERO;
	uint8_t *p;
	uint16_t i;
	for (i=RAMEND;i>0x100;i--) {
		p = (uint8_t *) i;
		if (*p == 0x5A) {
			free_ram++;
		}
	}
	return free_ram;
}

const char* Chip_cpu_type(void) {
	return pmChipCPUTypeAvr;
}

uint8_t digitalRead(volatile uint8_t *port,uint8_t pin) {
	uint8_t value = *port;
	return (value >> pin) & ONE;
}

void digitalWrite(volatile uint8_t *port,uint8_t pin,uint8_t value) {
	if (value>ZERO) {
		*port |= (ONE<<pin);
	} else {
		*port &= ~(ONE<<pin);
	}
}

void shiftOut(volatile uint8_t *port,uint8_t dataPin,uint8_t clkPin,uint8_t dataByte) {
	for( uint8_t i = 8;i>ZERO; i-- ){
		digitalWrite(port,dataPin,(dataByte & 0x80)>ZERO);
		dataByte <<= ONE;
		digitalWrite(port,clkPin, ONE);
		digitalWrite(port,clkPin, ZERO);
	}
}

void Chip_eeprom_read(void* eemem) {
	eeprom_read_block((void*)&pf_conf,eemem,sizeof(pf_conf_struct));
}
void Chip_eeprom_write(void* eemem) {
	wdt_disable();
	eeprom_update_block((const void*)&pf_conf,eemem,sizeof(pf_conf_struct));
	wdt_enable(WDT_MAIN_TIMEOUT);
}

uint8_t Chip_pgm_readByte(const char* p) {
	return pgm_read_byte(p);
}

CHIP_PTR_TYPE Chip_pgm_readWord(const CHIP_PTR_TYPE* p) {
	return pgm_read_word(p);
}

void Chip_reg_set(uint8_t reg,uint16_t value) {
	switch (reg) {
#ifdef SF_ENABLE_PWM
	case CHIP_REG_PWM_CLOCK:	TCCR1B = value & 7;		break;
	case CHIP_REG_PWM_OCR_A:	OCR1A = value;			break;
	case CHIP_REG_PWM_OCR_B:	OCR1B = value;			break;
	case CHIP_REG_PWM_TCNT:		TCNT1 = value;			break;
#endif
#ifdef SF_ENABLE_CIT
	case CHIP_REG_CIT0_CLOCK:	TCCR2B = (TCCR2B & 247) + (value & 7);			break;
	case CHIP_REG_CIT0_MODE:	TCCR2A = (TCCR2A & 252) + (value & 3);TCCR2B = (TCCR2B & 247) + (value & 8);break; // bit 0/1 + bit 3/4 in B
	case CHIP_REG_CIT0_INT:		TIMSK2 = (TIMSK2 & 247) + (value & 7);			break;
	case CHIP_REG_CIT0_OCR_A:	OCR2A = value;									break;
	case CHIP_REG_CIT0_COM_A:	TCCR2A = (TCCR2A & 63)  + ((value << 6) & 192);	break; // bit 6/7
	case CHIP_REG_CIT0_OCR_B:	OCR2B = value;									break;
	case CHIP_REG_CIT0_COM_B:	TCCR2A = (TCCR2A & 207) + ((value << 4) & 48);	break; // bit 4/5
#endif
	default:
		break;
	}
}

void Chip_out_pwm(uint16_t data) {
	// Send data to output depending on connection mode; max outs 16,8,3,6
#if defined(SF_ENABLE_EXT_OUT_16BIT)
	digitalWrite(IO_DEF_OUT_PORT,IO_EXT_OUT_E_PIN,ZERO);
		shiftOut(IO_DEF_OUT_PORT,IO_EXT_OUT_DATA_PIN,IO_EXT_OUT_CLK_PIN,(uint8_t)(data >> 8)); // high byte
		shiftOut(IO_DEF_OUT_PORT,IO_EXT_OUT_DATA_PIN,IO_EXT_OUT_CLK_PIN,(uint8_t)data);        // low byte, is last to that fist chip is zero !
	digitalWrite(IO_DEF_OUT_PORT,IO_EXT_OUT_E_PIN,ONE);
#elif defined(SF_ENABLE_EXT_OUT)
	digitalWrite(IO_DEF_OUT_PORT,IO_EXT_OUT_E_PIN,ZERO);
		shiftOut(IO_DEF_OUT_PORT,IO_EXT_OUT_DATA_PIN,IO_EXT_OUT_CLK_PIN,(uint8_t)data);
	digitalWrite(IO_DEF_OUT_PORT,IO_EXT_OUT_E_PIN,ONE);
#elif defined(SF_ENABLE_EXT_LCD)
	digitalWrite(IO_DEF_OUT_PORT,IO_DEF_OUT_0_PIN,(data & 1) >> 0); // only set 3 bits on output, other 3 are for lcd
	digitalWrite(IO_DEF_OUT_PORT,IO_DEF_OUT_1_PIN,(data & 2) >> 1);
	digitalWrite(IO_DEF_OUT_PORT,IO_DEF_OUT_2_PIN,(data & 4) >> 2);
#else
	volatile uint8_t *port = IO_DEF_OUT_PORT;
	*port = data;
#endif
}

void Chip_out_serial(uint8_t data) {
	while ( !(UCSR0A & (1<<UDRE0)));
	UDR0 = data;
}

#ifdef SF_ENABLE_EXT_LCD
void lcd_write_s2p(uint8_t value) {
	digitalWrite(IO_DEF_OUT_PORT,IO_EXT_S2P_E_PIN,ZERO);
#ifdef SF_ENABLE_EXT_LCD_DOC
	uint16_t doc_out = ZERO;

#ifdef SF_ENABLE_DOC
	for (uint8_t t=ZERO;t<DOC_PORT_NUM_MAX;t++) {
		if (pf_data.doc_port[t] > ZERO) {
			doc_out += (ONE << t);
		}
	}
#endif

#ifdef SF_ENABLE_EXT_LCD_DOC_16BIT
	shiftOut(IO_DEF_OUT_PORT,IO_EXT_S2P_DATA_PIN,IO_EXT_S2P_CLK_PIN,(doc_out >> 8)); // send data to last chip first
	shiftOut(IO_DEF_OUT_PORT,IO_EXT_S2P_DATA_PIN,IO_EXT_S2P_CLK_PIN,doc_out);
	shiftOut(IO_DEF_OUT_PORT,IO_EXT_S2P_DATA_PIN,IO_EXT_S2P_CLK_PIN,value);
#else
	shiftOut(IO_DEF_OUT_PORT,IO_EXT_S2P_DATA_PIN,IO_EXT_S2P_CLK_PIN,doc_out);
	shiftOut(IO_DEF_OUT_PORT,IO_EXT_S2P_DATA_PIN,IO_EXT_S2P_CLK_PIN,value);
#endif

#else
	shiftOut(IO_DEF_OUT_PORT,IO_EXT_S2P_DATA_PIN,IO_EXT_S2P_CLK_PIN,value);
#endif
	digitalWrite(IO_DEF_OUT_PORT,IO_EXT_S2P_E_PIN,ONE);
}
#endif

void Chip_out_lcd(uint8_t data,uint8_t cmd,uint8_t mux) {
	uint8_t hn = data >> 4;
	uint8_t ln = data & 0x0F;

	//Send high nibble
#ifdef SF_ENABLE_EXT_LCD
	uint8_t lcd_out = hn + ((mux & 3) << 6);
	if (cmd==LCD_SEND_DATA) {
		lcd_out += 16; // make RS high
	}
	lcd_out += 32; // make E high
	lcd_write_s2p(lcd_out);
	lcd_out -= 32; // Make E low
	lcd_write_s2p(lcd_out);
	if (cmd!=LCD_SEND_INIT) {
		asm volatile ("nop");
		asm volatile ("nop");
		uint8_t lcd_out = ln + ((mux & 3) << 6);
		if (cmd==LCD_SEND_DATA) {
			lcd_out += 16; // make RS high
		}
		lcd_out += 32; // make E high
		lcd_write_s2p(lcd_out);
		lcd_out -= 32; // Make E low
		lcd_write_s2p(lcd_out);
	}
#else
	if (cmd==LCD_SEND_DATA) {
		digitalWrite(IO_DEF_IO_PORT,IO_DEF_LCD_RS_PIN,ONE); // write data
	} else {
		digitalWrite(IO_DEF_IO_PORT,IO_DEF_LCD_RS_PIN,ZERO);  // write command
	}
	volatile uint8_t *port = IO_DEF_ADC_PORT;
	*port=(*port & 0xF0)|hn;
	digitalWrite(IO_DEF_IO_PORT,IO_DEF_LCD_E_PIN,ONE);
	asm volatile ("nop");
	asm volatile ("nop");
	digitalWrite(IO_DEF_IO_PORT,IO_DEF_LCD_E_PIN,ZERO);  //Now data lines are stable pull E low for transmission
	if (cmd!=LCD_SEND_INIT) {
		asm volatile ("nop");
		asm volatile ("nop");
		volatile uint8_t *port = IO_DEF_ADC_PORT;
		*port=(*port & 0xF0)|ln;
		digitalWrite(IO_DEF_IO_PORT,IO_DEF_LCD_E_PIN,ONE);
		asm volatile ("nop");
		asm volatile ("nop");
		digitalWrite(IO_DEF_IO_PORT,IO_DEF_LCD_E_PIN,ZERO);
	}
#endif

	if (cmd==LCD_SEND_DATA) {
		Chip_delayU(30);
	} else {
		Chip_delay(5); // wait for busy flag
	}
}

void Chip_out_doc(uint16_t data) {
#ifdef SF_ENABLE_DOC
	if (pf_conf.avr_pin2_map == PIN2_DOC2_OUT) {
		digitalWrite(IO_DEF_IO_PORT,IO_DEF_PIN2_PIN,pf_data.doc_port[2] > ZERO);
	}
	if (pf_conf.avr_pin3_map == PIN3_DOC3_OUT) {
		digitalWrite(IO_DEF_IO_PORT,IO_DEF_PIN3_PIN,pf_data.doc_port[3] > ZERO);
	}
	if (pf_conf.avr_pin4_map == PIN4_DOC4_OUT) {
		digitalWrite(IO_DEF_IO_PORT,IO_DEF_PIN4_PIN,pf_data.doc_port[4] > ZERO);
	}
	if (pf_conf.avr_pin5_map == PIN5_DOC5_OUT) {
		digitalWrite(IO_DEF_IO_PORT,IO_DEF_PIN5_PIN,pf_data.doc_port[5] > ZERO);
	}
	if (pf_conf.avr_pin2_map == PIN2_DOC8_OUT) {
		digitalWrite(IO_DEF_IO_PORT,IO_DEF_PIN2_PIN,pf_data.doc_port[8] > ZERO);
	}
	if (pf_conf.avr_pin3_map == PIN3_DOC9_OUT) {
		digitalWrite(IO_DEF_IO_PORT,IO_DEF_PIN3_PIN,pf_data.doc_port[9] > ZERO);
	}
	if (pf_conf.avr_pin4_map == PIN4_DOC10_OUT) {
		digitalWrite(IO_DEF_IO_PORT,IO_DEF_PIN4_PIN,pf_data.doc_port[10] > ZERO);
	}
	if (pf_conf.avr_pin5_map == PIN5_DOC11_OUT) {
		digitalWrite(IO_DEF_IO_PORT,IO_DEF_PIN5_PIN,pf_data.doc_port[11] > ZERO);
	}
#endif
}

void Chip_in_int_pin(uint8_t pin,uint8_t enable) {
	if (pin==ZERO) {
		if (enable==ZERO) {
			EIMSK |=  (ONE << INT0);   // Enable INT0 External Interrupt
		} else {
			EIMSK &= ~(ONE << INT0);
		}
	} else {
		if (enable==ZERO) {
			EIMSK |=  (ONE << INT1);   // Enable INT1 External Interrupt
		} else {
			EIMSK &= ~(ONE << INT1);
		}
	}
}

void Chip_in_adc(uint8_t channel) {
	ADMUX = (1 << REFS0 ) | (channel & 0x07);
	ADCSRA |= (1<<ADSC); // request start
}

uint8_t Chip_in_menu(void) {
	if (pf_conf.avr_pin3_map != PIN3_MENU0_IN || pf_conf.avr_pin4_map != PIN4_MENU1_IN) {
		return 0xFF;// todo use adc or dic for menu pins.
	}
	uint8_t input0 = digitalRead(IO_DEF_IO_PORT_IN,IO_DEF_PIN3_PIN);
	uint8_t input1 = digitalRead(IO_DEF_IO_PORT_IN,IO_DEF_PIN4_PIN);
	uint8_t result = input0 + (input1 << 1);
	return result;
}

uint16_t Chip_in_dic(void) {
#ifdef SF_ENABLE_DIC
	uint16_t result=ZERO;

	// Special readout of pin2 into dic2 for 1or3 digital inputs without extension
	if (pf_conf.avr_pin2_map == PIN2_DIC2_IN) {
		result += digitalRead(IO_DEF_IO_PORT_IN,IO_DEF_PIN2_PIN) << 2;
	}
	if (pf_conf.avr_pin3_map == PIN3_DIC3_IN) {
		result += digitalRead(IO_DEF_IO_PORT_IN,IO_DEF_PIN3_PIN) << 3;
	}
	if (pf_conf.avr_pin4_map == PIN4_DIC4_IN) {
		result += digitalRead(IO_DEF_IO_PORT_IN,IO_DEF_PIN4_PIN) << 4;
	}
	if (pf_conf.avr_pin5_map == PIN5_DIC5_IN) {
		result += digitalRead(IO_DEF_IO_PORT_IN,IO_DEF_PIN5_PIN) << 5;
	}
	if (pf_conf.avr_pin2_map == PIN2_DIC8_IN) {
		result += digitalRead(IO_DEF_IO_PORT_IN,IO_DEF_PIN2_PIN) << 8;
	}
	if (pf_conf.avr_pin3_map == PIN3_DIC9_IN) {
		result += digitalRead(IO_DEF_IO_PORT_IN,IO_DEF_PIN3_PIN) << 9;
	}
	if (pf_conf.avr_pin4_map == PIN4_DIC10_IN) {
		result += digitalRead(IO_DEF_IO_PORT_IN,IO_DEF_PIN4_PIN) << 10;
	}
	if (pf_conf.avr_pin5_map == PIN5_DIC11_IN) {
		result += digitalRead(IO_DEF_IO_PORT_IN,IO_DEF_PIN5_PIN) << 11;
	}

#ifdef SF_ENABLE_EXT_LCD_DIC
	for (uint8_t i=ZERO;i < DIC_MAP_MAX/2  ;i++) {
		Chip_out_lcd(0x80,LCD_SEND_CMD,i/2);
		if (i==2  && pf_conf.avr_pin2_map == PIN2_DIC2_IN)  { continue; }
		if (i==3  && pf_conf.avr_pin3_map == PIN3_DIC3_IN)  { continue; }
		if (i==4  && pf_conf.avr_pin4_map == PIN4_DIC4_IN)  { continue; }
		if (i==5  && pf_conf.avr_pin5_map == PIN5_DIC5_IN)  { continue; }
		if (((result >> i) & ONE)==ZERO) {
			if ((i & ONE)==ZERO) {
				result += digitalRead(IO_DEF_IO_PORT_IN,IO_EXT_INPUT0_PIN) << i;
			} else {
				result += digitalRead(IO_DEF_IO_PORT_IN,IO_EXT_INPUT1_PIN) << i;
			}
		}
	}
#else
	result += digitalRead(IO_DEF_IO_PORT_IN,IO_EXT_INPUT0_PIN) << ZERO;
	result += digitalRead(IO_DEF_IO_PORT_IN,IO_EXT_INPUT1_PIN) << ONE;
#endif
	return result;
#endif
}

// Pin2 input via interrupts
ISR(INT0_vect) {
	if (pf_conf.avr_pin2_map == PIN2_TRIG_IN) {
#ifdef SF_ENABLE_PWM
		PWM_pulsefire();
#endif
		return;
	}
#ifdef SF_ENABLE_DEV
	if (pf_conf.avr_pin2_map == PIN2_FREQ_IN) {
		pf_data.dev_freq_cnt++;
		return;
	}
#endif
#ifdef SF_ENABLE_PWM
	if (pf_conf.avr_pin2_map == PIN2_FIRE_IN && pf_data.pulse_fire == ZERO) {
		Vars_setValueInt(Vars_getIndexFromPtr((CHIP_PTR_TYPE*)&pf_data.pulse_fire),ZERO,ZERO,ONE);
		return;
	}
	if (pf_conf.avr_pin2_map == PIN2_HOLD_FIRE_IN && pf_data.pulse_hold_fire == ZERO) {
		Vars_setValueInt(Vars_getIndexFromPtr((CHIP_PTR_TYPE*)&pf_data.pulse_hold_fire),ZERO,ZERO,ONE);
		return;
	}
#endif
}

ISR(INT1_vect) {
#ifdef SF_ENABLE_DEV
	if (pf_conf.avr_pin3_map == PIN3_FREQ_IN) {
		pf_data.dev_freq_cnt++;
		return;
	}
#endif
#ifdef SF_ENABLE_PWM
	if (pf_conf.avr_pin3_map == PIN3_FIRE_IN && pf_data.pulse_fire == ZERO) {
		Vars_setValueInt(Vars_getIndexFromPtr((CHIP_PTR_TYPE*)&pf_data.pulse_fire),ZERO,ZERO,ONE);
		return;
	}
	if (pf_conf.avr_pin3_map == PIN3_HOLD_FIRE_IN && pf_data.pulse_hold_fire == ZERO) {
		Vars_setValueInt(Vars_getIndexFromPtr((CHIP_PTR_TYPE*)&pf_data.pulse_hold_fire),ZERO,ZERO,ONE);
		return;
	}
#endif
}

ISR(TIMER0_OVF_vect) {
	pf_prog.sys_time_ticks++;
	if (pf_prog.sys_time_ticks >= (F_CPU/8/256/100)) {
		pf_prog.sys_time_ticks=ZERO;
		pf_prog.sys_time_ssec++;
	}
}

ISR(ADC_vect) {
#ifdef SF_ENABLE_PWM
	Input_adc_int(ADCW);
#endif
}

ISR(TIMER1_COMPB_vect) {
#ifdef SF_ENABLE_PWM
	PWM_do_work_b();
#endif
}

ISR(TIMER1_COMPA_vect) {
#ifdef SF_ENABLE_PWM
	PWM_do_work_a();
#endif
}

ISR(USART_RX_vect) {
	Serial_rx_int(UDR0);
}

ISR(TIMER2_COMPA_vect) {
#ifdef SF_ENABLE_CIT
#endif
}

ISR(TIMER2_COMPB_vect) {
#ifdef SF_ENABLE_CIT
#endif
}

ISR(TIMER2_OVF_vect) {
#ifdef SF_ENABLE_CIT
#endif
}



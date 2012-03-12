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


// PIN MAPPING FOR MEGA CONNECTION MODE
#define IO_MEGA_SERIAL_PORT      &PORTE
#define IO_MEGA_RX_PIN               0  // PIN 0 = Rx/Tx for serial is wired internally to USB
#define IO_MEGA_TX_PIN               1  // PIN 1

#define IO_MEGA_PIN_TRIG_PORT    &PORTD
#define IO_MEGA_PIN19_PIN            2  // PIN 19 = INT2  (def) off
#define IO_MEGA_PIN18_PIN            3  // PIN 18 = INT3  (def) off
#define IO_MEGA_PIN_CLK_PORT     &PORTL
#define IO_MEGA_PIN49_PIN            0  // PIN 49 = (def) enter menu
#define IO_MEGA_PIN48_PIN            1  // PIN 48 = (def) enter menu
#define IO_MEGA_PIN47_PIN            2  // PIN 47 = T5 (def) External clock source for pwm

#define IO_MEGA_DIC_PORT         &PORTL
#define IO_MEGA_DIC_PORT_IN       &PINL
#define IO_MEGA_DIC_0_PIN            4  // PIN 45
#define IO_MEGA_DIC_1_PIN            5  // PIN 44
#define IO_MEGA_DIC_2_PIN            6  // PIN 43
#define IO_MEGA_DIC_3_PIN            7  // PIN 42

#define IO_MEGA_LCD_DATA_PORT    &PORTC // PIN37-36-35-34 = D0-D3 (if no lcd then DIC8-15) (if glcd then 8bit data)
#define IO_MEGA_LCD_CNTR_PORT    &PORTC //
#define IO_MEGA_LCD_RS_PIN           6  // PIN 31
#define IO_MEGA_LCD_E_PIN            7  // PIN 30

#define IO_MEGA_DOC_PORT         &PORTB
#define IO_MEGA_DOC_0_PIN            0  // PIN 53
#define IO_MEGA_DOC_1_PIN            1  // PIN 52
#define IO_MEGA_DOC_2_PIN            2  // PIN 51
#define IO_MEGA_DOC_3_PIN            3  // PIN 50

#define IO_MEGA_OUT_PORT         &PORTA
#define IO_MEGA_OUT_0_PIN            0  // PIN 22
#define IO_MEGA_OUT_1_PIN            1  // PIN 23
#define IO_MEGA_OUT_2_PIN            2  // PIN 24
#define IO_MEGA_OUT_3_PIN            3  // PIN 25
#define IO_MEGA_OUT_4_PIN            4  // PIN 26
#define IO_MEGA_OUT_5_PIN            5  // PIN 27
#define IO_MEGA_OUT_6_PIN            6  // PIN 28
#define IO_MEGA_OUT_7_PIN            7  // PIN 29
#define IO_MEGA_EXT_OUT_DATA_PIN     0  // PIN 22 = output 0-7 and 8-15 via 2 chip casade
#define IO_MEGA_EXT_OUT_CLK_PIN      1  // PIN 23
#define IO_MEGA_EXT_OUT_E_PIN        2  // PIN 24
#define IO_MEGA_EXT_S2P_DATA_PIN     5  // PIN 27 = lcd D0-D3,RS,E, doc8, doc16
#define IO_MEGA_EXT_S2P_CLK_PIN      6  // PIN 28
#define IO_MEGA_EXT_S2P_E_PIN        7  // PIN 29

#define IO_MEGA_ADCL_PORT        &PORTF // ANALOG 0-7
#define IO_MEGA_ADCH_PORT        &PORTK // ANALOG 8-15 (if GLCD then limited)

#define IO_MEGA_GLCD_PORT        &PORTK
#define IO_MEGA_GLCD_E_PIN           7  // ANALOG-15
#define IO_MEGA_GLCD_RS_PIN          6  // ANALOG-14
#define IO_MEGA_GLCD_CS0_PIN         5  // ANALOG-13
#define IO_MEGA_GLCD_CS1_PIN         4  // ANALOG-12



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

	// Port 2 has only 2 pins connected used for interupt functions
	DDRD = 0x00;  // default to input
	PORTD = 0xFF; // with pullup
	switch (pf_conf.avr_pin18_map) {
		case PIN18_DOC4_OUT:
		case PIN18_DOC6_OUT:
			DDRD  |=  (ONE<<IO_MEGA_PIN18_PIN);
			PORTD &= ~(ONE<<IO_MEGA_PIN18_PIN);
		default:
			break;
	}
	switch (pf_conf.avr_pin19_map) {
		case PIN19_DOC5_OUT:
		case PIN19_DOC7_OUT:
			DDRD  |=  (ONE<<IO_MEGA_PIN19_PIN);
			PORTD &= ~(ONE<<IO_MEGA_PIN19_PIN);
		default:
			break;
	}
	// Conf dic and special ports
	DDRL = 0x00;
	PORTL = 0xFF;
/*
	switch (pf_conf.avr_pin47_map) {
		case PIN47_RELAY_OUT:
			DDRL  |=  (ONE<<IO_MEGA_PIN47_PIN);
			PORTL &= ~(ONE<<IO_MEGA_PIN47_PIN);
		default:
			break;
	}
*/
	switch (pf_conf.avr_pin48_map) {
		case PIN48_DOC4_OUT:
		case PIN48_DOC6_OUT:
			DDRL  |=  (ONE<<IO_MEGA_PIN48_PIN);
			PORTL &= ~(ONE<<IO_MEGA_PIN48_PIN);
		default:
			break;
	}
	switch (pf_conf.avr_pin49_map) {
		case PIN49_DOC5_OUT:
		case PIN49_DOC7_OUT:
			DDRL  |=  (ONE<<IO_MEGA_PIN49_PIN);
			PORTL &= ~(ONE<<IO_MEGA_PIN49_PIN);
		default:
			break;
	}

#ifndef SF_ENABLE_LCD
	DDRC = ZERO; // all input for dic
#elif SF_ENABLE_EXT_LCD
	DDRC = ZERO; // all input for dic
#else
	DDRC = 0xFF; // all output for lcd
	PORTC = ZERO;
#endif

	// pwm output
	DDRA  = 0xFF; // Port A is in all connection modes always output
#ifdef SF_ENABLE_PWM
	PWM_send_output(PULSE_DATA_OFF); // send off state to output
#endif

	// Timer5 16bit timer used for pulse steps.
	ICR5 = 0xFFFF;OCR5A = 0xFFFF;OCR5B = 0xFFFF;
	TCCR5A = ZERO;
	TCCR5B = (ONE+ONE) & 7;
	TIMSK5|= (ONE << OCF5A);
	TIMSK5|= (ONE << OCF5B);
	TCNT5  = ZERO;
#ifdef SF_ENABLE_PWM
	TCCR5B = pf_conf.pwm_clock & 7;
#endif

	// Conf doc
	DDRB = 0xFF;  // all output
	PORTB = ZERO;

	// Enable external interrupts on startup
	if (pf_conf.avr_pin18_map == PIN18_TRIG_IN || pf_conf.avr_pin18_map == PIN18_FREQ_IN || pf_conf.avr_pin18_map == PIN18_FIRE_IN) {
		EICRA |= (1 << ISC31);  // Falling-Edge Triggered INT3
		EIMSK |= (1 << INT3);   // Enable INT3 External Interrupt
	}
	if (pf_conf.avr_pin19_map == PIN19_TRIG_IN || pf_conf.avr_pin19_map == PIN19_FREQ_IN || pf_conf.avr_pin19_map == PIN19_FIRE_IN) {
		EICRA |= (1 << ISC21);  // Falling-Edge Triggered INT2
		EIMSK |= (1 << INT2);   // Enable INT2 External Interrupt
	}

	// Timer0 is used for timemanagement
	TCCR0A |= (1 << WGM00) | (1 << WGM01); // Fast pwm
	TCCR0B |= (1 << CS00) | (1 << CS01);   // prescaler /64
	TIMSK0 |= (1 << TOIE0); // enable int op overflow
	TCNT0 = ZERO;

	// enable adc
	ADCSRA |= (1 << ADEN) | (1 << ADPS2) /* | (1 << ADPS1) */ | (1 << ADPS0); // div 32 if 1 then 128
	DIDR0 |= (1 << ADC4D) | (1 << ADC5D) | (1 << ADC6D) | (1 << ADC7D); // disable digital input on adc pins
	DIDR0 |= (1 << ADC0D) | (1 << ADC1D) | (1 << ADC2D) | (1 << ADC3D);
	DIDR1 |= (1 << ADC8D) | (1 << ADC9D) | (1 << ADC10D) | (1 << ADC11D);
#ifndef SF_ENABLE_GLCD
	DIDR1 |= (1 << ADC12D) | (1 << ADC13D) | (1 << ADC14D) | (1 << ADC15D);
#endif
#ifdef SF_ENABLE_GLCD
	DDRK = 0x0F; // set glcd control lines to output
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
	DDRE &= ~_BV(PINE0);
	PORTE |= _BV(PINE0);

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
	return pmChipCPUTypeAvrMega;
}

uint8_t digitalRead(volatile uint8_t *port,uint8_t pin) {
	uint8_t value = *port;
	return (value >> pin) & 0x01;
}

void digitalWrite(volatile uint8_t *port,uint8_t pin,uint8_t value) {
	if (value>ZERO) {
		*port |= (1<<pin);
	} else {
		*port &= ~(1<<pin);
	}
}

void shiftOut(volatile uint8_t *port,uint8_t dataPin,uint8_t clkPin,uint8_t dataByte) {
	for( uint8_t i = 8;i>ZERO; i-- ){
		digitalWrite(port,dataPin,(dataByte & 0x80)>ZERO);
		dataByte <<= 1;
		digitalWrite(port,clkPin, ONE);
		digitalWrite(port,clkPin, ZERO);
	}
}

void Chip_eeprom_read(void* eemem) {
	eeprom_read_block((void*)&pf_conf,eemem,sizeof(pf_conf_struct));
}

void Chip_eeprom_write(void* eemem) {
	eeprom_write_block((const void*)&pf_conf,eemem,sizeof(pf_conf_struct));
}

uint8_t Chip_pgm_readByte(const char* p) {
	return pgm_read_byte(p);
}

CHIP_PTR_TYPE Chip_pgm_readWord(const CHIP_PTR_TYPE* p) {
	return pgm_read_word(p);
}

void Chip_pwm_timer(uint8_t reg,uint16_t value) {
	switch (reg) {
	case PWM_REG_CLOCK:
		TCCR5B = value & 7;
		break;
	case PWM_REG_OCRA:
		OCR5A = value;
		break;
	case PWM_REG_OCRB:
		OCR5B = value;
		break;
	case PWM_REG_TCNT:
		TCNT5 = value;
		break;
	default:
		break;
	}
}

void Chip_out_pwm(uint16_t data) {
	// Send data to output depending on connection mode; max outs 16,8,3,6
#if defined(SF_ENABLE_EXT_OUT_16BIT)
	digitalWrite(IO_MEGA_OUT_PORT,IO_MEGA_EXT_OUT_E_PIN,ZERO);
		shiftOut(IO_MEGA_OUT_PORT,IO_MEGA_EXT_OUT_DATA_PIN,IO_MEGA_EXT_OUT_CLK_PIN,(uint8_t)(data >> 8)); // high byte
		shiftOut(IO_MEGA_OUT_PORT,IO_MEGA_EXT_OUT_DATA_PIN,IO_MEGA_EXT_OUT_CLK_PIN,(uint8_t)data);        // low byte, is last to that fist chip is zero !
	digitalWrite(IO_MEGA_OUT_PORT,IO_MEGA_EXT_OUT_E_PIN,ONE);
#elif defined(SF_ENABLE_EXT_OUT)
	digitalWrite(IO_MEGA_OUT_PORT,IO_MEGA_EXT_OUT_E_PIN,ZERO);
		shiftOut(IO_MEGA_OUT_PORT,IO_MEGA_EXT_OUT_DATA_PIN,IO_MEGA_EXT_OUT_CLK_PIN,(uint8_t)data);
	digitalWrite(IO_MEGA_OUT_PORT,IO_MEGA_EXT_OUT_E_PIN,ONE);
#elif defined(SF_ENABLE_EXT_LCD)
	digitalWrite(IO_MEGA_OUT_PORT,IO_MEGA_OUT_0_PIN,(data & 1) >> 0); // only set 5 bits on output, other 3 are for lcd
	digitalWrite(IO_MEGA_OUT_PORT,IO_MEGA_OUT_1_PIN,(data & 2) >> 1);
	digitalWrite(IO_MEGA_OUT_PORT,IO_MEGA_OUT_2_PIN,(data & 4) >> 2);
	digitalWrite(IO_MEGA_OUT_PORT,IO_MEGA_OUT_3_PIN,(data & 4) >> 3);
	digitalWrite(IO_MEGA_OUT_PORT,IO_MEGA_OUT_4_PIN,(data & 4) >> 4);
#else
	volatile uint8_t *port = IO_MEGA_OUT_PORT;
	*port = data;
#endif
}

void Chip_out_serial(uint8_t data) {
	while ( !(UCSR0A & (1<<UDRE0)));
	UDR0 = data;
}

#ifdef SF_ENABLE_EXT_LCD
void Chip_lcd_write_ext_s2p(uint8_t value) {
	digitalWrite(IO_MEGA_OUT_PORT,IO_MEGA_EXT_S2P_E_PIN,ZERO);
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
	shiftOut(IO_MEGA_OUT_PORT,IO_MEGA_EXT_S2P_DATA_PIN,IO_MEGA_EXT_S2P_CLK_PIN,(doc_out >> 8)); // send data to last chip first
	shiftOut(IO_MEGA_OUT_PORT,IO_MEGA_EXT_S2P_DATA_PIN,IO_MEGA_EXT_S2P_CLK_PIN,doc_out);
	shiftOut(IO_MEGA_OUT_PORT,IO_MEGA_EXT_S2P_DATA_PIN,IO_MEGA_EXT_S2P_CLK_PIN,value);
#else
	shiftOut(IO_MEGA_OUT_PORT,IO_MEGA_EXT_S2P_DATA_PIN,IO_MEGA_EXT_S2P_CLK_PIN,doc_out);
	shiftOut(IO_MEGA_OUT_PORT,IO_MEGA_EXT_S2P_DATA_PIN,IO_MEGA_EXT_S2P_CLK_PIN,value);
#endif

#else
	shiftOut(IO_MEGA_OUT_PORT,IO_MEGA_EXT_S2P_DATA_PIN,IO_MEGA_EXT_S2P_CLK_PIN,value);
#endif
	digitalWrite(IO_MEGA_OUT_PORT,IO_MEGA_EXT_S2P_E_PIN,ONE);
}
#endif

#ifdef SF_ENABLE_EXT_LCD
void Chip_lcd_write_ext(uint8_t data,uint8_t cmd,uint8_t mux) {
	uint8_t hn = data >> 4;
	uint8_t ln = data & 0x0F;
	uint8_t lcd_out = hn + ((mux & 3) << 6);
	if (cmd==LCD_SEND_DATA) {
		lcd_out += 16; // make RS high
	}
	lcd_out += 32; // make E high
	Chip_lcd_write_ext_s2p(lcd_out);
	lcd_out -= 32; // Make E low
	Chip_lcd_write_ext_s2p(lcd_out);
	if (cmd!=LCD_SEND_INIT) {
		asm volatile ("nop");
		asm volatile ("nop");
		uint8_t lcd_out = ln + ((mux & 3) << 6);
		if (cmd==LCD_SEND_DATA) {
			lcd_out += 16; // make RS high
		}
		lcd_out += 32; // make E high
		Chip_lcd_write_ext_s2p(lcd_out);
		lcd_out -= 32; // Make E low
		Chip_lcd_write_ext_s2p(lcd_out);
	}
}
#endif

#ifdef SF_ENABLE_LCD
#ifndef SF_ENABLE_EXT_LCD
void Chip_lcd_write_pins(uint8_t data,uint8_t cmd,uint8_t mux) {
	uint8_t hn = data >> 4;
	uint8_t ln = data & 0x0F;
	if (cmd==LCD_SEND_DATA) {
		digitalWrite(IO_MEGA_LCD_CNTR_PORT,IO_MEGA_LCD_RS_PIN,ONE); // write data
	} else {
		digitalWrite(IO_MEGA_LCD_CNTR_PORT,IO_MEGA_LCD_RS_PIN,ZERO);  // write command
	}
	volatile uint8_t *port = IO_MEGA_LCD_DATA_PORT;
	*port=(*port & 0xF0)|hn;
	//*port=(IO_DEF_ADC_PORT & 0xF0)|hn;
	digitalWrite(IO_MEGA_LCD_CNTR_PORT,IO_MEGA_LCD_E_PIN,ONE);
	asm volatile ("nop");
	asm volatile ("nop");
	digitalWrite(IO_MEGA_LCD_CNTR_PORT,IO_MEGA_LCD_E_PIN,ZERO);  //Now data lines are stable pull E low for transmission
	if (cmd!=LCD_SEND_INIT) {
		asm volatile ("nop");
		asm volatile ("nop");
		volatile uint8_t *port = IO_MEGA_LCD_DATA_PORT;
		*port=(*port & 0xF0)|ln;
		digitalWrite(IO_MEGA_LCD_CNTR_PORT,IO_MEGA_LCD_E_PIN,ONE);
		asm volatile ("nop");
		asm volatile ("nop");
		digitalWrite(IO_MEGA_LCD_CNTR_PORT,IO_MEGA_LCD_E_PIN,ZERO);
	}
}
#endif
#endif

#ifdef SF_ENABLE_GLCD
void Chip_lcd_write_glcd(uint8_t data,uint8_t cmd,uint8_t mux) {
	digitalWrite(IO_MEGA_GLCD_PORT,IO_MEGA_GLCD_E_PIN,ZERO);
	if ((cmd & 0x0F)==LCD_SEND_DATA) {
		digitalWrite(IO_MEGA_GLCD_PORT,IO_MEGA_GLCD_RS_PIN,ONE);
	} else {
		digitalWrite(IO_MEGA_GLCD_PORT,IO_MEGA_GLCD_RS_PIN,ZERO);
	}
	digitalWrite(IO_MEGA_GLCD_PORT,IO_MEGA_GLCD_CS0_PIN,(cmd >> 7) & ONE);
	digitalWrite(IO_MEGA_GLCD_PORT,IO_MEGA_GLCD_CS1_PIN,(cmd >> 6) & ONE);
	Chip_delayU(30);
	digitalWrite(IO_MEGA_GLCD_PORT,IO_MEGA_GLCD_E_PIN,ONE);
	volatile uint8_t *port = IO_MEGA_LCD_DATA_PORT;
	*port=data;
	Chip_delayU(10);
	digitalWrite(IO_MEGA_GLCD_PORT,IO_MEGA_GLCD_E_PIN,ZERO);
}
#endif

void Chip_out_lcd(uint8_t data,uint8_t cmd,uint8_t mux) {

#ifdef SF_ENABLE_GLCD
	Chip_lcd_write_glcd(data,cmd,mux);
#elif SF_ENABLE_EXT_LCD
	Chip_lcd_write_ext(data,cmd,mux);
#elif SF_ENABLE_LCD
	Chip_lcd_write_pins(data,cmd,mux);
#endif
	if ((cmd & 0x0F)==LCD_SEND_DATA) {
		Chip_delayU(30);
	} else {
		Chip_delay(5); // wait for busy flag
	}
}

void Chip_out_doc(uint16_t data) {
	PORTB = data & 0x0F; // only write 4 bits

#ifdef SF_ENABLE_DOC
	if (pf_conf.avr_pin18_map == PIN18_DOC4_OUT) {
		digitalWrite(IO_MEGA_PIN_TRIG_PORT,IO_MEGA_PIN18_PIN,pf_data.doc_port[4] > ZERO);
	}
	if (pf_conf.avr_pin18_map == PIN18_DOC6_OUT) {
		digitalWrite(IO_MEGA_PIN_TRIG_PORT,IO_MEGA_PIN18_PIN,pf_data.doc_port[6] > ZERO);
	}
	if (pf_conf.avr_pin19_map == PIN19_DOC5_OUT) {
		digitalWrite(IO_MEGA_PIN_TRIG_PORT,IO_MEGA_PIN19_PIN,pf_data.doc_port[5] > ZERO);
	}
	if (pf_conf.avr_pin19_map == PIN19_DOC7_OUT) {
		digitalWrite(IO_MEGA_PIN_TRIG_PORT,IO_MEGA_PIN19_PIN,pf_data.doc_port[7] > ZERO);
	}
	if (pf_conf.avr_pin48_map == PIN48_DOC4_OUT) {
		digitalWrite(IO_MEGA_PIN_CLK_PORT,IO_MEGA_PIN48_PIN,pf_data.doc_port[4] > ZERO);
	}
	if (pf_conf.avr_pin48_map == PIN48_DOC6_OUT) {
		digitalWrite(IO_MEGA_PIN_CLK_PORT,IO_MEGA_PIN48_PIN,pf_data.doc_port[6] > ZERO);
	}
	if (pf_conf.avr_pin49_map == PIN49_DOC5_OUT) {
		digitalWrite(IO_MEGA_PIN_CLK_PORT,IO_MEGA_PIN49_PIN,pf_data.doc_port[5] > ZERO);
	}
	if (pf_conf.avr_pin49_map == PIN49_DOC7_OUT) {
		digitalWrite(IO_MEGA_PIN_CLK_PORT,IO_MEGA_PIN49_PIN,pf_data.doc_port[7] > ZERO);
	}
#endif
}

void Chip_in_int_pin(uint8_t pin,uint8_t enable) {
	if (pin==ZERO) {
		if (enable==ZERO) {
			EIMSK |= (1 << INT2);   // Enable INT2 External Interrupt
		} else {
			EIMSK &= ~(1 << INT2);
		}
	} else {
		if (enable==ZERO) {
			EIMSK |= (1 << INT3);   // Enable INT3 External Interrupt
		} else {
			EIMSK &= ~(1 << INT3);
		}
	}
}

void Chip_in_adc(uint8_t channel) {
	ADMUX = (1 << REFS0 ) | (channel & 0x07);
	ADCSRB = (ADCSRB & ~(1 << MUX5)) | (((channel >> 3) & 0x01) << MUX5);
	ADCSRA |= (1<<ADSC); // start
}

uint8_t Chip_in_menu(void) {
	if (pf_conf.avr_pin48_map != PIN48_MENU0_IN && pf_conf.avr_pin49_map != PIN49_MENU1_IN) {
		return 0xFF;// todo use dic for menu pins.
	}
	uint8_t input0 = digitalRead(IO_MEGA_PIN_CLK_PORT,IO_MEGA_PIN48_PIN);
	uint8_t input1 = digitalRead(IO_MEGA_PIN_CLK_PORT,IO_MEGA_PIN49_PIN);
	uint8_t result = input0 + (input1 << 1);
	return result;
}

uint16_t Chip_in_dic(void) {
	uint16_t result = PINL >> 4; // read upper nibble for 4 bit DIC

	// Read high dic only if no lcd or when lcd is extended.
#ifndef SF_ENABLE_LCD
	result += PINC << 8;
#elif SF_ENABLE_EXT_LCD
	result += PINC << 8;
#endif
#ifdef SF_ENABLE_DIC
	if (pf_conf.avr_pin18_map == PIN18_DIC4_IN) {
		result += digitalRead(IO_MEGA_PIN_TRIG_PORT,IO_MEGA_PIN18_PIN) << 4;
	}
	if (pf_conf.avr_pin18_map == PIN18_DIC6_IN) {
		result += digitalRead(IO_MEGA_PIN_TRIG_PORT,IO_MEGA_PIN18_PIN) << 6;
	}
	if (pf_conf.avr_pin19_map == PIN19_DIC5_IN) {
		result += digitalRead(IO_MEGA_PIN_TRIG_PORT,IO_MEGA_PIN19_PIN) << 5;
	}
	if (pf_conf.avr_pin19_map == PIN19_DIC7_IN) {
		result += digitalRead(IO_MEGA_PIN_TRIG_PORT,IO_MEGA_PIN19_PIN) << 7;
	}
	if (pf_conf.avr_pin48_map == PIN48_DIC4_IN) {
		result += digitalRead(IO_MEGA_PIN_TRIG_PORT,IO_MEGA_PIN48_PIN) << 4;
	}
	if (pf_conf.avr_pin48_map == PIN48_DIC6_IN) {
		result += digitalRead(IO_MEGA_PIN_TRIG_PORT,IO_MEGA_PIN48_PIN) << 6;
	}
	if (pf_conf.avr_pin49_map == PIN49_DIC5_IN) {
		result += digitalRead(IO_MEGA_PIN_TRIG_PORT,IO_MEGA_PIN49_PIN) << 5;
	}
	if (pf_conf.avr_pin49_map == PIN49_DIC7_IN) {
		result += digitalRead(IO_MEGA_PIN_TRIG_PORT,IO_MEGA_PIN49_PIN) << 7;
	}
#endif
	return result;
}

// Pin19 input via interrupts
ISR(INT2_vect) {
	if (pf_conf.avr_pin19_map == PIN19_TRIG_IN) {
#ifdef SF_ENABLE_PWM
		if (pf_conf.pulse_trig == PULSE_TRIG_EXT) {
			pf_data.pwm_state = PWM_STATE_RUN; // Trigger pulse train on external interrupt pin if pulse_trigger
		} else if (pf_conf.pulse_trig == PULSE_TRIG_EXT_FIRE) {
			pf_data.pwm_state = PWM_STATE_RUN;
		}
#endif
		return;
	}
	if (pf_conf.avr_pin19_map == PIN19_FREQ_IN) {
		pf_data.dev_freq_cnt++;
		return;
	}
	if (pf_conf.avr_pin19_map == PIN19_FIRE_IN) {
		Vars_setValue(Vars_getIndexFromName(UNPSTR(pmDataPulseFire)),ZERO,ZERO,ONE);
		return;
	}
}

ISR(INT3_vect) {
	if (pf_conf.avr_pin18_map == PIN18_TRIG_IN) {
#ifdef SF_ENABLE_PWM
		if (pf_conf.pulse_trig == PULSE_TRIG_EXT) {
			pf_data.pwm_state = PWM_STATE_RUN; // Trigger pulse train on external interrupt pin if pulse_trigger
		} else if (pf_conf.pulse_trig == PULSE_TRIG_EXT_FIRE) { 
			pf_data.pwm_state = PWM_STATE_RUN;
		}
#endif
		return;
	}
	if (pf_conf.avr_pin18_map == PIN18_FREQ_IN) {
		pf_data.dev_freq_cnt++;
		return;
	}
	if (pf_conf.avr_pin18_map == PIN18_FIRE_IN) {
		Vars_setValue(Vars_getIndexFromName(UNPSTR(pmDataPulseFire)),ZERO,ZERO,ONE);
		return;
	}
}

ISR(TIMER0_OVF_vect) {
	pf_prog.sys_time_ticks++;
	if (pf_prog.sys_time_ticks>(F_CPU/64/256/100)) {
		pf_prog.sys_time_ticks=0;
		pf_prog.sys_time_ssec++;
	}
}

ISR(ADC_vect) {
#ifdef SF_ENABLE_PWM
	Input_adc_int(ADCW);
#endif
}

ISR(TIMER5_COMPB_vect) {
	#ifdef SF_ENABLE_PWM
		PWM_do_work_b();
	#endif
}

ISR(TIMER5_COMPA_vect) {
	#ifdef SF_ENABLE_PWM
		PWM_do_work_a();
	#endif
}

ISR(USART0_RX_vect) {
	Serial_rx_int(UDR0);
}


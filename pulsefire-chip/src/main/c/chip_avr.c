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

/*
Arduino mappings table;

- DEFAULT
- LCD
- SPI
- SPI+LCD

=========================================================================
Pin#	I/O		DEFAULT		LCD			SPI			SPI+LCD		PulseFire
=========================================================================
- 0     IN      USB-RX      <--         <--         <--
- 1     OUT     USB-TX      <--         <--         <--
- 2     IN      PIN2        <--         <--         <--
- 3     IN      PIN3        <--         <--         <--
- 4     I/O     PIN4        <--         <--         <--
- 5     I/O     PIN5        <--         <--         <--
- 6     I/O     DIC0        LCD_RS      DIC0        <--
- 7     I/0     DIC1        LCD_E       DIC1        <--
- 8     OUT     OUT0        <--         SPI_LCD_E   <--
- 9     OUT     OUT1        <--         SPI_DOC_E   <--
- 10    OUT     OUT2        <--         SPI_OUT_E   <--
- 11    OUT     OUT3        <--         SPI_MOSI    <--
- 12    OUT     OUT4        <--         SPI_MISO    <--
- 13    OUT     OUT5        <--         SPI_SCK     <--

- A0    I/O     ADC0        LCD_D0      ADC0        <--
- A1    I/O     ADC1        LCD_D1      ADC1        <--
- A2    I/0     ADC2        LCD_D2      ADC2        <--
- A3    I/0     ADC3        LCD_D3      ADC3        <--
- A4    IN      ADC4        <--         <--         <--
- A5    IN      ADC5        <--         <--         <--


*/



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

// PIN MAPPING FOR EXTENDED SPI CONNECTION MODE
#define IO_SPI_RX_PIN               0  // PIN 0 = Rx/Tx for serial is wired internally to USB
#define IO_SPI_TX_PIN               1  // PIN 1
#define IO_SPI_PIN2_PIN             2  // PIN 2 - in/out int0 Trigger
#define IO_SPI_PIN3_PIN             3  // PIN 3 - in/out intB enter menu
#define IO_SPI_PIN4_PIN             4  // PIN 4 - in/out 
#define IO_SPI_PIN5_PIN             5  // PIN 5 - in/out
#define IO_SPI_INPUT0_PIN           6  // PIN 6 - input dic0 - dic_mux0
#define IO_SPI_INPUT1_PIN           7  // PIN 7 - input dic1 - dic_mux1
#define IO_SPI_LCD_E_PIN            0  // PIN 8 - lcd D0-D3,RS,E,mux0/1=Select digital input via dual 4to1 multiplexer
#define IO_SPI_DOC_E_PIN            1  // PIN 9
#define IO_SPI_OUT_E_PIN            2  // PIN 10 = output 0-7 and 8-15 via 2 chip casade
#define IO_SPI_MOSI_PIN             3  // PIN 11
#define IO_SPI_MISO_PIN             4  // PIN 12
#define IO_SPI_SCK_PIN              5  // PIN 13
#define IO_SPI_ADC0_PIN             0  // PIN A0
#define IO_SPI_ADC1_PIN             1  // PIN A1
#define IO_SPI_ADC2_PIN             2  // PIN A2
#define IO_SPI_ADC3_PIN             3  // PIN A3
#define IO_SPI_ADC4_PIN             4  // PIN A4
#define IO_SPI_ADC5_PIN             5  // PIN A5


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
}

void Chip_reset(void) {
	wdt_enable(WDTO_15MS); // reboot in 15ms.
	Chip_delay(30);
}

void Chip_setup_serial(void) {
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
}

void Chip_setup(void) {

	// === Pin 0 and 1
	// Are done in Chip_setup_serial

	// === Pin 2 - 5

	DDRD = 0x00;  // default to input
	PORTD = 0xFF; // with pullup
	if (pf_conf.avr_pin2_map == PIN2_INT0_IN) {
		Chip_in_int_pin(ZERO,ZERO); // turn on int0
	}
	if (pf_conf.avr_pin3_map == PIN3_INT1_IN) {
		Chip_in_int_pin(ONE,ZERO); // turn on int1
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

	// === Pin

	DDRB  = 0xFF; // Port B is in all connection modes always output

#ifdef SF_ENABLE_SPI
	SPCR |=  (1 << SPE) | (1 << MSTR); // SPI enable and master mode in 0 SPI mode
	SPSR |=  (1 << SPI2X); // Double speed
	digitalWrite(IO_DEF_OUT_PORT,IO_SPI_OUT_E_PIN,ONE);
	digitalWrite(IO_DEF_OUT_PORT,IO_SPI_LCD_E_PIN,ONE);
	digitalWrite(IO_DEF_OUT_PORT,IO_SPI_DOC_E_PIN,ONE);
#endif

	// setup interrupts signals
	EICRA |= (1 << ISC01);  // Falling-Edge Triggered INT0
	EICRA |= (1 << ISC11);  // Falling-Edge Triggered INT1

	// === Init all timers

	// Timer0 is used for timemanagement
	TCCR0A |= (1 << WGM00) | (1 << WGM01); // Fast pwm
	TCCR0B |= (1 << CS01);                 // prescaler /8
	TIMSK0 |= (1 << TOIE0);                // int op overflow
	TCNT0 = ZERO;

	// Timer1 16bit timer used for pulse steps.
	ICR1 = 0xFFFF;OCR1A = 0xFFFF;OCR1B = 0xFFFF;
	TCCR1A = ZERO;
	TCCR1B = (ONE+ONE) & 7;
	TIMSK1|= (ONE << OCF1A);
	TCNT1  = ZERO;
#ifdef SF_ENABLE_PWM
	TCCR1B = pf_conf.pwm_clock & 7;
#endif

	// Timer2 8bit timer is free
	//OCR2A  = 0xFF;OCR2B  = 0xFF;
	//TCCR2A = ZERO;TCCR2B = ZERO;
	//TIMSK2|= (ONE << TOIE2);
	//TIMSK2|= (ONE << OCF2A);
	//TIMSK2|= (ONE << OCF2B);


	// === Analog Inputs

	// enable adc
	DDRC = 0x00;
	PORTC = 0x00;
	ADCSRA |= (1 << ADEN) | (1 << ADPS2) /*| (1 << ADPS1)*/ | (1 << ADPS0); // enable and div32
	ADCSRA |= (1<<ADIE); // enable interupts after conversion
	DIDR0 |= (1 << ADC4D) | (1 << ADC5D); // disable digital input on adc pins
#ifdef SF_ENABLE_LCD
#ifndef SF_ENABLE_SPI
	DIDR0 |= (1 << ADC0D) | (1 << ADC1D) | (1 << ADC2D) | (1 << ADC3D);

	DDRD  |=  (ONE<<IO_DEF_LCD_RS_PIN); // Map LCD pins
	PORTD &= ~(ONE<<IO_DEF_LCD_RS_PIN);
	DDRD  |=  (ONE<<IO_DEF_LCD_E_PIN);
	PORTD &= ~(ONE<<IO_DEF_LCD_E_PIN);
	DDRC  |=  (ONE<<PINC0);
	DDRC  |=  (ONE<<PINC1);
	DDRC  |=  (ONE<<PINC2);
	DDRC  |=  (ONE<<PINC3);
	PORTC  =  0x0F;
#endif
#endif

	// === Finish

	wdt_enable(WDT_MAIN_TIMEOUT); // enable watchdog timer, so if main loop to slow then reboot
}

uint32_t Chip_centi_secs(void) {
	return pf_data.sys_time_csec;
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

uint8_t shiftOut(uint8_t dataByte) {
	SPDR = dataByte;
	while (!(SPSR & _BV(SPIF)))
		;
	return SPDR; // clear bit
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
#ifdef SF_ENABLE_SPI
	case CHIP_REG_SPI_CLOCK:	SPCR = (SPCR & (0xFF-3))   + (value & 3);	break;
#endif
	default:
		break;
	}
}

void Chip_out_pwm(uint16_t data) {

	// Send data to output depending on connection mode; max outs 16,8,3,6
#if defined(SF_ENABLE_SPI)
	uint8_t chips = pf_conf.spi_chips;
	if ((chips & SPI_CHIPS_OUT8) > ZERO) {
		digitalWrite(IO_DEF_OUT_PORT,IO_SPI_OUT_E_PIN,ZERO);	
		if ((chips & SPI_CHIPS_OUT16) > ZERO) {
			shiftOut((uint8_t)(data >> 8)); // high byte
			shiftOut((uint8_t)data);        // low byte, is last to that fist chip is zero !
		} else {
			shiftOut((uint8_t)data);
		}
		digitalWrite(IO_DEF_OUT_PORT,IO_SPI_OUT_E_PIN,ONE);
	}
#else
	volatile uint8_t *port = IO_DEF_OUT_PORT;
	*port = data;
#endif
}

void Chip_out_serial(uint8_t data) {
	while ( !(UCSR0A & (1<<UDRE0)));
	UDR0 = data;
}

#ifdef SF_ENABLE_SPI
void lcd_write_s2p(uint8_t value) {
	// wait till free
	while (pf_data.spi_int_req>ZERO) {}
	pf_data.spi_int_pin = IO_SPI_LCD_E_PIN;
	pf_data.spi_int_data8 = value;
	pf_data.spi_int_req = 1;
}
#endif

void Chip_out_lcd(uint8_t data,uint8_t cmd,uint8_t mux) {
#ifdef SF_ENABLE_LCD
	uint8_t hn = data >> 4;
	uint8_t ln = data & 0x0F;

	//Send high nibble
#ifdef SF_ENABLE_SPI
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
		Chip_delay(pf_conf.lcd_hcd+ONE); // Lcd Hardware Command Delay (for busy flag)
	}
#endif
}

void Chip_out_doc(void) {

#ifdef SF_ENABLE_SPI
	uint16_t doc_out = ZERO;
	for (uint8_t t=ZERO;t<DOC_PORT_NUM_MAX;t++) {
		if (pf_data.doc_port[t] > ZERO) {
			doc_out += (ONE << t);
		}
	}
	// wait till free
	while (pf_data.spi_int_req>ZERO) {}
	uint8_t chips = pf_conf.spi_chips;
	if ((chips & SPI_CHIPS_DOC8) > ZERO) {
		pf_data.spi_int_pin = IO_SPI_DOC_E_PIN;
		if ((chips & SPI_CHIPS_DOC16) > ZERO) {
			pf_data.spi_int_data8 = (doc_out >> 8); // send data to last chip first
			pf_data.spi_int_data16 = doc_out;
			pf_data.spi_int_req = 2;
		} else {
			pf_data.spi_int_data8 = doc_out;
			pf_data.spi_int_req = 1;
		}
	}
#endif
	if (pf_conf.avr_pin4_map == PIN4_DOC4_OUT) {
		digitalWrite(IO_DEF_IO_PORT,IO_DEF_PIN4_PIN,pf_data.doc_port[4] > ZERO);
	}
	if (pf_conf.avr_pin5_map == PIN5_DOC5_OUT) {
		digitalWrite(IO_DEF_IO_PORT,IO_DEF_PIN5_PIN,pf_data.doc_port[5] > ZERO);
	}
	if (pf_conf.avr_pin4_map == PIN4_DOC10_OUT) {
		digitalWrite(IO_DEF_IO_PORT,IO_DEF_PIN4_PIN,pf_data.doc_port[10] > ZERO);
	}
	if (pf_conf.avr_pin5_map == PIN5_DOC11_OUT) {
		digitalWrite(IO_DEF_IO_PORT,IO_DEF_PIN5_PIN,pf_data.doc_port[11] > ZERO);
	}
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

uint16_t Chip_in_dic(void) {
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

#ifdef SF_ENABLE_SPI
	if (pf_conf.dic_mux > ZERO) {
		for (uint8_t i=ZERO;i < DIC_MAP_MAX/2  ;i++) {
			Chip_out_lcd(0x80,LCD_SEND_CMD,i/2);
			if (i==2  && pf_conf.avr_pin2_map == PIN2_DIC2_IN)  { continue; }
			if (i==3  && pf_conf.avr_pin3_map == PIN3_DIC3_IN)  { continue; }
			if (i==4  && pf_conf.avr_pin4_map == PIN4_DIC4_IN)  { continue; }
			if (i==5  && pf_conf.avr_pin5_map == PIN5_DIC5_IN)  { continue; }
			if (((result >> i) & ONE)==ZERO) {
				if ((i & ONE)==ZERO) {
					result += digitalRead(IO_DEF_IO_PORT_IN,IO_SPI_INPUT0_PIN) << i;
				} else {
					result += digitalRead(IO_DEF_IO_PORT_IN,IO_SPI_INPUT1_PIN) << i;
				}
			}
		}
	} else {
		result += digitalRead(IO_DEF_IO_PORT_IN,IO_SPI_INPUT0_PIN) << ZERO;
		result += digitalRead(IO_DEF_IO_PORT_IN,IO_SPI_INPUT1_PIN) << ONE;
	}
#else
	result += digitalRead(IO_DEF_IO_PORT_IN,IO_SPI_INPUT0_PIN) << ZERO;
	result += digitalRead(IO_DEF_IO_PORT_IN,IO_SPI_INPUT1_PIN) << ONE;
#endif
	return result;
}

// Pin2 input via interrupts
ISR(INT0_vect) {
	Sys_do_int(ZERO);
}

ISR(INT1_vect) {
	Sys_do_int(ONE);
}

ISR(TIMER0_OVF_vect) {
	Sys_time_int();
#ifdef SF_ENABLE_SPI
	if (pf_data.spi_int_req>ZERO) {
		digitalWrite(IO_DEF_OUT_PORT,pf_data.spi_int_pin,ZERO);
		if (pf_data.spi_int_req==2) {
			shiftOut(pf_data.spi_int_data16); // shift high first
		}
		shiftOut(pf_data.spi_int_data8);
		digitalWrite(IO_DEF_OUT_PORT,pf_data.spi_int_pin,ONE);
		pf_data.spi_int_req=ZERO;
	}
#endif
}

ISR(ADC_vect) {
#ifdef SF_ENABLE_ADC
	Adc_do_int(ADCW);
#endif
}

ISR(TIMER1_COMPA_vect) {
#ifdef SF_ENABLE_PWM
	PWM_work_int();
#endif
}

ISR(USART_RX_vect) {
	Serial_rx_int(UDR0);
}

ISR(TIMER2_COMPA_vect) {
}

ISR(TIMER2_COMPB_vect) {
}

ISR(TIMER2_OVF_vect) {
}



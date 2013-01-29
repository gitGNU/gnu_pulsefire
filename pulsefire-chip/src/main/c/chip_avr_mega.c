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

/*

Arduino mega mappings table;

- DEFAULT
- SPI
- LCD
- LCD + SPI

=========================================================================
Pin#	I/O		DEFAULT		SPI			LCD			LCD+SPI		PulseFire
=========================================================================
- 0     IN      USB-RX      <--         <--         <--
- 1     OUT     USB-TX      <--         <--         <--
- 2     OUT     OSC-3B      <--         <--         <--         CIP 1
- 3     OUT     OSC-3C      <--         <--         <--         CIP 1
- 4     n/a     free        free        free        free
- 5     OUT     OSC-3A      <--         <--         <--         CIP 1
- 6     OUT     OSC-4A      <--         <--         <--         CIP 2
- 7     OUT     OSC-4B      <--         <--         <--         CIP 2
- 8     OUT     OSC-4C      <--         <--         <--         CIP 2
- 9     n/a     free        <--         <--         <--
- 10    n/a     free        <--         <--         <--
- 11    OUT     OSC-1A      <--         <--         <--         CIP 0
- 12    OUT     OSC-1B      <--         <--         <--         CIP 0
- 13    OUT     OSC-1C      <--         <--         <--         CIP 0
- 14    n/a     free
- 15    n/a     free
- 16    n/a     free
- 17    n/a     free
- 18    IN      INT3        <--         <--         <--         INT 1
- 19    IN      INT2        <--         <--         <--         INT 0
- 20    I/O     SDA         <--         <--         <--         TODO
- 21    I/O     SCL         <--         <--         <--         TODO
- 22    OUT     PA0         <--         <--         <--         avr_port_a
- 23    OUT     PA1         <--         <--         <--
- 24    OUT     PA2         <--         <--         <--
- 25    OUT     PA3         <--         <--         <--
- 26    OUT     PA4         <--         <--         <--
- 27    OUT     PA5         <--         <--         <--
- 28    OUT     PA6         <--         <--         <--
- 29    OUT     PA7         <--         <--         <--
- 30    OUT     PC7         <--         LCD-D3      LCD-D3      avr_port_c
- 31    OUT     PC6         <--         LCD-D2      LCD-D2
- 32    OUT     PC5         <--         LCD-D1      LCD-D1
- 33    OUT     PC4         <--         LCD-D0      LCD-D0
- 34    OUT     PC3         <--         <--         <--
- 35    OUT     PC2         <--         <--         <--
- 36    OUT     PC1         <--         <--         <--
- 37    OUT     PC0         <--         <--         <--
- 38    OUT     free        free        LCD-RS      LCD-RS
- 39    OUT     free        free        LCD-E       LCD-E
- 40    OUT     DIC-MUX1    <--         <--         <--
- 41    OUT     DIC-MUX0    <--         <--         <--
- 42    IN      DIC-3       <--         <--         <--
- 43    IN      DIC-2       <--         <--         <--
- 44    IN      DIC-1       <--         <--         <--
- 45    IN      DIC-0       <--         <--         <--
- 46    OUT     free        SPI_DOC_E   free        SPI_DOC_E
- 47    IN      PWM_CLK     <--         <--         <--
- 48    n/a     free        <--         <--         <--
- 49    n/a     free        <--         <--         <--
- 50    OUT     free        MISO        free        MISO
- 51    IN?     free        MOSI        free        MOSI
- 52    OUT     free        SCK         free        SCK
- 53    OUT     free        SPI_OUT_E   free        SPI_OUT_E

- A0 - A7  = Analog inputs
- A8 - A15 = Analog inputs
*/

// PIN MAPPING FOR MEGA CONNECTION MODE
#define IO_MEGA_SERIAL_PORT      &PORTE
#define IO_MEGA_RX_PIN               0  // PIN 0 = Rx/Tx for serial is wired internally to USB
#define IO_MEGA_TX_PIN               1  // PIN 1

#define IO_MEGA_PORT_A           &PORTA // PIN 22-29
#define IO_MEGA_PORT_C           &PORTC // PIN 30-37

#define IO_MEGA_LCD_DATA_PORT    &PORTC // PIN  = D0-D3 (if no lcd then OUT/DOC 8-15)
#define IO_MEGA_LCD_RS_PORT      &PORTG //
#define IO_MEGA_LCD_RS_PIN           2  // PIN 38
#define IO_MEGA_LCD_E_PORT       &PORTD
#define IO_MEGA_LCD_E_PIN            7  // PIN 39

#define IO_MEGA_DIC_MUX_PORT     &PORTG
#define IO_MEGA_DIC_MUX_0_PIN        0  // PIN 41
#define IO_MEGA_DIC_MUX_1_PIN        1  // PIN 40
#define IO_MEGA_DIC_PORT         &PORTL
#define IO_MEGA_DIC_PORT_IN       &PINL
#define IO_MEGA_DIC_0_PIN            4  // PIN 45
#define IO_MEGA_DIC_1_PIN            5  // PIN 44
#define IO_MEGA_DIC_2_PIN            6  // PIN 43
#define IO_MEGA_DIC_3_PIN            7  // PIN 42

#define IO_MEGA_PWM_CLK_PIN          2  // PIN 47

#define IO_MEGA_SPI_DOC_E_PORT   &PORTL
#define IO_MEGA_SPI_DOC_E_PIN        3  // PIN 46

#define IO_MEGA_SPI_MISO_PIN         3  // PIN 50
#define IO_MEGA_SPI_MOSI_PIN         2  // PIN 51
#define IO_MEGA_SPI_SCK_PIN          1  // PIN 52
#define IO_MEGA_SPI_OUT_E_PORT   &PORTB
#define IO_MEGA_SPI_OUT_E_PIN        0  // PIN 53

#define IO_MEGA_ADCL_PORT        &PORTF // ANALOG 0-7
#define IO_MEGA_ADCH_PORT        &PORTK // ANALOG 8-15


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
	DDRE &= ~_BV(PINE0);
	PORTE |= _BV(PINE0);
}

void Chip_setup(void) {

	// === Pin 0 and 1
	// Done in Chip_setup_serial

	// === Pin 2 - 13 Init (All) timers

	// Timer0 is used for timemanagement
	TCCR0A |= (1 << WGM00) | (1 << WGM01); // Fast pwm
	TCCR0B |= (1 << CS01);  // prescaler /8
	TIMSK0 |= (1 << TOIE0); // enable int op overflow
	TCNT0 = ZERO;

	// Timer2 8bit timer is free
	//DDRH |= (1<<PINH6); // OC2B
	//DDRB |= (1<<PINB4); // OC2A
	//OCR2A  = 0xFF;OCR2B  = 0xFF;
	//TCCR2A = ZERO;TCCR2B = ZERO;
	//TIMSK2|= (ONE << TOIE2);
	//TIMSK2|= (ONE << OCF2A);
	//TIMSK2|= (ONE << OCF2B);

#ifdef SF_ENABLE_CIP
	DDRE |= (1<<PINE3)|(1<<PINE4)|(1<<PINE5); // OC3A,OC3B,OC3C - cip1
	DDRH |= (1<<PINH3)|(1<<PINH4)|(1<<PINH5); // OC4A,OC4B,OC4C - cip2
	DDRB |= (1<<PINB5)|(1<<PINB6)|(1<<PINB7); // OC1A,OC1B,OC1C - cip0

	Chip_reg_set(CHIP_REG_CIP0_CLOCK,	pf_conf.cip_0clock);
	Chip_reg_set(CHIP_REG_CIP0_MODE,	pf_conf.cip_0mode);
	Chip_reg_set(CHIP_REG_CIP0_OCR_A,	pf_conf.cip_0a_ocr);
	Chip_reg_set(CHIP_REG_CIP0_COM_A,	pf_conf.cip_0a_com);
	Chip_reg_set(CHIP_REG_CIP0_OCR_B,	pf_conf.cip_0b_ocr);
	Chip_reg_set(CHIP_REG_CIP0_COM_B,	pf_conf.cip_0b_com);
	Chip_reg_set(CHIP_REG_CIP0_OCR_C,	pf_conf.cip_0c_ocr);
	Chip_reg_set(CHIP_REG_CIP0_COM_C,	pf_conf.cip_0c_com);

	Chip_reg_set(CHIP_REG_CIP1_CLOCK,	pf_conf.cip_1clock);
	Chip_reg_set(CHIP_REG_CIP1_MODE,	pf_conf.cip_1mode);
	Chip_reg_set(CHIP_REG_CIP1_OCR_A,	pf_conf.cip_1a_ocr);
	Chip_reg_set(CHIP_REG_CIP1_COM_A,	pf_conf.cip_1a_com);
	Chip_reg_set(CHIP_REG_CIP1_OCR_B,	pf_conf.cip_1b_ocr);
	Chip_reg_set(CHIP_REG_CIP1_COM_B,	pf_conf.cip_1b_com);
	Chip_reg_set(CHIP_REG_CIP1_OCR_C,	pf_conf.cip_1c_ocr);
	Chip_reg_set(CHIP_REG_CIP1_COM_C,	pf_conf.cip_1c_com);

	Chip_reg_set(CHIP_REG_CIP2_CLOCK,	pf_conf.cip_2clock);
	Chip_reg_set(CHIP_REG_CIP2_MODE,	pf_conf.cip_2mode);
	Chip_reg_set(CHIP_REG_CIP2_OCR_A,	pf_conf.cip_2a_ocr);
	Chip_reg_set(CHIP_REG_CIP2_COM_A,	pf_conf.cip_2a_com);
	Chip_reg_set(CHIP_REG_CIP2_OCR_B,	pf_conf.cip_2b_ocr);
	Chip_reg_set(CHIP_REG_CIP2_COM_B,	pf_conf.cip_2b_com);
	Chip_reg_set(CHIP_REG_CIP2_OCR_C,	pf_conf.cip_2c_ocr);
	Chip_reg_set(CHIP_REG_CIP2_COM_C,	pf_conf.cip_2c_com);
#endif

#ifdef SF_ENABLE_PWM
	// Timer5 16bit timer used for pulse steps.
	ICR5 = 0xFFFF;OCR5A = 0xFFFF;OCR5B = 0xFFFF;
	TCCR5A = ZERO;
	TCCR5B = (ONE+ONE) & 7;
	TIMSK5|= (ONE << OCF5A);
	TCNT5  = ZERO;
	TCCR5B = pf_conf.pwm_clock & 7;
#endif

	// === Pins 18 & 19

	// setup interrupts signals
	EICRA |= (1 << ISC21);  // Falling-Edge Triggered INT2
	EICRA |= (1 << ISC31);  // Falling-Edge Triggered INT3

	Chip_in_int_pin(ZERO,ZERO);
	Chip_in_int_pin(ONE,ZERO);

	// === Pins 22 - 37

	DDRA = 0xFF;  // all output
	PORTA = ZERO;

	DDRC = 0xFF;  // all output
	PORTC = ZERO;

	// === Pins 38 - 39

	// LCD control lines
	DDRG  |=  (ONE<<IO_MEGA_LCD_RS_PIN);
	PORTG &= ~(ONE<<IO_MEGA_LCD_RS_PIN);

	DDRD  |=  (ONE<<IO_MEGA_LCD_E_PIN);
	PORTD &= ~(ONE<<IO_MEGA_LCD_E_PIN);


	// === Pins 42 - 45

	DDRL = 0x0F - IO_MEGA_PWM_CLK_PIN; // Input pins

	// Conf doc
	DDRB = 0xFF;  // all output
	PORTB = ZERO;

#ifdef SF_ENABLE_SPI
	SPCR |=  (1 << SPE) | (1 << MSTR); // SPI enable and master mode in 0 SPI mode
	SPSR |=  (1 << SPI2X); // Double speed
	digitalWrite(IO_MEGA_SPI_OUT_E_PORT,IO_MEGA_SPI_OUT_E_PIN,ONE);
	digitalWrite(IO_MEGA_SPI_DOC_E_PORT,IO_MEGA_SPI_DOC_E_PIN,ONE);
#endif


	// === Analog Inputs

	// enable adc
	DDRF = 0x00;
	PORTF = 0x00;
	DDRK = 0x00;
	PORTK = 0x00;
	// div128 = 16000000/128/13/16 = 600 requests per sec = 111
	// div32  = 16000000/32/13/16  = 2403 requests per sec = 101
	// with div32: one channel ~505 hz, with 16 drops to 980 hz total.
	ADCSRA |= (1 << ADEN) | (1 << ADPS2) /* | (1 << ADPS1)*/ | (1 << ADPS0);
	ADCSRA |= (1<<ADIE); // enable interupts after conversion

	DIDR0 |= (1 << ADC4D) | (1 << ADC5D) | (1 << ADC6D) | (1 << ADC7D); // disable digital input on adc pins
	DIDR0 |= (1 << ADC0D) | (1 << ADC1D) | (1 << ADC2D) | (1 << ADC3D);
	DIDR1 |= (1 << ADC8D) | (1 << ADC9D) | (1 << ADC10D) | (1 << ADC11D);
	DIDR1 |= (1 << ADC12D) | (1 << ADC13D) | (1 << ADC14D) | (1 << ADC15D);

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
	case CHIP_REG_PWM_CLOCK:	TCCR5B = value & 7;		break;
	case CHIP_REG_PWM_OCR_A:	OCR5A = value;			break;
	case CHIP_REG_PWM_OCR_B:	OCR5B = value;			break;
	case CHIP_REG_PWM_TCNT:		TCNT5 = value;			break;
#endif
#ifdef SF_ENABLE_CIP
	case CHIP_REG_CIP0_CLOCK:	TCCR1B = (TCCR1B & (0xFF-7))   + (value & 7);			break;
	case CHIP_REG_CIP0_MODE:	TCCR1A = (TCCR1A & (0xFF-3))   + (value & 3);
								TCCR1B = (TCCR1B & (0xFF-24))  + ((value << 1) & 24);	break;
	case CHIP_REG_CIP0_OCR_A:	OCR1A = value;											break;
	case CHIP_REG_CIP0_COM_A:	TCCR1A = (TCCR1A & (0xFF-192)) + ((value & 3) << 6);	break;
	case CHIP_REG_CIP0_OCR_B:	OCR1B = value;											break;
	case CHIP_REG_CIP0_COM_B:	TCCR1A = (TCCR1A & (0xFF-48))  + ((value & 3) << 4);	break;
	case CHIP_REG_CIP0_OCR_C:	OCR1C = value;											break;
	case CHIP_REG_CIP0_COM_C:	TCCR1A = (TCCR1A & (0xFF-12))  + ((value & 3) << 2);	break;

	case CHIP_REG_CIP1_CLOCK:	TCCR3B = (TCCR3B & (0xFF-7))   + (value & 7);			break;
	case CHIP_REG_CIP1_MODE:	TCCR3A = (TCCR3A & (0xFF-3))   + (value & 3);
								TCCR3B = (TCCR3B & (0xFF-24))  + ((value << 1) & 24);	break;
	case CHIP_REG_CIP1_OCR_A:	OCR3A = value;											break;
	case CHIP_REG_CIP1_COM_A:	TCCR3A = (TCCR3A & (0xFF-192)) + ((value & 3) << 6);	break;
	case CHIP_REG_CIP1_OCR_B:	OCR3B = value;											break;
	case CHIP_REG_CIP1_COM_B:	TCCR3A = (TCCR3A & (0xFF-48))  + ((value & 3) << 4);	break;
	case CHIP_REG_CIP1_OCR_C:	OCR3C = value;											break;
	case CHIP_REG_CIP1_COM_C:	TCCR3A = (TCCR3A & (0xFF-12))  + ((value & 3) << 2);	break;

	case CHIP_REG_CIP2_CLOCK:	TCCR4B = (TCCR4B & (0xFF-7))   + (value & 7);			break;
	case CHIP_REG_CIP2_MODE:	TCCR4A = (TCCR4A & (0xFF-3))   + (value & 3);
								TCCR4B = (TCCR4B & (0xFF-24))  + ((value << 1) & 24);	break;
	case CHIP_REG_CIP2_OCR_A:	OCR4A = value;											break;
	case CHIP_REG_CIP2_COM_A:	TCCR4A = (TCCR4A & (0xFF-192)) + ((value & 3) << 6);	break;
	case CHIP_REG_CIP2_OCR_B:	OCR4B = value;											break;
	case CHIP_REG_CIP2_COM_B:	TCCR4A = (TCCR4A & (0xFF-48))  + ((value & 3) << 4);	break;
	case CHIP_REG_CIP2_OCR_C:	OCR4C = value;											break;
	case CHIP_REG_CIP2_COM_C:	TCCR4A = (TCCR4A & (0xFF-12))  + ((value & 3) << 2);	break;
#endif
#ifdef SF_ENABLE_SPI
	case CHIP_REG_SPI_CLOCK:	SPCR = (SPCR & (0xFF-3))   + (value & 3);				break;
#endif
	default:
		break;
	}
}

void Chip_out_pwm(uint16_t data) {
	// Send data to output depending on connection mode; max outs 16,8,3,6
#if defined(SF_ENABLE_SPI)
	digitalWrite(IO_MEGA_SPI_OUT_E_PORT,IO_MEGA_SPI_OUT_E_PIN,ZERO);
		if ((pf_conf.spi_chips & SPI_CHIPS_OUT8) > ZERO) {
			shiftOut((uint8_t)(data >> 8)); // high byte
		}
		if ((pf_conf.spi_chips & SPI_CHIPS_OUT16) > ZERO) {
			shiftOut((uint8_t)data);        // low byte, is last to that fist chip is zero !
		}
	digitalWrite(IO_MEGA_SPI_OUT_E_PORT,IO_MEGA_SPI_OUT_E_PIN,ONE);
#endif
	if (pf_conf.mega_port_a == PORTA_OUT8) {
		volatile uint8_t *port = IO_MEGA_PORT_A;
		*port = data;
	}
	if (pf_conf.mega_port_c == PORTC_OUT16) {
		volatile uint8_t *port = IO_MEGA_PORT_C;
#ifdef SF_ENABLE_LCD
		*port = (*port & 0xF0)|((data >> 8) & 0x0F);
#else
		*port = data >> 8;
#endif

	}
}

void Chip_out_serial(uint8_t data) {
	while ( !(UCSR0A & (1<<UDRE0)));
	UDR0 = data;
}

#ifdef SF_ENABLE_LCD
void Chip_lcd_write_pins(uint8_t data,uint8_t cmd,uint8_t mux) {
	uint8_t hn = data >> 4;
	uint8_t ln = data & 0x0F;
	if (cmd==LCD_SEND_DATA) {
		digitalWrite(IO_MEGA_LCD_RS_PORT,IO_MEGA_LCD_RS_PIN,ONE); // write data
	} else {
		digitalWrite(IO_MEGA_LCD_RS_PORT,IO_MEGA_LCD_RS_PIN,ZERO);  // write command
	}
	volatile uint8_t *port = IO_MEGA_LCD_DATA_PORT;
	*port=(*port & 0x0F)|(hn << 4);
	digitalWrite(IO_MEGA_LCD_E_PORT,IO_MEGA_LCD_E_PIN,ONE);
	asm volatile ("nop");
	asm volatile ("nop");
	digitalWrite(IO_MEGA_LCD_E_PORT,IO_MEGA_LCD_E_PIN,ZERO);  //Now data lines are stable pull E low for transmission
	if (cmd!=LCD_SEND_INIT) {
		asm volatile ("nop");
		asm volatile ("nop");
		volatile uint8_t *port = IO_MEGA_LCD_DATA_PORT;
		*port=(*port & 0x0F)|(ln << 4);
		digitalWrite(IO_MEGA_LCD_E_PORT,IO_MEGA_LCD_E_PIN,ONE);
		asm volatile ("nop");
		asm volatile ("nop");
		digitalWrite(IO_MEGA_LCD_E_PORT,IO_MEGA_LCD_E_PIN,ZERO);
	}
}
#endif


void Chip_out_lcd(uint8_t data,uint8_t cmd,uint8_t mux) {
#if SF_ENABLE_LCD
	Chip_lcd_write_pins(data,cmd,mux);
	if ((cmd & 0x0F)==LCD_SEND_DATA) {
		Chip_delayU(30);
	} else {
		Chip_delay(pf_conf.lcd_hcd+ONE); // Lcd Hardware Command Delay (for busy flag)
	}
#endif
}

void Chip_out_doc(void) {
	uint16_t doc_out = ZERO;
	for (uint8_t t=ZERO;t<DOC_PORT_NUM_MAX;t++) {
		if (pf_data.doc_port[t] > ZERO) {
			doc_out += (ONE << t);
		}
	}

#ifdef SF_ENABLE_SPI
	// wait till free
	while (pf_data.spi_int_req>ZERO) {}
	uint8_t chips = pf_conf.spi_chips;
	if ((chips & SPI_CHIPS_DOC8) > ZERO) {
		pf_data.spi_int_pin = IO_MEGA_SPI_DOC_E_PIN;
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

	if (pf_conf.mega_port_a == PORTA_DOC8) {
		volatile uint8_t *port = IO_MEGA_PORT_A;
		*port = doc_out;
	}
	if (pf_conf.mega_port_c == PORTC_DOC8) {
		volatile uint8_t *port = IO_MEGA_PORT_C;
#ifdef SF_ENABLE_LCD
		*port= (*port & 0xF0)|((doc_out) & 0x0F);
#else
		*port = doc_out;
#endif
	} else if (pf_conf.mega_port_c == PORTC_DOC16) {
		volatile uint8_t *port = IO_MEGA_PORT_C;
#ifdef SF_ENABLE_LCD
		*port= (*port & 0xF0)|((doc_out >> 8) & 0x0F);
#else
		*port = doc_out >> 8;
#endif
	}
}

void Chip_in_int_pin(uint8_t pin,uint8_t enable) {
	if (pin==ZERO) {
		if (enable==ZERO) {
			EIMSK |=  (ONE << INT2);   // Enable INT2 External Interrupt
		} else {
			EIMSK &= ~(ONE << INT2);
		}
	} else {
		if (enable==ZERO) {
			EIMSK |=  (ONE << INT3);   // Enable INT3 External Interrupt
		} else {
			EIMSK &= ~(ONE << INT3);
		}
	}
}

void Chip_in_adc(uint8_t channel) {
	ADMUX = (1 << REFS0 ) | (channel & 0x07);
	ADCSRB = (ADCSRB & ~(1 << MUX5)) | (((channel >> 3) & 0x01) << MUX5);
	ADCSRA |= (1<<ADSC); // start
}

uint16_t Chip_in_dic(void) {
	uint16_t result = ZERO;
	if (pf_conf.dic_mux > ZERO) {
		for (uint8_t i=ZERO;i < DIC_MAP_MAX/4  ;i++) {
			digitalWrite(IO_MEGA_DIC_MUX_PORT,IO_MEGA_DIC_MUX_0_PIN,i & 1);
			digitalWrite(IO_MEGA_DIC_MUX_PORT,IO_MEGA_DIC_MUX_1_PIN,(i & 2) > ZERO);
			result += (PINL >> 4) << (i*4);
		}
	} else {
		result = PINL >> 4; // read upper nibble for 4 bit DIC
	}
	return result;
}

// Pin19 input via interrupts
ISR(INT2_vect) {
	Sys_do_int(ZERO);
}

ISR(INT3_vect) {
	Sys_do_int(ONE);
}

ISR(TIMER0_OVF_vect) {
	Sys_time_int();
#ifdef SF_ENABLE_SPI
	if (pf_data.spi_int_req>ZERO) {
		digitalWrite(IO_MEGA_SPI_DOC_E_PORT,pf_data.spi_int_pin,ZERO);
		if (pf_data.spi_int_req==2) {
			shiftOut(pf_data.spi_int_data16); // shift high first
		}
		shiftOut(pf_data.spi_int_data8);
		digitalWrite(IO_MEGA_SPI_DOC_E_PORT,pf_data.spi_int_pin,ONE);
		pf_data.spi_int_req=ZERO;
	}
#endif
}

ISR(ADC_vect) {
#ifdef SF_ENABLE_ADC
	Adc_do_int(ADCW);
#endif
}

ISR(TIMER5_COMPA_vect) {
#ifdef SF_ENABLE_PWM
	PWM_work_int();
#endif
}

ISR(TIMER5_COMPB_vect) {
}

ISR(USART0_RX_vect) {
	Serial_rx_int(UDR0);
}

ISR(TIMER2_COMPA_vect) {
}

ISR(TIMER2_COMPB_vect) {
}

ISR(TIMER2_OVF_vect) {
}


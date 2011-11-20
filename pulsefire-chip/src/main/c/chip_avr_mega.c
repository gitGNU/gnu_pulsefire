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

  /*
  switch (pf_conf.avr_pin2_map) {
    case PIN2_RELAY_OUT:
    case PIN2_DOC2_OUT:
    case PIN2_DOC8_OUT:
      DDRD |= (OUTPUT<<IO_DEF_PIN2_PIN);
      PORTD &= ~(1 << PIND2);
    default:
      break;
  }
  switch (pf_conf.avr_pin3_map) {
    case PIN3_RELAY_OUT:
    case PIN3_DOC3_OUT:
    case PIN3_DOC9_OUT:
       DDRD |= (OUTPUT<<IO_DEF_PIN3_PIN);
       PORTD &= ~(1 << PIND3);
    default:
       break;
  }
  switch (pf_conf.avr_pin4_map) {
    case PIN4_RELAY_OUT:
    case PIN4_DOC4_OUT:
    case PIN4_DOC10_OUT:
        DDRD |= (OUTPUT<<IO_DEF_PIN4_PIN);
        PORTD &= ~(1 << PIND4);
    default:
       break;
  }
  switch (pf_conf.avr_pin5_map) {
    case PIN5_RELAY_OUT:
    case PIN5_DOC5_OUT:
    case PIN5_DOC11_OUT:
        DDRD |= (OUTPUT<<IO_DEF_PIN5_PIN);
        PORTD &= ~(1 << PIND5);
    default:
       break;
  }
  */

  DDRC = 0x00;
  PORTC = 0x00;
  // Map LCD pins  
  #ifndef SF_ENABLE_EXT_LCD
    DDRD |= (OUTPUT<<IO_DEF_LCD_RS_PIN);
    PORTD &= ~(1 << IO_DEF_LCD_RS_PIN);
    DDRD |= (OUTPUT<<IO_DEF_LCD_E_PIN);
    PORTD &= ~(1 << IO_DEF_LCD_E_PIN);
    DDRC |= (OUTPUT<<PINC0);
    DDRC |= (OUTPUT<<PINC1);
    DDRC |= (OUTPUT<<PINC2);
    DDRC |= (OUTPUT<<PINC3);
    PORTC = 0x0F;
  #endif

  DDRB  = 0xFF; // Port B is in all connection modes always output
  #ifdef SF_ENABLE_PWM
  int_send_output(PULSE_DATA_OFF); // send off state to output
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

  // Timer2 8bit timer used for freq/rpm calculation
  //OCR2A  = 0xFF;OCR2B  = 0xFF;
  //TCCR2A = ZERO;//TCCR2B = DEFAULT_STEP_CLOCK;
  //TIMSK2|= (ONE << TOIE2); //TCNT2  = ZERO;
  
  wdt_enable(WDT_MAIN_TIMEOUT); // enable watchdog timer, so if main loop to slow then reboot
  
  // Enable external interrupts on startup
  /*
  if (pf_conf.avr_pin2_map == PIN2_TRIG_IN || pf_conf.avr_pin2_map == PIN2_FREQ_IN || pf_conf.avr_pin2_map == PIN2_FIRE_IN) {
    EICRA |= (1 << ISC00);  // Falling-Edge Triggered INT0 for PIN2_TRIG_IN
    EIMSK |= (1 << INT0);   // Enable INT0 External Interrupt
  }
  if (pf_conf.avr_pin3_map == PIN3_FREQ_IN || pf_conf.avr_pin3_map == PIN3_FIRE_IN) {
    EICRA |= (1 << ISC01);  // Falling-Edge Triggered INT0 for PIN2_TRIG_IN
    EIMSK |= (1 << INT1);   // Enable INT1 External Interrupt
  }
  */

  // Timer0 is used for timemanagement
  TCCR0A |= (1 << WGM00) | (1 << WGM01); // Fast pwm
  TCCR0B |= (1 << CS00) | (1 << CS01);   // prescaler /64
  TIMSK0 |= (1 << TOIE0); // enable int op overflow
  TCNT0 = ZERO;

  // enable adc
  ADCSRA |= (1 << ADEN) | (1 << ADPS2) /* | (1 << ADPS1) */ | (1 << ADPS0); // div 32 if 1 then 128
  DIDR0 |= (1 << ADC4D) | (1 << ADC5D); // disable digital input on adc pins
  #ifdef SF_ENABLE_EXT_LCD
  DIDR0 |= (1 << ADC0D) | (1 << ADC1D) | (1 << ADC2D) | (1 << ADC3D);
  #endif

  // initialize UART0
  //UBRR0H = (((F_CPU/SERIAL_SPEED)/16)-1)>>8; 	// set baud rate
  //UBRR0L = (((F_CPU/SERIAL_SPEED)/16)-1);
  UBRR0H = ZERO;
  UBRR0L = 16;        // 115K with double rate enabled else 8.
  UCSR0A = (1<<U2X0); // use double so error rate is only 2.1%.

  UCSR0B = (1<<RXEN0)|(1<<TXEN0)|(1<<RXCIE0);  // enable Rx & Tx
  UCSR0C = (1<<UCSZ00) | (1<<UCSZ01);          // 8n1

  // Enable pull-up on D0/RX, to supress line noise
  DDRD &= ~_BV(PIND0);
  PORTD |= _BV(PIND0);
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

uint16_t analogRead(uint8_t channel) {
	#if defined(ADCSRB) && defined(MUX5)
		ADCSRB = (ADCSRB & ~(1 << MUX5)) | (((channel >> 3) & 0x01) << MUX5);
	#endif
	ADMUX = (1 << REFS0 ) | (channel & 0x0F);
	ADCSRA |= (1<<ADSC); // start
	while (bit_is_set(ADCSRA, ADSC)); // wait until finish
	return ADCW;
}


void shiftOut(volatile uint8_t *port,uint8_t dataPin,uint8_t clkPin,uint8_t dataByte) {
	for( uint8_t i = 8;i>ZERO; i-- ){
		digitalWrite(port,dataPin,(dataByte & 0x80)>ZERO);
		dataByte <<= 1;
		digitalWrite(port,clkPin, HIGH);
		digitalWrite(port,clkPin, LOW);
	}
}

uint8_t Chip_pgm_read(const char* p) {
	return pgm_read_byte(p);
}

void Chip_io_pwm(uint16_t data) {
  // Send data to output depending on connection mode; max outs 16,8,3,6
	#if defined(SF_ENABLE_EXT_OUT_16BIT)
	  digitalWrite(IO_MEGA_OUT_PORT,IO_MEGA_EXT_OUT_E_PIN,LOW);
		  shiftOut(IO_MEGA_OUT_PORT,IO_MEGA_EXT_OUT_DATA_PIN,IO_MEGA_EXT_OUT_CLK_PIN,(uint8_t)(data >> 8)); // high byte
		  shiftOut(IO_MEGA_OUT_PORT,IO_MEGA_EXT_OUT_DATA_PIN,IO_MEGA_EXT_OUT_CLK_PIN,(uint8_t)data);        // low byte, is last to that fist chip is zero !
	  digitalWrite(IO_MEGA_OUT_PORT,IO_MEGA_EXT_OUT_E_PIN,HIGH);
	#elif defined(SF_ENABLE_EXT_OUT)
	  digitalWrite(IO_MEGA_OUT_PORT,IO_MEGA_EXT_OUT_E_PIN,LOW);
		  shiftOut(IO_MEGA_OUT_PORT,IO_MEGA_EXT_OUT_DATA_PIN,IO_MEGA_EXT_OUT_CLK_PIN,(uint8_t)data);
	  digitalWrite(IO_MEGA_OUT_PORT,IO_MEGA_EXT_OUT_E_PIN,HIGH);
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

void Chip_io_serial(uint8_t data) {
	while ( !(UCSR0A & (1<<UDRE0)));
	UDR0 = data;
}

void Chip_io_lpm(uint8_t data) {
	/*
	  if (pf_conf.avr_pin2_map == PIN2_RELAY_OUT) {
	    digitalWrite(IO_DEF_OUT_PORT,IO_DEF_PIN2_PIN,pinLevel);
	  }
	  if (pf_conf.avr_pin3_map == PIN3_RELAY_OUT) {
	    digitalWrite(IO_DEF_OUT_PORT,IO_DEF_PIN3_PIN,pinLevel);
	  }
	  if (pf_conf.avr_pin4_map == PIN4_RELAY_OUT) {
	    digitalWrite(IO_DEF_OUT_PORT,IO_DEF_PIN4_PIN,pinLevel);
	  }
	  if (pf_conf.avr_pin5_map == PIN5_RELAY_OUT) {
	    digitalWrite(IO_DEF_OUT_PORT,IO_DEF_PIN4_PIN,pinLevel);
	  }
	  */
}

void Chip_io_int_pin(uint8_t pin,uint8_t enable) {
	if (pin==ZERO) {
		if (enable==ZERO) {
			EIMSK |= (1 << INT0);   // Enable INT0 External Interrupt
		} else {
			EIMSK &= ~(1 << INT0);
		}
	} else {
		if (enable==ZERO) {
			EIMSK |= (1 << INT1);   // Enable INT1 External Interrupt
		} else {
			EIMSK &= ~(1 << INT1);
		}
	}
}


// Prototype and function for specific c init location.
void wdt_init(void) __attribute__((naked)) __attribute__((section(".init3")));
void wdt_init(void) {
  MCUSR = ZERO;
  wdt_disable(); // Disable watchdog timer in early startup.
}


// Pin2 input via interrupts 
ISR(INT0_vect) {
	/*
  if (pf_conf.avr_pin2_map == PIN2_TRIG_IN) {
    #ifdef SF_ENABLE_PWM
    if (pf_conf.pulse_trig != PULSE_TRIG_EXT) {
      return;
    }
    pf_data.pwm_state = PWM_STATE_RUN; // Trigger pulse train on external interrupt pin if pulse_trigger
    #endif
    return;
  }
  if (pf_conf.avr_pin2_map == PIN2_FREQ_IN) {
    //pf_data.dev_freq_cnt++;
    return;
  }
  if (pf_conf.avr_pin2_map == PIN2_FIRE_IN) {
    Vars_setValue(Vars_getIndexFromName(UNPSTR(pmDataPulseFire)),ZERO,ZERO,ZERO);
    return;
  }
  */
}
ISR(INT1_vect) {
	/*
  if (pf_conf.avr_pin3_map == PIN3_FREQ_IN) {
    //pf_data.dev_freq_cnt++;
    return;
  }
  if (pf_conf.avr_pin3_map == PIN3_FIRE_IN) {
    Vars_setValue(Vars_getIndexFromName(UNPSTR(pmDataPulseFire)),ZERO,ZERO,ZERO);
    return;
  }
  */
}



ISR(TIMER0_OVF_vect) {
	pf_prog.sys_time_ticks++;
	if (pf_prog.sys_time_ticks>(F_CPU/64/256/100)) {
		pf_prog.sys_time_ticks=0;
		pf_prog.sys_time_ssec++;
	}
}

ISR(TIMER5_COMPB_vect) {
	#ifdef SF_ENABLE_PWM
		int_do_work_b();
	#endif
}


ISR(TIMER5_COMPA_vect) {
	#ifdef SF_ENABLE_PWM
		int_do_work_a();
	#endif
}


ISR(USART0_RX_vect) {
	while ( !(UCSR0A & (1<<RXC0)) );
	Serial_rx_int(UDR0);
}

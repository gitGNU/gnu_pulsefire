<!--
Copyright (c) 2011, Willem Cazander
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided
that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this list of conditions and the
  following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
  the following disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-->

# Examples

Listing of different example config to try out.
It is best todo "reset_conf" before running next example.


## Config control

To reset to defaults and save the config and view current config;

	root@pulsefire: reset_conf
	root@pulsefire: save
	root@pulsefire: info_conf


## Map analog inputs to freq/duty control

Maps analog input5 to req_freq_cmd with range .2hz to 80hz
And maps analog input4 to pwm_duty with range from 0 to 110 percent.
(Note that after startup touch both pots to set freq !)

	root@pulsefire: adc_map 5 req_pwm_freq 20 8000
	root@pulsefire: adc_map 4 pwm_duty 0 110

## Pulse Direction

	root@pulsefire: pulse_dir 0 ( LR = Default )
	root@pulsefire: pulse_dir 1 ( RL )
	root@pulsefire: pulse_dir 2 ( LRRL )

## Output control 

Set 6 outputs and invert the output state

	root@pulsefire: pulse_steps 6
	root@pulsefire: pulse_inv 1

## Mapping info

There are multipe variables which do mapping lists and they
accept the same mapping format called in code an QMAP. (4 values)
To make useful mapping it is needed to know some extra info
about value of the variable to map.
	
	List all max values;
	root@pulsefire: help max
	
	List all mappable variables;
	root@pulsefire: help map
	
	List all indexed variables;
	root@pulsefire: help idx
	The var listed as 'xxx_xxx_map==x 4' are qmaps and all work the 
	same like;
	root@pulsefire: adc_map 5 pwm_on_cnt 0 65535 2
	root@pulsefire: adc_map <MAP-NUM> <VAR-NAME|VAR-ID> <VALUE-A> <VALUE-B> <VAR-IDX>


## External clock

Hookup function generator/hall sensor to clock input PIN 5.
Select extenal clock 7(falling) or 8(rise) and then do not
use req_pwm_freq because that will reset clock to internal.
	
	root@pulsefire: pwm_clock 7  

## External trigger

Hookup button/hall sensor to PIN 2.
Select pin2 for trigger input and make pulse use ext trigger. 

	root@pulsefire: avr_pin2_map 1
	root@pulsefire: pulse_trig 2

## External freq/rpm

Hookup button/hall sensor to PIN 2.

	root@pulsefire: avr_pin2_map 3
	TODO: make this feedback to req_pwm_freq cmd.

## Dynamic pulsed freq

	root@pulsefire: pulse_steps 6
	root@pulsefire: pulse_mode 1
	root@pulsefire: pwm_off_cnt_a 1000
	root@pulsefire: pwm_on_cnt_a 10000 0
	root@pulsefire: pwm_on_cnt_a 20000 1
	root@pulsefire: pwm_on_cnt_a 30000 2
	root@pulsefire: pwm_on_cnt_a 40000 3
	root@pulsefire: pwm_on_cnt_a 50000 4
	root@pulsefire: pwm_on_cnt_a 60000 5


## Funny duel train

	root@pulsefire: pulse_steps 6
	root@pulsefire: pulse_mode 3
	root@pulsefire: pulse_init 9
	root@pulsefire: pulse_dir 2


## PPM night ride

	root@pulsefire: ppm_data_a 1 0
	root@pulsefire: ppm_data_a 514 1
	root@pulsefire: ppm_data_a 260 2
	root@pulsefire: ppm_data_a 136 3
	root@pulsefire: ppm_data_a 80 4
	root@pulsefire: ppm_data_a 32 5
	root@pulsefire: ppm_data_len 10
	root@pulsefire: pulse_mode 5
	root@pulsefire: pulse_dir 0
	root@pulsefire: pulse_steps 6
	root@pulsefire: info_ppm
	ppm_data_a00==0000000000000001
	ppm_data_a01==0000001000000010
	ppm_data_a02==0000000100000100
	ppm_data_a03==0000000010001000
	ppm_data_a04==0000000001010000
	ppm_data_a05==0000000000100000


## Dave Lawton output

4 Pulse 50% duty pulse and than 50% train duty rest on all outputs

	root@pulsefire: pulse_mode 5
	root@pulsefire: ppm_data_len 16
	root@pulsefire: ppm_data_a 85
	root@pulsefire: info_ppm
	ppm_data_a00==0000000001010101
	ppm_data_a01==0000000001010101
	ppm_data_a02==0000000001010101


## Duel loop run

PPMA data for 2 groups with dubbel speed;

	root@pulsefire: pulse_steps 6
	root@pulsefire: pulse_mode 5
	root@pulsefire: ppm_data_a 195 0
	root@pulsefire: ppm_data_a 780 1
	root@pulsefire: ppm_data_a 3120 2
	root@pulsefire: ppm_data_a 15 3 (=1+2+4+8)
	root@pulsefire: ppm_data_a 240 4 (=16+32+64+128)
	root@pulsefire: ppm_data_a 3840 5 (=256+512+1024+2048)
	root@pulsefire: ppm_data_len 12
	root@pulsefire: info_ppm
	ppm_data_a00==0000000011000011
	ppm_data_a01==0000001100001100
	ppm_data_a02==0000110000110000
	ppm_data_a03==0000000000001111
	ppm_data_a04==0000000011110000
	ppm_data_a05==0000111100000000

## Safety values

	root@pulsefire: pulse_mode 4
	root@pulsefire: pulse_steps 6
	root@pulsefire: adc_map 5 dev_volts 0 1023
	root@pulsefire: stv_warn_mode 1
	root@pulsefire: stv_warn_secs 10
	root@pulsefire: stv_error_mode 0
	root@pulsefire: stv_error_secs 30
	root@pulsefire: stv_max_map 0 dev_volts 200 400

## Timed output

Run for alway 2 steps , which switch of output on/off every 5 seconds.
(note: maybe time to wait will we changed to /10 of seconds)

	root@pulsefire: ptc_0map 0 pulse_enable 0 5
	root@pulsefire: ptc_0map 1 pulse_enable 1 5
	root@pulsefire: ptc_0run 255

## External trigger

Make pin 2 low for few seconds wait few seconds and do again.
note; this works better on the real DIC inputs.

	root@pulsefire: avr_pin2_map 3
	root@pulsefire: dic_map 02 ptt_fire 1 0 0
	root@pulsefire: ptt_0map 0 pulse_steps 1 10
	root@pulsefire: ptt_0map 1 pulse_steps 6 10
	root@pulsefire: ptt_0map 2 pulse_steps 6 10
	root@pulsefire: ptt_0map 3 pulse_steps 6 10

## Soft warmup

	TODO FIXME
	root@pulsefire: swc_delay 10  (wait 10 secords before going on)
	root@pulsefire: swc_mode 2    (use mode 2 in soft warmup instread of pulse_mode, 0=use pulse_mode)
	root@pulsefire: swc_secs 20   (after delay we use 20secs to do soft warmup)
	root@pulsefire: swc_duty 5    (some multiply factor for wait duty calc)
	root@pulsefire: save
	root@pulsefire: chip_reset



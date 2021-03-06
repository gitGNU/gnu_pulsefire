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

# Chip Commands

Current list of commands over PulseFire serial console.

![function-overview](img/pulsefire-overview.png "function-overview")


## Help

	help               - Shows the command listing
	help map           - Shows the variable mapping ids of the variables which can be mapped.
	                     The extra 1 indicates that the variable is a trigger variable.
	help max           - Show the maximum value of all variables.
	help idx           - Shows the maximum sized of (multi) indexed variables.
	help bits          - Shows the decimal version of the bitfield flags of the variables.

## Save

	save               - Save config value into eeprom

## Info

	info_conf [all]    - Shows all savable config information
	info_data          - Shows all runtime data information
	info_prog          - Shows all interal program information
	info_freq          - Shows all calculated information
	info_ppm           - Shows all bit patterns of PPM mode.
	info_chip          - Shows Chip and build info.

## Reset

	reset_conf         - Reset the config values to defaults. (does not auto save!)
	reset_data         - Reset the data variables to step0,etc.
	reset_chip         - Reset the devices and so resets the program.

## Request

	req_pulse_fire     - Requests software pulse trigger
	req_pwm_freq <hz*100> [output]
	                   - Request freqency of train pulse this will change pwm_compa,pwm_compb,pwm_clock,etc
	                     Examples;
	                         req_pwm_freq 500 - Sets all outputs to 50hz with pwm_duty.
	                         req_pwm_freq 5000 2 - Sets output 2 to 500hz with pwm_duty
	                       
	req_auto_lpm       - Requests automatic lpm messurement
	req_tx_push        - Enable config update changes to be pushed to serial automaticly.(1=On,0=off(def))
	req_tx_echo        - Echo serial data(1=On(def),0=off)
	req_tx_promt       - Echo promt after cmd (1=On(def),0=off)

## Program

	mal_program        - wip Program the mal byte per 4.

## Pulse

	pulse_enable       - Enable output (1=On,default)
	pulse_mode         - Pulse train mode, 0=off,1=flash,2=flash0,3=train(def),4=PPM,5=PPMA,6=PPMI
	pulse_steps        - Number of outputs
	pulse_trig         - Pulse train trigger, 0=Loop,1=Input1,2=Extern)
	pulse_dir          - Pulse direction (0=LR,default,1=RL,2=LRRL(nightrider))
	pulse_bank         - Bank A or B selection 
	pulse_inv          - Ouput data inververt
	pulse_trig_delay   - Pre delay for when using non loop pulse_trig!=0
	pulse_post_delay   - Delay for after pulse train(pulse-duty)
	pulse_mask_a       - 16bitwise output mask to disable outputs(0=off,1=on so default is 65535)
	pulse_mask_b       - 16bitwise output mask to disable outputs(0=off,1=on so default is 65535)
	pulse_init_a       - Start data for ouput pulse train shifting (used in pulse_mode=3 only)
	pulse_init_b       - Start data for ouput pulse train shifting (used in pulse_mode=3 only)

## PWM

	pwm_cnt_on_a       - Step timer value of train pulse counter (16bit)
	pwm_cnt_on_b       - Step timer value of train pulse counter (16bit)
	pwm_cnt_off_a      - Step timer value of train duty pulse.
	pwm_cnt_off_b      - Step timer value of train duty pulse.
	pwm_tune           - 8 bit, wait loop counter for per output step shifting of a few MS.
	pwm_loop           - 8 bit, loop counter of interrupts from clock of timer.
	pwm_loop_delta     - Auto increase speed of pwm_loop nice max is 1/6 of pwm_loop
	pwm_clock          - Clock prescaler for pwm timer, pwm_clock=7 is external clock source
	pwm_duty           - Pwm duty used only via req_pwm_freq !!

## PPM

	ppm_data_offset    - Seqence offset between next step max 15 
	ppm_data_len       - Seqence data length max 15 (ppm_* is pulse_mode=3,4,5 data only)
	ppm_data_a         - Custom seqence data 0-65535 per output
	ppm_data_b         - Custom seqence data 0-65535 per output

## PTC

	ptc_0run           - O=Off,1+ run times from startup, 255=loop
	ptc_1run           - O=Off,1+ run times from startup, 255=loop 
	ptc_0mul           - Time multiplier 1=0.1sec 10=1sec 100=10sec
	ptc_1mul           - Time multiplier 1=0.1sec 10=1sec 100=10sec
	ptc_0map           - Set variable to value after wait.
	ptc_1map           - Set variable to value after wait.

## PTT

	ptt_0map           - Trigger0 map with timed actions
	ptt_1map           - Trigger1 map with timed actions
	ptt_2map           - Trigger2 map with timed actions
	ptt_3map           - Trigger3 map with timed actions

## STV

	stv_warn_secs      - Minimal time in stv_warn_mode after trashhold passing.
	stv_warn_mode      - Change to this mode on warning
	stv_error_secs     - Minimal time in stv_error_mode after trashhold passing, 255=Stay forever
	stv_error_mode     - Change to this mode if error value.
	stv_max_map        - Max warning/error threshhold values for variable
	stv_min_map        - Min warning/error threshhold values for variable

## VFC

	vfc_input_map      - Virtual feedback input channels
	vfc_output_map     - Virtual feedback output channels

## SWC

	swc_delay          - Predelay in secords with outputs in off start
	swc_mode           - Use this pulse mode while soft warmup
	swc_secs           - Soft warmup in seconds
	swc_duty           - Soft warmup begin duty (must bigger then pulse_duty and is calc with pwm_compa0 !!)
	swc_trig           - Fire trigger after soft warmup..

## AVR

	avr_pin2_map       - Switch the mapping of pin2 (0=OFF,1=TRIG_IN,etc,etc)
	avr_pin3_map       - Switch the mapping of pin3
	avr_pin4_map       - Switch the mapping of pin4
	avr_pin5_map       - Switch the mapping of pin5, when switch from in/out then save and reset_chip before io changes.
	
	enum {PIN2_OFF,PIN2_TRIG_IN, PIN2_RELAY_OUT,PIN2_DIC2_IN,PIN2_DIC8_IN, PIN2_DOC2_OUT,PIN2_DOC8_OUT, PIN2_FREQ_IN,PIN2_FIRE_IN };
	enum {PIN3_OFF,PIN3_MENU0_IN,PIN3_RELAY_OUT,PIN3_DIC3_IN,PIN3_DIC9_IN, PIN3_DOC3_OUT,PIN3_DOC9_OUT, PIN3_FREQ_IN,PIN3_FIRE_IN };
	enum {PIN4_OFF,PIN4_MENU1_IN,PIN4_RELAY_OUT,PIN4_DIC4_IN,PIN4_DIC10_IN,PIN4_DOC4_OUT,PIN4_DOC10_OUT };
	enum {PIN5_OFF,PIN5_CLOCK_IN,PIN5_RELAY_OUT,PIN5_DIC5_IN,PIN5_DIC11_IN,PIN5_DOC5_OUT,PIN5_DOC11_OUT };

## AVR MEGA

	mega_port_a       - Switch the mapping of port A
	mega_port_b       - Switch the mapping of port B

## Inputs

	adc_map           - Maps analog inputs to config variables with range correction and indexed value
	adc_enable        - Bitfield for enabling analog inputs. (default is all OFF !!)
	adc_jitter        - Analog jitter removal
	dic_map           - Maps digital inputs to config variables with on and off value and indexed value

## Lcd

	lcd_size          - Supports 3 type of lcd 


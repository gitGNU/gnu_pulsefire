
# PulseFire versions

## Version 1.1.0
	- Made dev_volt/amp/temp variable indexed.
	- internal PF_VARS inverse nolimit flag.
	- Added lcd paging and lcd_defp for default page.
	- Added 4 button support with lcd_mode 3.
	- Added lcd_plp; programatic lcd page with 4 vars.
	- Added info_pwm to readout precalculated pwm data.
	- Changed pwm to precalculated data/time steps.
	- Dropped pulse_inv/pwm_loop_delta/pwm_tune_cnt options.
	- Dropped SF_ENABLE_DIC/_DOC/_PPM/_DEV compile flags.
	- Replaced SF_ENABLE_EXT_* with SF_ENABLE_SPI and spi_mode for hardware spi mode.
	- Added spi_chips and spi_clock settings.
	- Added 32 bit variable support.
	- Replaces chip_name_id to sys_id as 32 bit integer.
	- Added sys_pass for future login system.
	- Added req_doc to request doc port output bit.
	- Dropped LPM from chip code and moved to application.
	- Dropped experimental GLCD support to free pins.
	- Replaced mega pin mapping with mega_port_a and mega_port_c.
	- GUI: Small update for dark-red and black-white color thema.
	- GUI: Added new yellow-purple color thema.
	- Added two new pulse_dir modes; LRRL-2 and LRLR
	- Removed two pulse_modes PPM_ALL and PPM_INTERL.
	- Renamed pulse_trig_delay to pulse_pre_delay.
	- Added pulse_pre/post_mul step multiplier.
	- Added pulse_post_hold to select off or last step.
	- Added int0/1 variables to fire/hold/trigger/etc on int.
	- Replaced dev_freq.* to int_0freq/etc.
	- Dropped CIT (8bit-timer) support.
	- Changed avr_pin2/3 to input only.
	- GUI: Added visuals for info_pwm data.
	- Added dic_mux for mux chip mode select.
	- Added info_vars to output all variable meta data in one go.
	- Removed all(5) help arguments, now it only lists the help.
	- Removed 2 pulse_trig's now its LOOP or PULSE_FIRE.
	- GUI: Added serial cmd/error counters and improved serial code.
	- GUI: Added 250,500 and 1000 ms pull data speed.
	- Redone info_freq from pwm step data.
	- Renamed: mal_mticks to mal_wait and made 8b.
	- Redone main pulsefire time loop and removed *_time_cnt.
	- Added lcd_hcd for speed of hardware command delay.
	- Added 'info_data np' argument to remove auto push data from pulling.
	- Replaced SWC with generic stepper VSC on max 20hz.
	- Changed PTC speed from 10hz to 20hz with ptc_Xmul=0.
	- Added req_tx_hex for faster cmd rate, so over 1000 cmd/sec is done.
	- Added sys_uptime and sys_speed indicators.
	- Removed all but one 168p images which where to big.(>15kb)
	- Changed adc_jitter to 8bit value.
	- Removed dev_X_dot meta data for generic mapping.
	- Added sys_vvl/vvm_map, Variable Value Limit(min/max) and Meta(dot/lock)
	- GUI: Added inital 32b command variable support for sys_id/pass.
	- Renamed info_ppm output to info_ppm_a/b[0-15]
	- Made avr serial send use buffed lines.
	- GUI: Dropped experimental audio scope code.
	- GUI: Improved gui performance, so uses less cpu.
	- GUI: Reworked all tabs layouts.
	- GUI: Added keys; F3=connect/disconnect F11=toggleFullScreen (only when disconnected)
	- Dropped "==" was 'get' indicator now all response is single equals sign. 
	- Made adc skip readout if input not enabled.

## Version 1.0.4
	- Changed ptc_Xcnt and ptt_cntX to 16bit so mapping works with values above 255. 
	- Enabled cit_0a_com gui dropdown box.
	- Fixed avr-gcc const warning of pmCmdList array.
	- Added more variable filters in data tab panel.
	- Improved chip seconds timing from ~96% to ~99.5% relative to atom time.
	- Fixed/removed the 50% extra time offset of ptt and ptc 'time' mapping.
	  (now ptt time step of 6000 is 10min but in real 10min and ~24secs)
	- Redone firmware filter options, speed/type are saved and click column header to filter.
	- Fixed dial start offset, now relative from start so it is not jumping anymore.
	- Improved fire dial tooltip editor support.
	- Improved fire qmap table keyboard handling support.

## Version 1.0
	- Renamed lpm_auto_cmd to lpm_fire and made triggerable.
	- Renamed req_auto_lpm to req_lpm_fire like other triggers.
	- Reversed ppm idx bits so first fire works correct after mode switch.
	- Fixed PPM_ALL mode pulse train length.
	- Improved serial for only ascii and synced cmd echo of input.
	- Fixed PTC and PTT, reversed step and time action.
	- Added pulse_hold_fire trigger and req_pulse_hold_fire cmd and added to int pins.
	- Fixed AVR_MEGA pin18 & pin19 pin interrupt on/off based on avr_pinX_map
	- Fixed async interrupt command push which sometimes printed thru an other cmd.
	- Added readonly vars pulse_fire_cnt and pulse_fire_freq.
	- Fixed ext_pin and pulse_fire trigger speed and fixed ext_pin fire only on idle pwm state.
	- Added mal_ops/mal_ops_fire/mal_mticks interpreter speed control. 
	- Renamed mal_program to mal_code.
	- Splitted DIC_NUM_MAX to chip so now 8 normal and 16 on MEGA.
	- Recoded triggers removed all req_*_fire and replaced with req_trigger <idx|name> [<idxA>].
	- Added CIT,Chip Internal Timer on last hardware timer left in 168P/32P chip
	- Added pulse_reset_fire/pulse_resume_fire triggers to control pulse train.
	- Added pulse_fire_mode and pulse_hold_mode to control fire and hold setup.
	- Added pulse_fire/hold/resume/reset_map event maps on pulse fire sequence.
	- Improved save command speed by only update changed data.
	- Fixed MEGA save command, disabled watchdog while updating eeprom.
	- Fixed MEGA max value pulse_steps value and MEGA PTC map size.
	- Fixed MEGA MAL code address size issue when mal_code is bigger then 255.  

## Version 1.0-Beta8
	- Fixed internal event pulse_fire trigger.
	- Added load/save to (text) file of chip config options to gui.
	- Changed Freq from *10 to *100 for better readout and fixed bank_B support.
	- Added pulse_trigger EXT_FIRE for combined trigging source.
	- Added pwm_req_idx config variable to control hz freq request to channel.
	- Renamed pwm_duty to pwm_req_duty and moved pwm_req_freq from data to config.
	- Renamed mal_trig to mal_fire so all triggers are called '_fire'.
	- Added req_mal_fire cmd to request software mal code triggering.
	- Added invert per output; pulse_inv_a/b.
	- Changed swc_duty/swc_mode into swc_map for mappable options.
	- Changed stv_warn/error_mode into stv_warn/error_map so action is mappable.

## Version 1.0-Beta6
	- Converted project code to real C project.
	- Removed Arduino C libary dependeny.
	- Added SF_ENABLE_PWM flag to fully remove software pwm code.
	- Implemented Arduino flash protocol in java and added firmware flash dialog.
	- Moved compile option to config: lpm_relay_inv to inverse relay output signal.
	- Moved compile option to config: lcd_size; 0=2x16,1=2x20,2=4x20
	- Added dev_*_dot conf value for display and calc of volt/amp/temp.
	- Created makefile with ~40 target firmware builds for in system flashing.
	- Moved all chip IO functions to chip.h
	- Upped max digital inputs from 8 to 16.
	- Created Arduino MEGA support(non-tested).
	- Made adc_value and dic_value use auto push for gui update.
	- Added adc_enable bitfield to enable input readouts.
	- Added dic_enable/dic_inv/dic_sync bitfield per digital input.
	- Fixed main loop speed bug was in Input_loopLcd().
	- Removed SF_ENABLE_FRQ flag info_freq is always rest is moved to pwm flag.
	- Changed req_pwm_freq from 3 to 2 parameters removed duty uses always pwm_duty now.
	- Fixed makefile for mega 2650 and 1280.

## Version 0.9-fix
	- Fixed pulse_trig_delay to last step instead of step zero.
	- Fixed last step offset of pwm_* values of last step in train/ppmi mode.
	- Fixed STV set correct warn mode if input goes from error to warning.
	- Fixed indexA/indexB max checking when set/get variable falls back to idx=zero.
	- Fixed output of set all index values at once, cmd worked but printed only one.
	- Fixed info_ppm output to print per bank was per output.

## Version 0.9
	- Added stv_error_secs for optional recovery from error.
	- Added second timer map; ptc_time1_map.
	- Removed ptc_time_slots leaving unmapped is the same.
	- Added ptc_timeX_mul for time multipliers of steps.
	- Moved version cmd to chip_info output.
	- Changed feedback when mapping to echo only changed mapping.
	- Added sys_pin3_map and sys_pin4_map
	- WIP Added VFC_in/out_map for Virtual Feedback Channels
	- Added pulse_fire variable to do internal trigger replaces pulse_trigger=1.
	- Added req_pulse_fire to trigger fire on command.
	- Fixed default reset_data start step from one to zero.
	- Added dirty impl of freq/prm counter for vfc test.
	- Added trigger bit output to 'help map' cmd.
	- Fixed STV_*_secs minimal time in warning/error mode.
	- Fixed STV_*_secs 255 value of always off means always off.
	- Made sys_warmup_* unmappable for safety.
	- Added new pulse mode PULSE_MODE_FLASH_ZERO but shifted all modes;
	0=PULSE_MODE_OFF,
	1=PULSE_MODE_FLASH,
	2=PULSE_MODE_FLASH_ZERO,
	3=PULSE_MODE_TRAIN,
	4=PULSE_MODE_PPM,
	5=PULSE_MODE_PPMA,
	6=PULSE_MODE_PPMI
	- Added support for 16 digital outputs (DOC) without extra chip.(untested)
	- Added support for 12 digital inputs (DIC) done with pin mappings.
	- Renamed sys_warmup_* to swc; System Warmup Controller
	- Renamed SF_ENABLE_FREQ to SF_ENABLE_FRQ so chip_flags are inline.
	- Added bank support for pwm_on/of and ppm_data renamed to ppm_data_a/ppm_data_b/etc.
	- Renamed pulse_data_* to without _data_ keyword.
	- Added bank support for pulse_init_a/b and pulse_mask_b/b.
	- Renamed pulse_delay_post to pulse_post_delay.
	- Renamed/Impl pulse_trig_delay (was pulse_delay_pre)
	- Renamed sys_pinX_map to avr_pinX_map because is avr chip pins mappings only.
	- Renamed pulse_trigger to pulse_trig to be like others.
	- Added stv_*_mode value of 255 is keep the same, so message only.
	- Change swc_mode 0 was keep now 0 = off and 255 is keep the same.
	- Added chip_name and chip_name_id for manufacturing box type id.
	- Fixed &Renamed ptt_trigX_map timer looped for ever.
	- Added swc_trig to fire trigger after warmup.
	- Renamed sys_* to without sys_ for faster serial communition.
	- Fixed DIC reset bit to only clear one bit not all.
	- Fixed ptt_fire trig it would loop forever now runs once per trigger.

Errata:

	- Serial/io on high freq output.
	- Soft warmup and pulse_mode 3,4,5 is not oke.
	- MAL support is WorkInProgress.
	- When using 16 pwm outputs last output step had extratime.


## Version 0.8-fix
	- Dropped SF_ENABLE_INFO_CMD flags else gui wont work.
	- Renamed SF_ENABLE_FREQ_CMD -> SF_ENABLE_FREQ
	- Renamed SF_ENABLE_SOFT_START -> SF_ENABLE_WARMUP
	- Quick fix for auto_lpm will smooth more out when adding mmw code.
	- Added some missing chip_flags in info_chip
	- Added check to removed cmd which are removed by disable SF_ENABLE_* flags.
	- Dropped req_auto_lpm_tune cmd will be done in gui to many options for chip.
	- Fixed PF_VAR array size with SF_ENABLE_* flags so help cmd list is shorter.
	- Tuned lcd code to be bit slower to work on more models,Done by Russ.
	- Limited pwm_duty to 110, need to add extra counter for higher values.
	- Created readme/example/command files in project.

## Version 0.8
	- Fixed duplicate name so renamed pf_data.pulse_dir to pulse_dir_cnt
	- Added req_tx_promt and req_tx_echo for less serial output
	- Renamed req_auto_push to req_tx_push
	- droped SF_SHORT_CMD and SF_ENABLE_LOW_MEMORY compiler flags
	- Added auto config change based on eeprom space of avr chip.
	- Added 'help max' outputs max values of variales with 'max.' as prefix.
	- Changed 'help index' to 'help idx' and output max index values instead of map var id.
	- Added 'help bits' to output variable bit field values.
	- Added 'help map' to list it mappable value ids of variales with 'map.' prefix
	- Reserved extended lcd/out mode pins so in 3 output mode out0,1,2 are used.
	- Renamed reset_prog to reset_chip
	- Added chip/build info to info_chip
	- Renamed info_calc to info_freq and made output freq for all channels.
	- Added dev_volt,dev_amp,dev_temp,dev_freq,dev_varX for code and mappings.
	- Added sys_pin2_map to map pin2 do diffent features.
	- Renamed pwm_wait to pwm_tune_cnt,pwm_compa to pwm_on_cnt,pwm_compb to pwm_off_cnt
	- Renamed ppm_data_off to ppm_data_offset
	- Added stv Min and Max Safety Treshold Values.
	- Revered extened 16bit output so chip0 keeps output0
	- Added support for digital output my map to sys_doc_port[0-7] variable.
	- Removed named mapping support but left it working in command but output is numeric.
	- Improved lcd menu for all type lcd screens.
	- WIP ptc timers for variable like pulse_enable for conditioning of cells and trigger timers.
	- Changed safaty minimal value on pwm_cnt_on from 5 to 1 for motor setups.
	- Almost silenced boot so we can connect faster to port after flash.
	- Fixed pwm_off_cnt offset by one.
	- Fixed duty (pwm_off_cnt) to also use pwm_loop.

Errata:

	- Serial/io on high freq output.
	- Soft warmup and pulse_mode 3,4,5 is not oke.
	- MAL support is WorkInProgress.
	- req_auto_lpm_tune/pulse_delay_pre are not yet implemented.
	- sys_pin3_map and sys_pin4_map not yet implemented.

## Version 0.7

	- Rewrite of config variable handeling so smaller and more options
	- Wrote small lcd display driver for size and extended mode support
	- Fixed external trigger bug
	- Fixed soft warmup + added LCD keyword
	- Fixed one off ppm_data_len cmd
	- Changed sys_conn_mode to compiler flags
	- Moved sys_*_time and sys_input_delay to defines to save space in eeprom
	- WIP Added MicroAssemblyLanguage support for small scripts
	- Added sys_dic_map 2-8 digital inputs in extended mode
	- Added all fields to info_data to see internal pulse data.
	- Added info_prog command to see internal prog data.
	- Split source to multiple files phase1.
	- Renamed sys_start_* to sys_warmup_*.
	- Added sys_warmup_delay for startup predelay with all outputs off.
	- Added sys_warmup_mode to use diffent pulse mode in sys_warmup_secs warmup mode.
	- Added index printing argument to help command
	- Added pulse_mode 5 Interleaved ppm data
	- Made sys_adc/dic_map also work on indexed variables
	- Made sys_adc/dic_map work with variable names instead of numbers
	- Added more support for 16 column lcd and 4 row display
	- Added debug flag which print more info to serial
	- Moved sys_push_mode from eeprom into pf_prog with new name: req_auto_push
	- Fixed offset of pwm_compa,pwm_compb and pwm_wait so now pwm_compa 0 is step 0.
	- Fixed missed interrupt of compbX>0 so there was a loop of timer why result was stange.
	- Fixed duel step bug/wait in auto reveral pulse_dir mode.

Errata:

	- Serial/io on high freq output.
	- Soft warmup and pulse_mode 3,4,5 is not oke.
	- MAL support is WorkInProgress.
	- req_auto_lpm_tune/pulse_delay_pre are not yet implemented.

## Version 0.6
	- Added compiler flags to remove options so code can fit in 16K chip.
	- Note on flags; disabling LCD will make all 6 analog input usable.
	- Redone step duty timing now with option per output
	- Given each output step timing options
	- Added variable idx number to help output
	- Renamed train_* into pwm_*
	- Renamed _seq_* into ppm_*
	- Renamed defaults into reset_conf
	- Added reset_data(goto step0,etc) and reset_prog(reboots) cmds
	- req_auto_lpm cmd works (if lpm_size > 0 and adc mapping to 101)
	- LPM messurement code looks working with lcd output.
	- Added pulse_enable which is a global output enable useful for map adc for safety devices
	- Change pulse_modes into;
	0=PULSE_MODE_OFF
	1=PULSE_MODE_FLASH
	2=PULSE_MODE_TRAIN
	3=PULSE_MODE_PPM
	4=PULSE_MODE_PPMA
	- Added pulse_dir (0=LR,default,1=RL,2=LRRL(nightrider))
	- Added pulse_data_inv for inverting output data
	- Renamed pulse_duty into pulse_delay_post
	- pwm_compa is per step timer
	- pwm_compb is per step duty timer
	- pwm_wait is per step wait loop to delay output a few ms.
	- ppm_data is per output sequence data 
	- Added watchdog so if main loops is slower then 4sec device reboots.
	- Improved LCD output
	- Fixed pulse_steps 1 bug
	- Lots of things to make code faster/smaller
	- etc

Errata:

	- soft startup does not work
	- req_pwm_freq chooses sometimes low compa values which give some problems with receiving the serial data

## Version 0.5
	- Seqence mode offset looks working now
	- Fixed custom init data reversal in reverse mode
	- Added input validation for Serial and Lcd.
	- Given LCD UI all config options to change.
	- Added auto push mode which pushes config changes to serial
	- Added output masking for disabling some outputs
	- Spilt info into 3 commands info_conf,info_data and info_calc
	- Changed adc mapping now all config variables can be mapped.
	- Fixed flashmode output steps limit
	- Change _inc to _delta and added _tcnt_deltas's so now we have
	- 4 config values which puts the fire in the pulse.
	- start added auto lpm messurement code for freq tuning feature.
	- start working on req_train_freq command.

Errata:

	- pulse_steps 1 does not pulse
	- lpm code is yet connected
	- _delta seems not be working in pulse_mode 4
	- req_* requests are wip
	- Higher speeds breaks lcd/serial refresh (2out~600hz,6out~250hz)
	- In higher freq don't use req_train_freq but calc yourself for best result
	- bugs,etc

## Version 0.4
	- serial speed 115200 
	- config can be saved in eeprom
	- soft startup via extra off duty
	- mappable analog input to variable with range
	- 'WIP' code for pulse seqence with offset into next step
	- simple lcd ui via 2 button


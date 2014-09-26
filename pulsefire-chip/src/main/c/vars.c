
#include "vars.h"

pf_data_struct       pf_data;
pf_conf_struct       pf_conf;
#if defined(SF_ENABLE_AVR) | defined(SF_ENABLE_AVR_MEGA)
pf_conf_struct EEMEM pf_conf_eeprom;
#endif

CHIP_PROGMEM_ARRAY const pmCmdList[PMCMDLIST_SIZE] CHIP_PROGMEM = {
		pmCmdHelp,pmCmdSave,
		pmCmdInfoVars,pmCmdInfoConf,pmCmdInfoData,
		pmCmdInfoFreq,pmCmdInfoPPM,pmCmdInfoPWM,pmCmdInfoChip,
		pmCmdResetConfig,pmCmdResetData,pmCmdResetChip,pmCmdReqTrigger,pmCmdReqDoc,
		pmDataTXPush,pmDataTXEcho,pmDataTXPromt,pmDataTXHex,pmConfMALCode
};

/*
PF variable fields metadata:
0 = Variable Type
    0 = Disabled
    1 = uint8_t
    2 = uint16_t
    3 = uint32_t
    4-7 = Free
    8-12 = IndexA max
    13,14,15 = Index B max
1 = Pointer to variable in struct
2 = Pointer to ascii name of variable
3 = Max value
4 = Bitfield,
    0 = is data
    1 = Calc pwm
    2 = Remove from menu
    3 = No mapping
    4 = Step limiting of idx
    5 = Trigger variable
    6 = No reset
    7 = has auto pushing (only on data needed)
    8-16 = Free
5 = Default value

*/
// PFVT_TYPE,    VARIALBE_POINTER,                            ASCII_POINTER,                         MAX_VALUE,            VARIABLE_BITS,                 DEFAULT_VALUE
const CHIP_PTR_TYPE PF_VARS[PF_VARS_PF_SIZE+PF_VARS_AVR_SIZE+PF_VARS_AVR_MEGA_SIZE+
  PF_VARS_CIP_SIZE+PF_VARS_SPI_SIZE+PF_VARS_VSC0_SIZE+PF_VARS_VSC1_SIZE+
  PF_VARS_PWM_SIZE+PF_VARS_LCD_SIZE+
  PF_VARS_ADC_SIZE+PF_VARS_PTC0_SIZE+PF_VARS_PTC1_SIZE+
  PF_VARS_PTT_SIZE+PF_VARS_STV_SIZE+PF_VARS_VFC_SIZE+
  PF_VARS_MAL_SIZE][PFVF_DEF+ONE] CHIP_PROGMEM = {

#ifdef SF_ENABLE_PWM
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.pulse_enable,        (CHIP_PTR_TYPE)&pmConfPulseEnable,     ONE,                  PFVB_NONE,                     ONE},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.pulse_mode,          (CHIP_PTR_TYPE)&pmConfPulseMode,       PULSE_MODE_PPM,       PFVB_NONE+PFVB_CPWM,           PULSE_MODE_TRAIN},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.pulse_steps,         (CHIP_PTR_TYPE)&pmConfPulseSteps,
	#if defined(SF_ENABLE_SPI)
		OUTPUT_MAX,
	#elif defined(SF_ENABLE_AVR_MEGA)
		OUTPUT_MAX,
	#else
		6,
	#endif
	                                                                                                                      PFVB_NONE+PFVB_CPWM,            DEFAULT_PULSE_STEPS},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.pulse_trig,          (CHIP_PTR_TYPE)&pmConfPulseTrig,       PULSE_TRIG_PULSE_FIRE,PFVB_NONE,                     PULSE_TRIG_LOOP},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.pulse_bank,          (CHIP_PTR_TYPE)&pmConfPulseBank,       PULSE_BANK_MAX,      PFVB_NONE+PFVB_CPWM,            ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.pulse_dir,           (CHIP_PTR_TYPE)&pmConfPulseDir,        PULSE_DIR_LRPOLR,    PFVB_NONE+PFVB_CPWM,            ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.pulse_pre_delay,     (CHIP_PTR_TYPE)&pmConfPulsePreDelay,   0xFFFF,              PFVB_NONE+PFVB_CPWM,            ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.pulse_pre_mul,       (CHIP_PTR_TYPE)&pmConfPulsePreMul,     PULSE_DELAY_MUL_MAX, PFVB_NONE+PFVB_CPWM,            ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.pulse_post_delay,    (CHIP_PTR_TYPE)&pmConfPulsePostDelay,  0xFFFF,              PFVB_NONE+PFVB_CPWM,            ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.pulse_post_mul,      (CHIP_PTR_TYPE)&pmConfPulsePostMul,    PULSE_DELAY_MUL_MAX, PFVB_NONE+PFVB_CPWM,            ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.pulse_post_hold,     (CHIP_PTR_TYPE)&pmConfPulsePostHold,   PULSE_POST_HOLD_LAST2,PFVB_NONE+PFVB_CPWM,           ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.pulse_mask_a,        (CHIP_PTR_TYPE)&pmConfPulseMaskA,      0xFFFF,              PFVB_NONE+PFVB_CPWM,            PULSE_DATA_ON},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.pulse_mask_b,        (CHIP_PTR_TYPE)&pmConfPulseMaskB,      0xFFFF,              PFVB_NONE+PFVB_CPWM,            PULSE_DATA_ON},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.pulse_init_a,        (CHIP_PTR_TYPE)&pmConfPulseInitA,      0xFFFF,              PFVB_NONE+PFVB_CPWM,            DEFAULT_PULSE_DATA_INIT},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.pulse_init_b,        (CHIP_PTR_TYPE)&pmConfPulseInitB,      0xFFFF,              PFVB_NONE+PFVB_CPWM,            DEFAULT_PULSE_DATA_INIT},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.pulse_inv_a,         (CHIP_PTR_TYPE)&pmConfPulseInvA,       0xFFFF,              PFVB_NONE+PFVB_CPWM,            ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.pulse_inv_b,         (CHIP_PTR_TYPE)&pmConfPulseInvB,       0xFFFF,              PFVB_NONE+PFVB_CPWM,            ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.pulse_fire_mode,     (CHIP_PTR_TYPE)&pmConfPulseFireMode,   PULSE_FIRE_MODE_RESET,    PFVB_NONE,                 ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.pulse_hold_mode,     (CHIP_PTR_TYPE)&pmConfPulseHoldMode,   PULSE_HOLD_MODE_ZERO_CLR, PFVB_NONE,                 ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.pulse_hold_auto,     (CHIP_PTR_TYPE)&pmConfPulseHoldAuto,   PWM_DATA_MAX,        PFVB_NONE,                      ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.pulse_hold_autoclr,  (CHIP_PTR_TYPE)&pmConfPulseHoldAutoClr,ONE,                 PFVB_NONE,                      ZERO},

	{PFVT_16BIT+(QMAP_SIZE<<13)+(FIRE_MAP_MAX<<8),
			(CHIP_PTR_TYPE)&pf_conf.pulse_fire_map,           (CHIP_PTR_TYPE)&pmConfPulseFireMap,    0xFFFF,              PFVB_NOMAP+PFVB_NOMENU,         0xFFFF},
	{PFVT_16BIT+(QMAP_SIZE<<13)+(FIRE_MAP_MAX<<8),
			(CHIP_PTR_TYPE)&pf_conf.pulse_hold_map,           (CHIP_PTR_TYPE)&pmConfPulseHoldMap,    0xFFFF,              PFVB_NOMAP+PFVB_NOMENU,         0xFFFF},
	{PFVT_16BIT+(QMAP_SIZE<<13)+(FIRE_MAP_MAX<<8),
			(CHIP_PTR_TYPE)&pf_conf.pulse_resume_map,         (CHIP_PTR_TYPE)&pmConfPulseResumeMap,  0xFFFF,              PFVB_NOMAP+PFVB_NOMENU,         0xFFFF},
	{PFVT_16BIT+(QMAP_SIZE<<13)+(FIRE_MAP_MAX<<8),
			(CHIP_PTR_TYPE)&pf_conf.pulse_reset_map,          (CHIP_PTR_TYPE)&pmConfPulseResetMap,   0xFFFF,              PFVB_NOMAP+PFVB_NOMENU,         0xFFFF},

	{PFVT_16BIT+(OUTPUT_MAX<<8),
			(CHIP_PTR_TYPE)&pf_conf.pwm_on_cnt_a,             (CHIP_PTR_TYPE)&pmConfPWMOnCntA,       0xFFFF,              PFVB_SLIMIT+PFVB_CPWM,          DEFAULT_PWM_ON_CNT},
	{PFVT_16BIT+(OUTPUT_MAX<<8),
			(CHIP_PTR_TYPE)&pf_conf.pwm_on_cnt_b,             (CHIP_PTR_TYPE)&pmConfPWMOnCntB,       0xFFFF,              PFVB_SLIMIT+PFVB_CPWM,          DEFAULT_PWM_ON_CNT},
	{PFVT_16BIT+(OUTPUT_MAX<<8),
			(CHIP_PTR_TYPE)&pf_conf.pwm_off_cnt_a,            (CHIP_PTR_TYPE)&pmConfPWMOffCntA,      0xFFFF,              PFVB_SLIMIT+PFVB_CPWM,          ZERO},
	{PFVT_16BIT+(OUTPUT_MAX<<8),
			(CHIP_PTR_TYPE)&pf_conf.pwm_off_cnt_b,            (CHIP_PTR_TYPE)&pmConfPWMOffCntB,      0xFFFF,              PFVB_SLIMIT+PFVB_CPWM,          ZERO},

	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.pwm_loop,            (CHIP_PTR_TYPE)&pmConfPWMLoop,         0xFF,                PFVB_NONE,                      DEFAULT_PWM_LOOP},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.pwm_clock,           (CHIP_PTR_TYPE)&pmConfPWMClock,        CLOCK_VALUE_MAX,     PFVB_NONE,                      DEFAULT_PWM_CLOCK},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.pwm_req_idx,         (CHIP_PTR_TYPE)&pmConfPWMReqIdx,       QMAP_VAR_IDX_ALL,    PFVB_NONE,                      QMAP_VAR_IDX_ALL},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.pwm_req_duty,        (CHIP_PTR_TYPE)&pmConfPWMReqDuty,      0xFF,                PFVB_NONE,                      ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.pwm_req_freq,        (CHIP_PTR_TYPE)&pmConfPWMReqFreq,      0xFFFF,              PFVB_NONE,                      ZERO},

	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.ppm_data_offset,     (CHIP_PTR_TYPE)&pmConfPPMDataOffset,   OUTPUT_MAX-ONE,      PFVB_CPWM,                      ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.ppm_data_length,     (CHIP_PTR_TYPE)&pmConfPPMDataLength,   OUTPUT_MAX,          PFVB_CPWM,                      OUTPUT_MAX},
	{PFVT_16BIT+(OUTPUT_MAX<<8),
			(CHIP_PTR_TYPE)&pf_conf.ppm_data_a,               (CHIP_PTR_TYPE)&pmConfPPMDataA,        0xFFFF,              PFVB_SLIMIT+PFVB_CPWM,          ZERO},
	{PFVT_16BIT+(OUTPUT_MAX<<8),
			(CHIP_PTR_TYPE)&pf_conf.ppm_data_b,               (CHIP_PTR_TYPE)&pmConfPPMDataB,        0xFFFF,              PFVB_SLIMIT+PFVB_CPWM,          ZERO},
#endif

#ifdef SF_ENABLE_SPI
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.spi_clock,           (CHIP_PTR_TYPE)&pmConfSpiClock,        SPI_CLOCK_MAX,       PFVB_NONE,                      ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.spi_chips,           (CHIP_PTR_TYPE)&pmConfSpiChips,        0xFF,                PFVB_NONE,                      ZERO},
#endif

#ifdef SF_ENABLE_PTC0
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.ptc_0run,            (CHIP_PTR_TYPE)&pmConfPTC0Run,         PTC_RUN_LOOP,        PFVB_NONE,                      PTC_RUN_OFF},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.ptc_0mul,            (CHIP_PTR_TYPE)&pmConfPTC0Mul,         0xFF,                PFVB_NONE,                      ONE},
	{PFVT_16BIT+(QMAP_SIZE<<13)+(PTC_TIME_MAP_MAX<<8),
			(CHIP_PTR_TYPE)&pf_conf.ptc_0map,                 (CHIP_PTR_TYPE)&pmConfPTC0Map,         0xFFFF,              PFVB_NOMAP+PFVB_NOMENU,         0xFFFF},
#endif
#ifdef SF_ENABLE_PTC1
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.ptc_1run,            (CHIP_PTR_TYPE)&pmConfPTC1Run,         PTC_RUN_LOOP,        PFVB_NONE,                      PTC_RUN_OFF},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.ptc_1mul,            (CHIP_PTR_TYPE)&pmConfPTC1Mul,         0xFF,                PFVB_NONE,                      ONE},
	{PFVT_16BIT+(QMAP_SIZE<<13)+(PTC_TIME_MAP_MAX<<8),
			(CHIP_PTR_TYPE)&pf_conf.ptc_1map,                 (CHIP_PTR_TYPE)&pmConfPTC1Map,         0xFFFF,              PFVB_NOMAP+PFVB_NOMENU,         0xFFFF},
#endif
#ifdef SF_ENABLE_PTT
	{PFVT_16BIT+(QMAP_SIZE<<13)+(PTT_TRIG_MAP_MAX<<8),
			(CHIP_PTR_TYPE)&pf_conf.ptt_0map,                 (CHIP_PTR_TYPE)&pmConfPTT0Map,         0xFFFF,              PFVB_NOMAP+PFVB_NOMENU,         0xFFFF},
	{PFVT_16BIT+(QMAP_SIZE<<13)+(PTT_TRIG_MAP_MAX<<8),
			(CHIP_PTR_TYPE)&pf_conf.ptt_1map,                 (CHIP_PTR_TYPE)&pmConfPTT1Map,         0xFFFF,              PFVB_NOMAP+PFVB_NOMENU,         0xFFFF},
	{PFVT_16BIT+(QMAP_SIZE<<13)+(PTT_TRIG_MAP_MAX<<8),
			(CHIP_PTR_TYPE)&pf_conf.ptt_2map,                 (CHIP_PTR_TYPE)&pmConfPTT2Map,         0xFFFF,              PFVB_NOMAP+PFVB_NOMENU,         0xFFFF},
	{PFVT_16BIT+(QMAP_SIZE<<13)+(PTT_TRIG_MAP_MAX<<8),
			(CHIP_PTR_TYPE)&pf_conf.ptt_3map,                 (CHIP_PTR_TYPE)&pmConfPTT3Map,         0xFFFF,              PFVB_NOMAP+PFVB_NOMENU,         0xFFFF},
#endif

	{PFVT_32BIT, (CHIP_PTR_TYPE)&pf_conf.sys_id,              (CHIP_PTR_TYPE)&pmConfSysId,           0xFFFF,              PFVB_NONE,                      ZERO},
	{PFVT_32BIT, (CHIP_PTR_TYPE)&pf_conf.sys_pass,            (CHIP_PTR_TYPE)&pmConfSysPass,         0xFFFF,              PFVB_NONE,                      ZERO},
	{PFVT_16BIT+(QMAP_SIZE<<13)+(SYS_VVX_MAP_MAX<<8),
			(CHIP_PTR_TYPE)&pf_conf.sys_vvm_map,              (CHIP_PTR_TYPE)&pmConfSysVvmMap,       0xFFFF,              PFVB_NOMAP+PFVB_NOMENU,         0xFFFF},
	{PFVT_16BIT+(QMAP_SIZE<<13)+(SYS_VVX_MAP_MAX<<8),
			(CHIP_PTR_TYPE)&pf_conf.sys_vvl_map,              (CHIP_PTR_TYPE)&pmConfSysVvlMap,       0xFFFF,              PFVB_NOMAP+PFVB_NOMENU,         0xFFFF},

#ifdef SF_ENABLE_STV
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.stv_warn_secs,       (CHIP_PTR_TYPE)&pmConfSTVWarnSecs,     0xFF,                PFVB_NOMAP+PFVB_NOMENU,         ZERO},
	{PFVT_16BIT+(QMAP_SIZE<<13)+(STV_WARN_MAP_MAX<<8),
			(CHIP_PTR_TYPE)&pf_conf.stv_warn_map,             (CHIP_PTR_TYPE)&pmConfSTVWarnMap,      0xFFFF,              PFVB_NOMAP+PFVB_NOMENU,         0xFFFF},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.stv_error_secs,      (CHIP_PTR_TYPE)&pmConfSTVErrorSecs,    0xFF,                PFVB_NOMAP+PFVB_NOMENU,         ZERO},
	{PFVT_16BIT+(QMAP_SIZE<<13)+(STV_ERROR_MAP_MAX<<8),
			(CHIP_PTR_TYPE)&pf_conf.stv_error_map,            (CHIP_PTR_TYPE)&pmConfSTVErrorMap,     0xFFFF,              PFVB_NOMAP+PFVB_NOMENU,         0xFFFF},
	{PFVT_16BIT+(QMAP_SIZE<<13)+(STV_MAX_MAP_MAX<<8),
			(CHIP_PTR_TYPE)&pf_conf.stv_max_map,              (CHIP_PTR_TYPE)&pmConfSTVMaxMap,       0xFFFF,              PFVB_NOMAP+PFVB_NOMENU,         0xFFFF},
	{PFVT_16BIT+(QMAP_SIZE<<13)+(STV_MIN_MAP_MAX<<8),
			(CHIP_PTR_TYPE)&pf_conf.stv_min_map,              (CHIP_PTR_TYPE)&pmConfSTVMinMap,       0xFFFF,              PFVB_NOMAP+PFVB_NOMENU,         0xFFFF},
#endif
#ifdef SF_ENABLE_VFC
	{PFVT_16BIT+(QMAP_SIZE<<13)+(VFC_MAP_MAX<<8),
			(CHIP_PTR_TYPE)&pf_conf.vfc_input_map,            (CHIP_PTR_TYPE)&pmConfVFCInputMap,     0xFFFF,              PFVB_NOMAP+PFVB_NOMENU,         0xFFFF},
	{PFVT_16BIT+(QMAP_SIZE<<13)+(VFC_MAP_MAX<<8),
			(CHIP_PTR_TYPE)&pf_conf.vfc_output_map,           (CHIP_PTR_TYPE)&pmConfVFCOutputMap,    0xFFFF,              PFVB_NOMAP+PFVB_NOMENU,         0xFFFF},
#endif
#ifdef SF_ENABLE_VSC0
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.vsc_0mode,           (CHIP_PTR_TYPE)&pmConfVsc0Mode,        VSC_MODE_LOOP_UPDOWN,PFVB_NONE,                      ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.vsc_0time,           (CHIP_PTR_TYPE)&pmConfVsc0Time,        0xFFFF,              PFVB_NONE,                      ONE},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.vsc_0step,           (CHIP_PTR_TYPE)&pmConfVsc0Step,        0xFF,                PFVB_NONE,                      ONE},
	{PFVT_16BIT+(QMAP_SIZE<<13)+(VSC_MAP_MAX<<8),
			(CHIP_PTR_TYPE)&pf_conf.vsc_0map,                 (CHIP_PTR_TYPE)&pmConfVsc0Map,         0xFFFF,              PFVB_NOMAP+PFVB_NOMENU,         0xFFFF},
#endif
#ifdef SF_ENABLE_VSC1
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.vsc_1mode,           (CHIP_PTR_TYPE)&pmConfVsc1Mode,        VSC_MODE_LOOP_UPDOWN,PFVB_NONE,                      ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.vsc_1time,           (CHIP_PTR_TYPE)&pmConfVsc1Time,        0xFFFF,              PFVB_NONE,                      ONE},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.vsc_1step,           (CHIP_PTR_TYPE)&pmConfVsc1Step,        0xFF,                PFVB_NONE,                      ONE},
	{PFVT_16BIT+(QMAP_SIZE<<13)+(VSC_MAP_MAX<<8),
			(CHIP_PTR_TYPE)&pf_conf.vsc_1map,                 (CHIP_PTR_TYPE)&pmConfVsc1Map,         0xFFFF,              PFVB_NOMAP+PFVB_NOMENU,         0xFFFF},
#endif

#ifdef SF_ENABLE_AVR
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.avr_pin2_map,        (CHIP_PTR_TYPE)&pmConfAVRPin2Map,      PIN2_INT0_IN,        PFVB_NOMAP+PFVB_NOMENU,         PIN2_OFF},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.avr_pin3_map,        (CHIP_PTR_TYPE)&pmConfAVRPin3Map,      PIN3_INT1_IN,        PFVB_NOMAP+PFVB_NOMENU,         PIN3_OFF},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.avr_pin4_map,        (CHIP_PTR_TYPE)&pmConfAVRPin4Map,      PIN4_DOC10_OUT,      PFVB_NOMAP+PFVB_NOMENU,         PIN4_OFF},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.avr_pin5_map,        (CHIP_PTR_TYPE)&pmConfAVRPin5Map,      PIN5_CLOCK_IN,       PFVB_NOMAP+PFVB_NOMENU,         PIN5_OFF},
#endif

#ifdef SF_ENABLE_AVR_MEGA
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.mega_port_a,         (CHIP_PTR_TYPE)&pmConfMegaPortA,      PORTA_DOC8,          PFVB_NOMAP+PFVB_NOMENU,          PORTA_OFF},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.mega_port_c,         (CHIP_PTR_TYPE)&pmConfMegaPortC,      PORTC_DOC16,         PFVB_NOMAP+PFVB_NOMENU,          PORTC_OFF},
#endif

#ifdef SF_ENABLE_LCD
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.lcd_size,            (CHIP_PTR_TYPE)&pmConfLCDSize,         LCD_SIZE_4x20,       PFVB_NOMAP+PFVB_NOMENU,         ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.lcd_defp,            (CHIP_PTR_TYPE)&pmConfLCDDefp,         4,                   PFVB_NOMAP,                     ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.lcd_mode,            (CHIP_PTR_TYPE)&pmConfLCDMode,         3,                   PFVB_NOMAP,                     ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.lcd_hcd,             (CHIP_PTR_TYPE)&pmConfLCDHcd,          LCD_HCD_5,           PFVB_NOMAP,                     LCD_HCD_5},
	{PFVT_8BIT+(LCD_PLP_MAX<<8),
			(CHIP_PTR_TYPE)&pf_conf.lcd_plp,                  (CHIP_PTR_TYPE)&pmConfLCDPlp,          0xFF,                PFVB_NOMAP+PFVB_NOMENU,         0xFF},
#endif
#ifdef SF_ENABLE_MAL
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.mal_ops,             (CHIP_PTR_TYPE)&pmConfMALOps,          0xFF,                PFVB_NONE,                      0xFF},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.mal_ops_fire,        (CHIP_PTR_TYPE)&pmConfMALOpsFire,      0xFF,                PFVB_NONE,                      0xFF},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.mal_wait,            (CHIP_PTR_TYPE)&pmConfMALWait,         0xFF,                PFVB_NONE,                      ZERO},
#endif

#ifdef SF_ENABLE_ADC
	{PFVT_16BIT+(QMAP_SIZE<<13)+(ADC_MAP_MAX<<8),
			(CHIP_PTR_TYPE)&pf_conf.adc_map,                  (CHIP_PTR_TYPE)&pmConfAdcMap,          0xFFFF,              PFVB_NOMAP+PFVB_NOMENU,         0xFFFF},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.adc_enable,          (CHIP_PTR_TYPE)&pmConfAdcEnable,       0xFFFF,              PFVB_NOMAP+PFVB_NOMENU,         ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.adc_jitter,          (CHIP_PTR_TYPE)&pmConfAdcJitter,       0xFF,                PFVB_NOMAP+PFVB_NOMENU,         DEFAULT_SYS_ADC_JITTER},
#endif

	{PFVT_16BIT+(QMAP_SIZE<<13)+(INT_MAP_MAX<<8),
                 (CHIP_PTR_TYPE)&pf_conf.int_map,             (CHIP_PTR_TYPE)&pmConfIntMap,          0xFFFF,              PFVB_NOMAP+PFVB_NOMENU,         0xFFFF},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.int_0mode,           (CHIP_PTR_TYPE)&pmConfInt0Mode,        INT_MODE_MAP,        PFVB_NOMAP+PFVB_NOMENU,         ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.int_0trig,           (CHIP_PTR_TYPE)&pmConfInt0Trig,        INT_TRIG_EDGE_RISE,  PFVB_NOMENU,                    ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.int_0freq_mul,       (CHIP_PTR_TYPE)&pmConfInt0FreqMul,     INT_FREQ_DIV_100,    PFVB_NOMENU,                    ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.int_1mode,           (CHIP_PTR_TYPE)&pmConfInt1Mode,        INT_MODE_MAP,        PFVB_NOMAP+PFVB_NOMENU,         ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.int_1trig,           (CHIP_PTR_TYPE)&pmConfInt1Trig,        INT_TRIG_EDGE_RISE,  PFVB_NOMENU,                    ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.int_1freq_mul,       (CHIP_PTR_TYPE)&pmConfInt1FreqMul,     INT_FREQ_DIV_100,    PFVB_NOMENU,                    ZERO},

	{PFVT_16BIT+(QMAP_SIZE<<13)+(DIC_MAP_MAX<<8),
                 (CHIP_PTR_TYPE)&pf_conf.dic_map,             (CHIP_PTR_TYPE)&pmConfDicMap,          0xFFFF,              PFVB_NOMAP+PFVB_NOMENU,         0xFFFF},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.dic_enable,          (CHIP_PTR_TYPE)&pmConfDicEnable,       0xFFFF,              PFVB_NOMAP+PFVB_NOMENU,         ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.dic_inv,             (CHIP_PTR_TYPE)&pmConfDicInv,          0xFFFF,              PFVB_NOMAP+PFVB_NOMENU,         ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.dic_sync,            (CHIP_PTR_TYPE)&pmConfDicSync,         0xFFFF,              PFVB_NOMAP+PFVB_NOMENU,         ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.dic_mux,             (CHIP_PTR_TYPE)&pmConfDicMux,          ONE,                 PFVB_NOMAP+PFVB_NOMENU,         ZERO},

#ifdef SF_ENABLE_CIP
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.cip_0clock,          (CHIP_PTR_TYPE)&pmConfCip0Clock,       8,                   PFVB_NONE,                      ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.cip_0mode,           (CHIP_PTR_TYPE)&pmConfCip0Mode,        16,                  PFVB_NONE,                      ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.cip_0a_ocr,          (CHIP_PTR_TYPE)&pmConfCip0aOcr,        0xFFFF,              PFVB_NONE,                      ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.cip_0a_com,          (CHIP_PTR_TYPE)&pmConfCip0aCom,        0xFFFF,              PFVB_NONE,                      ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.cip_0b_ocr,          (CHIP_PTR_TYPE)&pmConfCip0bOcr,        0xFFFF,              PFVB_NONE,                      ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.cip_0b_com,          (CHIP_PTR_TYPE)&pmConfCip0bCom,        0xFFFF,              PFVB_NONE,                      ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.cip_0c_ocr,          (CHIP_PTR_TYPE)&pmConfCip0cOcr,        0xFFFF,              PFVB_NONE,                      ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.cip_0c_com,          (CHIP_PTR_TYPE)&pmConfCip0cCom,        0xFFFF,              PFVB_NONE,                      ZERO},

	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.cip_1clock,          (CHIP_PTR_TYPE)&pmConfCip1Clock,       8,                   PFVB_NONE,                      ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.cip_1mode,           (CHIP_PTR_TYPE)&pmConfCip1Mode,        16,                  PFVB_NONE,                      ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.cip_1a_ocr,          (CHIP_PTR_TYPE)&pmConfCip1aOcr,        0xFFFF,              PFVB_NONE,                      ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.cip_1a_com,          (CHIP_PTR_TYPE)&pmConfCip1aCom,        0xFFFF,              PFVB_NONE,                      ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.cip_1b_ocr,          (CHIP_PTR_TYPE)&pmConfCip1bOcr,        0xFFFF,              PFVB_NONE,                      ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.cip_1b_com,          (CHIP_PTR_TYPE)&pmConfCip1bCom,        0xFFFF,              PFVB_NONE,                      ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.cip_1c_ocr,          (CHIP_PTR_TYPE)&pmConfCip1cOcr,        0xFFFF,              PFVB_NONE,                      ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.cip_1c_com,          (CHIP_PTR_TYPE)&pmConfCip1cCom,        0xFFFF,              PFVB_NONE,                      ZERO},

	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.cip_2clock,          (CHIP_PTR_TYPE)&pmConfCip2Clock,       8,                   PFVB_NONE,                      ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.cip_2mode,           (CHIP_PTR_TYPE)&pmConfCip2Mode,        16,                  PFVB_NONE,                      ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.cip_2a_ocr,          (CHIP_PTR_TYPE)&pmConfCip2aOcr,        0xFFFF,              PFVB_NONE,                      ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.cip_2a_com,          (CHIP_PTR_TYPE)&pmConfCip2aCom,        0xFFFF,              PFVB_NONE,                      ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.cip_2b_ocr,          (CHIP_PTR_TYPE)&pmConfCip2bOcr,        0xFFFF,              PFVB_NONE,                      ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.cip_2b_com,          (CHIP_PTR_TYPE)&pmConfCip2bCom,        0xFFFF,              PFVB_NONE,                      ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.cip_2c_ocr,          (CHIP_PTR_TYPE)&pmConfCip2cOcr,        0xFFFF,              PFVB_NONE,                      ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.cip_2c_com,          (CHIP_PTR_TYPE)&pmConfCip2cCom,        0xFFFF,              PFVB_NONE,                      ZERO},
#endif

// =============== pf_data vars = +64

	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.sys_time_ticks,      (CHIP_PTR_TYPE)&pmDataSysTimeTicks,    ZERO,                PFVB_DATA+PFVB_NORST+PFVB_NOMAP,ZERO},
	{PFVT_32BIT, (CHIP_PTR_TYPE)&pf_data.sys_time_csec,       (CHIP_PTR_TYPE)&pmDataSysTimeCsec,     ZERO,                PFVB_DATA+PFVB_NORST+PFVB_NOMAP,ZERO},
	{PFVT_32BIT, (CHIP_PTR_TYPE)&pf_data.sys_uptime,          (CHIP_PTR_TYPE)&pmDataSysUpTime,       ZERO,                PFVB_DATA+PFVB_NORST+PFVB_NOMAP,ZERO},
	{PFVT_32BIT, (CHIP_PTR_TYPE)&pf_data.sys_speed,           (CHIP_PTR_TYPE)&pmDataSysSpeed,        ZERO,                PFVB_DATA+PFVB_NORST+PFVB_NOMAP,ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.sys_bad_isr,         (CHIP_PTR_TYPE)&pmDataSysBadIsr,       ZERO,                PFVB_DATA+PFVB_NORST+PFVB_NOMAP,ZERO},

#ifdef SF_ENABLE_ADC
	{PFVT_16BIT+(ADC_MAP_MAX<<8),
			(CHIP_PTR_TYPE)&pf_data.adc_value,                (CHIP_PTR_TYPE)&pmDataAdcValue,        0xFFFF,              PFVB_DATA+PFVB_NOMAP+PFVB_PUSH, ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.adc_state,           (CHIP_PTR_TYPE)&pmDataAdcState,        ADC_STATE_DONE,      PFVB_DATA+PFVB_NOMAP,           ADC_STATE_IDLE},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.adc_state_idx,       (CHIP_PTR_TYPE)&pmDataAdcStateIdx,     ADC_MAP_MAX,         PFVB_DATA+PFVB_NOMAP,           ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_data.adc_state_value,     (CHIP_PTR_TYPE)&pmDataAdcStateValue,   0xFFFF,              PFVB_DATA+PFVB_NOMAP,           ZERO},
#endif

	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_data.int_0freq,           (CHIP_PTR_TYPE)&pmDataInt0Freq,        0xFFFF,              PFVB_DATA+PFVB_NOMAP,           ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_data.int_0freq_cnt,       (CHIP_PTR_TYPE)&pmDataInt0FreqCnt,     0xFFFF,              PFVB_DATA+PFVB_NOMAP,           ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_data.int_1freq,           (CHIP_PTR_TYPE)&pmDataInt1Freq,        0xFFFF,              PFVB_DATA+PFVB_NOMAP,           ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_data.int_1freq_cnt,       (CHIP_PTR_TYPE)&pmDataInt1FreqCnt,     0xFFFF,              PFVB_DATA+PFVB_NOMAP,           ZERO},

	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_data.dic_value,           (CHIP_PTR_TYPE)&pmDataDicValue,        0xFFFF,              PFVB_DATA+PFVB_NOMAP,           ZERO},

	{PFVT_8BIT+(DOC_PORT_NUM_MAX<<8),
			(CHIP_PTR_TYPE)&pf_data.doc_port,                 (CHIP_PTR_TYPE)&pmDataDocPort,         ONE,                 PFVB_DATA+PFVB_PUSH,            ZERO},


#ifdef SF_ENABLE_VSC0
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_data.vsc_0time_cnt,       (CHIP_PTR_TYPE)&pmDataVsc0TimeCnt,     0xFFFF,              PFVB_DATA+PFVB_NOMAP,           ZERO},
	{PFVT_8BIT+(VSC_MAP_MAX<<8),
                 (CHIP_PTR_TYPE)&pf_data.vsc_0state,          (CHIP_PTR_TYPE)&pmDataVsc0State,       0xFF,                PFVB_DATA+PFVB_NOMAP,           ZERO},
#endif
#ifdef SF_ENABLE_VSC1
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_data.vsc_1time_cnt,       (CHIP_PTR_TYPE)&pmDataVsc1TimeCnt,     0xFFFF,              PFVB_DATA+PFVB_NOMAP,           ZERO},
	{PFVT_8BIT+(VSC_MAP_MAX<<8),
                 (CHIP_PTR_TYPE)&pf_data.vsc_1state,          (CHIP_PTR_TYPE)&pmDataVsc1State,       0xFF,                PFVB_DATA+PFVB_NOMAP,           ZERO},
#endif
#ifdef SF_ENABLE_LCD
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.lcd_input,           (CHIP_PTR_TYPE)&pmDataLcdInput,        ZERO,                PFVB_DATA,                      ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.lcd_page,            (CHIP_PTR_TYPE)&pmDataLcdPage,         ZERO,                PFVB_DATA+PFVB_NOMAP,           ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.lcd_redraw,          (CHIP_PTR_TYPE)&pmDataLcdRedraw,       ZERO,                PFVB_DATA+PFVB_NOMAP,           ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.lcd_menu_state,      (CHIP_PTR_TYPE)&pmDataLcdMenuState,    0xFF,                PFVB_DATA+PFVB_NORST+PFVB_NOMAP,ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_data.lcd_menu_mul,        (CHIP_PTR_TYPE)&pmDataLcdMenuMul,      0xFFFF,              PFVB_DATA+PFVB_NORST+PFVB_NOMAP,ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.lcd_menu_idx,        (CHIP_PTR_TYPE)&pmDataLcdMenuIdx,      0xFF,                PFVB_DATA+PFVB_NORST+PFVB_NOMAP,ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.lcd_menu_value_idx,  (CHIP_PTR_TYPE)&pmDataLcdMenuValueIdx, 0xFF,                PFVB_DATA+PFVB_NORST+PFVB_NOMAP,ZERO},
	{PFVT_32BIT, (CHIP_PTR_TYPE)&pf_data.lcd_menu_time_cnt,   (CHIP_PTR_TYPE)&pmDataLcdMenuTimeCnt,  ZERO,                PFVB_DATA+PFVB_NORST+PFVB_NOMAP,ZERO},
#endif

#ifdef SF_ENABLE_PTC0
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_data.ptc_0cnt,            (CHIP_PTR_TYPE)&pmDataPTC0Cnt,         0xFF,                PFVB_DATA+PFVB_NOMAP,           ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.ptc_0run_cnt,        (CHIP_PTR_TYPE)&pmDataPTC0RunCnt,      0xFF,                PFVB_DATA+PFVB_NOMAP,           ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.ptc_0map_idx,        (CHIP_PTR_TYPE)&pmDataPTC0MapIdx,      PTC_TIME_MAP_MAX,    PFVB_DATA+PFVB_NOMAP,           ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_data.ptc_0mul_cnt,        (CHIP_PTR_TYPE)&pmDataPTC0MulCnt,      0xFFFF,              PFVB_DATA+PFVB_NOMAP,           ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.ptc_0step,           (CHIP_PTR_TYPE)&pmDataPTC0Step,        ONE,                 PFVB_DATA+PFVB_NOMAP,           ZERO},
#endif
#ifdef SF_ENABLE_PTC1
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_data.ptc_1cnt,            (CHIP_PTR_TYPE)&pmDataPTC1Cnt,         0xFF,                PFVB_DATA+PFVB_NOMAP,           ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.ptc_1run_cnt,        (CHIP_PTR_TYPE)&pmDataPTC1RunCnt,      0xFF,                PFVB_DATA+PFVB_NOMAP,           ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.ptc_1map_idx,        (CHIP_PTR_TYPE)&pmDataPTC1MapIdx,      PTC_TIME_MAP_MAX,    PFVB_DATA+PFVB_NOMAP,           ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_data.ptc_1mul_cnt,        (CHIP_PTR_TYPE)&pmDataPTC1MulCnt,      0xFFFF,              PFVB_DATA+PFVB_NOMAP,           ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.ptc_1step,           (CHIP_PTR_TYPE)&pmDataPTC1Step,        ONE,                 PFVB_DATA+PFVB_NOMAP,           ZERO},
#endif
#ifdef SF_ENABLE_PTT
	{PFVT_8BIT+(PTT_TRIG_VAR_SIZE<<8),
			(CHIP_PTR_TYPE)&pf_data.ptt_idx,                  (CHIP_PTR_TYPE)&pmDataPTTIdx,          0xFF,                PFVB_DATA+PFVB_NOMAP,           ZERO},
	{PFVT_16BIT+(PTT_TRIG_VAR_SIZE<<8),
			(CHIP_PTR_TYPE)&pf_data.ptt_cnt,                  (CHIP_PTR_TYPE)&pmDataPTTCnt,          0xFF,                PFVB_DATA+PFVB_NOMAP,           ZERO},
	{PFVT_8BIT+(PTT_TRIG_VAR_SIZE<<8),
			(CHIP_PTR_TYPE)&pf_data.ptt_fire,                 (CHIP_PTR_TYPE)&pmDataPTTFire,         0xFF,                PFVB_DATA+PFVB_TRIG,            ZERO},
	{PFVT_8BIT+(PTT_TRIG_VAR_SIZE<<8),
			(CHIP_PTR_TYPE)&pf_data.ptt_step,                 (CHIP_PTR_TYPE)&pmDataPTTStep,         ONE,                 PFVB_DATA+PFVB_NOMAP,           ZERO},
#endif

	{PFVT_16BIT+(DEV_VAR_MAX<<8),
			(CHIP_PTR_TYPE)&pf_data.dev_volt,                 (CHIP_PTR_TYPE)&pmDataDevVolt,         0xFFFF,              PFVB_DATA+PFVB_PUSH,            ZERO},
	{PFVT_16BIT+(DEV_VAR_MAX<<8),
			(CHIP_PTR_TYPE)&pf_data.dev_amp,                  (CHIP_PTR_TYPE)&pmDataDevAmp,          0xFFFF,              PFVB_DATA+PFVB_PUSH,            ZERO},
	{PFVT_16BIT+(DEV_VAR_MAX<<8),
			(CHIP_PTR_TYPE)&pf_data.dev_temp,                 (CHIP_PTR_TYPE)&pmDataDevTemp,         0xFFFF,              PFVB_DATA+PFVB_PUSH,            ZERO},
	{PFVT_16BIT+(DEV_VAR_MAX<<8),
			(CHIP_PTR_TYPE)&pf_data.dev_var,                  (CHIP_PTR_TYPE)&pmDataDevVar,          0xFFFF,              PFVB_DATA+PFVB_PUSH,            ZERO},


#ifdef SF_ENABLE_PWM
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.pulse_fire,          (CHIP_PTR_TYPE)&pmDataPulseFire,       ONE,                 PFVB_DATA+PFVB_TRIG,            ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_data.pulse_fire_cnt,      (CHIP_PTR_TYPE)&pmDataPulseFireCnt,    0xFFFF,              PFVB_DATA+PFVB_NOMAP,           ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_data.pulse_fire_freq,     (CHIP_PTR_TYPE)&pmDataPulseFireFreq,   0xFFFF,              PFVB_DATA+PFVB_NOMAP,           ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.pulse_hold_fire,     (CHIP_PTR_TYPE)&pmDataPulseHoldFire,   ONE,                 PFVB_DATA+PFVB_TRIG,            ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.pulse_reset_fire,    (CHIP_PTR_TYPE)&pmDataPulseResetFire,  ONE,                 PFVB_DATA+PFVB_TRIG,            ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.pulse_resume_fire,   (CHIP_PTR_TYPE)&pmDataPulseResumeFire, ONE,                 PFVB_DATA+PFVB_TRIG,            ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.pulse_step,          (CHIP_PTR_TYPE)&pmDataPulseStep,       0xFF,                PFVB_DATA+PFVB_NOMAP,           ZERO},

	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.pwm_state,           (CHIP_PTR_TYPE)&pmDataPWMState,        0xFF,                PFVB_DATA+PFVB_NOMAP,           ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.pwm_loop_cnt,        (CHIP_PTR_TYPE)&pmDataPWMLoopCnt,      0xFF,                PFVB_DATA+PFVB_NOMAP,           ZERO},
#endif

#ifdef SF_ENABLE_MAL
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.mal_pc,              (CHIP_PTR_TYPE)&pmDataMALPc,           0xFF,                PFVB_DATA+PFVB_NORST+PFVB_NOMAP,            ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.mal_state,           (CHIP_PTR_TYPE)&pmDataMALState,        0xFF,                PFVB_DATA+PFVB_NORST+PFVB_NOMAP,            ZERO},
	{PFVT_16BIT+(OUTPUT_MAX<<8),
			(CHIP_PTR_TYPE)&pf_data.mal_var,                  (CHIP_PTR_TYPE)&pmDataMALVar,          0xFFFF,              PFVB_DATA+PFVB_NORST+PFVB_NOMAP,            ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.mal_wait_cnt,        (CHIP_PTR_TYPE)&pmDataMALWaitCnt,      ZERO,                PFVB_DATA+PFVB_NORST+PFVB_NOMAP,            ZERO},

	{PFVT_16BIT+(MAL_FIRE_MAX<<8),
			(CHIP_PTR_TYPE)&pf_data.mal_fire,                 (CHIP_PTR_TYPE)&pmDataMALFire,         0xFFFF,              PFVB_DATA+PFVB_TRIG,            ZERO},
#endif

#ifdef SF_ENABLE_STV
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.stv_state,           (CHIP_PTR_TYPE)&pmDataSTVState,        STV_STATE_ERROR_MIN, PFVB_DATA+PFVB_NORST+PFVB_NOMAP,            STV_STATE_OKE},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.stv_wait_cnt,        (CHIP_PTR_TYPE)&pmDataSTVWaitCnt,      0xFF,                PFVB_DATA+PFVB_NORST+PFVB_NOMAP,            ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.stv_map_idx,         (CHIP_PTR_TYPE)&pmDataSTVMapIdx,       0xFF,                PFVB_DATA+PFVB_NORST+PFVB_NOMAP,            ZERO},
#endif

	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.req_tx_push,         (CHIP_PTR_TYPE)&pmDataTXPush,          ONE,                 PFVB_DATA+PFVB_NORST+PFVB_NOMAP+PFVB_PUSH,  ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.req_tx_echo,         (CHIP_PTR_TYPE)&pmDataTXEcho,          ONE,                 PFVB_DATA+PFVB_NORST+PFVB_NOMAP+PFVB_PUSH,  ONE},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.req_tx_promt,        (CHIP_PTR_TYPE)&pmDataTXPromt,         ONE,                 PFVB_DATA+PFVB_NORST+PFVB_NOMAP+PFVB_PUSH,  ONE},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.req_tx_hex,          (CHIP_PTR_TYPE)&pmDataTXHex,           ONE,                 PFVB_DATA+PFVB_NORST+PFVB_NOMAP+PFVB_PUSH,  ONE},

};

uint8_t Vars_getSize(void) {
	return sizeof(PF_VARS)/sizeof(CHIP_PTR_TYPE)/(PFVF_DEF+ONE);
}
uint16_t Vars_getBitsRaw(uint8_t idx) {
	return Chip_pgm_readWord(&(PF_VARS[idx][PFVF_BITS]));
}
uint16_t Vars_getValueMax(uint8_t idx) {
	return Chip_pgm_readWord(&(PF_VARS[idx][PFVF_MAX]));
}

boolean Vars_isTrigger(byte idx) {
	uint16_t bits = Chip_pgm_readWord(&(PF_VARS[idx][PFVF_BITS])) & PFVB_TRIG;
	if (bits > ZERO) {
		return true;
	}
	return false;
}

boolean Vars_isNolimit(byte idx) {
	uint16_t bits = Chip_pgm_readWord(&(PF_VARS[idx][PFVF_BITS])) & PFVB_SLIMIT;
	if (bits > ZERO) {
		return false;
	}
	return true;
}

boolean Vars_isIndexA(byte idx) {
	if (idx > PF_VARS_SIZE) {
		return false;
	}
	return Vars_getIndexAMax(idx) > ZERO;
}

boolean Vars_isIndexB(byte idx) {
	if (idx > PF_VARS_SIZE) {
		return false;
	}
	return Vars_getIndexBMax(idx) > ZERO;
}

boolean Vars_isNomap(byte idx) {
	if (idx > PF_VARS_SIZE) {
		return true; // cannot map idx out of range
	}
	uint16_t bits = Chip_pgm_readWord(&(PF_VARS[idx][PFVF_BITS])) & PFVB_NOMAP;
	if (bits > ZERO) {
		return true;
	}
	return false;
}

#ifdef SF_ENABLE_LCD
boolean Vars_isMenuSkip(byte idx) {
	uint16_t bits = Chip_pgm_readWord(&(PF_VARS[idx][PFVF_BITS])) & PFVB_NOMENU;
	if (bits > ZERO) {
		return true;
	}
	return false;
}
#endif

boolean Vars_isNoReset(byte idx) {
	uint16_t bits = Chip_pgm_readWord(&(PF_VARS[idx][PFVF_BITS])) & PFVB_NORST;
	if (bits > ZERO) {
		return true;
	}
	return false;
}

boolean Vars_isPush(byte idx) {
	uint16_t bits = Chip_pgm_readWord(&(PF_VARS[idx][PFVF_BITS])) & PFVB_PUSH;
	if (bits > ZERO) {
		return true;
	}
	return false;
}

boolean Vars_isTypeData(byte idx) {
	uint16_t bits = Chip_pgm_readWord(&(PF_VARS[idx][PFVF_BITS])) & PFVB_DATA;
	if (bits > ZERO) {
		return true;
	}
	return false;
}


volatile char* Vars_getName(uint8_t idx) {
	return UNPSTRA(&PF_VARS[idx][PFVF_NAME]);
}

uint8_t Vars_getBitType(byte idx) {
	return Chip_pgm_readWord(&(PF_VARS[idx][PFVF_TYPE])) & 0x0F;
}

uint8_t Vars_getIndexAMax(uint8_t idx) {
	uint16_t bits = Chip_pgm_readWord(&(PF_VARS[idx][PFVF_TYPE])) & 0xFF00;
	uint8_t result = (bits >> 8) & 0x1F; // only use 5 bits
	return result;
}

uint8_t Vars_getIndexBMax(uint8_t idx) {
	uint16_t bits = Chip_pgm_readWord(&(PF_VARS[idx][PFVF_TYPE])) & 0xFF00;
	uint8_t result = (bits >> 13);
	return result;
}

uint16_t Vars_getDefaultValue(uint8_t idx) {
	return Chip_pgm_readWord(&(PF_VARS[idx][PFVF_DEF]));
}

volatile char* UNPSTR_rm2(const char* dstring) {
	for (uint8_t i=ZERO;i < UNPSTR_BUFF_SIZE;i++) {
		pf_data.unpstr_buff_rm2[i]='\0'; // clean buffer
	}
	int index = ZERO;
	while (Chip_pgm_readByte(dstring) != 0x00) {
		uint8_t c = Chip_pgm_readByte(dstring++);
		pf_data.unpstr_buff_rm2[index]=c;
		index++;
	}
	return pf_data.unpstr_buff_rm2;
}

uint16_t Vars_getIndexFromName(volatile char* name) {
	for (uint8_t i=ZERO;i < PF_VARS_SIZE;i++) {
#ifdef SF_ENABLE_ARM_7M
		if (strcmp(name, (void*)Chip_pgm_readWord(&(PF_VARS[i][PFVF_NAME]))) == ZERO) {
#else
		if (strcmp(name, UNPSTR_rm2((void*)Chip_pgm_readWord(&(PF_VARS[i][PFVF_NAME])))) == ZERO) { // TODO: rm _P but not with UNPSTR
#endif
			return i;
		}
	}
	return QMAP_VAR_NONE;
}

uint16_t Vars_getIndexFromPtr(uint16_t* ptr) {
	for (uint8_t i=ZERO;i < PF_VARS_SIZE;i++) {
		if (ptr == (void*)Chip_pgm_readWord(&(PF_VARS[i][PFVF_VAR]))) {
			return i;
		}
	}
	return QMAP_VAR_NONE;
}

uint16_t Vars_getValue(uint8_t idx,uint8_t idxA,uint8_t idxB) {
#ifdef SF_ENABLE_DEBUG_HTX
	Debug_htx_c('g');
	Debug_htx_hex8(idx);
#endif
	boolean indexedA   = Vars_isIndexA(idx);
	boolean indexedB   = Vars_isIndexB(idx);
	uint8_t idxMaxA    = Vars_getIndexAMax(idx);
	uint8_t idxMaxB    = Vars_getIndexBMax(idx);
	uint16_t fieldType = Chip_pgm_readWord(&(PF_VARS[idx][PFVF_TYPE])) & 0x0F;
	uint16_t value = ZERO;
	if (indexedA && idxA>=idxMaxA) {
		idxA = ZERO; // safty check for indexes
	}
	if (indexedB && idxB>=idxMaxB) {
		idxB = ZERO;
	}
	if (fieldType == PFVT_16BIT) {
		uint16_t* valuePtr = (uint16_t*)Chip_pgm_readWord(&(PF_VARS[idx][PFVF_VAR]));
		if (indexedA) {
			if (indexedB) {
				valuePtr += idxMaxB * idxA + idxB;
			} else {
				valuePtr += idxA;
			}
		}
		value = *(valuePtr);
	} else if (fieldType == PFVT_8BIT) {
		uint8_t* valuePtr = (uint8_t*)Chip_pgm_readWord(&(PF_VARS[idx][PFVF_VAR]));
		//TODO: CHIP_PTR_TYPE *valuePtr = (CHIP_PTR_TYPE*)Chip_pgm_readWord(&(PF_VARS[idx][PFVF_VAR]));
		if (indexedA) {
			if (indexedB) {
				valuePtr += idxMaxB * idxA + idxB;
			} else {
				valuePtr += idxA;
			}
		}
		value = *(valuePtr);
	}
	return value;
}

uint32_t Vars_getValue32(uint8_t idx,uint8_t idxA) {
	boolean indexedA   = Vars_isIndexA(idx);
	uint8_t idxMaxA    = Vars_getIndexAMax(idx);
	uint32_t* valuePtr = (uint32_t*)Chip_pgm_readWord(&(PF_VARS[idx][PFVF_VAR]));
	if (indexedA && idxA>=idxMaxA) {
		idxA = ZERO; // safty check for indexes
	}
	if (indexedA) {
		valuePtr += idxA;
	}
	uint32_t value = *(valuePtr);
	return value;
}

#ifdef SF_ENABLE_VFC
uint8_t vfc_is_variable_mapped(uint8_t idx,uint8_t idxA) {
	for (uint8_t i=ZERO;i < VFC_MAP_MAX;i++) {
		uint16_t v = pf_conf.vfc_input_map[i][QMAP_VAR];
		if (v==QMAP_VAR_NONE) {
			continue;
		}
		if (v != idx) {
			continue;
		}
		if (Vars_isIndexA(idx)==false) {
			return i;
		}
		uint16_t vi = pf_conf.vfc_input_map[i][QMAP_VAR_IDX];
		if (vi == QMAP_VAR_IDX_ALL) {
			return i;
		}
		if (vi == idxA) {
			return i;
		}
	}
	return QMAP_VAR_IDX_ALL;
}
#endif

uint8_t sys_vvm_is_variable_locked(uint8_t idx,uint8_t idxA) {
	for (uint8_t i=ZERO;i < SYS_VVX_MAP_MAX;i++) {
		uint16_t v = pf_conf.sys_vvm_map[i][QMAP_VAR];
		if (v==QMAP_VAR_NONE) {
			continue;
		}
		if (v != idx) {
			continue;
		}
		if (Vars_isIndexA(idx)==false) {
			return i;
		}
		uint16_t vi = pf_conf.sys_vvm_map[i][QMAP_VAR_IDX];
		if (vi == QMAP_VAR_IDX_ALL) {
			return i;
		}
		if (vi == idxA) {
			return i;
		}
	}
	return QMAP_VAR_IDX_ALL;
}

uint8_t sys_vvl_is_variable_limited(uint8_t idx,uint8_t idxA) {
	for (uint8_t i=ZERO;i < SYS_VVX_MAP_MAX;i++) {
		uint16_t v = pf_conf.sys_vvl_map[i][QMAP_VAR];
		if (v==QMAP_VAR_NONE) {
			continue;
		}
		if (v != idx) {
			continue;
		}
		if (Vars_isIndexA(idx)==false) {
			return i;
		}
		uint16_t vi = pf_conf.sys_vvl_map[i][QMAP_VAR_IDX];
		if (vi == QMAP_VAR_IDX_ALL) {
			return i;
		}
		if (vi == idxA) {
			return i;
		}
	}
	return QMAP_VAR_IDX_ALL;
}



// removes the dubbel print is set is done with serial and req_auto_push==1 
uint16_t Vars_setValueSerial(uint8_t idx,uint8_t idxA,uint8_t idxB,uint16_t value) {
	return Vars_setValueImpl(idx,idxA,idxB,value,true,true,false);
}
// removes the external function calls in reset phase.
uint16_t Vars_setValueReset(uint8_t idx,uint8_t idxA,uint16_t value) {
	if (Vars_isIndexB(idx)) {
		for (uint8_t b=ZERO;b<Vars_getIndexBMax(idx);b++) {
			uint16_t valueSet = value;
			if (b>ZERO) {
				valueSet = ZERO;
			}
			Vars_setValueImpl(idx,idxA,b,valueSet,false,true,false); // TODO: fix disabled printing of idxB reset values.
		}
		return ZERO;
	} else {
		return Vars_setValueImpl(idx,idxA,(uint8_t)0,value,false,false,false);
	}
}
uint16_t Vars_setValueInt(uint8_t idx,uint8_t idxA,uint8_t idxB,uint16_t value) {
	return Vars_setValueImpl(idx,idxA,idxB,value,true,false,true);
}
uint16_t Vars_setValue(uint8_t idx,uint8_t idxA,uint8_t idxB,uint16_t value) {
	return Vars_setValueImpl(idx,idxA,idxB,value,true,false,false);
}

uint32_t Vars_setValue32(uint8_t idx,uint8_t idxA,uint8_t idxB,uint32_t value) {
	if (idx > PF_VARS_SIZE) {
		return value;
	}
	boolean indexedA   = Vars_isIndexA(idx);
	boolean indexedB   = Vars_isIndexB(idx);
	uint8_t idxMaxA    = Vars_getIndexAMax(idx);
	uint8_t idxMaxB    = Vars_getIndexBMax(idx);
	uint16_t fieldType = Chip_pgm_readWord(&(PF_VARS[idx][PFVF_TYPE])) & 0x0F;
	if (sys_vvm_is_variable_locked(idx,idxA) != QMAP_VAR_IDX_ALL) {
		return Vars_getValue32(idx,idxA); // locked
	}
	if (fieldType != PFVT_32BIT) {
		return ZERO;
	}
	uint32_t* valuePtr = (uint32_t*)Chip_pgm_readWord(&(PF_VARS[idx][PFVF_VAR]));
	if (indexedA) {
		if (indexedB) {
			valuePtr += idxMaxB * idxA + idxB;
		} else {
			valuePtr += idxA;
		}
		if (idxA != QMAP_VAR_IDX_ALL) {
			*valuePtr = value;
		} else {
			uint8_t i=ZERO;
			for (i=ZERO;i<idxMaxA;i++) {
				uint32_t *valuePtr = (uint32_t*)Chip_pgm_readWord(&(PF_VARS[idx][PFVF_VAR]));
				if (indexedB) {
					valuePtr += Vars_getIndexBMax(idx)*i + idxB;
				} else {
					valuePtr += i;
				}
				*valuePtr = value;
			}
		}
	} else {
		*valuePtr = value;
	}
	return value;
}

uint16_t Vars_setValueImpl(uint8_t idx,uint8_t idxA,uint8_t idxB,uint16_t value,boolean trig,boolean serial,boolean intBuff) {
	if (idx > PF_VARS_SIZE) {
		return value;
	}
#ifdef SF_ENABLE_DEBUG_HTX
	Debug_htx_c('s');
	Debug_htx_hex8(idx);
#endif
	if (sys_vvm_is_variable_locked(idx,idxA) != QMAP_VAR_IDX_ALL) {
		return Vars_getValue(idx,idxA,idxB); // locked
	}
	boolean indexedA   = Vars_isIndexA(idx);
	boolean indexedB   = Vars_isIndexB(idx);
	uint8_t idxMaxA    = Vars_getIndexAMax(idx);
	uint8_t idxMaxB    = Vars_getIndexBMax(idx);
	uint16_t fieldType = Chip_pgm_readWord(&(PF_VARS[idx][PFVF_TYPE])) & 0x0F;
	uint16_t value_max = Chip_pgm_readWord(&(PF_VARS[idx][PFVF_MAX]));
	uint16_t value_min = ZERO;
#ifdef SF_ENABLE_PWM
	if ( Chip_pgm_readWord(&(PF_VARS[idx][PFVF_NAME])) == (CHIP_PTR_TYPE)&pmConfPWMOnCntA) {
		value_min = ONE;
	}
	if ( Chip_pgm_readWord(&(PF_VARS[idx][PFVF_NAME])) == (CHIP_PTR_TYPE)&pmConfPWMOnCntB) {
		value_min = ONE;
	}
	if ( Chip_pgm_readWord(&(PF_VARS[idx][PFVF_NAME])) == (CHIP_PTR_TYPE)&pmConfPPMDataLength) {
		value_min = ONE;
	}
#endif

	// safty check for indexes
	if (indexedA && idxA>=idxMaxA && idxA != QMAP_VAR_IDX_ALL) {
		idxA = ZERO;
	}
	if (indexedB && idxB>=idxMaxB && idxA != QMAP_VAR_IDX_ALL) {
		idxB = ZERO;
	}

	if (value==0xFF) {
		value_max = ZERO; // big hack for swc_mode,stv_*_mode.
	}

	// User defined var limits
	uint8_t idxLimit = sys_vvl_is_variable_limited(idx,idxA);
	if (idxLimit != QMAP_VAR_IDX_ALL) {
		uint16_t limit = ZERO;
		limit = pf_conf.sys_vvl_map[idxLimit][QMAP_VALUE_A];
		if (limit > ZERO) {
			value_min = limit;
		}
		limit = pf_conf.sys_vvl_map[idxLimit][QMAP_VALUE_B];
		if (limit > ZERO) {
			value_max = limit;
		}
	}

	if (value_max != ZERO && value > value_max) {
		value = value_max;
	}
	if (value_min != ZERO && value < value_min) {
		value = value_min;
	}

	// Set value
	if (fieldType == PFVT_16BIT) {
		uint16_t* valuePtr = (uint16_t*)Chip_pgm_readWord(&(PF_VARS[idx][PFVF_VAR]));
		if (indexedA) {
			if (indexedB) {
				valuePtr += idxMaxB * idxA + idxB;
			} else {
				valuePtr += idxA;
			}
			if (idxA != QMAP_VAR_IDX_ALL) {
				*valuePtr = value;
			} else {
				uint8_t i=ZERO;
				for (i=ZERO;i<idxMaxA;i++) {
					uint16_t *valuePtr = (uint16_t*)Chip_pgm_readWord(&(PF_VARS[idx][PFVF_VAR]));
					if (indexedB) {
						valuePtr += Vars_getIndexBMax(idx)*i + idxB;
					} else {
						valuePtr += i;
					}
					*valuePtr = value;
				}
			}
		} else {
			*valuePtr = value;
		}
	} else if (fieldType == PFVT_8BIT) {
		uint8_t* valuePtr = (uint8_t*)Chip_pgm_readWord(&(PF_VARS[idx][PFVF_VAR]));
		if (indexedA) {
			if (indexedB) {
				valuePtr += idxMaxB * idxA + idxB;
			} else {
				valuePtr += idxA;
			}
			if (idxA != QMAP_VAR_IDX_ALL) {
				*valuePtr = value;
			} else {
				uint8_t i=ZERO;
				for (i=ZERO;i<idxMaxA;i++) {
					uint8_t *valuePtr = (uint8_t*)Chip_pgm_readWord(&(PF_VARS[idx][PFVF_VAR]));
					if (indexedB) {
						valuePtr += idxMaxB * i + idxB;
					} else {
						valuePtr += i;
					}
					*valuePtr = value;
				}
			}
		} else {
			*valuePtr = value;
		}
	} else if (fieldType == PFVT_32BIT) {
		return value; // use setValue32
	}

	// Send to serial if push is on
	if (pf_data.req_tx_push == ONE && serial==false) {
	//	if (intBuff==false) {
	//		Serial_printVar(idx,idxA,value);
	//	} else {
			for (uint8_t i=ZERO;i < VARS_INT_NUM_SIZE;i++) {
				uint16_t intIdx = pf_data.vars_int_buff[i][0];
				if ((intIdx == 0xFFFF) | (intIdx == idx)) {
					pf_data.vars_int_buff[i][0] = idx;
					pf_data.vars_int_buff[i][1] = idxA;
					pf_data.vars_int_buff[i][2] = value;
					break;
				}
			}
			if (intBuff==true) {
				return value; // done setting int buff
			}
	//	}
	}

	// Some fields require extra update code;
	uint16_t varName = Chip_pgm_readWord(&(PF_VARS[idx][PFVF_NAME]));

#ifdef SF_ENABLE_AVR
	if ( varName == (uint16_t)&pmConfAVRPin2Map) {
		if (pf_conf.avr_pin2_map == PIN2_INT0_IN) {
			Chip_in_int_pin(ZERO,ZERO);
		} else {
			Chip_in_int_pin(ZERO,ONE);
		}
	}
	if ( varName == (uint16_t)&pmConfAVRPin3Map) {
		if (pf_conf.avr_pin3_map == PIN3_INT1_IN) {
			Chip_in_int_pin(ONE,ZERO);
		} else {
			Chip_in_int_pin(ONE,ONE);
		}
	}
#endif
#ifdef SF_ENABLE_AVR_MEGA
	if ( varName == (uint16_t)&pmConfInt0Mode) {
		if (value > ZERO) {
			Chip_in_int_pin(ZERO,ZERO);
		} else {
			Chip_in_int_pin(ZERO,ONE); // on mega turn int off when mode is off.
		}
	}
	if ( varName == (uint16_t)&pmConfInt0Mode) {
		if (value > ZERO) {
			Chip_in_int_pin(ONE,ZERO);
		} else {
			Chip_in_int_pin(ONE,ONE);
		}
	}
#endif
#ifdef SF_ENABLE_SPI
	if ( varName == (CHIP_PTR_TYPE)&pmConfSpiClock)  {	Chip_reg_set(CHIP_REG_SPI_CLOCK,value);		}
#endif
#ifdef SF_ENABLE_PWM
	if ( varName == (CHIP_PTR_TYPE)&pmConfPWMClock)  {	Chip_reg_set(CHIP_REG_PWM_CLOCK,value);		}
#endif
#ifdef SF_ENABLE_CIP
	if ( varName == (CHIP_PTR_TYPE)&pmConfCip0Clock) {	Chip_reg_set(CHIP_REG_CIP0_CLOCK,value);	}
	if ( varName == (CHIP_PTR_TYPE)&pmConfCip0Mode)  {	Chip_reg_set(CHIP_REG_CIP0_MODE,value);		}
	if ( varName == (CHIP_PTR_TYPE)&pmConfCip0aOcr)  {	Chip_reg_set(CHIP_REG_CIP0_OCR_A,value);	}
	if ( varName == (CHIP_PTR_TYPE)&pmConfCip0aCom)  {	Chip_reg_set(CHIP_REG_CIP0_COM_A,value);	}
	if ( varName == (CHIP_PTR_TYPE)&pmConfCip0bOcr)  {	Chip_reg_set(CHIP_REG_CIP0_OCR_B,value);	}
	if ( varName == (CHIP_PTR_TYPE)&pmConfCip0bCom)  {	Chip_reg_set(CHIP_REG_CIP0_COM_B,value);	}
	if ( varName == (CHIP_PTR_TYPE)&pmConfCip0cOcr)  {	Chip_reg_set(CHIP_REG_CIP0_OCR_C,value);	}
	if ( varName == (CHIP_PTR_TYPE)&pmConfCip0cCom)  {	Chip_reg_set(CHIP_REG_CIP0_COM_C,value);	}

	if ( varName == (CHIP_PTR_TYPE)&pmConfCip1Clock) {	Chip_reg_set(CHIP_REG_CIP1_CLOCK,value);	}
	if ( varName == (CHIP_PTR_TYPE)&pmConfCip1Mode)  {	Chip_reg_set(CHIP_REG_CIP1_MODE,value);		}
	if ( varName == (CHIP_PTR_TYPE)&pmConfCip1aOcr)  {	Chip_reg_set(CHIP_REG_CIP1_OCR_A,value);	}
	if ( varName == (CHIP_PTR_TYPE)&pmConfCip1aCom)  {	Chip_reg_set(CHIP_REG_CIP1_COM_A,value);	}
	if ( varName == (CHIP_PTR_TYPE)&pmConfCip1bOcr)  {	Chip_reg_set(CHIP_REG_CIP1_OCR_B,value);	}
	if ( varName == (CHIP_PTR_TYPE)&pmConfCip1bCom)  {	Chip_reg_set(CHIP_REG_CIP1_COM_B,value);	}
	if ( varName == (CHIP_PTR_TYPE)&pmConfCip1cOcr)  {	Chip_reg_set(CHIP_REG_CIP1_OCR_C,value);	}
	if ( varName == (CHIP_PTR_TYPE)&pmConfCip1cCom)  {	Chip_reg_set(CHIP_REG_CIP1_COM_C,value);	}

	if ( varName == (CHIP_PTR_TYPE)&pmConfCip2Clock) {	Chip_reg_set(CHIP_REG_CIP2_CLOCK,value);	}
	if ( varName == (CHIP_PTR_TYPE)&pmConfCip2Mode)  {	Chip_reg_set(CHIP_REG_CIP2_MODE,value);		}
	if ( varName == (CHIP_PTR_TYPE)&pmConfCip2aOcr)  {	Chip_reg_set(CHIP_REG_CIP2_OCR_A,value);	}
	if ( varName == (CHIP_PTR_TYPE)&pmConfCip2aCom)  {	Chip_reg_set(CHIP_REG_CIP2_COM_A,value);	}
	if ( varName == (CHIP_PTR_TYPE)&pmConfCip2bOcr)  {	Chip_reg_set(CHIP_REG_CIP2_OCR_B,value);	}
	if ( varName == (CHIP_PTR_TYPE)&pmConfCip2bCom)  {	Chip_reg_set(CHIP_REG_CIP2_COM_B,value);	}
	if ( varName == (CHIP_PTR_TYPE)&pmConfCip2cOcr)  {	Chip_reg_set(CHIP_REG_CIP2_OCR_C,value);	}
	if ( varName == (CHIP_PTR_TYPE)&pmConfCip2cCom)  {	Chip_reg_set(CHIP_REG_CIP2_COM_C,value);	}
#endif
	if (trig==false) {
		return value; // no update of triggers
	}

#ifdef SF_ENABLE_STV
	uint8_t stvIdxMax = Stv_is_variable_mapped(idx,idxA,true);
	uint8_t stvIdxMin = Stv_is_variable_mapped(idx,idxA,false);
	if (stvIdxMax != QMAP_VAR_IDX_ALL) { Stv_vars_max(value,stvIdxMax);	}
	if (stvIdxMin != QMAP_VAR_IDX_ALL) { Stv_vars_min(value,stvIdxMin);	}
#endif

#ifdef SF_ENABLE_PWM
	uint16_t bits = Chip_pgm_readWord(&(PF_VARS[idx][PFVF_BITS])) & PFVB_CPWM;
	if (bits > ZERO) {
		PWM_calc_data();
	}
	if ( varName == (CHIP_PTR_TYPE)&pmDataPulseFire && value > ZERO) {
		PWM_pulsefire();
	}
	if ( varName == (CHIP_PTR_TYPE)&pmDataPulseHoldFire && value > ZERO) {
		if (pf_data.pwm_state != PWM_STATE_FIRE_HOLD) {
			pf_data.pwm_state = PWM_STATE_FIRE_HOLD;
			if (pf_conf.pulse_hold_mode == PULSE_HOLD_MODE_CLEAR || pf_conf.pulse_hold_mode == PULSE_HOLD_MODE_ZERO_CLR) {
				PWM_send_output(PULSE_DATA_OFF);
			}
			if ( pf_conf.pulse_hold_mode >= PULSE_HOLD_MODE_ZERO ) {
				pf_data.pulse_step = ZERO; // goto step zero
			}
			for (uint8_t i=0;i<FIRE_MAP_MAX;i++) {
				uint16_t v = pf_conf.pulse_hold_map[i][QMAP_VAR];
				if (v==QMAP_VAR_NONE) { continue; }
				Vars_setValueImpl(v,pf_conf.pulse_hold_map[i][QMAP_VAR_IDX],ZERO,pf_conf.pulse_hold_map[i][QMAP_VALUE_A],trig,serial,intBuff);  // recursive function !!
			}
		}
	}
	if ( varName == (CHIP_PTR_TYPE)&pmDataPulseResumeFire && value > ZERO) {
		if (pf_data.pwm_state == PWM_STATE_FIRE_HOLD) {
			if (pf_data.pulse_step == ZERO && pf_conf.pulse_hold_auto == pf_data.pwm_data_size) {
				pf_data.pwm_state = PWM_STATE_FIRE_END; // special case for auto holding on last step.
			} else {
				pf_data.pwm_state = PWM_STATE_RUN;
			}
			pf_data.pulse_hold_fire = ZERO;
			Chip_reg_set(CHIP_REG_PWM_OCR_A,ONE);
			Chip_reg_set(CHIP_REG_PWM_TCNT,ZERO);
			for (uint8_t i=0;i<FIRE_MAP_MAX;i++) {
				uint16_t v = pf_conf.pulse_resume_map[i][QMAP_VAR];
				if (v==QMAP_VAR_NONE) { continue; }
				Vars_setValueImpl(v,pf_conf.pulse_resume_map[i][QMAP_VAR_IDX],ZERO,pf_conf.pulse_resume_map[i][QMAP_VALUE_A],trig,serial,intBuff);  // recursive function !!
			}
		}
	}
	if ( varName == (CHIP_PTR_TYPE)&pmDataPulseResetFire && value > ZERO) {
		pf_data.pulse_fire           = ZERO;
		pf_data.pulse_hold_fire      = ZERO;
		pf_data.pulse_resume_fire    = ZERO;
		pf_data.pwm_state            = PWM_STATE_FIRE_RESET;
		pf_data.pulse_step           = ZERO; // goto step zero
		PWM_send_output(PULSE_DATA_OFF);
		for (uint8_t i=0;i<FIRE_MAP_MAX;i++) {
			uint16_t v = pf_conf.pulse_reset_map[i][QMAP_VAR];
			if (v==QMAP_VAR_NONE) { continue; }
			Vars_setValueImpl(v,pf_conf.pulse_reset_map[i][QMAP_VAR_IDX],ZERO,pf_conf.pulse_reset_map[i][QMAP_VALUE_A],trig,serial,intBuff);  // recursive function !!
		}
	}
	if ( varName == (CHIP_PTR_TYPE)&pmConfPWMReqIdx)  { Freq_requestTrainFreq(); }
	if ( varName == (CHIP_PTR_TYPE)&pmConfPWMReqFreq) { Freq_requestTrainFreq(); }
	if ( varName == (CHIP_PTR_TYPE)&pmConfPWMReqDuty) { Freq_requestTrainFreq(); }
#endif
#ifdef SF_ENABLE_MAL
	if ( varName == (CHIP_PTR_TYPE)&pmDataMALFire) { Mal_fire(idxA); }
#endif
#ifdef SF_ENABLE_VFC
	uint8_t vfcIdx = vfc_is_variable_mapped(idx,idxA);
	if (vfcIdx != QMAP_VAR_IDX_ALL) {
		uint16_t outVar   = pf_conf.vfc_output_map[vfcIdx][QMAP_VAR];
		uint16_t minLevel = pf_conf.vfc_input_map[vfcIdx][QMAP_VALUE_A];
		uint16_t maxLevel = pf_conf.vfc_input_map[vfcIdx][QMAP_VALUE_B];
		if (value > minLevel && value < maxLevel && outVar!=QMAP_VAR_NONE && outVar!=idx) {
			uint16_t minMapLevel = pf_conf.vfc_output_map[vfcIdx][QMAP_VALUE_A];
			uint16_t maxMapLevel = pf_conf.vfc_output_map[vfcIdx][QMAP_VALUE_B];
			uint16_t valueVfc = map_value(value,minLevel,maxLevel,minMapLevel,maxMapLevel);
			Vars_setValueImpl(outVar,pf_conf.vfc_output_map[vfcIdx][QMAP_VAR_IDX],ZERO,valueVfc,trig,serial,intBuff);  // recursive function !!
		}
	}
#endif
	return value; // return corrected value
}

void Vars_readConfig(void) {
#if defined(SF_ENABLE_AVR) | defined(SF_ENABLE_AVR_MEGA)
	Chip_eeprom_read((void*)&pf_conf_eeprom);
#endif
}
void Vars_writeConfig(void) {
#if defined(SF_ENABLE_AVR) | defined(SF_ENABLE_AVR_MEGA)
	Chip_eeprom_write((void*)&pf_conf_eeprom);
#endif
}

// Init all config and data to init state.
void Vars_resetConfig(void) {  
	// Reset all config values
	for (uint8_t i=ZERO;i < PF_VARS_SIZE;i++) {
		if (Vars_isTypeData(i)) {
			continue;
		}
		Vars_setValueReset(i,QMAP_VAR_IDX_ALL,Vars_getDefaultValue(i));
#ifdef SF_ENABLE_DEBUG
		Serial_printChar("ResetConf: ");
		Serial_printChar(UNPSTRA(&PF_VARS[i][PFVF_NAME]));
		Serial_printChar(" to: ");
		Serial_printDec(Vars_getDefaultValue(i));
		Serial_printChar(" setto: ");
		Serial_printDec(Vars_getValue(i,0,0));
		Serial_println();
#endif
	}

	pf_conf.sys_version          = PULSE_FIRE_VERSION;
	pf_conf.sys_struct_size      = sizeof(pf_conf_struct);
	pf_conf.sys_id               = ZERO;// TODO: add 32b reset data..
	pf_conf.sys_pass             = ZERO;

#ifdef SF_ENABLE_PWM
	for (uint8_t i=ZERO;i < OUTPUT_MAX;i++) {
		pf_conf.ppm_data_a[i]      = (i << 8) + i + DEFAULT_PPM_DATA; // example demo data
		pf_conf.ppm_data_b[i]      = OUTPUT_MAX-i + DEFAULT_PPM_DATA;
	}
#endif
#ifdef SF_ENABLE_MAL
	for (uint16_t addr=ZERO;addr < MAL_CODE_SIZE;addr++) {
		pf_conf.mal_code[addr] = 0xFF;
	}
	for (uint8_t trigIdx=ZERO;trigIdx < MAL_FIRE_MAX;trigIdx++) {
		uint16_t addr = trigIdx*4;
		pf_conf.mal_code[addr+0] = 0x40;
		pf_conf.mal_code[addr+1] = 0x20;
		pf_conf.mal_code[addr+2] = 0x00;
		pf_conf.mal_code[addr+3] = 4*MAL_FIRE_MAX; // add default jump table to next line after table
	}
#endif
}

// Reset runtime data
void Vars_resetData(void) {
	// Reset all data values
	for (uint8_t i=ZERO;i < PF_VARS_SIZE;i++) {
		if (Vars_isTypeData(i)==false) {
			continue;
		}
		if (Vars_isNoReset(i)) {
			continue;
		}
		Vars_setValueReset(i,QMAP_VAR_IDX_ALL,Vars_getDefaultValue(i));
#ifdef SF_ENABLE_DEBUG
		Serial_printCharP(PSTR("ResetData: "));
		Serial_printChar(UNPSTRA(&PF_VARS[i][PFVF_NAME]));
		Serial_printChar(" to: ");
		Serial_printDec(Vars_getDefaultValue(i));
		Serial_println();
#endif
	}
#ifdef SF_ENABLE_LCD
	pf_data.lcd_page = pf_conf.lcd_defp;	// Set default lcd page.
#endif

#ifdef SF_ENABLE_PWM
	if (pf_conf.pulse_trig == PULSE_TRIG_LOOP) {
		pf_data.pwm_state = PWM_STATE_RUN;
	} else {
		pf_data.pwm_state = PWM_STATE_IDLE; // Reset pwm to idle when in trigger mode.
	}
#endif
}

// Setup all interal variables to init state
void Vars_setup(void) {
#ifdef SF_ENABLE_DEBUG
	Serial_printCharP(PSTR("Vars_setup begin."));
	Serial_println();
#endif
	// Read or reset config to init state
	Vars_readConfig(); // First read config from eeprom then check if valid for this version
	if (pf_conf.sys_version != PULSE_FIRE_VERSION || pf_conf.sys_struct_size != sizeof(pf_conf_struct)) {
		Vars_resetConfig(); // if newer/other/none version and/or size in flash then reset all to defaults and save.
		Vars_writeConfig();
	}

	// Reset data to init state
	Vars_resetData();

#ifdef SF_ENABLE_DEBUG
	Serial_printCharP(PSTR("Clear prog data."));
	Serial_println();
#endif
	// Reset prog to init state
	pf_data.sys_loop0_cnt        = ZERO;
	pf_data.sys_loop1_cnt        = ZERO;
	pf_data.sys_loop1_cnt_idx    = ZERO;
	pf_data.req_tx_push          = ZERO;
	pf_data.req_tx_echo          = ONE;
	pf_data.req_tx_promt         = ONE;
	pf_data.cmd_buff_idx         = ZERO;
#ifdef SF_ENABLE_LCD
	pf_data.lcd_menu_state       = LCD_MENU_STATE_OFF;
	pf_data.lcd_menu_mul         = ONE;
	pf_data.lcd_menu_idx         = ZERO;
	pf_data.lcd_menu_value_idx   = ZERO;
	pf_data.lcd_menu_time_cnt    = ZERO;
#endif
#ifdef SF_ENABLE_MAL
	pf_data.mal_pc               = ZERO;
	pf_data.mal_state            = MAL_STATE_IDLE;
	for (uint8_t i=ZERO;i < OUTPUT_MAX;i++) {
		pf_data.mal_var[i]         = ZERO;
	}
#endif
#ifdef SF_ENABLE_STV
	pf_data.stv_state            = STV_STATE_OKE;
	pf_data.stv_wait_cnt         = ZERO;
	pf_data.stv_map_idx          = ZERO;
#endif
	for (uint8_t i=ZERO;i < VARS_INT_NUM_SIZE;i++) {
		pf_data.vars_int_buff[i][0] = 0xFFFF;
		pf_data.vars_int_buff[i][1] = ZERO;
		pf_data.vars_int_buff[i][2] = ZERO;
	}
#ifdef SF_ENABLE_PWM
	PWM_calc_data();
#endif
	pf_data.idx_adc_value = Vars_getIndexFromName(UNPSTR(pmDataAdcValue));
	pf_data.idx_dic_value = Vars_getIndexFromName(UNPSTR(pmDataDicValue));
#ifdef SF_ENABLE_DEBUG
	Serial_printCharP(PSTR("Vars_setup end."));
	Serial_println();
#endif
}

void Vars_loop(void) {
	for (uint8_t i=ZERO;i < VARS_INT_NUM_SIZE;i++) {
		uint16_t idx = pf_data.vars_int_buff[i][0];
		if (idx == 0xFFFF) {
			continue;
		}
#ifdef SF_ENABLE_DEBUG_HTX
		Debug_htx_c('v');
		Debug_htx_hex8(idx);
#endif
		// copy data
		uint16_t idxA  = pf_data.vars_int_buff[i][1];
		uint16_t value = pf_data.vars_int_buff[i][2];
		// free buffer row
		//asm volatile("" : : : "memory");
		pf_data.vars_int_buff[i][0] = 0xFFFF; // onyl non-0xFFFF value is free so this shuold be int save.
		// print data
		Serial_printVar(idx,idxA,value);
#ifdef SF_ENABLE_DEBUG_HTX
		Debug_htx_c('!');
#endif
	}
}


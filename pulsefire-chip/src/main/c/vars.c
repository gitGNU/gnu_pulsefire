
#include "vars.h"

pf_data_struct       pf_data;
pf_prog_struct       pf_prog;
pf_conf_struct       pf_conf;
#if defined(SF_ENABLE_AVR) | defined(SF_ENABLE_AVR_MEGA)
pf_conf_struct EEMEM pf_conf_eeprom;
#endif

CHIP_PROGMEM_ARRAY pmCmdList[PMCMDLIST_SIZE] CHIP_PROGMEM = {
		pmCmdHelp,pmCmdSave,
		pmCmdInfoConf,pmCmdInfoData,pmCmdInfoProg,pmCmdInfoFreq,pmCmdInfoPPM,pmCmdInfoChip,
		pmCmdResetConfig,pmCmdResetData,pmCmdResetChip,
		pmCmdReqPulseFire,pmCmdReqPWMFreq,pmCmdReqAutoLPM,
		pmProgTXPush,pmProgTXEcho,pmProgTXPromt,
		pmConfMALProgram
};

/*
PF variable fields metadata:
0 = Variable Type
    0 = Disabled
    1 = uint8_t
    2 = uint16_t
    3 = uint32_t
1 = Pointer to variable in struct
2 = Pointer to ascii name of variable
3 = Max value
4 = Bitfield,
    0 = index0
    1 = index1
    2 = Remove from menu
    3 = No mapping
    4 = No limiting to configed steps
    5 = Trigger variable
    6&7 = Data type (0=conf,1=data,2=prog,3=reserved)
    8-12 = IndexA max
    13,14,15 = Index B max
5 = Default value

*/
// PFVT_TYPE,  VARIALBE_POINTER,                       ASCII_POINTER,                    MAX_VALUE,            VARIABLE_BITS,                  DEFAULT_VALUE
const CHIP_PTR_TYPE PF_VARS[PF_VARS_PF_SIZE+PF_VARS_AVR_SIZE+PF_VARS_AVR_MEGA_SIZE+
  PF_VARS_PWM_SIZE+PF_VARS_LCD_SIZE+PF_VARS_LPM_SIZE+
  PF_VARS_PPM_SIZE+PF_VARS_ADC_SIZE+PF_VARS_DIC_SIZE+
  PF_VARS_DOC_SIZE+PF_VARS_DEV_SIZE+PF_VARS_PTC_SIZE+
  PF_VARS_PTT_SIZE+PF_VARS_STV_SIZE+PF_VARS_VFC_SIZE+
  PF_VARS_MAL_SIZE+PF_VARS_SWC_SIZE][PFVF_DEF+ONE] CHIP_PROGMEM = {

#ifdef SF_ENABLE_PWM
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.pulse_enable,        (CHIP_PTR_TYPE)&pmConfPulseEnable,     ONE,                  PFVB_NONE,                      ONE},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.pulse_mode,          (CHIP_PTR_TYPE)&pmConfPulseMode,       PULSE_MODE_PPMI,      PFVB_NONE,                      PULSE_MODE_TRAIN},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.pulse_steps,         (CHIP_PTR_TYPE)&pmConfPulseSteps,
	#if defined(SF_ENABLE_EXT_OUT_16BIT)
		OUTPUT_MAX,
	#elif defined(SF_ENABLE_EXT_OUT)
		8,
	#elif defined(SF_ENABLE_EXT_LCD)
		3,
	#else
		6,
	#endif
	                                                                                                                      PFVB_NONE,                      DEFAULT_PULSE_STEPS},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.pulse_trig,          (CHIP_PTR_TYPE)&pmConfPulseTrig,       PULSE_TRIG_EXT,      PFVB_NONE,                      PULSE_TRIG_LOOP},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.pulse_dir,           (CHIP_PTR_TYPE)&pmConfPulseDir,        PULSE_DIR_LRRL,      PFVB_NONE,                      ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.pulse_bank,          (CHIP_PTR_TYPE)&pmConfPulseBank,       ALL_BANK_MAX,        PFVB_NONE,                      ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.pulse_inv,           (CHIP_PTR_TYPE)&pmConfPulseInv,        ONE,                 PFVB_NONE,                      ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.pulse_trig_delay,    (CHIP_PTR_TYPE)&pmConfPulseTrigDelay,  0xFFFF,              PFVB_NONE,                      ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.pulse_post_delay,    (CHIP_PTR_TYPE)&pmConfPulsePostDelay,  0xFFFF,              PFVB_NONE,                      ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.pulse_mask_a,        (CHIP_PTR_TYPE)&pmConfPulseMaskA,      0xFFFF,              PFVB_NONE,                      PULSE_DATA_ON},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.pulse_mask_b,        (CHIP_PTR_TYPE)&pmConfPulseMaskB,      0xFFFF,              PFVB_NONE,                      PULSE_DATA_ON},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.pulse_init_a,        (CHIP_PTR_TYPE)&pmConfPulseInitA,      0xFFFF,              PFVB_NONE,                      DEFAULT_PULSE_DATA_INIT},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.pulse_init_b,        (CHIP_PTR_TYPE)&pmConfPulseInitB,      0xFFFF,              PFVB_NONE,                      DEFAULT_PULSE_DATA_INIT},

	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.pwm_on_cnt_a,        (CHIP_PTR_TYPE)&pmConfPWMOnCntA,       0xFFFF,              (OUTPUT_MAX<<8)+PFVB_IDXA,      DEFAULT_PWM_ON_CNT},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.pwm_on_cnt_b,        (CHIP_PTR_TYPE)&pmConfPWMOnCntB,       0xFFFF,              (OUTPUT_MAX<<8)+PFVB_IDXA,      DEFAULT_PWM_ON_CNT},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.pwm_off_cnt_a,       (CHIP_PTR_TYPE)&pmConfPWMOffCntA,      0xFFFF,              (OUTPUT_MAX<<8)+PFVB_IDXA,      ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.pwm_off_cnt_b,       (CHIP_PTR_TYPE)&pmConfPWMOffCntB,      0xFFFF,              (OUTPUT_MAX<<8)+PFVB_IDXA,      ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.pwm_tune_cnt,        (CHIP_PTR_TYPE)&pmConfPWMTuneCnt,      0xFFFF,              (OUTPUT_MAX<<8)+PFVB_IDXA,      ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.pwm_loop,            (CHIP_PTR_TYPE)&pmConfPWMLoop,         0xFF,                PFVB_NONE,                      DEFAULT_PWM_LOOP},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.pwm_loop_delta,      (CHIP_PTR_TYPE)&pmConfPWMLoopDelta,    0xFF,                PFVB_NONE,                      ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.pwm_clock,           (CHIP_PTR_TYPE)&pmConfPWMClock,        CLOCK_VALUE_MAX,     PFVB_NONE,                      DEFAULT_PWM_CLOCK},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.pwm_duty,            (CHIP_PTR_TYPE)&pmConfPWMDuty,         110,                 PFVB_NONE,                      ZERO},
#endif

#ifdef SF_ENABLE_PPM
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.ppm_data_offset,     (CHIP_PTR_TYPE)&pmConfPPMDataOffset,   OUTPUT_MAX-ONE,      PFVB_NONE,                      ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.ppm_data_length,     (CHIP_PTR_TYPE)&pmConfPPMDataLength,   OUTPUT_MAX,          PFVB_NONE,                      OUTPUT_MAX},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.ppm_data_a,          (CHIP_PTR_TYPE)&pmConfPPMDataA,        0xFFFF,              (OUTPUT_MAX<<8)+PFVB_IDXA,      ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.ppm_data_b,          (CHIP_PTR_TYPE)&pmConfPPMDataB,        0xFFFF,              (OUTPUT_MAX<<8)+PFVB_IDXA,      ZERO},
#endif
#ifdef SF_ENABLE_LPM
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.lpm_start,           (CHIP_PTR_TYPE)&pmConfLPMStart,        0xFFFF,              PFVB_NONE,                      DEFAULT_LPM_START},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.lpm_stop,            (CHIP_PTR_TYPE)&pmConfLPMStop,         0xFFFF,              PFVB_NONE,                      DEFAULT_LPM_STOP},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.lpm_size,            (CHIP_PTR_TYPE)&pmConfLPMSize,         0xFFFF,              PFVB_NONE,                      ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.lpm_relay_inv,       (CHIP_PTR_TYPE)&pmConfLPMRelayInv,     ONE,                 PFVB_NONE,                      ZERO},
#endif
#ifdef SF_ENABLE_PTC
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.ptc_0run,            (CHIP_PTR_TYPE)&pmConfPTC0Run,         PTC_RUN_LOOP,        PFVB_NONE,                      PTC_RUN_OFF},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.ptc_1run,            (CHIP_PTR_TYPE)&pmConfPTC1Run,         PTC_RUN_LOOP,        PFVB_NONE,                      PTC_RUN_OFF},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.ptc_0mul,            (CHIP_PTR_TYPE)&pmConfPTC0Mul,         0xFF,                PFVB_NONE,                      ONE},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.ptc_1mul,            (CHIP_PTR_TYPE)&pmConfPTC1Mul,         0xFF,                PFVB_NONE,                      ONE},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.ptc_0map,            (CHIP_PTR_TYPE)&pmConfPTC0Map,         0xFFFF,              (QMAP_SIZE<<13)+(PTC_TIME_MAP_MAX<<8)+PFVB_IDXB+PFVB_IDXA+PFVB_NOLIMIT+PFVB_NOMAP+PFVB_NOMENU,  0xFFFF},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.ptc_1map,            (CHIP_PTR_TYPE)&pmConfPTC1Map,         0xFFFF,              (QMAP_SIZE<<13)+(PTC_TIME_MAP_MAX<<8)+PFVB_IDXB+PFVB_IDXA+PFVB_NOLIMIT+PFVB_NOMAP+PFVB_NOMENU,  0xFFFF},
#endif
#ifdef SF_ENABLE_PTT
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.ptt_0map,            (CHIP_PTR_TYPE)&pmConfPTT0Map,         0xFFFF,              (QMAP_SIZE<<13)+(PTT_TRIG_MAP_MAX<<8)+PFVB_IDXB+PFVB_IDXA+PFVB_NOLIMIT+PFVB_NOMAP+PFVB_NOMENU,  0xFFFF},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.ptt_1map,            (CHIP_PTR_TYPE)&pmConfPTT1Map,         0xFFFF,              (QMAP_SIZE<<13)+(PTT_TRIG_MAP_MAX<<8)+PFVB_IDXB+PFVB_IDXA+PFVB_NOLIMIT+PFVB_NOMAP+PFVB_NOMENU,  0xFFFF},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.ptt_2map,            (CHIP_PTR_TYPE)&pmConfPTT2Map,         0xFFFF,              (QMAP_SIZE<<13)+(PTT_TRIG_MAP_MAX<<8)+PFVB_IDXB+PFVB_IDXA+PFVB_NOLIMIT+PFVB_NOMAP+PFVB_NOMENU,  0xFFFF},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.ptt_3map,            (CHIP_PTR_TYPE)&pmConfPTT3Map,         0xFFFF,              (QMAP_SIZE<<13)+(PTT_TRIG_MAP_MAX<<8)+PFVB_IDXB+PFVB_IDXA+PFVB_NOLIMIT+PFVB_NOMAP+PFVB_NOMENU,  0xFFFF},
#endif
#ifdef SF_ENABLE_DEV
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.dev_volt_dot,        (CHIP_PTR_TYPE)&pmProgDevVoltDot,      DEV_DOT_10000,       PFVB_NOMAP,                     ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.dev_amp_dot,         (CHIP_PTR_TYPE)&pmProgDevAmpDot,       DEV_DOT_10000,       PFVB_NOMAP,                     ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.dev_temp_dot,        (CHIP_PTR_TYPE)&pmProgDevTempDot,      DEV_DOT_10000,       PFVB_NOMAP,                     ZERO},
#endif
#ifdef SF_ENABLE_STV
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.stv_warn_secs,       (CHIP_PTR_TYPE)&pmConfSTVWarnSecs,     0xFF,                PFVB_NOMAP+PFVB_NOMENU,         ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.stv_warn_mode,       (CHIP_PTR_TYPE)&pmConfSTVWarnMode,     PULSE_MODE_PPMI,     PFVB_NOMAP+PFVB_NOMENU,         PULSE_MODE_OFF},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.stv_error_secs,      (CHIP_PTR_TYPE)&pmConfSTVErrorSecs,    0xFF,                PFVB_NOMAP+PFVB_NOMENU,         ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.stv_error_mode,      (CHIP_PTR_TYPE)&pmConfSTVErrorMode,    PULSE_MODE_PPMI,     PFVB_NOMAP+PFVB_NOMENU,         PULSE_MODE_OFF},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.stv_max_map,         (CHIP_PTR_TYPE)&pmConfSTVMaxMap,       0xFFFF,              (QMAP_SIZE<<13)+(STV_MAX_MAP_MAX<<8)+PFVB_IDXB+PFVB_IDXA+PFVB_NOLIMIT+PFVB_NOMAP+PFVB_NOMENU,0xFFFF},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.stv_min_map,         (CHIP_PTR_TYPE)&pmConfSTVMinMap,       0xFFFF,              (QMAP_SIZE<<13)+(STV_MIN_MAP_MAX<<8)+PFVB_IDXB+PFVB_IDXA+PFVB_NOLIMIT+PFVB_NOMAP+PFVB_NOMENU,0xFFFF},
#endif
#ifdef SF_ENABLE_VFC
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.vfc_input_map,       (CHIP_PTR_TYPE)&pmConfVFCInputMap,     0xFFFF,              (QMAP_SIZE<<13)+(VFC_MAP_MAX<<8)+PFVB_IDXB+PFVB_IDXA+PFVB_NOLIMIT+PFVB_NOMAP+PFVB_NOMENU,0xFFFF},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.vfc_output_map,      (CHIP_PTR_TYPE)&pmConfVFCOutputMap,    0xFFFF,              (QMAP_SIZE<<13)+(VFC_MAP_MAX<<8)+PFVB_IDXB+PFVB_IDXA+PFVB_NOLIMIT+PFVB_NOMAP+PFVB_NOMENU,0xFFFF},
#endif
#ifdef SF_ENABLE_SWC
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.swc_delay,           (CHIP_PTR_TYPE)&pmConfSWCDelay,        0xFF,                PFVB_NOMAP,                     ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.swc_mode,            (CHIP_PTR_TYPE)&pmConfSWCMode,         PULSE_MODE_PPMI,     PFVB_NOMAP,                     ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.swc_secs,            (CHIP_PTR_TYPE)&pmConfSWCSecs,         0xFFFF,              PFVB_NOMAP,                     ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.swc_duty,            (CHIP_PTR_TYPE)&pmConfSWCDuty,         0xFFFF,              PFVB_NOMAP,                     DEFAULT_SYS_WARMUP_DUTY},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.swc_trig,            (CHIP_PTR_TYPE)&pmConfSWCTrig,         PTT_TRIG_VAR_SIZE-ONE,PFVB_NOMAP,                    0xFF},
#endif

#ifdef SF_ENABLE_AVR
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.avr_pin2_map,        (CHIP_PTR_TYPE)&pmConfAVRPin2Map,      PIN2_FIRE_IN,        PFVB_NOMAP+PFVB_NOMENU,         PIN2_TRIG_IN},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.avr_pin3_map,        (CHIP_PTR_TYPE)&pmConfAVRPin3Map,      PIN3_FIRE_IN,        PFVB_NOMAP+PFVB_NOMENU,         PIN3_MENU0_IN},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.avr_pin4_map,        (CHIP_PTR_TYPE)&pmConfAVRPin4Map,      PIN4_DOC10_OUT,      PFVB_NOMAP+PFVB_NOMENU,         PIN4_MENU1_IN},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.avr_pin5_map,        (CHIP_PTR_TYPE)&pmConfAVRPin5Map,      PIN5_DOC11_OUT,      PFVB_NOMAP+PFVB_NOMENU,         PIN5_CLOCK_IN},
#endif

#ifdef SF_ENABLE_AVR_MEGA
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.avr_pin18_map,       (CHIP_PTR_TYPE)&pmConfAVRPin18Map,     PIN18_FIRE_IN,       PFVB_NOMAP+PFVB_NOMENU,         PIN18_OFF},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.avr_pin19_map,       (CHIP_PTR_TYPE)&pmConfAVRPin19Map,     PIN19_FIRE_IN,       PFVB_NOMAP+PFVB_NOMENU,         PIN19_OFF},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.avr_pin47_map,       (CHIP_PTR_TYPE)&pmConfAVRPin47Map,     PIN47_RELAY_OUT,     PFVB_NOMAP+PFVB_NOMENU,         PIN47_CLOCK_IN},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.avr_pin48_map,       (CHIP_PTR_TYPE)&pmConfAVRPin48Map,     PIN48_DOC6_OUT,      PFVB_NOMAP+PFVB_NOMENU,         PIN48_MENU0_IN},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.avr_pin49_map,       (CHIP_PTR_TYPE)&pmConfAVRPin49Map,     PIN49_DOC7_OUT,      PFVB_NOMAP+PFVB_NOMENU,         PIN49_MENU1_IN},
#endif

#ifdef SF_ENABLE_LCD
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_conf.lcd_size,            (CHIP_PTR_TYPE)&pmConfLCDSize,         LCD_SIZE_4x20,       PFVB_NOMAP+PFVB_NOMENU,         ZERO},
#endif

#ifdef SF_ENABLE_ADC
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.adc_map,             (CHIP_PTR_TYPE)&pmConfAdcMap,          0xFFFF,              (QMAP_SIZE<<13)+(ADC_NUM_MAX<<8)+PFVB_NOLIMIT+PFVB_NOMAP+PFVB_NOMENU+PFVB_IDXB+PFVB_IDXA,   0xFFFF},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.adc_jitter,          (CHIP_PTR_TYPE)&pmConfAdcJitter,       0xFFFF,              PFVB_NOMAP+PFVB_NOMENU,         DEFAULT_SYS_ADC_JITTER},
#endif
#ifdef SF_ENABLE_DIC
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_conf.dic_map,             (CHIP_PTR_TYPE)&pmConfDicMap,          0xFFFF,              (QMAP_SIZE<<13)+(DIC_NUM_MAX<<8)+PFVB_NOLIMIT+PFVB_NOMAP+PFVB_NOMENU+PFVB_IDXB+PFVB_IDXA,   0xFFFF},
#endif

// =============== pf_data vars = +64

	{PFVT_32BIT, (CHIP_PTR_TYPE)&pf_data.sys_main_loop_cnt,   (CHIP_PTR_TYPE)&pmDataSysMainLoopCnt,  ZERO,                PFVB_DT0+PFVB_NOMAP,            ZERO},
	{PFVT_32BIT, (CHIP_PTR_TYPE)&pf_data.sys_input_time_cnt,  (CHIP_PTR_TYPE)&pmDataSysInputTimeCnt, ZERO,                PFVB_DT0+PFVB_NOMAP,            ZERO},
#ifdef SF_ENABLE_ADC
	{PFVT_32BIT, (CHIP_PTR_TYPE)&pf_data.adc_time_cnt,        (CHIP_PTR_TYPE)&pmDataAdcTimeCnt,      ZERO,                PFVB_DT0+PFVB_NOMAP,            ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_data.adc_value,           (CHIP_PTR_TYPE)&pmDataAdcValue,        0xFFFF,              (ADC_NUM_MAX<<8)+PFVB_DT0+PFVB_NOMAP+PFVB_IDXA,  ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.adc_state,           (CHIP_PTR_TYPE)&pmDataAdcState,        ADC_STATE_DONE,      PFVB_DT0+PFVB_NOMAP,            ADC_STATE_IDLE},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.adc_state_idx,       (CHIP_PTR_TYPE)&pmDataAdcStateIdx,     ADC_NUM_MAX,         PFVB_DT0+PFVB_NOMAP,            ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_data.adc_state_value,     (CHIP_PTR_TYPE)&pmDataAdcStateValue,   0xFFFF,              PFVB_DT0+PFVB_NOMAP,            ZERO},
#endif
#ifdef SF_ENABLE_DIC
	{PFVT_32BIT, (CHIP_PTR_TYPE)&pf_data.dic_time_cnt,        (CHIP_PTR_TYPE)&pmDataDicTimeCnt,      ZERO,                PFVB_DT0+PFVB_NOMAP,            ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_data.dic_value,           (CHIP_PTR_TYPE)&pmDataDicValue,        0xFFFF,              PFVB_DT0+PFVB_NOMAP,            ZERO},
#endif
#ifdef SF_ENABLE_DOC
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.doc_port,            (CHIP_PTR_TYPE)&pmDataDocPort,         ONE,                 (DOC_PORT_NUM_MAX<<8)+PFVB_DT0+PFVB_IDXA,        ZERO},
#endif
#ifdef SF_ENABLE_SWC
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.swc_mode_org,        (CHIP_PTR_TYPE)&pmDataSWCModeOrg,      PULSE_MODE_PPMI,     PFVB_DT0+PFVB_NOMAP,            ONE},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_data.swc_secs_cnt,        (CHIP_PTR_TYPE)&pmDataSWCSecsCnt,      0xFFFF,              PFVB_DT0+PFVB_NOMAP,            ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_data.swc_duty_cnt,        (CHIP_PTR_TYPE)&pmDataSWCDutyCnt,      0xFFFF,              PFVB_DT0+PFVB_NOMAP,            ZERO},
#endif
#ifdef SF_ENABLE_LCD
	{PFVT_32BIT, (CHIP_PTR_TYPE)&pf_data.lcd_time_cnt,        (CHIP_PTR_TYPE)&pmDataLcdTimeCnt,      ZERO,                PFVB_DT0+PFVB_NOMAP,            ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.lcd_page,            (CHIP_PTR_TYPE)&pmDataLcdPage,         ZERO,                PFVB_DT0+PFVB_NOMAP,            ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.lcd_redraw,          (CHIP_PTR_TYPE)&pmDataLcdRedraw,       ZERO,                PFVB_DT0+PFVB_NOMAP,            ZERO},
#endif

#ifdef SF_ENABLE_LPM
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.lpm_state,           (CHIP_PTR_TYPE)&pmDataLPMState,        0xFF,                PFVB_DT0+PFVB_NOMAP,            LPM_IDLE},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.lpm_auto_cmd,        (CHIP_PTR_TYPE)&pmCmdReqAutoLPM,       0xFF,                PFVB_DT0+PFVB_NOMAP,            ZERO},
	{PFVT_32BIT, (CHIP_PTR_TYPE)&pf_data.lpm_start_time,      (CHIP_PTR_TYPE)&pmDataLPMStartTime,    ZERO,                PFVB_DT0+PFVB_NOMAP,            ZERO},
	{PFVT_32BIT, (CHIP_PTR_TYPE)&pf_data.lpm_total_time,      (CHIP_PTR_TYPE)&pmDataLPMTotalTime,    ZERO,                PFVB_DT0+PFVB_NOMAP,            ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_data.lpm_result,          (CHIP_PTR_TYPE)&pmDataLPMResult,       0xFFFF,              PFVB_DT0+PFVB_NOMAP,            ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_data.lpm_level,           (CHIP_PTR_TYPE)&pmDataLPMLevel,        0xFFFF,              PFVB_DT0,                       ZERO},
#endif

#ifdef SF_ENABLE_PTC
	{PFVT_32BIT, (CHIP_PTR_TYPE)&pf_data.ptc_sys_cnt,         (CHIP_PTR_TYPE)&pmDataPTCSysCnt,       0xFFFF,              PFVB_DT0+PFVB_NOMAP,            ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.ptc_0cnt,            (CHIP_PTR_TYPE)&pmDataPTC0Cnt,         0xFF,                PFVB_DT0+PFVB_NOMAP,            ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.ptc_1cnt,            (CHIP_PTR_TYPE)&pmDataPTC1Cnt,         0xFF,                PFVB_DT0+PFVB_NOMAP,            ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.ptc_0run_cnt,        (CHIP_PTR_TYPE)&pmDataPTC0RunCnt,      0xFF,                PFVB_DT0+PFVB_NOMAP,            ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.ptc_1run_cnt,        (CHIP_PTR_TYPE)&pmDataPTC1RunCnt,      0xFF,                PFVB_DT0+PFVB_NOMAP,            ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.ptc_0map_idx,        (CHIP_PTR_TYPE)&pmDataPTC0MapIdx,      PTC_TIME_MAP_MAX,    PFVB_DT0+PFVB_NOMAP,            ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.ptc_1map_idx,        (CHIP_PTR_TYPE)&pmDataPTC1MapIdx,      PTC_TIME_MAP_MAX,    PFVB_DT0+PFVB_NOMAP,            ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_data.ptc_0mul_cnt,        (CHIP_PTR_TYPE)&pmDataPTC0MulCnt,      0xFFFF,              PFVB_DT0+PFVB_NOMAP,            ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_data.ptc_1mul_cnt,        (CHIP_PTR_TYPE)&pmDataPTC1MulCnt,      0xFFFF,              PFVB_DT0+PFVB_NOMAP,            ZERO},
#endif
#ifdef SF_ENABLE_PTT
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.ptt_idx,             (CHIP_PTR_TYPE)&pmDataPTTIdx,          0xFF,                (PTT_TRIG_VAR_SIZE<<8)+PFVB_DT0+PFVB_IDXA+PFVB_NOMAP, ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.ptt_cnt,             (CHIP_PTR_TYPE)&pmDataPTTCnt,          0xFF,                (PTT_TRIG_VAR_SIZE<<8)+PFVB_DT0+PFVB_IDXA+PFVB_NOMAP, ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.ptt_fire,            (CHIP_PTR_TYPE)&pmDataPTTFire,         0xFF,                (PTT_TRIG_VAR_SIZE<<8)+PFVB_DT0+PFVB_IDXA+PFVB_TRIG,  ZERO},
#endif

#ifdef SF_ENABLE_DEV
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_data.dev_volt,            (CHIP_PTR_TYPE)&pmDataDevVolt,         0xFFFF,              PFVB_DT0,                       ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_data.dev_amp,             (CHIP_PTR_TYPE)&pmDataDevAmp,          0xFFFF,              PFVB_DT0,                       ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_data.dev_temp,            (CHIP_PTR_TYPE)&pmDataDevTemp,         0xFFFF,              PFVB_DT0,                       ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_data.dev_freq,            (CHIP_PTR_TYPE)&pmDataDevFreq,         0xFFFF,              PFVB_DT0,                       ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_data.dev_freq_cnt,        (CHIP_PTR_TYPE)&pmDataDevFreqCnt,      0xFFFF,              PFVB_DT0+PFVB_NOMAP,            ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_data.dev_var,             (CHIP_PTR_TYPE)&pmDataDevVar,          0xFFFF,              (DEV_VAR_MAX<<8)+PFVB_DT0+PFVB_IDXA,            ZERO},
#endif

#ifdef SF_ENABLE_PWM
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.pulse_fire,          (CHIP_PTR_TYPE)&pmDataPulseFire,       0xFF,                PFVB_DT0+PFVB_TRIG,             ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.pulse_step,          (CHIP_PTR_TYPE)&pmDataPulseStep,       0xFF,                PFVB_DT0+PFVB_NOMAP,            ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.pulse_data,          (CHIP_PTR_TYPE)&pmDataPulseData,       0xFF,                PFVB_DT0+PFVB_NOMAP,            ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.pulse_dir_cnt,       (CHIP_PTR_TYPE)&pmDataPulseDirCnt,     0xFF,                PFVB_DT0+PFVB_NOMAP,            ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.pulse_bank_cnt,      (CHIP_PTR_TYPE)&pmDataPulseBankCnt,    0xFF,                PFVB_DT0+PFVB_NOMAP,            ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.pulse_trig_delay_cnt,(CHIP_PTR_TYPE)&pmDataPulseTrigDelayCnt,0xFF,               PFVB_DT0+PFVB_NOMAP,            ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.pulse_post_delay_cnt,(CHIP_PTR_TYPE)&pmDataPulsePostDelayCnt,0xFF,               PFVB_DT0+PFVB_NOMAP,            ZERO},

	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.pwm_state,           (CHIP_PTR_TYPE)&pmDataPWMState,        0xFF,                PFVB_DT0+PFVB_NOMAP,            ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.pwm_loop_cnt,        (CHIP_PTR_TYPE)&pmDataPWMLoopCnt,      0xFF,                PFVB_DT0+PFVB_NOMAP,            ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.pwm_loop_max,        (CHIP_PTR_TYPE)&pmDataPWMLoopMax,      0xFF,                PFVB_DT0+PFVB_NOMAP,            ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_data.pwm_req_freq,        (CHIP_PTR_TYPE)&pmDataPWMReqFreq,      0xFFFF,              PFVB_DT0,                       ZERO},
#endif

#ifdef SF_ENABLE_PPM
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_data.ppm_idx,             (CHIP_PTR_TYPE)&pmDataPPMIdx,          0xFF,                (OUTPUT_MAX<<8)+PFVB_DT0+PFVB_NOMAP+PFVB_IDXA,  ZERO},
#endif

#ifdef SF_ENABLE_MAL
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_data.mal_trig,            (CHIP_PTR_TYPE)&pmDataMALTrig,         0xFFFF,              (MAL_PROGRAM_MAX<<8)+PFVB_DT0+PFVB_IDXA+PFVB_TRIG,ZERO},
#endif

// =============== pf_prog vars = +128

	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_prog.req_tx_push,         (CHIP_PTR_TYPE)&pmProgTXPush,          ONE,                 PFVB_DT1+PFVB_NOMAP,            ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_prog.req_tx_echo,         (CHIP_PTR_TYPE)&pmProgTXEcho,          ONE,                 PFVB_DT1+PFVB_NOMAP,            ONE},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_prog.req_tx_promt,        (CHIP_PTR_TYPE)&pmProgTXPromt,         ONE,                 PFVB_DT1+PFVB_NOMAP,            ONE},

	{PFVT_32BIT, (CHIP_PTR_TYPE)&pf_prog.sys_time_ticks,      (CHIP_PTR_TYPE)&pmProgSysTimeTicks,    ZERO,                PFVB_DT1+PFVB_NOMAP,            ZERO},
	{PFVT_32BIT, (CHIP_PTR_TYPE)&pf_prog.sys_time_ssec,       (CHIP_PTR_TYPE)&pmProgSysTimeSsec,     ZERO,                PFVB_DT1+PFVB_NOMAP,            ZERO},

#ifdef SF_ENABLE_LCD
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_prog.lcd_menu_state,      (CHIP_PTR_TYPE)&pmProgLcdMenuState,    0xFF,                PFVB_DT1+PFVB_NOMAP,            ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_prog.lcd_menu_mul,        (CHIP_PTR_TYPE)&pmProgLcdMenuMul,      0xFFFF,              PFVB_DT1+PFVB_NOMAP,            ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_prog.lcd_menu_idx,        (CHIP_PTR_TYPE)&pmProgLcdMenuIdx,      0xFF,                PFVB_DT1+PFVB_NOMAP,            ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_prog.lcd_menu_value_idx,  (CHIP_PTR_TYPE)&pmProgLcdMenuValueIdx, 0xFF,                PFVB_DT1+PFVB_NOMAP,            ZERO},
	{PFVT_32BIT, (CHIP_PTR_TYPE)&pf_prog.lcd_menu_time_cnt,   (CHIP_PTR_TYPE)&pmProgLcdMenuTimeCnt,  ZERO,                PFVB_DT1+PFVB_NOMAP,            ZERO},
#endif

#ifdef SF_ENABLE_MAL
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_prog.mal_pc,              (CHIP_PTR_TYPE)&pmProgMALPc,           0xFF,                PFVB_DT1+PFVB_NOMAP,            ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_prog.mal_state,           (CHIP_PTR_TYPE)&pmProgMALState,        0xFF,                PFVB_DT1+PFVB_NOMAP,            ZERO},
	{PFVT_16BIT, (CHIP_PTR_TYPE)&pf_prog.mal_var,             (CHIP_PTR_TYPE)&pmProgMALVar,          0xFFFF,              (OUTPUT_MAX<<8)+PFVB_DT1+PFVB_NOMAP+PFVB_IDXA,  ZERO},
#endif

#ifdef SF_ENABLE_STV
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_prog.stv_state,           (CHIP_PTR_TYPE)&pmProgSTVState,        STV_STATE_ERROR_MIN, PFVB_DT1+PFVB_NOMAP,            STV_STATE_OKE},
	{PFVT_32BIT, (CHIP_PTR_TYPE)&pf_prog.stv_time_cnt,        (CHIP_PTR_TYPE)&pmProgSTVTimeCnt,      0xFFFF,              PFVB_DT1+PFVB_NOMAP,            ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_prog.stv_mode_org,        (CHIP_PTR_TYPE)&pmProgSTVModeOrg,      0xFF,                PFVB_DT1+PFVB_NOMAP,            ZERO},
	{PFVT_8BIT,  (CHIP_PTR_TYPE)&pf_prog.stv_map_idx,         (CHIP_PTR_TYPE)&pmProgSTVMapIdx,       0xFF,                PFVB_DT1+PFVB_NOMAP,            ZERO},
#endif

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
	uint16_t bits = Chip_pgm_readWord(&(PF_VARS[idx][PFVF_BITS])) & PFVB_NOLIMIT;
	if (bits > ZERO) {
		return true;
	}
	return false;
}

boolean Vars_isIndexA(byte idx) {
	if (idx > PF_VARS_SIZE) {
		return false;
	}
	uint16_t bits = Chip_pgm_readWord(&(PF_VARS[idx][PFVF_BITS])) & PFVB_IDXA;
	if (bits > ZERO) {
		return true;
	}
	return false;
}

boolean Vars_isIndexB(byte idx) {
	if (idx > PF_VARS_SIZE) {
		return false;
	}
	uint16_t bits = Chip_pgm_readWord(&(PF_VARS[idx][PFVF_BITS])) & PFVB_IDXB;
	if (bits > ZERO) {
		return true;
	}
	return false;
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

boolean Vars_isBitSize32(byte idx) {
	uint16_t type = Chip_pgm_readWord(&(PF_VARS[idx][PFVF_TYPE]));
	if (type == PFVT_32BIT) {
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

boolean Vars_isTypeConf(byte idx) {
	uint16_t bits = Chip_pgm_readWord(&(PF_VARS[idx][PFVF_BITS])) & (PFVB_DT0+PFVB_DT1);
	if (bits == ZERO) {
		return true;
	}
	return false;
}

boolean Vars_isTypeData(byte idx) {
	uint16_t bits = Chip_pgm_readWord(&(PF_VARS[idx][PFVF_BITS])) & (PFVB_DT0+PFVB_DT1);
	if (bits == PFVB_DT0) {
		return true;
	}
	return false;
}


boolean Vars_isTypeProg(byte idx) {
	uint16_t bits = Chip_pgm_readWord(&(PF_VARS[idx][PFVF_BITS])) & (PFVB_DT0+PFVB_DT1);
	if (bits == PFVB_DT1) {
		return true;
	}
	return false;
}

char* Vars_getName(uint8_t idx) {
	return UNPSTRA(&PF_VARS[idx][PFVF_NAME]);
}

uint8_t Vars_getIndexAMax(uint8_t idx) {
	uint16_t bits = Chip_pgm_readWord(&(PF_VARS[idx][PFVF_BITS])) & 0xFF00;
	uint8_t result = (bits >> 8) & 0x1F; // only use 5 bits
	return result;
}

uint8_t Vars_getIndexBMax(uint8_t idx) {
	uint16_t bits = Chip_pgm_readWord(&(PF_VARS[idx][PFVF_BITS])) & 0xFF00;
	uint8_t result = (bits >> 13);
	return result;
}

uint16_t Vars_getDefaultValue(uint8_t idx) {
	return Chip_pgm_readWord(&(PF_VARS[idx][PFVF_DEF]));
}

uint16_t Vars_getIndexFromName(char* name) {
	for (uint8_t i=ZERO;i < PF_VARS_SIZE;i++) {
#ifdef SF_ENABLE_ARM_7M
		if (strcmp(name, (void*)Chip_pgm_readWord(&(PF_VARS[i][PFVF_NAME]))) == ZERO) {
#else
		// TODO: rm _P but not with UNPSTR
		if (strcmp_P(name, (const prog_char*)Chip_pgm_readWord(&(PF_VARS[i][PFVF_NAME]))) == ZERO) {
#endif
			return i;
		}
	}
	return QMAP_VAR_NONE;
}

uint16_t Vars_getValue(uint8_t idx,uint8_t idxA,uint8_t idxB) {
	boolean indexedA   = Vars_isIndexA(idx);
	boolean indexedB   = Vars_isIndexB(idx);
	uint8_t idxMaxA    = Vars_getIndexAMax(idx);
	uint8_t idxMaxB    = Vars_getIndexBMax(idx);
	uint16_t fieldType = Chip_pgm_readWord(&(PF_VARS[idx][PFVF_TYPE]));
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
		uint8_t *valuePtr = (uint8_t*)Chip_pgm_readWord(&(PF_VARS[idx][PFVF_VAR]));
		//CHIP_PTR_TYPE *valuePtr = (CHIP_PTR_TYPE*)Chip_pgm_readWord(&(PF_VARS[idx][PFVF_VAR]));
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
	boolean indexedA = Vars_isIndexA(idx);
	uint8_t idxMaxA    = Vars_getIndexAMax(idx);
	uint32_t *valuePtr = (uint32_t*)Chip_pgm_readWord(&(PF_VARS[idx][PFVF_VAR]));
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



// removes the dubbel print is set is done with serial and req_auto_push==1 
uint16_t Vars_setValueSerial(uint8_t idx,uint8_t idxA,uint8_t idxB,uint16_t value) {
	return Vars_setValueImpl(idx,idxA,idxB,value,true,true);
}
// removes the external function calls in reset phase.
uint16_t Vars_setValueReset(uint8_t idx,uint8_t idxA,uint16_t value) {
	if (Vars_isIndexB(idx)) {
		uint8_t b=0;
		for (b=0;b<Vars_getIndexBMax(idx);b++) {
			uint16_t valueSet = value;
			if (b>ZERO) {
				valueSet = ZERO;
			}
			Vars_setValueImpl(idx,idxA,b,valueSet,false,false);
		}
		return ZERO;
	} else {
		return Vars_setValueImpl(idx,idxA,(uint8_t)0,value,false,false);
	}
}
uint16_t Vars_setValue(uint8_t idx,uint8_t idxA,uint8_t idxB,uint16_t value) {
	return Vars_setValueImpl(idx,idxA,0,value,true,false);
}
uint16_t Vars_setValueImpl(uint8_t idx,uint8_t idxA,uint8_t idxB,uint16_t value,boolean trig,boolean serial) {
	boolean indexedA   = Vars_isIndexA(idx);
	boolean indexedB   = Vars_isIndexB(idx);
	uint8_t idxMaxA    = Vars_getIndexAMax(idx);
	uint8_t idxMaxB    = Vars_getIndexBMax(idx);
	uint16_t fieldType = Chip_pgm_readWord(&(PF_VARS[idx][PFVF_TYPE]));
	uint16_t value_max = Chip_pgm_readWord(&(PF_VARS[idx][PFVF_MAX]));
	uint16_t value_min = ZERO;
	if ( Chip_pgm_readWord(&(PF_VARS[idx][PFVF_NAME])) == (CHIP_PTR_TYPE)&pmConfPWMOnCntA) {
		value_min = ONE;
	}
	if ( Chip_pgm_readWord(&(PF_VARS[idx][PFVF_NAME])) == (CHIP_PTR_TYPE)&pmConfPWMOnCntB) {
		value_min = ONE;
	}
	if ( Chip_pgm_readWord(&(PF_VARS[idx][PFVF_NAME])) == (CHIP_PTR_TYPE)&pmConfSWCDuty) {
		value_min = ONE;
	}
#ifdef SF_ENABLE_PPM
	if ( Chip_pgm_readWord(&(PF_VARS[idx][PFVF_NAME])) == (CHIP_PTR_TYPE)&pmConfPPMDataLength) {
		value_min = ONE;
	}
#endif
#ifdef SF_ENABLE_PTC
	if ( Chip_pgm_readWord(&(PF_VARS[idx][PFVF_NAME])) == (CHIP_PTR_TYPE)&pmConfPTC0Mul) {
		value_min = ONE;
	}
	if ( Chip_pgm_readWord(&(PF_VARS[idx][PFVF_NAME])) == (CHIP_PTR_TYPE)&pmConfPTC1Mul) {
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

	if (value_max != ZERO && value > value_max) {
		value = value_max;
	}
	if (value_min != ZERO && value < value_min) {
		value = value_min;
	}
	// Set value
	if (fieldType == PFVT_16BIT) {
		uint16_t *valuePtr = (uint16_t*)Chip_pgm_readWord(&(PF_VARS[idx][PFVF_VAR]));
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
					if (Vars_isIndexB(idx)) {
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
		uint8_t *valuePtr = (uint8_t*)Chip_pgm_readWord(&(PF_VARS[idx][PFVF_VAR]));
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
					if (Vars_isIndexB(idx)) {
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
		uint32_t *valuePtr = (uint32_t*)Chip_pgm_readWord(&(PF_VARS[idx][PFVF_VAR]));
		if (indexedA) {
			valuePtr += idxA;
		}
		uint32_t v = value;
		*valuePtr = v;
	}

	// Send to serial if push is on
	if (pf_prog.req_tx_push == ONE && serial==false) {
		if (indexedA) {
			if (idxA == QMAP_VAR_IDX_ALL) {
				uint8_t i=ZERO;
				for (i=ZERO;i<idxMaxA;i++) {
					Serial_printChar(UNPSTRA(&PF_VARS[idx][PFVF_NAME]));
					if(i<10) {Serial_write('0');} Serial_printDec((int)i);
					Serial_printCharP(pmSetSpaced);
					Serial_printDec(value);
					Serial_println();
				}
			} else {
				Serial_printChar(UNPSTRA(&PF_VARS[idx][PFVF_NAME]));
				if(idxA<10) {Serial_write('0');} Serial_printDec((int)idxA);
				Serial_printCharP(pmSetSpaced);
				Serial_printDec(value);
				Serial_println();
			}
		} else {
			Serial_printChar(UNPSTRA(&PF_VARS[idx][PFVF_NAME]));
			Serial_printCharP(pmSetSpaced);
			Serial_printDec(value);
			Serial_println();
		}
	}

	// Some fields require extra update code;
	uint16_t varName = Chip_pgm_readWord(&(PF_VARS[idx][PFVF_NAME]));
	if ( varName == (CHIP_PTR_TYPE)&pmConfPulseMode && trig==false) {
		Vars_resetData();
	}
#ifdef SF_ENABLE_AVR
	if ( varName == (uint16_t)&pmConfAVRPin2Map) {
		if (pf_conf.avr_pin2_map == PIN2_TRIG_IN || pf_conf.avr_pin2_map == PIN2_FREQ_IN || pf_conf.avr_pin2_map == PIN2_FIRE_IN) {
			Chip_in_int_pin(ZERO,ZERO);
		} else {
			Chip_in_int_pin(ZERO,ONE);
		}
	}
	if ( varName == (uint16_t)&pmConfAVRPin3Map) {
		if (pf_conf.avr_pin3_map == PIN3_FREQ_IN || pf_conf.avr_pin3_map == PIN3_FIRE_IN) {
			Chip_in_int_pin(ONE,ZERO);
		} else {
			Chip_in_int_pin(ONE,ONE);
		}
	}
#endif
#ifdef SF_ENABLE_PWM
	if ( varName == (CHIP_PTR_TYPE)&pmConfPWMClock) {
		Chip_pwm_timer(PWM_REG_CLOCK,pf_conf.pwm_clock);
	}
#endif

	if (trig==false) {
		return value; // no update of triggers
	}

#ifdef SF_ENABLE_STV
	uint8_t stvIdxMax = STV_is_variable_mapped(idx,idxA,true);
	uint8_t stvIdxMin = STV_is_variable_mapped(idx,idxA,false);
	if (stvIdxMax != QMAP_VAR_IDX_ALL) {
		STV_vars_max(value,stvIdxMax);
	}
	if (stvIdxMin != QMAP_VAR_IDX_ALL) {
		STV_vars_min(value,stvIdxMin);
	}
#endif
#ifdef SF_ENABLE_PWM
	if ( varName == (CHIP_PTR_TYPE)&pmDataPWMReqFreq) {
		Freq_requestTrainFreq(value,QMAP_VAR_IDX_ALL);
	}
	if ( varName == (CHIP_PTR_TYPE)&pmConfPWMDuty && pf_data.pwm_req_freq != ZERO) {
		Freq_requestTrainFreq(pf_data.pwm_req_freq,QMAP_VAR_IDX_ALL);
	}
#endif
#ifdef SF_ENABLE_MAL
	if ( varName == (CHIP_PTR_TYPE)&pmDataMALTrig) {
		mal_execute(idxA,value);
	}
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
			uint16_t valueVfc = mapValue(value,minLevel,maxLevel,minMapLevel,maxMapLevel);
			Vars_setValue(outVar,pf_conf.vfc_output_map[vfcIdx][QMAP_VAR_IDX],ZERO,valueVfc);  // recursive function !!
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
		if (Vars_isTypeConf(i)==false) {
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
#ifdef SF_ENABLE_PPM
	for (uint8_t i=ZERO;i < OUTPUT_MAX;i++) {
		pf_conf.ppm_data_a[i]      = i + DEFAULT_PPM_DATA; // example demo data
		pf_conf.ppm_data_b[i]      = OUTPUT_MAX-i + DEFAULT_PPM_DATA;
	}
#endif
#ifdef SF_ENABLE_MAL
	for (uint8_t n=ZERO;n < MAL_PROGRAM_MAX;n++) {
		for (uint8_t i=ZERO;i < MAL_PROGRAM_SIZE;i++) {
			pf_conf.mal_program[i][n] = 0xFF;
		}
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
		Vars_setValueReset(i,QMAP_VAR_IDX_ALL,Vars_getDefaultValue(i));
#ifdef SF_ENABLE_DEBUG
		Serial_printCharP(PSTR("ResetData: "));
		Serial_printChar(UNPSTRA(&PF_VARS[i][PFVF_NAME]));
		Serial_printChar(" to: ");
		Serial_printDec(Vars_getDefaultValue(i));
		Serial_printChar(" setto: ");
		//cmd_print_var(i,false,false);
		Serial_println();
#endif
	}

#ifdef SF_ENABLE_PWM
	// Reset cutom pwm data
	pf_data.pulse_data           = pf_conf.pulse_init_a;
	pf_data.pulse_dir_cnt        = pf_conf.pulse_dir;
	pf_data.pwm_loop_max         = pf_conf.pwm_loop;
	if (pf_conf.pulse_trig      == PULSE_TRIG_LOOP) {
		pf_data.pwm_state          = PWM_STATE_RUN;
	} else {
		pf_data.pwm_state          = PWM_STATE_IDLE;
	}
#endif
#ifdef SF_ENABLE_SWC
#ifdef SF_ENABLE_PWM
	pf_data.swc_mode_org         = pf_conf.pulse_mode;
#endif
#endif
}


// Setup all interal variables to init state
void Vars_setup(void) {
	// Read or reset config to init state
	Vars_readConfig(); // First read config from eeprom then check if valid for this version
	if (pf_conf.sys_version != PULSE_FIRE_VERSION || pf_conf.sys_struct_size != sizeof(pf_conf_struct)) {
		Vars_resetConfig(); // if newer/other/none version and/or size in flash then reset all to defaults and save.
		Vars_writeConfig();
	}

	// Reset data to init state
	Vars_resetData();
#ifdef SF_ENABLE_SWC
#ifdef SF_ENABLE_PWM
	if (pf_conf.swc_mode != ZERO) {
		pf_conf.pulse_mode = pf_conf.swc_mode; // run in startup mode once.
	}
	pf_data.swc_secs_cnt  = ONE; // only set on startup to 'one' so softstart code runs once.
#endif
#endif

	// Reset prog to init state
	pf_prog.req_tx_push          = ZERO;
	pf_prog.req_tx_echo          = ONE;
	pf_prog.req_tx_promt         = ONE;
	pf_prog.cmd_buff_idx         = ZERO;
#ifdef SF_ENABLE_LCD
	pf_prog.lcd_menu_state       = LCD_MENU_STATE_OFF;
	pf_prog.lcd_menu_mul         = ONE;
	pf_prog.lcd_menu_idx         = ZERO;
	pf_prog.lcd_menu_value_idx   = ZERO;
	pf_prog.lcd_menu_time_cnt    = ZERO;
#endif
#ifdef SF_ENABLE_MAL
	pf_prog.mal_pc               = ZERO;
	pf_prog.mal_state            = MAL_STATE_IDLE;
	uint8_t i=ZERO;
	for (i=ZERO;i < OUTPUT_MAX;i++) {
		pf_prog.mal_var[i]         = ZERO;
	}
#endif
#ifdef SF_ENABLE_STV
	pf_prog.stv_state            = STV_STATE_OKE;
	pf_prog.stv_time_cnt         = ZERO;
#ifdef SF_ENABLE_PWM
	pf_prog.stv_mode_org         = pf_conf.pulse_mode;
#endif
	pf_prog.stv_map_idx          = ZERO;
#endif
}


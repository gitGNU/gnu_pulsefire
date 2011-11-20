
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
const uint16_t PF_VARS[PF_VARS_PF_SIZE+PF_VARS_AVR_SIZE+PF_VARS_AVR_MEGA_SIZE+
  PF_VARS_PWM_SIZE+PF_VARS_LCD_SIZE+PF_VARS_LPM_SIZE+
  PF_VARS_PPM_SIZE+PF_VARS_ADC_SIZE+PF_VARS_DIC_SIZE+
  PF_VARS_DOC_SIZE+PF_VARS_DEV_SIZE+PF_VARS_PTC_SIZE+
  PF_VARS_PTT_SIZE+PF_VARS_STV_SIZE+PF_VARS_VFC_SIZE+
  PF_VARS_MAL_SIZE+PF_VARS_FRQ_SIZE+PF_VARS_SWC_SIZE][PFVF_DEF+ONE] CHIP_PROGMEM = {

  #ifdef SF_ENABLE_PWM
  {PFVT_8BIT,  (uint16_t)&pf_conf.pulse_enable,        (uint16_t)&pmConfPulseEnable,     ONE,                  PFVB_NONE,                      ONE},
  {PFVT_8BIT,  (uint16_t)&pf_conf.pulse_mode,          (uint16_t)&pmConfPulseMode,       PULSE_MODE_PPMI,      PFVB_NONE,                      PULSE_MODE_TRAIN},
  {PFVT_8BIT,  (uint16_t)&pf_conf.pulse_steps,         (uint16_t)&pmConfPulseSteps,      
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
  {PFVT_8BIT,  (uint16_t)&pf_conf.pulse_trig,          (uint16_t)&pmConfPulseTrig,       PULSE_TRIG_EXT,      PFVB_NONE,                      PULSE_TRIG_LOOP},
  {PFVT_8BIT,  (uint16_t)&pf_conf.pulse_dir,           (uint16_t)&pmConfPulseDir,        PULSE_DIR_LRRL,      PFVB_NONE,                      ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_conf.pulse_bank,          (uint16_t)&pmConfPulseBank,       ALL_BANK_MAX,        PFVB_NONE,                      ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_conf.pulse_inv,           (uint16_t)&pmConfPulseInv,        ONE,                 PFVB_NONE,                      ZERO},
  {PFVT_16BIT, (uint16_t)&pf_conf.pulse_trig_delay,    (uint16_t)&pmConfPulseTrigDelay,  0xFFFF,              PFVB_NONE,                      ZERO},
  {PFVT_16BIT, (uint16_t)&pf_conf.pulse_post_delay,    (uint16_t)&pmConfPulsePostDelay,  0xFFFF,              PFVB_NONE,                      ZERO},
  {PFVT_16BIT, (uint16_t)&pf_conf.pulse_mask_a,        (uint16_t)&pmConfPulseMaskA,      0xFFFF,              PFVB_NONE,                      PULSE_DATA_ON},
  {PFVT_16BIT, (uint16_t)&pf_conf.pulse_mask_b,        (uint16_t)&pmConfPulseMaskB,      0xFFFF,              PFVB_NONE,                      PULSE_DATA_ON},
  {PFVT_16BIT, (uint16_t)&pf_conf.pulse_init_a,        (uint16_t)&pmConfPulseInitA,      0xFFFF,              PFVB_NONE,                      DEFAULT_PULSE_DATA_INIT},
  {PFVT_16BIT, (uint16_t)&pf_conf.pulse_init_b,        (uint16_t)&pmConfPulseInitB,      0xFFFF,              PFVB_NONE,                      DEFAULT_PULSE_DATA_INIT},
  
  {PFVT_16BIT, (uint16_t)&pf_conf.pwm_on_cnt_a,        (uint16_t)&pmConfPWMOnCntA,       0xFFFF,              (OUTPUT_MAX<<8)+PFVB_IDXA,      DEFAULT_PWM_ON_CNT},
  {PFVT_16BIT, (uint16_t)&pf_conf.pwm_on_cnt_b,        (uint16_t)&pmConfPWMOnCntB,       0xFFFF,              (OUTPUT_MAX<<8)+PFVB_IDXA,      DEFAULT_PWM_ON_CNT},
  {PFVT_16BIT, (uint16_t)&pf_conf.pwm_off_cnt_a,       (uint16_t)&pmConfPWMOffCntA,      0xFFFF,              (OUTPUT_MAX<<8)+PFVB_IDXA,      ZERO},
  {PFVT_16BIT, (uint16_t)&pf_conf.pwm_off_cnt_b,       (uint16_t)&pmConfPWMOffCntB,      0xFFFF,              (OUTPUT_MAX<<8)+PFVB_IDXA,      ZERO},
  {PFVT_16BIT, (uint16_t)&pf_conf.pwm_tune_cnt,        (uint16_t)&pmConfPWMTuneCnt,      0xFFFF,              (OUTPUT_MAX<<8)+PFVB_IDXA,      ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_conf.pwm_loop,            (uint16_t)&pmConfPWMLoop,         0xFF,                PFVB_NONE,                      DEFAULT_PWM_LOOP},
  {PFVT_8BIT,  (uint16_t)&pf_conf.pwm_loop_delta,      (uint16_t)&pmConfPWMLoopDelta,    0xFF,                PFVB_NONE,                      ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_conf.pwm_clock,           (uint16_t)&pmConfPWMClock,        CLOCK_VALUE_MAX,     PFVB_NONE,                      DEFAULT_PWM_CLOCK},
  {PFVT_8BIT,  (uint16_t)&pf_conf.pwm_duty,            (uint16_t)&pmConfPWMDuty,         110,                 PFVB_NONE,                      ZERO},
  #endif
  
  #ifdef SF_ENABLE_PPM
  {PFVT_8BIT,  (uint16_t)&pf_conf.ppm_data_offset,     (uint16_t)&pmConfPPMDataOffset,   OUTPUT_MAX-ONE,      PFVB_NONE,                      ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_conf.ppm_data_length,     (uint16_t)&pmConfPPMDataLength,   OUTPUT_MAX,          PFVB_NONE,                      OUTPUT_MAX},
  {PFVT_16BIT, (uint16_t)&pf_conf.ppm_data_a,          (uint16_t)&pmConfPPMDataA,        0xFFFF,              (OUTPUT_MAX<<8)+PFVB_IDXA,      ZERO},
  {PFVT_16BIT, (uint16_t)&pf_conf.ppm_data_b,          (uint16_t)&pmConfPPMDataB,        0xFFFF,              (OUTPUT_MAX<<8)+PFVB_IDXA,      ZERO},
  #endif
  #ifdef SF_ENABLE_LPM
  {PFVT_16BIT, (uint16_t)&pf_conf.lpm_start,           (uint16_t)&pmConfLPMStart,        0xFFFF,              PFVB_NONE,                      DEFAULT_LPM_START},
  {PFVT_16BIT, (uint16_t)&pf_conf.lpm_stop,            (uint16_t)&pmConfLPMStop,         0xFFFF,              PFVB_NONE,                      DEFAULT_LPM_STOP},
  {PFVT_16BIT, (uint16_t)&pf_conf.lpm_size,            (uint16_t)&pmConfLPMSize,         0xFFFF,              PFVB_NONE,                      ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_conf.lpm_relay_inv,       (uint16_t)&pmConfLPMRelayInv,     ONE,                 PFVB_NONE,                      ZERO},
  #endif
  #ifdef SF_ENABLE_PTC
  {PFVT_8BIT,  (uint16_t)&pf_conf.ptc_0run,            (uint16_t)&pmConfPTC0Run,         PTC_RUN_LOOP,        PFVB_NONE,                      PTC_RUN_OFF},
  {PFVT_8BIT,  (uint16_t)&pf_conf.ptc_1run,            (uint16_t)&pmConfPTC1Run,         PTC_RUN_LOOP,        PFVB_NONE,                      PTC_RUN_OFF},
  {PFVT_8BIT,  (uint16_t)&pf_conf.ptc_0mul,            (uint16_t)&pmConfPTC0Mul,         0xFF,                PFVB_NONE,                      ONE},
  {PFVT_8BIT,  (uint16_t)&pf_conf.ptc_1mul,            (uint16_t)&pmConfPTC1Mul,         0xFF,                PFVB_NONE,                      ONE},
  {PFVT_16BIT, (uint16_t)&pf_conf.ptc_0map,            (uint16_t)&pmConfPTC0Map,         0xFFFF,              (QMAP_SIZE<<13)+(PTC_TIME_MAP_MAX<<8)+PFVB_IDXB+PFVB_IDXA+PFVB_NOLIMIT+PFVB_NOMAP+PFVB_NOMENU,  0xFFFF},
  {PFVT_16BIT, (uint16_t)&pf_conf.ptc_1map,            (uint16_t)&pmConfPTC1Map,         0xFFFF,              (QMAP_SIZE<<13)+(PTC_TIME_MAP_MAX<<8)+PFVB_IDXB+PFVB_IDXA+PFVB_NOLIMIT+PFVB_NOMAP+PFVB_NOMENU,  0xFFFF},
  #endif
  #ifdef SF_ENABLE_PTT
  {PFVT_16BIT, (uint16_t)&pf_conf.ptt_0map,            (uint16_t)&pmConfPTT0Map,         0xFFFF,              (QMAP_SIZE<<13)+(PTT_TRIG_MAP_MAX<<8)+PFVB_IDXB+PFVB_IDXA+PFVB_NOLIMIT+PFVB_NOMAP+PFVB_NOMENU,  0xFFFF},
  {PFVT_16BIT, (uint16_t)&pf_conf.ptt_1map,            (uint16_t)&pmConfPTT1Map,         0xFFFF,              (QMAP_SIZE<<13)+(PTT_TRIG_MAP_MAX<<8)+PFVB_IDXB+PFVB_IDXA+PFVB_NOLIMIT+PFVB_NOMAP+PFVB_NOMENU,  0xFFFF},
  {PFVT_16BIT, (uint16_t)&pf_conf.ptt_2map,            (uint16_t)&pmConfPTT2Map,         0xFFFF,              (QMAP_SIZE<<13)+(PTT_TRIG_MAP_MAX<<8)+PFVB_IDXB+PFVB_IDXA+PFVB_NOLIMIT+PFVB_NOMAP+PFVB_NOMENU,  0xFFFF},
  {PFVT_16BIT, (uint16_t)&pf_conf.ptt_3map,            (uint16_t)&pmConfPTT3Map,         0xFFFF,              (QMAP_SIZE<<13)+(PTT_TRIG_MAP_MAX<<8)+PFVB_IDXB+PFVB_IDXA+PFVB_NOLIMIT+PFVB_NOMAP+PFVB_NOMENU,  0xFFFF},
  #endif
  #ifdef SF_ENABLE_DEV
  {PFVT_8BIT,  (uint16_t)&pf_conf.dev_volt_dot,        (uint16_t)&pmProgDevVoltDot,      DEV_DOT_10000,       PFVB_NOMAP,                     ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_conf.dev_amp_dot,         (uint16_t)&pmProgDevAmpDot,       DEV_DOT_10000,       PFVB_NOMAP,                     ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_conf.dev_temp_dot,        (uint16_t)&pmProgDevTempDot,      DEV_DOT_10000,       PFVB_NOMAP,                     ZERO},
  #endif
  #ifdef SF_ENABLE_STV
  {PFVT_8BIT,  (uint16_t)&pf_conf.stv_warn_secs,       (uint16_t)&pmConfSTVWarnSecs,     0xFF,                PFVB_NOMAP+PFVB_NOMENU,         ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_conf.stv_warn_mode,       (uint16_t)&pmConfSTVWarnMode,     PULSE_MODE_PPMI,     PFVB_NOMAP+PFVB_NOMENU,         PULSE_MODE_OFF},
  {PFVT_8BIT,  (uint16_t)&pf_conf.stv_error_secs,      (uint16_t)&pmConfSTVErrorSecs,    0xFF,                PFVB_NOMAP+PFVB_NOMENU,         ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_conf.stv_error_mode,      (uint16_t)&pmConfSTVErrorMode,    PULSE_MODE_PPMI,     PFVB_NOMAP+PFVB_NOMENU,         PULSE_MODE_OFF},
  {PFVT_16BIT, (uint16_t)&pf_conf.stv_max_map,         (uint16_t)&pmConfSTVMaxMap,       0xFFFF,              (QMAP_SIZE<<13)+(STV_MAX_MAP_MAX<<8)+PFVB_IDXB+PFVB_IDXA+PFVB_NOLIMIT+PFVB_NOMAP+PFVB_NOMENU,0xFFFF},
  {PFVT_16BIT, (uint16_t)&pf_conf.stv_min_map,         (uint16_t)&pmConfSTVMinMap,       0xFFFF,              (QMAP_SIZE<<13)+(STV_MIN_MAP_MAX<<8)+PFVB_IDXB+PFVB_IDXA+PFVB_NOLIMIT+PFVB_NOMAP+PFVB_NOMENU,0xFFFF},
  #endif
  #ifdef SF_ENABLE_VFC
  {PFVT_16BIT, (uint16_t)&pf_conf.vfc_input_map,       (uint16_t)&pmConfVFCInputMap,     0xFFFF,              (QMAP_SIZE<<13)+(VFC_MAP_MAX<<8)+PFVB_IDXB+PFVB_IDXA+PFVB_NOLIMIT+PFVB_NOMAP+PFVB_NOMENU,0xFFFF},
  {PFVT_16BIT, (uint16_t)&pf_conf.vfc_output_map,      (uint16_t)&pmConfVFCOutputMap,    0xFFFF,              (QMAP_SIZE<<13)+(VFC_MAP_MAX<<8)+PFVB_IDXB+PFVB_IDXA+PFVB_NOLIMIT+PFVB_NOMAP+PFVB_NOMENU,0xFFFF},
  #endif
  #ifdef SF_ENABLE_SWC
  {PFVT_8BIT,  (uint16_t)&pf_conf.swc_delay,           (uint16_t)&pmConfSWCDelay,        0xFF,                PFVB_NOMAP,                     ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_conf.swc_mode,            (uint16_t)&pmConfSWCMode,         PULSE_MODE_PPMI,     PFVB_NOMAP,                     ZERO},
  {PFVT_16BIT, (uint16_t)&pf_conf.swc_secs,            (uint16_t)&pmConfSWCSecs,         0xFFFF,              PFVB_NOMAP,                     ZERO},
  {PFVT_16BIT, (uint16_t)&pf_conf.swc_duty,            (uint16_t)&pmConfSWCDuty,         0xFFFF,              PFVB_NOMAP,                     DEFAULT_SYS_WARMUP_DUTY},
  {PFVT_8BIT,  (uint16_t)&pf_conf.swc_trig,            (uint16_t)&pmConfSWCTrig,         PTT_TRIG_VAR_SIZE-ONE,PFVB_NOMAP,                    0xFF},
  #endif

  #ifdef SF_ENABLE_AVR
  {PFVT_8BIT,  (uint16_t)&pf_conf.avr_pin2_map,        (uint16_t)&pmConfAVRPin2Map,      PIN2_FIRE_IN,        PFVB_NOMAP+PFVB_NOMENU,         PIN2_TRIG_IN},
  {PFVT_8BIT,  (uint16_t)&pf_conf.avr_pin3_map,        (uint16_t)&pmConfAVRPin3Map,      PIN3_FIRE_IN,        PFVB_NOMAP+PFVB_NOMENU,         PIN3_MENU0_IN},
  {PFVT_8BIT,  (uint16_t)&pf_conf.avr_pin4_map,        (uint16_t)&pmConfAVRPin4Map,      PIN4_DOC10_OUT,      PFVB_NOMAP+PFVB_NOMENU,         PIN4_MENU1_IN},
  {PFVT_8BIT,  (uint16_t)&pf_conf.avr_pin5_map,        (uint16_t)&pmConfAVRPin5Map,      PIN5_DOC11_OUT,      PFVB_NOMAP+PFVB_NOMENU,         PIN5_CLOCK_IN},
  #endif

  #ifdef SF_ENABLE_LCD
  {PFVT_8BIT,  (uint16_t)&pf_conf.lcd_size,            (uint16_t)&pmConfLCDSize,         LCD_SIZE_4x20,       PFVB_NOMAP+PFVB_NOMENU,         ZERO},
  #endif

  #ifdef SF_ENABLE_ADC
  {PFVT_16BIT, (uint16_t)&pf_conf.adc_map,             (uint16_t)&pmConfAdcMap,          0xFFFF,              (QMAP_SIZE<<13)+(ADC_NUM_MAX<<8)+PFVB_NOLIMIT+PFVB_NOMAP+PFVB_NOMENU+PFVB_IDXB+PFVB_IDXA,   0xFFFF},
  {PFVT_16BIT, (uint16_t)&pf_conf.adc_jitter,          (uint16_t)&pmConfAdcJitter,       0xFFFF,              PFVB_NOMAP+PFVB_NOMENU,         DEFAULT_SYS_ADC_JITTER},
  #endif
  #ifdef SF_ENABLE_DIC
  {PFVT_16BIT, (uint16_t)&pf_conf.dic_map,             (uint16_t)&pmConfDicMap,          0xFFFF,              (QMAP_SIZE<<13)+(DIC_NUM_MAX<<8)+PFVB_NOLIMIT+PFVB_NOMAP+PFVB_NOMENU+PFVB_IDXB+PFVB_IDXA,   0xFFFF},
  #endif

  // =============== pf_data vars = +64

  {PFVT_32BIT, (uint16_t)&pf_data.sys_main_loop_cnt,   (uint16_t)&pmDataSysMainLoopCnt,  ZERO,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_32BIT, (uint16_t)&pf_data.sys_input_time_cnt,  (uint16_t)&pmDataSysInputTimeCnt, ZERO,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  #ifdef SF_ENABLE_ADC
  {PFVT_32BIT, (uint16_t)&pf_data.adc_time_cnt,        (uint16_t)&pmDataAdcTimeCnt,      ZERO,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_16BIT, (uint16_t)&pf_data.adc_value,           (uint16_t)&pmDataAdcValue,        0xFFFF,              (ADC_NUM_MAX<<8)+PFVB_DT0+PFVB_NOMAP+PFVB_IDXA,  ZERO},
  #endif
  #ifdef SF_ENABLE_DIC  
  {PFVT_32BIT, (uint16_t)&pf_data.dic_time_cnt,        (uint16_t)&pmDataDicTimeCnt,      ZERO,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_data.dic_value,           (uint16_t)&pmDataDicValue,        0xFF,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  #endif
  #ifdef SF_ENABLE_DOC  
  {PFVT_8BIT,  (uint16_t)&pf_data.doc_port,            (uint16_t)&pmDataDocPort,         ONE,                 (DOC_PORT_NUM_MAX<<8)+PFVB_DT0+PFVB_IDXA,        ZERO},
  #endif
  #ifdef SF_ENABLE_SWC
  {PFVT_8BIT,  (uint16_t)&pf_data.swc_mode_org,        (uint16_t)&pmDataSWCModeOrg,      PULSE_MODE_PPMI,     PFVB_DT0+PFVB_NOMAP,            ONE},
  {PFVT_16BIT, (uint16_t)&pf_data.swc_secs_cnt,        (uint16_t)&pmDataSWCSecsCnt,      0xFFFF,              PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_16BIT, (uint16_t)&pf_data.swc_duty_cnt,        (uint16_t)&pmDataSWCDutyCnt,      0xFFFF,              PFVB_DT0+PFVB_NOMAP,            ZERO},
  #endif
  #ifdef SF_ENABLE_LCD
  {PFVT_32BIT, (uint16_t)&pf_data.lcd_time_cnt,        (uint16_t)&pmDataLcdTimeCnt,      ZERO,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_data.lcd_page,            (uint16_t)&pmDataLcdPage,         ZERO,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_data.lcd_redraw,          (uint16_t)&pmDataLcdRedraw,       ZERO,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  #endif
  #ifdef SF_ENABLE_FRQ
  {PFVT_16BIT, (uint16_t)&pf_data.req_pwm_freq,        (uint16_t)&pmCmdReqPWMFreq,       0xFFFF,              PFVB_DT0,                       ZERO},
  #endif

  #ifdef SF_ENABLE_LPM
  {PFVT_8BIT,  (uint16_t)&pf_data.lpm_state,           (uint16_t)&pmDataLPMState,        0xFF,                PFVB_DT0+PFVB_NOMAP,            LPM_IDLE},
  {PFVT_8BIT,  (uint16_t)&pf_data.lpm_auto_cmd,        (uint16_t)&pmCmdReqAutoLPM,       0xFF,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_32BIT, (uint16_t)&pf_data.lpm_start_time,      (uint16_t)&pmDataLPMStartTime,    ZERO,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_32BIT, (uint16_t)&pf_data.lpm_total_time,      (uint16_t)&pmDataLPMTotalTime,    ZERO,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_16BIT, (uint16_t)&pf_data.lpm_result,          (uint16_t)&pmDataLPMResult,       0xFFFF,              PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_16BIT, (uint16_t)&pf_data.lpm_level,           (uint16_t)&pmDataLPMLevel,        0xFFFF,              PFVB_DT0,                       ZERO},
  #endif

  #ifdef SF_ENABLE_PTC
  {PFVT_32BIT, (uint16_t)&pf_data.ptc_sys_cnt,         (uint16_t)&pmDataPTCSysCnt,       0xFFFF,              PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_data.ptc_0cnt,            (uint16_t)&pmDataPTC0Cnt,         0xFF,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_data.ptc_1cnt,            (uint16_t)&pmDataPTC1Cnt,         0xFF,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_data.ptc_0run_cnt,        (uint16_t)&pmDataPTC0RunCnt,      0xFF,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_data.ptc_1run_cnt,        (uint16_t)&pmDataPTC1RunCnt,      0xFF,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_data.ptc_0map_idx,        (uint16_t)&pmDataPTC0MapIdx,      PTC_TIME_MAP_MAX,    PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_data.ptc_1map_idx,        (uint16_t)&pmDataPTC1MapIdx,      PTC_TIME_MAP_MAX,    PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_16BIT, (uint16_t)&pf_data.ptc_0mul_cnt,        (uint16_t)&pmDataPTC0MulCnt,      0xFFFF,              PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_16BIT, (uint16_t)&pf_data.ptc_1mul_cnt,        (uint16_t)&pmDataPTC1MulCnt,      0xFFFF,              PFVB_DT0+PFVB_NOMAP,            ZERO},
  #endif
  #ifdef SF_ENABLE_PTT
  {PFVT_8BIT,  (uint16_t)&pf_data.ptt_idx,             (uint16_t)&pmDataPTTIdx,          0xFF,                (PTT_TRIG_VAR_SIZE<<8)+PFVB_DT0+PFVB_IDXA+PFVB_NOMAP, ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_data.ptt_cnt,             (uint16_t)&pmDataPTTCnt,          0xFF,                (PTT_TRIG_VAR_SIZE<<8)+PFVB_DT0+PFVB_IDXA+PFVB_NOMAP, ZERO}, 
  {PFVT_8BIT,  (uint16_t)&pf_data.ptt_fire,            (uint16_t)&pmDataPTTFire,         0xFF,                (PTT_TRIG_VAR_SIZE<<8)+PFVB_DT0+PFVB_IDXA+PFVB_TRIG,  ZERO},
  #endif

  #ifdef SF_ENABLE_DEV 
  {PFVT_16BIT, (uint16_t)&pf_data.dev_volt,            (uint16_t)&pmDataDevVolt,         0xFFFF,              PFVB_DT0,                       ZERO},
  {PFVT_16BIT, (uint16_t)&pf_data.dev_amp,             (uint16_t)&pmDataDevAmp,          0xFFFF,              PFVB_DT0,                       ZERO},
  {PFVT_16BIT, (uint16_t)&pf_data.dev_temp,            (uint16_t)&pmDataDevTemp,         0xFFFF,              PFVB_DT0,                       ZERO},
  {PFVT_16BIT, (uint16_t)&pf_data.dev_freq,            (uint16_t)&pmDataDevFreq,         0xFFFF,              PFVB_DT0,                       ZERO},
  {PFVT_16BIT, (uint16_t)&pf_data.dev_freq_cnt,        (uint16_t)&pmDataDevFreqCnt,      0xFFFF,              PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_16BIT, (uint16_t)&pf_data.dev_var,             (uint16_t)&pmDataDevVar,          0xFFFF,              (DEV_VAR_MAX<<8)+PFVB_DT0+PFVB_IDXA,            ZERO}, 
  #endif

  #ifdef SF_ENABLE_PWM
  {PFVT_8BIT,  (uint16_t)&pf_data.pulse_fire,          (uint16_t)&pmDataPulseFire,       0xFF,                PFVB_DT0+PFVB_TRIG,             ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_data.pulse_step,          (uint16_t)&pmDataPulseStep,       0xFF,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_data.pulse_data,          (uint16_t)&pmDataPulseData,       0xFF,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_data.pulse_dir_cnt,       (uint16_t)&pmDataPulseDirCnt,     0xFF,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_data.pulse_bank_cnt,      (uint16_t)&pmDataPulseBankCnt,    0xFF,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_data.pulse_trig_delay_cnt,(uint16_t)&pmDataPulseTrigDelayCnt,0xFF,               PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_data.pulse_post_delay_cnt,(uint16_t)&pmDataPulsePostDelayCnt,0xFF,               PFVB_DT0+PFVB_NOMAP,            ZERO},
  
  {PFVT_8BIT,  (uint16_t)&pf_data.pwm_state,           (uint16_t)&pmDataPWMState,        0xFF,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_data.pwm_loop_cnt,        (uint16_t)&pmDataPWMLoopCnt,      0xFF,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_data.pwm_loop_max,        (uint16_t)&pmDataPWMLoopMax,      0xFF,                PFVB_DT0+PFVB_NOMAP,            ZERO},
  #endif

  #ifdef SF_ENABLE_PPM  
  {PFVT_8BIT,  (uint16_t)&pf_data.ppm_idx,             (uint16_t)&pmDataPPMIdx,          0xFF,                (OUTPUT_MAX<<8)+PFVB_DT0+PFVB_NOMAP+PFVB_IDXA,  ZERO},
  #endif
  
  #ifdef SF_ENABLE_MAL
  {PFVT_16BIT, (uint16_t)&pf_data.mal_trig,            (uint16_t)&pmDataMALTrig,         0xFFFF,              (MAL_PROGRAM_MAX<<8)+PFVB_DT0+PFVB_IDXA+PFVB_TRIG,ZERO},
  #endif
  
  // =============== pf_prog vars = +128
 
  {PFVT_8BIT,  (uint16_t)&pf_prog.req_tx_push,         (uint16_t)&pmProgTXPush,          ONE,                 PFVB_DT1+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_prog.req_tx_echo,         (uint16_t)&pmProgTXEcho,          ONE,                 PFVB_DT1+PFVB_NOMAP,            ONE},
  {PFVT_8BIT,  (uint16_t)&pf_prog.req_tx_promt,        (uint16_t)&pmProgTXPromt,         ONE,                 PFVB_DT1+PFVB_NOMAP,            ONE},
  
  {PFVT_32BIT, (uint16_t)&pf_prog.sys_time_ticks,      (uint16_t)&pmProgSysTimeTicks,    ZERO,                PFVB_DT1+PFVB_NOMAP,            ZERO},
  {PFVT_32BIT, (uint16_t)&pf_prog.sys_time_ssec,       (uint16_t)&pmProgSysTimeSsec,     ZERO,                PFVB_DT1+PFVB_NOMAP,            ZERO},

  #ifdef SF_ENABLE_LCD
  {PFVT_8BIT,  (uint16_t)&pf_prog.lcd_menu_state,      (uint16_t)&pmProgLcdMenuState,    0xFF,                PFVB_DT1+PFVB_NOMAP,            ZERO},
  {PFVT_16BIT, (uint16_t)&pf_prog.lcd_menu_mul,        (uint16_t)&pmProgLcdMenuMul,      0xFFFF,              PFVB_DT1+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_prog.lcd_menu_idx,        (uint16_t)&pmProgLcdMenuIdx,      0xFF,                PFVB_DT1+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_prog.lcd_menu_value_idx,  (uint16_t)&pmProgLcdMenuValueIdx, 0xFF,                PFVB_DT1+PFVB_NOMAP,            ZERO},
  {PFVT_32BIT, (uint16_t)&pf_prog.lcd_menu_time_cnt,   (uint16_t)&pmProgLcdMenuTimeCnt,  ZERO,                PFVB_DT1+PFVB_NOMAP,            ZERO},
  #endif

  #ifdef SF_ENABLE_MAL
  {PFVT_8BIT,  (uint16_t)&pf_prog.mal_pc,              (uint16_t)&pmProgMALPc,           0xFF,                PFVB_DT1+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_prog.mal_state,           (uint16_t)&pmProgMALState,        0xFF,                PFVB_DT1+PFVB_NOMAP,            ZERO},
  {PFVT_16BIT, (uint16_t)&pf_prog.mal_var,             (uint16_t)&pmProgMALVar,          0xFFFF,              (OUTPUT_MAX<<8)+PFVB_DT1+PFVB_NOMAP+PFVB_IDXA,  ZERO},  
  #endif
  
  #ifdef SF_ENABLE_STV
  {PFVT_8BIT,  (uint16_t)&pf_prog.stv_state,           (uint16_t)&pmProgSTVState,        STV_STATE_ERROR_MIN, PFVB_DT1+PFVB_NOMAP,            STV_STATE_OKE},
  {PFVT_32BIT, (uint16_t)&pf_prog.stv_time_cnt,        (uint16_t)&pmProgSTVTimeCnt,      0xFFFF,              PFVB_DT1+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_prog.stv_mode_org,        (uint16_t)&pmProgSTVModeOrg,      0xFF,                PFVB_DT1+PFVB_NOMAP,            ZERO},
  {PFVT_8BIT,  (uint16_t)&pf_prog.stv_map_idx,         (uint16_t)&pmProgSTVMapIdx,       0xFF,                PFVB_DT1+PFVB_NOMAP,            ZERO},  
  #endif  

};

uint8_t Vars_getSize(void) {
	return sizeof(PF_VARS)/sizeof(uint16_t)/(PFVF_DEF+ONE);
}
uint16_t Vars_getBitsRaw(uint8_t idx) {
	return pgm_read_word(&(PF_VARS[idx][PFVF_BITS]));
}
uint16_t Vars_getValueMax(uint8_t idx) {
	return pgm_read_word(&(PF_VARS[idx][PFVF_MAX]));
}

boolean Vars_isTrigger(byte idx) {
  uint16_t bits = pgm_read_word(&(PF_VARS[idx][PFVF_BITS])) & PFVB_TRIG;
  if (bits > ZERO) {
    return true;
  }
  return false;
}

boolean Vars_isNolimit(byte idx) {
  uint16_t bits = pgm_read_word(&(PF_VARS[idx][PFVF_BITS])) & PFVB_NOLIMIT;
  if (bits > ZERO) {
    return true;
  }
  return false;
}

boolean Vars_isIndexA(byte idx) {
  if (idx > PF_VARS_SIZE) {
    return false;
  }
  uint16_t bits = pgm_read_word(&(PF_VARS[idx][PFVF_BITS])) & PFVB_IDXA;
  if (bits > ZERO) {
    return true;
  }
  return false;
}

boolean Vars_isIndexB(byte idx) {
  if (idx > PF_VARS_SIZE) {
    return false;
  }
  uint16_t bits = pgm_read_word(&(PF_VARS[idx][PFVF_BITS])) & PFVB_IDXB;
  if (bits > ZERO) {
    return true;
  }
  return false;
}

boolean Vars_isNomap(byte idx) {
  if (idx > PF_VARS_SIZE) {
    return true; // cannot map idx out of range
  }
  uint16_t bits = pgm_read_word(&(PF_VARS[idx][PFVF_BITS])) & PFVB_NOMAP;
  if (bits > ZERO) {
    return true;
  }
  return false;
}

boolean Vars_isBitSize32(byte idx) {
  uint16_t type = pgm_read_word(&(PF_VARS[idx][PFVF_TYPE]));
  if (type == PFVT_32BIT) {
    return true;
  }
  return false;
}

#ifdef SF_ENABLE_LCD
boolean Vars_isMenuSkip(byte idx) {
  uint16_t bits = pgm_read_word(&(PF_VARS[idx][PFVF_BITS])) & PFVB_NOMENU;
  if (bits > ZERO) {
    return true;
  }
  return false;
}
#endif

boolean Vars_isTypeConf(byte idx) {
  uint16_t bits = pgm_read_word(&(PF_VARS[idx][PFVF_BITS])) & (PFVB_DT0+PFVB_DT1);
  if (bits == ZERO) {
    return true;
  }
  return false;
}

boolean Vars_isTypeData(byte idx) {
  uint16_t bits = pgm_read_word(&(PF_VARS[idx][PFVF_BITS])) & (PFVB_DT0+PFVB_DT1);
  if (bits == PFVB_DT0) {
    return true;
  }
  return false;
}


boolean Vars_isTypeProg(byte idx) {
  uint16_t bits = pgm_read_word(&(PF_VARS[idx][PFVF_BITS])) & (PFVB_DT0+PFVB_DT1);
  if (bits == PFVB_DT1) {
    return true;
  }
  return false;
}

char* Vars_getName(uint8_t idx) {
  return UNPSTRA(&PF_VARS[idx][PFVF_NAME]);
}

uint8_t Vars_getIndexAMax(uint8_t idx) {
  uint16_t bits = pgm_read_word(&(PF_VARS[idx][PFVF_BITS])) & 0xFF00;
  uint8_t result = (bits >> 8) & 0x1F; // only use 5 bits
  return result;
}
uint8_t Vars_getIndexBMax(uint8_t idx) {
  uint16_t bits = pgm_read_word(&(PF_VARS[idx][PFVF_BITS])) & 0xFF00;
  uint8_t result = (bits >> 13);
  return result;
}
uint16_t Vars_getDefaultValue(uint8_t idx) {
  return pgm_read_word(&(PF_VARS[idx][PFVF_DEF]));
}

uint16_t Vars_getIndexFromName(char* name) {
  uint8_t i=ZERO;
  for (i=ZERO;i < PF_VARS_SIZE;i++) {
    if (strcmp_P(name, (const prog_char*)pgm_read_word(&(PF_VARS[i][PFVF_NAME]))) == ZERO) {
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
  uint16_t fieldType = pgm_read_word(&(PF_VARS[idx][PFVF_TYPE]));
  uint16_t value = ZERO;
  if (indexedA && idxA>=idxMaxA) {
    idxA = ZERO; // safty check for indexes
  }
  if (indexedB && idxB>=idxMaxB) {
    idxB = ZERO;
  }  
  if (fieldType == PFVT_16BIT) {
    uint16_t *valuePtr = (uint16_t*)pgm_read_word(&(PF_VARS[idx][PFVF_VAR]));
    if (indexedA) {
      if (indexedB) {
        valuePtr += idxMaxB * idxA + idxB;
      } else {
        valuePtr += idxA;
      }
    }
    value = *(valuePtr);
  } else if (fieldType == PFVT_8BIT) {
    uint8_t *valuePtr = (uint8_t*)pgm_read_word(&(PF_VARS[idx][PFVF_VAR]));
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
  uint32_t *valuePtr = (uint32_t*)pgm_read_word(&(PF_VARS[idx][PFVF_VAR]));
  if (indexedA && idxA>=idxMaxA) {
    idxA = ZERO; // safty check for indexes
  }
  if (indexedA) {
    valuePtr += idxA;
  }
  uint32_t value = *(valuePtr);
  return value;
}


void Vars_readConfig(void) {
  eeprom_read_block((void*)&pf_conf,(const void*)&pf_conf_eeprom,sizeof(pf_conf_struct));
}

void Vars_writeConfig(void) {
  eeprom_write_block((const void*)&pf_conf,(void*)&pf_conf_eeprom,sizeof(pf_conf_struct));
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


#ifdef SF_ENABLE_STV
uint8_t stv_is_variable_mapped(uint8_t idx,uint8_t idxA,boolean isMaxMap) {
  uint8_t maxMap = ZERO;
  if (isMaxMap) {
    maxMap = STV_MAX_MAP_MAX;
  } else {
    maxMap = STV_MIN_MAP_MAX;
  }
  for (uint8_t i=ZERO;i < maxMap;i++) {
    uint16_t v = ZERO;
    if (isMaxMap) {
      v = pf_conf.stv_max_map[i][QMAP_VAR];
    } else {
      v = pf_conf.stv_min_map[i][QMAP_VAR];
    }
    if (v==QMAP_VAR_NONE) {
      continue;
    }
    if (v != idx) {
      continue;
    }
    if (Vars_isIndexA(idx)==false) {
      return i;
    }
    uint16_t vi = ZERO;
    if (isMaxMap) {
      vi = pf_conf.stv_max_map[i][QMAP_VAR_IDX];
    } else {
      vi = pf_conf.stv_min_map[i][QMAP_VAR_IDX];
    }
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
  uint16_t fieldType = pgm_read_word(&(PF_VARS[idx][PFVF_TYPE]));
  uint16_t value_max = pgm_read_word(&(PF_VARS[idx][PFVF_MAX]));
  uint16_t value_min = ZERO;
  if ( pgm_read_word(&(PF_VARS[idx][PFVF_NAME])) == (uint16_t)&pmConfPWMOnCntA) {
    value_min = ONE;
  }
  if ( pgm_read_word(&(PF_VARS[idx][PFVF_NAME])) == (uint16_t)&pmConfPWMOnCntB) {
    value_min = ONE;
  }
  if ( pgm_read_word(&(PF_VARS[idx][PFVF_NAME])) == (uint16_t)&pmConfSWCDuty) {
    value_min = ONE;
  }
  #ifdef SF_ENABLE_PPM
  if ( pgm_read_word(&(PF_VARS[idx][PFVF_NAME])) == (uint16_t)&pmConfPPMDataLength) {
    value_min = ONE;
  }
  #endif
  #ifdef SF_ENABLE_PTC
  if ( pgm_read_word(&(PF_VARS[idx][PFVF_NAME])) == (uint16_t)&pmConfPTC0Mul) {
    value_min = ONE;
  }
  if ( pgm_read_word(&(PF_VARS[idx][PFVF_NAME])) == (uint16_t)&pmConfPTC1Mul) {
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
    uint16_t *valuePtr = (uint16_t*)pgm_read_word(&(PF_VARS[idx][PFVF_VAR]));
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
            uint16_t *valuePtr = (uint16_t*)pgm_read_word(&(PF_VARS[idx][PFVF_VAR]));
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
      uint8_t *valuePtr = (uint8_t*)pgm_read_word(&(PF_VARS[idx][PFVF_VAR]));
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
            uint8_t *valuePtr = (uint8_t*)pgm_read_word(&(PF_VARS[idx][PFVF_VAR]));
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
    uint32_t *valuePtr = (uint32_t*)pgm_read_word(&(PF_VARS[idx][PFVF_VAR]));
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
  uint16_t varName = pgm_read_word(&(PF_VARS[idx][PFVF_NAME]));
  if ( varName == (uint16_t)&pmConfPulseMode && trig==false) {
    Vars_resetData();
  }
  #ifdef SF_ENABLE_AVR
  if ( varName == (uint16_t)&pmConfAVRPin2Map) {
    if (pf_conf.avr_pin2_map == PIN2_TRIG_IN || pf_conf.avr_pin2_map == PIN2_FREQ_IN || pf_conf.avr_pin2_map == PIN2_FIRE_IN) {
    	Chip_io_int_pin(ZERO,ZERO);
    } else {
    	Chip_io_int_pin(ZERO,ONE);
    }
  }
  if ( varName == (uint16_t)&pmConfAVRPin3Map) {
    if (pf_conf.avr_pin3_map == PIN3_FREQ_IN || pf_conf.avr_pin3_map == PIN3_FIRE_IN) {
    	Chip_io_int_pin(ONE,ZERO);
    } else {
    	Chip_io_int_pin(ONE,ONE);
    }
  }
  #endif
  #ifdef SF_ENABLE_PWM
  if ( varName == (uint16_t)&pmConfPWMClock) {
    TCCR1B = pf_conf.pwm_clock & 7;
  }
  #endif
  
  if (trig==false) {
    return value; // no update of triggers 
  }
  
  #ifdef SF_ENABLE_STV
  uint8_t stvIdxMax = stv_is_variable_mapped(idx,idxA,true);
  uint8_t stvIdxMin = stv_is_variable_mapped(idx,idxA,false);
  if (stvIdxMax != QMAP_VAR_IDX_ALL) {
    uint16_t warningLevel = pf_conf.stv_max_map[stvIdxMax][QMAP_VALUE_A];
    uint16_t errorLevel   = pf_conf.stv_max_map[stvIdxMax][QMAP_VALUE_B];
    if (errorLevel > ZERO && value >= errorLevel && pf_prog.stv_state != STV_STATE_ERROR_MAX && pf_prog.stv_state != STV_STATE_ERROR_MIN) {
      pf_prog.stv_state            = STV_STATE_ERROR_MAX;
      pf_prog.stv_time_cnt         = millis() + (pf_conf.stv_warn_secs*1000);
      pf_prog.stv_map_idx          = stvIdxMax;
      if (pf_conf.stv_error_mode != 0xFF) {
        Vars_setValue(ONE,ZERO,ZERO,pf_conf.stv_error_mode);
      }
    } else if (warningLevel > ZERO && value >= warningLevel && pf_prog.stv_state == STV_STATE_OKE) {
      pf_prog.stv_state            = STV_STATE_WARNING_MAX;
      pf_prog.stv_time_cnt         = millis() + (pf_conf.stv_warn_secs*1000);
      pf_prog.stv_map_idx          = stvIdxMax;
      if (pf_conf.stv_warn_mode != 0xFF) {
        #ifdef SF_ENABLE_PWM
        pf_prog.stv_mode_org         = pf_conf.pulse_mode;
        #endif
        Vars_setValue(ONE,ZERO,ZERO,pf_conf.stv_warn_mode);
      }
    }
  }
  if (stvIdxMin != QMAP_VAR_IDX_ALL) {
    uint16_t warningLevel = pf_conf.stv_min_map[stvIdxMin][QMAP_VALUE_A];
    uint16_t errorLevel   = pf_conf.stv_min_map[stvIdxMin][QMAP_VALUE_B];
    if (errorLevel > ZERO && value <= errorLevel && pf_prog.stv_state != STV_STATE_ERROR_MIN && pf_prog.stv_state != STV_STATE_ERROR_MAX) {
      pf_prog.stv_state            = STV_STATE_ERROR_MIN;
      pf_prog.stv_time_cnt         = millis() + (pf_conf.stv_warn_secs*1000);
      pf_prog.stv_map_idx          = stvIdxMin;
      if (pf_conf.stv_error_mode != 0xFF) {
        Vars_setValue(ONE,ZERO,ZERO,pf_conf.stv_error_mode);
      }
    } else if (warningLevel > ZERO && value <= warningLevel && pf_prog.stv_state == STV_STATE_OKE) {
      pf_prog.stv_state            = STV_STATE_WARNING_MIN;
      pf_prog.stv_time_cnt         = millis() + (pf_conf.stv_warn_secs*1000);
      #ifdef SF_ENABLE_PWM
      pf_prog.stv_mode_org         = pf_conf.pulse_mode;
      #endif
      pf_prog.stv_map_idx          = stvIdxMin;
      if (pf_conf.stv_warn_mode != 0xFF) { 
        Vars_setValue(ONE,ZERO,ZERO,pf_conf.stv_warn_mode);
      }
    }
  } 
  #endif
  #ifdef SF_ENABLE_FRQ
  #ifdef SF_ENABLE_PWM
  if ( varName == (uint16_t)&pmCmdReqPWMFreq) {
    Freq_requestTrainFreq(value,QMAP_VAR_IDX_ALL,QMAP_VAR_IDX_ALL);
  }
  if ( varName == (uint16_t)&pmConfPWMDuty && pf_data.req_pwm_freq != ZERO) {
    Freq_requestTrainFreq(pf_data.req_pwm_freq,QMAP_VAR_IDX_ALL,QMAP_VAR_IDX_ALL);
  }
  #endif
  #endif
  #ifdef SF_ENABLE_MAL
  if ( varName == (uint16_t)&pmDataMALTrig) {  
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



// Init all config and data to init state.
void Vars_resetConfig(void) {  
  // Reset all config values
  uint8_t i=ZERO;
  for (i=ZERO;i < PF_VARS_SIZE;i++) {
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
  for (i=ZERO;i < OUTPUT_MAX;i++) {
    pf_conf.ppm_data_a[i]      = i + DEFAULT_PPM_DATA; // example demo data
    pf_conf.ppm_data_b[i]      = OUTPUT_MAX-i + DEFAULT_PPM_DATA;
  }
  #endif
  #ifdef SF_ENABLE_MAL
  uint8_t n=ZERO;
  for (n=ZERO;n < MAL_PROGRAM_MAX;n++) {
    for (i=ZERO;i < MAL_PROGRAM_SIZE;i++) {
      pf_conf.mal_program[i][n] = 0xFF;
    }
  }
  #endif
}

// Reset runtime data
void Vars_resetData(void) {  
  // Reset all data values
  uint8_t i=ZERO;
  for (i=ZERO;i < PF_VARS_SIZE;i++) {
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

  // Reset cutom pwm data
  #ifdef SF_ENABLE_PWM
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
  uint8_t  pfVersion    = eeprom_read_byte((uint8_t*)ZERO);
  uint16_t pfStructSize = eeprom_read_word((uint16_t*)ONE);
  if (pfVersion == PULSE_FIRE_VERSION && pfStructSize == sizeof(pf_conf_struct)) {
    Vars_readConfig();
  } else {
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


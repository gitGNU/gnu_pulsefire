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

// Include singleton
#ifndef _STRINGS_H
#define _STRINGS_H

#include "vars.h"

// Strings
extern const char pmPulseFire[];
extern const char pmPromt[];
extern const char pmDone[];
#ifdef SF_ENABLE_LCD
extern const char pmLcdSelect[];
extern const char pmLcdSelectIndex[];
extern const char pmLcdSelectOption[];
extern const char pmLcdSelectValue[];
extern const char pmLcdSelectedOption[];
extern const char pmLcdMultiply[];
extern const char pmLcdValue[];
extern const char pmLcdSoftStart[];
extern const char pmLcdSTVWarning[];
extern const char pmLcdSTVError[];
extern const char pmLcdSTVMin[];
extern const char pmLcdSTVMax[];
extern const char pmDataLcdInput[];
extern const char pmDataLcdPage[];
extern const char pmDataLcdRedraw[];
extern const char pmDataLcdMenuState[];
extern const char pmDataLcdMenuMul[];
extern const char pmDataLcdMenuIdx[];
extern const char pmDataLcdMenuValueIdx[];
extern const char pmDataLcdMenuTimeCnt[];
#endif
extern const char pmCmdUnknown[];
extern const char pmCmdHelpStart[];
extern const char pmCmdHelp[];
extern const char pmCmdSave[];
extern const char pmCmdResetConfig[];
extern const char pmCmdResetData[];
extern const char pmCmdResetChip[];
extern const char pmCmdInfoConf[];
extern const char pmCmdInfoData[];
extern const char pmCmdInfoFreq[];
extern const char pmCmdInfoFreqData[];
extern const char pmCmdInfoPPM[];
extern const char pmCmdInfoPWM[];
extern const char pmCmdInfoPWMData[];
extern const char pmCmdInfoPWMSize[];
extern const char pmCmdInfoChip[];
extern const char pmCmdInfoVars[];
extern const char pmCmdInfoVarsMap[];
extern const char pmCmdInfoVarsPrefix[];
extern const char pmCmdReqTrigger[];
extern const char pmCmdReqDoc[];
extern const char pmConfSysId[];
extern const char pmConfSysPass[];
extern const char pmConfSysVvmMap[];
extern const char pmConfSysVvlMap[];
extern const char pmConfSpiClock[];
extern const char pmConfSpiChips[];
extern const char pmConfLCDSize[];
extern const char pmConfLCDDefp[];
extern const char pmConfLCDMode[];
extern const char pmConfLCDHcd[];
extern const char pmConfLCDPlp[];
extern const char pmConfIntMap[];
extern const char pmConfInt0Mode[];
extern const char pmConfInt0Trig[];
extern const char pmConfInt0FreqMul[];
extern const char pmConfInt1Mode[];
extern const char pmConfInt1Trig[];
extern const char pmConfInt1FreqMul[];
extern const char pmConfDicMap[];
extern const char pmConfDicEnable[];
extern const char pmConfDicInv[];
extern const char pmConfDicSync[];
extern const char pmConfDicMux[];
extern const char pmConfAdcMap[];
extern const char pmConfAdcEnable[];
extern const char pmConfAdcJitter[];
extern const char pmConfAVRPin2Map[];
extern const char pmConfAVRPin3Map[];
extern const char pmConfAVRPin4Map[];
extern const char pmConfAVRPin5Map[];
#ifdef SF_ENABLE_AVR_MEGA
extern const char pmConfMegaPortA[];
extern const char pmConfMegaPortC[];
#endif
#ifdef SF_ENABLE_VSC0
extern const char pmConfVsc0Mode[];
extern const char pmConfVsc0Time[];
extern const char pmConfVsc0Step[];
extern const char pmConfVsc0Map[];
extern const char pmDataVsc0TimeCnt[];
extern const char pmDataVsc0State[];
#endif
#ifdef SF_ENABLE_VSC1
extern const char pmConfVsc1Mode[];
extern const char pmConfVsc1Time[];
extern const char pmConfVsc1Step[];
extern const char pmConfVsc1Map[];
extern const char pmDataVsc1TimeCnt[];
extern const char pmDataVsc1State[];
#endif
#ifdef SF_ENABLE_PWM
extern const char pmConfPulseEnable[];
extern const char pmConfPulseMode[];
extern const char pmConfPulseSteps[];
extern const char pmConfPulseTrig[];
extern const char pmConfPulseBank[];
extern const char pmConfPulseDir[];
extern const char pmConfPulsePreDelay[];
extern const char pmConfPulsePreMul[];
extern const char pmConfPulsePostDelay[];
extern const char pmConfPulsePostMul[];
extern const char pmConfPulsePostHold[];
extern const char pmConfPulseInitA[];
extern const char pmConfPulseInitB[];
extern const char pmConfPulseMaskA[];
extern const char pmConfPulseMaskB[];
extern const char pmConfPulseInvA[];
extern const char pmConfPulseInvB[];
extern const char pmConfPulseFireMode[];
extern const char pmConfPulseHoldMode[];
extern const char pmConfPulseHoldAuto[];
extern const char pmConfPulseHoldAutoClr[];
extern const char pmConfPulseFireMap[];
extern const char pmConfPulseHoldMap[];
extern const char pmConfPulseResumeMap[];
extern const char pmConfPulseResetMap[];
extern const char pmConfPWMOnCntA[];
extern const char pmConfPWMOnCntB[];
extern const char pmConfPWMOffCntA[];
extern const char pmConfPWMOffCntB[];
extern const char pmConfPWMLoop[];
extern const char pmConfPWMLoopDelta[];
extern const char pmConfPWMClock[];
extern const char pmConfPWMReqIdx[];
extern const char pmConfPWMReqFreq[];
extern const char pmConfPWMReqDuty[];
extern const char pmConfPPMDataOffset[];
extern const char pmConfPPMDataLength[];
extern const char pmConfPPMDataA[];
extern const char pmConfPPMDataB[];
#endif
extern const char pmConfMALCode[];
#ifdef SF_ENABLE_MAL
extern const char pmConfMALOps[];
extern const char pmConfMALOpsFire[];
extern const char pmConfMALWait[];
#endif
#ifdef SF_ENABLE_PTC0
extern const char pmConfPTC0Run[];
extern const char pmConfPTC0Mul[];
extern const char pmConfPTC0Map[];
extern const char pmDataPTC0Cnt[];
extern const char pmDataPTC0RunCnt[];
extern const char pmDataPTC0MapIdx[];
extern const char pmDataPTC0MulCnt[];
extern const char pmDataPTC0Step[];
#endif
#ifdef SF_ENABLE_PTC1
extern const char pmConfPTC1Run[];
extern const char pmConfPTC1Mul[];
extern const char pmConfPTC1Map[];
extern const char pmDataPTC1Cnt[];
extern const char pmDataPTC1RunCnt[];
extern const char pmDataPTC1MapIdx[];
extern const char pmDataPTC1MulCnt[];
extern const char pmDataPTC1Step[];
#endif
#ifdef SF_ENABLE_PTT
extern const char pmConfPTT0Map[];
extern const char pmConfPTT1Map[];
extern const char pmConfPTT2Map[];
extern const char pmConfPTT3Map[];
extern const char pmDataPTTIdx[];
extern const char pmDataPTTCnt[];
extern const char pmDataPTTFire[];
extern const char pmDataPTTStep[];
#endif
#ifdef SF_ENABLE_CIP
extern const char pmConfCip0Clock[];
extern const char pmConfCip0Mode[];
extern const char pmConfCip0aOcr[];
extern const char pmConfCip0aCom[];
extern const char pmConfCip0bOcr[];
extern const char pmConfCip0bCom[];
extern const char pmConfCip0cOcr[];
extern const char pmConfCip0cCom[];
extern const char pmConfCip1Clock[];
extern const char pmConfCip1Mode[];
extern const char pmConfCip1aOcr[];
extern const char pmConfCip1aCom[];
extern const char pmConfCip1bOcr[];
extern const char pmConfCip1bCom[];
extern const char pmConfCip1cOcr[];
extern const char pmConfCip1cCom[];
extern const char pmConfCip2Clock[];
extern const char pmConfCip2Mode[];
extern const char pmConfCip2aOcr[];
extern const char pmConfCip2aCom[];
extern const char pmConfCip2bOcr[];
extern const char pmConfCip2bCom[];
extern const char pmConfCip2cOcr[];
extern const char pmConfCip2cCom[];
#endif
#ifdef SF_ENABLE_STV
extern const char pmConfSTVWarnSecs[];
extern const char pmConfSTVWarnMap[];
extern const char pmConfSTVErrorSecs[];
extern const char pmConfSTVErrorMap[];
extern const char pmConfSTVMaxMap[];
extern const char pmConfSTVMinMap[];
#endif
#ifdef SF_ENABLE_VFC
extern const char pmConfVFCInputMap[];
extern const char pmConfVFCOutputMap[];
#endif
extern const char pmDataSysUptime[];
extern const char pmDataSysTimeTicks[];
extern const char pmDataSysTimeCsec[];
extern const char pmDataSysUpTime[];
extern const char pmDataSysSpeed[];
extern const char pmDataSysBadIsr[];
extern const char pmDataAdcValue[];
extern const char pmDataAdcState[];
extern const char pmDataAdcStateIdx[];
extern const char pmDataAdcStateValue[];
extern const char pmDataInt0Freq[];
extern const char pmDataInt0FreqCnt[];
extern const char pmDataInt1Freq[];
extern const char pmDataInt1FreqCnt[];
extern const char pmDataDicValue[];
extern const char pmDataDocPort[];
extern const char pmDataSysInputTimeCnt[];
extern const char pmDataDevVolt[];
extern const char pmDataDevAmp[];
extern const char pmDataDevTemp[];
extern const char pmDataDevVar[];
extern const char pmDataPulseFire[];
extern const char pmDataPulseFireCnt[];
extern const char pmDataPulseFireFreq[];
extern const char pmDataPulseHoldFire[];
extern const char pmDataPulseResetFire[];
extern const char pmDataPulseResumeFire[];
extern const char pmDataPulseStep[];
extern const char pmDataPWMState[];
extern const char pmDataPWMLoopCnt[];
extern const char pmDataMALFire[];
extern const char pmChipVersion[];
extern const char pmChipConfMax[];
extern const char pmChipConfSize[];
extern const char pmChipFreeSram[];
extern const char pmChipCPUFreq[];
extern const char pmChipCPUType[];
extern const char pmChipCPUTypeAvr[];
extern const char pmChipCPUTypeAvrMega[];
extern const char pmChipCPUTypeArm7m[];
extern const char pmChipFlags[];
extern const char pmChipFlagPWM[];
extern const char pmChipFlagSPI[];
extern const char pmChipFlagCIP[];
extern const char pmChipFlagLCD[];
extern const char pmChipFlagADC[];
extern const char pmChipFlagPTC0[];
extern const char pmChipFlagPTC1[];
extern const char pmChipFlagPTT[];
extern const char pmChipFlagSTV[];
extern const char pmChipFlagVFC[];
extern const char pmChipFlagVSC0[];
extern const char pmChipFlagVSC1[];
extern const char pmChipFlagMAL[];
extern const char pmChipFlagDEBUG[];
extern const char pmChipBuild[];
extern const char pmChipBuildDate[];
extern const char pmChipName[];
extern const char pmChipNameStr[];
extern const char pmDataMALPc[];
extern const char pmDataMALState[];
extern const char pmDataMALVar[];
extern const char pmDataMALWaitCnt[];
extern const char pmDataTXPush[];
extern const char pmDataTXEcho[];
extern const char pmDataTXPromt[];
extern const char pmDataTXHex[];
extern const char pmDataSTVState[];
extern const char pmDataSTVWaitCnt[];
extern const char pmDataSTVMapIdx[];

// end include
#endif

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
extern const char pmGetSpaced[];
extern const char pmSetSpaced[];
extern const char pmPulseFire[];
extern const char pmPromt[];
extern const char pmDone[];
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
extern const char pmLPMWait[];
extern const char pmLPMStart[];
extern const char pmLPMCancel[];
extern const char pmCmdUnknown[];
extern const char pmCmdHelpStart[];
extern const char pmCmdHelp[];
extern const char pmCmdHelpMax[];
extern const char pmCmdHelpMap[];
extern const char pmCmdHelpIdx[];
extern const char pmCmdHelpBits[];
extern const char pmCmdSave[];
extern const char pmCmdResetConfig[];
extern const char pmCmdResetData[];
extern const char pmCmdResetChip[];
extern const char pmCmdInfoConf[];
extern const char pmCmdInfoData[];
extern const char pmCmdInfoProg[];
extern const char pmCmdInfoFreq[];
extern const char pmCmdInfoPPM[];
extern const char pmCmdInfoChip[];
extern const char pmCmdReqPulseFire[];
extern const char pmCmdReqPWMFreq[];
extern const char pmCmdReqAutoLPM[];
extern const char pmConfLCDSize[];
extern const char pmConfDicMap[];
extern const char pmConfAdcMap[];
extern const char pmConfAdcJitter[];
extern const char pmConfAVRPin2Map[];
extern const char pmConfAVRPin3Map[];
extern const char pmConfAVRPin4Map[];
extern const char pmConfAVRPin5Map[];
extern const char pmConfAVRPin18Map[];
extern const char pmConfAVRPin19Map[];
extern const char pmConfAVRPin47Map[];
extern const char pmConfAVRPin48Map[];
extern const char pmConfAVRPin49Map[];
extern const char pmConfSWCDelay[];
extern const char pmConfSWCMode[];
extern const char pmConfSWCSecs[];
extern const char pmConfSWCDuty[];
extern const char pmConfSWCTrig[];
extern const char pmConfPulseEnable[];
extern const char pmConfPulseMode[];
extern const char pmConfPulseSteps[];
extern const char pmConfPulseTrig[];
extern const char pmConfPulseDir[];
extern const char pmConfPulseBank[];
extern const char pmConfPulseInv[];
extern const char pmConfPulseTrigDelay[];
extern const char pmConfPulsePostDelay[];
extern const char pmConfPulseInitA[];
extern const char pmConfPulseInitB[];
extern const char pmConfPulseMaskA[];
extern const char pmConfPulseMaskB[];
extern const char pmConfPWMOnCntA[];
extern const char pmConfPWMOnCntB[];
extern const char pmConfPWMOffCntA[];
extern const char pmConfPWMOffCntB[];
extern const char pmConfPWMTuneCnt[];
extern const char pmConfPWMLoop[];
extern const char pmConfPWMLoopDelta[];
extern const char pmConfPWMClock[];
extern const char pmConfPWMDuty[];
extern const char pmConfPPMDataOffset[];
extern const char pmConfPPMDataLength[];
extern const char pmConfPPMDataA[];
extern const char pmConfPPMDataB[];
extern const char pmConfLPMStart[];
extern const char pmConfLPMStop[];
extern const char pmConfLPMSize[];
extern const char pmConfLPMRelayInv[];
extern const char pmConfMALProgram[];
extern const char pmConfPTC0Run[];
extern const char pmConfPTC1Run[];
extern const char pmConfPTC0Mul[];
extern const char pmConfPTC1Mul[];
extern const char pmConfPTC0Map[];
extern const char pmConfPTC1Map[];
extern const char pmConfPTT0Map[];
extern const char pmConfPTT1Map[];
extern const char pmConfPTT2Map[];
extern const char pmConfPTT3Map[];
extern const char pmProgDevVoltDot[];
extern const char pmProgDevAmpDot[];
extern const char pmProgDevTempDot[];
extern const char pmConfSTVWarnSecs[];
extern const char pmConfSTVWarnMode[];
extern const char pmConfSTVErrorSecs[];
extern const char pmConfSTVErrorMode[];
extern const char pmConfSTVMaxMap[];
extern const char pmConfSTVMinMap[];
extern const char pmConfVFCInputMap[];
extern const char pmConfVFCOutputMap[];
extern const char pmDataSysUptime[];
extern const char pmDataSysMainLoopCnt[];
extern const char pmDataLcdTimeCnt[];
extern const char pmDataLcdPage[];
extern const char pmDataLcdRedraw[];
extern const char pmDataAdcTimeCnt[];
extern const char pmDataAdcValue[];
extern const char pmDataAdcState[];
extern const char pmDataAdcStateIdx[];
extern const char pmDataAdcStateValue[];
extern const char pmDataDicTimeCnt[];
extern const char pmDataDicValue[];
extern const char pmDataDocPort[];
extern const char pmDataSysInputTimeCnt[];
extern const char pmDataSWCModeOrg[];
extern const char pmDataSWCSecsCnt[];
extern const char pmDataSWCDutyCnt[];
extern const char pmDataLPMState[];
extern const char pmDataLPMStartTime[];
extern const char pmDataLPMTotalTime[];
extern const char pmDataLPMResult[];
extern const char pmDataLPMLevel[];
extern const char pmDataLPMFreqSettle[];
extern const char pmDataLPMFreqStart[];
extern const char pmDataLPMFreqStop[];
extern const char pmDataLPMFreqStep[];
extern const char pmDataPTCSysCnt[];
extern const char pmDataPTC0Cnt[];
extern const char pmDataPTC1Cnt[];
extern const char pmDataPTC0RunCnt[];
extern const char pmDataPTC1RunCnt[];
extern const char pmDataPTC0MapIdx[];
extern const char pmDataPTC1MapIdx[];
extern const char pmDataPTC0MulCnt[];
extern const char pmDataPTC1MulCnt[];
extern const char pmDataPTTIdx[];
extern const char pmDataPTTCnt[];
extern const char pmDataPTTFire[];
extern const char pmDataDevVolt[];
extern const char pmDataDevAmp[];
extern const char pmDataDevTemp[];
extern const char pmDataDevFreq[];
extern const char pmDataDevFreqCnt[];
extern const char pmDataDevVar[];
extern const char pmDataPulseFire[];
extern const char pmDataPulseStep[];
extern const char pmDataPulseData[];
extern const char pmDataPulseBankCnt[];
extern const char pmDataPulseDirCnt[];
extern const char pmDataPulseTrigDelayCnt[];
extern const char pmDataPulsePostDelayCnt[];
extern const char pmDataPWMState[];
extern const char pmDataPWMLoopCnt[];
extern const char pmDataPWMLoopMax[];
extern const char pmDataPPMIdx[];
extern const char pmDataMALTrig[];
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
extern const char pmChipFlagLCD[];
extern const char pmChipFlagLPM[];
extern const char pmChipFlagPPM[];
extern const char pmChipFlagADC[];
extern const char pmChipFlagDIC[];
extern const char pmChipFlagDOC[];
extern const char pmChipFlagDEV[];
extern const char pmChipFlagPTC[];
extern const char pmChipFlagPTT[];
extern const char pmChipFlagSTV[];
extern const char pmChipFlagVFC[];
extern const char pmChipFlagFRQ[];
extern const char pmChipFlagSWC[];
extern const char pmChipFlagMAL[];
extern const char pmChipFlagDEBUG[];
extern const char pmChipBuild[];
extern const char pmChipBuildDate[];
extern const char pmChipName[];
extern const char pmChipNameStr[];
extern const char pmChipNameId[];
extern const char pmChipNameIdStr[];
extern const char pmFreqPWMData[];
extern const char pmProgSysTimeTicks[];
extern const char pmProgSysTimeSsec[];
extern const char pmProgLcdMenuState[];
extern const char pmProgLcdMenuMul[];
extern const char pmProgLcdMenuIdx[];
extern const char pmProgLcdMenuValueIdx[];
extern const char pmProgLcdMenuTimeCnt[];
extern const char pmProgMALPc[];
extern const char pmProgMALState[];
extern const char pmProgMALVar[];
extern const char pmProgTXPush[];
extern const char pmProgTXEcho[];
extern const char pmProgTXPromt[];
extern const char pmProgSTVState[];
extern const char pmProgSTVTimeCnt[];
extern const char pmProgSTVModeOrg[];
extern const char pmProgSTVMapIdx[];

// end include
#endif

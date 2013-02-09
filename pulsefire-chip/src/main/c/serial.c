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


#include "serial.h"

void Serial_print(char c) {
	Chip_out_serial(c);
}

void Serial_printHex(uint8_t argu) {
	uint8_t hn = argu >> 4;
	uint8_t ln = argu & 0x0F;
	if (hn<10) {
		Serial_print('0'+hn);
	} else {
		Serial_print('A'+(hn-10));
	}
	if (ln<10) {
		Serial_print('0'+ln);
	} else {
		Serial_print('A'+(ln-10));
	}
}

void Serial_printDec(int argu) {
	u16toa(argu,pf_data.unpstr_buff);
	Serial_printChar(pf_data.unpstr_buff);
}

void Serial_printChar(char* dstring) {
	while(*dstring != 0) {
		Chip_out_serial(*dstring);
		dstring++;
	}
}
void Serial_println(void) {
	Serial_print('\n');
}

// short cuts to make code a bit smaller
void Serial_printCharP(const char* argu) {
	Serial_printChar(UNPSTR(argu));
}

void Serial_println_done_P(const char* argu) {
	Serial_printCharP(argu);
	Serial_printCharP(pmSetSpaced);
	Serial_printCharP(pmDone);
	Serial_println();
}

void Serial_println_get_P2(const char* argu0,const char* argu1) {
	Serial_printCharP(argu0);
	Serial_printCharP(pmGetSpaced);
	Serial_printCharP(argu1);
	Serial_println();
}

void Serial_printVar(uint8_t idx,uint8_t idxA,uint16_t value) {
	boolean indexedA = Vars_isIndexA(idx);
	uint8_t idxMaxA  = Vars_getIndexAMax(idx);
	uint8_t req_tx_hex  = pf_data.req_tx_hex;
	if (indexedA) {
		if (idxA == QMAP_VAR_IDX_ALL) {
			for (uint8_t i=ZERO;i<idxMaxA;i++) {
				if (req_tx_hex == ZERO) {
					Serial_printChar(Vars_getName(idx));
				} else {
					Serial_printHex(idx);
					Serial_print('@');
				}
				if(i<10) {Serial_print('0');} Serial_printDec((int)i);
				Serial_printCharP(pmSetSpaced);
				Serial_printDec(value);
				Serial_println();
			}
		} else {
			if (req_tx_hex == ZERO) {
				Serial_printChar(Vars_getName(idx));
			} else {
				Serial_printHex(idx);
				Serial_print('@');
			}
			if(idxA<10) {Serial_print('0');} Serial_printDec((int)idxA);
			Serial_printCharP(pmSetSpaced);
			Serial_printDec(value);
			Serial_println();
		}
	} else {
		if (req_tx_hex == ZERO) {
			Serial_printChar(Vars_getName(idx));
		} else {
			Serial_printHex(idx);
			Serial_print('@');
		}
		Serial_printCharP(pmSetSpaced);
		Serial_printDec(value);
		Serial_println();
	}
}

void cmd_print_var_indexed(uint8_t i,uint8_t setIndexA) {
	if (setIndexA>=Vars_getIndexAMax(i)) {
		setIndexA = ZERO; // safty check for indexes
	}
	if (pf_data.req_tx_hex == ZERO) {
		Serial_printChar(Vars_getName(i));
	} else {
		Serial_printHex(i);
		Serial_print('@');
	}
	if(setIndexA<10) {Serial_print('0');}
	Serial_printDec((int)setIndexA);
	Serial_printCharP(pmGetSpaced);
	if (Vars_getBitType(i) == PFVT_32BIT) {
		Serial_print(Vars_getValue32(i,setIndexA));
	} else {
		if (Vars_isIndexB(i)==false)  {
			Serial_printDec(Vars_getValue(i,setIndexA,ZERO));
		} else {
			int idxB=ZERO;
			for (idxB=ZERO;idxB<Vars_getIndexBMax(i);idxB++) {
				uint16_t value = Vars_getValue(i,setIndexA,idxB);
				Serial_printDec(value);
				Serial_print(' ');
			}
		}
	}
	Serial_println();
}

// Print a PF_VARS variable
void cmd_print_var(uint8_t i,boolean limit_to_steps,boolean isSet) {
	boolean indexedA = Vars_isIndexA(i);
	if (indexedA==false) {
		if (pf_data.req_tx_hex == ZERO) {
			Serial_printChar(Vars_getName(i));
		} else {
			Serial_printHex(i);
			Serial_print('@');
		}
		if (isSet) {
			Serial_printCharP(pmSetSpaced);
		} else {
			Serial_printCharP(pmGetSpaced);
		}
		if (Vars_getBitType(i) == PFVT_32BIT) {
			uint32_t value = Vars_getValue32(i,0);
			u32toa(value,pf_data.unpstr_buff);
			Serial_printChar(pf_data.unpstr_buff);
		} else {
			Serial_printDec(Vars_getValue(i,0,0));
		}
		Serial_println();
	} else {
		int limit_index_max = Vars_getIndexAMax(i);
		if (limit_to_steps && Vars_isNolimit(i)==false) {
#ifdef SF_ENABLE_PWM
			limit_index_max = pf_conf.pulse_steps; // limit output of indexed variabled up to configed steps, except nolimit Vars_
#endif
		}
		uint8_t idxA=ZERO;
		for (idxA=ZERO;idxA<limit_index_max;idxA++) {
			cmd_print_var_indexed(i,idxA);
		}
	}
}

// Print long value
void cmd_print_info_value_long(const char* dstring,uint32_t value) {
	Serial_printCharP(dstring);
	Serial_printCharP(pmGetSpaced);
	u32toa(value,pf_data.unpstr_buff);
	Serial_printChar(pf_data.unpstr_buff);
	Serial_println();
}

// Print all calculated values
void cmd_print_info_freq(void) {  
#ifdef SF_ENABLE_PWM
	for (uint8_t i=ZERO;i<pf_conf.pulse_steps;i++) {
		Serial_printCharP(pmCmdInfoFreqData);
		if(i<10) {Serial_print('0');}
		Serial_printDec((int)i);
		Serial_printCharP(pmGetSpaced);
		u32toa(calc_pwm_freq(i),pf_data.unpstr_buff);
		Serial_printChar(pf_data.unpstr_buff);
		Serial_print(' ');
		Serial_printDec(calc_pwm_duty(i));
		Serial_print(' ');
		Serial_println();
	}
#endif
	Serial_println_done_P(pmCmdInfoFreq);
}

void cmd_print_info_chip(void) {

	Serial_printCharP(pmChipVersion);
	Serial_printCharP(pmGetSpaced);
	Serial_printDec(PULSE_FIRE_VERSION/10 % 10);
	Serial_print('.');
	Serial_printDec(PULSE_FIRE_VERSION % 10);
	Serial_println();

	Serial_printCharP(pmChipConfMax);
	Serial_printCharP(pmGetSpaced);
	Serial_printDec(CHIP_EEPROM_SIZE);
	Serial_println();

	cmd_print_info_value_long(pmChipConfSize,        sizeof(pf_conf_struct));
	cmd_print_info_value_long(pmChipFreeSram,        Chip_free_ram());
	cmd_print_info_value_long(pmChipCPUFreq,         F_CPU);
	Serial_println_get_P2(pmChipCPUType,             Chip_cpu_type());
	Serial_println_get_P2(pmChipName,                pmChipNameStr);
	Serial_println_get_P2(pmChipBuild,               pmChipBuildDate);

	Serial_printCharP(pmChipFlags);
	Serial_printCharP(pmGetSpaced);
#ifdef SF_ENABLE_PWM
	Serial_printCharP(pmChipFlagPWM);
#endif
#ifdef SF_ENABLE_SPI
	Serial_printCharP(pmChipFlagSPI);
#endif
#ifdef SF_ENABLE_CIP
	Serial_printCharP(pmChipFlagCIP);
#endif
#ifdef SF_ENABLE_LCD
	Serial_printCharP(pmChipFlagLCD);
#endif
#ifdef SF_ENABLE_ADC
	Serial_printCharP(pmChipFlagADC);
#endif
#ifdef SF_ENABLE_STV
	Serial_printCharP(pmChipFlagSTV);
#endif
#ifdef SF_ENABLE_PTC0
	Serial_printCharP(pmChipFlagPTC0);
#endif
#ifdef SF_ENABLE_PTC1
	Serial_printCharP(pmChipFlagPTC1);
#endif
#ifdef SF_ENABLE_PTT
	Serial_printCharP(pmChipFlagPTT);
#endif
#ifdef SF_ENABLE_VSC0
	Serial_printCharP(pmChipFlagVSC0);
#endif
#ifdef SF_ENABLE_VSC1
	Serial_printCharP(pmChipFlagVSC1);
#endif
#ifdef SF_ENABLE_VFC
	Serial_printCharP(pmChipFlagVFC);
#endif
#ifdef SF_ENABLE_MAL
	Serial_printCharP(pmChipFlagMAL);
#endif
#ifdef SF_ENABLE_DEBUG
	Serial_printCharP(pmChipFlagDEBUG);
#endif
	Serial_printCharP(Chip_cpu_type()); // also print cpu type as flag.(=last because of no space after string cpu type)
	Serial_println();
	Serial_println_done_P(pmCmdInfoChip);
}

// execute cmd with the supplied argument
void cmd_execute(char* cmd, char** args) {
	uint8_t i=ZERO;
	if ( strcmp(cmd,UNPSTR(pmCmdHelp)) == ZERO ) {
		if (pf_data.req_tx_promt == ONE) {
			Serial_printCharP(pmCmdHelpStart);
		}
		uint8_t i=ZERO;
		for (i=ZERO;i < PMCMDLIST_SIZE;i++) {
			// Remove unsupported cmds when flag is disabled.
#ifndef SF_ENABLE_PWM
			if ( Chip_pgm_readWord((const CHIP_PTR_TYPE*)&pmCmdList[i]) == (CHIP_PTR_TYPE)&pmCmdInfoPPM) { continue; }
			if ( Chip_pgm_readWord((const CHIP_PTR_TYPE*)&pmCmdList[i]) == (CHIP_PTR_TYPE)&pmCmdInfoPWM) { continue; }
#endif
#ifndef SF_ENABLE_MAL
			if ( Chip_pgm_readWord((const CHIP_PTR_TYPE*)&pmCmdList[i]) == (CHIP_PTR_TYPE)&pmConfMALCode) { continue; }
#endif
			Serial_printChar(UNPSTRA((const CHIP_PTR_TYPE*)&pmCmdList[i]));
			Serial_println();
		}
		for (i=ZERO;i < PF_VARS_SIZE;i++) {
			if (Vars_isTypeData(i)) {
				continue;
			}
			Serial_printChar(Vars_getName(i));
			Serial_println();
		}
		Serial_println_done_P(pmCmdHelp); // end all multi line output with <cmd>=done for parser

	} else if (strcmp(cmd,UNPSTR(pmCmdInfoVars)) == ZERO) {
		uint8_t filter = ZERO;
		if ( strcmp(args[ZERO],UNPSTR(pmCmdInfoVarsMap)) == ZERO ) {
			filter = ONE;
		}
		for (i=ZERO;i < PF_VARS_SIZE;i++) {
			if (filter==ONE && Vars_isNomap(i)==true) {
				continue; // only show mappable
			}
			Serial_printCharP(pmCmdInfoVarsPrefix);
			Serial_printChar(Vars_getName(i));
			Serial_printCharP(pmGetSpaced);
			Serial_printDec(i); // index id
			Serial_print(' ');
			Serial_printDec(Vars_getBitType(i));
			Serial_print(' ');
			Serial_printDec(Vars_getIndexAMax(i));
			Serial_print(' ');
			Serial_printDec(Vars_getIndexBMax(i));
			Serial_print(' ');
			Serial_printDec(Vars_getValueMax(i));
			Serial_print(' ');
			Serial_printDec(Vars_getBitsRaw(i));
			Serial_print(' ');
			Serial_printDec(Vars_getDefaultValue(i));
			Serial_println();
		}
		Serial_println_done_P(pmCmdInfoVars);
	} else if (strcmp(cmd,UNPSTR(pmCmdInfoConf)) == ZERO) {
		for (i=ZERO;i < PF_VARS_SIZE;i++) {
			if (Vars_isTypeData(i) == true) {
				continue;
			}
			cmd_print_var(i,args[0] == NULL,false);
		}
		Serial_println_done_P(pmCmdInfoConf);
	} else if (strcmp(cmd,UNPSTR(pmCmdInfoData)) == ZERO) {
		for (i=ZERO;i < PF_VARS_SIZE;i++) {
			if (Vars_isTypeData(i) == false) {
				continue;
			}
			if (args[0] != NULL && Vars_isPush(i)) {
				continue; // if argument then filter auto push data out.
			}
			cmd_print_var(i,false,false);
		}
		Serial_println_done_P(pmCmdInfoData);
	} else if (strcmp(cmd,UNPSTR(pmCmdInfoChip)) == ZERO) {
		cmd_print_info_chip();

	} else if (strcmp(cmd,UNPSTR(pmCmdInfoFreq)) == ZERO) {
		cmd_print_info_freq();

#ifdef SF_ENABLE_PWM
	} else if (strcmp(cmd,UNPSTR(pmCmdInfoPPM)) == ZERO) {
		for (i=ZERO;i < OUTPUT_MAX;i++) {
			Serial_printCharP(pmConfPPMDataA);
			if (i <= 9) { Serial_print('0'); }
			Serial_printDec((int)i);
			Serial_printCharP(pmGetSpaced);
			uint16_t data_row = pf_conf.ppm_data_a[i];
			int ii=OUTPUT_MAX-ONE;
			for (ii=OUTPUT_MAX-ONE;ii>=ZERO;ii-- ) {
				uint16_t out = (data_row >> ii) & ONE;
				if (out == ZERO) {
					Serial_print('0');
				} else {
					Serial_print('1');
				}
			}
			Serial_println();
		}
		for (i=ZERO;i < OUTPUT_MAX;i++) {
			Serial_printCharP(pmConfPPMDataB);
			if (i <= 9) { Serial_print('0'); }
			Serial_printDec((int)i);
			Serial_printCharP(pmGetSpaced);
			uint16_t data_row = pf_conf.ppm_data_b[i];
			int ii=OUTPUT_MAX-ONE;
			for (ii=OUTPUT_MAX-ONE;ii>=ZERO;ii-- ) {
				uint16_t out = (data_row >> ii) & ONE;
				if (out == ZERO) {
					Serial_print('0');
				} else {
					Serial_print('1');
				}
			}
			Serial_println();
		}
		Serial_println_done_P(pmCmdInfoPPM);

	} else if (strcmp(cmd,UNPSTR(pmCmdInfoPWM)) == ZERO) {
		Serial_printCharP(pmCmdInfoPWMSize);
		Serial_printCharP(pmGetSpaced);
		Serial_printDec(pf_data.pwm_data_size);
		Serial_println();
		for (i=ZERO;i < pf_data.pwm_data_size;i++) {
			uint16_t data_out = pf_data.pwm_data[i][PWM_DATA_OUT];
			uint16_t data_cnt = pf_data.pwm_data[i][PWM_DATA_CNT];
			Serial_printCharP(pmCmdInfoPWMData);
			if (i <= 9) { Serial_print('0'); }
			Serial_printDec(i);
			Serial_printCharP(pmGetSpaced);
			int ii=OUTPUT_MAX-ONE;
			for (ii=OUTPUT_MAX-ONE;ii>=ZERO;ii-- ) {
				uint16_t out = (data_out >> ii) & ONE;
				if (out == ZERO) {
					Serial_print('0');
				} else {
					Serial_print('1');
				}
			}
			Serial_print(' ');
			Serial_printDec(data_cnt);
			Serial_println();
		}
		Serial_println_done_P(pmCmdInfoPWM);

#endif

	} else if (strcmp(cmd,UNPSTR(pmCmdResetConfig)) == ZERO) {
		Vars_resetConfig();
		Vars_resetData();
		Serial_println_done_P(pmCmdResetConfig);
	} else if (strcmp(cmd,UNPSTR(pmCmdResetData)) == ZERO) {
		Vars_resetData();
		Serial_println_done_P(pmCmdResetData);
	} else if (strcmp(cmd,UNPSTR(pmCmdResetChip)) == ZERO) {
		Serial_println_done_P(pmCmdResetChip);
		Chip_reset();
	} else if (strcmp(cmd,UNPSTR(pmCmdSave)) == ZERO) {
		Vars_writeConfig();
		Serial_println_done_P(pmCmdSave);

	} else if (strcmp(cmd,UNPSTR(pmCmdReqTrigger)) == ZERO) {
		if (args[ZERO] == NULL) {
			Serial_printCharP(pmCmdReqTrigger);
			Serial_printCharP(pmGetSpaced);
			Serial_printDec(ZERO);
			Serial_println();
			return;
		}
		uint16_t idx = ZERO;
		if (args[ZERO][ZERO] <= '9') {
			idx = atou16(args[ZERO]);
		} else {
			idx = Vars_getIndexFromName(args[ZERO]);
		}
		if (idx > PF_VARS_SIZE || Vars_isTrigger(idx)==false) {
			Serial_printCharP(pmCmdReqTrigger);
			Serial_printCharP(pmGetSpaced);
			Serial_printDec(ZERO);
			Serial_println();
			return;
		}
		uint16_t idxA = ZERO;
		if (args[ONE] != NULL) { idxA = atou16(args[ONE]); }

		if (Vars_isIndexA(idx)==false) {
			idxA = QMAP_VAR_IDX_ALL;
		} else if (idxA > Vars_getIndexAMax(idx)) {
			idxA = QMAP_VAR_IDX_ALL;
		}
		Vars_setValue(idx,idxA,ZERO,ONE); // use normal so print fire change 
		Serial_printCharP(pmCmdReqTrigger);
		Serial_printCharP(pmSetSpaced);
		Serial_printDec(idx);
		Serial_print(' ');
		Serial_printDec(idxA);
		Serial_print(' ');
		Serial_printDec(ONE);
		Serial_println();

	} else if (strcmp(cmd,UNPSTR(pmCmdReqDoc)) == ZERO) {

		if (args[0] != NULL && args[1] != NULL) {
			uint8_t docIdx = Vars_getIndexFromPtr((CHIP_PTR_TYPE*)&pf_data.doc_port);
			uint16_t port = atou16(args[0]);
			uint16_t value = atou16(args[1]);
			Vars_setValue(docIdx,port,0,value);

			Serial_printCharP(pmCmdReqDoc);
			Serial_printDec(port);
			Serial_printCharP(pmSetSpaced);
			Serial_printDec(value);
			Serial_println();
		} else {
			Serial_println_done_P(pmCmdReqDoc);	
		}

	} else if (strcmp(cmd,UNPSTR(pmProgTXPush)) == ZERO) {
		Serial_printCharP(pmProgTXPush);
		if (args[0] == NULL) {
			Serial_printCharP(pmGetSpaced);
		} else {
			Serial_printCharP(pmSetSpaced);
			uint16_t push = atou16(args[0]);
			if (push == ZERO) {  pf_data.req_tx_push = ZERO;
			} else {             pf_data.req_tx_push = ONE;
			}
		}
		Serial_printDec((int)pf_data.req_tx_push);
		Serial_println();
	} else if (strcmp(cmd,UNPSTR(pmProgTXEcho)) == ZERO) {
		Serial_printCharP(pmProgTXEcho);
		if (args[0] == NULL) {
			Serial_printCharP(pmGetSpaced);
		} else {
			Serial_printCharP(pmSetSpaced);
			uint16_t echo = atou16(args[0]);
			if (echo == ZERO) {  pf_data.req_tx_echo = ZERO;
			} else {             pf_data.req_tx_echo = ONE;
			}
		}
		Serial_printDec((int)pf_data.req_tx_echo);
		Serial_println();
	} else if (strcmp(cmd,UNPSTR(pmProgTXPromt)) == ZERO) {
		Serial_printCharP(pmProgTXPromt);
		if (args[0] == NULL) {
			Serial_printCharP(pmGetSpaced);
		} else {
			Serial_printCharP(pmSetSpaced);
			uint16_t promt = atou16(args[0]);
			if (promt == ZERO) {  pf_data.req_tx_promt = ZERO;
			} else {              pf_data.req_tx_promt = ONE;
			}
		}
		Serial_printDec((int)pf_data.req_tx_promt);
		Serial_println();
	} else if (strcmp(cmd,UNPSTR(pmProgTXHex)) == ZERO) {
		Serial_printCharP(pmProgTXHex);
		if (args[0] == NULL) {
			Serial_printCharP(pmGetSpaced);
		} else {
			Serial_printCharP(pmSetSpaced);
			uint16_t promt = atou16(args[0]);
			if (promt == ZERO) {  pf_data.req_tx_hex = ZERO;
			} else {              pf_data.req_tx_hex = ONE;
			}
		}
		Serial_printDec((int)pf_data.req_tx_hex);
		Serial_println();

#ifdef SF_ENABLE_MAL
	} else if (strcmp(cmd,UNPSTR(pmConfMALCode)) == ZERO) {
		if (args[ZERO] == NULL) {
			Serial_printCharP(pmConfMALCode);
			Serial_printCharP(pmGetSpaced);
			for (uint16_t addr=ZERO;addr < MAL_CODE_SIZE;addr++) {
				Serial_printHex(pf_conf.mal_code[addr]);
			}
			Serial_println();
		} else {
			uint16_t base = atou16(args[0]);
			uint32_t res =  htou32(args[1]);
			if (base+3 < MAL_CODE_SIZE) {
				pf_conf.mal_code[base+0] = (uint8_t) (res >> 24) & 0xFF;
				pf_conf.mal_code[base+1] = (uint8_t) (res >> 16) & 0xFF;
				pf_conf.mal_code[base+2] = (uint8_t) (res >> 8 ) & 0xFF;
				pf_conf.mal_code[base+3] = (uint8_t) (res >> 0 ) & 0xFF;
			}
			Serial_printCharP(pmConfMALCode);
			Serial_printCharP(pmSetSpaced);
			for (uint16_t addr=ZERO;addr < MAL_CODE_SIZE;addr++) {
				Serial_printHex(pf_conf.mal_code[addr]);
			}
			Serial_println();
		}
#endif

	} else {
		// process all get/set properties
		boolean done = false;
		for (i=ZERO;i < PF_VARS_SIZE;i++) {
			if (Vars_isTypeData(i)) {
				continue;
			}
			if (strcmp(cmd,Vars_getName(i)) != ZERO) {
				continue;
			}
			done = true;

			if (args[0] == NULL) {
				cmd_print_var(i,false,false);
				break; // print get and done
			}

			if (Vars_isIndexA(i)==false) {
				if (Vars_getBitType(i) == PFVT_32BIT) {
					Vars_setValue32(i,ZERO,ZERO,atou32(args[0]));
				} else {
					Vars_setValueSerial(i,ZERO,ZERO,atou16(args[0]));
				}
			} else {
				if (Vars_isIndexB(i)==false) {
					if (args[1] == NULL) {
						uint8_t ii=ZERO;
						for (ii=ZERO;ii < Vars_getIndexAMax(i);ii++) {
							Vars_setValueSerial(i,ii,ZERO,atou16(args[0]));
						}
					} else {
						Vars_setValueSerial(i,atou16(args[1]),ZERO,atou16(args[0]));
					}
				} else {

					int bIdxSize = Vars_getIndexBMax(i);
					if (bIdxSize == 4 ) {
						uint16_t idxA         = atou16(args[0]);
						uint16_t qmap_to      = ZERO;
						if (args[1][0] <= '9') {
							qmap_to = atou16(args[1]);
						} else {
							qmap_to = Vars_getIndexFromName(args[1]);
						}
						uint16_t qmap_value_a = ZERO;
						uint16_t qmap_value_b = QMAP_VAR_NONE;
						uint16_t qmap_idx     = QMAP_VAR_IDX_ALL;
						if (Vars_isNomap(qmap_to)) { qmap_to = QMAP_VAR_NONE; }
						if (args[2] != NULL) { qmap_value_a     = atou16(args[2]); }
						if (args[3] != NULL) { qmap_value_b     = atou16(args[3]); }
						if (args[4] != NULL) { qmap_idx         = atou16(args[4]); }
						if (Vars_isIndexA(qmap_to)==false) {
							qmap_idx = QMAP_VAR_IDX_ALL;
						} else if (qmap_idx > Vars_getIndexAMax(qmap_to)) {
							qmap_idx = QMAP_VAR_IDX_ALL;
						}
						Vars_setValueSerial(i,idxA,0,qmap_to);
						Vars_setValueSerial(i,idxA,1,qmap_value_a);
						Vars_setValueSerial(i,idxA,2,qmap_value_b);
						Vars_setValueSerial(i,idxA,3,qmap_idx);
					} else {
						// other sizes unsupported currently
					}
				}
			}
			if (Vars_isIndexA(i)==false) {
				cmd_print_var(i,false,true); // Print set of value
			} else {
				if (Vars_isIndexB(i)) {
					cmd_print_var_indexed(i,atou16(args[0]));
				} else {
					if (args[1] == NULL) {
						uint8_t ii=ZERO;
						for (ii=ZERO;ii < Vars_getIndexAMax(i);ii++) {
							cmd_print_var_indexed(i,ii);
						}
					} else {
						cmd_print_var_indexed(i,atou16(args[1]));
					}
				}
			}
			break; // we can only exec 1 at the time
		}
		if (done == false) {
			Serial_printCharP(pmCmdUnknown);
			Serial_println();
		}
	}
}

// Parse the cmd from the cmd_buff
void cmd_parse(void) {
	if (pf_data.req_tx_echo == ONE) {
		Serial_printChar((char *)pf_data.cmd_buff);
		Serial_println();
	}
	uint8_t idx = ZERO;
	char *cmd, *ptr, *args[CMD_MAX_ARGS];
	if (strtok((char *)pf_data.cmd_buff,CMD_WHITE_SPACE) == NULL) {
		Serial_printCharP(pmPromt);
		return; // no command given so just print new promt.
	}
	cmd = (char *)pf_data.cmd_buff;
	while( (ptr = strtok(NULL,CMD_WHITE_SPACE)) != NULL) {
		args[idx] = ptr;
		if (++idx == (CMD_MAX_ARGS - ONE)) {
			break;
		}
	}
	args[idx] = NULL;
	cmd_execute(cmd,args);
	if (pf_data.req_tx_promt == ONE) {
		Serial_printCharP(pmPromt);
	}
}

void Serial_rx_int(uint8_t c) {
	if (c < 0x07 || c > 0x7E) {
		return; // only process ascii chars, this is workaround for one unknown byte on SOME com ports under windows.
	}
	if (pf_data.cmd_process == ZERO) {
		return; // skip serial data ??? maybe store in some buff and apppend to cmd_buff when cmd is ready.
	}
	if (pf_data.cmd_buff_idx > CMD_BUFF_SIZE) {
		pf_data.cmd_buff_idx = ZERO; // protect against to long input
	}
	if (c=='\b') {
		pf_data.cmd_buff[pf_data.cmd_buff_idx] = '\0';// backspace
		pf_data.cmd_buff_idx--;
		if (pf_data.req_tx_echo == ONE) {
			Chip_out_serial(' ');
			Chip_out_serial(c);
		}
	} else if (c=='\n') {
		pf_data.cmd_buff[pf_data.cmd_buff_idx] = '\0';// newline
		pf_data.cmd_buff_idx = ZERO;
		pf_data.cmd_process  = ZERO;
	} else {
		pf_data.cmd_buff[pf_data.cmd_buff_idx] = c;   // store in buffer
		pf_data.cmd_buff_idx++;
	}
}


// Check for data from console and put in cmd_buff
void Serial_loop(void) {
	if (pf_data.cmd_process==ZERO) {
		cmd_parse();
		pf_data.cmd_process=ONE;
	}
}

void Serial_setup(void) {
	pf_data.cmd_process  = ONE; // rm me
	pf_data.cmd_buff_idx = ZERO;
	//pf_data.send_buff_idx = ZERO;

	// delay is needed else we get junk on terminal.
	Chip_delay(100);
	Serial_println();Serial_printCharP(pmPromt);
	/*
	Chip_delay(1000);
	Serial_println();Serial_printCharP(pmPromt);
	Chip_delay(1000);
	Serial_println();Serial_printCharP(pmPromt);
	Chip_delay(1000);
	Serial_println();Serial_printCharP(pmPromt);
	*/
}


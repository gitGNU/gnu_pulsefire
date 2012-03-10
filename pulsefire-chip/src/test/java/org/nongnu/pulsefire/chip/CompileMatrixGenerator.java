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

package org.nongnu.pulsefire.chip;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Test setup to generate all build options.
 * 
 * @author Willem Cazander
 *
 */
public class CompileMatrixGenerator {

	static public void main(String[] argu) {
		CompileMatrixGenerator g = new CompileMatrixGenerator();
		
		StringBuffer buff = new StringBuffer();
		g.generateMakeEntries(buff);
		System.out.println(buff);
		
		StringBuffer buff2 = new StringBuffer();
		g.generateTableConfig(buff2);
		System.out.println(buff2);
	}
	
	List<String> chips = null;
	List<Long> speeds = null;
	List<String> tableList = null;
	
	public CompileMatrixGenerator() {
		tableList = new ArrayList<String>(200);
		
		chips = new ArrayList<String>(20);
		chips.add("atmega328p");
		chips.add("atmega168p");
		chips.add("atmega1280");
		chips.add("atmega2560");
		
		speeds = new ArrayList<Long>(20);
		speeds.add(16000000l);
		speeds.add(20000000l);
		speeds.add(8000000l);
	}
	
	/*
	public class BuildOption {
		public String name;
		public String mcu;
		public Integer speed;
		public String ispMcu;
		public String ispProg;
		public List<String> flags = new ArrayList<String>(20);
		public List<String> options = new ArrayList<String>(20);
	}
	*/
	
	public void generateMakeEntries(StringBuffer buff) {
		
		List<String> megaOnlyFlags = new ArrayList<String>(20);
		megaOnlyFlags.add("SF_ENABLE_GLCD");
		
		List<String> defaultFlags = new ArrayList<String>(20);
		defaultFlags.add("SF_ENABLE_PWM");
		defaultFlags.add("SF_ENABLE_PPM");
		defaultFlags.add("SF_ENABLE_ADC");
		defaultFlags.add("SF_ENABLE_DIC");
		defaultFlags.add("SF_ENABLE_DOC");
		defaultFlags.add("SF_ENABLE_DEV");
		defaultFlags.add("SF_ENABLE_PTC");
		defaultFlags.add("SF_ENABLE_PTT");
		defaultFlags.add("SF_ENABLE_STV");
		defaultFlags.add("SF_ENABLE_VFC");
		defaultFlags.add("SF_ENABLE_SWC");
		defaultFlags.add("SF_ENABLE_MAL");
		defaultFlags.add("SF_ENABLE_LPM");
		
		List<String> matixFlags = new ArrayList<String>(20);
		matixFlags.add("SF_ENABLE_LCD");
		matixFlags.add("SF_ENABLE_EXT_LCD");
		matixFlags.add("SF_ENABLE_EXT_OUT");
		
		for (String chip:chips) {
			for (Long speed:speeds) {
				if (speed==8000000l && chip.endsWith("8p")==false) {
					continue; // only do 328p and 168p on 8Mhz
				}
				int idx = 0;
				
				for (int i=0;i<=matixFlags.size();i++) {
					List<String> flags = new ArrayList<String>(20);
					flags.addAll(defaultFlags);
					flags.addAll(matixFlags.subList(0, i));
					printMakeEntry(buff,chip,idx++,speed,flags);
					
					if (flags.contains("SF_ENABLE_EXT_OUT")) {
						flags.add("SF_ENABLE_EXT_OUT_16BIT");
						printMakeEntry(buff,chip,idx++,speed,flags);
					}
					if (flags.contains("SF_ENABLE_LCD") && flags.contains("SF_ENABLE_EXT_LCD")) {
						flags.add("SF_ENABLE_EXT_LCD_DIC");
						printMakeEntry(buff,chip,idx++,speed,flags);
						flags.add("SF_ENABLE_EXT_LCD_DOC");
						printMakeEntry(buff,chip,idx++,speed,flags);
						flags.add("SF_ENABLE_EXT_LCD_DOC_16BIT");
						printMakeEntry(buff,chip,idx++,speed,flags);
					}
				}
			}
		}
	}
	
	private void printMakeEntry(StringBuffer buff,String mcu,int idx,long speed,List<String> flags) {
		DecimalFormat idxFormat = new DecimalFormat("000");
		String target = mcu+"-"+(speed/1000000)+idxFormat.format(idx);
		String prefix = target+": ";
		buff.append(prefix+"MCU = "+mcu+"\n");
		buff.append(prefix+"F_CPU = "+speed+"\n");
		buff.append(prefix+"CFLAGS += -mmcu=$(MCU)"+"\n");
		for (String flag:flags) {
			buff.append(prefix+"PFLAGS += -D"+flag+"\n");	
		}
		buff.append(prefix+"TARGET = "+target+"\n");
		buff.append(prefix+"$(TPATH)/"+target+"/pulsefire.hex"+"\n");
		buff.append("$(TPATH)/"+target+"/pulsefire.hex:"+"\n");
		buff.append("\tmkdir -p $(TPATH)/$(TARGET)"+"\n");
		buff.append("\t$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)"+"\n");
		buff.append("\t$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex"+"\n");
		buff.append("\n");
		buff.append(target+"-isp: "+target+"\n");
		buff.append(target+"-isp: TARGET = "+target+"\n");
		String ispMcu = target;
		String ispProg = target;
		if ("atmega328p".equals(mcu)) {
			ispMcu = "328p";
			ispProg = "arduino";
		} else if ("atmega168p".equals(mcu)) {
			ispMcu = "168p";
			ispProg = "arduino";			
		} else if ("atmega1280".equals(mcu)) {
			ispMcu = "m1280";
			ispProg = "arduino";
		} else if ("atmega2560".equals(mcu)) {
			ispMcu = "m2560";
			ispProg = "stk500v2";
		}
		buff.append(target+"-isp: ISP_MCU = "+ispMcu+"\n");
		buff.append(target+"-isp: ISP_PROG = "+ispProg+"\n");
		buff.append(target+"-isp: avr-flash"+"\n");
		buff.append("\n");
		
		StringBuffer tableBuff = new StringBuffer();
		tableBuff.append(target);
		tableBuff.append(",");
		tableBuff.append(speed);
		tableBuff.append(",");
		for (String flag:flags) {
			tableBuff.append(flag);
			tableBuff.append("&");
		}
		tableBuff.append("\n");
		tableList.add(tableBuff.toString());
	}
	
	
	
	public void generateTableConfig(StringBuffer buff) {
		buff.append("# Table config\n");
		buff.append("# autogenerated for pulsefire.\n");
		buff.append("# date: "+new Date()+"\n");
		buff.append("\n");
		
		buff.append("speed=");
		for (Long speed:speeds) {
			buff.append(speed);
			buff.append(",");
		}
		buff.append("\n");
		
		buff.append("chips=");
		for (String chip:chips) {
			buff.append(chip);
			buff.append(",");
		}
		buff.append("\n");
		
		
		for (String entry:tableList) {
			buff.append(entry);
		}
		buff.append("# END\n");
	}
}

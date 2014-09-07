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

package org.nongnu.pulsefire.wire;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * MalCommand is byte data storage and encode/decoder for mal opcodes.
 * 
 * @author Willem Cazander
 */
public class MalCommand implements Cloneable {

	public enum CmdType {
		LOAD,
		EXTENDED,
		RESERVED,
		LAST_CMD
	}
	public enum ValueType {
		RAW_VALUE,
		PROG_VALUE,
		PF_VALUE,
		PF_VALUE_SET
	}
	public enum VarIndex {
		VAR_0,VAR_1,VAR_2,VAR_3,
		VAR_4,VAR_5,VAR_6,VAR_7,
		VAR_8,VAR_9,VAR_10,VAR_11,
		VAR_12,VAR_13,VAR_14,VAR_15
	}
	public enum ExtType {
		VOP,
		STOP,
		GOTO,
		IF,
		ENDIF
	}
	public enum ExtOpVar {
		ADD			("+"),
		SUBTRACT	("-"),
		MULTIPLY	("*"),
		DIVIDE		("/"),
		AND			("&"),
		OR			("|");
		
		private String charCode = null;
		private ExtOpVar(String charCode) {
			this.charCode=charCode;
		}
		public String getCharCode() {
			return charCode;
		}
	}
	public enum ExtOpIf {
		EQUAL				("=="),
		NOT_EQUAL			("!="),
		GREATER_THEN		(">"),
		SMALLER_THEN		("<"),
		GREATER_OR_EQUAL	("=>"),
		SMALLER_OR_EQUAL	("<=");
		
		private String charCode = null;
		private ExtOpIf(String charCode) {
			this.charCode=charCode;
		}
		public String getCharCode() {
			return charCode;
		}
	}
	
	private List<Byte> opcodes = new ArrayList<Byte>(4);
	
	// cmd = First byte
	private CmdType cmdType = null;
	private ValueType valueType = null;
	private VarIndex varIndex = null;
	// cmd_ext = second byte
	private ExtType extType = null;
	private int extOp = 0;
	// cmd_argu = 2/3 byte 
	private int cmdArgu = 0;
	// 3/4 byte in cmd
	private int cmdArguIdx = 0; // only used in PF_VALUE and PF_VALUE_SET
	// meta data for ui
	private int tabIndent = 0;
	private static int tabIndentParse = 0;
	
	/**
	 * Set basic value and compiles so we have working command.
	 */
	public void init() {
		cmdType = CmdType.LOAD;
		valueType = ValueType.RAW_VALUE;
		varIndex = VarIndex.VAR_0;
		extType = ExtType.VOP;
		compile(); // fill opcodes
	}
	
	public boolean parse(Iterator<Byte> data) {
		
		if (data.hasNext()==false) {
			return false;
		}
		tabIndent = tabIndentParse;
		Byte opcode = data.next();
		opcodes.add(opcode);
		int value_type = (opcode >> 4) & 3;
		int var_idx    =  opcode & 0x0F;
		int cmd_type   = (opcode >> 6) & 3;
		int ext_type   = 0;
		int ext_op     = 0;
		
		if (data.hasNext()==false) {
			return false;
		}
		cmdType = CmdType.values()[cmd_type];
		valueType = ValueType.values()[value_type];
		varIndex = VarIndex.values()[var_idx];
		
		if (cmd_type==1) {
			opcode = data.next();
			opcodes.add(opcode);
			ext_type        = (opcode >> 4) & 0x0F;
			ext_op          =  opcode & 0x0F;
			if (ext_type < ExtType.values().length) {
				extType = ExtType.values()[ext_type];
			}
			extOp = ext_op;
		} else if (cmd_type==3) {
			opcode = data.next();
			opcodes.add(opcode);
			return true;
		}
		if (ext_type==3) {
			tabIndentParse++;
		}
		if (ext_type==4) {
			tabIndentParse--;
			tabIndent = tabIndentParse;
			return true; // end if
		}
		if (data.hasNext()==false) {
			return false;
		}
		opcode = data.next();
		opcodes.add(opcode);
		cmdArgu = opcode;
		
		if (value_type==0) {
			opcode = data.next();
			opcodes.add(opcode);
			cmdArgu = (cmdArgu << 8) + opcode;
		} else if (value_type==1) {
			//value = pf_prog.mal_var[cmd_argu];
		} else if (value_type==2) {
			CommandName cmd = CommandName.valueOfMapIndex(cmdArgu);
			if (cmd.isIndexedA()) {
				opcode = data.next();
				opcodes.add(opcode);
				cmdArguIdx = opcode;
			}
		} else {
			CommandName cmd = CommandName.valueOfMapIndex(cmdArgu);
			if (cmd.isIndexedA()) {
				opcode = data.next();
				opcodes.add(opcode);
				cmdArguIdx = opcode;
			}
		}
		return true;
	}
	
	public void compile() {
		
		opcodes.clear();
		
		byte opcode = 0;
		opcode += getVarIndex().ordinal();
		opcode += getValueType().ordinal() << 4;
		opcode += getCmdType().ordinal() << 6;
		opcodes.add(opcode);
		
		switch (cmdType) {
		case LOAD:
			switch (valueType) {
			case RAW_VALUE:
				opcode = new Integer(getCmdArgu() >> 8).byteValue();
				opcodes.add(opcode);
				opcode = new Integer(getCmdArgu() & 0xFF).byteValue();
				opcodes.add(opcode);
				break;
			case PF_VALUE:
			case PF_VALUE_SET:
				opcode = new Integer(getCmdArgu() & 0xFF).byteValue();
				opcodes.add(opcode);
				CommandName cmd = CommandName.valueOfMapIndex(getCmdArgu());
				if (cmd.isIndexedA()) {
					opcode = new Integer(getCmdArguIdx() & 0xFF).byteValue();
					opcodes.add(opcode);
				}
				break;
			default:
				opcode = new Integer(getCmdArgu() & 0xFF).byteValue();
				opcodes.add(opcode);
				break;
			}
			break;
		case EXTENDED:
			if (getExtType()==null) {
				setExtType(ExtType.STOP); // default to stop
			}
			opcode = 0;
			opcode += getExtOp();
			opcode += getExtType().ordinal() << 4;
			opcodes.add(opcode);
			switch (extType) {
			case VOP:
				opcode = new Integer(getCmdArgu()).byteValue();
				opcodes.add(opcode);
				switch (valueType) {
				case RAW_VALUE:
					opcode = new Integer(getCmdArgu() >> 8).byteValue();
					opcodes.add(opcode);
					break;
				case PF_VALUE:
				case PF_VALUE_SET:
					CommandName cmd = CommandName.valueOfMapIndex(getCmdArgu());
					if (cmd.isIndexedA()) {
						opcode = new Integer(getCmdArguIdx() & 0xFF).byteValue();
						opcodes.add(opcode);
					}
					break;
				default:
					break;
				}
				break;
			case STOP:
				break;
			case GOTO:
				opcode = new Integer(getCmdArgu() >> 8).byteValue();
				opcodes.add(opcode);
				opcode = new Integer(getCmdArgu()).byteValue();
				opcodes.add(opcode);
				break;
			case IF:
				opcode = new Integer(getCmdArgu()).byteValue();
				opcodes.add(opcode);
				switch (valueType) {
				case RAW_VALUE:
					opcode = new Integer(getCmdArgu() >> 8).byteValue();
					opcodes.add(opcode);
					break;
				case PF_VALUE:
				case PF_VALUE_SET:
					CommandName cmd = CommandName.valueOfMapIndex(getCmdArgu());
					if (cmd.isIndexedA()) {
						opcode = new Integer(getCmdArguIdx() & 0xFF).byteValue();
						opcodes.add(opcode);
					}
					break;
				default:
					break;
				}
				break;
			case ENDIF:
				break;
			}
			break;
		case RESERVED:
			break;
		case LAST_CMD:
			opcodes.add(new Integer(0xFF).byteValue());
			break;
		}
	}
	
	public String toString() {
		StringBuilder buff = new StringBuilder(100);
		for (int i=0;i<tabIndent;i++) {
			buff.append("    ");
		}
		String malVarIndex = ""+varIndex;
		if (cmdType==null) {
			return "cmdType=null";
		}
		switch (cmdType) {
		case LOAD:
			buff.append("LOAD ");
			switch (valueType) {
			case RAW_VALUE:
				buff.append(malVarIndex);
				buff.append("=");
				buff.append(cmdArgu);
				break;
			case PROG_VALUE:
				buff.append(malVarIndex);
				buff.append("=");
				buff.append("VAR_");
				buff.append(cmdArgu);
				break;
			case PF_VALUE:
				buff.append(varIndex);
				buff.append("=");
				CommandName cmdA = CommandName.valueOfMapIndex(cmdArgu);
				buff.append(cmdA);
				if (cmdA.isIndexedA()) {
					buff.append('[');
					buff.append(cmdArguIdx);
					buff.append(']');
				}
				break;
			case PF_VALUE_SET:
				CommandName cmdB = CommandName.valueOfMapIndex(cmdArgu);
				buff.append(cmdB);
				if (cmdB.isIndexedA()) {
					buff.append('[');
					buff.append(cmdArguIdx);
					buff.append(']');
				}
				buff.append("=");
				buff.append(malVarIndex);
				break;
			}
			break;
		case EXTENDED:
			//buff.append("EXT_");
			if (extType==null) {
				return "extType=null";
			}
			switch (extType) {
			case VOP:
				buff.append("VOP ");
				buff.append(malVarIndex);
				buff.append("=");
				buff.append(malVarIndex);
				buff.append(" ");
				buff.append(ExtOpVar.values()[extOp].getCharCode());
				buff.append(" ");
				switch (valueType) {
				case RAW_VALUE:
					buff.append(cmdArgu);
					break;
				case PROG_VALUE:
					buff.append("VAR_");
					buff.append(cmdArgu);
					break;
				case PF_VALUE:
					CommandName cmd = CommandName.valueOfMapIndex(cmdArgu);
					buff.append(cmd);
					if (cmd.isIndexedA()) {
						buff.append('[');
						buff.append(cmdArguIdx);
						buff.append(']');
					}
					break;
				case PF_VALUE_SET:
					buff.append("reserved");
					break;
				}
				break;
			case STOP:
				buff.append("STOP");
				break;
			case GOTO:
				buff.append("GOTO ");
				buff.append(cmdArgu);
				break;
			case IF:
				buff.append("IF ( ");
				buff.append(malVarIndex);
				buff.append(" ");
				buff.append(ExtOpIf.values()[extOp].getCharCode());
				buff.append(" ");
				switch (valueType) {
				case RAW_VALUE:
					buff.append(cmdArgu);
					break;
				case PROG_VALUE:
					buff.append("VAR_");
					buff.append(cmdArgu);
					break;
				case PF_VALUE:
					CommandName cmd = CommandName.valueOfMapIndex(cmdArgu);
					buff.append(cmd);
					if (cmd.isIndexedA()) {
						buff.append('[');
						buff.append(cmdArguIdx);
						buff.append(']');
					}
					break;
				case PF_VALUE_SET:
					buff.append("reserved");
					break;
				}
				buff.append(" )");
				break;
			case ENDIF:
				buff.append("ENDIF");
				break;
			}
			break;
		case RESERVED:
			buff.append("RESERVED");
			break;
		case LAST_CMD:
			buff.append("END");
			break;
		}
		return buff.toString();
	}
	
	public String toStringHexOpcodes() {
		StringBuilder buff = new StringBuilder(16);
		for (Byte opcode:opcodes) {
			String hex = Integer.toHexString(opcode);
			if (hex.length()==1) {
				hex = "0"+hex;
			}
			if (hex.length()==8) {
				hex = hex.substring(6);
			}
			buff.append(hex);
		}
		return buff.toString();
	}
	
	/**
	 * @return the total opcode size in bytes.
	 */
	public int getOpcodeSize() {
		return opcodes.size();
	}
	
	/**
	 * @return the opcode bytes in order.
	 */
	public List<Byte> getOpcodes() {
		return opcodes;
	}

	/**
	 * @return the cmdType
	 */
	public CmdType getCmdType() {
		return cmdType;
	}

	/**
	 * @param cmdType the cmdType to set
	 */
	public void setCmdType(CmdType cmdType) {
		this.cmdType = cmdType;
	}

	/**
	 * @return the valueType
	 */
	public ValueType getValueType() {
		return valueType;
	}

	/**
	 * @param valueType the valueType to set
	 */
	public void setValueType(ValueType valueType) {
		this.valueType = valueType;
	}

	/**
	 * @return the varIndex
	 */
	public VarIndex getVarIndex() {
		return varIndex;
	}

	/**
	 * @param varIndex the varIndex to set
	 */
	public void setVarIndex(VarIndex varIndex) {
		this.varIndex = varIndex;
	}

	/**
	 * @return the extType
	 */
	public ExtType getExtType() {
		return extType;
	}

	/**
	 * @param extType the extType to set
	 */
	public void setExtType(ExtType extType) {
		this.extType = extType;
	}

	/**
	 * @return the extOp
	 */
	public int getExtOp() {
		return extOp;
	}

	/**
	 * @param extOp the extOp to set
	 */
	public void setExtOp(int extOp) {
		this.extOp = extOp;
	}

	/**
	 * @return the cmdArgu
	 */
	public int getCmdArgu() {
		return cmdArgu;
	}

	/**
	 * @param cmdArgu the cmdArgu to set
	 */
	public void setCmdArgu(int cmdArgu) {
		this.cmdArgu = cmdArgu;
	}

	/**
	 * @return the cmdArguIdx
	 */
	public int getCmdArguIdx() {
		return cmdArguIdx;
	}

	/**
	 * @param cmdArguIdx the cmdArguIdx to set
	 */
	public void setCmdArguIdx(int cmdArguIdx) {
		this.cmdArguIdx = cmdArguIdx;
	}

	/**
	 * Clone this for edit support.
	 * @see java.lang.Object#clone()
	 */
	@Override
	public MalCommand clone() {
		MalCommand cmd = new MalCommand();
		cmd.setCmdArguIdx(getCmdArguIdx());
		cmd.setCmdArgu(getCmdArgu());
		cmd.setCmdType(getCmdType());
		cmd.setExtOp(getExtOp());
		cmd.setExtType(getExtType());
		cmd.setValueType(getValueType());
		cmd.setVarIndex(getVarIndex());
		cmd.compile();
		return cmd;
	}
	
	
}

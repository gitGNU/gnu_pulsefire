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

public class CommandWire {
	
	static public Command decodeCommand(String line) {
		
		// parse based on commandType
		String commandName = null;
		CommandWireType commandType = CommandWireType.request_set;
		if (line.contains(Command.RESPONSE_GET)) {
			commandType = CommandWireType.response_get;
			String ss[] = line.split(Command.RESPONSE_GET);
			commandName = ss[0];
			line = ss[1];
		} else if (line.contains(Command.RESPONSE_SET)) {
			commandType = CommandWireType.response_set;
			String ss[] = line.split(Command.RESPONSE_SET);
			commandName = ss[0];
			line = ss[1];
		} else {
			if (line.contains(Command.SEPERATOR)) {
				commandName = line.substring(0,line.indexOf(Command.SEPERATOR));
				line = line.substring(line.indexOf(Command.SEPERATOR),line.length());
			} else {
				commandName = line;
				line = "";
			}
		}
		char last = commandName.charAt(commandName.length()-1);
		String idx = null;
		if (Character.isDigit(last)) {
			int index = commandName.length()-1;
			char lastOne = commandName.charAt(commandName.length()-2);
			if (Character.isDigit(lastOne)) {
				index--;
			}
			idx = commandName.substring(index); // append idx as argu0
			commandName = commandName.substring(0,index);
			
		}
		Command result = null;
		for (CommandName cmd:CommandName.values()) {
			if (cmd.isAliased()) {
				if (cmd.getAliasName().equals(commandName)) {
					result = new Command(cmd);
					break;
				}
			} else {
				if (cmd.name().equals(commandName)) {
					result = new Command(cmd);
				}
			}
		}
		if (result==null) {
			throw new IllegalStateException("Could not find command for: "+commandName);
		}
		
		if (result.getCommandName().isIndexedB()) {
			line = idx+" "+line;
		} else if (result.getCommandName().isIndexedA()) {
			line = line+" "+idx;
		}
		
		result.setType(commandType);
		String[] columns = line.split(Command.SEPERATOR);
		if (columns.length>7) {
			result.setArgu7(columns[7]);
		}
		if (columns.length>6) {
			result.setArgu6(columns[6]);
		}
		if (columns.length>5) {
			result.setArgu5(columns[5]);
		}
		if (columns.length>4) {
			result.setArgu4(columns[4]);
		}
		if (columns.length>3) {
			result.setArgu3(columns[3]);
		}
		if (columns.length>2) {
			result.setArgu2(columns[2]);
		}
		if (columns.length>1) {
			result.setArgu1(columns[1]);
		}
		if (columns.length>0) {
			result.setArgu0(columns[0]);
		}
		result.setLineRaw(line);
		return result;
	}
	
	static public String encodeCommand(Command command) {
		StringBuilder buff = new StringBuilder();
		if (command.getCommandName().isAliased()) {
			buff.append(command.getCommandName().getAliasName());
		} else {
			buff.append(command.getCommandName().toString());
		}
		if (command.getArgu0()!=null) {
			buff.append(Command.SEPERATOR);
			buff.append(command.getArgu0());
		}
		if (command.getArgu1()!=null) {
			buff.append(Command.SEPERATOR);
			buff.append(command.getArgu1());
		}
		if (command.getArgu2()!=null) {
			buff.append(Command.SEPERATOR);
			buff.append(command.getArgu2());
		}
		if (command.getArgu3()!=null) {
			buff.append(Command.SEPERATOR);
			buff.append(command.getArgu3());
		}
		if (command.getArgu4()!=null) {
			buff.append(Command.SEPERATOR);
			buff.append(command.getArgu4());
		}
		if (command.getArgu5()!=null) {
			buff.append(Command.SEPERATOR);
			buff.append(command.getArgu5());
		}
		if (command.getArgu6()!=null) {
			buff.append(Command.SEPERATOR);
			buff.append(command.getArgu6());
		}
		if (command.getArgu7()!=null) {
			buff.append(Command.SEPERATOR);
			buff.append(command.getArgu7());
		}
		String lineRaw =  buff.toString();
		command.setLineRaw(lineRaw);
		return lineRaw;
	}
}

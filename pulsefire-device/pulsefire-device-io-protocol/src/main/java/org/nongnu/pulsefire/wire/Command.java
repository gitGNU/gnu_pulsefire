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

/**
 * Command the decoded command from chip.
 * 
 * @author Willem Cazander
 */
public class Command {

	static public final char   LINE_END = '\n';
	static public final String SEPERATOR = " ";
	static public final String RESPONSE_SET = "=";
	static public final String RESPONSE_GET = "==";
	
	private String lineRaw = null;
	private CommandName commandName = null;
	private CommandWireType commandType = null;
	private String argu0 = null;
	private String argu1 = null;
	private String argu2 = null;
	private String argu3 = null;
	private String argu4 = null;
	private String argu5 = null;
	private String argu6 = null;
	private String argu7 = null;
	
	public Command(CommandName commandName) {
		this.commandName=commandName;
	}
	public Command(CommandName variable,String arg0) {
		this(variable);
		setArgu0(arg0);
	}
	
	public CommandName getCommandName() {
		return commandName;
	}
	public void setCommandName(CommandName commandName) {
		this.commandName = commandName;
	}
	public CommandWireType getType() {
		return commandType;
	}
	public void setType(CommandWireType commandType) {
		this.commandType=commandType;
	}
	public String getArgu0() {
		return argu0;
	}
	public void setArgu0(String argu0) {
		this.argu0 = argu0;
	}
	public String getArgu1() {
		return argu1;
	}
	public void setArgu1(String argu1) {
		this.argu1 = argu1;
	}
	public String getArgu2() {
		return argu2;
	}
	public void setArgu2(String argu2) {
		this.argu2 = argu2;
	}
	public String getArgu3() {
		return argu3;
	}
	public void setArgu3(String argu3) {
		this.argu3 = argu3;
	}
	public String getArgu4() {
		return argu4;
	}
	public void setArgu4(String argu4) {
		this.argu4 = argu4;
	}
	public String getArgu5() {
		return argu5;
	}
	public void setArgu5(String argu5) {
		this.argu5 = argu5;
	}
	public String getArgu6() {
		return argu6;
	}
	public void setArgu6(String argu6) {
		this.argu6 = argu6;
	}
	public String getArgu7() {
		return argu7;
	}
	public void setArgu7(String argu7) {
		this.argu7 = argu7;
	}
	public String getLineRaw() {
		return lineRaw;
	}
	public void setLineRaw(String lineRaw) {
		this.lineRaw = lineRaw;
	}
}

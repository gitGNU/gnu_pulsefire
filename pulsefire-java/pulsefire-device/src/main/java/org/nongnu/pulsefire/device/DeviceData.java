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


package org.nongnu.pulsefire.device;

import java.util.HashMap;
import java.util.Map;

import org.nongnu.pulsefire.wire.Command;
import org.nongnu.pulsefire.wire.CommandName;
import org.nongnu.pulsefire.wire.CommandVariableType;

/**
 * DeviceData holds all current data of the connected device.
 * 
 * @author Willem Cazander
 * @see DeviceWireManager
 */
public class DeviceData {

	private Map<CommandVariableType,Map<CommandName,Command>> deviceParameters = null;
	private Map<CommandName,Map<Integer,Command>> indexedDeviceParameters = null;
	
	/**
	 * Creates the DeviceData object.
	 */
	public DeviceData() {
		deviceParameters = new HashMap<CommandVariableType,Map<CommandName,Command>>(255);
		indexedDeviceParameters = new HashMap<CommandName,Map<Integer,Command>>(16);
	}
	
	/**
	 * Sets and device variable
	 * @param command	The command to set.
	 */
	public void setDeviceParameter(Command command) {
		CommandName commandName = command.getCommandName();
		if (commandName.isIndexedA()) {
			Map<Integer,Command> cmds = getTypeMapIndexed(commandName);
			Integer key = null;
			if (commandName.isIndexedB()) {
				key = new Integer(command.getArgu0());
			} else {
				key = new Integer(command.getArgu1());
			}
			cmds.put(key, command);
			return;
		}
		Map<CommandName,Command> typeMap = getTypeMap(commandName);
		typeMap.put(commandName, command);
	}	
	
	/**
	 * @return	Returns the the map with all the variable types.
	 */
	public Map<CommandVariableType,Map<CommandName,Command>> getDeviceParameters() {
		return deviceParameters;
	}
	
	/**
	 * Returns the Command map based on the command name type.
	 * @param commandName	The command name to get to map for.
	 * @return	The Command data map.
	 */
	public Map<CommandName,Command> getTypeMap(CommandName commandName) {
		return getTypeMap(commandName.getType());
	}
	
	/**
	 * Returns the index map for indexed variables
	 * @param commandName	The command name to get to map for.
	 * @return	The Command data map.
	 */
	public Map<Integer,Command> getTypeMapIndexed(CommandName commandName) {
		Map<Integer,Command> indexMap = indexedDeviceParameters.get(commandName);
		if (indexMap == null) {
			indexMap = new HashMap<Integer,Command>(16);
			if (commandName.isIndexedA()==false) {
				return indexMap;
			}
			indexedDeviceParameters.put(commandName,indexMap);
		}
		return indexMap;
	}
	
	/**
	 * Returns the Map of all types.
	 * @param commandType
	 * @return
	 */
	public Map<CommandName,Command> getTypeMap(CommandVariableType commandType) {
		Map<CommandName,Command> typeList = deviceParameters.get(commandType);
		if (typeList==null) {
			typeList = new HashMap<CommandName,Command>(100);
			deviceParameters.put(commandType, typeList);
		}
		return typeList;
	}
	
	/**
	 * Gets indexed parameters
	 * @param command	The parameter to get
	 * @param idx		The index of the parameter to get
	 * @return	The indexed parameter.
	 */
	public Command getDeviceParameterIndexed(Command command,Integer idx) {
		return getDeviceParameterIndexed(command.getCommandName(),idx);
	}
	
	/**
	 * Gets indexed parameters
	 * @param command	The parameter to get
	 * @param idx		The index of the parameter to get
	 * @return	The indexed parameter.
	 */
	public Command getDeviceParameterIndexed(CommandName commandName,Integer idx) {
		Map<Integer,Command> indexMap = getTypeMapIndexed(commandName);
		return indexMap.get(idx);
	}
	
	/**
	 * Returns device parameter
	 * @param command The parameter to get
	 * @return	The command of the parameter
	 */
	public Command getDeviceParameter(Command command) {
		return getDeviceParameter(command.getCommandName());
	}
	
	/**
	 * Returns device parameter
	 * @param command The parameter to get
	 * @return	The command of the parameter
	 */
	public Command getDeviceParameter(CommandName commandName) {
		Map<CommandName,Command> typeMap = getTypeMap(commandName);
		return typeMap.get(commandName);
	}
}

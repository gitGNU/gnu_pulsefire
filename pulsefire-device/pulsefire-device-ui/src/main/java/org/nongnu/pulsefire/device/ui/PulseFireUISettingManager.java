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

package org.nongnu.pulsefire.device.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.jdesktop.application.ApplicationContext;


/**
 * PulseFireUISettingManager managed all settings
 * 
 * @author Willem Cazander
 */
public class PulseFireUISettingManager {

	static private final String STORAGE_FILE = "pulsefire-settings.xml";
	private ApplicationContext context = null;
	private Properties settings = null;
	private Logger logger = null;
	private Map<PulseFireUISettingKeys,List<PulseFireUISettingListener>> listeners = null;
	
	public PulseFireUISettingManager(ApplicationContext context) {
		if (context==null) {
			throw new NullPointerException("Can work with null ApplicationContext.");
		}
		this.context=context;
		this.settings = new Properties();
		logger = Logger.getLogger(PulseFireUISettingManager.class.getName());
		listeners = new HashMap<PulseFireUISettingKeys,List<PulseFireUISettingListener>>(5);
	}
	
	public String getSettingString(PulseFireUISettingKeys key) {
		return settings.getProperty(key.name(),key.getDefaultValue());
	}
	
	public void setSettingString(PulseFireUISettingKeys key,String value) {
		settings.setProperty(key.name(),value);
		
		List<PulseFireUISettingListener> list = listeners.get(key);
		if (list==null) {
			return;
		}
		for (int i=0;i<list.size();i++) {
			PulseFireUISettingListener listener = list.get(i);
			listener.settingUpdated(key, value);
		}
	}
	
	public Boolean getSettingBoolean(PulseFireUISettingKeys key) {
		return Boolean.valueOf(getSettingString(key));
	}
	
	public Integer getSettingInteger(PulseFireUISettingKeys key) {
		return Integer.valueOf(getSettingString(key));
	}
	
	public void setSettingInteger(PulseFireUISettingKeys key,Integer value) {
		setSettingString(key, ""+value);
	}
	
	public void saveSettings() {
		try {
			context.getLocalStorage().save(settings,STORAGE_FILE);
		} catch (IOException e) {
			logger.warning("Could not save settings error: "+e.getMessage());
		}
	}
	
	public void loadSettings() {
		try {
			Properties loadSettings = (Properties)context.getLocalStorage().load(STORAGE_FILE);
			if (loadSettings!=null) {
				settings = loadSettings;
				logger.info("Loaded "+STORAGE_FILE+" with "+loadSettings.size()+" settings.");
			}
		} catch (IOException e) {
			logger.warning("Could not load settings error: "+e.getMessage());
		} 
	}
	
	public void addSettingListener(PulseFireUISettingKeys key,PulseFireUISettingListener listener) {
		List<PulseFireUISettingListener> list = listeners.get(key);
		if (list==null) {
			list = new ArrayList<PulseFireUISettingListener>(10);
			listeners.put(key, list);
		}
		list.add(listener);
	}
	public void removeSettingListener(PulseFireUISettingKeys key,PulseFireUISettingListener listener) {
		List<PulseFireUISettingListener> list = listeners.get(key);
		if (list==null) {
			return;
		}
		list.remove(listener);
	}
}

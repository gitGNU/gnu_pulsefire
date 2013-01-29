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

package org.nongnu.pulsefire.device.ui.pull;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import org.nongnu.pulsefire.device.DeviceCommandListener;
import org.nongnu.pulsefire.device.DeviceConnectListener;
import org.nongnu.pulsefire.device.ui.JMainPanel;
import org.nongnu.pulsefire.device.ui.PulseFireUI;
import org.nongnu.pulsefire.device.ui.time.EventTimeTrigger;
import org.nongnu.pulsefire.wire.Command;
import org.nongnu.pulsefire.wire.CommandName;
import org.nongnu.pulsefire.wire.CommandVariableType;

/**
 * PulseFireTimeData
 * 
 * @author Willem Cazander
 */
public class PulseFireTimeData implements DeviceConnectListener {

	private Logger logger = null;
	private Map<CommandName,TimeDataKey> timeDataQueueMap = null;
	
	public PulseFireTimeData() {
		logger = Logger.getLogger(PulseFireTimeData.class.getName());
		timeDataQueueMap = Collections.synchronizedMap(new HashMap<CommandName,TimeDataKey>(20));
		PulseFireUI.getInstance().getDeviceManager().addDeviceConnectListener(this);
		PulseFireUI.getInstance().getEventTimeManager().addEventTimeTrigger(new EventTimeTrigger("refreshTimeData",new DataCommandAutoAdd(),1000));
	}
	
	public TimeDataKey getKeyFromName(CommandName name) {
		TimeDataKey result = timeDataQueueMap.get(name);
		if (result!=null) {
			return result;
		}
		TimeDataKey key = new TimeDataKey();
		key.name=name;
		timeDataQueueMap.put(name,key);
		return key;
	}
	
	public void addTimeDataListener(CommandName name,TimeDataListener listener) {
		TimeDataKey k = getKeyFromName(name);
		k.timeDataListeners.add(listener);
	}
	
	public void removeTimeDataListener(CommandName name,TimeDataListener listener) {
		TimeDataKey k = getKeyFromName(name);
		k.timeDataListeners.remove(listener);
	}

	public int getTimeDataSize() {
		return timeDataQueueMap.size();
	}
	
	public List<CommandName> getTimeDataKeys() {
		List<CommandName>  result = new ArrayList<CommandName>(10);
		result.addAll(timeDataQueueMap.keySet());
		Collections.sort(result);
		return result;
	}
	
	public List<TimeData> getTimeData(CommandName name) {
		TimeDataKey k = getKeyFromName(name);
		List<TimeData> result = new ArrayList<TimeData>(300);
		result.addAll(k.timeDataQueue);
		return result;
	}
	
	public class TimeDataKey {
		CommandName name;
		TimeData timeDataLast = null;
		public List<TimeDataListener> timeDataListeners = new ArrayList<TimeDataListener>(4);
		public Color dataColorIdx[] = new Color[32];
		public Queue<TimeData> timeDataQueue = new LinkedBlockingQueue<TimeData>();
	}
	
	public class TimeData {
		public long receivedTime = 0;
		public int dataPoint = 0;
		public int dataPointIdx[] = new int[128]; // todo make lower, is high because info_pwm steps data
	}

	@Override
	public void deviceConnect() {
		// this is bad design redone asp
		if (onceAdd) {
			return;
		}
		onceAdd = true;
		for (CommandName name:CommandName.values()) {
			if (name.isDisabled()) {
				continue;
			}
			if (name.isMappable() | name.getType().equals(CommandVariableType.DATA) | name.getType().equals(CommandVariableType.PROG)) {
				logger.fine("Adding timedata for: "+name);
				getKeyFromName(name); // creates quee
				PulseFireUI.getInstance().getDeviceManager().addDeviceCommandListener(name, new DataCommandListener());
			}
		}
	}
	private boolean onceAdd = false;
	
	@Override
	public void deviceDisconnect() {
		timeDataQueueMap.clear();
	}
	
	class DataCommandAutoAdd implements Runnable {
		@Override
		public void run() {
			// todo: move to interface timer
			if (PulseFireUI.getInstance().getMainView()!=null && PulseFireUI.getInstance().getMainView().getComponent()!=null) {
				((JMainPanel)PulseFireUI.getInstance().getMainView().getComponent()).topPanelSerial.updateSpeedCounters();
			}
			
			for (TimeDataKey key:timeDataQueueMap.values()) {
				long now = System.currentTimeMillis();
				if (key.timeDataLast!=null && (now-key.timeDataLast.receivedTime)>1000) {
					TimeData timeData = new TimeData();
					timeData.receivedTime=now;
					timeData.dataPoint=key.timeDataLast.dataPoint;
					timeData.dataPointIdx=key.timeDataLast.dataPointIdx.clone();
					key.timeDataLast = timeData;
					Queue<TimeData> q = key.timeDataQueue;
					q.add(timeData);
					if (q.size()>(100)) {
						q.poll();
					}
					for (TimeDataListener l:key.timeDataListeners) {
						l.updateTimeData();
					}
				}
			}
		}
	}
	
	class DataCommandListener implements DeviceCommandListener {
		@Override
		public void commandReceived(Command command) {
			long now = System.currentTimeMillis();
			TimeDataKey k = getKeyFromName(command.getCommandName());
			Queue<TimeData> q = k.timeDataQueue;
			TimeData timeData = null;
			if (q.isEmpty()) {
				timeData = new TimeData();
				timeData.receivedTime=now;
				q.add(timeData);
			}
			//if (k.timeDataLast!=null && (now-k.timeDataLast.receivedTime)<1000) {
			//	return;
				//timeData = k.timeDataLast; 
				//timeData.receivedTime=now;
				//timeData.dataPointIdx=k.timeDataLast.dataPointIdx.clone();
				//q.add(timeData);
			//} else {
				timeData = new TimeData();
				timeData.receivedTime=now;
				if (k.timeDataLast!=null) {
					timeData.dataPointIdx=k.timeDataLast.dataPointIdx.clone();
				}
				q.add(timeData);
			//}
			Integer arguValue = null;
			try {
				arguValue = new Integer(command.getArgu0());
			} catch (NumberFormatException nfe) {
				return; // fixme in prot layer in v2.0
			}
			if (command.getCommandName().isIndexedA()) {
				timeData.dataPointIdx[new Integer(command.getArgu1())]=arguValue;
			} else {
				timeData.dataPoint=arguValue;
			}
			k.timeDataLast = timeData;
			if (q.size()>(100)) {
				q.poll();
			}
			for (int i=0;i<k.timeDataListeners.size();i++) {
				TimeDataListener l=k.timeDataListeners.get(i);
				l.updateTimeData();
			}
		}
	}
	
	public interface TimeDataListener {
		public void updateTimeData();
	}
}

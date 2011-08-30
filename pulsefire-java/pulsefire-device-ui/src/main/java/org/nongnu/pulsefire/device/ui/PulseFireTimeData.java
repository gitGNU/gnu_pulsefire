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
import org.nongnu.pulsefire.device.ui.time.EventTimeTrigger;
import org.nongnu.pulsefire.wire.Command;
import org.nongnu.pulsefire.wire.CommandName;

/**
 * PulseFireTimeData
 * 
 * @author Willem Cazander
 */
public class PulseFireTimeData implements DeviceConnectListener {

	private Logger logger = null;
	private Map<TimeDataKey,Queue<TimeData>> timeDataQueueMap = null;
	
	public PulseFireTimeData() {
		logger = Logger.getLogger(PulseFireTimeData.class.getName());
		timeDataQueueMap = Collections.synchronizedMap(new HashMap<TimeDataKey,Queue<TimeData>>(20));
		PulseFireUI.getInstance().getDeviceManager().addDeviceConnectListener(this);
		PulseFireUI.getInstance().getEventTimeManager().addEventTimeTrigger(new EventTimeTrigger("refreshTimeData",new DataCommandAutoAdd(),1000));
	}
	
	public TimeDataKey getKeyFromName(CommandName name) {
		for (TimeDataKey k:timeDataQueueMap.keySet()) {
			if (k.name.equals(name)) {
				return k;
			}
		}
		TimeDataKey key = new TimeDataKey();
		key.name=name;
		timeDataQueueMap.put(key,new LinkedBlockingQueue<TimeData>());
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
		for (TimeDataKey k:timeDataQueueMap.keySet()) {
			result.add(k.name);
		}
		Collections.sort(result);
		return result;
	}
	
	public List<TimeData> getTimeData(CommandName name) {
		TimeDataKey k = getKeyFromName(name);
		Queue<TimeData> q = timeDataQueueMap.get(k);
		List<TimeData> result = new ArrayList<TimeData>(300);
		if (q!=null) {
			result.addAll(q);
		}
		return result;
	}
	
	public class TimeDataKey {
		CommandName name;
		TimeData timeDataLast = null;
		public List<TimeDataListener> timeDataListeners = new ArrayList<TimeDataListener>(4);
		public Color dataColorIdx[] = new Color[32];
	}
	
	public class TimeData {
		public long receivedTime = 0;
		public int dataPoint = 0;
		public int dataPointIdx[] = new int[32];
	}

	@Override
	public void deviceConnect() {
		for (CommandName name:CommandName.values()) {
			if (name.isMappable()) {
				logger.fine("Adding timedata for: "+name);
				getKeyFromName(name); // creates quee
				PulseFireUI.getInstance().getDeviceManager().addDeviceCommandListener(name, new DataCommandListener());
			}
		}
	}

	@Override
	public void deviceDisconnect() {
		timeDataQueueMap.clear();
	}
	
	class DataCommandAutoAdd implements Runnable {
		@Override
		public void run() {
			for (TimeDataKey key:timeDataQueueMap.keySet()) {
				long now = System.currentTimeMillis();
				if (key.timeDataLast!=null && (now-key.timeDataLast.receivedTime)>1000) {
					TimeData timeData = new TimeData();
					timeData.receivedTime=now;
					timeData.dataPoint=key.timeDataLast.dataPoint;
					timeData.dataPointIdx=key.timeDataLast.dataPointIdx.clone();
					key.timeDataLast = timeData;
					Queue<TimeData> q = timeDataQueueMap.get(key);
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
			Queue<TimeData> q = timeDataQueueMap.get(k);
			TimeData timeData = null;
			if (q.isEmpty()) {
				timeData = new TimeData();
				timeData.receivedTime=now;
				q.add(timeData);
			}
			if (k.timeDataLast!=null && (now-k.timeDataLast.receivedTime)>1000) {
				timeData = k.timeDataLast; 
				//timeData.receivedTime=now;
				//timeData.dataPointIdx=k.timeDataLast.dataPointIdx.clone();
				//q.add(timeData);
			} else {
				timeData = new TimeData();
				timeData.receivedTime=now;
				if (k.timeDataLast!=null) {
					timeData.dataPointIdx=k.timeDataLast.dataPointIdx.clone();
				}
				q.add(timeData);
			}
			if (command.getCommandName().isIndexedA()) {
				timeData.dataPointIdx[new Integer(command.getArgu1())]=new Integer(command.getArgu0());
			} else {
				timeData.dataPoint=new Integer(command.getArgu0());
			}
			k.timeDataLast = timeData;
			if (q.size()>(100)) {
				q.poll();
			}
			for (TimeDataListener l:k.timeDataListeners) {
				l.updateTimeData();
			}
		}
	}
	
	public interface TimeDataListener {
		public void updateTimeData();
	}
}

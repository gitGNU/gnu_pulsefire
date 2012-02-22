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

package org.nongnu.pulsefire.device.ui.time;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * EventTimeThread runs all valid triggers.
 * 
 * @author Willem Cazander
 */
public class EventTimeThread extends Thread { 

	private Logger logger = null;
	private volatile boolean running = false;
	private EventTimeManager eventTimeManager = null;
	
	protected EventTimeThread(EventTimeManager eventTimeManager) {
		this.eventTimeManager=eventTimeManager;
		logger = Logger.getLogger(EventTimeThread.class.getName());
	}
	
	public void run() {
		try {
			running = true;
			logger.info("EventTimer started");
			long events = 0;
			while (running) {
				Thread.sleep(100);
				List<EventTimeTrigger> executeSteps = eventTimeManager.getEventExecuteSteps();
				for (int i=0;i<executeSteps.size();i++) {
					EventTimeTrigger trig = executeSteps.get(i);
					trig.setRunStartTime(System.currentTimeMillis());
					trig.setTimeNextRun(trig.getRunStartTime()+trig.getTimeStep());
					try {
						trig.getRunnable().run();
					} catch (Exception triggerException) {
						logger.log(Level.WARNING,triggerException.getMessage(),triggerException);
					} finally {
						events++;
						trig.setRunStopTime(System.currentTimeMillis());
						trig.setRunCounter(trig.getRunCounter()+1);
						if (trig.getTimeRuns()!=0 && trig.getRunCounter() > trig.getTimeRuns()) {
							eventTimeManager.removeEventTimeTrigger(trig); // done with trigger
						}
					}
				}
			}
			logger.info("EventTimer stopped, total events fired: "+events);
		} catch (Exception coreException) {
			logger.log(Level.WARNING,coreException.getMessage(),coreException);
		}
	}
	
	public void shutdown(){
		running = false;
	}
	
	public boolean isRunning() {
		return running;
	}
}

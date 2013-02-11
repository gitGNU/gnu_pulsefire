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

/**
 * EventTimeTrigger is config object to trigger runnable object.
 * 
 * @author Willem Cazander
 */
public class EventTimeTrigger {

	private String triggerName = null;
	private long timeLastTime = 0;
	private long timeNextRun = 0;
	private long timeStep = 0;
	private long timeRuns = 0;
	private long runCounter = 0;
	private long runStartTime = 0;
	private long runStopTime = 0;
	private Runnable runnable = null;
	
	public EventTimeTrigger(String name,Runnable runnable,long timeStep) {
		this(name,runnable,timeStep,0l);
	}
	
	public EventTimeTrigger(String name,Runnable runnable,long timeStep,long delay) {
		this.triggerName=name;
		this.runnable=runnable;
		this.timeStep=timeStep;
		this.timeNextRun=System.currentTimeMillis()+delay;
	}
	
	public long getTimeLastTime() {
		return timeLastTime;
	}
	public void setTimeLastTime(long timeLastTime) {
		this.timeLastTime = timeLastTime;
	}
	public long getTimeNextRun() {
		return timeNextRun;
	}
	public void setTimeNextRun(long timeNextRun) {
		this.timeNextRun = timeNextRun;
	}
	public Runnable getRunnable() {
		return runnable;
	}
	public void setRunnable(Runnable runnable) {
		this.runnable = runnable;
	}
	public long getTimeStep() {
		return timeStep;
	}
	public void setTimeStep(long timeStep) {
		this.timeStep = timeStep;
	}
	public long getRunCounter() {
		return runCounter;
	}
	public void setRunCounter(long runCounter) {
		this.runCounter = runCounter;
	}
	public long getRunStartTime() {
		return runStartTime;
	}
	public void setRunStartTime(long runStartTime) {
		this.runStartTime = runStartTime;
	}
	public long getRunStopTime() {
		return runStopTime;
	}
	public void setRunStopTime(long runStopTime) {
		this.runStopTime = runStopTime;
	}
	public long getTimeRuns() {
		return timeRuns;
	}
	public void setTimeRuns(long timeRuns) {
		this.timeRuns = timeRuns;
	}

	public String getTriggerName() {
		return triggerName;
	}

	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}
	
}

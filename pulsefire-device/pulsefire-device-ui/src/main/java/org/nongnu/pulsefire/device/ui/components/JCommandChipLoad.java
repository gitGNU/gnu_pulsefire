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

package org.nongnu.pulsefire.device.ui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.nongnu.pulsefire.device.io.protocol.Command;
import org.nongnu.pulsefire.device.io.protocol.CommandName;
import org.nongnu.pulsefire.device.io.transport.DeviceCommandListener;
import org.nongnu.pulsefire.device.io.transport.DeviceConnectListener;
import org.nongnu.pulsefire.device.io.transport.DeviceData;
import org.nongnu.pulsefire.device.ui.PulseFireUI;
import org.nongnu.pulsefire.device.ui.time.EventTimeTrigger;

/**
 * JCommandChipLoad shows chip load based on the inverse speed variable.
 * 
 * @author Willem Cazander
 */
public class JCommandChipLoad extends JPanel implements DeviceCommandListener {
	
	private static final long serialVersionUID = -2922342927574919902L;
	private static final float AVG_NORMAL_SPEED = 22000; //uno with spi chips 
	private final DeviceData deviceData;
	private boolean overloadBlink;
	
	public JCommandChipLoad() {
		this.deviceData = PulseFireUI.getInstance().getDeviceManager().getDeviceData();
		setBorder(BorderFactory.createEmptyBorder());
		setPreferredSize(new Dimension(40, 90));
		PulseFireUI.getInstance().getEventTimeManager().addEventTimeTriggerConnected(new EventTimeTrigger("OverLoadBlinker",new OverLoadBlinker(),500));
		PulseFireUI.getInstance().getDeviceManager().addDeviceCommandListener(CommandName.sys_speed, this);
		PulseFireUI.getInstance().getDeviceManager().addDeviceConnectListener(new DeviceConnectListener() {
			@Override
			public void deviceDisconnect() {
				repaint();
			}
			@Override
			public void deviceConnect() {
			}
		});
	}
	
	class OverLoadBlinker implements Runnable {
		@Override
		public void run() {
			overloadBlink = !overloadBlink;
			repaint();
		}
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Color lineColor = UIManager.getColor("gridColor"); //nimbusFocus,nimbusRed,gridColor
		Color loadColor = UIManager.getColor("gridColor"); //nimbusFocus,nimbusRed,gridColor
		Color textColor = UIManager.getColor("text");
		
		int sysSpeed = getLoadSpeed();
		if (sysSpeed < 3333) { // warning
			loadColor = UIManager.getColor("nimbusRed"); 
		}
		if (sysSpeed < 2000) { // overload
			if (overloadBlink) {
				loadColor = UIManager.getColor("gridColor");
			}
		}
		
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		int w = getWidth();
		int h = getHeight();
		int load = getLoadPercentage();
		int startLoad = h-load;
		
		for (int i=1;i<h;i=i+2) {
			//g2.setColor(gridColor);
			//g2.drawLine(0+2, i, w-2, i);
			if (i>startLoad) {
				g2.setColor(loadColor);
				g2.drawLine(0+3, i, w-4, i);
			}
		}
		
		g2.setColor(lineColor);
		g2.drawRect(0, 0, w-1, h-1);
		g2.setColor(textColor);
		g2.drawString(load+"%", 5, 15);
		if (sysSpeed < 2000) {
			g2.drawString("chip", 5, 45);
			g2.drawString("over", 5+1, 60);
			g2.drawString("load", 5+2, 75);
		}
	}
	
	private int getLoadSpeed() {
		int notConnectValue = new Float(AVG_NORMAL_SPEED*2).intValue(); 
		if (!PulseFireUI.getInstance().getDeviceManager().isConnected()) {
			return notConnectValue;
		}
		Command sysSpeedCommand = deviceData.getDeviceParameter(CommandName.sys_speed);
		if (sysSpeedCommand==null) {
			return notConnectValue;
		}
		int sysSpeed = new Integer(sysSpeedCommand.getArgu0());
		if (sysSpeed == 0) {
			return notConnectValue; // until data is received ...
		}
		return sysSpeed;
	}
	
	private int getLoadPercentage() {
		int sysSpeed = getLoadSpeed();
		float sysSpeedNormal = AVG_NORMAL_SPEED;
		if (sysSpeed > sysSpeedNormal) {
			return 0;
		}
		float load = 100-(((float)sysSpeed/sysSpeedNormal)*100);
		return new Float(load).intValue(); 
	}
	
	@Override
	public void commandReceived(Command command) {
		repaint();
	}
}

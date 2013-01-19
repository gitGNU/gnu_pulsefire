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

import org.nongnu.pulsefire.device.DeviceCommandListener;
import org.nongnu.pulsefire.device.DeviceConnectListener;
import org.nongnu.pulsefire.device.DeviceData;
import org.nongnu.pulsefire.device.ui.PulseFireUI;
import org.nongnu.pulsefire.wire.Command;
import org.nongnu.pulsefire.wire.CommandName;

/**
 * JFirePwmInfo shows pwm step data.
 * 
 * @author Willem Cazander
 */
public class JFirePwmInfo extends JPanel implements DeviceCommandListener,DeviceConnectListener {
	
	private static final long serialVersionUID = -2922342927574919902L;
	final int PAD = 0;
	private CommandName commandName = null;
	private DeviceData deviceData = null;
	private Color gridColor = null;
	
	public JFirePwmInfo() {
		this.commandName=CommandName.info_pwm_data;
		this.deviceData = PulseFireUI.getInstance().getDeviceManager().getDeviceData();
		this.gridColor = UIManager.getColor("nimbusGreen").darker();
		setPreferredSize(new Dimension(440,220));
		setMinimumSize(new Dimension(80,40));
		setBorder(BorderFactory.createEmptyBorder());
		PulseFireUI.getInstance().getDeviceManager().addDeviceCommandListener(commandName, this);
		PulseFireUI.getInstance().getDeviceManager().addDeviceConnectListener(this);
	}

	public CommandName getCommandName() {
		return commandName;
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		int w = getWidth();
		int h = getHeight();
		
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setPaint(gridColor);
		g2.drawRect(0, 0, w-1, h-1);
		
		Command pulseStepsCommand = deviceData.getDeviceParameter(CommandName.pulse_steps);
		Command pwmStepsCommand = deviceData.getDeviceParameter(CommandName.info_pwm_steps);
		if (pwmStepsCommand==null) {
			return;
		}
		int pulseSteps = new Integer(pulseStepsCommand.getArgu0());
		int steps = new Integer(pwmStepsCommand.getArgu0());
		int pulseHeight = h/pulseSteps/2;
		int totalTime = 0;
		for (int i=0;i<steps;i++) {
			Command stepData = deviceData.getDeviceParameterIndexed(CommandName.info_pwm_data,i);
			if (stepData==null) {
				continue;
			}
			int time = Integer.parseInt(stepData.getArgu2());
			totalTime+= time;
		}
		
		int yLines = pulseSteps;
		int xLines = steps;
		int yLine = (h-10)/yLines;
		//int xLine = w/xLines;
		
		
		//g2.setPaint(Color.RED);
		g2.setPaint(gridColor);
		int xPrefix = 0;
		for (int x=0;x<xLines;x++) {
			Command stepData = deviceData.getDeviceParameterIndexed(CommandName.info_pwm_data,x);
			if (stepData==null) {
				continue;
			}
			int time = Integer.parseInt(stepData.getArgu2());
			int xStep = time/(totalTime/w);
			int xDest = xPrefix;
			xPrefix+=xStep;
			
			
			if (w>400) {
				//g2.drawLine((int)((w/xLines)*x), 0, (int)((w/xLines)*x), h);
				g2.drawString(""+x, xDest+1, 12);
			}
			if (w>800 && h>500) {
				g2.drawString(""+time, xDest+1, 24);
				//g2.drawString(""+xStep, xDest+1, 75);
			}
		}
		
		g2.setPaint(gridColor);
		for (int y=0;y<=yLines;y++) {
			g2.drawLine(0,yLine*y, w,yLine*y);
		}
		
		//g2.drawString("Steps:", (w/2)-50, 25);
		//g2.drawString(""+steps, (w/2), 25);
		//g2.drawString("Outs:", (w/2)-50, 35);
		//g2.drawString(""+pulseSteps, (w/2), 35);
		
		g2.setPaint(Color.RED);
		for (int y=0;y<yLines;y++) {
			g2.drawString(""+y, 3, yLine*y+yLine);
			Boolean levelOrg = null;
			xPrefix = 0;
			for (int x=0;x<xLines;x++) {
				Command stepData = deviceData.getDeviceParameterIndexed(CommandName.info_pwm_data,x);
				if (stepData==null) {
					continue;
				}
				int data = Integer.parseInt(stepData.getArgu0(),2);
				int time = Integer.parseInt(stepData.getArgu2());
				int xStep = time/(totalTime/w);
				int xDest = xPrefix;
				xPrefix+=xStep;
				Boolean level = (data & (1 << y)) == 0;
				
				int startY = yLine*y+yLine;
				int startX = xDest; //xLine*x;
				int stopX  = xPrefix; //xLine*x+xLine;
				int stopY  = startY;
				
				if (!level) {
					startY-=pulseHeight;
					stopY-=pulseHeight;
					if (levelOrg != null && levelOrg.equals(level)==false) {
						g2.drawLine(startX,startY, startX, startY+pulseHeight); // up
					}
				} else {
					if (levelOrg != null && levelOrg.equals(level)==false) {
						g2.drawLine(startX,startY, startX, startY-pulseHeight); // down
					}
				}
				g2.drawLine(startX,startY, stopX, stopY);
				levelOrg = level;
			}
		}
	}

	@Override
	public void commandReceived(Command command) {
		repaint();
	}

	@Override
	public void deviceConnect() {
	}

	@Override
	public void deviceDisconnect() {
		repaint();
	}
}

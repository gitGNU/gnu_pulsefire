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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;

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
	private DeviceData deviceData = null;
	private BasicStroke dashedStroke = null;
	private String[] displayText = null;
	
	public JFirePwmInfo() {
		this.deviceData = PulseFireUI.getInstance().getDeviceManager().getDeviceData();
		this.dashedStroke = new BasicStroke(1f, 0, 0, 10f, new float[] {5f,4f,1f,4f}, 0f);
		setBorder(BorderFactory.createEmptyBorder());
		PulseFireUI.getInstance().getDeviceManager().addDeviceCommandListener(CommandName.info_pwm_data, this);
		PulseFireUI.getInstance().getDeviceManager().addDeviceCommandListener(CommandName.info_freq_data, this);
		PulseFireUI.getInstance().getDeviceManager().addDeviceConnectListener(this);
		displayText = new String[] {
				"#________#_#_#_#",
				"_#______________",
				"__#__________#__",
				"___#____________",
				"____#____###__##",
				"_____#____###__#",
		};
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Color lineColor = UIManager.getColor("nimbusFocus");
		Color lineDashedColor = UIManager.getColor("gridColor");
		Color pulseColor = UIManager.getColor("text");
		Color textStepColor = UIManager.getColor("text");
		Color textTimeColor = UIManager.getColor("nimbusRed");
		
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		int w = getWidth();
		int h = getHeight();
		
		//g2.setColor(lineColor);
		//g2.drawRect(0, 0, w-1, h-1);
		
		Command pulseStepsCommand = deviceData.getDeviceParameter(CommandName.pulse_steps);
		Command pwmStepsCommand = deviceData.getDeviceParameter(CommandName.info_pwm_steps);
		if (pwmStepsCommand==null) {
			g2.setPaint(pulseColor);
			for (int i=0;i<displayText.length;i++) {
				g2.drawString(displayText[i], 10, (h/2)-30+(i*12));
			}
			return;
		}
		int pulseSteps = new Integer(pulseStepsCommand.getArgu0());
		if (pulseSteps==0) {
			return; // nothing to draw. /0
		}
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
		if (totalTime==0) {
			return; // no time
		}
		int yLines = pulseSteps;
		int xLines = steps;
		int yOffset = 15;
		int yLine = (h-5-yOffset)/yLines;
		
		int xPrefix = 0;
		for (int x=0;x<xLines;x++) {
			Command stepData = deviceData.getDeviceParameterIndexed(CommandName.info_pwm_data,x);
			if (stepData==null) {
				continue;
			}
			int time = Integer.parseInt(stepData.getArgu2());
			int timeStep = totalTime/w;
			if (timeStep==0) {
				continue; // step to small ?
			}
			int xStep = time/timeStep;
			int xDest = xPrefix;
			xPrefix+=xStep;
			
			g2.setColor(textStepColor);
			if (x>9 && xStep>15) {
				g2.drawString(""+x, xDest+1, 12);
			} else if (x<=9 && xStep>10) {
				g2.drawString(""+x, xDest+1, 12);
			}
			if (xStep>40) {
				g2.setColor(textTimeColor);
				g2.drawString(""+time, xDest+1, 24);
			}
			if (x>0 && xStep>1 && w>500) {
				g2.setColor(lineDashedColor);
				Stroke line = g2.getStroke();
				g2.setStroke(dashedStroke);
				g2.drawLine(xDest,0, xDest,h);
				g2.setStroke(line);
			}
			//g2.drawString(""+xStep, xDest+1, 75);
		}
		
		g2.setPaint(lineColor);
		for (int y=1;y<=yLines;y++) {
			g2.drawLine(0,yOffset+yLine*y, w,yOffset+yLine*y);
		}
		
		g2.setPaint(pulseColor);
		for (int y=0;y<yLines;y++) {
			g2.drawString("Out: "+y, 3, yOffset+yLine*y+yLine);
			Command freqData = deviceData.getDeviceParameterIndexed(CommandName.info_freq_data,y);
			if (freqData!=null && w>400 && yLine>40) {
				g2.drawString("F: "+freqData.getArgu2(), 3, yOffset+yLine*y+yLine - 20);
				g2.drawString("D: "+freqData.getArgu1(), 3, yOffset+yLine*y+yLine - 10);
			}
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
				
				int startY = yOffset+yLine*y+yLine;
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

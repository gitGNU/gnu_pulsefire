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
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.nongnu.pulsefire.device.ui.PulseFireUI;
import org.nongnu.pulsefire.device.ui.PulseFireTimeData;
import org.nongnu.pulsefire.device.ui.PulseFireTimeData.TimeData;
import org.nongnu.pulsefire.device.ui.PulseFireTimeData.TimeDataKey;
import org.nongnu.pulsefire.device.ui.PulseFireTimeData.TimeDataListener;
import org.nongnu.pulsefire.wire.CommandName;

/**
 * JFireGraph is realtime simple graph painter.
 * 
 * @author Willem Cazander
 */
public class JFireGraph extends JPanel implements TimeDataListener {
	
	private static final long serialVersionUID = -2926409292528475132L;
	final int PAD = 0;
	private CommandName commandName = null;
	private PulseFireTimeData timeDataStore = null;
	private Color gridColor = null;
	
	public JFireGraph(CommandName commandName) {
		this.commandName=commandName;
		this.timeDataStore=PulseFireUI.getInstance().getTimeData();
		this.gridColor = UIManager.getColor("nimbusGreen");
		setPreferredSize(new Dimension(440,220));
		setMinimumSize(new Dimension(80,40));
		setBorder(BorderFactory.createEmptyBorder());
		PulseFireUI.getInstance().getTimeData().addTimeDataListener(commandName, this);
	}

	public CommandName getCommandName() {
		return commandName;
	}
	
	public Color randomColor(int idx){
		Random random=new Random();
		if (idx>16) {
			idx = idx/2;
		}
		idx++; // skip 0
		int red=random.nextInt(idx*16);
		if (red<33) {
			red+=random.nextInt(133);
		}
		int green=random.nextInt(idx*16);
		if (green<33) {
			green+=random.nextInt(166);
		}
		int blue=random.nextInt(idx*16);
		if (blue<33) {
			blue+=random.nextInt(199);
		}
		return new Color(red, green, blue);
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setPaint(gridColor);
		
		int w = getWidth();
		int h = getHeight();
		double maxValue = getSize().height;
		double maxRValue = 0.0;
		double minRValue = 100000;
		List<TimeData> timeData = timeDataStore.getTimeData(commandName);
		for (int ii=0;ii<timeData.size();ii++) {
			TimeData t = timeData.get(ii);
			if (commandName.isIndexedA()==false) {
				if (t.dataPoint>maxValue) {
					maxValue = t.dataPoint;
				}
				if (t.dataPoint>maxRValue) {
					maxRValue = t.dataPoint;
				}
				if (t.dataPoint<minRValue) {
					minRValue = t.dataPoint;
				}
			} else {
				for (int i=0;i<commandName.getMaxIndexA();i++) { 
					if (t.dataPointIdx[i]>maxValue) {
						maxValue = t.dataPointIdx[i];
					}
					if (t.dataPointIdx[i]>maxRValue) {
						maxRValue = t.dataPointIdx[i];
					}
					if (t.dataPointIdx[i]<minRValue) {
						minRValue = t.dataPointIdx[i];
					}
				}
			}
		}
		if (minRValue == 100000) {
			minRValue = 0;
		}
		double xScale = (w - 2*PAD)/(timeData.size()+1);  
		double yScale = (h - 2*PAD)/maxValue;  
		
		int yLines = 10;
		int xLines = 20;
		
		for (int y=0;y<=yLines;y++) {
			g2.drawLine(0,(int)((h/yLines)*y), w, (int)((h/yLines)*y));
		}
		for (int x=0;x<=xLines;x++) {
			g2.drawLine((int)((w/xLines)*x), 0, (int)((w/xLines)*x), h);
		}
		
		/*
		for (int y=0;y<=yLines;y++) {
			g2.drawLine(0,(int)((h/yLines)*y*yScale), w, (int)((h/yLines)*y*yScale));
		}
		for (int x=0;x<=xLines;x++) {
			g2.drawLine((int)((w/xLines)*x*xScale), 0, (int)((w/xLines)*x*xScale), h);
		}
*/
		int x0 = PAD;  
		int y0 = h-PAD;  

		int x1 = -1;  
		int y1 = -1;
		int dataPoint = 0;
		int j = 0;
		
		if (commandName.isIndexedA()==false) {
			for (TimeData t:timeData) {
				g2.setPaint(Color.GREEN);
				dataPoint = t.dataPoint;
				int x = x0 + (int)(xScale * (j+1));  
				int y = y0 - (int)(yScale * dataPoint);
				g2.fillOval(x-2, y-2, 4, 4);
				if (x1!=-1) {
					g2.drawLine(x1, y1, x, y);
				}
				x1 = x;
				y1 = y;
				j++;
			}
		} else {
			for (int i=0;i<commandName.getMaxIndexA();i++) {
				TimeDataKey key = PulseFireUI.getInstance().getTimeData().getKeyFromName(commandName);
				if (key.dataColorIdx[i]==null) {
					key.dataColorIdx[i]=randomColor(i);
				}
				g2.setColor(key.dataColorIdx[i]);
				x1 = -1;  
				y1 = -1;
				dataPoint = 0;
				j = 0;
				for (int ii=0;ii<timeData.size();ii++) {
					TimeData t = timeData.get(ii);
					dataPoint = t.dataPointIdx[i];
					int x = x0 + (int)(xScale * (j+1));  
					int y = y0 - (int)(yScale * dataPoint);
					g2.fillOval(x-2, y-2, 4, 4);
					if (x1!=-1) {
						g2.drawLine(x1, y1, x, y);
					}
					x1 = x;
					y1 = y;
					j++;
				}
			}
		}
	
		g2.setPaint(UIManager.getColor("nimbusAlertYellow").darker());
		g2.drawRect(0, 0, w-1, h-1);
		//g2.setPaint(UIManager.getColor("nimbusRed"));
		g2.drawString("Name:", 7, 20);
		g2.drawString(commandName.name(), 50, 20);
		g2.drawString("Value:", 7, 35);
		g2.drawString(""+dataPoint, 50, 35);
		g2.drawString("Min:", 7, 50);
		g2.drawString(""+minRValue, 50, 50);
		g2.drawString("Max:", 7, 65);
		g2.drawString(""+maxRValue, 50, 65);
	}

	@Override
	public void updateTimeData() {
		repaint();
	}
}

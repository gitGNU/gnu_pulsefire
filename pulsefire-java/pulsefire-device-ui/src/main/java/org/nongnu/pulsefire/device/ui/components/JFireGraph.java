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
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.nongnu.pulsefire.device.ui.PulseFireUI;
import org.nongnu.pulsefire.device.ui.pull.PulseFireTimeData;
import org.nongnu.pulsefire.device.ui.pull.PulseFireTimeData.TimeData;
import org.nongnu.pulsefire.device.ui.pull.PulseFireTimeData.TimeDataKey;
import org.nongnu.pulsefire.device.ui.pull.PulseFireTimeData.TimeDataListener;
import org.nongnu.pulsefire.wire.Command;
import org.nongnu.pulsefire.wire.CommandName;

/**
 * JFireGraph is realtime simple graph painter.
 * 
 * @author Willem Cazander
 */
public class JFireGraph extends JPanel implements TimeDataListener {
	
	private static final long serialVersionUID = -2926409292528475132L;
	private CommandName commandName = null;
	private PulseFireTimeData timeDataStore = null;
	private Color[] colors = new Color[] {
			Color.decode("#00FF00"),
			Color.decode("#CC3300"),
			Color.decode("#FFCC00"),
			Color.decode("#FF33FF"),
			Color.decode("#CCCC00"),
			Color.decode("#CC33FF"),
			Color.decode("#9933FF"),
			Color.decode("#6633FF"),
			Color.decode("#00CCFF"),
			Color.decode("#FF0000"),
			Color.decode("#99CC66"),
			Color.decode("#990066"),
			Color.decode("#333366"),
			Color.decode("#FFCC66"),
			Color.decode("#00CCCC"),
			Color.decode("#6600CC"),
			Color.decode("#993333")
	};
	
	public JFireGraph(final CommandName commandName) {
		this.commandName=commandName;
		this.timeDataStore=PulseFireUI.getInstance().getTimeData();
		setPreferredSize(new Dimension(440,220));
		setMinimumSize(new Dimension(80,40));
		setBorder(BorderFactory.createEmptyBorder());
		addHierarchyListener(new HierarchyListener() {
			
			@Override
			public void hierarchyChanged(HierarchyEvent e) {
				if (HierarchyEvent.PARENT_CHANGED!=e.getChangeFlags()) {
					return;
				}
				if (JFireGraph.this.getParent()!=null) {
					PulseFireUI.getInstance().getTimeData().addTimeDataListener(commandName, JFireGraph.this);
				} else {
					PulseFireUI.getInstance().getTimeData().removeTimeDataListener(commandName, JFireGraph.this);
				}
			}
		});
	}

	public CommandName getCommandName() {
		return commandName;
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		
		Color gridColor = UIManager.getColor("gridColor");
		g2.setPaint(gridColor);
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
		int w = getWidth();
		int h = getHeight();
		double xScale = 1.0;
		if (timeData.isEmpty()==false) {
			xScale = w/timeData.size();
		}
		double yScale = h/(maxValue+10);
		
		int yLines = 10;
		int xLines = 20;
		
		for (int y=0;y<=yLines;y++) {
			g2.drawLine(0,(int)((h/yLines)*y), w, (int)((h/yLines)*y));
		}
		for (int x=0;x<=xLines;x++) {
			g2.drawLine((int)((w/xLines)*x), 0, (int)((w/xLines)*x), h);
		}
		int x0 = 0;
		int y0 = h;
		int x1 = -1;
		int y1 = -1;
		int dataPoint = 0;
		int j = 1;
		
		if (commandName.isIndexedA()==false) {
			for (int tt=0;tt<timeData.size();tt++) {
				TimeData t = timeData.get(tt);
				g2.setPaint(colors[0]);
				dataPoint = t.dataPoint;
				int x = x0 + (int)(xScale * (j+1));  
				int y = y0 - (int)(yScale * dataPoint);
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
				if (key==null) {
					break;
				}
				if (i>=key.dataColorIdx.length) {
					break;
				}
				if (key.dataColorIdx[i]==null) {
					Color c = null;
					if (i > 16) {
						c = colors[0];
					} else {
						c = colors[i];
					}
					key.dataColorIdx[i]=c;
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
					if (x1!=-1) {
						g2.drawLine(x1, y1, x, y);
					}
					x1 = x;
					y1 = y;
					j++;
				}
			}
		}
	
		Color back = getBackground();
		Color trans = new Color(back.getRed(), back.getGreen(), back.getBlue(), 150);
		g2.setPaint(trans);
		g2.fillRect(0, 0, 150, 70);
	
		g2.setPaint(gridColor);
		g2.drawRect(0, 0, w-1, h-1);
		
		g2.setPaint(UIManager.getColor("text"));
		g2.drawString("Name:", 7, 20);
		g2.drawString(commandName.name(), 50, 20);
		if (commandName.isIndexedA()==false) {
			g2.drawString("Value:", 7, 35);
			
			String valueStr = ""+dataPoint;
			int dotIndex = 0;
			if (CommandName.dev_amp==commandName) {
				Command cmd = PulseFireUI.getInstance().getDeviceData().getDeviceParameter(CommandName.dev_amp_dot);
				if (cmd!=null && cmd.getArgu0()!=null && cmd.getArgu0().isEmpty()==false) {
					dotIndex = new Integer(cmd.getArgu0());
				}
			}
			if (CommandName.dev_volt==commandName) {
				Command cmd = PulseFireUI.getInstance().getDeviceData().getDeviceParameter(CommandName.dev_volt_dot);
				if (cmd!=null && cmd.getArgu0()!=null && cmd.getArgu0().isEmpty()==false) {
					dotIndex = new Integer(cmd.getArgu0());
				}
			}
			if (CommandName.dev_temp==commandName) {
				Command cmd = PulseFireUI.getInstance().getDeviceData().getDeviceParameter(CommandName.dev_temp_dot);
				if (cmd!=null && cmd.getArgu0()!=null && cmd.getArgu0().isEmpty()==false) {
					dotIndex = new Integer(cmd.getArgu0());
				}
			}
			if (dotIndex>0) {
				int idx = valueStr.length()-dotIndex;
				if (idx<0) {
					idx = 0;
				}
				String dotValue = valueStr.substring(idx,valueStr.length());
				if (dotValue.length()<dotIndex) {
					int zeros = dotIndex-dotValue.length(); 
					for (int i=0;i<zeros;i++) {
						dotValue = "0"+dotValue;
					}
				}
				String numberValue = valueStr.substring(0,idx);
				if (numberValue.isEmpty()) {
					numberValue = "0";
				}
				valueStr = numberValue+"."+dotValue;
			}
			g2.drawString(valueStr, 50, 35);
		}
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

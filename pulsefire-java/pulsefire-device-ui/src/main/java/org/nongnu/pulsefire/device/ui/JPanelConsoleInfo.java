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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.nongnu.pulsefire.device.DeviceConnectListener;
import org.nongnu.pulsefire.device.ui.components.JFireGraph;
import org.nongnu.pulsefire.wire.CommandName;

/**
 * JPanelConsoleInfo
 * 
 * @author Willem Cazander
 */
public class JPanelConsoleInfo extends JPanel implements ComponentListener,DeviceConnectListener {

	private static final long serialVersionUID = 5027054951800480326L;
	private int graphsW = 0;
	private int graphsH = 0;
	private JPanel infoPanel = null;
	
	public JPanelConsoleInfo() {
		addComponentListener(this);
		PulseFireUI.getInstance().getDeviceManager().addDeviceConnectListener(this);
		setLayout(new FlowLayout(FlowLayout.LEFT));
		setBorder(BorderFactory.createEmptyBorder());
		
		infoPanel = createInfoPanel();
		add(infoPanel);
	}
	
	private JPanel createInfoPanel() {
		JPanel infoPanel = JComponentFactory.createJFirePanel();
		JLabel text = new JLabel();
		text.setText("<html><center><h1>PulseFire</h1><sub>Copyright 2011 Willem Cazander</sub><br>For more information visit website;<br>http://www.nongnu.org/pulsefire/<br></center></html>");
		infoPanel.add(text);
		return infoPanel;
	}
	
	private void redoPanel() {
		int w = getSize().width;
		int h = getSize().height;
		int wMin = 220;
		int hMin = 100;
		int gW = w/wMin;
		int gH = h/hMin;
		if (gW==graphsW & gH==graphsH) {
			return;
		}
		graphsW = gW;
		graphsH = gH;
		
		for (Component c:getComponents()) {
			if (c instanceof JFireGraph) {
				JFireGraph g = (JFireGraph)c;
				PulseFireUI.getInstance().getTimeData().removeTimeDataListener(g.getCommandName(), g);
			}
		}
		removeAll();
		if (PulseFireUI.getInstance().getTimeData().getTimeDataKeys().isEmpty()) {
			add(infoPanel);
			return;
		}
		
		Iterator<CommandName> i = PulseFireUI.getInstance().getTimeData().getTimeDataKeys().iterator();
		i.next();
		List<CommandName> d = new ArrayList<CommandName>(10);
		while (i.hasNext()) {
			CommandName name = i.next();
			if (name==CommandName.pwm_loop) {
				d.add(name);
				continue;
			}
			if (name==CommandName.pwm_on_cnt_a) {
				d.add(name);
				continue;
			}
			if (name.name().startsWith("pulse")) {
				continue;
			}
			if (name.name().startsWith("pwm")) {
				continue;
			}
			if (name.name().startsWith("ppm")) {
				continue;
			}
			if (name.name().startsWith("lpm")) {
				continue;
			}
			if (name.name().startsWith("ptc")) {
				continue;
			}
			if (name.name().startsWith("ptt")) {
				continue;
			}
			if (name.name().startsWith("ptt")) {
				continue;
			}
			d.add(name);
		}
		int ii=0;
		for (int y=0;y<gH;y++) {
			for (int x=0;x<gW;x++) {
				if (ii>d.size()) {
					break;
				}
				CommandName name = d.get(ii);
				JFireGraph g = new JFireGraph(name);
				g.setPreferredSize(new Dimension(wMin,hMin));
				add(g);
				ii++;
			}
		}
		if (getComponentCount()==0) {
			//add(infoPanel); // add info panel
		}
	}
	
	@Override
	public void componentShown(ComponentEvent e) {
	}
	@Override
	public void componentResized(ComponentEvent e) {
		redoPanel();
	}
	@Override
	public void componentMoved(ComponentEvent e) {
	}
	@Override
	public void componentHidden(ComponentEvent e) {
	}

	@Override
	public void deviceConnect() {
		redoPanel();
	}

	@Override
	public void deviceDisconnect() {
		for (Component c:getComponents()) {
			if (c instanceof JFireGraph) {
				JFireGraph g = (JFireGraph)c;
				PulseFireUI.getInstance().getTimeData().removeTimeDataListener(g.getCommandName(), g);
			}
		}
		removeAll();
	}
}

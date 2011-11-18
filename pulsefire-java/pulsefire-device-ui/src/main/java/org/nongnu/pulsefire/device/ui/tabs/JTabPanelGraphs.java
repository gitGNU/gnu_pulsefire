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

package org.nongnu.pulsefire.device.ui.tabs;

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.nongnu.pulsefire.device.ui.JComponentFactory;
import org.nongnu.pulsefire.device.ui.PulseFireUI;
import org.nongnu.pulsefire.device.ui.SpringLayoutGrid;
import org.nongnu.pulsefire.device.ui.components.JFireGraph;
import org.nongnu.pulsefire.wire.CommandName;

/**
 * JTabPanelGraphs
 * 
 * @author Willem Cazander
 */
public class JTabPanelGraphs extends AbstractTabPanel {

	private static final long serialVersionUID = -1416072133032318563L;
	private JPanel graphPanel = null;
	
	public JTabPanelGraphs() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		graphPanel = JComponentFactory.createJFirePanel("Graphs");
		graphPanel.setLayout(new SpringLayout());
		SpringLayoutGrid.makeCompactGrid(graphPanel,0,0);
		add(graphPanel);
	}
	
	@Override
	public Class<?> getTabClassName() {
		return this.getClass();
	}

	@Override
	public void deviceConnect() {
		int gTotal = PulseFireUI.getInstance().getTimeData().getTimeDataSize();
		for (CommandName name:PulseFireUI.getInstance().getTimeData().getTimeDataKeys()) {
			graphPanel.add(new JFireGraph(name));
		}
		int unevenDetection = (gTotal & 1);
		if (unevenDetection > 0) {
			graphPanel.add(JComponentFactory.createJPanelJWrap(new JLabel("fill")));
		}
		SpringLayoutGrid.makeCompactGrid(graphPanel,gTotal/2,2);
		super.deviceConnect();
	}

	@Override
	public void deviceDisconnect() {
		super.deviceDisconnect();
		for (Component c:graphPanel.getComponents()) {
			if (c instanceof JFireGraph) {
				JFireGraph g = (JFireGraph)c;
				PulseFireUI.getInstance().getTimeData().removeTimeDataListener(g.getCommandName(), g);
			}
		}
		graphPanel.removeAll();
	}
}

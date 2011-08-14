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

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SpringLayout;

import org.nongnu.pulsefire.device.ui.JComponentFactory;
import org.nongnu.pulsefire.device.ui.SpringLayoutGrid;
import org.nongnu.pulsefire.device.ui.components.JCommandButton;
import org.nongnu.pulsefire.device.ui.components.JCommandDial;
import org.nongnu.pulsefire.wire.CommandName;

/**
 * JTabPanelLPM
 * 
 * @author Willem Cazander
 */
public class JTabPanelLPM extends AbstractTabPanel {

	private static final long serialVersionUID = -6711428986888517858L;

	public JTabPanelLPM() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel wrap = new JPanel();
		wrap.setLayout(new SpringLayout());
		wrap.add(createLPM());
		wrap.add(createLPMTune());
		SpringLayoutGrid.makeCompactGrid(wrap,1,2);
		add(wrap);
	}
	
	private JPanel createLPM() {
		JPanel inputPanel = JComponentFactory.createJFirePanel("LPM");
		inputPanel.setLayout(new SpringLayout());
				
		inputPanel.add(JComponentFactory.createJLabel("Lpm Start"));
		inputPanel.add(JComponentFactory.createJPanelJWrap(new JCommandDial(CommandName.lpm_start)));
		
		inputPanel.add(JComponentFactory.createJLabel("Lpm Stop"));
		inputPanel.add(JComponentFactory.createJPanelJWrap(new JCommandDial(CommandName.lpm_stop)));

		inputPanel.add(JComponentFactory.createJLabel("Lpm Size"));
		inputPanel.add(JComponentFactory.createJPanelJWrap(new JCommandDial(CommandName.lpm_size)));
		
		inputPanel.add(JComponentFactory.createJLabel("Start Lpm"));
		inputPanel.add(new JCommandButton("Start",CommandName.req_auto_lpm));
		
		JProgressBar bar = new JProgressBar();
		bar.setIndeterminate(true);
		inputPanel.add(JComponentFactory.createJLabel("Lpm progress"));
		inputPanel.add(bar);
		
		SpringLayoutGrid.makeCompactGrid(inputPanel,5,2);
		return inputPanel;
	}
	
	private JPanel createLPMTune() {
		JPanel panel = JComponentFactory.createJFirePanel("Auto Tune");
		panel.add(new JLabel("Test___________________TABLE"));
		return panel;
	}
	
	@Override
	public Class<?> getTabClassName() {
		return this.getClass();
	}
}

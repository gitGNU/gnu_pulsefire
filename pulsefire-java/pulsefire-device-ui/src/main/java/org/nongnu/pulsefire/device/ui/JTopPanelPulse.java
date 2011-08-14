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

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.nongnu.pulsefire.device.ui.components.JCommandComboBox;
import org.nongnu.pulsefire.device.ui.components.JCommandDial;
import org.nongnu.pulsefire.wire.CommandName;

/**
 * JTopPanelPulse
 * 
 * @author Willem Cazander
 */
public class JTopPanelPulse extends JPanel {

	private static final long serialVersionUID = 4669061117315844030L;

	public JTopPanelPulse() {
		setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		setLayout(new GridLayout(1,0));
		JPanel borderPanel = JComponentFactory.createJFirePanel("Pulse");
		add(borderPanel);
		
		JPanel splitPanel = new JPanel();
		splitPanel.setLayout(new BoxLayout(splitPanel,BoxLayout.LINE_AXIS));
		borderPanel.add(splitPanel);
		
		JPanel pulsePanel = new JPanel();
		pulsePanel.setLayout(new SpringLayout());
		splitPanel.add(pulsePanel);

		pulsePanel.add(JComponentFactory.createJLabel("Mode"));
		pulsePanel.add(new JCommandComboBox(CommandName.pulse_mode));
	
		pulsePanel.add(JComponentFactory.createJLabel("Direction"));
		pulsePanel.add(new JCommandComboBox(CommandName.pulse_dir));
	
		pulsePanel.add(JComponentFactory.createJLabel("Trigger"));
		pulsePanel.add(new JCommandComboBox(CommandName.pulse_trig));
		
		pulsePanel.add(JComponentFactory.createJLabel("Bank"));
		pulsePanel.add(new JCommandComboBox(CommandName.pulse_bank));
		
		SpringLayoutGrid.makeCompactGrid(pulsePanel,4,2);
		
		JPanel delayPanel = new JPanel();
		delayPanel.setLayout(new SpringLayout());
		splitPanel.add(delayPanel);
		delayPanel.add(new JCommandDial(CommandName.pulse_trig_delay));
		delayPanel.add(new JCommandDial(CommandName.pulse_post_delay));
		SpringLayoutGrid.makeCompactGrid(delayPanel,2,1);
		
	}
}

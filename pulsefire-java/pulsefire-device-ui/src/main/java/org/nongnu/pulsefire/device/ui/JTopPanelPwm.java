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
 * JTopPanelPwm
 * 
 * @author Willem Cazander
 */
public class JTopPanelPwm extends JPanel {

	private static final long serialVersionUID = -8859922202002873631L;

	public JTopPanelPwm() {
		setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		setLayout(new GridLayout(1,0));
		JPanel borderPanel = JComponentFactory.createJFirePanel("PWM");
		add(borderPanel);
		
		JPanel splitPanel = new JPanel();
		splitPanel.setLayout(new BoxLayout(splitPanel,BoxLayout.LINE_AXIS));
		borderPanel.add(splitPanel);
		
		JPanel pwmPanel = new JPanel();
		pwmPanel.setLayout(new SpringLayout());
		splitPanel.add(pwmPanel);
		
		pwmPanel.add(JComponentFactory.createJLabel("Loop"));
		pwmPanel.add(JComponentFactory.createJPanelJWrap(new JCommandDial(CommandName.pwm_loop)));
		
		pwmPanel.add(JComponentFactory.createJLabel("Delta"));
		pwmPanel.add(JComponentFactory.createJPanelJWrap(new JCommandDial(CommandName.pwm_loop_delta)));
		
		SpringLayoutGrid.makeCompactGrid(pwmPanel,2,2);
		
		JPanel clockPanel = new JPanel();
		clockPanel.setLayout(new SpringLayout());
		splitPanel.add(clockPanel);
		
		clockPanel.add(JComponentFactory.createJPanelJWrap(new JCommandComboBox(CommandName.pwm_clock)));
		
		SpringLayoutGrid.makeCompactGrid(clockPanel,1,1);
	}
}

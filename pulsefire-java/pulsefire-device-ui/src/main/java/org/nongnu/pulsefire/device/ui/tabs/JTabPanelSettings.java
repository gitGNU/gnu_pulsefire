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

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.nongnu.pulsefire.device.ui.JComponentFactory;
import org.nongnu.pulsefire.device.ui.SpringLayoutGrid;

/**
 * JTabPanelSettings
 * 
 * @author Willem Cazander
 */
public class JTabPanelSettings extends AbstractTabPanel {

	private static final long serialVersionUID = -1646229038565969537L;

	public JTabPanelSettings() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel loggingPanel = new JPanel();
		loggingPanel.setLayout(new SpringLayout());
		
		loggingPanel.add(JComponentFactory.createJLabel("Limit Channels"));
		JCheckBox c = new JCheckBox();
		loggingPanel.add(c);
		
		loggingPanel.add(JComponentFactory.createJLabel("Connect Auto"));
		loggingPanel.add(new JCheckBox());
		
		loggingPanel.add(JComponentFactory.createJLabel("Connect Timeout"));
		loggingPanel.add(new JTextField(20));
		
		SpringLayoutGrid.makeCompactGrid(loggingPanel,3,2);
		add(loggingPanel);
	}
	
	@Override
	public Class<?> getTabClassName() {
		return this.getClass();
	}
}

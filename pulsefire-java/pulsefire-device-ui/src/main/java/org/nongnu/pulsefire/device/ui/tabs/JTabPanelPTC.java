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
import javax.swing.SpringLayout;

import org.nongnu.pulsefire.device.ui.JComponentFactory;
import org.nongnu.pulsefire.device.ui.SpringLayoutGrid;
import org.nongnu.pulsefire.device.ui.components.JCommandDial;
import org.nongnu.pulsefire.device.ui.components.JFireQMapTable;
import org.nongnu.pulsefire.wire.CommandName;

/**
 * JTabPanelPTC
 * 
 * @author Willem Cazander
 */
public class JTabPanelPTC extends AbstractTabPanel {

	private static final long serialVersionUID = -1646229038565969537L;

	public JTabPanelPTC() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel wrap = new JPanel();
		wrap.setLayout(new SpringLayout());
		wrap.add(createTimeConfPanel0());
		wrap.add(createTimeConfPanel1());
		wrap.add(createTimePanel0());
		wrap.add(createTimePanel1());
		SpringLayoutGrid.makeCompactGrid(wrap,2,2);
		add(wrap);
	}
	
	private JPanel createTimeConfPanel0 () {
		JPanel panel = JComponentFactory.createJFirePanel("Config Timer 0");
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(new JLabel("Run Times"));
		panel.add(new JCommandDial(CommandName.ptc_0run));
		panel.add(new JLabel("Time Multi"));
		panel.add(new JCommandDial(CommandName.ptc_0mul));
		return panel;
	}
	
	private JPanel createTimeConfPanel1 () {
		JPanel panel = JComponentFactory.createJFirePanel("Config Timer 1");
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(new JLabel("Run Times"));
		panel.add(new JCommandDial(CommandName.ptc_1run));
		panel.add(new JLabel("Time Multi"));
		panel.add(new JCommandDial(CommandName.ptc_1mul));
		return panel;
	}
	
	private JPanel createTimePanel0 () {
		JPanel panel = JComponentFactory.createJFirePanel("Slots Timer 0");
		panel.add(new JFireQMapTable(CommandName.ptc_0map,"value","time"));
		return panel;
	}
	
	private JPanel createTimePanel1 () {
		JPanel panel = JComponentFactory.createJFirePanel("Slots Timer 1");
		panel.add(new JFireQMapTable(CommandName.ptc_1map,"value","time"));
		return panel;
	}
	
	@Override
	public Class<?> getTabClassName() {
		return this.getClass();
	}
}

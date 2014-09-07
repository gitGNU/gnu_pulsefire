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

import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.nongnu.pulsefire.device.ui.JComponentFactory;
import org.nongnu.pulsefire.device.ui.SpringLayoutGrid;
import org.nongnu.pulsefire.device.ui.components.JCommandComboBox;
import org.nongnu.pulsefire.device.ui.components.JCommandDial;
import org.nongnu.pulsefire.device.ui.components.JCommandLabel;
import org.nongnu.pulsefire.wire.CommandName;

/**
 * JTabPanelVsc
 * 
 * @author Willem Cazander
 */
public class JTabPanelVsc extends AbstractFireTabPanel {
	
	public JTabPanelVsc() {
		JPanel wrap = new JPanel();
		wrap.setLayout(new SpringLayout());
		wrap.add(createVscConfPanel0());
		wrap.add(createVscConfPanel1());
		wrap.add(JComponentFactory.createJFirePanelQMapTable(this, "vsc_0map", CommandName.vsc_0map,"min","max"));
		wrap.add(JComponentFactory.createJFirePanelQMapTable(this, "vsc_1map", CommandName.vsc_1map,"min","max"));
		SpringLayoutGrid.makeCompactGrid(wrap,2,2);
		getJPanel().add(wrap);
	}
	
	private JPanel createVscConfPanel0 () {
		JPanel panel = JComponentFactory.createJFirePanel(this,"config0");
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(new JCommandLabel(	CommandName.vsc_0time));
		panel.add(new JCommandDial(		CommandName.vsc_0time));
		panel.add(new JCommandLabel(	CommandName.vsc_0step));
		panel.add(new JCommandDial(		CommandName.vsc_0step));
		panel.add(new JCommandLabel(	CommandName.vsc_0mode));
		panel.add(new JCommandComboBox(	CommandName.vsc_0mode));
		return panel;
	}
	
	private JPanel createVscConfPanel1 () {
		JPanel panel = JComponentFactory.createJFirePanel(this,"config1");
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(new JCommandLabel(	CommandName.vsc_1time));
		panel.add(new JCommandDial(		CommandName.vsc_1time));
		panel.add(new JCommandLabel(	CommandName.vsc_1step));
		panel.add(new JCommandDial(		CommandName.vsc_1step));
		panel.add(new JCommandLabel(	CommandName.vsc_1mode));
		panel.add(new JCommandComboBox(	CommandName.vsc_1mode));
		return panel;
	}
}

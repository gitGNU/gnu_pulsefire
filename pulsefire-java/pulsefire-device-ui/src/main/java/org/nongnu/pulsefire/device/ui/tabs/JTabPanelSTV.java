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
import org.nongnu.pulsefire.device.ui.components.JCommandComboBox;
import org.nongnu.pulsefire.device.ui.components.JCommandDial;
import org.nongnu.pulsefire.device.ui.components.JFireQMapTable;
import org.nongnu.pulsefire.wire.CommandName;

/**
 * JTabPanelSTV
 * 
 * @author Willem Cazander
 */
public class JTabPanelSTV extends AbstractTabPanel {

	private static final long serialVersionUID = 2716662787208065889L;

	public JTabPanelSTV() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel wrap = new JPanel();
		wrap.setLayout(new SpringLayout());
		wrap.add(createMaxConfPanel());
		wrap.add(createMinConfPanel());
		wrap.add(createMaxPanel());
		wrap.add(createMinPanel());
		SpringLayoutGrid.makeCompactGrid(wrap,2,2);
		add(wrap);
	}
	
	private JPanel createMaxConfPanel() {
		JPanel panel = JComponentFactory.createJFirePanel("Warning Config");
		panel.add(new JLabel("Warn Secs"));
		panel.add(new JCommandDial(CommandName.stv_warn_secs));
		panel.add(new JLabel("Warn Mode"));
		panel.add(new JCommandComboBox(CommandName.stv_warn_mode));
		return panel;
	}
	
	private JPanel createMinConfPanel() {
		JPanel panel = JComponentFactory.createJFirePanel("Error Config");
		panel.add(new JLabel("Error Secs"));
		panel.add(new JCommandDial(CommandName.stv_error_secs));
		panel.add(new JLabel("Error Mode"));
		panel.add(new JCommandComboBox(CommandName.stv_error_mode));
		return panel;
	}
	
	private JPanel createMaxPanel() {
		JPanel panel = JComponentFactory.createJFirePanel("Maximal Values");
		panel.add(new JFireQMapTable(CommandName.stv_max_map,"warning","error"));
		return panel;
	}
	
	private JPanel createMinPanel() {
		JPanel panel = JComponentFactory.createJFirePanel("Minimal Values");
		panel.add(new JFireQMapTable(CommandName.stv_min_map,"warning","error"));
		return panel;
	}

	@Override
	public Class<?> getTabClassName() {
		return this.getClass();
	}
}

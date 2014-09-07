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
import org.nongnu.pulsefire.device.ui.components.JCommandLabel;
import org.nongnu.pulsefire.device.ui.components.JCommandSpinner;
import org.nongnu.pulsefire.wire.CommandName;

/**
 * JTabPanelSTV
 * 
 * @author Willem Cazander
 */
public class JTabPanelStv extends AbstractFireTabPanel {

	public JTabPanelStv() {
		JPanel wrap = new JPanel();
		wrap.setLayout(new SpringLayout());
		wrap.add(createWarnConfPanel());
		wrap.add(createErrorConfPanel());
		wrap.add(JComponentFactory.createJFirePanelQMapTable(this, "warningActions", CommandName.stv_warn_map,"warning","warning-exit"));
		wrap.add(JComponentFactory.createJFirePanelQMapTable(this, "errorActions", CommandName.stv_error_map,"error","error-exit"));
		wrap.add(JComponentFactory.createJFirePanelQMapTable(this, "maxValues", CommandName.stv_max_map,"warning","error"));
		wrap.add(JComponentFactory.createJFirePanelQMapTable(this, "minValues", CommandName.stv_min_map,"warning","error"));
		SpringLayoutGrid.makeCompactGrid(wrap,3,2);
		getJPanel().add(wrap);
	}
	
	private JPanel createWarnConfPanel() {
		JPanel panel = JComponentFactory.createJFirePanel(this,"warningConfig");
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(new JCommandLabel(CommandName.stv_warn_secs));
		panel.add(new JCommandSpinner(CommandName.stv_warn_secs));
		return panel;
	}
	
	private JPanel createErrorConfPanel() {
		JPanel panel = JComponentFactory.createJFirePanel(this,"errorConfig");
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(new JCommandLabel(CommandName.stv_error_secs));
		panel.add(new JCommandSpinner(CommandName.stv_error_secs));
		return panel;
	}
}

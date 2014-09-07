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

import org.nongnu.pulsefire.device.io.protocol.CommandName;
import org.nongnu.pulsefire.device.ui.JComponentFactory;
import org.nongnu.pulsefire.device.ui.SpringLayoutGrid;
import org.nongnu.pulsefire.device.ui.components.JCommandComboBox;
import org.nongnu.pulsefire.device.ui.components.JCommandDial;
import org.nongnu.pulsefire.device.ui.components.JCommandLabel;

/**
 * JTabPanelCip
 * 
 * @author Willem Cazander
 */
public class JTabPanelCip extends AbstractFireTabPanel {
	
	public JTabPanelCip() {
		JPanel wrap = new JPanel();
		wrap.setLayout(new SpringLayout());
		wrap.add(createCip0());
		wrap.add(createCip1());
		wrap.add(createCip2());
		SpringLayoutGrid.makeCompactGrid(wrap,3,1);
		getJPanel().add(wrap);
	}
	
	private JPanel createCip0() {
		JPanel firePanel = JComponentFactory.createJFirePanel("Chip Pwm 0");
		firePanel.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
		JPanel panel = new JPanel();
		panel.setLayout(new SpringLayout());
		panel.add(new JCommandLabel		(CommandName.cip_0clock));
		panel.add(new JCommandComboBox	(CommandName.cip_0clock));
		panel.add(new JCommandLabel		(CommandName.cip_0a_com));
		panel.add(new JCommandComboBox	(CommandName.cip_0a_com));
		panel.add(new JCommandLabel		(CommandName.cip_0mode));
		panel.add(new JCommandComboBox	(CommandName.cip_0mode));
		panel.add(new JCommandLabel		(CommandName.cip_0b_com));
		panel.add(new JCommandComboBox	(CommandName.cip_0b_com));
		panel.add(new JLabel());
		panel.add(new JLabel());
		panel.add(new JCommandLabel		(CommandName.cip_0c_com));
		panel.add(new JCommandComboBox	(CommandName.cip_0c_com));
		SpringLayoutGrid.makeCompactGrid(panel,3,4);
		firePanel.add(panel);
		firePanel.add(new JCommandDial(CommandName.cip_0a_ocr));
		firePanel.add(new JCommandDial(CommandName.cip_0b_ocr));
		firePanel.add(new JCommandDial(CommandName.cip_0c_ocr));
		return firePanel;
	}
	
	private JPanel createCip1() {
		JPanel firePanel = JComponentFactory.createJFirePanel("Chip Pwm 1");
		firePanel.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
		JPanel panel = new JPanel();
		panel.setLayout(new SpringLayout());
		panel.add(new JCommandLabel		(CommandName.cip_1clock));
		panel.add(new JCommandComboBox	(CommandName.cip_1clock));
		panel.add(new JCommandLabel		(CommandName.cip_1a_com));
		panel.add(new JCommandComboBox	(CommandName.cip_1a_com));
		panel.add(new JCommandLabel		(CommandName.cip_1mode));
		panel.add(new JCommandComboBox	(CommandName.cip_1mode));
		panel.add(new JCommandLabel		(CommandName.cip_1b_com));
		panel.add(new JCommandComboBox	(CommandName.cip_1b_com));
		panel.add(new JLabel());
		panel.add(new JLabel());
		panel.add(new JCommandLabel		(CommandName.cip_1c_com));
		panel.add(new JCommandComboBox	(CommandName.cip_1c_com));
		SpringLayoutGrid.makeCompactGrid(panel,3,4);
		firePanel.add(panel);
		firePanel.add(new JCommandDial(CommandName.cip_1a_ocr));
		firePanel.add(new JCommandDial(CommandName.cip_1b_ocr));
		firePanel.add(new JCommandDial(CommandName.cip_1c_ocr));
		return firePanel;
	}
	
	private JPanel createCip2() {
		JPanel firePanel = JComponentFactory.createJFirePanel("Chip Pwm 2");
		firePanel.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
		JPanel panel = new JPanel();
		panel.setLayout(new SpringLayout());
		panel.add(new JCommandLabel		(CommandName.cip_2clock));
		panel.add(new JCommandComboBox	(CommandName.cip_2clock));
		panel.add(new JCommandLabel		(CommandName.cip_2a_com));
		panel.add(new JCommandComboBox	(CommandName.cip_2a_com));
		panel.add(new JCommandLabel		(CommandName.cip_2mode));
		panel.add(new JCommandComboBox	(CommandName.cip_2mode));
		panel.add(new JCommandLabel		(CommandName.cip_2b_com));
		panel.add(new JCommandComboBox	(CommandName.cip_2b_com));
		panel.add(new JLabel());
		panel.add(new JLabel());
		panel.add(new JCommandLabel		(CommandName.cip_2c_com));
		panel.add(new JCommandComboBox	(CommandName.cip_2c_com));
		SpringLayoutGrid.makeCompactGrid(panel,3,4);
		firePanel.add(panel);
		firePanel.add(new JCommandDial(CommandName.cip_2a_ocr));
		firePanel.add(new JCommandDial(CommandName.cip_2b_ocr));
		firePanel.add(new JCommandDial(CommandName.cip_2c_ocr));
		return firePanel;
	}
}

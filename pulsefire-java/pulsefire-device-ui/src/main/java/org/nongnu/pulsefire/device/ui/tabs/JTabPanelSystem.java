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

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.nongnu.pulsefire.device.ui.JComponentFactory;
import org.nongnu.pulsefire.device.ui.SpringLayoutGrid;
import org.nongnu.pulsefire.device.ui.components.JCommandButton;
import org.nongnu.pulsefire.device.ui.components.JCommandCheckBox;
import org.nongnu.pulsefire.device.ui.components.JCommandComboBox;
import org.nongnu.pulsefire.device.ui.components.JCommandDial;
import org.nongnu.pulsefire.wire.CommandName;

/**
 * JTabPanelSystem
 * 
 * @author Willem Cazander
 */
public class JTabPanelSystem extends AbstractTabPanel {

	private static final long serialVersionUID = -5523263800067726564L;

	public JTabPanelSystem() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel wrap = new JPanel();
		wrap.setLayout(new SpringLayout());
		wrap.add(createSystemConfig());
		wrap.add(createSystemIO());
		wrap.add(createSystemWarmup());		
		SpringLayoutGrid.makeCompactGrid(wrap,1,3);
		add(wrap);
	}
	
	private JPanel createSystemConfig() {
		JPanel wrapPanel = JComponentFactory.createJFirePanel("Config");
		JPanel confPanel = new JPanel();
		confPanel.setLayout(new SpringLayout());
		
		confPanel.add(JComponentFactory.createJLabel("Save Config"));
		confPanel.add(new JCommandButton("Save",CommandName.save));
		
		confPanel.add(JComponentFactory.createJLabel("Reset Config"));
		confPanel.add(new JCommandButton("Reset",CommandName.reset_conf));

		confPanel.add(JComponentFactory.createJLabel("Reset Data"));
		confPanel.add(new JCommandButton("Reset",CommandName.reset_data));

		confPanel.add(JComponentFactory.createJLabel("Reset Chip"));
		confPanel.add(new JCommandButton("Reset",CommandName.reset_chip));
		
		JButton loadButton = new JButton("Load");
		loadButton.setEnabled(false);
		confPanel.add(JComponentFactory.createJLabel("Load File"));
		confPanel.add(loadButton);
		
		JButton saveButton = new JButton("Save");
		saveButton.setEnabled(false);
		confPanel.add(JComponentFactory.createJLabel("Save File"));
		confPanel.add(saveButton);
		
		SpringLayoutGrid.makeCompactGrid(confPanel,6,2);
		wrapPanel.add(confPanel);
		return wrapPanel;
	}

	private JPanel createSystemIO() {
		JPanel wrapPanel = JComponentFactory.createJFirePanel("IO");
		JPanel ioPanel = new JPanel();
		ioPanel.setLayout(new SpringLayout());
		
		ioPanel.add(JComponentFactory.createJLabel("Output Enable"));
		ioPanel.add(new JCommandCheckBox(CommandName.pulse_enable));
		
		ioPanel.add(JComponentFactory.createJLabel("Output Invert"));
		ioPanel.add(new JCommandCheckBox(CommandName.pulse_inv));

		ioPanel.add(JComponentFactory.createJLabel("Output Steps"));
		ioPanel.add(new JCommandComboBox(CommandName.pulse_steps));
		
		ioPanel.add(JComponentFactory.createJLabel("Pin2 Mapping"));
		ioPanel.add(new JCommandComboBox(CommandName.avr_pin2_map));
		
		ioPanel.add(JComponentFactory.createJLabel("Pin3 Mapping"));
		ioPanel.add(new JCommandComboBox(CommandName.avr_pin3_map));
		
		ioPanel.add(JComponentFactory.createJLabel("Pin4 Mapping"));
		ioPanel.add(new JCommandComboBox(CommandName.avr_pin4_map));
		
		ioPanel.add(JComponentFactory.createJLabel("Pin5 Mapping"));
		ioPanel.add(new JCommandComboBox(CommandName.avr_pin5_map));
		
		ioPanel.add(JComponentFactory.createJLabel("Lcd Size"));
		ioPanel.add(new JCommandComboBox(CommandName.lcd_size));
		
		ioPanel.add(JComponentFactory.createJLabel("Dev volt dot"));
		ioPanel.add(new JCommandComboBox(CommandName.dev_volt_dot));
		ioPanel.add(JComponentFactory.createJLabel("Dev amp dot"));
		ioPanel.add(new JCommandComboBox(CommandName.dev_amp_dot));
		ioPanel.add(JComponentFactory.createJLabel("Dev temp dot"));
		ioPanel.add(new JCommandComboBox(CommandName.dev_temp_dot));
		
		SpringLayoutGrid.makeCompactGrid(ioPanel,11,2);
		wrapPanel.add(ioPanel);
		return wrapPanel;
	}
	
	private JPanel createSystemWarmup() {
		JPanel wrapPanel = JComponentFactory.createJFirePanel("Warmup");
		wrapPanel.setLayout(new BoxLayout(wrapPanel,BoxLayout.PAGE_AXIS));
		
		JPanel warmDialPanel = new JPanel();
		warmDialPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		warmDialPanel.add(JComponentFactory.createJPanelJWrap(new JCommandDial(CommandName.swc_delay)));
		warmDialPanel.add(JComponentFactory.createJPanelJWrap(new JCommandDial(CommandName.swc_secs)));
		warmDialPanel.add(JComponentFactory.createJPanelJWrap(new JCommandDial(CommandName.swc_duty)));
		wrapPanel.add(warmDialPanel);
		
		JPanel warmPanel = new JPanel();
		warmPanel.setLayout(new SpringLayout());
		warmPanel.add(JComponentFactory.createJLabel("Warmup Mode"));
		warmPanel.add(JComponentFactory.createJPanelJWrap(new JCommandComboBox(CommandName.swc_mode)));
		warmPanel.add(JComponentFactory.createJLabel("Warmup Trigger"));
		warmPanel.add(JComponentFactory.createJPanelJWrap(new JCommandComboBox(CommandName.swc_trig)));
		SpringLayoutGrid.makeCompactGrid(warmPanel,2,2);
		wrapPanel.add(warmPanel);
		
		return wrapPanel;
	}	

	@Override
	public Class<?> getTabClassName() {
		return this.getClass();
	}
}

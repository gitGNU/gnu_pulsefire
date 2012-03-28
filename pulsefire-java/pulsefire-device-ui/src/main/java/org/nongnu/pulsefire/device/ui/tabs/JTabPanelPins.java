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
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.nongnu.pulsefire.device.ui.JComponentFactory;
import org.nongnu.pulsefire.device.ui.SpringLayoutGrid;
import org.nongnu.pulsefire.device.ui.components.JCommandComboBox;
import org.nongnu.pulsefire.wire.CommandName;

/**
 * JTabPanelSystem
 * 
 * @author Willem Cazander
 */
public class JTabPanelPins extends AbstractFireTabPanel {

	private static final long serialVersionUID = -552322342345005654L;

	public JTabPanelPins() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel wrap = new JPanel();
		wrap.setLayout(new SpringLayout());
		wrap.add(createPinsAvr());
		wrap.add(createPinsAvrMega());
		wrap.add(createPinsArm());		
		SpringLayoutGrid.makeCompactGrid(wrap,1,3);
		add(wrap);
	}
	
	private JPanel createPinsAvr() {
		JPanel wrapPanel = JComponentFactory.createJFirePanel("Pins Avr");
		JPanel ioPanel = new JPanel();
		ioPanel.setLayout(new SpringLayout());
		
		ioPanel.add(JComponentFactory.createJLabel("Pin2 Mapping"));
		ioPanel.add(new JCommandComboBox(CommandName.avr_pin2_map));
		
		ioPanel.add(JComponentFactory.createJLabel("Pin3 Mapping"));
		ioPanel.add(new JCommandComboBox(CommandName.avr_pin3_map));
		
		ioPanel.add(JComponentFactory.createJLabel("Pin4 Mapping"));
		ioPanel.add(new JCommandComboBox(CommandName.avr_pin4_map));
		
		ioPanel.add(JComponentFactory.createJLabel("Pin5 Mapping"));
		ioPanel.add(new JCommandComboBox(CommandName.avr_pin5_map));
		
		ioPanel.add(JComponentFactory.createJLabel("Note: Pin "));
		ioPanel.add(JComponentFactory.createJLabel("direction change chip restart"));
		
		SpringLayoutGrid.makeCompactGrid(ioPanel,5,2);
		wrapPanel.add(ioPanel);
		return wrapPanel;
	}

	private JPanel createPinsAvrMega() {
		JPanel wrapPanel = JComponentFactory.createJFirePanel("Pins Mega");
		JPanel ioPanel = new JPanel();
		ioPanel.setLayout(new SpringLayout());
		
		ioPanel.add(JComponentFactory.createJLabel("Pin18 Mapping"));
		ioPanel.add(new JCommandComboBox(CommandName.avr_pin18_map));
		
		ioPanel.add(JComponentFactory.createJLabel("Pin19 Mapping"));
		ioPanel.add(new JCommandComboBox(CommandName.avr_pin19_map));
		
		ioPanel.add(JComponentFactory.createJLabel("Pin47 Mapping"));
		ioPanel.add(new JCommandComboBox(CommandName.avr_pin47_map));
		
		ioPanel.add(JComponentFactory.createJLabel("Pin48 Mapping"));
		ioPanel.add(new JCommandComboBox(CommandName.avr_pin48_map));
		
		ioPanel.add(JComponentFactory.createJLabel("Pin49 Mapping"));
		ioPanel.add(new JCommandComboBox(CommandName.avr_pin49_map));
		
		SpringLayoutGrid.makeCompactGrid(ioPanel,5,2);
		wrapPanel.add(ioPanel);
		return wrapPanel;
	}
	
	private JPanel createPinsArm() {
		JPanel wrapPanel = JComponentFactory.createJFirePanel("Pins Arm");
		wrapPanel.setLayout(new BoxLayout(wrapPanel,BoxLayout.PAGE_AXIS));
		wrapPanel.add(JComponentFactory.createJPanelJWrap(JComponentFactory.createJLabel("  __  todo  __  ")));
		return wrapPanel;
	}	

	@Override
	public Class<?> getTabClassName() {
		return this.getClass();
	}
}

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
import org.nongnu.pulsefire.device.ui.components.JCommandLabel;
import org.nongnu.pulsefire.wire.CommandName;

/**
 * JTabPanelCit
 * 
 * @author Willem Cazander
 */
public class JTabPanelCit extends AbstractFireTabPanel {

	private static final long serialVersionUID = -1646229038565969537L;
	
	
	public JTabPanelCit() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel wrap = new JPanel();
		wrap.setLayout(new SpringLayout());
		wrap.add(createCit0());
		wrap.add(createCitMap0());
		SpringLayoutGrid.makeCompactGrid(wrap,2,1);
		add(wrap);
	}
	
	private JPanel createCit0 () {
		JPanel firePanel = JComponentFactory.createJFirePanel("Timer 0");
		firePanel.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
		
		JPanel panel = new JPanel();
		panel.setLayout(new SpringLayout());
		panel.add(new JCommandLabel		(CommandName.cit_0clock));
		panel.add(new JCommandComboBox	(CommandName.cit_0clock));
		panel.add(new JCommandLabel		(CommandName.cit_0a_com));
		panel.add(new JCommandComboBox	(CommandName.cit_0a_com));

		//panel.add(new JCommandComboBox	(CommandName.cit_0a_com));
		panel.add(new JCommandLabel		(CommandName.cit_0mode));
		panel.add(new JCommandComboBox	(CommandName.cit_0mode));
		panel.add(new JCommandLabel		(CommandName.cit_0b_com));
		panel.add(new JCommandComboBox	(CommandName.cit_0b_com));
		SpringLayoutGrid.makeCompactGrid(panel,2,4);
		firePanel.add(panel);
		
		firePanel.add(new JCommandDial(CommandName.cit_0a_ocr));
		firePanel.add(new JCommandDial(CommandName.cit_0b_ocr));		
		return firePanel;
	}
	
	private JPanel createCitMap0 () {
		JPanel firePanel = JComponentFactory.createJFirePanel("Timer Events");
		firePanel.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
		
		JPanel mapPanel = new JPanel();
		mapPanel.setLayout(new SpringLayout());
		//mapPanel.add(new JFireQMapTable(CommandName.cit_0a_map,"ocrA","zero"));
		//mapPanel.add(new JFireQMapTable(CommandName.cit_0b_map,"ocrB","zero"));
		mapPanel.add(new JLabel("Pulse CIP-0 output:"));
		mapPanel.add(new JLabel("false"));
		SpringLayoutGrid.makeCompactGrid(mapPanel,1,2);
		firePanel.add(mapPanel);
		
		return firePanel;
	}
	
	@Override
	public Class<?> getTabClassName() {
		return this.getClass();
	}
}

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
import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.nongnu.pulsefire.device.ui.JComponentFactory;
import org.nongnu.pulsefire.device.ui.SpringLayoutGrid;
import org.nongnu.pulsefire.device.ui.components.JCommandCheckBox;
import org.nongnu.pulsefire.device.ui.components.JCommandDial;
import org.nongnu.pulsefire.device.ui.components.JFireQMapTable;
import org.nongnu.pulsefire.wire.CommandName;

/**
 * JTabPanelInput
 * 
 * @author Willem Cazander
 */
public class JTabPanelInput extends AbstractTabPanel {

	private static final long serialVersionUID = 2716662787208065889L;

	public JTabPanelInput() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel wrap = new JPanel();
		wrap.setLayout(new SpringLayout());
		wrap.add(createAdcOptionPanel());
		wrap.add(createDicOptionPanel());
		wrap.add(createAdcPanel());
		wrap.add(createDicPanel());
		SpringLayoutGrid.makeCompactGrid(wrap,2,2);
		add(wrap);
	}
	
	private JPanel createAdcOptionPanel() {
		JPanel adcOptionPanel = JComponentFactory.createJFirePanel("Analog Options");
		adcOptionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		adcOptionPanel.add(new JLabel("Jitter"));
		adcOptionPanel.add(new JCommandDial(CommandName.adc_jitter));
		adcOptionPanel.add(new JLabel("Enable"));
		
		JPanel enablePanel = new JPanel();
		enablePanel.setLayout(new GridLayout(0,4));
		for (int i=0;i<16;i++) {
			JCheckBox enableMask = new JCommandCheckBox(CommandName.adc_enable,i);
			enableMask.setText(""+i);
			enableMask.putClientProperty("JComponent.sizeVariant", "mini");
			enablePanel.add(enableMask);
		}
		
		adcOptionPanel.add(enablePanel);
		return adcOptionPanel;
	}
	
	private JPanel createAdcPanel() {
		JPanel adcPanel = JComponentFactory.createJFirePanel("Analog");
		adcPanel.add(new JFireQMapTable(CommandName.adc_map,"map-min","map-max"));
		return adcPanel;
	}
	
	private JPanel createDicOptionPanel() {
		JPanel adcOptionPanel = JComponentFactory.createJFirePanel("Digital Options");
		adcOptionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		adcOptionPanel.add(new JLabel("Enable"));
		JPanel enablePanel = new JPanel();
		enablePanel.setLayout(new GridLayout(0,4));
		for (int i=0;i<16;i++) {
			JCheckBox enableMask = new JCommandCheckBox(CommandName.dic_enable,i);
			enableMask.setText(""+i);
			enableMask.putClientProperty("JComponent.sizeVariant", "mini");
			enablePanel.add(enableMask);
		}
		adcOptionPanel.add(enablePanel);
		
		adcOptionPanel.add(new JLabel("Invert"));
		JPanel invPanel = new JPanel();
		invPanel.setLayout(new GridLayout(0,4));
		for (int i=0;i<16;i++) {
			JCheckBox invMask = new JCommandCheckBox(CommandName.dic_inv,i);
			invMask.setText(""+i);
			invMask.putClientProperty("JComponent.sizeVariant", "mini");
			invPanel.add(invMask);
		}
		adcOptionPanel.add(invPanel);
		
		adcOptionPanel.add(new JLabel("Sync"));
		JPanel syncPanel = new JPanel();
		syncPanel.setLayout(new GridLayout(0,4));
		for (int i=0;i<16;i++) {
			JCheckBox syncMask = new JCommandCheckBox(CommandName.dic_sync,i);
			syncMask.setText(""+i);
			syncMask.putClientProperty("JComponent.sizeVariant", "mini");
			syncPanel.add(syncMask);
		}
		adcOptionPanel.add(syncPanel);
		
		return adcOptionPanel;
	}
	
	private JPanel createDicPanel() {
		JPanel dicPanel = JComponentFactory.createJFirePanel("Digital");
		dicPanel.add(new JFireQMapTable(CommandName.dic_map,"low","high"));
		return dicPanel;
	}
	
	@Override
	public Class<?> getTabClassName() {
		return this.getClass();
	}
}

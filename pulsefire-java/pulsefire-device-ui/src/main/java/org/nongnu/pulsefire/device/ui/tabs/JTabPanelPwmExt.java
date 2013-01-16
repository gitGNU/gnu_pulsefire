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
import org.nongnu.pulsefire.device.ui.components.JCommandButton;
import org.nongnu.pulsefire.device.ui.components.JCommandCheckBox;
import org.nongnu.pulsefire.device.ui.components.JCommandComboBox;
import org.nongnu.pulsefire.device.ui.components.JCommandDial;
import org.nongnu.pulsefire.device.ui.components.JCommandLabel;
import org.nongnu.pulsefire.device.ui.components.JFireQMapTable;
import org.nongnu.pulsefire.wire.CommandName;

/**
 * JTabPanelPwmExt extended pwm controls.
 * 
 * @author Willem Cazander
 */
public class JTabPanelPwmExt extends AbstractFireTabPanel {

	private static final long serialVersionUID = -1646229038565969537L;
	
	
	public JTabPanelPwmExt() {
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new SpringLayout());
		topPanel.add(createPulseOutput());
		topPanel.add(createPulseHold());
		topPanel.add(createPulseTriggers());
		SpringLayoutGrid.makeCompactGrid(topPanel,1,3,0,0,0,0);
		
		JPanel wrap = new JPanel();
		wrap.setLayout(new SpringLayout());
		wrap.add(topPanel);
		wrap.add(createPulseEvents());
		SpringLayoutGrid.makeCompactGrid(wrap,2,1,6,6,6,6);
		
		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(wrap);
	}

	private JPanel createPulseOutput() {
		JPanel topPanel = JComponentFactory.createJFirePanel("Output");
		topPanel.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
		JPanel pulsePanel = new JPanel();
		pulsePanel.setLayout(new SpringLayout());
		pulsePanel.add(new JCommandLabel	(CommandName.pulse_steps));
		pulsePanel.add(new JCommandComboBox	(CommandName.pulse_steps));
		pulsePanel.add(new JCommandLabel	(CommandName.pulse_enable));
		pulsePanel.add(new JCommandCheckBox	(CommandName.pulse_enable));
		//pulsePanel.add(new JCommandLabel	(CommandName.pulse_inv));
		//pulsePanel.add(new JCommandCheckBox	(CommandName.pulse_inv));
		SpringLayoutGrid.makeCompactGrid(pulsePanel,2,2);
		topPanel.add(pulsePanel);
		return topPanel;
	}
	
	private JPanel createPulseTriggers() {
		JPanel butPanel = JComponentFactory.createJFirePanel("Triggers");
		butPanel.setLayout(new SpringLayout());
		butPanel.add(new JCommandButton(CommandName.req_trigger,CommandName.pulse_fire,null));
		butPanel.add(new JCommandButton(CommandName.req_trigger,CommandName.pulse_reset_fire,null));
		butPanel.add(new JCommandButton(CommandName.req_trigger,CommandName.pulse_hold_fire,null));
		butPanel.add(new JCommandButton(CommandName.req_trigger,CommandName.pulse_resume_fire,null));
		SpringLayoutGrid.makeCompactGrid(butPanel,2,2);
		return butPanel;
	}
	
	private JPanel createPulseHold() {
		JPanel firePanel = JComponentFactory.createJFirePanel("Pulse Control");
		firePanel.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
		
		JPanel pulsePanel = new JPanel();
		pulsePanel.setLayout(new SpringLayout());
		pulsePanel.add(new JCommandLabel	(CommandName.pulse_fire_mode));
		pulsePanel.add(new JCommandComboBox	(CommandName.pulse_fire_mode));
		pulsePanel.add(new JCommandLabel	(CommandName.pulse_hold_mode));
		pulsePanel.add(new JCommandComboBox	(CommandName.pulse_hold_mode));
		pulsePanel.add(new JCommandLabel	(CommandName.pulse_hold_autoclr));
		pulsePanel.add(new JCommandCheckBox	(CommandName.pulse_hold_autoclr));
		SpringLayoutGrid.makeCompactGrid(pulsePanel,3,2);
		
		firePanel.add(pulsePanel);
		firePanel.add(new JCommandDial(CommandName.pulse_hold_auto));
		return firePanel;
	}
	
	private JPanel createPulseEvents () {
		JPanel firePanel = JComponentFactory.createJFirePanel("Pulse Fire Events");
		firePanel.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
		
		JPanel mapPanel = new JPanel();
		mapPanel.setLayout(new SpringLayout());
		mapPanel.add(new JFireQMapTable(CommandName.pulse_fire_map,"fire","zero"));
		mapPanel.add(new JFireQMapTable(CommandName.pulse_hold_map,"hold","zero"));
		mapPanel.add(new JFireQMapTable(CommandName.pulse_resume_map,"resume","zero"));
		mapPanel.add(new JFireQMapTable(CommandName.pulse_reset_map,"reset","zero"));
		SpringLayoutGrid.makeCompactGrid(mapPanel,2,2);
		firePanel.add(mapPanel);
		
		return firePanel;
	}
	
	@Override
	public Class<?> getTabClassName() {
		return this.getClass();
	}
}

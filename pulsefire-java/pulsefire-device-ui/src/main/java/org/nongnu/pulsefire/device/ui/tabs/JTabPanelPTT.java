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
import org.nongnu.pulsefire.device.ui.components.JFireQMapTable;
import org.nongnu.pulsefire.wire.CommandName;

/**
 * JTabPanelPTT
 * 
 * @author Willem Cazander
 */
public class JTabPanelPTT extends AbstractTabPanel {

	private static final long serialVersionUID = 2716662787208065889L;

	public JTabPanelPTT() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel wrap = new JPanel();
		wrap.setLayout(new SpringLayout());
		
		JPanel trig0Panel = JComponentFactory.createJFirePanel("Trigger0");
		trig0Panel.add(new JFireQMapTable(CommandName.ptt_0map,"value","time"));
		wrap.add(trig0Panel);
		
		JPanel trig1Panel = JComponentFactory.createJFirePanel("Trigger1");
		trig1Panel.add(new JFireQMapTable(CommandName.ptt_1map,"value","time"));
		wrap.add(trig1Panel);

		JPanel trig2Panel = JComponentFactory.createJFirePanel("Trigger2");
		trig2Panel.add(new JFireQMapTable(CommandName.ptt_2map,"value","time"));
		wrap.add(trig2Panel);
		
		JPanel trig3Panel = JComponentFactory.createJFirePanel("Trigger3");
		trig3Panel.add(new JFireQMapTable(CommandName.ptt_3map,"value","time"));
		wrap.add(trig3Panel);
		
		SpringLayoutGrid.makeCompactGrid(wrap,2,2);
		add(wrap);
	}
	
	@Override
	public Class<?> getTabClassName() {
		return this.getClass();
	}
}

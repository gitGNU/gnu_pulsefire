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

package org.nongnu.pulsefire.device.ui;

import java.awt.FlowLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.nongnu.pulsefire.device.ui.components.JFireBorder;

/**
 * JComponentFactory
 * 
 * @author Willem Cazander
 */
public class JComponentFactory {

	static public JLabel createJLabel(JComponent comp,String name) {
		JLabel label = new JLabel();
		label.setName(comp.getClass().getName()+".label."+name);
		return label;
	}
	
	static public JLabel createJLabel(String name) {
		JLabel label = new JLabel(name);
		//label.setName(name);
		return label;
	}
	
	static public JPanel createJPanelJWrap(JComponent comp) {
		JPanel panel = new JPanel();
		panel.add(comp);
		return panel;
	}
	
	static public JPanel createJFirePanel() {
		return createJFirePanel(null);
	}
	static public JPanel createJFirePanel(Object nameObject,String name) {
		String i18nName = PulseFireUI.getInstance().getContext().getResourceMap().getString(nameObject.getClass().getName()+".firepanel."+name+".text");
		return createJFirePanel(i18nName);
		
	}
	
	static public JPanel createJFirePanel(String name) {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		JFireBorder fireBorder = new JFireBorder(name,panel);
		panel.setBorder(fireBorder);
		return panel;
	}
}

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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.nongnu.pulsefire.device.ui.components.JFireBorder;
import org.nongnu.pulsefire.device.ui.components.JFireDial;

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
	
	static public JCheckBox createSettingsJCheckBox(final PulseFireUISettingKeys key) {
		JCheckBox c = new JCheckBox();
		c.setSelected(PulseFireUI.getInstance().getSettingsManager().getSettingBoolean(key));
		c.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PulseFireUI.getInstance().getSettingsManager().setSettingString(key, ""+((JCheckBox)e.getSource()).isSelected());
				PulseFireUI.getInstance().getSettingsManager().saveSettings();
			}
		});
		return c;
	}
	
	static public JComboBox createSettingsJComboBox(final PulseFireUISettingKeys key,Object[] items) {
		JComboBox comboBox = new JComboBox(items);
		comboBox.setSelectedItem(PulseFireUI.getInstance().getSettingsManager().getSettingString(key));
		comboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PulseFireUI.getInstance().getSettingsManager().setSettingString(key, ""+((JComboBox)e.getSource()).getSelectedItem());
			}
		});
		return comboBox;
	}
	
	static public JTextField createSettingsJTextField(final PulseFireUISettingKeys key) {
		final JTextField textField = new JTextField(25);
		textField.setText(PulseFireUI.getInstance().getSettingsManager().getSettingString(key));
		textField.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent arg0) {
				PulseFireUI.getInstance().getSettingsManager().setSettingString(key, textField.getText());
			}
			@Override
			public void focusGained(FocusEvent arg0) {
			}
		});
		textField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				PulseFireUI.getInstance().getSettingsManager().setSettingString(key, textField.getText());
			}
		});
		return textField;
	}
	
	static public JFireDial createSettingsJFireDial(final PulseFireUISettingKeys key,int minValue, int maxValue) {
		final JFireDial fireDial = new JFireDial(minValue,maxValue,PulseFireUI.getInstance().getSettingsManager().getSettingInteger(key));
		fireDial.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent arg0) {
				PulseFireUI.getInstance().getSettingsManager().setSettingString(key, ""+fireDial.getValue());
			}
			@Override
			public void focusGained(FocusEvent arg0) {
			}
		});
		return fireDial;
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
		panel.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
		JFireBorder fireBorder = new JFireBorder(name,panel);
		panel.setBorder(fireBorder);
		
		return panel;
	}
	
	static public void showWarningDialog(JComponent parent,String title,String message) {
		JOptionPane.showMessageDialog(parent,message,title,JOptionPane.WARNING_MESSAGE);
	}
}

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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

import org.nongnu.pulsefire.device.DeviceCommandListener;
import org.nongnu.pulsefire.device.DeviceConnectListener;
import org.nongnu.pulsefire.device.ui.JComponentFactory;
import org.nongnu.pulsefire.device.ui.PulseFireUI;
import org.nongnu.pulsefire.device.ui.SpringLayoutGrid;
import org.nongnu.pulsefire.device.ui.components.JMalEditor;
import org.nongnu.pulsefire.wire.Command;
import org.nongnu.pulsefire.wire.CommandName;

/**
 * JTabPanelMAL
 * 
 * @author Willem Cazander
 */
public class JTabPanelMAL extends AbstractTabPanel implements DeviceConnectListener, ActionListener, DeviceCommandListener {

	private static final long serialVersionUID = 4091488961980523054L;
	private JComboBox programBox = null;
	private JButton loadButton = null;
	private JButton saveButton = null;
	private JButton clearButton = null;
	private JMalEditor malEditor = null;
	
	public JTabPanelMAL() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel wrap = new JPanel();
		wrap.setLayout(new SpringLayout());
		wrap.add(createHeader());
		wrap.add(createEditor());
		SpringLayoutGrid.makeCompactGrid(wrap,2,1);
		add(wrap);
		deviceDisconnect();
		PulseFireUI.getInstance().getDeviceManager().addDeviceConnectListener(this);
		PulseFireUI.getInstance().getDeviceManager().addDeviceCommandListener(CommandName.mal_program, this);
	}
		
	private JPanel createHeader() {	
		JPanel result = JComponentFactory.createJFirePanel("Program");
		result.setLayout(new FlowLayout(FlowLayout.LEFT));
		result.add(new JLabel("Select"));
		programBox = new JComboBox();
		result.add(programBox);
		
		result.add(new JLabel("Actions"));
		loadButton = new JButton("Load");
		loadButton.setEnabled(false);
		loadButton.addActionListener(this);
		result.add(loadButton);
		saveButton = new JButton("Save");
		saveButton.setEnabled(false);
		saveButton.addActionListener(this);
		result.add(saveButton);
		clearButton = new JButton("Clear");
		clearButton.setEnabled(false);
		clearButton.addActionListener(this);
		result.add(clearButton);		
		return result;
	}
	
	private JPanel createEditor() {	
		JPanel result = JComponentFactory.createJFirePanel("Editor");
		malEditor = new JMalEditor();
		result.add(malEditor);
		return result;
	}
	
	@Override
	public Class<?> getTabClassName() {
		return this.getClass();
	}

	@Override
	public void deviceConnect() {
		if (CommandName.mal_fire.isDisabled()) {
			return;
		}
		loadButton.setEnabled(true);
		saveButton.setEnabled(true);
		clearButton.setEnabled(true);
		programBox.setEnabled(true);
		malEditor.setEnabled(true);
		
		programBox.removeAllItems();
		for (int i=0;i<CommandName.mal_fire.getMaxIndexA();i++) {
			programBox.addItem("mal"+i);
		}
	}

	@Override
	public void deviceDisconnect() {
		programBox.setEnabled(false);
		malEditor.setEnabled(false);
		loadButton.setEnabled(false);
		saveButton.setEnabled(false);
		clearButton.setEnabled(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (loadButton.equals(e.getSource())) {
			if (programBox.getSelectedIndex()>=0) {
				Command cmd = new Command(CommandName.mal_program);
				//cmd.setArgu0(""+programBox.getSelectedIndex());
				PulseFireUI.getInstance().getDeviceManager().requestCommand(cmd);
			}
		} else if (saveButton.equals(e.getSource())) {
			List<Byte> programData = malEditor.saveData();
			StringBuffer buf = new StringBuffer();
			for (Byte b:programData) {
				byte high = (byte) ( (b & 0xf0) >> 4);
				byte low =  (byte)   (b & 0x0f);
				buf.append(nibble2char(high));
				buf.append(nibble2char(low));
			}
			System.out.println("mm save: "+buf.toString());
		} else if (clearButton.equals(e.getSource()) && malEditor.getMaxOpcodes()>0) {
			List<Byte> programData = new ArrayList<Byte>(512);
			for (int i=0;i<malEditor.getMaxOpcodes();i++) {
				programData.add(new Integer(255).byteValue());
			}
			malEditor.loadData(programData);
		}
		
	}
	
	private static char nibble2char(byte b) {
		byte nibble = (byte) (b & 0x0f);
		if (nibble < 10) {
			return (char) ('0' + nibble);
		}
		return (char) ('A' + nibble - 10);
	}

	@Override
	public void commandReceived(Command command) {
		
		List<Byte> programData = new ArrayList<Byte>(512);
		String data = command.getArgu0();
		for (int i=0;i<data.length();i=i+2) {
			char hex0 = data.charAt(i);
			char hex1 = data.charAt(i+1);
			programData.add(((Integer)Integer.parseInt(hex0+""+hex1, 16)).byteValue());
		}
		malEditor.setMaxOpcodes(programData.size());
		malEditor.loadData(programData);
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SwingUtilities.updateComponentTreeUI(getParentScrollPane());
				SwingUtilities.updateComponentTreeUI(malEditor);
			}
		});
	}
}

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
import org.nongnu.pulsefire.device.ui.JComponentFactory;
import org.nongnu.pulsefire.device.ui.PulseFireUI;
import org.nongnu.pulsefire.device.ui.SpringLayoutGrid;
import org.nongnu.pulsefire.device.ui.components.JCommandDial;
import org.nongnu.pulsefire.device.ui.components.JMalEditor;
import org.nongnu.pulsefire.wire.Command;
import org.nongnu.pulsefire.wire.CommandName;

/**
 * JTabPanelMAL
 * 
 * @author Willem Cazander
 */
public class JTabPanelMal extends AbstractFireTabPanel implements ActionListener, DeviceCommandListener {

	private static final long serialVersionUID = 4091488961980523054L;
	private JButton loadButton = null;
	private JButton saveButton = null;
	private JButton clearButton = null;
	private JButton fireButton = null;
	private JComboBox<Integer> fireIndexBox = null;
	private JMalEditor malEditor = null;
	
	public JTabPanelMal() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel wrap = new JPanel();
		wrap.setLayout(new SpringLayout());
		wrap.add(createHeader());
		wrap.add(createEditor());
		SpringLayoutGrid.makeCompactGrid(wrap,2,1);
		add(wrap);
		deviceDisconnect();
		PulseFireUI.getInstance().getDeviceManager().addDeviceCommandListener(CommandName.mal_code, this);
	}
		
	private JPanel createHeader() {	
		JPanel result = JComponentFactory.createJFirePanel("Program");
		result.setLayout(new FlowLayout(FlowLayout.LEFT));
		
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
		
		fireButton = new JButton("Fire");
		fireButton.setEnabled(false);
		fireButton.addActionListener(this);
		result.add(fireButton);
		
		fireIndexBox = new JComboBox<Integer>();
		fireIndexBox.setEnabled(false);
		result.add(fireIndexBox);
		
		result.add(new JCommandDial(CommandName.mal_ops_fire));
		result.add(new JCommandDial(CommandName.mal_ops));
		result.add(new JCommandDial(CommandName.mal_wait));
		
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
		super.deviceConnect();
		if (CommandName.mal_fire.isDisabled()) {
			return;
		}
		Command flags = PulseFireUI.getInstance().getDeviceData().getDeviceParameter(CommandName.chip_flags);
		if (flags==null || flags.getArgu0().contains("MAL")==false ) {
			return;
		}
		fireIndexBox.removeAllItems();
		for (int i=0;i<CommandName.mal_fire.getMaxIndexA();i++) {
			fireIndexBox.addItem(i);
		}
		loadButton.setEnabled(true);
	}

	@Override
	public void deviceDisconnect() {
		super.deviceDisconnect();
		loadButton.setEnabled(false);
		saveButton.setEnabled(false);
		clearButton.setEnabled(false);
		fireIndexBox.setEnabled(false);
		fireButton.setEnabled(false);
		malEditor.setEnabled(false);
		malEditor.clearData();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (loadButton.equals(e.getSource())) {
			PulseFireUI.getInstance().getDeviceManager().requestCommand(new Command(CommandName.mal_code));
		} else if (saveButton.equals(e.getSource())) {
			List<Byte> programData = malEditor.saveData();
			StringBuilder buf = new StringBuilder();
			for (Byte b:programData) {
				byte high = (byte) ( (b & 0xf0) >> 4);
				byte low =  (byte)   (b & 0x0f);
				buf.append(CommandName.nibble2hex(high));
				buf.append(CommandName.nibble2hex(low));
			}
			//System.out.println("mm save: "+buf.toString());
			// todo make dialog progress bar
			final String data = buf.toString();
			PulseFireUI.getInstance().getEventTimeManager().addRunOnce(new Runnable() {
				@Override
				public void run() {
					for (int i=0;i<data.length();i=i+8) {
						Command cmd = new Command(CommandName.mal_code);
						cmd.setArgu0(""+(i/2));
						cmd.setArgu1(""+data.charAt(i)+data.charAt(i+1)+data.charAt(i+2)+data.charAt(i+3)+data.charAt(i+4)+data.charAt(i+5)+data.charAt(i+6)+data.charAt(i+7)); // mm
						PulseFireUI.getInstance().getDeviceManager().requestCommand(cmd).waitForResponse();
					}
				}
			});
		} else if (clearButton.equals(e.getSource()) && malEditor.getMaxProgramSize()>0) {
			List<Byte> programData = new ArrayList<Byte>(512);
			for (int i=0;i<CommandName.mal_fire.getMaxIndexA();i++) {
				programData.add((byte)0x40);
				programData.add((byte)0x20);
				programData.add((byte)0x00);
				programData.add((new Integer(4*CommandName.mal_fire.getMaxIndexA()).byteValue()));
			}
			for (int i=0;i<malEditor.getMaxProgramSize()-(CommandName.mal_fire.getMaxIndexA()*4);i++) {
				programData.add(new Integer(255).byteValue());
			}
			malEditor.loadData(programData);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					SwingUtilities.updateComponentTreeUI(getParentScrollPane());
					SwingUtilities.updateComponentTreeUI(malEditor);
				}
			});
		} else if (fireButton.equals(e.getSource()) && fireIndexBox.getSelectedIndex()>=0) {
			Command cmd = new Command(CommandName.req_trigger);
			cmd.setArgu0(""+CommandName.mal_fire.getMapIndex());
			cmd.setArgu1(fireIndexBox.getSelectedItem().toString());
			PulseFireUI.getInstance().getDeviceManager().requestCommand(cmd);
		}
		
	}
	
	@Override
	public void commandReceived(Command command) {
		
		if (command.getArgu1()!=null) {
			return; // skip cmd with argu for programing mal chip code.
		}
		
		List<Byte> programData = new ArrayList<Byte>(512);
		String data = command.getArgu0();
		for (int i=0;i<data.length();i=i+2) {
			char hex0 = data.charAt(i);
			char hex1 = data.charAt(i+1);
			programData.add(((Integer)Integer.parseInt(hex0+""+hex1, 16)).byteValue());
		}
		malEditor.loadData(programData);
		
		saveButton.setEnabled(true);
		clearButton.setEnabled(true);
		fireIndexBox.setEnabled(true);
		fireButton.setEnabled(true);
		malEditor.setEnabled(true);
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SwingUtilities.updateComponentTreeUI(getParentScrollPane());
				SwingUtilities.updateComponentTreeUI(malEditor);
			}
		});
	}
}

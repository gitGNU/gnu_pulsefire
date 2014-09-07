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

package org.nongnu.pulsefire.device.ui.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import org.nongnu.pulsefire.device.DeviceCommandListener;
import org.nongnu.pulsefire.device.DeviceConnectListener;
import org.nongnu.pulsefire.device.DeviceWireManager;
import org.nongnu.pulsefire.device.ui.JComponentEnableStateListener;
import org.nongnu.pulsefire.device.ui.PulseFireUI;
import org.nongnu.pulsefire.wire.Command;
import org.nongnu.pulsefire.wire.CommandName;

/**
 * JCommandComboBox
 * 
 * @author Willem Cazander
 */
public class JCommandComboBox extends JComboBox<String> implements ActionListener,DeviceCommandListener,DeviceConnectListener  {

	private static final long serialVersionUID = -8483163326183468077L;
	private DeviceWireManager deviceManager = null;
	private Command command = null;
	private int idx = -1;
	volatile private boolean noEvent = false;
	
	public JCommandComboBox(CommandName commandName) {
		this(commandName,commandName.getListValues(),-1);
	}
	
	public JCommandComboBox(CommandName commandName,int idx) {
		this(commandName,commandName.getListValues(),idx);
	}
	
	public JCommandComboBox(CommandName commandName,String[] values,int idx) {
		super(values);
		this.idx=idx;
		deviceManager = PulseFireUI.getInstance().getDeviceManager();
		command = new Command(commandName);
		this.addActionListener(this);
		JComponentEnableStateListener.attach(this,commandName);
		deviceManager.addDeviceCommandListener(command.getCommandName(), this);
		setName("commandname."+commandName.name()+".combobox");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String enumName = (String)getSelectedItem();
		for (int i=0;i<command.getCommandName().getListValues().length;i++) {
			String sel = command.getCommandName().getListValues()[i];
			if (sel.equals(enumName)) {
				if (command.getCommandName().isMagicTopListValue() && getSelectedIndex()==command.getCommandName().getListValues().length-1) {
					command.setArgu0(new Integer(255).toString());
				} else {
					command.setArgu0(new Integer(i).toString());
				}
				if (idx != -1) {
					command.setArgu1(""+idx);
				}
				if (noEvent==false) {
					deviceManager.requestCommand(command);
				}
				return;
			}
		}
	}
	@Override
	public void commandReceived(Command command) {
		if (getItemCount()==1) {
			// init
			try {
				noEvent = true;
				removeAllItems();
				for (String i:command.getCommandName().getListValues()) {
					addItem(i);
				}
			} finally {
				noEvent = false;
			}
		}
		if (idx != -1) {
			if (command.getArgu1()==null) {
				return;
			}
			if (new Integer(command.getArgu1()).equals(idx)==false) {
				return; // not for me.
			}
		}
		Integer idx = Integer.parseInt(command.getArgu0());
		if (idx==255) {
			idx = getItemCount()-1;
		}
		if (idx.equals(getSelectedIndex())==false) {
			if (idx>=getItemCount()) {
				throw new IllegalStateException("Idx: "+idx+" is larger then: "+getItemCount()+" of "+command.getCommandName().name());
			}
			try {
				noEvent = true;
				setSelectedIndex(idx);
			} finally {
				noEvent = false;
			}
		}
		
	}

	@Override
	public void deviceConnect() {
		repaint();
	}

	@Override
	public void deviceDisconnect() {
		try {
			noEvent = true;
			removeAllItems();
			addItem("    ");
		} finally {
			noEvent = false;
		}
	}
}

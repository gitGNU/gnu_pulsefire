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

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.nongnu.pulsefire.device.io.protocol.Command;
import org.nongnu.pulsefire.device.io.protocol.CommandName;
import org.nongnu.pulsefire.device.io.transport.DeviceCommandListener;
import org.nongnu.pulsefire.device.ui.PulseFireUI;

/**
 * JCommandStatusBox
 * 
 * @author Willem Cazander
 */
public class JCommandStatusBox extends JLabel {

	private static final long serialVersionUID = -945889336846942L;
	private final Icon iconIdle;
	private final Icon iconRunning;
	private boolean running = false;
	
	public JCommandStatusBox(CommandName runCommand,CommandName displayCommand,CommandName stepCommand) {
		this(runCommand,displayCommand,stepCommand,null);
	}
	
	public JCommandStatusBox(CommandName runCommand,final CommandName displayCommand,CommandName stepCommand,final Integer index) {
		super();
		if (runCommand==null) {
			throw new NullPointerException("Can't make JCommandStatusBox with null run command.");
		}
		if (displayCommand==null) {
			throw new NullPointerException("Can't make JCommandStatusBox with null display command.");
		}
		if (stepCommand==null) {
			throw new NullPointerException("Can't make JCommandStatusBox with null step command.");
		}
		this.iconIdle = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/org/nongnu/pulsefire/device/ui/resources/images/status-idle.png")));
		this.iconRunning = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/org/nongnu/pulsefire/device/ui/resources/images/status-running.png")));
		this.setPreferredSize(new Dimension(120,  20));
		
		PulseFireUI.getInstance().getDeviceManager().addDeviceCommandListener(runCommand, new DeviceCommandListener() {
			@Override
			public void commandReceived(Command command) {
				if (command.getArgu0()==null) {
					return;
				}
				int v = Integer.parseInt(command.getArgu0());
				if (index!=null) {
					if (command.getArgu1()==null) {
						return;
					}
					int idx = Integer.parseInt(command.getArgu1());
					if (!index.equals(idx)) {
						return;
					}
				}
				running = v > 0;
			}
		});
		
		PulseFireUI.getInstance().getDeviceManager().addDeviceCommandListener(stepCommand, new DeviceCommandListener() {
			@Override
			public void commandReceived(Command command) {
				Command display = null;
				if (!displayCommand.equals(command.getCommandName())) {
					if (index==null) {
						display = PulseFireUI.getInstance().getDeviceManager().getDeviceData().getDeviceParameter(displayCommand);
					} else {
						display = PulseFireUI.getInstance().getDeviceManager().getDeviceData().getDeviceParameterIndexed(displayCommand,index);
					}
				}
				String displayPrefix = "Step ";
				if (display != null) {
					displayPrefix += display.getArgu0() + "/";
				}
				if (command.getArgu0()==null) {
					return;
				}
				if (index==null) {
					int v = Integer.parseInt(command.getArgu0());
					setStatus(running,displayPrefix+v);
				} else {
					if (command.getArgu1()==null) {
						return;
					}
					int vIndex = Integer.parseInt(command.getArgu1());
					if (!index.equals(vIndex)) {
						return;
					}
					int v = Integer.parseInt(command.getArgu0());
					setStatus(running,displayPrefix+v);
				}
			}
		});
		
		setStatus(false,"Idle");
	}
	
	public void setStatus(boolean running,String statusText) {
		if (!running) {
			setIcon(iconIdle);
		} else {
			setIcon(iconRunning);
		}
		setText(statusText);
	}
}

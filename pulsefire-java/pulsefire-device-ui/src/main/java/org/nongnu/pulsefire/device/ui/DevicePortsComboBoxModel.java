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

import javax.swing.DefaultComboBoxModel;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * DevicePortsComboBoxModel
 * 
 * @author Willem Cazander
 */
public class DevicePortsComboBoxModel extends DefaultComboBoxModel implements PopupMenuListener {

	private static final long serialVersionUID = 4610540916741783097L;

	public DevicePortsComboBoxModel() {
		super(PulseFireUI.getInstance().getDeviceManager().getDevicePorts().toArray());
	}

	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		removeAllElements();
		for (String port:PulseFireUI.getInstance().getDeviceManager().getDevicePorts()) {
			addElement(port);
		}
	}

	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
	}

	@Override
	public void popupMenuCanceled(PopupMenuEvent e) {
	}
}

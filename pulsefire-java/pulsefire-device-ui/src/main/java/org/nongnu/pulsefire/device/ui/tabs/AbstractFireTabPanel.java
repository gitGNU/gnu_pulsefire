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

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.nongnu.pulsefire.device.DeviceConnectListener;
import org.nongnu.pulsefire.device.ui.PulseFireUI;

/**
 * AbstractTabPanel
 * 
 * @author Willem Cazander
 */
@SuppressWarnings("serial")
abstract public class AbstractFireTabPanel extends JPanel implements JFireTabPanel,DeviceConnectListener {

	private JScrollPane parentScrollPane = null;
	
	public AbstractFireTabPanel() {
		PulseFireUI.getInstance().getDeviceManager().addDeviceConnectListener(this);
	}
	
	abstract Class<?> getTabClassName();

	public String getTabName() {
		String nameKey = getTabClassName().getName()+".text";
		return PulseFireUI.getInstance().getContext().getResourceMap().getString(nameKey);
	}
	public String getTabTooltip() {
		String nameKey = getTabClassName().getName()+".tooltip";
		return PulseFireUI.getInstance().getContext().getResourceMap().getString(nameKey);
	}
	public Icon getTabIcon() {
		String nameKey = getTabClassName().getName()+".icon";
		return PulseFireUI.getInstance().getContext().getResourceMap().getIcon(nameKey);
	}
	
	public JPanel getJPanel() {
		return this;
	}
	
	public JPanel getJPanelSide() {
		return null;
	}
	
	public JScrollPane getParentScrollPane() {
		return parentScrollPane;
	}

	public void setParentScrollPane(JScrollPane parentScrollPane) {
		this.parentScrollPane = parentScrollPane;
	}

	@Override
	public void deviceConnect() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SwingUtilities.updateComponentTreeUI(parentScrollPane);
			}
		});
	}

	@Override
	public void deviceDisconnect() {
	}
	
	@Override
	public void release() {
	}
}

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
abstract public class AbstractFireTabPanel implements JFireTabPanel {

	private final JPanel tabPane;
	private final JPanel tabSidePane;
	private final JScrollPane tabScrollPane;
	
	public AbstractFireTabPanel() {
		tabPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		tabScrollPane = new JScrollPane(tabPane);
		PulseFireUI.getInstance().getDeviceManager().addDeviceConnectListener(new DeviceConnectListener() {
			@Override
			public void deviceDisconnect() {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void deviceConnect() {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								SwingUtilities.updateComponentTreeUI(tabScrollPane);
								SwingUtilities.updateComponentTreeUI(tabPane);
							}
						});
					}
				});
			}
		});
		tabSidePane = createTabSidePane();
	}
	
	protected JPanel createTabSidePane() {
		return null;
	}
	
	public final Class<?> getTabClassName() {
		return this.getClass();
	}
	
	public final String getTabName() {
		String nameKey = getTabClassName().getName()+".text";
		return PulseFireUI.getInstance().getContext().getResourceMap().getString(nameKey);
	}
	
	public final String getTabTooltip() {
		String nameKey = getTabClassName().getName()+".tooltip";
		return PulseFireUI.getInstance().getContext().getResourceMap().getString(nameKey);
	}
	
	public final Icon getTabIcon() {
		String nameKey = getTabClassName().getName()+".icon";
		return PulseFireUI.getInstance().getContext().getResourceMap().getIcon(nameKey);
	}
	
	public final JPanel getJPanel() {
		return tabPane;
	}
	
	public final JScrollPane getJScrollPane() {
		return tabScrollPane;
	}
	
	public final JPanel getJPanelSide() {
		return tabSidePane;
	}
	
	@Override
	public void release() {
	}
}

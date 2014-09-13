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
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

import org.nongnu.pulsefire.device.io.protocol.CommandName;
import org.nongnu.pulsefire.device.io.transport.DeviceConnectListener;
import org.nongnu.pulsefire.device.ui.JComponentFactory;
import org.nongnu.pulsefire.device.ui.PulseFireUI;
import org.nongnu.pulsefire.device.ui.SpringLayoutGrid;
import org.nongnu.pulsefire.device.ui.components.JCommandButton;
import org.nongnu.pulsefire.device.ui.components.JCommandCheckBox;
import org.nongnu.pulsefire.device.ui.components.JCommandComboBox;
import org.nongnu.pulsefire.device.ui.components.JCommandDial;
import org.nongnu.pulsefire.device.ui.components.JCommandLabel;
import org.nongnu.pulsefire.device.ui.components.JCommandSpinner;
import org.nongnu.pulsefire.device.ui.components.JCommandStatusBox;
import org.nongnu.pulsefire.device.ui.components.JFireQMapTable;

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
		return i18n(nameKey);
	}
	
	public final String getTabTooltip() {
		String nameKey = getTabClassName().getName()+".tooltip";
		return i18n(nameKey);
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
	
	private final String i18n(String key) {
		return PulseFireUI.getInstance().getContext().getResourceMap().getString(key);
	}
	
	private JComponent createCommandComponentLabelGrid(CommandName cmdName,JComponent component) {
		return createCommandComponentLabelGrid(cmdName,null,component);
	}
	
	private JComponent createCommandComponentLabelGrid(CommandName cmdName,Integer index,final JComponent component) {
		final JCommandLabel result = new JCommandLabel(cmdName,index);
		result.addHierarchyListener(new HierarchyListener() {
			@Override
			public void hierarchyChanged(HierarchyEvent e) {
				if (HierarchyEvent.PARENT_CHANGED!=e.getChangeFlags()) {
					return;
				}
				if (result.getParent()!=null) {
					result.getParent().add(component);
					result.removeHierarchyListener(this);
				}
			}
		});
		return result;
	}
	
	protected JComponent createCommandSpinnerLabelGrid(CommandName cmdName) {
		return createCommandComponentLabelGrid(cmdName,new JCommandSpinner(cmdName));
	}
	
	protected JComponent createCommandComboBoxLabelGrid(CommandName cmdName) {
		return createCommandComponentLabelGrid(cmdName,new JCommandComboBox(cmdName));
	}
	
	protected JComponent createCommandCheckBoxLabelGrid(CommandName cmdName) {
		return createCommandComponentLabelGrid(cmdName,new JCommandCheckBox(cmdName));
	}
	
	protected JComponent createCommandStatusBoxLabelGrid(CommandName runCommand,CommandName displayCommand,CommandName stepCommand) {
		return createCommandComponentLabelGrid(displayCommand,createCommandStatusBox(runCommand,displayCommand,stepCommand));
	}
	
	protected JComponent createCommandStatusBoxLabelGrid(CommandName runCommand,CommandName displayCommand,CommandName stepCommand,int idx) {
		return createCommandComponentLabelGrid(displayCommand,idx,createCommandStatusBox(runCommand,displayCommand,stepCommand,idx));
	}
	
	protected JComponent createCommandStatusBox(CommandName runCommand,CommandName displayCommand,CommandName stepCommand) {
		return new JCommandStatusBox(runCommand,displayCommand,stepCommand);
	}
	
	protected JComponent createCommandStatusBox(CommandName runCommand,CommandName displayCommand,CommandName stepCommand,int idx) {
		return new JCommandStatusBox(runCommand,displayCommand,stepCommand,idx);
	}
	
	protected JComponent createCommandButtonTrigger(CommandName commandName,Integer index,CommandName commandTrigger) {
		return new JCommandButton(commandName,index,commandTrigger);
	}
	
	protected JComponent createCommandDial(CommandName cmdName) {
		return new JCommandDial(cmdName);
	}
	
	protected JComponent createCommandQMapTable(CommandName cmdName) {
		JPanel result = JComponentFactory.createJFirePanel(i18n(cmdName.getKeyLabel()));
		result.add(new JFireQMapTable(cmdName,i18n(cmdName.getKeyQMapValueA()),i18n(cmdName.getKeyQMapValueB())));
		return result;
	}
	
	protected JComponent createFlowLeftFirePanel(String fireName,JComponent...components) {
		JPanel result = JComponentFactory.createJFirePanel(this,fireName);
		result.setLayout(new FlowLayout(FlowLayout.LEFT));
		for (JComponent comp:components) {
			result.add(comp);
		}
		return result;
	}
	
	protected JComponent createLabeledGrid(int rows,int cols,JComponent...components) {
		return createCompactGrid(rows,cols * 2,components);
	}
	
	protected JComponent createCompactGrid(final int rows,final int cols,JComponent...components) {
		return createCompactGrid(rows,cols,6,6,6,6,components);
	}
	
	protected JComponent createCompactGrid(final int rows,final int cols,final int initialX,final int initialY,final int xPad,final int yPad,JComponent...components) {
		final JPanel result = new JPanel();
		result.setLayout(new SpringLayout());
		for (JComponent comp:components) {
			result.add(comp);
		}
		result.addHierarchyListener(new HierarchyListener() {
			@Override
			public void hierarchyChanged(HierarchyEvent e) {
				if (HierarchyEvent.PARENT_CHANGED!=e.getChangeFlags()) {
					return;
				}
				if (result.getParent()!=null) {
					SpringLayoutGrid.makeCompactGrid(result,rows,cols,initialX,initialY,xPad,yPad);
					result.removeHierarchyListener(this);
				}
			}
		});
		return result;
	}
	
	protected final void build(JComponent rootComponent) {
		getJPanel().add(rootComponent);
	}
}

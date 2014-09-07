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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;

import org.nongnu.pulsefire.device.ui.tabs.AbstractFireTabPanel;
import org.nongnu.pulsefire.device.ui.tabs.JFireTabPanel;
import org.nongnu.pulsefire.device.ui.tabs.JTabPanelCip;
import org.nongnu.pulsefire.device.ui.tabs.JTabPanelPFDataLog;
import org.nongnu.pulsefire.device.ui.tabs.JTabPanelPwm;
import org.nongnu.pulsefire.device.ui.tabs.JTabPanelGraphs;
import org.nongnu.pulsefire.device.ui.tabs.JTabPanelInput;
import org.nongnu.pulsefire.device.ui.tabs.JTabPanelPFLpm;
import org.nongnu.pulsefire.device.ui.tabs.JTabPanelMal;
import org.nongnu.pulsefire.device.ui.tabs.JTabPanelPtc;
import org.nongnu.pulsefire.device.ui.tabs.JTabPanelPtt;
import org.nongnu.pulsefire.device.ui.tabs.JTabPanelPins;
import org.nongnu.pulsefire.device.ui.tabs.JTabPanelPwmExt;
import org.nongnu.pulsefire.device.ui.tabs.JTabPanelStv;
import org.nongnu.pulsefire.device.ui.tabs.JTabPanelPFSettings;
import org.nongnu.pulsefire.device.ui.tabs.JTabPanelSystem;
import org.nongnu.pulsefire.device.ui.tabs.JTabPanelPFDebugLog;
import org.nongnu.pulsefire.device.ui.tabs.JTabPanelVfc;
import org.nongnu.pulsefire.device.ui.tabs.JTabPanelVariables;
import org.nongnu.pulsefire.device.ui.tabs.JTabPanelVsc;

/**
 * JMainPanel
 * 
 * @author Willem Cazander
 */
public class JMainPanel extends JPanel implements PulseFireUISettingListener {

	private static final long serialVersionUID = -9173866662540287337L;
	public List<JFireTabPanel> tabPanels = null;
	public JTabbedPane tabbedPane = null;
	public JSplitPane contentSplitPane = null;
	public JSplitPane bottomSplitPane = null;
	public JSplitPane bottomLogSplitPane = null;
	public AbstractFireTabPanel uiLogPanel = null;
	public AbstractFireTabPanel scopePanel = null;
	public AbstractFireTabPanel lpmPanel = null;
	public JTopPanelSerial topPanelSerial = null;
	
	public JMainPanel() {
		
		tabPanels = new ArrayList<JFireTabPanel>(10);
		tabPanels.add(new JTabPanelPwm());
		tabPanels.add(new JTabPanelPwmExt());
		tabPanels.add(new JTabPanelSystem());
		tabPanels.add(new JTabPanelPins());
		tabPanels.add(new JTabPanelInput());
		tabPanels.add(new JTabPanelVsc());
		tabPanels.add(new JTabPanelCip());
		tabPanels.add(new JTabPanelStv());
		tabPanels.add(new JTabPanelPtc());
		tabPanels.add(new JTabPanelPtt());
		tabPanels.add(new JTabPanelVfc());
		tabPanels.add(new JTabPanelMal());
		tabPanels.add(new JTabPanelGraphs());
		tabPanels.add(new JTabPanelVariables());
		tabPanels.add(new JTabPanelPFSettings());
		tabPanels.add(new JTabPanelPFDataLog());
		
		if (PulseFireUI.getInstance().getSettingsManager().getSettingBoolean(PulseFireUISettingKeys.TAB_UILOG_ENABLE)) {
			uiLogPanel = new JTabPanelPFDebugLog(); 
			tabPanels.add(uiLogPanel);
		}
		if (PulseFireUI.getInstance().getSettingsManager().getSettingBoolean(PulseFireUISettingKeys.TAB_LPM_ENABLE)) {
			lpmPanel = new JTabPanelPFLpm(); 
			tabPanels.add(lpmPanel);
		}
		
		JPanel main = this; //new JPanel(); //new ContentPanel(this);
		main.setLayout(new BorderLayout());
		main.add(createTop(), BorderLayout.PAGE_START);
		main.add(createContentSplit(), BorderLayout.CENTER);
		
		PulseFireUI.getInstance().getSettingsManager().addSettingListener(PulseFireUISettingKeys.TAB_UILOG_ENABLE, this);
		PulseFireUI.getInstance().getSettingsManager().addSettingListener(PulseFireUISettingKeys.TAB_LPM_ENABLE, this);
	}
	
	private JPanel createTop() {
		JPanel top = new JPanel();
		top.setLayout(new GridLayout(1,2));
		topPanelSerial = new JTopPanelSerial();
		top.add(topPanelSerial);
		top.add(new JTopPanelStatus());
		return top;
	}
	
	private JSplitPane createContentSplit() {
		JPanel sp0 = createCenterContent();
		JSplitPane sp1 = createBottomSplit();
		bottomSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,sp0,sp1);
		bottomSplitPane.setOneTouchExpandable(true);
		bottomSplitPane.setResizeWeight(0.7);
		bottomSplitPane.setDividerLocation(PulseFireUI.getInstance().getSettingsManager().getSettingInteger(PulseFireUISettingKeys.UI_SPLIT_BOTTOM));
		sp0.setMinimumSize(new Dimension(100, 225));
		sp1.setMinimumSize(new Dimension(200, 125));
		return bottomSplitPane;
	}
	
	private JSplitPane createBottomSplit() {
		JPanel sp0 = createBottomConsole();
		JPanel sp1 = createBottomInfo();
		bottomLogSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,sp0,sp1);
		bottomLogSplitPane.setOneTouchExpandable(true);
		bottomLogSplitPane.setResizeWeight(0.4);
		bottomLogSplitPane.setDividerLocation(PulseFireUI.getInstance().getSettingsManager().getSettingInteger(PulseFireUISettingKeys.UI_SPLIT_BOTTOM_LOG));
		sp0.setMinimumSize(new Dimension(400, 150));
		sp1.setMinimumSize(new Dimension(300, 150));
		return bottomLogSplitPane;
	}
	
	private JPanel createCenterContent() {
		JPanel center = new JPanel();
		center.setLayout(new GridLayout(1,0));
		tabbedPane = new JTabbedPane();
		tabbedPane.setFont(UIManager.getFont("TabbedPane.font")); // workaround
		tabbedPane.setBorder(BorderFactory.createEmptyBorder());
		for (JFireTabPanel panel:tabPanels) {
			Component pane = createJScrollPane(panel);
			tabbedPane.addTab(panel.getTabName(),panel.getTabIcon(),pane,panel.getTabTooltip());
		}
		center.add(tabbedPane);
		return center;
	}
	
	private JPanel createBottomConsole() {
		return new JPanelConsole();
	}
	
	private JPanel createBottomInfo() {
		return new JPanelConsoleInfo();
	}
	
	@Override
	public void settingUpdated(PulseFireUISettingKeys key, String value) {
		if (PulseFireUISettingKeys.TAB_UILOG_ENABLE==key) {
			if (PulseFireUI.getInstance().getSettingsManager().getSettingBoolean(key)) {
				if (uiLogPanel==null) {
					uiLogPanel = new JTabPanelPFDebugLog();
					Component pane = createJScrollPane(uiLogPanel);
					tabbedPane.addTab(uiLogPanel.getTabName(),uiLogPanel.getTabIcon(),pane,uiLogPanel.getTabTooltip());
				}
			} else {
				if (uiLogPanel!=null) {
					removeTabPanel(uiLogPanel);
					uiLogPanel.release(); // remove listeners.
					uiLogPanel = null;
				}
			}
		} else if (PulseFireUISettingKeys.TAB_LPM_ENABLE==key) {
			if (PulseFireUI.getInstance().getSettingsManager().getSettingBoolean(key)) {
				if (lpmPanel==null) {
					lpmPanel = new JTabPanelPFLpm();
					Component pane = createJScrollPane(lpmPanel);
					tabbedPane.addTab(lpmPanel.getTabName(),lpmPanel.getTabIcon(),pane,lpmPanel.getTabTooltip());
				}
			} else {
				if (lpmPanel!=null) {
					removeTabPanel(lpmPanel);
					lpmPanel.release(); // remove listeners.
					lpmPanel = null;
				}
			}
		}
	}
	
	private void removeTabPanel(JFireTabPanel panel) {
		for (int i=0;i<tabbedPane.getTabCount();i++) {
			Object tab = tabbedPane.getComponentAt(i);
			if (panel.getJScrollPane()==tab) {
				tabbedPane.removeTabAt(i);
				break;
			}
		}
	}
	
	private Component createJScrollPane(JFireTabPanel innerPanel) {
		JScrollPane scrollPane = innerPanel.getJScrollPane();
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
		if (innerPanel.getJPanelSide()!=null) {
			return createContentSplit(scrollPane,innerPanel.getJPanelSide());
		}
		return scrollPane;
	}
	
	private Component createContentSplit(Component sp0,Component sp1) {
		contentSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,sp0,sp1);
		contentSplitPane.setOneTouchExpandable(true);
		contentSplitPane.setResizeWeight(0.8);
		contentSplitPane.setDividerLocation(PulseFireUI.getInstance().getSettingsManager().getSettingInteger(PulseFireUISettingKeys.UI_SPLIT_CONTENT));
		sp0.setMinimumSize(new Dimension(150, 200));
		sp1.setMinimumSize(new Dimension(150, 200));
		return contentSplitPane;
	}
}

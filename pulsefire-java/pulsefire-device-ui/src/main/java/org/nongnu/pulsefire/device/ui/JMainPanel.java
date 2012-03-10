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

import org.nongnu.pulsefire.device.ui.tabs.AbstractTabPanel;
import org.nongnu.pulsefire.device.ui.tabs.JTabFirePanel;
import org.nongnu.pulsefire.device.ui.tabs.JTabPanelPWM;
import org.nongnu.pulsefire.device.ui.tabs.JTabPanelGraphs;
import org.nongnu.pulsefire.device.ui.tabs.JTabPanelInput;
import org.nongnu.pulsefire.device.ui.tabs.JTabPanelLPM;
import org.nongnu.pulsefire.device.ui.tabs.JTabPanelMAL;
import org.nongnu.pulsefire.device.ui.tabs.JTabPanelPTC;
import org.nongnu.pulsefire.device.ui.tabs.JTabPanelPTT;
import org.nongnu.pulsefire.device.ui.tabs.JTabPanelPins;
import org.nongnu.pulsefire.device.ui.tabs.JTabPanelSTV;
import org.nongnu.pulsefire.device.ui.tabs.JTabPanelScope;
import org.nongnu.pulsefire.device.ui.tabs.JTabPanelSettings;
import org.nongnu.pulsefire.device.ui.tabs.JTabPanelSystem;
import org.nongnu.pulsefire.device.ui.tabs.JTabPanelVFC;
import org.nongnu.pulsefire.device.ui.tabs.JTabPanelVariables;

/**
 * JMainPanel
 * 
 * @author Willem Cazander
 */
public class JMainPanel extends JPanel implements PulseFireUISettingListener {

	private static final long serialVersionUID = -9173866662540287337L;
	private List<JTabFirePanel> tabPanels = null;
	private JTabbedPane tabbedPane = null;
	//public JSplitPane contentSplitPane = null;
	public JSplitPane bottomSplitPane = null;
	public JSplitPane bottomLogSplitPane = null;
	public AbstractTabPanel scopePanel = null;
	public JTopPanelSerial topPanelSerial = null;
	
	public JMainPanel() {
		
		tabPanels = new ArrayList<JTabFirePanel>(10);
		tabPanels.add(new JTabPanelPWM());
		tabPanels.add(new JTabPanelSystem());
		tabPanels.add(new JTabPanelPins());
		tabPanels.add(new JTabPanelInput());
		tabPanels.add(new JTabPanelSTV());
		tabPanels.add(new JTabPanelPTC());
		tabPanels.add(new JTabPanelPTT());
		tabPanels.add(new JTabPanelVFC());
		tabPanels.add(new JTabPanelMAL());
		tabPanels.add(new JTabPanelLPM());
		tabPanels.add(new JTabPanelGraphs());
		tabPanels.add(new JTabPanelVariables());
		tabPanels.add(new JTabPanelSettings());
		if (PulseFireUI.getInstance().getSettingsManager().getSettingBoolean(PulseFireUISettingKeys.SCOPE_ENABLE)) {
			scopePanel = new JTabPanelScope(); 
			tabPanels.add(scopePanel);
		}

		JPanel main = this; //new JPanel(); //new ContentPanel(this);
		main.setLayout(new BorderLayout());
		main.add(createTop(), BorderLayout.PAGE_START);
		main.add(createContentSplit(), BorderLayout.CENTER);
		
		PulseFireUI.getInstance().getSettingsManager().addSettingListener(PulseFireUISettingKeys.SCOPE_ENABLE, this);
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
		sp0.setMinimumSize(new Dimension(100, 350));
		sp1.setMinimumSize(new Dimension(200, 150));
		return bottomSplitPane;
	}
	
	private JSplitPane createBottomSplit() {
		JPanel sp0 = createBottomConsole();
		JPanel sp1 = createBottomInfo();
		bottomLogSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,sp0,sp1);
		bottomLogSplitPane.setOneTouchExpandable(true);
		bottomLogSplitPane.setResizeWeight(0.4);
		bottomLogSplitPane.setDividerLocation(PulseFireUI.getInstance().getSettingsManager().getSettingInteger(PulseFireUISettingKeys.UI_SPLIT_BOTTOM_LOG));
		sp0.setMinimumSize(new Dimension(200, 100));
		sp1.setMinimumSize(new Dimension(200, 100));
		return bottomLogSplitPane;
	}
	
	private JPanel createCenterContent() {
		JPanel center = new JPanel();
		//center.setOpaque(false);
		center.setLayout(new GridLayout(1,0));
		tabbedPane = new JTabbedPane();
		tabbedPane.setFont(UIManager.getFont("TabbedPane.font")); // workaround
		tabbedPane.setBorder(BorderFactory.createEmptyBorder());
		for (JTabFirePanel panel:tabPanels) {
			JScrollPane scrollPane = new JScrollPane(panel.getJPanel());
			scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			scrollPane.setBorder(BorderFactory.createEmptyBorder());
			panel.setParentScrollPane(scrollPane);
			//scrollPane.getViewport().setOpaque(false);
			tabbedPane.addTab(panel.getTabName(),panel.getTabIcon(),scrollPane,panel.getTabTooltip());
		}
		//tabbedPane.setEnabledAt(8, false); // mal is not done
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
		if (PulseFireUI.getInstance().getSettingsManager().getSettingBoolean(PulseFireUISettingKeys.SCOPE_ENABLE)) {
			if (scopePanel==null) {
				scopePanel = new JTabPanelScope();
				
				JScrollPane scrollPane = new JScrollPane(scopePanel.getJPanel());
				scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
				scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
				scrollPane.setBorder(BorderFactory.createEmptyBorder());
				scopePanel.setParentScrollPane(scrollPane);
				tabbedPane.addTab(scopePanel.getTabName(),scopePanel.getTabIcon(),scrollPane,scopePanel.getTabTooltip());
			}
		} else {
			if (scopePanel!=null) {
				tabbedPane.removeTabAt(tabbedPane.getTabCount()-1);
				scopePanel = null;
			}
		}
	}
}
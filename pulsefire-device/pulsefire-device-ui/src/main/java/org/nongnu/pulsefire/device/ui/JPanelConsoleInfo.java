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
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.nongnu.pulsefire.device.io.protocol.CommandName;
import org.nongnu.pulsefire.device.io.transport.DeviceConnectListener;
import org.nongnu.pulsefire.device.ui.components.JFireGraph;

/**
 * JPanelConsoleInfo
 * 
 * @author Willem Cazander
 */
public class JPanelConsoleInfo extends JPanel implements ComponentListener, DeviceConnectListener, PulseFireUISettingListener {
	
	private static final long serialVersionUID = 5027054951800480326L;
	private final CardLayout cardLayout;
	private final JPanel graphPanel;
	private InfoViewType graphZeroState;
	
	public JPanelConsoleInfo() {
		addComponentListener(this);
		PulseFireUI.getInstance().getDeviceManager().addDeviceConnectListener(this);
		
		graphPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		cardLayout = new CardLayout();
		setLayout(cardLayout);
		setBorder(BorderFactory.createEmptyBorder());
		add(createInfoPanel(), InfoViewType.INFO.name());
		add(graphPanel, InfoViewType.GRAPH.name());
		
		PulseFireUI.getInstance().getSettingsManager().addSettingListener(PulseFireUISettingKeys.GRAPH_LIST_FRONT, this);
	}
	
	private enum InfoViewType {
		INFO, GRAPH
	}
	
	private JPanel createInfoPanel() {
		final String siteUrl = "http://www.nongnu.org/pulsefire/";
		String yearPart = "2011-"+Calendar.getInstance().get(Calendar.YEAR);
		JPanel infoPanel = new JPanel();
		infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
		infoPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new BorderLayout());
		
		JLabel text = new JLabel();
		text.setText("<html><center><h1>PulseFire</h1><sub>Copyright "+yearPart+" Willem Cazander</sub>"+
					"<br>For more information visit website;</center></html>");
		innerPanel.add(text,BorderLayout.NORTH);
		JLabel textUrl = new JLabel();
		textUrl.setText("<html><a href=\""+siteUrl+"\">"+siteUrl+"</a></html>");
		textUrl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		textUrl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					Desktop.getDesktop().browse(new URI(siteUrl));
				} catch (IOException ex) {
					ex.printStackTrace();
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		innerPanel.add(textUrl,BorderLayout.SOUTH);
		
		
		infoPanel.add(innerPanel);
		return infoPanel;
	}
	
	private void redoPanel() {
		if (!PulseFireUI.getInstance().getDeviceManager().isConnected()) {
			return;
		}
		
		int w = getSize().width;
		int h = getSize().height;
		int wMin = 200;
		int hMin = 100;
		int gW = w / wMin;
		int gH = h / hMin;
		
		List<CommandName> d = new ArrayList<CommandName>(10);
		d = CommandName.decodeCommandList(PulseFireUI.getInstance().getSettingsManager().getSettingString(PulseFireUISettingKeys.GRAPH_LIST_FRONT));
		int ii = 0;
		for (int y = 0; y < gH; y++) {
			for (int x = 0; x < gW; x++) {
				if (ii >= d.size()) {
					break;
				}
				ii++;
			}
		}
		
		// Auto switch back and forward when settings or size goes to zero
		// graphs.
		if (ii == 0) {
			if (!InfoViewType.INFO.equals(graphZeroState)) {
				cardLayout.show(JPanelConsoleInfo.this, InfoViewType.INFO.name());
				graphZeroState = InfoViewType.INFO; // do once
			}
		} else {
			if (!InfoViewType.GRAPH.equals(graphZeroState)) {
				cardLayout.show(JPanelConsoleInfo.this, InfoViewType.GRAPH.name());
				graphZeroState = InfoViewType.GRAPH;
			}
		}
		
		if (graphPanel.getComponentCount() == ii) {
			return; // nop
		}
		graphPanel.removeAll();
		ii = 0;
		for (int y = 0; y < gH; y++) {
			for (int x = 0; x < gW; x++) {
				if (ii >= d.size()) {
					break;
				}
				CommandName name = d.get(ii);
				JFireGraph g = new JFireGraph(name);
				g.setPreferredSize(new Dimension(wMin, hMin));
				graphPanel.add(g);
				ii++;
			}
		}
		SwingUtilities.updateComponentTreeUI(this); // fixes redraw artifacts
													// after removing most
													// graphs.
	}
	
	@Override
	public void componentShown(ComponentEvent e) {
	}
	
	@Override
	public void componentResized(ComponentEvent e) {
		redoPanel();
	}
	
	@Override
	public void componentMoved(ComponentEvent e) {
	}
	
	@Override
	public void componentHidden(ComponentEvent e) {
	}
	
	@Override
	public void deviceConnect() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				cardLayout.show(JPanelConsoleInfo.this, InfoViewType.GRAPH.name());
				redoPanel();
			}
		});
	}
	
	@Override
	public void deviceDisconnect() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				cardLayout.show(JPanelConsoleInfo.this, InfoViewType.INFO.name());
			}
		});
	}
	
	public void settingUpdated(PulseFireUISettingKeys key, String value) {
		redoPanel();
	}
}

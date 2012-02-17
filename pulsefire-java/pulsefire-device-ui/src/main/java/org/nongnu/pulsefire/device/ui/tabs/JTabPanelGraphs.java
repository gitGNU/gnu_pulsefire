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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.nongnu.pulsefire.device.ui.JComponentFactory;
import org.nongnu.pulsefire.device.ui.PulseFireUI;
import org.nongnu.pulsefire.device.ui.PulseFireUISettingKeys;
import org.nongnu.pulsefire.device.ui.SpringLayoutGrid;
import org.nongnu.pulsefire.device.ui.components.JFireGraph;
import org.nongnu.pulsefire.wire.CommandName;

/**
 * JTabPanelGraphs
 * 
 * @author Willem Cazander
 */
public class JTabPanelGraphs extends AbstractTabPanel implements ActionListener {

	private static final long serialVersionUID = -1416072133032318563L;
	private JPanel graphPanel = null;
	private JComboBox sizeBox = null;
	private JComboBox columnBox = null;
	
	public JTabPanelGraphs() {
		setLayout(new BorderLayout());
		add(JComponentFactory.createJPanelJWrap(createPanelGraphConfig()),BorderLayout.NORTH);
		add(JComponentFactory.createJPanelJWrap(createPanelGraph()),BorderLayout.CENTER);
	}
	
	private JPanel createPanelGraph() {
		JPanel resultPanel = JComponentFactory.createJFirePanel("Graphs");
		graphPanel = new JPanel();
		graphPanel.setLayout(new SpringLayout());
		SpringLayoutGrid.makeCompactGrid(graphPanel,0,0);
		resultPanel.add(graphPanel);
		return resultPanel;
	}
	
	private JPanel createPanelGraphConfig() {
		JPanel resultPanel = JComponentFactory.createJFirePanel("Config");
		resultPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		resultPanel.add(new JLabel("Size:"));
		sizeBox = new JComboBox(new String[] {"Large","Medium","Small"});
		sizeBox.setSelectedIndex(PulseFireUI.getInstance().getSettingInteger(PulseFireUISettingKeys.GRAPH_SIZE));
		sizeBox.addActionListener(this);
		resultPanel.add(sizeBox);
		
		resultPanel.add(new JLabel("Columns:"));
		columnBox = new JComboBox(new Integer[] {2,3,4,5,6,7,8,9,10,11,12,13});
		columnBox.setSelectedIndex(PulseFireUI.getInstance().getSettingInteger(PulseFireUISettingKeys.GRAPH_COLS));
		columnBox.addActionListener(this);
		resultPanel.add(columnBox);
		
		return resultPanel;
	}
	
	@Override
	public Class<?> getTabClassName() {
		return this.getClass();
	}

	@Override
	public void deviceConnect() {
		for (CommandName name:PulseFireUI.getInstance().getTimeData().getTimeDataKeys()) {
			graphPanel.add(new JFireGraph(name));
		}
		resizeGraphs();
		makeGraphGrid();
		super.deviceConnect();
	}

	private void makeGraphGrid() {
		for (Component c:graphPanel.getComponents()) {
			if (c instanceof JLabel) {
				graphPanel.remove(c); // remove label before adding them again.
			}
		}
		int compomentCount = graphPanel.getComponentCount();
		int columnCount = (Integer)columnBox.getSelectedItem();
		int spaceSize = (compomentCount/columnCount)*columnCount;
		if (spaceSize!=compomentCount) {
			spaceSize += columnCount;
		}
		for (int i=compomentCount;i<spaceSize;i++) {
			graphPanel.add(new JLabel(""));
			compomentCount++;
		}
		SpringLayoutGrid.makeCompactGrid(graphPanel,compomentCount/columnCount,columnCount);
	}
	
	private void resizeGraphs() {
		for (Component c:graphPanel.getComponents()) {
			if (c instanceof JFireGraph) {
				if (sizeBox.getSelectedIndex()==0) {
					c.setPreferredSize(new Dimension(400,200));
				} else if (sizeBox.getSelectedIndex()==1) {
					c.setPreferredSize(new Dimension(300,150));
				} else if (sizeBox.getSelectedIndex()==2) {
					c.setPreferredSize(new Dimension(200,100));
				}
			}
		}
	}
	
	@Override
	public void deviceDisconnect() {
		super.deviceDisconnect();
		for (Component c:graphPanel.getComponents()) {
			if (c instanceof JFireGraph) {
				JFireGraph g = (JFireGraph)c;
				PulseFireUI.getInstance().getTimeData().removeTimeDataListener(g.getCommandName(), g);
			}
		}
		graphPanel.removeAll();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (sizeBox.equals(e.getSource())) {
			resizeGraphs();
			PulseFireUI.getInstance().setSettingInteger(PulseFireUISettingKeys.GRAPH_SIZE,sizeBox.getSelectedIndex());
		} else if (columnBox.equals(e.getSource())) {
			makeGraphGrid();
			PulseFireUI.getInstance().setSettingInteger(PulseFireUISettingKeys.GRAPH_COLS,columnBox.getSelectedIndex());
		}
		graphPanel.revalidate();
		super.deviceConnect();
	}
}

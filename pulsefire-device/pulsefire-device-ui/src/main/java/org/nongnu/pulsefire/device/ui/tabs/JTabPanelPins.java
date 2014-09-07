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

import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.nongnu.pulsefire.device.io.protocol.CommandName;
import org.nongnu.pulsefire.device.ui.JComponentFactory;
import org.nongnu.pulsefire.device.ui.SpringLayoutGrid;
import org.nongnu.pulsefire.device.ui.components.JCommandCheckBox;
import org.nongnu.pulsefire.device.ui.components.JCommandComboBox;
import org.nongnu.pulsefire.device.ui.components.JCommandLabel;

/**
 * JTabPanelSystem
 * 
 * @author Willem Cazander
 */
public class JTabPanelPins extends AbstractFireTabPanel {

	public JTabPanelPins() {
		JPanel wrapL = new JPanel();
		wrapL.setLayout(new SpringLayout());
		wrapL.add(createChips());
		wrapL.add(createPinsAvrMega());
		SpringLayoutGrid.makeCompactGrid(wrapL,2,1,0,0,6,6);
		
		JPanel wrapT = new JPanel();
		wrapT.setLayout(new SpringLayout());
		wrapT.add(wrapL);
		wrapT.add(createPinsAvr());
		//wrapT.add(createPinsArm());
		SpringLayoutGrid.makeCompactGrid(wrapT,1,2,0,0,6,6);
		
		JPanel wrapI = new JPanel();
		wrapI.setLayout(new SpringLayout());
		wrapI.add(createInt());
		wrapI.add(createCommandQMapTable(CommandName.int_map));
		SpringLayoutGrid.makeCompactGrid(wrapI,2,1,0,0,6,6);
		
		JPanel wrap = new JPanel();
		wrap.setLayout(new SpringLayout());
		wrap.add(wrapT);
		wrap.add(wrapI);
		SpringLayoutGrid.makeCompactGrid(wrap,1,2,6,6,6,6);
		
		getJPanel().add(wrap);
	}
	
	private JPanel createChips() {
		JPanel wrapPanel = JComponentFactory.createJFirePanel(this,"chips");
		JPanel ioPanel = new JPanel();
		ioPanel.setLayout(new SpringLayout());
		
		ioPanel.add(new JCommandLabel(CommandName.dic_mux));
		ioPanel.add(new JCommandCheckBox(CommandName.dic_mux));
		
		ioPanel.add(new JCommandLabel(CommandName.spi_clock));
		ioPanel.add(new JCommandComboBox(CommandName.spi_clock));
		
		ioPanel.add(new JCommandLabel(CommandName.spi_chips));
		JPanel chipsPanel = new JPanel();
		chipsPanel.setLayout(new GridLayout(0,2));
		ioPanel.add(chipsPanel);
		JCheckBox box = null;
		
		box = new JCommandCheckBox(CommandName.spi_chips,0);
		box.setText("OUT8");
		box.putClientProperty("JComponent.sizeVariant", "mini");
		chipsPanel.add(box);
		box = new JCommandCheckBox(CommandName.spi_chips,1);
		box.setText("OUT16");
		box.putClientProperty("JComponent.sizeVariant", "mini");
		chipsPanel.add(box);
		box = new JCommandCheckBox(CommandName.spi_chips,2);
		box.setText("DOC8");
		box.putClientProperty("JComponent.sizeVariant", "mini");
		chipsPanel.add(box);
		box = new JCommandCheckBox(CommandName.spi_chips,3);
		box.setText("DOC16");
		box.putClientProperty("JComponent.sizeVariant", "mini");
		chipsPanel.add(box);
		box = new JCommandCheckBox(CommandName.spi_chips,4);
		box.setText("LCD");
		box.putClientProperty("JComponent.sizeVariant", "mini");
		chipsPanel.add(box);
		box = new JCommandCheckBox(CommandName.spi_chips,5);
		box.setText("FREE0");
		box.putClientProperty("JComponent.sizeVariant", "mini");
		chipsPanel.add(box);
		box = new JCommandCheckBox(CommandName.spi_chips,6);
		box.setText("FREE1");
		box.putClientProperty("JComponent.sizeVariant", "mini");
		chipsPanel.add(box);
		box = new JCommandCheckBox(CommandName.spi_chips,7);
		box.setText("FREE2");
		box.putClientProperty("JComponent.sizeVariant", "mini");
		chipsPanel.add(box);
		
		SpringLayoutGrid.makeCompactGrid(ioPanel,3,2);
		wrapPanel.add(ioPanel);
		return wrapPanel;
	}
	
	private JPanel createPinsAvr() {
		JPanel wrapPanel = JComponentFactory.createJFirePanel(this,"avr");
		JPanel ioPanel = new JPanel();
		ioPanel.setLayout(new SpringLayout());
		
		ioPanel.add(new JCommandLabel	(CommandName.avr_pin2_map));
		ioPanel.add(new JCommandComboBox(CommandName.avr_pin2_map));
		
		ioPanel.add(new JCommandLabel	(CommandName.avr_pin3_map));
		ioPanel.add(new JCommandComboBox(CommandName.avr_pin3_map));
		
		ioPanel.add(new JCommandLabel	(CommandName.avr_pin4_map));
		ioPanel.add(new JCommandComboBox(CommandName.avr_pin4_map));
		
		ioPanel.add(new JCommandLabel	(CommandName.avr_pin5_map));
		ioPanel.add(new JCommandComboBox(CommandName.avr_pin5_map));
		
		ioPanel.add(JComponentFactory.createJLabel(this,"noteA"));
		ioPanel.add(JComponentFactory.createJLabel(this,"noteB"));
		
		SpringLayoutGrid.makeCompactGrid(ioPanel,5,2);
		wrapPanel.add(ioPanel);
		return wrapPanel;
	}
	
	private JPanel createPinsAvrMega() {
		JPanel wrapPanel = JComponentFactory.createJFirePanel(this,"mega");
		JPanel ioPanel = new JPanel();
		ioPanel.setLayout(new SpringLayout());
		
		ioPanel.add(new JCommandLabel	(CommandName.mega_port_a));
		ioPanel.add(new JCommandComboBox(CommandName.mega_port_a));
		
		ioPanel.add(new JCommandLabel	(CommandName.mega_port_c));
		ioPanel.add(new JCommandComboBox(CommandName.mega_port_c));
		
		SpringLayoutGrid.makeCompactGrid(ioPanel,2,2);
		wrapPanel.add(ioPanel);
		return wrapPanel;
	}
	
	private JPanel createInt() {
		JPanel wrapPanel = JComponentFactory.createJFirePanel(this,"int");
		wrapPanel.setLayout(new BoxLayout(wrapPanel,BoxLayout.PAGE_AXIS));
		
		JPanel ioPanel = new JPanel();
		ioPanel.setLayout(new SpringLayout());
		
		ioPanel.add(new JCommandLabel	(CommandName.int_0mode));
		ioPanel.add(new JCommandComboBox(CommandName.int_0mode));
		ioPanel.add(new JCommandLabel	(CommandName.int_0trig));
		ioPanel.add(new JCommandComboBox(CommandName.int_0trig));
		ioPanel.add(new JCommandLabel	(CommandName.int_0freq_mul));
		ioPanel.add(new JCommandComboBox(CommandName.int_0freq_mul));
		
		ioPanel.add(new JCommandLabel	(CommandName.int_1mode));
		ioPanel.add(new JCommandComboBox(CommandName.int_1mode));
		ioPanel.add(new JCommandLabel	(CommandName.int_1trig));
		ioPanel.add(new JCommandComboBox(CommandName.int_1trig));
		ioPanel.add(new JCommandLabel	(CommandName.int_1freq_mul));
		ioPanel.add(new JCommandComboBox(CommandName.int_1freq_mul));
		
		SpringLayoutGrid.makeCompactGrid(ioPanel,2,6);
		wrapPanel.add(ioPanel);
		
		return wrapPanel;
	}
}

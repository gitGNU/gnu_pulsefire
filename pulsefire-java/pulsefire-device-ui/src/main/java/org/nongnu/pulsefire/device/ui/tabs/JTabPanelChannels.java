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
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.UIManager;

import org.nongnu.pulsefire.device.DeviceCommandListener;
import org.nongnu.pulsefire.device.ui.JComponentEnableStateListener;
import org.nongnu.pulsefire.device.ui.JComponentFactory;
import org.nongnu.pulsefire.device.ui.PulseFireUI;
import org.nongnu.pulsefire.device.ui.SpringLayoutGrid;
import org.nongnu.pulsefire.device.ui.components.JCommandCheckBox;
import org.nongnu.pulsefire.device.ui.components.JCommandDial;
import org.nongnu.pulsefire.device.ui.components.JFireBorderChild;
import org.nongnu.pulsefire.wire.Command;
import org.nongnu.pulsefire.wire.CommandName;

/**
 * JTabPanelChannels
 * 
 * @author Willem Cazander
 */
public class JTabPanelChannels extends AbstractTabPanel implements DeviceCommandListener {

	private static final long serialVersionUID = 8834117894619851885L;
	private JPanel centerPanel = null;
	private List<JPanel> channels = null;
	private List<JPanel> channelsEmpty = null;

	public JTabPanelChannels() {
		
		channels = new ArrayList<JPanel>(16);
		channelsEmpty = new ArrayList<JPanel>(16);
		for (int i=0;i<16;i++) {
			channelsEmpty.add(new JPanel());
		}
		
		PulseFireUI.getInstance().getDeviceManager().addDeviceCommandListener(CommandName.pulse_steps, this);
		JPanel splitPanel = new JPanel();
		splitPanel.setLayout(new BoxLayout(splitPanel,BoxLayout.LINE_AXIS));
		splitPanel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
		//splitPanel.add(createFirst());
		splitPanel.add(new JPanel());
		
		centerPanel = new JPanel();
		GridLayout centerLayout = new GridLayout(1,16,5,5);
		centerPanel.setLayout(centerLayout);
		for (int i=0;i<16;i++) {
			JPanel out = JComponentFactory.createJFirePanel("OUT"+i);
			out.setLayout(new BoxLayout(out, BoxLayout.PAGE_AXIS));

			JPanel boxPanel = new JPanel();
			boxPanel.setLayout(new SpringLayout());
			
			JCheckBox boxMaskA = new JCommandCheckBox(CommandName.pulse_mask_a,i);
			boxMaskA.putClientProperty("JComponent.sizeVariant", "mini");
			boxPanel.add(boxMaskA);
			JLabel maskLabel = new JLabel("Mask");
			boxPanel.add(maskLabel);
			JCheckBox boxMaskB = new JCommandCheckBox(CommandName.pulse_mask_b,i);
			boxMaskB.putClientProperty("JComponent.sizeVariant", "mini");
			boxPanel.add(boxMaskB);
			JCheckBox boxInitA = new JCommandCheckBox(CommandName.pulse_init_a,i);
			boxInitA.putClientProperty("JComponent.sizeVariant", "mini");
			boxPanel.add(boxInitA);
			JLabel intiLabel = new JLabel("Init");
			boxPanel.add(intiLabel);
			JCheckBox boxInitB = new JCommandCheckBox(CommandName.pulse_init_b,i);
			boxInitB.putClientProperty("JComponent.sizeVariant", "mini");
			boxPanel.add(boxInitB);

			SpringLayoutGrid.makeCompactGrid(boxPanel,2,3,0,0,0,0);
			out.add(boxPanel);
			
			JPanel dialPanel = new JPanel();
			dialPanel.setBorder(new JFireBorderChild(out));
			dialPanel.setLayout(new SpringLayout());
			
			JCommandDial dialA = new JCommandDial(CommandName.pwm_on_cnt_a,i);
			dialPanel.add(dialA);
			JCommandDial dialB = new JCommandDial(CommandName.pwm_on_cnt_b,i);
			dialPanel.add(dialB);
			dialPanel.add(new JCommandDial(CommandName.pwm_off_cnt_a,i));
			dialPanel.add(new JCommandDial(CommandName.pwm_off_cnt_b,i));
			
			SpringLayoutGrid.makeCompactGrid(dialPanel,2,2,0,0,0,0);
			out.add(dialPanel);
			
			JPanel flagsPanel = new JPanel();
			//flagsPanel.setBorder(new JFireBorderChild(out));
			flagsPanel.setLayout(new SpringLayout());
			
			JPanel flags = new JPanel();
			flags.setLayout(new BoxLayout(flags,BoxLayout.PAGE_AXIS));
			JCheckBox pwmFlag = new JCheckBox();
			pwmFlag.setText("pwm");
			pwmFlag.setEnabled(false);
			//JComponentEnableStateListener.attach(pwmFlag,null);
			pwmFlag.putClientProperty("JComponent.sizeVariant", "mini");
			flags.add(pwmFlag);
			JCheckBox ppmFlag = new JCheckBox();
			ppmFlag.setText("ppm");
			ppmFlag.setEnabled(false);
			ppmFlag.putClientProperty("JComponent.sizeVariant", "mini");
			//JComponentEnableStateListener.attach(ppmFlag,null);
			flags.add(ppmFlag);
			flagsPanel.add(flags);
			flagsPanel.add(new JCommandDial(CommandName.pwm_tune_cnt,i));

			SpringLayoutGrid.makeCompactGrid(flagsPanel,1,2,0,0,0,0);
			out.add(flagsPanel);

			JPanel ppmPanel = new JPanel();
			ppmPanel.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
			//ppmPanel.setBorder(new JFireBorderChild(out));
			//ppmPanel.setLayout(new FlowLayout(FlowLayout.CENTER,2,2));
			ppmPanel.setLayout(new BorderLayout(5,5));
			JPanel ppmPanelA = new JPanel();
			ppmPanelA.setLayout(new GridLayout(0,2));
			for (int p=0;p<16;p++) {
				JCheckBox box = new JCommandCheckBox(CommandName.ppm_data_a,p,i);
				box.putClientProperty("JComponent.sizeVariant", "mini");
				box.setBorder(BorderFactory.createEmptyBorder());
				ppmPanelA.add(box);
			}
			JPanel ppmPanelT = new JPanel();
			ppmPanelT.setLayout(new GridLayout(0,1));
			int num = 0;
			for (int p=0;p<8;p++) {
				StringBuilder buf = new StringBuilder(20);
				int n = num++;
				if (n<=9) {
					buf.append('0');
				}
				buf.append(n);
				buf.append('-');
				n = num++;
				if (n<=9) {
					buf.append('0');
				}
				buf.append(n);
				JLabel l = new JLabel(buf.toString());
				l.setFont(UIManager.getFont("FireDial.font"));
				l.setForeground(UIManager.getColor("nimbusDisabledText"));
				ppmPanelT.add(l);
			}
			JPanel ppmPanelB = new JPanel();
			//ppmPanelB.setBorder(new JFireBorderChild((JFireBorder)out.getBorder(),0,0,1));
			ppmPanelB.setLayout(new GridLayout(0,2));
			for (int p=0;p<16;p++) {
				JCheckBox box = new JCommandCheckBox(CommandName.ppm_data_b,p,i);
				box.putClientProperty("JComponent.sizeVariant", "mini");
				ppmPanelB.add(box);
			}
			
			ppmPanel.add(ppmPanelA,BorderLayout.WEST);
			ppmPanel.add(ppmPanelT,BorderLayout.CENTER);
			ppmPanel.add(ppmPanelB,BorderLayout.EAST);
			out.add(ppmPanel);
			centerPanel.add(out);
			channels.add(out);
		}
		splitPanel.add(centerPanel);
		add(splitPanel);
	}
	
	private JPanel createFirst() {
		JPanel borderPanel = JComponentFactory.createJFirePanel("ALL");
		borderPanel.setLayout(new BoxLayout(borderPanel,BoxLayout.PAGE_AXIS));
		
		JPanel boxPanel = new JPanel();
		boxPanel.setLayout(new SpringLayout());
		JCheckBox boxMaskA = new JCommandCheckBox(CommandName.pulse_mask_a,255);
		boxMaskA.putClientProperty("JComponent.sizeVariant", "mini");
		boxPanel.add(boxMaskA);
		JLabel maskLabel = new JLabel("Mask");
		boxPanel.add(maskLabel);
		JCheckBox boxMaskB = new JCommandCheckBox(CommandName.pulse_mask_b,255);
		boxMaskB.putClientProperty("JComponent.sizeVariant", "mini");
		boxPanel.add(boxMaskB);
		JCheckBox boxInitA = new JCommandCheckBox(CommandName.pulse_init_a,255);
		boxInitA.putClientProperty("JComponent.sizeVariant", "mini");
		boxPanel.add(boxInitA);
		JLabel intiLabel = new JLabel("Init");
		boxPanel.add(intiLabel);
		JCheckBox boxInitB = new JCommandCheckBox(CommandName.pulse_init_b,255);
		boxInitB.putClientProperty("JComponent.sizeVariant", "mini");
		boxPanel.add(boxInitB);
		SpringLayoutGrid.makeCompactGrid(boxPanel,2,3,0,0,0,0);
		borderPanel.add(boxPanel);
		
		
		JPanel dialPanel = new JPanel();
		dialPanel.setBorder(new JFireBorderChild(borderPanel));
		dialPanel.setLayout(new SpringLayout());
		dialPanel.add(new JCommandDial(CommandName.pwm_on_cnt_a,255));
		dialPanel.add(new JCommandDial(CommandName.pwm_on_cnt_b,255));
		dialPanel.add(new JCommandDial(CommandName.pwm_off_cnt_a,255));
		dialPanel.add(new JCommandDial(CommandName.pwm_off_cnt_b,255));
		SpringLayoutGrid.makeCompactGrid(dialPanel,2,2,0,0,0,0);
		borderPanel.add(dialPanel);
		
		JPanel flagsPanel = new JPanel();
		flagsPanel.setLayout(new SpringLayout());
		JPanel flags = new JPanel();
		flags.setLayout(new BoxLayout(flags,BoxLayout.PAGE_AXIS));
		
		flagsPanel.add(flags);
		flagsPanel.add(new JCommandDial(CommandName.pwm_tune_cnt,255));
		SpringLayoutGrid.makeCompactGrid(flagsPanel,1,2,0,0,0,0);
		borderPanel.add(flagsPanel);
		
		JPanel ppmPanel = new JPanel();
		ppmPanel.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
		//ppmPanel.setBorder(new JFireBorderChild(out));
		//ppmPanel.setLayout(new FlowLayout(FlowLayout.CENTER,2,2));
		ppmPanel.setLayout(new BorderLayout(5,5));
		JPanel ppmPanelA = new JPanel();
		ppmPanelA.setLayout(new GridLayout(0,2));
		for (int p=0;p<16;p++) {
			JCheckBox box = new JCommandCheckBox(CommandName.ppm_data_a,p,255);
			box.putClientProperty("JComponent.sizeVariant", "mini");
			box.setBorder(BorderFactory.createEmptyBorder());
			ppmPanelA.add(box);
		}
		JPanel ppmPanelT = new JPanel();
		ppmPanelT.setLayout(new GridLayout(0,1));
		int num = 0;
		for (int p=0;p<8;p++) {
			StringBuilder buf = new StringBuilder(20);
			int n = num++;
			if (n<=9) {
				buf.append('0');
			}
			buf.append(n);
			buf.append('-');
			n = num++;
			if (n<=9) {
				buf.append('0');
			}
			buf.append(n);
			JLabel l = new JLabel(buf.toString());
			l.setFont(UIManager.getFont("FireDial.font"));
			l.setForeground(UIManager.getColor("nimbusDisabledText"));
			ppmPanelT.add(l);
		}
		JPanel ppmPanelB = new JPanel();
		//ppmPanelB.setBorder(new JFireBorderChild((JFireBorder)out.getBorder(),0,0,1));
		ppmPanelB.setLayout(new GridLayout(0,2));
		for (int p=0;p<16;p++) {
			JCheckBox box = new JCommandCheckBox(CommandName.ppm_data_b,p,255);
			box.putClientProperty("JComponent.sizeVariant", "mini");
			ppmPanelB.add(box);
		}
		
		ppmPanel.add(ppmPanelA,BorderLayout.WEST);
		ppmPanel.add(ppmPanelT,BorderLayout.CENTER);
		ppmPanel.add(ppmPanelB,BorderLayout.EAST);
		borderPanel.add(ppmPanel);
		return borderPanel;
	}
	
	@Override
	public Class<?> getTabClassName() {
		return this.getClass();
	}

	@Override
	public void commandReceived(Command command) {
		int steps = new Integer(command.getArgu0());
		checkChannels(steps);
	}
	
	private void checkChannels(int steps) {
		centerPanel.removeAll();
		for (int i=0;i<16;i++) {
			if (i>=steps) {
				centerPanel.add(channelsEmpty.get(i));
			} else {
				centerPanel.add(channels.get(i));
			}
		}
	}
}

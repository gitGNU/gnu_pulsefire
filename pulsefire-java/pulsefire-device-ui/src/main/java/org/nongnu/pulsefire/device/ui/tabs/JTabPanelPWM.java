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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.UIManager;

import org.nongnu.pulsefire.device.DeviceCommandListener;
import org.nongnu.pulsefire.device.ui.JComponentEnableStateListener;
import org.nongnu.pulsefire.device.ui.JComponentFactory;
import org.nongnu.pulsefire.device.ui.PulseFireUI;
import org.nongnu.pulsefire.device.ui.PulseFireUISettingKeys;
import org.nongnu.pulsefire.device.ui.SpringLayoutGrid;
import org.nongnu.pulsefire.device.ui.components.JCommandCheckBox;
import org.nongnu.pulsefire.device.ui.components.JCommandComboBox;
import org.nongnu.pulsefire.device.ui.components.JCommandDial;
import org.nongnu.pulsefire.device.ui.components.JFireBorderChild;
import org.nongnu.pulsefire.device.ui.components.JFireDial;
import org.nongnu.pulsefire.device.ui.components.JFireDial.DialEvent;
import org.nongnu.pulsefire.wire.Command;
import org.nongnu.pulsefire.wire.CommandName;

/**
 * JTabPanelPWM
 * 
 * @author Willem Cazander
 */
public class JTabPanelPWM extends AbstractTabPanel implements DeviceCommandListener {

	private static final long serialVersionUID = 8834117894619851885L;
	private JPanel centerPanel = null;
	private List<JPanel> channels = null;
	private List<JPanel> channelsEmpty = null;

	public JTabPanelPWM() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		setBorder(BorderFactory.createEmptyBorder(4,4,4,4)); // align with spring layout on other tabs (6-2=4)
		
		channels = new ArrayList<JPanel>(16);
		channelsEmpty = new ArrayList<JPanel>(16);
		for (int i=0;i<16;i++) {
			channelsEmpty.add(new JPanel());
		}
		PulseFireUI.getInstance().getDeviceManager().addDeviceCommandListener(CommandName.pulse_steps, this);
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		
		JPanel topLayoutPanel = new JPanel();
		topLayoutPanel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
		topLayoutPanel.setLayout(new FlowLayout(FlowLayout.LEFT,6,0));
		JPanel pulsePanel = createTopPulse();
		topLayoutPanel.add(pulsePanel);
		JPanel pwmPanel = createTopPWM();
		pwmPanel.setPreferredSize(new Dimension(pwmPanel.getPreferredSize().width, pulsePanel.getPreferredSize().height));
		topLayoutPanel.add(pwmPanel);
		JPanel ppmPanel = createTopPPM();
		ppmPanel.setPreferredSize(new Dimension(ppmPanel.getPreferredSize().width, pulsePanel.getPreferredSize().height));
		topLayoutPanel.add(ppmPanel);
		JPanel freqPanel = createTopFreq();
		freqPanel.setPreferredSize(new Dimension(freqPanel.getPreferredSize().width, pulsePanel.getPreferredSize().height));
		topLayoutPanel.add(freqPanel);
		topPanel.add(topLayoutPanel,BorderLayout.NORTH);
		
		JPanel splitPanel = new JPanel();
		splitPanel.setLayout(new BoxLayout(splitPanel,BoxLayout.LINE_AXIS));
		splitPanel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
		//splitPanel.add(createChannelFirst());
		splitPanel.add(createChannelAll());
		topPanel.add(splitPanel,BorderLayout.CENTER);
		
		add(topPanel);
	}
	
	private JPanel createChannelAll() {
		centerPanel = new JPanel();
		GridLayout centerLayout = new GridLayout(1,16,5,5);
		centerPanel.setLayout(centerLayout);
		centerPanel.setBorder(BorderFactory.createEmptyBorder(0,5,0,0)); // only left 5 px like grid layout
		
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
		return centerPanel;
	}
	
	/*
	private JPanel createChannelFirst() {
		JPanel borderPanel = JComponentFactory.createJFirePanel("ALL");
		borderPanel.setLayout(new BoxLayout(borderPanel,BoxLayout.PAGE_AXIS));
		
		JPanel boxPanel = new JPanel();
		boxPanel.setLayout(new SpringLayout());
		JCheckBox boxMaskA = new JCommandCheckBoxAll(CommandName.pulse_mask_a);
		boxMaskA.putClientProperty("JComponent.sizeVariant", "mini");
		boxPanel.add(boxMaskA);
		JLabel maskLabel = new JLabel("Mask");
		boxPanel.add(maskLabel);
		JCheckBox boxMaskB = new JCommandCheckBoxAll(CommandName.pulse_mask_b);
		boxMaskB.putClientProperty("JComponent.sizeVariant", "mini");
		boxPanel.add(boxMaskB);
		JCheckBox boxInitA = new JCommandCheckBoxAll(CommandName.pulse_init_a);
		boxInitA.putClientProperty("JComponent.sizeVariant", "mini");
		boxPanel.add(boxInitA);
		JLabel intiLabel = new JLabel("Init");
		boxPanel.add(intiLabel);
		JCheckBox boxInitB = new JCommandCheckBoxAll(CommandName.pulse_init_b);
		boxInitB.putClientProperty("JComponent.sizeVariant", "mini");
		boxPanel.add(boxInitB);
		SpringLayoutGrid.makeCompactGrid(boxPanel,2,3,0,0,0,0);
		borderPanel.add(boxPanel);
		
		
		JPanel dialPanel = new JPanel();
		dialPanel.setBorder(new JFireBorderChild(borderPanel));
		dialPanel.setLayout(new SpringLayout());
		dialPanel.add(new JCommandDial(CommandName.pwm_on_cnt_a));
		dialPanel.add(new JCommandDial(CommandName.pwm_on_cnt_b));
		dialPanel.add(new JCommandDial(CommandName.pwm_off_cnt_a));
		dialPanel.add(new JCommandDial(CommandName.pwm_off_cnt_b));
		SpringLayoutGrid.makeCompactGrid(dialPanel,2,2,0,0,0,0);
		borderPanel.add(dialPanel);
		
		JPanel flagsPanel = new JPanel();
		flagsPanel.setLayout(new SpringLayout());
		JPanel flags = new JPanel();
		flags.setLayout(new BoxLayout(flags,BoxLayout.PAGE_AXIS));
		
		flagsPanel.add(flags);
		flagsPanel.add(new JCommandDial(CommandName.pwm_tune_cnt));
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
			JCheckBox box = new JCommandCheckBoxAll(CommandName.ppm_data_a,p);
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
			JCheckBox box = new JCommandCheckBoxAll(CommandName.ppm_data_b,p);
			box.putClientProperty("JComponent.sizeVariant", "mini");
			ppmPanelB.add(box);
		}
		
		ppmPanel.add(ppmPanelA,BorderLayout.WEST);
		ppmPanel.add(ppmPanelT,BorderLayout.CENTER);
		ppmPanel.add(ppmPanelB,BorderLayout.EAST);
		borderPanel.add(ppmPanel);
		return borderPanel;
	}
	*/
	
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
		Boolean limit = PulseFireUI.getInstance().getSettingBoolean(PulseFireUISettingKeys.LIMIT_CHANNELS);
		if (limit==false) {
			steps = CommandName.pulse_steps.getMaxValue();
		}
		centerPanel.removeAll();
		for (int i=0;i<16;i++) {
			if (i>=steps) {
				centerPanel.add(channelsEmpty.get(i));
			} else {
				centerPanel.add(channels.get(i));
			}
		}
	}
	
	private JPanel createTopFreq() {
		JPanel borderPanel = JComponentFactory.createJFirePanel("Freq");
		
		JPanel splitPanel = new JPanel();
		splitPanel.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
		borderPanel.add(splitPanel);
		
		JPanel freqDialPanel = new JPanel();
		freqDialPanel.setLayout(new SpringLayout());
		splitPanel.add(freqDialPanel);
		final JFireDial freqReqDial = new JFireDial("freq",1,65535,32768);
		freqReqDial.setDotIndex(2);
		JComponentEnableStateListener.attach(freqReqDial, CommandName.req_pwm_freq);
		PulseFireUI.getInstance().getDeviceManager().addDeviceCommandListener(CommandName.pwm_req_freq, new DeviceCommandListener() {
			@Override
			public void commandReceived(Command command) {
				if (command.getArgu0()!=null && command.getArgu0().isEmpty()==false) {
					freqReqDial.setValue(new Integer(command.getArgu0()));
				}
			}
		});
		freqDialPanel.add(JComponentFactory.createJPanelJWrap(freqReqDial));
		freqDialPanel.add(new JCommandDial(CommandName.pwm_duty));
		SpringLayoutGrid.makeCompactGrid(freqDialPanel,1,2);
		
		JPanel pulsePanel = new JPanel();
		pulsePanel.setLayout(new SpringLayout());
		splitPanel.add(pulsePanel);
		pulsePanel.add(JComponentFactory.createJLabel("Channel:"));
		final JComboBox freqChannelBox = new JComboBox(new String[] {"ALL","0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15"});
		JComponentEnableStateListener.attach(freqChannelBox, CommandName.req_pwm_freq);
		pulsePanel.add(freqChannelBox);
		pulsePanel.add(JComponentFactory.createJLabel("Freq:"));
		JButton freqReqButton = new JButton("Request"); 
		JComponentEnableStateListener.attach(freqReqButton, CommandName.req_pwm_freq);
		pulsePanel.add(freqReqButton);
		SpringLayoutGrid.makeCompactGrid(pulsePanel,2,2);
		
		freqReqButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String chStr = freqChannelBox.getSelectedItem().toString();
				if ("ALL".equals(chStr)) {
					chStr = "255";
				}
				Command reqPwmCmd = new Command(CommandName.req_pwm_freq);
				reqPwmCmd.setArgu0(""+freqReqDial.getValue());
				reqPwmCmd.setArgu1(chStr);
				PulseFireUI.getInstance().getDeviceManager().requestCommand(reqPwmCmd);
			}
		});
		return borderPanel;
	}
	
	private JPanel createTopPPM() {
		JPanel resultPanel = JComponentFactory.createJFirePanel("PPM");

		JPanel splitPanel = new JPanel();
		splitPanel.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
		resultPanel.add(splitPanel);
		
		JPanel ppmPanel = new JPanel();
		ppmPanel.setLayout(new SpringLayout());
		splitPanel.add(ppmPanel);
		JCommandDial dial = null;
		dial = new JCommandDial(CommandName.ppm_data_len);
		ppmPanel.add(dial);
		dial = new JCommandDial(CommandName.ppm_data_offset);
		ppmPanel.add(dial);
		
		SpringLayoutGrid.makeCompactGrid(ppmPanel,1,2);
		return resultPanel;
	}
	
	private JPanel createTopPWM() {
		JPanel resultPanel = JComponentFactory.createJFirePanel("PWM");
		
		JPanel splitPanel = new JPanel();
		splitPanel.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
		resultPanel.add(splitPanel);
		
		JPanel pwmPanel = new JPanel();
		pwmPanel.setLayout(new SpringLayout());
		splitPanel.add(pwmPanel);
		pwmPanel.add(new JCommandDial(CommandName.pwm_loop));
		pwmPanel.add(new JCommandDial(CommandName.pwm_loop_delta));
		SpringLayoutGrid.makeCompactGrid(pwmPanel,1,2);
		
		JPanel clockPanel = new JPanel();
		clockPanel.setLayout(new SpringLayout());
		splitPanel.add(clockPanel);
		clockPanel.add(new JCommandComboBox(CommandName.pwm_clock));
		SpringLayoutGrid.makeCompactGrid(clockPanel,1,1);

		return resultPanel;
	}
	
	private JPanel createTopPulse() {
		JPanel borderPanel = JComponentFactory.createJFirePanel("Pulse");
		
		JPanel splitPanel = new JPanel();
		splitPanel.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
		borderPanel.add(splitPanel);
		
		JPanel pulsePanel = new JPanel();
		pulsePanel.setLayout(new SpringLayout());
		splitPanel.add(pulsePanel);

		pulsePanel.add(JComponentFactory.createJLabel("Mode"));
		pulsePanel.add(new JCommandComboBox(CommandName.pulse_mode));
	
		pulsePanel.add(JComponentFactory.createJLabel("Direction"));
		pulsePanel.add(new JCommandComboBox(CommandName.pulse_dir));
	
		pulsePanel.add(JComponentFactory.createJLabel("Trigger"));
		pulsePanel.add(new JCommandComboBox(CommandName.pulse_trig));
		
		pulsePanel.add(JComponentFactory.createJLabel("Bank"));
		pulsePanel.add(new JCommandComboBox(CommandName.pulse_bank));
		
		SpringLayoutGrid.makeCompactGrid(pulsePanel,2,4);
		
		JPanel delayPanel = new JPanel();
		delayPanel.setLayout(new SpringLayout());
		splitPanel.add(delayPanel);
		delayPanel.add(new JCommandDial(CommandName.pulse_trig_delay));
		delayPanel.add(new JCommandDial(CommandName.pulse_post_delay));
		SpringLayoutGrid.makeCompactGrid(delayPanel,1,2);
		
		return borderPanel;
	}
}

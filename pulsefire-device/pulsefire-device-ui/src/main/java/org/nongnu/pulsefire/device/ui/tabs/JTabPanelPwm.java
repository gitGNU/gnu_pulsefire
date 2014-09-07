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
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.UIManager;

import org.nongnu.pulsefire.device.io.protocol.Command;
import org.nongnu.pulsefire.device.io.protocol.CommandName;
import org.nongnu.pulsefire.device.io.transport.DeviceCommandListener;
import org.nongnu.pulsefire.device.ui.JComponentFactory;
import org.nongnu.pulsefire.device.ui.PulseFireUI;
import org.nongnu.pulsefire.device.ui.PulseFireUISettingKeys;
import org.nongnu.pulsefire.device.ui.PulseFireUISettingListener;
import org.nongnu.pulsefire.device.ui.SpringLayoutGrid;
import org.nongnu.pulsefire.device.ui.components.JCommandCheckBox;
import org.nongnu.pulsefire.device.ui.components.JCommandComboBox;
import org.nongnu.pulsefire.device.ui.components.JCommandDial;
import org.nongnu.pulsefire.device.ui.components.JCommandLabel;
import org.nongnu.pulsefire.device.ui.components.JFireBorderChild;
import org.nongnu.pulsefire.device.ui.components.JFirePwmInfo;

/**
 * JTabPanelPWM
 * 
 * @author Willem Cazander
 */
public class JTabPanelPwm extends AbstractFireTabPanel implements DeviceCommandListener,PulseFireUISettingListener {

	private JPanel centerPanel = null;
	private List<JPanel> channels = null;
	private List<JPanel> channelsEmpty = null;
	
	public JTabPanelPwm() {
		getJPanel().setBorder(BorderFactory.createEmptyBorder(4,4,4,4)); // align with spring layout on other tabs (6-2=4)
		getJPanel().add(createContent());
	}
	
	protected JPanel createTabSidePane() {
		JPanel result = new JPanel();
		result.setLayout(new GridLayout(1,1,0,0));
		result.add(new JFirePwmInfo());
		return result;
	}
	
	private JPanel createContent() {
		JPanel result = new JPanel();
		
		channels = new ArrayList<JPanel>(16);
		channelsEmpty = new ArrayList<JPanel>(16);
		for (int i=0;i<16;i++) {
			channelsEmpty.add(new JPanel());
		}
		PulseFireUI.getInstance().getDeviceManager().addDeviceCommandListener(CommandName.pulse_steps, this);
		PulseFireUI.getInstance().getSettingsManager().addSettingListener(PulseFireUISettingKeys.LIMIT_CHANNELS, this);
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		
		JPanel topLayoutPanel = new JPanel();
		topLayoutPanel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
		topLayoutPanel.setLayout(new FlowLayout(FlowLayout.LEFT,6,0));
		JPanel pulsePanel = createTopPulse();
		topLayoutPanel.add(pulsePanel);
		JPanel delayPanel = createTopDelay();
		delayPanel.setPreferredSize(new Dimension(delayPanel.getPreferredSize().width, pulsePanel.getPreferredSize().height));
		topLayoutPanel.add(delayPanel);
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
		
		result.add(topPanel);
		
		return result;
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
			JCheckBox boxInvA = new JCommandCheckBox(CommandName.pulse_inv_a,i);
			boxInvA.putClientProperty("JComponent.sizeVariant", "mini");
			boxPanel.add(boxInvA);
			JLabel invLabel = new JLabel("Invert");
			boxPanel.add(invLabel);
			JCheckBox boxInvB = new JCommandCheckBox(CommandName.pulse_inv_b,i);
			boxInvB.putClientProperty("JComponent.sizeVariant", "mini");
			boxPanel.add(boxInvB);

			SpringLayoutGrid.makeCompactGrid(boxPanel,3,3,0,0,0,0);
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
			
			/*
			JPanel flagsPanel = new JPanel();
			flagsPanel.setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
			flagsPanel.add(new JCommandDial(CommandName.pwm_tune_cnt,i));
			out.add(flagsPanel);
			*/

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
	
	@Override
	public void commandReceived(Command command) {
		int steps = new Integer(command.getArgu0());
		checkChannels(steps);
	}
	
	private void checkChannels(long steps) {
		Boolean limit = PulseFireUI.getInstance().getSettingsManager().getSettingBoolean(PulseFireUISettingKeys.LIMIT_CHANNELS);
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
		JPanel borderPanel = JComponentFactory.createJFirePanel(this,"freq");
		
		JPanel splitPanel = new JPanel();
		splitPanel.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
		borderPanel.add(splitPanel);
		
		JPanel freqDialPanel = new JPanel();
		freqDialPanel.setLayout(new SpringLayout());
		splitPanel.add(freqDialPanel);

		JCommandDial freqDial = new JCommandDial(CommandName.pwm_req_freq);
		freqDial.getFireDial().setDotIndex(2);
		freqDialPanel.add(freqDial);
		freqDialPanel.add(new JCommandDial(CommandName.pwm_req_duty));
		SpringLayoutGrid.makeCompactGrid(freqDialPanel,1,2);
		
		JPanel pulsePanel = new JPanel();
		pulsePanel.setLayout(new SpringLayout());
		splitPanel.add(pulsePanel);
		pulsePanel.add(new JCommandLabel(CommandName.pwm_req_idx));
		JComboBox<String> freqChannelBox = new JCommandComboBox(CommandName.pwm_req_idx);
		pulsePanel.add(freqChannelBox);
		SpringLayoutGrid.makeCompactGrid(pulsePanel,2,1);
		return borderPanel;
	}
	
	private JPanel createTopPPM() {
		JPanel resultPanel = JComponentFactory.createJFirePanel(this,"ppm");

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
		JPanel resultPanel = JComponentFactory.createJFirePanel(this,"pwm");
		
		JPanel splitPanel = new JPanel();
		splitPanel.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
		resultPanel.add(splitPanel);
		
		JPanel pwmPanel = new JPanel();
		pwmPanel.setLayout(new SpringLayout());
		splitPanel.add(pwmPanel);
		pwmPanel.add(new JCommandDial(CommandName.pwm_loop));
		//pwmPanel.add(new JCommandDial(CommandName.pwm_loop_delta));
		SpringLayoutGrid.makeCompactGrid(pwmPanel,1,1);
		
		JPanel clockPanel = new JPanel();
		clockPanel.setLayout(new SpringLayout());
		splitPanel.add(clockPanel);
		clockPanel.add(new JCommandLabel(CommandName.pwm_clock));
		clockPanel.add(new JCommandComboBox(CommandName.pwm_clock));
		SpringLayoutGrid.makeCompactGrid(clockPanel,2,1);

		return resultPanel;
	}
	
	private JPanel createTopDelay() {
		JPanel borderPanel = JComponentFactory.createJFirePanel(this,"delay");
		
		JPanel splitPanel = new JPanel();
		splitPanel.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
		borderPanel.add(splitPanel);
		
		JPanel delayPanel = new JPanel();
		delayPanel.setLayout(new SpringLayout());
		splitPanel.add(delayPanel);
		delayPanel.add(new JCommandDial(CommandName.pulse_pre_delay));
		delayPanel.add(new JCommandDial(CommandName.pulse_post_delay));
		SpringLayoutGrid.makeCompactGrid(delayPanel,1,2);
		
		JPanel mulPanel = new JPanel();
		mulPanel.setLayout(new SpringLayout());
		splitPanel.add(mulPanel);
		mulPanel.add(new JCommandLabel(CommandName.pulse_pre_mul));
		mulPanel.add(new JCommandComboBox(CommandName.pulse_pre_mul));
		mulPanel.add(new JCommandLabel(CommandName.pulse_post_mul));
		mulPanel.add(new JCommandComboBox(CommandName.pulse_post_mul));
		SpringLayoutGrid.makeCompactGrid(mulPanel,2,2);
		
		JPanel postPanel = new JPanel();
		postPanel.setLayout(new SpringLayout());
		splitPanel.add(postPanel);
		postPanel.add(new JCommandLabel(CommandName.pulse_post_hold));
		postPanel.add(new JCommandComboBox(CommandName.pulse_post_hold));
		SpringLayoutGrid.makeCompactGrid(postPanel,2,1);
		
		return borderPanel;
	}
	
	private JPanel createTopPulse() {
		JPanel borderPanel = JComponentFactory.createJFirePanel(this,"pulse");
		
		JPanel splitPanel = new JPanel();
		splitPanel.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
		borderPanel.add(splitPanel);
		
		JPanel pulsePanel = new JPanel();
		pulsePanel.setLayout(new SpringLayout());
		splitPanel.add(pulsePanel);
		
		pulsePanel.add(new JCommandLabel(CommandName.pulse_mode));
		pulsePanel.add(new JCommandComboBox(CommandName.pulse_mode));
		pulsePanel.add(new JCommandLabel(CommandName.pulse_dir));
		pulsePanel.add(new JCommandComboBox(CommandName.pulse_dir));
		pulsePanel.add(new JCommandLabel(CommandName.pulse_trig));
		pulsePanel.add(new JCommandComboBox(CommandName.pulse_trig));
		pulsePanel.add(new JCommandLabel(CommandName.pulse_bank));
		pulsePanel.add(new JCommandComboBox(CommandName.pulse_bank));
		SpringLayoutGrid.makeCompactGrid(pulsePanel,2,4);
				
		return borderPanel;
	}
	
	@Override
	public void settingUpdated(PulseFireUISettingKeys key, String value) {
		Command cmd = PulseFireUI.getInstance().getDeviceData().getDeviceParameter(CommandName.pulse_steps);
		commandReceived(cmd); // recheck channels
	}
}

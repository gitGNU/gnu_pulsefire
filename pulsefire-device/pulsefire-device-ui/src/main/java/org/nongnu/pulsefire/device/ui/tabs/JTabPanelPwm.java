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
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.nongnu.pulsefire.device.io.protocol.Command;
import org.nongnu.pulsefire.device.io.protocol.CommandName;
import org.nongnu.pulsefire.device.io.protocol.WirePulseMode;
import org.nongnu.pulsefire.device.io.transport.DeviceCommandListener;
import org.nongnu.pulsefire.device.ui.JComponentFactory;
import org.nongnu.pulsefire.device.ui.PulseFireUI;
import org.nongnu.pulsefire.device.ui.SpringLayoutGrid;
import org.nongnu.pulsefire.device.ui.components.JCommandCheckBox;
import org.nongnu.pulsefire.device.ui.components.JCommandComboBox;
import org.nongnu.pulsefire.device.ui.components.JCommandDial;
import org.nongnu.pulsefire.device.ui.components.JCommandLabel;
import org.nongnu.pulsefire.device.ui.components.JFireBorderChild;
import org.nongnu.pulsefire.device.ui.components.JCommandPwmInfo;

/**
 * JTabPanelPwm
 * 
 * @author Willem Cazander
 */
public class JTabPanelPwm extends AbstractFireTabPanel  {

	private final CardLayout cardLayout;
	private final JPanel cardPanel;
	
	public JTabPanelPwm() {
		cardLayout = new CardLayout();
		cardPanel = new JPanel(cardLayout);
		
		PulseFireUI.getInstance().getDeviceManager().addDeviceCommandListener(CommandName.pulse_mode, new DeviceCommandListener() {
			@Override
			public void commandReceived(Command command) {
				int modeNumber = new Integer(command.getArgu0());
				WirePulseMode mode = WirePulseMode.values()[modeNumber];
				cardLayout.show(cardPanel, mode.name());
			}
		});
		
		build(
			createCompactGrid(1, 2, 0,0,0,0,
				createJPanelWrap(
					createCompactGrid(4, 1,0,0,6,6,
						createPulseConfig(),
						createCompactGrid(1, 2, 0,0,0,0, createPulseDelay(),createTopPWM()),
						createCompactGrid(1, 2, 0,0,0,0,
							createBitABFirePanel("mask",CommandName.pulse_mask_a,CommandName.pulse_mask_b),
							createBitABFirePanel("inv",CommandName.pulse_inv_a,CommandName.pulse_inv_b)
						),
						createTopFreq()
					)
				),
				createCardContent()
			)
		);
	}
	
	private JPanel createJPanelWrap(JComponent...comps) {
		JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT));
		for (JComponent comp:comps) {
			wrap.add(comp);
		}
		return wrap;
	}
	
	
	protected JPanel createTabSidePane() {
		JPanel result = new JPanel();
		result.setLayout(new GridLayout(1,1,0,0));
		result.add(new JCommandPwmInfo());
		return result;
	}
	
	JComponent createCardContent() {
		
		cardPanel.add(createCardContentTrain(),WirePulseMode.TRAIN.name());
		cardPanel.add(createCardContentFlash(),WirePulseMode.FLASH.name());
		cardPanel.add(createCardContentFlashZero(),WirePulseMode.FLASH_ZERO.name());
		cardPanel.add(createCardContentPPM(),WirePulseMode.PPM.name());
		cardPanel.add(new JPanel(),WirePulseMode.OFF.name());
		
		JPanel result = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
		result.add(cardPanel);
		return result;
	}
	
	JComponent createCardContentTrain() {
		return createPWMChannels("OUT",true,false,CommandName.pulse_steps);
	}
	
	JComponent createCardContentFlash() {
		return createPWMChannels("STEP",false,false,null);
	}
	
	JComponent createCardContentFlashZero() {
		return createPWMChannels("STEP",false,true,null);
	}
	
	JComponent createCardContentPPM() {
		return createCompactGrid(2, 1, 0,0,0,0,
			createPWMChannels("STEP",false,false,CommandName.ppm_data_len),
			createCompactGrid(1, 3,
				createPPMCheckMatrix("ppmA",CommandName.ppm_data_a),
				createPPMCheckMatrix("ppmB",CommandName.ppm_data_b),
				createTopPPM()
			)
		);
	}
	
	JComponent createPPMCheckMatrix(String fireName,CommandName cmdName) {
		JPanel result = new JPanel();
		result.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
		result.setLayout(new GridLayout(16,0));
		for (int i=0;i<16;i++) {
			result.add(new JLabel(""+i));
			for (int p=0;p<16;p++) {
				JCheckBox box = new JCommandCheckBox(cmdName,p,i);
				box.putClientProperty("JComponent.sizeVariant", "mini");
				result.add(box);
			}
		}
		return createFlowLeftFirePanel(fireName,result);
	}
	
	private JPanel createPWMChannels(String prefix,boolean addInit,boolean limitToOne,CommandName autoLimitCommand) {
		JPanel row0 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		for (int i=0;i<16;i++) {
			final int outI = i;
			final JPanel out = JComponentFactory.createJFirePanel(prefix+i);
			out.setLayout(new BoxLayout(out, BoxLayout.PAGE_AXIS));
			
			if (addInit) {
				JPanel boxPanel = new JPanel();
				boxPanel.setLayout(new SpringLayout());
				
				JCheckBox boxInitA = new JCommandCheckBox(CommandName.pulse_init_a,i);
				boxInitA.putClientProperty("JComponent.sizeVariant", "mini");
				boxPanel.add(boxInitA);
				JLabel intiLabel = new JLabel("Init");
				boxPanel.add(intiLabel);
				JCheckBox boxInitB = new JCommandCheckBox(CommandName.pulse_init_b,i);
				boxInitB.putClientProperty("JComponent.sizeVariant", "mini");
				boxPanel.add(boxInitB);
				
				SpringLayoutGrid.makeCompactGrid(boxPanel,1,3,0,0,0,0);
				out.add(boxPanel);
			}
			
			JPanel dialPanel = new JPanel();
			if (addInit) {
				dialPanel.setBorder(new JFireBorderChild(out));
			}
			dialPanel.setLayout(new SpringLayout());
			
			dialPanel.add(new JCommandDial(CommandName.pwm_on_cnt_a,i));
			dialPanel.add(new JCommandDial(CommandName.pwm_on_cnt_b,i));
			dialPanel.add(new JCommandDial(CommandName.pwm_off_cnt_a,i));
			dialPanel.add(new JCommandDial(CommandName.pwm_off_cnt_b,i));
			
			SpringLayoutGrid.makeCompactGrid(dialPanel,2,2,0,0,0,0);
			out.add(dialPanel);
			
			if (autoLimitCommand!=null) {
				PulseFireUI.getInstance().getDeviceManager().addDeviceCommandListener(autoLimitCommand, new DeviceCommandListener() {
					@Override
					public void commandReceived(Command command) {
						if (command.getArgu0()==null) {
							return;
						}
						int value = Integer.parseInt(command.getArgu0());
						out.setVisible(value > outI);
					}
				});
			}
			
			
			if (i<8) {
				row0.add(out);
			} else {
				row1.add(out);
			}
			if (limitToOne) {
				break;
			}
		}
		
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout(0,0));
		centerPanel.add(row0,BorderLayout.CENTER);
		centerPanel.add(row1,BorderLayout.PAGE_END);
		
		JPanel resultPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
		resultPanel.add(centerPanel);
		return resultPanel;
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
	
	JComponent createPulseDelay() {
		return
			createFlowLeftFirePanel("delay",
				createCompactGrid(1, 2,
					createCompactGrid(1, 2,
						createCommandDial(CommandName.pulse_pre_delay),
						createCommandDial(CommandName.pulse_post_delay)
					),
					createLabeledGrid(3, 1,
						createCommandComboBoxLabelGrid(CommandName.pulse_pre_mul),
						createCommandComboBoxLabelGrid(CommandName.pulse_post_mul),
						createCommandComboBoxLabelGrid(CommandName.pulse_post_hold)
					)
				)
			);
	}
	
	JComponent createPulseConfig() {
		return createFlowLeftFirePanel("pulse",createLabeledGrid(2, 3,
			createCommandComboBoxLabelGrid(CommandName.pulse_mode),
			createCommandComboBoxLabelGrid(CommandName.pulse_dir),
			createCommandComboBoxLabelGrid(CommandName.pulse_trig),
			createCommandComboBoxLabelGrid(CommandName.pulse_bank),
			createCommandComboBoxLabelGrid(CommandName.pulse_steps),
			createCommandCheckBoxLabelGrid(CommandName.pulse_enable)
		));
	}
	
	JComponent createBitABFirePanel(String fireName,CommandName cmdA,CommandName cmdB) {
		JPanel result = new JPanel();
		result.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
		result.setLayout(new GridLayout(3,0));
		result.add(new JLabel("A"));
		for (int p=0;p<16;p++) {
			JCheckBox box = new JCommandCheckBox(cmdA,p);
			box.putClientProperty("JComponent.sizeVariant", "mini");
			result.add(box);
		}
		result.add(new JLabel());
		for (int p=0;p<16;p++) {
			String labelText = ""+p;
			if (labelText.length()==1) {
				labelText = "0"+labelText;
			}
			JLabel label = new JLabel(labelText);
			label.setFont(label.getFont().deriveFont(10.0f));
			result.add(label);
		}
		result.add(new JLabel("B"));
		for (int p=0;p<16;p++) {
			JCheckBox box = new JCommandCheckBox(cmdB,p);
			box.putClientProperty("JComponent.sizeVariant", "mini");
			result.add(box);
		}
		
		JPanel panel = JComponentFactory.createJFirePanel(this, fireName);
		panel.add(result);
		return panel;
	}
}

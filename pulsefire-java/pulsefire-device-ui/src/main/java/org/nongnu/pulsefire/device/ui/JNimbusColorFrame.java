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
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * JNimbusColorFrame
 * 
 * @author Willem Cazander
 */
public class JNimbusColorFrame extends JFrame {

	private static final long serialVersionUID = 5416437245805376356L;
	private Logger logger = null;
	private JFrame[] colorFrames = null;
	private static List<String> NIMBUS_PRIMARY_COLORS = Arrays.asList(
			"text", "control", "nimbusBase", "nimbusOrange", "nimbusGreen", "nimbusRed", "nimbusInfoBlue",
			"nimbusAlertYellow", "nimbusFocus", "nimbusSelectedText", "nimbusSelectionBackground",
			"nimbusDisabledText", "nimbusLightBackground", "info");
	private static List<String> NIMBUS_SECONDARY_COLORS = Arrays.asList(
			"textForeground", "textBackground", "background",
			"nimbusBlueGrey", "nimbusBorder", "nimbusSelection", "infoText", "menuText", "menu", "scrollbar",
			"controlText", "controlHighlight", "controlLHighlight", "controlShadow", "controlDkShadow", "textHighlight",
			"textHighlightText", "textInactiveText", "desktop", "activeCaption", "inactiveCaption");
	private List<ColorModel> models = new ArrayList<ColorModel>(100);

	public JNimbusColorFrame(JFrame mainFrame) {
		logger = Logger.getLogger(JNimbusColorFrame.class.getName());
		colorFrames = new JFrame[2];
		colorFrames[0] = this;
		colorFrames[1] = mainFrame;
		setTitle("PulseFire Colors");
		setMinimumSize(new Dimension(600,400));
		setSize(getMinimumSize());
		setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		
		JPanel main = new JPanel();
		main.setLayout(new BorderLayout());
		main.add(createTop(), BorderLayout.PAGE_START);
		main.add(createCenter(), BorderLayout.CENTER);
		
		JScrollPane scroll = new JScrollPane(main);
		add(scroll);
	}
	
	public JPanel createTop() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder("Core Colors"));
		panel.setLayout(new SpringLayout());
		for (String colorName:NIMBUS_PRIMARY_COLORS) {
			addColorSetting(panel,colorName,true);	
		}
		SpringLayoutGrid.makeCompactGrid(panel,NIMBUS_PRIMARY_COLORS.size(),7);
		return panel;
	}
	
	public JPanel createCenter() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder("Colors"));
		panel.setLayout(new SpringLayout());
		for (String colorName:NIMBUS_SECONDARY_COLORS) {
			addColorSetting(panel,colorName,false);	
		}
		SpringLayoutGrid.makeCompactGrid(panel,NIMBUS_SECONDARY_COLORS.size(),7);
		return panel;
	}
	
	private void addColorSetting(JPanel panel,String colorName,boolean isPrimary) {

		final ColorSetting cs = new ColorSetting();
		cs.colorName=colorName;
		
		panel.add(new JLabel(colorName));
		
		JButton colorLabel = new JButton("   ") {
			private static final long serialVersionUID = -1022540334405897683L;
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor((Color)getClientProperty("showColor"));
				g.fillRect(0, 0, getSize().width, getSize().height);
			}
			
		};
		colorLabel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Color c = (Color)((JButton)e.getSource()).getClientProperty("showColor");
				Color cNew = JColorChooser.showDialog(((Component) e.getSource()).getParent(), "Choose Color", c);
				if (cNew==null) {
					return; // cancel
				}
				((JButton)e.getSource()).putClientProperty("showColor", cNew);
				((JButton)e.getSource()).repaint();
				cs.setColor(cNew);
				for (ColorModel cm:models) {
					cm.dataChanged();
				}
			}
		});
		colorLabel.putClientProperty("showColor", cs.getColor());
		colorLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		panel.add(colorLabel);
		cs.preview=colorLabel;
		for (int i=0;i<4;i++) {
			JSpinner spinner = new JSpinner(new ColorModel(cs,i));
			spinner.setEditor(new JSpinner.NumberEditor(spinner));
			panel.add(spinner);
		}
		
		JLabel hexLabel = new JLabel();
		cs.hexLabel=hexLabel;
		panel.add(hexLabel);
		cs.setHexLabel();
	}
	
	class ColorModel extends SpinnerNumberModel {
		private static final long serialVersionUID = -8007038375574192155L;
		ColorSetting colorSetting = null;
		int t = 0;
		public ColorModel(ColorSetting colorSetting,int t) {
			this.colorSetting=colorSetting;
			this.t=t;
			models.add(this);
		}
		public void dataChanged() {
			fireStateChanged();
		}
		
		@Override
		public Object getNextValue() {
			int v = (Integer)getValue();
			if (v==254) {
				return null;
			}
			v++;
			return v;
		}

		@Override
		public Object getPreviousValue() {
			int v = (Integer)getValue();
			if (v==0) {
				return null;
			}
			v--;
			return v;
		}

		@Override
		public Object getValue() {
			Color c = colorSetting.getColor();
			switch (t) {
			default:
			case 0: return c.getRed();
			case 1: return c.getGreen();
			case 2: return c.getBlue();
			case 3: return c.getAlpha();
			}
		}

		@Override
		public void setValue(Object value) {
			int v = (Integer)value;
			Color c = colorSetting.getColor();
			Color cNew = null;
			switch (t) {
			default:
			case 0: cNew = new Color(v,c.getGreen(),c.getBlue(),c.getAlpha());break;
			case 1: cNew = new Color(c.getRed(),v,c.getBlue(),c.getAlpha());break;
			case 2: cNew = new Color(c.getRed(),c.getGreen(),v,c.getAlpha());break;
			case 3: cNew = new Color(c.getRed(),c.getGreen(),c.getBlue(),v);break;
			}
			colorSetting.setColor(cNew);
			colorSetting.preview.putClientProperty("showColor", cNew);
			colorSetting.preview.repaint();
			colorSetting.setHexLabel();
			fireStateChanged();
		}
		
		
	}
	
	class ColorSetting {
		String colorName = null;
		JLabel hexLabel = null;
		JButton preview = null;
		ColorModel colorModel = null;
		
		public void setHexLabel() {
			Color c = getColor();
			String hexRed = Integer.toHexString(c.getRed());
			if (hexRed.length()==1) {
				hexRed = "0"+hexRed;
			}
			String hexGreen = Integer.toHexString(c.getGreen());
			if (hexGreen.length()==1) {
				hexGreen = "0"+hexGreen;
			}
			String hexBlue = Integer.toHexString(c.getBlue());
			if (hexBlue.length()==1) {
				hexBlue = "0"+hexBlue;
			}
			String hexColor = "#"+hexRed+hexGreen+hexBlue;
			hexLabel.setText(hexColor.toUpperCase());
		}
		public Color getColor() {
			return UIManager.getColor(colorName);
		}
		public void setColor(Color c) {
			for (JFrame f:colorFrames) {
				f.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			}
			UIManager.put(colorName, c);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					for (JFrame frame:colorFrames) {
						SwingUtilities.updateComponentTreeUI(frame);
					}
					try {
						UIManager.setLookAndFeel(UIManager.getLookAndFeel().getClass().getName());
					} catch (Exception lafException) {
						logger.log(Level.WARNING,lafException.getMessage(),lafException);
					} finally {
						for (JFrame f:colorFrames) {
							f.setCursor(Cursor.getDefaultCursor());
						}
					}
				}
			});
		}
	}
}

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

package org.nongnu.pulsefire.device.ui.components;

import java.awt.BasicStroke;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.EventListener;
import java.util.EventObject;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.UIManager;

/**
 * JFireDial is a swing component to dial the value in with the mouse.
 * 
 * @author Willem Cazander
 */
public class JFireDial extends JComponent  {

	private static final long serialVersionUID = 7895789282149616239L;
	private boolean mouseDialing = false;
	private boolean entered = false;
	private String text = null;
	private int value = 0;
	private int valueOld = 0;
	private int valueMin = 0;
	private int valueMax = 0;
	private int radiusSize = 0;
	private int dotIndex = -1;
	private int spinStartX = 0;
	private int spinStartY = 0;
	private int spinStartValue = 0;
	static private Popup globalPopup = null; 
	
	public JFireDial() {
		this(0,100,0);
	}
	
	public JFireDial(int minValue, int maxValue, int value) {
		setMinimum(minValue);
		setMaximum(maxValue);
		setValue(value);
		addMouseMotionListener(new MouseMotionAdapter(  ) {
			public void mouseDragged(MouseEvent e) {
				spin(e);
			}
		});
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && !e.isConsumed()) {
					e.consume();
					showPopup(e);
				}
			}
			@Override
			public void mousePressed(MouseEvent e) {
				if (globalPopup!=null) {
					globalPopup.hide();
					globalPopup = null;
				}
				spinStartValue = getValue();
				spinStartX = e.getX();
				spinStartY = e.getY();
				mouseDialing = true;
				spin(e);
				repaint();
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				mouseDialing = false;
				repaint();
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
				entered = true;
				repaint();
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
				entered = false;
				repaint();
			}
		});
	}

	private void showPopup(MouseEvent e) {

		if (globalPopup!=null) {
			globalPopup.hide();
			globalPopup = null;
		}
		
		JPanel PopUpPanel = new JPanel();
		PopUpPanel.setPreferredSize(new Dimension(80,30));
		PopUpPanel.setLayout(new BoxLayout(PopUpPanel,BoxLayout.Y_AXIS));
		PopupFactory factory = PopupFactory.getSharedInstance();

		globalPopup = factory.getPopup( ((JFireDial)e.getSource()).getRootPane(),PopUpPanel,e.getXOnScreen()-10,e.getYOnScreen()-10);
		JIntegerTextField textField = new JIntegerTextField(getValue(),10);
		textField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				globalPopup.hide();
				globalPopup = null;
				setValue(((JIntegerTextField)e.getSource()).getValue());
			}
		});
		PopUpPanel.add(textField);
		globalPopup.show();
		textField.requestFocus();
	}
	
	protected void spin(MouseEvent e) {
		if (isEnabled()==false) {
			return;
		}
		
		// Calc new value based on mouse x/y
		double valueNewTh = Math.atan( ((1.0*e.getY()-radiusSize)/(e.getX()-radiusSize)) );
		int valueNewOffset=(int)(valueNewTh/(2*Math.PI)*(valueMax-valueMin));
		int valueNew = 0;
		if (e.getX() < radiusSize) {
			valueNew = valueNewOffset + (valueMax-valueMin)/2 + valueMin;
		} else if (e.getY() < radiusSize) {
			valueNew = valueNewOffset + valueMax;
		} else {
			valueNew = valueNewOffset + valueMin;
		}
		
		// Calc start value based on spin start x/y
		double valueStartTh = Math.atan( ((1.0*spinStartY-radiusSize)/(spinStartX-radiusSize)) );
		int valueStartOffset=(int)(valueStartTh/(2*Math.PI)*(valueMax-valueMin));
		int valueStart = 0;
		if (spinStartX < radiusSize) {
			valueStart = valueStartOffset + (valueMax-valueMin)/2 + valueMin;
		} else if (spinStartY < radiusSize) {
			valueStart = valueStartOffset + valueMax;
		} else {
			valueStart = valueStartOffset + valueMin;
		}
		
		// Calc and set relative spin value
		int valueChange = spinStartValue-valueStart;
		int valueNewRelative = valueNew + valueChange;
		if (valueNewRelative < 0) {
			valueNewRelative = valueMax + valueNewRelative;
		} else if (valueNewRelative > valueMax) {
			valueNewRelative = valueNewRelative - valueMax;
		}
		setValue ( valueNewRelative );
	}

	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		
		// Calc circle values
		int h = getSize().height;
		int h2 = h/2;
		int w = getSize().width;
		int w2 = w/2;
		radiusSize = (Math.min(w,h)/2)-1;
		int radiusInner = Math.min(w2,h2)-1;
		double th = value*(2*Math.PI)/(valueMax-valueMin)+(Math.PI/2);
		
		// Draw knob value line of dial
		if (isEnabled()) {
			if (entered | mouseDialing) {
				g2.setColor(UIManager.getColor("nimbusOrange"));
			} else {
				g2.setColor(getForeground());
			}
		} else {
			g2.setColor(UIManager.getColor("nimbusDisabledText"));
		}
		g2.setStroke(new BasicStroke(2));
		int xI = (int)(Math.cos(th)*(radiusSize/4));
		int yI = (int)(Math.sin(th)*(radiusSize/4));
		int xV = (int)(Math.cos(th)*(radiusSize));
		int yV = (int)(Math.sin(th)*(radiusSize));
		g2.drawLine(xI+radiusInner,yI+radiusInner,xV+radiusInner,yV+radiusInner);
		
		// Select color and draw dial circle
		if (isEnabled()) {
			if (mouseDialing) {
				g2.setColor(UIManager.getColor("nimbusOrange"));
			} else {
				if (entered) {
					g2.setColor(UIManager.getColor("nimbusFocus"));
				} else {
					g2.setColor(UIManager.getColor("controlShadow"));
				}
			}
		} else {
			g2.setColor(UIManager.getColor("controlDkShadow"));
		}
		g2.setStroke(new BasicStroke(2));
		g2.drawArc(1,1,radiusSize*2, radiusSize*2, 45, 180);
		g2.drawArc(1,1,radiusSize*2, radiusSize*2, 225, 180);
		
		// Draw background of text value
		g2.setColor(UIManager.getColor("controlDkShadow"));
		g2.fillRect(2, h-16, w-2, h+16);
		
		// Draw value as text
		if (isEnabled()) {
			g2.setColor(getForeground());
		} else {
			g2.setColor(UIManager.getColor("nimbusDisabledText"));
		}
		g2.setFont(UIManager.getFont("FireDial.font"));
		if (entered && isEnabled() && !mouseDialing && text!=null) {
			g2.setColor(UIManager.getColor("nimbusOrange"));
			g2.drawString(text, 4, h-4);
		} else {
			String valueStr = ""+getValue();
			if (dotIndex>0) {
				if (valueStr.length()>dotIndex) {
					int idx = valueStr.length()-dotIndex;
					String dotValue = valueStr.substring(idx,valueStr.length());
					if (dotValue.length()==1) {
						dotValue = "0"+dotValue;
					}
					valueStr = valueStr.substring(0,idx)+"."+dotValue;
				} else {
					valueStr = "0."+valueStr;
				}
			}
			g2.drawString(valueStr, 4, h-4);
		}
	}

	public Dimension getPreferredSize() {
		return new Dimension(36, 36+20);
	}

	public boolean isMouseDialing() {
		return mouseDialing;
	}
	
	public void setValue( int valueSet ) {
		value = valueSet - valueMin;
		if (value==valueOld) {
			return;
		}
		valueOld = value;
		repaint();
		fireDialEvent();
	}
	
	public int getValue() {
		return value+valueMin;
	}
	public void setMinimum(int minValue) {
		valueMin = minValue;
	}
	public int getMinimum() {
		return valueMin;
	}
	public void setMaximum(int maxValue) {
		valueMax = maxValue;
	}
	public int getMaximum() {
		return valueMax;
	}
	
	public void addDialListener(DialListener listener) {
		listenerList.add( DialListener.class, listener );
	}
	public void removeDialListener(DialListener listener) {
		listenerList.remove( DialListener.class, listener );
	}
	
	protected void fireDialEvent() {
		Object[] listeners = listenerList.getListenerList(); // comes in pairs
		for ( int i = 0; i < listeners.length; i += 2 ) {
			if ( listeners[i] == DialListener.class) {
				((DialListener)listeners[i+1]).dialAdjusted(new DialEvent(this, getValue()));
			}
		}
	}

	public class DialEvent extends EventObject {
		private static final long serialVersionUID = -2060054863081294219L;
		int value;
		protected DialEvent( JFireDial source, int value ) {
			super(source);
			this.value = value;
		}
		public int getValue() {
			return value;
		}
	}
	public interface DialListener extends EventListener {
		void dialAdjusted(DialEvent e);
	}
	
	/**
	 * @return the dotIndex
	 */
	public int getDotIndex() {
		return dotIndex;
	}

	/**
	 * @param dotIndex the dotIndex to set
	 */
	public void setDotIndex(int dotIndex) {
		this.dotIndex = dotIndex;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}
}

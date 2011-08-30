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
	private String name = null;
	private Popup popupContainer = null;
	private int value;
	private int valueOld;
	private int valueMin;
	private int valueMax;
	private int radiusSize;
	
	public JFireDial(String name) {
		this(name,0,100,0);
	}
	
	public JFireDial(String name,int minValue, int maxValue, int value) {
		this.name = name;
		setMinimum(minValue);
		setMaximum(maxValue);
		setValue(value);
		setToolTipText(name);
		addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {

				if (popupContainer!=null) {
					e.consume();
					popupContainer.hide();
					popupContainer = null;
					return;
				}
				
				if (e.getClickCount() == 2 && !e.isConsumed()) {
					e.consume();
					if (popupContainer!=null) {
						popupContainer.show();
					} else {
						JPanel PopUpPanel = new JPanel();
						PopUpPanel.setPreferredSize(new Dimension(80,30));
						PopUpPanel.setLayout(new BoxLayout(PopUpPanel,BoxLayout.Y_AXIS));
						PopupFactory factory = PopupFactory.getSharedInstance();
						popupContainer = factory.getPopup( ((JFireDial)e.getSource()).getRootPane(),PopUpPanel,e.getXOnScreen()-10,e.getYOnScreen()-10);
						JIntegerTextField textField = new JIntegerTextField(getValue(),10);
						textField.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								popupContainer.hide();
								setValue(((JIntegerTextField)e.getSource()).getValue());
							}
						});
						PopUpPanel.add(textField);
						popupContainer.show();
						textField.requestFocus();
					}
				}
			}
			@Override
			public void mousePressed(MouseEvent e) {
				mouseDialing = true;
				spin(e);
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
		addMouseMotionListener(new MouseMotionAdapter(  ) {
			public void mouseDragged(MouseEvent e) {
				spin(e);
			}
		});
	}

	protected void spin(MouseEvent e) {
		if (isEnabled()==false) {
			return;
		}
		int y = e.getY();
		int x = e.getX();
		if (y > getPreferredSize().width) {
			return; // looks stange but hight is offseted so width is radius
		}
		double th = Math.atan((1.0*y-radiusSize)/(x-radiusSize));
		int value=(int)(th/(2*Math.PI)*(valueMax-valueMin));
		if (x < radiusSize) {
			setValue(value+(valueMax-valueMin)/2+valueMin);
		} else if (y < radiusSize) {
			setValue( value + valueMax );
		} else {
			setValue( value + valueMin);
		}
	}

	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		
		int h = getSize().height;
		int h2 = h/2;
		int w = getSize().width;
		int w2 = w/2;
		radiusSize = (Math.min(w,h)/2)-1;
		int radiusInner = Math.min(w2,h2)-1;
		//int radiusOut = Math.min(w+1,h+1)/2;
		double th = value*(2*Math.PI)/(valueMax-valueMin)+(Math.PI/2);
		
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
		
		//g2.setStroke(new BasicStroke(1));
		//g2.setColor(Color.BLACK);
		//g2.drawArc(0,0,radiusOut*2, radiusOut*2, 45, 180);
		//g2.drawArc(0,0,radiusOut*2, radiusOut*2, 225, 180);
		
		// Select color of dial circle
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
		
		g2.setColor(UIManager.getColor("controlDkShadow"));
		g2.fillRect(2, h-16, w-2, h+16);
		
		if (isEnabled()) {
			g2.setColor(getForeground());
		} else {
			g2.setColor(UIManager.getColor("nimbusDisabledText"));
		}
		g2.setFont(UIManager.getFont("FireDial.font"));
		if (entered && isEnabled() && !mouseDialing) {
			g2.setColor(UIManager.getColor("nimbusOrange"));
			g2.drawString(name, 4, h-4);
		} else {
			g2.drawString(""+getValue(), 4, h-4);
		}

		//double deg = th*(180/Math.PI);
		//g2.fillArc(0,0,radius*2, radius*2,0,360-(int)deg);
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
}

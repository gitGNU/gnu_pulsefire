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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.Border;

/**
 * JFireBorder
 * 
 * @author Willem Cazander
 */
public class JFireBorder implements Border,MouseListener {

	private int radius;
	boolean entered = false;
	private JComponent comp = null;
	private String title;
	static private JFireBorder lastFireBorder = null;
	private GradientPaint gradientNormal = null;
	private GradientPaint gradientEntered = null;
	private int gradientWidth = 0;
	
	public JFireBorder(String title,JComponent comp) {
		this.radius = 10;
		this.comp=comp;
		this.title = title;
		comp.addMouseListener(this);
	}
	
	public Insets getBorderInsets(Component c) {
		 return new Insets(getTitleHeight(c)+1, 1, radius, 1);
	}

	public boolean isBorderOpaque() {
		return true;
	}


	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		int titleHeight = getTitleHeight(c);
		
		if (gradientWidth!=width) {
			gradientNormal = null;
			gradientEntered = null;
		}
		gradientWidth = width;
		
		Color endColor = UIManager.getColor("control");
		if (endColor==null) {
			endColor = c.getBackground();
		}
		Color startColor = UIManager.getColor("controlLHighlight");
		if (startColor==null) {
			startColor = c.getForeground();
		}
		
		//BufferedImage titleImage = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
		if (gradientNormal==null) {
			gradientNormal = new GradientPaint(
					0, 0, startColor,
					titleHeight*5, width/3,endColor,
					false);
		}
		if (gradientEntered==null) {
			startColor = UIManager.getColor("nimbusOrange");
			if (startColor==null) {
				startColor = c.getForeground();
			}
			gradientEntered = new GradientPaint(
					0, 0, startColor,
					titleHeight*5, width/3,endColor,
					false);
		}
		if (entered) {
			g2.setPaint(gradientEntered);
		} else {
			g2.setPaint(gradientNormal);
		}
		g2.fillRoundRect(x, y, width, titleHeight, radius, radius);
		g2.fillRect(x,titleHeight/2,radius+1, titleHeight/2);

		//g2.drawLine(x, titleHeight-1, width, titleHeight-1);
		g2.drawRoundRect(x,y,width-1,height-1,radius,radius);
		
		if (title==null) {
			return;
		}
		Font font = UIManager.getFont("TitledBorder.font");
		g2.setColor(c.getForeground());
		FontMetrics metrics = c.getFontMetrics(font);
		g2.setFont(font);
		g2.drawString(title,x+8,y+(titleHeight-metrics.getHeight())/2 +metrics.getAscent()); 
	}

	protected int getTitleHeight(Component c) {
		Font font = UIManager.getFont("TitledBorder.font");
		FontMetrics metrics = c.getFontMetrics(font);
		return (int)(metrics.getHeight() * 1.40);
	}

	public GradientPaint getGradient() {
		if (entered) {
			return gradientEntered;
		} else {
			return gradientNormal;
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		if (entered==true && lastFireBorder==this) {
			return;
		}
		entered = true;
		if (comp!=null) {
			comp.repaint();
			if (lastFireBorder!=null) {
				lastFireBorder.entered = false;
				lastFireBorder.comp.repaint();
			}
			lastFireBorder = this;
		}
	}
	@Override
	public void mouseExited(MouseEvent e) {
	}
	@Override
	public void mousePressed(MouseEvent e) {
	}
	@Override
	public void mouseReleased(MouseEvent e) {
	}
}

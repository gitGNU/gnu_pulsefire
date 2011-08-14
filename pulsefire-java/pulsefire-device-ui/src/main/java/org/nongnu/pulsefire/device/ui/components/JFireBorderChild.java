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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.border.Border;

/**
 * JFireBorderChild
 * 
 * @author Willem Cazander
 */
public class JFireBorderChild implements Border {
	
	private JFireBorder parentBorder = null;
	private int topBorderSize = 0;
	private int bottomBorderSize = 0;
	private int leftBorderSize = 0;

	public JFireBorderChild(JComponent parentHolder) {
		if (parentHolder==null) {
			throw new IllegalArgumentException("JComponent is null");
		}
		if ((parentHolder.getBorder() instanceof JFireBorder)==false) {
			throw new IllegalArgumentException("JComponent has not JFireBorder");
		}
		this.parentBorder=(JFireBorder)parentHolder.getBorder();
		this.topBorderSize = 1;
	}
	
	public JFireBorderChild(JFireBorder parentBorder) {
		this(parentBorder,1,0,0);
	}
	
	public JFireBorderChild(JFireBorder parentBorder,int topBorderSize,int bottomBorderSize,int leftBorderSize) {
		this.parentBorder=parentBorder;
		this.topBorderSize = topBorderSize;
		this.bottomBorderSize = bottomBorderSize;
		this.leftBorderSize=leftBorderSize;
	}
	
	public Insets getBorderInsets(Component c) {
		return new Insets(topBorderSize, 0, bottomBorderSize, 0);
	}

	public boolean isBorderOpaque() {
		return true;
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Graphics2D g2 = (Graphics2D)g;
		if (parentBorder==null) {
			return;
		}
		if (parentBorder.getGradient()==null) {
			return;
		}
		g2.setPaint(parentBorder.getGradient());
		if (topBorderSize > 0) {
			g2.fillRect(x,y,width,topBorderSize);
		}
		if (bottomBorderSize > 0) {
			g2.fillRect(x,height-bottomBorderSize,width,bottomBorderSize);
		}
		if (leftBorderSize > 0) {
			g2.fillRect(0,0,leftBorderSize,height);
		}
	}
}

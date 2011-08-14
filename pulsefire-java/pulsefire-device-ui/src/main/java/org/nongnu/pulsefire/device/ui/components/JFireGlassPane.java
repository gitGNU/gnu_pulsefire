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
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * JFireGlassPane
 * 
 * @author Willem Cazander
 */
public class JFireGlassPane extends JPanel implements MouseListener {

	private static final long serialVersionUID = 8377972503397815117L;
	private JFrame rootFrame = null;
	
	public JFireGlassPane(JFrame rootFrame) {
		this.rootFrame=rootFrame;
		rootFrame.setGlassPane(this);
		rootFrame.addMouseListener(this);
		setOpaque(false);
		setVisible(true);
	}
	
	private void redispatchMouseEvent(MouseEvent e) {
		Point glassPanePoint = e.getPoint();
		Container container = rootFrame.getContentPane(); //contentPane;
		Point containerPoint = SwingUtilities.convertPoint(this,glassPanePoint,container);


		if (e.getY()>getSize().width-40 && e.getX()<20) {
			
		}

		
		if (containerPoint.y < 0) {
			// not in the content pane
		} else {
			Component component = SwingUtilities.getDeepestComponentAt(container,containerPoint.x,containerPoint.y);
			if ((component != null)) {
				//Forward events over the check box.
				Point componentPoint = SwingUtilities.convertPoint(this,glassPanePoint,component);
				component.dispatchEvent(
						new MouseEvent(
								component,e.getID(),e.getWhen(),e.getModifiers(),
								componentPoint.x,componentPoint.y,e.getClickCount(),e.isPopupTrigger())
						);
			}
		}

		//Update the glass pane if requested.
		//if (repaint) {
		//	setPoint(glassPanePoint);
		//	repaint();
		//}
	}
	
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setColor(Color.red);
		
		int offset = 20;
		g2.fillOval(getSize().width-160,offset, 20,20);
		g2.fillOval(getSize().width-120,offset, 20,20);
		g2.fillOval(getSize().width-80, offset, 20,20);
		g2.fillOval(getSize().width-40, offset, 20,20);
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		redispatchMouseEvent(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		redispatchMouseEvent(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		redispatchMouseEvent(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		redispatchMouseEvent(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		redispatchMouseEvent(e);
	}
}

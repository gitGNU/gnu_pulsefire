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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.nongnu.pulsefire.device.DeviceWireManager;
import org.nongnu.pulsefire.device.ui.PulseFireUI;

/**
 * JConnectDialog
 * 
 * @author Willem Cazander
 */
public class JConnectDialog extends JDialog implements MouseListener {
	
	private static final long serialVersionUID = 3128697350365724499L;
	private JProgressBar bar = null;
	
	public JConnectDialog(JFrame parentFrame,String port) {
		super(parentFrame,"Connect",true);
		
		bar = new JProgressBar(0, 100);
		bar.setStringPainted(true);
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		topPanel.setBorder(BorderFactory.createEmptyBorder());
		JLabel cancelButton = new JLabel("cancel");
		cancelButton.addMouseListener(this);
		topPanel.add(new JLabel("Connecting....."));
		topPanel.add(cancelButton);
		
		add(BorderLayout.CENTER, bar);
		add(BorderLayout.NORTH, topPanel);
		
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setSize(300, 75);
		setLocationRelativeTo(parentFrame);
		setResizable(false);
		new UpdateProgressThread().start();
		new ConnectThread(port).start();
		setVisible(true);
	}
	
	class UpdateProgressThread extends Thread {
		@Override
		public void run() {
			DeviceWireManager sm = PulseFireUI.getInstance().getDeviceManager();
			// auto timeout
			for(int i=0;i<200;i++) {
				int progress = sm.getConnectProgress();
				if (progress < 100) {
					bar.setValue(progress);
					bar.setString(sm.getConnectPhase());
				} else {
					try {Thread.sleep(333);} catch (InterruptedException e) {}
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							setVisible(false);
							dispose();
						}
					});
					return; // we are done
				}
				try {Thread.sleep(100);} catch (InterruptedException e) {}
			}
			bar.setValue(100);
			bar.setString(sm.getConnectPhase());
			try {Thread.sleep(333);} catch (InterruptedException e) {}
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					setVisible(false);
					dispose();
				}
			});
		}
	}
	
	class ConnectThread extends Thread {
		
		private String port = null;
		public ConnectThread(String port) {
			this.port=port;
		}
		
		@Override
		public void run() {
			try {
				PulseFireUI.getInstance().getDeviceManager().connect(port);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	
	@Override
	public void mouseClicked(MouseEvent e) {
		PulseFireUI.getInstance().getDeviceManager().disconnect();
		bar.setString("Canceled");
		// remove dialog just a small bit later so SerialThread can do shutdown, this
		// is small guard to really fast connect/disconnect/connect/disconnect/connect cycle.
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try { Thread.sleep(333); } catch (InterruptedException e) { e.printStackTrace(); }
				setVisible(false);
				dispose();
			}
		});
	}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
}

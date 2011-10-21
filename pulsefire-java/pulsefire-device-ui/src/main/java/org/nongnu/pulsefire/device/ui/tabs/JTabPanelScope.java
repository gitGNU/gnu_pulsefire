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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.nongnu.pulsefire.device.ui.JComponentFactory;
import org.nongnu.pulsefire.device.ui.SpringLayoutGrid;
import org.nongnu.pulsefire.device.ui.components.JFireDial;
import org.nongnu.pulsefire.device.ui.components.JFireDial.DialEvent;
import org.nongnu.pulsefire.device.ui.components.JFireDial.DialListener;

/**
 * JTabPanelScope
 * 
 * @author Willem Cazander
 */
public class JTabPanelScope extends AbstractTabPanel implements ActionListener,DialListener {

	private static final long serialVersionUID = -6711428986888517858L;
	private JScopePanel scopePanel = null;
	private JButton startButton = null;
	private CaptureManager captureManager = null;
	private JFireDial gainDialA = null;
	private JFireDial gainDialB = null;
	private JFireDial timeDial = null;
	
	public JTabPanelScope() {
		captureManager = new CaptureManager();
		setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel wrap = new JPanel();
		wrap.setLayout(new SpringLayout());
		wrap.add(createScopeOptions());
		wrap.add(createScope());
		SpringLayoutGrid.makeCompactGrid(wrap,2,1);
		add(wrap);
	}
	
	private JPanel createScopeOptions() {
		JPanel inputPanel = JComponentFactory.createJFirePanel("Options");
		//inputPanel.setLayout(new SpringLayout());
				
		startButton = new JButton("Start");
		startButton.addActionListener(this);
		inputPanel.add(startButton);
		
		gainDialA = new JFireDial("gainB",-100,100,0);
		gainDialA.addDialListener(this);
		inputPanel.add(gainDialA);
		
		gainDialB = new JFireDial("gainA",-100,100,0);
		gainDialB.addDialListener(this);
		inputPanel.add(gainDialB);
		
		timeDial = new JFireDial("time",1,32768,32768);
		timeDial.addDialListener(this);
		inputPanel.add(timeDial);
		
		//SpringLayoutGrid.makeCompactGrid(inputPanel,5,2);
		return inputPanel;
	}
	
	private JPanel createScope() {
		JPanel panel = JComponentFactory.createJFirePanel("Scope");
		scopePanel = new JScopePanel();
		panel.add(scopePanel);
		return panel;
	}
	
	@Override
	public Class<?> getTabClassName() {
		return this.getClass();
	}
	
	
	public class ScopeDataChannel {
		
	}
	
	private List<Line2D> lines0 = Collections.synchronizedList(new ArrayList<Line2D>(15000));
	private List<Line2D> lines1 = Collections.synchronizedList(new ArrayList<Line2D>(15000));
	private AudioInputStream audioInputStream = null;
	
	private AudioFormat getAudioFormat() {
		float sampleRate = 44100.0F; //8000,11025,16000,22050,44100
		int sampleSizeInBits = 16; //8,16
		int channels = 2; // 1,2
		boolean signed = true;
		boolean bigEndian = false;
		return new AudioFormat(sampleRate,sampleSizeInBits,channels,signed,bigEndian);
	}
	
	class CaptureManager implements Runnable {
		volatile TargetDataLine line = null;
		volatile Thread thread = null;
		
		public void start() {
			thread = new Thread(this);
			thread.setName("Capture");
			thread.start();
		}

		public boolean isRunning() {
			return thread!=null;
		}
		
		public void stop() {
			thread = null;
		}
		
		private void shutDown(String message) {
			stop();
		}
		
		public void run() {
			AudioFormat format = getAudioFormat();
			DataLine.Info info = new DataLine.Info(TargetDataLine.class,format);
			if (!AudioSystem.isLineSupported(info)) {
				shutDown("Line matching "+info+" not supported.");
				return;
			}
			try {
				line = (TargetDataLine) AudioSystem.getLine(info);
				line.open(format, line.getBufferSize());
			} catch (LineUnavailableException ex) { 
				shutDown("Unable to open the line: "+ex);
				return;
			} catch (SecurityException ex) { 
				shutDown(ex.toString());
				return;
			} catch (Exception ex) { 
				shutDown(ex.toString());
				return;
			}

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int frameSizeInBytes = format.getFrameSize();
			int bufferLengthInFrames = line.getBufferSize() / 8;
			int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
			byte[] data = new byte[bufferLengthInBytes];
			int numBytesRead;
			
			line.start();
			while (thread != null) {
				if (line.available()<10000) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
					continue;
				}
				if((numBytesRead = line.read(data, 0, bufferLengthInBytes)) == -1) {
					break;
				}
				System.out.println("Readed bytes: "+numBytesRead);
				out.write(data, 0, numBytesRead);
				
				
				byte audioBytes[] = out.toByteArray();
				ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);

				audioInputStream = new AudioInputStream(bais, format, audioBytes.length / frameSizeInBytes);
				long milliseconds = (long)((audioInputStream.getFrameLength() * 1000) / format.getFrameRate());
				double duration = milliseconds / 1000.0;
				System.out.println("time: "+duration);

				try {
					audioInputStream.reset();
				} catch (Exception ex) { 
					ex.printStackTrace(); 
					return;
				}
				scopePanel.createWaveForm(audioBytes);
				out.reset();
			}

			// close resources
			try {
				line.stop();
				line.close();
				line = null;
				out.flush();
				out.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	class JScopePanel extends JPanel {
		private static final long serialVersionUID = -8942308841693048263L;

		public JScopePanel() {
			setPreferredSize(new Dimension(900,300));
		}
		
		public void paint(Graphics g) {
			Dimension d = getSize();
			int w = d.width;
			int h = d.height;

			Graphics2D g2 = (Graphics2D) g;
			g2.setBackground(getBackground());
			g2.clearRect(0, 0, w, h);

			g2.setColor(Color.YELLOW);
			for (int i=0;i<10;i++) {
				g2.drawLine(0, i*(h/10), w, i*(h/10));
			}
			g2.setColor(Color.YELLOW);
			for (int i=0;i<10;i++) {
				g2.drawLine(i*(w/10),0, i*(w/10),h);
			}
			
			g2.setColor(Color.BLUE);
			for (int i = 1; i < lines0.size(); i++) {
				g2.draw((Line2D) lines0.get(i));
			}
			g2.setColor(Color.CYAN);
			for (int i = 1; i < lines1.size(); i++) {
				g2.draw((Line2D) lines1.get(i));
			}
		}
		
		public void createWaveForm(byte[] audioBytes) {

			lines0.clear();
			lines1.clear();

			AudioFormat format = audioInputStream.getFormat();
			if (audioBytes==null) {
				try {
					audioBytes = new byte[(int) (audioInputStream.getFrameLength()*format.getFrameSize())];
					audioInputStream.read(audioBytes);
				} catch (Exception ex) { 
					ex.printStackTrace();
					return; 
				}
			}

			int[] audioData = null;
			if (format.getSampleSizeInBits() == 16) {
				int nlengthInSamples = audioBytes.length / 2;
				audioData = new int[nlengthInSamples];
				if (format.isBigEndian()) {
					for (int i = 0; i < nlengthInSamples; i++) {
						int MSB = (int) audioBytes[2*i];    /* First byte is MSB (high order) */
						int LSB = (int) audioBytes[2*i+1];  /* Second byte is LSB (low order) */
						audioData[i] = MSB << 8 | (255 & LSB);
					}
				} else {
					for (int i = 0; i < nlengthInSamples; i++) {
						int LSB = (int) audioBytes[2*i];   /* First byte is LSB (low order) */
						int MSB = (int) audioBytes[2*i+1]; /* Second byte is MSB (high order) */
						audioData[i] = MSB << 8 | (255 & LSB);
					}
				}
			} else if (format.getSampleSizeInBits() == 8) {
				int nlengthInSamples = audioBytes.length;
				audioData = new int[nlengthInSamples];
				if (format.getEncoding().toString().startsWith("PCM_SIGN")) {
					for (int i = 0; i < audioBytes.length; i++) {
						audioData[i] = audioBytes[i];
					}
				} else {
					for (int i = 0; i < audioBytes.length; i++) {
						audioData[i] = audioBytes[i] - 128;
					}
				}
			}

			Dimension d = getSize();
			int w = d.width;
			int h = d.height-15;
			int frames_per_pixel = audioBytes.length / format.getFrameSize()/w;
			byte my_byte0 = 0;
			byte my_byte1 = 0;
			double y_last0 = 0;
			double y_last1 = 0;
			int numChannels = format.getChannels();
			System.out.println("frames_per_pixel: "+frames_per_pixel);
			for (double x = 0; x < w && audioData != null; x++) {
				if (numChannels==1) {
					int idx = (int) (frames_per_pixel * numChannels * x);
					if (format.getSampleSizeInBits() == 8) {
						my_byte0 = (byte) audioData[idx];
					} else {
						my_byte0 = (byte) (128 * audioData[idx] / 32768 );
					}                
					double y_new = (double) (h * (128 - my_byte0) / 256);
					y_new += gainDialA.getValue();
					lines0.add(new Line2D.Double(x, y_last0, x, y_new));
					y_last0 = y_new;
				} else {
					int idx = (int) (frames_per_pixel * 2 * x);
					if (format.getSampleSizeInBits() == 8) {
						my_byte0 = (byte) audioData[idx];
						my_byte1 = (byte) audioData[idx+1];
					} else {
						my_byte0 = (byte) (128 * audioData[idx] / 32768 );
						my_byte1 = (byte) (128 * audioData[idx+1] / 32768 );
					}                
					double y_new0 = (double) (h * (128 - my_byte0) / 256);
					double y_new1 = (double) (h * (128 - my_byte1) / 256);
					
					y_new0 += gainDialA.getValue();
					lines0.add(new Line2D.Double(x, y_last0, x, y_new0));
					y_last0 = y_new0;
					
					y_new1 += gainDialB.getValue();
					lines1.add(new Line2D.Double(x, y_last1, x, y_new1));
					y_last1 = y_new1;
				}
			}
			repaint();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (captureManager.isRunning()) {
			captureManager.stop();
			startButton.setText("Start");
		} else {
			captureManager.start();
			startButton.setText("Stop");
		}
		
	}

	@Override
	public void dialAdjusted(DialEvent e) {
		// TODO Auto-generated method stub
		
	}
}

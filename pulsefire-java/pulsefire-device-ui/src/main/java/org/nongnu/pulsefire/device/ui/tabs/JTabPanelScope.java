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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Line2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ToolTipManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import org.nongnu.pulsefire.device.ui.JComponentFactory;
import org.nongnu.pulsefire.device.ui.PulseFireUI;
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
	private Logger logger = null;
	private JScopePanel scopePanel = null;
	private JButton startButton = null;
	private JButton channelsButton = null;
	private CaptureManager captureManager = null;
	private JFireDial gainDialA = null;
	private JFireDial gainDialB = null;
	private JFireDial timeDial = null;
	
	public JTabPanelScope() {
		logger = Logger.getLogger(JTabPanelScope.class.getName());
		captureManager = new CaptureManager();
		setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel wrap = new JPanel();
		//wrap.setLayout(new SpringLayout());
		//wrap.setLayout(new BorderLayout());
		wrap.add(createScope(),BorderLayout.CENTER);
		wrap.add(createScopeOptions(),BorderLayout.LINE_END);
		//SpringLayoutGrid.makeCompactGrid(wrap,1,2);
		add(wrap);
	}
	
	private JPanel createScopeOptions() {
		JPanel optionPanel = new JPanel();
		optionPanel.setLayout(new BoxLayout(optionPanel,BoxLayout.PAGE_AXIS));
		
		JPanel inputPanel = JComponentFactory.createJFirePanel("Options");
		//inputPanel.setLayout(new SpringLayout());
				
		startButton = new JButton("Start");
		startButton.addActionListener(this);
		inputPanel.add(startButton);

		channelsButton = new JButton("Channels");
		channelsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JAudioCardDialog audioDialog = new JAudioCardDialog(PulseFireUI.getInstance().getMainFrame());
				audioDialog.pack();
				audioDialog.setLocationRelativeTo(PulseFireUI.getInstance().getMainFrame());
				audioDialog.setVisible(true);
			}
		});
		inputPanel.add(channelsButton);
		
		gainDialA = new JFireDial("gainB",-100,100,0);
		gainDialA.addDialListener(this);
		inputPanel.add(gainDialA);
		
		gainDialB = new JFireDial("gainA",-100,100,0);
		gainDialB.addDialListener(this);
		inputPanel.add(gainDialB);
		
		timeDial = new JFireDial("time",1,32768,32768);
		timeDial.addDialListener(this);
		timeDial.setEnabled(false);
		inputPanel.add(timeDial);
		optionPanel.add(inputPanel);
		
		for (int i=0;i<3;i++) {
			JPanel channelPanel = JComponentFactory.createJFirePanel("Channel A");
			JFireDial gainChannel = new JFireDial("gainA",-100,100,0);
			gainChannel.setEnabled(false);
			channelPanel.add(gainChannel);
			optionPanel.add(channelPanel);
		}
		
		//SpringLayoutGrid.makeCompactGrid(inputPanel,5,2);
		return optionPanel;
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
			logger.info("Starting capture thread.");
		}

		public boolean isRunning() {
			return thread!=null;
		}
		
		public void stop() {
			logger.info("Stopping capture thread.");
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
				out.write(data, 0, numBytesRead);
				
				
				byte audioBytes[] = out.toByteArray();
				ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);

				audioInputStream = new AudioInputStream(bais, format, audioBytes.length / frameSizeInBytes);
				//long milliseconds = (long)((audioInputStream.getFrameLength() * 1000) / format.getFrameRate());
				//double duration = milliseconds / 1000.0;

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
			setPreferredSize(new Dimension(600,300));
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
			g2.drawLine(0,h-1,w-1,h-1);
			g2.drawLine(w-1,0,w-1,h-1);
			
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
	
	public class JAudioCardDialog extends JDialog implements ActionListener {
		
		private static final long serialVersionUID = 5741561145725436759L;
		private JTable table = null;
		private AudioCardTableModel tableModel = null;
		
		public JAudioCardDialog(Frame aFrame) {
			super(aFrame, true);
			setTitle("Audio");
			
			JPanel mainPanel = new JPanel();
			//mainPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
			mainPanel.setLayout(new BorderLayout());
			
			tableModel = new AudioCardTableModel();
			table = new JTable(tableModel);
			table.getTableHeader().setReorderingAllowed(false);
			table.getTableHeader().setResizingAllowed(true);
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			table.setFillsViewportHeight(true);
			table.setShowHorizontalLines(true);
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			//table.getSelectionModel().addListSelectionListener(this);
			table.setRowMargin(2);
			table.setRowHeight(26);
			TableColumn nameColumn = table.getColumnModel().getColumn(0);
			nameColumn.setPreferredWidth(150);
			TableColumn speedColumn = table.getColumnModel().getColumn(1);
			speedColumn.setPreferredWidth(130);
			ToolTipManager.sharedInstance().unregisterComponent(table);
			ToolTipManager.sharedInstance().unregisterComponent(table.getTableHeader());

			JScrollPane scroll = new JScrollPane(table);
			mainPanel.add(scroll,BorderLayout.CENTER);
			
			
			getContentPane().add(mainPanel);		
			setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent we) {
					setVisible(false);
				}
			});
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
		}
		
		class MixerInfo {
			Mixer.Info mixerInfo = null;
			List<Line.Info> data = new ArrayList<Line.Info>(10);
		}
		
		public class AudioCardTableModel extends AbstractTableModel {

			private static final long serialVersionUID = -1432038909521987705L;
			private List<MixerInfo> data = null;
			private String[] columnNames = new String[] {"name","description","vendor"};
			
			public AudioCardTableModel() {
				data = new ArrayList<MixerInfo>(10);
				for (Mixer.Info mixerInfo:AudioSystem.getMixerInfo()) {
					Mixer mixer = AudioSystem.getMixer(mixerInfo);
					try {
						mixer.open();
						MixerInfo mi = new MixerInfo();
						mi.mixerInfo=mixerInfo;
						for (Line.Info lineInfo:mixer.getSourceLineInfo()) {
							mi.data.add(lineInfo);
						}
						if (mi.data.isEmpty()) {
							continue;
						}
						data.add(mi);
					} catch (LineUnavailableException e) {
						e.printStackTrace();
					}
				}
			}
			
			@Override
			public int getRowCount() {
				return data.size();
			}
			
			@Override
			public int getColumnCount() {
				return columnNames.length;
			}
			
			public String getColumnName(int col) {
				return columnNames[col];
			}
			
			@Override
			public Object getValueAt(int row, int col) {
				MixerInfo mi = data.get(row);
				switch (col) {
				default:
				case 0:		return mi.mixerInfo.getName();
				case 1:		return mi.mixerInfo.getDescription();
				case 2:		return mi.data.size();

				}
			}
			

		}
	}
}

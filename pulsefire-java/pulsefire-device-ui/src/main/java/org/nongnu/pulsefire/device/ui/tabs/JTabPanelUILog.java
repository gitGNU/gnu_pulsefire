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

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.Handler;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

import org.nongnu.pulsefire.device.ui.JComponentFactory;
import org.nongnu.pulsefire.device.ui.PatternLogFormatter;
import org.nongnu.pulsefire.device.ui.PulseFireUI;
import org.nongnu.pulsefire.device.ui.PulseFireUISettingKeys;
import org.nongnu.pulsefire.device.ui.PulseFireUISettingListener;
import org.nongnu.pulsefire.device.ui.SpringLayoutGrid;

/**
 * JTabPanelUILog
 * 
 * @author Willem Cazander
 */
public class JTabPanelUILog extends AbstractFireTabPanel implements ActionListener,PulseFireUISettingListener {

	private static final long serialVersionUID = 4858978467459212054L;
	private UILogHandler logHandler = null;
	private JButton clearButton = null;
	private JComboBox levelBox = null;
	private JTextArea logTextArea = null;
	private JCheckBox autoScrollBox = null;
	private int logLinesMax = 255;
	
	public JTabPanelUILog() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel wrap = new JPanel();
		wrap.setLayout(new SpringLayout());
		wrap.add(createHeader());
		wrap.add(createEditor());
		SpringLayoutGrid.makeCompactGrid(wrap,2,1);
		add(wrap);

		Logger rootLogger = Logger.getAnonymousLogger();
		while (rootLogger.getParent()!=null) {
			rootLogger = rootLogger.getParent();
		}
		
		logHandler = new UILogHandler();
		logHandler.setFormatter(new PatternLogFormatter());
		rootLogger.addHandler(logHandler);
		
		logLinesMax = new Integer(PulseFireUI.getInstance().getSettingsManager().getSettingString(PulseFireUISettingKeys.CONSOLE_LINES));
		PulseFireUI.getInstance().getSettingsManager().addSettingListener(PulseFireUISettingKeys.CONSOLE_LINES,this); // we reuse this setting for now
	}
	
	/**
	 * This needs release if playing the the this tab add/removal very multiple times.
	 */
	@Override
	public void release() {
		PulseFireUI.getInstance().getSettingsManager().removeSettingListener(PulseFireUISettingKeys.CONSOLE_LINES,this);
		Logger rootLogger = Logger.getAnonymousLogger();
		while (rootLogger.getParent()!=null) {
			rootLogger = rootLogger.getParent();
		}
		rootLogger.removeHandler(logHandler);
	}
	
	private JPanel createHeader() {	
		JPanel result = JComponentFactory.createJFirePanel("Options");
		result.setLayout(new FlowLayout(FlowLayout.LEFT));
		result.add(new JLabel("Log Level"));
		levelBox = new JComboBox(new Level[] {Level.OFF,Level.SEVERE,Level.WARNING,Level.INFO,Level.FINE,Level.FINER,Level.FINEST,Level.ALL});
		levelBox.setSelectedItem(Level.INFO);
		levelBox.addActionListener(this);
		result.add(levelBox);
		clearButton = new JButton("Clear");
		clearButton.addActionListener(this);
		result.add(clearButton);
		autoScrollBox = new JCheckBox("Autoscroll");
		autoScrollBox.setSelected(true);
		result.add(autoScrollBox);
		return result;
	}
	
	private JPanel createEditor() {	
		JPanel result = JComponentFactory.createJFirePanel("UILog");
		logTextArea = new JTextArea(25, 120);
		logTextArea.setAutoscrolls(true);
		logTextArea.setEditable(false);
		JScrollPane logScrollPane = new JScrollPane(logTextArea);
		logScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		logScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		logScrollPane.getViewport().setOpaque(false);
		result.add(logScrollPane);
		return result;
	}
	
	@Override
	public Class<?> getTabClassName() {
		return this.getClass();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (clearButton.equals(e.getSource())) {
			logTextArea.setText("");
		} else if (levelBox.equals(e.getSource()) && levelBox.getSelectedIndex()!=-1) {
			Level level = (Level)levelBox.getSelectedItem();
			logHandler.setLevel(level);
			Enumeration<String> loggers = LogManager.getLogManager().getLoggerNames();
			while (loggers.hasMoreElements()) {
				String name = loggers.nextElement();
				Logger logger = LogManager.getLogManager().getLogger(name);
				if (logger!=null && name.contains("pulsefire")) {
					logger.setLevel(level); // only set pulsefire code loggers
				}
			}
		}
	}
	
	class UILogHandler extends Handler {
		@Override
		public void close() throws SecurityException {	
		}
		@Override
		public void flush() {
		}
		@Override
		public void publish(LogRecord record) {
			final String recordStr = getFormatter().format(record);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					logTextArea.append(recordStr);
					if (logTextArea.getLineCount() > logLinesMax) {
						String t = logTextArea.getText();
						int l = 0;
						int rm = logLinesMax/2;
						for (int i=0;i<rm;i++) {
							int ll = t.indexOf('\n',l+1);
							if (ll==-1) {
								break;
							}
							l = ll;
						}
						String tt = t.substring(l,t.length());
						logTextArea.setText(tt);
					}
					if (autoScrollBox.isSelected()) {
						logTextArea.setCaretPosition(logTextArea.getText().length());
					}
				}
			});
		}
	}
	
	public void settingUpdated(PulseFireUISettingKeys key,String value) {
		logLinesMax = new Integer(value);
		logTextArea.setText("");
	}
}

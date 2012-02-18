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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Date;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.UIManager;

import org.jdesktop.application.Application;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.SingleFrameApplication;
import org.nongnu.pulsefire.device.DeviceData;
import org.nongnu.pulsefire.device.DeviceWireManager;
import org.nongnu.pulsefire.device.DeviceWireManagerController;
import org.nongnu.pulsefire.device.ui.time.EventTimeManager;
import org.nongnu.pulsefire.device.ui.time.EventTimeTrigger;
import org.nongnu.pulsefire.device.ui.time.PulseFireDataPuller;
import org.nongnu.pulsefire.wire.serial.SerialDeviceWireManager;


/**
 * PulseFire application
 * 
 * @author Willem Cazander
 */
public class PulseFireUI extends SingleFrameApplication {

	static private final String STORAGE_FILE = "pulsefire-settings.xml";
	private DeviceWireManagerController deviceManagerController = null;
	private PulseFireTimeData timeData = null;
	private EventTimeManager eventTimeManager = null;
	private PulseFireDataLogManager dataLogManager = null;
	private PulseFireUIBuildInfo buildInfo = null;
	private boolean fullScreen = false;
	private Properties settings = null;
	private long startTimeTotal = System.currentTimeMillis();
	private Logger logger = null;
	
	static public void main(String[] args) {
		Application.launch(PulseFireUI.class, args);
	}
	
	/**
	 * Real nasty hack to silent rxtx on startup.
	 */
	private void initSerialLib() {
		PrintStream out = System.out;
		try {
			final StringBuffer buf = new StringBuffer(40);
			System.setOut(new PrintStream(new OutputStream() {
				public void write(int b) {
					buf.append(Character.toChars(b));
				}
			}));
			Class<?> clazz = Class.forName("gnu.io.CommPortIdentifier");
			clazz.getMethod("getPortIdentifiers").invoke(null);
			for (String line:buf.toString().split("\n")) {
				if (line.contains("Version")) {
					logger.info(line); // only log the lib versions. 
				}
			}
		} catch (Exception e1) {
			logger.warning("Could not init serial lib: "+e1.getMessage());
		} finally {
			System.setOut(out);
		}
	}
	
	/**
	 * Does some native lib loading because if is different in each final deployment everment :(
	 */
	private void loadSerialLib(boolean jniCopy,boolean jniCopyOs) {
		try {
			if (isWebStart()) {
				// in webstart lib comes from jar and we need to load it extra
				System.loadLibrary("rxtxSerial");
				return;
			}
			
			if (jniCopy==false) {
				return; // nothing todo
			}
			String libName = System.mapLibraryName("rxtxSerial"); // add .so or .dll
			File libFile = new File(libName);
			if (libFile.exists()) {
				return; // File is already copyed.
			}

			logger.info("Finding native lib: "+libName);
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			if (cl==null) {
				cl = libName.getClass().getClassLoader();
			}
			String arch = System.getProperty("os.arch");
			if ("amd64".endsWith(arch)) {
				arch = "x86_64"; // this name looks better in dir listings and is logical.
			}
			Enumeration<URL> libs = cl.getResources(libName);
			while (libs.hasMoreElements()) {	
				URL jarResourceUrl = libs.nextElement();
				logger.info("Copy native lib from: "+jarResourceUrl+" for: "+arch);
				if (jniCopyOs && jarResourceUrl.toExternalForm().contains(arch)==false) {
					continue;
				}
				InputStream is = jarResourceUrl.openStream();
				OutputStream os = new FileOutputStream(libName);
				byte[] buf = new byte[4096];
				int cnt = is.read(buf);
				while (cnt > 0) {
					os.write(buf, 0, cnt);
					cnt = is.read(buf);
				}
				os.close();
				is.close();
				break; // only do one
			}
			System.loadLibrary("rxtxSerial"); // copy once from jar resource and load native lib.
		} catch (Throwable t) {
			t.printStackTrace();	
		}
	}
	
	private void setupBuildInfo() {
		if (buildInfo!=null) {
			return;
		}
		try {
			Class<?> infoClass = Class.forName(PulseFireUIBuildInfo.class.getPackage().getName()+"."+PulseFireUIBuildInfo.class.getSimpleName()+"Impl");
			buildInfo = (PulseFireUIBuildInfo)infoClass.newInstance();
			return;
		} catch (Exception e) {
			logger.warning("Could not load build info impl fallback to local one.");
		}
		buildInfo = new PulseFireUIBuildInfo() {
			@Override
			public String getVersion() {
				return "0.0.0-NoInfo2";
			}

			@Override
			public String getBuildDate() {
				return new Date().toString();
			}
			
		};
	}
	
	/**
	 * Config logging and setup logger object.
	 */
	private void setupLogging() {
		File logConfig = new File("logfile.properties");
		if (logConfig.exists()) {
			InputStream in = null;
			try {
				in = new FileInputStream(logConfig);
				LogManager.getLogManager().readConfiguration(in);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (in!=null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			Logger rootLogger = Logger.getAnonymousLogger();
			while (rootLogger.getParent()!=null) {
				rootLogger = rootLogger.getParent();
			}
			for (Handler h:rootLogger.getHandlers()) {
				h.setFormatter(new PatternLogFormatter());
			}
		}
		logger = Logger.getLogger(PulseFireUI.class.getName());
	}
	
	protected void initialize(String[] args) {
		super.initialize(args);
		long startTime = System.currentTimeMillis();
		setupLogging();   // init logging with config
		setupBuildInfo(); // Get build version info
		logger.info("Starting PulseFire-UI version: "+buildInfo.getVersion()+" build: "+buildInfo.getBuildDate());

		boolean jniCopy = false;
		boolean jniCopyOs = false;
		for (String argu:args) {
			if ("-fs".equals(argu)) {
				fullScreen = true;
			}
			if ("-jni-cp".equals(argu)) {
				jniCopy = true;
			}
			if ("-jni-cp-os".equals(argu)) {
				jniCopy = true;
				jniCopyOs = true;
			}
		}
		loadSerialLib(jniCopy,jniCopyOs);
		initSerialLib();
		
		try {
			settings = (Properties)getContext().getLocalStorage().load(STORAGE_FILE);
			if (settings!=null) {
				logger.info("Loaded "+STORAGE_FILE+" with "+settings.size()+" settings.");
			}
			
		} catch (IOException e) {
			logger.warning("Could not load settings error: "+e.getMessage());
		} 
		if (settings==null) {
			settings = new Properties();
		}
		deviceManagerController = new DeviceWireManagerController();
		deviceManagerController.addDeviceManager(new SerialDeviceWireManager());
		eventTimeManager = new EventTimeManager();
		eventTimeManager.start();
		timeData = new PulseFireTimeData();
		dataLogManager = new PulseFireDataLogManager();
		dataLogManager.start();
		String colorName = installColorsLaF();
		logger.info("Color schema selected: "+colorName);
		long stopTime = System.currentTimeMillis();
		logger.info("PulseFireUI initialized in "+(stopTime-startTime)+" ms.");
	}
	
	protected void startup() {
		long startTime = System.currentTimeMillis();
		addExitListener(new ShutdownManager());
		
		FrameView mainView = getMainView();
		mainView.getFrame().setMinimumSize(new Dimension(1024-64,768-128));
		mainView.setComponent(new JMainPanel());
		mainView.getFrame().setTitle(mainView.getFrame().getTitle()+" "+buildInfo.getVersion());
		// //new JFireGlassPane(mainView.getFrame());
			
		if (fullScreen) {
			GraphicsDevice gd = mainView.getFrame().getGraphicsConfiguration().getDevice(); 
			mainView.getFrame().setUndecorated(true);
			gd.setFullScreenWindow(mainView.getFrame());
			mainView.getFrame().validate();
		} else {
			show(mainView);
		}
		
		eventTimeManager.addEventTimeTrigger(new EventTimeTrigger("refreshData",new PulseFireDataPuller(),10000));
		//new org.nongnu.pulsefire.device.ui.JNimbusColorFrame(getMainFrame()).setVisible(true);
		long stopTime = System.currentTimeMillis();
		logger.info("PulseFireUI startup in "+(stopTime-startTime)+" ms total startup in "+(stopTime-startTimeTotal)+" ms.");
	}
	
	private String installColorsLaF() {
		UIManager.put("TabbedPane.font",		Font.decode("SansSerif-BOLD-12"));
		UIManager.put("TitledBorder.font",		Font.decode("SansSerif-BOLD-16"));
		UIManager.put("FireDial.font",			Font.decode("SansSerif-9"));
		
		String colorName = getSettingString(PulseFireUISettingKeys.LAF_COLORS);
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (cl==null) {
			cl = this.getClass().getClassLoader();
		}
		InputStream in = cl.getResourceAsStream("org/nongnu/pulsefire/device/ui/resources/colors/"+colorName+".properties");
		if (in==null) {
			logger.warning("Color schema not found: "+colorName);
			return "unknown";
		}
		try {
			Properties p = new Properties();
			p.load(in);
			for (Object key:p.keySet()) {
				String value = p.getProperty(key.toString());
				Color colorValue = Color.decode(value);
				UIManager.put(key,colorValue);
			}
		} catch (IOException e) {
			logger.warning("Could not load color schema: "+colorName+" error: "+e.getMessage());
		} finally {
			try {
				in.close();
			} catch (IOException e) {
			}
		}
		return colorName;
	}
	
	static public PulseFireUI getInstance() {
		return getInstance(PulseFireUI.class);
	}
	
	public DeviceWireManagerController getDeviceManagerController() {
		return deviceManagerController;
	}

	public EventTimeManager getEventTimeManager() {
		return eventTimeManager;
	}
	
	public PulseFireTimeData getTimeData() {
		return timeData;
	}
	
	public DeviceWireManager getDeviceManager() {
		return getDeviceManagerController().getDefaultDeviceManager();
	}
	
	public DeviceData getDeviceData() {
		return getDeviceManager().getDeviceData();
	}
	
	public Properties getSettings() {
		return settings;
	}
	
	public String getSettingString(PulseFireUISettingKeys key) {
		return settings.getProperty(key.name(),key.getDefaultValue());
	}
	
	public void setSettingString(PulseFireUISettingKeys key,String value) {
		settings.setProperty(key.name(),value);
	}
	
	public Boolean getSettingBoolean(PulseFireUISettingKeys key) {
		return new Boolean(getSettingString(key));
	}
	
	public Integer getSettingInteger(PulseFireUISettingKeys key) {
		return new Integer(getSettingString(key));
	}
	
	public void setSettingInteger(PulseFireUISettingKeys key,Integer value) {
		setSettingString(key, ""+value);
	}
	
	public void saveSettings() {
		try {
			getContext().getLocalStorage().save(settings,STORAGE_FILE);
		} catch (IOException e) {
			logger.warning("Could not save settings error: "+e.getMessage());
		}
	}
	
	public boolean isWebStart() {
		try {
			Thread.currentThread().getContextClassLoader().loadClass("javax.jnlp.BasicService");
			return true;
		} catch (Exception e) {
			return false;	
		}
	}
	
	class ShutdownManager implements ExitListener {
		@Override
		public boolean canExit(EventObject e) {
			return true;
		}
		@Override
		public void willExit(EventObject event) {
			logger.info("Shutdown requested.");
			long startTime = System.currentTimeMillis();
			
			setSettingInteger(PulseFireUISettingKeys.UI_SPLIT_BOTTOM,((JMainPanel)getMainView().getComponent()).bottomSplitPane.getDividerLocation());
			setSettingInteger(PulseFireUISettingKeys.UI_SPLIT_BOTTOM_LOG,((JMainPanel)getMainView().getComponent()).bottomLogSplitPane.getDividerLocation());
			saveSettings();
			
			dataLogManager.stop();
			eventTimeManager.shutdown();
			PulseFireUI.getInstance().getDeviceManager().disconnect();
			for (int i=0;i<20;i++) {
				try { Thread.sleep(100); } catch (InterruptedException e) {}
				if (PulseFireUI.getInstance().getDeviceManager().isConnected()==false) {
					break;
				}
			}
			long stopTime = System.currentTimeMillis();
			logger.info("PulseFireUI stopped in "+(stopTime-startTime)+" ms.");
		}
	}
}

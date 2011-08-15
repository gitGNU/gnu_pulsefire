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
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventObject;

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

	private DeviceWireManagerController deviceManagerController = null;
	private PulseFireTimeData timeData = null;
	private EventTimeManager eventTimeManager = null;
	private boolean fullScreen = false;
	
	static public void main(String[] args) {
		Application.launch(PulseFireUI.class, args);
	}
	
	/**
	 * Does some native lib loading because if is different in each final deployment everment :(
	 */
	private void fixNativeLib(boolean jniCopy,boolean jniCopyOs) {
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

			System.out.println("Finding native lib: "+libName);
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
				System.out.println("Copy native lib from: "+jarResourceUrl+" for: "+arch);
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
	
	protected void initialize(String[] args) {
		super.initialize(args);

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
		fixNativeLib(jniCopy,jniCopyOs);
		
		deviceManagerController = new DeviceWireManagerController();
		deviceManagerController.addDeviceManager(new SerialDeviceWireManager());
		eventTimeManager = new EventTimeManager();
		eventTimeManager.start();
		timeData = new PulseFireTimeData();
		installColorsLaF();
	}
	
	protected void startup() {
		addExitListener(new ExitListener() {
			public boolean canExit(EventObject e) {
				return true;
			}
			public void willExit(EventObject event) {
				PulseFireUI.getInstance().getEventTimeManager().shutdown();
				PulseFireUI.getInstance().getDeviceManager().disconnect();
				for (int i=0;i<20;i++) {
					try { Thread.sleep(100); } catch (InterruptedException e) {}
					if (PulseFireUI.getInstance().getDeviceManager().isConnected()==false) {
						break;
					}
				}
			}
		});
		
		FrameView mainView = getMainView();
		mainView.getFrame().setMinimumSize(new Dimension(1024-64,768-128));
		mainView.setComponent(new JMainPanel());
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
		//new org.pulsefire.ui.JNimbusColorFrame(getMainFrame()).setVisible(true);
	}
	
	private void installColorsLaF() {
		//UIManager.put("nimbusBlueGrey", Color.BLUE.darker().darker());
		UIManager.put("TabbedPane.font",  		Font.decode("SansSerif-BOLD-12"));
		UIManager.put("TitledBorder.font",  	Font.decode("SansSerif-BOLD-16"));
		UIManager.put("FireDial.font",  		Font.decode("SansSerif-9"));
		
		
		UIManager.put("text",  					new Color(233,10, 10, 255));
		UIManager.put("info",  					new Color(55, 10, 0,  255));
		UIManager.put("control", 				new Color(10 ,20, 40, 255));
		UIManager.put("nimbusBase", 			new Color(0 , 5,  10, 255));
		UIManager.put("nimbusFocus", 			new Color(0 , 150,10, 255));
		UIManager.put("nimbusOrange", 			new Color(10, 100,40, 255));
		UIManager.put("nimbusDisabledText",		new Color(133,15 ,15, 255));
		UIManager.put("nimbusLightBackground",	new Color(5 , 10 ,40, 255));
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
	
	public boolean isWebStart() {
		try {
			Thread.currentThread().getContextClassLoader().loadClass("javax.jnlp.BasicService");
			return true;
		} catch (Exception e) {
			return false;	
		}
	}
}

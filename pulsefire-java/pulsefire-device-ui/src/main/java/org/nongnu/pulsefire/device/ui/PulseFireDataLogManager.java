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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.nongnu.pulsefire.device.DeviceConnectListener;
import org.nongnu.pulsefire.device.DeviceData;
import org.nongnu.pulsefire.wire.Command;
import org.nongnu.pulsefire.wire.CommandName;

/**
 * PulseFireDataLogManager
 * 
 * @author Willem Cazander
 */
public class PulseFireDataLogManager {

	private Logger logger = null;
	private LogDataWriter logDataWriter0 = null;
	private LogDataWriter logDataWriter1 = null;
	private LogDataWriter logDataWriter2 = null;
	
	public PulseFireDataLogManager() {
		logger = Logger.getLogger(PulseFireDataLogManager.class.getName());
	}
	
	public void start() {
		logDataWriter0 = new LogDataWriter(0);
		logDataWriter1 = new LogDataWriter(1);
		logDataWriter2 = new LogDataWriter(2);
		Thread t0 = new Thread(logDataWriter0);
		t0.setName(logDataWriter0.getClass().getSimpleName());
		t0.start();
		Thread t1 = new Thread(logDataWriter1);
		t1.setName(logDataWriter1.getClass().getSimpleName());
		t1.start();
		Thread t2 = new Thread(logDataWriter2);
		t2.setName(logDataWriter2.getClass().getSimpleName());
		t2.start();
	}
	
	public void stop() {
		if (logDataWriter0!=null) {
			logDataWriter0.shutdown();
		}
		if (logDataWriter1!=null) {
			logDataWriter1.shutdown();
		}
		if (logDataWriter2!=null) {
			logDataWriter2.shutdown();
		}
	}
	
	/*
	class LogDataWriter implements Runnable,DeviceDataListener {
		volatile private boolean run = true;
		private Queue<LogData> dataQueue = null;
		private Writer out = null;
		private DateFormat timeFormat = null;
		
		public LogDataWriter(File logFile,boolean logFileAppend) throws FileNotFoundException {
			dataQueue = new LinkedBlockingQueue<LogData>();
			out = new OutputStreamWriter(new FileOutputStream(logFile,logFileAppend),Charset.forName("UTF-8"));
			timeFormat = new SimpleDateFormat("HH:mm:ss-dd/MM/yyyy");
		}
		
		@Override
		public void run() {
			try {
				char sep = ',';
				while (run) {
					Thread.sleep(1000);
					Boolean logRx = PulseFireUI.getInstance().getSettingsManager().getSettingBoolean(PulseFireUISettingKeys.LOG_CMD_RX);
					Boolean logTx = PulseFireUI.getInstance().getSettingsManager().getSettingBoolean(PulseFireUISettingKeys.LOG_CMD_TX);
					LogData logData = dataQueue.poll();
					while(logData!=null) {
						if ("rx".equals(logData.type) && logRx==false) {
							logData = dataQueue.poll();
							continue;
						}
						if ("tx".equals(logData.type) && logTx==false) {
							logData = dataQueue.poll();
							continue;
						}
						out.append(Long.toString(logData.time));
						out.append(sep);
						out.append(timeFormat.format(logData.time));
						out.append(sep);
						out.append(logData.type);
						out.append(sep);
						out.append(logData.data);
						out.append("\r\n");
						logData = dataQueue.poll();
					}
					out.flush();
				}
			} catch (Exception e) {
				logger.log(Level.WARNING,e.getMessage(),e);
			} finally {
				try {
					if (out!=null) {
						out.close();
					}
				} catch (IOException e) {
					logger.log(Level.WARNING,e.getMessage(),e);
				}
			}
		}
		public void shutdown() {
			run = false;
		}
		
		@Override
		public void deviceDataSend(String data) {
			LogData ld = new LogData();
			ld.data=data;
			ld.type="tx";
			ld.time=System.currentTimeMillis();
			dataQueue.offer(ld);
		}
		@Override
		public void deviceDataReceived(String data) {
			LogData ld = new LogData();
			ld.data=data;
			ld.type="rx";
			ld.time=System.currentTimeMillis();
			dataQueue.offer(ld);
		}
	}
	
	class LogData {
		String data;
		String type;
		long time;
	}
	*/
	
	class LogDataWriter implements Runnable,DeviceConnectListener,PulseFireUISettingListener  {
		
		private int loggerIndex = 0;
		volatile private boolean connected = false;
		volatile private boolean run = true;
		volatile private boolean logEnable = false;
		volatile private boolean logTimeStamp = true;
		volatile private String logFileName = null;
		volatile private String logPath = null;
		volatile private int logSpeed = 60000;
		volatile private List<CommandName> logFields = null;
		volatile private File writerFile = null;
		volatile private boolean writerStart = false;
		volatile private boolean writerStop = false;
		volatile private DateFormat logTimeFormat = null;
		volatile private DateFormat fileTimeFormat = null;
		volatile private long recordCount = 0;
		static final private char FIELD_SEPERATOR = ',';
		static final private char FIELD_QUOTE = '"';
		static final private char FIELD_SPACE = ' ';
		
		public LogDataWriter(int loggerIndexId) {
			this.loggerIndex=loggerIndexId;
			logTimeFormat = new SimpleDateFormat("HH:mm:ss-dd/MM/yyyy");
			fileTimeFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
			PulseFireUI.getInstance().getDeviceManager().addDeviceConnectListener(this);
			
			String logId = "LOG"+loggerIndex+"_";
			PulseFireUI.getInstance().getSettingsManager().addSettingListener(PulseFireUISettingKeys.valueOf(logId+"ENABLE"),   this);
			PulseFireUI.getInstance().getSettingsManager().addSettingListener(PulseFireUISettingKeys.valueOf(logId+"TIMESTAMP"),this);
			PulseFireUI.getInstance().getSettingsManager().addSettingListener(PulseFireUISettingKeys.valueOf(logId+"FILENAME"), this);
			PulseFireUI.getInstance().getSettingsManager().addSettingListener(PulseFireUISettingKeys.valueOf(logId+"PATH"),     this);
			PulseFireUI.getInstance().getSettingsManager().addSettingListener(PulseFireUISettingKeys.valueOf(logId+"SPEED"),    this);
			PulseFireUI.getInstance().getSettingsManager().addSettingListener(PulseFireUISettingKeys.valueOf(logId+"FIELDS"),   this);
			
			updateFields();
		}
		
		public void updateFields() {
			String logId = "LOG"+loggerIndex+"_";
			logEnable         = PulseFireUI.getInstance().getSettingsManager().getSettingBoolean(PulseFireUISettingKeys.valueOf(logId+"ENABLE"));
			logTimeStamp      = PulseFireUI.getInstance().getSettingsManager().getSettingBoolean(PulseFireUISettingKeys.valueOf(logId+"TIMESTAMP"));
			logFileName        = PulseFireUI.getInstance().getSettingsManager().getSettingString(PulseFireUISettingKeys.valueOf(logId+"FILENAME"));
			logPath            = PulseFireUI.getInstance().getSettingsManager().getSettingString(PulseFireUISettingKeys.valueOf(logId+"PATH"));
			logSpeed          = PulseFireUI.getInstance().getSettingsManager().getSettingInteger(PulseFireUISettingKeys.valueOf(logId+"SPEED"));
			String logFieldStr = PulseFireUI.getInstance().getSettingsManager().getSettingString(PulseFireUISettingKeys.valueOf(logId+"FIELDS"));
			logFields = CommandName.decodeCommandList(logFieldStr);
			
			if (logSpeed<1000) {
				logSpeed = 1000;
			}
		}
		
		@Override
		public void run() {
			try {
				while (run) {
					if (writerStart==false) {
						Thread.sleep(500);
						continue;
					}
					writerStart=false;
					
					Writer writer = startWriter();
					if (writer==null) {
						continue;
					}
					writeHeader(writer);
					while (writerStop==false) {
						writeLine(writer);
						for (int i=0;writerStop==false && i<(logSpeed/1000);i++) {
							Thread.sleep(1000);
						}
					}
					writerStop=false;
					stopWriter(writer);
				}
			} catch (Exception e) {
				logger.log(Level.WARNING,e.getMessage(),e);
			}
		}
		
		private Writer startWriter() {
			if (logEnable==false) {
				return null;
			}
			if (logFields.isEmpty()) {
				return null;
			}
			if (logFileName==null) {
				return null;
			}
			if (logFileName.isEmpty()) {
				return null;
			}
			if (logPath==null) {
				return null;
			}
			if (logPath.isEmpty()) {
				return null;
			}
			File realPath = new File(logPath);
			if (realPath.exists()==false) {
				return null;
			}
			if (realPath.isFile()) {
				return null;
			}
			StringBuilder buf = new StringBuilder(100);
			buf.append(realPath.getAbsolutePath());
			buf.append(File.separator);
			buf.append(logFileName);
			if (logTimeStamp) {
				buf.append("-");
				buf.append(fileTimeFormat.format(new Date()));
			}
			buf.append(".log");
			writerFile = new File(buf.toString());
			try {
				Writer writer = new OutputStreamWriter(new FileOutputStream(writerFile,true),Charset.forName("UTF-8"));
				logger.info("Opening log file: "+writerFile.getAbsolutePath());
				return writer;
			} catch (Exception e) {
				logger.log(Level.WARNING,"Error while creating writer for file: "+writerFile.getAbsolutePath(),e);
			}
			return null;
		}
		
		private void stopWriter(Writer writer) {
			try {
				logger.info(" Closing log file: "+writerFile.getAbsolutePath()+" records: "+recordCount);
				recordCount = 0l;
				writerFile = null;
				writer.flush();
				writer.close();
			} catch (Exception e) {
				logger.log(Level.WARNING,"Error while closing writer: "+e.getMessage(),e);
			}
		}
		
		private void writeLine(Writer out) throws IOException {
			DeviceData devData = PulseFireUI.getInstance().getDeviceData();
			long time = System.currentTimeMillis();
			out.append(FIELD_QUOTE);
			out.append(Long.toString(time));
			out.append(FIELD_QUOTE);
			out.append(FIELD_SEPERATOR);
			out.append(FIELD_QUOTE);
			out.append(logTimeFormat.format(time));
			out.append(FIELD_QUOTE);
			out.append(FIELD_SEPERATOR);
			out.append(FIELD_QUOTE);
			for (int f=0;f<logFields.size();f++) {
				CommandName cn = logFields.get(f);
				if (cn.isIndexedA()) {
					for (int i=0;i<cn.getMaxIndexA();i++) {
						Command cmd = devData.getDeviceParameterIndexed(cn, i);
						if (cmd!=null) {
							if (cn.isIndexedB()) {
								out.append(cmd.getArgu0());
								if (cmd.getArgu1()!=null) { out.append(FIELD_SPACE);out.append(cmd.getArgu1()); }
								if (cmd.getArgu2()!=null) { out.append(FIELD_SPACE);out.append(cmd.getArgu2()); }
								if (cmd.getArgu3()!=null) { out.append(FIELD_SPACE);out.append(cmd.getArgu3()); }
								if (cmd.getArgu4()!=null) { out.append(FIELD_SPACE);out.append(cmd.getArgu4()); }
								if (cmd.getArgu5()!=null) { out.append(FIELD_SPACE);out.append(cmd.getArgu5()); }
								if (cmd.getArgu6()!=null) { out.append(FIELD_SPACE);out.append(cmd.getArgu6()); }
								if (cmd.getArgu7()!=null) { out.append(FIELD_SPACE);out.append(cmd.getArgu7()); }
							} else {
								out.append(cmd.getArgu0());
							}
						} else {
							out.append(FIELD_SPACE);
						}
						if (i<cn.getMaxIndexA()-1) {
							out.append(FIELD_QUOTE);
							out.append(FIELD_SEPERATOR);
							out.append(FIELD_QUOTE);
						}
					}
				} else {
					Command cmd = devData.getDeviceParameter(cn);
					if (cmd!=null) {
						out.append(cmd.getArgu0());
					} else {
						out.append(FIELD_SPACE);
					}
				}
				if (f<logFields.size()-1) {
					out.append(FIELD_QUOTE);
					out.append(FIELD_SEPERATOR);
					out.append(FIELD_QUOTE);
				}
			}
			out.append(FIELD_QUOTE);
			out.append("\r\n");
			out.flush();
			recordCount++;
		}
		
		private void writeHeader(Writer out) throws IOException {
			out.append('#');
			out.append("epoch,time,");
			for (int f=0;f<logFields.size();f++) {
				CommandName cn = logFields.get(f);
				if (cn.isIndexedA()) {
					for (int i=0;i<cn.getMaxIndexA();i++) {
						out.append(cn.name());
						out.append(Integer.toString(i));
						if (i<cn.getMaxIndexA()-1) {
							out.append(FIELD_SEPERATOR);
						}
					}
				} else {
					out.append(cn.name());
				}
				if (f<logFields.size()-1) {
					out.append(FIELD_SEPERATOR);
				}
			}
			out.append("\r\n");
			out.flush();
		}
		
		public void shutdown() {
			writerStop = true;
			run = false;
		}

		@Override
		public void deviceConnect() {
			writerStart = true;
			connected = true;
		}

		@Override
		public void deviceDisconnect() {
			writerStop = true;
			connected = false;
		}

		@Override
		public void settingUpdated(PulseFireUISettingKeys key, String value) {
			updateFields();
			if (connected==false) {
				return;
			}
			if (writerFile!=null) {
				writerStop = true;
			}
			writerStart = true;
		}
	}
}

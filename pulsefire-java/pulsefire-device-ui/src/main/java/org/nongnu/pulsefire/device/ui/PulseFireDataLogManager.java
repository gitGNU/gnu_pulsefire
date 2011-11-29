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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.nongnu.pulsefire.device.DeviceCommandListener;
import org.nongnu.pulsefire.device.DeviceData;
import org.nongnu.pulsefire.device.DeviceDataListener;
import org.nongnu.pulsefire.wire.Command;
import org.nongnu.pulsefire.wire.CommandName;
import org.nongnu.pulsefire.wire.CommandVariableType;

/**
 * PulseFireDataLogManager
 * 
 * @author Willem Cazander
 */
public class PulseFireDataLogManager {

	private LogDataWriter logDataWriter = null;
	private LogPullWriter logPullWriter = null;
	
	public void start() {
		Boolean logFileAppend = PulseFireUI.getInstance().getSettingBoolean(PulseFireUISettingKeys.LOG_FILE_APPEND);
		Boolean logCmdEnable = PulseFireUI.getInstance().getSettingBoolean(PulseFireUISettingKeys.LOG_CMD_ENABLE);
		String cmdFile = PulseFireUI.getInstance().getSettingString(PulseFireUISettingKeys.LOG_CMD_FILE);
		if (logCmdEnable && cmdFile.isEmpty()==false) {
			File logFile = new File(cmdFile);
			try {
				if (logFile.exists()==false) {
					logFile.createNewFile();
				}
				logDataWriter = new LogDataWriter(logFile,logFileAppend);
			} catch (IOException ioe) {
				ioe.printStackTrace();
				return;
			}
			PulseFireUI.getInstance().getDeviceManager().addDeviceDataListener(logDataWriter);
			Thread t = new Thread(logDataWriter);
			t.setName("pulsefire-log-data-writer");
			t.start();
		}
		
		Boolean logPullEnable = PulseFireUI.getInstance().getSettingBoolean(PulseFireUISettingKeys.LOG_PULL_ENABLE);
		String pullFile = PulseFireUI.getInstance().getSettingString(PulseFireUISettingKeys.LOG_PULL_FILE);
		if (logPullEnable && pullFile.isEmpty()==false) {
			File logFile = new File(pullFile);
			try {
				if (logFile.exists()==false) {
					logFile.createNewFile();
				}
				logPullWriter = new LogPullWriter(logFile,logFileAppend);
			} catch (IOException ioe) {
				ioe.printStackTrace();
				return;
			}
			PulseFireUI.getInstance().getDeviceManager().addDeviceCommandListener(CommandName.info_data, logPullWriter);
			Thread t = new Thread(logPullWriter);
			t.setName("pulsefire-log-pull-writer");
			t.start();
		}
		
	}
	
	public void stop() {
		if (logDataWriter!=null) {
			logDataWriter.shutdown();
		}
		if (logPullWriter!=null) {
			logPullWriter.shutdown();
		}
	}
	
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
					Boolean logRx = PulseFireUI.getInstance().getSettingBoolean(PulseFireUISettingKeys.LOG_CMD_RX);
					Boolean logTx = PulseFireUI.getInstance().getSettingBoolean(PulseFireUISettingKeys.LOG_CMD_TX);
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
						out.append(""+logData.time);
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
				e.printStackTrace();
			} finally {
				try {
					if (out!=null) {
						out.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
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
	
	class LogPullWriter implements Runnable,DeviceCommandListener {
		volatile private boolean run = true;
		private Writer out = null;
		private DateFormat timeFormat = null;
		volatile private boolean doPullLog = false;
		volatile private boolean writeHeader = true;
		
		public LogPullWriter(File logFile,boolean logFileAppend) throws IOException {
			out = new OutputStreamWriter(new FileOutputStream(logFile,logFileAppend),Charset.forName("UTF-8"));
			timeFormat = new SimpleDateFormat("HH:mm:ss-dd/MM/yyyy");
		}
		
		@Override
		public void run() {
			try {
				char sep = ',';
				DeviceData devData = PulseFireUI.getInstance().getDeviceData();
				while (run) {
					Thread.sleep(1000);
					if (doPullLog==false) {
						continue;
					}
					doPullLog = false;
					if (writeHeader) {
						writeHeader();
						writeHeader = false;
					}
					
					long time = System.currentTimeMillis();
					out.append(""+time);
					out.append(sep);
					out.append(timeFormat.format(time));
					out.append(sep);
					
					for (CommandName cn:CommandName.values()) {
						if (CommandVariableType.DATA.equals(cn.getType())==false) {
							continue;
						}
						if (cn.isIndexedA()) {
							for (int i=0;i<cn.getMaxIndexA();i++) {
								Command cmd = devData.getDeviceParameterIndexed(cn, i);
								if (cmd!=null) {
									out.append(cmd.getArgu0());
									out.append(sep);
								}
							}
						} else {
							Command cmd = devData.getDeviceParameter(cn);
							if (cmd!=null) {
								out.append(cmd.getArgu0());
								out.append(sep);
							}
						}
						
					}
					out.append("\r\n");
					out.flush();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (out!=null) {
						out.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		private void writeHeader() throws IOException {
			out.append('#');
			out.append("epoch,time,");
			for (CommandName cn:CommandName.values()) {
				if (CommandVariableType.DATA.equals(cn.getType())==false) {
					continue;
				}
				if (cn.isIndexedA()) {
					for (int i=0;i<cn.getMaxIndexA();i++) {
						out.append(cn.name());
						out.append(""+i);
						out.append(',');
					}
					
				} else {
					out.append(cn.name());
					out.append(',');
				}
			}
			out.append("\r\n");
			out.flush();
		}
		
		public void shutdown() {
			run = false;
		}
		
		@Override
		public void commandReceived(Command command) {
			doPullLog = true;
		}
	}
}

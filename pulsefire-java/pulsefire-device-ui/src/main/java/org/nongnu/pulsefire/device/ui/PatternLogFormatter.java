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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

/**
 * PatternLogFormatter Formats the messages of the logger.
 * 
 * @author Willem Cazander
 */
public class PatternLogFormatter extends Formatter {

	private final String lineSeperator;
	private final MessageFormat logFormat;
	private final MessageFormat logErrorFormat;
	private final DateFormat dateFormat;
	static private final String DEFAULT_LOG_FORMAT = "%d %l [%C.%s] %m%r";
	static private final String DEFAULT_LOG_ERROR_FORMAT = "%d %l [%C.%s] %m%r%S";
	static private final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	static private final String[] LOG_OPTIONS = { 
		"%d", /* Formated date string */
		"%l", /* Logger level */
		"%n", /* Logger name */
		"%m", /* Logger message */
		"%t", /* Thread ID */
		"%s", /* Source method */
		"%c", /* Source Class */
		"%C", /* Source Class Simple */
		"%S", /* Stacktrace */
		"%r", /* Return/newline */
		};
	
	public PatternLogFormatter() {
		String logFormatStr = LogManager.getLogManager().getProperty(getClass().getName()+".log_pattern");
		String logFormatErrorStr = LogManager.getLogManager().getProperty(getClass().getName()+".log_error_pattern");
		String logDateStr = LogManager.getLogManager().getProperty(getClass().getName()+".date_pattern");
		
		if (logDateStr!=null && logDateStr.isEmpty()==false) {
			dateFormat = new SimpleDateFormat(logDateStr);
		} else {
			dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
		}
		
		if (logFormatStr==null || logFormatStr.isEmpty()) {
			logFormatStr = DEFAULT_LOG_FORMAT;
		}
		if (logFormatStr.contains("{") || logFormatStr.contains("}")) {
			throw new IllegalArgumentException("Curly braces not allowed in log pattern.");
		}
		for (int i=0;i<LOG_OPTIONS.length;i++) {
			logFormatStr = logFormatStr.replace(LOG_OPTIONS[i], "{"+i+"}");
		}
		logFormat = new MessageFormat(logFormatStr);
		
		if (logFormatErrorStr==null || logFormatErrorStr.isEmpty()) {
			logFormatErrorStr = DEFAULT_LOG_ERROR_FORMAT;
		}
		if (logFormatErrorStr.contains("{") || logFormatErrorStr.contains("}")) {
			throw new IllegalArgumentException("Curly braces not allowed in log pattern.");
		}
		for (int i=0;i<LOG_OPTIONS.length;i++) {
			logFormatErrorStr = logFormatErrorStr.replace(LOG_OPTIONS[i], "{"+i+"}");
		}
		logErrorFormat = new MessageFormat(logFormatErrorStr);
		
		lineSeperator = String.format("%n"); // Used platform dependent seperator
	}

	@Override
	public String format(LogRecord record) {
		String[] logFields = new String[10];
		logFields[1] = record.getLevel().toString();
		logFields[2] = record.getLoggerName();
		logFields[3] = record.getMessage();
		if ((logFields[3] == null || logFields[3].isEmpty()) && record.getThrown()!=null) {
			logFields[3] = record.getThrown().getMessage();
		}
		logFields[4] = Integer.toString(record.getThreadID());
		logFields[5] = record.getSourceMethodName() != null ? record.getSourceMethodName() : "?";
		logFields[6] = record.getSourceClassName() != null  ? record.getSourceClassName()  : "?";
		int dotIdx = logFields[6].lastIndexOf(".") + 1;
		if (dotIdx > 0 && dotIdx < logFields[6].length()) {
			logFields[7] = logFields[6].substring(dotIdx);
		} else {
			logFields[7] = logFields[6];
		}
		logFields[8] = record.getThrown()!=null  ? createStackTrace(record.getThrown())  : "";
		logFields[9] = lineSeperator;
		synchronized (logFormat) {
			logFields[0] = dateFormat.format(new Date(record.getMillis())); // dateFormat is guarded by the logFormat lock.
			if (record.getThrown()==null) {
				return logFormat.format(logFields);
			} else {
				return logErrorFormat.format(logFields);
			}
		}
	}
	
	private String createStackTrace(Throwable t) {
		StringWriter buf = new StringWriter();
		t.printStackTrace(new PrintWriter(buf));
		return buf.getBuffer().toString();
	}
}

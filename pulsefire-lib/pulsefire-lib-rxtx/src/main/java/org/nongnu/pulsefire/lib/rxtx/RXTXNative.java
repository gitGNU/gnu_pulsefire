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

package org.nongnu.pulsefire.lib.rxtx;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * RXTXNative provides phased pre check, native lib copy, loading, booting of the rxtx library.
 * 
 * @author Willem Cazander
 */
public class RXTXNative {
	
	private static String NATIVE_LIBRARY_FILE_NAME = "rxtxSerial";
	private static String RESOURCE_PREFIX = "org/nongnu/pulsefire/lib/rxtx/native";
	private static volatile boolean initOnce = false;
	
	public final RXTXNativePhase1 phase1;
	public final RXTXNativePhase2 phase2;
	public final RXTXNativePhase3 phase3;
	public final RXTXNativePhase4 phase4;
	
	public RXTXNative() {
		doInitOnce();
		RXTXNativeLoader phasedLoader = createLoader();
		phase1 = phasedLoader;
		phase2 = phasedLoader;
		phase3 = phasedLoader;
		phase4 = phasedLoader;
	}
	
	protected void doInitOnce() {
		if (initOnce) {
			throw new IllegalStateException("Can init only once.");
		} else {
			initOnce = true;
		}
	}
	
	protected RXTXNativeLoader createLoader() {
		return new RXTXNativeLoader();
	}
	
	static public interface DefaultPhasedBootIntegration {
		void log(String message);
		void showAndExit(String message);
	}
	
	static public void defaultPhasedBoot(File serialDestFolder,DefaultPhasedBootIntegration app) throws RXTXNativeException, IOException {
		long startTime = System.currentTimeMillis();
		RXTXNative serial = new RXTXNative();
		
		// PHASE 1
		if (serial.phase1.checkLockFolderNotExistMacOSX()) {
			app.log("RXTXNative MacFail = Missing folder /var/lock");
			app.showAndExit(serial.phase1.getLockFolderErrorMacOSX());
			return;
		}
		
		// PHASE 2
		if (!serial.phase2.checkLibraryFileExist(serialDestFolder)) {
			app.log("RXTXNative LibCopy = "+serial.phase2.getLibraryFileNameNativeAsResource());
			serial.phase2.copyLibraryFile(serialDestFolder);
		}
		app.log("RXTXNative LibFile = "+serial.phase2.getLibraryFileNameNative());
		app.log("RXTXNative LibType = "+serial.phase2.getLibraryFolderNameNative());
		
		// PHASE 3
		serial.phase3.loadLibrary();
		
		// PHASE 4
		List<String> bootLog = serial.phase4.bootLibrary(); 
		for (String bootLine:bootLog) {
			app.log(bootLine);
		}
		long totalTime = System.currentTimeMillis() - startTime;
		app.log("RXTXNative boot ms = "+totalTime);
	}
	
	protected class RXTXNativeLoader implements RXTXNativePhase1,RXTXNativePhase2,RXTXNativePhase3,RXTXNativePhase4 { 
	
		protected String getLibraryFileName() {
			return NATIVE_LIBRARY_FILE_NAME;
		}
		
		public String getLibraryFileNameNative() {
			return System.mapLibraryName(getLibraryFileName());
		}
		
		public boolean checkLibraryFileExist(File destFolder) {
			return new File(destFolder,getLibraryFileNameNative()).exists();
		}
		
		public void loadLibrary() {
			System.loadLibrary(getLibraryFileName());
		}
		
		protected ClassLoader getClassLoader() {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			if (cl==null) {
				cl = getClass().getClassLoader();
			}
			return cl;
		}
		
		public String getLibraryFileNameNativeAsResource() {
			return RESOURCE_PREFIX+"/"+getLibraryFolderNameNative()+"/"+getLibraryFileNameNative();
		}
		
		public String getLibraryFolderNameNative() {
			String osname = System.getProperty("os.name");
			
			String arch = System.getProperty("os.arch");
			if ("amd64".endsWith(arch)) {
				arch = "x86_64"; // this name looks better in dir listings and is logical.
			}
			StringBuilder result = new StringBuilder(20);
			result.append(osname.toLowerCase());
			result.append('-');
			result.append(arch.toLowerCase());
			return result.toString();
		}
		
		protected boolean copyLibraryNative(File destFile,InputStream is) throws IOException {
			if (is==null) {
				return false; // FIXME
			}
			OutputStream os = null;
			try {
				os = new FileOutputStream(destFile);
				byte[] buf = new byte[4096];
				int cnt = is.read(buf);
				while (cnt > 0) {
					os.write(buf, 0, cnt);
					cnt = is.read(buf);
				}
				return true;
			} finally {
				is.close();
				if (os!=null) {
					os.close();
				}
			}
		}
		
		public boolean copyLibraryFile(File destFolder) throws IOException {
			if (checkLibraryFileExist(destFolder)) {
				return false;
			}
			File destFile = new File(destFolder,getLibraryFileNameNative());
			String srcResource = getLibraryFileNameNativeAsResource();
			return copyLibraryNative(destFile,getClassLoader().getResourceAsStream(srcResource));
		}
		
		/**
		 * Real nasty hack to silent rxtx on startup.
		 */
		public List<String> bootLibrary() throws RXTXNativeException {
			return executeCleanConsoleRunner(new CleanConsoleRunner() {
				@Override
				public void run() throws RXTXNativeException {
					try {
						Class<?> clazz = Class.forName("gnu.io.CommPortIdentifier");
						clazz.getMethod("getPortIdentifiers").invoke(null);
					} catch (Exception e) {
						throw new RXTXNativeException("Could not init serial lib: "+e.getMessage(),e);
					}
				}
			});
		}
		
		protected List<String> executeCleanConsoleRunner(CleanConsoleRunner code) throws RXTXNativeException {
			List<String> result = new ArrayList<String>(5);
			PrintStream out = System.out;
			try {
				final StringBuilder outBuffer = new StringBuilder(40);
				System.setOut(new PrintStream(new OutputStream() {
					public void write(int b) {
						outBuffer.append(Character.toChars(b));
					}
				}));
				try {
					code.run();
				} finally {
					for (String line:outBuffer.toString().split("\n")) {
						if (line.contains("Version")) {
							result.add(line); // only log the lib versions. 
						}
					}
				}
			} finally {
				System.setOut(out);
			}
			return result;
		}
		
		public boolean checkLockFolderNotExistMacOSX() {
			String osname = System.getProperty("os.name");
			if (osname==null) {
				return false;
			}
			if (osname.startsWith("Mac")==false) {
				return false; // This check is only needed on mac platform.
			}
			File varLock = new File("/var/lock");
			if (varLock.exists()) {
				return false; // Only check existance 
			}
			return true; // we have problem
		}
		
		public String getLockFolderErrorMacOSX() {
			String macError = "Fatal Max OS X Error:\n"+
					"Directory '/var/lock' does not exists.\n"+
					"Please do the following commands in 'Terminal';\n"+
					"$ sudo bash\n"+
					"# mkdir /var/lock\n"+
					"# chmod 777 /var/lock\n"+
					"# exit\n$ exit\n"+
					"note: the 'sudo' command will ask for your password.\n"+
					"Done, now start pulsefire again.";
			return macError;
		}
		
	}
	
	interface CleanConsoleRunner {
		void run() throws RXTXNativeException;
	}
	
	// Later: build proper generic precheck api.
	public interface RXTXNativePhase1 {
		boolean checkLockFolderNotExistMacOSX();
		String getLockFolderErrorMacOSX();
	}
	
	public interface RXTXNativePhase2 {
		String getLibraryFolderNameNative();
		String getLibraryFileNameNative();
		String getLibraryFileNameNativeAsResource();
		boolean checkLibraryFileExist(File destFolder);
		boolean copyLibraryFile(File destFolder) throws IOException;
	}
	
	public interface RXTXNativePhase3 {
		void loadLibrary() throws IOException;
	}
	
	public interface RXTXNativePhase4 {
		List<String> bootLibrary() throws RXTXNativeException;
	}
}

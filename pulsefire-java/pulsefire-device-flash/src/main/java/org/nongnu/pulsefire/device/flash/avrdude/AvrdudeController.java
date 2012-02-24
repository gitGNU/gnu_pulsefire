package org.nongnu.pulsefire.device.flash.avrdude;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.nongnu.pulsefire.device.flash.AbstractFlashProgramController;
import org.nongnu.pulsefire.device.flash.FlashControllerConfig;
import org.nongnu.pulsefire.device.flash.FlashException;
import org.nongnu.pulsefire.device.flash.FlashHexReader;


/**
 * AvrdudeController flashes with avrdude cmd.
 * 
 * @author Willem Cazander
 */
public class AvrdudeController extends AbstractFlashProgramController {

	@Override
	public void flash(FlashControllerConfig conf) throws IOException, FlashException {
		if (conf.getNativeFlashCmd()==null) {
			throw new FlashException("Can't flash with null nativeFlashCmd in config.");
		}
		if (conf.getNativeFlashCmd().isEmpty()) {
			throw new FlashException("Can't flash with empty nativeFlashCmd in config.");
		}
		File nativeCmd = new File(conf.getNativeFlashCmd()); 
		if (nativeCmd.canExecute()==false) {
			throw new FlashException("Can't execute the nativeFlashCmd in config.");
		}
		progress = 1;

		List<String> cmd = new ArrayList<String>(15);
		cmd.add(nativeCmd.getAbsolutePath());
		
		// HACK loopup table :(
		String chipId = null;
		if (conf.getDeviceSignature()==0x1e940b) {
			chipId = "m168p";
		} else if (conf.getDeviceSignature()==0x1e950f) {
			chipId = "m328p";
		} else if (conf.getDeviceSignature()==0x1e9703) {
			chipId = "m1280";
		} else if (conf.getDeviceSignature()==0x1e9801) {
			chipId = "m2560";
		} else {
			throw new FlashException("unknown device id in conf: "+conf.getDeviceSignature());
		}
		cmd.add("-p");
		cmd.add(chipId);
		cmd.add("-P");
		cmd.add(conf.getPort());
		cmd.add("-c");
		cmd.add(conf.getPortProtocol());
		if (conf.isLogDebug()) {
			cmd.add("-v");
			cmd.add("-v");
		}
		if (conf.isFlashVerify()==false) {
			cmd.add("-V");
		}
		if (conf.getNativeFlashConfig()!=null && conf.getNativeFlashConfig().isEmpty()==false) {
			cmd.add("-C");
			cmd.add(conf.getNativeFlashConfig());
		}
		
		File tmp = File.createTempFile("pulsefire-", ".hex");
		tmp.deleteOnExit();
		FlashHexReader hex = new FlashHexReader();
		hex.writeHexData(conf.getFlashData(), new FileOutputStream(tmp));
		cmd.add("-U");
		
		String fullFilePath = tmp.getAbsolutePath();
		if (fullFilePath.contains(":")) {
			fullFilePath = "\""+fullFilePath+"\""; // make bug report avrdude this is needed on windows but not under linux !!!
		}
		cmd.add("flash:w:"+fullFilePath+":i"); // win32 avrdude needs the format !!
		
		StringBuilder buf = new StringBuilder(200);
		for (String c:cmd) {
			buf.append(c);
			buf.append(' ');
		}
		logMessage("Exec: "+buf.toString());
		
		progress = 2;
		ProcessBuilder builder = new ProcessBuilder(cmd);
		builder.redirectErrorStream(true);
		Process process = builder.start();
		InputStream stdout = process.getInputStream ();
		BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
		String line = null;
		while ((line = reader.readLine ()) != null) {
			if (line.contains("device initialized")) {
				progress = 10;
			}
			if (line.contains("writing")) {
				progress = 50;
			}
			if (line.contains("Thank you")) {
				progress = 90;
			}
			logMessage(line);
		}
		progress = 100;
		logMessage("Avrdude is done.");
	}
}

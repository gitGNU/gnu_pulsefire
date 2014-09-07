package org.nongnu.pulsefire.device.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;
import org.nongnu.pulsefire.device.io.protocol.CommandName;

import junit.framework.TestCase;

public class CommandI18nTest extends TestCase {
	
	Map<String,String> keywords;
	Map<String,String> qmaps;
	
	public CommandI18nTest() {
		keywords = new HashMap<String,String>();
		keywords.put("vsc","variable stepper controller");
		keywords.put("cip","chip internal pwm");
		keywords.put("mal","micro assembly language");
		keywords.put("ptc","programmatic time controller");
		keywords.put("ptt","programmatic time trigger");
		keywords.put("stv","safety threshold values");
		keywords.put("ppm","programmatic pulse mode");
		keywords.put("vfc","virtual feedback controller");
		
		keywords.put("sys","system");
		keywords.put("adc","analog");
		keywords.put("cnt","counter");
		keywords.put("com","compare action");
		keywords.put("ocr","output compare register");
		keywords.put("mul","multiplier");
		keywords.put("dev","device");
		keywords.put("inv","invert");
		keywords.put("idx","index");
		keywords.put("map","mapping");
		keywords.put("freq","frequency");
		keywords.put("int","interrupt");
		keywords.put("conf","config");
		keywords.put("req","request");
		keywords.put("warn","warning");
		keywords.put("avr","Arduino uno");
		keywords.put("mega","Ardiomp mega");
		
		qmaps = new HashMap<String,String>();
		qmaps.put("adc_map","map-min,map-max");
		qmaps.put("dic_map","low,high");
		qmaps.put("int_map","int0,int1");
		qmaps.put("ptc_0map","value,time");
		qmaps.put("ptc_1map","value,time");
		qmaps.put("ptt_0map","value,time");
		qmaps.put("ptt_1map","value,time");
		qmaps.put("ptt_2map","value,time");
		qmaps.put("ptt_3map","value,time");
		qmaps.put("pulse_fire_map","fire,zero");
		qmaps.put("pulse_hold_map","hold,zero");
		qmaps.put("pulse_reset_map","reset,zero");
		qmaps.put("pulse_resume_map","resume,zero");
		qmaps.put("stv_error_map","error,error-exit");
		qmaps.put("stv_max_map","warning,error");
		qmaps.put("stv_min_map","warning,error");
		qmaps.put("stv_warn_map","warning,warning-exit");
		qmaps.put("sys_vvl_map","min,max");
		qmaps.put("sys_vvm_map","dot,lock");
		qmaps.put("vfc_input_map","min,max");
		qmaps.put("vfc_output_map","min-map,max-map");
		qmaps.put("vsc_0map","min,max");
		qmaps.put("vsc_1map","min,max");
	}
	
	private String makeHumanName(CommandName cn) {
		StringBuilder buf = new StringBuilder();
		String[] parts = cn.name().split("_");
		char firstChar = ' ';
		for (int i=0;i<parts.length;i++) {
			String part = parts[i];
			part = part.substring(0,1).toUpperCase()+part.substring(1);
			if (Character.isDigit(part.charAt(0))) {
				firstChar = part.charAt(0);
				part = part.substring(1);
			}
			
			buf.append(part);
			if (i<parts.length-1) {
				buf.append(" ");
			}
		}
		if (Character.isDigit(firstChar)) {
			buf.append(" ");
			buf.append(firstChar);
		}
		return buf.toString();
	}
	
	private String removeFirst(String line) {
		StringBuilder buf = new StringBuilder();
		String[] parts = line.split(" ");
		for (int i=0;i<parts.length;i++) {
			if (i==0 && i<parts.length-1) {
				continue;
			}
			String part = parts[i];
			buf.append(part);
			if (i<parts.length-1) {
				buf.append(" ");
			}
		}
		return buf.toString();
	}
	
	private String explainKeyworks(String line) {
		StringBuilder buf = new StringBuilder();
		String[] parts = line.split(" ");
		for (int i=0;i<parts.length;i++) {
			String part = parts[i];
			if (keywords.containsKey(part.toLowerCase())) {
				part = keywords.get(part.toLowerCase());
			}
			buf.append(part);
			if (i<parts.length-1) {
				buf.append(" ");
			}
		}
		buf.append('.');
		String res =  buf.toString();
		res = res.substring(0,1).toUpperCase()+res.substring(1);
		return res;
	}
	
	@Test
	public void testGenerateListing() {
		Map<String,String> resourceData = new TreeMap<String,String>();
		
		// name:        hello <name>
		// label:       <label> <edit-value>
		// description: tooltip/table/help
		for (CommandName cn:CommandName.values()) {
			String humanValue = makeHumanName(cn);
			resourceData.put(cn.getKeyName(), humanValue);
			resourceData.put(cn.getKeyLabel(), removeFirst(humanValue));
			resourceData.put(cn.getKeyDescription(), explainKeyworks(humanValue));
			
			if (qmaps.containsKey(cn.name())) {
				String[] maps = qmaps.get(cn.name()).split(",");
				resourceData.put(cn.getKeyQMapValueA(), maps[0]);
				resourceData.put(cn.getKeyQMapValueB(), maps[1]);
			}
		}
		
		System.out.println("KEYS START");
		for (String key:resourceData.keySet()) {
			String value = resourceData.get(key);
			System.out.println(key+"="+value);
		}
		System.out.println("KEYS DONE");
	}
}

package com.liferay.glowroot.plugins.client.extensions;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JSONUtil {

	public static String prettyPrintJson(String json) {

		StringBuilder prettyJson = new StringBuilder();
		
		int lvl = 0;
		
		for(int i = 0; i < json.length(); i++) {
			char c = json.charAt(i);
			if(c == '{' || c == '[') {
				prettyJson.append(c);
				lvl++;
				prettyJson.append(newLine(lvl));
			} else if(c == '}' || c == ']') {
				lvl--;
				prettyJson.append(newLine(lvl));
				prettyJson.append(c);
			} else if(c == ',') {
				prettyJson.append(c);
				prettyJson.append(newLine(lvl));
			} else {
				prettyJson.append(c);
			}
		}
		
		return prettyJson.toString();
	}
	
	private static String newLine(int lvl) {
		return newLines.putIfAbsent(lvl, "\n".concat("    ".repeat(lvl)));
	}

	private static Map<Integer, String> newLines = new ConcurrentHashMap<>();
}

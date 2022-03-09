package com.ranfa.lib.handler;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.slf4j.LoggerFactory;

public class CrashReportList <E> extends ArrayList<E> {

	private static final String EMPTY_LINE_PLACEHOLDER = "{empty}";
	
	private final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss.txt");
	
	public CrashReportList() {
		super();
	}
	
	public String generateCrashReport() {
		StringBuilder builder = new StringBuilder();
		Iterator<E> itr = super.iterator();
		if(!itr.hasNext())
			throw new IllegalStateException("There is no lines to generate crash report.");
		while(itr.hasNext()) {
			String next = itr.next().toString();
			if(next.equals(EMPTY_LINE_PLACEHOLDER))
				next.replaceAll(EMPTY_LINE_PLACEHOLDER, "\n");
			builder.append(next).append("\n");
		}
		builder.deleteCharAt(builder.length()).deleteCharAt(builder.length());
		try {
			FileWriter writer = new FileWriter(Paths.get(FORMAT.format(new Date())).toFile());
			writer.close();
		} catch (IOException e) {
			LoggerFactory.getLogger(CrashReportList.class).error("Cannot write crash report.", e);
		}
		return builder.toString();
	}
	
	@SuppressWarnings("unchecked")
	public void emptyLine() {
		super.add((E) EMPTY_LINE_PLACEHOLDER);
	}
}

package com.ranfa.lib.handler;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.LoggerFactory;

public class CrashReportList <E> extends ArrayList<E> {

	private static final String EMPTY_LINE_PLACEHOLDER = "{empty}";
	
	private final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	
	private List<E> store;
	
	public CrashReportList() {
		super();
		store = new ArrayList<>();
	}
	
	@Override
	public boolean add(E e) {
		return store.add(e);
	}
	
	@SuppressWarnings("unchecked")
	public String generateCrashReport() {
		StringBuilder builder = new StringBuilder();
		for(E str : store) {
			if(str.equals(EMPTY_LINE_PLACEHOLDER))
				str = (E) "\n";
			builder.append(str).append("\n");
		}
		try {
			FileWriter writer = new FileWriter(Paths.get("Crash-Report/" + FORMAT.format(new Date()) + ".txt").toFile());
			writer.write(builder.toString());
			writer.close();
		} catch (IOException e) {
			LoggerFactory.getLogger(CrashReportList.class).error("Cannot write crash report.", e);
		}
		return builder.toString();
	}
	
	@SuppressWarnings("unchecked")
	public boolean emptyLine() {
		return store.add((E) EMPTY_LINE_PLACEHOLDER);
	}
}

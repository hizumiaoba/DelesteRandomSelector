package com.ranfa.lib;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LimitedLog {

	private final static String STRING_DATE_FORMAT = "YYYY-MM-dd HH-mm-ss-SSS: ";
	private static SimpleDateFormat format = new SimpleDateFormat(STRING_DATE_FORMAT);

	public static void print(String s) {
		Calendar calendar = Calendar.getInstance();
		if(!Settings.outputDebugSentences()) {
			return;
		} else {
			System.out.print(format.format(calendar.getTime()) + s);
			return;
		}
	}

	public static void println(String s) {
		Calendar calendar = Calendar.getInstance();
		if(!Settings.outputDebugSentences()) {
			return;
		}
		System.out.println(format.format(calendar.getTime()) + s);
	}

	public static void println() {
		// TODO 自動生成されたメソッド・スタブ
		if(!Settings.outputDebugSentences()) {
			return;
		}
		System.out.println();
	}
}

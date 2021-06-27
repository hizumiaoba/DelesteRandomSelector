package com.ranfa.lib;

public class LimitedLog {

	private final static boolean ISALLOWED = Settings.outputDebugSentences();

	public static void print(String s) {
		if(!ISALLOWED) {
			return;
		} else {
			System.out.print(s);
			return;
		}
	}

	public static void println(String s) {
		if(!ISALLOWED) {
			return;
		}
		System.out.println(s);
	}
}

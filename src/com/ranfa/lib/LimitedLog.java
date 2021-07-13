package com.ranfa.lib;

public class LimitedLog {

	public static void print(String s) {
		if(!Settings.outputDebugSentences()) {
			return;
		} else {
			System.out.print(s);
			return;
		}
	}

	public static void println(String s) {
		if(!Settings.outputDebugSentences()) {
			return;
		}
		System.out.println(s);
	}
}

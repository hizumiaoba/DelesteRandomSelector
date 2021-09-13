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

	public static void println() {
		// TODO 自動生成されたメソッド・スタブ
		if(!Settings.outputDebugSentences()) {
			return;
		}
		System.out.println();
	}
}

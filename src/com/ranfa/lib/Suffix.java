package com.ranfa.lib;

public enum Suffix {
	
	/** Alpha channel*/
	ALPHA("Alpha"),
	
	/** Beta channel*/
	BETA("Beta"),
	
	/** Stable channel*/
	STABLE("Stable");
	
	private String suf;
	
	Suffix(String str) {
		this.suf = str;
	}
	
	@Override
	public String toString() {
		return suf;
	}
}

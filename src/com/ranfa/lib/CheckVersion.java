package com.ranfa.lib;

import com.ranfa.main.DelesteRandomSelector;

public class CheckVersion {
	
	public static boolean needToBeUpdated() {
		
	}
	
	
	/**
	 * アノテーションで記載されているバージョンを取得します
	 * @since v1.0.0
	 * @return アノテーションで定義されているバージョン
	 */
	public static String getVersion() {
		String value = "v"
				+ getMajorVersion() + "."
				+ getMinorVersion() + "."
				+ getPatchVersion();
		return value;
	}

	public static int getMajorVersion() {
		Version version = (Version) DelesteRandomSelector.class.getAnnotation(Version.class);
		return version.major();
	}

	public static int getMinorVersion() {
		Version version = (Version) DelesteRandomSelector.class.getAnnotation(Version.class);
		return version.minor();
	}

	public static int getPatchVersion() {
		Version version = (Version) DelesteRandomSelector.class.getAnnotation(Version.class);
		return version.patch();
	}

}

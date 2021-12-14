package com.ranfa.main;

import java.util.Locale;
import java.util.ResourceBundle;

/*
 * ja_JPはUTF-16BE
 */
public enum Messages {

	MSGCalcStart,
	MSGDatabaseNotExist,
	MSGAlbumTypeBeingCalculated,
	MSGNarrowingDownSongs;

	@Override
	public String toString() {
		try {
			return ResourceBundle.getBundle("com.ranfa.languages.List", Locale.getDefault()).getString(this.name());
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(-1);
			return null;
		}
	}

}

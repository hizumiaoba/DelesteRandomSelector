package com.ranfa.lib;

public class SettingJSONProperty {

	private boolean checkVersion;
	private boolean checkLibraryUpdates;
	private int windowWidth;
	private int windowHeight;
	private int songLimit;
	private boolean saveScoreLog;
	private boolean outputDebugSentences;


	public boolean isCheckVersion() {
		return checkVersion;
	}
	public void setCheckVersion(boolean checkVersion) {
		this.checkVersion = checkVersion;
	}
	public boolean isCheckLibraryUpdates() {
		return checkLibraryUpdates;
	}
	public void setCheckLibraryUpdates(boolean checkLibraryUpdates) {
		this.checkLibraryUpdates = checkLibraryUpdates;
	}
	public int getWindowWidth() {
		return windowWidth;
	}
	public void setWindowWidth(int windowWidth) {
		this.windowWidth = windowWidth;
	}
	public int getWindowHeight() {
		return windowHeight;
	}
	public void setWindowHeight(int windowHeight) {
		this.windowHeight = windowHeight;
	}
	public int getSongLimit() {
		return songLimit;
	}
	public void setSongLimit(int songLimit) {
		this.songLimit = songLimit;
	}
	public boolean isSaveScoreLog() {
		return saveScoreLog;
	}
	public void setSaveScoreLog(boolean saveScoreLog) {
		this.saveScoreLog = saveScoreLog;
	}
	public boolean isOutputDebugSentences() {
		return outputDebugSentences;
	}
	public void setOutputDebugSentences(boolean outputDebugSentences) {
		this.outputDebugSentences = outputDebugSentences;
	}

}

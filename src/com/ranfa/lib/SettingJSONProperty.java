package com.ranfa.lib;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SettingJSONProperty {

	@JsonProperty
	private boolean checkVersion;
	@JsonProperty
	private boolean checkLibraryUpdates;
	@JsonProperty
	private int windowWidth;
	@JsonProperty
	private int windowHeight;
	@JsonProperty
	private int songLimit;
	@JsonProperty
	private boolean saveScoreLog;
	@JsonProperty
	private boolean outputDebugSentences;

	public void setCheckVersion(boolean checkVersion) {
		this.checkVersion = checkVersion;
	}

	public void setCheckLibraryUpdates(boolean checkLibraryUpdates) {
		this.checkLibraryUpdates = checkLibraryUpdates;
	}

	public void setWindowWidth(int windowWidth) {
		this.windowWidth = windowWidth;
	}

	public void setWindowHeight(int windowHeight) {
		this.windowHeight = windowHeight;
	}

	public void setSongLimit(int songLimit) {
		this.songLimit = songLimit;
	}

	public void setSaveScoreLog(boolean saveScoreLog) {
		this.saveScoreLog = saveScoreLog;
	}

	public void setOutputDebugSentences(boolean outputDebugSentences) {
		this.outputDebugSentences = outputDebugSentences;
	}

}

package com.ranfa.lib.io;

import java.io.Serializable;

public class OutputDataStructure implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// fields
	private String songname;
	private int level;
	private String difficulty;
	private String attribute;
	private int score;
	
	public OutputDataStructure() {
		this(null, -1, null, null, -1);
	}
	
	public OutputDataStructure(String songname, int level, String difficulty, String attribute, int score) {
		this.songname = songname;
		this.level = level;
		this.difficulty = difficulty;
		this.attribute = attribute;
		this.score = score;
	}

	/**
	 * @return songname
	 */
	public String getSongname() {
		return songname;
	}

	/**
	 * @param songname セットする songname
	 */
	public void setSongname(String songname) {
		this.songname = songname;
	}

	/**
	 * @return level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @param level セットする level
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * @return difficulty
	 */
	public String getDifficulty() {
		return difficulty;
	}

	/**
	 * @param difficulty セットする difficulty
	 */
	public void setDifficulty(String difficulty) {
		this.difficulty = difficulty;
	}

	/**
	 * @return attribute
	 */
	public String getAttribute() {
		return attribute;
	}

	/**
	 * @param attribute セットする attribute
	 */
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	/**
	 * @return score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * @param score セットする score
	 */
	public void setScore(int score) {
		this.score = score;
	}
	
	
}

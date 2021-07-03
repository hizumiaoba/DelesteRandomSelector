package com.ranfa.lib;

public class Song {

	private String attribute;
	private String name;
	private String difficulty;
	private int level;
	private int notes;

	public Song(String attribute, String name, String difficulty, int level, int notes) {
		this.attribute = attribute;
		this.name = name;
		this.difficulty = difficulty;
		this.level = level;
		this.notes = notes;
	}

	public String getAttribute() {
		return this.attribute;
	}

	public String getName() {
		return this.name;
	}

	public String getDifficulty() {
		return this.difficulty;
	}

	public int getLevel() {
		return this.level;
	}

	public int getNotes() {
		return this.notes;
	}

	public String toString() {
		return "Attribute: " + getAttribute()
				+ ", Song Name: " + getName()
				+ ", Difficulty: " + getDifficulty()
				+ ", Level :" + String.valueOf(getLevel())
				+ ", Notes :" + String.valueOf(getNotes());
	}
}

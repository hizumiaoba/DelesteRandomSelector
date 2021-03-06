package com.ranfa.lib.database;

public class Song {

	private String attribute;
	private String name;
	private String difficulty;
	private int level;
	private int notes;
	private String albumType;


	public String getAttribute() {
		return attribute;
	}


	public String getName() {
		return name;
	}


	public String getDifficulty() {
		return difficulty;
	}


	public int getLevel() {
		return level;
	}


	public int getNotes() {
		return notes;
	}


	public void setNotes(int notes) {
		this.notes = notes;
	}


	public void setLevel(int level) {
		this.level = level;
	}


	public void setDifficulty(String difficulty) {
		this.difficulty = difficulty;
	}


	public void setName(String name) {
		this.name = name;
	}


	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}


	@Override
	public String toString() {
		return "Attribute: " + getAttribute()
				+ ", Song Name: " + getName()
				+ ", Difficulty: " + getDifficulty()
				+ ", Level :" + String.valueOf(getLevel())
				+ ", Notes :" + String.valueOf(getNotes());
	}


	public String getAlbumType() {
		return albumType;
	}


	public void setAlbumType(String albumType) {
		this.albumType = albumType;
	}

}

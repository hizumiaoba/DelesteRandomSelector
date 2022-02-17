package com.ranfa.lib.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileIO {
	
	private static Logger logger = LoggerFactory.getLogger(FileIO.class);
	
	// constants
	private static final String FILE_NAME_PATTERN = "YYYY-MM-dd-HH-mm-ss";
	private static final String FILE_EXTENSION = ".drs";
	
	// fields
	/**
	 * songname
	 * level
	 * difficulty
	 * attribute
	 * score
	 */
	List<Map<String, String>> songList;
	
	
	public FileIO(List<Map<String, String>> songList) {
		this.songList = songList;
	}
	
	public FileIO() {
		this.songList = null;
	}
	
	public boolean write() {
		StringBuilder builder = new StringBuilder(new SimpleDateFormat(FILE_NAME_PATTERN).format(new Date()));
		builder.append(FILE_EXTENSION);
		try {
			ObjectOutputStream outStream = new ObjectOutputStream(new FileOutputStream(builder.toString()));
			OutputDataStructure structure = new OutputDataStructure(songList);
			outStream.writeObject(structure);
			outStream.flush();
			outStream.close();
			return true;
		} catch (IOException e) {
			logger.error("Exception while output objects", e);
			return false;
		}
	}
	
	public static OutputDataStructure read(String fileName) {
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(fileName);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
	        OutputDataStructure structure = (OutputDataStructure) objectInputStream.readObject();
	        objectInputStream.close();
	        return structure;
		} catch (IOException | ClassNotFoundException e) {
			logger.error("Exception while reading objects", e);
			return null;
		}
	}

}

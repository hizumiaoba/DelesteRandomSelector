package com.ranfa.lib.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

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
	private OutputDataStructure data;
	
	
	public FileIO(OutputDataStructure data) {
		this.data = data;
	}
	
	public void write() throws IOException {
		StringBuilder builder = new StringBuilder(new SimpleDateFormat(FILE_NAME_PATTERN).format(new Date()));
		builder.append(FILE_EXTENSION);
			ObjectOutputStream outStream = new ObjectOutputStream(new FileOutputStream(builder.toString()));
			outStream.writeObject(data);
			outStream.close();
	}
	
	public static OutputDataStructure read(File file) {
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(file);
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

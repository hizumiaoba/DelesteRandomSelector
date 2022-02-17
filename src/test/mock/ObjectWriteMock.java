package test.mock;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import com.ranfa.lib.io.OutputDataStructure;

public class ObjectWriteMock {
	
	public static boolean write(List<Map<String, String>> songList, String filename) {
		try {
			ObjectOutputStream outStream = new ObjectOutputStream(new FileOutputStream(filename));
			OutputDataStructure structure = new OutputDataStructure(songList);
			outStream.writeObject(structure);
			outStream.flush();
			outStream.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

}

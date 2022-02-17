package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ranfa.lib.io.FileIO;
import com.ranfa.lib.io.OutputDataStructure;

import test.mock.ObjectWriteMock;

/**
 * songname
 * level
 * difficulty
 * attribute
 * score
 */
public class ObjectIOTest {
	
	private static final String filename = "test.drs";
	private static Map<String, String> testMap;
	private static List<Map<String, String>> testList;
	
	@BeforeClass
	public static void init() {
		testMap = new LinkedHashMap<>();
		testMap.put("songname", "testname001");
		testMap.put("level", "28");
		testMap.put("difficulty", "MASTER");
		testMap.put("attribute", "キュート");
		testMap.put("score", "1200000");
		testList = new ArrayList<>();
		testList.add(testMap);
	}
	
	
	@Test
	public void writeTest() {
		assertTrue(ObjectWriteMock.write(testList, filename));
	}
	
	@Test
	public void readNonNullTest() {
		OutputDataStructure structure = FileIO.read(filename);
		assertNotNull(structure);
	}
	
	@Test
	public void readEqualsTest() {
		OutputDataStructure structure = FileIO.read(filename);
		if(structure != null) {
			assertEquals(new OutputDataStructure(testList), structure);
		}
	}
	
	@AfterClass
	public static void del() {
			try {
				Files.deleteIfExists(Paths.get(filename));
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

}

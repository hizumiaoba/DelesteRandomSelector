package test;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import com.ranfa.lib.Scraping;
import com.ranfa.lib.Song;

public class FileIOTest {

	private ArrayList<Song> webData = Scraping.getWholeData();

	// Local write test
	@Test
	public void writeToLocalTest() {

		assertTrue(Scraping.writeToJson(webData));

	}

	// Local read test
	@Test
	public void readFromLocalTest() {

		assertTrue(Arrays.equals(webData.toArray(), Scraping.getFromJson().toArray()));

	}

}

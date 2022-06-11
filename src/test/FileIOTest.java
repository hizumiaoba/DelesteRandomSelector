package test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.ranfa.lib.database.Scraping;
import com.ranfa.lib.database.Song;

public class FileIOTest {

	private List<Song> webData = Scraping.getWholeData();

	// Local write test
	@Test
	public void writeToLocalTest() {

		assertTrue(Scraping.writeToJson(webData));

	}

	// Local read test
	@Test
	public void readFromLocalTest() {

		assertTrue(webData.size() == Scraping.getFromJson().size());

	}

}

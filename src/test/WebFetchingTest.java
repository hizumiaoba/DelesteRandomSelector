package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ranfa.lib.database.EstimateAlbumTypeCycle;
import com.ranfa.lib.database.Scraping;
import com.ranfa.lib.database.Song;

public class WebFetchingTest {

	private String wrongValueString = "Invaild Value";
	private List<Song> emptyList = new ArrayList<>();
	private List<Song> correctList = Scraping.getWholeData();
	private String emptyMessageString = "ArrayList must not empty.";

	// Scraping.getSpecificAttributeSongs
	@Test
	public void getSpecificAttributeSongsTest() {

		assertThrows(emptyMessageString, IllegalArgumentException.class, () -> Scraping.getSpecificAttributeSongs(emptyList, Scraping.ALL));
		assertThrows(String.format("Illegal attribute value: %s", wrongValueString), IllegalArgumentException.class, () -> Scraping.getSpecificAttributeSongs(correctList, wrongValueString));

	}

	// Scraping.getSpecificDifficultySongs
	@Test
	public void getSpecificDifficultySongsTest() {

		assertThrows(emptyMessageString, IllegalArgumentException.class, () -> Scraping.getSpecificDifficultySongs(emptyList, Scraping.DEBUT));
		assertThrows(String.format("Illegal difficulty value: %s", wrongValueString), IllegalArgumentException.class, () -> Scraping.getSpecificDifficultySongs(correctList, wrongValueString));

	}

	// Scraping.getSpecificLevelSongs
	@Test
	public void getSpecifiLevelSongsTest() {

		assertThrows(emptyMessageString, IllegalArgumentException.class, () -> Scraping.getSpecificLevelSongs(emptyList, 18, true, false));
		assertThrows("Level must not negative.", IllegalArgumentException.class, () -> Scraping.getSpecificLevelSongs(correctList, -1, true, false));
		assertThrows("Illegal boolean value.", IllegalArgumentException.class, () -> Scraping.getSpecificLevelSongs(correctList, 18, false, false));

	}

	// Scraping.getSpecificAlbumTypeSongs
	@Test
	public void getSpecificAlbumTypeSongsTest() {

		assertThrows(emptyMessageString, IllegalArgumentException.class, () -> Scraping.getSpecificAlbumTypeSongs(emptyList, EstimateAlbumTypeCycle.ALBUM_A));
		assertThrows("type must not null.", IllegalArgumentException.class, () -> Scraping.getSpecificAlbumTypeSongs(correctList, null));

	}

}

package test;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import com.ranfa.lib.Easter;

import test.mocks.EasterMock;

public class EasterTest {

	private Easter easter = new Easter();

	// nonNull tests
	@Test
	public void nonNullWebData() {
		assertNotNull(this.easter.fetchBirthData());
	}

	@Test
	public void nonNullLocalData() {
		assertNotNull(this.easter.readBirthData());
	}

	@Test
	public void specifiedEasterTest() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(2020, 6, 13);
		Date mutsumi = new Date(calendar.toInstant().toEpochMilli());
		calendar.clear();
		calendar.set(2020, 9, 27);
		Date fumika = new Date(calendar.toInstant().toEpochMilli());
		String mutsumiBirthString = null;
		String fumikaBirthString = null;
		try {
			mutsumiBirthString = EasterMock.getSpecifiedDateBirth(mutsumi);
			fumikaBirthString = EasterMock.getSpecifiedDateBirth(fumika);
		} catch (ClassNotFoundException | SecurityException | NoSuchFieldException | IllegalArgumentException
				| IllegalAccessException e) {
			fail(e.getMessage());
		}
		
		assertNotNull(mutsumiBirthString);
		assertNotNull(fumikaBirthString);
		
		assertTrue(mutsumiBirthString.contains(" 「お誕生日って、なんだかわくわくしますよねっ！」"));
		assertTrue(fumikaBirthString.contains(" 「素敵な物語の1頁を、一緒に刻んでいきましょう…プロデューサーさん」"));
	}
}

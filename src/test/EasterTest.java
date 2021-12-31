package test;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.ranfa.lib.Easter;

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

}

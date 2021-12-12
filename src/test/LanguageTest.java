package test;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.After;
import org.junit.Test;

import com.ranfa.main.Messages;

public class LanguageTest {

	// Declare field
	private Locale defaultLocale = Locale.getDefault();
	private Locale jp = Locale.JAPAN;
	private Locale us = Locale.US;

	@Test
	public void jpTest() {
		// Set locale
		Locale.setDefault(jp);

		// asserts
		assertEquals("開始！", Messages.MSGCalcStart.toString());
	}

	@Test
	public void enTest() {
		//set locale
		Locale.setDefault(us);

		// asserts
		assertEquals("Start!", Messages.MSGCalcStart.toString());
	}

	@After
	public void revertDefault() {
		Locale.setDefault(defaultLocale);
	}

}

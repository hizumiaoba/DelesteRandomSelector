package test;

import static org.junit.Assert.assertTrue;

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
		Locale.setDefault(this.jp);

		// asserts
		assertTrue(Messages.MSGCalcStart.toString().equals("開始！"));
	}

	@Test
	public void enTest() {
		//set locale
		Locale.setDefault(this.us);

		// asserts
		assertTrue(Messages.MSGCalcStart.toString().equals("Start!"));
	}

	@After
	public void revertDefault() {
		Locale.setDefault(this.defaultLocale);
	}

}

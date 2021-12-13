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
		assertTrue(Messages.MSGDatabaseNotExist.toString().equals("楽曲データベースが見つかりませんでした。自動的に作成されます…\n注意：初回起動ではなく、かつ、Jarファイルと同じ階層に\"database.json\"というファイルが存在するにも関わらず\nこのポップアップが出た場合、開発者までご一報ください。\nGithub URL: https://github.com/hizumiaoba/DelesteRandomSelector/issues"));
		assertTrue(Messages.MSGAlbumTypeBeingCalculated.toString().equals("計算中..."));
	}

	@Test
	public void enTest() {
		//set locale
		Locale.setDefault(this.us);

		// asserts
		assertTrue(Messages.MSGCalcStart.toString().equals("Start!"));
		assertTrue(Messages.MSGDatabaseNotExist.toString().equals("Music database does not exist.\nIt will be automatically created.\nATTENTION:There is the JSON file named \"database.json\" in the same directory which executable is contained and you see this pop up,\nPlease contact the Developer.\nGithub URL: https://github.com/hizumiaoba/DelesteRandomSelector/issues"));
		assertTrue(Messages.MSGAlbumTypeBeingCalculated.toString().equals("Simulating..."));
	}

	@After
	public void revertDefault() {
		Locale.setDefault(this.defaultLocale);
	}

}

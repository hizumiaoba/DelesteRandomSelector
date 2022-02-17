package test;

import static org.junit.Assert.*;

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
		assertEquals("開始！", Messages.MSGCalcStart.toString());
		assertEquals("楽曲データベースが見つかりませんでした。自動的に作成されます…\n注意：初回起動ではなく、かつ、Jarファイルと同じ階層に\"database.json\"というファイルが存在するにも関わらず\nこのポップアップが出た場合、開発者までご一報ください。\nGithub URL: https://github.com/hizumiaoba/DelesteRandomSelector/issues", Messages.MSGDatabaseNotExist.toString());
		assertEquals(Messages.MSGAlbumTypeBeingCalculated.toString(), "計算中...");
		assertEquals(Messages.MSGNarrowingDownSongs.toString(), "<html><body>楽曲<br>絞り込み</body></html>");
		assertEquals(Messages.MSGTitle.toString(), "デレステ課題曲セレクター");
		assertEquals(Messages.MSGSelectDifficulty.toString(), "難易度選択");
		assertEquals(Messages.MSGNonSelected.toString(), "指定なし");
		assertEquals(Messages.MSGSongLevel.toString(), "楽曲Lv");
		assertTrue(Messages.MSGBelowSpecificLevel.toString().equals("指定Lv以下"));
		assertTrue(Messages.MSGOverSpecificLevel.toString().equals("指定Lv以上"));
		assertTrue(Messages.MSGLevelCheckboxInfo.toString().equals("<html><body>以下以上両方にチェックをつけることで指定レベルのみ絞り込むことができます</body></html>"));
		assertTrue(Messages.MSGUpdatingDatabase.toString().equals("<html><body>データベース<br>更新中…</body></html>"));
		assertTrue(Messages.MSGCompleteNarrowDown.toString().equals("絞り込み完了！「開始」をクリックすることで選曲できます！"));
		assertTrue(Messages.MSGNumberOfSongs.toString().equals("曲目："));
		assertTrue(Messages.MSGThisPhrase.toString().equals("この"));
		assertTrue(Messages.MSGPlayPhrase.toString().equals("曲をプレイしましょう！！！"));
		assertTrue(Messages.MSGTwitterIntegration.toString().equals("Twitter連携"));
		assertTrue(Messages.MSGUsingThisAppPhrase.toString().equals("デレステ課題曲セレクターで\n"));
		assertTrue(Messages.MSGNotPlayYet.toString().equals("ちひろ「まだプレイを始めていないみたいですね」"));
		assertTrue(Messages.MSGTwitterPlayOtherwisePhrase.toString().equals("…その他数曲\nをプレイしました！\n"));
		assertTrue(Messages.MSGTwitterPlayOnlyPhrase.toString().equals("をプレイしました！"));
		assertTrue(Messages.MSGTwitterIntegrationConfirm.toString().equals("Twitterへ以下の内容を投稿します。よろしいですか？\n\n"));
		assertTrue(Messages.MSGStringLength.toString().equals("\n\n文字数："));
		assertTrue(Messages.MSGCompletePost.toString().equals("投稿が完了しました。"));
		assertTrue(Messages.MSGTerminate.toString().equals("終了"));
		assertTrue(Messages.MSGInternalYpdateNotDoneYet.toString().equals("内部更新処理が完了していません。少し待ってからやり直してください。"));
		assertTrue(Messages.MSGNarrowDownProcedure.toString().equals("楽曲選択の手順\r\n１．難易度、属性、レベルを選択する\r\n２．「楽曲取り込み」ボタンを押す！\r\n３．「開始」ボタンを押す！\r\n４．選択された楽曲がここに表示されます！\r\n現在設定されている楽曲選択の最大数："));
		assertTrue(Messages.MSGCurrentAlbumType.toString().equals("\n現在のMASTER+アルバム周期（推定）："));
		assertTrue(Messages.MSGManualUpdate.toString().equals("<html><body>手動更新</body></html>"));
		assertTrue(Messages.MSGManualUpdateNotCompleteYet.toString().equals("手動更新が完了していません。もうしばらくお待ちください。"));
		assertEquals("設定", Messages.MSGConfigurations.toString());
	}

	@Test
	public void enTest() {
		//set locale
		Locale.setDefault(this.us);

		// asserts
		assertTrue(Messages.MSGCalcStart.toString().equals("Start!"));
		assertTrue(Messages.MSGDatabaseNotExist.toString().equals("Music database does not exist.\nIt will be automatically created.\nATTENTION:There is the JSON file named \"database.json\" in the same directory which executable is contained and you see this pop up,\nPlease contact the Developer.\nGithub URL: https://github.com/hizumiaoba/DelesteRandomSelector/issues"));
		assertTrue(Messages.MSGAlbumTypeBeingCalculated.toString().equals("Simulating..."));
		assertTrue(Messages.MSGNarrowingDownSongs.toString().equals("<html><body>Narrow down<br>songs</body></html>"));
		assertTrue(Messages.MSGTitle.toString().equals("DelesteRandomSelector"));
		assertTrue(Messages.MSGSelectDifficulty.toString().equals("Difficulty"));
		assertTrue(Messages.MSGNonSelected.toString().equals("Non-Select"));
		assertTrue(Messages.MSGSongLevel.toString().equals("Level"));
		assertTrue(Messages.MSGBelowSpecificLevel.toString().equals("Below Lv"));
		assertTrue(Messages.MSGOverSpecificLevel.toString().equals("Above Lv"));
		assertTrue(Messages.MSGLevelCheckboxInfo.toString().equals("<html><body>Only specified level songs will be selected if you mark both checkbox.</body></html>"));
		assertTrue(Messages.MSGUpdatingDatabase.toString().equals("<html><body>Updating<br>database</body></html>"));
		assertTrue(Messages.MSGCompleteNarrowDown.toString().equals("Narrowing down complete! Click \"Start!\" to play!"));
		assertTrue(Messages.MSGNumberOfSongs.toString().equals("tracks:"));
		assertTrue(Messages.MSGThisPhrase.toString().equals("Let's play these"));
		assertTrue(Messages.MSGPlayPhrase.toString().equals("songs!"));
		assertTrue(Messages.MSGTwitterIntegration.toString().equals("<html><body>Post to Twitter</body></html>"));
		assertTrue(Messages.MSGUsingThisAppPhrase.toString().equals("I played\n"));
		assertTrue(Messages.MSGNotPlayYet.toString().equals("It seems that you don't start playing yet?"));
		assertTrue(Messages.MSGTwitterPlayOtherwisePhrase.toString().equals("...and some songs!\n"));
		assertTrue(Messages.MSGTwitterPlayOnlyPhrase.toString().equals("songs!"));
		assertTrue(Messages.MSGTwitterIntegrationConfirm.toString().equals("Are you sure you want to post this?\n\n"));
		assertTrue(Messages.MSGStringLength.toString().equals("\n\nNumber of Characters"));
		assertTrue(Messages.MSGCompletePost.toString().equals("Complete to post."));
		assertTrue(Messages.MSGTerminate.toString().equals("End"));
		assertTrue(Messages.MSGInternalYpdateNotDoneYet.toString().equals("Internal update in progress. Please wait a moment."));
		assertTrue(Messages.MSGNarrowDownProcedure.toString().equals("How to select songs\r\n1.Select difficulty, attribute, and level.\r\n2.Click [Narrow down songs] button.\r\n3.Click [start!] button.\r\n4.Selected songs will be shown here!\r\nThe maximum number of selected songs:"));
		assertTrue(Messages.MSGCurrentAlbumType.toString().equals("\nCurrent MASTER+ ALBUM type(based on simulation):"));
		assertTrue(Messages.MSGManualUpdate.toString().equals("<html><body>Manual<br>Update</body></html>"));
		assertTrue(Messages.MSGManualUpdateNotCompleteYet.toString().equals("Manual Update has not been finished yet. Please wait a moment."));
		assertEquals("Config", Messages.MSGConfigurations.toString());
	}

	@After
	public void revertDefault() {
		Locale.setDefault(this.defaultLocale);
	}

}

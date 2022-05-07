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
		assertEquals("計算中...", Messages.MSGAlbumTypeBeingCalculated.toString());
		assertEquals("<html><body>楽曲<br>絞り込み</body></html>", Messages.MSGNarrowingDownSongs.toString());
		assertEquals("デレステ課題曲セレクター", Messages.MSGTitle.toString());
		assertEquals("難易度選択", Messages.MSGSelectDifficulty.toString());
		assertEquals("指定なし", Messages.MSGNonSelected.toString());
		assertEquals("楽曲Lv", Messages.MSGSongLevel.toString());
		assertEquals("指定Lv以下", Messages.MSGBelowSpecificLevel.toString());
		assertEquals("指定Lv以上", Messages.MSGOverSpecificLevel.toString());
		assertEquals("<html><body>以下以上両方にチェックをつけることで指定レベルのみ絞り込むことができます</body></html>", Messages.MSGLevelCheckboxInfo.toString());
		assertEquals("<html><body>データベース<br>更新中…</body></html>", Messages.MSGUpdatingDatabase.toString());
		assertEquals("絞り込み完了！「開始」をクリックすることで選曲できます！", Messages.MSGCompleteNarrowDown.toString());
		assertEquals("曲目：", Messages.MSGNumberOfSongs.toString());
		assertEquals("この", Messages.MSGThisPhrase.toString());
		assertEquals("曲をプレイしましょう！！！", Messages.MSGPlayPhrase.toString());
		assertEquals("Twitter連携", Messages.MSGTwitterIntegration.toString());
		assertEquals("デレステ課題曲セレクターで\n", Messages.MSGUsingThisAppPhrase.toString());
		assertEquals("ちひろ「まだプレイを始めていないみたいですね」", Messages.MSGNotPlayYet.toString());
		assertEquals("…その他数曲\nをプレイしました！\n", Messages.MSGTwitterPlayOtherwisePhrase.toString());
		assertEquals("をプレイしました！", Messages.MSGTwitterPlayOnlyPhrase.toString());
		assertEquals("Twitterへ以下の内容を投稿します。よろしいですか？\n\n", Messages.MSGTwitterIntegrationConfirm.toString());
		assertEquals("\n\n文字数：", Messages.MSGStringLength.toString());
		assertEquals("投稿が完了しました。", Messages.MSGCompletePost.toString());
		assertEquals("終了", Messages.MSGTerminate.toString());
		assertEquals("内部更新処理が完了していません。少し待ってからやり直してください。", Messages.MSGInternalYpdateNotDoneYet.toString());
		assertEquals("楽曲選択の手順\r\n１．難易度、属性、レベルを選択する\r\n２．「楽曲取り込み」ボタンを押す！\r\n３．「開始」ボタンを押す！\r\n４．選択された楽曲がここに表示されます！\r\n現在設定されている楽曲選択の最大数：", Messages.MSGNarrowDownProcedure.toString());
		assertEquals("\n現在のMASTER+アルバム周期（推定）：", Messages.MSGCurrentAlbumType.toString());
		assertEquals("<html><body>手動更新</body></html>", Messages.MSGManualUpdate.toString());
		assertEquals("手動更新が完了していません。もうしばらくお待ちください。", Messages.MSGManualUpdateNotCompleteYet.toString());
		assertEquals("設定", Messages.MSGConfigurations.toString());
		assertEquals("楽曲情報APIより取得中です。\nしばらくお待ちください。", Messages.MSGAPIWaitAPIFetch.toString());
		assertEquals("今回プレイした楽曲", Messages.MSGInfoPlayedSongs.toString());
		assertEquals("しばらくお待ちください…", Messages.MSGInfoWait.toString());
		assertEquals("曲名", Messages.MSGInfoSongName.toString());
		assertEquals("作詞者", Messages.MSGInfoLyricsBy.toString());
		assertEquals("曲タイプ", Messages.MSGInfoSongAttribute.toString());
		assertEquals("作曲者", Messages.MSGInfoComposedBy.toString());
		assertEquals("難易度", Messages.MSGInfoSongDifficulty.toString());
		assertEquals("編曲者", Messages.MSGInfoArrangedBy.toString());
		assertEquals("レベル", Messages.MSGInfoSongLevel.toString());
		assertEquals("登場メンバー", Messages.MSGInfoMember.toString());
		assertEquals("ノート数", Messages.MSGInfoSongNotes.toString());
		assertEquals("更に詳しい情報を見る", Messages.MSGInfoOpenBrowser.toString());
		assertEquals("前", Messages.MSGPrev.toString());
		assertEquals("次", Messages.MSGNext.toString());
		assertEquals("情報取得完了", Messages.MSGCompleteInformationParse.toString());
		assertEquals("楽曲情報", Messages.MSGInfoTab.toString());
	}

	@Test
	public void enTest() {
		//set locale
		Locale.setDefault(this.us);

		// asserts
		assertEquals("Start!", Messages.MSGCalcStart.toString());
		assertEquals("Music database does not exist.\nIt will be automatically created.\nATTENTION:There is the JSON file named \"database.json\" in the same directory which executable is contained and you see this pop up,\nPlease contact the Developer.\nGithub URL: https://github.com/hizumiaoba/DelesteRandomSelector/issues", Messages.MSGDatabaseNotExist.toString());
		assertEquals("Simulating...", Messages.MSGAlbumTypeBeingCalculated.toString());
		assertEquals("<html><body>Narrow down<br>songs</body></html>", Messages.MSGNarrowingDownSongs.toString());
		assertEquals("DelesteRandomSelector", Messages.MSGTitle.toString());
		assertEquals("Difficulty", Messages.MSGSelectDifficulty.toString());
		assertEquals("Non-Select", Messages.MSGNonSelected.toString());
		assertEquals("Level", Messages.MSGSongLevel.toString());
		assertEquals("Below Lv", Messages.MSGBelowSpecificLevel.toString());
		assertEquals("Above Lv", Messages.MSGOverSpecificLevel.toString());
		assertEquals("<html><body>Only specified level songs will be selected if you mark both checkbox.</body></html>", Messages.MSGLevelCheckboxInfo.toString());
		assertEquals("<html><body>Updating<br>database</body></html>", Messages.MSGUpdatingDatabase.toString());
		assertEquals("Narrowing down complete! Click \"Start!\" to play!", Messages.MSGCompleteNarrowDown.toString());
		assertEquals("tracks:", Messages.MSGNumberOfSongs.toString());
		assertEquals("Let's play these", Messages.MSGThisPhrase.toString());
		assertEquals("songs!", Messages.MSGPlayPhrase.toString());
		assertEquals("<html><body>Post to Twitter</body></html>", Messages.MSGTwitterIntegration.toString());
		assertEquals("I played\n", Messages.MSGUsingThisAppPhrase.toString());
		assertEquals("It seems that you don't start playing yet?", Messages.MSGNotPlayYet.toString());
		assertEquals("...and some songs!\n", Messages.MSGTwitterPlayOtherwisePhrase.toString());
		assertEquals("songs!", Messages.MSGTwitterPlayOnlyPhrase.toString());
		assertEquals("Are you sure you want to post this?\n\n", Messages.MSGTwitterIntegrationConfirm.toString());
		assertEquals("\n\nNumber of Characters", Messages.MSGStringLength.toString());
		assertEquals("Complete to post.", Messages.MSGCompletePost.toString());
		assertEquals("End", Messages.MSGTerminate.toString());
		assertEquals("Internal update in progress. Please wait a moment.", Messages.MSGInternalYpdateNotDoneYet.toString());
		assertEquals("How to select songs\r\n1.Select difficulty, attribute, and level.\r\n2.Click [Narrow down songs] button.\r\n3.Click [start!] button.\r\n4.Selected songs will be shown here!\r\nThe maximum number of selected songs:", Messages.MSGNarrowDownProcedure.toString());
		assertEquals("\nCurrent MASTER+ ALBUM type(based on simulation):", Messages.MSGCurrentAlbumType.toString());
		assertEquals("<html><body>Manual<br>Update</body></html>", Messages.MSGManualUpdate.toString());
		assertEquals("Manual Update has not been finished yet. Please wait a moment.", Messages.MSGManualUpdateNotCompleteYet.toString());
		assertEquals("Config", Messages.MSGConfigurations.toString());
		assertEquals("Getting informations from API. Please wait.", Messages.MSGAPIWaitAPIFetch.toString());
		assertEquals("The songs you played", Messages.MSGInfoPlayedSongs.toString());
		assertEquals("Please wait...", Messages.MSGInfoWait.toString());
		assertEquals("Song Name", Messages.MSGInfoSongName.toString());
		assertEquals("Lyrics By", Messages.MSGInfoLyricsBy.toString());
		assertEquals("Song Attribute", Messages.MSGInfoSongAttribute.toString());
		assertEquals("Composed By", Messages.MSGInfoComposedBy.toString());
		assertEquals("Difficulty", Messages.MSGInfoSongDifficulty.toString());
		assertEquals("Arranged By", Messages.MSGInfoArrangedBy.toString());
		assertEquals("Song Level", Messages.MSGInfoSongLevel.toString());
		assertEquals("Member", Messages.MSGInfoMember.toString());
		assertEquals("Notes", Messages.MSGInfoSongNotes.toString());
		assertEquals("More Information", Messages.MSGInfoOpenBrowser.toString());
		assertEquals("Prev", Messages.MSGPrev.toString());
		assertEquals("Next", Messages.MSGNext.toString());
		assertEquals("Information parse Complete.", Messages.MSGCompleteInformationParse.toString());
		assertEquals("Song Informations", Messages.MSGInfoTab.toString());
	}

	@After
	public void revertDefault() {
		Locale.setDefault(this.defaultLocale);
	}

}

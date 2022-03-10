package com.ranfa.main;

import java.util.Locale;
import java.util.ResourceBundle;

import org.slf4j.LoggerFactory;

/*
 * ja_JPはUTF-16BE
 */
public enum Messages {

	MSGCalcStart,
	MSGDatabaseNotExist,
	MSGAlbumTypeBeingCalculated,
	MSGNarrowingDownSongs,
	MSGTitle,
	MSGSelectDifficulty,
	MSGNonSelected,
	MSGSongLevel,
	MSGBelowSpecificLevel,
	MSGOverSpecificLevel,
	MSGLevelCheckboxInfo,
	MSGUpdatingDatabase,
	MSGCompleteNarrowDown,
	MSGNumberOfSongs,
	MSGThisPhrase,
	MSGPlayPhrase,
	MSGTwitterIntegration,
	MSGUsingThisAppPhrase,
	MSGNotPlayYet,
	MSGTwitterPlayOtherwisePhrase,
	MSGTwitterPlayOnlyPhrase,
	MSGTwitterIntegrationConfirm,
	MSGStringLength,
	MSGCompletePost,
	MSGTerminate,
	MSGInternalYpdateNotDoneYet,
	MSGNarrowDownProcedure,
	MSGCurrentAlbumType,
	MSGManualUpdate,
	MSGManualUpdateNotCompleteYet,
	MSGConfigurations,
	MSGAPIWaitAPIFetch,
	MSGInfoPlayedSongs,
	MSGInfoWait,
	MSGInfoSongName,
	MSGInfoLyricsBy,
	MSGInfoSongAttribute,
	MSGInfoComposedBy,
	MSGInfoSongDifficulty,
	MSGInfoArrangedBy,
	MSGInfoSongLevel,
	MSGInfoMember,
	MSGInfoSongNotes,
	MSGInfoOpenBrowser;

	@Override
	public String toString() {
		try {
			return ResourceBundle.getBundle("com.ranfa.languages.List", Locale.getDefault()).getString(this.name());
		} catch(Exception e) {
			LoggerFactory.getLogger(Messages.class).error("Exception was thrown while processing automatic translation.", e);
			System.exit(-1);
			return null;
		}
	}

}

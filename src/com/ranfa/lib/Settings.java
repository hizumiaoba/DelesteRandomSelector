package com.ranfa.lib;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 「settings.json」ファイルから各種設定を読み込みます
 * 設定できる内容は
 * ・起動時のバージョンチェック (checkVersion)
 * ・起動時にライブラリの更新有無のチェック (checkLibraryUpdates)
 * ・ウィンドウサイズ (windowWidth), (windowHeight)
 * ・デフォルトの曲指定上限数 (songsLimit)
 * ・スコアログの保存 (saveScoreLog)
 * ・標準出力へデバッグ用簡易ログを流すかどうか (outputDebugSentences)
 * の6つ(括弧内はフィールド名)
 * 今後、設定可能事項は増える可能性あり
 * @author hizum
 * @since v1.0.0
 *
 */
public class Settings {

	// 設定ファイルパス
	private final static String FILEPATH = "settings.json";

	public static boolean needToCheckVersion() {
		boolean res = true;
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode node = mapper.readTree(new File(FILEPATH));
			res = node.get("checkVersion").asBoolean();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}

	public static boolean needToCheckLibraryUpdates() {
		boolean res = true;
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode node = mapper.readTree(new File(FILEPATH));
			res = node.get("checkLibraryUpdates").asBoolean();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}

	public static int getWindowWidth() {
		int res = 640;
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode node = mapper.readTree(new File(FILEPATH));
			res = node.get("windowWidth").asInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}

	public static int getWindowHeight() {
		int res = 480;
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode node = mapper.readTree(new File(FILEPATH));
			res = node.get("windowHeight").asInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}

	public static int getSongsLimit() {
		int res = 3;
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode node = mapper.readTree(new File(FILEPATH));
			res = node.get("songsLimit").asInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}

	public static boolean saveScoreLog() {
		boolean res = false;
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode node = mapper.readTree(new File(FILEPATH));
			res = node.get("saveScoreLog").asBoolean();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}

	public static boolean outputDebugSentences() {
		boolean res = false;
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode node = mapper.readTree(new File(FILEPATH));
			res = node.get("outputDebugSentences").asBoolean();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}
}

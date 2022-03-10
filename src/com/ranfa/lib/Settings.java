package com.ranfa.lib;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * 「settings.json」ファイルから各種設定を読み込みます
 * 設定できる内容は
 * ・起動時のバージョンチェック (checkVersion)
 * ・起動時にライブラリの更新有無のチェック (checkLibraryUpdates)
 * ・ウィンドウサイズ (windowWidth), (windowHeight)
 * ・デフォルトの曲指定上限数 (songsLimit)
 * ・スコアログの保存 (saveScoreLog)
 * の5つ(括弧内はフィールド名)
 * 今後、設定可能事項は増える可能性あり
 * @author hizum
 * @since v1.0.0
 *
 */
public class Settings {

	// 設定ファイルパス
	private final static String FILEPATH = "generated/settings.json";
	private static Logger logger = LoggerFactory.getLogger(Settings.class);

	public static boolean fileExists() {
		Path path = Paths.get(FILEPATH);
		return Files.exists(path);
	}

	public static boolean needToCheckVersion() {
		boolean res = true;
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode node = mapper.readTree(new File(FILEPATH));
			res = node.get("checkVersion").asBoolean();
		} catch (IOException e) {
			logger.error("Couldn't read setting file.", e);
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
			logger.error("Couldn't read setting file.", e);
		}
		return res;
	}

	public static int getWindowWidth() {
		int res = 960;
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode node = mapper.readTree(new File(FILEPATH));
			res = node.get("windowWidth").asInt();
		} catch (IOException e) {
			logger.error("Couldn't read setting file.", e);
		}
		return res < 1 ? 640 : res;
	}

	public static int getWindowHeight() {
		int res = 540;
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode node = mapper.readTree(new File(FILEPATH));
			res = node.get("windowHeight").asInt();
		} catch (IOException e) {
			logger.error("Couldn't read setting file.", e);
		}
		return res < 1 ? 360 : res;
	}

	public static int getSongsLimit() {
		int res = 3;
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode node = mapper.readTree(new File(FILEPATH));
			res = node.get("songLimit").asInt();
		} catch (IOException e) {
			logger.error("Couldn't read setting file.", e);
		}
		return res < 1 ? 3 : res;
	}

	public static boolean saveScoreLog() {
		boolean res = false;
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode node = mapper.readTree(new File(FILEPATH));
			res = node.get("saveScoreLog").asBoolean();
		} catch (IOException e) {
			logger.error("Couldn't read setting file.", e);
		}
		return res;
	}

	public static boolean writeDownJSON() {
		boolean res = true;
		SettingJSONProperty property = new SettingJSONProperty();
		property.setCheckVersion(true);
		property.setCheckLibraryUpdates(true);
		property.setWindowWidth(960);
		property.setWindowHeight(540);
		property.setSongLimit(3);
		property.setSaveScoreLog(false);
		ObjectWriter writer = new ObjectMapper().writer(new DefaultPrettyPrinter());
		try {
			if(Files.notExists(Paths.get("generated")))
				Files.createDirectory(Paths.get("generated"));
			writer.writeValue(Paths.get(FILEPATH).toFile(), property);
		} catch (IOException e) {
			logger.error("Couldn't write down setting file.", e);
			res = false;
		}
		return res;
	}
}

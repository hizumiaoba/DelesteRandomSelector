package com.ranfa.lib;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JOptionPane;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ranfa.main.DelesteRandomSelector;

public class CheckVersion {

	private final static String URI = "https://raw.githubusercontent.com/hizumiaoba/DelesteRandomSelector/CU-k5vk2f_DelesteRandomSelector_hizumiaoba/version.json";
	private final static String RELEASE_STRING = "https://github.com/hizumiaoba/DelesteRandomSelector/releases";

	public static void needToBeUpdated() {
		int latestMajor = 0;
		int latestMinor = 0;
		int latestPatch = 0;
		try {
			JsonNode node = new ObjectMapper().readTree(new URL(URI));
			latestMajor = node.get("major").asInt();
			latestMinor = node.get("minor").asInt();
			latestPatch = node.get("patch").asInt();
		} catch (JsonMappingException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		if(latestPatch > getPatchVersion()) {
			JOptionPane.showInputDialog(null, "ソフトウェアのバグ修正が公開されています。こちらから最新バージョンをダウンロードしてください。\n", RELEASE_STRING);
		} else if(latestMinor > getMinorVersion()) {
			JOptionPane.showInputDialog(null, "ソフトウェアの軽微な機能改修が公開されています。こちらから最新バージョンをダウンロードしてください。\n", RELEASE_STRING);
		} else if(latestMajor > getMajorVersion()) {
			JOptionPane.showInputDialog(null, "大規模なソフトウェアの更新が公開されています。速やかにアップデートをお願いします。\n" + RELEASE_STRING);
		}
	}


	/**
	 * アノテーションで記載されているバージョンを取得します
	 * @since v1.0.0
	 * @return アノテーションで定義されているバージョン
	 */
	public static String getVersion() {
		String value = "v"
				+ getMajorVersion() + "."
				+ getMinorVersion() + "."
				+ getPatchVersion();
		return value;
	}

	public static int getMajorVersion() {
		Version version = (Version) DelesteRandomSelector.class.getAnnotation(Version.class);
		return version.major();
	}

	public static int getMinorVersion() {
		Version version = (Version) DelesteRandomSelector.class.getAnnotation(Version.class);
		return version.minor();
	}

	public static int getPatchVersion() {
		Version version = (Version) DelesteRandomSelector.class.getAnnotation(Version.class);
		return version.patch();
	}

}

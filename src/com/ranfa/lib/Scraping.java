package com.ranfa.lib;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Scraping {

	private final static String URI = "https://imascg-slstage-wiki.gamerch.com/楽曲詳細一覧";

	public static ArrayList<String> getWholeData() {
		if(databaseExists())
			return null;
		ArrayList<String> res = new ArrayList<>();
		try {
			Document document = Jsoup.parse(new URL(URI), 0);
			Elements rows = document.getElementsByTag("tbody").get(0).select("tr");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}

	public static boolean databaseExists() {
		Path path = Paths.get(URI);
		return Files.exists(path);
	}
}

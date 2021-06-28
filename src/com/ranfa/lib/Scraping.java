package com.ranfa.lib;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Scraping {

	private final static String URI = "https://imascg-slstage-wiki.gamerch.com/楽曲詳細一覧";

	public static ArrayList<String> getWholeData() {
		ArrayList<String> res = new ArrayList<>();
		try {
			Document document = Jsoup.parse(new URL(URI), 0);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

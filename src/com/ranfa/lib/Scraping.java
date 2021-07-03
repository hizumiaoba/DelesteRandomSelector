package com.ranfa.lib;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Scraping {

	private final static String URI = "https://imascg-slstage-wiki.gamerch.com/%E6%A5%BD%E6%9B%B2%E8%A9%B3%E7%B4%B0%E4%B8%80%E8%A6%A7";
	private final static String DBPATH = "database.json";
	public final static String ALL = "全タイプ";
	public final static String CUTE = "キュート";
	public final static String COOL = "クール";
	public final static String PASSION = "パッション";
	public final static String DEBUT = "DEBUT";
	public final static String REGULAR = "REGULAR";
	public final static String PRO = "PRO";
	public final static String MASTER = "MASTER";
	public final static String MASTERPLUS = "MASTER+";
	public final static String LEGACYMASTERPLUS = "ⓁMASTER+";

	public static boolean databaseExists() {
		Path path = Paths.get(DBPATH);
		return Files.exists(path);
	}

	public static synchronized ArrayList<Song> getWholeData() {
		// if(databaseExists())
		// 	return null;
		ArrayList<Song> res = new ArrayList<>();
		try {

			Document document = Jsoup.connect(URI)
					.userAgent("Java/DeresteRandomSelector  More information is available at https://github.com/hizumiaoba/DeresteRandomSelector/")
					.maxBodySize(0)
					.timeout(0)
					.get();
			Elements rows = document.getElementsByTag("tbody").get(0).select("tr");
			for(int i = 0; i < rows.size(); i++) {
				String attribute = rows.get(i).select("td").get(0).text();
				String name = rows.get(i).select("td").get(1).text();
				String difficulty = rows.get(i).select("td").get(2).text();
				int level = Integer.parseInt(rows.get(i).select("td").get(3).text());
				int notes = 0;
				if(rows.get(i).select("td").get(5).text().indexOf(",") == -1) {
					notes = Integer.parseInt(rows.get(i).select("td").get(5).text());
				} else {
					String temp = rows.get(i).select("td").get(5).text();
					String first = temp.substring(0, temp.indexOf(","));
					String end = temp.substring(temp.indexOf(",") + 1);
					notes = Integer.parseInt(first + end);
				}
				Song tmp = new Song(attribute, name, difficulty, level, notes);
				res.add(tmp);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}

	public static synchronized ArrayList<Song> getSpecificAttributeSongs(ArrayList<Song> data, String attribute) {
		if(!attribute.equals(ALL) && !attribute.equals(CUTE) && !attribute.equals(COOL) && !attribute.equals(PASSION))
			throw new IllegalArgumentException("Illegal attribute value.");
		if(data.isEmpty())
			throw new IllegalArgumentException("ArrayList must not empty.");
		ArrayList<Song> res = new ArrayList<Song>();
		for(int i = 0; i < data.size(); i ++) {
			if(data.get(i).getAttribute().equals(attribute))
				res.add(data.get(i));
		}
		return res;
	}

	public static synchronized ArrayList<Song> getSpecificDifficultySongs(ArrayList<Song> data, String difficulty) {
		if(!difficulty.equals(DEBUT) && !difficulty.equals(REGULAR) && !difficulty.equals(PRO) && !difficulty.equals(MASTER) && !difficulty.equals(MASTERPLUS) && !difficulty.equals(LEGACYMASTERPLUS))
			throw new IllegalArgumentException("Illegal difficulty value.");
		if(data.isEmpty())
			throw new IllegalArgumentException("ArrayList must not empty.");
		ArrayList<Song> res = new ArrayList<Song>();
		for(int i = 0; i < data.size(); i++) {
			if(data.get(i).getDifficulty().equals(difficulty))
				res.add(data.get(i));
		}
		return res;
	}

	public static synchronized ArrayList<Song> getSpecificLevelSongs(ArrayList<Song> data, int level, boolean isLess, boolean isOnly) {
		if(level <= 0)
			throw new IllegalArgumentException("Level must not negative.");
		if(data.isEmpty())
			throw new IllegalArgumentException("ArrayList must not empty.");
		if(isOnly)
			return getOnlyLevelSongs(data, level);
		ArrayList<Song> res = new ArrayList<Song>();
		if(isLess) {
			for(int i = 0; i < data.size(); i++) {
				if(data.get(i).getLevel() < level)
					res.add(data.get(i));
			}
		} else {
			for (int i = 0; i < data.size(); i++) {
				if(data.get(i).getLevel() > level)
					res.add(data.get(i));
			}
		}
		return res;
	}

	private static synchronized ArrayList<Song> getOnlyLevelSongs(ArrayList<Song> data, int level) {
		if(level < 0)
			throw new IllegalArgumentException("Level must not negative.");
		if(data.isEmpty())
			throw new IllegalArgumentException("ArrayList must not empty");
		ArrayList<Song> res = new ArrayList<Song>();
		for(int i = 0; i < data.size(); i++) {
			if(data.get(i).getLevel() == level)
				res.add(data.get(i));
		}
		return res;

	}
}
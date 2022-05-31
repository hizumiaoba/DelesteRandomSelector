package com.ranfa.lib.database;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.ranfa.lib.concurrent.CountedThreadFactory;
import com.ranfa.lib.database.AlbumTypeEstimate.MASTERPLUS_TYPE;
import com.ranfa.main.Messages;

public class Scraping {

	private final static String URI = "https://imascg-slstage-wiki.gamerch.com/%E6%A5%BD%E6%9B%B2%E8%A9%B3%E7%B4%B0%E4%B8%80%E8%A6%A7";
	private final static String DBPATH = "generated/database.json";
	private static Logger logger = LoggerFactory.getLogger(Scraping.class);
	public final static String NONSELECTED = "指定なし";
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
	public final static String LIGHT = "LIGHT";
	public final static String TRICK = "TRICK";
	public final static String PIANO = "PIANO";
	public final static String FORTE = "FORTE";
	public final static String WITCH = "WITCH";

	public static boolean databaseExists() {
		Path path = Paths.get(DBPATH);
		return Files.exists(path);
	}

	public static String getDBPath() {
		return DBPATH;
	}

	public static List<Song> getWholeData() {
		long time = System.currentTimeMillis();
		ExecutorService es = Executors.newCachedThreadPool(new CountedThreadFactory(() -> "DRS", "WebdataFetchThread"));
		CompletableFuture<ArrayList<ArrayList<Album>>> typeFetchFuture = CompletableFuture.supplyAsync(() -> AlbumTypeEstimate.getAlbumType(), es);
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
			ArrayList<ArrayList<Album>> typeLists = typeFetchFuture.get();
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
				Song tmp = new Song();
				tmp.setAttribute(attribute);
				tmp.setName(name);
				tmp.setDifficulty(difficulty);
				tmp.setLevel(level);
				tmp.setNotes(notes);
				if(difficulty.equals(LEGACYMASTERPLUS)) {
					ArrayList<Album> newTypeList = typeLists.get(MASTERPLUS_TYPE.LEGACYMASTERPLUS.ordinal());
					newTypeList.stream()
					.filter(element -> element.getSongName().equals(name))
					.forEach(element -> {
						tmp.setAlbumType(element.getAlbumType());
					});
				} else if(difficulty.equals(MASTERPLUS)) {
					ArrayList<Album> legacyTypeList = typeLists.get(MASTERPLUS_TYPE.NEWMASTERPLUS.ordinal());
					legacyTypeList.stream()
					.filter(element -> element.getSongName().equals(name))
					.forEach(element -> {
						tmp.setAlbumType(element.getAlbumType());
					});
				} else {
					tmp.setAlbumType("Not-Implemented");
				}
				res.add(tmp);
			}
		} catch (IOException | InterruptedException | ExecutionException e) {
			logger.error("Exception was thrown during web scraping", e);
		}
		logger.info("scraping compeleted in {} ms", (System.currentTimeMillis() - time));
		return res;
	}

	public static List<Song> getSpecificAttributeSongs(List<Song> data, String attribute) {
		if(!attribute.equals(ALL)
				&& !attribute.equals(CUTE)
				&& !attribute.equals(COOL)
				&& !attribute.equals(PASSION)
				&& !attribute.equals(Messages.MSGNonSelected.toString())) {
			throw new IllegalArgumentException("Illegal attribute value: " + attribute);
		}
		if(data.isEmpty()) {
			JOptionPane.showMessageDialog(null, "指定された属性の曲は存在しません。\n条件を変えてみてください");
			throw new IllegalArgumentException("ArrayList must not empty.");
		}
		List<Song> res = new ArrayList<>();
		if(attribute.equals(Messages.MSGNonSelected.toString())) {
			res = data;
		} else {
			res = data.stream()
					.parallel()
					.filter(element -> element.getAttribute().equals(attribute))
					.collect(Collectors.toList());
		}
		return res;
	}

	public static  List<Song> getSpecificDifficultySongs(List<Song> data, String difficulty) {
		if(!difficulty.equals(DEBUT)
				&& !difficulty.equals(REGULAR)
				&& !difficulty.equals(PRO)
				&& !difficulty.equals(MASTER)
				&& !difficulty.equals(MASTERPLUS)
				&& !difficulty.equals(LEGACYMASTERPLUS)
				&& !difficulty.equals(LIGHT)
				&& !difficulty.equals(TRICK)
				&& !difficulty.equals(PIANO)
				&& !difficulty.equals(FORTE)
				&& !difficulty.equals(WITCH)
				&& !difficulty.equals(Messages.MSGNonSelected.toString())) {
			throw new IllegalArgumentException("Illegal difficulty value.");
		}
		if(data.isEmpty()) {
			throw new IllegalArgumentException("ArrayList must not empty.");
		}
		List<Song> res = new ArrayList<>();
		if(difficulty.equals(Messages.MSGNonSelected.toString())) {
			res = data;
		} else {
			res = data.stream()
					.parallel()
					.filter(element -> element.getDifficulty().equals(difficulty))
					.collect(Collectors.toList());
		}
		return res;
	}

	public static  List<Song> getSpecificLevelSongs(List<Song> data, int level, boolean isLess, boolean isMore) {
		if(level <= 0) {
			throw new IllegalArgumentException("Level must not negative.");
		}
		if(data.isEmpty()) {
			throw new IllegalArgumentException("ArrayList must not empty.");
		}
		if(!(isLess || isMore)) {
			throw new IllegalArgumentException("Illegal boolean value.");
		}
		if(isLess && isMore) {
			return getOnlyLevelSongs(data, level);
		}
		List<Song> res = new ArrayList<>();
		if(isLess) {
			res = data.stream()
					.parallel()
					.filter(element -> element.getLevel() < level)
					.collect(Collectors.toList());
		} else if (isMore) {
			res = data.stream()
					.parallel()
					.filter(element -> element.getLevel() > level)
					.collect(Collectors.toList());
		}
		return res;
	}

	public static List<Song> getSpecificAlbumTypeSongs(List<Song> data, String type) {
		if(type == null) {
			throw new IllegalArgumentException("type must not null.");
		}
		if(data.isEmpty()) {
			throw new IllegalArgumentException("ArrayList must not empty");
		}
		return data.stream()
				.parallel()
				.filter(element -> element.getAlbumType().equals(type))
				.collect(Collectors.toList());
	}

	private static List<Song> getOnlyLevelSongs(List<Song> data, int level) {
		return data.stream()
				.parallel()
				.filter(element -> element.getLevel() == level)
				.collect(Collectors.toList());
	}

	public static List<Song> getFromJson() {
		long time = System.currentTimeMillis();
		SongJSONProperty property = null;
		try {
			property = new ObjectMapper().readValue(new File(DBPATH), SongJSONProperty.class);
		} catch (IOException e) {
			logger.error("IOException was thrown during reading From JSON Database file.", e);
		}
		List<Song> res = new ArrayList<>();
		if(property != null) {
			res.addAll(property.getList());
		} else {
			throw new NullPointerException("json is null.");
		}
		logger.info("JSON reading compeleted in {} ms", (System.currentTimeMillis() - time));
		return res;
	}

	public static boolean writeToJson(List<Song> list) {
		boolean res = true;
		SongJSONProperty property = new SongJSONProperty();
		property.setList(list);
		ObjectWriter writer = new ObjectMapper().writer(new DefaultPrettyPrinter());
		try {
			if(Files.notExists(Paths.get("generated"))) {
				Files.createDirectory(Paths.get("generated"));
			}
			writer.writeValue(Paths.get(DBPATH).toFile(), property);
		} catch (IOException e) {
			logger.error("IOException was thrown during writing to JSON database file.", e);
			res = false;
		}
		return res;
	}
}

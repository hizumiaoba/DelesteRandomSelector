package com.ranfa.lib.database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ranfa.lib.concurrent.CountedThreadFactory;

public class AlbumTypeEstimate {

	public final static String ALBUM_DATA_URI = "https://imascg-slstage-wiki.gamerch.com/%E6%A5%BD%E6%9B%B2%E8%A9%B3%E7%B4%B0%E4%B8%80%E8%A6%A7%EF%BC%88%E9%9B%A3%E6%98%93%E5%BA%A6%E5%88%A5%EF%BC%89";
	private static ExecutorService executor = Executors.newCachedThreadPool(new CountedThreadFactory(() -> "DRS", "AlbumEstimateThread"));
	private static Logger logger = LoggerFactory.getLogger(AlbumTypeEstimate.class);

	//アルバム種類の定義
	public enum ALBUM_TYPE {
		// 全体曲+キュート
		ALBUM_A,
		//全体曲+クール
		ALBUM_B,
		//全体曲+パッション
		ALBUM_C
	};

	public enum MASTERPLUS_TYPE {
		//新MASTER＋
		NEWMASTERPLUS,
		//旧MASTER+
		LEGACYMASTERPLUS
	}

	public static ArrayList<ArrayList<Album>> getAlbumType() {
		long time = System.currentTimeMillis();
		ArrayList<ArrayList<Album>> res = new ArrayList<>();
		try {
			Document document = Jsoup.connect(ALBUM_DATA_URI)
					.userAgent("Java/DelesteRandomSelector  More Information is available at https://github.com/hizumiaoba/DeresteRandomSelector/")
					.maxBodySize(0)
					.timeout(0)
					.get();
			Elements elements = document.getElementsByTag("tbody");
			Elements newMasterPlus = elements.get(4).select("tr");
			Elements legacyMasterPlus = elements.get(5).select("tr");
			CompletableFuture<ArrayList<Album>> fetchNew = CompletableFuture.supplyAsync(() -> fetch(newMasterPlus), executor);
			CompletableFuture<ArrayList<Album>> fetchLegacy = CompletableFuture.supplyAsync(() -> fetch(legacyMasterPlus), executor);
			res.add(fetchNew.get());
			res.add(fetchLegacy.get());
		} catch (IOException | InterruptedException | ExecutionException e) {
			logger.warn("Exception was thrown while fetching Album type.", e);
		}
		logger.info("Album type fetched in {} ms", (System.currentTimeMillis() - time));
		return res;
	}

	private static ArrayList<Album> fetch(Elements elements) {
		ArrayList<Album> res = new ArrayList<>();
		elements.stream()
		.forEach(element -> {
			String type = element.select("td").get(0).text().isEmpty() ? "Not-implemented" : element.select("td").get(0).text();
			String songName = element.select("td").get(2).text();
			Album tmp = new Album();
			tmp.setSongName(songName);
			tmp.setAlbumType(type);
			res.add(tmp);
		});
		return res;
	}
}

package com.ranfa.lib;

import java.io.IOException;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class AlbumTypeEstimate {

	public final static String ALBUM_DATA_URI = "https://imascg-slstage-wiki.gamerch.com/%E6%A5%BD%E6%9B%B2%E8%A9%B3%E7%B4%B0%E4%B8%80%E8%A6%A7%EF%BC%88%E9%9B%A3%E6%98%93%E5%BA%A6%E5%88%A5%EF%BC%89";

	//アルバム種類の定義
	public enum ALBUM_TYPE {
		// 全体曲+キュート
		ALBUM_A,
		//全体曲+クール
		ALBUM_B,
		//全体曲+パッション
		ALBUM_C
	};

	public static Map<String, String> getLastModified() {
		long time = System.currentTimeMillis();
		try {
			Document document = Jsoup.connect(ALBUM_DATA_URI)
					.userAgent("Java/DelesteRandomSelector  More Information is available at https://github.com/hizumiaoba/DeresteRandomSelector/")
					.maxBodySize(0)
					.timeout(0)
					.get();
			Elements elements = document.head().getElementsByAttribute("Last-Modified");
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

}

package com.ranfa.lib;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class Easter {

	private final Date TODAY;
	private final String dATE_FORMAT_STRING = "M月d日";
	private final String BIRTH_DATA_URL = "generated/easter.json";
	private final String WEBURL = "https://imas.gamedbs.jp/cg/idol/birthday";

	private Logger logger = LoggerFactory.getLogger(Easter.class);
	private Map<String, String> BirthData = this.fetchBirthData();

	public Easter() {
		if(Files.notExists(Paths.get(this.BIRTH_DATA_URL))) {
			this.writeBirthData(this.BirthData);
		}
		this.logger.info("LeT's mAkE ThIs aPp FuNnY...");
		this.TODAY = new Date();
	}

	public Map<String, String> readBirthData() {
		Map<String, String> res = null;
		try {
			res = new ObjectMapper().readValue(Paths.get(this.BIRTH_DATA_URL).toFile(), new TypeReference<Map<String, String>>() {});
		} catch (IOException e) {
			this.logger.warn("Error while reading database from local.", e);
		}
		return res;
	}

	public void writeBirthData(Map<String, String> data) {
		ObjectWriter writer = new ObjectMapper().writer(new DefaultPrettyPrinter());
		try {
			writer.writeValue(Paths.get(this.BIRTH_DATA_URL).toFile(), data);
		} catch (IOException e) {
			this.logger.error("Error while writing to database for easter.", e);
		}
	}

	public Map<String, String> fetchBirthData() {
		Map<String, String> res = new HashMap<>();
		try {
			Document document = Jsoup.connect(this.WEBURL)
					.userAgent("java/DelesteRandomSelector More information is available at https;//github.com/hizumiaoba/DelesteRandomSelector/")
					.maxBodySize(0)
					.timeout(0)
					.get();
			Elements elements = document.getElementsByClass("font0");
			elements.stream()
			.forEachOrdered(element -> {
				String[] data = element.text().split(" ");
				res.put(data[1], data[0]);
			});
			if(elements.size() != res.size()) {
				throw new IllegalArgumentException(new IOException());
			}
		} catch (IOException e) {
			this.logger.error("Error while fetching data for easter. this is not good...", e);
		}
		return res;
	}

	public String getTodaysBirth() {
		Map<String, String> birthData = this.readBirthData();
		SimpleDateFormat format = new SimpleDateFormat(this.dATE_FORMAT_STRING);
		String todayDateString = format.format(this.TODAY);
		List<String> res =  birthData.entrySet().stream()
				.filter((e) -> e.getValue().equals(todayDateString))
				.map(e -> e.getKey())
				.collect(Collectors.toList());
		StringBuilder builder = new StringBuilder("Happy Birth Day! : ");
		for(String element : res) {
			builder.append(element);
		}
		if(builder.indexOf("氏家むつみ") != -1) {
			builder.append(" 「お誕生日って、なんだかわくわくしますよねっ！」");
		} else if(builder.indexOf("鷺沢文香") != -1) {
			builder.append(" 「素敵な物語の1頁を、一緒に刻んでいきましょう…プロデューサーさん」");
		}
		return res.isEmpty() ? "" : builder.toString();
	}

}

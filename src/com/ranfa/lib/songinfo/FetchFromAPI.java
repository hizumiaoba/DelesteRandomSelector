package com.ranfa.lib.songinfo;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.ranfa.lib.concurrent.CountedThreadFactory;

import HajimeAPI4J.api.HajimeAPI4J.List_Params;
import HajimeAPI4J.api.HajimeAPI4J.List_Type;
import HajimeAPI4J.api.HajimeAPI4J.Music_Params;
import HajimeAPI4J.api.HajimeAPI4J.Token;
import HajimeAPI4J.api.HajimeAPIBuilder;
import HajimeAPI4J.api.util.HajimeAPI4JImpl;
import HajimeAPI4J.api.util.datatype.Member;
import HajimeAPI4J.api.util.parse.ParseList;
import HajimeAPI4J.api.util.parse.ParseMusic;

public class FetchFromAPI {
	
	private Logger logger = LoggerFactory.getLogger(FetchFromAPI.class);
	
	private String songname;
	private ExecutorService apiExecutor = Executors.newFixedThreadPool(16, new CountedThreadFactory(() -> "DRS", "HajimeAPIInquerier"));
	private JsonNode node;
	
	// constructor
	public FetchFromAPI(String songname) {
		this.songname = songname;
		int taxId = Integer.parseInt(fetchList().get("tax_id"));
		HajimeAPI4JImpl impl = HajimeAPIBuilder.createDefault(Token.MUSIC)
				.addParameter(Music_Params.ID	, String.valueOf(taxId))
				.build();
		logger.info("fetch data : {}", taxId);
		node = impl.getAsync(apiExecutor).join();
	}
	
	private Map<String, String> fetchList() {
		HajimeAPI4JImpl api = HajimeAPIBuilder.createDefault(Token.LIST)
				.addParameter(List_Params.TYPE, List_Type.MUSIC.toString())
				.addParameter(List_Params.SEARCH, songname)
				.build();
		ParseList parse = new ParseList(api.getAsync(apiExecutor).join());
		for(Map<String, String> tmp : parse.converse().asList()) {
			if(tmp.get("name").contains(songname))
				return tmp;
		}
		return new HashMap<String, String>();
	}
	
	public Map<String, String> getInformation() {
		LinkedHashMap<String, String> result = new LinkedHashMap<>();
		result.put("songname", node.get("name").asText());
		result.put("link", node.get("link").asText());
		String lyric = "",
				composer = "",
				arrange = "";
		ParseMusic parse = new ParseMusic(node).converse();
		List<Map<String, Object>> lyricList = parse.getLyrics(),
				composerList = parse.getComposer(),
				arrangeList = parse.getArrange();
		lyric = CompletableFuture.supplyAsync(() -> getArrayedNames(lyricList), apiExecutor).join();
		composer = CompletableFuture.supplyAsync(() -> getArrayedNames(composerList), apiExecutor).join();
		arrange = CompletableFuture.supplyAsync(() -> getArrayedNames(arrangeList), apiExecutor).join();
		result.put("lyric", lyric);
		result.put("composer", composer);
		result.put("arrange", arrange);
		StringBuilder memberBuilder = new StringBuilder();
		for(Member tmpMember : parse.getMember()) {
			memberBuilder.append(tmpMember.getName()).append(",");
		}
		memberBuilder.deleteCharAt(memberBuilder.length() - 1);
		result.put("member", memberBuilder.toString());
		logger.info("data fetch complete. : {}", result);
		return result;
	}
	
	private String getArrayedNames(List<Map<String, Object>> data) {
		StringBuilder builder = new StringBuilder();
		for(Map<String, Object> tmp : data) {
			builder.append(tmp.get("name").toString()).append(",");
		}
		builder.deleteCharAt(builder.length() - 1);
		return builder.toString();
	}

}

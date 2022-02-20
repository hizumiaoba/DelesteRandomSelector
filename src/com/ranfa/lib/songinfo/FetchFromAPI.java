package com.ranfa.lib.songinfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ranfa.lib.concurrent.CountedThreadFactory;

import HajimeAPI4J.api.HajimeAPI4J.List_Params;
import HajimeAPI4J.api.HajimeAPI4J.List_Type;
import HajimeAPI4J.api.HajimeAPI4J.Music_Params;
import HajimeAPI4J.api.HajimeAPI4J.Token;
import HajimeAPI4J.api.HajimeAPIBuilder;
import HajimeAPI4J.api.util.HajimeAPI4JImpl;
import HajimeAPI4J.api.util.datatype.Member;

public class FetchFromAPI {
	
	private Logger logger = LoggerFactory.getLogger(FetchFromAPI.class);
	
	
	private ExecutorService apiExecutor = Executors.newCachedThreadPool(new CountedThreadFactory(() -> "DRS", "HajimeAPIInquerier"));
	private List<JsonNode> nodes;
	
	// constructor
	
	public FetchFromAPI(String... songnames) {
		List<CompletableFuture<JsonNode>> listFutures = new ArrayList<>();
		for(String songname : Arrays.asList(songnames)) {
			Map<String, Object> data = fetchList(songname);
			if(data.getOrDefault("error", "false").equals("true")) {
				JsonNode errorNode = new ObjectMapper().valueToTree(data);
				CompletableFuture<JsonNode> error = CompletableFuture.supplyAsync(() -> errorNode, apiExecutor);
				listFutures.add(error);
				continue;
			}
			int taxId = Integer.parseInt(data.get("song_id").toString());
			HajimeAPI4JImpl impl = HajimeAPIBuilder.createDefault(Token.MUSIC)
					.addParameter(Music_Params.ID	, String.valueOf(taxId))
					.build();
			logger.info("fetch data : {}", taxId);
			listFutures.add(impl.getAsync(apiExecutor));
		}
		CompletableFuture.allOf(listFutures.toArray(new CompletableFuture[listFutures.size()])).join();
		nodes = new ArrayList<>();
		listFutures.stream().forEach(cf -> {
			nodes.add(cf.join());
		});
	}
	
	private Map<String, Object> fetchList(String songname) {
		HajimeAPI4JImpl api = HajimeAPIBuilder.createDefault(Token.LIST)
				.addParameter(List_Params.TYPE, List_Type.MUSIC.toString())
				.addParameter(List_Params.SEARCH, songname)
				.build();
		List<Map<String, Object>> parse = Collections.emptyList();
		try {
			parse = new ObjectMapper().readValue( api.getAsync().join().traverse(), new TypeReference<List<Map<String, Object>>>() {});
			TimeUnit.SECONDS.sleep(1);
		} catch (IOException | InterruptedException e) {
			logger.error("Exception while processing json map");
		}
		for(Map<String, Object> tmp : parse ) {
			if(tmp.get("name").toString().equals(songname))
				return tmp;
		}
		HashMap<String, Object> altRes = new HashMap<>();
		altRes.put("error", "true");
		return altRes;
	}
	
	public List<Map<String, String>> getInformation() {
		List<Map<String, String>> resultList = new ArrayList<>();
		int nodeSize = nodes.size();
		try {
			for(JsonNode node : nodes) {
				LinkedHashMap<String, String> result = new LinkedHashMap<>();
				result.put("songname", node.get("name").asText());
				result.put("link", node.get("link").asText());
				String lyric = "",
						composer = "",
						arrange = "";
				TypeReference<List<Map<String, Object>>> typeRef = new TypeReference<List<Map<String,Object>>>() {};
				ObjectMapper mapper = new ObjectMapper();
				List<Map<String, Object>> lyricList = mapper.readValue(node.get("lyrics").traverse(), typeRef),
						composerList = mapper.readValue(node.get("composer").traverse(), typeRef),
						arrangeList = mapper.readValue(node.get("arrange").traverse(), typeRef);
				lyric = CompletableFuture.supplyAsync(() -> getArrayedNames(lyricList), apiExecutor).join();
				composer = CompletableFuture.supplyAsync(() -> getArrayedNames(composerList), apiExecutor).join();
				arrange = CompletableFuture.supplyAsync(() -> getArrayedNames(arrangeList), apiExecutor).join();
				result.put("lyric", lyric);
				result.put("composer", composer);
				result.put("arrange", arrange);
				StringBuilder memberBuilder = new StringBuilder();
				for(Member tmpMember : mapper.readValue(node.get("member").traverse(), new TypeReference<List<Member>>() {})) {
					memberBuilder.append(tmpMember.getName()).append(",");
				}
				memberBuilder.deleteCharAt(memberBuilder.length() - 1);
				result.put("member", memberBuilder.toString());
				logger.info("data fetch complete. : {}", result);
				resultList.add(result);
			}
		} catch(IOException e) {
			logger.warn("Exception while processing json", e);
			String errorStr = "No data";
			for(int i = 0; i < nodeSize; i++) {
				Map<String, String> tmp = new HashMap<>();
				JsonNode tmpNode = nodes.get(i);
				tmp.put("songname", tmpNode.get("name").asText());
				tmp.put("link", tmpNode.get("link").asText());
				tmp.put("lyric", errorStr);
				tmp.put("composer", errorStr);
				tmp.put("arrange", errorStr);
				tmp.put("member", errorStr);
				resultList.add(tmp);
			}
		}
		
		return resultList;
	}
	
	
	private String getArrayedNames(List<Map<String, Object>> data) {
		if(data == null) {
			return "No Data";
		}
		StringBuilder builder = new StringBuilder();
		for(Map<String, Object> tmp : data) {
			builder.append(tmp.get("name").toString()).append(",");
		}
		builder.deleteCharAt(builder.length() - 1);
		return builder.toString();
	}

}

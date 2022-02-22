package com.ranfa.lib.songinfo;

import java.io.IOException;
import java.text.Normalizer;
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

import HajimeAPI4J.api.HajimeAPI4J;
import HajimeAPI4J.api.HajimeAPI4J.List_Params;
import HajimeAPI4J.api.HajimeAPI4J.List_Type;
import HajimeAPI4J.api.HajimeAPI4J.Music_Params;
import HajimeAPI4J.api.HajimeAPI4J.Token;
import HajimeAPI4J.api.HajimeAPIBuilder;
import HajimeAPI4J.api.util.datatype.Member;
import HajimeAPI4J.exception.NoSuchURIException;

public class FetchFromAPI {
	
	private Logger logger = LoggerFactory.getLogger(FetchFromAPI.class);
	
	
	private ExecutorService localDispatcher = Executors.newCachedThreadPool(new CountedThreadFactory(() -> "DRS", "LocalDispatcher"));
	private List<JsonNode> nodes;
	
	// constructor
	
	public FetchFromAPI(String... songnames) {
		List<JsonNode> listFutures = new ArrayList<>();
		for(String songname : Arrays.asList(songnames)) {
			Map<String, Object> data = fetchList(songname);
			if(data.getOrDefault("error", "false").equals("true")) {
				JsonNode errorNode = new ObjectMapper().valueToTree(data);
				listFutures.add(errorNode);
				continue;
			}
			int taxId = Integer.parseInt(data.get("song_id").toString());
			HajimeAPI4J impl = HajimeAPIBuilder.createDefault(Token.MUSIC)
					.addParameter(Music_Params.ID	, String.valueOf(taxId))
					.build();
			logger.info("fetch data : {}", taxId);
			try {
				listFutures.add(impl.get());
			} catch (NoSuchURIException | IOException | InterruptedException e) {
				logger.error("Exception while processing json.", e);
			}
		}
		nodes = listFutures;
	}
	
	private Map<String, Object> fetchList(String songname) {
		HajimeAPI4J api = HajimeAPIBuilder.createDefault(Token.LIST)
				.addParameter(List_Params.TYPE, List_Type.MUSIC.toString())
				.addParameter(List_Params.SEARCH, songname)
				.build();
		List<Map<String, Object>> parse = Collections.emptyList();
		try {
			parse = new ObjectMapper().readValue( api.get().traverse(), new TypeReference<List<Map<String, Object>>>() {});
			TimeUnit.SECONDS.sleep(1);
		} catch (IOException | InterruptedException | NoSuchURIException e) {
			logger.error("Exception while processing json map");
		}
		if(parse == null) {
			parse = new ArrayList<>(1);
			Map<String, Object> tmp = new HashMap<>();
			tmp.put("error", "true");
			tmp.put("name", songname);
			parse.add(tmp);
		} else if(parse.isEmpty()) {
			parse = new ArrayList<>(1);
			Map<String, Object> tmp = new HashMap<>();
			tmp.put("error", "true");
			tmp.put("name", songname);
			parse.add(tmp);
		}
		for(Map<String, Object> tmp : parse ) {
			String normalizeApiName = Normalizer.normalize(tmp.get("name").toString(), Normalizer.Form.NFKD);
			String normalizeLocalName = Normalizer.normalize(songname, Normalizer.Form.NFKD);
			if(normalizeApiName.equalsIgnoreCase(normalizeLocalName))
				return tmp;
		}
		HashMap<String, Object> altRes = new HashMap<>();
		altRes.put("error", "true");
		altRes.put("name", songname);
		return altRes;
	}
	
	public List<Map<String, String>> getInformation() {
		List<Map<String, String>> resultList = new ArrayList<>();
		int nodeSize = nodes.size();
		try {
			TimeUnit.SECONDS.sleep(1);
			for(JsonNode node : nodes) {
				if(node == null) {
					Map<String, String> tmp = new HashMap<>();
					String errorStr = "Failed to get.";
					tmp.put("songname", errorStr);
					tmp.put("link", errorStr);
					tmp.put("lyric", errorStr);
					tmp.put("composer", errorStr);
					tmp.put("arrange", errorStr);
					tmp.put("member", errorStr);
					resultList.add(tmp);
					continue;
				}
				if(node.get("error") != null) {
					Map<String, String> tmp = new HashMap<>();
					String errorStr = "Failed to get.";
					tmp.put("songname", node.get("name").asText());
					tmp.put("link", errorStr);
					tmp.put("lyric", errorStr);
					tmp.put("composer", errorStr);
					tmp.put("arrange", errorStr);
					tmp.put("member", errorStr);
					resultList.add(tmp);
					continue;
				}
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
				lyric = CompletableFuture.supplyAsync(() -> getArrayedNames(lyricList), localDispatcher).join();
				composer = CompletableFuture.supplyAsync(() -> getArrayedNames(composerList), localDispatcher).join();
				arrange = CompletableFuture.supplyAsync(() -> getArrayedNames(arrangeList), localDispatcher).join();
				result.put("lyric", lyric);
				result.put("composer", composer);
				result.put("arrange", arrange);
				StringBuilder memberBuilder = new StringBuilder();
				for(Member tmpMember : mapper.readValue(node.get("member").traverse(), new TypeReference<List<Member>>() {})) {
					if(tmpMember.getProduction().equals("cg"))
						memberBuilder.append(tmpMember.getName()).append(",");
				}
				memberBuilder.deleteCharAt(memberBuilder.length() - 1);
				result.put("member", memberBuilder.toString());
				resultList.add(result);
			}
		} catch(IOException | InterruptedException e) {
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
		resultList.stream().forEach(map -> logger.debug("data fetch complete : {}", map));
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

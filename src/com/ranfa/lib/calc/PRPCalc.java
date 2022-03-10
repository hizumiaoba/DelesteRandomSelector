package com.ranfa.lib.calc;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PRPCalc {
	
	private final Logger logger = LoggerFactory.getLogger(PRPCalc.class);
	
	private static final String PRP_STORAGE_FILE_PATH = "generated/prp.json";
	private static final BigDecimal THAUSAND = BigDecimal.valueOf(1000);
	
	private List<Integer> TotalPRPList;
	private int TotalPRP;

	public PRPCalc() {
		if(Files.notExists(Paths.get(PRP_STORAGE_FILE_PATH)))
			generateEmptyPRPFile();
		TypeReference<List<Integer>> typeref = new TypeReference<List<Integer>>() {};
		try {
			TotalPRPList = new ObjectMapper().readValue(Paths.get(PRP_STORAGE_FILE_PATH).toFile(), typeref);
		} catch (IOException e) {
			logger.error("Couldn't read prp file from disk.", e);
		}
		TotalPRP = calcCurrentTotal();
	}
	
	public boolean generateEmptyPRPFile() {
		if(Files.notExists(Paths.get("generated")))
			try {
				Files.createDirectory(Paths.get("generated"));
				Files.createFile(Paths.get(PRP_STORAGE_FILE_PATH));
			} catch (IOException e) {
				logger.error("cannot make prp file.", e);
				return false;
			}
		return true;
	}
	
	public int calcCurrentTotal() {
		int res = 0;
		for(int val : TotalPRPList)
			res += val;
		return res;
	}
	
	public int getTotalPRP() {
		return TotalPRP;
	}
	
	public static int calcPRPFromScore(int score) {
		BigDecimal scoreDecimal = BigDecimal.valueOf(score);
		scoreDecimal.divide(THAUSAND, RoundingMode.DOWN);
		return scoreDecimal.intValueExact();
	}
}

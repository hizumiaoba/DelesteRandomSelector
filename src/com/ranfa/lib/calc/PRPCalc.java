/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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

/**
 * PRPの計算ライブラリ。
 * PRPの保管も兼ねています。
 * 
 * @author Ranfa
 * 
 *@since 4.0.0
 */
public class PRPCalc {
	
	// Logger
	private final Logger logger = LoggerFactory.getLogger(PRPCalc.class);
	
	/**
	 * PRPを保管しているファイルパス
	 */
	private static final String PRP_STORAGE_FILE_PATH = "generated/prp.json";
	
	/**
	 * 計算用のBigDecimal値
	 */
	private static final BigDecimal THAUSAND = BigDecimal.valueOf(1000);
	
	/**
	 * 合計PRP値を計算するために保管しているPRPのList
	 */
	private List<Integer> TotalPRPList;
	/**
	 * 合計PRP値
	 */
	private int TotalPRP;

	/**
	 * コンストラクタ。
	 * <p>
	 * 合計PRP値の算出に必要な値を読み出し、Listへ詰め込むまでを行います。
	 */
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
	
	/**
	 * 内容が空のPRP保管ファイルを生成します。
	 * <p>
	 * ファイルを生成するのみで内容の書き込みはしません。
	 * 
	 * @return ファイルの生成に成功した場合は<code>true</code>、それ以外は<code>false</code>
	 */
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
	
	/**
	 * {@link #TotalPRPList} を参照して現在時点の合計を算出します。
	 * @return 現在時点の合計PRP
	 */
	public int calcCurrentTotal() {
		int res = 0;
		for(int val : TotalPRPList)
			res += val;
		return res;
	}
	
	/**
	 * {@link #TotalPRP} を返します
	 * @return {@link #TotalPRP}の値
	 */
	public int getTotalPRP() {
		return TotalPRP;
	}
	
	/**
	 * 入力されたスコアからPRPを計算します。
	 * <p>
	 * PRPは「端数切捨て（スコア×0.001）」になります
	 * @param score 計算するスコア
	 * @return 入力から計算したPRP値
	 */
	public static int calcPRPFromScore(int score) {
		BigDecimal scoreDecimal = BigDecimal.valueOf(score);
		scoreDecimal.divide(THAUSAND, RoundingMode.DOWN);
		return scoreDecimal.intValueExact();
	}
}

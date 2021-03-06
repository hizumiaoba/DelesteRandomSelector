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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ranfa.lib.concurrent.CountedThreadFactory;

/**
 * ファン計算用とのライブラリ
 * ファン数計算とスコア計算を実装
 * @author hizum
 * 
 * @since 4.0.0
 */
public class FanCalc {
	
	// Logger
	private static final Logger logger = LoggerFactory.getLogger(FanCalc.class);
	/** 
	 * 非同期処理用のスレッドプール
	 */
	private static final ExecutorService async = Executors.newCachedThreadPool(new CountedThreadFactory(() -> "DRS", "FanCalcThread", false));
	
	/**
	 * 計算式は
	 * 端数切り上げ（スコア*0.001*ルーム補正値*センター、ゲスト効果補正値*プロデュース方針補正値)
	 * ルーム、センター、ゲスト、プロデュース方針の補正値は百分率。計算時に自動で変換します
	 * 
	 * @param score ライブで獲得したスコアを入力します
	 * @param room ルームアイテムによる補正値を百分率のまま(xxx%)入力します
	 * @param center センター、ゲスト効果による補正値を百分率のまま入力します
	 * @param produce プロデュース方針による補正値を百分率のまま入力します
	 * @param premium プレミアムパスによる補正値を百分率のまま入力します
	 * 
	 * @return 一人あたりの獲得ファン数。1回のライブで獲得出来るファン数はこの値の5倍です
	 */
	public static int fan(int score, int room, int center, int produce, int premium) {
    	BigDecimal toPercent = new BigDecimal(100);
    	BigDecimal res = new BigDecimal(0);
    	BigDecimal roomPercent = new BigDecimal(room).divide(toPercent);
    	BigDecimal centerPercent = new BigDecimal(center).divide(toPercent);
    	BigDecimal producePercent = new BigDecimal(produce).divide(toPercent);
    	BigDecimal premiumPercent = new BigDecimal(premium).divide(toPercent);
    	BigDecimal corrections = new BigDecimal(-1)
    			.add(roomPercent)
    			.add(centerPercent);
    	res = res.add(new BigDecimal(score))
			.multiply(corrections)
    			.divide(new BigDecimal("1000"));
    	res = res.setScale(0,RoundingMode.UP);
	BigDecimal resCorrected = res.multiply(producePercent);
	resCorrected = resCorrected.setScale(0,RoundingMode.UP);
	BigDecimal resPremiumed = resCorrected.multiply(premiumPercent);
	resPremiumed = resPremiumed.setScale(0, RoundingMode.UP);
    	return (resPremiumed.compareTo(BigDecimal.ZERO) == 0) ? 0 : Integer.parseInt(resPremiumed.toString());
    }
	
	/**
	 * 計算式は
	 * 端数切り上げ（スコア*0.001*ルーム補正値*センター、ゲスト効果補正値*プロデュース方針補正値)
	 * ルーム、センター、ゲスト、プロデュース方針の補正値は百分率。計算時に自動で変換します
	 * 
	 * @param score スコア
	 * @param room ルーム補正値
	 * @param center センターアイドル補正値
	 * @param produce プロデュース方針補正値
	 * @param premium プレミアムパス補正値
	 * 
	 * @return 一人当たりの獲得ファン数がラップされているCompletableFuture
	 */
	public static CompletableFuture<Integer> fanAsync(int score, int room, int center, int produce, int premium) {
		return CompletableFuture.supplyAsync(() -> fan(score, room, center, produce, premium), async);
	}
	
	/**
	 * 目標スコアを計算。
	 * 初期実装の思想は並列処理による再帰計算。
	 * 
	 * @param fan 目標ファン
	 * @param multiplier LIVEの繰り返し回数
	 * @param room ルームアイテム補正値（百分率）
	 * @param center センター、ゲスト効果による補正値
	 * @param produce プロデュース方針にとる補正値
	 * @param premium プレミアムパスによる補正値
	 * 
	 * @return LIVE一回当たりの目標スコアがラップされているCompletableFuture
	 */
	public static CompletableFuture<Integer> scoreAsync(int fan, int multiplier, int room, int center, int produce, int premium) {
		return CompletableFuture.supplyAsync(() -> score(fan, multiplier, room, center, produce, premium), async);
	}
	
	/**
	 * 目標スコアを計算。
	 * 初期実装の思想は並列処理による再帰計算。
	 * 
	 * @param fan 目標ファン
	 * @param multiplier LIVEの繰り返し回数
	 * @param room ルームアイテム補正値（百分率）
	 * @param center センター、ゲスト効果による補正値
	 * @param produce プロデュース方針にとる補正値
	 * @param premium プレミアムパスによる補正値
	 * 
	 * @return LIVE一回当たりの目標スコア
	 */
	private static int score(int fan, int multiplier, int room, int center, int produce, int premium) {
		BigDecimal goalFan = new BigDecimal(fan).divide(new BigDecimal(multiplier), 0, RoundingMode.UP);
		final AtomicInteger result = new AtomicInteger(0);
		final AtomicBoolean flag = new AtomicBoolean(false);
			logger.info("Started to calculate score at dedicated thread.");
			while(!flag.get()) {
                int localFan = fan(result.incrementAndGet(), room, center, produce, premium) * 5;
                if(goalFan.compareTo(new BigDecimal(localFan)) <= 0) {
                    flag.set(true);
                }
            }
			logger.info("Finished calculating. Estimated Fan value : {}", result.intValue());
		return Integer.parseInt(result.toString());
	}
	

}

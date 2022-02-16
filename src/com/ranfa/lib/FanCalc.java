package com.ranfa.lib;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FanCalc {
	
	/**
	 * 計算式は
	 * 端数切り上げ（スコア*0.001*ルーム補正値*センター、ゲスト効果補正値*プロデュース方針補正値)
	 * ルーム、センター、ゲスト、プロデュース方針の補正値は百分率。計算時に自動で変換します
	 * @param score ライブで獲得したスコアを入力します
	 * @param room ルームアイテムによる補正値を百分率のまま(xxx%)入力します
	 * @param center センター、ゲスト効果による補正値を百分率のまま入力します
	 * @param produce プロデュース方針による補正値を百分率のまま入力します
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
    	return (resPremiumed.compareTo(BigDecimal.ZERO) == 0) || (resPremiumed == null) ? 0 : Integer.parseInt(resPremiumed.toString());
    }

}

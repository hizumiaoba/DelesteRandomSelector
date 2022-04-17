package test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ranfa.lib.calc.FanCalc;

public class FanCalcTest {
	
	@Test
	public void scoreToFanTest() {
		final int score = 1000000;
		// 端数切り上げ（スコア*0.001*ルーム補正値*センター、ゲスト効果補正値*プロデュース方針補正値)
		int actual = FanCalc.fan(score, 100, 100, 100, 100);
		assertEquals(1000, actual);
	}

}

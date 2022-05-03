package test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ranfa.lib.JST;

public class JSTTest {

	@Test
	public void JSTNonNullTest() {
		assertNotNull(JST.JSTNow());
	}
}

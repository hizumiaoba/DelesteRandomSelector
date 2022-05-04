package test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import com.ranfa.lib.JST;

public class JSTTest {

	@Test
	public void JSTNonNullTest() {
		try {
			assertNotNull(JST.JSTNow());
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
}

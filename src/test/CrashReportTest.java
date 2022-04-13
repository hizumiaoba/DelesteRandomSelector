package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ranfa.lib.handler.CrashHandler;

public class CrashReportTest {
	
	private static final String INPUT_DIR = "Crash-Report";
	
	private static List<Path> before;
	
	@BeforeClass
	public static void flushFolder() throws IOException {
		if(Files.notExists(Paths.get(INPUT_DIR)))
			Files.createDirectory(Paths.get(INPUT_DIR));
		before = Arrays.asList(Files.list(Paths.get(INPUT_DIR)).toArray(Path[]::new));
	}
	
	@Test
	public void generateSystemReportTest() {
		CrashHandler handle = new CrashHandler();
		assertNotNull(handle.outSystemInfo());
	}
	
	@Test
	public void generateCrashReportTest() {
		CrashHandler handle = new CrashHandler(new RuntimeException());
		assertEquals("Unexpected Error", handle.getDescription());
		assertEquals(Integer.MIN_VALUE, handle.getEstimateExitCode());
		assertEquals(handle.getThrowable().getClass(), RuntimeException.class);
	}

}

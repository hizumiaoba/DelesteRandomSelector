package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ranfa.lib.CheckVersion;

public class VersionTest {
	
	@Test
	@Ignore("Alpha, Betaバージョンはチェックを行わない")
	public void matchJSONandAnnotations() {
		String annotationVersion = CheckVersion.getVersion();
		int major, minor, patch;
		major = minor = patch = 0;
		try {
			JsonNode node = new ObjectMapper().readTree(Paths.get("version.json").toFile());
			major = node.get("major").asInt();
			minor = node.get("minor").asInt();
			patch = node.get("patch").asInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String jsonVersion = String.format("v%d.%d.%d", major, minor, patch);
		// check if json version and annotation version are equal.
		assertTrue(jsonVersion.equals(annotationVersion));
	}

}

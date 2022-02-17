package com.ranfa.lib.io;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OutputDataStructure implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// fields
	List<Map<String, String>> songList;
	
	public OutputDataStructure(List<Map<String, String>> songList) {
		this.songList = songList;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<Map<String, String>> getSongList() {
		return songList;
	}


	@Override
	public int hashCode() {
		return Objects.hash(songList);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof OutputDataStructure)) {
			return false;
		}
		OutputDataStructure other = (OutputDataStructure) obj;
		return Objects.equals(songList, other.songList);
	}
	
	
}

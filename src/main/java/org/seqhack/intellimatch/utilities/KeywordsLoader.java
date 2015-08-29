package org.seqhack.intellimatch.utilities;

import java.io.File;
import java.net.URL;

import org.codehaus.jackson.map.ObjectMapper;
import org.seqhack.intellimatch.models.KeyWords;

public class KeywordsLoader {

	public static KeyWords loadKeyWords() {
		URL url = KeywordsLoader.class.getClassLoader().getResource(
				"keywords.json");

		ObjectMapper mapper = new ObjectMapper();
		KeyWords keywords = null;
		try {
			keywords = mapper.readValue(new File("src/main/resources/keywords.json"),
					KeyWords.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return keywords;

	}
}

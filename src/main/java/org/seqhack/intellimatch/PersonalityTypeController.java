package org.seqhack.intellimatch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.seqhack.intellimatch.models.KeyWords;
import org.seqhack.intellimatch.models.PersonalityType;
import org.seqhack.intellimatch.models.Speech;
import org.seqhack.intellimatch.utilities.KeywordsLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/personality")
public class PersonalityTypeController {

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<PersonalityType> getType(@RequestBody Speech speech) {

		String text = speech.getText();
		String processedText = preprocessText(text);
		KeyWords keywords = KeywordsLoader.loadKeyWords();

		double extraverted = scorePercentage(
				matchingWords(processedText, keywords.getExtrovertedWords()),
				matchingWords(processedText, keywords.getIntrovertedWords()));

		double intuitive = scorePercentage(
				matchingWords(processedText, keywords.getIntuitiveWords()),
				matchingWords(processedText, keywords.getSensorWords()));

		double thinking = scorePercentage(
				matchingWords(processedText, keywords.getThinkerWords()),
				matchingWords(processedText, keywords.getFeelerWords()));

		double judgemental = scorePercentage(
				matchingWords(processedText, keywords.getJudgerWords()),
				matchingWords(processedText, keywords.getPerceiverWords()));

		PersonalityType personalityType = new PersonalityType(extraverted,
				intuitive, thinking, judgemental);
		return new ResponseEntity<PersonalityType>(personalityType,
				HttpStatus.OK);

	}

	public String preprocessText(String text) {
		String lowerCaseText = text.toLowerCase();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < lowerCaseText.length(); i++) {
			char c = lowerCaseText.charAt(i);
			if (c == ' ' || Character.isLetter(c))
				builder.append(c);

		}
		return builder.toString();
	}

	public Map<String, Integer> matchingWords(String text, List<String> keys) {
		Map<String, Integer> keyWords = new HashMap<String, Integer>();
		for (String key : keys)
			keyWords.put(key, 0);

		String[] words = text.split(" ");

		int i = 0;
		while (i < words.length) {
			String word = words[i];
			if (keyWords.containsKey(word)) {
				keyWords.put(word, keyWords.get(word) + 1);
			} else if (i + 1 < words.length
					&& keyWords.containsKey(word + words[i + 1])) {
				keyWords.put(word + words[i + 1],
						keyWords.get(word + words[i + 1]) + 1);
				i += 1;

			}
			i += 1;
		}
		return keyWords;

	}

	public Double scorePercentage(Map<String, Integer> typeWordsCount,
			Map<String, Integer> complementTypeWordsCount) {
		int typeKeywordCount = keyWordCount(typeWordsCount);
		int complementTypeKeywordCount = keyWordCount(complementTypeWordsCount);
		return (typeKeywordCount * 100.0)
				/ (typeKeywordCount + complementTypeKeywordCount);

	}

	public int keyWordCount(Map<String, Integer> wordCount) {
		int keywordCount = 0;
		for (Map.Entry<String, Integer> entry : wordCount.entrySet())
			keywordCount += entry.getValue();
		return keywordCount;
	}
}

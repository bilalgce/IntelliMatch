package org.seqhack.intellimatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.Math;

import org.seqhack.intellimatch.models.KeyWords;
import org.seqhack.intellimatch.models.PersonalityMatch;
import org.seqhack.intellimatch.models.PersonalityMatch.MatchingPersonality;
import org.seqhack.intellimatch.models.PersonalityType;
import org.seqhack.intellimatch.models.Speech;
import org.seqhack.intellimatch.utilities.DBUtil;
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
	public ResponseEntity<PersonalityMatch> getType(@RequestBody Speech speech) {

		String text = speech.getText();

		// Check if user's record already exists
		boolean recordExists = false;
		PersonalityType type = DBUtil.getType(speech.getEmail());
		if (type != null) {
			text += type.getText();
			recordExists = true;
		}

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

		PersonalityMatch personalityType = new PersonalityMatch(extraverted,
				intuitive, thinking, judgemental);
		personalityType.setEmail(speech.getEmail());
		personalityType.setFirstName(speech.getFirstName());
		personalityType.setLastName(speech.getLastName());

		matchmaking(personalityType, DBUtil.getType());

		if (!recordExists)
			DBUtil.addPersonalityType(speech.getFirstName(),
					speech.getLastName(), speech.getEmail(), processedText,
					extraverted, intuitive, thinking, judgemental);
		else
			DBUtil.updateType(type, processedText, extraverted, intuitive,
					thinking, judgemental);

		return new ResponseEntity<PersonalityMatch>(personalityType,
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

	public void matchmaking(PersonalityMatch myType,
			List<PersonalityType> allTypes) {
		if (allTypes.size() == 0)
			return;
		Map<Double, List<Integer>> jellers = new HashMap<Double, List<Integer>>();
		int i = -1;
		for (PersonalityType otherType : allTypes) {
			i += 1;
			if (otherType.getEmail() == myType.getEmail())
				continue;
			Double jelling = jellingFactor(myType, otherType);
			if (jellers.containsKey(jelling)) {
				List<Integer> currentScorer = jellers.get(jelling);
				currentScorer.add(i);
			} else {
				List<Integer> jeller = new ArrayList<Integer>();
				jeller.add(i);
				jellers.put(jelling, jeller);
			}

		}

		List<Double> sortedJellingFactors = new ArrayList<Double>();
		sortedJellingFactors.addAll(jellers.keySet());
		Collections.sort(sortedJellingFactors);
		Collections.reverse(sortedJellingFactors);

		List<PersonalityMatch.MatchingPersonality> sortedMatchers = new ArrayList<PersonalityMatch.MatchingPersonality>();
		for (Double jellingFactor : sortedJellingFactors) {
			for (Integer jellerIndex : jellers.get(jellingFactor)) {
				if (allTypes.get(jellerIndex).getEmail() == myType.getEmail())
					continue;
				MatchingPersonality personality = new MatchingPersonality();
				personality.setEmail(allTypes.get(jellerIndex).getEmail());
				personality.setFirstName(allTypes.get(jellerIndex)
						.getFirstName());
				personality
						.setLastName(allTypes.get(jellerIndex).getLastName());
				personality.setJellingFactor(jellingFactor);
				personality.setExtraverted(allTypes.get(jellerIndex)
						.getExtraverted());
				personality.setIntuitive(allTypes.get(jellerIndex)
						.getIntuitive());
				personality
						.setThinking(allTypes.get(jellerIndex).getThinking());
				personality.setJudgemental(allTypes.get(jellerIndex)
						.getJudgemental());
				sortedMatchers.add(personality);
			}

		}

		myType.setMatchers(sortedMatchers);

	}

	public Double jellingFactor(PersonalityMatch myType,
			PersonalityType otherType) {
		double jelling = 0;
		jelling += 100 - Math.abs(myType.getExtraverted()
				- otherType.getExtraverted());
		jelling += 100 - Math.abs(myType.getIntuitive()
				- otherType.getIntuitive());
		jelling += 100 - Math.abs(myType.getThinking()
				- otherType.getThinking());
		jelling += 100 - Math.abs(myType.getJudgemental()
				- otherType.getJudgemental());

		return jelling / 4;

	}
}

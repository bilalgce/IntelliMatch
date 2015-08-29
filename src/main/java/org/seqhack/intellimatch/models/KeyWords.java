package org.seqhack.intellimatch.models;

import java.util.List;

import lombok.Data;

@Data
public class KeyWords {
	private List<String> extrovertedWords;
	private List<String> introvertedWords;
	private List<String> intuitiveWords;
	private List<String> sensorWords;
	private List<String> thinkerWords;
	private List<String> feelerWords;
	private List<String> perceiverWords;
	private List<String> judgerWords;
}

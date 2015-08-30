package org.seqhack.intellimatch.models;

import java.util.List;

import lombok.Data;

@Data
public class PersonalityMatch {
	private String email;
	private String firstName;
	private String lastName;
	private final double extraverted;
	private final double intuitive;
	private final double thinking;
	private final double judgemental;

	private List<MatchingPersonality> matchers;

	@Data
	public static class MatchingPersonality {
		private String firstName;
		private String lastName;
		private String email;
		private double jellingFactor;
		private double extraverted;
		private double intuitive;
		private double thinking;
		private double judgemental;
		
	}

}

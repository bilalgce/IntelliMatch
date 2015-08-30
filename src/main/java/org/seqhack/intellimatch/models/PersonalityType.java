package org.seqhack.intellimatch.models;

import lombok.Data;

@Data
public class PersonalityType {
	private int id;
	private String email;
	private String firstName;
	private String lastName;
	private String text;
	private double extraverted;
	private double intuitive;
	private double thinking;
	private double judgemental;
}

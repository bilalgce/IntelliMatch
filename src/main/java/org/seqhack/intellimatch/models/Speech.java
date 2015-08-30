package org.seqhack.intellimatch.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Speech {
	private String id;
	private String firstName;
	private String lastName;
	private String email;
	private String text;
}

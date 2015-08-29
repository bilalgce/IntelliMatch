package org.seqhack.intellimatch;

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

		double thinking = 0;
		double extraverted = 0;
		double intuitive = 0;
		double judgemental = 0;
		
		
		PersonalityType personalityType = new PersonalityType(extraverted,
				intuitive, thinking, judgemental);
		return new ResponseEntity<PersonalityType>(personalityType,
				HttpStatus.OK);

	}
}

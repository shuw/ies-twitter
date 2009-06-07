package edu.shu.nlt.crunchbase.data;

import edu.nlt.shallow.data.Keyable;

public class Person implements Keyable {
	private String crunchbaseId;
	private String firstName;
	private String lastName;

	public Person(String crunchbaseId, String firstName, String lastName) {
		super();
		this.crunchbaseId = crunchbaseId;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getCrunchBaseId() {
		return crunchbaseId;
	}

	public String getFirstName() {
		return firstName;
	}

	@Override
	public String getKey() {
		return crunchbaseId;
	}

	public String getLastName() {
		return lastName;
	}

}

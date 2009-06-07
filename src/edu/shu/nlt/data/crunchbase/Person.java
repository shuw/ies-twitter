package edu.shu.nlt.data.crunchbase;

import edu.nlt.shallow.data.Keyable;

public class Person implements Keyable {
	private String firstName;
	private String lastName;
	private String crunchbaseId;

	public Person(String crunchbaseId, String firstName, String lastName) {
		super();
		this.crunchbaseId = crunchbaseId;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getCrunchBaseId() {
		return crunchbaseId;
	}

	@Override
	public String getKey() {
		return crunchbaseId;
	}

}

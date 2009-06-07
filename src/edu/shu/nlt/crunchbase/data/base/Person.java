package edu.shu.nlt.crunchbase.data.base;

import java.io.PrintStream;

import org.json.JSONException;
import org.json.JSONObject;

import edu.nlt.shallow.data.Keyable;

public class Person implements Keyable {
	public static Person getInstance(JSONObject jsonObject) {
		try {
			String crunchbaseID = jsonObject.getString("permalink");

			String firstName = jsonObject.getString("first_name");
			String lastName = jsonObject.getString("last_name");

			return new Person(crunchbaseID, firstName, lastName);
		} catch (JSONException ex) {
			throw new RuntimeException(ex);
		}

	}

	private String crunchbaseId;
	private String firstName;

	private String lastName;

	private Person(String crunchbaseId, String firstName, String lastName) {
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

	public void printDetails(PrintStream out) {
		out.println(getFirstName() + " " + getLastName() + " ID: " + getCrunchBaseId());
	}

}

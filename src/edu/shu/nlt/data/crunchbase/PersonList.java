package edu.shu.nlt.data.crunchbase;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PersonList {
	public PersonList(File file) {
		try {
			initialize(file);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Hashtable<String, Person> people;

	private void initialize(File file) throws JSONException, IOException {
		JSONArray jsonArray = JsonUtil.GetJsonArray(file);

		people = new Hashtable<String, Person>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			String crunchbaseID = jsonObject.getString("permalink");

			String firstName = jsonObject.getString("first_name");
			String lastName = jsonObject.getString("last_name");

			Person newPerson = new Person(crunchbaseID, firstName, lastName);

			people.put(getFullNameKey(firstName, lastName), newPerson);
		}
	}

	public Collection<Person> getPeople() {
		return people.values();
	}

	private static String getFullNameKey(String firstName, String lastName) {
		// if the last name contains multiple names, only take the last one
		// keying off of
		//
		// TODO: note that this means this table will only store 1 instance for
		// 2 people with the same first & last name
		String[] splitLastlastName = lastName.split(" ");

		return firstName.toLowerCase() + " " + splitLastlastName[splitLastlastName.length - 1].toLowerCase();
	}

	public Person getPerson(String firstName, String lastName) {

		return people.get(getFullNameKey(firstName, lastName));

	}

	public static void main(String[] args) throws JSONException, IOException {

		PersonList personList = new PersonList(new File("data/crunchbase/people.js"));

		for (Person person : personList.getPeople())
			System.out.println(person.getFirstName() + " " + person.getLastName() + " ID: " + person.getCrunchBaseId());

		System.out.println("Total # of companies: " + personList.getPeople().size());

	}
}

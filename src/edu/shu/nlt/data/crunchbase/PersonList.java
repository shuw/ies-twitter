package edu.shu.nlt.data.crunchbase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PersonList {
	public PersonList(File file) throws JSONException, IOException {
		initialize(file);
	}

	private List<Person> people;

	private void initialize(File file) throws JSONException, IOException {
		JSONArray jsonArray = JsonUtil.GetJsonArray(file);

		people = new ArrayList<Person>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			String crunchbaseID = jsonObject.getString("permalink");

			String firstName = jsonObject.getString("first_name");
			String lastName = jsonObject.getString("last_name");

			people.add(new Person(crunchbaseID, firstName, lastName));
		}
	}

	public Collection<Person> getPeople() {
		return people;
	}

	public static void main(String[] args) throws JSONException, IOException {

		PersonList personList = new PersonList(new File("data/crunchbase/people.js"));

		for (Person person : personList.getPeople())
			System.out.println(person.getFirstName() + " " + person.getLastName() + " ID: "
					+ person.getCrunchBaseId());

		System.out.println("Total # of companies: " + personList.getPeople().size());
	}
}

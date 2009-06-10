package edu.shu.nlt.crunchbase.data.lists;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONException;

import edu.shu.nlt.crunchbase.data.JsonUtil;
import edu.shu.nlt.crunchbase.data.base.Person;

public class PersonList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static String getFullNameKey(String firstName, String lastName) {
		// if the last name contains multiple names, only take the last one
		// keying off of
		//
		// TODO: note that this means this table will only store 1 instance for
		// 2 people with the same first & last name
		String[] splitLastlastName = lastName.split(" ");

		return firstName.toLowerCase() + " " + splitLastlastName[splitLastlastName.length - 1].toLowerCase();
	}

	public static void main(String[] args) throws JSONException, IOException {

		PersonList personList = new PersonList(new File("data/crunchbase/people.js"));

		for (Person person : personList.getPeople())
			person.printDetails(System.out);

		System.out.println("Total # of companies: " + personList.getPeople().size());

	}

	private Hashtable<String, Person> people;

	public PersonList(File file) {
		try {
			initializeInstances(file);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public PersonList() {
		people = new Hashtable<String, Person>();
	}

	private void initializeInstances(File file) throws JSONException, IOException {
		JSONArray jsonArray = JsonUtil.GetJsonArray(file);

		people = new Hashtable<String, Person>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			Person newPerson = Person.getInstance(jsonArray.getJSONObject(i));

			addPerson(newPerson);
		}
	}

	public void addPerson(Person person) {
		people.put(getFullNameKey(person.getFirstName(), person.getLastName()), person);
	}

	public Collection<Person> getPeople() {
		return people.values();
	}

	public Person getPerson(String firstName, String lastName) {
		return people.get(getFullNameKey(firstName, lastName));

	}

}

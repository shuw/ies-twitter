package edu.shu.nlt.ie;

public class NamedEntity {

	private String name;
	private Type type;

	public NamedEntity(String name, String type) {
		super();
		this.name = name;

		if (type.equals("ORGANIZATION")) {
			this.type = Type.Organization;
		} else if (type.equals("LOCATION")) {
			this.type = Type.Location;
		} else if (type.equals("MISC")) {
			this.type = Type.Misc;
		} else if (type.equals("PERSON")) {
			this.type = Type.Person;
		}

		else {
			throw new IllegalArgumentException("Unrecognized type:" + type);
		}

	}

	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}

	@Override
	public String toString() {

		return name + " (" + type + ")";
	}

	public enum Type {
		Misc, Location, Organization, Person
	}
}

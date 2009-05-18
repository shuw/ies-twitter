package edu.shu.nlt.twitter.ie;

public class NamedEntity {

	private String name;
	private String type;

	public NamedEntity(String name, String type) {
		super();
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {

		return name + " (" + type + ")";
	}
}

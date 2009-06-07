package edu.shu.nlt.crunchbase.data;

import edu.nlt.shallow.data.Keyable;

public class Company implements Keyable {
	private String crunchBaseId;
	private String name;

	public Company(String crunchBaseId, String name) {
		super();
		this.name = name;
		this.crunchBaseId = crunchBaseId;
	}

	public String getCrunchBaseId() {
		return crunchBaseId;
	}

	@Override
	public String getKey() {
		return crunchBaseId;
	}

	public String getName() {
		return name;
	}

	public void setCrunchBaseId(String crunchBaseId) {
		this.crunchBaseId = crunchBaseId;
	}

	public void setName(String name) {
		this.name = name;
	}

}

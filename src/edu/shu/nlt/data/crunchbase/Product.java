package edu.shu.nlt.data.crunchbase;

import edu.nlt.shallow.data.Keyable;

public class Product implements Keyable {
	private String name;
	private String crunchBaseId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCrunchBaseId() {
		return crunchBaseId;
	}

	public void setCrunchBaseId(String crunchBaseId) {
		this.crunchBaseId = crunchBaseId;
	}

	public Product(String crunchBaseId, String name) {
		super();
		this.name = name;
		this.crunchBaseId = crunchBaseId;
	}

	@Override
	public String getKey() {
		return crunchBaseId;
	}

}

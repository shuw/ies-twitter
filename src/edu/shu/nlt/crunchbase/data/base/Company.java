package edu.shu.nlt.crunchbase.data.base;

import java.io.PrintStream;

import org.json.JSONException;
import org.json.JSONObject;

import edu.nlt.shallow.data.Keyable;
import edu.shu.nlt.crunchbase.data.expanded.CompanyInfo;

public class Company implements Keyable {
	public static Company getInstance(JSONObject jsonObject) {
		try {
			String name = jsonObject.getString("name");
			String crunchbaseID = jsonObject.getString("permalink");

			return new Company(crunchbaseID, name);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}

	}

	private CompanyInfo companyInfo;

	private String crunchBaseId;

	private String name;

	private Company(String crunchBaseId, String name) {
		super();
		this.name = name;
		this.crunchBaseId = crunchBaseId;
	}

	public CompanyInfo getCompanyInfo() {
		if (companyInfo == null)
			companyInfo = CompanyInfo.getInstance(getCrunchBaseId());

		return companyInfo;
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

	public void printDetails(PrintStream out) {
		out.println(getName() + " ID: " + getCrunchBaseId());
	}

	public void setCrunchBaseId(String crunchBaseId) {
		this.crunchBaseId = crunchBaseId;
	}

	public void setName(String name) {
		this.name = name;
	}

}

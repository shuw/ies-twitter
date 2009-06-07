package edu.shu.nlt.twitter.crawler.data;

public class User {

	public static User getInstance(int id, String screenName, String name, String location, String description) {
		return new User(id, screenName, name, location, description);
	}

	/**
	 * @param value
	 *            Serialized value
	 * @return User
	 */
	public static User getInstance(String value) {
		value = value.replace("\t\t", "\t \t");
		value = value.replace("\t\n", "\t \n");
		
		String[] values = value.split("\n");

		String[] values2 = values[0].split("\t");

		String description = null;
		if (values.length == 2) {
			description = values[1];
		}

		String id = values2[0];
		String screenName = values2[1];
		String name = values2[2];

		String location = null;
		if (values2.length == 4) {
			location = values2[3];
		}

		return getInstance(Integer.parseInt(id), screenName, name, location, description);

	}

	/**
	 * Adapter for jtwitter class
	 * 
	 * @param user
	 * @return User
	 */
	public static User getInstance(winterwell.jtwitter.Twitter.User user) {
		return new User(user.getId(), user.getScreenName(), user.getName(), user.getLocation(), user.getDescription());
	}

	private String description;

	private int id;
	private String location;
	private String name;

	private String screenName;

	private User(int id, String screenName, String name, String location, String description) {
		super();
		this.id = id;
		this.screenName = Util.stripNewLineCharacters(screenName);
		this.name = Util.stripNewLineCharacters(name);
		this.location = Util.stripNewLineCharacters(location);

		this.description = Util.stripNewLineCharacters(description);
	}

	public String getDescription() {
		return description;
	}

	public int getId() {
		return id;
	}

	public String getLocation() {
		return location;
	}

	public String getName() {
		return name;
	}

	public String getScreenName() {
		return screenName;
	}

	public String serialize() {
		return id + "\t" + screenName + "\t" + name + "\t" + location + "\n" + description;
	}

}

package edu.shu.nlt.twitter.crawler.data;

import java.util.Date;

public class Status {

	public static Status getInstance(int id, String text, Date createdAt) {
		return new Status(id, text, createdAt);
	}

	public static Status getInstance(String value) {
		String[] values = value.split("\t");
		return getInstance(Integer.parseInt(values[0]), values[2], new Date(Long.parseLong(values[1])));
	}

	public static Status getInstance(winterwell.jtwitter.Twitter.Status status) {
		return getInstance(status.getId(), status.getText(), status.getCreatedAt());
	}

	public String serialize() {
		return id + "\t" + createdAt.getTime() + "\t" + Util.stripNewLineCharacters(text);
	}

	private Date createdAt;
	private int id;
	private String text;

	public Date getCreatedAt() {
		return createdAt;
	}

	public int getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	private Status(int id, String text, Date createdAt) {
		super();
		this.id = id;
		this.text = Util.stripNewLineCharacters(text);
		this.createdAt = createdAt;
	}

}

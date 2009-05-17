package edu.shu.nlt.twitter.crawler.data;

class Util {
	public static final String Delimiter = "!------------------------------";

	public static String stripNewLineCharacters(String value) {
		if (value != null) {
			return value.replace("\n", " ").replace("\t", " ");
		}
		return null;
	}

}

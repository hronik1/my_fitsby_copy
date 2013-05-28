package gravatar;

import java.util.Locale;


public class Gravatar {
	
	private static final int DEFAULT_SIZE = 120;
	private static final String DEFAULT_RATING = "pg";
	private static final String DEFAULT_IMAGE = "mm";
	private static final String GRAVATAR_BASE_URL = "http://www.gravatar.com/avatar/";
	
	public static String getGravatar(String email) {
		return getGravatar(email, DEFAULT_SIZE);
	}
	
	public static String getGravatar(String email, int size) {
		String stringURL = GRAVATAR_BASE_URL + MD5Util.md5Hex(format(email));
		stringURL += "?size=" + size;
		stringURL += "&rating=" + DEFAULT_RATING;
		stringURL += "&d=" + DEFAULT_IMAGE;
		return stringURL;
	}
	
	private static String format(String email) {
		String stripped = email.trim();
		return stripped.toLowerCase(Locale.ENGLISH);
	}
}

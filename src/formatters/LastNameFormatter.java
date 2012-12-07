package formatters;

public class LastNameFormatter {

	public static String format(String input) {
		if (input == null || input.trim().equals("")) {
			return "";
		} else {
			char[] letters = input.toCharArray();
			return letters[0] + ".";
		}
	}
	
}

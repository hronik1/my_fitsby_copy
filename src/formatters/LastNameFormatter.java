package formatters;

public class LastNameFormatter {

	public static String format(String input) {
		if (input == null || input.equals("")) {
			return "";
		} else {
			char[] letters = input.toCharArray();
			return letters[0] + ".";
		}
	}
	
}

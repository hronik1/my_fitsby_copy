package stepcounter;

/**
 * NextPowerOfTwo finds is responsible for calculating the next power of 2.
 * @author brenthronk
 *
 */
public class NextPowerOfTwo {

	/**
	 * Calculated the next integer greater to or equal to the input that is a
	 * power of 2.
	 * 
	 * Note, errors on negative and large elements will occur.
	 * 
	 * @param input		integer in which the next largest power of 2 is desired
	 * @return			next largest power of 2 than input
	 */
	public static int find(int input) {
		input--;
		for (int shiftAmount = 1; shiftAmount < Integer.SIZE; shiftAmount *= 2) {
			input |= input >> shiftAmount; 
		}
		input++;  
		return input;
	}
}

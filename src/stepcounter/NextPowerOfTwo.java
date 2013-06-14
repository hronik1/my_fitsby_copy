package stepcounter;

public class NextPowerOfTwo {

	public static int find(int input) {
		input--;
		input |= input >> 1;   // Divide by 2^k for consecutive doublings of k up to 32,
		input |= input >> 2;   // and then or the results.
		input |= input >> 4;
		input |= input >> 8;
		input |= input >> 16;
		input++;  
		return input;
	}
}

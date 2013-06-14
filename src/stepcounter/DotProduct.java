package stepcounter;

import java.util.Vector;

import android.util.Log;

/**
 * Class used to calculate dot products.
 * 
 * @author brenthronk
 *
 */
public class DotProduct {
	
	private final static String TAG = "DotProduct";
	
	/**
	 * Calculates the dot products of two vectors
	 * 
	 * @param a		first collection to be dotted
	 * @param b		second collection to be dotted
	 * @return 		the dot product of a and b
	 */
	public static double calculate(Vector<Double> a, Vector<Double> b) 
	{
		if (a.size() != b.size()) 
		{
			Log.d(TAG, "calculate invalid dimensions");
			return 0;
		}
		else 
		{
			double sum = 0;
			
			for (int i = 0; i < a.size(); i++) 
			{
				sum += (a.get(i)) * (b.get(i));
			}
			return sum;
		}
	}
}

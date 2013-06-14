package stepcounter;

public class Linspace {

	private double current;
	private final double end;
	private final double step;

	public Linspace(double start, double end, int totalCount) 
	{
		this.current = start;
		this.end = end;
		this.step = (double)((end - start) / (totalCount-1));
	}

	public boolean hasNext() 
	{
		if(current + step > (end)) // MAY stop floating point error
			return false;
		else 
			return true; 
	}

	public double getNextFloat() 
	{
			current += step;
			return current;
	}
	
}

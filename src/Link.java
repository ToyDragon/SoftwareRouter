
public class Link {

	private final NetworkDevice target;
	private int linkCost;
	private final double failRate;
	private List<Link> queue;
	
	public Link(NetworkDevice t, double f) throws IllegalArgumentException
	{
		if(f < 0.0 || f > 1.0)
			throw new IllegalArgumentException();
		queue = new List<Link>();
		target = t;		
	}
	
	public void setCost(int k) throws IllegalArgumentException
	{
		if(k <= 0)
			throw new IllegalArgumentException();
		linkCost = k;
	}
	
	public int getCost()
	{
		return linkCost;
	}
}

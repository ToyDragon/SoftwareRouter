
public class RoutingRow {
	final int source;
	Pair[] vector;
	
	public RoutingRow(int src, int n)
	{
		source = src;
		vector = new Pair[n];
		for(int i = 0; i < n; i++)
		{
			vector[i] = new Pair(); 
		}
	}

}

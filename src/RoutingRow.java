
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
	
	public RoutingRow(int src, Pair[] v)
	{
		source = src;
		vector = new Pair[v.length];
		for(int i = 0; i < v.length; i++)
			vector[i] = v[i];
	}

}

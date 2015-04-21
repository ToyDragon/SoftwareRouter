
public class RoutingPacket extends Packet{
	
	private final Pair[] payload;
	
	public RoutingPacket(int k, Pair[] o)
	{
		super(k);
		payload = o;
	}
	
	public Pair getPair(int i)
	{
		Pair temp = new Pair();
		temp.weight = payload[i].weight;
		temp.dest = payload[i].dest;
		
		return temp;
	}

}

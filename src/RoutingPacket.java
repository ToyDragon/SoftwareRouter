
public class RoutingPacket extends Packet
{	
	private final RoutingRow payload;
	
	public RoutingPacket(int k, RoutingRow o)
	{
		super(k);
		payload = o;
	}
	
	public RoutingRow getPayload()
	{		
		return payload;
	}
}
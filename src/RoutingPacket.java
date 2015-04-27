
public class RoutingPacket extends Packet
{	
	private final RoutingRow payload;
	
	public RoutingPacket(int des, int src, RoutingRow o)
	{
		super(des, src);
		payload = o;
	}
	
	public RoutingRow getPayload()
	{		
		return payload;
	}
}
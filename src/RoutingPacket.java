
public class RoutingPacket extends Packet{
	
	private final Object placeHolder;
	
	public RoutingPacket(int k, Object o)
	{
		super(k);
		placeHolder = o;
	}

}


public abstract class NetworkDevice {
	
	private static int tally = 0;
	
	private final int ID;
	private List<Link> out;
	
	public NetworkDevice()
	{
		ID = tally++;
	}
}

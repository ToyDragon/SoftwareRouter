import java.util.ArrayList;
import java.util.List;


public abstract class NetworkDevice {
	
	private static int tally = 0;
	
	private final int ID;
	private List<Link> outLinks;
	
	public NetworkDevice()
	{
		ID = tally++;
		outLinks = new ArrayList<Link>();
	}
	
	public void addLink(Link link){
		outLinks.add(link);
	}
}

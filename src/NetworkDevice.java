import java.util.ArrayList;
import java.util.List;


public abstract class NetworkDevice {
	
	private static int tally = 0;
	
	final int ID;
	List<Link> outLinks;
	List<Link> inLinks;
	
	public NetworkDevice()
	{
		ID = tally++;
		outLinks = new ArrayList<Link>();
	}
	
	public void addOutLink(Link link){
		outLinks.add(link);
	}
	
	public void addInLink(Link link){
		inLinks.add(link);
	}
	
	public boolean removeLink(Link link){
		return outLinks.remove(link) || inLinks.remove(link);
	}
	
	abstract void tick();
}

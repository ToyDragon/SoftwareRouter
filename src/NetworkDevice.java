import java.util.ArrayList;
import java.util.List;


public abstract class NetworkDevice {
	
	protected int ID;
	List<Link> outLinks;
	List<Link> inLinks;
	
	public NetworkDevice()
	{
		outLinks = new ArrayList<Link>();
		inLinks = new ArrayList<Link>();
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

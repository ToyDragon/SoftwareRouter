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
	
	public int getID()
	{
		return ID;		
	}
	
	public void process(Packet p)
	{
		if(p.getDest() == ID)
			System.out.println("Packet Received.");
		else
			System.out.println("Something went wrong.");
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
	
	private void updateTable()
	{}
}

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public abstract class NetworkDevice {
	private static int tally = 0;
	private static HashMap<Integer, NetworkDevice> idMap = new HashMap<Integer, NetworkDevice>();
	
	private int ID;
	protected int drawID;
	private boolean isDisabled;
	List<Link> outLinks;
	List<Link> inLinks;
	
	public NetworkDevice()
	{
		ID = tally++;
		outLinks = new ArrayList<Link>();
		inLinks = new ArrayList<Link>();
		idMap.put(ID, this);
	}
	
	public boolean isDisabled(){
		return isDisabled;
	}
	
	public int getID()
	{
		return ID;		
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

	public static NetworkDevice getDevice(int ID){
		return idMap.get(ID);
	}
}

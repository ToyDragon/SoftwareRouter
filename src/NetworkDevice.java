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
	
	public int drawx,drawy;
	
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
	
	public void setDisabled(boolean disabled){
		isDisabled = disabled;
		
		if(disabled){
			for(int i = outLinks.size()-1; i>=0; i--){
				outLinks.get(i).setDisabled(true);
				outLinks.get(i).getPartnerLink().setDisabled(true);
			}
			for(int i = inLinks.size()-1; i>=0; i--){
				inLinks.get(i).setDisabled(true);
				inLinks.get(i).getPartnerLink().setDisabled(true);
			}
		}
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
	
	public void process(Packet p)
	{}
	
	public void sendDV()
	{}
	
	public static NetworkDevice getDevice(int ID){
		return idMap.get(ID);
	}
}

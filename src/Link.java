import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class Link {

	private final NetworkDevice source;
	private final NetworkDevice target;
	private int linkCost;
	private final double failRate;
	private boolean isBusy;
	private Link partnerLink;
	private List<Packet> transitPackets;
	private Queue<Packet> arrivedPackets;
		
	public Link(NetworkDevice source, NetworkDevice target, double f) throws IllegalArgumentException
	{
		if(f < 0.0 || f > 1.0)
			throw new IllegalArgumentException();
		transitPackets = new ArrayList<Packet>();
		arrivedPackets = new LinkedList<Packet>();
		isBusy = false;
		failRate = f;
		this.target = target;		
		this.source = source;
	}
	
	public void setPartnerLink(Link partnerLink){
		this.partnerLink = partnerLink;
	}
	
	public void addPacket(Packet packet){
		if(!isBusy){
			isBusy = true;
			
			packet.timeInQueue = 0;
			transitPackets.add(packet);
		}
	}
	
	public void tick(){	
		for(int i = transitPackets.size()-1; i >= 0; i--){
			Packet packet = transitPackets.get(i);
			packet.timeInQueue++;
			
			if(packet.timeInQueue >= linkCost){
				transitPackets.remove(i);
				arrivedPackets.offer(packet);
			}
		}
		isBusy = false;
	}
	
	public Packet peekHead(){
		return arrivedPackets.peek();
	}
	
	public Packet popHead(){
		return arrivedPackets.poll();
	}
	
	public boolean isBusy(){
		return isBusy;
	}
	
	public NetworkDevice getDevice(){
		return target;
	}
	
	public void setCost(int k) throws IllegalArgumentException
	{
		if(k <= 0)
			throw new IllegalArgumentException();
		linkCost = k;
	}
	
	public int getCost()
	{
		return linkCost;
	}
}

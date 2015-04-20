import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class Link {

	private final NetworkDevice source;
	private final NetworkDevice target;
	private int linkCost;
	private int clock;
	public static double failRate;
	private boolean isBusy;
	private Link partnerLink;
	private MinHeap<Packet> transitPackets;
	private Queue<Packet> arrivedPackets;
		
	public Link(NetworkDevice source, NetworkDevice target, int cost)
	{
		transitPackets = new MinHeap<Packet>();
		arrivedPackets = new LinkedList<Packet>();
		isBusy = false;
		clock = 0;
		this.linkCost = cost;
		this.target = target;		
		this.source = source;
	}
	
	public NetworkDevice getSource(){
		return source;
	}
	
	public NetworkDevice getTarget(){
		return target;
	}
	
	public void setPartnerLink(Link partnerLink){
		this.partnerLink = partnerLink;
	}
	
	public Link getPartnerLink(){
		return partnerLink;
	}
	
	public void addPacket(Packet packet){
		if(!isBusy){
			isBusy = true;
			
			//packet.timeInQueue = 0;
			transitPackets.add(clock + linkCost, packet);
		}
	}
	
	public void tick(){	
		/*for(int i = transitPackets.size()-1; i >= 0; i--){
			Packet packet = transitPackets.get(i);
			packet.timeInQueue++;
			
			if(packet.timeInQueue >= linkCost){
				transitPackets.remove(i);
				arrivedPackets.offer(packet);
			}
		}*/
		clock++;
		boolean b = true;
		while(b)
		{
			b = false;
			try
			{
				if(!(transitPackets.isEmpty()) && transitPackets.peek() <= clock)
				{
					arrivedPackets.offer(transitPackets.extract());
					b = true;
				}
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
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

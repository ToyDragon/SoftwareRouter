import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class Link {

	private final NetworkDevice source;
	private final NetworkDevice target;
	private int linkCost;
	private Clock clock;
	public static double failRate;
	private boolean isBusy;
	private boolean isDisabled;
	private Link partnerLink;
	private MinHeap<Packet> transitPackets;
	private Queue<Packet> arrivedPackets;
		
	public Link(NetworkDevice source, NetworkDevice target, int cost, Clock c)
	{
		transitPackets = new MinHeap<Packet>();
		arrivedPackets = new LinkedList<Packet>();
		isBusy = false;
		clock = c;
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
	
	public boolean getDisabled(){
		return isDisabled;
	}
	
	public void setDisabled(boolean disabled){
		isDisabled = disabled;
	}
	
	public void setPartnerLink(Link partnerLink){
		this.partnerLink = partnerLink;
	}
	
	public Link getPartnerLink(){
		return partnerLink;
	}
	
	public void addPacket(Packet packet){
		if(!isBusy && !isDisabled){
			isBusy = true;
			
			//packet.timeInQueue = 0;
			transitPackets.add(clock.time() + linkCost, packet);
		}
	}
	
	public boolean hasPackets(){
		return !transitPackets.isEmpty();
	}
	
	public void tick(){	
		if(isDisabled)return;
		/*for(int i = transitPackets.size()-1; i >= 0; i--){
			Packet packet = transitPackets.get(i);
			packet.timeInQueue++;
			
			if(packet.timeInQueue >= linkCost){
				transitPackets.remove(i);
				arrivedPackets.offer(packet);
			}
		}*/

		boolean b = true;
		while(b)
		{
			b = false;
			try
			{
				if(!(transitPackets.isEmpty()) && transitPackets.peek() <= clock.time())
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

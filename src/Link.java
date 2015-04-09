import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class Link {

	private final NetworkDevice target;
	private int linkCost;
	private final double failRate;
	private List<Packet> transitPackets;
	private Queue<Packet> arrivedPackets;
	
	public Link(NetworkDevice t, double f) throws IllegalArgumentException
	{
		if(f < 0.0 || f > 1.0)
			throw new IllegalArgumentException();
		transitPackets = new ArrayList<Packet>();
		arrivedPackets = new LinkedList<Packet>();
		failRate = f;
		target = t;		
	}
	
	public void addPacket(Packet packet){
		packet.timeInQueue = 0;
		transitPackets.add(packet);
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

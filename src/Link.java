

public class Link {

	private final NetworkDevice source;
	private final NetworkDevice target;
	private int linkCost;
	private int clock;
	public static double failRate;
	private Link partnerLink;
	private MinHeap<Packet> transitPackets;
		
	public Link(NetworkDevice source, NetworkDevice target, int cost)
	{
		transitPackets = new MinHeap<Packet>();
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
	
	public void addPacket(Packet packet)
	{
			//packet.timeInQueue = 0;
			transitPackets.add(clock + linkCost, packet);
	}
	
	public void tick()
	{	
		clock++;
		boolean b = true;
		while(b)
		{
			b = false;
			try
			{
				if(!(transitPackets.isEmpty()) && transitPackets.peek() <= clock)
				{
					target.process(transitPackets.extract());
					b = true;
				}
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
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
		source.sendDV();
		target.sendDV();
	}
	
	public int getCost()
	{
		return linkCost;
	}
}

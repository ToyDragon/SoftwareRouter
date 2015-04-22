
public class Link {

	private final NetworkDevice source;
	private final NetworkDevice target;
	private int linkCost;
	private Clock clock;
	public static double failRate;
	private boolean isDisabled;
	private Link partnerLink;
	private MinHeap<Packet> transitPackets;
		
	public Link(NetworkDevice source, NetworkDevice target, int cost, Clock c)
	{
		transitPackets = new MinHeap<Packet>();
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
		if(!isDisabled){			
			//packet.timeInQueue = 0;
			transitPackets.add(clock.time() + linkCost, packet);
		}
	}
	
	public boolean hasPackets(){
		return !transitPackets.isEmpty();
	}
	
	public void tick()
	{	
		boolean b = true;
		while(b)
		{
			b = false;
			try
			{
				if(!(transitPackets.isEmpty()) && transitPackets.peek() <= clock.time())
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
	}
	
	public int getCost()
	{
		return linkCost;
	}
}

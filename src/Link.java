
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
		if(getPartnerLink().getDisabled() != disabled){
			getPartnerLink().setDisabled(disabled);
		}
		if(isDisabled){
			//setCost(Integer.MAX_VALUE);
			if(getTarget() instanceof Router){
				((Router)getTarget()).updateTable();
			}
			if(getSource() instanceof Router){
				((Router)getSource()).updateTable();
			}
			System.out.println("Update from removed link");
		}else{
			setCost(1);
		}
		

		if(getSource() instanceof Router){
			((Router)getSource()).removeVector(getTarget().getID());
		}
		if(getTarget() instanceof Router){
			((Router)getTarget()).removeVector(getSource().getID());
		}
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

					if(Math.random() < failRate){
						setDisabled(true);
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
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
		if(getPartnerLink().getCost() != k){
			getPartnerLink().setCost(k);
		}
		if(source instanceof Router)((Router)source).updateTable();
		if(target instanceof Router)((Router)target).updateTable();
	}
	
	public int getCost()
	{
		return linkCost;
	}
}

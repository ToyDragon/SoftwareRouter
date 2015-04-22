
import java.util.ArrayList;

public class Router extends NetworkDevice{

	private static int drawtally = 0;
	boolean hasTableChanged;
	private ArrayList<RoutingRow> neighborVectors;
	private Pair[] table;
	
	public Router(){
		super();
		drawID = drawtally++;
	}
	
	public void initializeTable(int n)
	{
		table = new Pair[n];
		neighborVectors = new ArrayList<RoutingRow>();
		sendDV();
	}
	
	public void sendDV()
	{
		//for each link
		{
			Link l = new Link(this,this,1);
			RoutingPacket temp = new RoutingPacket(l.getTarget().getID(),table);
		}
	}
	
	public void tick() {
		if(hasTableChanged){
			RoutingPacket routingPacket = getRoutingPacket();
			
			if(routingPacket != null){
				for(Link outLink : outLinks){
					if(!outLink.isBusy()){
						outLink.addPacket(routingPacket);
					}
				}
			}
		}
		
		for(Link sourceLink : inLinks){
			Packet toSend = sourceLink.peekHead();
			
			if(toSend instanceof RoutingPacket){
				updateRoutingTable((RoutingPacket)toSend);
				
			}else{
				Link destLink = getDestinationLink(toSend);
				
				if(destLink == null){
					//Drop packet if we can't forward it
					sourceLink.popHead();
				}else{
					if(!destLink.isBusy()){
						//if outbound link isn't busy forward the packet
						destLink.addPacket(toSend);
						sourceLink.popHead();
					}else{
						//if the outbound link is busy, just move on to the next input link
						//do nothing
					}
				}
			}
		}
	}
	
	public RoutingPacket getRoutingPacket(){
		//TODO
		//create and return a meaningful routing packet. maybe just a copy of the routing table?
		return null;
	}
	
	public void updateRoutingTable(RoutingPacket packet){
		//TODO
		//read packet and update our routing table appropriately
		//if it changed, set hasTableChanged to true, which will trigger new routing packets to send in next tick
	}
	
	public Link getDestinationLink(Packet packet){
		//TODO
		//use routing table to determine where packet should go, return outbound link
		return null;
	}
	
	public void addInLink(Link link){
		super.addInLink(link);
		//routing table will update when routingpacket is received
		//do not need to do anything else here
	}
	
	public void addOutLink(Link link){
		super.addOutLink(link);
		hasTableChanged = true;
		//TODO
		//Update routing table and set hasTableChanged to true if it has changed
	}
	
	public boolean removeLink(Link link){
		boolean value = super.removeLink(link);

		hasTableChanged = true;
		
		//TODO
		//Update routing table and set hasTableChanged to true if it has changed
		
		return value;
	}
	
}

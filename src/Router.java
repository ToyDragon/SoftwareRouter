
import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.FileNotFoundException;

public class Router extends NetworkDevice{

	private static int tally = 0;
	boolean hasTableChanged;
	private ArrayList<RoutingRow> neighborVectors;
	private Pair[] table;
	
	public Router(){
		super();
		ID = tally++;
	}
	
	public void initializeTable(int n)
	{
		table = new Pair[n];
		neighborVectors = new ArrayList<RoutingRow>();
		
		for(Link l : outLinks)
		{
			table[l.getTarget().getID()].dest = l;
			table[l.getTarget().getID()].weight = l.getCost();
		}
		sendDV();
	}
	
	public void sendDV()
	{
		for(Link l : outLinks)
		{
			l.addPacket(new RoutingPacket(l.getTarget().getID(), new RoutingRow(getID(), table)));
		}
	}
	
	private void updateTable()
	{
		boolean updated = false;
		Pair[] temp = new Pair[table.length];
		
		for(int i = 0; i < temp.length; i++)
			temp[i] = new Pair();
		for(RoutingRow r : neighborVectors)
		{
			for(Link l : outLinks)
			{
				if(l.getTarget().getID() == r.source)
				{
					for(int i = 0; i < temp.length; i++)
					{
						if(r.vector[i].weight + l.getCost() < temp[i].weight)
						{
							temp[i].weight = r.vector[i].weight + l.getCost();
							temp[i].dest = l; 
						}
					}
					break;
				}
			}
		}
		
		for(int i = 0; i < temp.length; i++)
		{
			if(table[i].weight > temp[i].weight)
			{
				table[i] =temp[i];
				updated = true;
			}
		}
		
		if(updated)
			sendDV();			
	}
	
	public void process(Packet p)
	{
		if(this.getID() == p.getDest())
		{
			boolean found = false;
			for(RoutingRow r : neighborVectors)
			{
				if(r.source == ((RoutingPacket)p).getPayload().source)
				{
					r = ((RoutingPacket)p).getPayload();
					found = true;
				}				
			}
			if(!found)
				neighborVectors.add(((RoutingPacket)p).getPayload());
			updateTable();
		}
		else
			table[p.getDest()].dest.addPacket(p);
		
	}
	
	/*public void tick() {
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
	}*/
	
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
	
	public void terminate()
	{
		try
		{
			PrintWriter out = new PrintWriter("Router"+getID());
			out.println("Source|Destination|Cost|Next" );
			for(int i = 0; i < table.length; i++)
			{
				out.println(getID() + " | " + i + " | " + table[i].weight + " | " + table[i].dest.getTarget().getID() );
			}
		}
		catch(FileNotFoundException e)
		{
		}
	}
	
}

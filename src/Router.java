
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Router extends NetworkDevice{

	private static int drawtally = 0;
	boolean hasTableChanged;
	private ArrayList<RoutingRow> neighborVectors;
	private Pair[] table;
	
	public Router(){
		super();
		drawID = drawtally++;
		initializeTable(50);
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
	
	public void process(Packet p)
	{
		p.mark(getID());
		if(this.getID() == p.getDest())
		{
			boolean found = false;
			for(int i = neighborVectors.size()-1; i>=0; i--)
			{
				RoutingRow r = neighborVectors.get(i);
				if(r.source == ((RoutingPacket)p).getPayload().source)
				{
					neighborVectors.set(i,((RoutingPacket)p).getPayload());
					found = true;
				}				
			}
			if(!found)
				neighborVectors.add(((RoutingPacket)p).getPayload());
			updateTable();
		}
		else{
			if(table[p.getDest()] == null || table[p.getDest()].dest == null){
				System.out.println("table["+p.getDest()+"] is null for "+getID());
				return;
			}
			System.out.println("From "+getID()+" to "+p.getDest()+": link " + table[p.getDest()].dest + " ("+table[p.getDest()].weight+")");
			table[p.getDest()].dest.addPacket(p);
		}
		
	}
	
	public void updateTable()
	{
		boolean updated = false;
		Pair[] temp = new Pair[table.length];
		
		for(int i = 0; i < temp.length; i++){
			temp[i] = table[i];
			if(temp[i] == null || (temp[i].dest != null && temp[i].dest.getDisabled()))
				temp[i] = new Pair();
		}
		for(RoutingRow r : neighborVectors)
		{
			for(Link l : outLinks)
			{
				if(l.getTarget().getID() == r.source)
				{
					//if a link changed cost, update it in the table
					for(int i = 0; i < temp.length; i++){
						Pair otherPair = r.vector[i];
						if(otherPair == null)otherPair = new Pair();
						int newCost = otherPair.weight + l.getCost();
						if(temp[i].dest == l && temp[i].weight != newCost && newCost >= 0 && newCost < Integer.MAX_VALUE){
							temp[i].weight = newCost;
						}
					}
					//find new shortest paths
					for(int i = 0; i < temp.length; i++)
					{
						Pair otherPair = r.vector[i];
						if(otherPair != null){
							int newCost = otherPair.weight + l.getCost();
							if(r.vector[i] != null && i!=getID() && newCost < Integer.MAX_VALUE && newCost >= 0 && newCost < temp[i].weight)
							{
								temp[i].weight = newCost;
								temp[i].dest = l;
								updated = true;
							}
						}
					}
					break;
				}
			}
		}
		if(updated){
			table = temp;
			sendDV();
		}
	}
	
	public void sendDV()
	{
		for(Link l : outLinks)
		{
			l.addPacket(new RoutingPacket(l.getTarget().getID(), this.getID(),new RoutingRow(getID(), table)));
		}
	}
	
	public void addInLink(Link link){
		super.addInLink(link);
		//routing table will update when routingpacket is received
		//do not need to do anything else here
	}
	
	public void addOutLink(Link link){
		super.addOutLink(link);
		hasTableChanged = true;
		table[link.getTarget().getID()] = new Pair();
		table[link.getTarget().getID()].dest = link;
		table[link.getTarget().getID()].weight = link.getCost();
		sendDV();
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
			File dir = new File("data");
			if(!dir.exists())dir.mkdir();
			File file = new File("data/Router"+getID()+".dat");
			PrintWriter out = new PrintWriter(file);
			out.println("Source|Destination|Cost|Next" );
			for(int i = 0; i < table.length; i++)
			{
				if(table[i] == null || table[i].dest == null)continue;
				out.println(getID() + " | " + i + " | " + table[i].weight + " | " + table[i].dest.getTarget().getID() );
			}
			out.close();
			System.out.println("Printed");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
}

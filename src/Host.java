import java.util.concurrent.CopyOnWriteArrayList;


public class Host extends NetworkDevice{

	private static int drawtally = 0;
	private CopyOnWriteArrayList<Packet> packetsToSend = new CopyOnWriteArrayList<Packet>();
	
	public Host(){
		super();
		drawID = drawtally++;
	}
	
	public void sendPacket(Packet packet){
		//packetsToSend.add(packet);
		for(Link link : outLinks)
		{
			link.addPacket(packet);
			break; //Yeah, this is a silly block, I know.
		}
	}
	
	/*public void tick(){
		while(packetsToSend.size() > 0){
			for(Link link : outLinks){
				if(!link.getDisabled()){
					link.addPacket(packetsToSend.remove(0));
					break;
				}
			}
		}
	}*/
}

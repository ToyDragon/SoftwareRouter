
public class Host extends NetworkDevice{

	private static int drawtally = 0;
	
	public Host(){
		super();
		drawID = drawtally++;
	}
	
	public void sendPacket(Packet packet){
		outLinks.get(0).addPacket(packet);
	}
	
	public void tick(){
		//TODO
	}
}

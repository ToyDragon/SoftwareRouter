
public class Packet {
	
	private final int dest;
	int timeInQueue;
	
	public Packet(int dest)
	{
		this.dest = dest;
	}
	
	public int getDest()
	{
		return dest;
	}

}

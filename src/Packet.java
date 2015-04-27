
public class Packet {
	
	private final int dest;
	int timeInQueue;
	private String path;
	
	public Packet(int dest, int src)
	{
		this.dest = dest;
		path = "" + src;
	}
	
	public int getDest()
	{
		return dest;
	}
	
	public void mark(int k)
	{
		path = path + ',' + k;
	}

	public String retrievePath()
	{
		return path;
	}
}


public class Packet {
	
	private final int dest;
	private final int src;
	int timeInQueue;
	private String path;
	
	public Packet(int dest, int src)
	{
		this.dest = dest;
		this.src = src;
		path = "" + src;
	}
	
	public int getDest()
	{
		return dest;
	}
	
	public int getSrc()
	{
		return src;		
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

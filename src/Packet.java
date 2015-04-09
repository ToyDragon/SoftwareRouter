
public class Packet {
	
	private final int des;
	
	public Packet(int des)
	{
		this.des = des;
	}
	
	boolean isDes(int k)
	{
		return k == des;
	}

}

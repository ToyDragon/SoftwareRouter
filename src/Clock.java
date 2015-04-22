
public class Clock {

	private int time;
	
	public Clock()
	{
		time = 0;
	}
	
	public void tick()
	{
		time++;
	}
	
	public int time()
	{
		return time;
	}
}

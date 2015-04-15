
public class Host extends NetworkDevice{

	private static int tally = 0;
	
	public Host(){
		super();
		ID = tally++;
	}
	
	public void tick(){
		//TODO
	}
}

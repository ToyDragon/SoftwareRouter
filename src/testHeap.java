
public class testHeap {

	public static void main(String[] args)
	{
		MinHeap<String> h = new MinHeap<String>();
		h.add(5, "Five");
		h.add(1, "One");
		h.add(8,"Eight");
		h.add(9,"Nine");
		h.add(17,"Seventeen");
		h.add(2, "Two");
		h.add(6,"Six");
		h.add(15,"Fifteen");
		
		while(!(h.isEmpty()))
		{
			try
			{
				System.out.println(h.extract());
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
			}
		}
		System.out.println("All done!");
	}
}

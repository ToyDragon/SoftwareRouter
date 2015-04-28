import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;


public class PathCache {
	public class Entry
	{
		final int src;
		private String[] arr;
		
		public Entry(int k)
		{
			src = k;
			arr = new String[3];
		}
		public Entry(int k, String s)
		{
			src = k;
			arr = new String[3];
			arr[0] = s;
		}
		
		public int getSrc()
		{
			return src;
		}
		
		public void add(String s)//Lazy coding follows. Array abuse.
		{
			if(arr[0].compareTo(s) == 0)
			{
				//Nothing happens...
			}
			else if(arr[1] == null || arr[1].compareTo(s) == 0)
			{	
				arr[1] = arr[0];
				arr[0] = s;
			}
			else//Doesn't matter if arr[2] matches, same thing happens.
			{   
				arr[2] = arr[1];  // the upper two strings move down, and s is put in arr[0].
				arr[1] = arr[0];
				arr[0] = s;
			}
			
		}
		
		public String toString()
		{
			String temp = "";
			if(arr[0].length() > 0)
			{
				temp = temp + arr[0];
				if(arr[1] != null && arr[1].length() > 0)
				{
					temp = temp + "\n" + arr[1];
					if(arr[2] != null && arr[2].length() > 0)
						temp = temp + "\n" + arr[2];
				}
			}
			
			return temp;
		}
		
	}
	
	private ArrayList<Entry> cacheEntries;
	
	public PathCache()
	{
		cacheEntries = new ArrayList<Entry>();
	}
	
	public void enter(int k, String s)
	{
		for(Entry e :cacheEntries)
		{
			if(e.src == k)
			{
				e.add(s);
				return;
			}
		}
		cacheEntries.add(new Entry(k, s));
	}
	
	public void save(int k)
	{
		try
		{
			File dir = new File("data");
			if(!dir.exists())dir.mkdir();
			PrintWriter out = new PrintWriter(new File("data/Host" + k + ".dat"));
			for(Entry e : cacheEntries)
			{
				out.println("The most popular routes from device" + e.getSrc() + " to device" + k + " are: ");
				out.println(e);
			}
			out.close();
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
	}

}

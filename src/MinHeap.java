
public class MinHeap<T> 
{
	
	public class Wrapper<E>
	{		
		E thing;
		int key;
		
		public Wrapper(int x, E t)
		{
			thing = t;
			key = x;
		}
		
	}
	
	Wrapper<T>[] heap;
	int size;
	
	public MinHeap()
	{
		heap = new Wrapper[16];
		size = 0;
	}
	
	public MinHeap(int n)
	{
		if(n <= 0)
			throw new IllegalArgumentException();
		heap = new Wrapper[n];
		size = 0;
	}
	
	private void grow()
	{
		Wrapper<T>[] temp = new Wrapper[heap.length * 2];
		for(int i = 0; i <= size; i++)
			temp[i] = heap[i];
		heap = temp;
	}
	
	private void swap(int i, int j)
	{
		Wrapper<T> temp = heap[i];
		heap[i] = heap[j];
		heap[j] = temp;
	}
	
	private void rollUp(int i) //Check some volatile element and move if up if
	{                           //it is less than it's parent.
		if(i>0)
		{
			if(heap[i].key < heap[(i-1)/2].key)
			{//For some element i, (i-1)/2 is it's parent.
				swap(i,(i-1)/2); 
				rollUp((i-1)/2);
			}
		}
	}
	
	public void add(int x, T t)
	{
		if(size >= heap.length)
			grow();
		heap[size] = new Wrapper<T>(x,t);
		rollUp(size);
		size++;
	}
	
	private void heapify(int i)
	{
		if((i*2 + 1) < size)
		{
			int l = i*2 + 1;
			
			if(i*2 + 2 < size)
			{
				int r = i*2 + 2;
				
				if(heap[l].key < heap[i].key )
				{
					if(heap[r].key < heap[l].key)
					{
						swap(r,i);
						heapify(r);
					}
					else
					{
						swap(l,i);
						heapify(l);
					}
				}
				else if(heap[r].key < heap[i].key )
				{
					swap(r,i);
					heapify(r);
				}
			}
			else
			{
				if(heap[l].key < heap[i].key)
				{
					swap(l,i);
					heapify(l);
				}
			}
		}
	}
	
	public T extract() throws Exception
	{
		if(size <= 0)
			throw new Exception("You tried to extract from an empty heap.");
		size--;
		swap(0, size);
		heapify(0);
		return heap[size].thing;
	}
	
	public boolean isEmpty()
	{
		return size == 0;
	}
	
	public void rollBack(int x)
	{
		for(int i = 0; i < size; i++)
			heap[i].key = heap[i].key - x;
	}
}

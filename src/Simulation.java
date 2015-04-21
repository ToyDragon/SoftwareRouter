import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class Simulation {
	private static Simulation instance;
	
	Runner runner;
	
	List<NetworkDevice> networkDevices;
	List<NetworkDevice> hostDevices;
	List<Link> linkList;
	
	List<NetworkDevice> devicesToRemove;
	List<NetworkDevice> devicesToAdd;
	List<Link> linksToRemove;
	List<Link> linksToAdd;
	
	TickThread tickThread;
	
	public Simulation(Runner runner){
		instance = this;
		this.runner = runner;
		hostDevices = new CopyOnWriteArrayList<NetworkDevice>();
		networkDevices = new CopyOnWriteArrayList<NetworkDevice>();
		linkList = new CopyOnWriteArrayList<Link>();
		
		devicesToRemove = new CopyOnWriteArrayList<NetworkDevice>();
		devicesToAdd = new CopyOnWriteArrayList<NetworkDevice>();
		linksToRemove = new CopyOnWriteArrayList<Link>();
		linksToAdd = new CopyOnWriteArrayList<Link>();
		
		tickThread = new TickThread(runner, this);
	}
	
	public static void log(String msg){
		System.out.println(msg);
	}
	
	public void startSimulation(){
		if(!tickThread.isAlive()){
			tickThread.start();
		}
	}
	
	public void tick(){
		System.out.println("Ticked!");
		runner.graphicsPanel.repaint();
		if(runner.isPaused())return;
		
		//tick all devices and links
		for(NetworkDevice device : hostDevices){
			device.tick();
		}
		for(NetworkDevice device : networkDevices){
			device.tick();
		}
		for(Link link : linkList){
			link.tick();
		}
		
		//modify lists
		for(int i = devicesToRemove.size()-1; i >= 0; i--){
			NetworkDevice device = devicesToRemove.remove(i);
			hostDevices.remove(device);
			networkDevices.remove(device);
		}
		for(int i = devicesToAdd.size()-1; i >= 0; i--){
			NetworkDevice device = devicesToAdd.remove(i);
			if(device instanceof Host){
				hostDevices.add(device);
			}else if(device instanceof Router){
				networkDevices.add(device);
			}
		}
		
		for(int i = linksToRemove.size()-1; i >= 0; i--){
			Link link = linksToRemove.remove(i);
			for(NetworkDevice device : networkDevices){
				device.removeLink(link);
			}
			for(NetworkDevice device : hostDevices){
				device.removeLink(link);
			}
		}
		
		for(int i = linksToAdd.size()-1; i >= 0; i--){
			Link link = linksToAdd.remove(i);
			linkList.add(link);
		}
		
	}
	
	public void removeDevice(NetworkDevice device){
		devicesToRemove.add(device);
	}
	
	public void addDevice(NetworkDevice device){
		devicesToAdd.add(device);
	}
	
	public void removeLink(Link link){
		linksToRemove.add(link);
	}
	
	public void addLink(Link link){
		linksToAdd.add(link);
	}
	
	public void setNetworkDevices(List<NetworkDevice> devices){
		this.networkDevices = devices;
	}
	
	public void setHostDevices(List<NetworkDevice> devices){
		this.hostDevices = devices;
	}
	
	public void setLinkList(List<Link> links){
		this.linkList = links;
	}
	
	public static class TickThread extends Thread{
		int tickTime = 250;
		Runner runner;
		Simulation simulation;
		
		public TickThread(Runner runner, Simulation simulation){
			this.runner = runner;
			this.simulation = simulation;
		}
		
		public void run(){
			while(runner.isRunning()){
				long startTime = System.currentTimeMillis();
				simulation.tick();
				long endTime = System.currentTimeMillis();
				try{
					long sleepTime = tickTime - (endTime - startTime);
					Thread.sleep(sleepTime);
				}catch(Exception e){}
			}
		}
	}
}

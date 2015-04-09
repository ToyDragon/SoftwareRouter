import java.util.ArrayList;
import java.util.Scanner;


public class Runner {
	
	boolean isRunning;
	boolean isPaused;
	
	Scanner uiScanner;
	Simulation simulation;
	int amtRouters;
	int amtLinks;
	int amtHosts;
	double failRate;
	ArrayList<NetworkDevice> hostList;
	ArrayList<NetworkDevice> routerList;
	ArrayList<Link> linkList;
	
	public static void main(String[] args){
		new Runner();
	}
	
	public Runner(){
		isRunning = true;
		isPaused = false;
		
		uiScanner = new Scanner(System.in);
		simulation = new Simulation(this);
		getUserConfig();
		initializeNetwork();
		
		simulation.startSimulation();
		System.out.println("Simulation started");
	}
	
	public boolean isRunning(){
		return isRunning;
	}
	
	public boolean isPaused(){
		return isPaused;
	}
	
	public void initializeNetwork(){
		
		simulation.setNetworkDevices(routerList);
		simulation.setHostDevices(hostList);
		simulation.setLinkList(linkList);
	}
	
	public void getUserConfig(){
		printBanner();
		
		readRouterConfiguration();
		readLinkConfiguration();
		readHostConfiguration();
	}
	
	private void printBanner(){
		System.out.println("--------------------------------------------------");
		System.out.println("|                                                |");
		System.out.println("|                                                |");
		System.out.println("|                 Software Router                |");
		System.out.println("|                   Simulation                   |");
		System.out.println("|                                                |");
		System.out.println("|                Joseph Contarino                |");
		System.out.println("|                  Matthew Bates                 |");
		System.out.println("|                                                |");
		System.out.println("--------------------------------------------------");
	}
	
	private void readHostConfiguration(){
		amtHosts = 0;
		while(amtHosts < 2){
			System.out.print("Enter number of host devices(2 minimum): ");
			try{
				String raw = uiScanner.nextLine();
				amtHosts = Integer.parseInt(raw);
			}catch(Exception e){}
		}
		
		hostList = new ArrayList<NetworkDevice>(amtHosts);
		
		System.out.println("Enter " + amtHosts + " lines describing which router each host is connected to, in the format \"X\" where X is a router index.");
		for(int i = 0; i < amtHosts; i++){
			Host newHost = null;
			while(newHost == null){
				try{
					String raw = uiScanner.nextLine();
					int routerIndex = Integer.parseInt(raw);
					
					if(routerIndex < 0 || routerIndex >= amtRouters)
						throw new IllegalArgumentException("Router index must be between 0 and " + (amtRouters-1) + "!");
					
					Router connectedRouter = (Router)routerList.get(routerIndex);
					
					newHost = new Host();

					Link inLink = new Link(newHost, failRate);
					Link outLink = new Link(connectedRouter, failRate);
					
					newHost.addLink(outLink);
					connectedRouter.addLink(inLink);
					
				}catch(IllegalArgumentException e){
					System.out.println(e.getMessage());
				}catch(Exception e){
					System.out.println("Invalid router index!");
				}
			}
			hostList.add(newHost);
		}
	}
	
	private void readRouterConfiguration(){
		amtRouters = 0;
		while(amtRouters < 6){
			System.out.print("Enter number of routers(6 minimum): ");
			try{
				String raw = uiScanner.nextLine();
				amtRouters = Integer.parseInt(raw);
			}catch(Exception e){}
		}

		routerList = new ArrayList<NetworkDevice>(amtRouters);
		for(int i = 0; i < amtRouters; i++){
			routerList.add(new Router());
		}
	}
	
	private void readLinkConfiguration(){
		
		/////////////////////////////////////////////////////////////////////////////////////////////////
		//Failure rate
		failRate = 0;
		while(failRate <= 0 || failRate >= 1){
			System.out.print("Enter link failure rate(0 to 1):");
			try{
				String raw = uiScanner.nextLine();
				failRate = Double.parseDouble(raw);
			}catch(Exception e){}
		}
		
		/////////////////////////////////////////////////////////////////////////////////////////////////
		//Amount of links
		amtLinks = 0;
		while(amtLinks < amtRouters-1){
			System.out.print("Enter number of links between routers(" + (amtRouters-1) + " minimum): ");
			try{
				String raw = uiScanner.nextLine();
				amtLinks = Integer.parseInt(raw);
			}catch(Exception e){}
		}
		
		/////////////////////////////////////////////////////////////////////////////////////////////////
		//Link connection information
		System.out.println("Enter " + amtLinks + " lines of link information, in the format \"X Y C\" where X and Y are router indexes and C is an integer cost.");
		linkList = new ArrayList<Link>(amtLinks * 2);
		for(int i = 0; i < amtLinks; i++){
			Link leftLink = null;
			Link rightLink = null;
			Router leftRouter = null;
			Router rightRouter = null;
			while(leftLink == null || rightLink == null){
				try{
					String raw = uiScanner.nextLine();
					Scanner linkScanner = new Scanner(raw);
					int leftID = linkScanner.nextInt();
					int rightID = linkScanner.nextInt();
					int cost = linkScanner.nextInt();
					
					if(leftID == rightID)
						throw new IllegalArgumentException("X and Y cannot be the same!");
					if(leftID < 0 || leftID >= amtRouters)
						throw new IllegalArgumentException("X must be between 0 and " + (amtRouters-1) + "!");
					if(rightID < 0 || rightID >= amtRouters)
						throw new IllegalArgumentException("Y must be between 0 and " + (amtRouters-1) + "!");
					if(cost < 0)
						throw new IllegalArgumentException("Cost must be 0 or higher!");
					
					
					
					leftRouter = (Router)routerList.get(leftID);
					rightRouter = (Router)routerList.get(rightID);

					leftLink = new Link(leftRouter, failRate);
					rightLink = new Link(rightRouter, failRate);
					
				}catch(IllegalArgumentException e){
					System.out.println(e.getMessage());
				}catch(Exception e){
					System.out.println("Invalid link!");
				}
			}
			linkList.add(leftLink);
			linkList.add(rightLink);

			leftRouter.addLink(rightLink);
			rightRouter.addLink(leftLink);
		}
	}
}

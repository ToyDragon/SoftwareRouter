import java.util.ArrayList;
import java.util.Scanner;


public class Runner {
	
	Scanner uiScanner;
	Simulation simulation;
	int amtRouters;
	
	public static void main(String[] args){
		new Runner();
	}
	
	public Runner(){
		uiScanner = new Scanner(System.in);
		simulation = new Simulation();
		getUserConfig();
		initializeNetwork();
	}
	
	public void initializeNetwork(){
		ArrayList<Router> routerList = new ArrayList<Router>(amtRouters);
		for(int i = 0; i < amtRouters; i++){
			routerList.add(new Router());
		}
	}
	
	public void getUserConfig(){
		System.out.println("--------------------------------------");
		System.out.println("|                                    |");
		System.out.println("|                                    |");
		System.out.println("|           Software Router          |");
		System.out.println("|             Simulation             |");
		System.out.println("|                                    |");
		System.out.println("|          Joseph Contarino          |");
		System.out.println("|            Matthew Bates           |");
		System.out.println("|                                    |");
		System.out.println("--------------------------------------");
		
		amtRouters = 0;
		while(amtRouters < 6){
			System.out.print("Number of routers(6 minimum): ");
			try{
				String raw = uiScanner.nextLine();
				amtRouters = Integer.parseInt(raw);
			}catch(Exception e){}
		}
		
		
	}
}

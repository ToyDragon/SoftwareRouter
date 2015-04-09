import java.util.List;


public class Simulation {
	List<NetworkDevice> networkDevices;
	List<NetworkDevice> hostDevices;
	
	public Simulation(){
		//Thread stuff here?
	}
	
	public void setNetworkDevices(List<NetworkDevice> devices){
		this.networkDevices = devices;
	}
	
	public void setHostDevices(List<NetworkDevice> devices){
		this.hostDevices = devices;
	}
}

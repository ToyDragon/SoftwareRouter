import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventPostProcessor;
import java.awt.KeyboardFocusManager;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.ScrollPaneConstants;


public class Runner implements KeyEventPostProcessor, MouseListener, MouseMotionListener{
	
	HashMap<Object, Shape> clickBoxMap = new HashMap<Object, Shape>();
	
	boolean isRunning;
	boolean isPaused;
	
	boolean drawingLink;
	int[] mousePosition;
	
	Scanner uiScanner;
	Simulation simulation;
	int amtRouters;
	int amtLinks;
	int amtHosts;
	double failRate;
	ArrayList<NetworkDevice> hostList;
	ArrayList<NetworkDevice> routerList;
	ArrayList<Link> linkList;
	
	Object selectedObject;
	
	int[][] routerPositions;
	int[][] hostPositions;
	
	JFrame window;
	JTextArea logArea;
	JPanel graphicsPanel,mainPanel,leftPanel,rightPanel,buttonPanel;
	JButton button_removelink;
	
	BufferedImage curBuffer,buffer1,buffer2;
	BufferedImage imgRouter, imgRouterSelected;
	BufferedImage imgHost, imgHostSelected, imgHostInactive;
	
	public static void main(String[] args){
		new Runner();
	}
	
	public Runner(){
		isRunning = true;
		isPaused = false;
		
		uiScanner = new Scanner(System.in);
		simulation = new Simulation(this);
		
		mousePosition = new int[2];
		
		try{
			imgRouter = ImageIO.read(new File("imgs/router.png"));
			imgRouterSelected = ImageIO.read(new File("imgs/routerSelected.png"));

			imgHost = ImageIO.read(new File("imgs/server.png"));
			imgHostSelected = ImageIO.read(new File("imgs/serverSelected.png"));
		}catch(Exception e){}
		
		initNetwork();
		
		initUI();
		
		//getUserConfig();
		//initializeNetwork();
		
		simulation.startSimulation();
		System.out.println("Simulation started");
		
		userInputLoop();
	}
	
	private void initNetwork(){
		amtRouters = 6;
		routerList = new ArrayList<NetworkDevice>(amtRouters);
		hostList = new ArrayList<NetworkDevice>(amtRouters);
		linkList = new ArrayList<Link>(amtLinks);
		for(int i = 0; i < amtRouters; i++){
			
			NetworkDevice leftDevice = new Router();
			NetworkDevice rightDevice = new Host();
			
			routerList.add(leftDevice);
			hostList.add(rightDevice);
			
			Link leftLink = new Link(leftDevice, rightDevice, 1);
			Link rightLink = new Link(rightDevice, leftDevice, 1);

			leftLink.setPartnerLink(rightLink);
			rightLink.setPartnerLink(leftLink);

			leftDevice.addOutLink(leftLink);
			leftDevice.addInLink(rightLink);
			rightDevice.addOutLink(rightLink);
			rightDevice.addInLink(leftLink);
			
			linkList.add(leftLink);
			linkList.add(rightLink);
		}
		simulation.setNetworkDevices(routerList);
		simulation.setHostDevices(hostList);
		
		int[][] rawLinks = {
				{0,1,1},
				{1,2,1},
				{2,3,1},
				{3,4,1},
				{4,5,1},
				{5,0,1},
				{0,3,2},
				{1,4,2},
				{2,5,2}
		};
		amtLinks = rawLinks.length;
		for(int i = 0; i < rawLinks.length; i++){
			NetworkDevice leftDevice = routerList.get(rawLinks[i][0]);
			NetworkDevice rightDevice = routerList.get(rawLinks[i][1]);
			Link leftLink = new Link(leftDevice, rightDevice, rawLinks[i][2]);
			Link rightLink = new Link(rightDevice, leftDevice, rawLinks[i][2]);

			leftLink.setPartnerLink(rightLink);
			rightLink.setPartnerLink(leftLink);

			leftDevice.addOutLink(leftLink);
			leftDevice.addInLink(rightLink);
			rightDevice.addOutLink(rightLink);
			rightDevice.addInLink(leftLink);
			
			linkList.add(leftLink);
			linkList.add(rightLink);
		}
		
		simulation.setLinkList(linkList);
	}
	
	private void initUI(){
		window = new JFrame("Software Router");
		window.setSize(800,600);
		window.setLocationRelativeTo(null);
		window.setResizable(false);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		mainPanel = new JPanel();
		leftPanel = new JPanel();
		rightPanel = new JPanel();
		buttonPanel = new JPanel();
		
		leftPanel.setLayout(null);
		mainPanel.setLayout(null);
		
		int graphicsWidth = 638;
		int graphicsHeight = 566;

		leftPanel.setPreferredSize(new Dimension(200, 600));
		leftPanel.setBounds(0, 0, 150, 600);
		rightPanel.setPreferredSize(new Dimension(graphicsWidth, graphicsHeight));
		rightPanel.setBounds(150, 0, graphicsWidth, graphicsHeight);
		
		buffer1 = new BufferedImage(graphicsWidth, graphicsHeight, BufferedImage.TYPE_INT_ARGB);
		buffer2 = new BufferedImage(graphicsWidth, graphicsHeight, BufferedImage.TYPE_INT_ARGB);
		curBuffer = buffer1;
		
		graphicsPanel = new JPanel(){
			public void paintComponent(Graphics g){
				paintCanvas(g);
			}
		};
		
		graphicsPanel.setPreferredSize(new Dimension(graphicsWidth, graphicsHeight));
		
		graphicsPanel.addMouseListener(this);
		graphicsPanel.addMouseMotionListener(this);
		graphicsPanel.setFocusable(true);

        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventPostProcessor(this);
        
        JButton btnAddRouter = new JButton("Add Router");
        btnAddRouter.setBounds(75 - 50, 40, 100, 30);
        btnAddRouter.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				NetworkDevice leftDevice = new Router();
				NetworkDevice rightDevice = new Host();
				
				Link leftLink = new Link(leftDevice, rightDevice, 1);
				Link rightLink = new Link(rightDevice, leftDevice, 1);

				leftLink.setPartnerLink(rightLink);
				rightLink.setPartnerLink(leftLink);

				leftDevice.addOutLink(leftLink);
				leftDevice.addInLink(rightLink);
				rightDevice.addOutLink(rightLink);
				rightDevice.addInLink(leftLink);
				
				linkList.add(leftLink);
				linkList.add(rightLink);
				
				simulation.addDevice(leftDevice);
				simulation.addDevice(rightDevice);
			}
        });

        JLabel label_linkinfo = new JLabel("Link Info");
        label_linkinfo.setBounds(75 - 50, 100, 100, 30);
        final JLabel label_failrate = new JLabel("Fail Rate: "+ Link.failRate);
        label_failrate.setBounds(75 - 50, 130, 100, 30);
        final JTextField field_failrate = new JTextField(""+Link.failRate,10);
        field_failrate.setBounds(75-50,160,100,30);
        
        button_removelink = new JButton("Disable Link");
        button_removelink.setBounds(75-60, 200, 120, 30);
        
        button_removelink.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Remove link " + selectedObject);
				if(selectedObject instanceof Link){
					//remove link
					Link link = (Link)selectedObject;
					link.setDisabled(!link.getDisabled());
					
					graphicsPanel.repaint();
				}
			}
        });
        
        field_failrate.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try{
					double fail_rate = Double.parseDouble(field_failrate.getText());
					if(fail_rate < 0 || fail_rate > 1){
						throw new IllegalArgumentException();
					}
					Link.failRate = fail_rate;
					label_failrate.setText("Fail Rate: "+ Link.failRate);
				}catch(Exception unused){
					field_failrate.setText("" + Link.failRate);
				}
			}
        });

		leftPanel.add(btnAddRouter);
		leftPanel.add(label_linkinfo);
		leftPanel.add(label_failrate);
		leftPanel.add(field_failrate);
		leftPanel.add(button_removelink);
		
		rightPanel.add(graphicsPanel);

		mainPanel.add(leftPanel);
		mainPanel.add(rightPanel);
		
		window.getContentPane().add(mainPanel);
		
		//window.pack();
		window.setVisible(true);
	}
	
	public void paintCanvas(Graphics graw){
		Graphics2D canvasg = (Graphics2D) graw;
		if(curBuffer == buffer1)curBuffer = buffer2;
		else curBuffer = buffer1;
		
		Graphics2D g = (Graphics2D)curBuffer.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, curBuffer.getWidth(), curBuffer.getHeight());

		List<NetworkDevice> routers = simulation.networkDevices;
		List<NetworkDevice> hosts = simulation.hostDevices;
		
		routerPositions = new int[routers.size()][2];
		hostPositions = new int[routers.size()][2];
		for(int i = 0; i < routers.size(); i++){
			double angle = i * Math.PI*2/routers.size();
			routerPositions[i][0] = rightPanel.getWidth()/2 + (int)(Math.cos(angle)*rightPanel.getWidth()*3/13);
			routerPositions[i][1] = rightPanel.getHeight()/2 + (int)(Math.sin(angle)*rightPanel.getHeight()*3/13);
			
			hostPositions[i][0] = rightPanel.getWidth()/2 + (int)(Math.cos(angle)*rightPanel.getWidth()*4/9);
			hostPositions[i][1] = rightPanel.getHeight()/2 + (int)(Math.sin(angle)*rightPanel.getHeight()*4/9);
		}
		
		//Draw links
		List<Link> linksToDraw = simulation.linkList;
		for(Link link : linksToDraw){
			if(link.getDisabled())continue;
			
			if(link.getSource() instanceof Host && link.getTarget() instanceof Router)continue;
			if(link.getSource().drawID > link.getTarget().drawID)continue;
			
			int x1 = 0;
			int y1 = 0;
			int x2 = 0;
			int y2 = 0;
			
			if(link.getSource() instanceof Router){
				x1 = routerPositions[link.getSource().drawID][0];
				y1 = routerPositions[link.getSource().drawID][1];
			}
			if(link.getSource() instanceof Host){
				x1 = hostPositions[link.getSource().drawID][0];
				y1 = hostPositions[link.getSource().drawID][1];
			}
			if(link.getTarget() instanceof Router){
				x2 = routerPositions[link.getTarget().drawID][0];
				y2 = routerPositions[link.getTarget().drawID][1];
			}
			if(link.getTarget() instanceof Host){
				x2 = hostPositions[link.getTarget().drawID][0];
				y2 = hostPositions[link.getTarget().drawID][1];
			}
			
			g.setColor(Color.BLACK);
			if(selectedObject == link) g.setColor(Color.GREEN);
			g.drawLine(x1, y1, x2, y2);
			
			int rx = (x1+x2)/2;
			int ry = (y1+y2)/2;
			
			if(link.getTarget().drawID - link.getSource().drawID == routers.size()/2){
				rx = (3*x1 + 2*x2)/5;
				ry = (3*y1 + 2*y2)/5;
			}
			
			g.setColor(Color.GRAY);
			if(selectedObject == link) g.setColor(Color.YELLOW.darker());
			g.fillRect(rx - 3, ry - 7, 12, 15);
			
			g.setColor(Color.BLACK);
			g.drawString(""+link.getCost(), rx, ry + 5);
		}
		
		//Draw new link
		if(drawingLink && selectedObject instanceof Router){
			Router router = (Router)selectedObject;
			
			int x1 = routerPositions[router.drawID][0];
			int y1 = routerPositions[router.drawID][1];
			int x2 = mousePosition[0];
			int y2 = mousePosition[1];
			
			double distance = Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
			if(distance >= 30){
				g.setColor(Color.BLACK);
				g.drawLine(x1, y1, x2, y2);
			}
		}
		
		//Draw routers
		for(NetworkDevice router : routers){
			int x = routerPositions[router.drawID][0];
			int y = routerPositions[router.drawID][1];
			g.setColor(Color.BLACK);
			if(selectedObject == router)
				g.drawImage(imgRouterSelected, x-25, y-25, 50, 50, null);
			else
				g.drawImage(imgRouter, x-25, y-25, 50, 50, null);
		}
		
		//Draw hosts
		for(NetworkDevice hostRaw : hosts){
			Host host = (Host)hostRaw;
			int x = hostPositions[host.drawID][0];
			int y = hostPositions[host.drawID][1];
			g.setColor(Color.BLACK);
			if(selectedObject == host)
				g.drawImage(imgHostSelected, x-25, y-25, 50, 50, null);
			else
				g.drawImage(imgHost, x-25, y-25, 50, 50, null);
		}
		
		canvasg.drawImage(curBuffer, 0, 0, null);
	}
	
	private void userInputLoop(){
		while(true){
			String rawInput = uiScanner.nextLine();
			String[] inputTokens = rawInput.split(" ");
			if(inputTokens[0].equalsIgnoreCase("pause")){
				isPaused = true;
			}
			if(inputTokens[0].equalsIgnoreCase("unpause")){
				isPaused = false;
			}
			
		}
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
		//printBanner();
		
		//readRouterConfiguration();
		//readLinkConfiguration();
		//readHostConfiguration();
	}
	
	//Compute the dot product AB . AC
	private double DotProduct(double[] pointA, double[] pointB, double[] pointC){
		double[] AB = new double[2];
		double[] BC = new double[2];
		AB[0] = pointB[0] - pointA[0];
		AB[1] = pointB[1] - pointA[1];
		BC[0] = pointC[0] - pointB[0];
		BC[1] = pointC[1] - pointB[1];
		double dot = AB[0] * BC[0] + AB[1] * BC[1];
		
		return dot;
	}

	//Compute the cross product AB x AC
	private double CrossProduct(double[] pointA, double[] pointB, double[] pointC){
		double[] AB = new double[2];
		double[] AC = new double[2];
		AB[0] = pointB[0] - pointA[0];
		AB[1] = pointB[1] - pointA[1];
		AC[0] = pointC[0] - pointA[0];
		AC[1] = pointC[1] - pointA[1];
		double cross = AB[0] * AC[1] - AB[1] * AC[0];
		
		return cross;
	}

	//Compute the distance from A to B
	public double Distance(double[] pointA, double[] pointB){
		double d1 = pointA[0] - pointB[0];
		double d2 = pointA[1] - pointB[1];
		
		return Math.sqrt(d1 * d1 + d2 * d2);
	}

	//Compute the distance from AB to C
	//if isSegment is true, AB is a segment, not a line.
	public double LineToPointDistance2D(double[] pointA, double[] pointB, double[] pointC){
		double dist = CrossProduct(pointA, pointB, pointC) / Distance(pointA, pointB);
		double dot1 = DotProduct(pointA, pointB, pointC);
		if (dot1 > 0) 
			return Distance(pointB, pointC);
		
		double dot2 = DotProduct(pointB, pointA, pointC);
		if (dot2 > 0) 
			return Distance(pointA, pointC);
		return Math.abs(dist);
	} 

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		double[] clickPoint = {(double)e.getX(), (double)e.getY()};
		selectedObject = null;
		
		for(Link link : simulation.linkList){
			if(link.getSource() instanceof Host && link.getTarget() instanceof Router)continue;
			if(link.getSource().drawID > link.getTarget().drawID)continue;
			double[] pointa = {routerPositions[link.getSource().drawID][0],routerPositions[link.getSource().drawID][1]};
			double[] pointb = {routerPositions[link.getTarget().drawID][0],routerPositions[link.getTarget().drawID][1]};
			if(link.getSource() instanceof Host){
				pointa = new double[]{hostPositions[link.getSource().drawID][0],hostPositions[link.getSource().drawID][1]};
			}
			if(link.getTarget() instanceof Host){
				pointb = new double[]{hostPositions[link.getSource().drawID][0],hostPositions[link.getSource().drawID][1]};
			}
			
			double distance = LineToPointDistance2D(pointa, pointb, clickPoint);
			if(distance < 15){
				selectedObject = link;
			}
		}
		
		for(NetworkDevice router : simulation.networkDevices){
			
			int rx = routerPositions[router.drawID][0];
			int ry = routerPositions[router.drawID][1];

			int ex = e.getX();
			int ey = e.getY();
			
			double distance = Math.sqrt(Math.pow(rx-ex, 2) + Math.pow(ry-ey, 2));
			
			if(distance < 25){
				selectedObject = router;
				mousePosition = new int[]{e.getX(), e.getY()};
				drawingLink = true;
			}
		}
		
		graphicsPanel.repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(drawingLink && selectedObject instanceof Router){
			Router sourceRouter = (Router)selectedObject;
			Router destinationRouter = null;
			for(NetworkDevice router : simulation.networkDevices){
				
				int rx = routerPositions[router.drawID][0];
				int ry = routerPositions[router.drawID][1];

				int ex = e.getX();
				int ey = e.getY();
				
				double distance = Math.sqrt(Math.pow(rx-ex, 2) + Math.pow(ry-ey, 2));
				
				if(distance < 25){
					destinationRouter = (Router)router;
				}
			}
			
			if(destinationRouter != null){
				//If this link doesnt exist add it
				boolean linkExists = false;
				for(Link link : linkList){
					if(link.getSource().drawID == sourceRouter.drawID && link.getTarget().drawID == destinationRouter.drawID){
						linkExists = true;
						link.setDisabled(false);
						break;
					}
				}
				
				if(!linkExists){
					//add new link
					Link leftLink = new Link(sourceRouter, destinationRouter, 1);
					Link rightLink = new Link(destinationRouter, sourceRouter, 1);

					leftLink.setPartnerLink(rightLink);
					rightLink.setPartnerLink(leftLink);

					sourceRouter.addOutLink(leftLink);
					sourceRouter.addInLink(rightLink);
					destinationRouter.addOutLink(rightLink);
					destinationRouter.addInLink(leftLink);
					
					linkList.add(leftLink);
					linkList.add(rightLink);
					
					simulation.addLink(leftLink);
					simulation.addLink(rightLink);
				}
			}
		}
		
		drawingLink = false;
		graphicsPanel.repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mousePosition = new int[]{e.getX(), e.getY()};
		if(drawingLink){
			graphicsPanel.repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean postProcessKeyEvent(KeyEvent e) {
		// TODO Auto-generated method stub
		if (e.getID() == KeyEvent.KEY_PRESSED){
			char keyChar = e.getKeyChar();
			if(keyChar >= '1' && keyChar <= '9'){
				int value = keyChar - '0';
				if(selectedObject instanceof Link){
					Link link = (Link) selectedObject;
					link.setCost(value);
					link.getPartnerLink().setCost(value);
					graphicsPanel.repaint();
					
					return true;
				}
			}
		}
		return false;
	}
}

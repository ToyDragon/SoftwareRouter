import java.awt.Color;
import java.awt.Component;
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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
	boolean movingDevice,spacePressed;
	int[] mousePosition, startPos, deviceStart;
	
	Scanner uiScanner;
	Simulation simulation;
	int amtRouters;
	int amtLinks;
	int amtHosts;
	double failRate;
	Clock clock;
	ArrayList<NetworkDevice> hostList;
	ArrayList<NetworkDevice> routerList;
	ArrayList<Link> linkList;
	
	Object selectedObject;
	
	int[][] routerPositions;
	int[][] hostPositions;
	
	JComboBox<String> comboSource,comboDest;
	
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
		clock = new Clock();
		
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
		
		int[][] rawRouters = {
				{212,100},
				{324,100},
				{436,100},
				{212,450},
				{324,450},
				{436,450}
		};
		
		int[][] rawHosts = {
				{100,100},
				{550,100},
				{100,450},
				{550,450}
		};

		for(int i = 0; i < rawRouters.length; i++){
			Router router = new Router();
			router.drawx = rawRouters[i][0];
			router.drawy = rawRouters[i][1];
			routerList.add(router);
		}
		
		for(int i = 0; i < rawHosts.length; i++){
			Host host = new Host();
			host.drawx = rawHosts[i][0];
			host.drawy = rawHosts[i][1];
			hostList.add(host);
		}
		
		simulation.setNetworkDevices(routerList);
		simulation.setHostDevices(hostList);
		
		int[][] rawLinks = {
				{0,1,3},
				{1,2,3},
				{3,4,3},
				{4,5,3},
				{0,3,3},
				{1,3,3},
				{1,5,3},
				{1,4,3},
				{2,4,3},
				{0,6,3},
				{2,7,3},
				{8,3,3},
				{5,9,3}
		};
		
		amtLinks = rawLinks.length;
		for(int i = 0; i < rawLinks.length; i++){
			NetworkDevice leftDevice = NetworkDevice.getDevice(rawLinks[i][0]);
			NetworkDevice rightDevice = NetworkDevice.getDevice(rawLinks[i][1]);
			Link leftLink = new Link(leftDevice, rightDevice, rawLinks[i][2], clock);
			Link rightLink = new Link(rightDevice, leftDevice, rawLinks[i][2], clock);

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
		window.addWindowListener(new WindowListener() {
			@Override public void windowOpened(WindowEvent arg0) {}
			@Override public void windowIconified(WindowEvent arg0) {}
			@Override public void windowDeiconified(WindowEvent arg0) {}
			@Override public void windowDeactivated(WindowEvent arg0) {}
			@Override public void windowClosing(WindowEvent arg0) {
				NetworkDevice.saveAll();
				}
			@Override public void windowActivated(WindowEvent arg0) {}			
			@Override public void windowClosed(WindowEvent arg0) {
				//save shit to file
			}
		});
		
		mainPanel = new JPanel();
		leftPanel = new JPanel();
		rightPanel = new JPanel();
		buttonPanel = new JPanel();
		
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		mainPanel.setLayout(null);
		
		int graphicsWidth = 638;
		int graphicsHeight = 566;

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
        //btnAddRouter.setBounds(75 - 50, 40, 100, 30);
        btnAddRouter.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				NetworkDevice device = null;
				for(int i = simulation.networkDevices.size()-1; i>=0; i--){
					if(simulation.networkDevices.get(i).isDisabled()){
						device = simulation.networkDevices.get(i);
						device.setDisabled(false);
						break;
					}
				}
				
				if(device == null){
					device = new Router();
					simulation.addDevice(device);
				}
				
				device.drawx = 50;
				device.drawy = 300;
				
				graphicsPanel.repaint();
			}
        });
        
        JButton btnAddHost = new JButton("Add Host");
        //btnAddRouter.setBounds(75 - 50, 40, 100, 30);
        btnAddHost.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				NetworkDevice device = null;
				for(int i = simulation.hostDevices.size()-1; i>=0; i--){
					if(simulation.hostDevices.get(i).isDisabled()){
						device = simulation.hostDevices.get(i);
						device.setDisabled(false);
						updateHostComboboxes();
						break;
					}
				}
				
				if(device == null){
					device = new Host();
					simulation.addDevice(device);
				}
				
				device.drawx = 50;
				device.drawy = 300;
				
				graphicsPanel.repaint();
			}
        });

        btnAddRouter.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAddHost.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel label_linkinfo = new JLabel("Link Info");
        label_linkinfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        final JLabel label_failrate = new JLabel("Fail Rate: "+ Link.failRate);
        label_failrate.setAlignmentX(Component.CENTER_ALIGNMENT);
        final JTextField field_failrate = new JTextField(""+Link.failRate,10);
        field_failrate.setAlignmentX(Component.CENTER_ALIGNMENT);
        field_failrate.setMaximumSize( field_failrate.getPreferredSize() );
        
        button_removelink = new JButton("Disable Device");
        //button_removelink.setBounds(75-60, 200, 120, 30);
        button_removelink.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        button_removelink.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Remove link " + selectedObject);
				if(selectedObject instanceof Link){
					//remove link
					Link link = (Link)selectedObject;
					link.setDisabled(!link.getDisabled());
					link.getPartnerLink().setDisabled(link.getDisabled());
					
					graphicsPanel.repaint();
				}
				if(selectedObject instanceof NetworkDevice){

					NetworkDevice device = (NetworkDevice)selectedObject;
					device.setDisabled(!device.isDisabled());
					
					updateHostComboboxes();
					
					graphicsPanel.repaint();
				}
			}
        });
        
        field_failrate.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
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
        
        JButton buttonSendPacket = new JButton("Send Packet");
        buttonSendPacket.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int sourceID = Integer.parseInt(""+comboSource.getSelectedItem());
				int destID = Integer.parseInt(""+comboDest.getSelectedItem());
				
				Packet newPacket = new Packet(destID, sourceID);
				(NetworkDevice.getDevice(sourceID)).process(newPacket);
				
				graphicsPanel.repaint();
			}
        });
        buttonSendPacket.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        final JButton buttonPause = new JButton("Pause");
        buttonPause.addActionListener(new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent arg0){
        		isPaused = !isPaused;
        		if(isPaused){
        			buttonPause.setText("Unpause");
        		}else{
        			buttonPause.setText("Pause");
        		}
        	}
        });
        buttonPause.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPause.setPreferredSize(new Dimension((int)(buttonPause.getPreferredSize().getWidth()+15),(int)(buttonPause.getPreferredSize().getHeight())));
        buttonPause.setMaximumSize(buttonPause.getPreferredSize());
        
        JLabel labelSource = new JLabel("Source:");
        labelSource.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel labelDestination = new JLabel("Destination:");
        labelDestination.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        
        comboSource = new JComboBox<String>(new String[]{"Temp1","Temp2"});
        comboSource.setMaximumSize(comboSource.getPreferredSize());
        comboSource.setAlignmentX(Component.CENTER_ALIGNMENT);
        comboDest = new JComboBox<String>(new String[]{"Temp1","Temp2"});
        comboDest.setMaximumSize(comboDest.getPreferredSize());
        comboDest.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftPanel.add(Box.createVerticalStrut(10));
		leftPanel.add(buttonPause);
		leftPanel.add(Box.createVerticalStrut(10));
		leftPanel.add(btnAddRouter);
		leftPanel.add(Box.createVerticalStrut(10));
		leftPanel.add(btnAddHost);
		leftPanel.add(Box.createVerticalStrut(10));
		leftPanel.add(label_linkinfo);
		leftPanel.add(Box.createVerticalStrut(10));
		leftPanel.add(label_failrate);
		leftPanel.add(Box.createVerticalStrut(10));
		leftPanel.add(field_failrate);
		leftPanel.add(Box.createVerticalStrut(10));
		leftPanel.add(button_removelink);
		leftPanel.add(Box.createVerticalStrut(10));
		leftPanel.add(labelSource);
		leftPanel.add(comboSource);
		leftPanel.add(Box.createVerticalStrut(10));
		leftPanel.add(labelDestination);
		leftPanel.add(comboDest);
		leftPanel.add(Box.createVerticalStrut(10));
		leftPanel.add(buttonSendPacket);
		
		rightPanel.add(graphicsPanel);

		mainPanel.add(leftPanel);
		mainPanel.add(rightPanel);
		
		window.getContentPane().add(mainPanel);
		
		
		updateHostComboboxes();
		//window.pack();
		window.setVisible(true);
	}
	
	public void updateHostComboboxes(){
		List<NetworkDevice> hosts = simulation.hostDevices;
		int count = 0;
		for(NetworkDevice host : hosts){
			if(!host.isDisabled()) count++;
		}
		int i = 0;
		String[] hostIds = new String[count];
		for(NetworkDevice host : hosts){
			if(!host.isDisabled()) hostIds[i++] = "" + host.getID();
		}

		comboSource.setModel(new JComboBox<>(hostIds).getModel());
		comboDest.setModel(new JComboBox<>(hostIds).getModel());
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
		
		//Draw links
		List<Link> linksToDraw = simulation.linkList;
		for(int i = linksToDraw.size()-1; i>=0; i--){
			Link link = linksToDraw.get(i);
			if(link.getDisabled())continue;
			
			if(link.getSource().getID() > link.getTarget().getID())continue;
			
			int x1 = link.getSource().drawx;
			int y1 = link.getSource().drawy;
			int x2 = link.getTarget().drawx;
			int y2 = link.getTarget().drawy;
			
			g.setColor(Color.BLACK);
			if(selectedObject == link) g.setColor(Color.GREEN);
			g.drawLine(x1, y1, x2, y2);
			
			int rx = (x1*2+x2)/3;
			int ry = (y1*2+y2)/3;
			
			if(link.hasPackets() || link.getPartnerLink().hasPackets()){
				g.setColor(Color.PINK);
				g.fillRect(rx-13,ry-17,32,35);
			}
			
			g.setColor(Color.GRAY);
			if(selectedObject == link) g.setColor(Color.YELLOW.darker());
			g.fillRect(rx - 3, ry - 7, 12, 15);
			
			
			g.setColor(Color.BLACK);
			g.drawString(""+link.getCost(), rx, ry + 5);
		}
		
		//Draw new link
		if(drawingLink && selectedObject instanceof NetworkDevice){
			NetworkDevice device = (NetworkDevice)selectedObject;
			
			int x1 = device.drawx;
			int y1 = device.drawy;
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
			if(router.isDisabled())continue;
			int x = router.drawx;
			int y = router.drawy;
			if(selectedObject == router)
				g.drawImage(imgRouterSelected, x-25, y-25, 50, 50, null);
			else
				g.drawImage(imgRouter, x-25, y-25, 50, 50, null);
			
			g.setColor(Color.GRAY);
			g.fillRect(x-1, y-12, 15, 15);
			g.setColor(Color.BLACK);
			g.drawString(""+router.getID(), x, y);
		}
		
		//Draw hosts
		for(NetworkDevice hostRaw : hosts){
			if(hostRaw.isDisabled())continue;
			Host host = (Host)hostRaw;
			int x = host.drawx;
			int y = host.drawy;
			g.setColor(Color.BLACK);
			if(selectedObject == host)
				g.drawImage(imgHostSelected, x-25, y-25, 50, 50, null);
			else
				g.drawImage(imgHost, x-25, y-25, 50, 50, null);
			
			g.setColor(Color.GRAY);
			g.fillRect(x-1, y-12, 15, 15);
			g.setColor(Color.BLACK);
			g.drawString(""+host.getID(), x, y);
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
			if(link.getSource().getID() > link.getTarget().getID())continue;
			double[] pointa = {link.getSource().drawx,link.getSource().drawy};
			double[] pointb = {link.getTarget().drawx,link.getTarget().drawy};

			double distance = LineToPointDistance2D(pointa, pointb, clickPoint);
			if(distance < 15){
				selectedObject = link;
			}
		}

		for(NetworkDevice device : simulation.networkDevices){
			if(device.isDisabled())continue;
			int rx = device.drawx;
			int ry = device.drawy;

			int ex = e.getX();
			int ey = e.getY();
			
			double distance = Math.sqrt(Math.pow(rx-ex, 2) + Math.pow(ry-ey, 2));
			
			if(distance < 25){
				selectedObject = device;
				mousePosition = new int[]{e.getX(), e.getY()};
				if(spacePressed){
					movingDevice = true;
					startPos = new int[]{e.getX(), e.getY()};
					deviceStart = new int[]{device.drawx,device.drawy};
				}else{
					drawingLink = true;
				}
			}
		}
		for(NetworkDevice device : simulation.hostDevices){
			if(device.isDisabled())continue;
			
			int rx = device.drawx;
			int ry = device.drawy;

			int ex = e.getX();
			int ey = e.getY();
			
			double distance = Math.sqrt(Math.pow(rx-ex, 2) + Math.pow(ry-ey, 2));
			
			if(distance < 25){
				selectedObject = device;
				mousePosition = new int[]{e.getX(), e.getY()};
				if(spacePressed){
					movingDevice = true;
					startPos = new int[]{e.getX(), e.getY()};
					deviceStart = new int[]{device.drawx,device.drawy};
				}else{
					drawingLink = true;
				}
			}
		}
		
		graphicsPanel.repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(drawingLink && selectedObject instanceof NetworkDevice){
			NetworkDevice sourceDevice = (NetworkDevice)selectedObject;
			NetworkDevice destinationDevice = null;
			for(NetworkDevice device : simulation.networkDevices){
				if(device.isDisabled())continue;
				
				int rx = device.drawx;
				int ry = device.drawy;

				int ex = e.getX();
				int ey = e.getY();
				
				double distance = Math.sqrt(Math.pow(rx-ex, 2) + Math.pow(ry-ey, 2));
				
				if(distance < 25){
					destinationDevice = device;
				}
			}
			for(NetworkDevice device : simulation.hostDevices){
				if(device.isDisabled())continue;
				
				int rx = device.drawx;
				int ry = device.drawy;

				int ex = e.getX();
				int ey = e.getY();
				
				double distance = Math.sqrt(Math.pow(rx-ex, 2) + Math.pow(ry-ey, 2));
				
				if(distance < 25){
					destinationDevice = device;
				}
			}
			
			if(destinationDevice != null && (!(destinationDevice instanceof Host) || !(selectedObject instanceof Host)) && destinationDevice != selectedObject){
				//If this link doesnt exist add it
				boolean linkExists = false;
				for(Link link : linkList){
					if(link.getSource().getID() == sourceDevice.getID() && link.getTarget().getID() == destinationDevice.getID()){
						linkExists = true;
						link.setDisabled(false);
						link.getPartnerLink().setDisabled(false);
						System.out.println("Found existing link");
						break;
					}
				}
				
				if(!linkExists){
					//add new link
					Link leftLink = new Link(sourceDevice, destinationDevice, 1, clock);
					Link rightLink = new Link(destinationDevice, sourceDevice, 1, clock);

					leftLink.setPartnerLink(rightLink);
					rightLink.setPartnerLink(leftLink);

					sourceDevice.addOutLink(leftLink);
					sourceDevice.addInLink(rightLink);
					destinationDevice.addOutLink(rightLink);
					destinationDevice.addInLink(leftLink);
					
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
		if(movingDevice && selectedObject instanceof NetworkDevice){
			NetworkDevice device = (NetworkDevice)selectedObject;
			int dx = mousePosition[0] - startPos[0];
			int dy = mousePosition[1] - startPos[1];

			device.drawx = dx + deviceStart[0];
			device.drawy = dy + deviceStart[1];
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
		if (e.getKeyCode() == KeyEvent.VK_M){
			if(e.getID() == KeyEvent.KEY_PRESSED){
				spacePressed = true;
			}else{
				if(movingDevice){
					NetworkDevice device = (NetworkDevice)selectedObject;
					int dx = mousePosition[0] - startPos[0];
					int dy = mousePosition[1] - startPos[1];

					device.drawx = dx + deviceStart[0];
					device.drawy = dy + deviceStart[1];
					graphicsPanel.repaint();
				}
				spacePressed = false;
				movingDevice = false;
			}
		}
		return false;
	}
}

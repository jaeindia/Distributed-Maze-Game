package Peer2Peer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import Client.Client;
import Server.Constant;
import Server.Coordinate;
import Server.GameInfo;
import Server.Server;

public class P2PGameImpl extends UnicastRemoteObject implements P2PGame, Runnable,ActionListener,KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private P2PGameInfo gameInfoObj;
	private int gridSize;
	private int timeToStart;
	private int timeToStartVal;
	private boolean populateTreasureMap;
	private ArrayList<Coordinate> coordinateList;
	private int row;
	private int column;
	private Random random;
	private Coordinate userCoordinate;
	private static boolean primaryServer;
	private boolean secondaryServer;
	
	public String userId= null;
	public JFrame gameFrame = new JFrame();
	public JPanel panel = new JPanel();
	public JLabel welcomeText = new JLabel("Welcome to Distributed Maze Game!!",JLabel.CENTER);
	public JLabel userName = new JLabel("USERID",JLabel.LEFT);
	public JTextField keyboardEntry = new JTextField();
	public JLabel password = new JLabel("PASSWORD",JLabel.LEFT);
	public JTextField userNameField = new JTextField();
	public JPasswordField passwordField = new JPasswordField();
	public JLabel mazeField = new JLabel("MAZE",JLabel.LEFT);
	public JButton logIn = new JButton();
	public JButton signUp = new JButton();
   
    public boolean playerAddCheck=false;
    public boolean hasGamestarted = false;
    public boolean hasGameEnded = false;
    private Map<String,Coordinate> playerPostionMap = new ConcurrentHashMap<String,Coordinate>();
	private Map<Coordinate,Integer> treasureMap = new HashMap<Coordinate,Integer>();
  	private Map<String,Integer> playerScoreMap = new ConcurrentHashMap<String, Integer>();
  	
	
	public P2PGame serverStub;
//	private BufferedReader br;
	
//	private String msg;
	
	protected P2PGameImpl(boolean primaryServer) throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
		gameInfoObj = new P2PGameInfo();
		// Set gridSize and treasureCount
		gameInfoObj.setGridSize(5);
		gameInfoObj.setTreasureCount(10);
		
		gridSize = gameInfoObj.getGridSize();
		timeToStart = 20; // Time to start the game - hard coded
		timeToStartVal = 20;
		
		populateTreasureMap = true;
		coordinateList = new ArrayList<Coordinate>();
		random = new Random();
		userCoordinate = new Coordinate(0,0);
//		br = new BufferedReader(new InputStreamReader(System.in));
	}
	
	protected P2PGameImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
//		br = new BufferedReader(new InputStreamReader(System.in));
	}
	
	public static void main(String[] args) throws AlreadyBoundException, java.rmi.AlreadyBoundException, NotBoundException, IOException {
		// TODO Auto-generated method stub
		
		String tempString = null;
		
		System.out.println("Is this the primary Server?? [Y/N]");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		tempString = br.readLine();
		if (tempString != null && tempString.equalsIgnoreCase("Y")) {
			primaryServer = true;
		}
		else {
			primaryServer = false;
		}

		if (primaryServer) {
			P2PGameImpl serverStub = new P2PGameImpl(true);
			Registry registry = LocateRegistry.createRegistry(Constant.RMIPORT);
			registry.bind(Constant.RMIID, serverStub);
			spawnClient(serverStub);
		}
		else {

			System.out.println("Inside another player");
			P2PGameImpl clientObj = new P2PGameImpl();
			Registry registry = LocateRegistry.getRegistry("127.0.0.1", Constant.RMIPORT);
			clientObj.serverStub = (P2PGame) registry.lookup(Constant.RMIID);
            clientObj.gridSize = clientObj.serverStub.getGridSize();
			try{
				clientObj.initLoginFrame();
				while(!clientObj.hasGamestarted){
					System.out.println("Sleeping");
					Thread.sleep(100);
				}

				if(true){
					System.out.println(" Player added.. Game has started");
					clientObj.playerPostionMap= clientObj.serverStub.getPlayerPostionMap();	
					System.out.println(clientObj.playerPostionMap.size());
					clientObj.playerScoreMap = clientObj.serverStub.getPlayerScoreMap();
					clientObj.treasureMap = clientObj.serverStub.getTreasureMap();
					clientObj.displayupdatedMaze();
				}
			}catch(Exception ee){
				System.err.println("Error Initializing the game !! Game will exit now...");
				JOptionPane.showMessageDialog(null,
						"Error Initializing the game !! Game will Exit now");
			}
		}
	}
	
	
	private static void spawnClient(P2PGameImpl impl) {
		// TODO Auto-generated method stub
		
		try{
		System.out.println("Spawning Players....");
		Registry registry = LocateRegistry.getRegistry("127.0.0.1", Constant.RMIPORT);
		impl.serverStub = (P2PGame) registry.lookup(Constant.RMIID);
		impl.initLoginFrame();
	
		while(!impl.hasGamestarted){
			Thread.sleep(100);
		}
		//if(impl.hasGamestarted){
		
		if(true){
			System.out.println("Player added.. Game has started");
		   
			impl.playerPostionMap= impl.serverStub.getPlayerPostionMap();	
			impl.playerScoreMap = impl.serverStub.getPlayerScoreMap();
			impl.treasureMap = impl.serverStub.getTreasureMap();
			impl.displayupdatedMaze();
			//impl.readButtons();
			}
		}catch(Exception ee){
			System.err.println("Error Initializing the game !! Game will exit now..."+ee.getMessage());
			JOptionPane.showMessageDialog(null,
	                "Error Initializing the game !! Game will Exit now");
			
			
		}
		
		
	}

	private void displayupdatedMaze() {
		// TODO Auto-generated method stub
		
		
		//playerPostionMap.put("abhinav", new Coordinate(2, 2));
		//playerPostionMap.put("sarja", new Coordinate(4, 0));
		//treasureMap.put(new Coordinate(3,2), 2);
		//treasureMap.put(new Coordinate(4,3), 3);
		//Set<String> playerSet = playerPostionMap.keySet();
		System.out.println("***********************************************\n");
		   
			
			for (int i =0;i<this.gridSize;i++){
			for(int j=0;j<this.gridSize;j++){
				String present = entityCheck(i,j);
				//System.out.println(present);
				if(present!=null){
					System.out.print(padString(present,10));
				}else{
					System.out.print(padString("_____",10));
				}
				//System.out.print();
				
			}
			
			System.out.println();
			System.out.println();
		
		}
		System.out.println("************************************************\n");		          
		
	}
	
	private String entityCheck(int i, int j) {
		// TODO Auto-generated method stub
		String present = null;
		//System.out.println("Sizes are "+playerPostionMap.size()+","+treasureMap.size());
		try{
			//System.out.print(playerPostionMap.size());
		Set<String> playerSet = playerPostionMap.keySet();
		Iterator<String> itr = playerSet.iterator();
		while(itr.hasNext()){
			String user = itr.next().toString();
			Coordinate c = (Coordinate)playerPostionMap.get(user);
			if(i==c.getRow() && j==c.getColumn()){
				//System.out.println("Player exists");
				present = user;
				return user;
			}
			
		}
		//System.out.println(treasureMap.size());
		treasureMap = this.serverStub.getTreasureMap();
		Set<Coordinate> treasureSet = treasureMap.keySet();
		Iterator<Coordinate> itr1 = treasureSet.iterator();
		while(itr1.hasNext()){
			Coordinate c = (Coordinate) itr1.next();
			if(i==c.getRow() && j==c.getColumn()){
				//System.out.println("treasure exists");
				present = String.valueOf(treasureMap.get(c));
				return present;
				
			}
		}
		}catch(Exception e){
			System.out.println("Exception "+e.getMessage());
			return null;
		}
		return present;
	}

	
	@Override
	public void actionPerformed(ActionEvent buttonClick) {
		// TODO Auto-generated method stub
		System.out.println("Inside Action");
		JLabel errors = new JLabel();
		errors.setBounds(20, 130, 480, 80);
		errors.setForeground(Color.RED);	


		if(buttonClick.getSource() instanceof JButton){
			JButton button = (JButton) buttonClick.getSource();			
			String userName = this.userNameField.getText();
			StringBuilder passwordBuilder = new StringBuilder();
			for(char c : this.passwordField.getPassword()){				
				passwordBuilder.append(c);
			}

			String password = passwordBuilder.toString();			
			if(userName ==null || password==null || userName.isEmpty() || password.isEmpty()){				
				errors.setText("<html> Invalid Login!!.<br> Login Again!!</html>");
				errors.setVisible(true);				
				panel.add(errors);
				panel.repaint();
			}else{
				// when the login button is clicked, this method is invoked
				
				if("login".equals(button.getActionCommand())){
					try {
						
						//System.out.println("Output :"+serverStub.addUser(userName, password,this));
						if(serverStub.addUser(userName, password,this)){
						//if(true){
						System.out.println("Login Successful");
						    
							this.userId = this.userNameField.getText();
							for(Component component : panel.getComponents()){
								panel.remove(component);
							}
							JLabel welcomeUser = new JLabel();
							welcomeUser.setText("Welcome " + this.userId +  "," );
							welcomeUser.setVisible(true);
							welcomeUser.setBounds(0, 0, 500, 15);
							panel.add(welcomeUser);
							keyboardEntry.setLocation(0, 40);
							keyboardEntry.setBounds(150, 40, 160, 20);
							keyboardEntry.setVisible(true);
							keyboardEntry.addKeyListener(this);
							panel.add(keyboardEntry);		
							panel.repaint();
							this.playerAddCheck= true;
							this.hasGamestarted=true;
						}else{
							JOptionPane.showMessageDialog(null,
					                "Login failed !!\n"
					                + "Reasons:"
					                + " 1.User Exists."
					                + " 2.Wrong Password.");
						}
					}catch(Exception e){
                        System.out.println("Exception "+e.getMessage());
					}
				}
			}
		}
	}

	@Override
	public void notifyPlayer(boolean gameStarted) throws RemoteException {
		// TODO Auto-generated method stub
		
		
	}

	@Override
	public void notifyGameEnd(boolean gameEnded) throws RemoteException {
		// TODO Auto-generated method stub
		
	}


	public synchronized boolean addUser(String username, String password, P2PGame clientObj)
			throws RemoteException {
		// TODO Auto-generated method stub
		
		boolean flag = true;
		
		
		if (username != null && password != null) {
			flag = gameInfoObj.doesUserExist(username, password);
//			System.out.println("add user flag " + flag);
			if (!flag) {
				if (populateTreasureMap) {
					// Populate Treasure Map - hard coded
					gameInfoObj.populateTreasureMap();
					
//					System.out.println("populate treasure map\n");
					
					populateTreasureMap = false;
					
					for(Entry<Coordinate, Integer> entry: gameInfoObj.getP2PtreasureMap().entrySet()) {
//						System.out.println("Inside loop\n" + entry.getKey());
						coordinateList.add(entry.getKey());
//						System.out.println("Entry added\n");
					}
					
//					System.out.println("Outside loop\n");
				}
				
				
//				System.out.println("User is added ");
				
				do{
					row = random.nextInt(this.gridSize);
					column = random.nextInt(this.gridSize);
					userCoordinate = new Coordinate(row, column);
				} while(!coordinateList.contains(userCoordinate));
				coordinateList.add(userCoordinate);
				
				gameInfoObj.setP2PplayerPostionMap(username.toLowerCase(), userCoordinate);
				//gameInfoObj.setPlayerObjectMap(username.toLowerCase(), clientObj);
				
				if(this.timeToStart == this.timeToStartVal){
					Thread t = new Thread(this);
					t.start();
				}
				else if(this.timeToStart == 0){
					return false;
				}
			}
		}
		
//		System.out.println("add user");
//		System.out.println(gameInfoObj.getPlayerPostionMap());
//		System.out.println(gameInfoObj.getPlayerObjectMap().size());
		return !flag;
	}

	@Override
	public synchronized boolean moveUser(String username, Coordinate coordinate)
			throws RemoteException {
		// TODO Auto-generated method stub
		
		System.out.println("Inside Move "+coordinate.getRow()+","+coordinate.getColumn());
		Map<String,Coordinate> playerPositionMap = gameInfoObj.getP2PplayerPostionMap();
		boolean moved = true;
		// Check first if the user actually exists
		if (!gameInfoObj.doesUserExist(username, "TestGame")) {
			System.out.println("User does not exist");
			moved = false;
		}
//		System.out.println(coordinate.getRow()+", "+coordinate.getColumn()+","+ gridSize);
		// Check for valid coordinates
		if (coordinate.getRow() < 0 || coordinate.getRow() > gridSize-1
				|| coordinate.getColumn() < 0 || coordinate.getColumn() > gridSize-1) {
			System.out.println("Wrong Corodinates");
			moved= false;
		}
		
		// Search logic for the 1-1 position map
		if(playerPositionMap != null){
			for (Entry<String, Coordinate> entry : playerPositionMap.entrySet()) {
				
				if (Objects.equals(coordinate, entry.getValue())) {
					System.out.println("Another player is present in the coordinate");
		            moved = false;
		        }
		    }
		}
		// Update position
		System.out.println("moved flag value " + moved);
		if(moved){
			gameInfoObj.setP2PplayerPostionMap(username, coordinate);
			// Update treasure count map
//			if (gameInfoObj.updateTreasureMap(username, coordinate) == 1 
//					|| gameInfoObj.updateTreasureMap(username, coordinate) == -1) {
//				// Update user - treasure count
////				gameInfoObj.updatePlayerScoreMap(username);
//			}
		
			if (gameInfoObj.updateTreasureMap(username, coordinate) == -1) {
				// Update user - End Game
				Iterator<Entry<String, Client>> clientObjectIterator = gameInfoObj.getPlayerObjectMap().entrySet().iterator();
				while (clientObjectIterator.hasNext()) {
					Entry<String, Client> clientObjectEntry = clientObjectIterator.next();
					try {
						clientObjectEntry.getValue().notifyGameEnd(true);
					} 
					catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return moved;
	}


	@Override
	public synchronized int getGridSize() throws RemoteException {
		// TODO Auto-generated method stub
		return gridSize;
	}

	@Override
	public synchronized Map<String, Coordinate> getPlayerPostionMap() throws RemoteException {
		// TODO Auto-generated method stub
		return gameInfoObj.getP2PplayerPostionMap();
	}

	@Override
	public synchronized Map<Coordinate, Integer> getTreasureMap() throws RemoteException {
		// TODO Auto-generated method stub
		return gameInfoObj.getP2PtreasureMap();
	}

	@Override
	public synchronized Map<String, Integer> getPlayerScoreMap() throws RemoteException {
		// TODO Auto-generated method stub
		//System.out.println("Size of Scoer Map is "+gameInfoObj.getPlayerScoreMap().size());
		return gameInfoObj.getP2PplayerScoreMap();
	}

	@Override
	public boolean updateBackupServer() throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updatePrimaryServer() throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	public void initLoginFrame(){
		gameFrame.setBackground(Color.BLUE);
		gameFrame.setVisible(true);

		gameFrame.setSize(500, 500);
		gameFrame.setLocation(200, 200);
		panel.setLayout(null);
		panel.setName("Login panel");
		panel.setVisible(true);
		panel.setBounds(0, 0, (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth(), (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight());
		panel.setBackground(Color.LIGHT_GRAY);
		gameFrame.getContentPane().add(panel);

		welcomeText.setLocation(0, 5);
		welcomeText.setSize(500, 20);
		welcomeText.setVisible(true);
		panel.add(welcomeText);

		userName.setLocation(0, 40);
		userName.setSize(70, 10);
		userName.setVisible(true);
		panel.add(userName);		

		userNameField.setName("userName");
		userNameField.setEditable(true);
		userNameField.setVisible(true);
		userNameField.setBounds(150, 40, 160, 20);
		panel.add(userNameField);		

		password.setLocation(0, 70);  
		password.setSize(70, 10);
		password.setVisible(true);
		panel.add(password);		

		passwordField.setName("password");
		passwordField.setEditable(true);
		passwordField.setVisible(true);
		passwordField.setBounds(150, 70, 160, 20);
		panel.add(passwordField);		



		logIn.setName("login");
		logIn.setVisible(true);
		logIn.setBounds(85, 125, 120, 20);
		logIn.setText("ENTER GAME!!");
		logIn.setActionCommand("login");
		logIn.addActionListener(this);
		panel.add(logIn);		



		gameFrame.getContentPane().add(panel);	

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (primaryServer) {
			int timeToBegin = this.timeToStart;
			while(timeToBegin > 0){
				//System.out.println("Game Start - Countdown : " + timeToStart);
					timeToBegin--;
					
					try {
						Thread.sleep(1000L);
					} 
					catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					this.timeToStart = timeToBegin;
			}
			
			if (this.timeToStart == 0) {				
//				Set<String> userSet = gameInfoObj.getPlayerObjectMap().keySet();
//				Iterator clientObjectIterator = userSet.iterator();
//				System.out.println("Size is "+userSet.size());
//				while (clientObjectIterator.hasNext()) {
//					String user = (String) clientObjectIterator.next();
//					
//					Client c = (Client) gameInfoObj.getPlayerObjectMap().get(user);
//					try {
//						c.notifyPlayer(true);
//					} 
//					catch (RemoteException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
				
//				for (Map.Entry<Coordinate, Integer> entry : gameInfoObj.getTreasureMap().entrySet()) {
//				    Coordinate key = entry.getKey();
//				    System.out.println(key.getRow() + "," + key.getColumn() + " " + key.hashCode());
//				}
				
				Iterator<Entry<String, Client>> clientObjectIterator = gameInfoObj.getPlayerObjectMap().entrySet().iterator();
				while (clientObjectIterator.hasNext()) {
					Entry<String, Client> clientObjectEntry = clientObjectIterator.next();
					try {
						clientObjectEntry.getValue().notifyPlayer(true);
					} 
					catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	

	public static String padString(String str, int leng) {
        for (int i = str.length(); i <= leng; i++)
            str += " ";
        return str;
    }

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Here");
		if(!checkGameEnded()){
		try{
            boolean moved = false;
			playerPostionMap= this.serverStub.getPlayerPostionMap();	
			System.out.println(playerPostionMap.size());
			Coordinate c = playerPostionMap.get(userId);
			if(c!=null){
			System.out.println(c.toString());
			}else {
				System.out.println("c is null");
			}
			if(e.getKeyCode()==KeyEvent.VK_UP){
				System.out.println("UP");
				c.setRow(c.getRow()-1);
				c.setColumn(c.getColumn());
				
				System.out.println("Move is "+moved);
				moved = this.serverStub.moveUser(userId, c);
				System.out.println("Move is "+moved);
				if(moved){
				displayupdatedMaze();
				}else{
					System.out.println("Cannot Move");
				}
			}else if(e.getKeyCode()==KeyEvent.VK_DOWN){
				System.out.println("DOWN");
				c.setRow(c.getRow()+1);
				c.setColumn(c.getColumn());
				//System.out.println(c.toString());
				moved = this.serverStub.moveUser(userId, c);
				if(moved){
				displayupdatedMaze();
				}else{
					System.out.println("Cannot Move");
				}
			}else if(e.getKeyCode()==KeyEvent.VK_RIGHT){    
				System.out.println("RIGHT");
				c.setRow(c.getRow());
				c.setColumn(c.getColumn()+1);
				//System.out.println(c.toString());
				moved = this.serverStub.moveUser(userId, c);
				if(moved){
				displayupdatedMaze();
				}else{
					System.out.println("Cannot Move");
				}
			}else if(e.getKeyCode()==KeyEvent.VK_LEFT){
				System.out.println("LEFT");
				c.setRow(c.getRow());
				c.setColumn(c.getColumn()-1);
				//System.out.println(c.toString());
				moved = this.serverStub.moveUser(userId, c);
				if(moved){
				displayupdatedMaze();
				}else{
					System.out.println("Cannot Move");
				}
			}
			}catch(Exception e1){
                 System.out.println(e1.getMessage());
		}
		}else{
			System.out.println("Game has ended");
			try{
			playerScoreMap = serverStub.getPlayerScoreMap();
			System.out.println("***********************************************");
			System.out.println("FINAL SCORES\n");
			Set<String> scores = new HashSet<String>();
			scores = playerScoreMap.keySet();
			Iterator<String> scoreIterator = scores.iterator();
			System.out.println(scores.size());
			while(scoreIterator.hasNext()){
				String user = (String) scoreIterator.next();
				System.out.print(user+" - ");
				System.out.println(playerScoreMap.get(user));
			}
		
			}catch(Exception mm){
				
			}
			
			}
	}

	private boolean checkGameEnded() {
		// TODO Auto-generated method stub
		this.hasGameEnded= false;
		if(treasureMap.size()==0){
			this.hasGameEnded=true;
		}
		return this.hasGameEnded;
	}



	
	
	
}

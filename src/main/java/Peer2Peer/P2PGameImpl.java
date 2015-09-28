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
import java.util.List;
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

import Server.Constant;
import Server.Coordinate;

public class P2PGameImpl extends UnicastRemoteObject implements P2PGame, Runnable,ActionListener,KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public P2PGameInfo gameInfoObj;
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
	private static boolean backupServer;
	
	private String primaryServerId;
	private String backupServerId;
	
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
		timeToStart = 30; // Time to start the game - hard coded
		timeToStartVal = 30;
		
		populateTreasureMap = true;
		coordinateList = new ArrayList<Coordinate>();
		random = new Random();
		userCoordinate = new Coordinate(0,0);
//		br = new BufferedReader(new InputStreamReader(System.in));
	}
	
	protected P2PGameImpl() throws RemoteException {
		super();
		gameInfoObj = new P2PGameInfo();
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
			backupServer = false;
		}
		else {
			primaryServer = false;
			backupServer = true;
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
					System.out.println(" System.out.println");
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
	
	
//	private static void backUpUpdate() {
//		// TODO Auto-generated method stub
//		System.out.println("Backup Server and Primary Server up.. Need to copy data");
//	}

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
			this.userId = this.userNameField.getText();
			StringBuilder passwordBuilder = new StringBuilder();
			for(char c : this.passwordField.getPassword()){				
				passwordBuilder.append(c);
			}

			String password = passwordBuilder.toString();			
			if(userName ==null || password==null || userId.isEmpty() || password.isEmpty()){				
				errors.setText("<html> Invalid Login!!.<br> Login Again!!</html>");
				errors.setVisible(true);				
				panel.add(errors);
				panel.repaint();
			}else{
				// when the login button is clicked, this method is invoked
				
				if("login".equals(button.getActionCommand())){
					try {
						
						//System.out.println("Output :"+serverStub.addUser(userName, password,this));
						if(serverStub.addUser(userId, password, this)){
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
				gameInfoObj.addPlayerToList(username.toLowerCase());
				if (gameInfoObj.getPlayerList().size() == 2) {
					this.backupServer = true;
				}
				gameInfoObj.setPlayerObjectMap(username.toLowerCase(), clientObj);
				
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
			
			//Update the backupServer after each move
			gameInfoObj.getPlayerObjectMap().get(gameInfoObj.getPlayerList().get(1)).updateBackupServer();
		
			if (gameInfoObj.updateTreasureMap(username, coordinate) == -1) {
				hasGameEnded = true;
				
				// Update user - End Game
				Iterator<Entry<String, P2PGame>> clientObjectIterator = gameInfoObj.getPlayerObjectMap().entrySet().iterator();
				while (clientObjectIterator.hasNext()) {
					Entry<String, P2PGame> clientObjectEntry = clientObjectIterator.next();
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
	
//	@Override
//	public synchronized void updateToBackupServer(P2PGameStats gameStats) throws RemoteException {
//		System.out.println("SERVER: " + this.userID);
//		System.out.println("Updating the Server: " + this.userID + " to Backup Server..");
//		this.gameStats.setPrimaryServer(gameStats.getPrimaryServer());
//		this.gameStats.setBackupServer(gameStats.getBackupServer());
//		this.gameStats.setBackupServerID(gameStats.getBackupServerID());
//		this.gameStats.setPrimaryServerID(gameStats.getPrimaryServerID());
//		System.out.println("Primary Server :" + this.gameStats.getPrimaryServerID());
//		System.out.println("Backup Server: " + this.gameStats.getBackupServerID());
//		this.isPrimaryServer = false;
//		this.isBackupServer = true;
//		System.out.println("isPrimaryServer? " + isPrimaryServer);
//		System.out.println("isBackupServer? " + isBackupServer);		
//		Thread t = new Thread(this);
//		t.start();		
//	}	

	@Override
	public synchronized void updateBackupServer() throws RemoteException {
		P2PGameInfo primaryServerGameInfoObj = this.serverStub.getGameInfoObj();
		
		this.gameInfoObj.setGridSize(primaryServerGameInfoObj.getGridSize());
		this.gameInfoObj.setTreasureCount(primaryServerGameInfoObj.getTreasureCount());
		this.gameInfoObj.setTreasureCounter(primaryServerGameInfoObj.getTreasureCounter());
		this.gameInfoObj.setPlayerList(primaryServerGameInfoObj.getPlayerList());
		this.gameInfoObj.setPlayerObjectMap(primaryServerGameInfoObj.getPlayerObjectMap());
		this.gameInfoObj.setP2PplayerPostionMap(primaryServerGameInfoObj.getP2PplayerPostionMap());
		this.gameInfoObj.setP2PtreasureMap(primaryServerGameInfoObj.getP2PtreasureMap());
		this.gameInfoObj.setP2PplayerScoreMap(primaryServerGameInfoObj.getP2PplayerScoreMap());	
	}
	
	@SuppressWarnings("static-access")
	@Override
	public synchronized boolean upgradeToBackupServer() throws RemoteException {
		P2PGameInfo primaryServerGameInfoObj = this.serverStub.getGameInfoObj();
		// TODO Auto-generated method stub
//		System.out.println("Player Object Map\n" + this.gameInfoObj.getPlayerObjectMap());
//		System.out.println(backupServerObj);
//		System.out.println("Update Backup server");
//		System.out.println("Backup server" + this.gameInfoObj.getPlayerList().get(1));
		this.gameInfoObj.setGridSize(primaryServerGameInfoObj.getGridSize());
		this.gameInfoObj.setTreasureCount(primaryServerGameInfoObj.getTreasureCount());
		this.gameInfoObj.setTreasureCounter(primaryServerGameInfoObj.getTreasureCounter());
		this.gameInfoObj.setPlayerList(primaryServerGameInfoObj.getPlayerList());
		this.gameInfoObj.setPlayerObjectMap(primaryServerGameInfoObj.getPlayerObjectMap());
		this.gameInfoObj.setP2PplayerPostionMap(primaryServerGameInfoObj.getP2PplayerPostionMap());
		this.gameInfoObj.setP2PtreasureMap(primaryServerGameInfoObj.getP2PtreasureMap());
		this.gameInfoObj.setP2PplayerScoreMap(primaryServerGameInfoObj.getP2PplayerScoreMap());
		
		this.primaryServer = false;
		this.backupServer = true;
		
		System.out.println("Primary Server : " + this.gameInfoObj.getPlayerList().get(0));
		System.out.println("Backup Server : " + this.gameInfoObj.getPlayerList().get(1));
		
		Thread t = new Thread(this);
		t.start();
		
		return true;
	}

	@Override
	public synchronized boolean upgradeToPrimaryServer() throws RemoteException {
		// TODO Auto-generated method stub
		if (this.gameInfoObj.getPlayerList().size() > 1) {
			this.gameInfoObj.getPlayerObjectMap().get(this.gameInfoObj.getPlayerList().get(1)).upgradeToBackupServer();
			System.out.println("Created new backupServer " + this.gameInfoObj.getPlayerList().get(1));
		}
		else {
			System.out.println("No backupServer available");
		}
		
//		this.gameInfoObj
		return true;
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

	@SuppressWarnings("static-access")
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (primaryServer) {
			int timeToBegin = this.timeToStart;
			while(timeToBegin > 0){
				System.out.println("Game Start - Countdown : " + timeToStart);
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
//				try {
//					updateBackupServer();
//				} catch (RemoteException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				
//				System.out.println("Player Object Map");
//				System.out.println(gameInfoObj.getPlayerObjectMap());
				
				
				primaryServerId = gameInfoObj.getPlayerList().get(0);
				if(gameInfoObj.getPlayerList().size() < 2){
					throw new RuntimeException("We need more than two players to start the game.. Exiting....");
				} else {
					backupServerId = gameInfoObj.getPlayerList().get(1);
					// Create backupServer
					try {
						this.gameInfoObj.getPlayerObjectMap().get(gameInfoObj.getPlayerList().get(1)).upgradeToBackupServer();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				Iterator<Entry<String, P2PGame>> clientObjectIterator = gameInfoObj.getPlayerObjectMap().entrySet().iterator();
				while (clientObjectIterator.hasNext()) {
					Entry<String, P2PGame> clientObjectEntry = clientObjectIterator.next();
					try {
						clientObjectEntry.getValue().notifyPlayer(true);
					} 
					catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				// Handled backupServer crash
				while(primaryServer && !this.hasGameEnded){
					try{
						Thread.sleep(5000L);
						clientObjectIterator = gameInfoObj.getPlayerObjectMap().entrySet().iterator();
						while (clientObjectIterator.hasNext()) {
							Entry<String, P2PGame> clientObjectEntry = clientObjectIterator.next();
							try {
								@SuppressWarnings("unused")
								boolean flag = clientObjectEntry.getValue().isAlive();
							} 
							catch (RemoteException e) {
								// TODO Auto-generated catch block
								System.err.println("Player " + clientObjectEntry.getKey() + " has QUIT");
								// Remove from playerList
								List<String> playerList = this.gameInfoObj.getPlayerList();
								String backupServer = clientObjectEntry.getKey();
								playerList.remove(clientObjectEntry.getKey());
								this.gameInfoObj.setPlayerList(playerList);
								// Remove from playerPositionMap
								Map<String, Coordinate> p2PplayerPostionMap = this.gameInfoObj.getP2PplayerPostionMap();
								p2PplayerPostionMap.remove(clientObjectEntry.getKey());
								this.gameInfoObj.setP2PplayerPostionMap(p2PplayerPostionMap);
								// Remove from playerObjectMap
								Map<String, P2PGame> playerObjectMap = this.gameInfoObj.getPlayerObjectMap();
								playerObjectMap.remove(clientObjectEntry.getKey());
								this.gameInfoObj.setPlayerObjectMap(playerObjectMap);
								
								if (this.gameInfoObj.getPlayerList().size() > 1) {
									if (backupServer.equalsIgnoreCase(backupServerId)) {
										System.out.println("backupServer " + backupServer + " has crashed");
										// New backupServer
										backupServerId = playerList.get(1);
										System.out.println("Creating new backupServer " + backupServerId);
									}
									
									try {
										this.gameInfoObj.getPlayerObjectMap().get(backupServerId).upgradeToBackupServer();
									} catch (RemoteException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								}
							}
						}
						
					}catch(InterruptedException e){
						e.printStackTrace();
					}
				}

				// Handled primaryServer crash
				while(backupServer && !this.hasGameEnded){
					try {
						Thread.sleep(5000L);
						@SuppressWarnings("unused")
						boolean flag = this.gameInfoObj.getPlayerObjectMap().get(this.gameInfoObj.getPlayerList().get(0)).isAlive();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						System.err.println("primaryServer " + this.gameInfoObj.getPlayerList().get(0) + "has crashed");
						
						System.err.println("Upgrading backupServer " + this.gameInfoObj.getPlayerList().get(1) + " to primaryServer");
						System.err.println("Creating new backupServer " + this.gameInfoObj.getPlayerList().get(2));
						
						// Remove from playerPositionMap
						Map<String, Coordinate> p2PplayerPostionMap = this.gameInfoObj.getP2PplayerPostionMap();
						p2PplayerPostionMap.remove(this.gameInfoObj.getPlayerList().remove(0));
						this.gameInfoObj.setP2PplayerPostionMap(p2PplayerPostionMap);
						// Remove from playerObjectMap
						Map<String, P2PGame> playerObjectMap = this.gameInfoObj.getPlayerObjectMap();
						playerObjectMap.remove(this.gameInfoObj.getPlayerList().remove(0));
						this.gameInfoObj.setPlayerObjectMap(playerObjectMap);
						// Remove from playerList
						List<String> playerList = this.gameInfoObj.getPlayerList();
						playerList.remove(this.gameInfoObj.getPlayerList().remove(0));
						this.gameInfoObj.setPlayerList(playerList);
						
						try {
							this.gameInfoObj.getPlayerObjectMap().get(this.gameInfoObj.getPlayerList().get(0)).upgradeToPrimaryServer();
						} catch (RemoteException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						// New primaryServer and backupServer
						this.primaryServerId = this.gameInfoObj.getPlayerList().get(0);
						this.backupServerId = null;
						
						if (this.gameInfoObj.getPlayerList().size() > 1) {
							this.backupServerId = this.gameInfoObj.getPlayerList().get(1);
						}
						
						this.primaryServer = true;
						this.backupServer = false;
						
						Thread.currentThread().interrupt();
						Thread t = new Thread(this);
						t.start();
						break;
					}
					
				}
			}
//			backUpUpdate();
		}
		
	}
	
	public boolean isAlive() {
		return true;
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
//			System.out.println("Size " + playerPostionMap.size() + "Username " + this.userId);
			Coordinate c = playerPostionMap.get(this.userId.toLowerCase());
//			System.out.println("BACKUP server check\n");
//			System.out.println(this.gameInfoObj.getP2PplayerPostionMap() + "\n");
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

	@Override
	public P2PGameInfo getGameInfoObj() throws RemoteException {
		// TODO Auto-generated method stub
		P2PGameInfo gameData = null;
		gameData = this.gameInfoObj;
		
		return gameData;
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}



	
	
	
}

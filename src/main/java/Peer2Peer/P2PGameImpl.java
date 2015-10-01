package Peer2Peer;
/*  *****************************************************
 * Distributed Systems Assignment - 
 * @author
 * Abhinav Sarja - A0134505N
 * Jayakumar - 
 * 
 * This class is the final version of the assignment providing the required functionality as mentioned in the Assignment document 1.
 * 
 *  *****************************************************
 */


/*
 * 
 *   Import Statements required for the class 
 * 
 */

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
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;


import javax.swing.JOptionPane;



public class P2PGameImpl extends UnicastRemoteObject implements P2PGame, Runnable {

	/**
	 * Class Variables required for the class
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
	public boolean playerAddCheck=false;
	public boolean hasGamestarted = false;
	public boolean hasGameEnded = false;
	private Map<String,Coordinate> playerPostionMap = new ConcurrentHashMap<String,Coordinate>();
	private Map<Coordinate,Integer> treasureMap = new HashMap<Coordinate,Integer>();
	private Map<String,Integer> playerScoreMap = new ConcurrentHashMap<String, Integer>();
	public P2PGame serverStub;

	/*
	 * Constructor methods
	 * P2PGameImpl(boolean primaryServer)
	 * P2PGameImpl()
	 */

	protected P2PGameImpl(boolean primaryServer) throws RemoteException {
		super();

		gameInfoObj = new P2PGameInfo();	
		gameInfoObj.setGridSize(5);
		gameInfoObj.setTreasureCount(10);
		gridSize = gameInfoObj.getGridSize();
		timeToStart = 30; // Time to start the game - hard coded
		timeToStartVal = 30;

		populateTreasureMap = true;
		coordinateList = new ArrayList<Coordinate>();
		random = new Random();
		userCoordinate = new Coordinate(0,0);

	}

	protected P2PGameImpl() throws RemoteException {
		super();
		gameInfoObj = new P2PGameInfo();
	}
	/*
	 *   Main method
	 * 
	 */

	public static void main(String[] args) throws AlreadyBoundException, java.rmi.AlreadyBoundException, NotBoundException, IOException {

		String tempString = null;
		tempString = args[0];
		String username = args[1];
		String password = args[2];
		if (tempString != null && tempString.equalsIgnoreCase("Y")) {
			primaryServer = true;
			backupServer = false;
			
			
			
		}
		else if (tempString != null && tempString.equalsIgnoreCase("N")) {
			primaryServer = false;
			backupServer = true;
		}
		else{
			System.out.println("************************");
			System.out.println("Wrong input PLease use the below command to run the server !!\n"
					+ "Server //-java Peer2Peer.P2PGameImpl Y\n"
					+ "Client //-java Peer2Peer.P2PGameImpl N\n");
			System.out.println("************************");
		}

		if (primaryServer) {
			P2PGameImpl serverStub = new P2PGameImpl(true);
			Registry registry = LocateRegistry.createRegistry(Constant.RMIPORT);
			try {
				registry.bind(Constant.RMIID, serverStub);
			}
			catch (Exception e) {
				registry.unbind(Constant.RMIID);
				System.out.println("RECREATING REGISTRY");
				registry.bind(Constant.RMIID, serverStub);
			}
		spawnClient(serverStub,username, password);

		}
		else {
			//			
			P2PGameImpl clientObj = new P2PGameImpl();
			Registry registry = LocateRegistry.getRegistry("127.0.0.1", Constant.RMIPORT);
			clientObj.serverStub = (P2PGame) registry.lookup(Constant.RMIID);
			clientObj.gridSize = clientObj.serverStub.getGridSize();
			
			//scanner.close();
			clientObj.actionPerformed(username,password);

			try{
				clientObj.initGame();

				while(!clientObj.hasGamestarted){
				
					Thread.sleep(100);
				}

				clientObj.playerPostionMap= clientObj.serverStub.getPlayerPostionMap();	
				clientObj.playerScoreMap = clientObj.serverStub.getPlayerScoreMap();
				clientObj.treasureMap = clientObj.serverStub.getTreasureMap();
				clientObj.displayupdatedMaze();
				clientObj.readButtons();
			}catch(Exception ee){
				System.err.println("Error Initializing the game !! Game will exit now...");
				JOptionPane.showMessageDialog(null,
						"Error Initializing the game !! Game will Exit now");
			}
		}
	}


	private  void readButtons() { 
		System.out.println("Game has started .. Please click the below buttons to start playing the game...\n");
		System.out.println("W - UP , A - LEFT ,S - DOWN , D - RIGHT");
		while(!hasGameEnded){

			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			try 
			{
				String ascii = br.readLine();

				keyPressed(ascii);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	private static void spawnClient(P2PGameImpl impl,String username,String password) {
		try{
			System.out.println("Spawning Players....");
			Registry registry = LocateRegistry.getRegistry("127.0.0.1", Constant.RMIPORT);
			impl.serverStub = (P2PGame) registry.lookup(Constant.RMIID);
			
			impl.actionPerformed(username,password);

			while(!impl.hasGamestarted){
				Thread.sleep(100);
			}

			if(true){
				impl.playerPostionMap= impl.serverStub.getPlayerPostionMap();	
				impl.playerScoreMap = impl.serverStub.getPlayerScoreMap();
				impl.treasureMap = impl.serverStub.getTreasureMap();
				impl.displayupdatedMaze();
				impl.readButtons();
			}
		}catch(Exception ee){
			System.err.println("Error Initializing the game !! Game will exit now..."+ee.getMessage());
			JOptionPane.showMessageDialog(null,
					"Error Initializing the game !! Game will Exit now");


		}
	}

	private void displayupdatedMaze() {

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
		
		String present = null;
		try{

			Set<String> playerSet = playerPostionMap.keySet();
			Iterator<String> itr = playerSet.iterator();
			while(itr.hasNext()){
				String user = itr.next().toString();
				Coordinate c = (Coordinate)playerPostionMap.get(user);
				if(i==c.getRow() && j==c.getColumn()){//System.out.println("Player exists");
					present = user;
					return user;
				}
			}
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


	public void actionPerformed(String userName, String password) {

		if(userName ==null || password==null || userName.isEmpty() || password.isEmpty()){				
			System.out.println("Invalid Login!!.<br> Login Again!!");

		}else{
			try {
				if(this.serverStub.addUser(userName, password,this)){
					//if(true){
					System.out.println("Login Successful");
					this.userId = userName;
					this.playerAddCheck= true;
				}else{
					JOptionPane.showMessageDialog(null,
							"Login failed !!\n"
									+ "Reasons:"
									+ " 1.User Exists."
									+ " 2.Wrong Password.");
				}
			}catch(Exception e){

			}
		}
	}

	@Override
	public void notifyPlayer(boolean gameStarted) throws RemoteException {

		this.hasGamestarted=true;

	}

	@Override
	public boolean notifyGameEnd(boolean gameEnded) throws RemoteException {

		return gameEnded;
	}


	public synchronized boolean addUser(String username, String password, P2PGame clientObj)
			throws RemoteException {
		boolean flag = true;

		if (username != null && password != null) {
			flag = gameInfoObj.doesUserExist(username, password);

			if (!flag && this.timeToStart > 0) {
				if (populateTreasureMap) {

					gameInfoObj.populateTreasureMap();
					populateTreasureMap = false;
					for(Entry<Coordinate, Integer> entry: gameInfoObj.getP2PtreasureMap().entrySet()) {
						coordinateList.add(entry.getKey());

					}

				}

				while(true){
					row = random.nextInt(this.gridSize);
					column = random.nextInt(this.gridSize);
					userCoordinate = new Coordinate(row, column);
					if(!coordinateList.contains(userCoordinate)){
						coordinateList.add(userCoordinate);
						break;
					}
					else {
						continue;
					}
				}


				gameInfoObj.setP2PplayerPostionMap(username.toLowerCase(), userCoordinate);
				gameInfoObj.addPlayerToList(username.toLowerCase());
				gameInfoObj.addtoP2PPlayerScoreMap(username.toLowerCase());
				gameInfoObj.setPlayerObjectMap(username.toLowerCase(), clientObj);

				if(this.timeToStart == this.timeToStartVal){
					Thread t = new Thread(this);
					t.start();
				}
			}
			else {
				return false;
			}
		}


		return !flag;
	}

	@Override
	public synchronized boolean moveUser(String username, Coordinate coordinate)
			throws RemoteException {
		
		Map<String,Coordinate> playerPositionMap = gameInfoObj.getP2PplayerPostionMap();
		boolean moved = true;
		
		if (!gameInfoObj.doesUserExist(username, "TestGame")) {
			System.out.println("User does not exist");
			moved = false;
		}

		if (coordinate.getRow() < 0 || coordinate.getRow() > gridSize-1
				|| coordinate.getColumn() < 0 || coordinate.getColumn() > gridSize-1) {
			System.out.println("Wrong Coordinates");
			moved= false;
		}


		if(playerPositionMap != null){
			for (Entry<String, Coordinate> entry : playerPositionMap.entrySet()) {

				if (Objects.equals(coordinate, entry.getValue())) {
				
					moved = false;
				}
			}
		}

		if(moved){
			gameInfoObj.setP2PplayerPostionMap(username, coordinate);

			gameInfoObj.getPlayerObjectMap().get(gameInfoObj.getPlayerList().get(1)).updateBackupServer();

			if (gameInfoObj.updateTreasureMap(username, coordinate) == -1) {
				this.hasGameEnded = true;
				Iterator<Entry<String, P2PGame>> clientObjectIterator = gameInfoObj.getPlayerObjectMap().entrySet().iterator();
				while (clientObjectIterator.hasNext()) {
					Entry<String, P2PGame> clientObjectEntry = clientObjectIterator.next();
					try {
						clientObjectEntry.getValue().notifyGameEnd(true);
					} 
					catch (RemoteException e) {
					
						e.printStackTrace();
					}
				}
			}
		}
		return moved;
	}


	@Override
	public synchronized int getGridSize() throws RemoteException {
	
		return gridSize;
	}

	@Override
	public synchronized Map<String, Coordinate> getPlayerPostionMap() throws RemoteException {

		return gameInfoObj.getP2PplayerPostionMap();
	}

	@Override
	public synchronized Map<Coordinate, Integer> getTreasureMap() throws RemoteException {

		return gameInfoObj.getP2PtreasureMap();
	}

	@Override
	public synchronized Map<String, Integer> getPlayerScoreMap() throws RemoteException {

		return gameInfoObj.getP2PplayerScoreMap();
	}


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

		System.out.println("Starting Backup Server Thread : " + this.gameInfoObj.getPlayerList().get(1));

		Thread t = new Thread(this);
		t.start();

		return true;
	}

	@Override
	public synchronized boolean upgradeToPrimaryServer() throws RemoteException {

		if (this.gameInfoObj.getPlayerList().size() > 1 && this.gameInfoObj.getP2PtreasureMap().size() != 0) {
			this.gameInfoObj.getPlayerObjectMap().get(this.gameInfoObj.getPlayerList().get(1)).upgradeToBackupServer();
			System.out.println("Created new backupServer " + this.gameInfoObj.getPlayerList().get(1));
		}
		else {
			System.out.println("No backupServer available");
			System.out.println("Game is exiting.....");
		}


		return true;
	}

	public void initGame(){


	}

	@SuppressWarnings("static-access")
	@Override
	public void run() {

		if (primaryServer) {
			int timeToBegin = this.timeToStart;
			while(timeToBegin > 0){
            System.out.println("Game will start in "+timeToBegin);
				timeToBegin--;
                
				try {
					Thread.sleep(1000L);
				} 
				catch (InterruptedException e) {

					e.printStackTrace();
				}

				this.timeToStart = timeToBegin;
			}

			if (this.timeToStart == 0) {				



				this.primaryServerId = gameInfoObj.getPlayerList().get(0);
				if(gameInfoObj.getPlayerList().size() < 2){
					throw new RuntimeException("We need atleast two players to start the game.. Exiting....");
					
				} else {
					this.backupServerId = gameInfoObj.getPlayerList().get(1);
					
					try {
						this.gameInfoObj.getPlayerObjectMap().get(gameInfoObj.getPlayerList().get(1)).upgradeToBackupServer();
					} catch (RemoteException e) {
					
						e.printStackTrace();
					}
				}

				System.out.println("Notifying GAMESTART");

				Iterator<Entry<String, P2PGame>> clientObjectIterator = gameInfoObj.getPlayerObjectMap().entrySet().iterator();
				while (clientObjectIterator.hasNext()) {
					Entry<String, P2PGame> clientObjectEntry = clientObjectIterator.next();
					try {
						clientObjectEntry.getValue().notifyPlayer(true);
					} 
					catch (RemoteException e) {

						e.printStackTrace();
					}
				}
			}
		}

		while(primaryServer && !this.hasGameEnded){
			try{
				Iterator<Entry<String, P2PGame>> clientObjectIterator = gameInfoObj.getPlayerObjectMap().entrySet().iterator();
				Thread.sleep(1000L);
				clientObjectIterator = gameInfoObj.getPlayerObjectMap().entrySet().iterator();


				while (clientObjectIterator.hasNext()) {
					Entry<String, P2PGame> clientObjectEntry = clientObjectIterator.next();
					try {
						@SuppressWarnings("unused")
						boolean flag = clientObjectEntry.getValue().isAlive();
					} 
					catch (RemoteException e) {

						System.err.println("Player " + clientObjectEntry.getKey() + " EXITED");

						if ((this.gameInfoObj.getPlayerList().size()- 1) < 2) {
							System.out.println("\nWe need atleast two players to play the game.. Exiting....\n");
							finalPlayerScores();
							/*Thread.currentThread().interrupt();
							System.exit(0);*/
						}


						List<String> playerList = this.gameInfoObj.getPlayerList();
						String backupServer = clientObjectEntry.getKey();
						playerList.remove(clientObjectEntry.getKey());
						this.gameInfoObj.setPlayerList(playerList);

						Map<String, Coordinate> p2PplayerPostionMap = this.gameInfoObj.getP2PplayerPostionMap();
						p2PplayerPostionMap.remove(clientObjectEntry.getKey());
						this.gameInfoObj.setP2PplayerPostionMap(p2PplayerPostionMap);

						Map<String, P2PGame> playerObjectMap = this.gameInfoObj.getPlayerObjectMap();
						playerObjectMap.remove(clientObjectEntry.getKey());
						this.gameInfoObj.setPlayerObjectMap(playerObjectMap);

						if (this.gameInfoObj.getPlayerList().size() > 1 
								&& !this.gameInfoObj.getPlayerList().contains(backupServer)) {
							if (backupServer.equalsIgnoreCase(backupServerId)) {
								System.out.println("backupServer " + backupServer + " has crashed");

								this.backupServerId = playerList.get(1);
								System.out.println("Creating new backupServer " + backupServerId);
								if (this.gameInfoObj.getP2PtreasureMap().size() != 0) {
									try {
										this.gameInfoObj.getPlayerObjectMap().get(backupServerId).upgradeToBackupServer();
									} catch (RemoteException e1) {

										e1.printStackTrace();
									}
								}
							}
						}
						if (this.gameInfoObj.getPlayerList().size() > 1) {
							//							System.out.println("CALLING ...");
							this.gameInfoObj.getPlayerObjectMap().get(gameInfoObj.getPlayerList().get(1)).updateBackupServer();
						}
					}
				}

			}catch(Exception e){
				e.printStackTrace();
			}
		}


		while(backupServer && !this.hasGameEnded){
			try {

				Thread.sleep(5000L);

				@SuppressWarnings("unused")
				boolean flag = this.gameInfoObj.getPlayerObjectMap().get(this.gameInfoObj.getPlayerList().get(0)).isAlive();
			} catch (Exception e) {

				if ((this.gameInfoObj.getPlayerList().size()- 1) < 2) {
					System.err.println("\nWe need atleast two players to play the game.. Exiting....\n");
					finalPlayerScores();
					/*Thread.currentThread().interrupt();
					System.exit(0);*/
				}

				System.err.println("primaryServer " + this.gameInfoObj.getPlayerList().get(0) + " has crashed");

				this.primaryServerId = this.gameInfoObj.getPlayerList().get(1);
				System.err.println("Upgrading backupServer " + this.gameInfoObj.getPlayerList().get(1) + " to primaryServer\n");


				Map<String, Coordinate> p2PplayerPostionMap = this.gameInfoObj.getP2PplayerPostionMap();
				p2PplayerPostionMap.remove(this.gameInfoObj.getPlayerList().get(0));
				this.gameInfoObj.setP2PplayerPostionMap(p2PplayerPostionMap);

				Map<String, P2PGame> playerObjectMap = this.gameInfoObj.getPlayerObjectMap();
				playerObjectMap.remove(this.gameInfoObj.getPlayerList().get(0));
				this.gameInfoObj.setPlayerObjectMap(playerObjectMap);



				List<String> playerList = this.gameInfoObj.getPlayerList();
				playerList.remove(this.gameInfoObj.getPlayerList().get(0));
				this.gameInfoObj.setPlayerList(playerList);

				if (this.gameInfoObj.getPlayerList().size() > 1) {
					System.err.println("Creating new backupServer " + this.gameInfoObj.getPlayerList().get(1));
				}

				Registry registry = null;
				try {
					this.primaryServer=true;

					registry = LocateRegistry.createRegistry(Constant.RMIPORT);
					registry.bind(Constant.RMIID, this);
					Iterator<Entry<String, P2PGame>> clientObjectIterator = gameInfoObj.getPlayerObjectMap().entrySet().iterator();
					while (clientObjectIterator.hasNext()) {
						Entry<String, P2PGame> clientObjectEntry = clientObjectIterator.next();
						try {
							clientObjectEntry.getValue().notifynewRegistry();
						} 
						catch (RemoteException excp1) {

							e.printStackTrace();
						}
					}
				}catch(Exception bindexcpt){
					System.out.println("Exception "+bindexcpt.getMessage());
				}
				try {

					this.gameInfoObj.getPlayerObjectMap().get(this.gameInfoObj.getPlayerList().get(0)).upgradeToPrimaryServer();
				} catch (RemoteException e1) {

					
				}


				this.primaryServerId = this.gameInfoObj.getPlayerList().get(0);
				this.backupServerId = null;

				if (this.gameInfoObj.getPlayerList().size() > 1) {
					this.backupServerId = this.gameInfoObj.getPlayerList().get(1);
				}

				this.primaryServer = true;
				this.backupServer = false;


				this.timeToStart = -1;

				Thread.currentThread().interrupt();
				Thread t = new Thread(this);
				t.start();
				break;
			}

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


	public void keyPressed(String e) {

		
		if(!checkGameEnded()){
			try{
				boolean moved = false;
				playerPostionMap= this.serverStub.getPlayerPostionMap();	
				System.out.println("Size is "+playerPostionMap.size());
				Coordinate c = playerPostionMap.get(userId);
				if(c!=null){
					System.out.println(c.toString());
				}else {
					System.out.println("c is null ");
				}
				if(e.equalsIgnoreCase("W")){
					System.out.println("UP");
					c.setRow(c.getRow()-1);
					c.setColumn(c.getColumn());

					System.out.println("Move is "+moved);
					moved = this.serverStub.moveUser(userId, c);
					System.out.println("Move is "+moved);
					if(moved){
						displayupdatedMaze();
						this.printPlayerScores(this.checkGameEnded());
					}else{
						System.out.println("Cannot Move");
					}
				}else if(e.equalsIgnoreCase("S")){
					System.out.println("DOWN");
					c.setRow(c.getRow()+1);
					c.setColumn(c.getColumn());

					moved = this.serverStub.moveUser(userId, c);
					if(moved){
						displayupdatedMaze();
						this.printPlayerScores(this.checkGameEnded());
					}else{
						System.out.println("Cannot Move");
					}
				}else if(e.equalsIgnoreCase("D")){    
					System.out.println("RIGHT");
					c.setRow(c.getRow());
					c.setColumn(c.getColumn()+1);

					moved = this.serverStub.moveUser(userId, c);
					if(moved){
						displayupdatedMaze();
						this.printPlayerScores(this.checkGameEnded());
					}else{
						System.out.println("Cannot Move");
					}
				}else if(e.equalsIgnoreCase("A")){
					System.out.println("LEFT");
					c.setRow(c.getRow());
					c.setColumn(c.getColumn()-1);

					moved = this.serverStub.moveUser(userId, c);
					if(moved){
						displayupdatedMaze();
						this.printPlayerScores(this.checkGameEnded());
					}else{
						System.out.println("Cannot Move");
					}
				}
			}catch(Exception e1){
				System.out.println(e1.getMessage());
			}
		}else{
			this.finalPlayerScores();
		}
	}
	
	private void printPlayerScores(boolean gameEnded){
		if (gameEnded) {
			this.finalPlayerScores();
		}
		else {
			this.interimPlayerScores();
		}
	}

	private void interimPlayerScores() {
		try{
			playerScoreMap = serverStub.getPlayerScoreMap();
			System.out.println("***********************************************");
			System.out.println("PLAYER SCORES\n");
			Set<String> scores = new HashSet<String>();
			scores = playerScoreMap.keySet();
			Iterator<String> scoreIterator = scores.iterator();
			//			System.out.println(scores.size());
			while(scoreIterator.hasNext()){
				String user = (String) scoreIterator.next();
				System.out.print(user+" - ");
				System.out.println(playerScoreMap.get(user));
			}
			System.out.println();
			System.out.println("***********************************************");

		}catch(Exception mm){
			mm.printStackTrace();
		}
	}

	private void finalPlayerScores() {
		System.out.println("Game has ended");
		try{
			playerScoreMap = serverStub.getPlayerScoreMap();
			System.out.println("***********************************************");
			System.out.println("FINAL SCORES\n");
			Set<String> scores = new HashSet<String>();
			scores = playerScoreMap.keySet();
			Iterator<String> scoreIterator = scores.iterator();

			while(scoreIterator.hasNext()){
				String user = (String) scoreIterator.next();
				System.out.print(user+" - ");
				System.out.println(playerScoreMap.get(user));
			}
			System.out.println();
			System.out.println("***********************************************");
		   
		}catch(Exception mm){
		  playerScoreMap = this.gameInfoObj.getP2PplayerScoreMap();
		  System.out.println("***********************************************");
			System.out.println("FINAL SCORES\n");
			Set<String> scores = new HashSet<String>();
			scores = playerScoreMap.keySet();
			Iterator<String> scoreIterator = scores.iterator();

			while(scoreIterator.hasNext()){
				String user = (String) scoreIterator.next();
				System.out.print(user+" - ");
				System.out.println(playerScoreMap.get(user));
			}
			System.out.println();
			System.out.println("***********************************************");
		   
		}
	}

	private boolean checkGameEnded() {

		this.hasGameEnded= false;

		try{

			if(this.serverStub.getTreasureMap().size()==0){
				this.hasGameEnded=true;
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		return this.hasGameEnded;
	}

	@Override
	public P2PGameInfo getGameInfoObj() throws RemoteException {

		P2PGameInfo gameData = null;
		gameData = this.gameInfoObj;

		return gameData;
	}



	@Override
	public void notifynewRegistry() throws RemoteException {

		Registry registry = LocateRegistry.getRegistry("127.0.0.1", Constant.RMIPORT);

		this.serverStub = null;
		try {
			this.serverStub = (P2PGame) registry.lookup(Constant.RMIID);
		} catch (NotBoundException e) {
			
			e.printStackTrace();
		}

	}






}

package Server;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

import Client.Client;

public class ServerImpl extends UnicastRemoteObject implements Server, Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3034452825691365904L;
	private GameInfo gameInfoObj;
	private int gridSize;
	private int timeToStart;
	private int timeToStartVal;
	private boolean populateTreasureMap;
	private ArrayList<Coordinate> coordinateList;
	private int row;
	private int column;
	private Random random;
	private Coordinate userCoordinate;
	
	public ServerImpl() throws RemoteException {
		// TODO Auto-generated constructor stub
		super();
		gameInfoObj = new GameInfo();
		// Set gridSize and treasureCount
		gameInfoObj.setGridSize(5);
		gameInfoObj.setTreasureCount(10);
		
		gridSize = gameInfoObj.getGridSize();
		timeToStart = 10; // Time to start the game - hard coded
		timeToStartVal = 10;
		
		populateTreasureMap = true;
		coordinateList = new ArrayList<Coordinate>();
		random = new Random();
		userCoordinate = new Coordinate(0,0);
	}
	
	public static void main(String[] args) throws RemoteException, AlreadyBoundException {
		// TODO Auto-generated method stub
		ServerImpl serverImplObj = new ServerImpl();
		Registry registry = LocateRegistry.createRegistry(Constant.RMIPORT);
		registry.bind(Constant.RMIID, serverImplObj);

		System.out.println("Server Started ...\n");
		
		
		@SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);
		System.out.println("Enter the gridSize..");
		String gridSize = in.nextLine();
		serverImplObj.gridSize=Integer.parseInt(gridSize);
		System.out.println("GridSize is "+gridSize);		
		
	}	

	public synchronized boolean addUser(String username, String password, Client clientObj)
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
					
					for(Entry<Coordinate, Integer> entry: gameInfoObj.getTreasureMap().entrySet()) {
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
				
				gameInfoObj.setPlayerPostionMap(username.toLowerCase(), userCoordinate);
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
		Map<String,Coordinate> playerPositionMap = gameInfoObj.getPlayerPostionMap();
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
			gameInfoObj.setPlayerPostionMap(username, coordinate);
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
		return gameInfoObj.getPlayerPostionMap();
	}

	@Override
	public synchronized Map<Coordinate, Integer> getTreasureMap() throws RemoteException {
		// TODO Auto-generated method stub
		return gameInfoObj.getTreasureMap();
	}

	@Override
	public synchronized Map<String, Integer> getPlayerScoreMap() throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("Size of Scoer Map is "+gameInfoObj.getPlayerScoreMap().size());
		return gameInfoObj.getPlayerScoreMap();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
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
//			Set<String> userSet = gameInfoObj.getPlayerObjectMap().keySet();
//			Iterator clientObjectIterator = userSet.iterator();
//			System.out.println("Size is "+userSet.size());
//			while (clientObjectIterator.hasNext()) {
//				String user = (String) clientObjectIterator.next();
//				
//				Client c = (Client) gameInfoObj.getPlayerObjectMap().get(user);
//				try {
//					c.notifyPlayer(true);
//				} 
//				catch (RemoteException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
			
//			for (Map.Entry<Coordinate, Integer> entry : gameInfoObj.getTreasureMap().entrySet()) {
//			    Coordinate key = entry.getKey();
//			    System.out.println(key.getRow() + "," + key.getColumn() + " " + key.hashCode());
//			}
			
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

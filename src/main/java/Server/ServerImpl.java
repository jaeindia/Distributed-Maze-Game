package Server;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import Client.Client;

public class ServerImpl extends UnicastRemoteObject implements Server, Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3034452825691365904L;
	private GameInfo gameInfoObj;
	private int gridSize;
	private int timeToStart;
	
	public ServerImpl() throws RemoteException {
		// TODO Auto-generated constructor stub
		super();
		gameInfoObj = new GameInfo();
		// Set gridSize and treasureCount
		gameInfoObj.setGridSize(5);
		gameInfoObj.setTreasureCount(10);
		
		gridSize = gameInfoObj.getGridSize();
		timeToStart = 20; // Time to start the game - hard coded
	}
	
	public static void main(String[] args) throws RemoteException, AlreadyBoundException {
		// TODO Auto-generated method stub
		ServerImpl serverImplObj = new ServerImpl();
		Registry registry = LocateRegistry.createRegistry(Constant.RMIPORT);
		registry.bind(Constant.RMIID, serverImplObj);

		System.out.println("Server Started ...\n");
	}	

	public synchronized boolean addUser(String username, String password, Client clientObj)
			throws RemoteException {
		// TODO Auto-generated method stub
		
		boolean flag = true;
		
		if (username != null && password != null) {
			flag = gameInfoObj.doesUserExist(username, password);
//			System.out.println("add user flag " + flag);
			if (!flag) {
				System.out.println("User is added ");
				gameInfoObj.setPlayerPostionMap(username, new Coordinate(0, 0));
				gameInfoObj.setPlayerObjectMap(username, clientObj);
				
				if(this.timeToStart == 20){
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
			if (gameInfoObj.updateTreasureMap(coordinate) == 1 
					|| gameInfoObj.updateTreasureMap(coordinate) == -1) {
				// Update user - treasure count
				gameInfoObj.updatePlayerScoreMap(username);
			}
		
			if (gameInfoObj.updateTreasureMap(coordinate) == -1) {
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
			// Populate Treasure Map - hard coded
			gameInfoObj.populateTreasureMap();
			
			
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

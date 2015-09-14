package Server;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

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

	public synchronized boolean addUser(String username, String password)
			throws RemoteException {
		// TODO Auto-generated method stub
		
		boolean flag = true;
		
		if(this.timeToStart == 20){
			Thread t = new Thread(this);
			t.start();
		}
		else if(this.timeToStart == 0){
			return false;
		}
		
		if (username != null && password != null) {
			flag = gameInfoObj.doesUserExist(username, password);
			if (!flag) {
				gameInfoObj.setPlayerPostionMap(username, new Coordinate(0, 0));
			}
		}
		
		System.out.println("add user");
		System.out.println(gameInfoObj.getPlayerPostionMap());
		
		return !flag;
	}

	@Override
	public synchronized boolean moveUser(String username, Coordinate coordinate)
			throws RemoteException {
		// TODO Auto-generated method stub
		Map<String,Coordinate> playerPositionMap = gameInfoObj.getPlayerPostionMap();
		
		// Check first if the user actually exists
		if (!gameInfoObj.doesUserExist(username, "TestGame")) {
			return false;
		}
		
		// Check for valid coordinates
		if (!(coordinate.getRow() >= 0 && coordinate.getRow() < gridSize
				&& coordinate.getColumn() >= 0 && coordinate.getColumn() < gridSize)) {
			return false;
		}
		
		// Search logic for the 1-1 position map
		for (Entry<String, Coordinate> entry : playerPositionMap.entrySet()) {
			if (Objects.equals(coordinate, entry.getValue())) {
	            return false;
	        }
	    }
		
		// Update position
		gameInfoObj.setPlayerPostionMap(username, coordinate);
		// Update treasure count map
		if (gameInfoObj.updateTreasureMap(coordinate)) {
			// Update user - treasure count
			gameInfoObj.updatePlayerScoreMap(username);
		}
		
		System.out.println("move user");
		System.out.println(gameInfoObj.getPlayerPostionMap());
		
		System.out.println("Treasure map");
		
		return true;
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
			// Set gridSize and treasureCount
			gameInfoObj.setGridSize(5);
			gameInfoObj.setTreasureCount(10);
			// Populate Treasure Map - hard coded
			gameInfoObj.populateTreasureMap();	
		}
	}

}

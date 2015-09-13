package Server;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

//import server.Server;

public class ServerImpl extends UnicastRemoteObject implements Server{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3034452825691365904L;
	private static GameInfo gameInfoObj;
	
	public ServerImpl() throws RemoteException {
		super();
		gameInfoObj = new GameInfo();
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String[] args) throws RemoteException, AlreadyBoundException {
		// TODO Auto-generated method stub
		ServerImpl serverImplObj = new ServerImpl();
		Registry registry = LocateRegistry.createRegistry(Constant.RMIPORT);
		registry.bind(Constant.RMIID, serverImplObj);
		
		// Populate Treasure Map - hard coded
		gameInfoObj.populateTreasureMap(5, 10);

		System.out.println("Server Started ...\n");
	}	

	public boolean addUser(String username, String password)
			throws RemoteException {
		// TODO Auto-generated method stub
		
		boolean flag = true;
		
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
	public boolean moveUser(String username, Coordinate coordinate)
			throws RemoteException {
		// TODO Auto-generated method stub
		Map<String,Coordinate> playerPositionMap = gameInfoObj.getPlayerPostionMap();
		
		// CHeck first if the user actually exists
		if (!gameInfoObj.doesUserExist(username, "TestGame")) {
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
	public GameInfo fetchGameInfo(String username) {
		// TODO Auto-generated method stub
		
		return gameInfoObj;
	}

}

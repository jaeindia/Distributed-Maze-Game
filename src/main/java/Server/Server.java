package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface Server extends Remote{
	
	/*
	 * 
	 *  Add all server related method declarations
	 */
	
	public boolean addUser(String username,String password) throws RemoteException;
	
	public boolean moveUser(String username, Coordinate coordinate) throws RemoteException;
	
	public int getGridSize() throws RemoteException;
	
	public Map<String, Coordinate> getPlayerPostionMap() throws RemoteException;
	
	public Map<Coordinate, Integer> getTreasureMap() throws RemoteException;
	
	public Map<String, Integer> getPlayerScoreMap() throws RemoteException;
	
//	private Map<Coordinate,Integer> treasureMap = new HashMap<Coordinate,Integer>();
//	
//	private Map<String,Integer> playerScoreMap = new ConcurrentHashMap<String, Integer>();

}

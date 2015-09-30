package Peer2Peer;
 import java.rmi.*;
import java.util.Map;


import Client.Client;
import Server.Coordinate;
 
 
/**
 * @author a0134505
 *
 */
public interface P2PGame extends Remote {
	
	public void notifyPlayer(boolean gameStarted) throws RemoteException;
	
	public boolean notifyGameEnd(boolean gameEnded) throws RemoteException;
	
	public boolean addUser(String username, String password, P2PGame clientObj) throws RemoteException;
	
	public boolean moveUser(String username, Coordinate coordinate) throws RemoteException;
	
	public int getGridSize() throws RemoteException;
	
	public Map<String, Coordinate> getPlayerPostionMap() throws RemoteException;
	
	public Map<Coordinate, Integer> getTreasureMap() throws RemoteException;
	
	public Map<String, Integer> getPlayerScoreMap() throws RemoteException;
	
	public void updateBackupServer() throws RemoteException;
	
	public boolean upgradeToBackupServer() throws RemoteException;
	
	public boolean upgradeToPrimaryServer() throws RemoteException;
	
	public P2PGameInfo getGameInfoObj() throws RemoteException;
	
	public boolean isAlive() throws RemoteException;

	public void notifynewRegistry() throws RemoteException;
	
//	public String getMessage() throws RemoteException;
	
	

}

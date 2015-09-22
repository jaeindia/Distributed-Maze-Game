package Server;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

import Client.Client;

public interface Server extends Remote{
	
	/*
	 * 
	 *  Add all server related method declarations
	 */
	
	public boolean addUser(String username, String password, Client clientObj) throws RemoteException;
	
	public boolean moveUser(String username, Coordinate coordinate) throws RemoteException, AlreadyBoundException, IOException;
	
	public int getGridSize() throws RemoteException;
	
	public Map<String, Coordinate> getPlayerPostionMap() throws RemoteException;
	
	public Map<Coordinate, Integer> getTreasureMap() throws RemoteException;
	
	public Map<String, Integer> getPlayerScoreMap() throws RemoteException;

}

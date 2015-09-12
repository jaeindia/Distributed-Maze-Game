package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote{
	
	/*
	 * 
	 *  Add all server related method declarations
	 */
	
	public boolean isLoginValid(String password) throws RemoteException;
	
	public boolean addUser(String username,String password) throws RemoteException;
	
	public boolean moveUser(String username, Coordinate coordinate) throws RemoteException;
	
	public GameInfo fetchGameInfo(String username);
	
	

}

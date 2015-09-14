package Client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Client extends Remote {
	
	/*
	 * 
	 * Add  all Client method declarations
	 * 
	 */
	
	public void notifyPlayer(boolean gameStarted) throws RemoteException ;
	
}

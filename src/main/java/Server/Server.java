package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote{
	
	/*
	 * 
	 *  Add all server related method declarations
	 */
	
	public boolean isLoginValid(String password) throws RemoteException;

}

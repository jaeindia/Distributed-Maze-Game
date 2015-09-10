package Server;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServerImpl extends UnicastRemoteObject implements Serializable,Server{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3034452825691365904L;

	protected ServerImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}

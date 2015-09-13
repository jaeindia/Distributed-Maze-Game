/**
 * 
 */
package Client;

import java.io.Serializable;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import Server.Coordinate;
import Server.Server;


/**
 * @author a0134505
 *
 */
public class ClientImpl extends UnicastRemoteObject implements  Serializable, Client{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7437795715343481173L;
	
	

	protected ClientImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws RemoteException 
	 * @throws NotBoundException 
	 */
	public static void main(String[] args) throws RemoteException, NotBoundException {
		// TODO Auto-generated method stub
		Registry registry = LocateRegistry.getRegistry("localhost", Constant.RMIPORT);
		Server serverObj = (Server) registry.lookup(Constant.RMIID);
		
		System.out.println(serverObj.addUser("1", "TestGame"));
		System.out.println(serverObj.addUser("2", "TestGame"));
		
		System.out.println(serverObj.moveUser("1", new Coordinate(0, 1)));
		System.out.println(serverObj.moveUser("2", new Coordinate(0, 1)));
		System.out.println(serverObj.moveUser("Test", new Coordinate(1, 1)));
		
		/*
		 * 
		 * Swing implementation
		 * 
		 * Button reading
		 * 
		 * Call to Server
		 * 
		 * Update Maze based on GameInfo
		 */
		
		
	}


}

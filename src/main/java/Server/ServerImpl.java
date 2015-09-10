package Server;

import java.io.Serializable;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
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
	
	public static void main(String[] args) throws RemoteException, AlreadyBoundException {
		// TODO Auto-generated method stub
		ServerImpl serverImplObj = new ServerImpl();
		Registry registry = LocateRegistry.createRegistry(Constant.RMIPORT);
		registry.bind(Constant.RMIID, serverImplObj);

		System.out.println("Server Started ...\n");
	}

	@Override
	public boolean isLoginValid(String password) throws RemoteException {
		// TODO Auto-generated method stub
		
		if (password.equals("TestGame")) {
			return true;
		}
		
		return false;
	}

}

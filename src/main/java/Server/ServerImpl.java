package Server;

import java.io.Serializable;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//import server.Server;

public class ServerImpl implements Server{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3034452825691365904L;
	
	private static final int RMIPORT = 1234;
	private static final String RMIID = "Server";
	
	private static Map<String,GameInfo> userInfoMap = new ConcurrentHashMap<String, GameInfo>();
	
	//private 
	//private Map<String,CoOrdinate> 
	
	public static void test(){
		
	
		
	}
	
	
	

	public ServerImpl() throws RemoteException {
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


		//System.out.println("Server Started ...\n");
	

	public boolean isLoginValid(String password) throws RemoteException {
		// TODO Auto-generated method stub
		if (password.equals("TestGame")) {
			return true;
		}
		
		return false;
	}




	public boolean addUser(String username, String password)
			throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}




	public boolean moveUser(String username, Coordinate coordinate)
			throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	/*@Override
	public boolean isLoginValid(String password) throws RemoteException {
		// TODO Auto-generated method stub
		
		if (password.equals("TestGame")) {
			return true;
		}
		
		return false;
	}*/

}

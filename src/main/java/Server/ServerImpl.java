package Server;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

//import server.Server;

public class ServerImpl extends UnicastRemoteObject implements Server{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3034452825691365904L;
	private GameInfo gameInfoObj = new GameInfo();
	
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

	public boolean addUser(String username, String password)
			throws RemoteException {
		// TODO Auto-generated method stub
		
		boolean flag = true;
		
		if (username != null && password != null) {
			flag = gameInfoObj.doesUserExist(username, password);
			if (!flag) {
				gameInfoObj.setPlayerPostionMap(username, new Coordinate(0, 0));
			}
		}
		
		System.out.println(gameInfoObj.getPlayerPostionMap());
		
		return !flag;
	}

	public boolean moveUser(String username, Coordinate coordinate)
			throws RemoteException {
		// TODO Auto-generated method stub
		
		
		return false;
	}

	@Override
	public GameInfo fetchGameInfo(String username) {
		// TODO Auto-generated method stub
		return gameInfoObj;
	}

}

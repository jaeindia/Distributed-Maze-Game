/**
 * 
 */
package Client;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

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
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}


}

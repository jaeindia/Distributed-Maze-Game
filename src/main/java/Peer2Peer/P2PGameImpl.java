package Peer2Peer;

import java.rmi.RemoteException;
import java.util.Map;

import Client.Client;

import Server.Coordinate;

public class P2PGameImpl implements P2PGame {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyPlayer(boolean gameStarted) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyGameEnd(boolean gameEnded) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean addUser(String username, String password, Client clientObj)
			throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean moveUser(String username, Coordinate coordinate)
			throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getGridSize() throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Map<String, Coordinate> getPlayerPostionMap() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Coordinate, Integer> getTreasureMap() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Integer> getPlayerScoreMap() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean updateBackupServer() throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updatePrimaryServer() throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

}

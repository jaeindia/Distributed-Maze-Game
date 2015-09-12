package Server;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3469905445419971617L;

	private Map<String,Coordinate> playerPostionMap = new ConcurrentHashMap<String,Coordinate>();
	
	private Map<Coordinate,Integer> treasuremap = new HashMap<Coordinate,Integer>();
	
	private Map<String,Integer> playerScoreMap = new ConcurrentHashMap<String, Integer>();

	Map<String,Coordinate> getPlayerPostionMap() {
		return playerPostionMap;
	}

	void setPlayerPostionMap(String username, Coordinate coordinate) {
		this.playerPostionMap.put(username.toLowerCase(), coordinate);
	}
	
	boolean doesUserExist(String username, String password) {
		// TODO Auto-generated method stub
		
		if (password.equals("TestGame") 
				&& !playerPostionMap.containsKey(username.toLowerCase()) ) {
			return false;
		}
		
		return true;
	}
	
	
}

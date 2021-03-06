package Server;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import Client.Client;

public class GameInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3469905445419971617L;

	private Map<String,Coordinate> playerPostionMap = new ConcurrentHashMap<String,Coordinate>();
	
	private Map<String, Client> playerObjectMap = new ConcurrentHashMap<String, Client>();
	
	private Map<Coordinate,Integer> treasureMap = new HashMap<Coordinate,Integer>();
	
	private Map<String,Integer> playerScoreMap = new ConcurrentHashMap<String, Integer>();
	
	private int gridSize;
	
	private int treasureCount;
	
	private int treasureCounter;
	
	private Random random = new Random();

	public Map<String,Coordinate> getPlayerPostionMap() {
		return playerPostionMap;
	}
	
	public Map<Coordinate, Integer> getTreasureMap() {
		return treasureMap;
	}
	
	public Map<String, Integer> getPlayerScoreMap() {
		
		System.out.println("Size in Server is "+playerScoreMap.size());
		
		return playerScoreMap;
	}

	public void setPlayerPostionMap(String username, Coordinate coordinate) {
		playerPostionMap.put(username.toLowerCase(), coordinate);
	}
	
	public boolean doesUserExist(String username, String password) {
		// TODO Auto-generated method stub
		
		boolean exists = true;
		if (password.equals("TestGame") 
				&& !playerPostionMap.containsKey(username.toLowerCase()) ) {
			exists = false;
		}
		
		return exists;
	
	}
	
	public void populateTreasureMap() {				
		int row = 0;
		int column = 0;

		for (int i = this.treasureCount; i > 0; i--) {
			do{
				row = random.nextInt(this.gridSize);
				column = random.nextInt(this.gridSize);
			} while(row==0 && column==0);
			
			Coordinate coordinate = new Coordinate(row, column);
			
			if (treasureMap.containsKey(coordinate)) {
				treasureMap.put(coordinate, treasureMap.get(coordinate) + 1);
			}
			else {
				treasureMap.put(coordinate, 1);
			}
			
			System.out.format("row - %d, col - %d, treasureCount - %d\n", row, column, treasureCount);
		}

	}
	
	public int updateTreasureMap(String username, Coordinate coordinate) {
//		System.out.println("OUT");
//		System.out.println(coordinate.getRow()+", "+coordinate.getColumn());
//		System.out.println("hashcode" + coordinate.hashCode());
//		System.out.println("flag " + this.treasureMap.containsKey(coordinate));
//		for (Entry<Coordinate, Integer> entry : this.treasureMap.entrySet()) {		
//			if (Objects.equals(coordinate, entry.getValue())) {
//				System.out.println("match found");
//	        }
//	    }
		this.treasureMap.containsKey(coordinate);
		if (this.treasureMap.containsKey(coordinate) && this.treasureMap.get(coordinate) != 0) {
//			System.out.println("IN");
//			System.out.println("Treasure Counter" + this.treasureCounter);
			this.updatePlayerScoreMap(username, this.treasureMap.get(coordinate));
			this.treasureCounter -= this.treasureMap.get(coordinate);
			this.treasureMap.remove(coordinate);
			
			if (treasureCounter == 0) {
				return -1;
			}
			
//			System.out.println("Treasure Map Update");
//			System.out.println(this.treasureMap);
			
			// Return true if the treasure is found 
			return 1;
		}
		
		// Return false if the treasure is not found
		return 0;
	}
	
	boolean updatePlayerScoreMap(String username, int score) {
//		System.out.println("Inside update player score map" + username + " " + score);
		if (!this.playerScoreMap.containsKey(username.toLowerCase())) {
//			System.out.println("1st time");
			this.playerScoreMap.put(username.toLowerCase(), score);
		} 
		else {
			this.playerScoreMap.put(username.toLowerCase(), 
			this.playerScoreMap.get(username.toLowerCase()) + score);
		}
		
		System.out.println("Player Score Map");
		System.out.println(playerScoreMap);
		
		return true;
	}

	public int getGridSize() {
		return gridSize;
	}

	public void setGridSize(int gridSize) {
		this.gridSize = gridSize;
	}

	int getTreasureCount() {
		return treasureCount;
	}

	public void setTreasureCount(int treasureCount) {
		this.treasureCount = treasureCount;
		this.treasureCounter = treasureCount;
	}

	public Map<String, Client> getPlayerObjectMap() {
		return playerObjectMap;
	}

	public void setPlayerObjectMap(String username, Client clientObj) {
//		System.out.println(username + ","+clientObj);
		playerObjectMap.put(username.toLowerCase(), clientObj);
	}
}

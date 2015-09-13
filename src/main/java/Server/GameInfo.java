package Server;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class GameInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3469905445419971617L;

	private Map<String,Coordinate> playerPostionMap = new ConcurrentHashMap<String,Coordinate>();
	
	private Map<Coordinate,Integer> treasureMap = new HashMap<Coordinate,Integer>();
	
	private Map<String,Integer> playerScoreMap = new ConcurrentHashMap<String, Integer>();
	
	private int gridSize;
	
	private int treasureCount;
	
	private Random random = new Random();

	Map<String,Coordinate> getPlayerPostionMap() {
		return playerPostionMap;
	}
	
	Map<Coordinate, Integer> getTreasureMap() {
		return treasureMap;
	}
	
	Map<String, Integer> getPlayerScoreMap() {
		return playerScoreMap;
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
	
	void populateTreasureMap() {				
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
	
	boolean updateTreasureMap(Coordinate coordinate) {
		if (this.treasureMap.containsKey(coordinate) && this.treasureMap.get(coordinate) != 0) {
			this.treasureMap.put(coordinate, this.treasureMap.get(coordinate) - 1);
			
			System.out.println("Treasure Map");
			System.out.println(treasureMap);
			
			// Return true if the treasure is found 
			return true;
		}
		
		// Return false if the treasure is not found
		return false;
	}
	
	boolean updatePlayerScoreMap(String username) {
		if (this.playerScoreMap.containsKey(username)) {
			this.playerScoreMap.put(username, this.playerScoreMap.get(username) + 1);
			System.out.println("Player Score Map");
			System.out.println(playerScoreMap);
		} 
		else { 
			this.playerScoreMap.put(username, 1);
		}
		
		return true;
	}

	int getGridSize() {
		return gridSize;
	}

	void setGridSize(int gridSize) {
		this.gridSize = gridSize;
	}

	int getTreasureCount() {
		return treasureCount;
	}

	void setTreasureCount(int treasureCount) {
		this.treasureCount = treasureCount;
	}
}

package Peer2Peer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import java.util.Random;

public class P2PGameInfo implements Serializable {

	
		private static final long serialVersionUID = -7586744666506268851L;
		
		private int gridSize;
		private int treasureCount;
		private int treasureCounter;
//		private String primaryServerId;
//		private String backupServerId;
		
		public int getGridSize() {
			return gridSize;
		}

		public void setGridSize(int gridSize) {
			this.gridSize = gridSize;
		}

		public int getTreasureCount() {
			return treasureCount;
		}

		public void setTreasureCount(int treasureCount) {
			this.treasureCount = treasureCount;
		}

		public int getTreasureCounter() {
			return treasureCounter;
		}

		public void setTreasureCounter(int treasureCounter) {
			this.treasureCounter = treasureCounter;
		}

		private Random random = new Random();
	
	
		private Map<String,Coordinate> P2PplayerPostionMap = new ConcurrentHashMap<String,Coordinate>();
		
		
	  	private Map<Coordinate,Integer> P2PtreasureMap = new HashMap<Coordinate,Integer>();
	  	
	  	private Map<String,Integer> P2PplayerScoreMap = new ConcurrentHashMap<String, Integer>();
	  	
	  	private Map<String, P2PGame> playerObjectMap = new ConcurrentHashMap<String, P2PGame>();
	  	
	  	private List<String> playerList = new LinkedList<String>();

		public List<String> getPlayerList() {
			return playerList;
		}

		public void addPlayerToList(String user) {
			playerList.add(user);
		}
		
		public void setPlayerList(List<String> playerList) {
			this.playerList = playerList; 
		}

		public Map<String, P2PGame> getPlayerObjectMap() {
			return playerObjectMap;
		}

		public void setPlayerObjectMap(Map<String, P2PGame> playerObjectMap) {
			this.playerObjectMap = playerObjectMap;
		}
		
		public void setPlayerObjectMap(String username, P2PGame clientObj) {
//			System.out.println(username + ","+clientObj);
			this.playerObjectMap.put(username.toLowerCase(), clientObj);
		}

		public Map<String, Coordinate> getP2PplayerPostionMap() {
			return P2PplayerPostionMap;
		}

		public void setP2PplayerPostionMap(String username, Coordinate coordinate) {
			P2PplayerPostionMap.put(username.toLowerCase(), coordinate);
		}
		
		public void setP2PplayerPostionMap(Map<String,Coordinate> P2PplayerPostionMap ) {
			this.P2PplayerPostionMap = P2PplayerPostionMap;
		}

		public Map<Coordinate, Integer> getP2PtreasureMap() {
			return P2PtreasureMap;
		}

		public void setP2PtreasureMap(Map<Coordinate, Integer> p2PtreasureMap) {
			this.P2PtreasureMap = p2PtreasureMap;
		}

		public Map<String, Integer> getP2PplayerScoreMap() {
			return P2PplayerScoreMap;
		}

		public void setP2PplayerScoreMap(Map<String, Integer> p2PplayerScoreMap) {
			P2PplayerScoreMap = p2PplayerScoreMap;
		}
	
		public boolean doesUserExist(String username, String password) {
			// TODO Auto-generated method stub
			
			boolean exists = true;
			if (password.equals("TestGame") 
					&& !P2PplayerPostionMap.containsKey(username.toLowerCase()) ) {
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
				
				if (P2PtreasureMap.containsKey(coordinate)) {
					P2PtreasureMap.put(coordinate, P2PtreasureMap.get(coordinate) + 1);
				}
				else {
					P2PtreasureMap.put(coordinate, 1);
				}
				
				System.out.format("row - %d, col - %d, treasureCount - %d\n", row, column, treasureCount);
			}

		}
		
		boolean addtoP2PPlayerScoreMap(String username) {
			this.P2PplayerScoreMap.put(username.toLowerCase(), 0);
			
			return true;
		}
		
		boolean updatePlayerScoreMap(String username, int score) {
//			System.out.println("Inside update player score map" + username + " " + score);
			if (!this.P2PplayerScoreMap.containsKey(username.toLowerCase())) {
//				System.out.println("1st time");
				this.P2PplayerScoreMap.put(username.toLowerCase(), score);
			} 
			else {
				this.P2PplayerScoreMap.put(username.toLowerCase(), 
				this.P2PplayerScoreMap.get(username.toLowerCase()) + score);
			}
			
//			System.out.println("Player Score Map");
//			System.out.println(P2PplayerScoreMap);
			
			return true;
		}

		
		public int updateTreasureMap(String username, Coordinate coordinate) {
//			System.out.println("OUT");
//			System.out.println(coordinate.getRow()+", "+coordinate.getColumn());
//			System.out.println("hashcode" + coordinate.hashCode());
//			System.out.println("flag " + this.treasureMap.containsKey(coordinate));
//			for (Entry<Coordinate, Integer> entry : this.treasureMap.entrySet()) {		
//				if (Objects.equals(coordinate, entry.getValue())) {
//					System.out.println("match found");
//		        }
//		    }
			this.P2PtreasureMap.containsKey(coordinate);
			if (this.P2PtreasureMap.containsKey(coordinate) && this.P2PtreasureMap.get(coordinate) != 0) {
//				System.out.println("IN");
//				System.out.println("Treasure Counter" + this.treasureCounter);
				this.updatePlayerScoreMap(username, this.P2PtreasureMap.get(coordinate));
				this.treasureCounter -= this.P2PtreasureMap.get(coordinate);
				this.P2PtreasureMap.remove(coordinate);
				
				if (treasureCounter == 0) {
					return -1;
				}
				
//				System.out.println("Treasure Map Update");
//				System.out.println(this.treasureMap);
				
				// Return true if the treasure is found 
				return 1;
			}
			
			// Return false if the treasure is not found
			return 0;
		}

//		public String getPrimaryServerId() {
//			return primaryServerId;
//		}
//
//		public void setPrimaryServerId(String primaryServerId) {
//			this.primaryServerId = primaryServerId;
//		}
//
//		public String getBackupServerId() {
//			return backupServerId;
//		}
//
//		public void setBackupServerId(String backupServerId) {
//			this.backupServerId = backupServerId;
//		}
}

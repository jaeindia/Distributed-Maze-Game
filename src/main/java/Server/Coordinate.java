package Server;

import java.io.Serializable;

public class Coordinate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3205049466478759683L;

	private int row;
	private int column;
	
	
	public Coordinate(int row, int column) {
		super();
		this.row = row;
		this.column = column;
	}
	
	
	public boolean equals(Object o){
		
	  Coordinate obj = (Coordinate) o;
		boolean equal = false;
		if(obj.row==this.row && obj.column==this.column){
			equal = true;
		}
		return equal;
		
	}

}

package Peer2Peer;
/*  *****************************************************
 * Distributed Systems Assignment - 
 * @author
 * Abhinav Sarja - A0134505N
 * Jayakumar - A0134431U
 * 
 * This class is the final version of the assignment providing the required functionality as mentioned in the Assignment document 1.
 * 
 *  *****************************************************
 */


import java.io.Serializable;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Coordinate implements Serializable {

	@Override
	public String toString() {
		return "Coordinate [row=" + row + ", column=" + column + "]";
	}



	/**
	 * 
	 */
	private static final long serialVersionUID = 3205049466478759683L;

	public  int row;
	public  int column;
	
	
	public Coordinate(int row, int column) {
		super();
		this.row = row;
		this.column = column;
	}
	
	
	
	public int getRow() {
		return row;
	}



	public void setRow(int row) {
		this.row = row;
	}



	public int getColumn() {
		return column;
	}



	public void setColumn(int column) {
		this.column = column;
	}


	@Override
    public int hashCode() {
        return new HashCodeBuilder(11, 37). // 2 randomly chosen prime numbers
            // if deriving: appendSuper(super.hashCode()).
            append(this.getRow()).
            append(this.getColumn()).
            toHashCode();
    }	

	@Override
	public boolean equals(Object o){	
	  Coordinate obj = (Coordinate) o;
		boolean equal = false;
//		System.out.println(obj.row +"-"+ this.row+","+obj.column+"-"+this.column);
//		System.out.println(obj.getRow() +"-"+ this.getRow()+","+obj.getColumn()+"-"+this.getColumn());
		if(obj.getRow()==this.getRow() && obj.getColumn()==this.getColumn()){
			equal = true;
		}
		return equal;
		
	}

}

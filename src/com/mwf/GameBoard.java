/**
 * 
 */
package com.mwf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * @author Danny
 *
 */
public class GameBoard {

	private final int height;
	private final int width;
	private int activeCount;
	private Cell board[][];
	private HashMap<String,Cell> toRemoveList = null;
	private static final Random generator = new Random();
	
	public GameBoard (int w, int h) throws Exception {
		if (0 == h || 0 == w) {
			// someone needs to be shot! 
			throw new Exception ("can't create an empy map");
		}
		height = h;
		width = w;
		activeCount = height * width;
		board = new Cell[width][height];
		toRemoveList = new HashMap<String,Cell>();
		for (int x = 0; x < width; x++){
			for (int y = 0; y < height; y++) {
				Cell c = new Cell(x,y, calcAdjacent (x,y,width,height));
				toRemoveList.put(c.getKey(), c);
				board[x][y] = c;
			}
		}
	}
	private int calcAdjacent (int x, int y, int width, int height) {
		int retval = 0;
		// if we have a cell to left, we increment
		if (x > 0) {
			retval++;
		}
		// if we have a cell to right, we increment
		if (x < width -1) {
			retval++;
		}
		// if we have a cell above us, we increment
		if (y > 0) {
			retval++;
		}
		// if we have a cell below, we increment
		if (y < height - 1) {
			retval++;
		}
		return retval;
	}
	public void randomizeEmptyCells () {
		// bail out if we only have a 1x1 map
		if (1 == height && 1 == width) {
			return;
		}
		int emptyInit = (height+width)/2;
		// don't remove any cells if we are down to our last active cell
		for (int i =0; activeCount > 1 && toRemoveList.size() > 0 && i< emptyInit; i++) {
			int rand = generator.nextInt();
			int targ = 0;
			targ = Math.abs(rand%toRemoveList.size());
			Cell ctarg = toRemoveList.get(toRemoveList.keySet().toArray()[targ]);
			ctarg.setCurrentState(Cell.State.DANGER);
			activeCount--;
			// update adjacent cells
			updateAdjacentCells(ctarg);
			// remove the current cell from the remove list
			toRemoveList.remove(ctarg.getKey());
			// now update all adjacent including diagonals
			updateRemoveList (ctarg);
		}
	}
	public void print () {
		for (int x = 0; x < width; x++){
			for (int y = 0; y < height; y++) {
				board[x][y].print();
			}
		}
	}
	
	private Cell getAboveCell (Cell ctarg) {
		Cell aboveCell = null;
		if (null != ctarg && ctarg.getYcord() > 0) {
			aboveCell = board[ctarg.getXcord()][ctarg.getYcord()-1];		
		}
		return aboveCell;
	}
	
	private Cell getBelowCell (Cell ctarg) {
		Cell belowCell = null;
		if (null != ctarg && ctarg.getYcord() < height-1) {
			belowCell = board[ctarg.getXcord()][ctarg.getYcord()+1];
		}
		return belowCell;
	}
	private Cell getLeftCell (Cell ctarg) {
		Cell leftCell = null;
		if (null != ctarg && ctarg.getXcord()>0) {
			leftCell = board[ctarg.getXcord()-1][ctarg.getYcord()];
		}
		return leftCell;
	}
	
	private Cell getRightCell (Cell ctarg) {
		Cell rightCell = null;
		if (null != ctarg && ctarg.getXcord() < width-1) {
			rightCell = board[ctarg.getXcord()+1][ctarg.getYcord()];
		}
		return rightCell;
	}
	// update the adjacent counter for all adjacent cells next to the ctarg
	// after we update the adjacent counter we will have to also pop stuff off
	private void updateAdjacentCells(Cell ctarg) {
		
		Cell aboveCell = getAboveCell (ctarg);
		Cell belowCell = getBelowCell (ctarg);
		Cell leftCell = getLeftCell (ctarg);
		Cell rightCell = getRightCell (ctarg);
		
		// if a cell to the left exists, decrement it's adjacent counter
		if (leftCell != null) {
			leftCell.setAdjacentCount(leftCell.getAdjacentCount()-1);
		}
		// if a cell to the right exists, decrement it's adjacent counter
		if (rightCell != null) {
			rightCell.setAdjacentCount(rightCell.getAdjacentCount()-1);
		}
		// if a cell above exists, decrement it's adjacent counter
		if (aboveCell != null) {
			aboveCell.setAdjacentCount(aboveCell.getAdjacentCount()-1);		
		}
		// if a cell below exists, decrement it's adjacent counter
		if (belowCell != null) {
			belowCell.setAdjacentCount(belowCell.getAdjacentCount()-1);
		}
	}
	
	// you call this method right after removing a cell from the board
	// this implies you need to see if any of the adjacent cells need to be
	// added or removed from the toremove list.
	private void updateRemoveList (Cell ctarg) {
		ArrayList<Cell> updateList = getActiveAdjacentCells(ctarg, true, false);
		// now we have to account for all adjacent cells including diagonals
		
		for (int i =0; i<updateList.size(); i++) {
			// not sure if we actually can add null values in an array list
			// just in case we do, I've added this check.
			// TODO: Can I remove this check?
			if (null == updateList.get(i)) {
				continue;
			}
			if (canRemove(updateList.get(i))) {
				toRemoveList.put(updateList.get(i).getKey(), updateList.get(i));
			}
			else {
				toRemoveList.remove(updateList.get(i).getKey());
			}	
		}
	}
	
	// only return the up down left and right active cells unless 
	// includeDiagonals flag is set
	private ArrayList<Cell> getActiveAdjacentCells (Cell ctarg, 
													boolean includeDiagonals, 
													boolean hideVisited) {
		ArrayList<Cell> retval = new ArrayList<Cell>();
		Cell aboveCell = getAboveCell (ctarg);
		Cell belowCell = getBelowCell (ctarg);
		Cell leftCell = getLeftCell (ctarg);
		Cell rightCell = getRightCell (ctarg);
		if (null != aboveCell && aboveCell.getCurrentState() == Cell.State.ACTIVE) {
			if (!(hideVisited && aboveCell.isVisited())) {
				retval.add(aboveCell);
			}
		}
		if (null != belowCell && belowCell.getCurrentState() == Cell.State.ACTIVE) {
			if (!(hideVisited && belowCell.isVisited())) {
				retval.add(belowCell);
			}
		}
		if (null != leftCell && leftCell.getCurrentState() == Cell.State.ACTIVE) {
			if (!(hideVisited && leftCell.isVisited())) {
				retval.add(leftCell);
			}
		}
		if (null != rightCell && rightCell.getCurrentState() == Cell.State.ACTIVE) {
			if (!(hideVisited && rightCell.isVisited())) {
				retval.add(rightCell);
			}
		}
		if (includeDiagonals) {
			Cell aboveRight = getRightCell(aboveCell);
			Cell aboveLeft = getLeftCell (aboveCell);
			Cell belowRight = getRightCell (belowCell);
			Cell belowLeft = getLeftCell (belowCell);
			if (null != aboveRight && aboveRight.getCurrentState() == Cell.State.ACTIVE) {
				if (!(hideVisited && aboveRight.isVisited())) {
					retval.add(aboveRight);
				}
			}
			if (null != aboveLeft && aboveLeft.getCurrentState() == Cell.State.ACTIVE) {
				if (!(hideVisited && aboveLeft.isVisited())) {
					retval.add(aboveLeft);
				}
			}
			if (null != belowRight && belowRight.getCurrentState() == Cell.State.ACTIVE) {
				if (!(hideVisited && belowRight.isVisited())) {
					retval.add(belowRight);
				}
			}
			if (null != belowLeft && belowLeft.getCurrentState() == Cell.State.ACTIVE) {
				if (!(hideVisited && belowLeft.isVisited())) {
					retval.add(belowLeft);
				}
			}
		}
		return retval;
	}
	private boolean canRemove (Cell ctarg) {
		// if we are attempting to remove a null, simply don't remove it
		if (null == ctarg) {
			return false;
		}
		// if we have a leaf, we know we can quickly remove it
		if (ctarg.getAdjacentCount() == 1) {
			return true;
		}
		if (ctarg.getCurrentState() == Cell.State.DANGER) {
			return false;
		}
		ArrayList<Cell> activeList = getActiveAdjacentCells(ctarg,false, false);
		
		// now we have our active list of cells.  Make sure each cell can get
		// to the other cell without going through the cell we are seeing if we
		// can remove (ctarg)
		boolean retval = true;
		for (int i = 0; i < activeList.size() -1; i++) {
			clearVisitFlag();
			retval &= canRemove (ctarg, activeList.get(0), activeList.get(i+1));
		}
		return retval;
	}
	// go go fancy pants recursive search!
	private boolean canRemove (Cell removeCell, Cell current, Cell finish) {
		ArrayList<Cell> activeList = getActiveAdjacentCells(current,false,true);
		current.setVisited(true);
		boolean retval = false;
		for (int i = 0; i < activeList.size(); i++) {
			if (activeList.get(i) == removeCell) {
				continue; // if we are referring back to the cell we plan
						  // to remove, simply ignore and move on
			}
			if (activeList.get(i) == finish) {
				return true; // we actually got to the cell we wanted!
			}
			// if we find one path from current to finish, then we can remove!
			retval |= canRemove (removeCell, activeList.get(i), finish);
		}
		return retval;
	}
	
	private void clearVisitFlag() {
		for (int x = 0; x < width; x++){
			for (int y = 0; y < height; y++) {
				board[x][y].setVisited(false);
			}
		}
	}
}

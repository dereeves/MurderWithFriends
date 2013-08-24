/**
 * 
 */
package com.mwf;

import java.util.HashMap;

/**
 * @author Danny
 * Simple structure class to be used and abused by the GameBoard class         
 */
public class Cell {

	public Cell (int x, int y, int aCnt) {
		this.xcord = x;
		this.ycord =y;
		this.adjacentCount = aCnt;
		this.key = x + "." + y;
		this.visited = false;
		currentState = State.ACTIVE;
		playerList = new HashMap<String,Player>();
	}
	public enum State {
		ACTIVE,
		HOT,
		DANGER,
		ARMORY,
		HOSPITAL
	}
	// Coordinates for the given cell
	private final int xcord;
	private final int ycord;
	private String key;
	private int adjacentCount;
	private State currentState;
	public boolean isVisited() {
		return visited;
	}
	public void setVisited(boolean visited) {
		this.visited = visited;
	}
	private boolean visited;
	public State getCurrentState() {
		return currentState;
	}
	public void setCurrentState(State currentState) {
		this.currentState = currentState;
	}
	private HashMap<String,Player> playerList = null;

	public int getXcord() {
		return xcord;
	}
	public int getYcord() {
		return ycord;
	}
	public void addPlayer (Player p) {
		playerList.put(p.getId(), p);
	}
	public void removePlayer (Player p) {
		playerList.remove(p.getId());
	}
	public int getPlayerCount() {
		return playerList.size();
	}
	public int getAdjacentCount() {
		return adjacentCount;
	}
	public void setAdjacentCount(int adjacentCount) {
		this.adjacentCount = adjacentCount;
	}
	public String getKey () {
		return key;
	}
	public void print () {
		System.out.println("cell[" + xcord + "][" + ycord + "], Adjacent=" + adjacentCount + " State="+currentState.name());
	}
}

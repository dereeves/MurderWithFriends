package com.mwf;

public class Driver {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			GameBoard gb = new GameBoard (3, 3);
			gb.randomizeEmptyCells();
			gb.print();
			System.out.println ("=================");
			gb.randomizeEmptyCells();
			gb.print();
			System.out.println ("=================");
			gb.randomizeEmptyCells();
			gb.print();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

package edu.wm.cs.cs301.KeisterDurmaz.falstad;

import java.util.ArrayList;

/**
 * This class has the responsibility to create a maze of given dimensions (width, height) together with a solution based on a distance matrix.
 * The Maze class depends on it. The MazeBuilder performs its calculations within its own separate thread such that communication between 
 * Maze and MazeBuilder operates as follows. Maze calls the build() method and provides width and height. Maze has a call back method newMaze that
 * this class calls to communicate a new maze and a BSP root node and a solution.
 * 
 * The maze is built with a randomized version of Prim's algorithm. 
 * This means a spanning tree is expanded into a set of cells by removing walls from the maze.
 * 
 * This code is refactored code from Maze.java by Paul Falstad, www.falstad.com, Copyright (C) 1998, all rights reserved
 * Paul Falstad granted permission to modify and use code for teaching purposes.
 * Refactored by Peter Kemper   
 * @author Jones.Andrew
 */

public class MazeBuilderPrim extends MazeBuilder implements Runnable {
		
	public MazeBuilderPrim(MazePanel panel) {
		super(panel);
		System.out.println("MazeBuilderPrim uses Prim's algorithm to generate maze.");
	}
	public MazeBuilderPrim(boolean det, MazePanel panel) {
		super(det, panel);
		System.out.println("MazeBuilderPrim uses Prim's algorithm to generate maze.");
	}

	/**
	 * This method generates pathways into the maze by using Prim's algorithm to generate a spanning tree for an undirected graph.
	 * The cells are the nodes of the graph and the spanning tree. An edge represents that one can move from one cell to an adjacent cell.
	 * So an edge implies that its nodes are adjacent cells in the maze and that there is no wall separating these cells in the maze. 
	 */
	@Override
	protected void generatePathways() {
		// pick initial position (x,y) at some random position on the maze
		int x = random.nextIntWithinInterval(0, width-1);
		int y = random.nextIntWithinInterval(0, height - 1);
		// create an initial list of all walls that could be removed
		// those walls lead to adjacent cells that are not part of the spanning tree yet.
		final ArrayList<Wall> candidates = new ArrayList<Wall>();
		updateListOfWalls(x, y, candidates);
		
		Wall curWall;
		// we need to consider each candidate wall and consider it only once
		while(!candidates.isEmpty()){
			// in order to have a randomized algorithm,
			// we randomly select and extract a wall from our candidate set
			// this also reduces the set to make sure we terminate the loop
			curWall = extractWallFromCandidateSetRandomly(candidates);
			// check if wall leads to a new cell that is not connected to the spanning tree yet
			if (cells.canGo(curWall.x, curWall.y, curWall.dx, curWall.dy))
			{
				// delete wall from maze, note that this takes place from both directions
				cells.deleteWall(curWall.x, curWall.y, curWall.dx, curWall.dy);
				// update current position
				x = curWall.x + curWall.dx;
				y = curWall.y + curWall.dy;
				
				cells.setCellAsVisited(x, y); // the flag is never reset, so this ensure we never go to (x,y) again
				updateListOfWalls(x, y, candidates); // checks to see if it has walls to new cells, if it does it adds them to the list
				// note that each wall can get added at most once. This is important for termination and efficiency
			}
		}
	}
	/**
	 * Pick a random position in the list of candidates, remove the candidate from the list and return it
	 * @param candidates
	 * @return candidate from the list, randomly chosen
	 */
	private Wall extractWallFromCandidateSetRandomly(final ArrayList<Wall> candidates) {
		return candidates.remove(random.nextIntWithinInterval(0, candidates.size()-1)); 
	}
	

	/**
	 * Updates a list of all walls that could be removed from the maze based on walls towards new cells
	 * @param x
	 * @param y
	 */
	private void updateListOfWalls(int x, int y, ArrayList<Wall> walls) {
		if (cells.canGo(x, y, 0, 1))
		{
			walls.add(new Wall(x, y, 0, 1));
		}
		if (cells.canGo(x, y, 0, -1))
		{
			walls.add(new Wall(x, y, 0, -1));
		}
		if (cells.canGo(x, y, 1, 0))
		{
			walls.add(new Wall(x, y, 1, 0));
		}
		if (cells.canGo(x, y, -1, 0))
		{
			walls.add(new Wall(x, y, -1, 0));
		}
	}

}

package edu.wm.cs.cs301.KeisterDurmaz.falstad;

import java.util.*;

public class MazeBuilderAldousBroder extends MazeBuilder{ 
	
	int[][] visited; // visited cells
	int numVisited;
	
	public MazeBuilderAldousBroder(MazePanel panel) {
		super(panel);
		System.out.println("MazeBuilderAldousBroder uses the Aldous-Broder algorithm to generate maze.");
	}
	public MazeBuilderAldousBroder(boolean det, MazePanel panel) {
		super(det, panel);
		System.out.println("MazeBuilderAldousBroder uses the Aldous-Broder algorithm to generate maze.");
	}

	
	/**
	 * This method generates pathways into the maze by using the Aldous-Broder algorithm to ...TODO P2 description
	 */
	@Override
	protected void generatePathways() {	
		int mazeSize = width * height;
		visited = new int[width][height]; // visited cells

		// pick initial position (x,y) at some random position on the maze
		int x = random.nextIntWithinInterval(0, width-1);
		int y = random.nextIntWithinInterval(0, height-1);
		//System.out.println("x: " + x + " y:" + y);
		cells.setCellAsVisited(x, y); 
		addToVisited(x, y);
		int[] dirs = randomDirection();
		int dx = dirs[0];
		int dy = dirs[1];
	
		
		while (numVisited < (width * height)){
			// find the next x, y coords
			int nx = x + dx;
			int ny = y + dy;
			//System.out.print("x: " + x + " y:" + y + " nx:" + nx + " ny:" + ny);
			
			// check if the next cell is within the scope of the maze
			if (nx >= 0 && ny >= 0 && nx < width && ny < height) {
				if (!inVisited(nx, ny)){  
					//Sytem.out.println(" OK");
					cells.deleteWall(x, y, dx, dy);
					cells.setCellAsVisited(nx, ny); 
					addToVisited(nx, ny);
				}
				//else {
				//	System.out.println(" visited");
				//}
				x = nx;
				y = ny;
			}
			//else {
			//	System.out.println(" OOB");
			//}
			dirs = randomDirection();
			dx = dirs[0];
			dy = dirs[1];
		}
	}
	
	/**
	 * Determines a random direction in which to turn
	 * @return int[] 2 values dx, dy that represent direction
	 */
	protected int[] randomDirection() {
	
		int[] dirs = new int[2];
		int direction = random.nextIntWithinInterval(1, 4);
		switch (direction) {
		case 1: 
			dirs[0] = 1;
			dirs[1] = 0;
			break;
		case 2: 
			dirs[0] = 0;
			dirs[1] = 1;
			break;
		case 3: 
			dirs[0] = -1;
			dirs[1] = 0;
			break;
		case 4: 
			dirs[0] = 0;
			dirs[1] = -1;
			break;
		//default:
			//System.out.println("Something went wrong in randomDirection()");
		}
		return dirs;
	}
	
	/**
	 * Returns a boolean indicating whether a given coordinate for 
	 * a cell in the maze has already been visited by the generate pathways method
	 * @param row
	 * @param col
	 * @return boolean
	 */
	protected boolean inVisited(int x, int y) {
		boolean result = false;
		
		if (visited[x][y] == 1){
			result = true;
		}
		
		return result;
	}
	
	/**
	 * Adds the 2 coordinates of the visited cell to the visited list 
	 * and increments the visited counter by one so that there is an index for every cell in the maze
	 * @param x coord of cell
	 * @param y coord of cell
	 */
	protected void addToVisited(int x, int y) {
		visited[x][y] = 1;
		numVisited++;
	}
}


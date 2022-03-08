package edu.wm.cs.cs301.KeisterDurmaz.falstad;

import android.graphics.Canvas;

/**
 * This class encapsulates all functionality to draw a map of the overall maze, the set of visible walls, the solution.
 * The map is drawn on the screen in such a way that the current position remains at the center of the screen.
 * The current position is visualized as a red dot with an attached arc for its current direction.
 * The solution is visualized as a yellow line from the current position towards the exit of the map.
 * Walls set are currently visible in the first person view are drawn white, all other walls are drawn in grey.
 * 
 * This code is refactored code from Maze.java by Paul Falstad, www.falstad.com, Copyright (C) 1998, all rights reserved
 * Paul Falstad granted permission to modify and use code for teaching purposes.
 * Refactored by Peter Kemper
 *
 */
public class MapDrawer extends DefaultViewer {

	// keep local copies of values determined in Maze.java
	int view_width = 400;
	int view_height = 400;
	int map_unit = 128;
	int map_scale = 10 ;
	int step_size = map_unit/4;
	Cells mazecells ;
	Cells seencells ; 
	int[][] mazedists ;
	// width and height of map are chosen according to a user given skill level
	int mazew ; // width 
	int mazeh ; // height
	
	MazePanel panel;

	Maze maze ;
	/**
	 * Constructor
	 * @param panel
	 * @param mazecells TODO
	 * @param seencells TODO
	 * @param map_scale TODO
	 * @param mazedists TODO
	 * @param mazew TODO
	 * @param mazeh TODO
	 */
	public MapDrawer(MazePanel panel, int width, int height, int map_unit, int step_size, Cells mazecells, Cells seencells, int map_scale, int[][] mazedists, int mazew, int mazeh, Maze maze){
		view_width = width ;
		view_height = height ;
		this.map_unit = map_unit ;
		this.step_size = step_size ;
		this.mazecells = mazecells ;
		this.seencells = seencells ;
		this.map_scale = map_scale ;
		this.mazedists = mazedists ;
		this.mazew = mazew ;
		this.mazeh = mazeh ;
		this.maze = maze ;
		this.panel = panel ;
		
	}
	@Override
	public void incrementMapScale() {
		if (maze.isInMapMode())
			map_scale += 1 ;
	}
	@Override
	public void decrementMapScale() {
		if (maze.isInMapMode())
		{
			map_scale -= 1 ;
			if (1 > map_scale)
				map_scale = 1 ;
		}
	}
	//////////
	final int viewd_unscale(int x) {
		return x >> 16;
	}
	
	private void dbg(String str) {
		System.out.println("MapDrawer:"+ str);
	}
	@Override
	public void redraw(Canvas canvas, int state, int px, int py,
			int view_dx, int view_dy, int walk_step, int view_offset, RangeSet rset, int ang) {
		//dbg("redraw") ;
		if (state != Constants.STATE_PLAY)
			return ;
		if (maze.isInMapMode()) {
			draw_map(canvas, px, py, walk_step, view_dx, view_dy, maze.isInShowMazeMode(), maze.isInShowSolutionMode()) ;
			draw_currentlocation(canvas, view_dx, view_dy) ;
		}
	}
	/**
	 * Helper method for redraw_play, called if map_mode is true, i.e. the users wants to see the overall map.
	 * The map is drawn only on a small rectangle inside the maze area such that only a part of the map is actually shown.
	 * Of course a part covering the current location needs to be displayed.
	 * @param gc graphics handler to manipulate screen
	 */
	public void draw_map(Canvas canvas, int px, int py, int walk_step, int view_dx, int view_dy, boolean showMaze, boolean showSolution) {
//		panel.setColor("white"); // not needed?
		int vx = px*map_unit+map_unit/2;
		vx += viewd_unscale(view_dx*(step_size*walk_step));
		int vy = py*map_unit+map_unit/2;
		vy += viewd_unscale(view_dy*(step_size*walk_step));
		int offx = -vx*map_scale/map_unit + view_width/2;
		int offy = -vy*map_scale/map_unit + view_height/2;
		// get minimum for x,y
		int xmin = -offx/map_scale;
		int ymin = -offy/map_scale;
		if (xmin < 0) xmin = 0; 
		if (ymin < 0) ymin = 0;
		// get maximum for x,y
		int xmax = (view_width -offx)/map_scale+1;
		int ymax = (view_height-offy)/map_scale+1;
		if (xmax >= mazew)  xmax = mazew;
		if (ymax >= mazeh)  ymax = mazeh;
		// iterate over integer grid between min and max of x,y
		for (int y = ymin; y <= ymax; y++)
			for (int x = xmin; x <= xmax; x++) {
				int nx1 = x*map_scale + offx;
				int ny1 = view_height-1-(y*map_scale + offy);
				int nx2 = nx1 + map_scale;
				int ny2 = ny1 - map_scale;
				//int nx2 = x*map_scale + offx + map_scale;
				//int ny2 = view_height-1-(y*map_scale + offy + map_scale);
				//boolean s = ((seencells[x][y] & MazeBuilder.CW_TOP) != 0);
				//boolean s = seencells.hasWallOnTop(x, y) ;
				// inlined for clarity
				boolean w = (x >= mazew) ? false : ((y < mazeh) ?
						mazecells.hasWallOnTop(x,y) :
							mazecells.hasWallOnBottom(x, y-1));

				panel.setColor(seencells.hasWallOnTop(x, y) ? "white" : "gray");
				if ((seencells.hasWallOnTop(x, y) || showMaze) && w)
					panel.drawLine(canvas, nx1, ny1, nx2, ny1);
				
				//s = ((seencells[x][y] & MazeBuilder.CW_LEFT) != 0);
				//s = seencells.hasWallOnLeft(x, y) ;
				// inlined for clarity
				w = (y >= mazeh) ? false : ((x < mazew) ?
						mazecells.hasWallOnLeft(x, y) :
							mazecells.hasWallOnRight((x-1), y));

				panel.setColor(seencells.hasWallOnLeft(x, y) ? "white" : "gray");
				if ((seencells.hasWallOnLeft(x, y) || showMaze) && w)
					panel.drawLine(canvas, nx1, ny1, nx1, ny2);
			}
		if (showSolution) {
			//draw_solution(gc, offx, offy);
			draw_solution(canvas, offx, offy, px, py) ;
		}
		// draw an oval red shape for the current position and direction on the maze
		//draw_currentlocation(gc);

	}
	/**
	 * Draws an oval red shape with and arrow for the current position and direction on the maze.
	 * It always reside on the center of the screen. The map drawing moves if the user changes location.
	 * @param gc
	 */
	public void draw_currentlocation(Canvas canvas, int view_dx, int view_dy) {
		panel.setColor("red");
		// draw oval of appropriate size at the center of the screen
		int ctrx = view_width/2; // center x
		int ctry = view_height/2; // center y
		int cirsiz = map_scale/2; // circle size
		panel.drawOval(canvas, ctrx-cirsiz/2, ctry-cirsiz/2, cirsiz, cirsiz);
		// draw a red arrow with the oval to indicate direction
		int arrlen = 7*map_scale/16; // arrow length
		int aptx = ctrx + ((arrlen * view_dx) >> 16);
		int apty = ctry - ((arrlen * view_dy) >> 16);
		int arrlen2 = map_scale/4;
		int aptx2 = ctrx + ((arrlen2 * view_dx) >> 16);
		int apty2 = ctry - ((arrlen2 * view_dy) >> 16);
		//int ptoflen = map_scale/8; // unused
		int ptofx = -( arrlen2 * view_dy) >> 16;
		int ptofy = -( arrlen2 * view_dx) >> 16;
		panel.drawLine(canvas, ctrx, ctry, aptx, apty);
		panel.drawLine(canvas, aptx, apty, aptx2 + ptofx, apty2 + ptofy);
		panel.drawLine(canvas, aptx, apty, aptx2 - ptofx, apty2 - ptofy);
	}
	
	/**
	 * Draws a yellow line to show the solution on the overall map. 
	 * Method is only called if in STATE_PLAY and map_mode and showSolution are true.
	 * Since the current position is fixed at the center of the screen, all lines on the map are drawn with some offset.
	 * @param gc to draw lines on
	 * @param offx
	 * @param offy
	 */
	public void draw_solution(Canvas canvas, int offx, int offy, int px, int py) {
		// check parameters:
		if ((px < 0 || px > mazew) || (px < 0 || py > mazew))
		{
			dbg(" Parameter error: position out of bounds: (" + px + "," + py + ") for maze of size " + mazew + "," + mazeh) ;
			return ;
		}
		// current position on the solution path (sx,sy)
		int sx = px;
		int sy = py;
		int d = mazedists[sx][sy]; // current distance towards end position
		panel.setColor("yellow");
		// while we are more than 1 step away from the final position
		while (d > 1) {
			// find the direction towards the end position
			int n = getDirectionIndexTowardsSolution(sx, sy, d) ;
			if (4 == n)
			{
				System.out.println("ERROR: draw_solution cannot identify direction towards solution!") ;
				// TODO: perform proper error handling here
				return ;
			}
			int dx = Constants.DIRS_X[n];
			int dy = Constants.DIRS_Y[n];
			int dn = mazedists[sx+dx][sy+dy];
			// calculate coordinates and delta values towards new coordinates
			int nx1 = sx*map_scale + offx + map_scale/2;
			int ny1 = view_height-1-(sy*map_scale + offy) - map_scale/2;
			int ndx =  dx * map_scale;
			int ndy = -dy * map_scale;
			// do the graphics
			panel.drawLine(canvas, nx1, ny1, nx1+ndx, ny1+ndy);
			// update loop variables for current position (sx,sy) and distance d for next iteration
			sx += dx;
			sy += dy;
			d = dn;
		}
	}
	
	// same code as in Maze.java
	private int getDirectionIndexTowardsSolution(int x, int y, int d) {
		for (int n = 0; n < 4; n++) {
			if (mazecells.hasMaskedBitsTrue(x,y,Constants.MASKS[n]))
				continue;
				int dx = Constants.DIRS_X[n];
				int dy = Constants.DIRS_Y[n];
				int dn = mazedists[x+dx][y+dy];
				if (dn < d)
					return n ;
		}
		return 4 ;
	}
}

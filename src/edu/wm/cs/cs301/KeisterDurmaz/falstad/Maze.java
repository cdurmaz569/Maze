package edu.wm.cs.cs301.KeisterDurmaz.falstad;

import java.util.ArrayList;
import java.util.Iterator;

import android.graphics.Canvas;
import android.util.*;

/**
 * Class handles the user interaction for the maze. 
 * It implements a state-dependent behavior that controls the display and reacts to key board input from a user. 
 * After refactoring the original code from an applet into a panel, it is wrapped by a MazeApplication to be a java application 
 * and a MazeApp to be an applet for a web browser. At this point user keyboard input is first dealt with a key listener
 * and then handed over to a Maze object by way of the keyDown method.
 *
 * This code is refactored code from Maze.java by Paul Falstad, www.falstad.com, Copyright (C) 1998, all rights reserved
 * Paul Falstad granted permission to modify and use code for teaching purposes.
 * Refactored by Peter Kemper
 */
// MEMO: original code: public class Maze extends Applet {
//public class Maze extends Panel {
public class Maze  {

	// Model View Controller pattern, the model needs to know the viewers
	// however, all viewers share the same graphics to draw on, such that the share graphics
	// are administered by the Maze object
	final private ArrayList<Viewer> views = new ArrayList<Viewer>() ; 
	
	ActivitySingleton activitySingleton = ActivitySingleton.getInstance();
	MazePanel panel = activitySingleton.getPanel(); // graphics to draw on, shared by all views

//	public MazeView view; // TODO P6 gg

	public int state;			// keeps track of the current GUI state, one of STATE_TITLE,...,STATE_FINISH, mainly used in redraw() 
	// possible values are defined in Constants
	// user can navigate 
	// title -> generating -(escape) -> title
	// title -> generation -> play -(escape)-> title
	// title -> generation -> play -> finish -> title
	// STATE_PLAY is the main state where the user can navigate through the maze in a first person view

	private int percentdone = 0; // describes progress during generation phase
	private boolean showMaze;		 	// toggle switch to show overall maze on screen 
	private boolean showSolution;		// toggle switch to show solution in overall maze on screen 
	private boolean solving;			// toggle switch 
	private boolean mapMode; // true: display map of maze, false: do not display map of maze 
	// map_mode is toggled by user keyboard input, causes a call to draw_map during play mode

	//static final int viewz = 50;
	int viewx, viewy, angle; // angle= E:0, N:90, W:180, S:270
	int dx, dy;  // current direction
	int px, py ; // current position on maze grid (x,y)
	int walkStep;
	int viewdx, viewdy; // current view direction


	// debug stuff
	boolean deepdebug = false;
	boolean allVisible = false;
	boolean newGame = false;

	// properties of the current maze
	public int mazew; // width of maze
	public int mazeh; // height of maze
	Cells mazecells ; // maze as a matrix of cells which keep track of the location of walls
	public Distance mazedists ; // a matrix with distance values for each cell towards the exit
	Cells seencells ; // a matrix with cells to memorize which cells are visible from the current point of view
	// the FirstPersonDrawer obtains this information and the MapDrawer uses it for highlighting currently visible walls on the map
	BSPNode rootnode ; // a binary tree type search data structure to quickly locate a subset of segments
	// a segment is a continuous sequence of walls in vertical or horizontal direction
	// a subset of segments need to be quickly identified for drawing
	// the BSP tree partitions the set of all segments and provides a binary search tree for the partitions
	

	// Mazebuilder is used to calculate a new maze together with a solution
	// The maze is computed in a separate thread. It is started in the local Build method.
	// The calculation communicates back by calling the local newMaze() method.
	public MazeBuilder mazebuilder;

	
	// fixing a value matching the escape key
	final int ESCAPE = 27;

	// generation method used to compute a maze
	int method = 0 ; // 0 : default method, Falstad's original code
	// method == 1: Prim's  

	int zscale = Constants.VIEW_HEIGHT/2;

	private RangeSet rset;

	private MazeCompleteObserver mazeCompleteCallback;
	
	/**
	 * Constructor
	 */
	public Maze() {
		super() ;
	}
	/**
	 * Constructor that also selects a particular generation method
	 */
	public Maze(int method) {
		super() ; 
		
		if (1 == method){
			this.method = 1 ;
		} else if (2 == method) {
			this.method = 2 ; //AB
		}
	}
	/**
	 * Method to initialize internal attributes. Called separately from the constructor. 
	 */
	public void init() {
		state = Constants.STATE_TITLE;
		rset = new RangeSet();
//		panel.initBufferImage() ; // TODO P6 gg
//		view = new MazeView(this, panel) ; // TODO P6 gg
//		addView(view) ; // TODO P6 gg
		notifyViewerInvalidate() ;
	}


	/**
	 * Method obtains a new Mazebuilder and has it compute new maze, 
	 * it is only used in keyDown()
	 * @param skill level determines the width, height and number of rooms for the new maze
	 */
	public void build(int skill) { 
		// switch screen
		state = Constants.STATE_GENERATING;
		percentdone = 0;
		notifyViewerInvalidate() ;
		// select generation method
		switch(method){
		case 2 : mazebuilder = new MazeBuilderAldousBroder(true, panel);
		break ;
		case 1 : mazebuilder = new MazeBuilderPrim(true, panel); // generate with Prim's algorithm
		// 										   true - in MazeBuilder. fixes random maze building
		break ;
		case 0: // generate with Falstad's original algorithm (0 and default), note the missing break statement
		default : mazebuilder = new MazeBuilder(true, panel); 
		//		   								true - in MazeBuilder. fixes random maze building
		break ;
		}
		// adjust settings and launch generation in a separate thread
		mazew = Constants.SKILL_X[skill];
		mazeh = Constants.SKILL_Y[skill];
		Log.v("", "Calling mazebuilder.build()");
		mazebuilder.build(this, mazew, mazeh, Constants.SKILL_ROOMS[skill], Constants.SKILL_PARTCT[skill]);
		// mazebuilder performs in a separate thread and calls back by calling newMaze() to return newly generated maze
	}
	
	/**
	 * Call back method for MazeBuilder to communicate newly generated maze as reaction to a call to build()
	 * @param root node for traversals, used for the first person perspective
	 * @param cells encodes the maze with its walls and border
	 * @param dists encodes the solution by providing distances to the exit for each position in the maze
	 * @param startx current position, x coordinate
	 * @param starty current position, y coordinate
	 */
	public void newMaze(BSPNode root, Cells c, Distance dists, int startx, int starty) {
		if (Cells.deepdebugWall)
		{   // for debugging: dump the sequence of all deleted walls to a log file
			// This reveals how the maze was generated
			c.saveLogFile(Cells.deepedebugWallFileName);
		}
		// adjust internal state of maze model
		showMaze = showSolution = solving = false;
		mazecells = c ;
		mazedists = dists;
		seencells = new Cells(mazew+1,mazeh+1) ;
		rootnode = root ;
		setCurrentDirection(1, 0) ;
		setCurrentPosition(startx,starty) ;
		walkStep = 0;
		viewdx = dx<<16; 
		viewdy = dy<<16;
		angle = 0;
		mapMode = false;
		// set the current state for the state-dependent behavior
		state = Constants.STATE_PLAY;
		cleanViews() ;
		// register views for the new maze
		// mazew and mazeh have been set in build() method before mazebuider was called to generate a new maze.
		// reset map_scale in mapdrawer to a value of 10
		addView(new FirstPersonDrawer(panel, Constants.VIEW_WIDTH,Constants.VIEW_HEIGHT,
				Constants.MAP_UNIT,Constants.STEP_SIZE, mazecells, seencells, 10, mazedists.getDists(), mazew, mazeh, root, this)) ;
		// order of registration matters, code executed in order of appearance!
		addView(new MapDrawer(panel, Constants.VIEW_WIDTH,Constants.VIEW_HEIGHT,Constants.MAP_UNIT,Constants.STEP_SIZE, mazecells, seencells, 10, mazedists.getDists(), mazew, mazeh, this)) ;

		// need to redraw
		notifyViewerInvalidate() ;
	}

	/////////////////////////////// Methods for the Model-View-Controller Pattern /////////////////////////////
	/**
	 * Register a view
	 */
	public void addView(Viewer view) {
		views.add(view) ;
	}
	/**
	 * Unregister a view
	 */
	public void removeView(Viewer view) {
		views.remove(view) ;
	}
	/**
	 * Remove obsolete FirstPersonDrawer and MapDrawer
	 */
	public void cleanViews() { 
		// go through views and notify each one
		Iterator<Viewer> it = views.iterator() ;
		while (it.hasNext())
		{
			Viewer v = it.next() ;
			if ((v instanceof FirstPersonDrawer)||(v instanceof MapDrawer))
			{
				//System.out.println("Removing " + v);
				it.remove() ;
			}
		}

	}

	/**
	 * Notify all registered viewers to redraw their graphics
	 */
	public void notifyViewerInvalidate() { 
//		Log.v("Maze: notifyViewerInvalidate", "invalidate");
		ActivitySingleton activitySingleton = ActivitySingleton.getInstance();
		MazePanel panel = activitySingleton.getPanel();
		panel.invalidate();
	}
	
	public void notifyViewerRedraw(MazePanel panel, Canvas canvas) {
		// go through views and notify each one
		Iterator<Viewer> it = views.iterator() ;
		while (it.hasNext())
		{
			Viewer v = it.next() ;
			// viewers draw on the buffer graphics
			v.redraw(canvas, state, px, py, viewdx, viewdy, walkStep, Constants.VIEW_OFFSET, rset, angle) ;
//			panel.updateBufferGraphics(); // TODO P6 gg
		}
		// update the screen with the buffer graphics
//		panel.update() ; // TODO P6 gg
	}
	/** 
	 * Notify all registered viewers to increment the map scale
	 */
//	private void notifyViewerIncrementMapScale() {
//		// go through views and notify each one
//		Iterator<Viewer> it = views.iterator() ;
//		while (it.hasNext())
//		{
//			Viewer v = it.next() ;
//			v.incrementMapScale() ;
//		}
//		// update the screen with the buffer graphics
////		panel.update() ; // TODO P6 gg
//	}
	/** 
	 * Notify all registered viewers to decrement the map scale
	 */
//	private void notifyViewerDecrementMapScale() {
//		// go through views and notify each one
//		Iterator<Viewer> it = views.iterator() ;
//		while (it.hasNext())
//		{
//			Viewer v = it.next() ;
//			v.decrementMapScale() ;
//		}
//		// update the screen with the buffer graphics
////		panel.update() ; // TODO P6 gg
//	}
	////////////////////////////// get methods ///////////////////////////////////////////////////////////////
	boolean isInMapMode() { 
		return mapMode ; 
	} 
	boolean isInShowMazeMode() { 
		return showMaze ; 
	} 
	boolean isInShowSolutionMode() { 
		return showSolution ; 
	} 
	public String getPercentDone(){
		return String.valueOf(percentdone) ;
	}
	////////////////////////////// set methods ///////////////////////////////////////////////////////////////
	////////////////////////////// Actions that can be performed on the maze model ///////////////////////////
	public void setCurrentPosition(int x, int y) 
	{
		px = x ;
		py = y ;
	}
	public void setCurrentDirection(int x, int y)
	{
		dx = x ;
		dy = y ;
	}
	
	
	public void buildInterrupted() {
		state = Constants.STATE_TITLE;
		notifyViewerInvalidate() ;
		mazebuilder = null;
	}

	final double radify(int x) {
		return x*Math.PI/180;
	}


	/**
	 * Allows external increase to percentage in generating mode with subsequence graphics update
	 * @param pc gives the new percentage on a range [0,100]
	 * @return true if percentage was updated, false otherwise
	 */
	public boolean increasePercentage(int pc) {
		if (percentdone < pc && pc < 100) {
			percentdone = pc;
			if (state == Constants.STATE_GENERATING)
			{
				notifyViewerInvalidate() ;
			}
			else
				dbg("Warning: Receiving update request for increasePercentage while not in generating state, skip redraw.") ;
				// TODO Warning here: increasePercentage
			return true ;
		}
		return false ;
	}

	



	/////////////////////// Methods for debugging ////////////////////////////////
	private void dbg(String str) {
		System.out.println(str);
	}

	private void logPosition() {
		if (!deepdebug)
			return;
		dbg("x="+viewx/Constants.MAP_UNIT+" ("+
				viewx+") y="+viewy/Constants.MAP_UNIT+" ("+viewy+") ang="+
				angle+" dx="+dx+" dy="+dy+" "+viewdx+" "+viewdy);
	}
	///////////////////////////////////////////////////////////////////////////////

	/**
	 * Helper method for walk()
	 * @param dir
	 * @return true if there is no wall in this direction
	 */
	private boolean checkMove(int dir) {
		// obtain appropriate index for direction (CW_BOT, CW_TOP ...) 
		// for given direction parameter
		int a = angle/90;
		if (dir == -1)
			a = (a+2) & 3; // TODO: check why this works
		// check if cell has walls in this direction
		// returns true if there are no walls in this direction
		return mazecells.hasMaskedBitsFalse(px, py, Constants.MASKS[a]) ;
	}



	private void rotateStep() {
		angle = (angle+1800) % 360;
		viewdx = (int) (Math.cos(radify(angle))*(1<<16));
		viewdy = (int) (Math.sin(radify(angle))*(1<<16));
		moveStep();
	}

	private void moveStep() { 
		notifyViewerInvalidate() ;
		try {
			Thread.currentThread().sleep(25);
		} catch (Exception e) { }
	}

	private void rotateFinish() {
		setCurrentDirection((int) Math.cos(radify(angle)), (int) Math.sin(radify(angle))) ;
		logPosition();
	}

	private void walkFinish(int dir) {
		setCurrentPosition(px + dir*dx, py + dir*dy) ;
		
		if (isEndPosition(px,py)) {
			state = Constants.STATE_FINISH;
			notifyViewerInvalidate() ;
			
			Log.v("Maze: walkFinish", "STATE_FINISH and passing outcome to playActivity");
			int outcome = Constants.OUTCOME_WIN;
			mazeCompleteCallback.startFinishActivity(outcome);
		}
		walkStep = 0;
		logPosition();
	}

	/**
	 * checks if the given position is outside the maze
	 * @param x
	 * @param y
	 * @return true if position is outside, false otherwise
	 */
	private boolean isEndPosition(int x, int y) {
		return x < 0 || y < 0 || x >= mazew || y >= mazeh;
	}



	private synchronized void walk(int dir) { 
		if (!checkMove(dir))
			return;
		for (int step = 0; step != 4; step++) {
			walkStep += dir;
			moveStep();
		}
		walkFinish(dir);
	}

	private synchronized void rotate(int dir) { 
		final int originalAngle = angle;
		final int steps = 4;

		for (int i = 0; i != steps; i++) {
			angle = originalAngle + dir*(90*(i+1))/steps;
			rotateStep();
		}
		rotateFinish();
	}



	/**
	 * Method incorporates all reactions to keyboard input in original code, 
	 * after refactoring, Java Applet and Java Application wrapper call this method to communicate input.
	 */
	public boolean keyDown(int key) { //Event evt
		switch (state) {
		// if screen shows title page, keys describe level of expertise
		// create a maze according to the user's selected level
		case Constants.STATE_TITLE:
			if (key >= '0' && key <= '9') {
				build(key - '0');  
				break;
			}
			if (key >= 'a' && key <= 'f') {
				build(key - 'a' + 10);
				break;
			}
			break;
		// if we are currently generating a maze, recognize interrupt signal (ESCAPE key)
		// to stop generation of current maze
		case Constants.STATE_GENERATING:
			if (key == ESCAPE) {
				mazebuilder.interrupt();
				buildInterrupted();
			}
			break;
		// if user explores maze, 
		// react to input for directions and interrupt signal (ESCAPE key)	
		// react to input for displaying a map of the current path or of the overall maze (on/off toggle switch)
		// react to input to display solution (on/off toggle switch)
		// react to input to increase/reduce map scale
		case Constants.STATE_PLAY:
			switch (key) {
//			case Event.UP: 
			case 'k': case '8':
				Log.v("Maze: keyDown", "Calling UP");
				walk(1);
				Log.v("Maze:keyDown", "Called UP");
				notifyViewerInvalidate();
				break;
//			case Event.LEFT: 
			case 'h': case '4':
				Log.v("Maze: keyDown", "Calling LEFT");
				rotate(1);
				Log.v("Maze:keyDown", "Called LEFT");
				notifyViewerInvalidate();
				break;
//			case Event.RIGHT: 
			case 'l': case '6':
				Log.v("Maze: keyDown", "Calling RIGHT");
				rotate(-1);
				Log.v("Maze:keyDown", "Called RIGHT");
				notifyViewerInvalidate();
				break;
//			case Event.DOWN: 
			case 'j': case '2':
				Log.v("Maze: keyDown", "Calling DOWN");
				walk(-1);
				Log.v("Maze:keyDown", "Called DOWN");
				notifyViewerInvalidate();
				break;
			case ESCAPE: case 65385:
				if (solving)
					solving = false;
				else
					state = Constants.STATE_TITLE;
				notifyViewerInvalidate() ;
				break;
			case ('w' & 0x1f):
			{ 
				setCurrentPosition(px + dx, py + dy) ;
				notifyViewerInvalidate() ;
				break;
			}
			case '\t': case 'm':
				mapMode = !mapMode; 		
				notifyViewerInvalidate() ; 
				break;
			case 'z':
				showMaze = !showMaze; 		
				notifyViewerInvalidate() ; 
				break;
			case 's':
				showSolution = !showSolution; 		
				notifyViewerInvalidate() ;
				break;
			case ('s' & 0x1f):
				if (solving)
					solving = false;
				else {
					solving = true;
				}
			break;
			case '+': case '=':
			{
				// TODO P6 gg notifyViewerIncrementMapScale() ;
				notifyViewerInvalidate() ; // seems useless but it is necessary to make the screen update
				break ;
			}
			case '-':
				//TODO P6 gg notifyViewerDecrementMapScale() ;
				notifyViewerInvalidate() ; // seems useless but it is necessary to make the screen update
				break ;
			}
			break;
		// if we are finished, return to initial state with title screen	
		case Constants.STATE_FINISH:
			state = Constants.STATE_TITLE;
			notifyViewerInvalidate() ;
			break;
		} 
		return true;
	}

	public void registerMazeCompleteObserver(MazeCompleteObserver callback) {
		Log.v("Maze: registerMazeCompleteObserver", "registering mazeCompleteCallback");
		mazeCompleteCallback = callback;
    }
	public MazePanel getMazePanel() {
		return panel;
	}


	


}

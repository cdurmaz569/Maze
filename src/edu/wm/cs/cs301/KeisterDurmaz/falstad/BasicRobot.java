package edu.wm.cs.cs301.KeisterDurmaz.falstad;

import java.util.Arrays;

import android.util.Log;

/**
 * @author nkeister
 *
 */
public class BasicRobot implements Robot {

	Maze maze;
	public Battery battery = new Battery();
	private int pathLength = 0;
	
	// sensors
	private boolean distanceSensorForward = true;
	private boolean distanceSensorBackward = true;
	private boolean distanceSensorLeft = true;
	private boolean distanceSensorRight = true;
	private boolean roomSensor = true;
	private boolean junctionSensor = true;
	
	public BasicRobot() {
		// default sensors (has all sensors)
		Log.v("AMazeActivity", "BasicRobot constructor");
	}

	/**
	 * Constructor
	 * @param dsf distanceSensorForward boolean
	 * @param dsb distanceSensorBackward boolean
	 * @param dsl distanceSensorLeft boolean
	 * @param dsr distanceSensorRight boolean
	 * @param rs roomSensor boolean
	 * @param js junctionSensor boolean
	 * @throws UnsuitableRobotException 
	 */
	public BasicRobot(boolean dsf, boolean dsb, boolean dsl, boolean dsr, 
			boolean rs, boolean js, float batteryLevel) throws UnsuitableRobotException {
		Log.v("AMazeActivity", "BasicRobot constructor");
		// sensors
		distanceSensorForward = dsf;
		distanceSensorBackward = dsb;
		distanceSensorLeft = dsl;
		distanceSensorRight = dsr;
		roomSensor = rs;
		junctionSensor = js;
		battery.setLevel(batteryLevel);
	}
	
	@Override
	public void rotate(Turn turn) throws Exception {
		
		//System.out.println("\nROTATE");
		//System.out.println("Start: " + getCardinalDirection());
		//System.out.println("Rotating " + turn);
		Log.v("AMazeActivity", "rotate()");
		
		if (turn == Turn.RIGHT) {
			battery.discharge(getEnergyForFullRotation() / 4);
			maze.keyDown('l');
		}
		else if (turn == Turn.LEFT) {
			battery.discharge(getEnergyForFullRotation() / 4);
			maze.keyDown('h');
		}
		else { // AROUND
			battery.discharge(getEnergyForFullRotation() / 4);
			maze.keyDown('l');
			battery.discharge(getEnergyForFullRotation() / 4);
			maze.keyDown('l');
		}

		//System.out.println("End: " + getCardinalDirection());
	}

	@Override
	public void move(int distance) throws Exception, OutOfBatteryException {
		//System.out.println("\nMOVE");
		//System.out.println("Start: " + maze.px + ", " + maze.py);
		Log.v("AMazeActivity", "move()");
		
		
		if (!hasDistanceSensor(Direction.FORWARD)) {
			System.out.println("ERROR: no sensor");
			throw new Exception();
		}
		if (!canMove(maze.px, maze.py, getCardinalDirection())) {
			System.out.println("WARNING: robot is facing a wall and cannot move");
			//throw new Exception();
		}
		
		// must have enough battery level to move, must not have hit a wall
		while (distance > 0 && canMove(maze.px, maze.py, getCardinalDirection())) {
			if (!canMove(maze.px, maze.py, getCardinalDirection())) {
				System.out.println("WARNING: robot is facing a wall and cannot move");
				throw new Exception();
			}
			pathLength++;
			distance--;

			battery.discharge(getEnergyForStepForward());			
			maze.keyDown('k') ;	
		}
		//System.out.println("End: " + maze.px + ", " + maze.py);
	}
		  
	public boolean canMove(int px, int py, CardinalDirection dir) {
		//System.out.println("isatgoal " + isAtGoal());
		//System.out.println("canseegoal-forward " + canSeeGoal(Direction.FORWARD));
		//System.out.println("direction-forward " + getSensingDirection(Direction.FORWARD) + " / dir " + dir);
		
		// check if robot is trying to exit maze
		if (isAtGoal() == true && canSeeGoal(Direction.FORWARD) == true && getSensingDirection(Direction.FORWARD) == dir){
			return true;
		}
		
		if (dir == CardinalDirection.NORTH){ 
			return maze.mazecells.hasNoWallOnBottom(maze.px, maze.py);
		}else if (dir == CardinalDirection.EAST){ 
			return maze.mazecells.hasNoWallOnRight(maze.px, maze.py);
		}else if (dir == CardinalDirection.SOUTH){ 
			return maze.mazecells.hasNoWallOnTop(maze.px, maze.py);
		}else { // EAST
			return maze.mazecells.hasNoWallOnLeft(maze.px, maze.py);
		}
	}

	@Override
	public int[] getCurrentPosition() throws Exception {
		// if out of bounds
		if (maze.px >= maze.mazew || maze.px < 0 || maze.py >= maze.mazeh || maze.py < 0) {
			System.out.println("ERROR: currect position is outside maze bounds");
			throw new Exception();
		}
		
		int[] position = {maze.px, maze.py};
		return position;
	}
	
	/**
	 * Sets the robots current position inside the maze. 
	 * Note: does not check for valid x and y since this is used only for testing.
	 * @param x
	 * @param y
	 */
	public void setCurrentPosition(int x, int y) {
		maze.setCurrentPosition(x, y);
	}

	@Override
	public void setMaze(Maze maze) {
		this.maze = maze;
	}

	@Override
	public boolean isAtGoal() {
		return maze.mazecells.isExitPosition(maze.px, maze.py);
	}

	@Override
	public boolean canSeeGoal(Direction direction) throws UnsupportedOperationException {

		if (hasDistanceSensor(direction) == false){
			throw new UnsupportedOperationException();
		}
		
		try {
			battery.discharge(getEnergyForSensing());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		int curx = maze.px;
		int cury = maze.py;
		int goalx = maze.mazedists.exitposition[0];
		int goaly = maze.mazedists.exitposition[1];
		CardinalDirection senseDir = getSensingDirection(direction);
		CardinalDirection exitDir;
		
		// translate bits representing direction from Cells class into CardinalDirection
		if (maze.mazecells.exitDirection == 1){
			exitDir = CardinalDirection.SOUTH;
		}else if (maze.mazecells.exitDirection == 2){
			exitDir = CardinalDirection.NORTH;
		}else if (maze.mazecells.exitDirection == 4){
			exitDir = CardinalDirection.WEST;
		}else { // 8
			exitDir = CardinalDirection.EAST;
		}
		
		// If the robot is facing the correct way (toward the exit)
		if (senseDir == exitDir) {
			
			if (senseDir == CardinalDirection.NORTH) {
				while (maze.mazecells.hasNoWallOnTop(curx, cury) == true && (curx != goalx && cury != goaly)) {
					cury--;
				}
			}else if (senseDir == CardinalDirection.EAST) {
				while (maze.mazecells.hasNoWallOnRight(curx, cury) == true && (curx != goalx && cury != goaly)) {
					curx++;
				}
			}else if (senseDir == CardinalDirection.SOUTH) {
				while (maze.mazecells.hasNoWallOnBottom(curx, cury) == true && (curx != goalx && cury != goaly)) {
					cury++;
				}
			}else { // WEST
				while (maze.mazecells.hasNoWallOnLeft(curx, cury) == true && (curx != goalx && cury != goaly)) {
					curx--;
				}
			}
			
			if ((curx == goalx && cury == goaly)) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean isAtJunction() throws UnsupportedOperationException {
		if (hasJunctionSensor() == false) {
			throw new UnsupportedOperationException();
		}

		try {
			battery.discharge(getEnergyForSensing());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (canMove(maze.px, maze.py, getSensingDirection(Direction.LEFT))  && canMove(maze.px, maze.py, getSensingDirection(Direction.RIGHT))) {
			System.out.println("Is at junction");
			return true;
		}
		System.out.println("Is not at junction");
		return false;
	}
	
	@Override
	public boolean hasPathOptions() throws UnsupportedOperationException {
		if (!hasJunctionSensor() || !hasDistanceSensor(Direction.FORWARD)){
			throw new UnsupportedOperationException();
		}
		
		int paths = 0;
		if (distanceToObstacle(Direction.FORWARD) > 0) {
			paths++;
		}
		if (distanceToObstacle(Direction.LEFT) > 0) {
			paths++;
		}
		if (distanceToObstacle(Direction.RIGHT) > 0) {
			paths++;
		}
		
		if (paths > 1) {
			return true;
		}
		return false;
	}

	@Override
	public boolean hasJunctionSensor() {
		return junctionSensor;
	}

	@Override
	public boolean isInsideRoom() throws UnsupportedOperationException {

		if (hasRoomSensor() == false) {
			throw new UnsupportedOperationException();
		}
		
		try {
			battery.discharge(getEnergyForSensing());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (maze.mazecells.isInRoom(maze.px, maze.py) == true) {
			return true;
		}
		
		return false;
	}

	@Override
	public boolean hasRoomSensor() {
		return roomSensor;
	}
	
	/**
	 * Helper method.
	 * Given the current direction of the robot, return the cardinal direction it's facing.
	 * @return String of cardinal direction (North, South, East, West)
	 */
	private CardinalDirection getCardinalDirection() {
		if (maze.dx == 0 && maze.dy == 1){ // facing north
			return CardinalDirection.NORTH;
		}else if (maze.dx == 1 && maze.dy == 0){ // facing east
			return CardinalDirection.EAST;
		}else if (maze.dx == 0 && maze.dy == -1){ // facing south
			return CardinalDirection.SOUTH;
		}else { // maze.dx == -1 && maze.dy == 0 // facing west
			return CardinalDirection.WEST;
		}
	}
	
	@SuppressWarnings("unused")
	private void printCardinalDirection() {
		  if (getCardinalDirection() == CardinalDirection.NORTH) {
		   System.out.print("North ");
		  }
		  if (getCardinalDirection() == CardinalDirection.EAST){
		   System.out.print("East ");
		  }
		  if (getCardinalDirection() == CardinalDirection.SOUTH){
		   System.out.print("South ");
		  }
		  if (getCardinalDirection() == CardinalDirection.WEST){
		   System.out.print("West ");
		  }
	}

	@Override
	public int[] getCurrentDirection() {
		int[] position = {maze.dx, maze.dy};
		return position;
	}
	
	/**
	 * Set the current direction for the robot in the maze.
	 * Note: does not check for valid x and y, since this is used only for testing.
	 * @param x
	 * @param y
	 */
	public void setCurrentDirection(int x, int y) {
		maze.setCurrentDirection(x, y);
		
		// set angle
		if (x == 0 && y == 1){ // facing north
			maze.angle = 90;
		}else if (x == 1 && y == 0){ // facing east
			maze.angle = 0;
		}else if (x == 0 && y == -1){ // facing south
			maze.angle = 270;
		}else { // x == -1, y == 0  facing west
			maze.angle = 180;
		}
	}

	@Override
	public float getBatteryLevel() {
		return battery.getLevel();
	}
	
	@Override
	public float getEnergyConsumed() {
		return battery.getEnergyConsumed();
	}
	
	@Override
	public int getPathLength() {
		return this.pathLength;
	}
	
	@Override
	public void setPathLengthToZero() {
		this.pathLength = 0;
	}

	@Override
	public void setBatteryLevel(float level) {
		battery.setLevel(level);
	}
	
	@Override
	public void dischargeBattery(float level) {
		try {
			battery.discharge(level);
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public float getEnergyForFullRotation() {		
		return 12; 
	}

	@Override
	public float getEnergyForStepForward() {
		return 5;
	}
	
	public float getEnergyForSensing() {
		return 1;
	}

	@Override
	public boolean hasStopped() {
		
		if (battery.getLevel() <= 0) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public CardinalDirection getSensingDirection(Direction direction) {
			CardinalDirection[] cardinalDirs = {CardinalDirection.NORTH, CardinalDirection.EAST, CardinalDirection.SOUTH, CardinalDirection.WEST};
			CardinalDirection curDir = getCardinalDirection();
			int curDirIndex = Arrays.asList(cardinalDirs).indexOf(curDir); 
			
			int sensingDirIndex;
			if (direction == Direction.FORWARD){
				sensingDirIndex = curDirIndex;
			}else if (direction == Direction.BACKWARD){
				sensingDirIndex = (curDirIndex + 2) % 4;
			}else if (direction == Direction.RIGHT){
				sensingDirIndex = (curDirIndex + 1) % 4;
			}else { // LEFT
				sensingDirIndex = (curDirIndex + 3) % 4;
			}
			
			CardinalDirection sensingDir = cardinalDirs[sensingDirIndex];
			//System.out.println("Sensing " + direction + " / " + sensingDir);
			return sensingDir;
	}

	@Override
	public int distanceToObstacle(Direction direction) throws UnsupportedOperationException {
		
		if (hasDistanceSensor(direction) == false){ 
			throw new UnsupportedOperationException();
		}
		
		try {
			battery.discharge(getEnergyForSensing());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		int distance = 0;
		int curpx = maze.px;
		int curpy = maze.py;
		
		if (getSensingDirection(direction) == CardinalDirection.NORTH){
			while (maze.mazecells.hasNoWallOnBottom(curpx, curpy)) {
				curpy++;
				distance++;
				if (curpy >= maze.mazeh || curpy <= 0){
					return Integer.MAX_VALUE;
				}
			}
		}else if (getSensingDirection(direction) == CardinalDirection.EAST){
			while (maze.mazecells.hasNoWallOnRight(curpx, curpy)) {
				curpx++;
				distance++;
				if (curpx >= maze.mazew || curpx <= 0){
					return Integer.MAX_VALUE;
				}
			}
		}else if (getSensingDirection(direction) == CardinalDirection.SOUTH){
			while (maze.mazecells.hasNoWallOnTop(curpx, curpy)) {
				curpy--;
				distance++;
				if (curpy >= maze.mazeh || curpy <= 0){
					return Integer.MAX_VALUE;
				}
			}
		}else { // WEST
			while (maze.mazecells.hasNoWallOnLeft(curpx, curpy)) {
				curpx--;
				distance++;
				if (curpx >= maze.mazew || curpx <= 0){
					return Integer.MAX_VALUE;
				}
			}
		}

		return distance;
		
	}

	@Override
	public boolean hasDistanceSensor(Direction direction) {
		if (direction == Direction.BACKWARD){
			return distanceSensorBackward;
		}else if (direction == Direction.FORWARD){
			return distanceSensorForward;
		}else if (direction == Direction.RIGHT){
			return distanceSensorRight;
		}else { // LEFT
			return distanceSensorLeft;
		}
	}

	
	/**
	 * 
	 * Manage the battery for the robot.
	 */
	public class Battery {
		float level = 2500;
		float startingLevel = level;
		
		/**
		 * Return how much battery charge is left.
		 * @return amount left
		 */
		public float getLevel() {
			return level;
		}
		
		/**
		 * Return how much battery the robot started with
		 * @return original battery level
		 */
		public float getStartingLevel() {
			return startingLevel;
		}
		
		/**
		 * @return energy consumed
		 */
		public float getEnergyConsumed() {
			return startingLevel - level;
		}
		
		/**
		 * Set the battery level to value.
		 * @param level to set battery to.
		 */
		public void setLevel(float level) {
			this.level = level;
			startingLevel = level;
		}
		
		/**
		 * Use some amount of energy.
		 * @param amount of energy to use.
		 * @throws Exception
		 */
		public void discharge (float amount) throws OutOfBatteryException {
			level -= amount;
			if (level <= 0) {
				System.out.println("Out of energy.");
				throw new OutOfBatteryException();
			}
		}
	}

}

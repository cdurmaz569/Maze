package edu.wm.cs.cs301.KeisterDurmaz.falstad;

/**
 * This interface specifies methods to operate a robot that resides in 
 * a maze on a particular location and with a particular direction.
 * An implementing class will configure the robot with sensors 
 * and provide it with an existing maze.
 * It provides an operating platform for a robotdriver that experiences a maze (the real world) through the sensors and actors of this robot interface.
 * 
 * Note that a robot may be very limited in its mobility, e.g. only 90 degree left or right turns, which makes sense in the artificial terrain of a maze,
 * and its sensing capability, e.g. only a sensor on its front or left to detect remote obstacles. Left/right is a notion relative to the robot's direction 
 * or relative to the underlying maze. To avoid a confusion, the latter is considered a direction in an absolute sense and it may be better to describe it as 
 * north, south, east, west than up, down, right, left. 
 * 
 * A robot comes with a battery level that is depleted during operations such that a robot may actually stop if it runs out of energy.
 * This interface supports energy consideration. 
 * A robot may also stop when hitting an obstacle. 
 * 
 * WARNING: the use of CW_BOT/CW_TOP and (0,1) in Cells and Mazebuilder is inconsistent with the MapDrawer which has position (0,0) at the lower left and
 * a southbound direction on the map is (0,-1). Or in other words, the maze is drawn upside down by the MapDrawer. The rotation is calculated with 
 * polar coordinates (angle) towards a cartesian coordinate system where a southbound direction is (dx,dy)=(0,-1).
 * 
 * Implementing classes: movable robots with distance sensors of different kind. 
 * 
 * Collaborators: a maze class to be explored, a robotdriver class that operates robot
 * 
 * @author peterkemper
 *
 */

public interface Robot {
	
	/** 
	 * Describes all possible turns that a robot can do when it rotates on the spot.
	 */
	public enum Turn { LEFT, RIGHT, AROUND } ;
	/**
	 * Describes all possible directions from the point of view of the robot,
	 * i.e., relative to its current forward position.
	 */
	public enum Direction { LEFT, RIGHT, FORWARD, BACKWARD } ;
	/**
	 * Describes the absolute direction or map cardinal direction.
	 */
	public enum CardinalDirection {NORTH, EAST, SOUTH, WEST} ;
	/**
	 * Turn robot on the spot. If robot runs out of energy, it stops and throws an Exception, 
	 * which can be checked by hasStopped() == true and by checking the battery level. 
	 * @param direction to turn to relative to current forward direction 
	 * @throws Exception if the robot stops for lack of energy. 
	 */
	void rotate(Turn turn) throws Exception ;
	/**
	 * Moves robot forward a given number of steps. A step matches a single cell.
	 * Since a robot may only have a distance sensor in its front.
	 * If the robot runs out of energy somewhere on its way, it stops, 
	 * which can be checked by hasStopped() == true and by checking the battery level. 
	 * If the robot hits an obstacle like a wall, it remains at the position in front 
	 * of the obstacle but hasStopped() == false.
	 * @param distance is the number of cells to move in the robot's current forward direction
	 * @throws Exception if robot hits an obstacle like a wall or border, 
	 * which indicates that current position is not as expected. 
	 * Also thrown if robot runs out of energy. 
	 * @precondition distance >= 0
	 */
	void move(int distance) throws Exception ;
	/**
	 * Provides the current position as (x,y) coordinates for the maze cell as an array of length 2 with [x,y].
	 * @postcondition 0 <= x < width, 0 <= y < height of the maze. 
	 * @return array of length 2, x = array[0], y=array[1]
	 * @throws Exception if position is outside of the maze
	 */
	int[] getCurrentPosition() throws Exception  ;
	/**
	 * Provides the robot with a reference to the maze it is currently in.
	 * The robot memorizes the maze such that this method is most likely called only once
	 * and for initialization purposes. The maze serves as the main source of information
	 * about the current location, the presence of walls, the reaching of an exit.
	 * @param maze is the current maze
	 * @precondition maze != null, maze refers to a fully operational, configured maze object
	 */
	void setMaze(Maze maze) ;
	/**
	 * Tells if current position is at the goal (the exit). Used to recognize termination of a search.
	 * @return true if robot is at the goal, false otherwise
	 */
	boolean isAtGoal() ;
	/**
	 * Tells if a sensor can identify the goal in given direction relative to 
	 * the robot's current forward direction from the current position.
	 * @return true if the goal (here: exit of the maze) is visible in a straight line of sight
	 * @throws UnsupportedOperationException if robot has no sensor in this direction
	 */
	boolean canSeeGoal(Direction direction) throws UnsupportedOperationException ;
	/**
	 * Tells if current position is at a junction. 
	 * A junction is a position where there is no wall to the robot's right or left. 
	 * Note that this method is not helpful when the robot is inside a room. 
	 * For most positions inside a room, the robot has no walls to it's right or left
	 * such that the method returns true.
	 * @return true if robot is at a junction, false otherwise
	 * @throws UnsupportedOperationException if not supported by robot
	 */	
	boolean isAtJunction() throws UnsupportedOperationException ;
	/**
	 * Checks if the robot has more than one path option
	 * @returns boolean
	 */
	boolean hasPathOptions() throws UnsupportedOperationException ;
	/**
	 * Tells if the robot has a junction sensor.
	 */
	boolean hasJunctionSensor() ;
	/**
	 * Tells if current position is inside a room. 
	 * @return true if robot is inside a room, false otherwise
	 * @throws UnsupportedOperationException if not supported by robot
	 */	
	boolean isInsideRoom() throws UnsupportedOperationException ;
	/**
	 * Tells if the robot has a room sensor.
	 */
	boolean hasRoomSensor() ;	
	/**
	 * Provides the current direction as (dx,dy) values for the robot as an array of length 2 with [dx,dy].
	 * Note that dx,dy are elements of {-1,0,1} and as in bitmasks masks in Cells.java and dirsx,dirsy in MazeBuilder.java.
	 * 
	 * @return array of length 2, dx = array[0], dy=array[1]
	 */	
	int[] getCurrentDirection() ;
	/**
	 * Helper method for distanceToObstacle and move. Given the current facing direction (cardinal direction 
	 * on map) and the local direction in which the robot is sensing (left, right, forward, backward), return 
	 * the equivalent cardinal direction in which robot is sensing.
	 * Note: for the move method, sensing (in this description) is equivalent to moving.
	 * @param direction Direction that robot is sensing or moving
	 * @return CardinalDirection (North, South, East, West enums)
	 */
	CardinalDirection getSensingDirection(Direction direction) ;
	/**
	 * Returns the current battery level.
	 * The robot has a given battery level (energy level) that it draws energy from during operations. 
	 * The particular energy consumption is device dependent such that a call for distance2Obstacle may use less energy than a move forward operation.
	 * If battery level <= 0 then robot stops to function and hasStopped() is true.
	 * @return current battery level, level is > 0 if operational. 
	 */
	float getBatteryLevel() ;
	/**
	 * Get the energy consumed by the robot.  This is the initial battery level minus energy
	 * used by movement and sensing.
	 * @return amount consumed 
	 */
	float getEnergyConsumed() ; 
	/**
	 * Discharges a certain level of battery
	 */
	void dischargeBattery(float level) ;
	/**
	 * Sets the current battery level.
	 * The robot has a given battery level (energy level) that it draws energy from during operations. 
	 * The particular energy consumption is device dependent such that a call for distance2Obstacle may use less energy than a move forward operation.
	 * If battery level <= 0 then robot stops to function and hasStopped() is true.
	 * @param level is the current battery level
	 * @precondition level >= 0 
	 */
	void setBatteryLevel(float level) ;
	/**
	 * Resets the path length to 0. This is used when the game is re-played but the application has not been restarted.
	 */
	void setPathLengthToZero() ;
	/**
	 * Gives the path length robot has traveled from the beginning to end of its journey.
	 * @return length of the path traveled
	 */
	int getPathLength() ;
	/**
	 * Gives the energy consumption for a full 360 degree rotation.
	 * Scaling by other degrees approximates the corresponding consumption. 
	 * @return energy for a full rotation
	 */
	float getEnergyForFullRotation() ;
	/**
	 * Gives the energy consumption for moving forward for a distance of 1 step.
	 * For simplicity, we assume that this equals the energy necessary 
	 * to move 1 step backwards and that scaling by a larger number of moves is 
	 * approximately the corresponding multiple.
	 * @return energy for a single step forward
	 */
	float getEnergyForStepForward() ;
	/**
	 * Tells if the robot has stopped for reasons like lack of energy, hitting an obstacle, etc.
	 * @return true if the robot has stopped, false otherwise
	 */
	boolean hasStopped() ;
	/**
	 * Tells the distance to an obstacle (a wall or border) for a the robot's current forward direction.
	 * Distance is measured in the number of cells towards that obstacle, 
	 * e.g. 0 if current cell has a wall in this direction
	 * @return number of steps towards obstacle if obstacle is visible 
	 * in a straight line of sight, Integer.MAX_VALUE otherwise
	 * @throws UnsupportedOperationException if not supported by robot
	 */
	int distanceToObstacle(Direction direction) throws UnsupportedOperationException ;
	/**
	 * Tells if the robot has a distance sensor for the given direction.
	 */
	boolean hasDistanceSensor(Direction direction) ;
	/**
	 * Returns a boolean indicating whether there is a clear pathway (no wall) between the
	 * current cell and the cell the robot is facing.
	 * @param px
	 * @param py
	 * @param dx
	 * @param dy
	 * @return boolean
	 */
	boolean canMove(int px, int py, CardinalDirection dir) ;
	

}

package edu.wm.cs.cs301.KeisterDurmaz.falstad;

import android.os.Handler;

/**
 * This interface specifies a robot driver that operates a robot to escape from a given maze. 
 * 
 * Collaborators: Robot
 * 
 * Implementing classes: Gambler, Tremaux, WallFollower, Wizard
 * 
 * @author peterkemper
 *
 */
public interface RobotDriver {
	
	Robot robot = null;

	/**
	 * Assigns a robot platform to the driver. Not all robot configurations may be suitable such that the method 
	 * will throw an exception if the robot does not match minimal configuration requirements, e.g. providing a sensor
	 * to measure the distance to an object in a particular direction. 
	 * @param r robot to operate
	 * @throws UnsuitableRobotException if driver cannot operate the given robot
	 */
	void setRobot(Robot r) throws UnsuitableRobotException ;
	
	/**
	 * Provides the robot driver with information on the dimensions of the 2D maze
	 * measured in the number of cells in each direction.
	 * Only some drivers such as Tremaux's algorithm need this information.
	 * @param width of the maze
	 * @param height of the maze
	 * @precondition 0 <= width, 0 <= height of the maze.
	 */
	void setDimensions(int width, int height) ;
	/**
	 * Provides the robot driver with information on the distance to the exit.
	 * Only some drivers such as the wizard rely on this information to find the exit.
	 * @param distance gives the length of path from current position to the exit.
	 * @precondition null != distance, a full functional distance object for the current maze.
	 */
	void setDistance(Distance distance) ;
	/**
	 * Drives the robot towards the exit given it exists and given the robot's energy supply lasts long enough. 
	 * @param handler 
	 * @return true if driver successfully reaches the exit, false otherwise
	 * @throws exception if robot stopped due to some problem, e.g. lack of energy
	 */
	boolean drive2Exit(Handler handler) throws Exception, OutOfBatteryException ;
	
	/**
	 * Returns the total energy consumption of the journey, i.e.,
	 * the difference between the robot's initial energy level at
	 * the starting position and its energy level at the exit position. 
	 * This is used as a measure of efficiency for a robot driver.
	 */
	float getEnergyConsumption() ;
	
	/**
	 * Returns the total length of the journey in number of cells traversed. 
	 * Being at the initial position counts as 0. 
	 * This is used as a measure of efficiency for a robot driver.
	 */
	int getPathLength() ;
	
}


package edu.wm.cs.cs301.KeisterDurmaz.falstad;

import android.os.Handler;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.Robot.Direction;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.Robot.Turn;

public class Wizard extends AbstractDriver {
	
	@Override
	public boolean drive2Exit(Handler handler) throws Exception {
		Direction bestDirection;

		this.handler = handler;
		Thread.sleep(1000);

		while (!robot.isAtGoal() && this.executing) {
			
			// Rotate towards the shortestDir and move
			bestDirection = getBestDirection();			
			moveInDirection(bestDirection);
	
			//If the robot is at the exit position, rotate and exit now.
			if (robot.isAtGoal()) {
				if (robot.canSeeGoal(Direction.RIGHT))
					moveAndWaitforUI(3);
				else if (robot.canSeeGoal(Direction.LEFT))
					moveAndWaitforUI(2);

				moveAndWaitforUI(1);
				return true;
			}
		}
							
		return false;
	}
	
	/**
	 * Rotate and move the robot in the direction specified.
	 * @param d Direction
	 * @throws Exception if a bad direction is passed.
	 */
	private void moveInDirection(Direction d) throws Exception {
		switch (d) {
		case FORWARD:
			moveAndWaitforUI(1);
			break;
		case BACKWARD:
			moveAndWaitforUI(4);
			moveAndWaitforUI(1);
			break;
		case LEFT:
			moveAndWaitforUI(2);
			moveAndWaitforUI(1);
			break;
		case RIGHT:
			moveAndWaitforUI(3);
			moveAndWaitforUI(1);
			break;
		}
	}

	/**
	 * Find the best direction to for the robot's next step based on shortest distance to the exit.
	 * Move forward if it is equal in distance to left or right to keep energy cost low.
	 * @return Direction - the best direction to move the robot
	 * @throws Exception
	 */
	private Direction getBestDirection() throws Exception {
		int next_x, next_y;
		int distanceToExit;
		int shortestDist = 99999999;
		Direction bestDirection = null;
		
		int px = robot.getCurrentPosition()[0];
		int py = robot.getCurrentPosition()[1];

		// Which direction is the shortest distance to the exit
		for (Direction d: Direction.values()) {
			next_x = getNextPosition(px, py, d)[0];
			next_y = getNextPosition(px, py, d)[1];
			
			// if a step in direction d is with the maze boundary, get distance for that cell.  
			if (robot.isAtGoal() || (next_x >= 0 && next_y >= 0 && next_x < mazeh && next_y < mazew)) {
				distanceToExit = mazedists.getDistance(next_x, next_y);

				if (robot.distanceToObstacle(d) > 0 && (distanceToExit <= shortestDist)) {
					// go forward if it is equal so we don't have to rotate
					if ((distanceToExit == shortestDist) && (bestDirection == Direction.FORWARD))
						shortestDist = distanceToExit;
					else {
						bestDirection = d;
						shortestDist = distanceToExit;
					}
				}
			}
		}
		return bestDirection;
	}

}

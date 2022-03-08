package edu.wm.cs.cs301.KeisterDurmaz.falstad;

import edu.wm.cs.cs301.KeisterDurmaz.falstad.Robot.CardinalDirection;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.Robot.Direction;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.Robot.Turn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.lang.String;

import android.os.Handler;

public class Tremaux extends AbstractDriver {
	private ArrayList<String> explored = new ArrayList<String>();
	private ArrayList<String> retraced = new ArrayList<String>();
	private boolean nextPathMarked = false; // used for helper method
	
	private void print(String s){
		System.out.println(s);
	}
	
	/*
	  * marked1 list
	  * marked2 list
	  * 
	  * save start position
	  * mark 1
	  * 
	  * exploring = 1
	  * retracing = 2
	  * state = exploring
	  *  
	  * move in some direction
	  * 
	  * while not at the goal
	  *   if at start position
	  *    return false
	  *    
	  *   else if in room
	  *    while the robot is in a room
	  *     if the robot is at an unexplored junction, mark explored
	  * 	else, mark retraced
	  *     if can move left, move left
	  * 	else, move forward
	  * 
	  *   else if at unmarked junction
	  *    state = exploring
	  *    move random direction 
	  *    mark 1
	  * 
	  *   else if at marked junction and exploring
	  *    turn around
	  *    move
	  *    mark 2
	  *    state = retracting
	  * 
	  *   else if at marked junction and retracing
	  *    if a path option is unmarked
	  *     turn towards path
	  *     walk
	  *     mark 1
	  *    else if no path options unmarked
	  *     turn toward path option in marked 1
	  *     walk
	  *     mark 2
	  * 
	  *   else if not at junction
	  *    walk
	  *    if exploring, mark 1
	  *    else, mark 2
	  * 
	  * walk through exit
	  * return true
	  * 
	  */
	@Override
	public boolean drive2Exit(Handler handler) throws Exception {
		this.handler = handler;
		Thread.sleep(1000);

		String start = pos2str(robot.getCurrentPosition());	
		explored.add(start);

		final int exploring = 1; // walking down unmarked paths
		final int retracing = 2; // walking down marked paths
		int state = exploring;

		
		// random first step
		if (robot.distanceToObstacle(Direction.FORWARD) > 0) {
			moveAndWaitforUI(1);
		}else if (robot.distanceToObstacle(Direction.RIGHT) > 0) {
			moveAndWaitforUI(3);
			moveAndWaitforUI(1);
		}else if (robot.distanceToObstacle(Direction.LEFT) > 0) {
			moveAndWaitforUI(2);
			robot.move(1);
		}else {
			moveAndWaitforUI(4);
			moveAndWaitforUI(1);
		}
		explored.add(pos2str(robot.getCurrentPosition()));
		
		while (!robot.isAtGoal() && this.executing) {
			
			// at start
			if (pos2str(robot.getCurrentPosition()) == start) {
				return false;
				
			// in room
			}else if (robot.isInsideRoom()){
				
				while (robot.isInsideRoom()) {
					// follow the left wall, marking cells along the way, 
					// until robot has found a way out of the room
					
					if (!explored.contains(pos2str(robot.getCurrentPosition()))){
						explored.add((pos2str(robot.getCurrentPosition())));
					}else {
						retraced.add((pos2str(robot.getCurrentPosition())));
					}
			
					if (robot.distanceToObstacle(Direction.LEFT) > 0){
						moveAndWaitforUI(2);
						moveAndWaitforUI(1);
					}else if (robot.distanceToObstacle(Direction.FORWARD) > 0){
						moveAndWaitforUI(1);
					}else {
						moveAndWaitforUI(3);
						moveAndWaitforUI(1);
					}
				}
					
			// at unmarked junction
			}else if (robot.hasPathOptions() && !explored.contains(pos2str(robot.getCurrentPosition()))) {
				System.out.println("Unmarked junction.  Exploring");
				explored.add((pos2str(robot.getCurrentPosition())));
				state = exploring;
				int[] oldPos = robot.getCurrentDirection();
				int[] newPos = robot.getCurrentDirection();
							
				while ((oldPos[0] == newPos[0]) && (oldPos[1] == newPos[1])){
					int dirIndex = (int)(Math.random()*2);

					switch (dirIndex){
					case 0://LEFT
						if (robot.distanceToObstacle(Direction.LEFT) > 0){
							moveAndWaitforUI(2);
							moveAndWaitforUI(1);
							newPos = robot.getCurrentPosition();
						}
						break;
					case 1: //RIGHT
						if (robot.distanceToObstacle(Direction.RIGHT) > 0){
							moveAndWaitforUI(3);
							moveAndWaitforUI(1);
							newPos = robot.getCurrentPosition();
						}
						break;
					case 2: //FORWARD
						if (robot.distanceToObstacle(Direction.FORWARD) > 0){
							moveAndWaitforUI(1);
							newPos = robot.getCurrentPosition();
						}
						break;
					}
				}
				
			// at marked junction and exploring
			}else if (state == exploring && robot.hasPathOptions() && explored.contains(pos2str(robot.getCurrentPosition()))) {
				System.out.println("Marked junction. Exploring");
				moveAndWaitforUI(4);
				moveAndWaitforUI(1);
				state = retracing;
			
			// at marked junction and retracing
			}else if (state == retracing && robot.hasPathOptions() && explored.contains(pos2str(robot.getCurrentPosition()))) {
				System.out.println("Marked junction and retracing");
				explored.add((pos2str(robot.getCurrentPosition())));
				Direction direction = getDirectionForNextPath();
				if (!nextPathMarked) {
					if (direction != Direction.FORWARD) 
						if (direction == Direction.LEFT)
							moveAndWaitforUI(2);
						else if (direction == Direction.RIGHT)
							moveAndWaitforUI(3);
					moveAndWaitforUI(1);
					state = exploring;

				}else if (nextPathMarked) {
					if (direction != Direction.FORWARD) 
						if (direction == Direction.LEFT)
							moveAndWaitforUI(2);
						else if (direction == Direction.RIGHT)
							moveAndWaitforUI(3);
					moveAndWaitforUI(1);
				}
					
			// not at junction (walking down corridor or at dead end)
			}else { 
				if (state == retracing) {
					System.out.println("Retracing");
					retraced.add(pos2str(robot.getCurrentPosition()));
				}
				
				// move in the direction possible
				if (robot.distanceToObstacle(Direction.FORWARD) > 0) {
					explored.add((pos2str(robot.getCurrentPosition())));
					moveAndWaitforUI(1);
				}else if (robot.distanceToObstacle(Direction.RIGHT) > 0) {
					explored.add((pos2str(robot.getCurrentPosition())));
					moveAndWaitforUI(3);
					moveAndWaitforUI(1);
				}else if (robot.distanceToObstacle(Direction.LEFT) > 0) {
					explored.add((pos2str(robot.getCurrentPosition())));
					moveAndWaitforUI(2);
					moveAndWaitforUI(1);
				}else { // dead end
					System.out.println("Dead end.  Retracing");
					moveAndWaitforUI(4);
					state = retracing;
				}
				
				
			}
		}
		
		if (!this.executing)
			return false;
		
		// exit
		while (!robot.canSeeGoal(Direction.FORWARD)) {
			moveAndWaitforUI(3);
		}
		moveAndWaitforUI(1);
		
		return true;
	}
	
	private String pos2str (int[] pos) {
		String result;
		result = Integer.toString(pos[0]) + "," + Integer.toString(pos[1]);
		return result;
	}

	/**
	 * Check which list each surrounding coords are in (except coords behind robot). add to marked1/Unmarked Option list. 
	 * if unmarked isn't empty, choose random. else, choose option in marked1.
	 * @return direction the next path to take (could be marked or unmarked)
	 */
	private Direction getDirectionForNextPath() throws Exception {
		
		int curx = 0;
		int cury = 0;
		try {
			curx = robot.getCurrentPosition()[0];
			cury = robot.getCurrentPosition()[1];
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String pathForward = pos2str(getNextPosition(curx, cury, Direction.FORWARD));
		String pathLeft = pos2str(getNextPosition(curx, cury, Direction.LEFT));
		String pathRight = pos2str(getNextPosition(curx, cury, Direction.RIGHT));
		
		ArrayList<String> exploredOptions = new ArrayList<String>();
		ArrayList<String> unexploredOptions = new ArrayList<String>();
		
		// if the robot can move in that direction, check if that cell is in a marked list 
		// and add it to the respective options lists
		if (robot.canMove(curx, cury, robot.getSensingDirection(Direction.FORWARD))) {
			if (!explored.contains(pathForward)) {
				unexploredOptions.add(pathForward);
			}
			if (explored.contains(pathForward) && !retraced.contains(pathForward)) {
				exploredOptions.add(pathForward);
			}
		}
		if (robot.canMove(curx, cury, robot.getSensingDirection(Direction.LEFT))) {
			if (!explored.contains(pathLeft)) {
				unexploredOptions.add(pathLeft);
			} 
			if (explored.contains(pathLeft) && !retraced.contains(pathLeft)) {
				exploredOptions.add(pathLeft);
			}
		}
		if (robot.canMove(curx, cury, robot.getSensingDirection(Direction.RIGHT))) {
			if (!explored.contains(pathRight)) {
				unexploredOptions.add(pathRight);
			} 
			if (explored.contains(pathRight) && !retraced.contains(pathRight)) {
				exploredOptions.add(pathRight);
			}
		}
		
		String coords;
		if (unexploredOptions.size() > 0) {
			int r = (int)(Math.random()*((unexploredOptions.size() - 1) +0));
			coords = unexploredOptions.get(r);
			nextPathMarked = false;
		}else if (exploredOptions.size() > 0) {
			int r = (int)(Math.random()*((exploredOptions.size() - 1) +0));
			coords = exploredOptions.get(r);
			nextPathMarked = true;
		}
		else {
			System.out.println("Can't find any options for turning.");
			throw new Exception();
		}
		
		Direction direction;
		if (coords == pathForward) {
			direction = Direction.FORWARD;
		}else if (coords == pathLeft) {
			direction = Direction.LEFT;
		}else { // pathRight
			direction = Direction.RIGHT;
		}
		
		return direction;
		
	}
		
	/**
	 * Converts the Direction that the robot must turn into a Turn type
	 * @param Direction
	 * @return Turn
	 */
	private Turn directionToTurn(Direction direction) {
		// change direction into a turn
		CardinalDirection[] compass = {CardinalDirection.NORTH, CardinalDirection.EAST, CardinalDirection.SOUTH, CardinalDirection.WEST};
		CardinalDirection start = robot.getSensingDirection(Direction.FORWARD);
		CardinalDirection end = robot.getSensingDirection(direction);
		int startIndex = Arrays.asList(compass).indexOf(start); 
		int endIndex = Arrays.asList(compass).indexOf(end);
		final int right = 1;
		final int left = 3;
		
		Turn turn = null;
		if ((startIndex + right) % 4 == endIndex) {
			turn = Turn.RIGHT;
		}else if ((startIndex + left) % 4 == endIndex) {
			turn = Turn.LEFT;
		}else { // forward
			turn = null;
		}
		
		return turn;
	}
	
	
}



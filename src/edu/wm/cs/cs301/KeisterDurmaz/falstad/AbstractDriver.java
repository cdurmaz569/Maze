package edu.wm.cs.cs301.KeisterDurmaz.falstad;

import android.os.Handler;
import android.os.Message;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.Robot.CardinalDirection;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.Robot.Direction;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.Robot.Turn;

public abstract class AbstractDriver implements RobotDriver {
	protected SingleRandom random;
	
	Robot robot;
	protected float energyConsumption;
	protected int pathLength;
	
	protected int mazeh;
	protected int mazew;
	protected Distance mazedists;
	
	Thread thread;
	Handler handler;
	
	ActivitySingleton activitySingleton = ActivitySingleton.getInstance();
	int moveNumber = 0;

	protected boolean executing = true;	// signal the to the main loop the thread should exit

	/**
	 * Tells the UI thread to move the robot
	 * @param arg move forward=1, left=2, right=3, turn around=4
	 * @throws InterruptedException
	 */
	protected void moveAndWaitforUI(int arg) throws InterruptedException {
		moveNumber++;
		Message message = new Message();
		message.setTarget(handler);
		
		message.arg1 = arg; 
		message.sendToTarget();
		
		while (activitySingleton.play == false) {
			Thread.sleep(500);
		}
		
		while (moveNumber != activitySingleton.getMove()){
			Thread.sleep(10);
		}
	}
	
	@Override
	public void setRobot(Robot r) throws UnsuitableRobotException {
		
		if (r.hasDistanceSensor(Direction.FORWARD) == false) {
			throw new UnsuitableRobotException();
		}
		this.robot = r;
	}  

	@Override
	public void setDimensions(int width, int height) {
		this.mazeh = height;
		this.mazew = width;
	}

	@Override
	public void setDistance(Distance distance) {
		this.mazedists = distance;
	}
	
	@Override
	public boolean drive2Exit(Handler handler) throws Exception {
		this.handler = handler;
		return false;
	}

	@Override
	public float getEnergyConsumption() {
		return (robot.getEnergyConsumed());
	}

	@Override
	public int getPathLength() {
		return robot.getPathLength();
	}
	
	/**
	 * Checks for possible directions the robot can move from its current location (forward, right, left, backward) 
	 * and determines a random direction in which to turn
	 * @return Turn where null means move forward
	 */
	protected Turn randomTurn() {
		random = SingleRandom.getRandom();
		
		Turn turn;
		Direction dir = null;
		Direction[] possible = {null, null, null, null};
		int i = 0;
		
		// record possible directions in which to move
		if (robot.distanceToObstacle(Direction.FORWARD) > 0) {
			possible[i] = Direction.FORWARD;
			i++;
		}else if (robot.distanceToObstacle(Direction.RIGHT) > 0) {
			possible[i] = Direction.RIGHT;
			i++;
		}else if (robot.distanceToObstacle(Direction.LEFT) > 0) {
			possible[i] = Direction.LEFT;
			i++;
		}else if (robot.distanceToObstacle(Direction.BACKWARD) > 0) {
			possible[i] = Direction.BACKWARD;
			i++;
		}
		
		// choose a random direction from the list
		while (dir == null) {
			int n = random.nextIntWithinInterval(0, 3);
			if (possible[n] != null) {
				dir = possible[n];
			}
		}
		
		// convert this direction to a turn with null (meaning move forward)
		if (dir == Direction.FORWARD) {
			turn = null;
		}else if (dir == Direction.RIGHT) {
			turn = Turn.RIGHT;
		}else if (dir == Direction.LEFT) {
			turn = Turn.LEFT;
		}else { // BACKWARD
			turn = Turn.AROUND;
		}
		
		return turn;
	}
	
	/**
 	 * Return the next position in the maze given the CardinalDirection parameter
 	 * 
 	 * @param x position
	 * @param y position
	 * @param direction
	 * @return array of next x, y position
	 */
	protected int[] getNextPosition (int x, int y, Direction d) {
		
		int next_x = 0;
		int next_y = 0;
	
		CardinalDirection cardinalDir = robot.getSensingDirection(d);
		switch (cardinalDir) {
		case NORTH: 
			next_x = x;
			next_y = y + 1;
			break;
		case SOUTH: 
			next_x = x;
			next_y = y - 1;
			break;
		case EAST: 
			next_x = x + 1;
			next_y = y;
			break;
		case WEST: 
			next_x = x - 1;
			next_y = y;
			break;
			
		}
	
		int[] pos = {next_x, next_y};
		
		return pos;
	}
	
}

package edu.wm.cs.cs301.KeisterDurmaz.falstad;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import edu.wm.cs.cs301.KeisterDurmaz.falstad.Robot.Direction;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.Robot.Turn;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.ActivitySingleton;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class WallFollower extends AbstractDriver {

	
	@Override
	public boolean drive2Exit(Handler handler) throws Exception {		
		if (!robot.hasDistanceSensor(Direction.FORWARD) || !robot.hasDistanceSensor(Direction.LEFT)) {
			throw new Exception();
		}
		
		this.handler = handler;
		Thread.sleep(1000);
		// find goal
		while (!robot.isAtGoal() && this.executing) {

			// left wall, no forward wall
			if (robot.distanceToObstacle(Direction.LEFT) == 0 && robot.distanceToObstacle(Direction.FORWARD) > 0) {
				moveAndWaitforUI(1); // move
				
			// left wall, forward wall
			}else if (robot.distanceToObstacle(Direction.LEFT) == 0 && robot.distanceToObstacle(Direction.FORWARD) == 0) {
				moveAndWaitforUI(3); // right

				
			// no left wall
			}else if (robot.distanceToObstacle(Direction.LEFT) > 0) {
				moveAndWaitforUI(2);
				moveAndWaitforUI(1);
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
}


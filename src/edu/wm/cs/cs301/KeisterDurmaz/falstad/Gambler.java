package edu.wm.cs.cs301.KeisterDurmaz.falstad;

import android.os.Handler;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.Robot.Direction;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.Robot.Turn;

public class Gambler extends AbstractDriver {

	@Override
	public boolean drive2Exit(Handler handler) throws Exception {

		this.handler = handler;
		Thread.sleep(500);
		
		//find goal
		while (!robot.isAtGoal() && this.executing){
			int dirIndex;
			
			if (robot.distanceToObstacle(Direction.FORWARD) > 0){
				dirIndex = (int)(Math.random()*((3) + 1)); 
				switch (dirIndex) {
				case 0: //LEFT
					if (robot.distanceToObstacle(Direction.LEFT) > 0){
						moveAndWaitforUI(2);
						moveAndWaitforUI(1);
					}
					break;
					case 1: //RIGHT
						moveAndWaitforUI(3);
					if (robot.distanceToObstacle(Direction.FORWARD) > 0){
						moveAndWaitforUI(1);
					}else{
						moveAndWaitforUI(2);
					}
					break;
				case 3: //FORWARD
					moveAndWaitforUI(1);
					break;
				case 4: //AROUND
					moveAndWaitforUI(4);
					moveAndWaitforUI(1);
					break;
				}
			//Checks if at dead end, so only option will be to turn around and move
			}else if (deadEnd() == true){
				moveAndWaitforUI(4);
				moveAndWaitforUI(1);
			}else{

				dirIndex = (int)(Math.random()*((2) + 1)); 
				switch (dirIndex) {
				case 0: //LEFT
					if (robot.distanceToObstacle(Direction.LEFT) > 0){
						moveAndWaitforUI(2);
						moveAndWaitforUI(1);
					}
					break;
				case 1: //RIGHT
					moveAndWaitforUI(3);
					if (robot.distanceToObstacle(Direction.FORWARD) > 0){
						moveAndWaitforUI(1);
					}else{
						moveAndWaitforUI(2);
					}
					break;
				case 3: //AROUND
					moveAndWaitforUI(4);
					moveAndWaitforUI(1);
					break;
					}
				}
		}
		
		if (!this.executing)
			return false;
		
		//exit
		while (!robot.canSeeGoal(Direction.FORWARD)) {
			moveAndWaitforUI(3);
		}
		moveAndWaitforUI(1);
		
		return true;

	}
	
	private boolean deadEnd() throws Exception {
		if (robot.distanceToObstacle(Direction.LEFT) == 0){
			if(robot.distanceToObstacle(Direction.FORWARD) == 0){
				moveAndWaitforUI(3);
				if (robot.distanceToObstacle(Direction.FORWARD) == 0){
					moveAndWaitforUI(2);
					return true;
				}
			}
		}
		return false;
	}
}

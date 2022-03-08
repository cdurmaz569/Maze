package edu.wm.cs.cs301.KeisterDurmaz.falstad;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Keeps track of complex objects that need to be accessed by multiple Activities.
 *
 */
public class ActivitySingleton {
	Maze maze;
	MazePanel panel;
	BasicRobot robot;
	RobotDriver driver;
	private int moveNum = 0;
	public boolean play = true;

   private static ActivitySingleton singleton = new ActivitySingleton( );
   
   /* Static 'instance' method */
   public static ActivitySingleton getInstance( ) {
      return singleton;
   }
   
   /* A private Constructor prevents any other 
    * class from instantiating.
    */
   private ActivitySingleton(){ }
   
   /**
    * @return maze
    */
   public Maze getMaze() {
      return maze;
   }

   /**
    * Set the global maze
    * @param maze
    */
   public void setMaze(Maze maze ) {
      this.maze = maze;
   }
   
   /**
    * Set the global robot
    * @param robot
    */
   public void setRobot(BasicRobot robot) {
	   this.robot = robot;
   }
   
   /**
    * Get the global robot
    * @return robot
    */
   public BasicRobot getRobot() {
	   return robot;
   }

   /**
    * Set the global panel object
    * @param panel
    */
	public void setPanel(MazePanel panel) {
		this.panel = panel;
	}
	
	/**
	 * Get the global panel
	 * @return panel
	 */
	public MazePanel getPanel() {
		return panel;
	}

	/**
	 * Set global driver
	 * @param driver
	 */
	public void setDriver(RobotDriver driver) {
		this.driver = driver;		
	}
	
	/**
	 * Get global driver
	 * @return driver
	 */
	public RobotDriver getDriver() {
		return driver;
	}


	public void setMove(int moveNum) {
		this.moveNum = moveNum;
	}
	public int getMove() {
		return moveNum;
	}
}

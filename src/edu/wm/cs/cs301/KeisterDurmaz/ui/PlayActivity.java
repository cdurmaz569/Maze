package edu.wm.cs.cs301.KeisterDurmaz.ui;
 
import edu.wm.cs.cs301.KeisterDurmaz.R;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.BasicRobot;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.Constants;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.MazeCompleteObserver;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.MazePanel;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.Maze;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.ActivitySingleton;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.OutOfBatteryException;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.Robot.Turn;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.RobotDriver;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ToggleButton;
 
public class PlayActivity extends Activity implements MazeCompleteObserver {
 
        RobotDriver driver;
        MazePanel panel;
        Maze maze;
        BasicRobot robot;
        ProgressBar batteryBar;
        boolean usesAlg;
    	ActivitySingleton activitySingleton = ActivitySingleton.getInstance();
    	
    	Thread thread;
    	Handler handler;
    	
    	int moveNumber = 0;
       
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_play);
                
                Intent intent = getIntent();
                usesAlg = intent.getBooleanExtra("usesAlg", false);
                maze = activitySingleton.getMaze();
                panel = activitySingleton.getPanel();
                robot = activitySingleton.getRobot();
                driver = activitySingleton.getDriver();
                driver.setDistance(maze.mazedists);
                batteryBar = (ProgressBar) findViewById(R.id.progressBarEnergy);
                batteryBar.setProgress(100);
                
                maze.registerMazeCompleteObserver(this); // used for callbacks to switch to finish activity
                
                // set button visibility depending on driver
                if (usesAlg) {
                	findViewById(R.id.buttonLeft).setVisibility(View.GONE);
                	findViewById(R.id.buttonRight).setVisibility(View.GONE);
                	findViewById(R.id.buttonDown).setVisibility(View.GONE);
                	findViewById(R.id.buttonUp).setVisibility(View.GONE);
                }else {
                	findViewById(R.id.toggleButtonPause).setVisibility(View.GONE);
                }
                
//                Toast msg = Toast.makeText(this, "Activity created", Toast.LENGTH_LONG).show();
                Log.v("PlayActivity: onCreate", "Play activity succesfully created");
                
                if (usesAlg) {
                	Log.v("PlayActivity: driverStarter", "starting driver");
                	handler = new Handler(Looper.getMainLooper()) {
                		@Override
						public void handleMessage(Message m) {
                			int action = m.arg1;
                			try {
	                			switch (action){
	                			case 1:
	                				Log.v("PlayActivity: Handler: handleMessage", "algorithm moving forward");
	                				robot.move(1);
	                				break;
	                			case 2:
	                				Log.v("PlayActivity: Handler: handleMessage", "algorithm turning left");
	                				robot.rotate(Turn.LEFT);
	                				break;
	                			case 3:
	                				Log.v("PlayActivity: Handler: handleMessage", "algorithm turning right");
	                				robot.rotate(Turn.RIGHT);
	                				break;
	                			case 4:
	                				Log.v("PlayActivity: Handler: handleMessage", "algorithm turning around");
	                				robot.rotate(Turn.AROUND);
	                				break;
	                			}
                			}catch (Exception e) {
                				if (e instanceof OutOfBatteryException) {
                					startFinishActivity(Constants.OUTCOME_BATTERY);
                					return;
                				}
                				else
                					e.printStackTrace();
                			}
                			
                			updateBatteryBar();
                			moveNumber++;
                			activitySingleton.setMove(moveNumber);
                		}
                	};
                	
                	thread = new Thread(new DriverThread());
                	thread.start();
                }
        }
        
        class DriverThread implements Runnable {

			@Override
			public void run() {
				try {
					driver.drive2Exit(handler);
				} catch (Exception e) {
					if (e instanceof OutOfBatteryException) {
						Log.v("AMazeActivity", "Caught OutOfBatteryException");
					}
					else 
						e.printStackTrace();
				} 
				Log.v("AMazeActivity", "Robot thread exiting");
			}
        }
        
        /**
         * updates the progress bar showing robot battery level
         */
        public void updateBatteryBar() {
        	float level = robot.getBatteryLevel();
        	
        	if (level <= 0.0) {
        		startFinishActivity(Constants.OUTCOME_BATTERY);
        	}
        	
        	float startingLevel = robot.battery.getStartingLevel();
        	int remaining = (int) ((level/startingLevel) * 100); // create percentage
        	batteryBar.setProgress(remaining);
        }
       
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
                // Inflate the menu; this adds items to the action bar if it is present.
                getMenuInflater().inflate(R.menu.play, menu);
                return true;
        }
 
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
                // Handle action bar item clicks here. The action bar will
                // automatically handle clicks on the Home/Up button, so long
                // as you specify a parent activity in AndroidManifest.xml.
                Toast.makeText(this, "" + item.getTitle(), Toast.LENGTH_LONG).show();
                Log.v("PlayActivity: onOptionsItemSelected", "Menu item selected: " + item.getTitle());
                int id = item.getItemId();
                if (id == R.id.action_settings) {
                        return true;
                }
                return super.onOptionsItemSelected(item);
        }
        
        /**
         * Toggle the play/pause button
         * @param view
         */
        public void togglePlayPause(View view) {
        	if (((ToggleButton) view).isChecked()) {
        		Toast.makeText(this, "Pause", Toast.LENGTH_SHORT).show();

        		Log.v("PlayActivity: togglePlayPause", "User has toggled the play/pause button to pause");
        		activitySingleton.play = false;
        	}else {
        		Toast.makeText(this, "Play", Toast.LENGTH_SHORT).show();
        		Log.v("PlayActivity: togglePlayPause", "User has toggled the play/pause button to play");
        		activitySingleton.play = true;
        	}
        }
        
        /**
         * Toggle the solution button
         * @param view
         */
        public void toggleSolution(View view) {
        	if (((ToggleButton) view).isChecked()) {
        		Toast.makeText(this, "Solution on", Toast.LENGTH_SHORT).show();
        		Log.v("PlayActivity: toggleSolution", "User has toggled the solution button on");
        	}else {
        		Toast.makeText(this, "Solution off", Toast.LENGTH_SHORT).show();
        		Log.v("PlayActivity: toggleSolution", "User has toggled the solution button off");
        	}
        	maze.keyDown('s');
        }
        
        /**
         * Toggle the walls button
         * @param view
         */
        public void toggleWalls(View view) {
        	if (((ToggleButton) view).isChecked()) {
        		Toast.makeText(this, "Walls on", Toast.LENGTH_SHORT).show();
        		Log.v("PlayActivity: toggleWalls", "User has toggled the walls button on");
        	}else {
        		Toast.makeText(this, "Walls off", Toast.LENGTH_SHORT).show();
        		Log.v("PlayActivity: toggleWalls", "User has toggled the walls button off");
        	}
        	maze.keyDown('z');
        }
        
        /**
         * Toggle the view button
         * @param view
         */
        public void toggleView(View view) {
        	if (((ToggleButton) view).isChecked()) {
        		Toast.makeText(this, "View on", Toast.LENGTH_SHORT).show();
            	Log.v("PlayActivity: toggleView", "User has toggled the view button on");
        	}else {
        		Toast.makeText(this, "View off", Toast.LENGTH_SHORT).show();
        		Log.v("PlayActivity: toggleView", "User has toggled the view button off");
        	}
        	maze.keyDown('m');
        }
        
        /**
         * Moves the robot up
         * @param view
         * @throws Exception 
         */
        public void moveUp(View view) throws Exception {
        	Log.v("PlayActivity: up", "User has pressed up");
        	try {
        		robot.move(1);
        	} catch (OutOfBatteryException e) {
        		startFinishActivity(Constants.OUTCOME_BATTERY);
        		return;
        	}
        	updateBatteryBar();
        }
        
        /**
         * Moves the robot down
         * @param view
         * @throws Exception 
         */
        public void moveDown(View view) throws Exception {
        	Log.v("PlayActivity: down", "User has pressed down");
        	try {
        		robot.rotate(Turn.AROUND);
        	} catch (OutOfBatteryException e) {
        		startFinishActivity(Constants.OUTCOME_BATTERY);
        		return;
        	}
        	
        	updateBatteryBar();
        }
        
        /**
         * Moves the robot left
         * @param view
         * @throws Exception 
         */
        public void moveLeft(View view) throws Exception {
        	Log.v("PlayActivity: left", "User has pressed left");
        	try {
        		robot.rotate(Turn.LEFT);
        	} catch (OutOfBatteryException e) {
        		startFinishActivity(Constants.OUTCOME_BATTERY);
        		return;
        	}
        	updateBatteryBar();
        }
        
        /**
         * Moves the robot right
         * @param view
         * @throws Exception 
         */
        public void moveRight(View view) throws Exception {
        	Log.v("PlayActivity: right", "User has pressed right");
        	try {
        		robot.rotate(Turn.RIGHT);
        	} catch (OutOfBatteryException e) {
        		startFinishActivity(Constants.OUTCOME_BATTERY);
        		return;
        	}
        	updateBatteryBar();
        }
        
        /**
         * depending on the outcome of the game, displays a message of winning or losing,
         * then creates an intent with a variable outcome and starts the finish activity
         * @param view
         */
        public void startFinishActivity(int outcome) {
        	Log.v("PlayActivity: startFinishActivity", "starting startFinishActivity");
        	Intent intent = new Intent(this, FinishActivity.class);
        	float battery = robot.getBatteryLevel();
        	intent.putExtra("batteryLevel", battery);
        	intent.putExtra("pathLength", robot.getPathLength());
        	intent.putExtra("outcome", outcome);
            startActivity(intent);
            finish();
        }      
}
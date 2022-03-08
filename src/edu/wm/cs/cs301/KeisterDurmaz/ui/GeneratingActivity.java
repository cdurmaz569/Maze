package edu.wm.cs.cs301.KeisterDurmaz.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.wm.cs.cs301.KeisterDurmaz.R;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.BSPNode;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.Cells;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.Gambler;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.ManualDriver;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.Maze;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.BasicRobot;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.MazeFileReader;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.MazeFileWriter;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.MazePanel;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.RobotDriver;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.Tremaux;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.UnsuitableRobotException;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.WallFollower;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.Wizard;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.Constants;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.ActivitySingleton;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.Distance;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class GeneratingActivity extends Activity {
	private String builderStr;
	private int level;
	private String driverStr;
	Maze maze;
	BasicRobot robot;
	RobotDriver driver;
	MazePanel panel;
	boolean usesAlg;
	
	private ProgressBar progressBar;
	Thread progressThread;
	private int progressStatus = 0;
	private TextView textView;
	private Handler handler = new Handler();
	ActivitySingleton activitySingleton = ActivitySingleton.getInstance();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_generating);
		
		Intent intent = getIntent();
		builderStr = intent.getStringExtra("builder");
		level = intent.getIntExtra("level", level);
		driverStr = intent.getStringExtra("driver");
        panel = new MazePanel(this);
        Log.v("", "In onCreate()");
        activitySingleton.setPanel(panel);
        Log.v("", "setPanel()");
		
		createProgressBar();
		createMaze();
		activitySingleton.setMaze(maze);
		createRobot();
	}

	/**
	 * Creates the maze based on the chosen builder
	 * This method is helper method for onCreate
	 */
	private void createMaze() {
		String tag = "GeneratingActivity: createMaze";
		String mes = "Generating random maze with ";
		
		switch (builderStr) {
		case "Default algorithm":
			Log.v(tag, mes + "default algorithm");
			maze = new Maze(0);
			maze.init();
			break;
		case "Prim\'s Algorithm":
			Log.v(tag, mes + "Prim's algorithm");
			maze = new Maze(1);
			maze.init();
			break;
		case "Aldous-Broder Algorithm":
			Log.v(tag, mes + "Aldous-Broder algorithm");
			maze = new Maze(2);
			maze.init();
			break;
		case "To file":
			break;
		case "From file":
			Log.v(tag, mes + "file");
		    maze = new Maze();
		    MazeFileReader mfr;
			try {
				mfr = new MazeFileReader(maze.getMazePanel(), "maze.xml");
				maze.mazeh = mfr.getHeight() ;
				maze.mazew = mfr.getWidth() ;
				Distance d = new Distance(mfr.getDistances()) ;
				maze.newMaze(mfr.getRootNode(), mfr.getCells(), d, mfr.getStartX(), mfr.getStartY()) ;
				maze.init();
				return; 	// don't call maze.build()
			} catch (Exception e) {
				// We should be able to read the maze file when we get here.  
				if (e instanceof FileNotFoundException) {
					Log.e("AMazeActivity", "Couldn't open saved maze.");
					return;
				}
				else
					Log.e("AMazeActivity", "Error " + e.getMessage());
				return;
			}
		}
		
		maze.build(level);
	}
	
	/**
	 * Creates the robot and driver that will operate in the maze
	 */
	private void createRobot(){
		robot = new BasicRobot();
		robot.setMaze(maze);
		usesAlg = true; 
		
		switch (driverStr) {
		case "Manual Driver":
			driver = new ManualDriver();
			usesAlg = false;
			break;
		case "Gambler":
			driver = new Gambler();
			break;
		case "Wall Follower":
			driver = new WallFollower();
			break;
		case "Wizard":
			driver = new Wizard();
			break;
		case "Tremaux Algorithm":
			driver = new Tremaux();
			break;
		}
		
		try{
			driver.setRobot(robot);
		}catch (UnsuitableRobotException e1){
			e1.printStackTrace();
		}
		
		if (!usesAlg) {
			try {
				maze.mazebuilder.buildThread.join();
			} catch (InterruptedException e2) {
				e2.printStackTrace();
			}
		}
		
		driver.setDistance(maze.mazedists);
		driver.setDimensions(maze.mazeh, maze.mazew);
		
		activitySingleton.setRobot(robot);
		activitySingleton.setDriver(driver);
	}
	
	/**
	 * Creates and starts the progress bar
	 * This method is helper method for onCreate
	 */
	private void createProgressBar() {
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		textView = (TextView) findViewById(R.id.textView3);
		// Start long running operation in a background thread
		progressThread = new Thread( new Runnable() {
			public void run() {
				
				while (progressStatus < 100) {
					progressStatus += 1;
					//progressStatus = Integer.parseInt(maze.getPercentDone());

					// Update the progress bar and display the 
					//current value in the text view
					handler.post(new Runnable() {
						public void run() {
							progressBar.setProgress(progressStatus);
							textView.setText(progressStatus+"/"+progressBar.getMax());
						}
					});
					try {
						Thread.sleep(30); // display the progress slowly
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				Log.v("AMazeActivity", "Maze complete " + maze.getPercentDone());
				
				// Just in case we aren't done for some reason
				while (maze.state != Constants.STATE_PLAY) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				startPlayActivity();
		     }
		  });
		  
		progressThread.start();
	}
	
	/**
	 * Creates the intent with a variable driver, and starts the play activity
	 */
	private void startPlayActivity() {
		Intent intent = new Intent(this, PlayActivity.class);
		intent.putExtra("usesAlg", usesAlg);
		startActivity(intent);
		finish();
	}
	
	@Override
	public void onBackPressed() {
		maze.mazebuilder.interrupt();
		maze.buildInterrupted();

	    super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.generating, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
